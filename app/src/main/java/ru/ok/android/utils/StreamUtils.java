package ru.ok.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.InsetDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import ru.ok.android.slidingmenu.SlidingMenuStrategy;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.StreamLayoutConfig;
import ru.ok.android.ui.users.fragments.OnlineFriendsStreamFragment;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;

public class StreamUtils {
    public static View applyExtraMarginsToLandscapeImagePaddings(ViewHolder holder, StreamLayoutConfig layoutConfig) {
        View view = holder.itemView;
        Context context = view.getContext();
        int extraMargin = layoutConfig.getExtraMarginForLandscapeAsInPortrait(true);
        int extraMarginForWidth = layoutConfig.getExtraMarginForLandscapeAsInPortrait(false);
        int leftPadding = holder.originalLeftPadding + extraMargin;
        int rightPadding = (((holder.originalRightPadding + layoutConfig.listViewWidth) - layoutConfig.listViewPortraitWidth) - extraMarginForWidth) + (extraMarginForWidth - extraMargin);
        int topPadding = view.getPaddingTop();
        int bottomPadding = view.getPaddingBottom();
        if (layoutConfig.screenOrientation == 2) {
            int innerPadding = context.getResources().getDimensionPixelOffset(2131230965);
            leftPadding += innerPadding;
            rightPadding += innerPadding;
        }
        view.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
        if (view instanceof FrameLayout) {
            FrameLayout frameLayout = (FrameLayout) view;
            Resources r = frameLayout.getResources();
            int highlightMargin = r.getDimensionPixelOffset(2131230963) + extraMargin;
            frameLayout.setForeground(new InsetDrawable(r.getDrawable(2130838722), highlightMargin, topPadding, highlightMargin, bottomPadding));
        }
        return view;
    }

    public static void updateLayoutConfig(StreamLayoutConfig layoutConfig, int listViewWidth, int currentScreenOrientation, Activity activity, FragmentManager fm) {
        boolean z;
        boolean z2 = true;
        layoutConfig.listViewWidth = listViewWidth;
        if (DeviceUtils.getType(activity) != DeviceLayoutType.SMALL) {
            z = true;
        } else {
            z = false;
        }
        layoutConfig.isTablet = z;
        layoutConfig.screenOrientation = currentScreenOrientation;
        Point displaySize = new Point();
        if (DeviceUtils.getScreenSize(activity, displaySize)) {
            layoutConfig.listViewPortraitWidth = SlidingMenuStrategy.getContentWidth(activity.getResources(), 1, SlidingMenuStrategy.getStrategyType(1), currentScreenOrientation == 1 ? displaySize.x : displaySize.y);
            if (fm != null) {
                Fragment fragment = fm.findFragmentByTag("online_friends_stream");
                if (fragment instanceof OnlineFriendsStreamFragment) {
                    OnlineFriendsStreamFragment onlineFriends = (OnlineFriendsStreamFragment) fragment;
                    layoutConfig.collapsedOnlineFriendsWidth = onlineFriends.getCollapsedWidth();
                    layoutConfig.expandedOnlineFriendsWidth = onlineFriends.getExpandedWidth();
                    layoutConfig.hasOnlineFriends = true;
                    if (onlineFriends.isCollapsed()) {
                        z2 = false;
                    }
                    layoutConfig.isOnlineFriendsExpanded = z2;
                    return;
                }
                layoutConfig.hasOnlineFriends = false;
            }
        }
    }
}
