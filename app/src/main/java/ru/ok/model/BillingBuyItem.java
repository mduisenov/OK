package ru.ok.model;

public enum BillingBuyItem {
    PACKET_20("ru.odnoklassniki.android.20ok"),
    PACKET_40("ru.odnoklassniki.android.40ok"),
    PACKET_80("ru.odnoklassniki.android.80ok"),
    PACKET_100("ru.odnoklassniki.android.100ok");
    
    public final String sku;

    private BillingBuyItem(String sku) {
        this.sku = sku;
    }
}
