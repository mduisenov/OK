package ru.ok.android.widget.menuitems;

import java.util.List;
import ru.mail.android.mytarget.nativeads.banners.NativeAppwallBanner;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity;
import ru.ok.android.widget.MenuView;
import ru.ok.android.widget.MenuView.MenuItem;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;
import ru.ok.android.widget.menuitems.StandardItem.BubbleState;

public class MoreBannersItem extends StandardItem {
    private final List<NativeAppwallBanner> banners;
    private OnMoreBannersClickListener listener;
    private final String section;

    public interface OnMoreBannersClickListener {
        void onMoreBannersClick(String str, List<NativeAppwallBanner> list);
    }

    public MoreBannersItem(OdklSlidingMenuFragmentActivity activity, int height, String section, List<NativeAppwallBanner> banners) {
        super(activity, 2130838452, 2131166591, Type.more, height, BubbleState.gray);
        this.banners = banners;
        this.section = section;
    }

    public void setListener(OnMoreBannersClickListener listener) {
        this.listener = listener;
    }

    public void onClick(MenuView menuView, MenuItem item) {
        menuView.close();
        if (this.listener != null) {
            this.listener.onMoreBannersClick(this.section, this.banners);
        }
    }
}
