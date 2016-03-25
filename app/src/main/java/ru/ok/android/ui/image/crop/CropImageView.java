package ru.ok.android.ui.image.crop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.Logger;

@SuppressLint({"Instantiatable"})
/* compiled from: CropImageActivity */
class CropImageView extends ImageViewTouchBase {
    ArrayList<HighlightView> mHighlightViews;
    float mLastX;
    float mLastY;
    int mMotionEdge;
    HighlightView mMotionHighlightView;

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mBitmapDisplayed.getBitmap() != null) {
            Iterator i$ = this.mHighlightViews.iterator();
            while (i$.hasNext()) {
                HighlightView hv = (HighlightView) i$.next();
                hv.mMatrix.set(getImageMatrix());
                hv.invalidate();
                if (hv.mIsFocused) {
                    centerBasedOnHighlightView(hv);
                }
            }
        }
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHighlightViews = new ArrayList();
        this.mMotionHighlightView = null;
        if (VERSION.SDK_INT >= 11 && VERSION.SDK_INT < 18) {
            setLayerType(1, null);
        }
    }

    protected void postTranslate(float deltaX, float deltaY) {
        super.postTranslate(deltaX, deltaY);
        for (int i = 0; i < this.mHighlightViews.size(); i++) {
            HighlightView hv = (HighlightView) this.mHighlightViews.get(i);
            hv.mMatrix.postTranslate(deltaX, deltaY);
            hv.invalidate();
        }
    }

    private void recomputeFocus(MotionEvent event) {
        int i;
        for (i = 0; i < this.mHighlightViews.size(); i++) {
            HighlightView hv = (HighlightView) this.mHighlightViews.get(i);
            hv.setFocus(false);
            hv.invalidate();
        }
        for (i = 0; i < this.mHighlightViews.size(); i++) {
            hv = (HighlightView) this.mHighlightViews.get(i);
            if (hv.getHit(event.getX(), event.getY()) != 1) {
                if (!hv.hasFocus()) {
                    hv.setFocus(true);
                    hv.invalidate();
                }
                invalidate();
            }
        }
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        CropImageActivity cropImage = (CropImageActivity) getContext();
        if (cropImage.mSaving) {
            return false;
        }
        int i;
        HighlightView hv;
        switch (event.getAction()) {
            case RECEIVED_VALUE:
                if (!cropImage.mWaitingToPick) {
                    for (i = 0; i < this.mHighlightViews.size(); i++) {
                        hv = (HighlightView) this.mHighlightViews.get(i);
                        int edge = hv.getHit(event.getX(), event.getY());
                        if (edge != 1) {
                            this.mMotionEdge = edge;
                            this.mMotionHighlightView = hv;
                            this.mLastX = event.getX();
                            this.mLastY = event.getY();
                            this.mMotionHighlightView.setMode(edge == 32 ? ModifyMode.Move : ModifyMode.Grow);
                            break;
                        }
                    }
                    break;
                }
                recomputeFocus(event);
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (cropImage.mWaitingToPick) {
                    for (i = 0; i < this.mHighlightViews.size(); i++) {
                        hv = (HighlightView) this.mHighlightViews.get(i);
                        if (hv.hasFocus()) {
                            cropImage.mCrop = hv;
                            for (int j = 0; j < this.mHighlightViews.size(); j++) {
                                if (j != i) {
                                    ((HighlightView) this.mHighlightViews.get(j)).setHidden(true);
                                }
                            }
                            centerBasedOnHighlightView(hv);
                            ((CropImageActivity) getContext()).mWaitingToPick = false;
                            return true;
                        }
                    }
                } else if (this.mMotionHighlightView != null) {
                    centerBasedOnHighlightView(this.mMotionHighlightView);
                    this.mMotionHighlightView.setMode(ModifyMode.None);
                }
                this.mMotionHighlightView = null;
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (!cropImage.mWaitingToPick) {
                    if (this.mMotionHighlightView != null) {
                        this.mMotionHighlightView.handleMotion(this.mMotionEdge, event.getX() - this.mLastX, event.getY() - this.mLastY);
                        this.mLastX = event.getX();
                        this.mLastY = event.getY();
                        ensureVisible(this.mMotionHighlightView);
                        break;
                    }
                }
                recomputeFocus(event);
                break;
                break;
        }
        switch (event.getAction()) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                center(true, true);
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (getScale() == 1.0f) {
                    center(true, true);
                    break;
                }
                break;
        }
        return true;
    }

    private void ensureVisible(HighlightView hv) {
        int panDeltaX;
        int panDeltaY;
        Rect r = hv.mDrawRect;
        int panDeltaX1 = Math.max(0, getLeft() - r.left);
        int panDeltaX2 = Math.min(0, getRight() - r.right);
        int panDeltaY1 = Math.max(0, getTop() - r.top);
        int panDeltaY2 = Math.min(0, getBottom() - r.bottom);
        if (panDeltaX1 != 0) {
            panDeltaX = panDeltaX1;
        } else {
            panDeltaX = panDeltaX2;
        }
        if (panDeltaY1 != 0) {
            panDeltaY = panDeltaY1;
        } else {
            panDeltaY = panDeltaY2;
        }
        if (panDeltaX != 0 || panDeltaY != 0) {
            panBy((float) panDeltaX, (float) panDeltaY);
        }
    }

    private void centerBasedOnHighlightView(HighlightView hv) {
    }

    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
            for (int i = 0; i < this.mHighlightViews.size(); i++) {
                ((HighlightView) this.mHighlightViews.get(i)).draw(canvas);
            }
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }

    public void add(HighlightView hv) {
        this.mHighlightViews.add(hv);
        invalidate();
    }
}
