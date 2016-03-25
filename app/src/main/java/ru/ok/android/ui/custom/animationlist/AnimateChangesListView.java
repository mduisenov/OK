package ru.ok.android.ui.custom.animationlist;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import ru.ok.android.ui.custom.OnSizeChangedListener;
import ru.ok.android.ui.custom.animationlist.UpdateListDataCommand.ListCellCreateAnimationCreator;
import ru.ok.android.ui.custom.animationlist.UpdateListDataCommand.ListCellRemoveAnimationCreator;
import ru.ok.android.ui.custom.animationlist.UpdateListDataCommand.ListInitialPositionCallback;
import ru.ok.android.ui.custom.scroll.ScrollListenerSet;
import ru.ok.android.ui.image.view.PhotoLayerHelper;
import ru.ok.android.ui.tabbar.HideTabbarListView;
import ru.ok.android.utils.Logger;

public class AnimateChangesListView<D> extends HideTabbarListView implements AnimatorListener, OnScrollListener {
    private DataChangeAdapter<D> adapter;
    private final ru.ok.android.ui.custom.animationlist.AnimateChangesListView$ru.ok.android.ui.custom.animationlist.AnimateChangesListView.BoundsAnimationListener boundsAnimationListener;
    private ListAnimationStepData<D> currentOperation;
    private final LinkedList<UpdateListDataCommand<D>> dataQueue;
    private DrawOrderCallback drawOrderCallback;
    private int[] drawOrderIndices;
    private final List<WeakReference<View>> headersView;
    private final Set<AnimationChangeIdleListener> idleListeners;
    private int layoutHeight;
    private int layoutWidth;
    private final OnPreDrawListener listener;
    private OnSizeChangedListener onSizeChangedListener;
    private int prevFirstVisible;
    private int prevVisibleCount;
    private ScrollListenerSet scrollListenerSet;
    private int scrollState;
    private final int[] sizesHolder;
    private boolean visible;

    /* renamed from: ru.ok.android.ui.custom.animationlist.AnimateChangesListView.1 */
    class C06361 implements OnPreDrawListener {
        C06361() {
        }

