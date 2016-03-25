package ru.ok.android.ui.stream.list;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView.RecyclerListener;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.statistics.stream.StreamBannerStatisticsHandler;
import ru.ok.android.ui.custom.animationlist.DataChangeBindAdapter;
import ru.ok.android.ui.stream.StreamListStatistics;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.controller.AbsStreamItemViewController.NotifyContentChangeListener;
import ru.ok.android.ui.stream.list.controller.DefaultStreamViewController;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.photos.PhotosFeedAdapter.PhotosAdapterListener;
import ru.ok.android.ui.stream.view.FeedOptionsPopupWindow.FeedOptionsPopupListener;
import ru.ok.android.ui.utils.ViewDrawObserver;
import ru.ok.android.ui.utils.ViewDrawObserver.ViewDrawListener;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.MakePresentRequest;
import ru.ok.java.api.request.serializer.http.RequestHttpSerializer;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.LikeInfoContext;

public class StreamItemAdapter extends DataChangeBindAdapter<List<StreamItem>, StreamItem, ViewHolder> implements OnScrollListener, RecyclerListener, NotifyContentChangeListener {
    private boolean isResumed;
    private final ArrayList<StreamItem> items;
    int lastFirstVisible;
    int lastVisibleCount;
    StreamLayoutConfig layoutConfig;
    int scrollState;
    ShownOnScrollListener shownOnScrollListener;
    private final StreamItemViewController streamItemViewController;
    private ViewDrawListener viewDrawListener;

    public interface FeedMediaTopicViewListener {
        void onUsersSelected(int i, Feed feed, ArrayList<UserInfo> arrayList);
    }

    public interface TextMediaTopicEditListener {
        void onMediaTopicTextEditClick(String str, int i, String str2);
    }

    public interface StreamAdapterListener extends FeedMediaTopicViewListener, TextMediaTopicEditListener, PhotosAdapterListener, FeedOptionsPopupListener {
        void onCommentClicked(int i, Feed feed, DiscussionSummary discussionSummary);

        void onGeneralUsersInfosClicked(int i, Feed feed, ArrayList<GeneralUserInfo> arrayList, String str);

        void onLikeClicked(int i, Feed feed, LikeInfoContext likeInfoContext);

        void onMediaTopicClicked(int i, Feed feed, DiscussionSummary discussionSummary);
    }

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public int adapterPosition;
        public Feed feed;
        public StreamItem item;
        public final int originalBottomPadding;
        public final int originalLeftPadding;
        public final int originalRightPadding;
        public final int originalTopPadding;

