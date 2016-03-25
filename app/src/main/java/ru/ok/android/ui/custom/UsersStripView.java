package ru.ok.android.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.ImageRoundView;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.utils.URLUtil;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public final class UsersStripView extends ViewGroup {
    private static final Comparator<GeneralUserInfo> AVATAR_SORTER;
    private final TextView count;
    private HandleBlocker handleBlocker;
    private final int height;
    private int maxViews;
    private final int padding;
    private int totalUsers;
    private List<? extends GeneralUserInfo> users;

    /* renamed from: ru.ok.android.ui.custom.UsersStripView.1 */
    static class C06291 implements Comparator<GeneralUserInfo> {
        C06291() {
        }

        public int compare(GeneralUserInfo a, GeneralUserInfo b) {
            boolean aEmpty;
            boolean bEmpty;
            if (URLUtil.isStubUrl(a.getPicUrl())) {
                aEmpty = false;
            } else {
                aEmpty = true;
            }
            if (URLUtil.isStubUrl(b.getPicUrl())) {
                bEmpty = false;
            } else {
                bEmpty = true;
            }
            if (!aEmpty || bEmpty) {
                return (aEmpty || !bEmpty) ? 0 : -1;
            } else {
                return 1;
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.UsersStripView.2 */
    class C06302 implements OnPreDrawListener {
        C06302() {
        }

        public boolean onPreDraw() {
            UsersStripView.this.getViewTreeObserver().removeOnPreDrawListener(this);
            UsersStripView.this.reconfigureUsers();
            return false;
        }
    }

    static {
        AVATAR_SORTER = new C06291();
    }

    public UsersStripView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.height = (int) TypedValue.applyDimension(1, 32.0f, context.getResources().getDisplayMetrics());
        this.padding = (int) TypedValue.applyDimension(1, 1.5f, context.getResources().getDisplayMetrics());
        this.count = new TextView(context);
        this.count.setBackgroundResource(2130837832);
        this.count.setGravity(17);
        this.count.setTextColor(-2236963);
        this.count.setTextSize(14.0f);
        addView(this.count);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int x = getPaddingLeft();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int y = getPaddingTop();
            view.layout(x, y, view.getMeasuredWidth() + x, view.getMeasuredHeight() + y);
            if (i < getChildCount() - 2) {
                x += view.getMeasuredWidth() + this.padding;
            }
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.maxViews = MeasureSpec.getSize(widthMeasureSpec) / (this.height + this.padding);
        if (this.maxViews > 5) {
            this.maxViews = 5;
        }
        setMeasuredDimension(((this.maxViews * (this.height + this.padding)) + getPaddingLeft()) + getPaddingRight(), (this.height + getPaddingTop()) + getPaddingBottom());
        int spec = MeasureSpec.makeMeasureSpec(this.height, 1073741824);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(spec, spec);
        }
    }

    public void setUsers(List<? extends GeneralUserInfo> users, int totalUsers) {
        if (users != null) {
            Collections.sort(users, AVATAR_SORTER);
            if (users.size() > 5) {
                users = users.subList(0, 5);
            }
            this.users = new ArrayList(users);
            this.totalUsers = totalUsers;
        }
        if (getWidth() <= 0) {
            getViewTreeObserver().addOnPreDrawListener(new C06302());
            requestLayout();
            return;
        }
        reconfigureUsers();
    }

    private void reconfigureUsers() {
        bringChildToFront(this.count);
        int childIndex = 0;
        List<? extends GeneralUserInfo> users = this.users;
        if (users != null) {
            for (GeneralUserInfo user : users) {
                if (childIndex >= this.maxViews) {
                    break;
                }
                UrlImageView imageView;
                if (childIndex < getChildCount() - 1) {
                    imageView = (UrlImageView) getChildAt(childIndex);
                } else {
                    imageView = new ImageRoundView(getContext(), null);
                    imageView.setIsAlpha(true);
                    addView(imageView, getChildCount() - 1);
                }
                int stubId = 0;
                if (user.getObjectType() == 0) {
                    stubId = ((UserInfo) user).genderType == UserGenderType.MALE ? 2130838321 : 2130837927;
                } else if (user.getObjectType() == 1) {
                    stubId = 2130837663;
                }
                ImageViewManager.getInstance().displayImage(user.getPicUrl(), imageView, stubId, this.handleBlocker);
                childIndex++;
            }
        }
        int restCount = this.totalUsers - childIndex;
        while (childIndex < getChildCount() - 1) {
            removeViewAt(childIndex);
        }
        this.count.setVisibility(8);
        if (restCount > 0) {
            this.count.setText(restCount > 99 ? "99+" : "+" + restCount);
        }
    }

    public void setHandleBlocker(HandleBlocker handleBlocker) {
        this.handleBlocker = handleBlocker;
    }
}
