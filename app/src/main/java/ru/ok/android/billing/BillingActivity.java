package ru.ok.android.billing;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.ui.activity.BaseActivity;

public class BillingActivity extends BaseActivity implements OnDismissListener {
    private BillingHelper billingHelper;

    public BillingActivity() {
        this.billingHelper = null;
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        this.billingHelper = BillingHelper.create(this);
        BillingDialogFragment.showBillingFragment(this);
    }

    public BillingHelper getBillingHelper() {
        return this.billingHelper;
    }

    protected void onDestroy() {
        this.billingHelper = BillingHelper.onDestroy(this.billingHelper);
        super.onDestroy();
    }

    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!BillingHelper.handleActivityResult(requestCode, resultCode, data, getBillingHelper())) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected boolean isSupportToolbarVisible() {
        return false;
    }
}
