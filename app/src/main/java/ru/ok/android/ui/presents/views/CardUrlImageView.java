package ru.ok.android.ui.presents.views;

import android.content.Context;
import android.util.AttributeSet;
import ru.ok.android.ui.custom.AspectRatioMeasureHelper;
import ru.ok.android.ui.custom.CompositePresentView;

public class CardUrlImageView extends CompositePresentView {
    private final AspectRatioMeasureHelper aspectRatioHelper;

    public CardUrlImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.aspectRatioHelper = new AspectRatioMeasureHelper(this, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.aspectRatioHelper.updateSpecs(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(this.aspectRatioHelper.getWidthSpec(), this.aspectRatioHelper.getHeightSpec());
    }
}
