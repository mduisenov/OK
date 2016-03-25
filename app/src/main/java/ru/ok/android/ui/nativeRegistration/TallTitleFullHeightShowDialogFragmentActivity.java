package ru.ok.android.ui.nativeRegistration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import ru.mail.libverify.C0176R;
import ru.ok.android.ui.activity.ShowDialogFragmentActivity;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;

public class TallTitleFullHeightShowDialogFragmentActivity extends ShowDialogFragmentActivity implements OnGlobalLayoutListener {

    /* renamed from: ru.ok.android.ui.nativeRegistration.TallTitleFullHeightShowDialogFragmentActivity.1 */
    class C11101 implements OnClickListener {
        C11101() {
        }

        public void onClick(View v) {
            TallTitleFullHeightShowDialogFragmentActivity.this.onBackPressed();
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.TallTitleFullHeightShowDialogFragmentActivity.2 */
    class C11112 implements OnGlobalLayoutListener {
        C11112() {
        }

        public void onGlobalLayout() {
        }
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            boolean isLargeScreen;
            if (DeviceUtils.getType(this) == DeviceLayoutType.LARGE) {
                isLargeScreen = true;
            } else {
                isLargeScreen = false;
            }
            Toolbar toolbar = getSupportToolbar();
            int padding = (int) getResources().getDimension(2131230745);
            String title = intent.getStringExtra("key_custom_title");
            if (title != null) {
                toolbar.setPadding(0, 0, 0, 0);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                View headerView = getLayoutInflater().inflate(2130903538, toolbar, false);
                ((TextView) headerView.findViewById(C0176R.id.title)).setText(title);
                if (isLargeScreen) {
                    toolbar.setContentInsetsAbsolute(padding, padding);
                } else {
                    toolbar.setContentInsetsAbsolute(0, padding);
                    View backBtn = headerView.findViewById(2131625400);
                    backBtn.setVisibility(0);
                    backBtn.setOnClickListener(new C11101());
                }
                toolbar.addView(headerView);
            } else {
                toolbar.setContentInsetsAbsolute(padding, padding);
                toolbar.setTitleTextColor(getResources().getColor(2131493081));
                if (!isLargeScreen) {
                    getSupportActionBar().setHomeAsUpIndicator(2130837946);
                }
            }
            toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new C11112());
        }
    }

    protected void updateWindowParams() {
        int width = getWindowManager().getDefaultDisplay().getWidth();
        LayoutParams params = getWindow().getAttributes();
        params.width = (width * 6) / 10;
        getWindow().setAttributes(params);
    }

    protected void onStop() {
        super.onStop();
        getSupportToolbar().getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    protected void onStart() {
        super.onStart();
        getSupportToolbar().getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public void onGlobalLayout() {
        this.contentWrapper.setPadding(0, getSupportToolbar().getHeight(), 0, 0);
    }

    protected boolean isToolbarLocked() {
        return true;
    }

    public boolean isShadowVisible() {
        return false;
    }
}
