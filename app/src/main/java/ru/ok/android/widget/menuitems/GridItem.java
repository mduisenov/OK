package ru.ok.android.widget.menuitems;

import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import ru.mail.libverify.C0176R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity.MenuAdapter;
import ru.ok.android.slidingmenu.SlidingMenuStrategy;
import ru.ok.android.slidingmenu.SlidingMenuStrategy.StrategyType;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.MenuView.MenuItem;
import ru.ok.android.widget.MenuView.ViewHolder;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;
import ru.ok.android.widget.menuitems.StandardItem.BubbleState;

public final class GridItem extends MenuItem implements OnClickListener {
    private OdklSlidingMenuFragmentActivity activity;
    private boolean doAnimateSearch;
    private boolean isShowGrid;
    private int mCounter1;
    private int mCounter2;
    private int mCounter3;
    private int mName0;
    private int mName1;
    private int mName2;
    private int mName3;
    private float openingRatio;
    private int searchClosedTranslationX;
    private int searchClosedTranslationY;
    private WeakReference<View> weakReferenceSearchIcon;
    private WeakReference<View> weakReferenceSearchName;

    /* renamed from: ru.ok.android.widget.menuitems.GridItem.1 */
    class C14981 implements Runnable {
        final /* synthetic */ View val$v;

        C14981(View view) {
            this.val$v = view;
        }

        public void run() {
            switch (this.val$v.getId()) {
                case 2131625068:
                    NavigationHelper.showSearchPage(GridItem.this.activity, null);
                case 2131625069:
                    NavigationHelper.showNotificationsPage(GridItem.this.activity, true);
                case 2131625070:
                    NavigationHelper.showGuestPage(GridItem.this.activity);
                case 2131625071:
                    NavigationHelper.showMarksPage(GridItem.this.activity);
                default:
            }
        }
    }

    class Holder extends ViewHolder {
        public View counter0;
        public TextView counter1;
        public TextView counter2;
        public TextView counter3;
        public View grid_layout;
        public View image0;
        public View image1;
        public View image2;
        public View image3;
        public TextView name0;
        public TextView name1;
        public TextView name2;
        public TextView name3;

        public Holder(int type, int position) {
            super(type, position);
        }
    }

