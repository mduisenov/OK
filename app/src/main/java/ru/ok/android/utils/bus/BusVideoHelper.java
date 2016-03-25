package ru.ok.android.utils.bus;

import android.os.Bundle;
import java.util.ArrayList;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;

public final class BusVideoHelper {
    public static void getVideoInfo(String videoId) {
        ArrayList<String> ids = new ArrayList(1);
        ids.add(videoId);
        Bundle args = new Bundle();
        args.putStringArrayList("VIDEO_IDS", ids);
        GlobalBus.send(2131624124, new BusEvent(args));
    }

    public static ArrayList<String> getIds(Bundle args) {
        ArrayList<String> array = args.getStringArrayList("VIDEO_IDS");
        if (array == null) {
            return new ArrayList();
        }
        return array;
    }

    public static void getSimilarVideoInfos(String videoId) {
        Bundle args = new Bundle();
        args.putString("VIDEO_ID", videoId);
        GlobalBus.send(2131624125, new BusEvent(args));
    }
}
