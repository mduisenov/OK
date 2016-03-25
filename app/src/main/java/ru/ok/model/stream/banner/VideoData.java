package ru.ok.model.stream.banner;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VideoData implements Parcelable {
    public static final Creator<VideoData> CREATOR;
    private static final Comparator<VideoProgressStat> progressComparator;
    public final int durationSec;
    final List<VideoStat>[] statEventsByType;
    public final String videoUrl;

    /* renamed from: ru.ok.model.stream.banner.VideoData.1 */
    static class C16081 implements Comparator<VideoProgressStat> {
        C16081() {
        }

        public int compare(VideoProgressStat lhs, VideoProgressStat rhs) {
            return lhs.positionSec - rhs.positionSec;
        }
    }

    /* renamed from: ru.ok.model.stream.banner.VideoData.2 */
    static class C16092 implements Creator<VideoData> {
        C16092() {
        }

        public VideoData createFromParcel(Parcel source) {
            return new VideoData(source);
        }

        public VideoData[] newArray(int size) {
            return new VideoData[size];
        }
    }

    VideoData(String videoUrl, int durationSec, List<VideoStat>[] statEventsByType) {
        this.videoUrl = videoUrl;
        this.durationSec = durationSec;
        this.statEventsByType = statEventsByType;
    }

    public VideoData(String videoUrl, int durationSec, List<VideoStat> statEvents) {
        this.videoUrl = videoUrl;
        this.durationSec = durationSec;
        this.statEventsByType = new List[5];
        initStatEvents(statEvents);
    }

    private void initStatEvents(List<VideoStat> statEvents) {
        if (statEvents != null) {
            for (VideoStat event : statEvents) {
                if (event.type >= 0 && event.type < this.statEventsByType.length) {
                    if (event.type != 1 || (event instanceof VideoProgressStat)) {
                        List<VideoStat> events = this.statEventsByType[event.type];
                        if (events == null) {
                            List[] listArr = this.statEventsByType;
                            int i = event.type;
                            events = new ArrayList();
                            listArr[i] = events;
                        }
                        events.add(event);
                    }
                }
            }
            List<VideoProgressStat> progressEvents = getProgressStats();
            if (!progressEvents.isEmpty()) {
                Collections.sort(progressEvents, progressComparator);
            }
        }
    }

    public List<VideoProgressStat> getProgressStats() {
        return getStats(1);
    }

    public List<VideoStat> getStats(int type) {
        List<VideoStat> stats = null;
        if (type >= 0 && type < this.statEventsByType.length) {
            stats = this.statEventsByType[type];
        }
        if (stats == null) {
            return Collections.emptyList();
        }
        return stats;
    }

    static {
        progressComparator = new C16081();
        CREATOR = new C16092();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.videoUrl);
        dest.writeInt(this.durationSec);
        dest.writeByte(this.statEventsByType != null ? (byte) 1 : (byte) 0);
        if (this.statEventsByType != null) {
            dest.writeInt(this.statEventsByType.length);
            for (List<VideoStat> list : this.statEventsByType) {
                dest.writeList(list);
            }
        }
    }

    protected VideoData(Parcel src) {
        this.videoUrl = src.readString();
        this.durationSec = src.readInt();
        ClassLoader cl = VideoData.class.getClassLoader();
        if (src.readByte() != null) {
            this.statEventsByType = new List[src.readInt()];
            for (int i = 0; i < this.statEventsByType.length; i++) {
                this.statEventsByType[i] = src.readArrayList(cl);
            }
            return;
        }
        this.statEventsByType = new List[0];
    }
}
