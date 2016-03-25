package ru.ok.android.ui.custom.indicator;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import java.util.ArrayList;
import ru.ok.android.C0206R;
import ru.ok.android.proto.MessagesProto.Message;

public class TitlePageIndicator extends View implements OnPageChangeListener, PageIndicator {
    protected int mActivePointerId;
    protected boolean mBoldText;
    protected final Rect mBounds;
    protected OnCenterItemClickListener mCenterItemClickListener;
    protected float mClipPadding;
    protected int mColorSelected;
    protected int mColorText;
    protected int mCurrentPage;
    protected float mFooterIndicatorHeight;
    protected IndicatorStyle mFooterIndicatorStyle;
    protected float mFooterIndicatorUnderlinePadding;
    protected float mFooterLineHeight;
    protected float mFooterPadding;
    protected boolean mIsDragging;
    protected float mLastMotionX;
    protected OnPageChangeListener mListener;
    protected float mPageOffset;
    protected PagerAdapter mPagerAdapter;
    protected final Paint mPaintFooter;
    protected final Paint mPaintFooterIndicator;
    protected final TextPaint mPaintText;
    protected Path mPath;
    protected int mScrollState;
    protected float mTitlePadding;
    protected float mTopPadding;
    protected int mTouchSlop;
    protected ViewPager mViewPager;
    protected int visiblePageCount;

