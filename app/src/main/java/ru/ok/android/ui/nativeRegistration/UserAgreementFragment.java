package ru.ok.android.ui.nativeRegistration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;

public class UserAgreementFragment extends BaseFragment implements OnStubButtonClickListener {
    SmartEmptyViewAnimated emptyView;
    private boolean errorOccurred;
    WebView webView;

    /* renamed from: ru.ok.android.ui.nativeRegistration.UserAgreementFragment.1 */
    class C11181 extends WebViewClient {
        C11181() {
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            UserAgreementFragment.this.errorOccurred = true;
            UserAgreementFragment.this.emptyView.setState(State.LOADED);
        }

        public void onPageFinished(WebView view, String url) {
            if (!UserAgreementFragment.this.errorOccurred) {
                UserAgreementFragment.this.webView.setVisibility(0);
                UserAgreementFragment.this.emptyView.setVisibility(4);
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LocalizationManager.inflate(getActivity(), 2130903550, container, false);
        this.webView = (WebView) view.findViewById(2131624796);
        this.emptyView = (SmartEmptyViewAnimated) view.findViewById(C0263R.id.empty_view);
        this.emptyView.setType(Type.NO_INTERNET);
        this.emptyView.setButtonClickListener(this);
        WebSettings settings = this.webView.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        this.webView.getSettings().setRenderPriority(RenderPriority.HIGH);
        this.webView.setWebViewClient(new C11181());
        if (savedInstanceState == null) {
            loadUrl();
        }
        return view;
    }

    private void loadUrl() {
        String url;
        this.errorOccurred = false;
        this.emptyView.setVisibility(0);
        this.webView.setVisibility(4);
        this.emptyView.setState(State.LOADING);
        String locale = Settings.getCurrentLocale(getActivity());
        if ("ru".equals(locale) || "uk".equals(locale)) {
            url = LocalizationManager.getString(getActivity(), 2131166788);
        } else {
            url = LocalizationManager.getString(getActivity(), 2131166789);
        }
        this.webView.loadUrl(url);
    }

    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        this.webView.restoreState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.webView.saveState(outState);
    }

    protected int getLayoutId() {
        return 2130903550;
    }

    protected CharSequence getTitle() {
        return LocalizationManager.getString(getContext(), 2131166786);
    }

    public void onStubButtonClick(Type type) {
        loadUrl();
    }
}
