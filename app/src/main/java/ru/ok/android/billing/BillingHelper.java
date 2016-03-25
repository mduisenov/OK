package ru.ok.android.billing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import org.json.JSONTokener;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.billingUtils.IabException;
import ru.ok.android.billingUtils.IabHelper;
import ru.ok.android.billingUtils.IabHelper.OnConsumeFinishedListener;
import ru.ok.android.billingUtils.IabHelper.OnIabSetupFinishedListener;
import ru.ok.android.billingUtils.IabResult;
import ru.ok.android.billingUtils.Purchase;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.exception.NoConnectionException;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.request.billing.ConfirmPayloadRequest;
import ru.ok.model.BillingBuyItem;

public class BillingHelper implements OnIabSetupFinishedListener {
    private final Activity activity;
    private volatile BillingConsumeTask billingConsumeTask;
    private volatile BillingStartTask billingStartTask;
    private final OnPurchased finishedListener;
    private boolean isInAppEnable;
    private IabHelper mHelper;

    /* renamed from: ru.ok.android.billing.BillingHelper.1 */
    static class C02331 implements OnPurchased {
        final /* synthetic */ Activity val$activity;
        final /* synthetic */ OnPurchased val$onPurchased;

        C02331(Activity activity, OnPurchased onPurchased) {
            this.val$activity = activity;
            this.val$onPurchased = onPurchased;
        }

        public void onPurchased(BillingBuyItem billingBuyItem) {
            if (this.val$activity != null) {
                TimeToast.show(this.val$activity, 2131165442, 1);
            }
            if (this.val$onPurchased != null) {
                this.val$onPurchased.onPurchased(billingBuyItem);
            }
        }
    }

    private static class BillingConsumeTask extends AsyncTask<Void, Void, String> {
        final Activity activity;
        final BillingDialogFragment billingDialogFragment;
        final BillingHelper billingHelper;
        final OnConsumeFinishedListener finishedListener;
        final IabHelper iabHelper;
        final Purchase purchase;

        private BillingConsumeTask(Purchase purchase, OnConsumeFinishedListener finishedListener, BillingDialogFragment billingDialogFragment) {
            this.purchase = purchase;
            this.finishedListener = finishedListener;
            this.activity = billingDialogFragment.getActivity();
            this.billingHelper = billingDialogFragment.getBillingHelper();
            this.iabHelper = this.billingHelper == null ? null : this.billingHelper.mHelper;
            this.billingDialogFragment = billingDialogFragment;
        }

        private BillingConsumeTask(Purchase purchase, OnConsumeFinishedListener finishedListener, Activity activity, BillingHelper helper) {
            this.purchase = purchase;
            this.finishedListener = finishedListener;
            this.activity = activity;
            this.billingHelper = helper;
            this.iabHelper = this.billingHelper == null ? null : this.billingHelper.mHelper;
            this.billingDialogFragment = null;
        }

        protected String doInBackground(Void... voids) {
            try {
                return new JSONTokener(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new ConfirmPayloadRequest(this.purchase.getOriginalJson() + this.purchase.getSignature())).getHttpResponse()).nextValue().toString();
            } catch (Throwable e) {
                Logger.m178e(e);
                return null;
            }
        }

        protected void onPostExecute(String s) {
            try {
                if ("0".equals(s) || "-3".equals(s)) {
                    BillingHelper.removeNotConsumeTransaction();
                    this.iabHelper.consumeAsync(this.purchase, this.finishedListener);
                    if (this.billingHelper != null) {
                        this.billingHelper.billingConsumeTask = null;
                        if (this.billingDialogFragment != null) {
                            this.billingDialogFragment.updateGui();
                        }
                    }
                }
                TimeToast.show(this.activity, 2131165827, 1);
                if (this.billingHelper != null) {
                    this.billingHelper.billingConsumeTask = null;
                    if (this.billingDialogFragment != null) {
                        this.billingDialogFragment.updateGui();
                    }
                }
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        }
    }

    private static class BillingStartTask extends AsyncTask<Void, Void, BillingResult> {
        final Activity activity;
        final BillingDialogFragment billingDialogFragment;
        final BillingHelper billingHelper;
        final BillingBuyItem item;

