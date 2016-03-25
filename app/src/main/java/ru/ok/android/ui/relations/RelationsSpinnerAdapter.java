package ru.ok.android.ui.relations;

import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup.MarginLayoutParams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.adapters.spinner.BaseNavigationSpinnerAdapter;
import ru.ok.android.ui.custom.indicator.PagerSlidingTabStrip;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.ViewPagerDisable;
import ru.ok.java.api.request.relatives.RelativesType;

public class RelationsSpinnerAdapter extends BaseNavigationSpinnerAdapter implements OnNavigationListener {
    private static List<RelativesType> RELATIVES;
    private static int[] TEXT_RES;
    private final BaseCompatToolbarActivity activity;
    private final List<RelationsSpinnerContainer> data;
    private PagerSlidingTabStrip indicator;
    private boolean isSearchMod;
    private RecyclerView mFriendsList;
    private RelationsAdapter mRelationsAdapter;
    private ViewPagerDisable viewPager;

    public static class RelationsSpinnerContainer {
        public int count;
        public RelativesType relativesType;

        public RelationsSpinnerContainer(RelativesType relativesType, int count) {
            this.relativesType = relativesType;
            this.count = count;
        }
    }

    static {
        RELATIVES = Arrays.asList(new RelativesType[]{RelativesType.ALL, RelativesType.LOVE, RelativesType.COLLEGUE, RelativesType.CLOSEFRIEND, RelativesType.CLASSMATE, RelativesType.CURSEMATE, RelativesType.COMPANIONINARMS, RelativesType.RELATIVE});
        TEXT_RES = new int[]{2131166446, 2131166452, 2131166449, 2131166448, 2131166447, 2131166451, 2131166450, 2131166453};
    }

    public RelationsSpinnerAdapter(BaseCompatToolbarActivity activity, RelationsAdapter relationsAdapter) {
        super(activity);
        this.isSearchMod = false;
        this.data = new ArrayList();
        this.activity = activity;
        this.mRelationsAdapter = relationsAdapter;
    }

    public void setPageViews(PagerSlidingTabStrip indicator, ViewPagerDisable viewPager) {
        this.indicator = indicator;
        this.viewPager = viewPager;
    }

    public void setFriendsList(RecyclerView friendsList) {
        this.mFriendsList = friendsList;
    }

    public void setData(Set<RelationsSpinnerContainer> types) {
        this.data.clear();
        for (RelativesType type : RELATIVES) {
            RelationsSpinnerContainer container = containsType(types, type);
            if (container != null) {
                this.data.add(container);
            }
        }
    }

    public List<RelationsSpinnerContainer> getData() {
        return this.data;
    }

    private RelationsSpinnerContainer containsType(Set<RelationsSpinnerContainer> types, RelativesType type) {
        for (RelationsSpinnerContainer container : types) {
            if (container.relativesType == type) {
                return container;
            }
        }
        return null;
    }

    protected String getItemText(int position) {
        return getItem(position);
    }

    protected String getCountText(int position) {
        return (((RelationsSpinnerContainer) this.data.get(position)).count != 0 || ((RelationsSpinnerContainer) this.data.get(position)).relativesType == RelativesType.ONLINE) ? Integer.toString(((RelationsSpinnerContainer) this.data.get(position)).count) : "";
    }

    public int getCount() {
        return this.data.size();
    }

    public String getItem(int i) {
        return LocalizationManager.getString(this.activity, TEXT_RES[RELATIVES.indexOf(((RelationsSpinnerContainer) this.data.get(i)).relativesType)]);
    }

    public long getItemId(int i) {
        return (long) RELATIVES.indexOf(((RelationsSpinnerContainer) this.data.get(i)).relativesType);
    }

    public void updateTabs(boolean isSearchMod) {
        if (this.indicator != null && this.viewPager != null) {
            if (this.mRelationsAdapter.getRelativesType() != RelativesType.ALL || isSearchMod) {
                this.indicator.setVisibility(8);
                ((MarginLayoutParams) this.viewPager.getLayoutParams()).topMargin = 0;
                this.viewPager.setCurrentItem(0, true);
                this.viewPager.setEnableScroll(false);
                return;
            }
            this.indicator.setVisibility(0);
            ((MarginLayoutParams) this.viewPager.getLayoutParams()).topMargin = this.activity.getResources().getDimensionPixelSize(2131231116);
            this.viewPager.setEnableScroll(true);
        }
    }

    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (this.mRelationsAdapter == null) {
            return false;
        }
        this.mRelationsAdapter.setRelativesType(((RelationsSpinnerContainer) this.data.get(itemPosition)).relativesType);
        if (this.mFriendsList != null) {
            this.mFriendsList.smoothScrollToPosition(0);
        }
        if (this.activity.isUseTabbar()) {
            this.activity.showTabbar(true);
        }
        if (this.activity instanceof OdklSlidingMenuFragmentActivity) {
            ((OdklSlidingMenuFragmentActivity) this.activity).closeMenu();
        }
        updateTabs(this.isSearchMod);
        return true;
    }
}
