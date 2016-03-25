package ru.ok.android.ui.fragments.messages.adapter;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.base.OfflineTable.Status;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.emoji.smiles.SmileTextProcessor;
import ru.ok.android.emoji.ui.custom.SimpleUrlImageView;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.adapters.ScrollLoadBlocker;
import ru.ok.android.ui.custom.MessageCheckBox;
import ru.ok.android.ui.custom.TouchFloatingDelegate;
import ru.ok.android.ui.custom.animationlist.DataChangeAdapter;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.ui.custom.text.OdklUrlsTextView.OnSelectOdklLinkListener;
import ru.ok.android.ui.fragments.messages.adapter.MessageDataView.MessageDateViewProvider;
import ru.ok.android.ui.fragments.messages.loaders.data.MessagesBundle;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.ui.utils.RowPosition;
import ru.ok.android.utils.URLUtil;
import ru.ok.android.utils.Utils;
import ru.ok.android.widget.attach.BaseAttachGridView.OnAttachClickListener;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.UserInfo.UserOnlineType;
import ru.ok.model.messages.Attachment;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase;
import ru.ok.model.stickers.Sticker;

public abstract class MessagesBaseAdapter<M extends MessageBase, G extends Parcelable> extends DataChangeAdapter<MessagesBundle<M, G>> implements OnCheckedChangeListener, OnSelectOdklLinkListener, MessageDateViewProvider<M> {
    private final OnClickListener _avatarClicked;
    private final Context _context;
    private final OnClickListener _statusClicked;
    private final String _userUid;
    private OnAttachClickListener attachClickListener;
    private AttachmentSelectionListener attachSelectionListener;
    private final OnClickListener authorClicked;
    private final int defaultMargin;
    private final OnClickListener editedClicked;
    protected final ScrollLoadBlocker imageLoadBlocker;
    private boolean isAdmin;
    private final OnClickListener likeClicked;
    private final OnClickListener likesCountClicked;
    private final MessagesAdapterListener listener;
    private MessagesBundle<M, G> messagesData;
    protected int paddingBeforeDateSeparator;
    protected int paddingBeforeMessageSeparator;
    IPresentationDetailsProvider presentationDetailsProvider;
    private final OnClickListener repliedClicked;
    private final OnClickListener repliedToBlockClicked;
    private final OnClickListener replyClicked;
    private final OnClickListener replyWithStickerListener;
    private final Set<Long> selectedIds;
    private boolean selectedUse;
    private final Set<Long> sendingMessages;
    private final Interpolator sentMessageInterpolator;
    private final Map<Long, Long> sentTimes;
    private final int stickerPadding;
    private final int stickerSize;
    private List<SimpleUrlImageView> stickersCache;
    private final int stickersMargin;
    protected final Map<String, UserInfo> userInfos;

    public interface AttachmentSelectionListener {
        void onAttachmentSelected(View view, List<Attachment> list, Attachment attachment);
    }

    public interface MessagesAdapterListener<M extends MessageBase> {
        RowPosition getRowPositionType(int i, int i2);

        void onAuthorClicked(String str, String str2);

        void onEditedClicked(OfflineMessage<M> offlineMessage);

        void onLikeClicked(M m);

        void onLikeCountClicked(String str);

        void onLinkClicked(String str);

        void onMessageChecked(int i, boolean z);

        void onRepliedToClicked(OfflineMessage<M> offlineMessage);

        void onReplyClicked(OfflineMessage<M> offlineMessage);

        void onStatusClicked(OfflineMessage<M> offlineMessage);

        void onStickersClicked(M m);
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.1 */
    class C08641 implements OnClickListener {
        C08641() {
        }