        public boolean onPreDraw() {
            ViewTreeObserver viewTreeObserver = AnimateChangesListView.this.getViewTreeObserver();
            ListAdapter adapter = AnimateChangesListView.this.getAdapter();
            ListAnimationStepData<D> currentOperation = AnimateChangesListView.this.currentOperation;
            if (viewTreeObserver == null) {
                return true;
            }
            viewTreeObserver.removeOnPreDrawListener(this);
            if (adapter == null) {
                return true;
            }
            if (currentOperation == null) {
                return true;
            }
            RowInfo info;
            boolean processed;
            List<Animator> animations = new ArrayList();
            Logger.m173d("First visible position: %d, scrollState: %d, size: %d x %d", Integer.valueOf(AnimateChangesListView.this.getFirstVisiblePosition()), Integer.valueOf(AnimateChangesListView.this.scrollState), Integer.valueOf(AnimateChangesListView.this.getWidth()), Integer.valueOf(AnimateChangesListView.this.getHeight()));
            for (int i = 0; i < AnimateChangesListView.this.getChildCount(); i++) {
                int j;
                int adapterPosition = i + AnimateChangesListView.this.getFirstVisiblePosition();
                if (adapterPosition < 0 || adapterPosition >= adapter.getCount()) {
                    Logger.m185w("List child exceed adapter size: view: %d, adapter: %d, size: %d", Integer.valueOf(i), Integer.valueOf(adapterPosition), Integer.valueOf(adapter.getCount()));
                } else {
                    long currentId = adapter.getItemId(adapterPosition);
                    View child = AnimateChangesListView.this.getChildAt(i);
                    if (!(child == null || AnimateChangesListView.this.isHeaderView(child))) {
                        String happens;
                        info = (RowInfo) currentOperation.initialRowsInfos.get(Long.valueOf(currentId));
                        if (info != null) {
                            happens = "Moved";
                            AnimateChangesListView.this.setupAnimationsForMovedRow(currentOperation, info, child, animations);
                            AnimateChangesListView.this.cleanupDrawable((BitmapDrawable) currentOperation.rowsDrawables.remove(Long.valueOf(currentId)));
                        } else if (currentOperation.initialIds.contains(Long.valueOf(currentId))) {
                            happens = "Slides In";
                            processed = false;
                            for (j = i - 1; j >= 0; j--) {
                                processed |= AnimateChangesListView.this.lookForNeighbour(adapter, currentOperation, j, animations, child);
                                if (processed) {
                                    break;
                                }
                            }
                            if (!processed) {
                                for (j = i + 1; j < AnimateChangesListView.this.getChildCount(); j++) {
                                    processed |= AnimateChangesListView.this.lookForNeighbour(adapter, currentOperation, j, animations, child);
                                    if (processed) {
                                        break;
                                    }
                                }
                            }
                            if (!processed) {
                                AnimateChangesListView.this.setupAnimationsForSlidedInRow(currentOperation, animations, child);
                            }
                        } else {
                            happens = "Created";
                            if (currentOperation.dataAdapterWasEmpty) {
                                AnimateChangesListView.this.setupAnimationsForInitialCreatedRow(currentOperation, animations, child);
                            } else if (currentOperation.positionWasSaved) {
                                AnimateChangesListView.this.setupAnimationsForSlidedInRow(currentOperation, animations, child);
                            } else {
                                AnimateChangesListView.this.setupAnimationsForCreatedRow(currentOperation, animations, child);
                            }
                        }
                        Logger.m173d("View position %d -> dataId %d, top: %d, what happens: %s, data: %s", Integer.valueOf(i), Long.valueOf(currentId), Integer.valueOf(child.getTop()), happens, adapter.getItem(adapterPosition));
                    }
                }
            }
            if (!currentOperation.rowsDrawables.isEmpty()) {
                for (Entry<Long, BitmapDrawable> drawableEntry : currentOperation.rowsDrawables.entrySet()) {
                    long movedOutId = ((Long) drawableEntry.getKey()).longValue();
                    Logger.m173d("Moved out id: %d", Long.valueOf(movedOutId));
                    info = (RowInfo) currentOperation.initialRowsInfos.get(Long.valueOf(movedOutId));
                    Drawable drawable = (Drawable) drawableEntry.getValue();
                    if (info != null) {
                        if (currentOperation.currentIds.contains(Long.valueOf(movedOutId))) {
                            int movedOutCurrentPosition = currentOperation.currentIds.indexOf(Long.valueOf(movedOutId));
                            int movedOutInitialPosition = currentOperation.initialIds.indexOf(Long.valueOf(movedOutId));
                            int viewIndex = movedOutCurrentPosition - AnimateChangesListView.this.getFirstVisiblePosition();
                            processed = false;
                            for (j = viewIndex - 1; j >= 0; j--) {
                                processed |= AnimateChangesListView.this.lookForNeighbour(adapter, currentOperation, j, animations, drawable, movedOutId, info, movedOutInitialPosition, movedOutCurrentPosition);
                                if (processed) {
                                    break;
                                }
                            }
                            if (!processed) {
                                for (j = Math.max(0, viewIndex + 1); j < AnimateChangesListView.this.getChildCount(); j++) {
                                    processed |= AnimateChangesListView.this.lookForNeighbour(adapter, currentOperation, j, animations, drawable, movedOutId, info, movedOutInitialPosition, movedOutCurrentPosition);
                                    if (processed) {
                                        break;
                                    }
                                }
                            }
                            if (!processed) {
                                AnimateChangesListView.this.setupAnimationsForSlidedOutRow(currentOperation, animations, drawable, movedOutId, info);
                            }
                        } else {
                            AnimateChangesListView.this.setupAnimationsForDeletedRow(currentOperation, animations, drawable, info);
                        }
                    }
                }
            }
            if (animations.isEmpty()) {
                AnimateChangesListView.this.nextAnimationStep(currentOperation);
            } else {
                launchAnimations(animations);
            }
            return true;
        }

