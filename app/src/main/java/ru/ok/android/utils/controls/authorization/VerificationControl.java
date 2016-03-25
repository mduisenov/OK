package ru.ok.android.utils.controls.authorization;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import ru.ok.android.fragments.web.VerificationFragment.VerificationValue;
import ru.ok.android.ui.VerificationActivity;

public class VerificationControl {
    private Context context;

    public interface VerificationCallBack {
        void onVerification(VerificationValue verificationValue, String str, Bundle bundle);
    }

    class VerificationReceiver extends ResultReceiver {
        private VerificationCallBack callBack;

        public VerificationReceiver(VerificationCallBack callBack) {
            super(new Handler());
            this.callBack = callBack;
        }

        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case RecyclerView.NO_POSITION /*-1*/:
                    if (resultData != null) {
                        VerificationValue value = (VerificationValue) resultData.getSerializable("result");
                        Bundle bundle = (Bundle) resultData.getParcelable("data_bundle");
                        String token = resultData.getString("result_token");
                        if (this.callBack != null) {
                            this.callBack.onVerification(value, token, bundle);
                        }
                    }
                default:
            }
        }
    }

    public VerificationControl(Context context) {
        this.context = context;
    }

    public boolean verification(String url, Bundle data, VerificationCallBack callBack) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        Intent intent = new Intent(this.context, VerificationActivity.class);
        intent.putExtra("verification_url", url);
        intent.putExtra("receiver", new VerificationReceiver(callBack));
        intent.putExtra("data_bundle", data);
        intent.addFlags(268435456);
        this.context.startActivity(intent);
        return true;
    }
}
