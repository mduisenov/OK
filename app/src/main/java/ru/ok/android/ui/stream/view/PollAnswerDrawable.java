package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.stream.mediatopic.PollAnswerState;

public final class PollAnswerDrawable extends Drawable {
    private final RectF boundsF;
    private final Paint paintError;
    private final Paint paintProgress;
    private PollAnswerState state;
    private final float strokeWidth;

    /* renamed from: ru.ok.android.ui.stream.view.PollAnswerDrawable.1 */
    static /* synthetic */ class C12641 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$ui$stream$mediatopic$PollAnswerState;

        static {
            $SwitchMap$ru$ok$android$ui$stream$mediatopic$PollAnswerState = new int[PollAnswerState.values().length];
            try {
                $SwitchMap$ru$ok$android$ui$stream$mediatopic$PollAnswerState[PollAnswerState.VOTING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$stream$mediatopic$PollAnswerState[PollAnswerState.ERROR.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public PollAnswerDrawable(Context context) {
        this.boundsF = new RectF();
        this.state = PollAnswerState.EMPTY;
        this.strokeWidth = TypedValue.applyDimension(1, 1.5f, context.getResources().getDisplayMetrics());
        this.paintProgress = createPaint(context, 2131493089, 1.0f);
        this.paintError = createPaint(context, 2131493136, 0.8f);
    }

    private Paint createPaint(Context context, int colorResource, float widthCoeff) {
        Paint result = new Paint();
        result.setAntiAlias(true);
        result.setStyle(Style.STROKE);
        result.setColor(context.getResources().getColor(colorResource));
        result.setStrokeWidth(this.strokeWidth * widthCoeff);
        return result;
    }

    public void draw(Canvas canvas) {
        switch (C12641.$SwitchMap$ru$ok$android$ui$stream$mediatopic$PollAnswerState[this.state.ordinal()]) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                canvas.drawCircle(this.boundsF.centerX(), this.boundsF.centerY(), this.boundsF.width() / 2.0f, this.paintError);
            default:
        }
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        this.boundsF.set((float) left, (float) top, (float) right, (float) bottom);
        this.boundsF.inset(this.strokeWidth, this.strokeWidth);
    }

    public int getOpacity() {
        return -2;
    }

    public void setAnswerState(PollAnswerState state) {
        this.state = state;
        invalidateSelf();
    }
}
