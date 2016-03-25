package ru.ok.android.ui.custom.radio;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.BaseSavedState;
import android.widget.Checkable;
import android.widget.ImageView;
import ru.ok.android.C0206R;

public class AppPollRadioView extends ImageView implements Checkable {
    private static final int[] CHECKED_STATE_SET;
    private boolean broadcasting;
    private boolean checked;
    private OnCheckedChangedListener onCheckedChangedListener;
    private String text;

    public interface OnCheckedChangedListener {
        void onCheckedChanged(View view, boolean z);
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR;
        boolean checked;
        String text;

        /* renamed from: ru.ok.android.ui.custom.radio.AppPollRadioView.SavedState.1 */
        static class C07501 implements Creator<SavedState> {
            C07501() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.checked = ((Boolean) in.readValue(null)).booleanValue();
            this.text = in.readString();
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(Boolean.valueOf(this.checked));
            out.writeString(this.text);
        }

        public String toString() {
            return getClass().getSimpleName() + ".SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " checked=" + this.checked + "}";
        }

        static {
            CREATOR = new C07501();
        }
    }

    static {
        CHECKED_STATE_SET = new int[]{16842912};
    }

    public AppPollRadioView(Context context) {
        super(context);
    }

    public AppPollRadioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, C0206R.styleable.AppPollRadioView);
        try {
            this.checked = ta.getBoolean(0, false);
            this.text = ta.getString(1);
        } finally {
            ta.recycle();
        }
    }

    public AppPollRadioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, C0206R.styleable.AppPollRadioView);
        try {
            this.checked = ta.getBoolean(0, false);
            this.text = ta.getString(1);
        } finally {
            ta.recycle();
        }
    }

    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            refreshDrawableState();
            if (!this.broadcasting) {
                this.broadcasting = true;
                if (this.onCheckedChangedListener != null) {
                    this.onCheckedChangedListener.onCheckedChanged(this, checked);
                }
                this.broadcasting = false;
            }
        }
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void toggle() {
        if (!isChecked()) {
            setChecked(true);
        }
    }

    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    public void setOnCheckedChangeListener(OnCheckedChangedListener listener) {
        this.onCheckedChangedListener = listener;
    }

    public String getText() {
        return this.text;
    }

    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.checked = isChecked();
        ss.text = this.text;
        return ss;
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);
        this.text = ss.text;
        requestLayout();
    }
}
