package ru.ok.android.widget.menuitems;

import android.view.View;
import android.widget.TextView;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;
import ru.ok.android.widget.menuitems.StandardItem.BubbleState;
import ru.ok.model.stream.banner.Banner;
import ru.ok.model.stream.banner.PromoLink;

public final class BannerItem extends StandardItem {
    private PromoLink promoLink;

    class ViewHolder extends Holder {
        public UrlImageView imageView;

        public ViewHolder(int type, int position) {
            super(type, position);
        }
    }

    public BannerItem(int height, OdklSlidingMenuFragmentActivity activity) {
        super(activity, 2130838446, 2131166602, Type.banner, height, BubbleState.green_tablet);
        this.promoLink = null;
    }

    protected void setText(TextView textView) {
        if (this.promoLink == null || this.promoLink.banner == null) {
            textView.setText(null);
        } else {
            textView.setText(this.promoLink.banner.header);
        }
    }

    public Banner getBanner() {
        return this.promoLink != null ? this.promoLink.banner : null;
    }

    public View getView(LocalizationManager inflater, View view, int position, Type selectedItem) {
        ViewHolder holder;
        if (view == null) {
            view = LocalizationManager.inflate(inflater.getContext(), 2130903313, null, false);
            holder = createViewHolder(getType(), position);
            holder.name = (TextView) view.findViewById(2131625064);
            holder.counter = (TextView) view.findViewById(2131625065);
            holder.imageView = (UrlImageView) view.findViewById(2131625062);
            holder.imageView.setClickable(false);
            holder.greenCounter = (TextView) view.findViewById(2131625076);
            view.setTag(holder);
            Utils.sendPixels(this.promoLink, 1, inflater.getContext());
            Utils.sendPixels(this.promoLink, 0, inflater.getContext());
        } else {
            holder = (ViewHolder) view.getTag();
            holder.position = position;
        }
        setCounterText(this.mCounter, this.mCounterTwo, holder.counter, holder.greenCounter, this.mBubbleState);
        setText(holder.name);
        if (!(this.promoLink == null || this.promoLink.banner == null)) {
            ImageViewManager.getInstance().displayImage(this.promoLink.banner.iconUrlHd, holder.imageView, null);
        }
        return view;
    }

    public int getType() {
        return 6;
    }

    public void setCurrentPromoLink(PromoLink promoLink) {
        this.promoLink = promoLink;
        invalidateView();
    }

    public PromoLink getPromoLink() {
        return this.promoLink;
    }

    public ViewHolder createViewHolder(int type, int position) {
        return new ViewHolder(type, position);
    }
}
