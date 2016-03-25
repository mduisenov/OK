package ru.ok.android.billingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    Map<String, Purchase> mPurchaseMap;
    Map<String, SkuDetails> mSkuMap;

    Inventory() {
        this.mSkuMap = new HashMap();
        this.mPurchaseMap = new HashMap();
    }

    public Purchase getPurchase(String sku) {
        return (Purchase) this.mPurchaseMap.get(sku);
    }

    List<String> getAllOwnedSkus(String itemType) {
        List<String> result = new ArrayList();
        for (Purchase p : this.mPurchaseMap.values()) {
            if (p.getItemType().equals(itemType)) {
                result.add(p.getSku());
            }
        }
        return result;
    }

    void addSkuDetails(SkuDetails d) {
        this.mSkuMap.put(d.getSku(), d);
    }

    void addPurchase(Purchase p) {
        this.mPurchaseMap.put(p.getSku(), p);
    }
}
