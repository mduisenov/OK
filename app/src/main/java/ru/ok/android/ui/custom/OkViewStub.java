package ru.ok.android.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import java.lang.ref.WeakReference;
import ru.ok.android.C0206R;
import ru.ok.android.utils.localization.LocalizationManager;

public class OkViewStub extends View {
    private Context context;
    private LocalizationManager localizationManager;
    private int mInflatedId;
    private WeakReference<View> mInflatedViewRef;
    private LayoutInflater mInflater;
    private int mLayoutResource;
    private boolean useInflatedLp;

    public OkViewStub(Context context) {
        this(context, null);
    }

    public OkViewStub(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OkViewStub(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OkViewStub(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        this.mLayoutResource = 0;
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.OkViewStub, 0, 0);
        this.mInflatedId = a.getResourceId(0, -1);
        this.mLayoutResource = a.getResourceId(2, 0);
        this.useInflatedLp = a.getBoolean(1, false);
        a.recycle();
        setVisibility(8);
        setWillNotDraw(true);
    }

    public int getInflatedId() {
        return this.mInflatedId;
    }

    public void setInflatedId(int inflatedId) {
        this.mInflatedId = inflatedId;
    }

    public int getLayoutResource() {
        return this.mLayoutResource;
    }

    public void setLayoutResource(int layoutResource) {
        this.mLayoutResource = layoutResource;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(0, 0);
    }

    public void draw(Canvas canvas) {
    }

    protected void dispatchDraw(Canvas canvas) {
    }

    public void setVisibility(int visibility) {
        if (this.mInflatedViewRef != null) {
            View view = (View) this.mInflatedViewRef.get();
            if (view != null) {
                view.setVisibility(visibility);
                return;
            }
            throw new IllegalStateException("setVisibility called on un-referenced view");
        }
        super.setVisibility(visibility);
        if (visibility == 0 || visibility == 4) {
            inflate();
        }
    }

    public void setLayoutInflater(LayoutInflater inflater) {
        this.mInflater = inflater;
    }

    public View inflate() {
        return inflate(this.useInflatedLp);
    }

    public View inflate(boolean useInflatedLp) {
        ViewParent viewParent = getParent();
        if (viewParent == null || !(viewParent instanceof ViewGroup)) {
            throw new IllegalStateException("OkViewStub must have a non-null ViewGroup viewParent");
        } else if (this.mLayoutResource != 0) {
            LayoutInflater factory;
            ViewGroup parent = (ViewGroup) viewParent;
            if (this.mInflater != null) {
                factory = this.mInflater;
            } else {
                factory = LayoutInflater.from(this.context);
            }
            View view = factory.inflate(this.mLayoutResource, parent, false);
            if (this.localizationManager == null) {
                this.localizationManager = LocalizationManager.from(this.context);
            }
            if (this.mInflatedId != -1) {
                view.setId(this.mInflatedId);
            }
            int index = parent.indexOfChild(this);
            parent.removeViewInLayout(this);
            LayoutParams layoutParams = getLayoutParams();
            if (useInflatedLp || layoutParams == null) {
                parent.addView(view, index);
            } else {
                parent.addView(view, index, layoutParams);
            }
            this.mInflatedViewRef = new WeakReference(view);
            this.localizationManager.registerView(view, this.mLayoutResource);
            return view;
        } else {
            throw new IllegalArgumentException("OkViewStub must have a valid layoutResource");
        }
    }
}
