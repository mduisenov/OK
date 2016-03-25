package ru.ok.android.ui.custom.photo;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import ru.ok.android.fragments.web.hooks.WebLinksProcessor;
import ru.ok.android.ui.custom.ProgressWheelView;
import ru.ok.android.ui.custom.photo.DragPeekContentView.OnScrollChangeListener;
import ru.ok.android.ui.custom.photo.LikesView.OnLikesActionListener;
import ru.ok.android.ui.custom.photo.PhotoMarksBarView.OnDrawerStateChangeListener;
import ru.ok.android.ui.custom.photo.PhotoMarksBarView.OnMarkSelectedListener;
import ru.ok.android.ui.custom.text.OdklUrlsTextView;
import ru.ok.android.ui.custom.text.OdklUrlsTextView.OnSelectOdklLinkListener;
import ru.ok.android.ui.image.view.DecorHandler;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.stream.LikeInfoContext;

public abstract class AbstractPhotoInfoView extends AbstractPhotoView {
    protected PhotoViewDecorController decorController;
    private ObjectAnimator mAlphaAnimator;
    private Rect mBottomPanelRect;
    protected View mBottomPanelView;
    protected DragPeekContentView mCommentContainerView;
    private final int[] mCommentLocation;
    protected View mCommentShadowView;
    protected OdklUrlsTextView mCommentView;
    protected TextView mCommentsCountView;
    protected View mDraggableContentView;
    protected View mDrawerPushableView;
    protected LikesViewSynced mLikesView;
    protected PhotoMarksBarView mMarksbarView;
    protected OnPhotoActionListener mOnPhotoActionListener;
    protected ProgressWheelView mProgressView;
    protected PhotoInfo photoInfo;
    protected int photoTagSize;
    protected int state;
    private WebLinksProcessor webLinksProcessor;

    public interface OnPhotoActionListener {
        void onCommentsClicked(View view, String str);

        void onLikeClicked(String str, LikeInfoContext likeInfoContext);

        void onLikesCountClicked(View view, String str, LikeInfoContext likeInfoContext);

        void onMark(String str, int i);

        void onUnlikeClicked(String str, LikeInfoContext likeInfoContext);

        void onUserClicked(UserInfo userInfo);
    }

    /* renamed from: ru.ok.android.ui.custom.photo.AbstractPhotoInfoView.1 */
    class C06941 implements OnClickListener {
        C06941() {
        }

