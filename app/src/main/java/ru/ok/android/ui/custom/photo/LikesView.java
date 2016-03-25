package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.Locale;
import ru.ok.android.C0206R;
import ru.ok.android.ui.custom.AnimatedScaleDrawable;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.stream.LikeInfoContext;

public class LikesView extends LinearLayout {
    private final Drawable bulletDrawable;
    private final int bulletDrawablePadding;
    private final int bulletPadding;
    private final int klassCountPaddingRight;
    private final AnimatedScaleDrawable klassDrawable;
    private final int klassDrawablePadding;
    private final int klassPadding;
    protected LikeInfoContext likeInfo;
    protected int likesCount;
    private final NumberFormat numberFormat;
    protected OnLikesActionListener onLikesActionListener;
    public final TextView textKlass;
    public final TextView textKlassCount;
    protected boolean userCanLike;
    protected boolean userCanUnLike;
    protected boolean userLiked;

    public interface OnLikesActionListener {
        void onLikeClicked(View view, LikeInfoContext likeInfoContext);

        void onLikesCountClicked(View view, LikeInfoContext likeInfoContext);

        void onUnlikeClicked(View view, LikeInfoContext likeInfoContext);
    }

    /* renamed from: ru.ok.android.ui.custom.photo.LikesView.1 */
    class C07081 implements OnClickListener {
        C07081() {
        }

        public void onClick(View view) {
            LikesView.this.onLikeUnlikeCliked(view);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.LikesView.2 */
    class C07092 implements OnClickListener {
        C07092() {
        }

        public void onClick(View view) {
            if (LikesView.this.onLikesActionListener != null) {
                LikesView.this.onLikesActionListener.onLikesCountClicked(view, LikesView.this.likeInfo);
            }
        }
    }

    public LikesView(Context context) {
        this(context, null);
    }

    public LikesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikesView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LikesView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.numberFormat = NumberFormat.getIntegerInstance(Locale.FRENCH);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.LikesView, defStyleAttr, defStyleRes);
        int klassIconResId = a.getResourceId(0, 2130838047);
        int actionBgResId = a.getResourceId(1, 2130837913);
        int layoutResId = a.getResourceId(2, 2130903271);
        a.recycle();
        LocalizationManager.inflate(context, layoutResId, (ViewGroup) this, true);
        this.textKlassCount = (TextView) findViewById(2131625007);
        this.textKlass = (TextView) findViewById(2131625008);
        this.bulletDrawable = this.textKlass.getCompoundDrawables()[0];
        this.bulletPadding = this.textKlass.getPaddingLeft();
        this.bulletDrawablePadding = this.textKlass.getCompoundDrawablePadding();
        this.textKlassCount.setCompoundDrawablesWithIntrinsicBounds(new AnimatedScaleDrawable(getResources().getDrawable(klassIconResId), this.textKlassCount), null, null, null);
        this.klassDrawable = (AnimatedScaleDrawable) this.textKlassCount.getCompoundDrawables()[0];
        this.klassPadding = this.textKlassCount.getPaddingLeft();
        this.klassDrawablePadding = this.textKlassCount.getCompoundDrawablePadding();
        this.klassCountPaddingRight = this.textKlassCount.getPaddingRight();
        setBackgroundResource(actionBgResId);
        this.textKlass.setOnClickListener(new C07081());
        this.textKlassCount.setOnClickListener(new C07092());
        updateAppearance();
    }

    protected void onLikeUnlikeCliked(View view) {
        if (this.onLikesActionListener == null) {
            return;
        }
        if (this.userLiked) {
            this.onLikesActionListener.onUnlikeClicked(view, this.likeInfo);
        } else {
            this.onLikesActionListener.onLikeClicked(view, this.likeInfo);
        }
    }

    public void setLikeInfo(@Nullable LikeInfoContext likeInfo, boolean animateOwnLikeChange) {
        this.likeInfo = likeInfo;
        if (likeInfo == null) {
            setVisibility(8);
            return;
        }
        setVisibility(0);
        setLikeInfo(likeInfo.count, likeInfo.self, likeInfo.likePossible, likeInfo.unlikePossible, animateOwnLikeChange);
    }

    private void setLikeInfo(int count, boolean userLiked, boolean likePossible, boolean unlikePossible, boolean animateOwnLikeChange) {
        boolean needUpdateAppearance = false;
        if (this.likesCount != count) {
            this.likesCount = count;
            needUpdateAppearance = true;
        }
        boolean userLikedChange = this.userLiked != userLiked;
        if (userLikedChange) {
            this.userLiked = userLiked;
            needUpdateAppearance = true;
        }
        if (this.userCanLike != likePossible) {
            this.userCanLike = likePossible;
            needUpdateAppearance = true;
        }
        if (this.userCanUnLike != unlikePossible) {
            this.userCanUnLike = unlikePossible;
            needUpdateAppearance = true;
        }
        if (needUpdateAppearance) {
            updateAppearance();
        }
        if (userLikedChange && animateOwnLikeChange) {
            animateUserLike();
        }
    }

    private void animateUserLike() {
        this.klassDrawable.start();
    }

    private void updateAppearance() {
        boolean viewerLiked = this.userLiked;
        boolean viewerCanLike = this.userCanLike;
        boolean viewerCanUnlike = this.userCanUnLike;
        int displayLikesCount = this.likesCount;
        boolean textKlassCountVisible = false;
        boolean textKlassVisible = false;
        if (displayLikesCount > 0) {
            this.textKlassCount.setText(this.numberFormat.format((long) displayLikesCount));
            this.textKlassCount.setActivated(viewerLiked);
            textKlassCountVisible = true;
        } else if (this.textKlassCount.getVisibility() == 0) {
            this.textKlassCount.setText(null);
            this.textKlassCount.setActivated(false);
        }
        if (viewerCanLike || viewerCanUnlike) {
            Drawable leftDrawable;
            int leftPadding;
            int drawablePadding;
            this.textKlass.setActivated(viewerLiked);
            if (textKlassCountVisible) {
                leftDrawable = this.bulletDrawable;
                leftPadding = this.bulletPadding;
                drawablePadding = this.bulletDrawablePadding;
            } else {
                leftDrawable = this.klassDrawable;
                leftPadding = this.klassPadding;
                drawablePadding = this.klassDrawablePadding;
            }
            this.textKlass.setCompoundDrawables(leftDrawable, null, null, null);
            this.textKlass.setPadding(leftPadding, this.textKlass.getPaddingTop(), this.textKlass.getPaddingRight(), this.textKlass.getPaddingBottom());
            this.textKlass.setCompoundDrawablePadding(drawablePadding);
            textKlassVisible = true;
        }
        if (textKlassCountVisible) {
            this.textKlassCount.setPadding(this.textKlassCount.getPaddingLeft(), this.textKlassCount.getPaddingTop(), textKlassVisible ? this.bulletDrawablePadding : this.klassCountPaddingRight, this.textKlassCount.getPaddingBottom());
        }
        this.textKlassCount.setVisibility(textKlassCountVisible ? 0 : 8);
        this.textKlass.setVisibility(textKlassVisible ? 0 : 8);
    }

    public void setOnLikesActionListener(OnLikesActionListener onLikesActionListener) {
        this.onLikesActionListener = onLikesActionListener;
    }

    public LikeInfoContext getLikeInfo() {
        return this.likeInfo;
    }
}
