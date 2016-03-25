package ru.ok.android.ui.fragments.messages.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewStub;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import io.github.eterverda.sntp.SNTP;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.C0206R;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.app.GifAsMp4PlayerHelper.AutoplayContext;
import ru.ok.android.db.base.OfflineTable;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.AttachmentUtils;
import ru.ok.android.ui.custom.OkViewStub;
import ru.ok.android.ui.custom.layout.RelativeSetPressedLayout;
import ru.ok.android.ui.custom.text.OdklUrlsTextView;
import ru.ok.android.ui.custom.text.OdklUrlsTextView.OnSelectOdklLinkListener;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineData;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.ui.fragments.messages.loaders.data.RepliedToInfo;
import ru.ok.android.ui.fragments.messages.loaders.data.RepliedToInfo.Status;
import ru.ok.android.ui.fragments.messages.view.AudioMsgPlayer;
import ru.ok.android.ui.fragments.messages.view.AudioMsgPlayer.InputCallback;
import ru.ok.android.ui.utils.RowPosition;
import ru.ok.android.utils.AudioPlaybackController;
import ru.ok.android.utils.AudioPlaybackController.PlaybackEventsListener;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.attach.BaseAttachGridView.OnAttachClickListener;
import ru.ok.android.widget.attach.GifAsMp4AttachAdapter;
import ru.ok.android.widget.attach.GifAsMp4AttachGridView;
import ru.ok.android.widget.attach.PhotoAttachAdapter;
import ru.ok.android.widget.attach.PhotoAttachGridView;
import ru.ok.android.widget.attach.TopicLinkAttachView;
import ru.ok.android.widget.attach.VideoAttachAdapter;
import ru.ok.android.widget.attach.VideoAttachGridView;
import ru.ok.model.messages.Attachment;
import ru.ok.model.messages.Attachment.AttachmentType;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase;
import ru.ok.model.messages.MessageBase.RepliedTo;
import ru.ok.model.messages.MessageConversation;
import ru.ok.model.messages.MessageConversation.Type;

public class MessageDataView extends RelativeSetPressedLayout {
    private static float additionalDatePadding;
    private static int attachesMargin;
    private static int dateRightPadding;
    private View actionsBlock;
    private OkViewStub actionsBlockStub;
    private PlaybackEventsListener audioListener;
    private AudioMsgPlayer audioPlayer;
    private ViewStub audioPlayerStub;
    protected final TextView author;
    int cacheValue;
    private final View cantShowAttachText;
    protected final TextView date;
    protected boolean expandRepliedToMessage;
    private ViewStub gifAsMp4AttachStub;
    private GifAsMp4AttachGridView gifAsMp4AttachView;
    private final View isNewView;
    protected TextView like;
    private TextView likesCountView;
    private final int maxWidth;
    protected final OdklUrlsTextView messageText;
    private boolean nested;
    protected int padding4;
    protected int padding8;
    private ViewStub photoAttachStub;
    private PhotoAttachGridView photoAttachView;
    protected MessageDateViewProvider provider;
    protected final TextView repliedTo;
    private ViewStub repliedToBlockStub;
    private MessageDataView repliedToMessage;
    protected View reply;
    private ViewStub topicLinkAttachStub;
    private TopicLinkAttachView topicLinkAttachView;
    private ViewStub videoAttachStub;
    private VideoAttachGridView videoAttachView;

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.MessageDataView.1 */
    class C08621 implements InputCallback {
        C08621() {
        }

        public void onPlayPauseClick(View view) {
            Attachment tag = view.getTag();
            if (tag instanceof Attachment) {
                Attachment attachment = tag;
                if (AudioPlaybackController.isPlaying(attachment)) {
                    MessageDataView.this.togglePlayback();
                    return;
                }
                AudioPlaybackController.startPlayback(MessageDataView.this.audioPlayer.getContext(), attachment, MessageDataView.this.getAudioPlayerListener(), 33);
                AttachmentUtils.sendShowAttachStatEvents(AttachmentType.AUDIO_RECORDING);
                return;
            }
            Logger.m176e("Tag of AudioPlayer not attachment");
            MessageDataView.this.audioPlayer.onError();
        }

