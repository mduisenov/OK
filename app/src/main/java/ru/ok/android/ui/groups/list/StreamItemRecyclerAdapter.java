package ru.ok.android.ui.groups.list;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.statistics.stream.StreamBannerStatisticsHandler;
import ru.ok.android.ui.image.view.PhotoLayerAnimationHelper;
import ru.ok.android.ui.stream.list.AbsStreamWithOptionsItem;
import ru.ok.android.ui.stream.list.StreamItem;
import ru.ok.android.ui.stream.list.StreamItemAdapter.StreamAdapterListener;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.StreamLayoutConfig;
import ru.ok.android.ui.stream.list.StreamPollAnswerItem;
import ru.ok.android.ui.stream.list.controller.AbsStreamItemViewController.NotifyContentChangeListener;
import ru.ok.android.ui.stream.list.controller.RecyclerViewCallback;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.utils.AdapterItemViewTypeMaxValueProvider;
import ru.ok.android.ui.utils.ViewDrawObserver;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.animation.AnimationBundleHandler;
import ru.ok.android.utils.animation.AnimationHelper;
import ru.ok.android.utils.animation.SyncBus.MessageCallback;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.FeedUtils;

public class StreamItemRecyclerAdapter extends Adapter<ViewHolder> implements OnAttachStateChangeListener, NotifyContentChangeListener, AdapterItemViewTypeMaxValueProvider, MessageCallback {
    final Activity activity;
    private AnimationBundleHandler animationBundleHandler;
    private boolean isResumed;
    private final List<StreamItem> items;
    private volatile StreamLayoutConfig layoutConfig;
    private boolean loading;
    int previousFirstVisible;
    int previousLastVisible;
    private RecyclerView recyclerView;
    private RecyclerViewCallback recyclerViewCallback;
    private ScrollListener scrollListener;
    int scrollState;
    final StreamAdapterListener streamAdapterListener;
    private final StreamItemViewController streamItemViewController;

    /* renamed from: ru.ok.android.ui.groups.list.StreamItemRecyclerAdapter.1 */
    class C09401 implements RecyclerViewCallback {
        C09401() {
        }

        public int findFirstVisibleStreamItemPosition() {
            return ((LinearLayoutManager) StreamItemRecyclerAdapter.this.recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        }

        public int findLastVisibleStreamItemPosition() {
            return ((LinearLayoutManager) StreamItemRecyclerAdapter.this.recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        }
    }

    public static class Interval {
        public static final Interval EMPTY;
        public int end;
        public int start;

        static {
            EMPTY = new Interval(0, 0);
        }

        public Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public String toString() {
            return String.format("Interval{start %d, end %d}", new Object[]{Integer.valueOf(this.start), Integer.valueOf(this.end)});
        }
    }

    public class ScrollListener extends OnScrollListener {
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            StreamItemRecyclerAdapter.this.scrollState = newState;
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (StreamItemRecyclerAdapter.this.scrollState == 1 || StreamItemRecyclerAdapter.this.scrollState == 0) {
                int firstVisibleItem = StreamItemRecyclerAdapter.this.getRecyclerViewCallback().findFirstVisibleStreamItemPosition();
                int lastVisibleItem = StreamItemRecyclerAdapter.this.getRecyclerViewCallback().findLastVisibleStreamItemPosition();
                if (StreamItemRecyclerAdapter.this.previousLastVisible != lastVisibleItem || StreamItemRecyclerAdapter.this.previousFirstVisible != firstVisibleItem || StreamItemRecyclerAdapter.this.previousLastVisible == -1 || StreamItemRecyclerAdapter.this.previousFirstVisible == -1) {
                    int start;
                    if (lastVisibleItem == -1) {
                        start = 0;
                    } else {
                        start = lastVisibleItem;
                    }
                    int i = start;
                    while (i < start + 50 && i < StreamItemRecyclerAdapter.this.getItemCount()) {
                        StreamItemRecyclerAdapter.this.getItem(i).prefetch();
                        i++;
                    }
                }
            }
        }
    }