        public void onClick(View view) {
            MessagesBaseAdapter.this.callAuthorClicked((MessageAuthor) view.getTag());
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.2 */
    class C08652 implements OnClickListener {
        C08652() {
        }

        public void onClick(View view) {
            MessagesBaseAdapter.this.listener.onRepliedToClicked((OfflineMessage) view.getTag());
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.3 */
    class C08663 implements OnClickListener {
        C08663() {
        }

        public void onClick(View view) {
            OfflineMessage<M> message = (OfflineMessage) view.getTag();
            MessagesBaseAdapter.this.messagesData.messages.indexOf(message);
            MessagesBaseAdapter.this.listener.onReplyClicked(message);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.4 */
    class C08674 implements OnClickListener {
        C08674() {
        }

        public void onClick(View view) {
            MessagesBaseAdapter.this.listener.onLikeClicked((MessageBase) view.getTag());
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.5 */
    class C08685 implements OnClickListener {
        C08685() {
        }

        public void onClick(View view) {
            MessagesBaseAdapter.this.listener.onLikeCountClicked((String) view.getTag());
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.6 */
    class C08696 implements OnClickListener {
        C08696() {
        }

        public void onClick(View view) {
            MessagesBaseAdapter.this.listener.onStatusClicked((OfflineMessage) view.getTag());
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.7 */
    class C08707 implements OnClickListener {
        C08707() {
        }

        public void onClick(View view) {
            MessagesBaseAdapter.this.listener.onEditedClicked((OfflineMessage) view.getTag());
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.8 */
    class C08718 implements OnClickListener {
        C08718() {
        }

        public void onClick(View view) {
            MessagesBaseAdapter.this.listener.onRepliedToClicked((OfflineMessage) view.getTag());
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.9 */
    class C08729 implements OnClickListener {
        C08729() {
        }

        public void onClick(View v) {
            MessagesBaseAdapter.this.callAuthorClicked((MessageAuthor) v.getTag());
        }
    }

    public enum RowAvatarPosition {
        LEFT(2130903323) {
            int extractBubbleResourceId(RowPosition position) {
                return position.getBackgroundLeftResourceId();
            }
        },
        RIGHT(2130903324) {
            int extractBubbleResourceId(RowPosition position) {
                return position.getBackgroundRightResourceId();
            }
        };
        
        private final int layoutResourceId;

        abstract int extractBubbleResourceId(RowPosition rowPosition);

        private RowAvatarPosition(int layoutResourceId) {
            this.layoutResourceId = layoutResourceId;
        }
    }

    protected class ViewHolder {
        protected final AvatarImageView avatar;
        protected final MessageCheckBox checkbox;
        protected final View editedIcon;
        protected final MessageDataView messageDataView;
        protected View replyWithStickerContainer;
        protected ViewGroup replyWithStickers;
        protected ViewStub replyWithStickersStub;
        protected final RelativeLayout row;
        protected final ImageView status;

        @SuppressLint({"WrongViewCast"})
        public ViewHolder(View view) {
            this.row = (RelativeLayout) view;
            this.messageDataView = (MessageDataView) view.findViewById(2131624716);
            this.messageDataView.setProvider(MessagesBaseAdapter.this);
            this.avatar = (AvatarImageView) view.findViewById(2131624657);
            this.status = (ImageView) view.findViewById(2131624717);
            this.checkbox = (MessageCheckBox) view.findViewById(2131624713);
            if (this.avatar != null) {
                this.avatar.setOnClickListener(MessagesBaseAdapter.this._avatarClicked);
            }
            if (this.status != null) {
                this.status.setOnClickListener(MessagesBaseAdapter.this._statusClicked);
            }
            this.checkbox.addOnCheckedChangeListener(MessagesBaseAdapter.this);
            this.editedIcon = view.findViewById(2131624714);
            this.editedIcon.setOnClickListener(MessagesBaseAdapter.this.editedClicked);
            ((View) this.editedIcon.getParent()).setTouchDelegate(new TouchFloatingDelegate(this.editedIcon, (int) Utils.dipToPixels(10.0f)));
            this.replyWithStickersStub = (ViewStub) view.findViewById(2131625087);
        }

        public View getReplyWithStickersView() {
            return this.replyWithStickersStub != null ? this.replyWithStickersStub : this.replyWithStickerContainer;
        }

        public void inflateReplyWithStickers() {
            if (this.replyWithStickersStub != null) {
                this.replyWithStickerContainer = this.replyWithStickersStub.inflate();
                this.replyWithStickers = (ViewGroup) this.replyWithStickerContainer.findViewById(2131625089);
                this.replyWithStickerContainer.setOnClickListener(MessagesBaseAdapter.this.replyWithStickerListener);
                this.replyWithStickersStub = null;
            }
        }
    }

    protected abstract boolean isCommentingAllowed();

    public void setPresentationDetailsProvider(IPresentationDetailsProvider presentationDetailsProvider) {
        this.presentationDetailsProvider = presentationDetailsProvider;
    }

    public MessagesBaseAdapter(Context context, String userUid, MessagesAdapterListener listener) {
        this.imageLoadBlocker = ScrollLoadBlocker.forIdleOnly();
        this.selectedUse = false;
        this.userInfos = new HashMap();
        this.authorClicked = new C08641();
        this.repliedClicked = new C08652();
        this.replyClicked = new C08663();
        this.likeClicked = new C08674();
        this.likesCountClicked = new C08685();
        this._statusClicked = new C08696();
        this.editedClicked = new C08707();
        this.repliedToBlockClicked = new C08718();
        this._avatarClicked = new C08729();
        this.replyWithStickerListener = new OnClickListener() {
            public void onClick(View v) {
                MessagesBaseAdapter.this.listener.onStickersClicked((MessageBase) v.getTag());
            }
        };
        this.selectedIds = new HashSet();
        this.sendingMessages = new HashSet();
        this.sentTimes = new HashMap();
        this.sentMessageInterpolator = new AccelerateInterpolator();
        this._context = context;
        this._userUid = userUid;
        this.paddingBeforeDateSeparator = (int) TypedValue.applyDimension(2, 12.0f, this._context.getResources().getDisplayMetrics());
        this.paddingBeforeMessageSeparator = (int) TypedValue.applyDimension(2, 2.0f, this._context.getResources().getDisplayMetrics());
        this.listener = listener;
        Resources resources = getContext().getResources();
        this.stickerSize = resources.getDimensionPixelSize(2131231186);
        this.stickerPadding = resources.getDimensionPixelSize(2131231185);
        this.defaultMargin = resources.getDimensionPixelOffset(2131231081);
        this.stickersMargin = resources.getDimensionPixelOffset(2131231083);
    }

    public MessagesBundle<M, G> getData() {
        return this.messagesData;
    }

    public void setData(MessagesBundle<M, G> info) {
        this.messagesData = info;
        for (int i = 0; i < getCount(); i++) {
            long itemId = getItemId(i);
            OfflineMessage<M> item = getItem(i);
            if (!(item == null || item.offlineData == null)) {
                Status status = item.offlineData.status;
                if (status == Status.SENDING || status == Status.WAITING || status == Status.LOCKED) {
                    this.sendingMessages.add(Long.valueOf(itemId));
                }
            }
        }
    }

    private void callAuthorClicked(@Nullable MessageAuthor author) {
        if (author != null) {
            this.listener.onAuthorClicked(author.getId(), author.getType());
        }
    }

    protected View newView(Context context, ViewGroup parent, int position) {
        View result = LayoutInflater.from(context).inflate(getAvatarPosition(position).layoutResourceId, parent, false);
        ViewHolder holder = new ViewHolder(result);
        holder.messageDataView.setExpandRepliedToMessage(isWantToShowNames());
        result.setTag(holder);
        return result;
    }

    protected void bindView(ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter$ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.ViewHolder holder, int position, OfflineMessage<M> message) {
        int avatarVisibility;
        boolean wasEdited;
        int i = 0;
        updateCheckbox(message, holder);
        updateStatus(message, holder);
        RowPosition rowPosition = this.listener.getRowPositionType(position, position);
        MessageDataView messageDataView = holder.messageDataView;
        messageDataView.setMessage(message, rowPosition);
        if (rowPosition.isAvatarVisible()) {
            avatarVisibility = 0;
        } else {
            avatarVisibility = 8;
        }
        if (holder.avatar != null) {
            holder.avatar.setVisibility(avatarVisibility);
        }
        if (avatarVisibility == 0) {
            updateAvatar(message.message, holder);
        }
        messageDataView.setSelected(this.selectedIds.contains(Long.valueOf(getItemId(position))));
        RelativeLayout relativeLayout = holder.row;
        int paddingLeft = holder.row.getPaddingLeft();
        int i2 = (rowPosition.isAvatarVisible() || shouldHavePaddingBefore(position)) ? this.paddingBeforeDateSeparator : this.paddingBeforeMessageSeparator;
        relativeLayout.setPadding(paddingLeft, i2, holder.row.getPaddingRight(), holder.row.getPaddingBottom());
        updateMessageViewBackground(holder, message, rowPosition);
        updateReplyWithSticker(holder, message);
        messageDataView.setAttachments(message);
        if (message.message.dateEdited > 0) {
            wasEdited = true;
        } else {
            wasEdited = false;
        }
        View view = holder.editedIcon;
        if (!wasEdited) {
            i = 8;
        }
        view.setVisibility(i);
        if (wasEdited) {
            holder.editedIcon.setTag(message);
        }
    }

    private void updateReplyWithSticker(ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter$ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.ViewHolder holder, OfflineMessage<M> message) {
        View replyWithStickersView = holder.getReplyWithStickersView();
        if (replyWithStickersView != null) {
            collectStickerViews(holder.replyWithStickers);
            if (message.message.replyStickers.isEmpty()) {
                replyWithStickersView.setVisibility(8);
                updateRightMargin(holder.messageDataView, this.defaultMargin);
                return;
            }
            updateRightMargin(holder.messageDataView, this.stickersMargin);
            holder.inflateReplyWithStickers();
            holder.replyWithStickerContainer.setVisibility(0);
            holder.replyWithStickerContainer.setTag(message.message);
            int i = 0;
            while (i < message.message.replyStickers.size()) {
                Sticker sticker = (Sticker) message.message.replyStickers.get(i);
                SimpleUrlImageView draweeView = getStickerView();
                draweeView.setUrl(SmileTextProcessor.paymentSmileUrl(sticker.code));
                ((MarginLayoutParams) draweeView.getLayoutParams()).rightMargin = i == message.message.replyStickers.size() + -1 ? 0 : this.stickerPadding;
                holder.replyWithStickers.addView(draweeView);
                i++;
            }
        }
    }

    private void updateRightMargin(View view, int margin) {
        MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams();
        lp.rightMargin = margin;
        view.setLayoutParams(lp);
    }

    private void collectStickerViews(ViewGroup container) {
        if (container != null && container.getChildCount() != 0) {
            if (this.stickersCache == null) {
                this.stickersCache = new ArrayList();
            }
            for (int i = 0; i < container.getChildCount(); i++) {
                this.stickersCache.add((SimpleUrlImageView) container.getChildAt(i));
            }
            container.removeAllViews();
        }
    }

    @NonNull
    private SimpleUrlImageView getStickerView() {
        if (this.stickersCache != null && !this.stickersCache.isEmpty()) {
            return (SimpleUrlImageView) this.stickersCache.remove(this.stickersCache.size() - 1);
        }
        SimpleUrlImageView view = new SimpleUrlImageView(getContext());
        LayoutParams lp = new LayoutParams(this.stickerSize, this.stickerSize);
        view.setPlaceholderId(C0263R.drawable.ic_placeholder_paidsmile);
        view.setLayoutParams(lp);
        return view;
    }

    private boolean shouldHavePaddingBefore(int position) {
        if (this.presentationDetailsProvider != null) {
            return this.presentationDetailsProvider.shouldHavePaddingBefore(position);
        }
        return false;
    }

    protected void updateMessageViewBackground(ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter$ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.ViewHolder holder, OfflineMessage<M> message, RowPosition rowPosition) {
        holder.messageDataView.setBackgroundResource(getAvatarPosition(message.message).extractBubbleResourceId(rowPosition));
    }

    private void updateCheckbox(OfflineMessage<M> message, ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter$ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter$ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.ViewHolder holder) {
        if (this.selectedUse) {
            holder.checkbox.setVisibility(0);
            boolean selected = this.selectedIds.contains(Long.valueOf(identifyMessageId(-1, message)));
            holder.checkbox.setTag(Integer.valueOf(this.messagesData.messages.indexOf(message)));
            holder.checkbox.setChecked(selected);
            return;
        }
        holder.checkbox.setVisibility(8);
    }

    public int getCount() {
        return this.messagesData == null ? 0 : this.messagesData.messages.size();
    }

    public OfflineMessage<M> getItem(int position) {
        return this.messagesData == null ? null : (OfflineMessage) this.messagesData.messages.get(position);
    }

    public long getItemId(int position) {
        if (this.messagesData == null) {
            return (long) position;
        }
        return identifyMessageId(position, (OfflineMessage) this.messagesData.messages.get(position));
    }

    protected static <M extends MessageBase> long identifyMessageId(int defaultValue, OfflineMessage<M> offlineMessage) {
        M message = offlineMessage.message;
        if (offlineMessage.offlineData != null) {
            return (long) offlineMessage.offlineData.databaseId;
        }
        if (TextUtils.isEmpty(message.id)) {
            return (long) defaultValue;
        }
        return (long) message.id.hashCode();
    }

    private RowAvatarPosition getAvatarPosition(int position) {
        return getAvatarPosition(getItem(position).message);
    }

    private RowAvatarPosition getAvatarPosition(M message) {
        return isMy(message.authorId) ? RowAvatarPosition.RIGHT : RowAvatarPosition.LEFT;
    }

    public int getViewTypeCount() {
        return RowAvatarPosition.values().length;
    }

    public int getItemViewType(int position) {
        return getAvatarPosition(position).ordinal();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newView(this._context, parent, position);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder != null) {
            bindView(holder, position, getItem(position));
        }
        return convertView;
    }

    public boolean isReplyDisallowed(boolean callFromAdapter, M message) {
        boolean commentId;
        if (message.hasServerId()) {
            commentId = false;
        } else {
            commentId = true;
        }
        String authorId = message.authorId;
        if ((this.isAdmin || !TextUtils.equals(authorId, this._userUid)) && ((!this.isAdmin || !TextUtils.equals(authorId, getGroupId())) && !commentId && isCommentingAllowed())) {
            return false;
        }
        return true;
    }

    @Nullable
    private UserInfo getUser(String userId) {
        UserInfo result = (UserInfo) this.userInfos.get(userId);
        if (result != null) {
            return result;
        }
        UserInfo currentUser = OdnoklassnikiApplication.getCurrentUser();
        if (TextUtils.equals(userId, currentUser.uid)) {
            return currentUser;
        }
        return null;
    }

    private void cancelTagAnimation(View view) {
        if (view.getTag() instanceof Animator) {
            ((Animator) view.getTag()).end();
            view.setTag(null);
        }
    }

    public boolean isMessageNew(M message) {
        boolean isNew;
        int i;
        int i2 = 1;
        long dateToCompare = this.messagesData.initialAccessDate;
        boolean isMy = isMy(message.authorId);
        if (message.date > dateToCompare) {
            isNew = true;
        } else {
            isNew = false;
        }
        if (dateToCompare > 0) {
            i = 1;
        } else {
            i = 0;
        }
        isNew &= i;
        if (isMy) {
            i2 = 0;
        }
        return isNew & i2;
    }

    public boolean isSendingAttachment(String localId) {
        if (localId == null || this.sendingMessages.isEmpty()) {
            return false;
        }
        List<OfflineMessage<M>> messages = getMessages();
        if (messages == null) {
            return false;
        }
        for (OfflineMessage<M> m : messages) {
            if (m.message.taskId != null) {
                Attachment[] attachments = m.message.attachments;
                if (attachments != null) {
                    for (Attachment a : attachments) {
                        if (localId.equals(a.localId)) {
                            return this.sendingMessages.contains(Long.valueOf(identifyMessageId(-1, m)));
                        }
                    }
                    continue;
                } else {
                    continue;
                }
            }
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateStatus(ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage<M> r19, ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter$ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter$ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.ViewHolder r20) {
        /*
        r18 = this;
        r0 = r20;
        r14 = r0.status;
        if (r14 != 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r0 = r19;
        r14 = r0.offlineData;
        if (r14 == 0) goto L_0x0097;
    L_0x000d:
        r0 = r19;
        r14 = r0.offlineData;
        r12 = r14.status;
    L_0x0013:
        r0 = r20;
        r14 = r0.status;
        r0 = r18;
        r0.cancelTagAnimation(r14);
        r14 = ru.ok.android.db.base.OfflineTable.Status.RECEIVED;
        if (r12 == r14) goto L_0x0024;
    L_0x0020:
        r14 = ru.ok.android.db.base.OfflineTable.Status.SENT;
        if (r12 != r14) goto L_0x0128;
    L_0x0024:
        r14 = -1;
        r0 = r19;
        r10 = identifyMessageId(r14, r0);
        r13 = 0;
        r0 = r18;
        r14 = r0.sentTimes;
        r15 = java.lang.Long.valueOf(r10);
        r14 = r14.containsKey(r15);
        if (r14 == 0) goto L_0x009a;
    L_0x003a:
        r0 = r18;
        r14 = r0.sentTimes;
        r15 = java.lang.Long.valueOf(r10);
        r13 = r14.get(r15);
        r13 = (java.lang.Long) r13;
    L_0x0048:
        if (r13 == 0) goto L_0x0115;
    L_0x004a:
        r14 = r13.longValue();
        r16 = 0;
        r14 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r14 <= 0) goto L_0x0115;
    L_0x0054:
        r14 = java.lang.System.currentTimeMillis();
        r16 = r13.longValue();
        r6 = r14 - r16;
        r14 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r14 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1));
        if (r14 >= 0) goto L_0x010a;
    L_0x0064:
        r0 = r20;
        r14 = r0.status;
        r15 = 0;
        r14.setVisibility(r15);
        r0 = r20;
        r14 = r0.status;
        r15 = 2130838106; // 0x7f02025a float:1.7281185E38 double:1.052773905E-314;
        r14.setBackgroundResource(r15);
        r0 = r20;
        r14 = r0.status;
        r4 = r14.getBackground();
        r3 = 0;
        r14 = r4 instanceof android.graphics.drawable.AnimationDrawable;
        if (r14 == 0) goto L_0x00ca;
    L_0x0083:
        r5 = r4;
        r5 = (android.graphics.drawable.AnimationDrawable) r5;
        r8 = 0;
    L_0x0087:
        r14 = r5.getNumberOfFrames();
        r14 = r14 + -1;
        if (r8 >= r14) goto L_0x00c7;
    L_0x008f:
        r14 = r5.getDuration(r8);
        r3 = r3 + r14;
        r8 = r8 + 1;
        goto L_0x0087;
    L_0x0097:
        r12 = 0;
        goto L_0x0013;
    L_0x009a:
        r0 = r18;
        r14 = r0.sendingMessages;
        r15 = java.lang.Long.valueOf(r10);
        r14 = r14.contains(r15);
        if (r14 == 0) goto L_0x0048;
    L_0x00a8:
        r0 = r18;
        r14 = r0.sentTimes;
        r15 = java.lang.Long.valueOf(r10);
        r16 = java.lang.System.currentTimeMillis();
        r13 = java.lang.Long.valueOf(r16);
        r14.put(r15, r13);
        r0 = r18;
        r14 = r0.sendingMessages;
        r15 = java.lang.Long.valueOf(r10);
        r14.remove(r15);
        goto L_0x0048;
    L_0x00c7:
        r5.start();
    L_0x00ca:
        r3 = r3 / 2;
        r0 = r20;
        r14 = r0.status;
        r15 = "alpha";
        r16 = 2;
        r0 = r16;
        r0 = new float[r0];
        r16 = r0;
        r16 = {1065353216, 0};
        r2 = android.animation.ObjectAnimator.ofFloat(r14, r15, r16);
        r0 = r18;
        r14 = r0.sentMessageInterpolator;
        r2.setInterpolator(r14);
        r14 = r3 + 500;
        r14 = (long) r14;
        r2.setDuration(r14);
        r14 = (long) r3;
        r2.setStartDelay(r14);
        r2.setCurrentPlayTime(r6);
        r2.start();
        r0 = r20;
        r14 = r0.status;
        r14.setTag(r2);
        r0 = r20;
        r14 = r0.status;
        r15 = 0;
        r14.setClickable(r15);
        goto L_0x0006;
    L_0x010a:
        r0 = r18;
        r14 = r0.sentTimes;
        r15 = java.lang.Long.valueOf(r10);
        r14.remove(r15);
    L_0x0115:
        r0 = r20;
        r14 = r0.status;
        r15 = 0;
        r14.setBackgroundDrawable(r15);
        r0 = r20;
        r14 = r0.status;
        r15 = 8;
        r14.setVisibility(r15);
        goto L_0x0006;
    L_0x0128:
        if (r12 == 0) goto L_0x016c;
    L_0x012a:
        r9 = r12.getIconResourceId();
    L_0x012e:
        if (r9 <= 0) goto L_0x016e;
    L_0x0130:
        r0 = r20;
        r14 = r0.status;
        r15 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r14.setAlpha(r15);
        r0 = r20;
        r14 = r0.status;
        r15 = 0;
        r14.setVisibility(r15);
        r0 = r20;
        r14 = r0.status;
        r14.setBackgroundResource(r9);
        r0 = r20;
        r14 = r0.status;
        r15 = 1;
        r14.setClickable(r15);
        r0 = r20;
        r14 = r0.status;
        r4 = r14.getBackground();
        r14 = r4 instanceof android.graphics.drawable.AnimationDrawable;
        if (r14 == 0) goto L_0x0161;
    L_0x015c:
        r4 = (android.graphics.drawable.AnimationDrawable) r4;
        r4.start();
    L_0x0161:
        r0 = r20;
        r14 = r0.status;
        r0 = r19;
        r14.setTag(r0);
        goto L_0x0006;
    L_0x016c:
        r9 = 0;
        goto L_0x012e;
    L_0x016e:
        r0 = r20;
        r14 = r0.status;
        r15 = 0;
        r14.setBackgroundDrawable(r15);
        r0 = r20;
        r14 = r0.status;
        r15 = 8;
        r14.setVisibility(r15);
        goto L_0x0006;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.updateStatus(ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage, ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter$ViewHolder):void");
    }

    private void updateAvatar(MessageBase comment, ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter$ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.ViewHolder holder) {
        if (holder.avatar != null) {
            String authorType = comment.authorType;
            boolean isMan = true;
            UserOnlineType onlineType = UserOnlineType.OFFLINE;
            String url = getAuthorAvatar(authorType, comment.authorId);
            if (TextUtils.isEmpty(authorType)) {
                UserInfo userInfo = getUser(comment.authorId);
                if (userInfo != null) {
                    isMan = userInfo.genderType == UserGenderType.MALE;
                    onlineType = Utils.onlineStatus(userInfo);
                }
            }
            ImageViewManager.getInstance().displayImage(url, holder.avatar, isMan, this.imageLoadBlocker);
            holder.avatar.updateOnlineViewForMessages(onlineType);
            holder.avatar.setTag(new MessageAuthor(comment.authorId, comment.authorType));
        }
    }

    public String getAuthorAvatar(String authorType, String authorId) {
        String url = null;
        if ("GROUP".equals(authorType)) {
            url = getGroupAvatar();
        } else if (TextUtils.isEmpty(authorType)) {
            UserInfo userInfo = getUser(authorId);
            if (userInfo != null) {
                url = userInfo.picUrl;
            }
        }
        if (URLUtil.isStubUrl(url)) {
            return null;
        }
        return url;
    }

    public boolean isMy(String authorId) {
        return (!this.isAdmin && TextUtils.equals(this._userUid, authorId)) || (this.isAdmin && TextUtils.equals(getGroupId(), authorId));
    }

    public void updateUserInfos(Set<UserInfo> users) {
        this.userInfos.clear();
        for (UserInfo user : users) {
            this.userInfos.put(user.uid, user);
        }
    }

    public void setAttachmentSelectionListener(AttachmentSelectionListener listener) {
        this.attachSelectionListener = listener;
    }

    public List<OfflineMessage<M>> getMessages() {
        if (this.messagesData != null) {
            return this.messagesData.messages;
        }
        return null;
    }

    public OnScrollListener getScrollListener() {
        return this.imageLoadBlocker;
    }

    public void setIsAdmin(boolean admin) {
        this.isAdmin = admin;
        notifyDataSetChanged();
    }

    public void onSelectOdklLink(String url) {
        this.listener.onLinkClicked(url);
    }

    public void setSelected(OfflineMessage<M> message, boolean isSelected, boolean isSilent) {
        long id = identifyMessageId(-1, message);
        if (isSelected) {
            this.selectedIds.add(Long.valueOf(id));
        } else {
            this.selectedIds.remove(Long.valueOf(id));
        }
        if (!isSilent) {
            notifyDataSetChanged();
        }
    }

    public void clearSelection() {
        this.selectedIds.clear();
        notifyDataSetChanged();
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int index = ((Integer) buttonView.getTag()).intValue();
        this.listener.onMessageChecked(index, isChecked);
        if (this.messagesData != null) {
            setSelected((OfflineMessage) this.messagesData.messages.get(index), isChecked, true);
        }
    }

    public HandleBlocker getBlocker() {
        return this.imageLoadBlocker;
    }

    public String getAuthorName(String authorType, String authorId) {
        if ("GROUP".equals(authorType)) {
            return getGroupName();
        }
        UserInfo userInfo = getUser(authorId);
        return userInfo != null ? userInfo.getAnyName() : null;
    }

    protected Context getContext() {
        return this._context;
    }

    public Map<String, UserInfo> getUsers() {
        return this.userInfos;
    }

    public boolean isInActionMode() {
        return this.selectedUse;
    }

    public void showSelectedUse() {
        this.selectedUse = true;
    }

    public void cancelSelectedUse() {
        this.selectedUse = false;
    }

    public OnAttachClickListener getAttachClickListener() {
        if (this.attachClickListener == null) {
            this.attachClickListener = new OnAttachClickListener() {
                public void onAttachClick(View view, List<Attachment> attachments, Attachment selected) {
                    if (MessagesBaseAdapter.this.attachSelectionListener != null) {
                        MessagesBaseAdapter.this.attachSelectionListener.onAttachmentSelected(view, attachments, selected);
                    }
                }
            };
        }
        return this.attachClickListener;
    }

    public OnClickListener getRepliedToClickListener() {
        return this.repliedClicked;
    }

    public OnClickListener getLikeClickListener() {
        return this.likeClicked;
    }

    public OnClickListener getLikesCountClickListener() {
        return this.likesCountClicked;
    }

    public OnClickListener getReplyClickListener() {
        return this.replyClicked;
    }

    public OnClickListener getAuthorClickListener() {
        return this.authorClicked;
    }

    public OnClickListener getRepliedToBlockClickListener() {
        return this.repliedToBlockClicked;
    }

    protected String getGroupAvatar() {
        return null;
    }

    protected String getGroupId() {
        return null;
    }

    public String getGroupName() {
        return null;
    }
}
