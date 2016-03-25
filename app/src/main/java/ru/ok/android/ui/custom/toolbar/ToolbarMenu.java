package ru.ok.android.ui.custom.toolbar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import ru.ok.android.utils.Logger;

public class ToolbarMenu implements Menu {
    private static int instanceCount;
    final Context context;
    private final int instanceNum;
    final ArrayList<ToolbarMenuItem> items;
    ToolbarMenuContentListener listener;

    interface ToolbarMenuContentListener {
        void onToolbarMenuDataSetChanged();
    }

    ToolbarMenu(Context context) {
        this.items = new ArrayList();
        int i = instanceCount + 1;
        instanceCount = i;
        this.instanceNum = i;
        m162d("Ctor", new Object[0]);
        this.context = context;
    }

    void setListener(ToolbarMenuContentListener listener) {
        this.listener = listener;
    }

    void notifyDataSetChanged() {
        if (this.listener != null) {
            this.listener.onToolbarMenuDataSetChanged();
        }
    }

    private void addItem(ToolbarMenuItem item) {
        this.items.add(item);
        notifyDataSetChanged();
    }

    public MenuItem add(CharSequence title) {
        m162d("title=%s", title);
        ToolbarMenuItem item = new ToolbarMenuItem(this.context, 0);
        item.setTitle(title);
        addItem(item);
        return item;
    }

