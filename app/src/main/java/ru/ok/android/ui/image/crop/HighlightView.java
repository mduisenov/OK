package ru.ok.android.ui.image.crop;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.view.View;

class HighlightView {
    private Drawable cropArea;
    private boolean mCircle;
    View mContext;
    RectF mCropRect;
    Rect mDrawRect;
    private final Paint mFocusPaint;
    boolean mHidden;
    private RectF mImageRect;
    private float mInitialAspectRatio;
    boolean mIsFocused;
    private boolean mMaintainAspectRatio;
    Matrix mMatrix;
    private ModifyMode mMode;
    private final Paint mNoFocusPaint;
    private final Paint mOutlinePaint;

    enum ModifyMode {
        None,
        Move,
        Grow
    }

    public HighlightView(View ctx) {
        this.mMode = ModifyMode.None;
        this.mMaintainAspectRatio = false;
        this.mCircle = false;
        this.mFocusPaint = new Paint();
        this.mNoFocusPaint = new Paint();
        this.mOutlinePaint = new Paint();
        this.mContext = ctx;
    }

    private void init() {
        Resources resources = this.mContext.getResources();
    }

    public boolean hasFocus() {
        return this.mIsFocused;
    }

    public void setFocus(boolean f) {
        this.mIsFocused = f;
    }

    public void setHidden(boolean hidden) {
        this.mHidden = hidden;
    }

    protected void draw(Canvas canvas) {
        if (!this.mHidden) {
            canvas.save();
            Path path = new Path();
            if (hasFocus()) {
                Rect viewDrawingRect = new Rect();
                this.mContext.getDrawingRect(viewDrawingRect);
                if (this.mCircle) {
                    float width = (float) this.mDrawRect.width();
                    path.addCircle(((float) this.mDrawRect.left) + (width / 2.0f), ((float) this.mDrawRect.top) + (((float) this.mDrawRect.height()) / 2.0f), width / 2.0f, Direction.CW);
                    this.mOutlinePaint.setColor(-1112874);
                } else {
                    path.addRect(new RectF(this.mDrawRect), Direction.CW);
                    this.mOutlinePaint.setColor(-1);
                }
                canvas.clipPath(path, Op.DIFFERENCE);
                canvas.drawRect(viewDrawingRect, hasFocus() ? this.mFocusPaint : this.mNoFocusPaint);
                canvas.restore();
                if (this.mCircle) {
                    canvas.drawPath(path, this.mOutlinePaint);
                } else {
                    this.cropArea.setBounds(this.mDrawRect.left, this.mDrawRect.top, this.mDrawRect.right, this.mDrawRect.bottom);
                    this.cropArea.draw(canvas);
                }
                if (this.mMode != ModifyMode.Grow || !this.mCircle) {
                    return;
                }
                return;
            }
            this.mOutlinePaint.setColor(ViewCompat.MEASURED_STATE_MASK);
            canvas.drawRect(this.mDrawRect, this.mOutlinePaint);
        }
    }

    public void setMode(ModifyMode mode) {
        if (mode != this.mMode) {
            this.mMode = mode;
            this.mContext.invalidate();
        }
    }

    public int getHit(float x, float y) {
        Rect r = computeLayout();
        int retval = 1;
        if (this.mCircle) {
            float distX = x - ((float) r.centerX());
            float distY = y - ((float) r.centerY());
            int distanceFromCenter = (int) Math.sqrt((double) ((distX * distX) + (distY * distY)));
            int radius = this.mDrawRect.width() / 2;
            if (((float) Math.abs(distanceFromCenter - radius)) <= 25.0f) {
                if (Math.abs(distY) > Math.abs(distX)) {
                    if (distY < 0.0f) {
                        return 8;
                    }
                    return 16;
                } else if (distX < 0.0f) {
                    return 2;
                } else {
                    return 4;
                }
            } else if (distanceFromCenter < radius) {
                return 32;
            } else {
                return 1;
            }
        }
        boolean leftCheck = x >= ((float) r.left) - 25.0f && x <= ((float) r.left) + 25.0f;
        boolean topCheck = y >= ((float) r.top) - 25.0f && y <= ((float) r.top) + 25.0f;
        boolean rightCheck = x <= ((float) r.right) + 25.0f && x >= ((float) r.right) - 25.0f;
        boolean bottomCheck = y <= ((float) r.bottom) + 25.0f && y >= ((float) r.bottom) - 25.0f;
        if (Math.abs(((float) r.left) - x) < 25.0f && leftCheck) {
            retval = 1 | 2;
        }
        if (Math.abs(((float) r.right) - x) < 25.0f && rightCheck) {
            retval |= 4;
        }
        if (Math.abs(((float) r.top) - y) < 25.0f && topCheck) {
            retval |= 8;
        }
        if (Math.abs(((float) r.bottom) - y) < 25.0f && bottomCheck) {
            retval |= 16;
        }
        if (retval == 1 && r.contains((int) x, (int) y)) {
            return 32;
        }
        return retval;
    }

