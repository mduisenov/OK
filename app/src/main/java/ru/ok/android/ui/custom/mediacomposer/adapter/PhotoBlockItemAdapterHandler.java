package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import java.util.Arrays;
import ru.ok.android.ui.custom.mediacomposer.EditablePhotoItem;
import ru.ok.android.ui.custom.mediacomposer.EditablePhotoItemView;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerStyleParams;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.ScaleToInsertInfo;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.android.ui.custom.mediacomposer.PhotoBlockItem;
import ru.ok.android.ui.custom.mediacomposer.SimpleLayoutTransitionAnimator;
import ru.ok.android.ui.custom.mediacomposer.ViewUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.mediatopics.MediaItemType;

public class PhotoBlockItemAdapterHandler extends MediaItemAdapterHandler<PhotoBlockItem> {
    private final int spacing;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.adapter.PhotoBlockItemAdapterHandler.1 */
    class C06911 implements OnTouchListener {
        final /* synthetic */ GestureDetector val$gestureDetector;

        C06911(GestureDetector gestureDetector) {
            this.val$gestureDetector = gestureDetector;
        }

        public boolean onTouch(View v, MotionEvent event) {
            return this.val$gestureDetector.onTouchEvent(event);
        }
    }

    private class ChildrenClickListener extends SimpleOnGestureListener {
        private final ViewGroup itemRootView;

        private ChildrenClickListener(ViewGroup itemRootView) {
            this.itemRootView = itemRootView;
        }

        public boolean onSingleTapUp(MotionEvent event) {
            int childCount = this.itemRootView.getChildCount();
            int scrollX = this.itemRootView.getScrollX();
            int scrollY = this.itemRootView.getScrollY();
            for (int i = 0; i < childCount; i++) {
                View child = this.itemRootView.getChildAt(i);
                float localX = (event.getX() + ((float) scrollX)) - ((float) child.getLeft());
                float localY = (event.getY() + ((float) scrollY)) - ((float) child.getTop());
                if (localX >= 0.0f && localX < ((float) child.getWidth()) && localY >= 0.0f && localY < ((float) child.getHeight())) {
                    return onItemClick(i, child);
                }
            }
            return false;
        }

        private boolean onItemClick(int position, View view) {
            PhotoBlockItemAdapterHandler.this.showActionPopup(view, (Bundle) view.getTag(2131624309));
            return true;
        }
    }

    protected PhotoBlockItemAdapterHandler(Context context, LocalizationManager localizationManager, FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType, MediaComposerStyleParams styleParams, ImageHandler imageHandler) {
        super(context, localizationManager, styleParams, mediaComposerController, fragmentBridge, mediaTopicType, imageHandler);
        this.spacing = context.getResources().getDimensionPixelOffset(2131231038);
    }

    MediaItemActionProvider createActionProvider() {
        return new PhotoBlockItemActionProvider(this.fragmentBridge, this.mediaComposerController, this.mediaTopicType, this.imageHandler, this);
    }

    public View createView(PhotoBlockItem photoBlock, ViewGroup parent, boolean isEditable, int viewId) {
        View container = createContainer(parent);
        container.setId(viewId);
        installChildrenClickListener(container);
        addChildrenItemViews(photoBlock, container, viewId * 10000);
        updateViewIsEditable(container, photoBlock, parent, isEditable);
        return container;
    }

    private void installChildrenClickListener(ViewGroup container) {
        OnTouchListener onTouchListener = new C06911(new GestureDetector(this.context, new ChildrenClickListener(container, null)));
        container.setOnTouchListener(onTouchListener);
        container.setTag(2131624352, onTouchListener);
    }

    private ViewGroup createContainer(ViewGroup parent) {
        LocalizationManager localizationManager = this.localizationManager;
        return (ViewGroup) LocalizationManager.inflate(this.context, 2130903307, parent, false);
    }