        static class BillingResult {
            final int errorReason;
            final String payload;

            private BillingResult(String payload, int errorReason) {
                this.payload = payload;
                this.errorReason = errorReason;
            }
        }

        BillingStartTask(BillingBuyItem item, BillingDialogFragment billingDialogFragment) {
            this.item = item;
            this.billingDialogFragment = billingDialogFragment;
            this.activity = billingDialogFragment.getActivity();
            this.billingHelper = billingDialogFragment.getBillingHelper();
        }

        boolean checkParams() {
            return (this.activity == null || this.billingHelper == null) ? false : true;
        }

        protected BillingResult doInBackground(Void... voids) {
            int i = 2;
            if (!checkParams()) {
                return new BillingResult(1, null);
            }
            if (!NetUtils.isConnectionAvailable(this.activity, true)) {
                return new BillingResult(2, null);
            }
            try {
                return new BillingResult(0, null);
            } catch (Throwable e) {
                Logger.m178e(e);
                if (!(e instanceof NoConnectionException)) {
                    i = 3;
                }
                return new BillingResult(i, null);
            }
        }

        protected void onPostExecute(BillingResult result) {
            if (checkParams()) {
                if (TextUtils.isEmpty(result.payload)) {
                    int errorMessage = 2131165827;
                    switch (result.errorReason) {
                        case Message.AUTHORID_FIELD_NUMBER /*2*/:
                            errorMessage = 2131166272;
                            break;
                    }
                    TimeToast.show(this.activity, errorMessage, 1);
                } else {
                    try {
                        String sku = this.item.sku;
                        BillingOnConsumeFinished consumeFinishedListener = new BillingOnConsumeFinished(this.item, this.billingDialogFragment);
                        BillingOnIabPurchaseFinishedListener purchaseFinishedListener = new BillingOnIabPurchaseFinishedListener(consumeFinishedListener, this.billingDialogFragment);
                        Purchase purchase = BillingUtils.getPurchase(this.billingHelper.mHelper, sku);
                        if (purchase != null) {
                            BillingHelper.consume(purchase, consumeFinishedListener, this.billingDialogFragment);
                        } else {
                            this.billingHelper.mHelper.launchPurchaseFlow(this.activity, sku, 10001, purchaseFinishedListener, result.payload);
                        }
                    } catch (Throwable e) {
                        Logger.m178e(e);
                    }
                }
                if (this.billingHelper != null) {
                    this.billingHelper.billingStartTask = null;
                    this.billingDialogFragment.updateGui();
                }
            }
        }
    }

    public static class DestroyHelper implements OnPurchased {
        public BillingHelper billingHelper;

        public DestroyHelper() {
            this.billingHelper = null;
        }

        public void onPurchased(BillingBuyItem billingBuyItem) {
            BillingHelper.onDestroy(this.billingHelper);
        }
    }

