package ru.ok.android.billing;

import ru.ok.android.billingUtils.IabHelper.OnIabPurchaseFinishedListener;
import ru.ok.android.billingUtils.IabResult;
import ru.ok.android.billingUtils.Purchase;

public class BillingOnIabPurchaseFinishedListener implements OnIabPurchaseFinishedListener {
    private final BillingDialogFragment billingDialogFragment;
    private final BillingOnConsumeFinished consumeFinishedListener;

    public BillingOnIabPurchaseFinishedListener(BillingOnConsumeFinished consumeFinishedListener, BillingDialogFragment billingDialogFragment) {
        this.billingDialogFragment = billingDialogFragment;
        this.consumeFinishedListener = consumeFinishedListener;
    }

    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        if (result.isFailure()) {
            this.billingDialogFragment.updateGui();
        } else {
            BillingHelper.consume(purchase, this.consumeFinishedListener, this.billingDialogFragment);
        }
    }
}