    private void addChildrenItemViews(PhotoBlockItem photoBlock, ViewGroup container, int baseViewId) {
        int size = photoBlock.size();
        for (int i = 0; i < size; i++) {
            EditablePhotoItem photoItem = photoBlock.getPhotoItem(i);
            Bundle extras = new Bundle();
            extras.putInt("pos_in_block", i);
            extras.putParcelable("block", photoBlock);
            View itemView = createChildView(photoItem, container, extras);
            itemView.setId((baseViewId + i) + 1);
            updateChildLayoutParams(i, itemView, baseViewId);
            container.addView(itemView);
        }
    }

    private View createChildView(EditablePhotoItem imageItem, ViewGroup container, Bundle extras) {
        View itemView;
        LocalizationManager localizationManager = this.localizationManager;
        View imageItemView = (EditablePhotoItemView) LocalizationManager.inflate(this.context, 2130903304, container, false);
        imageItemView.bindItem(imageItem);
        if (this.styleParams.showItemActionIcon) {
            DecoratedView decoratedView = createDecoratedView(imageItem, imageItemView, container);
            itemView = decoratedView.rootView;
            installActions(imageItem, itemView, decoratedView.iconView, extras);
            itemView.setClickable(false);
            itemView.setFocusable(false);
            itemView.setFocusableInTouchMode(false);
        } else {
            itemView = imageItemView;
            installActions(imageItem, itemView, itemView, extras);
        }
        itemView.setTag(2131624333, imageItemView);
        this.imageHandler.setImage(imageItem.getImageUri(), imageItemView, imageItem.getOrientation());
        return itemView;
    }

    void updateChildLayoutParams(int position, View childView, int baseViewId) {
        childView.setId((baseViewId + position) + 1);
        LayoutParams lp = (LayoutParams) childView.getLayoutParams();
        int column = position % 3;
        if (position == 0) {
            lp.addRule(9);
            lp.addRule(10);
        } else if (column == 0) {
            int topId = (position - 3) + 1;
            lp.addRule(5, baseViewId + topId);
            lp.addRule(3, baseViewId + topId);
            lp.topMargin = this.spacing;
        } else {
            int previousIdInRow = position;
            int firstInRowId = (position - (position % 3)) + 1;
            lp.addRule(3, baseViewId + ((position - 3) + 1));
            lp.addRule(1, baseViewId + previousIdInRow);
            lp.addRule(6, baseViewId + firstInRowId);
            lp.leftMargin = this.spacing;
        }
    }

    static void clearChildLayoutParams(View childView) {
        LayoutParams lp = (LayoutParams) childView.getLayoutParams();
        int[] rules = lp.getRules();
        lp.topMargin = 0;
        lp.leftMargin = 0;
        Arrays.fill(rules, 0, rules.length, 0);
    }

    public void updateViewIsEditable(View view, PhotoBlockItem mediaItem, ViewGroup parent, boolean isEditable) {
        super.updateViewIsEditable(view, mediaItem, parent, isEditable);
        view.setClickable(isEditable);
    }

    public void disposeView(View view, PhotoBlockItem mediaItem) {
        this.imageHandler.onViewRemoved(view);
        super.disposeView(view, mediaItem);
    }

    protected ScaleToInsertInfo canScaleToInsert(MediaItem mediaItem, View view, float x1, float y1, float x2, float y2) {
        if (mediaItem.type != MediaItemType.PHOTO_BLOCK) {
            return null;
        }
        if (view instanceof RelativeLayout) {
            RelativeLayout container = (RelativeLayout) view;
            int pos1 = ViewUtils.getChildPositionByXY(container, x1, y1);
            int pos2 = ViewUtils.getChildPositionByXY(container, x2, y2);
            if (pos1 == -1 || pos2 == -1) {
                return null;
            }
            boolean swapPosition = pos2 < pos1;
            if (swapPosition) {
                pos1 ^= pos2;
                pos2 ^= pos1;
                pos1 ^= pos2;
            }
            if (pos2 / 3 == (pos1 / 3) + 1) {
                ScaleToInsertInfo info = new ScaleToInsertInfo();
                info.parent = container;
                info.pos1 = pos1;
                info.pos2 = pos2;
                info.swapPosition = swapPosition;
                if (swapPosition) {
                    info.x1 = x2;
                    info.y1 = y2;
                    info.x2 = x1;
                    info.y2 = y1;
                } else {
                    info.x1 = x1;
                    info.y1 = y1;
                    info.x2 = x2;
                    info.y2 = y2;
                }
                info.mode = 1;
                info.item2 = mediaItem;
                info.item1 = mediaItem;
                return info;
            }
        }
        return null;
    }

