package ru.ok.android.ui.adapters.adman;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import ru.mail.android.mytarget.nativeads.NativeAppwallAd;
import ru.mail.android.mytarget.nativeads.banners.NativeAppwallBanner;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.utils.Logger;

public class BannersAdapter extends BaseAdapter {
    protected static LayoutInflater inflater;
    private boolean[] admanShowsHandlerCalled;
    private final String admanStatSectionName;
    private List<NativeAppwallBanner> data;
    private NativeAppwallAd nativeAppwallAd;

    public static class ViewHolder {
        public UrlImageView image;
        public TextView notify;
        public TextView textView;
        public TextView textViewDisc;

        public void setBanner(NativeAppwallBanner banner) {
            this.textView.setText(banner.getTitle());
            this.textViewDisc.setText(banner.getDescription());
            if (TextUtils.isEmpty(banner.getStatus())) {
                this.notify.setText("!");
            } else {
                this.notify.setText(banner.getStatus());
            }
            TextView textView = this.notify;
            int i = (banner.isHasNotification() || banner.getStatus().length() > 0) ? 0 : 8;
            textView.setVisibility(i);
            if (banner.getIcon() != null && banner.getIcon().getBitmap() != null) {
                this.image.setImageBitmap(banner.getIcon().getBitmap());
            }
        }
    }

    static {
        inflater = null;
    }

    public BannersAdapter(Context context, NativeAppwallAd nativeAppwallAd, List<NativeAppwallBanner> banners, String admanStatSectionName) {
        inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.data = banners;
        if (this.data != null && this.data.size() > 0) {
            this.admanShowsHandlerCalled = new boolean[this.data.size()];
        }
        this.admanStatSectionName = admanStatSectionName;
        this.nativeAppwallAd = nativeAppwallAd;
    }

    public int getCount() {
        return this.data.size();
    }

    public NativeAppwallBanner getBanner(int position) {
        return (NativeAppwallBanner) this.data.get(position);
    }

    public Object getItem(int i) {
        return this.data.get(i);
    }

    public long getItemId(int i) {
        return (long) ((NativeAppwallBanner) this.data.get(i)).getId().hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(2130903251, null);
            holder.textView = (TextView) convertView.findViewById(2131624665);
            holder.textViewDisc = (TextView) convertView.findViewById(2131624967);
            holder.image = (UrlImageView) convertView.findViewById(C0263R.id.image);
            holder.notify = (TextView) convertView.findViewById(2131624966);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        NativeAppwallBanner banner = (NativeAppwallBanner) this.data.get(position);
        holder.setBanner(banner);
        if (!this.admanShowsHandlerCalled[position]) {
            Logger.m173d("Adman banner from banners fragment fire stat adShows %s %s %s", banner.getId(), this.admanStatSectionName, banner.getTitle());
            if (!(this.nativeAppwallAd == null || banner == null)) {
                this.nativeAppwallAd.handleBannerShow(banner);
            }
            this.admanShowsHandlerCalled[position] = true;
        }
        return convertView;
    }
}
