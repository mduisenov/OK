package ru.ok.android.fragments.web;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import ru.ok.android.fragments.web.WebBaseFragment.DefaultWebViewClient;
import ru.ok.android.fragments.web.client.interceptor.UrlInterceptor;

public class VerificationFragment extends WebBaseFragment {

    public interface OnVerificationListener {
        void onVerification(VerificationValue verificationValue, String str);
    }

    class VerificationObserver implements UrlInterceptor {
        VerificationObserver() {
        }

        public boolean handleUrl(String url) {
            Activity activity = VerificationFragment.this.getActivity();
            if (activity == null) {
                return true;
            }
            if (!url.contains("st.verificationResult")) {
                return false;
            }
            Uri uri = Uri.parse(url);
            String result = uri.getQueryParameter("st.verificationResult");
            String token = uri.getQueryParameter("st.verificationToken");
            if (VerificationValue.OK.text.equals(result)) {
                ((OnVerificationListener) activity).onVerification(VerificationValue.OK, token);
                return true;
            } else if (VerificationValue.CANCEL.text.equals(result)) {
                ((OnVerificationListener) activity).onVerification(VerificationValue.CANCEL, token);
                return true;
            } else if (!VerificationValue.FAIL.text.equals(result)) {
                return true;
            } else {
                ((OnVerificationListener) activity).onVerification(VerificationValue.FAIL, token);
                return true;
            }
        }
    }

    public enum VerificationValue {
        OK("ok"),
        CANCEL("canceled"),
        FAIL("failure");
        
        public String text;

        private VerificationValue(String text) {
            this.text = text;
        }
    }

    class VerificationWebViewClient extends DefaultWebViewClient {
        final VerificationObserver verificationObserver;

        public VerificationWebViewClient(Context context) {
            super(context);
            this.verificationObserver = new VerificationObserver();
            if (VERSION.SDK_INT > 8) {
                addInterceptor(this.verificationObserver);
            }
        }

        public boolean isExternalUrl(String url) {
            return false;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (VERSION.SDK_INT <= 8) {
                this.verificationObserver.handleUrl(url);
            }
        }
    }

    public static Bundle newArguments(String verificationUrl) {
        Bundle args = new Bundle();
        args.putSerializable("verification_url", verificationUrl);
        return args;
    }

    private String getVerificationUrl() {
        return getArguments().getString("verification_url");
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUrl(getVerificationUrl());
    }

    public DefaultWebViewClient createWebViewClient() {
        return new VerificationWebViewClient(getContext());
    }
}
