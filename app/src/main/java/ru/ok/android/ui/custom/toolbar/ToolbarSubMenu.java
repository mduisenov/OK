package ru.ok.android.ui.custom.toolbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

class ToolbarSubMenu extends ToolbarMenu implements ContextMenu, SubMenu {
    final MenuItem menuItem;

    ToolbarSubMenu(Context context, ToolbarMenuItem menuItem) {
        super(context);
        this.menuItem = menuItem;
        menuItem.subMenu = this;
    }

    public ToolbarSubMenu setHeaderTitle(int titleRes) {
        m162d("titleRes=%s", ToolbarView.logResName(this.context, titleRes));
        return this;
    }

    public ToolbarSubMenu setHeaderTitle(CharSequence title) {
        m162d("title=%s", title);
        return this;
    }

    public ToolbarSubMenu setHeaderIcon(int iconRes) {
        m162d("iconRes=%s", ToolbarView.logResName(this.context, iconRes));
        return this;
    }

    public ToolbarSubMenu setHeaderIcon(Drawable icon) {
        m162d("icon=%s", icon);
        return this;
    }

    public ToolbarSubMenu setHeaderView(View view) {
        m162d("view=%s", view);
        return this;
    }

    public void clearHeader() {
        m162d("", new Object[0]);
    }

    public SubMenu setIcon(int iconRes) {
        m162d("iconRes=%s", ToolbarView.logResName(this.context, iconRes));
        return this;
    }

    public SubMenu setIcon(Drawable icon) {
        m162d("icon=%s", icon);
        return this;
    }

    public MenuItem getItem() {
        return this.menuItem;
    }
}
