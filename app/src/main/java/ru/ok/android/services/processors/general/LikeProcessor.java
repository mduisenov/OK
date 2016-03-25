package ru.ok.android.services.processors.general;

import android.os.Bundle;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.json.JsonLikeInfoParser;
import ru.ok.java.api.request.like.LikeRequest;
import ru.ok.java.api.request.like.UnLikeRequest;
import ru.ok.model.stream.LikeInfo;

public final class LikeProcessor {
    private ConcurrentHashMap<String, LikeInfoQueue> likeQueuesMap;

    private static class LikeInfoQueue {
        private List<BusEvent> requests;
        private List<BusEvent> responses;

        private LikeInfoQueue() {
            this.requests = new ArrayList();
            this.responses = new ArrayList();
        }

        public synchronized void addRequest(BusEvent request) {
            this.requests.add(request);
        }

        public synchronized void addResponseAndCheckForCombineResult(@AnyRes int kind, BusEvent response) {
            this.responses.add(response);
            if (this.requests.size() == this.responses.size()) {
                GlobalBus.send(kind, combineResultEvent());
                this.requests.clear();
                this.responses.clear();
            }
        }

        private BusEvent combineResultEvent() {
            BusEvent lastSuccessResponse = null;
            long lastSuccessResponseDate = 0;
            for (int i = this.responses.size() - 1; i >= 0; i--) {
                BusEvent response = (BusEvent) this.responses.get(i);
                if (response.resultCode == -1) {
                    long responseDate = ((LikeInfo) response.bundleOutput.get("like_info")).lastDate;
                    if (lastSuccessResponse == null || responseDate > lastSuccessResponseDate) {
                        lastSuccessResponse = response;
                        lastSuccessResponseDate = responseDate;
                    }
                }
            }
            if (lastSuccessResponse != null) {
                return lastSuccessResponse;
            }
            BusEvent ret = (BusEvent) this.responses.get(this.responses.size() - 1);
            ret.bundleOutput.putParcelable("like_info", ((BusEvent) this.requests.get(0)).bundleInput.getParcelable("like_info_origin"));
            return ret;
        }
    }

    public LikeProcessor() {
        this.likeQueuesMap = new ConcurrentHashMap();
    }

    @Subscribe(on = 2131623944, to = 2131624009)
    public void like(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        processRequest(2131624184, event, bundleInput, new Bundle(), bundleInput.getString("like_id"), true, bundleInput.getString("LOG_CONTEXT"));
    }

    @Subscribe(on = 2131623944, to = 2131624117)
    public void unlike(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        processRequest(2131624261, event, bundleInput, new Bundle(), bundleInput.getString("like_id"), false, bundleInput.getString("LOG_CONTEXT"));
    }

    @NonNull
    public static LikeInfo performLikeRequest(@NonNull String likeId, boolean like, @Nullable String logContext) throws BaseApiException {
        try {
            JSONObject likeJson = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(like ? new LikeRequest(likeId, logContext) : new UnLikeRequest(likeId, logContext)).getResultAsObject().optJSONObject("summary");
            if (likeJson != null) {
                return new JsonLikeInfoParser(likeJson).parse();
            }
            throw new ResultParsingException("'summary' field not found in response");
        } catch (JSONException e) {
            throw new ResultParsingException(e);
        }
    }

    private void processRequest(@AnyRes int responseKind, BusEvent event, Bundle bundleInput, Bundle bundleOutput, String lid, boolean like, String logContext) {
        int resultCode = -2;
        try {
            queueLikeRequest(lid, event);
            bundleOutput.putParcelable("like_info", performLikeRequest(lid, like, logContext));
            resultCode = -1;
        } catch (Exception exc) {
            Logger.m180e(exc, "Can't like/unlike object: likeID=%s like=%s", lid, Boolean.valueOf(like));
        }
        ((LikeInfoQueue) this.likeQueuesMap.get(lid)).addResponseAndCheckForCombineResult(responseKind, new BusEvent(bundleInput, bundleOutput, resultCode));
    }

    private void queueLikeRequest(String lid, BusEvent request) {
        Throwable th;
        LikeInfoQueue likeInfoQueue = (LikeInfoQueue) this.likeQueuesMap.get(lid);
        if (likeInfoQueue == null) {
            synchronized (this.likeQueuesMap) {
                likeInfoQueue = (LikeInfoQueue) this.likeQueuesMap.get(lid);
                if (likeInfoQueue == null) {
                    LikeInfoQueue likeInfoQueue2 = new LikeInfoQueue();
                    try {
                        this.likeQueuesMap.put(lid, likeInfoQueue2);
                        likeInfoQueue = likeInfoQueue2;
                    } catch (Throwable th2) {
                        th = th2;
                        likeInfoQueue = likeInfoQueue2;
                        throw th;
                    }
                }
                try {
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        likeInfoQueue.addRequest(request);
    }
}
