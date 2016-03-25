package ru.ok.android.ui.stream.list;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.C0206R;
import ru.ok.android.drawable.VariableInsetDrawable;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.ViewUtil;

public abstract class StreamItem {
    final int bottomEdgeType;
    private int countInFeed;
    public final FeedWithState feedWithState;
    private int positionInFeed;
    boolean sendShowOnScroll;
    final int topEdgeType;
    public final int viewType;

    protected StreamItem(int viewType, int topEdgeType, int bottomEdgeType, FeedWithState feedWithState) {
        this.viewType = viewType;
        this.feedWithState = feedWithState;
        this.topEdgeType = topEdgeType;
        this.bottomEdgeType = bottomEdgeType;
    }

    public long getId() {
        return (this.feedWithState.feed.getId() << 8) | (255 & ((long) this.positionInFeed));
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (!this.sendShowOnScroll || this.feedWithState.shownOnScrollSent) {
            holder.itemView.setTag(2131624341, null);
        } else {
            holder.itemView.setTag(2131624341, this.feedWithState);
            streamItemViewController.getViewDrawObserver().startObserving(holder.itemView);
        }
        if (sharePressedState()) {
            holder.itemView.setTag(2131624340, Boolean.TRUE);
        } else {
            holder.itemView.setTag(2131624340, null);
        }
    }

    public void onUnbindView(@NonNull ViewHolder holder) {
    }

    public int getPositionInFeed() {
        return this.positionInFeed;
    }

    public boolean isFirstInFeed() {
        return this.positionInFeed == 0;
    }

    public boolean isLastInFeed() {
        return this.positionInFeed == this.countInFeed + -1;
    }

    static boolean needSpaceBetween(StreamItem topItem, StreamItem bottomItem) {
        int topEdge = topItem.bottomEdgeType;
        int bottomEdge = bottomItem.topEdgeType;
        if (topEdge == 4 || bottomEdge == 4) {
            return false;
        }
        if (topEdge == 1 || bottomEdge == 1) {
            return false;
        }
        return true;
    }

    public void prefetch() {
    }

    boolean canHaveLineAbove() {
        return this.topEdgeType != 2;
    }

    void setPositionInFeed(int orderInFeed, int countInFeed) {
        this.positionInFeed = orderInFeed;
        this.countInFeed = countInFeed;
    }

    void setSendShowOnScroll(boolean sendShowOnScroll) {
        this.sendShowOnScroll = sendShowOnScroll;
    }

    boolean sharePressedState() {
        return true;
    }

    int getVSpacingTop(Context context) {
        return 0;
    }

    int getVSpacingBottom(Context context) {
        return 0;
    }

    boolean isLaidOutInsideCard() {
        return true;
    }

    public void updateForLayoutSize(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        int extraMargin = layoutConfig.getExtraMarginForLandscapeAsInPortrait(true);
        applyExtraMarginsToBg(holder.itemView, extraMargin, extraMargin);
        int extraLeftPadding = 0;
        int extraRightPadding = 0;
        if (isLaidOutInsideCard()) {
            extraRightPadding = extraMargin;
            extraLeftPadding = extraMargin;
        }
        applyExtraMarginsToPaddings(holder, extraLeftPadding, extraRightPadding, layoutConfig);
    }

    static void applyExtraMarginsToBg(View view, int extraCardMarginLeft, int extraCardMarginRight) {
        Drawable bg = view.getBackground();
        if (bg != null) {
            Drawable originalBg;
            if (bg instanceof VariableInsetDrawable) {
                originalBg = ((VariableInsetDrawable) bg).getDrawable();
            } else {
                originalBg = bg;
            }
            VariableInsetDrawable insetBg = new VariableInsetDrawable(originalBg.getConstantState().newDrawable(view.getContext().getResources()));
            insetBg.setInsets(extraCardMarginLeft, 0, extraCardMarginRight, 0);
            ViewUtil.setBackgroundCompat(view, insetBg);
        }
    }

    void applyExtraMarginsToPaddings(ViewHolder holder, int extraLeftPadding, int extraRightPadding, StreamLayoutConfig layoutConfig) {
        holder.itemView.setPadding(holder.originalLeftPadding + extraLeftPadding, holder.originalTopPadding, holder.originalRightPadding + extraRightPadding, holder.originalBottomPadding);
    }

