package ru.ok.android.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import ru.mail.libverify.C0176R;

public class DimenUtils {
    public static final int getRealDisplayPixels(int dip, Context context) {
        return getRealDisplayPixels(dip, context.getResources());
    }

    public static final int getRealDisplayPixels(int dip, Resources resources) {
        if (dip <= 0) {
            return 0;
        }
        return Math.max(1, (int) (((float) dip) * resources.getDisplayMetrics().density));
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getNavBarWidth(Context context) {
        return getNavBarDimen(context, "navigation_bar_width");
    }

    public static int getNavBarHeight(Context context) {
        return getNavBarDimen(context, "navigation_bar_height");
    }

    private static int getNavBarDimen(Context context, String resourceString) {
        Resources r = context.getResources();
        int id = r.getIdentifier(resourceString, "dimen", "android");
        if (id > 0) {
            return r.getDimensionPixelSize(id);
        }
        return 0;
    }

    public static int getNavBarBottomSize(View attachedView) {
        Context context = attachedView.getContext();
        if (!DeviceUtils.hasNavigationBar(context)) {
            return 0;
        }
        View navBarView = findNavigationBarView(attachedView);
        if (navBarView != null) {
            return navBarView.getMeasuredHeight();
        }
        int rotation = ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRotation();
        return (rotation == 1 || rotation == 3) ? getNavBarWidth(context) : getNavBarHeight(context);
    }

    public static View findNavigationBarView(View attachedView) {
        int navBarId = attachedView.getContext().getResources().getIdentifier("navigationBarBackground", "id", "android");
        if (navBarId != 0) {
            View root = attachedView.getRootView();
            if (root instanceof ViewGroup) {
                ViewGroup rootViewGroup = (ViewGroup) root;
                for (int i = rootViewGroup.getChildCount() - 1; i >= 0; i--) {
                    View view = rootViewGroup.getChildAt(i);
                    if (view.getId() == navBarId) {
                        return view;
                    }
                }
            }
        }
        return null;
    }

    public static int getToolbarHeight(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(C0176R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return 0;
    }
}
