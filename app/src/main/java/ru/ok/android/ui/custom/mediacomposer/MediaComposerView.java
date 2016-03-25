package ru.ok.android.ui.custom.mediacomposer;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import ru.ok.android.C0206R;
import ru.ok.android.services.processors.mediatopic.MediaPayloadBuilder;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.custom.mediacomposer.MediaTopicMessage.Stats;
import ru.ok.android.ui.custom.mediacomposer.ScaleToInsertTouchListener.ScaleToInsertProvider;
import ru.ok.android.ui.custom.mediacomposer.SwipeDismissTouchListener.OnDismissCallback;
import ru.ok.android.ui.custom.mediacomposer.adapter.MediaItemAdapter;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.animation.SimpleAnimatorListener;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.mediatopics.MediaItemType;
import ru.ok.model.places.Place;
import ru.ok.model.settings.MediaComposerSettings;

public class MediaComposerView extends LinearLayout implements ScaleToInsertProvider {
    MediaItemAdapter adapter;
    final ContentResolver contentResolver;
    private final MediaComposerController controller;
    View insertableChildPrototype;
    boolean isEditable;
    final LayoutInflater layoutInflater;
    MediaComposerContentListener mediaComposerContentListener;
    int nextViewId;
    private OnLayoutListener onLayoutListener;
    ScaleToInsertTouchListener scaleToInsertTouchListener;
    private String statMode;
    boolean stateSaved;
    final MediaComposerStyleParams styleParams;
    public String textHint;
    private TextView textViewWithHint;

    public static class LayoutParams extends android.widget.LinearLayout.LayoutParams {
        public int widthPercents;

        public LayoutParams(int width, int height) {
            super(width, height);
            this.widthPercents = -1;
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = null;
            try {
                a = context.obtainStyledAttributes(attrs, C0206R.styleable.MediaComposerView_Layout);
                this.widthPercents = a.getInt(0, -1);
            } finally {
                a.recycle();
            }
        }
    }

    public interface MediaComposerContentListener {
        void onMediaComposerContentChanged();
    }

    public class MediaComposerController implements MediaItemContentListener, OnDismissCallback {
        private boolean isMediaTopicEmpty;
        private MediaTopicMessage mediaTopicMessage;
        @NonNull
        private MediaTopicType mediaTopicType;
        @NonNull
        private MediaComposerSettings settings;

        /* renamed from: ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController.1 */
        class C06721 extends SimpleAnimatorListener {
            final /* synthetic */ View val$itemView;

            C06721(View view) {
                this.val$itemView = view;
            }

            public void onAnimationEnd(Animator animation) {
                this.val$itemView.requestFocus();
            }
        }

        public MediaComposerController() {
            this.mediaTopicMessage = new MediaTopicMessage();
            this.isMediaTopicEmpty = true;
            this.settings = new MediaComposerSettings();
            this.mediaTopicType = MediaTopicType.USER;
        }

        public MediaTopicMessage getMediaTopicMessage() {
            return MediaComposerView.copy(this.mediaTopicMessage);
        }

        public MediaTopicMessage getMediaTopicMessageUnsafe() {
            return this.mediaTopicMessage;
        }

        public ArrayList<String> getWithFriendsUids() {
            return this.mediaTopicMessage.getWithFriendsUids();
        }

        public Place getWithPlace() {
            return this.mediaTopicMessage.getWithPlace();
        }

        public Stats getStats() {
            return this.mediaTopicMessage.getStats();
        }

        public boolean isEmpty() {
            return this.mediaTopicMessage.isEmpty();
        }

        public int getItemsCount() {
            return this.mediaTopicMessage.getItemsCount();
        }

        public int getBlockCount() {
            return MediaPayloadBuilder.getBlockCount(this.mediaTopicMessage);
        }

        public int getMaxAllowedBlockCount() {
            return this.mediaTopicType == MediaTopicType.USER ? this.settings.maxBlockCount : this.settings.maxGroupBlockCount;
        }

        public int getMaxTextLength() {
            return this.settings.maxTextLength;
        }

        public MediaItemType getItemType(int position) {
            return this.mediaTopicMessage.getItem(position).type;
        }

        MediaItem getItem(int position) {
            return this.mediaTopicMessage.getItem(position);
        }