    public StreamItemRecyclerAdapter(Activity activity, StreamItemViewController streamItemViewController, StreamAdapterListener streamAdapterListener, StreamBannerStatisticsHandler bannerStatHandler, String logContext) {
        this.items = new ArrayList();
        this.layoutConfig = new StreamLayoutConfig();
        this.scrollListener = null;
        this.scrollState = 0;
        this.previousFirstVisible = -1;
        this.previousLastVisible = -1;
        this.activity = activity;
        this.streamAdapterListener = streamAdapterListener;
        this.streamItemViewController = streamItemViewController;
        streamItemViewController.setNotifyContentChangeListener(this);
        streamItemViewController.getPlayerStateHolder().init();
        streamItemViewController.setStreamBannerStatisticsHandler(bannerStatHandler);
    }

    @NonNull
    public RecyclerViewCallback getRecyclerViewCallback() {
        if (this.recyclerViewCallback == null) {
            this.recyclerViewCallback = new C09401();
        }
        return this.recyclerViewCallback;
    }

    public void setRecyclerViewCallback(RecyclerViewCallback recyclerViewCallback) {
        this.recyclerViewCallback = recyclerViewCallback;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (this.streamItemViewController.getViewDrawObserver() == null) {
            ViewDrawObserver viewDrawObserver = new ViewDrawObserver(parent, this.streamItemViewController.getViewDrawListener());
            if (this.isResumed) {
                viewDrawObserver.resume();
            }
            this.streamItemViewController.setViewDrawObserver(viewDrawObserver);
        }
        return StreamItem.getViewHolder(this.streamItemViewController.getLayoutInflater(), parent, viewType, this.streamItemViewController);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.adapterPosition = position;
        StreamItem item = (StreamItem) this.items.get(position);
        item.bindView(holder, this.streamItemViewController, this.layoutConfig);
        item.updateForLayoutSize(holder, this.streamItemViewController, this.layoutConfig);
        holder.feed = item.feedWithState.feed;
        holder.item = item;
        holder.itemView.setTag(2131624318, item.feedWithState.feed);
    }

    public int getItemCount() {
        return this.items.size();
    }

    public int getItemViewType(int position) {
        return ((StreamItem) this.items.get(position)).viewType;
    }

    public List<StreamItem> getItems() {
        return this.items;
    }

    public void addItems(List<StreamItem> newItems) {
        this.items.addAll(newItems);
    }

    public void setItems(List<StreamItem> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        this.streamItemViewController.closeOptions();
    }

    public long getItemId(int position) {
        StreamItem item = getItem(position);
        return (item.feedWithState.feed.getId() << 8) | (255 & ((long) item.getPositionInFeed()));
    }

    public StreamItem getItem(int position) {
        return (StreamItem) this.items.get(position);
    }