    public View postGetView(View view, MenuAdapter adapter, Type selectedItem) {
        super.postGetView(view, adapter, selectedItem);
        this.lastSelectedItem = selectedItem;
        this.cachedView = new WeakReference(view);
        view.setMinimumHeight(this.height);
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setMinimumHeight(this.height);
        }
        return view;
    }

    public void setCounter(int counter1, int counter2, int counter3) {
        this.mCounter1 = counter1;
        this.mCounter2 = counter2;
        this.mCounter3 = counter3;
        View view = getCachedView();
        if (view != null) {
            getView(LocalizationManager.from(view.getContext()), view, ((Holder) view.getTag()).getPosition(), this.lastSelectedItem);
        }
    }

    public GridItem(int name0, int name1, int counter1, int name2, int counter2, int name3, int counter3, OdklSlidingMenuFragmentActivity activity, int height) {
        boolean z = true;
        super(height, Type.grid);
        this.isShowGrid = true;
        this.doAnimateSearch = false;
        this.weakReferenceSearchName = null;
        this.weakReferenceSearchIcon = null;
        if (SlidingMenuStrategy.getStrategyType() != StrategyType.Custom) {
            z = false;
        }
        this.doAnimateSearch = z;
        this.mName0 = name0;
        this.mName1 = name1;
        this.mName2 = name2;
        this.mName3 = name3;
        this.mCounter1 = counter1;
        this.mCounter2 = counter2;
        this.mCounter3 = counter3;
        this.activity = activity;
    }

    public int getType() {
        return 1;
    }

    public void onClick(View v) {
        this.activity.getSlidingMenuStrategy().processRunnableClick(new C14981(v));
        SlidingMenuHelper.closeMenu(this.activity);
    }

    public View getView(LocalizationManager inflater, View view, int position, Type selectedItem) {
        Holder holder;
        if (view == null) {
            view = LocalizationManager.inflate(inflater.getContext(), 2130903315, null, false);
            holder = createViewHolder(getType(), position);
            View bufferView = view.findViewById(2131625069);
            bufferView.setOnClickListener(this);
            holder.name1 = (TextView) bufferView.findViewById(2131625310);
            holder.counter1 = (TextView) bufferView.findViewById(2131625076);
            holder.image1 = bufferView.findViewById(C0176R.id.icon);
            holder.image1.setBackgroundResource(2130838458);
            bufferView = view.findViewById(2131625071);
            bufferView.setOnClickListener(this);
            holder.name2 = (TextView) bufferView.findViewById(2131625310);
            holder.counter2 = (TextView) bufferView.findViewById(2131625076);
            holder.image2 = bufferView.findViewById(C0176R.id.icon);
            holder.image2.setBackgroundResource(2130838472);
            bufferView = view.findViewById(2131625070);
            bufferView.setOnClickListener(this);
            holder.name3 = (TextView) bufferView.findViewById(2131625310);
            holder.counter3 = (TextView) bufferView.findViewById(2131625076);
            holder.image3 = bufferView.findViewById(C0176R.id.icon);
            holder.image3.setBackgroundResource(2130838436);
            bufferView = view.findViewById(2131625068);
            bufferView.setOnClickListener(this);
            holder.name0 = (TextView) bufferView.findViewById(2131625310);
            holder.counter0 = bufferView.findViewById(2131625076);
            holder.image0 = bufferView.findViewById(C0176R.id.icon);
            holder.image0.setBackgroundResource(2130838475);
            holder.grid_layout = view.findViewById(2131625067);
            view.setTag(createViewHolder(getType(), position));
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (this.doAnimateSearch) {
            this.weakReferenceSearchName = new WeakReference(holder.name0);
            this.weakReferenceSearchIcon = new WeakReference(holder.image0);
            Resources resources = this.activity.getResources();
            this.searchClosedTranslationX = -resources.getDimensionPixelSize(2131231180);
            this.searchClosedTranslationY = resources.getDimensionPixelSize(2131231181);
            onSlidingMenuChangedOpeningRatio(this.openingRatio);
        }
        this.lastSelectedItem = selectedItem;
        boolean notificationSelected = selectedItem == Type.notifications;
        boolean marksSelected = selectedItem == Type.marks;
        boolean guestsSelected = selectedItem == Type.guests;
        holder.name1.setSelected(notificationSelected);
        holder.image1.setSelected(notificationSelected);
        holder.name2.setSelected(marksSelected);
        holder.image2.setSelected(marksSelected);
        holder.name3.setSelected(guestsSelected);
        holder.image3.setSelected(guestsSelected);
        ViewUtil.gone(holder.counter0);
        ViewUtil.setVisibility(holder.counter1, this.mCounter1 > 0);
        ViewUtil.setVisibility(holder.counter2, this.mCounter2 > 0);
        ViewUtil.setVisibility(holder.counter3, this.mCounter3 > 0);
        holder.counter1.setText(MenuItem.getCounterText(this.mCounter1, BubbleState.green_tablet));
        holder.counter2.setText(MenuItem.getCounterText(this.mCounter2, BubbleState.green_tablet));
        holder.counter3.setText(MenuItem.getCounterText(this.mCounter3, BubbleState.green_tablet));
        holder.name0.setText(LocalizationManager.getString(OdnoklassnikiApplication.getContext(), this.mName0));
        holder.name1.setText(LocalizationManager.getString(OdnoklassnikiApplication.getContext(), this.mName1));
        holder.name2.setText(LocalizationManager.getString(OdnoklassnikiApplication.getContext(), this.mName2));
        holder.name3.setText(LocalizationManager.getString(OdnoklassnikiApplication.getContext(), this.mName3));
        holder.grid_layout.setVisibility(this.isShowGrid ? 0 : 4);
        return view;
    }

    public Holder createViewHolder(int type, int position) {
        return new Holder(type, position);
    }

    public void onSlidingMenuChangedOpeningRatio(float openingRatio) {
        this.openingRatio = openingRatio;
        if (this.weakReferenceSearchName != null) {
            View searchName = (View) this.weakReferenceSearchName.get();
            if (searchName != null) {
                searchName.setAlpha(openingRatio > 0.5f ? 2.0f * (openingRatio - 0.5f) : 0.0f);
            }
        }
        if (this.weakReferenceSearchIcon != null) {
            View searchIcon = (View) this.weakReferenceSearchIcon.get();
            if (searchIcon != null) {
                searchIcon.setTranslationX((1.0f - openingRatio) * ((float) this.searchClosedTranslationX));
                searchIcon.setTranslationY((1.0f - openingRatio) * ((float) this.searchClosedTranslationY));
            }
        }
    }
}
