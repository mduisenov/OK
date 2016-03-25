package ru.ok.android.services.processors.stream;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import ru.mail.android.mytarget.core.providers.FingerprintDataProvider;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.benchmark.StreamBenchmark;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.PromoLinkStorageFacade;
import ru.ok.android.flurry.StreamErrors;
import ru.ok.android.graylog.GrayLog;
import ru.ok.android.services.processors.banners.BannerLinksProcessor;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.processors.settings.GifSettings;
import ru.ok.android.services.processors.settings.PhotoCollageSettings;
import ru.ok.android.services.processors.settings.PhotoRollSettingsHelper;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.storage.StorageException;
import ru.ok.android.storage.Storages;
import ru.ok.android.ui.presents.helpers.PresentSettingsHelper;
import ru.ok.android.ui.stream.data.StreamContext;
import ru.ok.android.ui.stream.data.StreamWithPromoLinks;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.StringUtils;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.json.stream.JsonGetBannersHeadParser;
import ru.ok.java.api.json.stream.JsonGetStreamParser;
import ru.ok.java.api.json.users.JsonGetFriendsHolidaysParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.SettingsGetRequest;
import ru.ok.java.api.request.banners.GetBannersLinksRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.stream.GetStreamRequest;
import ru.ok.java.api.request.users.GetFriendsHolidaysRequest;
import ru.ok.java.api.request.users.UserInfoRequest.FIELDS;
import ru.ok.java.api.response.stream.GetStreamResponse;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.Holidays;
import ru.ok.model.stream.LikeInfo;
import ru.ok.model.stream.StreamPage;
import ru.ok.model.stream.StreamPageKey;
import ru.ok.model.stream.banner.BannerLinkType;
import ru.ok.model.stream.banner.PromoLinkBuilder;
import ru.ok.model.stream.entities.AbsFeedPhotoEntityBuilder;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.BaseEntityBuilder;
import ru.ok.model.stream.entities.FeedPollEntityBuilder;

public final class GetStreamProcessor {
    private static final BannerLinkType[] REQUEST_PROMO_LINK_TYPES;
    private static JSONObject bannerOpt;

    @Subscribe(on = 2131623944, to = 2131624017)
    public void markStreamAllRead(BusEvent event) {
        Context context = OdnoklassnikiApplication.getContext();
        if (context == null) {
            Logger.m184w("null context");
            return;
        }
        String currentUserId = OdnoklassnikiApplication.getCurrentUser().getId();
        if (TextUtils.isEmpty(currentUserId)) {
            Logger.m184w("current user ID not set");
            return;
        }
        Logger.m172d("");
        try {
            getStreamFromAPI(context, Storages.getInstance(context, currentUserId), StreamPageKey.firstPageKey(1), StreamContext.stream(), false, true, null, 0);
        } catch (StreamLoadException e) {
            Logger.m180e(e, "Failed to mark stream as read: %s", e);
            StreamErrors.logAndFilterError("mark_stream_all_read", e.getMessage(), e);
        }
    }