        public ViewHolder(View view) {
            super(view);
            this.originalLeftPadding = view.getPaddingLeft();
            this.originalRightPadding = view.getPaddingRight();
            this.originalTopPadding = view.getPaddingTop();
            this.originalBottomPadding = view.getPaddingBottom();
        }
    }

    /* renamed from: ru.ok.android.ui.stream.list.StreamItemAdapter.1 */
    class C12361 implements ViewDrawListener {
        private final Rect visibleRect;

        C12361() {
            this.visibleRect = new Rect();
        }

        public void onViewDraw(View view) {
            view.getGlobalVisibleRect(this.visibleRect);
            if (StreamItemAdapter.this.shownOnScrollListener != null) {
                FeedWithState feedWithState = (FeedWithState) view.getTag(2131624341);
                if (feedWithState != null && !feedWithState.shownOnScrollSent && feedWithState.feed.getStatPixels(1) != null && StreamItemAdapter.this.shownOnScrollListener.onShownOnScroll(feedWithState.feed, this.visibleRect, view.getWidth(), view.getHeight())) {
                    feedWithState.shownOnScrollSent = true;
                    view.setTag(2131624341, null);
                }
            }
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (this.scrollState != 1 && this.scrollState != 0) {
            return;
        }
        if (this.lastVisibleCount != visibleItemCount || this.lastFirstVisible != firstVisibleItem || this.lastVisibleCount == -1 || firstVisibleItem == -1) {
            int start = firstVisibleItem + visibleItemCount;
            int i = start;
            while (i < start + 50 && i < totalItemCount) {
                if (i < getCount()) {
                    getItem(i).prefetch();
                }
                i++;
            }
        }
    }

    public StreamItemAdapter(Activity activity, StreamListStatistics stats, StreamBannerStatisticsHandler bannerStatHandler, StreamAdapterListener listener, String logContext) {
        this.scrollState = 0;
        this.lastFirstVisible = -1;
        this.lastVisibleCount = -1;
        this.items = new ArrayList();
        this.layoutConfig = new StreamLayoutConfig();
        this.viewDrawListener = new C12361();
        this.streamItemViewController = new DefaultStreamViewController(activity, listener, logContext);
        this.streamItemViewController.setDebugMode(false);
        this.streamItemViewController.setStreamBannerStatisticsHandler(bannerStatHandler);
        this.streamItemViewController.setStreamListStatistics(stats);
        this.streamItemViewController.setNotifyContentChangeListener(this);
        this.streamItemViewController.getPlayerStateHolder().init();
    }

    public void setLayoutConfig(StreamLayoutConfig config) {
        this.layoutConfig = config;
    }

    public int getItemViewType(int position) {
        return getItem(position).viewType;
    }

    public int getViewTypeCount() {
        return 47;
    }

    public boolean hasStableIds() {
        return true;
    }

    public int getCount() {
        return this.items.size();
    }

    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public StreamItem getItem(int position) {
        return (StreamItem) this.items.get(position);
    }

    @Deprecated
    protected ViewHolder createViewHolder(View convertView) {
        return null;
    }

    protected View newView(int position, ViewGroup parent) {
        if (this.streamItemViewController.getViewDrawObserver() == null) {
            ViewDrawObserver viewDrawObserver = new ViewDrawObserver(parent, this.viewDrawListener);
            if (this.isResumed) {
                viewDrawObserver.resume();
            }
            this.streamItemViewController.setViewDrawObserver(viewDrawObserver);
        }
        ViewHolder viewHolder = StreamItem.getViewHolder(this.streamItemViewController.getLayoutInflater(), parent, getItem(position).viewType, this.streamItemViewController);
        viewHolder.itemView.setTag(viewHolder);
        return viewHolder.itemView;
    }

    protected void bindView(ViewHolder holder, StreamItem item, int position) {
        holder.adapterPosition = position;
        item.bindView(holder, this.streamItemViewController, this.layoutConfig);
        item.updateForLayoutSize(holder, this.streamItemViewController, this.layoutConfig);
        holder.feed = item.feedWithState.feed;
        holder.item = item;
        if (item.isFirstInFeed() || item.isLastInFeed()) {
            holder.itemView.addOnAttachStateChangeListener(this.streamItemViewController.getStreamListStatistics().getAttachListener());
        }
    }

    public void setData(List<StreamItem> data) {
        this.items.clear();
        if (data != null) {
            this.items.addAll(data);
        }
        this.streamItemViewController.closeOptions();
    }

    public List<StreamItem> getData() {
        return this.items;
    }

    public void onMovedToScrapHeap(View view) {
        ViewHolder viewTag = view.getTag();
        if (viewTag instanceof ViewHolder) {
            ViewHolder vh = viewTag;
            vh.item.onUnbindView(vh);
            vh.item = null;
        }
    }

    public static String buildMakePresentRequest(String userId, String presentId, String holidayId) {
        try {
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new MakePresentRequest(ConfigurationPreferences.getInstance().getWebServer(), userId, presentId, holidayId)).getURI().toString();
        } catch (Throwable e) {
            Logger.m178e(e);
            return "";
        }
    }

    public StreamItemViewController getStreamItemViewController() {
        return this.streamItemViewController;
    }

    public void onContentWithOptionsChanged() {
        notifyDataSetChanged();
    }
}
