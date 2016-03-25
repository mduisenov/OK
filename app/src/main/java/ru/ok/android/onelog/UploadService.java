package ru.ok.android.onelog;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import java.io.IOException;

public class UploadService extends IntentService {
    public UploadService() {
        super("one-append");
    }

    public IBinder onBind(@NonNull Intent intent) {
        return null;
    }

    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null && action.equals("ru.ok.android.onelog.action.UPLOAD")) {
                onHandleUpload(getCollector(intent));
            }
        }
    }

    private void onHandleUpload(String collector) {
        try {
            OneLog log = OneLog.getInstance(collector);
            log.grab();
            log.getUploader().upload();
        } catch (IOException e) {
            Log.e("one-log", "Cannot upload", e);
        }
    }

    private String getCollector(Intent intent) {
        Uri data = intent.getData();
        return data != null ? data.getSchemeSpecificPart() : "external.app.collector";
    }

    public static void startUpload(@NonNull Context context, @NonNull String collector) {
        context.startService(new Intent().setAction("ru.ok.android.onelog.action.UPLOAD").setData(Uri.fromParts("one-log", collector, null)).setClass(context, UploadService.class));
    }
}