    @NonNull
    public static StreamWithPromoLinks getStream(Context context, StreamPageKey pageKey, StreamContext streamContext, boolean doGetPromoLinks, boolean forceLoadFromWeb, boolean firstTryLoadFromWeb, @Nullable StreamPageKey topPageKey, long streamTs) throws StreamLoadException {
        long startTime = System.currentTimeMillis();
        Logger.m173d(">>> pageKey=%s streamContext=%s doGetPromoLinks=%s forceLoadFromWeb=%s firstTryLoadFromWeb=%s topPageKey=%s", pageKey, streamContext, Boolean.valueOf(doGetPromoLinks), Boolean.valueOf(forceLoadFromWeb), Boolean.valueOf(firstTryLoadFromWeb), topPageKey);
        String currentUserId = OdnoklassnikiApplication.getCurrentUser().getId();
        if (TextUtils.isEmpty(currentUserId)) {
            throw new StreamLoadException("Current user ID not set");
        }
        StreamWithPromoLinks response;
        Storages storages = Storages.getInstance(context, currentUserId);
        if (forceLoadFromWeb) {
            response = getFromAPIandSaveToCache(context, storages, pageKey, streamContext, doGetPromoLinks, true, topPageKey, streamTs);
        } else if (firstTryLoadFromWeb) {
            response = getFromAPIThenFromCache(context, storages, pageKey, streamContext, doGetPromoLinks, topPageKey, streamTs);
        } else {
            response = getFromCacheThenFromAPI(context, storages, pageKey, streamContext, doGetPromoLinks, topPageKey, streamTs);
        }
        onLoadedStreamPage(context, response.streamPage, storages, response.fromAPI);
        Logger.m173d("<<< %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
        return response;
    }

    private static StreamWithPromoLinks getFromAPIThenFromCache(Context context, Storages storages, StreamPageKey pageKey, StreamContext streamContext, boolean doGetPromoLinks, StreamPageKey topPageKey, long streamTs) throws StreamLoadException {
        StreamWithPromoLinks response;
        try {
            response = getFromAPIandSaveToCache(context, storages, pageKey, streamContext, doGetPromoLinks, true, topPageKey, streamTs);
        } catch (StreamLoadException e) {
            Logger.m185w("failed to load from web, will load from cache: %s", e);
            response = getStreamFromCache(context, pageKey, streamContext, doGetPromoLinks);
            if (response == null) {
                throw e;
            }
            StreamErrors.logAndFilterError("get_from_api_then_from_cache", e.getMessage(), e);
        }
        return response;
    }

    private static StreamWithPromoLinks getFromCacheThenFromAPI(Context context, Storages storages, StreamPageKey pageKey, StreamContext streamContext, boolean doGetPromoLinks, StreamPageKey topPageKey, long streamTs) throws StreamLoadException {
        StreamWithPromoLinks response = getStreamFromCache(context, pageKey, streamContext, doGetPromoLinks);
        if (response != null) {
            return response;
        }
        Logger.m184w("failed to load from cache, load from web...");
        return getFromAPIandSaveToCache(context, storages, pageKey, streamContext, doGetPromoLinks, true, topPageKey, streamTs);
    }

    @NonNull
    private static StreamWithPromoLinks getFromAPIandSaveToCache(Context context, Storages storages, StreamPageKey pageKey, StreamContext streamContext, boolean doGetPromoLinks, boolean markAsRead, StreamPageKey topPageKey, long streamTs) throws StreamLoadException {
        StreamWithPromoLinks response = getStreamFromAPI(context, storages, pageKey, streamContext, doGetPromoLinks, markAsRead, topPageKey, streamTs);
        saveToCache(pageKey, streamContext, response.streamPage, storages);
        StreamBenchmark.saveToCache(response.benchmarkSeqId);
        return response;
    }

    @Nullable
    @CheckResult
    private static StreamWithPromoLinks getStreamFromCache(Context context, StreamPageKey pageKey, StreamContext streamContext, boolean doGetPromoLinks) {
        String currentUserId = OdnoklassnikiApplication.getCurrentUser().getId();
        if (TextUtils.isEmpty(currentUserId)) {
            Logger.m176e("currentUserId is empty");
            return null;
        }
        try {
            StreamPage page = Storages.getInstance(context, currentUserId).getStreamCache().get(streamContext, pageKey);
            if (page == null) {
                return null;
            }
            page.resolveRefs();
            ArrayList<PromoLinkBuilder> promoLinks = null;
            Holidays holidays = null;
            if (doGetPromoLinks) {
                if (PresentSettingsHelper.getSettings().streamHolidaysEnabled) {
                    holidays = getHolidaysFromCache(context, currentUserId, streamContext);
                }
                promoLinks = getPromoLinksFromCache(streamContext);
                if (promoLinks != null && promoLinks.isEmpty()) {
                    promoLinks = null;
                }
            }
            return new StreamWithPromoLinks(page, promoLinks, holidays, false);
        } catch (StorageException e) {
            Logger.m180e(e, "Failed to load stream from cache: %s", e);
            StreamErrors.logAndFilterError("get_stream_from_cache", e.getMessage(), e);
            return null;
        }
    }

    @Nullable
    private static ArrayList<PromoLinkBuilder> getPromoLinksFromCache(StreamContext streamContext) {
        String userId = null;
        if (streamContext.type == 2) {
            userId = streamContext.id;
        } else if (streamContext.type == 3) {
            return null;
        }
        return PromoLinkStorageFacade.queryPromoLinks(OdnoklassnikiApplication.getContext().getContentResolver(), 1, userId);
    }

    @Nullable
    private static Holidays getHolidaysFromCache(@NonNull Context context, @NonNull String currentUserId, @NonNull StreamContext streamContext) {
        if (streamContext.type != 1) {
            return null;
        }
        try {
            Holidays holidays = Storages.getInstance(context, currentUserId).getHolidaysCache().get();
            if (holidays == null) {
                return holidays;
            }
            holidays.prepareHolidays();
            return holidays;
        } catch (StorageException e) {
            Logger.m180e(e, "Failed to holidays from cache: %s", e);
            return null;
        }
    }

    static {
        REQUEST_PROMO_LINK_TYPES = new BannerLinkType[]{BannerLinkType.HEAD_LINK, BannerLinkType.SIDE_LINK, BannerLinkType.SIDE_LINK_2};
    }

    @NonNull
    public static StreamWithPromoLinks getStreamFromAPI(Context context, Storages storages, StreamPageKey pageKey, StreamContext streamContext, boolean doGetPromoLinks, boolean markAsRead, StreamPageKey topPageKey, long streamTs) throws StreamLoadException {
        Logger.m173d("pageKey=%s context=%s", pageKey, streamContext);
        GetBannersLinksRequest promoLinksRequest = null;
        GetFriendsHolidaysRequest holidaysRequest = null;
        JSONObject bannerOpt = getBannerOpt(context);
        boolean markAsReadApi = markAsRead && pageKey.isFirstPage();
        GetStreamRequest streamRequest = createRequest(pageKey, streamContext, markAsReadApi, null, "android.5", "FRIENDSHIP,JOIN,MESSAGE,PRESENT,PIN,CONTENT,GIFTS_CAMPAIGN,BANNER", bannerOpt);
        if (doGetPromoLinks) {
            promoLinksRequest = new GetBannersLinksRequest(streamContext.type != 1 ? streamContext.id : null, bannerOpt, REQUEST_PROMO_LINK_TYPES);
            if (streamContext.type == 1 && PresentSettingsHelper.getSettings().streamHolidaysEnabled) {
                holidaysRequest = new GetFriendsHolidaysRequest(new RequestFieldsBuilder().addField(FIELDS.NAME).addField(FIELDS.GENDER).addField(DeviceUtils.getUserAvatarPicFieldName()).build(), "RECOMMENDED");
            }
        }
        try {
            return performRequest(storages, streamRequest, promoLinksRequest, holidaysRequest, pageKey, markAsReadApi, true, topPageKey, streamContext, streamTs);
        } catch (Throwable e) {
            Logger.m180e(e, "Failed to get stream from API: %s", e);
            Bundle errorBundle = new Bundle();
            CommandProcessor.fillErrorBundle(errorBundle, e);
            throw new StreamLoadException(e, errorBundle);
        }
    }

    private static StreamWithPromoLinks performRequest(@NonNull Storages storages, @NonNull GetStreamRequest streamRequest, @Nullable GetBannersLinksRequest promoLinksRequest, @Nullable GetFriendsHolidaysRequest holidaysRequest, StreamPageKey pageKey, boolean markAsReadApi, boolean markAsReadCache, StreamPageKey topPageKey, StreamContext streamContext, long streamTs) throws Exception {
        BaseRequest request;
        Logger.m173d("key=%s markAsReadApi=%s markAsReadCache=%s", pageKey, Boolean.valueOf(markAsReadApi), Boolean.valueOf(markAsReadCache));
        String promoLinksId = null;
        BaseRequest photoSettingsRequest = buildPhotoSettingsRequest();
        boolean isBatch = (promoLinksRequest == null && photoSettingsRequest == null && holidaysRequest == null) ? false : true;
        if (isBatch) {
            BatchRequests requests = new BatchRequests();
            requests.addRequest(streamRequest);
            if (promoLinksRequest != null) {
                requests.addRequest(promoLinksRequest, true);
                promoLinksId = promoLinksRequest.getId();
            }
            if (holidaysRequest != null) {
                requests.addRequest(holidaysRequest, true);
            }
            if (photoSettingsRequest != null) {
                requests.addRequest(photoSettingsRequest, true);
            }
            request = new BatchRequest(requests);
        } else {
            request = streamRequest;
        }
        JsonSessionTransportProvider transportProvider = JsonSessionTransportProvider.getInstance();
        int benchmarkSeqId = StreamBenchmark.sendRequest(pageKey.getCount(), pageKey.getPageNumber());
        long requestTs = System.currentTimeMillis();
        JsonHttpResult result = transportProvider.execJsonHttpMethod(request);
        StreamBenchmark.receiveResponse(benchmarkSeqId);
        try {
            JSONObject streamResponse;
            JSONObject promoLinksResponse;
            JSONObject holidaysResponse;
            JSONObject photoSettingsResponse;
            JSONObject jsonResponse = result.getResultAsObject();
            if (isBatch) {
                streamResponse = jsonResponse.getJSONObject("stream_get_response");
                promoLinksResponse = jsonResponse.optJSONObject("banners_getBannerLinks_response");
                holidaysResponse = jsonResponse.optJSONObject("users_getFriendsHolidays_response");
                photoSettingsResponse = jsonResponse.optJSONObject("settings_get_response");
            } else {
                streamResponse = jsonResponse;
                promoLinksResponse = null;
                holidaysResponse = null;
                photoSettingsResponse = null;
            }
            GetStreamResponse stream = handleStreamResponse(storages, streamResponse, pageKey, streamContext, markAsReadApi, topPageKey, requestTs, streamTs, benchmarkSeqId);
            ArrayList<PromoLinkBuilder> promoLinks = handlePromoLinksResponse(OdnoklassnikiApplication.getContext(), promoLinksResponse, promoLinksId);
            Holidays holidays = handleHolidaysResponse(holidaysResponse, storages);
            if (photoSettingsResponse != null) {
                handlePhotoSettingsResponse(photoSettingsResponse);
            }
            return new StreamWithPromoLinks(stream.streamPage, promoLinks, holidays, true, benchmarkSeqId);
        } catch (Throwable jsonException) {
            if (GrayLog.isEnabled()) {
                GrayLog.log("JSONException. Api response:\n" + StringUtils.trimToLength(result.getHttpResponse(), 97000), jsonException);
            }
            throw jsonException;
        }
    }

    @Nullable
    private static BaseRequest buildPhotoSettingsRequest() {
        boolean enablePhotoCollage = PhotoCollageSettings.isReadyToUpdateCollageEnabled();
        boolean updateGifSettings = GifSettings.isReadyToUpdateGifSettings();
        boolean updatePhotoRollViewSettings = PhotoRollSettingsHelper.isReadyToUpdateSettings();
        if (!enablePhotoCollage && !updateGifSettings && !updatePhotoRollViewSettings) {
            return null;
        }
        List<String> fields = new ArrayList(5);
        if (enablePhotoCollage) {
            Collections.addAll(fields, PhotoCollageSettings.getFields());
        }
        if (updateGifSettings) {
            Collections.addAll(fields, GifSettings.getFields());
        }
        if (updatePhotoRollViewSettings) {
            Collections.addAll(fields, PhotoRollSettingsHelper.getFields());
        }
        if (fields.isEmpty()) {
            return null;
        }
        return new SettingsGetRequest((String[]) fields.toArray(new String[fields.size()]), 182);
    }

    public static GetStreamRequest createRequest(StreamPageKey key, StreamContext streamContext, boolean markAsRead, String[] fields, String fieldset, String patterns, JSONObject bannerOpt) {
        if (!key.isFirstPage()) {
            markAsRead = false;
        }
        return new GetStreamRequest(patterns, fields, fieldset, key.getAnchor(), key.getCount(), streamContext.type == 2 ? streamContext.id : null, streamContext.type == 3 ? streamContext.id : null, markAsRead, bannerOpt);
    }

    private static GetStreamResponse handleStreamResponse(Storages storages, JSONObject jsonResponse, StreamPageKey pageKey, StreamContext streamContext, boolean markAsReadApi, StreamPageKey topPageKey, long requestTs, long streamTs, int benchmarkSeqId) throws Exception {
        Context context = OdnoklassnikiApplication.getContext();
        GetStreamResponse response = JsonGetStreamParser.parseGetStreamResponse(context, jsonResponse, pageKey);
        response.streamPage.resolveRefs();
        response.streamPage.setPageTs(requestTs);
        if (topPageKey == null || streamTs == 0) {
            response.streamPage.setStreamTs(requestTs);
        } else {
            response.streamPage.setStreamTs(streamTs);
        }
        StreamBenchmark.parseResponse(benchmarkSeqId);
        response.streamPage.setTopKey(topPageKey);
        FeedDigestCalculator.calculateFeedDigests(response.streamPage.feeds);
        storages.getFeedIdStorage().generateFeedIds(response.streamPage.feeds);
        generateFeedUuids(response.streamPage.feeds);
        if (markAsReadApi && streamContext.type == 1 && pageKey.isFirstPage()) {
            UnreadStream.getInstance(context, OdnoklassnikiApplication.getCurrentUser().getId()).onLoadedFirstStreamPage();
        }
        return response;
    }

    private static void generateFeedUuids(ArrayList<Feed> feeds) {
        int size = feeds.size();
        for (int i = 0; i < size; i++) {
            ((Feed) feeds.get(i)).setUuid(UUID.randomUUID().toString());
        }
    }

    private static void handlePhotoSettingsResponse(JSONObject jsonResponse) {
        PhotoCollageSettings.parseAndSave(jsonResponse);
        GifSettings.parseAndSave(jsonResponse);
        PhotoRollSettingsHelper.parseAndSave(jsonResponse);
    }

    private static void saveToCache(StreamPageKey pageKey, StreamContext streamContext, StreamPage streamPage, Storages storages) {
        Context context = OdnoklassnikiApplication.getContext();
        String currentUserId = OdnoklassnikiApplication.getCurrentUser().uid;
        if (context == null) {
            Logger.m184w("context is null");
        } else if (TextUtils.isEmpty(currentUserId)) {
            Logger.m184w("current user ID is empty");
        } else {
            try {
                storages.getStreamCache().put(streamContext, pageKey, streamPage, streamPage.getStreamTs());
            } catch (StorageException e) {
                Logger.m180e(e, "Failed to save stream to cache: %s", e);
                StreamErrors.logAndFilterError("save_to_cache", e.getMessage(), e);
            }
        }
    }

    private static ArrayList<PromoLinkBuilder> handlePromoLinksResponse(Context context, JSONObject jsonResponse, String fid) {
        ArrayList<PromoLinkBuilder> promoLinks = null;
        if (jsonResponse == null) {
            return null;
        }
        try {
            promoLinks = JsonGetBannersHeadParser.parse(context, jsonResponse, fid, System.currentTimeMillis(), REQUEST_PROMO_LINK_TYPES);
            BannerLinksProcessor.processBannerLinksResponse(promoLinks, fid, REQUEST_PROMO_LINK_TYPES);
            return promoLinks;
        } catch (ResultParsingException e) {
            Logger.m180e(e, "Failed to parse promo links: %s", e);
            return promoLinks;
        }
    }

    @Nullable
    private static Holidays handleHolidaysResponse(@Nullable JSONObject jsonResponse, @NonNull Storages storages) {
        Holidays holidays = null;
        if (jsonResponse != null) {
            try {
                holidays = new JsonGetFriendsHolidaysParser(jsonResponse).parse();
                if (holidays != null) {
                    holidays.prepareHolidays();
                    storages.getHolidaysCache().replace(holidays);
                }
            } catch (ResultParsingException e) {
                Logger.m180e(e, "Failed to parse holidays: %s", e);
            } catch (StorageException e2) {
                Logger.m180e(e2, "Failed to save holidays: %s", e2);
            }
        }
        return holidays;
    }

    private static void onLoadedStreamPage(Context context, StreamPage page, Storages storages, boolean fromAPI) {
        preloadLikeIds(context, page, storages);
        preloadDeletedFeeds(context, page, storages);
        preloadUnsubscribedFeedOwners(context, page, storages, fromAPI);
        preloadMtPolls(context, page, storages);
    }

    private static void preloadLikeIds(Context context, StreamPage page, Storages storages) {
        long startTime = System.currentTimeMillis();
        Logger.m172d("preloadLikeIds >>>");
        ArrayList<String> likeIds = new ArrayList();
        for (BaseEntityBuilder entity : page.entities.values()) {
            LikeInfo like = entity.getLikeInfo();
            if (like == null && (entity instanceof AbsFeedPhotoEntityBuilder)) {
                PhotoInfo photoInfo = ((AbsFeedPhotoEntityBuilder) entity).getPhotoInfo();
                if (photoInfo != null) {
                    like = photoInfo.getLikeInfo();
                }
            }
            String likeId = like == null ? null : like.likeId;
            if (!TextUtils.isEmpty(likeId)) {
                likeIds.add(likeId);
            }
        }
        int size = page.feeds.size();
        for (int i = 0; i < size; i++) {
            like = ((Feed) page.feeds.get(i)).getLikeInfo();
            likeId = like == null ? null : like.likeId;
            if (!TextUtils.isEmpty(likeId)) {
                likeIds.add(likeId);
            }
        }
        storages.getLikeManager().preload(likeIds);
        Logger.m173d("preloadLikeIds <<< %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
    }

    private static void preloadDeletedFeeds(Context context, StreamPage page, Storages storages) {
        long startTime = System.currentTimeMillis();
        Logger.m172d("preloadDeletedFeeds >>>");
        ArrayList<String> deleteIds = new ArrayList(page.feeds.size());
        int size = page.feeds.size();
        for (int i = 0; i < size; i++) {
            String deleteId = ((Feed) page.feeds.get(i)).getDeleteId();
            if (!TextUtils.isEmpty(deleteId)) {
                deleteIds.add(deleteId);
            }
        }
        storages.getDeletedFeedsManager().preload(deleteIds);
        Logger.m173d("preloadDeletedFeeds <<< %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
    }

    private static void preloadUnsubscribedFeedOwners(Context context, StreamPage page, Storages storages, boolean fromAPI) {
        long startTime = System.currentTimeMillis();
        ArrayList<String> userIds = null;
        ArrayList<String> groupIds = null;
        int size = page.feeds.size();
        for (int i = 0; i < size; i++) {
            ArrayList<? extends BaseEntity> feedOwners = ((Feed) page.feeds.get(i)).getFeedOwners();
            for (int j = feedOwners.size() - 1; j >= 0; j--) {
                BaseEntity entity = (BaseEntity) feedOwners.get(j);
                int type = entity.getType();
                if (type == 2) {
                    if (groupIds == null) {
                        groupIds = new ArrayList();
                    }
                    groupIds.add(entity.getId());
                } else if (type == 7) {
                    if (userIds == null) {
                        userIds = new ArrayList();
                    }
                    userIds.add(entity.getId());
                }
            }
        }
        storages.getStreamSubscriptionManager().preload(userIds, groupIds, fromAPI, page.getPageTs());
        Logger.m173d("preloadUnsubscribedFeedOwners <<< %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
    }

    private static void preloadMtPolls(Context context, StreamPage page, Storages storages) {
        long startTime = System.currentTimeMillis();
        ArrayList<String> pollIds = null;
        for (BaseEntityBuilder entity : page.entities.values()) {
            if (entity.getType() == 11 && (entity instanceof FeedPollEntityBuilder)) {
                FeedPollEntityBuilder poll = (FeedPollEntityBuilder) entity;
                if (pollIds == null) {
                    pollIds = new ArrayList();
                }
                pollIds.add(poll.getId());
            }
        }
        if (pollIds != null) {
            storages.getMtPollsManager().preload(pollIds);
        }
        Logger.m173d("preloadMtPolls <<< %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
    }

    public static JSONObject getBannerOpt(Context context) {
        if (bannerOpt == null) {
            JSONObject bannerOpt = addDeviceParams(addAdvertisingInfo(null, context));
            if (Logger.isLoggingEnable()) {
                try {
                    String str = "Using banner_opt=%s";
                    Object[] objArr = new Object[1];
                    objArr[0] = bannerOpt == null ? "null" : bannerOpt.toString(4);
                    Logger.m173d(str, objArr);
                } catch (JSONException e) {
                }
            }
            bannerOpt = bannerOpt;
        }
        return bannerOpt;
    }

    private static JSONObject addAdvertisingInfo(JSONObject bannerOpt, Context context) {
        Info info = getAdvertisingInfo(context);
        if (info != null) {
            if (bannerOpt == null) {
                bannerOpt = new JSONObject();
            }
            try {
                bannerOpt.put("advertising_id", info.getId());
                bannerOpt.put("advertising_tracking_enabled", info.isLimitAdTrackingEnabled() ? 0 : 1);
            } catch (Throwable e) {
                Logger.m179e(e, "Failed to build banner_opt json");
            }
        }
        return bannerOpt;
    }

    private static JSONObject addDeviceParams(JSONObject bannerOpt) {
        Logger.m172d(">>> calling AdMan to get device params...");
        Map<String, String> params = null;
        try {
            params = FingerprintDataProvider.getInstance().getData();
            Logger.m173d("AdMan returned params=%s", params);
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to get device params from AdMan");
        }
        int count = 0;
        if (!(params == null || params.isEmpty())) {
            if (bannerOpt == null) {
                bannerOpt = new JSONObject();
            }
            for (Entry<String, String> param : params.entrySet()) {
                try {
                    Logger.m173d("Adding device param from AdMan: %s=%s", (String) param.getKey(), (String) param.getValue());
                    bannerOpt.put(key, value);
                    count++;
                } catch (JSONException e2) {
                    Logger.m180e(e2, "Failed to add device param from adman: %s=%s", key, value);
                }
            }
        }
        Logger.m173d("<<< added %d device params from AdMan", Integer.valueOf(count));
        return bannerOpt;
    }

    private static Info getAdvertisingInfo(Context context) {
        Logger.m172d(">>>");
        Info info = null;
        try {
            info = AdvertisingIdClient.getAdvertisingIdInfo(context);
        } catch (Throwable e) {
            Logger.m186w(e, "Failed to get advertising info");
        } catch (Throwable e2) {
            Logger.m186w(e2, "Failed to get advertising info");
        } catch (Throwable e22) {
            Logger.m186w(e22, "Failed to get advertising info");
        }
        String str = "<<< info=%s, id=%s, limitAdTracking=%s";
        Object[] objArr = new Object[3];
        objArr[0] = info;
        objArr[1] = info == null ? "null" : info.getId();
        objArr[2] = info == null ? "null" : Boolean.valueOf(info.isLimitAdTrackingEnabled());
        Logger.m173d(str, objArr);
        return info;
    }
}
