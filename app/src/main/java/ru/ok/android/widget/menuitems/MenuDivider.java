package ru.ok.android.widget.menuitems;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity.MenuAdapter;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.MenuView.MenuItem;
import ru.ok.android.widget.MenuView.ViewHolder;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;

public final class MenuDivider extends MenuItem {
    private String text;
    private int textResId;

    class Holder extends ViewHolder {
        public TextView textView;

        public Holder(int type, int position) {
            super(type, position);
        }
    }

    public MenuDivider(int height) {
        this(height, 0);
    }

    public MenuDivider(int height, @StringRes int textResId) {
        super(height, Type.menu);
        this.textResId = textResId;
    }

    public MenuDivider(int height, String text) {
        super(height, Type.menu);
        this.text = text;
    }

    public int getType() {
        return 4;
    }

    public ViewHolder createViewHolder(int type, int position) {
        return new Holder(type, position);
    }

    public View getView(LocalizationManager inflater, View view, int position, Type selectedItem) {
        Holder holder;
        if (view == null) {
            view = LocalizationManager.inflate(inflater.getContext(), 2130903314, null, false);
            holder = (Holder) createViewHolder(getType(), position);
            view.setTag(holder);
            holder.textView = (TextView) view.findViewById(C0263R.id.text);
        } else {
            holder = (Holder) view.getTag();
        }
        String s = this.textResId == 0 ? this.text : inflater.getString(this.textResId);
        if (TextUtils.isEmpty(s)) {
            view.setPadding(0, 0, 0, 0);
            ViewUtil.gone(holder.textView);
        } else {
            view.setPadding(0, view.getResources().getDimensionPixelSize(2131231037), 0, 0);
            ViewUtil.visible(holder.textView);
            holder.textView.setText(s);
        }
        return view;
    }

    public View postGetView(View view, MenuAdapter adapter, Type selectedItem) {
        this.lastSelectedItem = selectedItem;
        this.cachedView = new WeakReference(view);
        view.setClickable(false);
        return view;
    }
}
