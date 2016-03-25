package ru.ok.android.ui.presents.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.ui.presents.BusWithEventStates;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.android.utils.bus.BusReceivePresentHelper;
import ru.ok.java.api.response.presents.PresentNotificationResponse;

public class PreloadPresentReceivedActivity extends BasePresentActivity {
    private final BusWithEventStates busWithEventStates;
    private String keyLoadNotificationInfo;
    private String presentNotificationId;

    public PreloadPresentReceivedActivity() {
        this.busWithEventStates = BusWithEventStates.getInstance();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.keyLoadNotificationInfo = savedInstanceState.getString("key_load_notification_info");
        }
        this.presentNotificationId = getIntent().getStringExtra("present_notification_id");
        if (this.keyLoadNotificationInfo == null) {
            loadNotification();
        } else if (this.busWithEventStates.isProcessing(this.keyLoadNotificationInfo)) {
            showProgressDialog();
        } else {
            finish();
        }
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.keyLoadNotificationInfo != null) {
            outState.putString("key_load_notification_info", this.keyLoadNotificationInfo);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624189)
    public void onDataLoaded(@NonNull BusEvent event) {
        if (this.busWithEventStates.isResultForKey(event, this.keyLoadNotificationInfo)) {
            hideProgressDialog();
            if (event.resultCode == -1) {
                PresentNotificationResponse notificationResponse = (PresentNotificationResponse) event.bundleOutput.getParcelable("PRESENT_NOTIFICATION");
                finish();
                startActivity(PresentReceivedActivity.createIntent(this, this.presentNotificationId, notificationResponse));
                return;
            }
            finish();
            NavigationHelper.showExternalUrlPage((Activity) this, WebUrlCreator.getNotificationPageUrl(), false);
        }
    }

    private void loadNotification() {
        showProgressDialogDelayed();
        this.keyLoadNotificationInfo = BusReceivePresentHelper.loadPresentNotification(this.presentNotificationId);
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull String presentNotificationId) {
        Intent intent = new Intent(context, PreloadPresentReceivedActivity.class);
        intent.putExtra("present_notification_id", presentNotificationId);
        return intent;
    }
}
