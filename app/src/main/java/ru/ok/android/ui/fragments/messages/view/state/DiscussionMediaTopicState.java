package ru.ok.android.ui.fragments.messages.view.state;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.services.mediatopic_polls.MtPollsManager.PollAnswersChangeListener;
import ru.ok.android.statistics.stream.StreamStats;
import ru.ok.android.storage.Storages;
import ru.ok.android.ui.stream.FeedHeaderActionsDialog;
import ru.ok.android.ui.stream.FeedHeaderActionsDialog.FeedHeaderActionsDialogListener;
import ru.ok.android.ui.stream.StreamListStatistics;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.Feed2StreamItemBinder;
import ru.ok.android.ui.stream.list.StreamItem;
import ru.ok.android.ui.stream.list.StreamItemAdapter;
import ru.ok.android.ui.stream.list.StreamItemAdapter.StreamAdapterListener;
import ru.ok.android.ui.stream.list.StreamLayoutConfig;
import ru.ok.android.ui.stream.list.StreamLinkItem;
import ru.ok.android.ui.stream.list.StreamPollAnswerItem;
import ru.ok.android.ui.stream.list.StreamVSpaceItem;
import ru.ok.android.ui.stream.view.FeedHeaderInfo;
import ru.ok.android.ui.stream.view.FeedHeaderView.FeedHeaderViewListener;
import ru.ok.android.ui.stream.view.FeedMediaTopicStyle;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.Utils;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.LikeInfoContext;

public class DiscussionMediaTopicState extends DiscussionState implements PollAnswersChangeListener, FeedHeaderActionsDialogListener, StreamAdapterListener, FeedHeaderViewListener {
    private LinearLayout contentView;
    private DiscussionInfoResponse discussion;
    private int dp4;
    private int dp8;
    private final Fragment fragment;
    @Nullable
    private HashSet<String> pollIds;
    private final StreamItemAdapter streamItemAdapter;

    /* renamed from: ru.ok.android.ui.fragments.messages.view.state.DiscussionMediaTopicState.1 */
    class C09041 extends DataSetObserver {
        C09041() {
        }

        public void onChanged() {
            super.onChanged();
            DiscussionMediaTopicState.this.refreshContentView();
        }
    }

    public DiscussionMediaTopicState(DiscussionInfoResponse discussionInfo, Fragment fragment) {
        this.dp8 = (int) Utils.dipToPixels(8.0f);
        this.dp4 = (int) Utils.dipToPixels(4.0f);
        this.fragment = fragment;
        this.discussion = discussionInfo;
        Activity activity = fragment.getActivity();
        this.streamItemAdapter = new StreamItemAdapter(activity, new StreamListStatistics(), null, this, "DiscussionMediaTopicInfo");
        this.streamItemAdapter.registerDataSetObserver(new C09041());
        this.streamItemAdapter.getStreamItemViewController().setFeedReshareHeaderViewListener(this);
        this.streamItemAdapter.getStreamItemViewController().setFeedHeaderViewListener(this);
        Storages.getInstance(activity, OdnoklassnikiApplication.getCurrentUser().getId()).getMtPollsManager().addWeakPollAnswersChangeListener(this);
    }

    public boolean isMessageVisible() {
        return false;
    }

    public void configureView(View view, DiscussionInfoResponse discussion) {
        if (this.contentView == null) {
            this.contentView = (LinearLayout) view;
            this.contentView.removeAllViews();
        }
        this.discussion = discussion;
        refreshContentView();
    }

    public View createContentView(Context context) {
        LinearLayout view = new LinearLayout(context);
        view.setOrientation(1);
        view.setGravity(17);
        view.setLayoutParams(new LayoutParams(-1, -2));
        view.setPadding(this.dp4, 0, this.dp4, 0);
        return view;
    }

    public void onContentClicked() {
    }

    public void onLikeClicked(int position, Feed feed, LikeInfoContext likeInfo) {
    }

    public void onGeneralUsersInfosClicked(int position, Feed feed, ArrayList<GeneralUserInfo> infos, String source) {
        if (infos.size() == 1) {
            onFeedHeaderActionSelected((GeneralUserInfo) infos.get(0));
        } else if (infos.size() > 0) {
            showUsers(infos);
        }
    }

    public void onCommentClicked(int position, Feed feed, DiscussionSummary discussionSummary) {
    }

    public void onMediaTopicClicked(int position, Feed feed, DiscussionSummary discussionSummary) {
    }

    public void onUsersSelected(int position, Feed feed, ArrayList<UserInfo> users) {
        showUsers(users);
    }

    public void onMarkAsSpamClicked(int position, Feed feed, int itemAdapterPosition) {
    }

    public LikeInfoContext onLikePhotoClicked(int position, Feed feed, LikeInfoContext likeInfo) {
        return likeInfo;
    }