        private void launchAnimations(List<Animator> animations) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.addListener(AnimateChangesListView.this);
            animatorSet.playTogether(animations);
            animatorSet.start();
        }
    }

    public class BoundsAnimationListener implements AnimatorUpdateListener {
        private final Rect mCurrentBound;
        private final Rect mLastBound;

        public BoundsAnimationListener() {
            this.mLastBound = new Rect();
            this.mCurrentBound = new Rect();
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            Rect bounds = (Rect) valueAnimator.getAnimatedValue();
            if (bounds != null) {
                this.mCurrentBound.set(bounds);
                if (this.mLastBound != null) {
                    this.mCurrentBound.union(this.mLastBound);
                }
                this.mLastBound.set(bounds);
                AnimateChangesListView.this.invalidate(this.mCurrentBound);
            }
        }
    }

    public interface DrawOrderCallback {
        void fillDrawingOrder(AnimateChangesListView animateChangesListView, int[] iArr);
    }

    public static class RectTypeEvaluator implements TypeEvaluator<Rect> {
        private final Rect cachedRect;

        public RectTypeEvaluator() {
            this.cachedRect = new Rect();
        }

        public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
            this.cachedRect.set(interpolate(startValue.left, endValue.left, fraction), interpolate(startValue.top, endValue.top, fraction), interpolate(startValue.right, endValue.right, fraction), interpolate(startValue.bottom, endValue.bottom, fraction));
            return this.cachedRect;
        }

        private int interpolate(int start, int end, float fraction) {
            return (int) (((float) start) + (((float) (end - start)) * fraction));
        }
    }

    public void addHeaderView(View v) {
        super.addHeaderView(v);
        this.headersView.add(new WeakReference(v));
    }

    public void addHeaderView(View v, Object data, boolean isSelectable) {
        super.addHeaderView(v, data, isSelectable);
        this.headersView.add(new WeakReference(v));
    }

    public void setOnSizeChangedListener(OnSizeChangedListener listener) {
        this.onSizeChangedListener = listener;
    }

    public boolean removeHeaderView(View v) {
        Iterator<WeakReference<View>> iterator = this.headersView.iterator();
        while (iterator.hasNext()) {
            View view1 = (View) ((WeakReference) iterator.next()).get();
            if (view1 == null || view1.equals(v)) {
                iterator.remove();
            }
        }
        return super.removeHeaderView(v);
    }

    private boolean isHeaderView(View view) {
        Iterator<WeakReference<View>> iterator = this.headersView.iterator();
        while (iterator.hasNext()) {
            View view1 = (View) ((WeakReference) iterator.next()).get();
            if (view1 == null) {
                iterator.remove();
            } else if (view1.equals(view)) {
                return true;
            }
        }
        return false;
    }

    private void cleanupDrawable(BitmapDrawable drawable) {
        Bitmap bitmap = drawable == null ? null : drawable.getBitmap();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    private boolean lookForNeighbour(ListAdapter adapter, ListAnimationStepData<D> currentOperation, int index, List<Animator> animations, View targetView) {
        int adapterIndex = index + getFirstVisiblePosition();
        if (adapterIndex < 0 || adapterIndex >= adapter.getCount()) {
            return false;
        }
        RowInfo rowInfo = (RowInfo) currentOperation.initialRowsInfos.get(Long.valueOf(adapter.getItemId(adapterIndex)));
        View childView = getChildAt(index);
        if (rowInfo == null || childView == null) {
            return false;
        }
        setupAnimationsForMovedRow(currentOperation, rowInfo, childView, targetView, animations);
        return true;
    }

    private boolean lookForNeighbour(ListAdapter adapter, ListAnimationStepData<D> currentOperation, int neighborViewIndex, List<Animator> animations, Drawable animatingDrawable, long rowId, RowInfo rowInfo, int rowInitialPosition, int rowCurrentPosition) {
        int adapterIndex = neighborViewIndex + getFirstVisiblePosition();
        if (adapterIndex < 0 || adapterIndex >= adapter.getCount()) {
            return false;
        }
        long neighborId = adapter.getItemId(adapterIndex);
        RowInfo neighborRowInfo = (RowInfo) currentOperation.initialRowsInfos.get(Long.valueOf(neighborId));
        if (neighborRowInfo != null) {
            int initialDelta = rowInitialPosition - currentOperation.initialIds.indexOf(Long.valueOf(neighborId));
            int currentDelta = rowCurrentPosition - currentOperation.currentIds.indexOf(Long.valueOf(neighborId));
            View childView = getChildAt(neighborViewIndex);
            if (initialDelta == currentDelta && childView != null) {
                setupAnimationsForSlidedOutRow(currentOperation, animations, animatingDrawable, rowId, rowInfo, childView, neighborRowInfo);
                return true;
            }
        }
        return false;
    }

    private void setupAnimationsForCreatedRow(ListAnimationStepData<D> currentOperation, List<Animator> animations, View child) {
        ListCellCreateAnimationCreator createAnimationCreator = currentOperation.operation.createAnimationCreator;
        if (createAnimationCreator != null) {
            createAnimationCreator.createAnimations(child, animations);
            return;
        }
        Animator translation = ObjectAnimator.ofFloat(child, "translationY", new float[]{(float) (getHeight() - child.getTop()), 0.0f});
        translation.setDuration(currentOperation.operation.animationDuration);
        Animator scale = ObjectAnimator.ofFloat(child, "scaleX", new float[]{1.4f, 1.0f});
        scale.setDuration(currentOperation.operation.animationDuration);
        fillInterpolator(currentOperation, translation, scale);
        animations.add(translation);
        animations.add(scale);
    }

    private void setupAnimationsForInitialCreatedRow(ListAnimationStepData<D> currentOperation, List<Animator> animations, View child) {
        ListCellCreateAnimationCreator initialCreateAnimationCreator = currentOperation.operation.initialCreateAnimationCreator;
        if (initialCreateAnimationCreator != null) {
            initialCreateAnimationCreator.createAnimations(child, animations);
        }
    }

    private void setupAnimationsForSlidedInRow(ListAnimationStepData<D> currentOperation, List<Animator> animations, View child) {
        ListCellCreateAnimationCreator slidInAnimationCreator = currentOperation.operation.slideInAnimationCreator;
        if (slidInAnimationCreator != null) {
            slidInAnimationCreator.createAnimations(child, animations);
            return;
        }
        int initialTranslateY;
        if (child.getTop() < getHeight() - child.getBottom()) {
            initialTranslateY = -child.getHeight();
        } else {
            initialTranslateY = child.getHeight();
        }
        Animator animation = ObjectAnimator.ofFloat(child, "translationY", new float[]{(float) initialTranslateY, 0.0f});
        animation.setDuration(currentOperation.operation.animationDuration);
        fillInterpolator(currentOperation, animation);
        animations.add(animation);
    }

    private void setupAnimationsForSlidedOutRow(ListAnimationStepData<D> currentOperation, List<Animator> animations, Drawable drawable, long rowId, RowInfo rowInfo) {
        setupAnimationsForSlidedOutRow(currentOperation, animations, drawable, rowId, rowInfo, null, null);
    }

    private void setupAnimationsForSlidedOutRow(ListAnimationStepData<D> currentOperation, List<Animator> animations, Drawable drawable, long rowId, RowInfo rowInfo, View neighborView, RowInfo neighborRowInfo) {
        ListCellRemoveAnimationCreator slideOutAnimationCreator = currentOperation.operation.slideOutAnimationCreator;
        if (slideOutAnimationCreator != null) {
            slideOutAnimationCreator.createAnimations(drawable, rowInfo, animations, this.boundsAnimationListener);
            return;
        }
        Rect startBounds = new Rect(rowInfo.left, rowInfo.top, rowInfo.right, rowInfo.bottom);
        drawable.setBounds(startBounds);
        Rect endBounds = new Rect(startBounds);
        if (neighborView == null || neighborRowInfo == null) {
            endBounds.offset(0, currentOperation.currentIds.indexOf(Long.valueOf(rowId)) < getFirstVisiblePosition() ? -rowInfo.bottom : getHeight() - rowInfo.top);
        } else {
            endBounds.offset(0, neighborRowInfo.top < rowInfo.top ? neighborView.getBottom() - neighborRowInfo.bottom : neighborView.getTop() - neighborRowInfo.top);
        }
        ObjectAnimator animation = ObjectAnimator.ofObject(drawable, "bounds", new RectTypeEvaluator(), new Object[]{startBounds, endBounds});
        animation.setDuration(currentOperation.operation.animationDuration);
        animation.addUpdateListener(this.boundsAnimationListener);
        fillInterpolator(currentOperation, animation);
        animations.add(animation);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        if (!(width == this.layoutWidth && height == this.layoutHeight)) {
            if (this.onSizeChangedListener != null) {
                this.onSizeChangedListener.onSizeChanged(width, height, this.layoutWidth, this.layoutHeight);
            }
            this.layoutWidth = width;
            this.layoutHeight = height;
        }
        try {
            super.onLayout(changed, l, t, r, b);
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }

    private void setupAnimationsForDeletedRow(ListAnimationStepData<D> currentOperation, List<Animator> animations, Drawable drawable, RowInfo rowInfo) {
        if (currentOperation.operation.removeAnimation != null) {
            currentOperation.operation.removeAnimation.createAnimations(drawable, rowInfo, animations, this.boundsAnimationListener);
            return;
        }
        Rect startBounds = new Rect(rowInfo.left, rowInfo.top, rowInfo.right, rowInfo.bottom);
        drawable.setBounds(startBounds);
        new Rect(startBounds).offset(getWidth(), 0);
        ObjectAnimator animation = ObjectAnimator.ofObject(drawable, "bounds", new RectTypeEvaluator(), new Object[]{startBounds, endBounds});
        animation.setDuration(currentOperation.operation.animationDuration);
        animation.addUpdateListener(this.boundsAnimationListener);
        fillInterpolator(currentOperation, animation);
        animations.add(animation);
    }

    private void setupAnimationsForMovedRow(ListAnimationStepData<D> currentOperation, RowInfo info, View child, List<Animator> animations) {
        setupAnimationsForMovedRow(currentOperation, info, child, child, animations);
    }

    private void setupAnimationsForMovedRow(ListAnimationStepData<D> currentOperation, RowInfo info, View child, View targetView, List<Animator> animations) {
        int currentLeft = child.getLeft();
        int currentTop = child.getTop();
        int currentRight = child.getRight();
        int currentBottom = child.getBottom();
        if (currentTop != info.top || currentBottom != info.bottom || currentLeft != info.left || currentRight != info.right) {
            View view;
            Animator animation;
            if (info.top - currentTop != 0) {
                view = targetView;
                animation = ObjectAnimator.ofFloat(view, "translationY", new float[]{(float) (info.top - currentTop), 0.0f});
                animation.setDuration(currentOperation.operation.animationDuration);
                animations.add(animation);
                fillInterpolator(currentOperation, animation);
            }
            if (info.left - currentLeft != 0) {
                view = targetView;
                animation = ObjectAnimator.ofFloat(view, "translationX", new float[]{(float) (info.left - currentLeft), 0.0f});
                animation.setDuration(currentOperation.operation.animationDuration);
                animations.add(animation);
                fillInterpolator(currentOperation, animation);
            }
        }
    }

    private void fillInterpolator(ListAnimationStepData<D> currentOperation, Animator... animators) {
        if (currentOperation.operation.interpolator != null) {
            for (Animator animator : animators) {
                animator.setInterpolator(currentOperation.operation.interpolator);
            }
        }
    }

    public void onAnimationStart(Animator animation) {
    }

    public void onAnimationEnd(Animator animation) {
        ListAnimationStepData<D> currentOperation = this.currentOperation;
        if (currentOperation != null) {
            nextAnimationStep(currentOperation);
        }
    }

    private void nextAnimationStep(ListAnimationStepData<D> currentOperation) {
        if (this.visible) {
            callIdleListeners();
            UpdateListDataCommand<D> data = (UpdateListDataCommand) this.dataQueue.poll();
            if (data != null) {
                processData(data);
                return;
            } else {
                cleanupAnimation(currentOperation);
                return;
            }
        }
        cleanupAnimation(currentOperation);
    }

    private void callIdleListeners() {
        for (AnimationChangeIdleListener listener : this.idleListeners) {
            listener.onListIdle();
        }
    }

    private void processNextDataIfExists() {
        if (this.currentOperation == null && this.visible) {
            callIdleListeners();
            UpdateListDataCommand<D> data = (UpdateListDataCommand) this.dataQueue.poll();
            if (data != null) {
                processData(data);
            }
        }
    }

    private void cleanupAnimation(ListAnimationStepData<D> currentOperation) {
        boolean wantInvalidate = true;
        setEnabled(true);
        if (currentOperation.rowsDrawables.isEmpty()) {
            wantInvalidate = false;
        }
        for (BitmapDrawable drawable : currentOperation.rowsDrawables.values()) {
            cleanupDrawable(drawable);
        }
        if (wantInvalidate) {
            invalidate();
        }
        this.currentOperation = null;
    }

    public void onAnimationCancel(Animator animation) {
        ListAnimationStepData<D> currentOperation = this.currentOperation;
        if (currentOperation != null) {
            cleanupAnimation(currentOperation);
        }
    }

    public void onAnimationRepeat(Animator animation) {
    }

    public AnimateChangesListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.scrollState = 0;
        this.idleListeners = new HashSet();
        this.dataQueue = new LinkedList();
        this.boundsAnimationListener = new BoundsAnimationListener();
        this.visible = true;
        this.prevFirstVisible = -1;
        this.prevVisibleCount = -1;
        this.headersView = new ArrayList();
        this.sizesHolder = new int[2];
        this.listener = new C06361();
        this.scrollListenerSet = null;
        PhotoLayerHelper.getSizesForPhotos(getContext(), this.sizesHolder);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getScrollListenerSet().addListener(this);
    }

    private ScrollListenerSet getScrollListenerSet() {
        if (this.scrollListenerSet == null) {
            this.scrollListenerSet = new ScrollListenerSet();
            super.setOnScrollListener(this.scrollListenerSet);
        }
        return this.scrollListenerSet;
    }

    public void setDataAdapter(DataChangeAdapter adapter) {
        this.adapter = adapter;
    }

    public DataChangeAdapter<D> getDataAdapter() {
        return this.adapter;
    }

    public void setData(UpdateListDataCommand<D> operation) {
        if (isAnimating() || !this.visible) {
            this.dataQueue.offer(operation);
        } else {
            processData(operation);
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (this.visible) {
            processNextDataIfExists();
        }
    }

    public boolean isAnimating() {
        Logger.m173d("Operation: %s, state: %d", this.currentOperation, Integer.valueOf(this.scrollState));
        if (this.currentOperation == null && this.scrollState == 0) {
            return false;
        }
        return true;
    }

    private void processData(UpdateListDataCommand<D> operation) {
        String str = "Size: %d x %d, dataClass: %s";
        Object[] objArr = new Object[3];
        objArr[0] = Integer.valueOf(getWidth());
        objArr[1] = Integer.valueOf(getHeight());
        objArr[2] = operation.data != null ? operation.data.getClass().getSimpleName() : "null";
        Logger.m173d(str, objArr);
        DataChangeAdapter<D> adapter = this.adapter;
        if (adapter != null) {
            this.currentOperation = new ListAnimationStepData(operation, adapter.getData());
            if (this.currentOperation.manualPositioningRequired) {
                requestLayout();
            } else {
                prepareForAnimation();
            }
        }
    }

    private void prepareForAnimation() {
        if (this.currentOperation != null && this.currentOperation.operation != null) {
            if (!this.currentOperation.operation.doNotAnimate) {
                collectInitialInfo();
            }
            changeData();
            if (!this.currentOperation.operation.doNotAnimate) {
                collectCurrentInfo();
                subscribe2PreDraw();
            }
        }
    }

    private void subscribe2PreDraw() {
        setEnabled(false);
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if (viewTreeObserver != null) {
            viewTreeObserver.removeOnPreDrawListener(this.listener);
            viewTreeObserver.addOnPreDrawListener(this.listener);
        }
    }

    private void changeData() {
        ListAnimationStepData<D> currentOperation = this.currentOperation;
        DataChangeAdapter adapter = this.adapter;
        if (currentOperation != null && adapter != null) {
            UpdateListDataCommand<D> operation = currentOperation.operation;
            if (operation.onDataSetCallback != null) {
                Logger.m172d("Call 'onDataSetCallback'");
                currentOperation.onPreDataSetResult = operation.onDataSetCallback.onPreDataSet(operation.data);
            }
            if (!operation.doNotChangeData) {
                adapter.setData(operation.data);
            }
            if (operation.onDataSetCallback != null) {
                operation.onDataSetCallback.onPostDataSet(operation.data);
            }
            adapter.notifyDataSetChanged();
            this.prevVisibleCount = -1;
            this.prevFirstVisible = -1;
        }
    }

    private void collectInitialInfo() {
        Logger.m172d(String.format("First visible: %d", new Object[]{Integer.valueOf(getFirstVisiblePosition())}));
        ListAdapter adapter = getAdapter();
        ListAnimationStepData<D> currentOperation = this.currentOperation;
        if (adapter != null && currentOperation != null) {
            int i;
            int adapterCount = adapter.getCount();
            for (i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (view != null) {
                    view.clearAnimation();
                    int adapterPosition = i + getFirstVisiblePosition();
                    if (adapterPosition < 0 || adapterPosition >= adapterCount) {
                        Logger.m185w("List child exceed adapter size: view: %d, adapter: %d, size: %d", Integer.valueOf(i), Integer.valueOf(adapterPosition), Integer.valueOf(adapterCount));
                    } else {
                        long itemId = adapter.getItemId(adapterPosition);
                        if (itemId == -1) {
                            Logger.m185w("Evict header view: %s", view);
                        } else {
                            currentOperation.initialRowsInfos.put(Long.valueOf(itemId), new RowInfo(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
                            if (!(currentOperation.operation.doNotChangeData && currentOperation.operation.saveListPosition && !currentOperation.manualPositioningRequired)) {
                                BitmapDrawable drawable = createViewDrawable(view);
                                if (!(drawable == null || isHeaderView(view))) {
                                    currentOperation.rowsDrawables.put(Long.valueOf(itemId), drawable);
                                }
                            }
                            Logger.m173d("View position: %d -> dataId: %d, top: %d, data: %s", Integer.valueOf(adapterPosition), Long.valueOf(itemId), Integer.valueOf(view.getTop()), adapter.getItem(adapterPosition));
                        }
                    }
                }
            }
            for (i = 0; i < adapterCount; i++) {
                currentOperation.initialIds.add(Long.valueOf(adapter.getItemId(i)));
            }
            DataChangeAdapter<D> dataAdapter = this.adapter;
            if (dataAdapter != null) {
                currentOperation.dataAdapterWasEmpty = dataAdapter.getCount() == 0;
            }
        }
    }

    private BitmapDrawable createViewDrawable(View view) {
        int width = view.getWidth();
        int height = view.getHeight();
        if (width <= 0 || height <= 0) {
            return null;
        }
        try {
            Bitmap bitmap = Bitmap.createBitmap(Math.min(width, this.sizesHolder[0]), Math.min(height, this.sizesHolder[1]), Config.ARGB_8888);
            if (bitmap == null) {
                return null;
            }
            view.draw(new Canvas(bitmap));
            return new BitmapDrawable(getResources(), bitmap);
        } catch (Throwable ex) {
            Logger.m179e(ex, "Failed to create bitmap");
            return null;
        }
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.currentOperation != null) {
            for (Drawable drawable : this.currentOperation.rowsDrawables.values()) {
                drawable.draw(canvas);
            }
        }
    }

    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        Logger.m173d("currentState: %d, newState: %d", Integer.valueOf(this.scrollState), Integer.valueOf(scrollState));
        this.scrollState = scrollState;
        if (scrollState == 0) {
            processNextDataIfExists();
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (this.prevFirstVisible != firstVisibleItem || this.prevVisibleCount != visibleItemCount) {
            this.prevFirstVisible = firstVisibleItem;
            this.prevVisibleCount = visibleItemCount;
            updateDrawingOrderIndices();
        }
    }

    public void setOnScrollListener(OnScrollListener l) {
        ScrollListenerSet scrollListenerSet = getScrollListenerSet();
        if (l != null) {
            scrollListenerSet.addListener(l);
            return;
        }
        scrollListenerSet.removeAllListeners();
        scrollListenerSet.addListener(this);
    }

    public void addOnIdleListener(AnimationChangeIdleListener listener) {
        this.idleListeners.add(listener);
    }

    public void addFooterView(View v) {
        super.addFooterView(v);
    }

    protected void layoutChildren() {
        int i = 0;
        ListAnimationStepData<D> currentOperation = this.currentOperation;
        if (currentOperation == null || currentOperation.layoutPassed) {
            Logger.m172d("no animation");
            super.layoutChildren();
            updateDrawingOrderIndices();
            return;
        }
        if (currentOperation.manualPositioningRequired) {
            Logger.m172d("Call 'setInitialPosition'...");
            ListInitialPositionCallback<D> listInitialPositionCallback = currentOperation.operation.listInitialPositionCallback;
            if (listInitialPositionCallback != null) {
                listInitialPositionCallback.setInitialPosition();
            }
            super.layoutChildren();
            prepareForAnimation();
        } else {
            ListAdapter dataAdapter = this.adapter;
            if (currentOperation.operation.saveListPosition && dataAdapter != null) {
                int i2 = 0;
                while (i2 < dataAdapter.getCount()) {
                    long dataId = dataAdapter.getItemId(i2);
                    RowInfo rowInfo = (RowInfo) currentOperation.initialRowsInfos.get(Long.valueOf(dataId));
                    if (rowInfo != null) {
                        int position = currentOperation.currentIds.indexOf(Long.valueOf(dataId));
                        if (currentOperation.operation.restorePositionCallback != null) {
                            Logger.m173d("Call 'restorePositionCallback' for dataId: %d at current position: %d", Long.valueOf(dataId), Integer.valueOf(position));
                            currentOperation.operation.restorePositionCallback.onRestorePosition(rowInfo, position);
                        } else {
                            Logger.m173d("Default position restoring for dataId: %d at current position: %d", Long.valueOf(dataId), Integer.valueOf(position));
                            setSelectionFromTop(position, rowInfo.top - getPaddingTop());
                        }
                        currentOperation.positionWasSaved = true;
                    } else {
                        i2++;
                    }
                }
            }
            super.layoutChildren();
        }
        if (currentOperation.operation.listFinalPositionCallback != null) {
            Logger.m172d("Calling 'listPositionCallback': " + currentOperation.operation.listFinalPositionCallback);
            boolean positionChanged = currentOperation.operation.listFinalPositionCallback.setFinalPosition(currentOperation.oldData, currentOperation.operation.data, currentOperation.onPreDataSetResult);
            boolean z = currentOperation.positionWasSaved;
            if (!positionChanged) {
                i = 1;
            }
            currentOperation.positionWasSaved = i & z;
            if (positionChanged) {
                super.layoutChildren();
            }
        }
        updateDrawingOrderIndices();
        currentOperation.layoutPassed = true;
    }

    public void setDrawOrderCallback(DrawOrderCallback drawOrderCallback) {
        this.drawOrderCallback = drawOrderCallback;
        setChildrenDrawingOrderEnabled(drawOrderCallback != null);
    }

    private void updateDrawingOrderIndices() {
        if (this.drawOrderCallback != null) {
            int childCount = getChildCount();
            if (this.drawOrderIndices == null || this.drawOrderIndices.length != childCount) {
                this.drawOrderIndices = new int[childCount];
            }
            this.drawOrderCallback.fillDrawingOrder(this, this.drawOrderIndices);
        }
    }

    private void collectCurrentInfo() {
        ListAdapter listAdapter = getAdapter();
        ListAnimationStepData<D> currentOperation = this.currentOperation;
        if (listAdapter != null && currentOperation != null) {
            int count = listAdapter.getCount();
            for (int i = 0; i < count; i++) {
                currentOperation.currentIds.add(Long.valueOf(listAdapter.getItemId(i)));
            }
        }
    }

    protected int getChildDrawingOrder(int childCount, int iteration) {
        return this.drawOrderIndices != null ? this.drawOrderIndices[iteration] : iteration;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 3 && this.scrollState == 1) {
            getScrollListenerSet().onScrollStateChanged(this, 0);
        }
        return super.onTouchEvent(ev);
    }
}
