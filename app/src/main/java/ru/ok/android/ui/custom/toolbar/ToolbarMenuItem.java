package ru.ok.android.ui.custom.toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.widget.Checkable;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;

public class ToolbarMenuItem implements MenuItem {
    private static int instanceCount;
    int align;
    private OnMenuItemClickListener clickListener;
    private final Context context;
    int customLayoutId;
    Drawable icon;
    final int id;
    private final int instanceNum;
    Intent intent;
    boolean isCheckable;
    boolean isChecked;
    boolean isEnabled;
    boolean isVisible;
    View itemView;
    private final LocalizationManager localizationManager;
    ToolbarSubMenu subMenu;
    String title;

    ToolbarMenuItem(Context context, int id) {
        int i = instanceCount + 1;
        instanceCount = i;
        this.instanceNum = i;
        this.context = context;
        this.localizationManager = LocalizationManager.from(context);
        this.id = id;
    }

    public int getItemId() {
        return this.id;
    }

    public int getGroupId() {
        return 0;
    }

    public int getOrder() {
        return 0;
    }

    public MenuItem setTitle(CharSequence title) {
        m163d("title=%s", title);
        this.title = title.toString();
        return this;
    }

    public MenuItem setTitle(int titleResId) {
        m163d("titleResId=%s", ToolbarView.logResName(this.context, titleResId));
        this.title = this.localizationManager.getString(titleResId);
        return this;
    }

    public CharSequence getTitle() {
        return this.title;
    }

    public MenuItem setTitleCondensed(CharSequence title) {
        m163d("title=%s", title);
        return this;
    }

    public CharSequence getTitleCondensed() {
        return this.title;
    }

    public MenuItem setIcon(Drawable icon) {
        m163d("icon=%s", icon);
        this.icon = icon;
        return this;
    }

    public MenuItem setIcon(int iconRes) {
        m163d("iconRes=%s", ToolbarView.logResName(this.context, iconRes));
        this.icon = this.context.getResources().getDrawable(iconRes);
        return this;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public MenuItem setIntent(Intent intent) {
        m163d("intent=%s", intent);
        this.intent = intent;
        return this;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public MenuItem setShortcut(char numericChar, char alphaChar) {
        m163d("numericChar=%c alphaChar=%c", Character.valueOf(numericChar), Character.valueOf(alphaChar));
        return this;
    }

    public MenuItem setNumericShortcut(char numericChar) {
        m163d("numericChar=%c", Character.valueOf(numericChar));
        return this;
    }

    public char getNumericShortcut() {
        return '\u0000';
    }

    public MenuItem setAlphabeticShortcut(char alphaChar) {
        m163d("alphaChar=%c", Character.valueOf(alphaChar));
        return this;
    }

    public char getAlphabeticShortcut() {
        return '\u0000';
    }

    public MenuItem setCheckable(boolean checkable) {
        m163d("checkable=%s", Boolean.valueOf(checkable));
        this.isCheckable = checkable;
        return this;
    }

    public boolean isCheckable() {
        return this.isCheckable;
    }

    public MenuItem setChecked(boolean checked) {
        m163d("checked=%s", Boolean.valueOf(checked));
        this.isChecked = checked;
        if (this.itemView instanceof Checkable) {
            ((Checkable) this.itemView).setChecked(checked);
        }
        return this;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public MenuItem setVisible(boolean visible) {
        m163d("visible=%s", Boolean.valueOf(visible));
        this.isVisible = visible;
        return this;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public MenuItem setEnabled(boolean enabled) {
        m163d("enabled=%s", Boolean.valueOf(enabled));
        this.isEnabled = enabled;
        return this;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public boolean hasSubMenu() {
        return this.subMenu != null;
    }

    public SubMenu getSubMenu() {
        return this.subMenu;
    }

    public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
        m163d("listener=%s", menuItemClickListener);
        this.clickListener = menuItemClickListener;
        return this;
    }

    public ContextMenuInfo getMenuInfo() {
        return null;
    }

    public void setShowAsAction(int actionEnum) {
        String str = "actionEnum=%s";
        Object[] objArr = new Object[1];
        objArr[0] = Logger.isLoggingEnable() ? Integer.toBinaryString(actionEnum) : "";
        m163d(str, objArr);
    }

    public MenuItem setShowAsActionFlags(int actionEnum) {
        String str = "actionEnum=%s";
        Object[] objArr = new Object[1];
        objArr[0] = Logger.isLoggingEnable() ? Integer.toBinaryString(actionEnum) : "";
        m163d(str, objArr);
        return this;
    }

    public MenuItem setActionView(View view) {
        m163d("view=%s", view);
        return this;
    }

    public MenuItem setActionView(int resId) {
        m163d("resId=%s", ToolbarView.logResName(this.context, resId));
        this.customLayoutId = resId;
        return this;
    }

    public View getActionView() {
        return this.itemView;
    }

    public MenuItem setActionProvider(ActionProvider actionProvider) {
        m163d("actionProvider=%s", actionProvider);
        return this;
    }

    public ActionProvider getActionProvider() {
        return null;
    }

    public boolean expandActionView() {
        m163d("", new Object[0]);
        return false;
    }

    public boolean collapseActionView() {
        m163d("", new Object[0]);
        return false;
    }

    public boolean isActionViewExpanded() {
        return false;
    }

    public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
        m163d("listener=%s", listener);
        return this;
    }

    protected void m163d(String format, Object... args) {
        if (Logger.isLoggingEnable()) {
            Logger.m173d("[" + this.instanceNum + "] " + format, args);
        }
    }

    static {
        instanceCount = 0;
    }
}