    void handleMotion(int edge, float dx, float dy) {
        float growBottom = 0.0f;
        Rect r = computeLayout();
        if (edge != 1) {
            if (edge == 32) {
                moveBy((this.mCropRect.width() / ((float) r.width())) * dx, (this.mCropRect.height() / ((float) r.height())) * dy);
                return;
            }
            float growLeft;
            float growTop;
            float growRight;
            if ((edge & 6) == 0) {
                dx = 0.0f;
            }
            if ((edge & 24) == 0) {
                dy = 0.0f;
            }
            float xDelta = dx * (this.mCropRect.width() / ((float) r.width()));
            float yDelta = dy * (this.mCropRect.height() / ((float) r.height()));
            if ((edge & 2) == 0) {
                growLeft = 0.0f;
            } else {
                growLeft = xDelta;
            }
            if ((edge & 8) == 0) {
                growTop = 0.0f;
            } else {
                growTop = yDelta;
            }
            if ((edge & 4) == 0) {
                growRight = 0.0f;
            } else {
                growRight = xDelta;
            }
            if ((edge & 16) != 0) {
                growBottom = yDelta;
            }
            growBy(growLeft, growTop, growRight, growBottom);
        }
    }

    void moveBy(float dx, float dy) {
        Rect invalRect = new Rect(this.mDrawRect);
        this.mCropRect.offset(dx, dy);
        this.mCropRect.offset(Math.max(0.0f, this.mImageRect.left - this.mCropRect.left), Math.max(0.0f, this.mImageRect.top - this.mCropRect.top));
        this.mCropRect.offset(Math.min(0.0f, this.mImageRect.right - this.mCropRect.right), Math.min(0.0f, this.mImageRect.bottom - this.mCropRect.bottom));
        this.mDrawRect = computeLayout();
        invalRect.union(this.mDrawRect);
        invalRect.inset(-10, -10);
        this.mContext.invalidate(invalRect);
    }

    void growBy(float growLeft, float growTop, float growRight, float growBottom) {
        float heightCap = 90.0f;
        RectF r = new RectF(this.mCropRect);
        r.left = Math.max(this.mImageRect.left, r.left + growLeft);
        r.top = Math.max(this.mImageRect.top, r.top + growTop);
        r.right = Math.min(this.mImageRect.right, r.right + growRight);
        r.bottom = Math.min(this.mImageRect.bottom, r.bottom + growBottom);
        if (r.width() < 90.0f) {
            r.inset((-(90.0f - r.width())) / 2.0f, 0.0f);
        }
        if (this.mMaintainAspectRatio) {
            heightCap = 90.0f / this.mInitialAspectRatio;
        }
        if (r.height() < heightCap) {
            r.inset(0.0f, (-(heightCap - r.height())) / 2.0f);
        }
        if (r.left < this.mImageRect.left) {
            r.offset(this.mImageRect.left - r.left, 0.0f);
        } else if (r.right > this.mImageRect.right) {
            r.offset(-(r.right - this.mImageRect.right), 0.0f);
        }
        if (r.top < this.mImageRect.top) {
            r.offset(0.0f, this.mImageRect.top - r.top);
        } else if (r.bottom > this.mImageRect.bottom) {
            r.offset(0.0f, -(r.bottom - this.mImageRect.bottom));
        }
        this.mCropRect.set(r);
        this.mDrawRect = computeLayout();
        this.mContext.invalidate();
    }

    public Rect getCropRect() {
        return new Rect((int) this.mCropRect.left, (int) this.mCropRect.top, (int) this.mCropRect.right, (int) this.mCropRect.bottom);
    }

    private Rect computeLayout() {
        RectF r = new RectF(this.mCropRect.left, this.mCropRect.top, this.mCropRect.right, this.mCropRect.bottom);
        this.mMatrix.mapRect(r);
        return new Rect(Math.round(r.left), Math.round(r.top), Math.round(r.right), Math.round(r.bottom));
    }

    public void invalidate() {
        this.mDrawRect = computeLayout();
    }

    public void setup(Matrix m, Rect imageRect, RectF cropRect, boolean circle, boolean maintainAspectRatio) {
        if (circle) {
            maintainAspectRatio = true;
        }
        this.mMatrix = new Matrix(m);
        this.mCropRect = cropRect;
        this.mImageRect = new RectF(imageRect);
        this.mMaintainAspectRatio = maintainAspectRatio;
        this.mCircle = circle;
        this.mInitialAspectRatio = this.mCropRect.width() / this.mCropRect.height();
        this.mDrawRect = computeLayout();
        this.mFocusPaint.setARGB(125, 50, 50, 50);
        this.mNoFocusPaint.setARGB(125, 50, 50, 50);
        this.mOutlinePaint.setStrokeWidth(1.0f);
        this.mOutlinePaint.setStyle(Style.STROKE);
        this.mOutlinePaint.setAntiAlias(true);
        this.mMode = ModifyMode.None;
        this.cropArea = this.mContext.getResources().getDrawable(2130837828);
        init();
    }
}
