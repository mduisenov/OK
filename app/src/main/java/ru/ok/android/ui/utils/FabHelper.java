package ru.ok.android.ui.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import java.util.List;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.coordinator.behaviors.FabBottomBehavior;
import ru.ok.android.ui.coordinator.behaviors.MediaComposerBehavior;
import ru.ok.android.ui.mediatopic.view.MediaComposerPanel;
import ru.ok.android.ui.stream.view.StreamScrollTopView;
import ru.ok.android.utils.DeviceUtils;

public class FabHelper {
    public static MediaComposerPanel createMediaComposerPanel(Context context, CoordinatorLayout coordinatorLayout, View fragmentView) {
        MediaComposerPanel mediaComposerPanel = new MediaComposerPanel(context);
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        if (fragmentView == null || fragmentView.findViewById(2131625272) == null) {
            layoutParams.setAnchorId(2131624880);
            layoutParams.anchorGravity = 85;
            layoutParams.gravity = 51;
        } else {
            layoutParams.setAnchorId(2131625272);
            layoutParams.anchorGravity = 83;
            layoutParams.gravity = 51;
        }
        layoutParams.setBehavior(new MediaComposerBehavior(context));
        mediaComposerPanel.setLayoutParams(layoutParams);
        return mediaComposerPanel;
    }

    public static StreamScrollTopView createStreamScrollTop(Context context, CoordinatorLayout coordinatorLayout) {
        StreamScrollTopView streamScrollTopView = (StreamScrollTopView) LayoutInflater.from(context).inflate(2130903520, coordinatorLayout, false);
        updateScrollTopAnchoring(coordinatorLayout, streamScrollTopView);
        return streamScrollTopView;
    }

    public static FloatingActionButton createChatFab(Context context, CoordinatorLayout coordinatorLayout) {
        FloatingActionButton fab = createDefaultFab(context, coordinatorLayout, 2130838038);
        if (!DeviceUtils.isSmall(context)) {
            View leftContainer = coordinatorLayout.findViewById(2131625149);
            if (leftContainer != null && leftContainer.getVisibility() == 0) {
                LayoutParams layoutParams = (LayoutParams) fab.getLayoutParams();
                layoutParams.setAnchorId(2131625149);
                layoutParams.anchorGravity = 85;
                layoutParams.gravity = 51;
            }
        }
        return fab;
    }

    public static FloatingActionButton createCameraFab(Context context, CoordinatorLayout coordinatorLayout) {
        return createDefaultFab(context, coordinatorLayout, 2130838039);
    }

    public static FloatingActionButton createTopicsFab(Context context, CoordinatorLayout coordinatorLayout) {
        return createDefaultFab(context, coordinatorLayout, 2130838038);
    }

    public static FloatingActionButton createVideoFab(Context context, CoordinatorLayout coordinatorLayout) {
        return createDefaultFab(context, coordinatorLayout, 2130838040);
    }

    public static FloatingActionButton createDefaultFab(Context context, CoordinatorLayout coordinatorLayout) {
        FloatingActionButton fab = (FloatingActionButton) LayoutInflater.from(context).inflate(2130903181, coordinatorLayout, false).findViewById(2131624808);
        LayoutParams layoutParams = (LayoutParams) fab.getLayoutParams();
        layoutParams.gravity = 85;
        layoutParams.rightMargin = 0;
        layoutParams.bottomMargin = context.getResources().getDimensionPixelSize(2131230798);
        layoutParams.setBehavior(new FabBottomBehavior(context, null));
        fab.setTranslationX((float) (-context.getResources().getDimensionPixelSize(2131230799)));
        return fab;
    }

    public static FloatingActionButton createDefaultFab(Context context, CoordinatorLayout coordinatorLayout, int imageResId) {
        FloatingActionButton fab = createDefaultFab(context, coordinatorLayout);
        fab.setImageResource(imageResId);
        return fab;
    }

    public static void updateScrollTopAnchoring(CoordinatorLayout coordinatorLayout, View streamScrollTopView) {
        int i = C0263R.id.indicator;
        boolean hasIndicator = hasIndicator(coordinatorLayout);
        LayoutParams layoutParams = (LayoutParams) streamScrollTopView.getLayoutParams();
        View smallRightContainer = coordinatorLayout.findViewById(2131625152);
        if (coordinatorLayout.findViewById(2131625272) != null) {
            layoutParams.setAnchorId(2131625272);
            layoutParams.anchorGravity = 51;
            layoutParams.gravity = 83;
        } else if (smallRightContainer == null) {
            if (!hasIndicator) {
                i = 2131624640;
            }
            layoutParams.setAnchorId(i);
            layoutParams.anchorGravity = 85;
            layoutParams.gravity = 83;
        } else if (hasIndicator) {
            if (!hasIndicator) {
                i = 2131624640;
            }
            layoutParams.setAnchorId(i);
            layoutParams.anchorGravity = 85;
            layoutParams.gravity = 83;
        } else {
            layoutParams.setAnchorId(2131625152);
            layoutParams.anchorGravity = 51;
            layoutParams.gravity = 83;
        }
        Resources resources = coordinatorLayout.getContext().getResources();
        layoutParams.topMargin = 0;
        layoutParams.rightMargin = 0;
        streamScrollTopView.setTranslationY((float) resources.getDimensionPixelSize(2131231154));
        streamScrollTopView.setTranslationX((float) (-resources.getDimensionPixelSize(2131231153)));
    }

    private static boolean hasIndicator(CoordinatorLayout coordinatorLayout) {
        View indicator = coordinatorLayout.findViewById(C0263R.id.indicator);
        if (indicator == null || indicator.getVisibility() != 0) {
            return false;
        }
        List<Fragment> fragments = ((FragmentActivity) coordinatorLayout.getContext()).getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (!(fragment == null || fragment.isVisible())) {
                    View fragmentView = fragment.getView();
                    if (!(fragmentView == null || fragmentView.findViewById(C0263R.id.indicator) == null)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
