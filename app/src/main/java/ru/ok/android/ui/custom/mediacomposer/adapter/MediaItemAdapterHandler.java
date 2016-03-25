package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerStyleParams;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.ScaleToInsertInfo;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionsPopup;
import ru.ok.android.ui.fragments.MediaTopicEditorFragment;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ViewTagUtils;
import ru.ok.android.utils.ViewTagUtils.ViewTagVisitor;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.mediatopics.MediaItemType;

public abstract class MediaItemAdapterHandler<TMediaItem extends MediaItem> {
    protected OnClickListener actionClickListener;
    protected final Context context;
    protected final FragmentBridge fragmentBridge;
    protected final ImageHandler imageHandler;
    protected final LocalizationManager localizationManager;
    protected final MediaComposerController mediaComposerController;
    private final MediaItemActionProvider mediaItemActionProvider;
    protected final MediaTopicType mediaTopicType;
    private final ViewTagVisitor setInvisibleVisitor;
    private final ViewTagVisitor setVisibleVisitor;
    protected final String statsMode;
    protected final MediaComposerStyleParams styleParams;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.adapter.MediaItemAdapterHandler.1 */
    class C06881 implements ViewTagVisitor {
        C06881() {
        }

        public void visitViewTag(View view, int key, Object tag) {
            ((View) tag).setVisibility(0);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.adapter.MediaItemAdapterHandler.2 */
    class C06892 implements ViewTagVisitor {
        C06892() {
        }

        public void visitViewTag(View view, int key, Object tag) {
            ((View) tag).setVisibility(4);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.adapter.MediaItemAdapterHandler.3 */
    class C06903 implements OnClickListener {
        C06903() {
        }

        public void onClick(View v) {
            MediaItemAdapterHandler.this.showActionPopup(v, (Bundle) v.getTag(2131624309));
        }
    }

    protected static class DecoratedView {
        final View iconView;
        final View mediaItemView;
        final View rootView;

        DecoratedView(View rootView, View mediaItemView, View iconView) {
            this.rootView = rootView;
            this.mediaItemView = mediaItemView;
            this.iconView = iconView;
        }
    }

    protected static class ItemActionViewHolder {
        final View actionViewAnchor;
        final View itemView;

        ItemActionViewHolder(View actionViewAnchor, View itemView) {
            this.actionViewAnchor = actionViewAnchor;
            this.itemView = itemView;
        }
    }

    abstract MediaItemActionProvider createActionProvider();

    protected MediaItemAdapterHandler(Context context, LocalizationManager localizationManager, MediaComposerStyleParams styleParams, MediaComposerController mediaComposerController, FragmentBridge fragmentBridge, MediaTopicType mediaTopicType, ImageHandler imageHandler) {
        this.setVisibleVisitor = new C06881();
        this.setInvisibleVisitor = new C06892();
        this.actionClickListener = new C06903();
        this.context = context;
        this.localizationManager = localizationManager;
        this.styleParams = styleParams;
        this.mediaComposerController = mediaComposerController;
        this.fragmentBridge = fragmentBridge;
        this.mediaTopicType = mediaTopicType;
        this.imageHandler = imageHandler;
        this.statsMode = fragmentBridge.getFragment() instanceof MediaTopicEditorFragment ? "new" : "edit";
        this.mediaItemActionProvider = createActionProvider();
    }

    public MediaItemActionProvider getActionProvider() {
        return this.mediaItemActionProvider;
    }

    public void updateViewIsEditable(View view, TMediaItem tMediaItem, ViewGroup parent, boolean isEditable) {
        if (this.styleParams.showItemActionIcon) {
            ViewTagUtils.traverseViewTags(view, 2131624310, isEditable ? this.setVisibleVisitor : this.setInvisibleVisitor);
        }
        ItemActionViewHolder itemActionViewHolder = (ItemActionViewHolder) view.getTag(2131624349);
        if (itemActionViewHolder != null) {
            itemActionViewHolder.itemView.setClickable(isEditable);
        }
        view.setFocusable(isEditable);
    }

    public void disposeView(View view, TMediaItem tMediaItem) {
    }

    protected boolean canHaveInsertTextAction() {
        return true;
    }

    protected View createDecoratedViewWithActions(TMediaItem mediaItem, View itemView, ViewGroup parent, Bundle extras) {
        if (this.styleParams.showItemActionIcon) {
            DecoratedView decoratedView = createDecoratedView(mediaItem, itemView, parent);
            installActions(mediaItem, decoratedView.rootView, decoratedView.iconView, extras);
            return decoratedView.rootView;
        }
        installActions(mediaItem, itemView, itemView, extras);
        return itemView;
    }

    protected void installActions(MediaItem item, View itemView, View actionIconView, Bundle extras) {
        View anchorView;
        if (actionIconView == null) {
            anchorView = itemView;
        } else {
            anchorView = actionIconView;
        }
        itemView.setTag(2131624349, new ItemActionViewHolder(anchorView, itemView));
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putParcelable("action_item", item);
        extras.putString("mode", this.statsMode);
        ArrayList<ActionItem> actions = new ArrayList();
        onCreateActions(item, itemView, actions);
        extras.putParcelableArrayList("actions", actions);
        itemView.setTag(2131624309, extras);
        itemView.setOnClickListener(this.actionClickListener);
    }

    protected void onCreateActions(MediaItem item, View itemView, List<ActionItem> outActions) {
        outActions.add(new ActionItem(2131624280, 2131165724, 2130838203));
        outActions.add(new ActionItem(2131624283, 2131166457, 2130838574));
        if (canHaveInsertTextAction()) {
            outActions.add(new ActionItem(2131624281, 2131166006, 2130838211));
        }
    }

    protected void showActionPopup(View itemView, Bundle extras) {
        ItemActionViewHolder holder = (ItemActionViewHolder) itemView.getTag(2131624349);
        MediaItem item = (MediaItem) extras.getParcelable("action_item");
        MediaItemActionProvider actionProvider = getActionProvider();
        if (holder != null && actionProvider != null) {
            new MediaItemActionsPopup(this.context, item, holder.actionViewAnchor, actionProvider, extras).show();
        }
    }

    protected DecoratedView createDecoratedView(MediaItem item, View mediaView, ViewGroup parent) {
        int layoutResId = item.type == MediaItemType.PHOTO ? 2130903300 : 2130903299;
        LocalizationManager localizationManager = this.localizationManager;
        ViewGroup frame = (ViewGroup) LocalizationManager.inflate(this.context, layoutResId, parent, false);
        LayoutParams mediaLp = mediaView.getLayoutParams();
        frame.setLayoutParams(mediaLp);
        FrameLayout.LayoutParams itemViewLp = new FrameLayout.LayoutParams(mediaLp.width, mediaLp.height);
        int i = this.styleParams.cornerIconOffsetPx;
        itemViewLp.rightMargin = i;
        itemViewLp.topMargin = i;
        itemViewLp.gravity = 53;
        mediaView.setLayoutParams(itemViewLp);
        mediaView.setClickable(false);
        View iconView = frame.findViewById(2131625060);
        frame.addView(mediaView, 0);
        frame.setTag(2131624310, iconView);
        return new DecoratedView(frame, mediaView, iconView);
    }

    protected ScaleToInsertInfo canScaleToInsert(MediaItem mediaItem, View view, float x1, float y1, float x2, float y2) {
        return null;
    }

    public void translateChildren(ScaleToInsertInfo info, float beforeTranslationY, float afterTranslationY) {
    }

    public void animateChildren(ScaleToInsertInfo info, float finalTranlationY, AnimatorListener listener) {
    }

    public void insertItem(ScaleToInsertInfo info, MediaItem newItem) {
    }

    public void onNeighboursChanged(TMediaItem tMediaItem, View view, int position) {
        if (canHaveInsertTextAction()) {
            boolean insertTextActionAvailable = position == 0 || this.mediaComposerController.getItemType(position - 1) != MediaItemType.TEXT;
            Bundle actionExtras = (Bundle) view.getTag(2131624309);
            if (actionExtras == null) {
                actionExtras = new Bundle();
                view.setTag(2131624309, actionExtras);
            }
            actionExtras.putBoolean("insert_text_before", insertTextActionAvailable);
        }
    }

    public void mergeItems(int itemPosition1, TMediaItem tMediaItem, View itemView1, int itemPosition2, TMediaItem tMediaItem2, View itemView2, TMediaItem mergedItem) {
        if (itemPosition1 + 1 != itemPosition2) {
            Logger.m185w("Unexpected positions: %d and %d", Integer.valueOf(itemPosition1), Integer.valueOf(itemPosition2));
        }
        this.mediaComposerController.removeItemAt(itemPosition1);
        this.mediaComposerController.removeItemAt(itemPosition1);
        this.mediaComposerController.insert((MediaItem) mergedItem, itemPosition1, false);
    }
}
