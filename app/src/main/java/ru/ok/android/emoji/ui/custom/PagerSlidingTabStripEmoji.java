package ru.ok.android.emoji.ui.custom;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.Locale;
import ru.ok.android.emoji.C0263R;

@Deprecated
public final class PagerSlidingTabStripEmoji extends HorizontalScrollView {
    private static final int[] ATTRS;
    private boolean checkedTabWidths;
    private int currentPosition;
    private float currentPositionOffset;
    private LayoutParams defaultTabLayoutParams;
    public OnPageChangeListener delegatePageListener;
    private int dividerColor;
    private int dividerPadding;
    private Paint dividerPaint;
    private int dividerWidth;
    private LayoutParams expandedTabLayoutParams;
    private int indicatorColor;
    private int indicatorHeight;
    private int lastScrollX;
    private Locale locale;
    private final DataSetObserver observer;
    private final PageListener pageListener;
    private ViewPager pager;
    private Paint rectPaint;
    private int scrollOffset;
    private boolean shouldExpand;
    private int tabBackgroundResId;
    private TabClickListener tabClickListener;
    private int tabCount;
    private int tabPadding;
    private ColorStateList tabTextColor;
    private int tabTextSize;
    private LinearLayout tabsContainer;
    private boolean textAllCaps;
    private boolean topLine;
    private int underlineColor;
    private int underlineHeight;

    static {
        ATTRS = new int[]{16842901, 16842904};
    }

    public PagerSlidingTabStripEmoji(Context context) {
        this(context, null);
    }

