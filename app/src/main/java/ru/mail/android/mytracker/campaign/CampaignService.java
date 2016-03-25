package ru.mail.android.mytracker.campaign;

import android.app.IntentService;
import android.content.Intent;
import ru.mail.android.mytracker.Tracer;
import ru.mail.android.mytracker.async.AsyncCommandExecutor;
import ru.mail.android.mytracker.factories.AsyncCommandsFactory;

public class CampaignService extends IntentService {
    private static volatile String trackerId;

    public static String getTrackerId() {
        return trackerId;
    }

    public static void setTrackerId(String trackerId) {
        trackerId = trackerId;
    }

    public CampaignService() {
        super("MyTrackerCampaignService");
    }

    public void onCreate() {
        Tracer.m38d("CampaignService created");
        super.onCreate();
    }

    public void onDestroy() {
        Tracer.m38d("CampaignService destroyed");
        super.onDestroy();
    }

    protected void onHandleIntent(Intent intent) {
        if (intent.hasExtra("referrer")) {
            String referrer = intent.getStringExtra("referrer");
            if (referrer == null) {
                referrer = "";
            }
            try {
                AsyncCommandExecutor.getExecutor().submit(AsyncCommandsFactory.getTrackReferrerCommand(referrer, trackerId, this)).get();
            } catch (Exception e) {
                Tracer.m38d("Error executing track referrer: " + e);
            }
        }
    }
}
