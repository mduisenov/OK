package ru.ok.android.ui.mediatopics;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.utils.FabHelper;

public abstract class MediaTopicsTabFragmentWithComposer extends MediaTopicsTabFragment {
    protected FloatingActionButton fab;

    protected abstract void initFab(FloatingActionButton floatingActionButton);

    protected abstract boolean isMediaPostPanelRequired();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void updateMediaPostPanel(View view) {
        updateMediaPostPanel(view, false);
    }

    protected void updateMediaPostPanel(View view, boolean orientationChanged) {
        if (isMediaPostPanelRequired()) {
            if (this.fab == null) {
                this.fab = FabHelper.createTopicsFab(view.getContext(), ((BaseCompatToolbarActivity) getActivity()).getCoordinatorManager().coordinatorLayout);
                initFab(this.fab);
                getCoordinatorManager().ensureFab(this.fab, "fab_stream");
            }
        } else if (this.fab != null) {
            this.fab.setVisibility(8);
        }
    }

    protected void selectFilterAllPage() {
        if (this.viewPager.getCurrentItem() != 0) {
            this.viewPager.setCurrentItem(0);
        }
    }
}
