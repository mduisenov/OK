package ru.ok.android.ui.custom.loadmore;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Observable;
import java.util.Observer;
import ru.ok.android.C0206R;
import ru.ok.android.utils.localization.LocalizationManager;

public class LoadMoreView extends LinearLayout implements Observer {
    public static int LIST_ORIENTATION_HORIZONTAL;
    public static int LIST_ORIENTATION_VERTICAL;
    private boolean isAttachedToWindow;
    private boolean isListHorizontal;
    private final TextView message;
    private LoadMoreViewData observedData;
    private final ProgressBar progress;

    public enum LoadMoreState {
        LOADING(2131166047, 0),
        IDLE(2131166047, 8),
        WAITING(2131166047, 0),
        LOAD_POSSIBLE(2131166044, 8),
        LOAD_POSSIBLE_NO_LABEL(0, 8),
        LOAD_IMPOSSIBLE(2131166045, 8),
        DISABLED(0, 8),
        DISCONNECTED(2131166271, 8);
        
        private final int _progressVisibility;
        private final int _textResId;

        private LoadMoreState(int textResId, int progressVisibility) {
            this._textResId = textResId;
            this._progressVisibility = progressVisibility;
        }

        public int getTextResId() {
            return this._textResId;
        }

        public int getProgressVisibility() {
            return this._progressVisibility;
        }

        public static boolean isLoadPossibleState(LoadMoreState state) {
            return state == LOAD_POSSIBLE || state == LOAD_POSSIBLE_NO_LABEL;
        }
    }

    static {
        LIST_ORIENTATION_VERTICAL = 0;
        LIST_ORIENTATION_HORIZONTAL = 1;
    }

    public LoadMoreView(Context context, AttributeSet attrs, int defLayoutId) {
        boolean z = false;
        super(context);
        this.isAttachedToWindow = false;
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.LoadMoreView);
        int layoutId = a.getResourceId(1, defLayoutId);
        int listOrientation = a.getInt(0, LIST_ORIENTATION_VERTICAL);
        a.recycle();
        LayoutInflater.from(context).inflate(layoutId, this, true);
        if (listOrientation == LIST_ORIENTATION_HORIZONTAL) {
            z = true;
        }
        this.isListHorizontal = z;
        setLayoutParams(this.isListHorizontal ? new LayoutParams(-2, -1) : new LayoutParams(-1, -2));
        setOrientation(1);
        setGravity(17);
        this.progress = (ProgressBar) findViewById(2131624548);
        this.message = (TextView) findViewById(2131624538);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 2130903274);
    }

    public LoadMoreView(Context context) {
        this(context, null, 2130903274);
    }

    public void bind(LoadMoreViewData data) {
        updateVisibleState(data);
        if (data != this.observedData) {
            if (this.observedData != null) {
                this.observedData.deleteObserver(this);
            }
            if (data != null && this.isAttachedToWindow) {
                data.addObserver(this);
            }
            this.observedData = data;
        }
    }

    public void update(Observable observable, Object data) {
        updateVisibleState((LoadMoreViewData) observable);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.isAttachedToWindow = true;
        if (this.observedData != null) {
            this.observedData.addObserver(this);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isAttachedToWindow = false;
        if (this.observedData != null) {
            this.observedData.deleteObserver(this);
        }
    }

    private void updateVisibleState(LoadMoreViewData data) {
        if (data.currentState != LoadMoreState.IDLE) {
            updateVisibleStateWithState(data, data.currentState);
        } else {
            updateVisibleStateWithState(data, data.permanentState);
        }
    }

    private void updateVisibleStateWithState(LoadMoreViewData data, LoadMoreState state) {
        this.progress.setVisibility(state.getProgressVisibility());
        Integer textResourceId = (Integer) data.customStates.get(state);
        if (textResourceId == null || textResourceId.intValue() == 0) {
            textResourceId = Integer.valueOf(state.getTextResId());
        }
        if (this.message == null) {
            return;
        }
        if (textResourceId.intValue() != 0) {
            this.message.setText(LocalizationManager.getString(getContext(), textResourceId.intValue()));
        } else {
            this.message.setText(null);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.isListHorizontal && getParent() != null && (getParent() instanceof View)) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(((View) getParent()).getHeight(), 1073741824);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
