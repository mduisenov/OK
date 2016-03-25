package ru.mail.android.mytarget.core.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import ru.mail.android.mytarget.core.engines.ActivityAdEngine;
import ru.mail.android.mytarget.core.engines.ActivityAdEngine$AdEngineListener;
import ru.mail.android.mytarget.core.facades.MyTargetAd;
import ru.mail.android.mytarget.core.factories.EnginesFactory;
import ru.mail.android.mytarget.core.ui.views.AdTitle;
import ru.mail.android.mytarget.nativeads.NativeAppwallAd;

public class InterstitialAdDialog extends Dialog implements ActivityAdEngine$AdEngineListener {
    private MyTargetAd ad;
    private ActivityAdEngine engine;

    public InterstitialAdDialog(MyTargetAd ad, boolean hideStatusBar, Context context) {
        super(context);
        this.ad = ad;
        requestWindowFeature(1);
        if (hideStatusBar) {
            getWindow().setFlags(1024, 1024);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(1);
        if (this.ad instanceof NativeAppwallAd) {
            NativeAppwallAd appwallAd = this.ad;
            AdTitle adTitle = new AdTitle(getContext());
            adTitle.setLabel(appwallAd.getTitle());
            adTitle.setCloseClickListener(this);
            adTitle.setLayoutParams(new LayoutParams(-1, (int) TypedValue.applyDimension(1, 52.0f, getContext().getResources().getDisplayMetrics())));
            adTitle.setStripeColor(appwallAd.getTitleSupplementaryColor());
            adTitle.setMainColor(appwallAd.getTitleBackgorundColor());
            adTitle.setTitleColor(appwallAd.getTitleTextColor());
            layout.addView(adTitle);
        }
        this.engine = EnginesFactory.getActivityAdEngine(this.ad, layout, getContext());
        setContentView(layout);
        getWindow().setLayout(-1, -1);
        this.engine.setAdEngineListener(this);
        super.onCreate(savedInstanceState);
    }

    public void onClick(boolean shouldWeFinish) {
        if (shouldWeFinish) {
            dismiss();
        }
    }

    public void onCloseClick() {
        dismiss();
    }
}
