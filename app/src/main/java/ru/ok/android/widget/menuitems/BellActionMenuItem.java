package ru.ok.android.widget.menuitems;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import ru.ok.android.ui.custom.NotificationsView;

public final class BellActionMenuItem {
    private int count;
    private final Fragment fragment;
    private MenuItem item;

    /* renamed from: ru.ok.android.widget.menuitems.BellActionMenuItem.1 */
    class C14961 implements OnClickListener {
        C14961() {
        }

        public void onClick(View view) {
            Activity activity = BellActionMenuItem.this.fragment.getActivity();
            if (activity != null) {
                activity.onOptionsItemSelected(BellActionMenuItem.this.item);
            }
        }
    }

    /* renamed from: ru.ok.android.widget.menuitems.BellActionMenuItem.2 */
    class C14972 implements OnClickListener {
        C14972() {
        }

        public void onClick(View view) {
            Activity activity = BellActionMenuItem.this.fragment.getActivity();
            if (activity != null) {
                activity.onOptionsItemSelected(BellActionMenuItem.this.item);
            }
        }
    }

    public BellActionMenuItem(Fragment fragment) {
        this.count = 0;
        this.fragment = fragment;
    }

    public void setItemMenu(MenuItem item) {
        View notificationButton;
        this.item = item;
        View actionView = item == null ? null : MenuItemCompat.getActionView(item);
        if (actionView == null) {
            notificationButton = null;
        } else {
            notificationButton = actionView.findViewById(2131624498);
        }
        if (notificationButton != null) {
            notificationButton.setOnClickListener(new C14961());
        }
        notificationButton = actionView == null ? null : actionView.findViewById(2131624500);
        if (notificationButton != null) {
            notificationButton.setOnClickListener(new C14972());
        }
    }

    public boolean setCount(int count) {
        if (count == this.count) {
            return false;
        }
        this.count = count;
        return refreshCount();
    }

    public boolean refreshCount() {
        boolean visible = false;
        if (this.item == null) {
            return false;
        }
        ((NotificationsView) MenuItemCompat.getActionView(this.item).findViewById(2131624499)).setValue(this.count);
        boolean oldVisible = this.item.isVisible();
        if (this.count > 0) {
            visible = true;
        }
        this.item.setVisible(visible);
        return visible ^ oldVisible;
    }
}
