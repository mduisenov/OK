package ru.ok.android.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.WindowManager.LayoutParams;
import ru.ok.android.ui.utils.HomeButtonUtils;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.Logger;

public class ShowDialogFragmentActivity extends BaseActivity {
    protected void onCreateLocalized(Bundle savedInstanceState) {
        if (DeviceUtils.getType(this) == DeviceLayoutType.LARGE) {
            updateWindowParams();
        }
        super.onCreateLocalized(savedInstanceState);
        HomeButtonUtils.hideHomeButton(this);
        Intent intent = getIntent();
        if (intent != null) {
            Class<Fragment> clazz = (Class) intent.getSerializableExtra("key_class_name");
            if (clazz == null) {
                Logger.m184w("Fragment class now specified");
                return;
            }
            setContentView();
            Bundle bundle = intent.getBundleExtra("key_argument_name");
            String tag = intent.getStringExtra("key_fragment_tag");
            try {
                Fragment fragment = (Fragment) clazz.newInstance();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(2131624639, fragment, tag).commit();
            } catch (Throwable e) {
                Logger.m179e(e, "<<< Failed to instantiate fragment");
            }
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (DeviceUtils.getType(this) == DeviceLayoutType.LARGE) {
            updateWindowParams();
        }
    }

    protected void updateWindowParams() {
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        LayoutParams params = getWindow().getAttributes();
        params.width = (width * 6) / 10;
        params.height = (height * 8) / 10;
        if (((float) params.height) > ((float) params.width) * 1.3f) {
            params.height = (int) (((float) params.width) * 1.3f);
        }
        getWindow().setAttributes(params);
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