        public int getItemPosition(MediaItem item) {
            int count = MediaComposerView.this.getChildCount();
            int viewId = item.viewId;
            for (int i = 0; i < count; i++) {
                if (MediaComposerView.this.getChildAt(i).getId() == viewId) {
                    return i;
                }
            }
            return -1;
        }

        public void reset(MediaTopicMessage newMediaTopicMessage) {
            int i;
            this.mediaTopicMessage = MediaComposerView.copy(newMediaTopicMessage);
            MediaComposerView.this.removeAllViews();
            int itemCount = this.mediaTopicMessage.getItemsCount();
            for (i = 0; i < itemCount; i++) {
                MediaItem item = this.mediaTopicMessage.getItem(i);
                View childView = createItemView(item);
                if (i == itemCount - 1 && item.type == MediaItemType.TEXT) {
                    adjustLastTextViewBeforeAdd(childView);
                }
                MediaComposerView.this.addView(childView);
            }
            for (i = 0; i < itemCount; i++) {
                MediaComposerView.this.adapter.onNeighboursChanged(this.mediaTopicMessage.getItem(i), MediaComposerView.this.getChildAt(i), i);
            }
            MediaComposerView.this.notifyContentChanged();
        }

        public void setSettings(MediaComposerSettings settings) {
            this.settings = settings;
        }

        public void setMediaTopicType(MediaTopicType type) {
            this.mediaTopicType = type;
        }

        public void setWithPlace(Place place) {
            this.mediaTopicMessage.setWithPlace(place);
        }

        public void clearWithPlace() {
            this.mediaTopicMessage.setWithPlace(null);
        }

        public void setWithFriends(ArrayList<String> uids) {
            this.mediaTopicMessage.setWithFriendsUids(uids);
        }

        public void restartFromBlankPage() {
            this.mediaTopicMessage = new MediaTopicMessage();
            MediaComposerView.this.removeAllViews();
            addAfterEnd(MediaItem.emptyText());
        }

        void changeIsEditable(boolean isEditable) {
            int count = this.mediaTopicMessage.getItemsCount();
            for (int i = 0; i < count; i++) {
                MediaItem item = this.mediaTopicMessage.getItem(i);
                MediaComposerView.this.adapter.updateViewEdiatable(MediaComposerView.this.getChildAt(i), item, MediaComposerView.this, isEditable);
            }
        }

        public void addAfterEnd(MediaItem item) {
            if (MediaComposerView.this.isEditable) {
                int childCount = MediaComposerView.this.getChildCount();
                if (childCount > 0 && this.mediaTopicMessage.getItem(childCount - 1).type == MediaItemType.TEXT) {
                    adjustExistingTextViewNormalHeight(MediaComposerView.this.getChildAt(childCount - 1));
                }
                View view = createItemView(item);
                if (item.type == MediaItemType.TEXT) {
                    adjustLastTextViewBeforeAdd(view);
                }
                add(item, view);
                view.requestFocus();
                if (item.type == MediaItemType.TEXT) {
                    KeyBoardUtils.showKeyBoard(view.getWindowToken());
                }
                MediaComposerView.this.notifyContentChanged();
                return;
            }
            Logger.m184w("MediaComposerView in non-editable mode");
            Toast.makeText(MediaComposerView.this.getContext(), "Non-editable mode", 0).show();
        }