    public PagerSlidingTabStripEmoji(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerSlidingTabStripEmoji(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.pageListener = new PageListener(this);
        this.currentPosition = 0;
        this.currentPositionOffset = 0.0f;
        this.checkedTabWidths = false;
        this.indicatorColor = -10066330;
        this.underlineColor = 436207616;
        this.dividerColor = 436207616;
        this.shouldExpand = false;
        this.textAllCaps = true;
        this.scrollOffset = 52;
        this.indicatorHeight = 8;
        this.underlineHeight = 2;
        this.dividerPadding = 12;
        this.tabPadding = 24;
        this.dividerWidth = 1;
        this.tabTextSize = 13;
        this.lastScrollX = 0;
        this.observer = new 1(this);
        setFillViewport(true);
        setWillNotDraw(false);
        this.tabsContainer = new LinearLayout(context);
        this.tabsContainer.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        this.scrollOffset = (int) TypedValue.applyDimension(1, (float) this.scrollOffset, dm);
        this.indicatorHeight = (int) TypedValue.applyDimension(1, (float) this.indicatorHeight, dm);
        this.underlineHeight = (int) TypedValue.applyDimension(1, (float) this.underlineHeight, dm);
        this.dividerPadding = (int) TypedValue.applyDimension(1, (float) this.dividerPadding, dm);
        this.tabPadding = (int) TypedValue.applyDimension(1, (float) this.tabPadding, dm);
        this.dividerWidth = (int) TypedValue.applyDimension(1, (float) this.dividerWidth, dm);
        this.tabTextSize = (int) TypedValue.applyDimension(2, (float) this.tabTextSize, dm);
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        this.tabTextSize = a.getDimensionPixelSize(0, this.tabTextSize);
        ColorStateList colorStateList = a.getColorStateList(1);
        if (colorStateList != null) {
            this.tabTextColor = colorStateList;
        }
        a.recycle();
        a = context.obtainStyledAttributes(attrs, C0263R.styleable.PagerSlidingTabStripEmoji);
        this.indicatorColor = a.getColor(C0263R.styleable.PagerSlidingTabStripEmoji_indicatorColor, this.indicatorColor);
        this.underlineColor = a.getColor(C0263R.styleable.PagerSlidingTabStripEmoji_underlineColor, this.underlineColor);
        this.dividerColor = a.getColor(C0263R.styleable.PagerSlidingTabStripEmoji_dividerColor, this.dividerColor);
        this.indicatorHeight = a.getDimensionPixelSize(C0263R.styleable.PagerSlidingTabStripEmoji_indicatorHeight, this.indicatorHeight);
        this.underlineHeight = a.getDimensionPixelSize(C0263R.styleable.PagerSlidingTabStripEmoji_underlineHeight, this.underlineHeight);
        this.dividerPadding = a.getDimensionPixelSize(C0263R.styleable.PagerSlidingTabStripEmoji_pstsDividerPadding, this.dividerPadding);
        this.tabPadding = a.getDimensionPixelSize(C0263R.styleable.PagerSlidingTabStripEmoji_tabPaddingLeftRight, this.tabPadding);
        this.tabBackgroundResId = a.getResourceId(C0263R.styleable.PagerSlidingTabStripEmoji_emojiTabBackground, this.tabBackgroundResId);
        this.shouldExpand = a.getBoolean(C0263R.styleable.PagerSlidingTabStripEmoji_shouldExpand, this.shouldExpand);
        this.scrollOffset = a.getDimensionPixelSize(C0263R.styleable.PagerSlidingTabStripEmoji_scrollOffset, this.scrollOffset);
        this.textAllCaps = a.getBoolean(C0263R.styleable.PagerSlidingTabStripEmoji_pstsTextAllCaps, this.textAllCaps);
        a.recycle();
        this.rectPaint = new Paint();
        this.rectPaint.setAntiAlias(true);
        this.rectPaint.setStyle(Style.FILL);
        this.dividerPaint = new Paint();
        this.dividerPaint.setAntiAlias(true);
        this.dividerPaint.setStrokeWidth((float) this.dividerWidth);
        this.defaultTabLayoutParams = new LayoutParams(-2, -1);
        this.expandedTabLayoutParams = new LayoutParams(0, -1, 1.0f);
        if (this.locale == null) {
            this.locale = getResources().getConfiguration().locale;
        }
    }

    public void setViewPager(ViewPager pager) {
        if (this.pager != null) {
            PagerAdapter adapter = this.pager.getAdapter();
            if (adapter != null) {
                adapter.unregisterDataSetObserver(this.observer);
            }
        }
        this.pager = pager;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        pager.setOnPageChangeListener(this.pageListener);
        pager.getAdapter().registerDataSetObserver(this.observer);
        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {
        this.tabsContainer.removeAllViews();
        this.tabCount = this.pager.getAdapter().getCount();
        PagerAdapter adapter = this.pager.getAdapter();
        for (int i = 0; i < this.tabCount; i++) {
            if (adapter instanceof ViewTabProvider) {
                addIconTab(i, ((ViewTabProvider) adapter).getPageIconView(i));
            } else if (adapter instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) adapter).getPageIconResId(i));
            } else {
                addTextTab(i, adapter.getPageTitle(i));
            }
        }
        updateTabStyles();
        this.checkedTabWidths = false;
        getViewTreeObserver().addOnGlobalLayoutListener(new 2(this));
    }

    private void addTextTab(int position, CharSequence title) {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setFocusable(true);
        tab.setGravity(17);
        tab.setSingleLine();
        tab.setOnClickListener(getClickListener(position));
        this.tabsContainer.addView(tab);
    }

    private void addIconTab(int position, int resId) {
        ImageButton tab = new ImageButton(getContext());
        tab.setFocusable(true);
        tab.setImageResource(resId);
        tab.setOnClickListener(getClickListener(position));
        this.tabsContainer.addView(tab);
    }

    private void addIconTab(int position, View view) {
        view.setFocusable(true);
        view.setOnClickListener(getClickListener(position));
        this.tabsContainer.addView(view);
    }

    @NonNull
    private OnClickListener getClickListener(int position) {
        return new 3(this, position);
    }

    private void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View v = this.tabsContainer.getChildAt(i);
            v.setLayoutParams(this.defaultTabLayoutParams);
            v.setBackgroundResource(this.tabBackgroundResId);
            if (this.shouldExpand) {
                v.setPadding(0, 0, 0, 0);
            } else {
                v.setPadding(this.tabPadding, 0, this.tabPadding, 0);
            }
            if (v instanceof TextView) {
                TextView tab = (TextView) v;
                tab.setTextSize(0, (float) this.tabTextSize);
                tab.setTextColor(this.tabTextColor);
                if (this.textAllCaps) {
                    tab.setAllCaps(true);
                }
            }
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.shouldExpand && MeasureSpec.getMode(widthMeasureSpec) != 0 && !this.checkedTabWidths) {
            int i;
            int myWidth = getMeasuredWidth();
            int childWidth = 0;
            for (i = 0; i < this.tabCount; i++) {
                childWidth += this.tabsContainer.getChildAt(i).getMeasuredWidth();
            }
            if (childWidth > 0 && myWidth > 0) {
                if (childWidth <= myWidth) {
                    for (i = 0; i < this.tabCount; i++) {
                        this.tabsContainer.getChildAt(i).setLayoutParams(this.expandedTabLayoutParams);
                    }
                }
                this.checkedTabWidths = true;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    private void scrollToChild(int position, int offset) {
        if (this.tabCount != 0) {
            int newScrollX = this.tabsContainer.getChildAt(position).getLeft() + offset;
            if (position > 0 || offset > 0) {
                newScrollX -= this.scrollOffset;
            }
            if (newScrollX != this.lastScrollX) {
                this.lastScrollX = newScrollX;
                smoothScrollTo(newScrollX, 0);
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode() && this.tabCount != 0) {
            int height = getHeight();
            this.rectPaint.setColor(this.indicatorColor);
            View currentTab = this.tabsContainer.getChildAt(this.currentPosition);
            float lineLeft = (float) currentTab.getLeft();
            float lineRight = (float) currentTab.getRight();
            if (this.currentPositionOffset > 0.0f && this.currentPosition < this.tabCount - 1) {
                View nextTab = this.tabsContainer.getChildAt(this.currentPosition + 1);
                lineLeft = (this.currentPositionOffset * ((float) nextTab.getLeft())) + ((1.0f - this.currentPositionOffset) * lineLeft);
                lineRight = (this.currentPositionOffset * ((float) nextTab.getRight())) + ((1.0f - this.currentPositionOffset) * lineRight);
            }
            if (this.topLine) {
                canvas.drawRect(lineLeft, 0.0f, lineRight, (float) this.indicatorHeight, this.rectPaint);
            } else {
                canvas.drawRect(lineLeft, (float) (height - this.indicatorHeight), lineRight, (float) height, this.rectPaint);
            }
            this.rectPaint.setColor(this.underlineColor);
            canvas.drawRect(0.0f, (float) (height - this.underlineHeight), (float) this.tabsContainer.getWidth(), (float) height, this.rectPaint);
            this.dividerPaint.setColor(this.dividerColor);
            for (int i = 0; i < this.tabCount - 1; i++) {
                View tab = this.tabsContainer.getChildAt(i);
                canvas.drawLine((float) tab.getRight(), (float) this.dividerPadding, (float) tab.getRight(), (float) (height - this.dividerPadding), this.dividerPaint);
            }
        }
    }

    public void setTopLine(boolean topLine) {
        this.topLine = topLine;
    }

    private void setCurrentPosition(int position) {
        this.currentPosition = position;
        int i = 0;
        while (i < this.tabCount) {
            this.tabsContainer.getChildAt(i).setSelected(i == this.currentPosition);
            i++;
        }
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return this.indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return this.underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return this.dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return this.underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return this.dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return this.scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return this.shouldExpand;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return this.tabTextSize;
    }

    public void setTextColor(ColorStateList textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColorStateList(resId);
        updateTabStyles();
    }

    public ColorStateList getTextColor() {
        return this.tabTextColor;
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
    }

    public int getTabBackground() {
        return this.tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return this.tabPadding;
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setCurrentPosition(savedState.currentPosition);
        requestLayout();
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.currentPosition = this.currentPosition;
        return savedState;
    }

    public void setTabClickListener(TabClickListener tabClickListener) {
        this.tabClickListener = tabClickListener;
    }
}
