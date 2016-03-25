package ru.ok.android.statistics.stream;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import java.util.ArrayList;
import java.util.List;
import ru.mail.android.mytarget.core.async.Sender;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.storage.IFeedBannerStatsStorage;
import ru.ok.android.storage.StorageException;
import ru.ok.android.storage.Storages;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.Feed;

public class StreamBannerStatisticsHandler {
    @NonNull
    final Context context;
    @NonNull
    final SendHandler handler;
    @NonNull
    final IFeedBannerStatsStorage statsStorage;

    class SendHandler extends Handler {
        public SendHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    StreamBannerStatisticsHandler.this.handleShownOnScroll(msg.obj);
                case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                    StreamBannerStatisticsHandler.this.handleClick(msg.obj);
                default:
            }
        }
    }

    public StreamBannerStatisticsHandler(@NonNull Context context) {
        this.context = context;
        this.statsStorage = Storages.getInstance(context, OdnoklassnikiApplication.getCurrentUser().getId()).getFeedBannerStatsStorage();
        HandlerThread thread = new HandlerThread("BannerStats.Send", 10);
        thread.start();
        this.handler = new SendHandler(thread.getLooper());
    }

    public void dispose() {
        this.handler.getLooper().quit();
    }

    public void onShownOnScroll(@NonNull Feed feed) {
        this.handler.sendMessage(Message.obtain(this.handler, 1, feed));
    }

    public void onClick(@NonNull ArrayList<String> urls) {
        this.handler.sendMessage(Message.obtain(this.handler, 2, urls));
    }

    private static void send(Context context, String logTag, ArrayList<String> urls) {
        if (Logger.isLoggingEnable() && urls != null) {
            int size = urls.size();
            for (int i = 0; i < size; i++) {
                Logger.m173d("%s: url[%d]=%s", logTag, Integer.valueOf(i), urls.get(i));
            }
        }
        Sender.addStat((List) urls, context);
    }

    public static void handleShown(Context context, Feed feed) {
        try {
            ArrayList<String> urls = feed.getStatPixels(0);
            if (urls != null && !urls.isEmpty() && !Storages.getInstance(context, OdnoklassnikiApplication.getCurrentUser().getId()).getFeedBannerStatsStorage().checkSaveFeedIsShown(feed.getUuid())) {
                Logger.m173d("send shown for uuid=%s", feed.getUuid());
                send(context, "shown", urls);
            }
        } catch (StorageException e) {
            Logger.m180e(e, "Failed to check stats: %s", e);
        }
    }

    private void handleShownOnScroll(Feed feed) {
        try {
            ArrayList<String> urls = feed.getStatPixels(1);
            if (urls != null && !urls.isEmpty() && !this.statsStorage.checkSaveFeedIsShownOnScroll(feed.getUuid())) {
                Logger.m173d("send shownOnScroll for uuid=%s", feed.getUuid());
                send(this.context, "shownOnScroll", urls);
            }
        } catch (StorageException e) {
            Logger.m180e(e, "Failed to check stats: %s", e);
        }
    }

    private void handleClick(ArrayList<String> urls) {
        if (urls != null && !urls.isEmpty()) {
            send(this.context, ProductAction.ACTION_CLICK, urls);
        }
    }
}