    public void translateChildren(ScaleToInsertInfo info, float beforeTranslationY, float afterTranslationY) {
        if (info.parent instanceof RelativeLayout) {
            int lastInFirstRow = Math.min((((info.pos1 / 3) + 1) * 3) - 1, info.item1.size() - 1);
            ViewUtils.setChildrenTranslationY(info.parent, 0, lastInFirstRow, beforeTranslationY);
            ViewUtils.setChildrenTranslationY(info.parent, lastInFirstRow + 1, info.parent.getChildCount() - 1, afterTranslationY);
        }
    }

    public void animateChildren(ScaleToInsertInfo info, float finalTranlationY, AnimatorListener listener) {
        if (info.parent instanceof RelativeLayout) {
            ViewUtils.startAnimateChildrenTranslationY(info.parent, 0, info.parent.getChildCount() - 1, finalTranlationY, listener);
        }
    }

    public void insertItem(ScaleToInsertInfo info, MediaItem newItem) {
        if (info.parent instanceof RelativeLayout) {
            RelativeLayout originalBlockView = info.parent;
            PhotoBlockItem originalBlock = info.item1;
            int lastInFirstRow = Math.min((((info.pos1 / 3) + 1) * 3) - 1, originalBlock.size() - 1);
            info.parent = (ViewGroup) originalBlockView.getParent();
            int remove = this.mediaComposerController.remove(originalBlock, false, true);
            Pair<PhotoBlockItem, View>[] newBlocks = breakIntoTwoBlocks(originalBlock, originalBlockView, lastInFirstRow, info.parent);
            MediaItem block1 = newBlocks[0].first;
            MediaItem block2 = newBlocks[1].first;
            int i = remove + 1;
            this.mediaComposerController.insert(block1, remove, (View) newBlocks[0].second);
            remove = i + 1;
            this.mediaComposerController.insert(newItem, i);
            i = remove + 1;
            this.mediaComposerController.insert(block2, remove, (View) newBlocks[1].second);
            info.pos2 = i - 1;
            info.pos1 = i - 3;
            info.item1 = block1;
            info.item2 = block2;
        }
    }

    Pair[] breakIntoTwoBlocks(PhotoBlockItem originalBlock, ViewGroup originalBlockView, int breakPosition, ViewGroup newParent) {
        int i;
        PhotoBlockItem block1 = new PhotoBlockItem();
        PhotoBlockItem block2 = new PhotoBlockItem();
        int originalBlockSize = originalBlock.size();
        for (i = 0; i <= breakPosition; i++) {
            block1.add(originalBlock.getPhotoItem(i));
        }
        for (i = breakPosition + 1; i < originalBlockSize; i++) {
            block2.add(originalBlock.getPhotoItem(i));
        }
        View[] recycledChildren = recycleChildren(originalBlockView);
        View blockView1 = createBlockFromRecycledChildren(recycledChildren, newParent, 0, breakPosition, block1);
        View blockView2 = createBlockFromRecycledChildren(recycledChildren, newParent, breakPosition + 1, originalBlockSize - 1, block2);
        return new Pair[]{new Pair(block1, blockView1), new Pair(block2, blockView2)};
    }

    private View[] recycleChildren(ViewGroup container) {
        int childCount = container.getChildCount();
        View[] views = new View[childCount];
        for (int i = 0; i < childCount; i++) {
            View view = container.getChildAt(i);
            clearChildLayoutParams(view);
            view.setId(-1);
            view.setTranslationY(0.0f);
            view.setTag(2131624309, null);
            views[i] = view;
        }
        container.removeAllViews();
        return views;
    }

