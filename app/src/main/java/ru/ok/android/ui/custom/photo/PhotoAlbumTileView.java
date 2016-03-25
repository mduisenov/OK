package ru.ok.android.ui.custom.photo;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.DraweeHolder;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import ru.ok.android.fresco.DraweeHolderView;
import ru.ok.android.fresco.FrescoOdkl;

public class PhotoAlbumTileView extends DraweeHolderView implements AnimatorUpdateListener {
    private long albumId;
    private ValueAnimator alphaAnimator;
    private Paint backgroundPaint;
    private String countText;
    private int coverAlpha;
    private Bitmap coverBitmap;
    private Drawable coverDrawable;
    private final Rect coverDstRect;
    private final Paint coverPaint;
    private BitmapDrawable gradient;
    private GenericDraweeHierarchy hierarchy;
    private StringBuilder lineBuilder;
    private Drawable lockDrawable;
    private boolean locked;
    private String name;
    private boolean noCoverSet;
    private int padding;
    private Rect textBounds;
    private int textPadding;
    private TextPaint textPaint;
    private LinkedList<String> wordsStack;

    public PhotoAlbumTileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.textBounds = new Rect();
        this.lineBuilder = new StringBuilder();
        this.wordsStack = new LinkedList();
        this.coverPaint = new Paint();
        this.coverDstRect = new Rect();
        this.coverAlpha = MotionEventCompat.ACTION_MASK;
        onCreate();
    }

    public PhotoAlbumTileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.textBounds = new Rect();
        this.lineBuilder = new StringBuilder();
        this.wordsStack = new LinkedList();
        this.coverPaint = new Paint();
        this.coverDstRect = new Rect();
        this.coverAlpha = MotionEventCompat.ACTION_MASK;
        onCreate();
    }

    public PhotoAlbumTileView(Context context) {
        super(context);
        this.textBounds = new Rect();
        this.lineBuilder = new StringBuilder();
        this.wordsStack = new LinkedList();
        this.coverPaint = new Paint();
        this.coverDstRect = new Rect();
        this.coverAlpha = MotionEventCompat.ACTION_MASK;
        onCreate();
    }

    private final void onCreate() {
        this.hierarchy = GenericDraweeHierarchyBuilder.newInstance(getResources()).setFailureImage(ResourcesCompat.getDrawable(getResources(), 2130838537, getContext().getTheme()), ScaleType.CENTER_INSIDE).build();
        this.padding = getContext().getResources().getDimensionPixelSize(2131231111);
        setPadding(this.padding, this.padding, this.padding, this.padding);
        this.backgroundPaint = new Paint();
        this.backgroundPaint.setColor(getResources().getColor(2131493102));
        this.textPaint = new TextPaint();
        this.textPaint.setColor(getResources().getColor(2131492895));
        this.textPaint.setTextSize(TypedValue.applyDimension(2, 12.0f, getResources().getDisplayMetrics()));
        this.textPaint.setFakeBoldText(true);
        this.textPaint.setAntiAlias(true);
        this.textPadding = getResources().getDimensionPixelSize(2131231122);
        this.lockDrawable = getResources().getDrawable(2130838161);
        this.gradient = (BitmapDrawable) getResources().getDrawable(2130838544);
        this.gradient.setDither(true);
        this.gradient.setAntiAlias(true);
        this.gradient.setFilterBitmap(true);
        this.coverPaint.setAntiAlias(true);
        this.coverPaint.setFilterBitmap(true);
        this.coverPaint.setDither(true);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public final void setAlbumName(String albumName) {
        this.name = albumName;
        invalidate();
    }

    public final void setPhotosCount(int count) {
        this.countText = "   " + count + "";
        invalidate();
    }

    public final void setAlbumCover(int coverResId, boolean animateIfNew) {
        setAlbumCoverUri(FrescoOdkl.uriFromResId(coverResId), animateIfNew);
        ((GenericDraweeHierarchy) getHolder().getHierarchy()).setActualImageScaleType(ScaleType.CENTER_INSIDE);
    }

    public final void setAlbumCover(Drawable cover, boolean animateIfNew) {
        if (this.coverDrawable != null) {
            this.coverDrawable.setCallback(null);
        }
        this.coverDrawable = cover;
        cover.setCallback(this);
        setAlbumCoverInternal(cover, animateIfNew);
    }

    private final void setAlbumCoverInternal(Object cover, boolean animateIfNew) {
        cancelAnimation();
        if (cover == null) {
            this.noCoverSet = true;
        } else {
            if (this.noCoverSet && animateIfNew) {
                animateAlbumCover();
            }
            this.noCoverSet = false;
        }
        invalidate();
    }

    private final void animateAlbumCover() {
        cancelAnimation();
        this.coverAlpha = 0;
        this.alphaAnimator = ValueAnimator.ofInt(new int[]{0, MotionEventCompat.ACTION_MASK});
        this.alphaAnimator.addUpdateListener(this);
        this.alphaAnimator.start();
    }

    private void cancelAnimation() {
        if (this.alphaAnimator != null && this.alphaAnimator.isRunning()) {
            this.alphaAnimator.cancel();
        }
        this.coverAlpha = MotionEventCompat.ACTION_MASK;
    }

    public void onAnimationUpdate(ValueAnimator animator) {
        this.coverAlpha = ((Integer) animator.getAnimatedValue()).intValue();
        invalidate();
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.coverBitmap == null || this.coverAlpha != MotionEventCompat.ACTION_MASK) {
            drawBackground(canvas);
        }
        drawCover(canvas);
        drawGradient(canvas);
        drawText(canvas);
        if (this.locked) {
            drawLock(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawRect((float) getPaddingLeft(), (float) getPaddingTop(), (float) (getPaddingLeft() + getContentWidth()), (float) (getPaddingTop() + getContentHeight()), this.backgroundPaint);
    }

    private void drawCover(Canvas canvas) {
        recalculateDstRect();
        this.coverDrawable.setBounds(this.coverDstRect);
        this.coverDrawable.setAlpha(this.coverAlpha);
        this.coverDrawable.draw(canvas);
    }

    private void recalculateDstRect() {
        this.coverDstRect.left = getPaddingLeft();
        this.coverDstRect.top = getPaddingTop();
        this.coverDstRect.right = this.coverDstRect.left + getContentWidth();
        this.coverDstRect.bottom = this.coverDstRect.top + getContentHeight();
    }

    private void drawGradient(Canvas canvas) {
        int left = getPaddingLeft();
        this.gradient.setBounds(left, (getMeasuredHeight() - getPaddingBottom()) - 208, left + getContentWidth(), getMeasuredHeight() - getPaddingBottom());
        this.gradient.draw(canvas);
    }

    protected void drawText(Canvas canvas) {
        if (!TextUtils.isEmpty(this.name)) {
            String str = this.name;
            String fullText = r0 + this.countText;
            this.textPaint.getTextBounds(fullText, 0, fullText.length(), this.textBounds);
            float leftEdge = (float) (this.padding + this.textPadding);
            float lineWidth = ((float) ((getContentWidth() - (this.textPadding * 2)) - (this.padding * 2))) - leftEdge;
            float bottomEdge = (float) getContentHeight();
            float lineHeight = (float) (this.textBounds.bottom - this.textBounds.top);
            float totalNameWidth = this.textPaint.measureText(this.name);
            float countTextWidth = this.textPaint.measureText(this.countText);
            float textBottomY = bottomEdge - ((float) this.textPadding);
            if (totalNameWidth + countTextWidth <= lineWidth) {
                str = this.name;
                canvas.drawText(r0 + this.countText, leftEdge, textBottomY, this.textPaint);
                return;
            }
            String[] split = this.name.split(" ");
            StringBuilder stringBuilder;
            StringBuilder stringBuilder2;
            if (this.textPaint.measureText(split[0]) + countTextWidth > lineWidth) {
                CharSequence result = TextUtils.ellipsize(this.name, this.textPaint, lineWidth - countTextWidth, TruncateAt.END);
                stringBuilder = new StringBuilder();
                canvas.drawText(stringBuilder2.append(result).append(this.countText).toString(), leftEdge, textBottomY, this.textPaint);
                return;
            }
            Collections.addAll(this.wordsStack, split);
            this.lineBuilder.append((String) this.wordsStack.remove(0));
            if (this.wordsStack.size() > 1) {
                int i = 0;
                while (true) {
                    if (i >= split.length - 1) {
                        break;
                    }
                    if (this.textPaint.measureText(this.lineBuilder.toString() + " " + ((String) this.wordsStack.get(0))) > lineWidth) {
                        break;
                    }
                    this.lineBuilder.append(" ").append((String) this.wordsStack.remove(0));
                    if (this.wordsStack.size() == 1) {
                        break;
                    }
                    i++;
                }
            }
            canvas.drawText(this.lineBuilder.toString(), leftEdge, textBottomY - lineHeight, this.textPaint);
            this.lineBuilder.setLength(0);
            this.lineBuilder.append((String) this.wordsStack.remove(0));
            Iterator i$ = this.wordsStack.iterator();
            while (i$.hasNext()) {
                String word = (String) i$.next();
                this.lineBuilder.append(" ").append(word);
            }
            stringBuilder2 = this.lineBuilder;
            CharSequence secondLine = TextUtils.ellipsize(stringBuilder.toString(), this.textPaint, lineWidth - countTextWidth, TruncateAt.END);
            stringBuilder = new StringBuilder();
            canvas.drawText(stringBuilder2.append(secondLine).append(this.countText).toString(), leftEdge, textBottomY, this.textPaint);
            this.wordsStack.clear();
            this.lineBuilder.setLength(0);
        }
    }

    protected void drawLock(Canvas canvas) {
        int left = getPaddingLeft() + this.padding;
        int top = getPaddingTop() + this.padding;
        this.lockDrawable.setBounds(left, top, left + this.lockDrawable.getIntrinsicWidth(), top + this.lockDrawable.getIntrinsicHeight());
        this.lockDrawable.draw(canvas);
    }

    private int getContentWidth() {
        return (getMeasuredWidth() - getPaddingRight()) - getPaddingLeft();
    }

    private int getContentHeight() {
        return (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
    }

    public void setAlbumCoverUri(Uri albumCoverUri, boolean animateIfNew) {
        DraweeHolder<GenericDraweeHierarchy> holder = getHolder();
        if (holder == null) {
            holder = DraweeHolder.create(this.hierarchy, getContext());
        }
        holder.setController(((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setOldController(holder.getController())).setUri(albumCoverUri).build());
        setHolder(holder);
        ((GenericDraweeHierarchy) holder.getHierarchy()).setActualImageScaleType(ScaleType.CENTER_CROP);
        setAlbumCover(holder.getTopLevelDrawable(), animateIfNew);
    }
}
