package ru.ok.android.onelog.api;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionClassManager.ConnectionClassStateChangeListener;
import com.facebook.network.connectionclass.ConnectionQuality;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.utils.CountryUtil;
import ru.ok.android.utils.datastructure.RingBuffer;
import ru.ok.onelog.api.ApiMethodCallFactory;
import ru.ok.onelog.util.NetworkQuality;

public final class ApiRequestsReporter {
    private static final Handler apiRequestReportHandler;

    /* renamed from: ru.ok.android.onelog.api.ApiRequestsReporter.1 */
    static /* synthetic */ class C03841 {
        static final /* synthetic */ int[] f71xe72178e6;

        static {
            f71xe72178e6 = new int[ConnectionQuality.values().length];
            try {
                f71xe72178e6[ConnectionQuality.POOR.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f71xe72178e6[ConnectionQuality.MODERATE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f71xe72178e6[ConnectionQuality.GOOD.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f71xe72178e6[ConnectionQuality.EXCELLENT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private static class ApiReportCallback implements Callback {
        private RingBuffer<ApiRequestLogParams> buffer;
        private String countryCode;
        private NetworkQuality networkQuality;

        private ApiReportCallback() {
            this.buffer = new RingBuffer(20);
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case RECEIVED_VALUE:
                    this.buffer.rotate((ApiRequestLogParams) msg.obj);
                    report();
                    break;
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    tryAssignNetworkQuality((ConnectionQuality) msg.obj);
                    report();
                    break;
            }
            return false;
        }

        private void tryAssignNetworkQuality(ConnectionQuality connectionQuality) {
            switch (C03841.f71xe72178e6[connectionQuality.ordinal()]) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    this.networkQuality = NetworkQuality.poor;
                case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                    this.networkQuality = NetworkQuality.moderate;
                case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                    this.networkQuality = NetworkQuality.good;
                case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    this.networkQuality = NetworkQuality.excellent;
                default:
            }
        }

        private void report() {
            if (validateReport()) {
                while (!this.buffer.isEmpty()) {
                    ApiRequestLogParams params = (ApiRequestLogParams) this.buffer.poll();
                    OneLog.log(ApiMethodCallFactory.get(params.method, params.time, this.countryCode, this.networkQuality));
                }
            }
        }

        private boolean validateReport() {
            if (this.countryCode == null) {
                this.countryCode = CountryUtil.tryGetCurrentCountryCode();
            }
            if (this.networkQuality == null) {
                tryAssignNetworkQuality(ConnectionClassManager.getInstance().getCurrentBandwidthQuality());
            }
            return (this.networkQuality == null || this.countryCode == null) ? false : true;
        }
    }

    private static class ApiRequestLogParams {
        private final String method;
        private final long time;

        ApiRequestLogParams(String method, long time) {
            this.method = method;
            this.time = time;
        }
    }

    public static class ConnectionClassChangeListener implements ConnectionClassStateChangeListener {
        public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
            Message msg = ApiRequestsReporter.apiRequestReportHandler.obtainMessage(1);
            msg.obj = bandwidthState;
            msg.sendToTarget();
        }
    }

    static {
        apiRequestReportHandler = new Handler(Looper.getMainLooper(), new ApiReportCallback());
    }

    public static void report(String method, long time) {
        Message message = apiRequestReportHandler.obtainMessage(0);
        message.obj = new ApiRequestLogParams(method, time);
        message.sendToTarget();
    }
}
