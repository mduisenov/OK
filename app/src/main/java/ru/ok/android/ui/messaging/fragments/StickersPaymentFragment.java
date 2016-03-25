package ru.ok.android.ui.messaging.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import ru.ok.android.fragments.web.WebBaseFragment.DefaultWebViewClient;
import ru.ok.android.fragments.web.WebFragment;
import ru.ok.android.fragments.web.client.interceptor.hooks.AppHooksInterceptor;
import ru.ok.android.fragments.web.hooks.HookPaymentCancelled;
import ru.ok.android.fragments.web.hooks.HookPaymentCancelled.HookPaymentCancelledListener;
import ru.ok.android.fragments.web.hooks.HookPaymentDone;
import ru.ok.android.fragments.web.hooks.HookPaymentDone.HookPaymentDoneListener;
import ru.ok.android.ui.messaging.activity.PayStickersActivity;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.WebUrlCreator;

public final class StickersPaymentFragment extends WebFragment implements HookPaymentCancelledListener, HookPaymentDoneListener {

    class PaymentWebClient extends DefaultWebViewClient {
        public PaymentWebClient(Context context) {
            super(context);
            addInterceptor(new AppHooksInterceptor().addHookProcessor(new HookPaymentCancelled(r6)).addHookProcessor(new HookPaymentDone(r6)));
        }

        public boolean isExternalUrl(String url) {
            return false;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public String getStartUrl() {
        return WebUrlCreator.getStickerPaymentUrl();
    }

    public DefaultWebViewClient createWebViewClient() {
        return new PaymentWebClient(getContext());
    }

    public void reloadUrl() {
        loadUrl(getStartUrl());
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getWebView().saveState(outState);
    }

    public void onPaymentCancelled(int serviceId) {
        Logger.m173d("serviceId = %d", Integer.valueOf(serviceId));
        PayStickersActivity activity = (PayStickersActivity) getActivity();
        if (activity != null) {
            activity.onPaymentCancelled();
        }
    }

    public void onPaymentDone(int serviceId) {
        Logger.m173d("serviceId = %d", Integer.valueOf(serviceId));
        PayStickersActivity activity = (PayStickersActivity) getActivity();
        if (activity != null) {
            activity.onPaymentDone();
        }
    }

    protected int getTitleResId() {
        return 2131165453;
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        PayStickersActivity activity = (PayStickersActivity) getActivity();
        if (activity != null) {
            activity.onPaymentCancelled();
        }
    }

    protected CharSequence getTitle() {
        return getStringLocalized(2131166629);
    }
}
