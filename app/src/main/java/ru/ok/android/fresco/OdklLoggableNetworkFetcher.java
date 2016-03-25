package ru.ok.android.fresco;

import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.producers.BaseNetworkFetcher;
import com.facebook.imagepipeline.producers.BaseProducerContextCallbacks;
import com.facebook.imagepipeline.producers.Consumer;
import com.facebook.imagepipeline.producers.FetchState;
import com.facebook.imagepipeline.producers.NetworkFetcher.Callback;
import com.facebook.imagepipeline.producers.ProducerContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OdklLoggableNetworkFetcher extends BaseNetworkFetcher<FetchState> {
    private final ExecutorService mExecutorService;

    /* renamed from: ru.ok.android.fresco.OdklLoggableNetworkFetcher.1 */
    class C03441 implements Runnable {
        final /* synthetic */ Callback val$callback;
        final /* synthetic */ FetchState val$fetchState;

        C03441(FetchState fetchState, Callback callback) {
            this.val$fetchState = fetchState;
            this.val$callback = callback;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r12 = this;
            r1 = 0;
            r10 = r12.val$fetchState;
            r7 = r10.getUri();
            r6 = r7.getScheme();
            r10 = r12.val$fetchState;
            r10 = r10.getUri();
            r8 = r10.toString();
        L_0x0015:
            r10 = com.facebook.network.connectionclass.DeviceBandwidthSampler.getInstance();	 Catch:{ Exception -> 0x006a }
            r10.startSampling();	 Catch:{ Exception -> 0x006a }
            r9 = new java.net.URL;	 Catch:{ Exception -> 0x006a }
            r9.<init>(r8);	 Catch:{ Exception -> 0x006a }
            r10 = r9.openConnection();	 Catch:{ Exception -> 0x006a }
            r0 = r10;
            r0 = (java.net.HttpURLConnection) r0;	 Catch:{ Exception -> 0x006a }
            r1 = r0;
            r10 = "Location";
            r5 = r1.getHeaderField(r10);	 Catch:{ Exception -> 0x006a }
            if (r5 != 0) goto L_0x0052;
        L_0x0032:
            r4 = 0;
        L_0x0033:
            if (r5 == 0) goto L_0x003b;
        L_0x0035:
            r10 = r4.equals(r6);	 Catch:{ Exception -> 0x006a }
            if (r10 == 0) goto L_0x005b;
        L_0x003b:
            r3 = r1.getInputStream();	 Catch:{ Exception -> 0x006a }
            r10 = r12.val$callback;	 Catch:{ Exception -> 0x006a }
            r11 = -1;
            r10.onResponse(r3, r11);	 Catch:{ Exception -> 0x006a }
            if (r1 == 0) goto L_0x004a;
        L_0x0047:
            r1.disconnect();
        L_0x004a:
            r10 = com.facebook.network.connectionclass.DeviceBandwidthSampler.getInstance();
            r10.stopSampling();
        L_0x0051:
            return;
        L_0x0052:
            r10 = android.net.Uri.parse(r5);	 Catch:{ Exception -> 0x006a }
            r4 = r10.getScheme();	 Catch:{ Exception -> 0x006a }
            goto L_0x0033;
        L_0x005b:
            r8 = r5;
            r6 = r4;
            if (r1 == 0) goto L_0x0062;
        L_0x005f:
            r1.disconnect();
        L_0x0062:
            r10 = com.facebook.network.connectionclass.DeviceBandwidthSampler.getInstance();
            r10.stopSampling();
            goto L_0x0015;
        L_0x006a:
            r2 = move-exception;
            r10 = r12.val$callback;	 Catch:{ all -> 0x007d }
            r10.onFailure(r2);	 Catch:{ all -> 0x007d }
            if (r1 == 0) goto L_0x0075;
        L_0x0072:
            r1.disconnect();
        L_0x0075:
            r10 = com.facebook.network.connectionclass.DeviceBandwidthSampler.getInstance();
            r10.stopSampling();
            goto L_0x0051;
        L_0x007d:
            r10 = move-exception;
            if (r1 == 0) goto L_0x0083;
        L_0x0080:
            r1.disconnect();
        L_0x0083:
            r11 = com.facebook.network.connectionclass.DeviceBandwidthSampler.getInstance();
            r11.stopSampling();
            throw r10;
            */
            throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.fresco.OdklLoggableNetworkFetcher.1.run():void");
        }
    }

    /* renamed from: ru.ok.android.fresco.OdklLoggableNetworkFetcher.2 */
    class C03452 extends BaseProducerContextCallbacks {
        final /* synthetic */ Callback val$callback;
        final /* synthetic */ Future val$future;

        C03452(Future future, Callback callback) {
            this.val$future = future;
            this.val$callback = callback;
        }

        public void onCancellationRequested() {
            if (this.val$future.cancel(false)) {
                this.val$callback.onCancellation();
            }
        }
    }

    public OdklLoggableNetworkFetcher() {
        this.mExecutorService = Executors.newFixedThreadPool(3);
    }

    public FetchState createFetchState(Consumer<EncodedImage> consumer, ProducerContext context) {
        return new FetchState(consumer, context);
    }

    public void fetch(FetchState fetchState, Callback callback) {
        fetchState.getContext().addCallbacks(new C03452(this.mExecutorService.submit(new C03441(fetchState, callback)), callback));
    }
}