        public void insertAtCursor(MediaItem item) {
            Logger.m173d("item=%s", item);
            if (!MediaComposerView.this.isEditable) {
                Logger.m184w("MediaComposerView in non-editable mode");
                Toast.makeText(MediaComposerView.this.getContext(), "Non-editable mode", 0).show();
            } else if (item.type != MediaItemType.TEXT) {
                int insertPosition = MediaComposerView.this.getChildCount();
                int selectedPosition = getSelectedPosition();
                boolean isSelectedLastItem = selectedPosition == MediaComposerView.this.getChildCount() + -1;
                if (selectedPosition == -1) {
                    insertPosition = MediaComposerView.this.getChildCount();
                } else if (this.mediaTopicMessage.getItem(selectedPosition).type == MediaItemType.TEXT) {
                    EditText textView = (EditText) MediaComposerView.this.getChildAt(selectedPosition);
                    int cursorPosition = textView.getSelectionStart();
                    if (cursorPosition == -1) {
                        insertPosition = selectedPosition + 1;
                    } else {
                        Editable fullText = textView.getText();
                        CharSequence textPart1 = fullText.subSequence(0, cursorPosition);
                        CharSequence textPart2 = fullText.subSequence(cursorPosition, fullText.length());
                        boolean emptyPart1 = textPart1 == null || TextUtils.isEmpty(textPart1.toString().trim());
                        boolean emptyPart2 = textPart2 == null || TextUtils.isEmpty(textPart2.toString().trim());
                        if (emptyPart1 && emptyPart2) {
                            insertPosition = selectedPosition;
                        } else if (emptyPart2) {
                            insertPosition = selectedPosition + 1;
                            if (isSelectedLastItem) {
                                adjustExistingTextViewNormalHeight(textView);
                            }
                        } else if (emptyPart1) {
                            insertPosition = selectedPosition;
                        } else {
                            textView.setText(textPart1);
                            adjustExistingTextViewNormalHeight(textView);
                            MediaItem textItem2 = MediaItem.text(textPart2.toString());
                            View textView2 = (EditText) createItemView(textItem2);
                            if (isSelectedLastItem) {
                                adjustLastTextViewBeforeAdd(textView2);
                            }
                            insert(textItem2, selectedPosition + 1, textView2);
                            insertPosition = selectedPosition + 1;
                        }
                    }
                } else {
                    insertPosition = selectedPosition;
                }
                Logger.m173d("insertPosition=%d", Integer.valueOf(insertPosition));
                insert(item, insertPosition);
                if (insertPosition == MediaComposerView.this.getChildCount() - 1) {
                    addAfterEnd(MediaItem.emptyText());
                }
                MediaComposerView.this.notifyContentChanged();
            }
        }

        public void insert(MediaItem item, int position) {
            insert(item, position, false);
        }

        public void insert(MediaItem item, int position, boolean withAnimation) {
            Logger.m173d("item=%s position=%d", item, Integer.valueOf(position));
            View itemView = createItemView(item);
            MediaComposerView.this.addView(itemView, position);
            this.mediaTopicMessage.insert(position, item);
            notifyNeighboursChanged(position - 1, position, position + 1);
            if (withAnimation) {
                LayoutTransitionAnimator animator = new LinearLayoutInsertTransitionAnimator(MediaComposerView.this, position, MediaComposerView.this.styleParams.removeAnimationTime);
                if (itemView.isFocusable()) {
                    animator.startAnimation(new C06721(itemView));
                } else {
                    animator.startAnimation();
                }
            } else if (itemView.isFocusable()) {
                itemView.requestFocus();
            }
        }

        private void add(MediaItem item, View view) {
            Logger.m172d("item=%s");
            MediaComposerView.this.addView(view);
            this.mediaTopicMessage.add(item);
            int position = this.mediaTopicMessage.getItemsCount() - 1;
            notifyNeighboursChanged(position - 1, position);
        }

        private void notifyNeighboursChanged(int... positions) {
            for (int position : positions) {
                if (position >= 0 && position < this.mediaTopicMessage.getItemsCount()) {
                    MediaComposerView.this.adapter.onNeighboursChanged(this.mediaTopicMessage.getItem(position), MediaComposerView.this.getChildAt(position), position);
                }
            }
        }

        public void insert(MediaItem item, int position, View view) {
            Logger.m173d("item=%s position=%d viewId=%d", item, Integer.valueOf(position), Integer.valueOf(view.getId()));
            initItemView(item, view);
            MediaComposerView.this.addView(view, position);
            this.mediaTopicMessage.insert(position, item);
            notifyNeighboursChanged(position - 1, position);
        }

        public int generateNextViewId() {
            MediaComposerView mediaComposerView = MediaComposerView.this;
            int i = mediaComposerView.nextViewId;
            mediaComposerView.nextViewId = i + 1;
            return i;
        }

        private View createItemView(MediaItem item) {
            int viewId;
            if (item.viewId == 0) {
                viewId = generateNextViewId();
                item.viewId = viewId;
            } else {
                viewId = item.viewId;
            }
            View view = MediaComposerView.this.adapter.createView(item, MediaComposerView.this, MediaComposerView.this.isEditable, viewId);
            view.setId(viewId);
            initItemView(item, view);
            return view;
        }

