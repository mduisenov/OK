package ru.ok.android.services.processors.video;

import android.os.Bundle;
import java.util.ArrayList;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.video.OneLogVideo;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.json.video.VideosGetParser;
import ru.ok.java.api.request.video.VideoGetRequest;
import ru.ok.java.api.request.video.VideoGetRequest.ADVERTISEMENT_FIELDS;
import ru.ok.java.api.request.video.VideoGetRequest.FIELDS;
import ru.ok.java.api.request.video.VideoGetRequest.LIKE_FIELDS;
import ru.ok.java.api.response.video.VideoGetResponse;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;

public final class VideoProcessor {
    private static String fieldsString;

    static {
        fieldsString = createFieldsString();
    }

    @Subscribe(on = 2131623944, to = 2131624124)
    public void getVideosInfo(BusEvent e) {
        ArrayList<String> videoIds = e.bundleInput.getStringArrayList("VIDEO_IDS");
        try {
            long timeStart = System.currentTimeMillis();
            JsonHttpResult response = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new VideoGetRequest(videoIds, fieldsString));
            long timeEnd = System.currentTimeMillis();
            ArrayList<VideoGetResponse> result = new VideosGetParser(response.getResultAsObject()).parse();
            Bundle output = new Bundle();
            output.putParcelableArrayList("VIDEO_INFOS", result);
            GlobalBus.send(2131624267, new BusEvent(e.bundleInput, output, -1));
            logFirstTime(timeStart, timeEnd, videoIds);
        } catch (Exception ex) {
            Logger.m180e(ex, "Failed to fetch video infos id: %s", videoIds);
            GlobalBus.send(2131624267, new BusEvent(e.bundleInput, CommandProcessor.createErrorBundle(ex), -2));
        }
    }

    private static void logFirstTime(long timeStart, long timeEnd, ArrayList<String> videoIds) {
        if (videoIds.size() > 0) {
            OneLogVideo.logFirstBytesTime(Long.valueOf((String) videoIds.get(0)).longValue(), timeEnd - timeStart);
        }
    }

    private static String createFieldsString() {
        RequestFieldsBuilder fields = new RequestFieldsBuilder();
        fields.withPrefix("video.");
        fields.addFields(FIELDS.values());
        RequestFieldsBuilder likeFields = new RequestFieldsBuilder();
        likeFields.withPrefix("like_summary.");
        likeFields.addFields(LIKE_FIELDS.values());
        RequestFieldsBuilder advertisementFields = new RequestFieldsBuilder();
        advertisementFields.withPrefix("video_advertisement.");
        advertisementFields.addFields(ADVERTISEMENT_FIELDS.values());
        return fields.build() + ',' + likeFields.build() + ',' + advertisementFields.build();
    }
}
