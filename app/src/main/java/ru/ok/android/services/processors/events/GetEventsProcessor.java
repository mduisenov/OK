package ru.ok.android.services.processors.events;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.flurry.StreamErrors;
import ru.ok.android.services.processors.banners.BannerLinksProcessor;
import ru.ok.android.services.processors.poll.AppPollPreferences;
import ru.ok.android.services.processors.poll.AppPollProcessor;
import ru.ok.android.services.processors.stream.GetStreamProcessor;
import ru.ok.android.services.processors.stream.UnreadStream;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.presents.helpers.PresentSettingsHelper;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.json.JsonGetEventsParser;
import ru.ok.java.api.json.stream.JsonGetBannersHeadParser;
import ru.ok.java.api.json.users.JsonUserCountersParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.SettingsGetRequest;
import ru.ok.java.api.request.banners.GetBannersLinksRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.events.GetEventsRequest;
import ru.ok.java.api.request.users.UserCountersRequest;
import ru.ok.java.api.response.users.UserCounters;
import ru.ok.java.api.utils.JsonUtil;
import ru.ok.model.events.OdnkEvent;
import ru.ok.model.events.OdnkEvent.EventType;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.UnreadStreamPage;
import ru.ok.model.stream.banner.BannerLinkType;
import ru.ok.model.stream.banner.PromoLinkBuilder;
import ru.ok.model.stream.entities.BaseEntityBuilder;

public final class GetEventsProcessor {
    private static final BannerLinkType[] ALL_LINK_TYPES;
    private static final BannerLinkType[] BANNER_LINK_TYPES;
    private static final BannerLinkType[] PROMO_LINK_TYPES;
    private long lastBannerUpdateTs;
    private long prevPromoLinksRequestTime;

    @Subscribe(on = 2131623944, to = 2131624053)
    public void getEvents(BusEvent event) {
        Logger.m172d("visit to get events processor");
        Context context = OdnoklassnikiApplication.getContext();
        if (context == null) {
            Logger.m184w("App context is null, cannot proceed with getEvents().");
            return;
        }
        String currenUserId = OdnoklassnikiApplication.getCurrentUser().getId();
        if (TextUtils.isEmpty(currenUserId)) {
            Logger.m184w("Current user not set, cannot proceed with getEvents()");
            return;
        }
        try {
            getEvents(context, currenUserId);
        } catch (Exception e) {
            Logger.m172d("Error get events " + e.getMessage());
        }
    }

    @Subscribe(on = 2131623944, to = 2131623987)
    public void getPromoLinks(BusEvent event) {
        Context context = OdnoklassnikiApplication.getContext();
        if (context == null) {
            Logger.m184w("App context is null, cannot proceed with getEvents().");
            return;
        }
        try {
            GetBannersLinksRequest getBannersLinksRequest = new GetBannersLinksRequest(null, GetStreamProcessor.getBannerOpt(context), PROMO_LINK_TYPES);
            processBannersResponse(OdnoklassnikiApplication.getContext(), null, JsonSessionTransportProvider.getInstance().execJsonHttpMethod(getBannersLinksRequest).getResultAsObject(), System.currentTimeMillis(), PROMO_LINK_TYPES);
        } catch (Exception e) {
            Logger.m176e("Error get promo links" + e.getMessage());
        }
    }

    static {
        PROMO_LINK_TYPES = new BannerLinkType[]{BannerLinkType.HEAD_LINK, BannerLinkType.SIDE_LINK, BannerLinkType.SIDE_LINK_2};
        BANNER_LINK_TYPES = new BannerLinkType[]{BannerLinkType.FEED_BANNER};
        ALL_LINK_TYPES = new BannerLinkType[(PROMO_LINK_TYPES.length + 1)];
        System.arraycopy(PROMO_LINK_TYPES, 0, ALL_LINK_TYPES, 0, PROMO_LINK_TYPES.length);
        ALL_LINK_TYPES[PROMO_LINK_TYPES.length] = BannerLinkType.FEED_BANNER;
    }