    public boolean isLoading() {
        return this.loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void clear() {
        this.streamItemViewController.clear();
    }

    public void close() {
        this.streamItemViewController.close();
    }

    public void deleteItemsWithFeedId(long deleteFeedId) {
        Interval interval = feedIdInterval(deleteFeedId);
        if (interval != null) {
            for (int i = interval.start; i < interval.end; i++) {
                this.items.remove(interval.start);
            }
            notifyDataSetChanged();
        }
    }

    public void updateForLayoutSize(RecyclerView recyclerView, StreamLayoutConfig layoutConfig) {
        updateForLayoutSize(recyclerView, layoutConfig, true);
        this.layoutConfig = layoutConfig;
        this.streamItemViewController.onLayoutChanged();
    }

    public void updateForLayoutSize(RecyclerView recyclerView, StreamLayoutConfig layoutConfig, boolean notifyDataSetChanged) {
        this.layoutConfig = layoutConfig;
        this.streamItemViewController.onLayoutChanged();
        if (notifyDataSetChanged) {
            notifyDataSetChanged();
        }
    }

    public void onResume() {
        if (this.streamItemViewController.getViewDrawObserver() != null) {
            this.streamItemViewController.getViewDrawObserver().resume();
        }
        this.isResumed = true;
    }

    public void onPause() {
        if (this.streamItemViewController.getViewDrawObserver() != null) {
            this.streamItemViewController.getViewDrawObserver().pause();
        }
        this.isResumed = false;
    }

    public StreamItemViewController getStreamItemViewController() {
        return this.streamItemViewController;
    }

    private Interval getVisibleItemsIndexInterval() {
        if (this.recyclerView == null) {
            return Interval.EMPTY;
        }
        return new Interval(getRecyclerViewCallback().findFirstVisibleStreamItemPosition(), getRecyclerViewCallback().findLastVisibleStreamItemPosition());
    }

    public void onContentWithOptionsChanged() {
        Interval visibleItemsInterval = getVisibleItemsIndexInterval();
        if (visibleItemsInterval != Interval.EMPTY) {
            for (int i = visibleItemsInterval.start; i < visibleItemsInterval.end; i++) {
                if (((StreamItem) this.items.get(i)) instanceof AbsStreamWithOptionsItem) {
                    notifyItemChanged(i);
                }
            }
        }
    }

    private Interval feedIdInterval(long feedId) {
        int startIndex = -1;
        int endIndex = -1;
        for (int i = 0; i < this.items.size(); i++) {
            Feed itemFeed = ((StreamItem) this.items.get(i)).feedWithState.feed;
            if (itemFeed.getId() == feedId && startIndex == -1) {
                startIndex = i;
            }
            if (startIndex != -1 && -1 == -1 && itemFeed.getId() != feedId) {
                endIndex = i;
                break;
            }
        }
        if (startIndex == -1) {
            return null;
        }
        if (endIndex == -1) {
            endIndex = this.items.size();
        }
        return new Interval(startIndex, endIndex);
    }

    private void registerPhotoLayerAnimationCallbacks() {
        PhotoLayerAnimationHelper.registerCallback(1, this);
        PhotoLayerAnimationHelper.registerCallback(2, this);
        PhotoLayerAnimationHelper.registerCallback(3, this);
    }

    private void unregisterPhotoLayerAnimationCallbacks() {
        PhotoLayerAnimationHelper.unregisterCallback(1, this);
        PhotoLayerAnimationHelper.unregisterCallback(2, this);
        PhotoLayerAnimationHelper.unregisterCallback(3, this);
    }

    public Bundle onMessage(Message message) {
        String photoId = message.getData().getString("id");
        AnimationBundleHandler handler = getAnimationBundleHandler();
        return handler != null ? handler.onMessage(message, photoId) : null;
    }

    private AnimationBundleHandler getAnimationBundleHandler() {
        if (this.animationBundleHandler == null && this.recyclerView != null) {
            this.animationBundleHandler = AnimationHelper.createStreamPhotoAnimationHandler(this.recyclerView);
        }
        return this.animationBundleHandler;
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.addOnAttachStateChangeListener(this);
        if (recyclerView.isAttachedToWindow()) {
            onViewAttachedToWindow(recyclerView);
        }
    }

    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        recyclerView.removeOnAttachStateChangeListener(this);
        if (recyclerView.isAttachedToWindow()) {
            onDetachedFromRecyclerView(recyclerView);
        }
        this.recyclerView = null;
    }

    public void onViewAttachedToWindow(View v) {
        registerPhotoLayerAnimationCallbacks();
    }

    public void onViewDetachedFromWindow(View v) {
        unregisterPhotoLayerAnimationCallbacks();
        this.animationBundleHandler = null;
    }

    @NonNull
    public ScrollListener getScrollListener() {
        if (this.scrollListener == null) {
            this.scrollListener = new ScrollListener();
        }
        return this.scrollListener;
    }

