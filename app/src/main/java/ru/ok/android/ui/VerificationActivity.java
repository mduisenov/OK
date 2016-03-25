package ru.ok.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.KeyEvent;
import ru.ok.android.fragments.web.VerificationFragment;
import ru.ok.android.fragments.web.VerificationFragment.OnVerificationListener;
import ru.ok.android.fragments.web.VerificationFragment.VerificationValue;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.ui.utils.HomeButtonUtils;

public class VerificationActivity extends BaseActivity implements OnVerificationListener {
    private VerificationFragment fragment;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(5);
        super.onCreate(savedInstanceState);
        HomeButtonUtils.showHomeButton(this);
        String verificationUrl = "";
        if (!(getIntent() == null || getIntent().getExtras() == null)) {
            verificationUrl = getIntent().getExtras().getString("verification_url");
        }
        Bundle args = VerificationFragment.newArguments(verificationUrl);
        this.fragment = new VerificationFragment();
        this.fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(16908290, this.fragment).commit();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                if (!this.fragment.getWebView().canGoBack()) {
                    onVerification(VerificationValue.CANCEL, null);
                    break;
                }
                this.fragment.getWebView().goBack();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onVerification(VerificationValue value, String token) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("result", value);
        bundle.putString("result_token", token);
        if (!(getIntent() == null || getIntent().getExtras() == null)) {
            ResultReceiver receiver = (ResultReceiver) getIntent().getExtras().getParcelable("receiver");
            Bundle outBundle = (Bundle) getIntent().getExtras().getParcelable("data_bundle");
            if (outBundle != null) {
                bundle.putParcelable("data_bundle", outBundle);
            }
            if (receiver != null) {
                receiver.send(-1, bundle);
            }
        }
        new Intent().putExtras(bundle);
        setResult(-1);
        finish();
    }
}
