package ru.ok.android.ui.custom.toolbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupMenu;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import ru.ok.android.C0206R;
import ru.ok.android.utils.Logger;

public class ToolbarView extends LinearLayout implements OnClickListener, ToolbarMenuContentListener {
    private LayoutInflater layoutInflater;
    private OnToolbarItemSelectedListener listener;
    private ToolbarMenu menu;
    private boolean subMenuWithIcons;

    /* renamed from: ru.ok.android.ui.custom.toolbar.ToolbarView.1 */
    class C07591 implements OnMenuItemClickListener {
        final /* synthetic */ View val$anchorView;

        /* renamed from: ru.ok.android.ui.custom.toolbar.ToolbarView.1.1 */
        class C07581 implements Runnable {
            final /* synthetic */ ToolbarMenuItem val$toolbarMenuItem;

            C07581(ToolbarMenuItem toolbarMenuItem) {
                this.val$toolbarMenuItem = toolbarMenuItem;
            }

            public void run() {
                ToolbarView.this.onItemSelected(C07591.this.val$anchorView, this.val$toolbarMenuItem);
            }
        }

        C07591(View view) {
            this.val$anchorView = view;
        }

        public boolean onMenuItemClick(MenuItem item) {
            ToolbarMenuItem toolbarMenuItem;
            int itemId = item.getItemId();
            if (ToolbarView.this.menu != null) {
                toolbarMenuItem = ToolbarView.this.menu.findItem(itemId);
            } else {
                toolbarMenuItem = null;
            }
            if (toolbarMenuItem != null) {
                this.val$anchorView.post(new C07581(toolbarMenuItem));
            } else {
                ToolbarView.this.performItemAction(item);
            }
            return true;
        }
    }

    public interface OnToolbarItemSelectedListener {
        void onToolbarItemSelected(MenuItem menuItem);

        void onToolbarSubmenuOpened(MenuItem menuItem);
    }

    public ToolbarView(Context context) {
        this(context, null);
    }

    public ToolbarView(Context context, AttributeSet attrs) {
        this(context, attrs, 2130772009, 2131296800);
    }

