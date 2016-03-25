package ru.mail.android.mytarget.ads;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import ru.mail.android.mytarget.core.engines.ActivityAdEngine;
import ru.mail.android.mytarget.core.engines.ActivityAdEngine$AdEngineListener;
import ru.mail.android.mytarget.core.facades.MyTargetAd;
import ru.mail.android.mytarget.core.factories.EnginesFactory;
import ru.mail.android.mytarget.core.ui.views.AdTitle;
import ru.mail.android.mytarget.nativeads.NativeAppwallAd;

public class MyTargetActivity extends Activity implements ActivityAdEngine$AdEngineListener {
    public static MyTargetAd ad;
    private ActivityAdEngine engine;
    private LinearLayout rootLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.rootLayout = new LinearLayout(this);
        this.rootLayout.setOrientation(1);
        this.rootLayout.setLayoutParams(new LayoutParams(-1, -1));
        MyTargetAd ad = ad;
        ad = null;
        processIntent(getIntent(), ad);
        setContentView(this.rootLayout);
    }

    private void setupActionBarForAppwall(NativeAppwallAd ad) {
        ActionBar actionBar;
        if (VERSION.SDK_INT >= 21) {
            getWindow().addFlags(LinearLayoutManager.INVALID_OFFSET);
            setTheme(16974392);
            actionBar = getActionBar();
            actionBar.setTitle(ad.getTitle());
            actionBar.setIcon(17170445);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(ad.getTitleBackgorundColor()));
            setActionbarTextColor(actionBar, ad.getTitleTextColor());
            actionBar.setElevation(TypedValue.applyDimension(1, 4.0f, getResources().getDisplayMetrics()));
            getWindow().setStatusBarColor(ad.getTitleSupplementaryColor());
        } else if (VERSION.SDK_INT >= 11) {
            setTheme(16974105);
            actionBar = getActionBar();
            actionBar.setTitle(ad.getTitle());
            actionBar.setBackgroundDrawable(new ColorDrawable(ad.getTitleBackgorundColor()));
            setActionbarTextColor(actionBar, ad.getTitleTextColor());
            if (VERSION.SDK_INT >= 14) {
                actionBar.setIcon(17170445);
            }
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            setTheme(16973830);
            AdTitle adTitle = new AdTitle(this);
            adTitle.setLabel(ad.getTitle());
            adTitle.setCloseClickListener(this);
            adTitle.setLayoutParams(new LayoutParams(-1, (int) TypedValue.applyDimension(1, 52.0f, getResources().getDisplayMetrics())));
            adTitle.setStripeColor(ad.getTitleSupplementaryColor());
            adTitle.setMainColor(ad.getTitleBackgorundColor());
            adTitle.setTitleColor(ad.getTitleTextColor());
            this.rootLayout.addView(adTitle, 0);
        }
    }

    private void setActionbarTextColor(ActionBar actionBar, int color) {
        if (VERSION.SDK_INT >= 11) {
            SpannableStringBuilder sb = new SpannableStringBuilder(actionBar.getTitle());
            sb.setSpan(new ForegroundColorSpan(color), 0, actionBar.getTitle().length(), 18);
            actionBar.setTitle(sb);
        }
    }

    private void setupStatusbarForFullscreen() {
        setTheme(16973830);
        getWindow().setFlags(1024, 1024);
    }

    private void processIntent(Intent incomingIntent, MyTargetAd ad) {
        if (ad != null) {
            this.engine = EnginesFactory.getActivityAdEngine(ad, this.rootLayout, this);
            if ("ru.mail.android.mytarget.actions.appwall".equals(incomingIntent.getAction())) {
                setupActionBarForAppwall((NativeAppwallAd) ad);
            } else if ("ru.mail.android.mytarget.actions.interstitial".equals(incomingIntent.getAction())) {
                setupStatusbarForFullscreen();
            }
            this.engine.setAdEngineListener(this);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onPause() {
        super.onPause();
        if (this.engine != null) {
            this.engine.onPause();
        }
    }

    protected void onResume() {
        super.onResume();
        if (this.engine != null) {
            this.engine.onResume();
        }
    }

    protected void onStart() {
        super.onStart();
        if (this.engine != null) {
            this.engine.onStart();
        }
    }

    protected void onStop() {
        super.onStop();
        if (this.engine != null) {
            this.engine.onStop();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.engine != null) {
            this.engine.onDismiss();
            this.engine.onDestroy();
        }
    }

    public void onClick(boolean shouldWeFinish) {
        if (shouldWeFinish) {
            finish();
        }
    }

    public void onCloseClick() {
        finish();
    }
}
