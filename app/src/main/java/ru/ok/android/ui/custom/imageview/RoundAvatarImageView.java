package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.util.AttributeSet;

public class RoundAvatarImageView extends ImageRoundPressedView {
    public RoundAvatarImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAvatarMaleImage() {
        setImageResource(2130838321);
    }

    public void setAvatarFemaleImage() {
        setImageResource(2130837927);
    }
}
