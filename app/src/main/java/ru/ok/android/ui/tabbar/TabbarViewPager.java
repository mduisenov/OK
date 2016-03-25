package ru.ok.android.ui.tabbar;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import com.noundla.centerviewpagersample.comps.StreamCenterLockViewPager;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.custom.photo.StableViewPager;
import ru.ok.android.ui.tabbar.manager.BaseTabbarManager;

public class TabbarViewPager extends StableViewPager {
    private OnPageChangeListener onPageChangeListener;
    private final OnPageChangeListener onPageChangeListenerLocal;

    /* renamed from: ru.ok.android.ui.tabbar.TabbarViewPager.1 */
    class C12841 implements OnPageChangeListener {
        C12841() {
        }

        public void onPageSelected(int i) {
            if (TabbarViewPager.this.onPageChangeListener != null) {
                TabbarViewPager.this.onPageChangeListener.onPageSelected(i);
            }
            if (BaseCompatToolbarActivity.isUseTabbar(TabbarViewPager.this.getContext())) {
                ((BaseTabbarManager) TabbarViewPager.this.getContext()).showTabbar(true);
            }
        }

        public void onPageScrollStateChanged(int i) {
            if (TabbarViewPager.this.onPageChangeListener != null) {
                TabbarViewPager.this.onPageChangeListener.onPageScrollStateChanged(i);
            }
        }

        public void onPageScrolled(int i, float v, int i2) {
            if (TabbarViewPager.this.onPageChangeListener != null) {
                TabbarViewPager.this.onPageChangeListener.onPageScrolled(i, v, i2);
            }
        }
    }

    public TabbarViewPager(Context context) {
        super(context);
        this.onPageChangeListener = null;
        this.onPageChangeListenerLocal = new C12841();
        super.setOnPageChangeListener(this.onPageChangeListenerLocal);
    }

    public TabbarViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.onPageChangeListener = null;
        this.onPageChangeListenerLocal = new C12841();
        super.setOnPageChangeListener(this.onPageChangeListenerLocal);
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.onPageChangeListener = listener;
        super.setOnPageChangeListener(this.onPageChangeListenerLocal);
    }

    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v == this || (!(v instanceof StreamCenterLockViewPager) && !(v instanceof ViewPager))) {
            return super.canScroll(v, checkV, dx, x, y);
        }
        return true;
    }
}