    private synchronized void getEvents(Context context, String currentUserId) throws Exception {
        Logger.m172d(">>>");
        BatchRequests requests = new BatchRequests().addRequest(new GetEventsRequest()).addRequest(new UserCountersRequest(null));
        boolean doGetBanners = isTimeToUpdateBanners();
        boolean doGetPromoLinks = isTimeToUpdatePromoLinks();
        boolean doUpdateAppPollTiming = AppPollPreferences.isTimingReadyToUpdate(context) && DeviceUtils.isSmall(context);
        UnreadStream unreadStream = UnreadStream.getInstance(context, currentUserId);
        BaseRequest updateUnreadStreamRequest = unreadStream.getUpdateRequest();
        if (updateUnreadStreamRequest != null) {
            requests.addRequest(updateUnreadStreamRequest);
        } else {
            doGetBanners = false;
        }
        BannerLinkType[] requestedLinkTypes = null;
        if ((doGetPromoLinks | doGetBanners) != 0) {
            requestedLinkTypes = doGetPromoLinks ? doGetBanners ? ALL_LINK_TYPES : PROMO_LINK_TYPES : BANNER_LINK_TYPES;
            requests.addRequest(new GetBannersLinksRequest(null, GetStreamProcessor.getBannerOpt(context), requestedLinkTypes), true);
        }
        if (doUpdateAppPollTiming) {
            requests.addRequest(new SettingsGetRequest(new String[]{"app.poll.interval.*", "presents.*"}, 182), true);
        } else {
            requests.addRequest(new SettingsGetRequest("presents.*", 182), true);
        }
        BaseRequest batchRequest = new BatchRequest(requests, false);
        long requestTime = System.currentTimeMillis();
        JSONObject rootObject = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(batchRequest).getResultAsObject();
        JSONObject bannersResponse = rootObject.optJSONObject("banners_getBannerLinks_response");
        if (bannersResponse != null) {
            processBannersResponse(context, currentUserId, bannersResponse, requestTime, requestedLinkTypes);
        }
        JSONObject settingsResponse = rootObject.optJSONObject("settings_get_response");
        if (settingsResponse != null) {
            if (doUpdateAppPollTiming) {
                AppPollPreferences.parseAndSaveTimingFields(context, settingsResponse);
            }
            PresentSettingsHelper.saveSettings(settingsResponse);
        }
        ArrayList<OdnkEvent> events = new JsonGetEventsParser(rootObject.getJSONArray("events_get_response"), requestTime).parse();
        UserCounters userCounters = new JsonUserCountersParser(rootObject.getJSONObject("users_getCounters_response")).parse();
        events.add(new OdnkEvent("noneUid", String.valueOf(userCounters.friends), EventType.FRIENDS, 0, requestTime));
        events.add(new OdnkEvent("noneUid", String.valueOf(userCounters.groups), EventType.GROUPS, 0, requestTime));
        events.add(new OdnkEvent("noneUid", String.valueOf(userCounters.photosInPhotoAlbums + userCounters.photosPersonal), EventType.UPLOAD_PHOTO, 0, requestTime));
        events.add(new OdnkEvent("noneUid", String.valueOf(userCounters.friendsOnline), EventType.FRIENDS_ONLINE, 0, requestTime));
        events.add(new OdnkEvent("noneUid", String.valueOf(userCounters.holidays), EventType.HOLIDAYS, 0, requestTime));
        handleUnreadStreamResponse(rootObject, unreadStream, events, requestTime);
        EventsManager eventsManager = EventsManager.getInstance();
        if (TextUtils.equals(currentUserId, eventsManager.currentUserId)) {
            eventsManager.setEvents(events);
        } else {
            Logger.m184w("Current user has changed, not updating events");
        }
        Logger.m173d("<<< events=%s", events);
        if (AppPollPreferences.isTimeToLoadPoll(context) && DeviceUtils.isSmall(context)) {
            AppPollProcessor.downloadAndSaveAppPolls();
        }
    }