    @NonNull
    public static ViewHolder getViewHolder(LayoutInflater li, ViewGroup p, int viewType, StreamItemViewController streamItemViewController) {
        View v = null;
        ViewHolder vh = null;
        switch (viewType) {
            case RECEIVED_VALUE:
                v = StreamFeedHeaderItem.newView(li, p);
                vh = AbsStreamContentHeaderItem.newViewHolder(v, streamItemViewController);
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                v = StreamFeedFooterItem.newView(li, p, streamItemViewController);
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                v = StreamDividerItem.newView(li, p);
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                v = StreamPhotoLayerItem.newView(li, p);
                vh = StreamCenterLockPagerItem.newViewHolder(v, streamItemViewController);
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                v = StreamTextItem.newView(li, p);
                vh = AbsStreamTextItem.newViewHolder(v, streamItemViewController);
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                v = StreamSingleStaticPhotoItem.newView(li, p);
                vh = AbsStreamSingleStaticPhotoItem.newViewHolder(v, streamItemViewController);
                break;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                v = StreamLinkItem.newView(li, p);
                vh = StreamLinkItem.newViewHolder(v, streamItemViewController);
                break;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                v = StreamMusicTrackItem.newView(li, p);
                vh = AbsStreamMusicTrackItem.newViewHolder(v, streamItemViewController);
                break;
            case Message.TASKID_FIELD_NUMBER /*8*/:
                v = StreamMusicCoverItem.newView(li, p);
                vh = AbsStreamMusicTrackItem.newViewHolder(v, streamItemViewController);
                break;
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                v = StreamMusicPagerItem.newView(li, p);
                vh = StreamCenterLockPagerItem.newViewHolder(v, streamItemViewController);
                break;
            case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                v = StreamJoinGroupItem.newView(li, p);
                vh = StreamJoinGroupItem.newViewHolder(v, streamItemViewController);
                break;
            case Message.EDITINFO_FIELD_NUMBER /*11*/:
                v = StreamUserCommonFriendsItem.newView(li, p, streamItemViewController.getUserClickListener());
                vh = StreamUserCommonFriendsItem.newViewHolder(v, streamItemViewController);
                break;
            case Message.REPLYSTICKERS_FIELD_NUMBER /*12*/:
                v = StreamManyInRowItem.newView(li, p);
                vh = StreamUsersInRowItem.newViewHolder(v, streamItemViewController);
                break;
            case Conversation.OWNERID_FIELD_NUMBER /*13*/:
                v = StreamPresentItem.newView(li, p);
                vh = StreamPresentItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Toolbar_titleMarginEnd /*14*/:
                v = StreamManyPresentsItem.newView(li, p);
                vh = StreamManyPresentsItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Toolbar_titleMarginTop /*15*/:
                v = StreamVideoItem.newView(li, p);
                vh = AbsStreamVideoItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Toolbar_titleMarginBottom /*16*/:
                v = StreamVideoCaptionItem.newView(li, p);
                vh = StreamVideoCaptionItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Toolbar_maxButtonHeight /*17*/:
                v = StreamPollHeaderItem.newView(li, p);
                break;
            case C0206R.styleable.Toolbar_collapseIcon /*18*/:
                v = StreamPollAnswerItem.newView(li, p);
                vh = StreamPollAnswerItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Toolbar_collapseContentDescription /*19*/:
                v = StreamUserNamesItem.newView(li, p);
                vh = StreamUserNamesItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Toolbar_navigationIcon /*20*/:
                v = StreamPlacesItem.newView(li, p);
                vh = StreamPlacesItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Toolbar_navigationContentDescription /*21*/:
                v = StreamVSpaceItem.newView(li, p);
                break;
            case C0206R.styleable.Toolbar_logoDescription /*22*/:
                v = StreamCardBottomItem.newView(li, p);
                vh = StreamCardBottomItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Toolbar_titleTextColor /*23*/:
                v = StreamCardVSpaceItem.newView(li, p);
                break;
            case C0206R.styleable.Toolbar_subtitleTextColor /*24*/:
                v = StreamBannerCardTopItem.newView(li, p);
                vh = StreamBannerCardTopItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_actionMenuTextAppearance /*25*/:
                v = StreamBannerCardBottomItem.newView(li, p);
                vh = StreamBannerCardBottomItem.newViewHolder(v);
                break;
            case C0206R.styleable.Theme_actionMenuTextColor /*26*/:
                v = StreamBannerCardBottomAppItem.newView(li, p);
                vh = StreamBannerCardBottomAppItem.newViewHolder(v);
                break;
            case C0206R.styleable.Theme_actionModeStyle /*27*/:
                v = StreamBannerHeaderItem.newView(li, p);
                vh = StreamBannerHeaderItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_actionModeCloseButtonStyle /*28*/:
                v = StreamBannerTextItem.newView(li, p);
                vh = AbsStreamTextItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_actionModeBackground /*29*/:
                v = StreamBannerImageItem.newView(li, p);
                vh = StreamBannerImageItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_actionModeSplitBackground /*30*/:
                v = StreamAchievementsItem.newView(li, p);
                vh = StreamAchievementsItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_actionModeCloseDrawable /*31*/:
                v = StreamReshareAuthorItem.newView(li, p);
                vh = StreamReshareAuthorItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_actionModeCutDrawable /*32*/:
                v = StreamKlassHeaderItem.newView(li, p);
                vh = AbsStreamContentHeaderItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_actionModeCopyDrawable /*33*/:
                v = StreamKlassAuthorItem.newView(li, p);
                vh = AbsStreamContentHeaderItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_actionModePasteDrawable /*34*/:
                v = StreamNonSelectableTextItem.newView(li, p);
                vh = StreamNonSelectableTextItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_actionModeSelectAllDrawable /*35*/:
                v = StreamSecondaryAuthorItem.newView(li, p);
                vh = StreamSecondaryAuthorItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_actionModeShareDrawable /*36*/:
                v = StreamSingleStaticPhotoActionsItem.newView(li, p);
                vh = StreamSingleStaticPhotoActionsItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_actionModeFindDrawable /*37*/:
                v = StreamSingleGifAsMp4PhotoItem.newView(li, p);
                vh = AbsStreamSingleGifAsMp4PhotoItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_actionModeWebSearchDrawable /*38*/:
                v = StreamSingleGifAsMp4PhotoActionsItem.newView(li, p);
                vh = StreamSingleGifAsMp4PhotoActionsItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_actionModePopupWindowStyle /*39*/:
                v = TwoPhotoCollageItem.newView(li, p);
                vh = TwoPhotoCollageItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_textAppearanceLargePopupMenu /*40*/:
                v = OnePhotoCollageItem.newView(li, p);
                vh = OnePhotoCollageItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_textAppearanceSmallPopupMenu /*41*/:
                v = StreamThreeDotsItem.newView(li, p);
                break;
            case C0206R.styleable.Theme_dialogTheme /*42*/:
                v = OneGifCollageItem.newView(li, p);
                vh = OneGifCollageItem.newViewHolder(v);
                break;
            case C0206R.styleable.Theme_dialogPreferredPadding /*43*/:
                v = StreamGiftsCampaignHeaderItem.newView(li, p);
                vh = AbsStreamWithOptionsItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_listDividerAlertDialog /*44*/:
                v = StreamAppItem.newView(li, p);
                vh = StreamAppItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_actionDropDownStyle /*45*/:
                v = StreamStubItem.newView(li, p);
                vh = StreamStubItem.newViewHolder(v, streamItemViewController);
                break;
            case C0206R.styleable.Theme_dropdownListPreferredItemHeight /*46*/:
                v = StreamCardPresentItem.newView(li, p);
                vh = StreamCardPresentItem.newViewHolder(v, streamItemViewController);
                break;
        }
        if (v == null) {
            throw new IllegalStateException("No view for viewType " + viewType);
        } else if (vh == null) {
            return new ViewHolder(v);
        } else {
            return vh;
        }
    }

    public static ViewHolder newViewHolder(View v) {
        return new ViewHolder(v);
    }
}
