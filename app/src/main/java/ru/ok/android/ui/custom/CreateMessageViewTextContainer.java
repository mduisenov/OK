package ru.ok.android.ui.custom;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.EditText;
import android.widget.ViewAnimator;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.utils.Utils;

public final class CreateMessageViewTextContainer extends ViewGroup {
    private ViewAnimator actionAnimator;
    private View addSmileCheckbox;
    private View asAdmin;
    private View attachPhotoButton;
    private View attachVideoButton;
    private View audioPlayer;
    private View circle;
    private int circleRadius;
    private int dp100;
    private EditText newMessageText;

    public CreateMessageViewTextContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.dp100 = (int) Utils.dipToPixels(context, 100.0f);
        this.circleRadius = (int) Utils.dipToPixels(context, 12.0f);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.addSmileCheckbox = findViewById(2131624539);
        this.asAdmin = findViewById(2131624732);
        this.actionAnimator = (ViewAnimator) findViewById(2131624733);
        this.newMessageText = (EditText) findViewById(2131624738);
        this.audioPlayer = findViewById(2131624624);
        this.attachPhotoButton = findViewById(2131624737);
        this.attachVideoButton = findViewById(2131624736);
        this.circle = findViewById(C0263R.id.circle);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.audioPlayer == null) {
            this.audioPlayer = findViewById(2131624624);
        }
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeightMax = MeasureSpec.getSize(heightMeasureSpec);
        int wrapContentSpec = MeasureSpec.makeMeasureSpec(0, 0);
        this.addSmileCheckbox.measure(wrapContentSpec, wrapContentSpec);
        if (this.asAdmin.getVisibility() == 0) {
            this.asAdmin.measure(wrapContentSpec, wrapContentSpec);
        }
        this.actionAnimator.measure(wrapContentSpec, wrapContentSpec);
        int photoAttachButtonAllWidth = 0;
        if (this.attachPhotoButton.getVisibility() != 8) {
            measureChild(this.attachPhotoButton, widthMeasureSpec, heightMeasureSpec);
            photoAttachButtonAllWidth = this.attachPhotoButton.getMeasuredWidth();
        }
        int videoAttachButtonAllWidth = 0;
        if (this.attachVideoButton.getVisibility() != 8) {
            measureChild(this.attachVideoButton, widthMeasureSpec, heightMeasureSpec);
            videoAttachButtonAllWidth = this.attachVideoButton.getMeasuredWidth();
        }
        int newMessageTextWidth = ((width - totalWidth(this.asAdmin)) - totalWidth(this.actionAnimator)) - (getPaddingLeft() + getPaddingRight());
        int audioPlayerWidth = newMessageTextWidth;
        if (this.audioPlayer != null) {
            audioPlayerWidth -= getMarginWidth(this.audioPlayer);
        }
        this.newMessageText.measure(MeasureSpec.makeMeasureSpec(((newMessageTextWidth - photoAttachButtonAllWidth) - videoAttachButtonAllWidth) - totalWidth(this.addSmileCheckbox), 1073741824), MeasureSpec.makeMeasureSpec(Math.min(Math.max((parentHeightMax * 2) / 3, this.dp100), parentHeightMax), LinearLayoutManager.INVALID_OFFSET));
        int height = Math.max(this.newMessageText.getMeasuredHeight(), Math.max(this.addSmileCheckbox.getMeasuredHeight(), this.actionAnimator.getMeasuredHeight()));
        int heightSpec = MeasureSpec.makeMeasureSpec(height, 1073741824);
        this.addSmileCheckbox.measure(MeasureSpec.makeMeasureSpec(this.addSmileCheckbox.getMeasuredWidth(), 1073741824), heightSpec);
        this.asAdmin.measure(MeasureSpec.makeMeasureSpec(this.asAdmin.getMeasuredWidth(), 1073741824), heightSpec);
        if (this.audioPlayer != null) {
            this.audioPlayer.measure(MeasureSpec.makeMeasureSpec(audioPlayerWidth, 1073741824), heightSpec);
        }
        this.actionAnimator.measure(this.actionAnimator.getMeasuredWidth(), heightSpec);
        if (this.attachPhotoButton.getVisibility() != 8) {
            this.attachPhotoButton.measure(MeasureSpec.makeMeasureSpec(photoAttachButtonAllWidth, 1073741824), heightSpec);
        }
        if (this.attachVideoButton.getVisibility() != 8) {
            this.attachVideoButton.measure(MeasureSpec.makeMeasureSpec(videoAttachButtonAllWidth, 1073741824), heightSpec);
        }
        if (this.circle.getVisibility() == 0) {
            measureChild(this.circle, widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (this.audioPlayer == null) {
            this.audioPlayer = findViewById(2131624624);
        }
        int leftX = getPaddingLeft() + getLeftMargin(this.addSmileCheckbox);
        int y = getCenterY(this.addSmileCheckbox);
        this.addSmileCheckbox.layout(leftX, y, this.addSmileCheckbox.getMeasuredWidth() + leftX, this.addSmileCheckbox.getMeasuredHeight() + y);
        leftX += getRightMargin(this.addSmileCheckbox) + this.addSmileCheckbox.getMeasuredWidth();
        if (this.asAdmin.getVisibility() == 0) {
            leftX += getLeftMargin(this.asAdmin);
            y = getCenterY(this.asAdmin);
            this.asAdmin.layout(leftX, y, this.asAdmin.getMeasuredWidth() + leftX, this.asAdmin.getMeasuredHeight() + y);
            leftX += getRightMargin(this.asAdmin) + this.asAdmin.getMeasuredWidth();
        }
        int rightX = ((getMeasuredWidth() - getPaddingRight()) - getRightMargin(this.actionAnimator)) - this.actionAnimator.getMeasuredWidth();
        y = getCenterY(this.actionAnimator);
        this.actionAnimator.layout(rightX, y, this.actionAnimator.getMeasuredWidth() + rightX, this.actionAnimator.getMeasuredHeight() + y);
        if (this.attachPhotoButton.getVisibility() == 0) {
            rightX -= this.attachPhotoButton.getMeasuredWidth();
            this.attachPhotoButton.layout(rightX, y, this.attachPhotoButton.getMeasuredWidth() + rightX, this.attachPhotoButton.getMeasuredHeight() + y);
        }
        if (this.attachVideoButton.getVisibility() == 0) {
            rightX -= this.attachVideoButton.getMeasuredWidth();
            this.attachVideoButton.layout(rightX, y, this.attachVideoButton.getMeasuredWidth() + rightX, this.attachVideoButton.getMeasuredHeight() + y);
        }
        if (this.audioPlayer != null && this.audioPlayer.getVisibility() == 0) {
            this.audioPlayer.layout(getMarginLeft(this.audioPlayer) + getPaddingLeft(), y, this.audioPlayer.getMeasuredWidth() + getMarginLeft(this.audioPlayer), this.audioPlayer.getMeasuredHeight() + y);
        }
        if (this.circle.getVisibility() == 0) {
            int x = ((this.addSmileCheckbox.getLeft() + this.addSmileCheckbox.getRight()) / 2) + this.circleRadius;
            int circleY = ((this.addSmileCheckbox.getTop() + this.addSmileCheckbox.getBottom()) / 2) - this.circleRadius;
            this.circle.layout(x, circleY, this.circle.getMeasuredWidth() + x, this.circle.getMeasuredHeight() + circleY);
        }
        y = getCenterY(this.newMessageText);
        this.newMessageText.layout(leftX, y, this.newMessageText.getMeasuredWidth() + leftX, this.newMessageText.getMeasuredHeight() + y);
    }

    private static int getLeftMargin(View v) {
        if (v.getVisibility() == 8) {
            return 0;
        }
        LayoutParams lp = v.getLayoutParams();
        if (lp instanceof MarginLayoutParams) {
            return ((MarginLayoutParams) lp).leftMargin;
        }
        return 0;
    }

    private static int getRightMargin(View v) {
        if (v.getVisibility() == 8) {
            return 0;
        }
        LayoutParams lp = v.getLayoutParams();
        if (lp instanceof MarginLayoutParams) {
            return ((MarginLayoutParams) lp).rightMargin;
        }
        return 0;
    }

    private int getCenterY(View view) {
        return (getMeasuredHeight() - view.getMeasuredHeight()) / 2;
    }

    private static int totalWidth(View view) {
        if (view.getVisibility() == 8) {
            return 0;
        }
        return view.getMeasuredWidth() + getMarginWidth(view);
    }

    private static int getMarginWidth(View view) {
        LayoutParams lp = view.getLayoutParams();
        if (!(lp instanceof MarginLayoutParams)) {
            return 0;
        }
        MarginLayoutParams mlp = (MarginLayoutParams) lp;
        return mlp.leftMargin + mlp.rightMargin;
    }

    private static int getMarginLeft(View view) {
        LayoutParams lp = view.getLayoutParams();
        if (lp instanceof MarginLayoutParams) {
            return ((MarginLayoutParams) lp).leftMargin;
        }
        return 0;
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
