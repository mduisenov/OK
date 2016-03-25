package ru.ok.android.ui.image.view;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.CloseableImage;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.onelog.PhotoLayerLogger;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.ui.adapters.photo.PhotoLayerAdapter;
import ru.ok.android.ui.adapters.photo.PhotoLayerAdapter.OnFirstChildLayoutListener;
import ru.ok.android.ui.custom.ProgressWheelView;
import ru.ok.android.ui.custom.photo.AbstractPhotoView;
import ru.ok.android.ui.custom.photo.ScrollBlockingViewPager;
import ru.ok.android.ui.custom.photo.ThrowAwayViewTouchHelper.OnDragListener;
import ru.ok.android.ui.custom.photo.ThrowAwayViewTouchHelper.OnThrowAwayListener;
import ru.ok.android.ui.custom.photo.ThrowAwayViewTouchHelper.OnThrowedAwayListener;
import ru.ok.android.ui.custom.transform.bitmap.TransformBitmapView;
import ru.ok.android.ui.custom.transform.bitmap.TransformBitmapView.OnBitmapDrawListener;
import ru.ok.android.ui.image.PreviewDataHolder;
import ru.ok.android.ui.image.PreviewUriCache;
import ru.ok.android.ui.image.view.DecorHandler.DecorCallback;
import ru.ok.android.ui.image.view.DecorHandler.DecorComponentController;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.animation.SimpleAnimatorListener;
import ru.ok.android.utils.pagetransformer.ControllablePageTransformer.PageTransformerCallback;
import ru.ok.android.utils.pagetransformer.ZoomOutPageTransformer;
import ru.ok.model.Identifiable;

public abstract class PhotoLayerActivity extends TransparentToolbarBaseActivity implements OnDragListener, PageTransformerCallback {
    public static PreviewDataHolder previewDataHolder;
    private boolean activityIsFinishing;
    private boolean animating;
    private TransformBitmapView animationView;
    private int currentErrorCode;
    private DecorHandler decorViewsHandler;
    private ImageView errorIconView;
    private TextView errorMessageView;
    private TextView errorRetryView;
    private View errorView;
    private ScrollBlockingViewPager pagerView;
    private Runnable pendingPagerUpdate;
    protected PhotoLayerLogger photoLayerLogger;
    private ProgressSyncHelper progressSyncHelper;
    private ProgressWheelView progressView;
    private int realPositionBuffer;
    private boolean touchedWhileAnimating;

    /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.10 */
    class AnonymousClass10 extends SimpleAnimatorListener {
        final /* synthetic */ String val$pid;
        final /* synthetic */ Uri val$uri;

        AnonymousClass10(Uri uri, String str) {
            this.val$uri = uri;
            this.val$pid = str;
        }

        public void onAnimationStart(Animator animation) {
            PhotoLayerActivity.this.animating = true;
            PhotoLayerActivity.this.animationView.setVisibility(0);
            PhotoLayerActivity.this.setContentViewsVisibility(false);
            PhotoLayerActivity.this.decorViewsHandler.setBackgroundDrawableAlpha(0);
            PhotoLayerActivity.this.decorViewsHandler.setVisibilityChangeLocked(false);
            PhotoLayerActivity.this.decorViewsHandler.setDecorVisibility(false, false);
            PhotoLayerActivity.this.decorViewsHandler.setVisibilityChangeLocked(true);
        }