    public void onDeleteClicked(int feedPosition, Feed feed, int itemAdapterPosition) {
    }

    private void refreshContentView() {
        if (this.contentView != null && this.discussion != null && this.fragment.getActivity() != null && this.discussion != null && this.fragment.getActivity() != null) {
            List streamItems = new ArrayList();
            Feed dummyFeed = new Feed();
            for (String ref : this.discussion.topicEntities.keySet()) {
                dummyFeed.addTargetRef(ref);
            }
            dummyFeed.resolveRefs(this.discussion.topicEntities);
            new Feed2StreamItemBinder(this.fragment.getActivity(), null, new FeedMediaTopicStyle(this.fragment.getActivity(), null, 0, 2131296528)).bindMediaTopic(new FeedWithState(dummyFeed), 0, this.discussion.mediaTopic, false, streamItems);
            if (streamItems.size() > 0 && (streamItems.get(0) instanceof StreamVSpaceItem)) {
                streamItems.remove(0);
            }
            updateLayoutConfig();
            if (this.pollIds != null) {
                this.pollIds.clear();
            }
            this.streamItemAdapter.setData(streamItems);
            boolean newView = this.contentView.getChildCount() == 0;
            int i = 0;
            while (i < streamItems.size()) {
                View itemView = this.streamItemAdapter.getView(i, newView ? null : this.contentView.getChildAt(i), this.contentView);
                if (newView) {
                    this.contentView.addView(itemView);
                }
                StreamItem item = (StreamItem) streamItems.get(i);
                if (item instanceof StreamLinkItem) {
                    itemView.setBackgroundResource(2130837848);
                } else {
                    itemView.setBackgroundResource(2130838643);
                    itemView.setPadding(this.dp8, itemView.getPaddingTop(), this.dp8, itemView.getPaddingBottom());
                }
                if (item instanceof StreamPollAnswerItem) {
                    String pollId = ((StreamPollAnswerItem) item).poll.getId();
                    if (this.pollIds == null) {
                        this.pollIds = new HashSet();
                    }
                    this.pollIds.add(pollId);
                }
                i++;
            }
        }
    }

    private void updateLayoutConfig() {
        int parentWidth = ((View) this.contentView.getParent()).getMeasuredWidth();
        StreamLayoutConfig config = new StreamLayoutConfig();
        config.listViewWidth = (parentWidth - this.contentView.getPaddingLeft()) - this.contentView.getPaddingRight();
        config.listViewPortraitWidth = (parentWidth - this.contentView.getPaddingLeft()) - this.contentView.getPaddingRight();
        this.streamItemAdapter.setLayoutConfig(config);
    }

    public void onFeedHeaderActionSelected(GeneralUserInfo info) {
        Activity activity = this.fragment.getActivity();
        if (activity != null && !activity.isFinishing()) {
            if (info.getObjectType() == 1) {
                NavigationHelper.showGroupInfo(activity, info.getId());
                StreamStats.clickGroup("discussion_topic");
            } else if (info.getObjectType() == 0) {
                NavigationHelper.showUserInfo(activity, info.getId());
                StreamStats.clickUser("discussion_topic");
            }
        }
    }

    public void onClickedAvatar(FeedHeaderInfo info) {
        onClickedFeedHeader(info);
    }

    public void onClickedFeedHeader(FeedHeaderInfo info) {
        ArrayList<GeneralUserInfo> referencedUsers = info.referencedUsers;
        if (referencedUsers.size() == 1) {
            onFeedHeaderActionSelected((GeneralUserInfo) referencedUsers.get(0));
        } else if (referencedUsers.size() > 0) {
            showUsers(referencedUsers);
        }
    }

    public void onFeedHeaderActionSelected(GeneralUserInfo info, String source) {
        onFeedHeaderActionSelected(info);
    }

    public void showUsers(ArrayList<? extends GeneralUserInfo> users) {
        FeedHeaderActionsDialog dialog = FeedHeaderActionsDialog.newInstance(users, "with_friends");
        dialog.setListener(this);
        dialog.show(this.fragment.getFragmentManager(), "with_friends");
    }

    public void onMediaTopicTextEditClick(String topicId, int blockIndex, String text) {
        boolean isStatus = this.discussion.generalInfo.type == Type.USER_STATUS;
        NavigationHelper.showEditMediaTopicTextActivity(this.fragment, text, topicId, blockIndex, 777, isStatus ? 2131165729 : 2131165730, isStatus ? 2131166121 : 2131166123, isStatus ? 2131166120 : 2131166122);
    }

    public void onPollAnswersChanged(String pollId) {
        Logger.m173d("pollIds=%s pollId=%s", this.pollIds, pollId);
        if (this.pollIds != null && this.pollIds.contains(pollId)) {
            this.streamItemAdapter.notifyDataSetChanged();
        }
    }
}
