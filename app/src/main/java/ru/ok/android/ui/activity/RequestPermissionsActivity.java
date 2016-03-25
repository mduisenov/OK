package ru.ok.android.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.AppCompatActivity;
import ru.ok.android.utils.PermissionUtils;

@TargetApi(23)
public final class RequestPermissionsActivity extends AppCompatActivity {

    private static class RequestPermissionsResultReceiver extends ResultReceiver {
        private final OnRequestPermissionsResultCallback callback;

        public RequestPermissionsResultReceiver(OnRequestPermissionsResultCallback callback) {
            super(null);
            this.callback = callback;
        }

        protected void onReceiveResult(int resultCode, Bundle resultData) {
            this.callback.onRequestPermissionsResult(resultData.getInt("requestCode"), resultData.getStringArray("permissions"), resultData.getIntArray("grantResults"));
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            ActivityCompat.requestPermissions(this, extras.getStringArray("permissions"), extras.getInt("requestCode"));
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int resultCode = PermissionUtils.getGrantResult(grantResults) == 0 ? -1 : 0;
        Bundle resultData = new Bundle(3);
        resultData.putInt("requestCode", requestCode);
        resultData.putStringArray("permissions", permissions);
        resultData.putIntArray("grantResults", grantResults);
        ((ResultReceiver) getIntent().getParcelableExtra("resultReceiver")).send(resultCode, resultData);
        setResult(resultCode, new Intent().putExtras(resultData));
        finish();
    }

    public static Intent createRequestPermissionsIntent(Context context, String[] permissions, int requestCode, OnRequestPermissionsResultCallback callback) {
        return new Intent(context, RequestPermissionsActivity.class).putExtra("requestCode", requestCode).putExtra("permissions", permissions).putExtra("resultReceiver", new RequestPermissionsResultReceiver(callback));
    }
}
