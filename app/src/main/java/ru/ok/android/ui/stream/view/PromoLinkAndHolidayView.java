package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.utils.ViewUtil;
import ru.ok.model.stream.Holiday;
import ru.ok.model.stream.banner.PromoLink;

public class PromoLinkAndHolidayView extends LinearLayout {
    private HandleBlocker handleBlocker;
    private Holiday holiday;
    public HolidayView holidayView;
    private PromoLink promoLink;
    public PromoLinkView promoLinkView;

    public PromoLinkAndHolidayView(Context context) {
        super(context);
        init();
    }

    public PromoLinkAndHolidayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setPromoLink(@Nullable PromoLink promoLink) {
        this.promoLink = promoLink;
        update();
    }

    public void setHoliday(@Nullable Holiday holiday) {
        this.holiday = holiday;
        update();
    }

    public void setHandleBlocker(@Nullable HandleBlocker handleBlocker) {
        this.handleBlocker = handleBlocker;
    }

    private void update() {
        if (this.promoLink != null && this.promoLink.banner != null && this.holiday != null) {
            this.promoLinkView.setPromoLink(this.promoLink, this.handleBlocker);
            this.holidayView.setHoliday(this.holiday, this.handleBlocker);
            this.promoLinkView.setVisibility(0);
            this.holidayView.setVisibility(0);
            this.promoLinkView.setBackgroundResource(2130837910);
            this.holidayView.setBackgroundResource(2130837896);
        } else if (this.promoLink != null && this.promoLink.banner != null) {
            this.promoLinkView.setPromoLink(this.promoLink, this.handleBlocker);
            this.promoLinkView.setVisibility(0);
            this.holidayView.setVisibility(8);
            this.promoLinkView.setBackgroundResource(2130837907);
        } else if (this.holiday != null) {
            this.holidayView.setHoliday(this.holiday, this.handleBlocker);
            this.promoLinkView.setVisibility(8);
            this.holidayView.setVisibility(0);
            this.holidayView.setBackgroundResource(2130837907);
        } else {
            this.promoLinkView.setVisibility(8);
            this.holidayView.setVisibility(8);
        }
    }

    private void init() {
        setDividerDrawable(getResources().getDrawable(2130837899));
        setShowDividers(2);
        setOrientation(1);
        LayoutInflater.from(getContext()).inflate(2130903268, this);
        this.promoLinkView = (PromoLinkView) findViewById(2131625005);
        this.holidayView = (HolidayView) findViewById(2131625006);
        setPadding(0, getResources().getDimensionPixelOffset(2131231142), 0, 0);
        ViewUtil.resetLayoutParams(this, -1, -2);
    }
}
