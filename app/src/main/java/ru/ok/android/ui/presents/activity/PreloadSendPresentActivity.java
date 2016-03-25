package ru.ok.android.ui.presents.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.ExternalUrlWebFragment;
import ru.ok.android.fragments.web.hooks.HookFinishActivityProcessor;
import ru.ok.android.fragments.web.shortlinks.SendPresentShortLinkBuilder;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.presents.BusWithEventStates;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.android.utils.bus.BusSendPresentHelper;
import ru.ok.java.api.response.presents.SendInfoResponse;

public class PreloadSendPresentActivity extends BasePresentActivity {
    private final BusWithEventStates busWithEventStates;
    private String keyLoadInfo;
    private SendPresentShortLinkBuilder linkBuilder;
    private String presentId;
    private SendInfoResponse sendInfoResponse;
    private String userId;

    public PreloadSendPresentActivity() {
        this.busWithEventStates = BusWithEventStates.getInstance();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.linkBuilder = (SendPresentShortLinkBuilder) getIntent().getParcelableExtra("link_builder");
        this.presentId = this.linkBuilder.getPresentId();
        this.userId = this.linkBuilder.getUserId();
        if (savedInstanceState != null) {
            this.keyLoadInfo = savedInstanceState.getString("key_load_info");
        }
        if (this.userId == null) {
            this.userId = OdnoklassnikiApplication.getCurrentUser().getId();
            this.linkBuilder.setUser(this.userId);
        }
        if (this.keyLoadInfo == null) {
            loadData();
        } else if (this.busWithEventStates.isProcessing(this.keyLoadInfo)) {
            showProgressDialog();
        } else {
            finish();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            loadData();
        } else {
            finish();
        }
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.keyLoadInfo != null) {
            outState.putString("key_load_info", this.keyLoadInfo);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624188)
    public void onDataLoaded(@NonNull BusEvent event) {
        if (this.busWithEventStates.isResultForKey(event, this.keyLoadInfo)) {
            hideProgressDialog();
            if (event.resultCode == -1) {
                boolean firstRequest = this.sendInfoResponse == null;
                this.sendInfoResponse = (SendInfoResponse) event.bundleOutput.getParcelable("EXTRA_RESPONSE");
                if (this.sendInfoResponse.balancesResponse.userBalanceInOks >= 0) {
                    finish();
                    startActivity(SendPresentActivity.createIntent(this, this.sendInfoResponse, this.linkBuilder));
                    return;
                } else if (firstRequest) {
                    openPaymentActivity();
                    return;
                } else {
                    finish();
                    return;
                }
            }
            finish();
            showToast(ErrorType.from(event.bundleOutput).getDefaultErrorMessage());
        }
    }

    private void loadData() {
        showProgressDialogDelayed();
        this.keyLoadInfo = BusSendPresentHelper.loadPresentAndUser(this.presentId, this.userId);
    }

    private void openPaymentActivity() {
        ActivityExecutor builder = new ActivityExecutor(this, ExternalUrlWebFragment.class);
        builder.setArguments(ExternalUrlWebFragment.newArguments(WebUrlCreator.getPresentPaymentUrl(this.sendInfoResponse.presentInfo.price, HookFinishActivityProcessor.makeHookUrlWithResult(-1))));
        builder.setRequestCode(1);
        builder.execute();
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull SendPresentShortLinkBuilder linkBuilder) {
        Intent intent = new Intent(context, PreloadSendPresentActivity.class);
        intent.putExtra("link_builder", linkBuilder);
        return intent;
    }
}