        public boolean onSeekStarted(View view, long timeMS) {
            if (!isPlaying(view)) {
                return false;
            }
            AudioPlaybackController.startSeek(timeMS);
            return true;
        }

        public boolean onSeeking(View view, long timeMS) {
            if (!isPlaying(view)) {
                return false;
            }
            AudioPlaybackController.handleSeeking(timeMS);
            return true;
        }

        public boolean onSeekStopped(View view, long timeMS) {
            if (!isPlaying(view)) {
                return false;
            }
            AudioPlaybackController.stopSeek(timeMS);
            return true;
        }

        private boolean isPlaying(View view) {
            return AudioPlaybackController.isPlaying((Attachment) view.getTag());
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.MessageDataView.2 */
    static /* synthetic */ class C08632 {
        static final /* synthetic */ int[] f106xa181cdc7;

        static {
            f106xa181cdc7 = new int[Status.values().length];
            try {
                f106xa181cdc7[Status.LOADING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f106xa181cdc7[Status.EXPANDED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public interface MessageDateViewProvider<M extends MessageBase> extends OnSelectOdklLinkListener {
        OnAttachClickListener getAttachClickListener();

        String getAuthorAvatar(String str, String str2);

        OnClickListener getAuthorClickListener();

        String getAuthorName(String str, String str2);

        OnClickListener getLikeClickListener();

        OnClickListener getLikesCountClickListener();

        OnClickListener getRepliedToBlockClickListener();

        OnClickListener getRepliedToClickListener();

        OnClickListener getReplyClickListener();

        boolean isInActionMode();

        boolean isLikeAllowed();

        boolean isMessageNew(M m);

        boolean isMy(String str);

        boolean isReplyDisallowed(boolean z, M m);

        boolean isUnlikeAllowed();

        boolean isWantToShowNames();
    }

    public MessageDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LocalizationManager.inflate(context, getLayoutId(), (ViewGroup) this, true);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.MessageDataView);
        int authorDrawableLeft = a.getResourceId(1, -1);
        this.expandRepliedToMessage = a.getBoolean(2, true);
        this.maxWidth = a.getDimensionPixelSize(0, 0);
        a.recycle();
        this.isNewView = findViewById(2131624694);
        this.author = (TextView) findViewById(2131624696);
        if (authorDrawableLeft != -1) {
            this.author.setCompoundDrawablesWithIntrinsicBounds(authorDrawableLeft, 0, 0, 0);
        }
        this.repliedTo = (TextView) findViewById(2131624698);
        this.repliedTo.setEnabled(this.expandRepliedToMessage);
        this.messageText = (OdklUrlsTextView) findViewById(2131624538);
        this.actionsBlockStub = (OkViewStub) findViewById(2131624702);
        this.cantShowAttachText = findViewById(2131624701);
        this.date = (TextView) findViewById(2131624541);
        this.repliedToBlockStub = (ViewStub) findViewById(2131624700);
        this.videoAttachStub = (ViewStub) findViewById(2131624704);
        this.audioPlayerStub = (ViewStub) findViewById(2131624706);
        this.photoAttachStub = (ViewStub) findViewById(2131624707);
        this.gifAsMp4AttachStub = (ViewStub) findViewById(2131624709);
        this.topicLinkAttachStub = (ViewStub) findViewById(2131624711);
        setClipToPadding(false);
        if (additionalDatePadding == 0.0f) {
            additionalDatePadding = Utils.dipToPixels(0.8f);
            dateRightPadding = (int) Utils.dipToPixels(10.0f);
            attachesMargin = (int) Utils.dipToPixels(12.0f);
        }
        this.padding4 = (int) Utils.dipToPixels(getContext(), 4.0f);
        this.padding8 = (int) Utils.dipToPixels(getContext(), 8.0f);
    }

    protected int getLayoutId() {
        return 2130903321;
    }

    public void setMessage(OfflineMessage<? extends MessageBase> offlineMessage, RowPosition rowPosition) {
        MessageBase message = offlineMessage.message;
        int avatarVisibility = rowPosition.isAvatarVisible() ? 0 : 8;
        if (this.author != null) {
            boolean isMy = this.provider.isMy(offlineMessage.message.authorId);
            int nameVisibility = getAuthorVisibility(avatarVisibility, isMy);
            this.author.setVisibility(nameVisibility);
            if (nameVisibility == 0) {
                String authorName = this.provider.getAuthorName(message.authorType, message.authorId);
                if (TextUtils.isEmpty(authorName)) {
                    authorName = LocalizationManager.from(getContext()).getString(2131165423);
                }
                this.author.setText(authorName);
                this.author.setTextColor(getResources().getColor(getAuthorTextColor(isMy)));
                this.author.setTag(new MessageAuthor(message.authorId, message.authorType));
            }
        }
        if (this.date != null) {
            boolean isDateVisible = rowPosition.isDateVisible();
            this.date.setVisibility(isDateVisible ? 0 : 8);
            if (isDateVisible) {
                this.date.setText(getDateString(offlineMessage));
            }
        }
        String text = message.getActualText();
        if (TextUtils.isEmpty(text)) {
            this.messageText.setVisibility(8);
        } else {
            this.messageText.setVisibility(0);
            this.messageText.setText(text);
        }
        updateLikeBlock(message);
        updateIsNew(message);
        updateReplyToBlock(offlineMessage);
        boolean actionsVisible = this.actionsBlock != null && this.actionsBlock.getVisibility() == 0;
        LayoutParams lp = (LayoutParams) this.date.getLayoutParams();
        if (actionsVisible) {
            lp.addRule(6, 2131624702);
            removeRule(lp, 3);
        } else {
            addBelowRule(this.date, this.messageText, false);
            removeRule(lp, 6);
        }
        this.date.setLayoutParams(lp);
    }

    protected int getAuthorVisibility(int avatarVisibility, boolean isMy) {
        if (this.nested) {
            return avatarVisibility;
        }
        return (!this.provider.isWantToShowNames() || isMy) ? 8 : avatarVisibility;
    }

    protected String getDateString(OfflineMessage message) {
        long delta = 0;
        OfflineData offlineData = message.offlineData;
        if (offlineData == null || offlineData.status == OfflineTable.Status.RECEIVED || offlineData.status == OfflineTable.Status.SENT) {
            delta = System.currentTimeMillis() - SNTP.safeCurrentTimeMillisFromCache();
        }
        return DateFormatter.formatHHmm(message.message.date + delta);
    }

    protected int getAuthorTextColor(boolean isMy) {
        return isMy ? 2131493244 : 2131493072;
    }

    private static void removeRule(LayoutParams lp, int rule) {
        if (VERSION.SDK_INT >= 17) {
            lp.removeRule(rule);
        } else {
            lp.getRules()[rule] = 0;
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = widthMeasureSpec;
        if (this.maxWidth > 0) {
            int mode = MeasureSpec.getMode(widthMeasureSpec);
            int size = MeasureSpec.getSize(widthMeasureSpec);
            if (!(mode == 0 || size <= this.maxWidth || this.cacheValue == size)) {
                int value = (size * 3) / 4;
                width = MeasureSpec.makeMeasureSpec(Math.max(value, this.maxWidth), mode);
                this.cacheValue = value;
            }
        }
        super.onMeasure(width, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        boolean isActionsVisible = this.actionsBlock != null && this.actionsBlock.getVisibility() == 0;
        if (isActionsVisible) {
            this.date.offsetTopAndBottom((int) (((((float) this.actionsBlock.getTop()) + additionalDatePadding) + (((float) (this.actionsBlock.getHeight() - this.date.getHeight())) / 2.0f)) - ((float) this.date.getTop())));
        }
        if (this.isNewView.getVisibility() == 0) {
            layoutIsNewView();
        }
    }

    protected void layoutIsNewView() {
        alignToRight(this.isNewView, ((MarginLayoutParams) this.isNewView.getLayoutParams()).topMargin + getPaddingTop());
    }

    protected void alignToRight(View view, int additionalGap) {
        view.offsetLeftAndRight(((getWidth() - additionalGap) - view.getWidth()) - view.getLeft());
    }

    private void updateLikeBlock(MessageBase message) {
        boolean z;
        int visibility;
        int i = 0;
        if (this.provider.isLikeAllowed()) {
            inflateActionsBlock();
            Utils.formatLikeBlock(getContext(), message.likeInfo.count, message.likeInfo.self, message.flags.likeAllowed, this.provider.isUnlikeAllowed(), this.like, this.likesCountView);
            this.likesCountView.setTag(message.id);
            this.like.setTag(message);
            boolean isInActionMode = this.provider.isInActionMode();
            TextView textView = this.like;
            if (!this.like.isEnabled() || isInActionMode) {
                z = false;
            } else {
                z = true;
            }
            textView.setEnabled(z);
            textView = this.likesCountView;
            if (!this.likesCountView.isEnabled() || isInActionMode) {
                z = false;
            } else {
                z = true;
            }
            textView.setEnabled(z);
        } else if (this.likesCountView != null) {
            this.likesCountView.setVisibility(8);
            this.like.setVisibility(8);
        }
        if (this.provider.isReplyDisallowed(true, message)) {
            visibility = 8;
        } else {
            visibility = 0;
        }
        if (visibility == 0) {
            inflateActionsBlock();
        }
        if (this.reply != null) {
            this.reply.setVisibility(visibility);
            View view = this.reply;
            if (this.provider.isInActionMode()) {
                z = false;
            } else {
                z = true;
            }
            view.setEnabled(z);
        }
        boolean actionsVisible = false;
        if (this.actionsBlock != null) {
            int i2;
            if (this.likesCountView.getVisibility() == 0 || this.like.getVisibility() == 0 || this.reply.getVisibility() == 0) {
                actionsVisible = true;
            } else {
                actionsVisible = false;
            }
            view = this.actionsBlock;
            if (actionsVisible) {
                i2 = 0;
            } else {
                i2 = 8;
            }
            view.setVisibility(i2);
        }
        MarginLayoutParams lp = (MarginLayoutParams) this.date.getLayoutParams();
        if (actionsVisible) {
            i = dateRightPadding;
        }
        lp.rightMargin = i;
        this.date.setLayoutParams(lp);
    }

    private void inflateActionsBlock() {
        if (this.actionsBlockStub != null) {
            this.actionsBlock = this.actionsBlockStub.inflate();
            this.likesCountView = (TextView) this.actionsBlock.findViewById(2131625083);
            this.like = (TextView) this.actionsBlock.findViewById(2131625084);
            this.reply = this.actionsBlock.findViewById(2131625085);
            setActionsClickListeners();
            this.actionsBlockStub = null;
        }
    }

    public void setProvider(MessageDateViewProvider provider) {
        this.provider = provider;
        this.messageText.setLinkListener(provider);
        this.repliedTo.setOnClickListener(provider.getRepliedToClickListener());
        this.author.setOnClickListener(provider.getAuthorClickListener());
        setActionsClickListeners();
    }

    private void setActionsClickListeners() {
        if (this.like != null && this.provider != null) {
            this.like.setOnClickListener(this.provider.getLikeClickListener());
            this.likesCountView.setOnClickListener(this.provider.getLikesCountClickListener());
            this.reply.setOnClickListener(this.provider.getReplyClickListener());
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.repliedTo.setEnabled(enabled);
        this.author.setEnabled(enabled);
        if (this.like != null) {
            this.like.setEnabled(enabled);
            this.likesCountView.setEnabled(enabled);
            this.reply.setEnabled(enabled);
        }
        if (this.repliedToMessage != null) {
            this.repliedToMessage.setEnabled(enabled);
        }
    }

    private void updateIsNew(MessageBase message) {
        this.isNewView.setVisibility(this.provider.isMessageNew(message) ? 0 : 8);
    }

    protected void updateIsNewMargin() {
        if (this.isNewView.getVisibility() == 0) {
            ((MarginLayoutParams) this.isNewView.getLayoutParams()).topMargin = (this.padding4 / 2) - getPaddingTop();
        }
    }

    protected void updateReplyToBlock(OfflineMessage<? extends MessageBase> offlineMessage) {
        RepliedTo info = offlineMessage.message.repliedToInfo;
        if (this.repliedToBlockStub != null) {
            this.repliedToBlockStub.setVisibility(8);
        } else {
            this.repliedToMessage.setVisibility(8);
        }
        if (this.reply != null) {
            this.reply.setTag(offlineMessage);
        }
        String replyToName = info != null ? this.provider.getAuthorName(info.authorType, info.authorId) : null;
        if (TextUtils.isEmpty(replyToName) || !this.expandRepliedToMessage) {
            this.repliedTo.setVisibility(8);
            return;
        }
        this.repliedTo.setText(replyToName);
        this.repliedTo.setTag(offlineMessage);
        if (offlineMessage.repliedToInfo == null || offlineMessage.repliedToInfo.status == Status.COLLAPSED) {
            this.repliedTo.setVisibility(0);
        } else {
            invalidateRowForStatus(offlineMessage);
        }
    }

    public void setExpandRepliedToMessage(boolean expandRepliedToMessage) {
        this.expandRepliedToMessage = expandRepliedToMessage;
    }

    public void setAttachments(OfflineMessage<? extends MessageBase> message) {
        setAttachments(message, this.padding8);
    }

    private void setAttachments(OfflineMessage<? extends MessageBase> message, int bottomPadding) {
        Attachment[] attachments = message.message.attachments;
        Object audioTag = getAudioPlayerView().getTag();
        if (audioTag != null && AudioPlaybackController.isPlaying((Attachment) audioTag)) {
            resetAudioPlayer();
        }
        boolean video = false;
        boolean movie = false;
        boolean audio = false;
        boolean photoAttach = false;
        boolean gifAsMp4Attach = false;
        boolean topicLink = false;
        Attachment audioAttach = null;
        Attachment topicLinkAttach = null;
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                if (attachment.typeValue == AttachmentType.PHOTO) {
                    if (GifAsMp4PlayerHelper.shouldPlayGifAsMp4InPlace(attachment, AutoplayContext.CONVERSATION)) {
                        gifAsMp4Attach = true;
                    } else {
                        photoAttach = true;
                    }
                } else {
                    if (attachment.typeValue == AttachmentType.AUDIO_RECORDING) {
                        audio = true;
                        audioAttach = attachment;
                    } else {
                        if (attachment.typeValue != null) {
                            if (attachment.typeValue.isVideo()) {
                                movie |= attachment.typeValue == AttachmentType.MOVIE ? 1 : 0;
                                video = true;
                            }
                        }
                        if (attachment.typeValue == AttachmentType.TOPIC && isTopicLickAttachesSupported()) {
                            topicLink = true;
                            topicLinkAttach = attachment;
                        }
                    }
                }
            }
        }
        if ((video && !movie) || audio) {
            this.messageText.setVisibility(8);
        }
        checkAttach(attachments);
        View prevItem = this.messageText;
        if (video || audio || photoAttach || gifAsMp4Attach || topicLink) {
            int paddingTop;
            if (this.author.getVisibility() != 0) {
                if (this.messageText.getVisibility() != 0) {
                    paddingTop = this.padding8;
                    setPadding(getPaddingLeft(), paddingTop, getPaddingRight(), bottomPadding);
                    updateIsNewMargin();
                    if (video) {
                        getVideoAttachView().setVisibility(8);
                    } else {
                        fetchVideoAttachView(message, attachments);
                        getVideoAttachView().setVisibility(0);
                        addBelowRule(getVideoAttachView(), prevItem, true);
                        prevItem = this.videoAttachView;
                    }
                    if (audio) {
                        getAudioPlayerView().setVisibility(8);
                        resetAudioPlayer();
                    } else {
                        fetchAudioPlayerView(audioAttach, message.message.authorId);
                        getAudioPlayerView().setVisibility(0);
                        this.audioPlayer.setTag(audioAttach);
                        this.audioPlayer.setDuration(Long.valueOf(audioAttach == null ? audioAttach.duration : 0));
                        this.audioPlayer.setWaveInfo(audioAttach == null ? audioAttach.audioProfile : message.message.mediaMetadata);
                        addBelowRule(this.audioPlayer, prevItem, true);
                        prevItem = this.audioPlayer;
                    }
                    if (photoAttach) {
                        getPhotoAttachView().setVisibility(8);
                    } else {
                        fetchPhotoAttach(message, attachments);
                        getPhotoAttachView().setVisibility(0);
                        addBelowRule(getPhotoAttachView(), prevItem, true);
                        prevItem = this.photoAttachView;
                    }
                    if (gifAsMp4Attach) {
                        getGifAsMp4AttachView().setVisibility(8);
                    } else {
                        fetchGifAsMp4Attach(attachments);
                        getGifAsMp4AttachView().setVisibility(0);
                        addBelowRule(getGifAsMp4AttachView(), prevItem, true);
                        prevItem = this.gifAsMp4AttachView;
                    }
                    if (topicLink) {
                        getTopicLinkAttachView().setVisibility(8);
                    } else {
                        fetchTopicLinkAttach(topicLinkAttach);
                        getTopicLinkAttachView().setVisibility(0);
                        addBelowRule(getTopicLinkAttachView(), prevItem, true);
                        prevItem = this.topicLinkAttachView;
                    }
                    if (this.cantShowAttachText.getVisibility() == 0) {
                        addBelowRule(this.cantShowAttachText, prevItem, false);
                        prevItem = this.cantShowAttachText;
                    }
                    if (this.actionsBlock != null) {
                        if (this.actionsBlock.getVisibility() == 0) {
                            addBelowRule(this.actionsBlock, prevItem, false);
                            return;
                        }
                    }
                    addBelowRule(this.date, prevItem, false);
                    return;
                }
            }
            paddingTop = this.padding4;
            setPadding(getPaddingLeft(), paddingTop, getPaddingRight(), bottomPadding);
            updateIsNewMargin();
            if (video) {
                getVideoAttachView().setVisibility(8);
            } else {
                fetchVideoAttachView(message, attachments);
                getVideoAttachView().setVisibility(0);
                addBelowRule(getVideoAttachView(), prevItem, true);
                prevItem = this.videoAttachView;
            }
            if (audio) {
                getAudioPlayerView().setVisibility(8);
                resetAudioPlayer();
            } else {
                fetchAudioPlayerView(audioAttach, message.message.authorId);
                getAudioPlayerView().setVisibility(0);
                this.audioPlayer.setTag(audioAttach);
                if (audioAttach == null) {
                }
                this.audioPlayer.setDuration(Long.valueOf(audioAttach == null ? audioAttach.duration : 0));
                if (audioAttach == null) {
                }
                this.audioPlayer.setWaveInfo(audioAttach == null ? audioAttach.audioProfile : message.message.mediaMetadata);
                addBelowRule(this.audioPlayer, prevItem, true);
                prevItem = this.audioPlayer;
            }
            if (photoAttach) {
                getPhotoAttachView().setVisibility(8);
            } else {
                fetchPhotoAttach(message, attachments);
                getPhotoAttachView().setVisibility(0);
                addBelowRule(getPhotoAttachView(), prevItem, true);
                prevItem = this.photoAttachView;
            }
            if (gifAsMp4Attach) {
                getGifAsMp4AttachView().setVisibility(8);
            } else {
                fetchGifAsMp4Attach(attachments);
                getGifAsMp4AttachView().setVisibility(0);
                addBelowRule(getGifAsMp4AttachView(), prevItem, true);
                prevItem = this.gifAsMp4AttachView;
            }
            if (topicLink) {
                getTopicLinkAttachView().setVisibility(8);
            } else {
                fetchTopicLinkAttach(topicLinkAttach);
                getTopicLinkAttachView().setVisibility(0);
                addBelowRule(getTopicLinkAttachView(), prevItem, true);
                prevItem = this.topicLinkAttachView;
            }
            if (this.cantShowAttachText.getVisibility() == 0) {
                addBelowRule(this.cantShowAttachText, prevItem, false);
                prevItem = this.cantShowAttachText;
            }
            if (this.actionsBlock != null) {
                if (this.actionsBlock.getVisibility() == 0) {
                    addBelowRule(this.actionsBlock, prevItem, false);
                    return;
                }
            }
            addBelowRule(this.date, prevItem, false);
            return;
        }
        if (this.cantShowAttachText.getVisibility() == 0) {
            addBelowRule(this.cantShowAttachText, prevItem, false);
            prevItem = this.cantShowAttachText;
        }
        if (this.actionsBlock != null) {
            if (this.actionsBlock.getVisibility() == 0) {
                addBelowRule(this.actionsBlock, prevItem, false);
                getVideoAttachView().setVisibility(8);
                getAudioPlayerView().setVisibility(8);
                getPhotoAttachView().setVisibility(8);
                getGifAsMp4AttachView().setVisibility(8);
                getTopicLinkAttachView().setVisibility(8);
                setPadding(getPaddingLeft(), getTextTopPadding(message.message), getPaddingRight(), bottomPadding);
                updateIsNewMargin();
            }
        }
        addBelowRule(this.date, prevItem, false);
        getVideoAttachView().setVisibility(8);
        getAudioPlayerView().setVisibility(8);
        getPhotoAttachView().setVisibility(8);
        getGifAsMp4AttachView().setVisibility(8);
        getTopicLinkAttachView().setVisibility(8);
        setPadding(getPaddingLeft(), getTextTopPadding(message.message), getPaddingRight(), bottomPadding);
        updateIsNewMargin();
    }

    protected int getTextTopPadding(MessageBase message) {
        if (((MessageConversation) message).type == Type.STICKER) {
            return this.padding8;
        }
        return this.padding4;
    }

    private void addBelowRule(View view, View lastView, boolean addMargin) {
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        lp.addRule(3, lastView.getId());
        if (addMargin) {
            lp.topMargin = lastView.getVisibility() == 0 ? attachesMargin : 0;
        }
    }

    protected boolean isTopicLickAttachesSupported() {
        return false;
    }

    private View getAudioPlayerView() {
        return this.audioPlayerStub == null ? this.audioPlayer : this.audioPlayerStub;
    }

    private View getVideoAttachView() {
        return this.videoAttachStub == null ? this.videoAttachView : this.videoAttachStub;
    }

    private View getPhotoAttachView() {
        return this.photoAttachStub == null ? this.photoAttachView : this.photoAttachStub;
    }

    private View getGifAsMp4AttachView() {
        return this.gifAsMp4AttachStub == null ? this.gifAsMp4AttachView : this.gifAsMp4AttachStub;
    }

    private View getTopicLinkAttachView() {
        return this.topicLinkAttachStub == null ? this.topicLinkAttachView : this.topicLinkAttachStub;
    }

    public void resetAudioPlayer() {
        if (this.audioListener != null) {
            AudioPlaybackController.removeListener(this.audioListener);
        }
    }

    private PlaybackEventsListener getAudioPlayerListener() {
        if (this.audioListener == null) {
            this.audioListener = new AttachPlaybackListener(this.audioPlayer);
        }
        return this.audioListener;
    }

    void checkAttach(Attachment[] attachments) {
        boolean attachSupport = true;
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                if (!attachment.isSupport() || (attachment.typeValue == AttachmentType.TOPIC && !isTopicLickAttachesSupported())) {
                    attachSupport = false;
                    break;
                }
            }
        }
        this.cantShowAttachText.setVisibility(attachSupport ? 8 : 0);
    }

    private void fetchVideoAttachView(OfflineMessage message, Attachment[] attachments) {
        if (this.videoAttachStub != null) {
            this.videoAttachView = (VideoAttachGridView) this.videoAttachStub.inflate();
            this.videoAttachView.setOnAttachClickListener(this.provider.getAttachClickListener());
            this.videoAttachView.setAttachesAdapter(new VideoAttachAdapter());
            this.videoAttachStub = null;
        }
        List<Attachment> filteredAttachments = new ArrayList();
        for (Attachment attachment : attachments) {
            if (attachment.typeValue != null && attachment.typeValue.isVideo()) {
                filteredAttachments.add(attachment);
            }
        }
        if (message.offlineData != null) {
            this.videoAttachView.setMessageId((long) message.offlineData.databaseId);
        }
        ((VideoAttachAdapter) this.videoAttachView.getAttachesAdapter()).setData(filteredAttachments);
    }

    void fetchAudioPlayerView(Attachment audioAttachment, String authorId) {
        if (this.audioPlayerStub != null) {
            this.audioPlayer = (AudioMsgPlayer) this.audioPlayerStub.inflate();
            this.audioPlayerStub = null;
        }
        if (AudioPlaybackController.isPlaying(audioAttachment)) {
            this.audioPlayer.setPlaybackState(AudioPlaybackController.getState());
            AudioPlaybackController.addListener(getAudioPlayerListener());
        } else {
            AudioPlaybackController.removeListener(getAudioPlayerListener());
            this.audioPlayer.resetState();
        }
        if (this.provider.isMy(authorId)) {
            this.audioPlayer.setIsRight();
        }
        this.audioPlayer.setEventsListener(new C08621());
    }

    void fetchPhotoAttach(OfflineMessage message, Attachment[] attachments) {
        if (this.photoAttachStub != null) {
            this.photoAttachView = (PhotoAttachGridView) this.photoAttachStub.inflate();
            this.photoAttachStub = null;
            this.photoAttachView.setOnAttachClickListener(this.provider.getAttachClickListener());
            this.photoAttachView.setAttachesAdapter(new PhotoAttachAdapter());
        }
        List<Attachment> filteredAttachments = new ArrayList();
        for (Attachment attachment : attachments) {
            if (attachment.typeValue == AttachmentType.PHOTO && !GifAsMp4PlayerHelper.shouldPlayGifAsMp4InPlace(attachment, AutoplayContext.CONVERSATION)) {
                filteredAttachments.add(attachment);
            }
        }
        if (message.offlineData != null) {
            this.photoAttachView.setMessageId((long) message.offlineData.databaseId);
        }
        ((PhotoAttachAdapter) this.photoAttachView.getAttachesAdapter()).setData(filteredAttachments);
    }

    void fetchGifAsMp4Attach(@NonNull Attachment[] attachments) {
        if (this.gifAsMp4AttachStub != null) {
            this.gifAsMp4AttachView = (GifAsMp4AttachGridView) this.gifAsMp4AttachStub.inflate();
            this.gifAsMp4AttachStub = null;
            this.gifAsMp4AttachView.setOnAttachClickListener(this.provider.getAttachClickListener());
            this.gifAsMp4AttachView.setAttachesAdapter(new GifAsMp4AttachAdapter());
        }
        List<Attachment> filteredAttachments = new ArrayList();
        for (Attachment attachment : attachments) {
            if (attachment.typeValue == AttachmentType.PHOTO && GifAsMp4PlayerHelper.shouldPlayGifAsMp4InPlace(attachment, AutoplayContext.CONVERSATION)) {
                filteredAttachments.add(attachment);
            }
        }
        ((GifAsMp4AttachAdapter) this.gifAsMp4AttachView.getAttachesAdapter()).setData(filteredAttachments);
    }

    void fetchTopicLinkAttach(@NonNull Attachment attachment) {
        if (this.topicLinkAttachStub != null) {
            this.topicLinkAttachView = (TopicLinkAttachView) this.topicLinkAttachStub.inflate();
            this.topicLinkAttachView.setAttachClickListener(this.provider.getAttachClickListener());
            this.topicLinkAttachStub = null;
        }
        this.topicLinkAttachView.setAttachment(attachment);
    }

    private void togglePlayback() {
        if (AudioPlaybackController.isPlaying() || AudioPlaybackController.isBuffering()) {
            this.audioPlayer.onPaused();
            AudioPlaybackController.pausePlayback();
            return;
        }
        this.audioPlayer.onPlaying();
        AudioPlaybackController.resumePlayback();
        AttachmentUtils.sendShowAttachStatEvents(AttachmentType.AUDIO_RECORDING);
    }

    protected void invalidateRowForStatus(OfflineMessage<? extends MessageBase> offlineMessage) {
        RepliedToInfo info = offlineMessage.repliedToInfo;
        makeRepliedToBlockVisible();
        switch (C08632.f106xa181cdc7[info.status.ordinal()]) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (info.offlineMessage != null) {
                    this.repliedToMessage.setMessage(info.offlineMessage, RowPosition.SINGLE_DATE_AVATAR);
                }
                this.repliedToMessage.setTag(offlineMessage);
                this.repliedToMessage.setAttachments(info.offlineMessage, this.repliedToMessage.getPaddingBottom());
            default:
        }
    }

    private void makeRepliedToBlockVisible() {
        if (this.repliedToBlockStub != null) {
            this.repliedToMessage = (MessageDataView) this.repliedToBlockStub.inflate();
            this.repliedToMessage.setNested(true);
            this.repliedToMessage.setProvider(this.provider);
            this.repliedToMessage.setOnClickListener(this.provider.getRepliedToBlockClickListener());
            this.repliedToMessage.setEnabled(isEnabled());
            this.repliedToBlockStub = null;
        }
        this.repliedToMessage.setVisibility(0);
        this.repliedTo.setVisibility(8);
    }

    public void setNested(boolean nested) {
        this.nested = nested;
    }
}
