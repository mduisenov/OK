package ru.ok.android.ui.custom.emptyview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import ru.mail.libverify.C0176R;
import ru.ok.android.C0206R;
import ru.ok.android.onelog.StubLog;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.localization.LocalizationManager;

public final class SmartEmptyViewAnimated extends ViewGroup implements OnClickListener {
    private final View background;
    private final int backgroundStrokeWidth;
    private final TextView button;
    private OnStubButtonClickListener buttonClickListener;
    private final int buttonInvisibleHeight;
    private final ImageView icon;
    private boolean portrait;
    private final View progress;
    private final Point screenSize;
    private State state;
    private final TextView subtitle;
    private final TextView title;
    private Type type;

    public interface OnStubButtonClickListener {
        void onStubButtonClick(Type type);
    }

    /* renamed from: ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.1 */
    class C06521 implements OnPreDrawListener {
        C06521() {
        }

        public boolean onPreDraw() {
            SmartEmptyViewAnimated.this.getViewTreeObserver().removeOnPreDrawListener(this);
            SmartEmptyViewAnimated.this.background.setScaleX(0.2f);
            SmartEmptyViewAnimated.this.background.setScaleY(0.2f);
            SmartEmptyViewAnimated.this.background.setAlpha(0.0f);
            SmartEmptyViewAnimated.this.background.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(500).start();
            SmartEmptyViewAnimated.this.title.setTranslationX((float) (SmartEmptyViewAnimated.this.getWidth() - SmartEmptyViewAnimated.this.title.getLeft()));
            SmartEmptyViewAnimated.this.title.animate().translationX(0.0f).setDuration(300).setStartDelay(100).start();
            SmartEmptyViewAnimated.this.subtitle.setTranslationX((float) (SmartEmptyViewAnimated.this.getWidth() - SmartEmptyViewAnimated.this.subtitle.getLeft()));
            SmartEmptyViewAnimated.this.subtitle.animate().translationX(0.0f).setDuration(300).setStartDelay(200).start();
            SmartEmptyViewAnimated.this.button.setTranslationX((float) (SmartEmptyViewAnimated.this.getWidth() - SmartEmptyViewAnimated.this.button.getLeft()));
            SmartEmptyViewAnimated.this.button.animate().translationX(0.0f).setDuration(300).setStartDelay(200).start();
            if (SmartEmptyViewAnimated.this.getVisibility() == 0) {
                StubLog.logStubShow(SmartEmptyViewAnimated.this.type);
            }
            return false;
        }
    }

    public enum State {
        LOADING,
        LOADED
    }

