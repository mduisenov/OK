package ru.ok.android.ui.measuredobserver;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import java.util.Observer;
import ru.ok.android.ui.measuredobserver.MeasureObservable.MeasureObservableHelper;

public class MeasureObservableRelativeLayout extends RelativeLayout implements MeasureObservable {
    private final MeasureObservableHelper helper;

    public MeasureObservableRelativeLayout(Context context) {
        super(context);
        this.helper = new MeasureObservableHelper();
    }

    public MeasureObservableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.helper = new MeasureObservableHelper();
    }

    public MeasureObservableRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.helper = new MeasureObservableHelper();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.helper.onMeasure(this);
    }

    public void addMeasureObserver(Observer observer) {
        this.helper.addObserver(observer);
    }
}
