package ru.ok.android.billing;

import ru.ok.android.billingUtils.IabHelper.OnConsumeFinishedListener;
import ru.ok.android.billingUtils.IabResult;
import ru.ok.android.billingUtils.Purchase;
import ru.ok.model.BillingBuyItem;

public class BillingOnConsumeFinished implements OnConsumeFinishedListener {
    private final BillingBuyItem billingBuyItem;
    private final OnPurchased onPurchased;

    public BillingOnConsumeFinished(BillingBuyItem billingBuyItem, OnPurchased onPurchased) {
        this.billingBuyItem = billingBuyItem;
        this.onPurchased = onPurchased;
    }

    public void onConsumeFinished(Purchase purchase, IabResult result) {
        if (this.onPurchased != null) {
            this.onPurchased.onPurchased(this.billingBuyItem);
        }
    }
}