    public MenuItem add(int titleRes) {
        m162d("titleRes=%s", ToolbarView.logResName(this.context, titleRes));
        ToolbarMenuItem item = new ToolbarMenuItem(this.context, 0);
        item.setTitle(titleRes);
        addItem(item);
        return item;
    }

    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        m162d("groupId=%s itemId=%s order=%d title=%s", ToolbarView.logResName(this.context, groupId), ToolbarView.logResName(this.context, itemId), Integer.valueOf(order), title);
        ToolbarMenuItem item = new ToolbarMenuItem(this.context, itemId);
        item.setTitle(title);
        addItem(item);
        return item;
    }

    public MenuItem add(int groupId, int itemId, int order, int titleRes) {
        m162d("groupId=%s itemId=%s order=%d title=%s", ToolbarView.logResName(this.context, groupId), ToolbarView.logResName(this.context, itemId), Integer.valueOf(order), ToolbarView.logResName(this.context, titleRes));
        ToolbarMenuItem item = new ToolbarMenuItem(this.context, itemId);
        item.setTitle(titleRes);
        addItem(item);
        return item;
    }

    public SubMenu addSubMenu(CharSequence title) {
        m162d("title=%s", title);
        ToolbarMenuItem menuItem = new ToolbarMenuItem(this.context, 0);
        menuItem.setTitle(title);
        addItem(menuItem);
        return new ToolbarSubMenu(this.context, menuItem);
    }

    public SubMenu addSubMenu(int titleRes) {
        m162d("title=%s", ToolbarView.logResName(this.context, titleRes));
        ToolbarMenuItem menuItem = new ToolbarMenuItem(this.context, 0);
        menuItem.setTitle(titleRes);
        addItem(menuItem);
        return new ToolbarSubMenu(this.context, menuItem);
    }

    public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
        m162d("groupId=%s itemId=%s order=%d title=%s", ToolbarView.logResName(this.context, groupId), ToolbarView.logResName(this.context, itemId), Integer.valueOf(order), title);
        ToolbarMenuItem menuItem = new ToolbarMenuItem(this.context, itemId);
        menuItem.setTitle(title);
        addItem(menuItem);
        return new ToolbarSubMenu(this.context, menuItem);
    }

    public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
        m162d("groupId=%s itemId=%s order=%d titleRes=%s", ToolbarView.logResName(this.context, groupId), ToolbarView.logResName(this.context, itemId), Integer.valueOf(order), ToolbarView.logResName(this.context, titleRes));
        ToolbarMenuItem menuItem = new ToolbarMenuItem(this.context, itemId);
        menuItem.setTitle(titleRes);
        addItem(menuItem);
        return new ToolbarSubMenu(this.context, menuItem);
    }

    public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
        String str = "groupId=%s itemId=%s order=%d caller=%s specifics=%s intent=%s flags=%s outSpecificItems=%s";
        Object[] objArr = new Object[8];
        objArr[0] = ToolbarView.logResName(this.context, groupId);
        objArr[1] = ToolbarView.logResName(this.context, itemId);
        objArr[2] = Integer.valueOf(order);
        objArr[3] = caller;
        objArr[4] = Logger.isLoggingEnable() ? Arrays.toString(specifics) : "";
        objArr[5] = intent;
        objArr[6] = Logger.isLoggingEnable() ? "0x" + Integer.toHexString(flags) : "";
        objArr[7] = Logger.isLoggingEnable() ? Arrays.toString(outSpecificItems) : "";
        m162d(str, objArr);
        return 0;
    }

    public void removeItem(int id) {
        m162d("id=%s", ToolbarView.logResName(this.context, id));
        if (id != 0) {
            boolean removed = false;
            ListIterator<ToolbarMenuItem> itr = this.items.listIterator();
            while (itr.hasNext()) {
                if (((ToolbarMenuItem) itr.next()).getItemId() == id) {
                    removed = true;
                    itr.remove();
                }
            }
            if (removed) {
                notifyDataSetChanged();
            }
        }
    }

    public void removeGroup(int groupId) {
        m162d("groupId=%s", ToolbarView.logResName(this.context, groupId));
    }

    public void clear() {
        m162d("", new Object[0]);
        boolean wasEmpty = this.items.isEmpty();
        this.items.clear();
        if (!wasEmpty) {
            notifyDataSetChanged();
        }
    }

    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
        m162d("group=%s checkable=%s exclusive=%s", ToolbarView.logResName(this.context, group), Boolean.valueOf(checkable), Boolean.valueOf(exclusive));
    }

    public void setGroupVisible(int group, boolean visible) {
        m162d("group=%s visible=%s", ToolbarView.logResName(this.context, group), Boolean.valueOf(visible));
    }

    public void setGroupEnabled(int group, boolean enabled) {
        m162d("group=%s enabled=%s", ToolbarView.logResName(this.context, group), Boolean.valueOf(enabled));
    }

    public boolean hasVisibleItems() {
        m162d("", new Object[0]);
        Iterator i$ = this.items.iterator();
        while (i$.hasNext()) {
            if (((ToolbarMenuItem) i$.next()).isVisible) {
                return true;
            }
        }
        return false;
    }

    public ToolbarMenuItem findItem(int id) {
        m162d("id=%s", ToolbarView.logResName(this.context, id));
        if (id != 0) {
            Iterator i$ = this.items.iterator();
            while (i$.hasNext()) {
                ToolbarMenuItem item = (ToolbarMenuItem) i$.next();
                if (item.id == id) {
                    return item;
                }
                if (item.subMenu != null) {
                    ToolbarMenuItem subItem = item.subMenu.findItem(id);
                    if (subItem != null) {
                        return subItem;
                    }
                }
            }
        }
        return null;
    }

    public int size() {
        return this.items.size();
    }

    public ToolbarMenuItem getItem(int index) {
        m162d("index=%d", Integer.valueOf(index));
        if (index < 0 || index >= this.items.size()) {
            return null;
        }
        return (ToolbarMenuItem) this.items.get(index);
    }

    public void close() {
        m162d("", new Object[0]);
    }

    public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
        String str = "keyCode=%d event=%s flags=%s";
        Object[] objArr = new Object[3];
        objArr[0] = Integer.valueOf(keyCode);
        objArr[1] = event;
        objArr[2] = Logger.isLoggingEnable() ? "0x" + Integer.toHexString(flags) : "";
        m162d(str, objArr);
        return false;
    }

    public boolean isShortcutKey(int keyCode, KeyEvent event) {
        m162d("keyCode=%d event=%s", Integer.valueOf(keyCode), event);
        return false;
    }

    public boolean performIdentifierAction(int id, int flags) {
        String str = "id=%s flags=%s";
        Object[] objArr = new Object[2];
        objArr[0] = ToolbarView.logResName(this.context, id);
        objArr[1] = Logger.isLoggingEnable() ? "0x" + Integer.toHexString(flags) : "";
        m162d(str, objArr);
        return false;
    }

    public void setQwertyMode(boolean isQwerty) {
        m162d("isQwerty=%s", Boolean.valueOf(isQwerty));
    }

    protected void m162d(String format, Object... args) {
        if (Logger.isLoggingEnable()) {
            Logger.m173d("[" + this.instanceNum + "] " + format, args);
        }
    }

    static {
        instanceCount = 0;
    }
}
