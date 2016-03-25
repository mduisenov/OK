package ru.ok.android.ui.custom.profiles;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import ru.ok.android.C0206R;
import ru.ok.android.ui.custom.profiles.ProfilesButton.Mode;

public class ProfileButtonsLayout extends LinearLayout {
    private int maxBigViewsCount;

    public ProfileButtonsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.maxBigViewsCount = 1;
        this.maxBigViewsCount = context.obtainStyledAttributes(attrs, C0206R.styleable.ProfileButtonsLayout, 0, 0).getInt(0, 1);
        setOrientation(0);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = 0;
        int bigItemCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ProfilesButton) {
                ProfilesButton button = (ProfilesButton) child;
                if (button.getVisibility() == 0) {
                    LayoutParams params = (LayoutParams) button.getLayoutParams();
                    if (count >= this.maxBigViewsCount) {
                        button.setMode(Mode.Image);
                        params.weight = 0.0f;
                    } else {
                        button.setMode(Mode.TextAndImage);
                        params.weight = 1.0f;
                        bigItemCount++;
                    }
                    button.setLayoutParams(params);
                    count++;
                }
            }
        }
        setWeightSum((float) bigItemCount);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMaxBigViewsCount(int maxBigViewsCount) {
        this.maxBigViewsCount = maxBigViewsCount;
        setWeightSum((float) maxBigViewsCount);
    }
}
