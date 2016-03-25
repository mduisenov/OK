package ru.ok.android.ui.custom.video;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import java.util.Iterator;
import java.util.List;
import ru.mail.android.mytarget.core.async.Sender;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.banner.VideoData;
import ru.ok.model.stream.banner.VideoProgressStat;
import ru.ok.model.stream.banner.VideoStat;

public class VideoBannerStatEventHandler implements VideoStatEventHandler {
    private final Context context;
    private final VideoData data;
    private int maxReachedProgressSec;
    private final Iterator<VideoProgressStat> nextProgressItr;
    private VideoProgressStat nextReport;

    public VideoBannerStatEventHandler(Context context, VideoData data) {
        this.maxReachedProgressSec = LinearLayoutManager.INVALID_OFFSET;
        this.context = context;
        this.data = data;
        List<VideoProgressStat> progressStats = data.getProgressStats();
        if (progressStats != null) {
            this.nextProgressItr = progressStats.iterator();
            if (this.nextProgressItr.hasNext()) {
                this.nextReport = (VideoProgressStat) this.nextProgressItr.next();
                return;
            }
            return;
        }
        this.nextProgressItr = null;
    }

    public void playbackStarted() {
        Logger.m172d("");
        sendStats(0);
    }

    public void playbackCompleted() {
        Logger.m172d("");
        sendStats(2);
    }

    public void playbackResumed() {
        Logger.m172d("");
        sendStats(4);
    }

    public void playbackPaused() {
        Logger.m172d("");
        sendStats(3);
    }

    public void playHeadReachedPosition(int positionSec) {
        if (positionSec > this.maxReachedProgressSec) {
            this.maxReachedProgressSec = positionSec;
            while (this.nextReport != null && this.nextReport.positionSec <= positionSec) {
                Logger.m173d("positionSec=%d", Integer.valueOf(positionSec));
                Sender.addStat(this.nextReport.url, this.context);
                if (this.nextProgressItr.hasNext()) {
                    this.nextReport = (VideoProgressStat) this.nextProgressItr.next();
                } else {
                    this.nextReport = null;
                }
            }
        }
    }

    private void sendStats(int type) {
        List<VideoStat> stats = this.data.getStats(type);
        if (stats != null) {
            for (VideoStat stat : stats) {
                Sender.addStat(stat.url, this.context);
            }
        }
    }
}