    public enum Type {
        FRIENDS_LIST(2130837868, 2131165765, 2131165745, 2131165738),
        FRIENDS_LIST_CONVERSATIONS(2130837868, 2131165765, 2131165745, 0),
        FRIENDS_LIST_NO_BUTTON(2130837868, 2131165765, 2131165745, 0),
        FRIENDS_LIST_USER(2130837868, 2131165765, 2131166792, 0),
        FRIENDS_LIST_MUSIC(2130837868, 2131165379, 2131165745, 0),
        FRIENDS_ONLINE(2130837869, 2131165766, 2131165746, 0),
        SEARCH(2130837889, 2131165776, 0, 0),
        SEARCH_GLOBAL(2130837890, 0, 2131166483, 0),
        CONVERSATIONS_LIST(2130837866, 2131165763, 2131165743, 0),
        GROUPS_LIST(2130837871, 2131165768, 0, 0),
        GROUP_TOPICS_LIST(2130837871, 2131165767, 0, 0),
        MY_TOPICS_LIST(2130837873, 2131166799, 2131165750, 0),
        USER_TOPICS_LIST(2130837873, 2131166799, 2131165758, 0),
        STREAM(2130837891, 2131165777, 2131165756, 0),
        STREAM_PROFILE(2130837891, 2131165777, 0, 0),
        NO_INTERNET(2130968597, 2131165772, 2131165752, 2131166437),
        ERROR(2130837867, 2131165764, 2131165744, 0),
        EMPTY(0, 0, 0, 0),
        RESTRICTED(2130837888, 2131165740, 0, 0),
        RESTRICTED_YOU_ARE_IN_BLACK_LIST(2130837888, 2131165740, 2131165760, 0),
        RESTRICTED_ACCESS_FOR_FRIENDS(2130837888, 2131165740, 2131165741, 0),
        USER_BLOCKED(2130837892, 2131165781, 0, 0),
        GROUP_BLOCKED(2130837870, 2131165739, 0, 0),
        NOTIFICATIONS(2130837884, 2131166724, 2131165753, 0),
        GUESTS(2130837872, 2131165969, 2131165747, 0),
        MUSIC_TUNERS(2130837874, 2131166738, 2131166281, 0),
        MUSIC(2130837874, 2131166223, 2131166276, 0),
        MUSIC_EXTENSION_TRACKS(2130837874, 2131165775, 2131165755, 0),
        MUSIC_HISTORY_TRACKS(2130837874, 2131165769, 2131165748, 0),
        MUSIC_MY_COLLECTIONS(2130837874, 2131165770, 2131165749, 0),
        MUSIC_USER_COLLECTIONS(2130837874, 2131165778, 2131165757, 0),
        MUSIC_MY_TRACKS(2130837874, 2131165771, 2131165751, 0),
        MUSIC_USER_TRACKS(2130837874, 2131165780, 2131165759, 0),
        PHOTO_LOAD_FAIL(2130837885, 2131165773, 2131165754, 2131166437),
        ALBUM_LOAD_FAIL(2130837885, 2131165761, 2131165742, 2131166437),
        PHOTOS(2130837886, 2131165774, 0, 0),
        ALBUMS(2130837886, 2131165762, 0, 0),
        FRIEND_PRESENTS(2130837887, 2131166599, 2131165734, 0),
        MY_SENT_PRESENTS(2130837887, 2131166599, 2131165736, 0),
        MY_RECEIVED_PRESENTS(2130837887, 2131166599, 2131165735, 0);
        
        final int buttonTitleResourceId;
        final int drawableResourceId;
        final int subTitleResourceId;
        final int titleResourceId;

        private Type(int drawableResourceId, int titleResourceId, int subTitleResourceId, int buttonTitleResourceId) {
            this.drawableResourceId = drawableResourceId;
            this.titleResourceId = titleResourceId;
            this.subTitleResourceId = subTitleResourceId;
            this.buttonTitleResourceId = buttonTitleResourceId;
        }
    }

    public SmartEmptyViewAnimated(Context context, AttributeSet attrs) {
        int color;
        super(context, attrs);
        this.type = Type.EMPTY;
        this.state = State.LOADING;
        this.screenSize = new Point();
        LayoutInflater.from(context).inflate(2130903177, this, true);
        this.progress = findViewById(2131624548);
        this.icon = (ImageView) findViewById(C0176R.id.icon);
        this.background = findViewById(2131624802);
        this.title = (TextView) findViewById(C0176R.id.title);
        this.subtitle = (TextView) findViewById(C0158R.id.subtitle);
        this.button = (TextView) findViewById(2131624679);
        this.buttonInvisibleHeight = getContext().getResources().getDimensionPixelSize(2131230958);
        TypedArray arr = context.obtainStyledAttributes(attrs, C0206R.styleable.SmartEmptyViewAnimated);
        setTextColor(this.title, arr, 0);
        setTextColor(this.subtitle, arr, 1);
        setTextColor(this.button, arr, 2);
        this.backgroundStrokeWidth = arr.getDimensionPixelSize(3, getResources().getDimensionPixelSize(2131230957));
        if (VERSION.SDK_INT < 23) {
            color = getResources().getColor(2131493208);
        } else {
            color = getResources().getColor(2131493208, null);
        }
        ((GradientDrawable) this.background.getBackground()).setStroke(this.backgroundStrokeWidth, color);
        arr.recycle();
        this.icon.setOnClickListener(this);
        updateForCurrentState();
    }