    public void notifyPollAnswersChanged(String pollId, int firstVisibleItem, int lastVisibleItem) {
        for (int i = firstVisibleItem; i < lastVisibleItem; i++) {
            StreamItem item = (StreamItem) this.items.get(i);
            if ((item instanceof StreamPollAnswerItem) && ((StreamPollAnswerItem) item).poll.id.equals(pollId)) {
                notifyItemChanged(i);
            }
        }
    }

    public int getItemViewTypeMaxValue() {
        return 46;
    }

    public void deleteFeed(long feedId, int itemAdapterPositionHint) {
        Logger.m173d("feedId=%d itemAdapterPositionHint=%d", Long.valueOf(feedId), Integer.valueOf(itemAdapterPositionHint));
        int size = this.items.size();
        int foundItemPosition = -1;
        if (itemAdapterPositionHint >= 0 && itemAdapterPositionHint < size && ((StreamItem) this.items.get(itemAdapterPositionHint)).feedWithState.feed.getId() == feedId) {
            foundItemPosition = itemAdapterPositionHint;
        }
        if (foundItemPosition == -1) {
            for (int i = 0; i < size; i++) {
                if (((StreamItem) this.items.get(i)).feedWithState.feed.getId() == feedId) {
                    foundItemPosition = i;
                    break;
                }
            }
        }
        if (foundItemPosition < 0) {
            Logger.m185w("feed ID not found: %d", Long.valueOf(feedId));
            return;
        }
        Logger.m173d("foundItemPosition=%d", Integer.valueOf(foundItemPosition));
        int firstPosition = foundItemPosition;
        while (firstPosition > 0 && ((StreamItem) this.items.get(firstPosition - 1)).feedWithState.feed.getId() == feedId) {
            firstPosition--;
        }
        int lastPosition = foundItemPosition;
        while (lastPosition + 1 < size && ((StreamItem) this.items.get(lastPosition + 1)).feedWithState.feed.getId() == feedId) {
            lastPosition++;
        }
        for (int pos = lastPosition; pos >= firstPosition; pos--) {
            this.items.remove(pos);
        }
        Logger.m173d("deleted items from %d to %d", Integer.valueOf(firstPosition), Integer.valueOf(lastPosition));
        notifyItemRangeRemoved(firstPosition, (lastPosition - firstPosition) + 1);
    }

    public void onViewRecycled(ViewHolder holder) {
        holder.item.onUnbindView(holder);
        holder.item = null;
    }

    public AbsListView.OnScrollListener getScrollBlocker() {
        return this.streamItemViewController.getImageLoadBlocker();
    }

    public void deleteByOwner(ArrayList<String> userIds, ArrayList<String> groupIds) {
        long startTime = System.currentTimeMillis();
        Logger.m173d(">>> userIds=%s groupIds=%s", userIds, groupIds);
        Feed matchedFeed = null;
        int removedItemCount = 0;
        int removeRangeEnd = -1;
        for (int i = this.items.size() - 1; i >= 0; i--) {
            Feed feed = ((StreamItem) this.items.get(i)).feedWithState.feed;
            boolean removed = false;
            if (feed == matchedFeed) {
                this.items.remove(i);
                removed = true;
                removedItemCount++;
            } else if (FeedUtils.matchesFeedOwner(feed, userIds, groupIds)) {
                matchedFeed = feed;
                this.items.remove(i);
                removed = true;
                removedItemCount++;
            }
            if (removed) {
                if (removeRangeEnd == -1) {
                    removeRangeEnd = i;
                }
            } else if (removeRangeEnd != -1) {
                notifyItemRangeRemoved(i + 1, removeRangeEnd - i);
                removeRangeEnd = -1;
            }
        }
        if (removeRangeEnd != -1) {
            notifyItemRangeRemoved(0, removeRangeEnd + 1);
        }
        Logger.m173d("<<< removed %d items in %d ms", Integer.valueOf(removedItemCount), Long.valueOf(System.currentTimeMillis() - startTime));
    }
}