    private void handleUnreadStreamResponse(JSONObject batchResponse, UnreadStream unreadStream, ArrayList<OdnkEvent> outEvents, long requestTime) {
        JSONObject responseJson = JsonUtil.getJsonObjectSafely(batchResponse, unreadStream.getBatchResponseField());
        int unreadFeedCount = 0;
        if (responseJson != null) {
            UnreadStreamPage unreadPage = unreadStream.handleUpdateResponse(responseJson);
            if (unreadPage != null) {
                unreadFeedCount = unreadPage.getTotalUnreadFeedsCount();
            }
        }
        outEvents.add(new OdnkEvent("noneUid", String.valueOf(unreadFeedCount), EventType.ACTIVITIES, 0, requestTime));
    }

    private void processBannersResponse(Context context, String currentUserId, JSONObject bannersResponse, long requestTime, BannerLinkType[] requestedLinkTypes) throws ResultParsingException {
        ArrayList<PromoLinkBuilder> promoLinks = null;
        ArrayList<PromoLinkBuilder> banners = null;
        Iterator i$ = JsonGetBannersHeadParser.parse(context, bannersResponse, null, requestTime, requestedLinkTypes).iterator();
        while (i$.hasNext()) {
            PromoLinkBuilder promoLinkBuilder = (PromoLinkBuilder) i$.next();
            int linkType = promoLinkBuilder.getType();
            if (BannerLinkType.findByCode(PROMO_LINK_TYPES, linkType) != null) {
                if (promoLinks == null) {
                    promoLinks = new ArrayList();
                }
                promoLinks.add(promoLinkBuilder);
            } else if (BannerLinkType.findByCode(BANNER_LINK_TYPES, linkType) != null) {
                if (banners == null) {
                    banners = new ArrayList();
                }
                banners.add(promoLinkBuilder);
            }
        }
        String str = "processBannersResponse: promoLinks=%d banners=%d";
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(promoLinks == null ? 0 : promoLinks.size());
        objArr[1] = Integer.valueOf(banners == null ? 0 : banners.size());
        Logger.m173d(str, objArr);
        if (promoLinks != null) {
            processPromoLinks(null, promoLinks, PROMO_LINK_TYPES);
        }
        if (banners != null) {
            processBanners(context, currentUserId, banners, requestTime);
        }
    }

    private void processBanners(Context context, String currentUserId, ArrayList<PromoLinkBuilder> bannerLinks, long requestTs) {
        try {
            Logger.m173d("bannerLinks.size=%d", Integer.valueOf(bannerLinks.size()));
            if (bannerLinks.size() > 0) {
                ArrayList<Feed> feeds = new ArrayList();
                HashMap<String, BaseEntityBuilder> entities = new HashMap();
                setBannerUpdateTs(requestTs);
            }
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to process stream.get response with banners");
            StreamErrors.logAndFilterError("process_banners", e.getMessage(), e);
        }
    }

    private boolean isTimeToUpdatePromoLinks() {
        return System.currentTimeMillis() - this.prevPromoLinksRequestTime > 480000;
    }

    private void updatePromoLinksRequestTime() {
        this.prevPromoLinksRequestTime = System.currentTimeMillis();
    }

    private boolean isTimeToUpdateBanners() {
        return false;
    }

    private void setBannerUpdateTs(long ts) {
        this.lastBannerUpdateTs = ts;
    }

    private void processPromoLinks(String userId, ArrayList<PromoLinkBuilder> links, BannerLinkType[] types) {
        try {
            BannerLinksProcessor.processBannerLinksResponse(links, userId, types);
            Logger.m173d("Received promo links: %s", links);
            Bundle output = new Bundle();
            output.putParcelableArrayList("EXTRA_PROMO_LINKS", links);
            GlobalBus.send(2131624235, new BusEvent(output, -1));
        } catch (ResultParsingException e) {
            Logger.m180e(e, "Failed to parse promo links: %s", e);
            StreamErrors.logAndFilterError("process_promo_links", e.getMessage(), e);
        }
        updatePromoLinksRequestTime();
    }
}
