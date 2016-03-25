package ru.ok.android.onelog;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import io.github.eterverda.sntp.SNTP;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.onelog.app.launch.AppLaunchPushNotificationSubSource;
import ru.ok.onelog.app.push.PushDeliveryFactory;
import ru.ok.onelog.builtin.DurationInterval;

public class PushDeliveryLog {
    private static final PushDeliveryEventsQueue EVENTS_QUEUE;

    private static class PushDeliveryEventsQueue implements Callback {
        private final Handler handler;
        private boolean inProgress;
        private float power;
        private final Queue<PushDeliveryEvent> pushEventsQueue;
        private final SntpSyncTask sntpSyncTask;

        private static class PushDeliveryEvent {
            final long creationWorldTime;
            final long deliveryLocalRealTime;
            final AppLaunchPushNotificationSubSource type;

            PushDeliveryEvent(AppLaunchPushNotificationSubSource type, long creationWorldTime, long deliveryLocalRealTime) {
                this.type = type;
                this.creationWorldTime = creationWorldTime;
                this.deliveryLocalRealTime = deliveryLocalRealTime;
            }

            public long getDeliveryWorldTime(long currentWorldTime) {
                return currentWorldTime - (SystemClock.elapsedRealtime() - this.deliveryLocalRealTime);
            }
        }

        private static class SntpSyncTask implements Runnable {
            private final Handler handler;

            SntpSyncTask(Handler handler) {
                this.handler = handler;
            }

            public void run() {
                try {
                    this.handler.sendMessage(this.handler.obtainMessage(3, Long.valueOf(SNTP.currentTimeMillis())));
                } catch (IOException e) {
                    Logger.m172d("SNTP sync failed." + e.getLocalizedMessage());
                    this.handler.sendEmptyMessage(2);
                }
            }
        }

        private PushDeliveryEventsQueue() {
            this.pushEventsQueue = new LinkedList();
            this.handler = new Handler(Looper.getMainLooper(), this);
            this.sntpSyncTask = new SntpSyncTask(this.handler);
            this.inProgress = false;
            this.power = 1.0f;
        }

        public void add(AppLaunchPushNotificationSubSource type, long creationWorldTime, long deliveryLocalRealTime) {
            this.pushEventsQueue.add(new PushDeliveryEvent(type, creationWorldTime, deliveryLocalRealTime));
            if (!this.handler.hasMessages(1) && !this.inProgress) {
                this.handler.sendEmptyMessage(1);
            }
        }

        private void onSyncSuccess(long worldTime) {
            while (!this.pushEventsQueue.isEmpty()) {
                PushDeliveryEvent pushEvent = (PushDeliveryEvent) this.pushEventsQueue.poll();
                PushDeliveryLog.log(pushEvent.type, pushEvent.creationWorldTime, pushEvent.getDeliveryWorldTime(worldTime));
            }
        }

        public boolean handleMessage(Message msg) {
            float f = 50.0f;
            switch (msg.what) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    this.inProgress = true;
                    ThreadUtil.execute(this.sntpSyncTask);
                    break;
                case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                    this.handler.removeCallbacksAndMessages(null);
                    if (this.power < 50.0f) {
                        f = this.power * 1.1f;
                    }
                    this.power = f;
                    this.handler.sendEmptyMessageDelayed(1, (long) Math.pow(15000.0d, (double) this.power));
                    break;
                case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                    onSyncSuccess(((Long) msg.obj).longValue());
                    this.inProgress = false;
                    this.power = 1.0f;
                    break;
            }
            return true;
        }
    }

    public static void discussion(long creationTime) {
        enqueue(AppLaunchPushNotificationSubSource.discussion, creationTime);
    }

    public static void presents(long creationTime) {
        enqueue(AppLaunchPushNotificationSubSource.presents, creationTime);
    }

    public static void general(long creationTime) {
        enqueue(AppLaunchPushNotificationSubSource.general, creationTime);
    }

    public static void dflt(long creationTime) {
        enqueue(AppLaunchPushNotificationSubSource.dflt, creationTime);
    }

    public static void conversation(long creationTime) {
        enqueue(AppLaunchPushNotificationSubSource.conversation, creationTime);
    }

    public static void openUri(long creationTime) {
        enqueue(AppLaunchPushNotificationSubSource.open_uri, creationTime);
    }

    private static void enqueue(AppLaunchPushNotificationSubSource pushType, long creationWorldTime) {
        EVENTS_QUEUE.add(pushType, creationWorldTime, SystemClock.elapsedRealtime());
    }

    private static void log(AppLaunchPushNotificationSubSource type, long creationWorldTime, long deliveryWorldTime) {
        long dt = deliveryWorldTime - creationWorldTime;
        if (dt < 0) {
            dt = 0;
        }
        OneLog.log(PushDeliveryFactory.get(DurationInterval.valueOfMillis(dt), type));
    }

    static {
        EVENTS_QUEUE = new PushDeliveryEventsQueue();
    }
}