        public void onClick(View view) {
            if (AbstractPhotoInfoView.this.mOnPhotoActionListener != null) {
                AbstractPhotoInfoView.this.mOnPhotoActionListener.onCommentsClicked(view, AbstractPhotoInfoView.this.photoInfo.getId());
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.AbstractPhotoInfoView.2 */
    class C06952 implements OnScrollChangeListener {
        C06952() {
        }

        public void onScrollChanged(int l, int t, int oldl, int oldt) {
            if (oldt < 100 && t >= 100) {
                AbstractPhotoInfoView.this.setCommentShadowAlpha(1.0f, true);
            } else if (oldt >= 100 && t < 100) {
                AbstractPhotoInfoView.this.setCommentShadowAlpha(0.0f, true);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.AbstractPhotoInfoView.3 */
    class C06963 implements OnSelectOdklLinkListener {
        C06963() {
        }

        public void onSelectOdklLink(String url) {
            AbstractPhotoInfoView.this.getWebLinksProcessor().processUrl(url);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.AbstractPhotoInfoView.4 */
    class C06974 implements OnDrawerStateChangeListener {
        C06974() {
        }

        public void onDrawerStateChange(int newState) {
            AbstractPhotoInfoView.this.animatePushableView(newState != 0);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.AbstractPhotoInfoView.5 */
    class C06985 implements OnMarkSelectedListener {
        C06985() {
        }

        public void onMarkSelected(int mark) {
            if (AbstractPhotoInfoView.this.mOnPhotoActionListener != null) {
                AbstractPhotoInfoView.this.mOnPhotoActionListener.onMark(AbstractPhotoInfoView.this.photoInfo.getId(), mark);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.AbstractPhotoInfoView.6 */
    class C06996 implements OnLikesActionListener {
        C06996() {
        }

        public void onLikesCountClicked(View view, LikeInfoContext likeInfo) {
            if (AbstractPhotoInfoView.this.mOnPhotoActionListener != null) {
                AbstractPhotoInfoView.this.mOnPhotoActionListener.onLikesCountClicked(AbstractPhotoInfoView.this.mLikesView, AbstractPhotoInfoView.this.photoInfo.getId(), likeInfo);
            }
        }

        public void onUnlikeClicked(View view, LikeInfoContext likeInfo) {
            if (AbstractPhotoInfoView.this.mOnPhotoActionListener != null) {
                AbstractPhotoInfoView.this.mOnPhotoActionListener.onUnlikeClicked(AbstractPhotoInfoView.this.photoInfo.getId(), likeInfo);
            }
        }

        public void onLikeClicked(View view, LikeInfoContext likeInfo) {
            if (AbstractPhotoInfoView.this.mOnPhotoActionListener != null) {
                AbstractPhotoInfoView.this.mOnPhotoActionListener.onLikeClicked(AbstractPhotoInfoView.this.photoInfo.getId(), likeInfo);
            }
        }
    }

    public static class PhotoControlsState {
        public final boolean isCommentsVisible;
        public final boolean isLikesVisible;
        public final boolean isMarkAllowed;

        public PhotoControlsState(boolean isCommentsVisible, boolean isLikesVisible, boolean isMarkAllowed) {
            this.isCommentsVisible = isCommentsVisible;
            this.isLikesVisible = isLikesVisible;
            this.isMarkAllowed = isMarkAllowed;
        }
    }

    protected abstract int getPhotoViewId();

    public AbstractPhotoInfoView(Context context) {
        super(context);
        this.state = 0;
        this.mCommentLocation = new int[2];
        this.mBottomPanelRect = new Rect();
        onCreate();
    }

    protected void onCreate() {
        this.mDraggableContentView = LocalizationManager.inflate(getContext(), getPhotoViewId(), (ViewGroup) this, false);
        addView(this.mDraggableContentView, new LayoutParams(-1, -1));
        this.mProgressView = (ProgressWheelView) findViewById(2131624548);
        initStubView();
        this.mBottomPanelView = findViewById(2131624885);
        this.mLikesView = (LikesViewSynced) findViewById(2131624794);
        this.mCommentsCountView = (TextView) findViewById(2131625220);
        this.mCommentsCountView.setOnClickListener(new C06941());
        this.mCommentContainerView = (DragPeekContentView) findViewById(2131624886);
        this.mCommentContainerView.setOnScrollChangeListener(new C06952());
        this.mCommentView = (OdklUrlsTextView) findViewById(2131624887);
        this.mCommentView.setLinkListener(new C06963());
        this.mMarksbarView = (PhotoMarksBarView) findViewById(2131625221);
        this.mMarksbarView.setOnDrawerStateChangeListener(new C06974());
        this.mMarksbarView.setOnMarkSelectedListener(new C06985());
        this.mLikesView.setOnLikesActionListener(new C06996());
        this.mDrawerPushableView = findViewById(2131625219);
        this.photoTagSize = getResources().getDimensionPixelSize(2131231121);
        this.mCommentShadowView = findViewById(2131624884);
        setCommentShadowAlpha(0.0f, false);
        this.decorController = new PhotoViewDecorController(this.mBottomPanelView, this.mCommentContainerView);
    }

    private WebLinksProcessor getWebLinksProcessor() {
        if (this.webLinksProcessor == null) {
            this.webLinksProcessor = new WebLinksProcessor((Activity) getContext(), false);
        }
        return this.webLinksProcessor;
    }

    protected final void setCommentShadowAlpha(float alpha, boolean animate) {
        if (this.mAlphaAnimator != null && this.mAlphaAnimator.isStarted()) {
            this.mAlphaAnimator.cancel();
        }
        if (animate) {
            this.mAlphaAnimator = ObjectAnimator.ofFloat(this.mCommentShadowView, "alpha", new float[]{alpha});
            this.mAlphaAnimator.setDuration(200);
            this.mAlphaAnimator.start();
            return;
        }
        this.mCommentShadowView.setAlpha(alpha);
    }

    protected final void animatePushableView(boolean offscreen) {
        float from;
        float to = 1.0f;
        if (offscreen) {
            from = 0.0f;
        } else {
            from = 1.0f;
        }
        if (!offscreen) {
            to = 0.0f;
        }
        TranslateAnimation translate = new TranslateAnimation(1, from, 1, to, 0, 0.0f, 0, 0.0f);
        translate.setDuration((long) getResources().getInteger(2131427341));
        translate.setFillAfter(true);
        translate.setInterpolator(new DecelerateInterpolator());
        this.mDrawerPushableView.startAnimation(translate);
    }

    public void updateProgress(int progress) {
        this.mProgressView.setProgress((int) ((3.6d * ((double) progress)) / 100.0d));
    }

    public void setProgress(int progress) {
        this.mProgressView.setSpinProgress(progress);
        this.mProgressView.invalidate();
    }

    public final String getPhotoId() {
        return this.photoInfo.getId();
    }

    public PhotoInfo getPhotoInfo() {
        return this.photoInfo;
    }

    public void setPhotoInfo(PhotoInfo photoInfo) {
        this.photoInfo = photoInfo;
    }

    public void setDecorViewsHandler(DecorHandler decorViewsHandler) {
        super.setDecorViewsHandler(decorViewsHandler);
        this.mDecorViewsHandler.registerDecorComponent(this, this.decorController);
        updateCommentViewState();
    }

    protected final void updateCommentViewState() {
        boolean empty = TextUtils.isEmpty(this.mCommentView.getText());
        this.decorController.setCommentVisibile(!empty);
        if (empty || !this.mDecorViewsHandler.isDecorShown()) {
            this.mCommentContainerView.setVisibility(8);
        } else {
            this.mCommentContainerView.setVisibility(0);
        }
    }

    public void setState(int state, @NonNull PhotoControlsState controlsState) {
        int i;
        int i2 = 0;
        this.state = state;
        TextView textView = this.mCommentsCountView;
        if (controlsState.isCommentsVisible) {
            i = 0;
        } else {
            i = 8;
        }
        textView.setVisibility(i);
        LikesViewSynced likesViewSynced = this.mLikesView;
        if (controlsState.isLikesVisible) {
            i = 0;
        } else {
            i = 8;
        }
        likesViewSynced.setVisibility(i);
        PhotoMarksBarView photoMarksBarView = this.mMarksbarView;
        if (!controlsState.isMarkAllowed) {
            i2 = 8;
        }
        photoMarksBarView.setVisibility(i2);
    }

    public final ProgressWheelView getProgressView() {
        return this.mProgressView;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mDecorViewsHandler != null) {
            this.mDecorViewsHandler.unRegisterDecorComponent(this);
        }
    }

    protected void onDragStart() {
        if (this.mDecorViewsHandler != null) {
            this.mDecorViewsHandler.setDecorVisibility(false, false);
            this.mDecorViewsHandler.setVisibilityChangeLocked(true);
        }
        this.mDraggableContentView.setBackgroundColor(0);
    }

    protected void onBounceBack() {
        this.mDraggableContentView.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
    }

    protected void onUpdateScroll() {
        updateScrollAlpha();
    }

    protected final void updateScrollAlpha() {
        int newAlpha = (int) (((double) (100.0f * (1.0f - Math.abs(((float) getScrollY()) / ((float) getHeight()))))) * 2.55d);
        doUpdateScrollAlpha(newAlpha);
        this.mDecorViewsHandler.setBackgroundDrawableAlpha(newAlpha);
    }

    protected void doUpdateScrollAlpha(int newAlpha) {
    }

    protected void onTapped() {
        onViewTap();
    }

    protected boolean isThrowBlocked(MotionEvent event) {
        return isCommentHit(event) || isBottomPanelHit(event);
    }

    protected boolean isCommentHit(MotionEvent event) {
        boolean z = true;
        if (!this.mCommentView.isShown()) {
            return false;
        }
        this.mCommentView.getLocationInWindow(this.mCommentLocation);
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        int commentLeft = this.mCommentLocation[0];
        int commentTop = this.mCommentLocation[1];
        int commentRight = commentLeft + this.mCommentView.getMeasuredWidth();
        int commentBottom = commentTop + this.mCommentView.getMeasuredHeight();
        if (rawX < ((float) commentLeft) || rawX > ((float) commentRight) || rawY < ((float) commentTop) || rawY > ((float) commentBottom)) {
            z = false;
        }
        return z;
    }

    protected boolean isBottomPanelHit(MotionEvent event) {
        if (!this.mBottomPanelView.isShown()) {
            return false;
        }
        this.mBottomPanelView.getHitRect(this.mBottomPanelRect);
        return this.mBottomPanelRect.contains((int) event.getX(), (int) event.getY());
    }

    public void setOnPhotoActionListener(OnPhotoActionListener onPhotoActionListener) {
        this.mOnPhotoActionListener = onPhotoActionListener;
    }

    public final void setUserMark(int mark) {
        this.mMarksbarView.setUserMark(mark);
    }

    public final void setCommentsCount(int count) {
        this.mCommentsCountView.setText(String.valueOf(count));
    }

    public final void setComment(String comment) {
        this.mCommentView.setText(comment);
        updateCommentViewState();
    }

    public void setLikeInfo(LikeInfoContext likeInfoContext, boolean animate) {
        this.mLikesView.setLikeInfo(likeInfoContext, animate);
    }
}