    public ToolbarView(Context context, AttributeSet attrs, int defAttr, int defStyle) {
        super(context, attrs);
        this.layoutInflater = LayoutInflater.from(context);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.ToolbarView, defAttr, defStyle);
        int menuResId = a.getResourceId(0, 0);
        this.subMenuWithIcons = a.getBoolean(1, false);
        a.recycle();
        if (menuResId != 0) {
            setMenu(menuResId);
        }
    }

    public void setListener(OnToolbarItemSelectedListener listener) {
        this.listener = listener;
    }

    public void setMenu(int menuResId) {
        if (menuResId != 0) {
            MenuInflater menuInflater = createToolbarMenuInflater(getContext());
            ToolbarMenu menu = new ToolbarMenu(getContext());
            try {
                menuInflater.inflate(menuResId, menu);
                setMenuInternal(menu);
            } catch (Throwable e) {
                Logger.m176e("Failed to inflate menu: " + e);
                Logger.m178e(e);
            }
        }
    }

    public ToolbarMenu getMenu() {
        return this.menu;
    }

    protected ToolbarMenuInflater createToolbarMenuInflater(Context context) {
        return new ToolbarMenuInflater(context);
    }

    protected void notifyItemSelected(MenuItem item) {
        if (this.listener != null) {
            this.listener.onToolbarItemSelected(item);
        }
    }

    protected void notifySubmenuOpened(MenuItem item) {
        if (this.listener != null) {
            this.listener.onToolbarSubmenuOpened(item);
        }
    }

    private void setMenuInternal(ToolbarMenu menu) {
        if (this.menu != null) {
            this.menu.setListener(null);
        }
        this.menu = menu;
        if (menu != null) {
            menu.setListener(this);
        }
        reloadMenu();
    }

    private void reloadMenu() {
        removeAllViews();
        if (this.menu != null && this.menu.hasVisibleItems()) {
            ArrayList<ToolbarMenuItem> endAlignedItems = null;
            Iterator i$ = this.menu.items.iterator();
            while (i$.hasNext()) {
                ToolbarMenuItem item = (ToolbarMenuItem) i$.next();
                if (item.isVisible) {
                    if (item.align == 1 || (item.align != 2 && endAlignedItems == null)) {
                        addView(createToolbarItemView(item));
                    } else {
                        if (endAlignedItems == null) {
                            endAlignedItems = new ArrayList();
                        }
                        endAlignedItems.add(item);
                    }
                }
            }
            if (endAlignedItems != null && endAlignedItems.size() > 0) {
                View startEndSeparatorView = new View(getContext());
                LayoutParams lp = new LayoutParams(-1, 1);
                lp.weight = 1.0f;
                addView(startEndSeparatorView, lp);
                i$ = endAlignedItems.iterator();
                while (i$.hasNext()) {
                    addView(createToolbarItemView((ToolbarMenuItem) i$.next()));
                }
            }
        }
    }

    private View createToolbarItemView(ToolbarMenuItem item) {
        View itemView;
        if (item.customLayoutId == 0) {
            itemView = createToolbarItemDefaultView(item);
        } else {
            itemView = createToolbarItemCustomView(item);
        }
        itemView.setId(item.id);
        itemView.setEnabled(item.isEnabled);
        itemView.setOnClickListener(this);
        itemView.setTag(item);
        return itemView;
    }

    private View createToolbarItemDefaultView(ToolbarMenuItem item) {
        View itemView = this.layoutInflater.inflate(2130903539, this, false);
        ImageView imageView = (ImageView) itemView.findViewById(2131625401);
        imageView.setImageDrawable(item.getIcon());
        item.itemView = imageView;
        return itemView;
    }

    private View createToolbarItemCustomView(ToolbarMenuItem item) {
        FrameLayout customContainer = (FrameLayout) this.layoutInflater.inflate(2130903540, this, false);
        View itemView = this.layoutInflater.inflate(item.customLayoutId, customContainer, false);
        if (itemView.isClickable()) {
            itemView.setTag(item);
            itemView.setOnClickListener(this);
        }
        if (item.isCheckable && (itemView instanceof Checkable)) {
            ((Checkable) itemView).setChecked(item.isChecked);
        }
        ((FrameLayout.LayoutParams) itemView.getLayoutParams()).gravity = 17;
        customContainer.addView(itemView);
        item.itemView = itemView;
        return customContainer;
    }

    public void onClick(View v) {
        ToolbarMenuItem tag = v.getTag();
        if (tag instanceof ToolbarMenuItem) {
            ToolbarMenuItem item = tag;
            if (item.itemView == v || item.itemView == null || !item.itemView.isClickable()) {
                if (item.isCheckable) {
                    item.isChecked = !item.isChecked;
                }
                onItemSelected(v, item);
                return;
            }
            item.itemView.performClick();
        }
    }

    private void onItemSelected(View anchorView, ToolbarMenuItem item) {
        if (item.hasSubMenu()) {
            showPopupSubMenu(anchorView, item.subMenu);
            notifySubmenuOpened(item);
            return;
        }
        performItemAction(item);
    }

    private void performItemAction(MenuItem menuItem) {
        notifyItemSelected(menuItem);
    }

    @TargetApi(11)
    private void showPopupSubMenu(View anchorView, ToolbarSubMenu subMenu) {
        if (subMenu.hasVisibleItems()) {
            PopupMenu popupMenu = new PopupMenu(getContext(), anchorView);
            if (this.subMenuWithIcons) {
                enableIconsInPopupMenu(popupMenu);
            }
            inflateSubMenu(popupMenu.getMenu(), subMenu, anchorView);
            popupMenu.show();
        }
    }

    private void enableIconsInPopupMenu(PopupMenu popup) {
        try {
            for (Field field : popup.getClass().getDeclaredFields()) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class.forName(menuPopupHelper.getClass().getName()).getMethod("setForceShowIcon", new Class[]{Boolean.TYPE}).invoke(menuPopupHelper, new Object[]{Boolean.valueOf(true)});
                    return;
                }
            }
        } catch (Throwable e) {
            Logger.m176e("failed to enable icons in popup menu");
            Logger.m178e(e);
        }
    }

    private void inflateSubMenu(Menu targetMenu, ToolbarMenu srcMenu, View anchorView) {
        OnMenuItemClickListener subMenuItemClickListener = new C07591(anchorView);
        Iterator i$ = srcMenu.items.iterator();
        while (i$.hasNext()) {
            ToolbarMenuItem item = (ToolbarMenuItem) i$.next();
            if (item.isVisible) {
                MenuItem targetItem = targetMenu.add(0, item.id, 0, item.title);
                targetItem.setEnabled(item.isEnabled);
                targetItem.setCheckable(item.isCheckable);
                targetItem.setChecked(item.isChecked);
                targetItem.setIcon(item.icon);
                targetItem.setIntent(item.intent);
                targetItem.setOnMenuItemClickListener(subMenuItemClickListener);
            }
        }
    }

    public void onToolbarMenuDataSetChanged() {
        reloadMenu();
    }

    static String logResName(Context context, int resId) {
        String resName = null;
        if (Logger.isLoggingEnable()) {
            try {
                resName = context.getResources().getResourceName(resId);
            } catch (NotFoundException e) {
            }
        }
        if (resName == null) {
            return "0x" + Integer.toHexString(resId);
        }
        return resName;
    }
}
