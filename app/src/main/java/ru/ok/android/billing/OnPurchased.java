package ru.ok.android.billing;

import ru.ok.model.BillingBuyItem;

public interface OnPurchased {
    void onPurchased(BillingBuyItem billingBuyItem);
}
