package ru.ok.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import ru.ok.android.fragments.registr.NotLoggedInWebFragment;
import ru.ok.android.fragments.registr.NotLoggedInWebFragment.OnLoginCallBack;
import ru.ok.android.fragments.registr.NotLoggedInWebFragment.Page;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.utils.HomeButtonUtils;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.settings.Settings;

public final class NotLoggedInWebActivity extends BaseCompatToolbarActivity implements OnLoginCallBack {
    private NotLoggedInWebFragment fragment;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(5);
        super.onCreate(savedInstanceState);
        setContentView();
        HomeButtonUtils.showHomeButton(this);
        Page page = null;
        if (!(getIntent() == null || getIntent().getExtras() == null)) {
            page = (Page) getIntent().getSerializableExtra("page");
        }
        if (page == null) {
            page = Page.Registration;
        }
        Bundle args = NotLoggedInWebFragment.newArguments(page, false);
        this.fragment = new NotLoggedInWebFragment();
        this.fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(2131624639, this.fragment).commit();
    }

    public void onClose() {
        if (Settings.hasLoginData(this)) {
            setResult(-1, new Intent());
        }
        finish();
    }

    public void onLogin(String token, String userName) {
        NavigationHelper.loginAfterWebRegistration(this, userName, token);
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                if (!this.fragment.getWebView().canGoBack()) {
                    return super.onKeyDown(keyCode, event);
                }
                this.fragment.getWebView().goBack();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    protected boolean isSupportToolbarVisible() {
        return false;
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
