package ru.ok.android.ui.custom.text;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.EditText;

public class LineEditText extends EditText {
    private Runnable changeCursorRunnable;
    private Paint cursorPaint;
    private Path cursorPath;
    private RectF cursorRect;
    private boolean drawCursor;
    private Handler handler;
    private Paint linePaint;

    /* renamed from: ru.ok.android.ui.custom.text.LineEditText.1 */
    class C07531 implements Runnable {
        C07531() {
        }

        public void run() {
            LineEditText.this.drawCursor = !LineEditText.this.drawCursor;
            LineEditText.this.invalidate();
            LineEditText.this.handler.postDelayed(this, 500);
        }
    }

    public LineEditText(Context context) {
        super(context);
        this.changeCursorRunnable = new C07531();
        init();
    }

    public LineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.changeCursorRunnable = new C07531();
        init();
    }

    public LineEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.changeCursorRunnable = new C07531();
        init();
    }

    private void init() {
        this.linePaint = new Paint();
        this.linePaint.setColor(-3355444);
        this.linePaint.setStrokeWidth((float) dpToPx(1));
        this.linePaint.setAntiAlias(true);
        this.cursorPaint = new Paint();
        this.cursorPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.cursorPaint.setStrokeWidth((float) dpToPx(1));
        this.cursorPaint.setAntiAlias(true);
        this.cursorPath = new Path();
        this.cursorRect = new RectF();
        this.handler = new Handler();
        setGravity(49);
        setCursorVisible(false);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float step = (float) getLineHeight();
        for (float i = ((float) getLineHeight()) * 0.62f; i <= ((float) canvas.getHeight()); i += step) {
            canvas.drawLine(0.0f, i, (float) canvas.getWidth(), i, this.linePaint);
        }
        if (this.drawCursor) {
            this.cursorPath.reset();
            getLayout().getCursorPath(getSelectionStart(), this.cursorPath, getText());
            this.cursorPath.computeBounds(this.cursorRect, true);
            RectF rectF = this.cursorRect;
            rectF.left += (float) getPaddingLeft();
            this.cursorRect.right = this.cursorRect.left + this.cursorPaint.getStrokeWidth();
            this.cursorRect.top = (float) (getLayout().getLineForOffset(getSelectionStart()) * getLineHeight());
            this.cursorRect.bottom = this.cursorRect.top + (getTextSize() * 1.3f);
            canvas.drawRect(this.cursorRect, this.cursorPaint);
        }
    }

    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (needLineSpacingHack() && !TextUtils.isEmpty(text)) {
            ((Spannable) text).setSpan(new StyleSpan(0), 0, text.length(), 33);
        }
        if (!TextUtils.isEmpty(text)) {
            this.drawCursor = true;
            this.handler.removeCallbacksAndMessages(null);
            this.handler.postDelayed(this.changeCursorRunnable, 500);
        }
    }

    @TargetApi(16)
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (needLineSpacingHack()) {
            setMeasuredDimension(getMeasuredWidth(), Math.max(getLineHeight() * getLineCount(), getLineHeight() * getMinLines()));
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.handler.postDelayed(this.changeCursorRunnable, 500);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.handler.removeCallbacksAndMessages(null);
    }

    private boolean needLineSpacingHack() {
        return VERSION.SDK_INT >= 21;
    }

    private int dpToPx(int dp) {
        return Math.max((int) (getContext().getResources().getDisplayMetrics().density * ((float) dp)), 1);
    }
}