    /* renamed from: ru.ok.android.ui.custom.indicator.TitlePageIndicator.1 */
    static /* synthetic */ class C06651 {
        static final /* synthetic */ int[] f97xa414446a;

        static {
            f97xa414446a = new int[IndicatorStyle.values().length];
            try {
                f97xa414446a[IndicatorStyle.Triangle.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f97xa414446a[IndicatorStyle.Underline.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public enum IndicatorStyle {
        None(0),
        Triangle(1),
        Underline(2);
        
        public final int value;

        private IndicatorStyle(int value) {
            this.value = value;
        }

        public static IndicatorStyle fromValue(int value) {
            for (IndicatorStyle style : values()) {
                if (style.value == value) {
                    return style;
                }
            }
            return null;
        }
    }

    public interface OnCenterItemClickListener {
        void onCenterItemClick(int i);
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR;
        int currentPage;

        /* renamed from: ru.ok.android.ui.custom.indicator.TitlePageIndicator.SavedState.1 */
        static class C06661 implements Creator<SavedState> {
            C06661() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        protected SavedState(Parcel in) {
            super(in);
            this.currentPage = in.readInt();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.currentPage);
        }

        static {
            CREATOR = new C06661();
        }
    }

    public TitlePageIndicator(Context context) {
        this(context, null);
    }

    public TitlePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitlePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCurrentPage = -1;
        this.mPaintText = new TextPaint();
        this.mPath = new Path();
        this.mBounds = new Rect();
        this.mPaintFooter = new Paint();
        this.mPaintFooterIndicator = new Paint();
        this.mLastMotionX = -1.0f;
        this.mActivePointerId = -1;
        this.visiblePageCount = 1;
        if (!isInEditMode()) {
            Resources res = getResources();
            int defaultFooterColor = res.getColor(2131492967);
            int defaultFooterLineColor = res.getColor(2131492967);
            float defaultFooterLineHeight = res.getDimension(2131230926);
            int defaultFooterIndicatorStyle = res.getInteger(2131427339);
            float defaultFooterIndicatorHeight = res.getDimension(2131230924);
            float defaultFooterIndicatorUnderlinePadding = res.getDimension(2131230925);
            float defaultFooterPadding = res.getDimension(2131230927);
            int defaultSelectedColor = res.getColor(2131492968);
            boolean defaultSelectedBold = res.getBoolean(2131361804);
            int defaultTextColor = res.getColor(2131492969);
            float defaultTextSize = res.getDimension(2131230928);
            float defaultTitlePadding = res.getDimension(2131230929);
            float defaultClipPadding = res.getDimension(2131230923);
            float defaultTopPadding = res.getDimension(2131230930);
            TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.TitlePageIndicator, defStyle, 0);
            this.mFooterLineHeight = a.getDimension(10, defaultFooterLineHeight);
            this.mFooterIndicatorStyle = IndicatorStyle.fromValue(a.getInteger(11, defaultFooterIndicatorStyle));
            this.mFooterIndicatorHeight = a.getDimension(12, defaultFooterIndicatorHeight);
            this.mFooterIndicatorUnderlinePadding = a.getDimension(13, defaultFooterIndicatorUnderlinePadding);
            this.mFooterPadding = a.getDimension(14, defaultFooterPadding);
            this.mTopPadding = a.getDimension(6, defaultTopPadding);
            this.mTitlePadding = a.getDimension(16, defaultTitlePadding);
            this.mClipPadding = a.getDimension(7, defaultClipPadding);
            this.mColorSelected = a.getColor(18, defaultSelectedColor);
            this.mColorText = a.getColor(1, defaultTextColor);
            this.mBoldText = a.getBoolean(15, defaultSelectedBold);
            float textSize = a.getDimension(0, defaultTextSize);
            int textShadowColor = a.getColor(2, 0);
            float textShadowRadius = a.getFloat(5, 0.0f);
            float textShadowDx = a.getFloat(3, 0.0f);
            float textShadowDy = a.getFloat(4, 0.0f);
            int footerColor = a.getColor(8, defaultFooterColor);
            int footerLineColor = a.getColor(9, defaultFooterLineColor);
            this.mPaintText.setTextSize(textSize);
            this.mPaintText.setAntiAlias(true);
            if (textShadowColor != 0) {
                float density = getContext().getResources().getDisplayMetrics().density;
                float shadowRadiusRealPx = textShadowRadius * density;
                float shadowTextShadowDxRealPx = textShadowDx * density;
                float shadowTextShadowDyRealPx = textShadowDy * density;
                this.mPaintText.setShadowLayer(shadowRadiusRealPx, shadowTextShadowDxRealPx, shadowTextShadowDyRealPx, textShadowColor);
            }
            this.mPaintFooter.setStyle(Style.FILL_AND_STROKE);
            this.mPaintFooter.setStrokeWidth(this.mFooterLineHeight);
            this.mPaintFooter.setColor(footerLineColor);
            this.mPaintFooterIndicator.setStyle(Style.FILL_AND_STROKE);
            this.mPaintFooterIndicator.setColor(footerColor);
            a.recycle();
            this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(context));
        }
    }

    public int getFooterColor() {
        return this.mPaintFooter.getColor();
    }

    public void setFooterColor(int footerColor) {
        this.mPaintFooter.setColor(footerColor);
        this.mPaintFooterIndicator.setColor(footerColor);
        invalidate();
    }

    public float getFooterLineHeight() {
        return this.mFooterLineHeight;
    }

    public void setFooterLineHeight(float footerLineHeight) {
        this.mFooterLineHeight = footerLineHeight;
        this.mPaintFooter.setStrokeWidth(this.mFooterLineHeight);
        invalidate();
    }

    public float getFooterIndicatorHeight() {
        return this.mFooterIndicatorHeight;
    }

    public void setFooterIndicatorHeight(float footerTriangleHeight) {
        this.mFooterIndicatorHeight = footerTriangleHeight;
        invalidate();
    }

    public float getFooterIndicatorPadding() {
        return this.mFooterPadding;
    }

    public void setFooterIndicatorPadding(float footerIndicatorPadding) {
        this.mFooterPadding = footerIndicatorPadding;
        invalidate();
    }

    public IndicatorStyle getFooterIndicatorStyle() {
        return this.mFooterIndicatorStyle;
    }

    public void setFooterIndicatorStyle(IndicatorStyle indicatorStyle) {
        this.mFooterIndicatorStyle = indicatorStyle;
        invalidate();
    }

    public int getSelectedColor() {
        return this.mColorSelected;
    }

    public void setSelectedColor(int selectedColor) {
        this.mColorSelected = selectedColor;
        invalidate();
    }

    public void setSelectedBold(boolean selectedBold) {
        this.mBoldText = selectedBold;
        invalidate();
    }

    public int getTextColor() {
        return this.mColorText;
    }

    public void setTextColor(int textColor) {
        this.mPaintText.setColor(textColor);
        this.mColorText = textColor;
        invalidate();
    }

    public float getTextSize() {
        return this.mPaintText.getTextSize();
    }

    public void setTextSize(float textSize) {
        this.mPaintText.setTextSize(textSize);
        invalidate();
    }

    public float getTitlePadding() {
        return this.mTitlePadding;
    }

    public void setTitlePadding(float titlePadding) {
        this.mTitlePadding = titlePadding;
        invalidate();
    }

    public float getTopPadding() {
        return this.mTopPadding;
    }

    public void setTopPadding(float topPadding) {
        this.mTopPadding = topPadding;
        invalidate();
    }

    public float getClipPadding() {
        return this.mClipPadding;
    }

    public void setClipPadding(float clipPadding) {
        this.mClipPadding = clipPadding;
        invalidate();
    }

    public void setTypeface(Typeface typeface) {
        this.mPaintText.setTypeface(typeface);
        invalidate();
    }

    public Typeface getTypeface() {
        return this.mPaintText.getTypeface();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mViewPager != null) {
            int count = this.mViewPager.getAdapter().getCount();
            if (count != 0) {
                if (this.mCurrentPage == -1 && this.mViewPager != null) {
                    this.mCurrentPage = this.mViewPager.getCurrentItem();
                }
                ArrayList<Rect> bounds = calculateAllBounds(this.mPaintText);
                int boundsSize = bounds.size();
                if (this.mCurrentPage < boundsSize) {
                    float offsetPercent;
                    int i;
                    Rect bound;
                    int w;
                    int countMinusOne = count - 1;
                    float halfWidth = ((float) getWidth()) / 2.0f;
                    int left = getLeft();
                    float leftClip = ((float) left) + this.mClipPadding;
                    int width = getWidth();
                    int height = getHeight();
                    int right = left + width;
                    float rightClip = ((float) right) - this.mClipPadding;
                    int page = this.mCurrentPage;
                    if (((double) this.mPageOffset) <= 0.5d) {
                        offsetPercent = this.mPageOffset;
                    } else {
                        page++;
                        offsetPercent = 1.0f - this.mPageOffset;
                    }
                    boolean currentSelected = offsetPercent <= 0.25f;
                    boolean currentBold = offsetPercent <= 0.05f;
                    float selectedPercent = (0.25f - offsetPercent) / 0.25f;
                    Rect curPageBound = (Rect) bounds.get(this.mCurrentPage);
                    float curPageWidth = (float) (curPageBound.right - curPageBound.left);
                    if (((float) curPageBound.left) < leftClip) {
                        clipViewOnTheLeft(curPageBound, curPageWidth, left);
                    }
                    if (((float) curPageBound.right) > rightClip) {
                        clipViewOnTheRight(curPageBound, curPageWidth, right);
                    }
                    if (this.mCurrentPage > 0) {
                        for (i = this.mCurrentPage - 1; i >= 0; i--) {
                            bound = (Rect) bounds.get(i);
                            if (((float) bound.left) < leftClip) {
                                w = bound.right - bound.left;
                                clipViewOnTheLeft(bound, (float) w, left);
                                Rect rightBound = (Rect) bounds.get(i + 1);
                                if (((float) bound.right) + this.mTitlePadding > ((float) rightBound.left)) {
                                    bound.left = (int) (((float) (rightBound.left - w)) - this.mTitlePadding);
                                    bound.right = bound.left + w;
                                }
                            }
                        }
                    }
                    if (this.mCurrentPage < countMinusOne) {
                        for (i = this.mCurrentPage + 1; i < count; i++) {
                            bound = (Rect) bounds.get(i);
                            if (((float) bound.right) > rightClip) {
                                w = bound.right - bound.left;
                                clipViewOnTheRight(bound, (float) w, right);
                                Rect leftBound = (Rect) bounds.get(i - 1);
                                if (((float) bound.left) - this.mTitlePadding < ((float) leftBound.right)) {
                                    bound.left = (int) (((float) leftBound.right) + this.mTitlePadding);
                                    bound.right = bound.left + w;
                                }
                            }
                        }
                    }
                    int colorTextAlpha = this.mColorText >>> 24;
                    i = 0;
                    while (i < count) {
                        bound = (Rect) bounds.get(i);
                        if ((bound.left > left && bound.left < right) || (bound.right > left && bound.right < right)) {
                            boolean currentPage = i == page;
                            CharSequence pageTitle = getTitle(i);
                            TextPaint textPaint = this.mPaintText;
                            boolean z = currentPage && currentBold && this.mBoldText;
                            textPaint.setFakeBoldText(z);
                            this.mPaintText.setColor(this.mColorText);
                            if (currentPage && currentSelected) {
                                this.mPaintText.setAlpha(colorTextAlpha - ((int) (((float) colorTextAlpha) * selectedPercent)));
                            }
                            canvas.drawText(pageTitle, 0, pageTitle.length(), (float) bound.left, this.mTopPadding + ((float) bound.bottom), this.mPaintText);
                            if (currentPage && currentSelected) {
                                this.mPaintText.setColor(this.mColorSelected);
                                this.mPaintText.setAlpha((int) (((float) (this.mColorSelected >>> 24)) * selectedPercent));
                                canvas.drawText(pageTitle, 0, pageTitle.length(), (float) bound.left, this.mTopPadding + ((float) bound.bottom), this.mPaintText);
                            }
                        }
                        i++;
                    }
                    this.mPath.reset();
                    this.mPath.moveTo(0.0f, ((float) height) - (this.mFooterLineHeight / 2.0f));
                    this.mPath.lineTo((float) width, ((float) height) - (this.mFooterLineHeight / 2.0f));
                    this.mPath.close();
                    canvas.drawPath(this.mPath, this.mPaintFooter);
                    switch (C06651.f97xa414446a[this.mFooterIndicatorStyle.ordinal()]) {
                        case Message.TEXT_FIELD_NUMBER /*1*/:
                            this.mPath.reset();
                            this.mPath.moveTo(halfWidth, (((float) height) - this.mFooterLineHeight) - this.mFooterIndicatorHeight);
                            this.mPath.lineTo(this.mFooterIndicatorHeight + halfWidth, ((float) height) - this.mFooterLineHeight);
                            this.mPath.lineTo(halfWidth - this.mFooterIndicatorHeight, ((float) height) - this.mFooterLineHeight);
                            this.mPath.close();
                            canvas.drawPath(this.mPath, this.mPaintFooterIndicator);
                        case Message.AUTHORID_FIELD_NUMBER /*2*/:
                            if (currentSelected && page < boundsSize) {
                                Rect underlineBounds = (Rect) bounds.get(page);
                                this.mPath.reset();
                                this.mPath.moveTo(((float) underlineBounds.left) - this.mFooterIndicatorUnderlinePadding, ((float) height) - this.mFooterLineHeight);
                                this.mPath.lineTo(((float) underlineBounds.right) + this.mFooterIndicatorUnderlinePadding, ((float) height) - this.mFooterLineHeight);
                                this.mPath.lineTo(((float) underlineBounds.right) + this.mFooterIndicatorUnderlinePadding, (((float) height) - this.mFooterLineHeight) - this.mFooterIndicatorHeight);
                                this.mPath.lineTo(((float) underlineBounds.left) - this.mFooterIndicatorUnderlinePadding, (((float) height) - this.mFooterLineHeight) - this.mFooterIndicatorHeight);
                                this.mPath.close();
                                this.mPaintFooterIndicator.setAlpha((int) (255.0f * selectedPercent));
                                canvas.drawPath(this.mPath, this.mPaintFooterIndicator);
                                this.mPaintFooterIndicator.setAlpha(MotionEventCompat.ACTION_MASK);
                            }
                        default:
                    }
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r20) {
        /*
        r19 = this;
        r17 = super.onTouchEvent(r20);
        if (r17 == 0) goto L_0x0009;
    L_0x0006:
        r17 = 1;
    L_0x0008:
        return r17;
    L_0x0009:
        r0 = r19;
        r0 = r0.mViewPager;
        r17 = r0;
        if (r17 == 0) goto L_0x0021;
    L_0x0011:
        r0 = r19;
        r0 = r0.mViewPager;
        r17 = r0;
        r17 = r17.getAdapter();
        r17 = r17.getCount();
        if (r17 != 0) goto L_0x0024;
    L_0x0021:
        r17 = 0;
        goto L_0x0008;
    L_0x0024:
        r2 = r20.getAction();
        r0 = r2 & 255;
        r17 = r0;
        switch(r17) {
            case 0: goto L_0x0032;
            case 1: goto L_0x00bf;
            case 2: goto L_0x004d;
            case 3: goto L_0x00bf;
            case 4: goto L_0x002f;
            case 5: goto L_0x0175;
            case 6: goto L_0x0193;
            default: goto L_0x002f;
        };
    L_0x002f:
        r17 = 1;
        goto L_0x0008;
    L_0x0032:
        r17 = 0;
        r0 = r20;
        r1 = r17;
        r17 = android.support.v4.view.MotionEventCompat.getPointerId(r0, r1);
        r0 = r17;
        r1 = r19;
        r1.mActivePointerId = r0;
        r17 = r20.getX();
        r0 = r17;
        r1 = r19;
        r1.mLastMotionX = r0;
        goto L_0x002f;
    L_0x004d:
        r0 = r19;
        r0 = r0.mActivePointerId;
        r17 = r0;
        r0 = r20;
        r1 = r17;
        r3 = android.support.v4.view.MotionEventCompat.findPointerIndex(r0, r1);
        r0 = r20;
        r16 = android.support.v4.view.MotionEventCompat.getX(r0, r3);
        r0 = r19;
        r0 = r0.mLastMotionX;
        r17 = r0;
        r5 = r16 - r17;
        r0 = r19;
        r0 = r0.mIsDragging;
        r17 = r0;
        if (r17 != 0) goto L_0x008c;
    L_0x0071:
        r17 = java.lang.Math.abs(r5);
        r0 = r19;
        r0 = r0.mTouchSlop;
        r18 = r0;
        r0 = r18;
        r0 = (float) r0;
        r18 = r0;
        r17 = (r17 > r18 ? 1 : (r17 == r18 ? 0 : -1));
        if (r17 <= 0) goto L_0x008c;
    L_0x0084:
        r17 = 1;
        r0 = r17;
        r1 = r19;
        r1.mIsDragging = r0;
    L_0x008c:
        r0 = r19;
        r0 = r0.mIsDragging;
        r17 = r0;
        if (r17 == 0) goto L_0x002f;
    L_0x0094:
        r0 = r16;
        r1 = r19;
        r1.mLastMotionX = r0;
        r0 = r19;
        r0 = r0.mViewPager;
        r17 = r0;
        r17 = r17.isFakeDragging();
        if (r17 != 0) goto L_0x00b2;
    L_0x00a6:
        r0 = r19;
        r0 = r0.mViewPager;
        r17 = r0;
        r17 = r17.beginFakeDrag();
        if (r17 == 0) goto L_0x002f;
    L_0x00b2:
        r0 = r19;
        r0 = r0.mViewPager;
        r17 = r0;
        r0 = r17;
        r0.fakeDragBy(r5);
        goto L_0x002f;
    L_0x00bf:
        r0 = r19;
        r0 = r0.mIsDragging;
        r17 = r0;
        if (r17 != 0) goto L_0x014e;
    L_0x00c7:
        r0 = r19;
        r0 = r0.mViewPager;
        r17 = r0;
        r17 = r17.getAdapter();
        r4 = r17.getCount();
        r15 = r19.getWidth();
        r0 = (float) r15;
        r17 = r0;
        r18 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r7 = r17 / r18;
        r0 = (float) r15;
        r17 = r0;
        r18 = 1086324736; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r14 = r17 / r18;
        r9 = r7 - r14;
        r13 = r7 + r14;
        r6 = r20.getX();
        r17 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1));
        if (r17 >= 0) goto L_0x0110;
    L_0x00f3:
        r0 = r19;
        r0 = r0.mCurrentPage;
        r17 = r0;
        if (r17 <= 0) goto L_0x014e;
    L_0x00fb:
        r0 = r19;
        r0 = r0.mViewPager;
        r17 = r0;
        r0 = r19;
        r0 = r0.mCurrentPage;
        r18 = r0;
        r18 = r18 + -1;
        r17.setCurrentItem(r18);
        r17 = 1;
        goto L_0x0008;
    L_0x0110:
        r17 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1));
        if (r17 <= 0) goto L_0x0137;
    L_0x0114:
        r0 = r19;
        r0 = r0.mCurrentPage;
        r17 = r0;
        r18 = r4 + -1;
        r0 = r17;
        r1 = r18;
        if (r0 >= r1) goto L_0x014e;
    L_0x0122:
        r0 = r19;
        r0 = r0.mViewPager;
        r17 = r0;
        r0 = r19;
        r0 = r0.mCurrentPage;
        r18 = r0;
        r18 = r18 + 1;
        r17.setCurrentItem(r18);
        r17 = 1;
        goto L_0x0008;
    L_0x0137:
        r0 = r19;
        r0 = r0.mCenterItemClickListener;
        r17 = r0;
        if (r17 == 0) goto L_0x014e;
    L_0x013f:
        r0 = r19;
        r0 = r0.mCenterItemClickListener;
        r17 = r0;
        r0 = r19;
        r0 = r0.mCurrentPage;
        r18 = r0;
        r17.onCenterItemClick(r18);
    L_0x014e:
        r17 = 0;
        r0 = r17;
        r1 = r19;
        r1.mIsDragging = r0;
        r17 = -1;
        r0 = r17;
        r1 = r19;
        r1.mActivePointerId = r0;
        r0 = r19;
        r0 = r0.mViewPager;
        r17 = r0;
        r17 = r17.isFakeDragging();
        if (r17 == 0) goto L_0x002f;
    L_0x016a:
        r0 = r19;
        r0 = r0.mViewPager;
        r17 = r0;
        r17.endFakeDrag();
        goto L_0x002f;
    L_0x0175:
        r8 = android.support.v4.view.MotionEventCompat.getActionIndex(r20);
        r0 = r20;
        r16 = android.support.v4.view.MotionEventCompat.getX(r0, r8);
        r0 = r16;
        r1 = r19;
        r1.mLastMotionX = r0;
        r0 = r20;
        r17 = android.support.v4.view.MotionEventCompat.getPointerId(r0, r8);
        r0 = r17;
        r1 = r19;
        r1.mActivePointerId = r0;
        goto L_0x002f;
    L_0x0193:
        r12 = android.support.v4.view.MotionEventCompat.getActionIndex(r20);
        r0 = r20;
        r11 = android.support.v4.view.MotionEventCompat.getPointerId(r0, r12);
        r0 = r19;
        r0 = r0.mActivePointerId;
        r17 = r0;
        r0 = r17;
        if (r11 != r0) goto L_0x01b6;
    L_0x01a7:
        if (r12 != 0) goto L_0x01d4;
    L_0x01a9:
        r10 = 1;
    L_0x01aa:
        r0 = r20;
        r17 = android.support.v4.view.MotionEventCompat.getPointerId(r0, r10);
        r0 = r17;
        r1 = r19;
        r1.mActivePointerId = r0;
    L_0x01b6:
        r0 = r19;
        r0 = r0.mActivePointerId;
        r17 = r0;
        r0 = r20;
        r1 = r17;
        r17 = android.support.v4.view.MotionEventCompat.findPointerIndex(r0, r1);
        r0 = r20;
        r1 = r17;
        r17 = android.support.v4.view.MotionEventCompat.getX(r0, r1);
        r0 = r17;
        r1 = r19;
        r1.mLastMotionX = r0;
        goto L_0x002f;
    L_0x01d4:
        r10 = 0;
        goto L_0x01aa;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.custom.indicator.TitlePageIndicator.onTouchEvent(android.view.MotionEvent):boolean");
    }

    protected void clipViewOnTheRight(Rect curViewBound, float curViewWidth, int right) {
        curViewBound.right = (int) (((float) right) - this.mClipPadding);
        curViewBound.left = (int) (((float) curViewBound.right) - curViewWidth);
    }

    protected void clipViewOnTheLeft(Rect curViewBound, float curViewWidth, int left) {
        curViewBound.left = (int) (((float) left) + this.mClipPadding);
        curViewBound.right = (int) (this.mClipPadding + curViewWidth);
    }

    protected ArrayList<Rect> calculateAllBounds(Paint paint) {
        ArrayList<Rect> list = new ArrayList();
        int count = this.mViewPager.getAdapter().getCount();
        int width = getWidth() / this.visiblePageCount;
        int halfWidth = width / 2;
        for (int i = 0; i < count; i++) {
            Rect bounds = calcBounds(i, paint);
            int w = bounds.right - bounds.left;
            int h = bounds.bottom - bounds.top;
            bounds.left = (int) ((((float) halfWidth) - (((float) w) / 2.0f)) + ((((float) (i - this.mCurrentPage)) - this.mPageOffset) * ((float) width)));
            bounds.right = bounds.left + w;
            bounds.top = 0;
            bounds.bottom = h;
            list.add(bounds);
        }
        return list;
    }

    public void setVisiblePageCount(int visiblePageCount) {
        this.visiblePageCount = visiblePageCount;
    }

    protected Rect calcBounds(int index, Paint paint) {
        Rect bounds = new Rect();
        CharSequence title = getTitle(index);
        bounds.right = (int) paint.measureText(title, 0, title.length());
        bounds.bottom = (int) (paint.descent() - paint.ascent());
        return bounds;
    }

    public void setViewPager(ViewPager view) {
        if (this.mViewPager != view) {
            if (this.mViewPager != null) {
                this.mViewPager.setOnPageChangeListener(null);
            }
            this.mPagerAdapter = view.getAdapter();
            if (this.mPagerAdapter == null) {
                throw new IllegalStateException("ViewPager does not have adapter instance.");
            }
            this.mViewPager = view;
            this.mViewPager.setOnPageChangeListener(this);
            invalidate();
        }
    }

    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
        this.mCenterItemClickListener = listener;
    }

    public void onPageScrollStateChanged(int state) {
        this.mScrollState = state;
        if (this.mListener != null) {
            this.mListener.onPageScrollStateChanged(state);
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.mCurrentPage = position;
        this.mPageOffset = positionOffset;
        invalidate();
        if (this.mListener != null) {
            this.mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void onPageSelected(int position) {
        if (this.mScrollState == 0) {
            this.mCurrentPage = position;
            invalidate();
        }
        if (this.mListener != null) {
            this.mListener.onPageSelected(position);
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mListener = listener;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float height;
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (MeasureSpec.getMode(heightMeasureSpec) == 1073741824) {
            height = (float) MeasureSpec.getSize(heightMeasureSpec);
        } else {
            this.mBounds.setEmpty();
            this.mBounds.bottom = (int) (this.mPaintText.descent() - this.mPaintText.ascent());
            height = ((((float) (this.mBounds.bottom - this.mBounds.top)) + this.mFooterLineHeight) + this.mFooterPadding) + this.mTopPadding;
            if (this.mFooterIndicatorStyle != IndicatorStyle.None) {
                height += this.mFooterIndicatorHeight;
            }
        }
        setMeasuredDimension(measuredWidth, (int) height);
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mCurrentPage = savedState.currentPage;
        requestLayout();
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.currentPage = this.mCurrentPage;
        return savedState;
    }

    protected CharSequence getTitle(int i) {
        CharSequence title = this.mPagerAdapter.getPageTitle(i);
        if (title == null) {
            title = "";
        }
        return title.toString();
    }
}