    private BillingHelper(Activity activity, OnPurchased finishedListener) {
        this.isInAppEnable = false;
        this.billingStartTask = null;
        this.billingConsumeTask = null;
        this.activity = activity;
        this.finishedListener = finishedListener;
        this.mHelper = new IabHelper(activity, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs8mswURYh06IEbFNsE/47NXyAymo3QLC6FlF2Z07VpSZ7qlkEraSsNd143G7JBkWqJOND57kIKOov7mtb4yE5ZyP7/NTH+lrmJRDn2vtNNdAp6vDYdoH2M94kQqkuk9Nh2vsvWpCMRKHztfwA2zGStVKFYalxe7Ef+mrB257iQ4eNofYvwrBbI+saBXqF2umnUjsz6SP72urPbwioqB1Kneg7E8BwCnsd4o+hWhDDgx3HzyUPnhfv49KfKswmgBqMvsgGPleAclxbIeTglmroAJjGgem/ZgCYBTZnYGWEq4RdgdpxhiPGAqvYWLN3K2iNVUvctdnAnsLeEN9GbHufwIDAQAB");
        this.mHelper.enableDebugLogging(false);
        this.mHelper.startSetup(this);
    }

    public static synchronized boolean initializePaid(BillingBuyItem item, BillingDialogFragment billingDialogFragment) {
        boolean z = true;
        synchronized (BillingHelper.class) {
            BillingHelper billingHelper = billingDialogFragment == null ? null : billingDialogFragment.getBillingHelper();
            if (billingHelper != null) {
                if (billingHelper.isInAppSupported() && billingHelper.billingStartTask == null) {
                    addNotConsumeTransaction();
                    billingHelper.billingStartTask = new BillingStartTask(item, billingDialogFragment);
                    billingHelper.billingStartTask.execute(new Void[0]);
                }
            }
            if (!(billingHelper == null || billingHelper.isInAppSupported())) {
                TimeToast.show(billingDialogFragment.getActivity(), 2131165441, 1);
            }
            z = false;
        }
        return z;
    }

    public static void consume(Purchase purchase, OnConsumeFinishedListener finishedListener, BillingDialogFragment billingDialogFragment) {
        BillingHelper billingHelper = billingDialogFragment.getBillingHelper();
        if (billingHelper != null) {
            billingHelper.billingConsumeTask = new BillingConsumeTask(finishedListener, billingDialogFragment, null);
            billingHelper.billingConsumeTask.execute(new Void[0]);
            billingDialogFragment.updateGui();
        }
    }

    public static void consume(Purchase purchase, OnConsumeFinishedListener finishedListener, Activity activity, BillingHelper billingHelper) {
        if (billingHelper != null) {
            billingHelper.billingConsumeTask = new BillingConsumeTask(finishedListener, activity, billingHelper, null);
            billingHelper.billingConsumeTask.execute(new Void[0]);
        }
    }

    public static BillingHelper create(Activity activity) {
        return create(activity, null);
    }

    public static BillingHelper create(Activity activity, OnPurchased finishedListener) {
        return activity == null ? null : new BillingHelper(activity, finishedListener);
    }

    public static BillingHelper onDestroy(BillingHelper billingHelper) {
        if (billingHelper != null) {
            billingHelper.mHelper.dispose();
            billingHelper.mHelper = null;
        }
        return null;
    }

    public boolean isInAppSupported() {
        return this.isInAppEnable;
    }

    public void onIabSetupFinished(IabResult result) {
        this.isInAppEnable = result.isSuccess();
        if (this.isInAppEnable && getNotConsumeTransactionCount() != 0) {
            checkPurchase(this.activity, this, this.finishedListener);
        }
    }

    public static boolean handleActivityResult(int requestCode, int resultCode, Intent data, BillingHelper billingHelper) {
        if (resultCode != -1) {
            removeNotConsumeTransaction();
        }
        IabHelper iabHelper = billingHelper == null ? null : billingHelper.mHelper;
        return iabHelper != null && iabHelper.handleActivityResult(requestCode, resultCode, data);
    }

    public boolean isWork(BillingHelper billingHelper) {
        return (billingHelper == null || (billingHelper.billingStartTask == null && billingHelper.billingConsumeTask == null)) ? false : true;
    }

    public static void addNotConsumeTransaction() {
        synchronized ("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs8mswURYh06IEbFNsE/47NXyAymo3QLC6FlF2Z07VpSZ7qlkEraSsNd143G7JBkWqJOND57kIKOov7mtb4yE5ZyP7/NTH+lrmJRDn2vtNNdAp6vDYdoH2M94kQqkuk9Nh2vsvWpCMRKHztfwA2zGStVKFYalxe7Ef+mrB257iQ4eNofYvwrBbI+saBXqF2umnUjsz6SP72urPbwioqB1Kneg7E8BwCnsd4o+hWhDDgx3HzyUPnhfv49KfKswmgBqMvsgGPleAclxbIeTglmroAJjGgem/ZgCYBTZnYGWEq4RdgdpxhiPGAqvYWLN3K2iNVUvctdnAnsLeEN9GbHufwIDAQAB") {
            Context context = OdnoklassnikiApplication.getContext();
            Settings.storeIntValue(context, "key_not_confirm_purchase", Settings.getIntValue(context, "key_not_confirm_purchase", 0) + 1);
        }
    }

    public static void removeNotConsumeTransaction() {
        synchronized ("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs8mswURYh06IEbFNsE/47NXyAymo3QLC6FlF2Z07VpSZ7qlkEraSsNd143G7JBkWqJOND57kIKOov7mtb4yE5ZyP7/NTH+lrmJRDn2vtNNdAp6vDYdoH2M94kQqkuk9Nh2vsvWpCMRKHztfwA2zGStVKFYalxe7Ef+mrB257iQ4eNofYvwrBbI+saBXqF2umnUjsz6SP72urPbwioqB1Kneg7E8BwCnsd4o+hWhDDgx3HzyUPnhfv49KfKswmgBqMvsgGPleAclxbIeTglmroAJjGgem/ZgCYBTZnYGWEq4RdgdpxhiPGAqvYWLN3K2iNVUvctdnAnsLeEN9GbHufwIDAQAB") {
            Context context = OdnoklassnikiApplication.getContext();
            Settings.storeIntValue(context, "key_not_confirm_purchase", Settings.getIntValue(context, "key_not_confirm_purchase", 0) - 1);
        }
    }

    public static void clearNotConsumeTransaction() {
        synchronized ("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs8mswURYh06IEbFNsE/47NXyAymo3QLC6FlF2Z07VpSZ7qlkEraSsNd143G7JBkWqJOND57kIKOov7mtb4yE5ZyP7/NTH+lrmJRDn2vtNNdAp6vDYdoH2M94kQqkuk9Nh2vsvWpCMRKHztfwA2zGStVKFYalxe7Ef+mrB257iQ4eNofYvwrBbI+saBXqF2umnUjsz6SP72urPbwioqB1Kneg7E8BwCnsd4o+hWhDDgx3HzyUPnhfv49KfKswmgBqMvsgGPleAclxbIeTglmroAJjGgem/ZgCYBTZnYGWEq4RdgdpxhiPGAqvYWLN3K2iNVUvctdnAnsLeEN9GbHufwIDAQAB") {
            Settings.storeIntValue(OdnoklassnikiApplication.getContext(), "key_not_confirm_purchase", 0);
        }
    }

    public static int getNotConsumeTransactionCount() {
        int intValue;
        synchronized ("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs8mswURYh06IEbFNsE/47NXyAymo3QLC6FlF2Z07VpSZ7qlkEraSsNd143G7JBkWqJOND57kIKOov7mtb4yE5ZyP7/NTH+lrmJRDn2vtNNdAp6vDYdoH2M94kQqkuk9Nh2vsvWpCMRKHztfwA2zGStVKFYalxe7Ef+mrB257iQ4eNofYvwrBbI+saBXqF2umnUjsz6SP72urPbwioqB1Kneg7E8BwCnsd4o+hWhDDgx3HzyUPnhfv49KfKswmgBqMvsgGPleAclxbIeTglmroAJjGgem/ZgCYBTZnYGWEq4RdgdpxhiPGAqvYWLN3K2iNVUvctdnAnsLeEN9GbHufwIDAQAB") {
            intValue = Settings.getIntValue(OdnoklassnikiApplication.getContext(), "key_not_confirm_purchase", 0);
        }
        return intValue;
    }

    public static void checkPurchase(Activity activity, BillingHelper billingHelper, OnPurchased onPurchased) {
        boolean hasNotConfirm = false;
        BillingBuyItem[] arr$ = BillingBuyItem.values();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            BillingBuyItem item = arr$[i$];
            String sku = item.sku;
            BillingOnConsumeFinished consumeFinishedListener = new BillingOnConsumeFinished(item, new C02331(activity, onPurchased));
            if (billingHelper != null) {
                Purchase purchase = null;
                try {
                    purchase = BillingUtils.getPurchase(billingHelper.mHelper, sku);
                } catch (IabException e) {
                    e.printStackTrace();
                }
                if (purchase != null) {
                    hasNotConfirm = true;
                    consume(purchase, consumeFinishedListener, activity, billingHelper);
                }
                i$++;
            } else {
                return;
            }
        }
        if (hasNotConfirm) {
            addNotConsumeTransaction();
            return;
        }
        clearNotConsumeTransaction();
        if (onPurchased != null) {
            onPurchased.onPurchased(null);
        }
    }
}
