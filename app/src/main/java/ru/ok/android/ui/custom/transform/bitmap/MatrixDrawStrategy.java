package ru.ok.android.ui.custom.transform.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView.ScaleType;
import ru.ok.android.proto.MessagesProto.Message;

final class MatrixDrawStrategy implements BitmapDrawStrategy {
    private boolean isTopCrop;
    private final Matrix matrix;

    /* renamed from: ru.ok.android.ui.custom.transform.bitmap.MatrixDrawStrategy.1 */
    static /* synthetic */ class C07611 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType;

        static {
            $SwitchMap$android$widget$ImageView$ScaleType = new int[ScaleType.values().length];
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER_CROP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER_INSIDE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    MatrixDrawStrategy() {
        this.matrix = new Matrix();
    }

    public void draw(Canvas canvas, Bitmap bitmap, Rect drawRect, ScaleType scaleType, Paint paint) {
        recalculateBitmapMatrix(bitmap, scaleType, drawRect);
        canvas.drawBitmap(bitmap, this.matrix, paint);
    }

    public void setIsTopCrop(boolean isTopCrop) {
        this.isTopCrop = isTopCrop;
    }

    private void recalculateBitmapMatrix(Bitmap bitmap, ScaleType scaleType, Rect drawRect) {
        if (bitmap != null && !bitmap.isRecycled()) {
            int bWidth = bitmap.getWidth();
            int bHeight = bitmap.getHeight();
            int rWidth = drawRect.width();
            int rHeight = drawRect.height();
            if (bWidth != 0 && bHeight != 0 && rWidth != 0 && rHeight != 0) {
                this.matrix.reset();
                switch (C07611.$SwitchMap$android$widget$ImageView$ScaleType[scaleType.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        recalculateForCenter(bWidth, bHeight, rWidth, rHeight);
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        recalculateForCenterCrop(drawRect, bWidth, bHeight, rWidth, rHeight);
                    case Message.TYPE_FIELD_NUMBER /*3*/:
                        recalculateForCenterInside(bWidth, bHeight, rWidth, rHeight);
                    default:
                }
            }
        }
    }

    private void recalculateForCenter(int bWidth, int bHeight, int rWidth, int rHeight) {
        this.matrix.postTranslate(-(((float) (bWidth - rWidth)) * 0.5f), -(((float) (bHeight - rHeight)) * 0.5f));
    }

    private void recalculateForCenterCrop(Rect drawRect, int bWidth, int bHeight, int rWidth, int rHeight) {
        float scale = Math.max(((float) rWidth) / ((float) bWidth), ((float) rHeight) / ((float) bHeight));
        this.matrix.preScale(scale, scale);
        this.matrix.postTranslate(((float) drawRect.left) + ((((float) rWidth) - (((float) bWidth) * scale)) * 0.5f), ((float) drawRect.top) + (this.isTopCrop ? 0.0f : (((float) rHeight) - (((float) bHeight) * scale)) * 0.5f));
    }

    private void recalculateForCenterInside(int bWidth, int bHeight, int rWidth, int rHeight) {
        float scale = Math.min(((float) rWidth) / ((float) bWidth), ((float) rHeight) / ((float) bHeight));
        this.matrix.preScale(scale, scale);
        this.matrix.postTranslate((((float) rWidth) - (((float) bWidth) * scale)) * 0.5f, (((float) rHeight) - (((float) bHeight) * scale)) * 0.5f);
    }
}