        public void onAnimationEnd(Animator animation) {
            Message msg = Message.obtain(null, 3);
            msg.getData().putParcelable("pla_image_uri", this.val$uri);
            msg.getData().putString("id", this.val$pid);
            PhotoLayerAnimationHelper.sendMessage(msg);
            PhotoLayerActivity.this.finish();
            PhotoLayerActivity.this.overridePendingTransition(2130968625, 0);
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.1 */
    class C10101 implements OnPreDrawListener {
        final /* synthetic */ Bundle val$animationBundle;
        final /* synthetic */ CloseableReference val$ref;

        C10101(Bundle bundle, CloseableReference closeableReference) {
            this.val$animationBundle = bundle;
            this.val$ref = closeableReference;
        }

        public boolean onPreDraw() {
            PhotoLayerActivity.this.pagerView.getViewTreeObserver().removeOnPreDrawListener(this);
            PhotoLayerActivity.this.startScaleUpAnimation(this.val$animationBundle, this.val$ref);
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.2 */
    class C10112 implements Runnable {
        final /* synthetic */ boolean val$smoothScroll;

        C10112(boolean z) {
            this.val$smoothScroll = z;
        }

        public void run() {
            PhotoLayerActivity.this.onPagerDataUpdated(PhotoLayerActivity.this.realPositionBuffer, this.val$smoothScroll);
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.3 */
    class C10133 implements OnFirstChildLayoutListener {

        /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.3.1 */
        class C10121 implements Runnable {
            C10121() {
            }

            public void run() {
                PhotoLayerActivity.this.animating = false;
                if (PhotoLayerActivity.this.isOpenDecorOnChildLayout()) {
                    PhotoLayerActivity.this.decorViewsHandler.setVisibilityChangeLocked(false);
                    PhotoLayerActivity.this.decorViewsHandler.setDecorVisibility(true, true);
                    PhotoLayerActivity.this.decorViewsHandler.setVisibilityChangeLocked(false);
                }
            }
        }

        C10133() {
        }

        public void onFirstChildLayout() {
            ThreadUtil.queueOnMain(new C10121());
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.4 */
    class C10154 implements DecorCallback {
        final /* synthetic */ PhotoLayerAdapter val$imagesPagerAdapter;

        /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.4.1 */
        class C10141 implements OnThrowAwayListener {
            C10141() {
            }

            public void onThrowAway(boolean up) {
                PhotoLayerActivity.this.transitBack(up);
            }
        }

        C10154(PhotoLayerAdapter photoLayerAdapter) {
            this.val$imagesPagerAdapter = photoLayerAdapter;
        }

        public void visibilityChanged() {
            this.val$imagesPagerAdapter.setOnDragListener(PhotoLayerActivity.this);
            this.val$imagesPagerAdapter.setOnThrowAwayListener(new C10141());
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.5 */
    class C10175 implements OnGlobalLayoutListener {

        /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.5.1 */
        class C10161 implements Runnable {
            C10161() {
            }

            public void run() {
                C10175.this.onGlobalLayout();
            }
        }

        C10175() {
        }

        public void onGlobalLayout() {
            PhotoLayerActivity.this.pagerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            PhotoLayerActivity.this.pagerView.postDelayed(new C10161(), 50);
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.6 */
    class C10196 implements OnBitmapDrawListener {

        /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.6.1 */
        class C10181 implements Runnable {
            C10181() {
            }

            public void run() {
                PhotoLayerActivity.this.notifyPhotoSelected(PhotoLayerActivity.this.getIdentifiableFromIntent());
            }
        }

        C10196() {
        }

        public void onBitmapPreDraw(Canvas canvas, Rect rect) {
        }

        public void onBitmapPostDraw(Canvas canvas, Rect rect) {
            PhotoLayerActivity.this.animationView.setOnBitmapDrawListener(null);
            ThreadUtil.queueOnMain(new C10181());
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.7 */
    class C10217 extends SimpleAnimatorListener {
        final /* synthetic */ Bundle val$animationBundle;

        /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.7.1 */
        class C10201 extends SimpleAnimatorListener {
            C10201() {
            }

            public void onAnimationEnd(Animator animation) {
                PhotoLayerActivity.this.clearAnimationView();
            }
        }

        C10217(Bundle bundle) {
            this.val$animationBundle = bundle;
        }

        public void onAnimationEnd(Animator animation) {
            PhotoLayerActivity.this.onAnimationEnd((Uri) this.val$animationBundle.getParcelable("pla_image_uri"));
            PhotoLayerActivity.this.animationView.transform().contentAlpha(0).withDuration(50).withListener(new C10201()).start();
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.8 */
    class C10228 implements DecorComponentController {
        C10228() {
        }

        public void setComponentVisibility(Object component, boolean visible, boolean animate, DecorCallback callback) {
            PhotoLayerActivity.this.getAppBarLayout().setVisibility(visible ? 0 : 8);
            callback.visibilityChanged();
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.PhotoLayerActivity.9 */
    class C10239 implements OnThrowedAwayListener {
        C10239() {
        }

        public void onThrowedAway() {
            PhotoLayerActivity.this.finish();
            PhotoLayerActivity.this.overridePendingTransition(0, 0);
        }
    }

    protected abstract PhotoLayerAdapter createViewImageAdapter(@NonNull DecorHandler decorHandler, @NonNull ProgressSyncHelper progressSyncHelper, @Nullable PreviewDataHolder previewDataHolder);

    protected abstract String getCurrentPhotoId();

    protected abstract Identifiable getIdentifiableFromIntent();

    protected abstract PhotoLayerAdapter getViewImagesAdapter();

    protected abstract void notifyPhotoSelected(Identifiable identifiable);

    protected abstract void onAnimationEnd(Uri uri);

    protected abstract void onAnimationNotExists(int i);

    protected abstract void onPhotoSelected(int i);

    public PhotoLayerActivity() {
        this.decorViewsHandler = new DecorHandler();
        this.currentErrorCode = 0;
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setProgressBarIndeterminateVisibility(false);
        this.photoLayerLogger = createPhotoLayerLogger();
        if (savedInstanceState == null) {
            this.photoLayerLogger.logOpen();
        }
        setContentView(2130903094);
        getWindow().addFlags(32);
        this.errorView = findViewById(2131624551);
        this.errorIconView = (ImageView) findViewById(2131624592);
        this.errorMessageView = (TextView) findViewById(2131624593);
        this.errorRetryView = (TextView) findViewById(2131624594);
        this.errorRetryView.setText(getStringLocalized(2131165278));
        if (savedInstanceState != null) {
            this.currentErrorCode = savedInstanceState.getInt("errorCode");
        }
        if (this.currentErrorCode != 0) {
            showError(this.currentErrorCode);
            return;
        }
        this.animationView = (TransformBitmapView) findViewById(2131624591);
        this.pagerView = (ScrollBlockingViewPager) findViewById(C0263R.id.pager);
        this.progressView = (ProgressWheelView) findViewById(2131624548);
        this.progressSyncHelper = new ProgressSyncHelper();
        this.progressSyncHelper.registerPivotView(this.progressView);
        prepareActionBar();
        this.decorViewsHandler.registerBackgroundDrawable(findViewById(2131624590).getBackground().mutate(), MotionEventCompat.ACTION_MASK);
        this.decorViewsHandler.setDecorVisibility(false, false);
        this.decorViewsHandler.setVisibilityChangeLocked(true);
    }

    private PhotoLayerLogger createPhotoLayerLogger() {
        return new PhotoLayerLogger(getIntent().getIntExtra("source", 0));
    }

    protected void setRealPositionBuffer(int realPositionBuffer) {
        this.realPositionBuffer = realPositionBuffer;
    }

    protected DecorHandler getDecorViewsHandler() {
        return this.decorViewsHandler;
    }

    protected void startAnimation(Bundle savedInstanceState) {
        int position;
        boolean z = true;
        if (savedInstanceState == null) {
            position = getInitialRealPosition();
        } else {
            position = savedInstanceState.getInt("position");
        }
        Bundle animationBundle = getIntent().getBundleExtra("pla_animation_bundle");
        IOUtils.closeSilently(previewDataHolder);
        previewDataHolder = PreviewDataHolder.createFrom(animationBundle);
        CloseableReference<CloseableImage> ref = null;
        if (previewDataHolder != null) {
            ref = CloseableReference.cloneOrNull(previewDataHolder.getPreviewRef());
        }
        String str = "Start animation? %s";
        Object[] objArr = new Object[1];
        if (animationBundle == null || ref == null) {
            z = false;
        }
        objArr[0] = Boolean.valueOf(z);
        Logger.m173d(str, objArr);
        if (animationBundle == null || ref == null) {
            onAnimationNotExists(position);
            return;
        }
        getIntent().removeExtra("pla_animation_bundle");
        onBeforeEnterAnimation(animationBundle, ref);
    }

    protected void onBeforeEnterAnimation(@NonNull Bundle animationBundle, @NonNull CloseableReference<CloseableImage> ref) {
        this.decorViewsHandler.setBackgroundDrawableAlpha(0);
        setContentViewsVisibility(false);
        this.pagerView.getViewTreeObserver().addOnPreDrawListener(new C10101(animationBundle, ref));
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    protected void onInternetAvailable() {
        super.onInternetAvailable();
        PhotoLayerAdapter imagesAdapter = getViewImagesAdapter();
        if (imagesAdapter != null) {
            imagesAdapter.notifyDataSetChanged();
        }
    }

    protected void clearAfterAnimation() {
        setContentViewsVisibility(true);
        this.decorViewsHandler.setBackgroundDrawableAlpha(MotionEventCompat.ACTION_MASK);
        this.animationView.setBackgroundAlpha(0);
    }

    protected boolean isUserTouching() {
        if (getViewImagesAdapter() != null) {
            View currentView = getViewImagesAdapter().getCurrentView();
            if (currentView instanceof AbstractPhotoView) {
                if (((AbstractPhotoView) currentView).isDragging() || ((AbstractPhotoView) currentView).isTouching()) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    protected int getCurrentErrorCode() {
        return this.currentErrorCode;
    }

    protected final void showError(int error) {
        this.currentErrorCode = error;
        this.errorView.setVisibility(0);
        this.errorRetryView.setVisibility(8);
        this.errorRetryView.setOnClickListener(null);
        if (error == 1 || error == 4) {
            this.errorMessageView.setText(getStringLocalized(2131166347));
            this.errorIconView.setImageResource(2130838164);
        } else if (error == 3) {
            this.errorIconView.setImageResource(2130838164);
            this.errorMessageView.setText(getStringLocalized(2131166345));
        } else if (error == 2) {
            this.errorIconView.setImageResource(2130838166);
            this.errorMessageView.setText(getStringLocalized(2131166346));
        }
        setContentViewsVisibility(false);
    }

    public boolean shouldApplyTransformation(View page, float position) {
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
        Logger.m173d("Close preview data. %s", previewDataHolder);
        IOUtils.closeSilently(previewDataHolder);
    }

    protected final void onPagerDataUpdated(int newRealPosition, boolean smoothScroll) {
        if (!this.activityIsFinishing) {
            this.realPositionBuffer = newRealPosition;
            if (isUserTouching()) {
                this.pendingPagerUpdate = new C10112(smoothScroll);
                return;
            }
            this.pagerView.setBlockScrollToRight(false);
            this.pagerView.setBlockScrollToLeft(false);
            if (getViewImagesAdapter() != null) {
                onPagerDataUpdated();
                this.progressView.setVisibility(8);
                onPhotoSelected(newRealPosition);
                setPagerRealPosition(newRealPosition, smoothScroll);
            }
        }
    }

    protected void onPagerDataUpdated() {
        getViewImagesAdapter().notifyDataSetChanged();
    }

    protected final void setPagerRealPosition(int realPosition, boolean smoothScroll) {
        this.pagerView.setCurrentItem(getViewImagesAdapter().getVirtualPosition(realPosition), smoothScroll);
    }

    protected final void preparePager() {
        if (getViewImagesAdapter() == null) {
            doPreparePager();
        }
    }

    protected void doPreparePager() {
        this.pagerView.setPageTransformer(true, new ZoomOutPageTransformer(this));
        if (DeviceUtils.getMemoryClass(this) < 24) {
            this.pagerView.setOffscreenPageLimit(0);
        }
        PhotoLayerAdapter imagesPagerAdapter = createViewImageAdapter(this.decorViewsHandler, this.progressSyncHelper, previewDataHolder);
        imagesPagerAdapter.setOnFirstChildLayoutListener(new C10133());
        this.decorViewsHandler.addDecorCallback(new C10154(imagesPagerAdapter));
        this.pagerView.setAdapter(imagesPagerAdapter);
        setPagerRealPosition(getInitialRealPosition(), false);
    }

    public void onStartDrag() {
        this.realPositionBuffer = getCurrentRealPosition();
    }

    public void onFinishDrag() {
        if (this.pendingPagerUpdate != null) {
            this.pendingPagerUpdate.run();
            this.pendingPagerUpdate = null;
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", getCurrentRealPosition());
        outState.putInt("errorCode", this.currentErrorCode);
    }

    public void onBackPressed() {
        if (this.currentErrorCode != 0) {
            finish();
        } else if (!this.animating) {
            transitBack(true);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.pagerView.getViewTreeObserver().addOnGlobalLayoutListener(new C10175());
    }

    protected final int getCurrentRealPosition() {
        if (isUserTouching()) {
            return this.realPositionBuffer;
        }
        if (getViewImagesAdapter() != null) {
            return getViewImagesAdapter().getRealPosition(this.pagerView.getCurrentItem());
        }
        return -1;
    }

    private void startScaleUpAnimation(Bundle animationBundle, CloseableReference<CloseableImage> ref) {
        this.animating = true;
        this.animationView.setOnBitmapDrawListener(new C10196());
        if (PhotoLayerAnimationHelper.startScaleUpAnimation(this.animationView, animationBundle, ref, new C10217(animationBundle)) == null) {
            onAnimationEnd(null);
            clearAnimationView();
        }
    }

    protected final void clearAnimationView() {
        this.animationView.closeBitmapRef();
        this.animationView.setContentAlpha(MotionEventCompat.ACTION_MASK);
        this.animationView.setVisibility(4);
    }

    protected void setContentViewsVisibility(boolean visible) {
        int visibility = visible ? 0 : 4;
        if (this.pagerView != null) {
            this.pagerView.setVisibility(visibility);
        }
        if (!visible && this.progressView != null) {
            this.progressView.setVisibility(visibility);
        }
    }

    protected int getInitialRealPosition() {
        return 0;
    }

    protected void prepareActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.decorViewsHandler.registerDecorComponent(actionBar, new C10228());
    }

    protected void transitBack(boolean throwUp) {
        this.activityIsFinishing = true;
        if (getViewImagesAdapter() == null) {
            finish();
            return;
        }
        View view = getViewImagesAdapter().getCurrentView();
        if (view instanceof AbstractPhotoView) {
            AbstractPhotoView photoView = (AbstractPhotoView) view;
            Uri uri = photoView.getUri();
            Logger.m173d("ScaleDown. Looking for uri: %s", uri);
            if (uri == null) {
                finish();
                return;
            }
            Message msg = Message.obtain(null, 2);
            msg.getData().putString("id", getCurrentPhotoId());
            Bundle animationBundle = PhotoLayerAnimationHelper.sendMessage(msg);
            if (animationBundle == null || !photoView.isReadyForAnimation()) {
                throwAway(photoView, throwUp);
                return;
            }
            int srcWidth = photoView.getImageDisplayedWidth();
            int srcHeight = photoView.getImageDisplayedHeight();
            Logger.m173d("ScaleDown, src (%d, %d; %d, %d)", Integer.valueOf(srcWidth), Integer.valueOf(srcHeight), Integer.valueOf(photoView.getImageDisplayedX()), Integer.valueOf(photoView.getImageDisplayedY()));
            if (srcHeight >= 1 && srcWidth >= 1) {
                PhotoLayerAnimationHelper.fillExtraScaleDownParams(animationBundle, uri, srcWidth, srcHeight, srcX, srcY, 0, photoView.getScrollY(), this.decorViewsHandler.getBackgroundDrawableAlpha());
                if (!startScaleDownAnimation(getCurrentPhotoId(), animationBundle)) {
                    throwAway(photoView, throwUp);
                    return;
                }
                return;
            }
            return;
        }
        finish();
    }

    protected void throwAway(AbstractPhotoView photoView, boolean throwUp) {
        photoView.throwAway(throwUp, new C10239());
    }

    protected final boolean startScaleDownAnimation(String pid, Bundle animationBundle) {
        Uri uri = (Uri) animationBundle.getParcelable("pla_image_uri");
        CloseableReference<CloseableImage> closeableReference = previewDataHolder == null ? null : previewDataHolder.getRefIfMatch(PreviewUriCache.getInstance().get(uri));
        if (closeableReference == null) {
            PreviewDataHolder previewData = PreviewDataHolder.createFrom(animationBundle);
            if (previewData != null) {
                closeableReference = previewData.getPreviewRef();
            } else {
                closeableReference = null;
            }
            if (closeableReference == null) {
                return false;
            }
        }
        if (PhotoLayerAnimationHelper.startScaleDownAnimation(this.animationView, animationBundle, closeableReference, new AnonymousClass10(uri, pid)) != null) {
            return true;
        }
        return false;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (this.activityIsFinishing) {
            return true;
        }
        switch (event.getAction()) {
            case RECEIVED_VALUE:
                if (this.animating) {
                    this.touchedWhileAnimating = true;
                    break;
                }
                break;
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
            case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
            case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                onMoveEventEnds();
                if (!this.animating && this.touchedWhileAnimating) {
                    this.touchedWhileAnimating = false;
                    if (dispatchMotionEventToFirstChild(event)) {
                        return true;
                    }
                }
                this.touchedWhileAnimating = false;
                break;
            case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (this.animating) {
                    this.touchedWhileAnimating = true;
                    break;
                } else if (this.touchedWhileAnimating && dispatchMotionEventToFirstChild(event)) {
                    return true;
                }
        }
        return super.dispatchTouchEvent(event);
    }

    private void onMoveEventEnds() {
        Logger.m173d("Close preview data. %s", previewDataHolder);
        IOUtils.closeSilently(previewDataHolder);
    }

    private boolean dispatchMotionEventToFirstChild(MotionEvent event) {
        if (getViewImagesAdapter() == null) {
            return false;
        }
        View view = getViewImagesAdapter().getCurrentView();
        if (view == null || !(view instanceof AbstractPhotoView)) {
            return false;
        }
        return ((AbstractPhotoView) view).dispatchTouchEvent(event);
    }

    protected ScrollBlockingViewPager getPagerView() {
        return this.pagerView;
    }

    protected boolean isOpenDecorOnChildLayout() {
        return true;
    }
}
