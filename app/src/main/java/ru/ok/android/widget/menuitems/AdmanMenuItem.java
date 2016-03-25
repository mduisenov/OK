package ru.ok.android.widget.menuitems;

import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import ru.mail.android.mytarget.core.models.banners.AppwallBanner;
import ru.mail.android.mytarget.nativeads.NativeAppwallAd;
import ru.mail.android.mytarget.nativeads.banners.NativeAppwallBanner;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.MenuView;
import ru.ok.android.widget.MenuView.MenuItem;
import ru.ok.android.widget.MenuView.ViewHolder;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;

public final class AdmanMenuItem extends MenuItem {
    private final String admanStatSectionName;
    private boolean admanStatShowsSent;
    private final NativeAppwallBanner banner;
    private OnBannerClickListener clickListener;
    private NativeAppwallAd nativeAppwallAd;

    public interface OnBannerClickListener {
        void onBannerClick(NativeAppwallBanner nativeAppwallBanner);
    }

    class Holder extends ViewHolder {
        public UrlImageView icon;
        public TextView name;
        public TextView notifyView;

        public Holder(int type, int position) {
            super(type, position);
        }
    }

    public AdmanMenuItem(NativeAppwallBanner banner, String admanStatSectionName, int height, NativeAppwallAd nativeAppwallAd) {
        super(height, Type.blacklist);
        this.banner = banner;
        this.admanStatSectionName = admanStatSectionName;
        this.nativeAppwallAd = nativeAppwallAd;
    }

    public void setClickListener(OnBannerClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void onClick(MenuView menuView, MenuItem item) {
        StatisticManager.getInstance().addStatisticEvent("click-adman-app", new Pair("app", this.banner.getTitle()));
        if (this.clickListener != null) {
            this.clickListener.onBannerClick(this.banner);
        }
        if (this.banner != null && (this.banner instanceof AppwallBanner)) {
            ((AppwallBanner) this.banner).setHasNotification(false);
        }
        super.onClick(menuView, item);
    }

    public int getType() {
        return 5;
    }

    public View getView(LocalizationManager inflater, View view, int position, Type selectedItem) {
        Holder holder;
        if (view == null) {
            view = LocalizationManager.inflate(inflater.getContext(), 2130903312, null, false);
            holder = createViewHolder(getType(), position);
            holder.name = (TextView) view.findViewById(2131625064);
            holder.icon = (UrlImageView) view.findViewById(2131625062);
            holder.notifyView = (TextView) view.findViewById(2131625063);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
            holder.position = position;
        }
        bindView(holder);
        return view;
    }

    protected void bindView(Holder holder) {
        holder.name.setText(this.banner.getTitle());
        holder.notifyView.setText(!TextUtils.isEmpty(this.banner.getStatus()) ? this.banner.getStatus() : "!");
        TextView textView = holder.notifyView;
        int i = (this.banner.isHasNotification() || this.banner.getStatus().length() > 0) ? 0 : 8;
        textView.setVisibility(i);
        if (!(this.banner.getIcon() == null || this.banner.getIcon().getBitmap() == null)) {
            holder.icon.setImageBitmap(this.banner.getIcon().getBitmap());
        }
        if (!this.admanStatShowsSent) {
            Logger.m173d("Adman banner from sliding menu fire stat adShows %s %s %s", this.banner.getId(), this.admanStatSectionName, this.banner.getTitle());
            if (this.nativeAppwallAd != null) {
                this.nativeAppwallAd.handleBannerShow(this.banner);
            }
            this.admanStatShowsSent = true;
        }
    }

    public Holder createViewHolder(int type, int position) {
        return new Holder(type, position);
    }
}
