package ru.ok.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import java.lang.ref.WeakReference;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity.MenuAdapter;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;
import ru.ok.android.widget.menuitems.StandardItem;
import ru.ok.android.widget.menuitems.StandardItem.BubbleState;

public class MenuView extends ScrimInsetsListView {

    public static abstract class MenuItem {
        protected WeakReference<View> cachedView;
        protected int height;
        protected ViewHolder holder;
        protected Type lastSelectedItem;
        protected final Type type;

        public abstract int getType();

        public abstract View getView(LocalizationManager localizationManager, View view, int i, Type type);

        public MenuItem(int height, Type type) {
            this.height = 0;
            this.cachedView = null;
            this.height = height;
            this.type = type;
        }

        public void onClick(MenuView menuView, MenuItem item) {
            menuView.close();
        }

        protected static String getCounterText(int i, BubbleState bubbleState) {
            if (i == Integer.MAX_VALUE) {
                return null;
            }
            if (bubbleState == BubbleState.gray) {
                return String.valueOf(i);
            }
            return i > 99 ? "99+" : String.valueOf(i);
        }

        public View postGetView(View view, MenuAdapter adapter, Type selectedItem) {
            this.lastSelectedItem = selectedItem;
            this.cachedView = new WeakReference(view);
            view.setMinimumHeight(this.height);
            view.setClickable(true);
            view.setOnClickListener(adapter);
            Object tag = view.getTag();
            if (tag instanceof ViewHolder) {
                if (!(this.holder == null || this.holder.getMenuItemBase() == null)) {
                    this.holder.getMenuItemBase().holder = null;
                }
                ((ViewHolder) tag).setMenuItemBase(this);
                this.holder = (ViewHolder) tag;
            }
            return view;
        }

        public View getCachedView() {
            return this.cachedView == null ? null : (View) this.cachedView.get();
        }

        public void invalidateView() {
            View view = getCachedView();
            if (view != null) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                int position = viewHolder.getPosition();
                if (viewHolder.getMenuItemBase().equals(this)) {
                    getView(LocalizationManager.from(view.getContext()), view, position, this.lastSelectedItem).invalidate();
                }
            }
        }
    }

    public static class ViewHolder {
        private MenuItem menuItemBase;
        public int position;
        private final int type;

        public ViewHolder(int type, int position) {
            this.menuItemBase = null;
            this.type = type;
            this.position = position;
        }

        public int getPosition() {
            return this.position;
        }

        public MenuItem getMenuItemBase() {
            return this.menuItemBase;
        }

        public void setMenuItemBase(MenuItem menuItemBase) {
            this.menuItemBase = menuItemBase;
        }
    }

    public static void updateAvatar() {
        GlobalBus.send(2131624052, new BusEvent());
    }

    public void open() {
        if (getContext() instanceof OdklSlidingMenuFragmentActivity) {
            ((OdklSlidingMenuFragmentActivity) getContext()).openMenu();
        }
    }

    public void close() {
        if (getContext() instanceof OdklSlidingMenuFragmentActivity) {
            ((OdklSlidingMenuFragmentActivity) getContext()).closeMenu();
        }
    }

    public MenuView(Context context) {
        super(context);
        init();
    }

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public static void setCounterToStandartItem(StandardItem standartItem, int counter, int counter2) {
        if (standartItem != null) {
            standartItem.setCounter(counter, counter2);
            standartItem.invalidateView();
        }
    }

    public static void setCounterToStandartItem(StandardItem standartItem, int counter, int counter2, boolean isReply, boolean isLike) {
        if (standartItem != null) {
            standartItem.setCounter(counter, counter2, isReply, isLike);
            standartItem.invalidateView();
        }
    }

    public void init() {
        setCacheColorHint(0);
        setDescendantFocusability(262144);
    }
}
