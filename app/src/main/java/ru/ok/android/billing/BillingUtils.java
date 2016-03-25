package ru.ok.android.billing;

import java.util.ArrayList;
import java.util.List;
import ru.ok.android.billingUtils.IabException;
import ru.ok.android.billingUtils.IabHelper;
import ru.ok.android.billingUtils.Purchase;

public class BillingUtils {
    public static Purchase getPurchase(IabHelper helper, String sku) throws IabException, NullPointerException {
        List<String> list = new ArrayList();
        list.add(sku);
        return helper.queryInventory(true, list, new ArrayList()).getPurchase(sku);
    }
}