    private View createBlockFromRecycledChildren(View[] recycledChildren, ViewGroup parent, int fromPosition, int toPosition, PhotoBlockItem newBlockItem) {
        ViewGroup container = createContainer(parent);
        int viewId = this.mediaComposerController.generateNextViewId();
        newBlockItem.setViewId(viewId);
        int childrenBaseViewId = viewId * 10000;
        container.setId(viewId);
        installChildrenClickListener(container);
        int positionInNewBlock = 0;
        int newBlockSize = newBlockItem.size();
        int oldPosition = fromPosition;
        while (oldPosition <= toPosition) {
            View recycledView = recycledChildren[oldPosition];
            updateChildLayoutParams(positionInNewBlock, recycledView, childrenBaseViewId);
            Bundle extras = new Bundle();
            extras.putInt("pos_in_block", positionInNewBlock);
            extras.putParcelable("block", newBlockItem);
            MediaItem itemInBlock = positionInNewBlock < newBlockSize ? newBlockItem.getPhotoItem(positionInNewBlock) : null;
            if (itemInBlock != null) {
                extras.putParcelable("action_item", itemInBlock);
            }
            installActions(itemInBlock, recycledView, recycledView, extras);
            container.addView(recycledView);
            oldPosition++;
            positionInNewBlock++;
        }
        return container;
    }

    public void onNeighboursChanged(PhotoBlockItem item, View view, int position) {
        if (view instanceof ViewGroup) {
            ViewGroup photoBlockView = (ViewGroup) view;
            int childCount = photoBlockView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                boolean insertTextActionAvailable;
                if (i > 0 || position == 0 || this.mediaComposerController.getItemType(position - 1) != MediaItemType.TEXT) {
                    insertTextActionAvailable = true;
                } else {
                    insertTextActionAvailable = false;
                }
                Bundle actionExtras = (Bundle) photoBlockView.getChildAt(i).getTag(2131624309);
                if (actionExtras == null) {
                    actionExtras = new Bundle();
                }
                actionExtras.putBoolean("insert_text_before", insertTextActionAvailable);
            }
            return;
        }
        Logger.m185w("Invalid photo block view: %s", view);
    }

    public void mergeItems(int itemPosition1, PhotoBlockItem item1, View itemView1, int itemPosition2, PhotoBlockItem item2, View itemView2, PhotoBlockItem mergedItem) {
        if ((itemView1 instanceof RelativeLayout) && (itemView2 instanceof RelativeLayout)) {
            ViewGroup blockView1 = (ViewGroup) itemView1;
            ViewGroup blockView2 = (ViewGroup) itemView2;
            int size1 = blockView1.getChildCount();
            int size2 = blockView2.getChildCount();
            int[][] locationsOnScreen = new int[(size1 + size2)][];
            getChildrenLocationsOnScreent(blockView1, locationsOnScreen, 0);
            getChildrenLocationsOnScreent(blockView2, locationsOnScreen, size1);
            View[] recycledViews1 = recycleChildren(blockView1);
            View[] recycledViews2 = recycleChildren(blockView2);
            View[] allRecycledViews = new View[(size1 + size2)];
            System.arraycopy(recycledViews1, 0, allRecycledViews, 0, size1);
            System.arraycopy(recycledViews2, 0, allRecycledViews, size1, size2);
            View mergedView = createBlockFromRecycledChildren(allRecycledViews, (ViewGroup) itemView1.getParent(), 0, allRecycledViews.length - 1, mergedItem);
            this.mediaComposerController.removeItemAt(itemPosition1);
            this.mediaComposerController.removeItemAt(itemPosition1);
            this.mediaComposerController.insert((MediaItem) mergedItem, itemPosition1, mergedView);
            new SimpleLayoutTransitionAnimator((ViewGroup) mergedView, 200, locationsOnScreen).startAnimation();
            return;
        }
        super.mergeItems(itemPosition1, item1, itemView1, itemPosition2, item2, itemView2, mergedItem);
    }

    static void getChildrenLocationsOnScreent(ViewGroup parent, int[][] locations, int offset) {
        int childCount = parent.getChildCount();
        int i = 0;
        while (i < childCount && offset + i < locations.length) {
            View child = parent.getChildAt(i);
            locations[offset + i] = new int[2];
            child.getLocationOnScreen(locations[offset + i]);
            i++;
        }
    }
}