        private void initItemView(MediaItem item, View view) {
            Logger.m173d("item=%s viewId=%d", item, Integer.valueOf(view.getId()));
            if (MediaComposerView.this.styleParams.swipeToDismissEnabled && item.type != MediaItemType.TEXT) {
                SwipeDismissTouchListener swipeDismissTouchListener = new SwipeDismissTouchListener(view, null, this);
                OnTouchListener onTouchListener = (OnTouchListener) view.getTag(2131624352);
                if (onTouchListener != null) {
                    TouchListenersAggregator touchAggregator = new TouchListenersAggregator();
                    touchAggregator.add(swipeDismissTouchListener);
                    touchAggregator.add(onTouchListener);
                    view.setOnTouchListener(touchAggregator);
                } else {
                    view.setOnTouchListener(swipeDismissTouchListener);
                }
            }
            item.setMediaItemContentListener(this);
        }

        public int remove(MediaItem mediaItem, boolean withAnimation, boolean willAddAnotherItem) {
            Logger.m173d("mediaItem=%s withAnimation=%s willAddAnotherItem=%s", mediaItem, Boolean.valueOf(withAnimation), Boolean.valueOf(willAddAnotherItem));
            if (MediaComposerView.this.isEditable) {
                int childCount = MediaComposerView.this.getChildCount();
                int i = 0;
                while (i < childCount) {
                    if (MediaComposerView.this.getChildAt(i).getId() == mediaItem.viewId) {
                        removeItemAt(i);
                        if (withAnimation) {
                            new LinearLayoutRemoveTransitionAnimator(MediaComposerView.this, i, MediaComposerView.this.styleParams.removeAnimationTime).startAnimation();
                        }
                        if (!willAddAnotherItem && i > 0 && i + 1 < childCount) {
                            merge(i - 1);
                        }
                        if (willAddAnotherItem || i != childCount - 1) {
                            return i;
                        }
                        addAfterEnd(MediaItem.emptyText());
                        return i;
                    }
                    i++;
                }
                return -1;
            }
            Logger.m184w("MediaComposerView in non-editable mode");
            Toast.makeText(MediaComposerView.this.getContext(), "Non-editable mode", 0).show();
            return -1;
        }

        private void merge(int i) {
            Logger.m173d("position=%d", Integer.valueOf(i));
            int size = this.mediaTopicMessage.getItemsCount();
            if (i >= 0 && i + 1 <= size) {
                MediaItem item = this.mediaTopicMessage.getItem(i);
                MediaItem next = this.mediaTopicMessage.getItem(i + 1);
                MediaItem merged = item.append(next);
                if (merged != null) {
                    int i2 = i;
                    MediaComposerView.this.adapter.mergeItems(i2, item, MediaComposerView.this.getChildAt(i), i + 1, next, MediaComposerView.this.getChildAt(i + 1), merged);
                }
            }
        }

        public void removeItemAt(int position) {
            Logger.m173d("position=%d", Integer.valueOf(position));
            View childView = MediaComposerView.this.getChildAt(position);
            MediaItem item = this.mediaTopicMessage.getItem(position);
            MediaComposerView.this.removeViewAt(position);
            MediaComposerView.this.adapter.disposeView(childView, item);
            this.mediaTopicMessage.removeItem(position);
            notifyNeighboursChanged(position - 1, position);
            MediaComposerView.this.notifyContentChanged();
        }

        public void update(MediaItem mediaItem) {
            Logger.m173d("mediaItem=%s", mediaItem);
            update(mediaItem, null, false);
        }

        public void update(MediaItem mediaItem, boolean useOldView) {
            Logger.m173d("mediaItem=%s useOldView=%s", mediaItem, Boolean.valueOf(useOldView));
            update(mediaItem, null, useOldView);
        }

        public void update(MediaItem mediaItem, View newView, boolean useOldView) {
            String str = "mediaItem=%s viewId=%s useOldView=%s";
            Object[] objArr = new Object[3];
            objArr[0] = mediaItem;
            objArr[1] = newView == null ? "null" : Integer.toString(newView.getId());
            objArr[2] = Boolean.valueOf(useOldView);
            Logger.m173d(str, objArr);
            MediaTopicMessage message = this.mediaTopicMessage;
            int childCount = message.getItemsCount();
            for (int i = 0; i < childCount; i++) {
                if (message.getItem(i).viewId == mediaItem.viewId) {
                    message.set(i, mediaItem);
                    if (!useOldView) {
                        MediaComposerView.this.removeViewAt(i);
                        if (newView == null) {
                            newView = createItemView(mediaItem);
                        }
                        MediaComposerView.this.addView(newView, i);
                    }
                    notifyNeighboursChanged(i - 1, i, i + 1);
                    return;
                }
            }
        }