    private void setTextColor(TextView view, TypedArray arr, int id) {
        if (arr.hasValue(id)) {
            view.setTextColor(arr.getColor(id, 0));
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean z = !DeviceUtils.isSmall(getContext()) || DeviceUtils.getScreenOrientation((Activity) getContext()) == 1;
        this.portrait = z;
        this.subtitle.setGravity(this.portrait ? 1 : GravityCompat.START);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int measuredWidth;
        int measuredHeight;
        if (this.progress.getVisibility() == 0) {
            measureChild(this.progress, widthMeasureSpec, heightMeasureSpec);
            if (widthMode == Integer.MIN_VALUE) {
                measuredWidth = this.progress.getMeasuredWidth();
            } else {
                measuredWidth = width;
            }
            if (heightMode == Integer.MIN_VALUE) {
                measuredHeight = this.progress.getMeasuredHeight();
            } else {
                measuredHeight = height;
            }
            setMeasuredDimension(measuredWidth, measuredHeight);
            return;
        }
        measureTexts(widthMeasureSpec, heightMeasureSpec);
        int titleHeight = viewVisibleHeight(this.title);
        int subTitleHeight = viewVisibleHeight(this.subtitle);
        int buttonHeight = getButtonHeight();
        int titleWidth;
        int subTitleWidth;
        int buttonWidth;
        int iconSize;
        if (this.portrait) {
            titleWidth = viewVisibleWidth(this.title);
            subTitleWidth = viewVisibleWidth(this.subtitle);
            buttonWidth = viewVisibleWidth(this.button);
            measureIcon(width, ((height - titleHeight) - subTitleHeight) - buttonHeight);
            iconSize = viewVisibleHeight(this.icon);
            measuredWidth = Math.max(Math.max(Math.max(titleWidth, subTitleWidth), buttonWidth), iconSize);
            measuredHeight = ((iconSize + titleHeight) + subTitleHeight) + buttonHeight;
        } else {
            DeviceUtils.getScreenSize((Activity) getContext(), this.screenSize);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(this.screenSize.y, LinearLayoutManager.INVALID_OFFSET);
            if ((titleHeight + subTitleHeight) + buttonHeight > height) {
                ((MarginLayoutParams) this.button.getLayoutParams()).topMargin = 0;
            }
            measureTexts(widthMeasureSpec, heightMeasureSpec);
            titleWidth = viewVisibleWidth(this.title);
            subTitleWidth = viewVisibleWidth(this.subtitle);
            buttonWidth = viewVisibleWidth(this.button);
            measureIcon(width - Math.max(titleWidth, Math.max(subTitleWidth, buttonWidth)), height);
            iconSize = viewVisibleHeight(this.icon);
            measuredWidth = Math.max(Math.max(titleWidth, subTitleWidth), buttonWidth) + iconSize;
            measuredHeight = Math.max((titleHeight + subTitleHeight) + buttonHeight, iconSize);
        }
        measuredWidth += getPaddingLeft() + getPaddingRight();
        measuredHeight += getPaddingTop() + getPaddingBottom();
        if (widthMode != 0) {
            measuredWidth = Math.max(width, measuredWidth);
        }
        if (heightMode != 0) {
            measuredHeight = Math.max(height, measuredHeight);
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private void measureTexts(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildWithMargins(this.title, widthMeasureSpec, 0, heightMeasureSpec, 0);
        measureChildWithMargins(this.subtitle, widthMeasureSpec, 0, heightMeasureSpec, 0);
        if (this.button.getVisibility() == 0) {
            measureChildWithMargins(this.button, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
    }

    private void measureIcon(int widthForIcon, int heightForIcon) {
        int iconHeight;
        int iconWidth;
        int xPaddings = getPaddingLeft() + getPaddingRight();
        int yPaddings = getPaddingTop() + getPaddingBottom();
        int maxIconSize = (int) (((float) getContext().getResources().getDimensionPixelSize(2131230959)) / 0.765f);
        widthForIcon = Math.min(widthForIcon, maxIconSize + xPaddings);
        heightForIcon = Math.min(heightForIcon, maxIconSize + yPaddings);
        if (widthForIcon - xPaddings > heightForIcon - yPaddings) {
            iconHeight = heightForIcon;
            iconWidth = (heightForIcon - yPaddings) + xPaddings;
        } else {
            iconHeight = (widthForIcon - xPaddings) + yPaddings;
            iconWidth = widthForIcon;
        }
        measureChild(this.icon, MeasureSpec.makeMeasureSpec(iconWidth, 1073741824), MeasureSpec.makeMeasureSpec(iconHeight, 1073741824));
    }

    private static int viewVisibleHeight(View view) {
        if (view.getVisibility() == 8) {
            return 0;
        }
        MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams();
        return (lp.topMargin + lp.bottomMargin) + view.getMeasuredHeight();
    }

    private static int viewVisibleWidth(View view) {
        if (view.getVisibility() == 8) {
            return 0;
        }
        MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams();
        return (lp.leftMargin + lp.rightMargin) + view.getMeasuredWidth();
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        if (this.progress.getVisibility() == 0) {
            int leftHeight = (height - getPaddingTop()) - getPaddingBottom();
            int progressX = getPaddingLeft() + ((((width - getPaddingLeft()) - getPaddingRight()) - this.progress.getMeasuredWidth()) / 2);
            int progressY = getPaddingTop() + ((leftHeight - this.progress.getMeasuredHeight()) / 2);
            this.progress.layout(progressX, progressY, this.progress.getMeasuredWidth() + progressX, this.progress.getMeasuredHeight() + progressY);
            return;
        }
        int titleHeight = viewVisibleHeight(this.title);
        int subtitleHeight = viewVisibleHeight(this.subtitle);
        int buttonHeight = getButtonHeight();
        int iconSize = viewVisibleHeight(this.icon);
        int y;
        int x;
        if (this.portrait) {
            y = centeredTop(height, ((iconSize + titleHeight) + subtitleHeight) + buttonHeight);
            x = centeredLeft(width, iconSize);
            this.icon.layout(x, y, x + iconSize, y + iconSize);
            y += iconSize;
            y = layoutTextPortrait(this.title, width, y);
            y = layoutTextPortrait(this.subtitle, width, y);
            layoutTextPortrait(this.button, width, y);
        } else {
            int titleWidth = viewVisibleWidth(this.title);
            int subtitleWidth = viewVisibleWidth(this.subtitle);
            int buttonWidth = viewVisibleWidth(this.button);
            int contentWidth = Math.max(Math.max(titleWidth, subtitleWidth), buttonWidth) + iconSize;
            y = centeredTop(height, iconSize);
            x = centeredLeft(width, contentWidth);
            this.icon.layout(x, y, x + iconSize, y + iconSize);
            x += iconSize;
            y = centeredTop(height, (titleHeight + subtitleHeight) + buttonHeight);
            y = layoutTextLandscape(this.title, this.button.getPaddingLeft() + x, y);
            y = layoutTextLandscape(this.subtitle, this.button.getPaddingLeft() + x, y);
            layoutTextLandscape(this.button, x, y);
        }
        int size2 = ((int) ((0.765f * ((float) iconSize)) / 2.0f)) + this.backgroundStrokeWidth;
        int centerX = (this.icon.getLeft() + this.icon.getRight()) / 2;
        int centerY = (this.icon.getTop() + this.icon.getBottom()) / 2;
        this.background.layout(centerX - size2, centerY - size2, centerX + size2, centerY + size2);
    }

    private int getButtonHeight() {
        if (this.button.getVisibility() == 4) {
            return this.portrait ? this.buttonInvisibleHeight : 0;
        } else {
            return viewVisibleHeight(this.button);
        }
    }

    private int centeredTop(int height, int contentHeight) {
        return getPaddingTop() + ((((height - getPaddingTop()) - getPaddingBottom()) - contentHeight) / 2);
    }

    private int centeredLeft(int width, int contentWidth) {
        return getPaddingLeft() + ((((width - getPaddingLeft()) - getPaddingRight()) - contentWidth) / 2);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    private int layoutTextPortrait(View view, int width, int y) {
        return view.getVisibility() != 0 ? y : layoutText(view, centeredLeft(width, viewVisibleWidth(view)), y);
    }

    private int layoutTextLandscape(View view, int x, int y) {
        return view.getVisibility() != 0 ? y : layoutText(view, x, y);
    }

    private int layoutText(View view, int x, int y) {
        MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams();
        y += lp.topMargin;
        x += lp.leftMargin;
        int bottom = y + view.getMeasuredHeight();
        view.layout(x, y, view.getMeasuredWidth() + x, bottom);
        return bottom + lp.bottomMargin;
    }

    public void setState(@NonNull State state) {
        if (this.state != state) {
            this.state = state;
            updateForCurrentState();
        }
    }

    public void setType(@NonNull Type type) {
        if (this.type != type) {
            this.type = type;
            updateForCurrentState();
        }
    }

    public void setVisibility(int visibility) {
        int oldVisibility = getVisibility();
        super.setVisibility(visibility);
        if (oldVisibility != 0 && visibility == 0) {
            updateForCurrentState();
        }
    }

    public void setButtonClickListener(OnStubButtonClickListener listener) {
        OnClickListener onClickListener;
        this.buttonClickListener = listener;
        TextView textView = this.button;
        if (this.buttonClickListener == null) {
            onClickListener = null;
        }
        textView.setOnClickListener(onClickListener);
    }

    private void updateForCurrentState() {
        if (getVisibility() == 0) {
            if (this.state == State.LOADING) {
                this.progress.setVisibility(0);
                this.icon.setVisibility(8);
                this.background.setVisibility(8);
                this.title.setVisibility(8);
                this.subtitle.setVisibility(8);
                this.button.setVisibility(8);
            } else if (this.state == State.LOADED) {
                this.progress.setVisibility(8);
                this.icon.setVisibility(0);
                this.background.setVisibility(0);
                this.title.setVisibility(0);
                this.subtitle.setVisibility(0);
                this.icon.setImageResource(this.type.drawableResourceId);
                Drawable drawable = this.icon.getDrawable();
                if (drawable instanceof AnimationDrawable) {
                    ((AnimationDrawable) drawable).start();
                }
                prepareText(this.title, this.type.titleResourceId);
                prepareText(this.subtitle, this.type.subTitleResourceId);
                prepareText(this.button, this.type.buttonTitleResourceId);
                getViewTreeObserver().addOnPreDrawListener(new C06521());
            }
        }
    }

    private void prepareText(@NonNull TextView text, @StringRes int textResourceId) {
        LocalizationManager lm = LocalizationManager.from(getContext());
        if (textResourceId == 0) {
            text.setVisibility(8);
            return;
        }
        text.setText(lm.getString(textResourceId));
        text.setVisibility(0);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0176R.id.icon /*2131624456*/:
                if (this.type == Type.NO_INTERNET && this.buttonClickListener != null) {
                    this.buttonClickListener.onStubButtonClick(this.type);
                }
                StubLog.logStubClick(this.type);
            case 2131624679:
                this.buttonClickListener.onStubButtonClick(this.type);
                StubLog.logStubClickButton(this.type);
            default:
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public Type getType() {
        return this.type;
    }

    public State getState() {
        return this.state;
    }
}