        public MediaItem findItem(MediaItem lookupItem) {
            MediaTopicMessage message = this.mediaTopicMessage;
            int childCount = message.getItemsCount();
            for (int i = 0; i < childCount; i++) {
                MediaItem oldItem = message.getItem(i);
                if (oldItem.viewId == lookupItem.viewId) {
                    return oldItem;
                }
            }
            return null;
        }

        public View getItemView(MediaItem item) {
            int itemViewId = item.viewId;
            if (itemViewId == 0) {
                Logger.m184w("View ID not set");
                return null;
            }
            int count = MediaComposerView.this.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = MediaComposerView.this.getChildAt(i);
                int viewId = view.getId();
                if (viewId == 0) {
                    Logger.m184w("View ID not set");
                } else if (itemViewId == viewId) {
                    return view;
                }
            }
            Logger.m185w("View not found for item view ID: %d", Integer.valueOf(itemViewId));
            return null;
        }

        public void onDismiss(View view, Object token) {
            if (!MediaComposerView.this.stateSaved) {
                int childCount = MediaComposerView.this.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    if (MediaComposerView.this.getChildAt(i) == view) {
                        MediaItem removedItem = this.mediaTopicMessage.getItem(i);
                        MediaComposerStats.swipeToDismiss(removedItem, MediaComposerView.this.statMode);
                        remove(removedItem, false, false);
                        return;
                    }
                }
            }
        }

        public void onGoingToDismiss(View view, Object token) {
            MediaComposerView.this.performHapticFeedback(view);
        }

        public void onGoingToStay(View view, Object token) {
            MediaComposerView.this.performHapticFeedback(view);
        }

        private int getSelectedPosition() {
            View focusedChild = MediaComposerView.this.getFocusedChild();
            if (focusedChild != null) {
                int count = MediaComposerView.this.getChildCount();
                for (int i = 0; i < count; i++) {
                    if (MediaComposerView.this.getChildAt(i) == focusedChild) {
                        return i;
                    }
                }
            }
            return -1;
        }

        private void adjustExistingTextViewNormalHeight(View view) {
            getChildLayoutParams(view).height = -2;
            view.requestLayout();
        }

        private void adjustLastTextViewBeforeAdd(View view) {
            getChildLayoutParams(view).height = -1;
        }

        private LayoutParams getChildLayoutParams(View view) {
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            if (params != null) {
                return params;
            }
            params = MediaComposerView.this.generateDefaultLayoutParams();
            view.setLayoutParams(params);
            return params;
        }

        public void onMediaItemContentChanged(MediaItem item) {
            MediaComposerView.this.notifyContentChanged();
        }
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR;
        final boolean isEditable;
        final int nextViewId;
        final Parcelable superState;

        /* renamed from: ru.ok.android.ui.custom.mediacomposer.MediaComposerView.SavedState.1 */
        static class C06731 implements Creator<SavedState> {
            C06731() {
            }

            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        public SavedState(Parcel source) {
            super(source);
            this.superState = source.readParcelable(SavedState.class.getClassLoader());
            this.nextViewId = source.readInt();
            this.isEditable = source.readInt() != 0;
        }

        SavedState(Parcelable superState, int nextViewId, boolean isEditable) {
            super(superState);
            this.superState = superState;
            this.nextViewId = nextViewId;
            this.isEditable = isEditable;
        }

        public int describeContents() {
            return 0;
        }

        public String toString() {
            return "SavedState[nextViewId=" + this.nextViewId + " isEditable=" + this.isEditable + "]";
        }

        public void writeToParcel(Parcel dest, int flags) {
            int i = 0;
            super.writeToParcel(dest, flags);
            dest.writeParcelable(this.superState, 0);
            dest.writeInt(this.nextViewId);
            if (this.isEditable) {
                i = 1;
            }
            dest.writeInt(i);
        }

        static {
            CREATOR = new C06731();
        }
    }

    public static class ScaleToInsertInfo extends ru.ok.android.ui.custom.mediacomposer.ScaleToInsertTouchListener.ScaleToInsertInfo {
        public MediaItem item1;
        public MediaItem item2;
    }

    public MediaComposerView(Context context) {
        this(context, null);
    }

    public MediaComposerView(Context context, AttributeSet attrs) {
        this(context, attrs, 2130771994);
    }

    public MediaComposerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        this.controller = new MediaComposerController();
        this.nextViewId = 1;
        this.stateSaved = false;
        this.isEditable = true;
        this.statMode = "new";
        setClipChildren(false);
        this.contentResolver = context.getContentResolver();
        this.layoutInflater = LayoutInflater.from(context);
        this.styleParams = new MediaComposerStyleParams(context, attrs, defStyle, 0);
        this.textHint = LocalizationManager.getString(context, 2131166183);
        if (this.styleParams.scaleToInsertEnabled) {
            this.scaleToInsertTouchListener = new ScaleToInsertTouchListener(this);
        }
    }

    public MediaComposerStyleParams getStyleParams() {
        return this.styleParams;
    }

    public void setAdapter(MediaItemAdapter adapter) {
        this.adapter = adapter;
    }

    public void setMediaComposerContentListener(MediaComposerContentListener listener) {
        this.mediaComposerContentListener = listener;
    }

    public void setStatMode(String statMode) {
        if (statMode != null) {
            this.statMode = statMode;
        }
    }

    public void setEditable(boolean isEditable) {
        if (isEditable != this.isEditable) {
            this.isEditable = isEditable;
            this.controller.changeIsEditable(isEditable);
        }
    }

    public void onResume() {
        this.stateSaved = false;
    }

    public void onDestroy() {
        this.adapter.onDestroy();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.adapter.onDetachedFromWindow();
    }

    public void setOrientation(int orientation) {
        if (orientation != 1) {
            throw new IllegalArgumentException("MediaComposerView supports only vertical orientation: " + orientation);
        }
        super.setOrientation(orientation);
    }

    public MediaComposerController getController() {
        return this.controller;
    }

    public void setBlankTextHint(String textHint) {
        this.textHint = textHint;
        TextView textView = this.textViewWithHint;
        if (textView != null) {
            textView.setHint(textHint);
        }
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        LayoutParams params = new LayoutParams(getContext(), attrs);
        if (attrs.getAttributeValue("android", "layout_marginBottom") == null) {
            params.bottomMargin = this.styleParams.itemsDefaultVerticalSpacing;
        }
        return params;
    }

    protected Parcelable onSaveInstanceState() {
        this.stateSaved = true;
        SavedState savedState = new SavedState(super.onSaveInstanceState(), this.nextViewId, this.isEditable);
        Logger.m172d("" + savedState);
        return savedState;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        Logger.m172d("" + savedState);
        this.nextViewId = savedState.nextViewId;
        this.isEditable = savedState.isEditable;
        super.onRestoreInstanceState(savedState.superState);
    }

    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int widthPercents = lp.widthPercents;
        if (widthPercents < 0) {
            super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
            return;
        }
        int widthMode = MeasureSpec.getMode(parentWidthMeasureSpec);
        int widthSize = MeasureSpec.getSize(parentWidthMeasureSpec);
        switch (widthMode) {
            case LinearLayoutManager.INVALID_OFFSET /*-2147483648*/:
            case 1073741824:
                parentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthMode, (widthSize * widthPercents) / 100);
                break;
        }
        child.measure(getChildMeasureSpec(parentWidthMeasureSpec, (((getPaddingLeft() + getPaddingRight()) + lp.leftMargin) + lp.rightMargin) + widthUsed, lp.width), getChildMeasureSpec(parentHeightMeasureSpec, (((getPaddingTop() + getPaddingBottom()) + lp.topMargin) + lp.bottomMargin) + heightUsed, lp.height));
    }

    public View insertNewChild(ru.ok.android.ui.custom.mediacomposer.ScaleToInsertTouchListener.ScaleToInsertInfo info) {
        ScaleToInsertInfo mcInfo = (ScaleToInsertInfo) info;
        MediaItem item = MediaItem.emptyText();
        View view;
        if (info.parent == this && mcInfo.item1 != mcInfo.item2) {
            MediaComposerStats.pinchOutInsert(this.statMode, this.controller.getItemType(info.pos1), this.controller.getItemType(info.pos2));
            this.controller.insert(item, info.pos1 + 1);
            view = getChildAt(info.pos1 + 1);
            view.requestFocus();
            return view;
        } else if (info.parent == this || mcInfo.item1 != mcInfo.item2) {
            performHapticFeedback(this);
            return null;
        } else {
            this.adapter.insertItem(mcInfo, item);
            view = getChildAt(info.pos1 + 1);
            view.requestFocus();
            return view;
        }
    }

    public boolean canInsertAfter(int position) {
        return position >= 0 && position + 1 < getChildCount() && this.controller.getItemType(position) != MediaItemType.TEXT && this.controller.getItemType(position + 1) != MediaItemType.TEXT;
    }

    public ru.ok.android.ui.custom.mediacomposer.ScaleToInsertTouchListener.ScaleToInsertInfo canScaleToInsert(float x1, float y1, float x2, float y2) {
        int currentBlockCount = this.controller.getBlockCount();
        int maxBlockCount = this.controller.getMaxAllowedBlockCount();
        int remainingBlocksLimit = maxBlockCount == 0 ? Integer.MAX_VALUE : maxBlockCount > currentBlockCount ? maxBlockCount - currentBlockCount : 0;
        boolean canInsert = remainingBlocksLimit >= 2;
        int pos1 = ViewUtils.getChildPositionByY(this, y1);
        int pos2 = ViewUtils.getChildPositionByY(this, y2);
        boolean swapPosition = pos2 < pos1;
        if (swapPosition) {
            pos1 ^= pos2;
            pos2 ^= pos1;
            pos1 ^= pos2;
        }
        ScaleToInsertInfo info;
        if (pos2 == pos1 + 1 && canInsert && canInsertAfter(pos1)) {
            info = new ScaleToInsertInfo();
            info.pos1 = pos1;
            info.pos2 = pos2;
            info.parent = this;
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
            info.item1 = this.controller.getItem(pos1);
            info.item2 = this.controller.getItem(pos2);
            return info;
        } else if (pos1 == pos2 && canInsert) {
            MediaItem mediaItem = this.controller.getItem(pos1);
            View view = getChildAt(pos1);
            int x0 = view.getLeft();
            int y0 = view.getTop();
            return this.adapter.canScaleToInsert(mediaItem, view, x1 - ((float) x0), y1 - ((float) y0), x2 - ((float) x0), y2 - ((float) y0));
        } else if (pos2 != pos1 + 2 || !canRemove(pos1 + 1)) {
            return null;
        } else {
            info = new ScaleToInsertInfo();
            info.pos1 = pos1;
            info.pos2 = pos2;
            info.parent = this;
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
            info.mode = 2;
            info.item1 = this.controller.getItem(pos1);
            info.item2 = this.controller.getItem(pos2);
            return info;
        }
    }

    public void disallowInterceptTouchEvent(boolean disallow) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallow);
        }
    }

    public void translateChildren(ru.ok.android.ui.custom.mediacomposer.ScaleToInsertTouchListener.ScaleToInsertInfo info, float beforeTranslationY, float afterTranslationY) {
        int childCount = getChildCount();
        ScaleToInsertInfo mcInfo = (ScaleToInsertInfo) info;
        int i;
        if (info.parent == this && mcInfo.item1 != mcInfo.item2) {
            int pos1 = info.pos1;
            int pos2 = info.pos2;
            ViewUtils.setChildrenTranslationY(this, 0, pos1, beforeTranslationY);
            ViewUtils.setChildrenTranslationY(this, pos2, childCount - 1, afterTranslationY);
            if (pos2 > pos1 + 1) {
                for (i = pos1 + 1; i < pos2; i++) {
                    getChildAt(i).setTranslationY(((((float) (pos2 - i)) * beforeTranslationY) + (((float) (i - pos1)) * afterTranslationY)) / ((float) (pos2 - pos1)));
                }
            }
        } else if (mcInfo.parent != this && mcInfo.item1 == mcInfo.item2) {
            for (i = 0; i < childCount; i++) {
                if (getChildAt(i) == mcInfo.parent) {
                    ViewUtils.setChildrenTranslationY(this, 0, i - 1, beforeTranslationY);
                    ViewUtils.setChildrenTranslationY(this, i + 1, childCount - 1, afterTranslationY);
                    this.adapter.translateChildren(mcInfo, beforeTranslationY, afterTranslationY);
                    return;
                }
            }
        }
    }

    public void animateChildren(ru.ok.android.ui.custom.mediacomposer.ScaleToInsertTouchListener.ScaleToInsertInfo info, float finalTranslationY, AnimatorListener listener) {
        int childCount = getChildCount();
        ScaleToInsertInfo mcInfo = (ScaleToInsertInfo) info;
        ViewUtils.startAnimateChildrenTranslationY(this, 0, childCount - 1, finalTranslationY, listener);
        if (mcInfo.parent != this && mcInfo.item1 == mcInfo.item2) {
            for (int i = 0; i < childCount; i++) {
                if (getChildAt(i) == mcInfo.parent) {
                    this.adapter.animateChildren(mcInfo, finalTranslationY, null);
                    return;
                }
            }
        }
    }

    public void removeChildPinchIn(int position) {
        MediaItemType type1;
        MediaItemType type2 = null;
        Logger.m173d("position=%d", Integer.valueOf(position));
        int size = this.controller.getItemsCount();
        if (position - 1 < 0 || position - 1 >= size) {
            type1 = null;
        } else {
            type1 = this.controller.getItemType(position - 1);
        }
        if (position + 1 >= 0 && position + 1 < size) {
            type2 = this.controller.getItemType(position + 1);
        }
        if (!(type1 == null || type2 == null)) {
            MediaComposerStats.pinchInDelete(this.statMode, type1, type2);
        }
        this.controller.removeItemAt(position);
        performHapticFeedback(this);
    }

    public boolean canRemove(int position) {
        return position > 0 && position + 1 < getChildCount() && this.controller.getItemType(position) == MediaItemType.TEXT && this.controller.getItemType(position - 1) != MediaItemType.TEXT && this.controller.getItemType(position + 1) != MediaItemType.TEXT && this.controller.getItem(position).isEmpty();
    }

    public void onAfterItemRemoved(int removedPosition) {
        Logger.m173d("removedPosition=%d", Integer.valueOf(removedPosition));
        int childCount = getChildCount();
        if (removedPosition > 0 && removedPosition < childCount) {
            this.controller.merge(removedPosition - 1);
        }
    }

    public int getInsertHeight() {
        if (this.insertableChildPrototype == null) {
            this.insertableChildPrototype = this.adapter.createView(MediaItem.emptyText(), this, true, 0);
            measureChildWithMargins(this.insertableChildPrototype, MeasureSpec.makeMeasureSpec(getWidth(), LinearLayoutManager.INVALID_OFFSET), 0, MeasureSpec.makeMeasureSpec(0, 0), 0);
        }
        return this.insertableChildPrototype.getMeasuredHeight();
    }

    public void setOnLayoutListener(OnLayoutListener l) {
        this.onLayoutListener = l;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.onLayoutListener != null) {
            this.onLayoutListener.onLayout();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.isEditable && this.styleParams.scaleToInsertEnabled && this.scaleToInsertTouchListener != null) {
            return this.scaleToInsertTouchListener.onInterceptTouchEvent(ev);
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (this.styleParams.scaleToInsertEnabled && this.scaleToInsertTouchListener != null && this.scaleToInsertTouchListener.onTouch(ev)) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    private void performHapticFeedback(View view) {
        if (isHapticFeedbackEnabled()) {
            view.performHapticFeedback(0, 0);
        }
    }

    public void addView(View child, int index) {
        super.addView(child, index);
        updateTextHint();
    }

    public void removeViewAt(int index) {
        super.removeViewAt(index);
        updateTextHint();
    }

    private void updateTextHint() {
        TextView setHint;
        TextView unsetHint = this.textViewWithHint;
        if (getChildCount() == 1) {
            View onlyView = getChildAt(0);
            if (onlyView instanceof TextView) {
                setHint = (TextView) onlyView;
            } else {
                setHint = null;
            }
        } else {
            setHint = null;
        }
        if (unsetHint != null) {
            unsetHint.setHint("");
        }
        if (setHint != null) {
            setHint.setHint(this.textHint);
        }
        this.textViewWithHint = setHint;
    }

    void notifyContentChanged() {
        if (this.mediaComposerContentListener != null) {
            this.mediaComposerContentListener.onMediaComposerContentChanged();
        }
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -2);
    }

    private static MediaTopicMessage copy(MediaTopicMessage src) {
        Parcel parcel = Parcel.obtain();
        src.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        MediaTopicMessage copy = new MediaTopicMessage(parcel);
        parcel.recycle();
        return copy;
    }
}
