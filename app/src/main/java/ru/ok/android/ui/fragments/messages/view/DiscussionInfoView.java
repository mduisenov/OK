package ru.ok.android.ui.fragments.messages.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import com.afollestad.materialdialogs.C0047R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import java.util.ArrayList;
import java.util.List;
import ru.mail.libverify.C0176R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.like.LikeManager;
import ru.ok.android.storage.Storages;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.ui.custom.photo.LikesView.OnLikesActionListener;
import ru.ok.android.ui.custom.photo.LikesViewSynced;
import ru.ok.android.ui.custom.photo.LikesViewSynced.LikesInfoChangeListener;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.fragments.messages.view.state.DiscussionAlbumState;
import ru.ok.android.ui.fragments.messages.view.state.DiscussionEmptyState;
import ru.ok.android.ui.fragments.messages.view.state.DiscussionHappeningState;
import ru.ok.android.ui.fragments.messages.view.state.DiscussionMediaTopicState;
import ru.ok.android.ui.fragments.messages.view.state.DiscussionMovieState;
import ru.ok.android.ui.fragments.messages.view.state.DiscussionPhotoState;
import ru.ok.android.ui.fragments.messages.view.state.DiscussionShareState;
import ru.ok.android.ui.fragments.messages.view.state.DiscussionState;
import ru.ok.android.ui.fragments.messages.view.state.DiscussionWebState;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.FriendlySpannableStringBuilder;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type;
import ru.ok.java.api.response.discussion.info.DiscussionGroup;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;
import ru.ok.java.api.response.discussion.info.DiscussionUser;
import ru.ok.java.api.response.discussion.info.HappeningInfo;
import ru.ok.java.api.response.video.VideoGetResponse;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.stream.LikeInfoContext;

public final class DiscussionInfoView extends FrameLayout implements OnClickListener, OnLikesActionListener, LikesInfoChangeListener {
    private final UrlImageView authorAvatar;
    private final View authorBlock;
    private final View contentBlock;
    private final FrameLayout contentHolder;
    private final TextView creationDate;
    private DiscussionState currentState;
    private ArrayAdapter<DialogItem> dialogAdapter;
    private DiscussionInfoDialogClickListener dialogClickListener;
    private boolean hasSelfLike;
    private final LikeManager likeManager;
    private final ParticipantsPreviewView likedUsersView;
    private final View likesBlock;
    private final LikesViewSynced likesView;
    private DiscussionInfoViewListener listener;
    private final TextView message;
    private final TextView title;
    private final TextView titleSecondary;
    private final TextView titleThird;

    public interface DiscussionInfoDialogClickListener {
        void onDialogItemClick(long j);
    }

    public interface DiscussionInfoViewListener {
        void onAlbumClicked(PhotoAlbumInfo photoAlbumInfo);

        void onLikeCountClicked(boolean z);

        void onLikeInfoChanged(LikeInfoContext likeInfoContext);

        void onMovieClicked(VideoGetResponse videoGetResponse);

        void onPhotoClicked(PhotoInfo photoInfo, PhotoAlbumInfo photoAlbumInfo);
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.view.DiscussionInfoView.1 */
    class C08981 implements OnClickListener {
        C08981() {
        }

        public void onClick(View v) {
            DiscussionInfoView.this.showAuthorBlockDialog(v);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.view.DiscussionInfoView.2 */
    class C08992 extends ArrayAdapter<DialogItem> {
        C08992(Context x0, int x1, int x2, List x3) {
            super(x0, x1, x2, x3);
        }

        public long getItemId(int position) {
            return (long) ((DialogItem) getItem(position)).id;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View ret = super.getView(position, convertView, parent);
            ((TextView) ret.findViewById(C0176R.id.title)).setTextColor(ViewCompat.MEASURED_STATE_MASK);
            return ret;
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.view.DiscussionInfoView.3 */
    class C09003 implements OnItemClickListener {
        C09003() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            DiscussionInfoView.this.fireAuthorDialogClick(id);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.view.DiscussionInfoView.4 */
    static /* synthetic */ class C09014 {
        static final /* synthetic */ int[] f108x72d467a8;

        static {
            f108x72d467a8 = new int[Type.values().length];
            try {
                f108x72d467a8[Type.CITY_NEWS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f108x72d467a8[Type.USER_STATUS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f108x72d467a8[Type.GROUP_TOPIC.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f108x72d467a8[Type.USER_PHOTO.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f108x72d467a8[Type.GROUP_PHOTO.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f108x72d467a8[Type.USER_ALBUM.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f108x72d467a8[Type.GROUP_MOVIE.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f108x72d467a8[Type.MOVIE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f108x72d467a8[Type.SHARE.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                f108x72d467a8[Type.HAPPENING_TOPIC.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                f108x72d467a8[Type.USER_FORUM.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                f108x72d467a8[Type.UNKNOWN.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    private static class DialogItem {
        public int id;
        private String name;

        public DialogItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    public DiscussionInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LocalizationManager.inflate(context, 2130903164, (ViewGroup) this, true);
        this.contentHolder = (FrameLayout) findViewById(2131624791);
        this.contentBlock = findViewById(2131624790);
        this.authorBlock = findViewById(2131624786);
        this.title = (TextView) findViewById(C0176R.id.title);
        this.titleSecondary = (TextView) findViewById(2131624788);
        this.titleThird = (TextView) findViewById(2131624789);
        this.authorAvatar = (UrlImageView) findViewById(2131624787);
        this.message = (TextView) findViewById(2131624538);
        this.creationDate = (TextView) findViewById(2131624779);
        this.likedUsersView = (ParticipantsPreviewView) findViewById(2131624793);
        this.likesView = (LikesViewSynced) findViewById(2131624794);
        this.likesBlock = findViewById(2131624792);
        this.authorBlock.setOnClickListener(new C08981());
        this.likedUsersView.setOnClickListener(this);
        this.contentBlock.setOnClickListener(this);
        this.likesView.setOnLikesActionListener(this);
        this.likesView.setLikesInfoChangeListener(this);
        configureForDiscussion(null, null);
        setBackgroundColor(getResources().getColor(2131493131));
        this.likeManager = Storages.getInstance(context, OdnoklassnikiApplication.getCurrentUser().getId()).getLikeManager();
    }

    public void setListener(DiscussionInfoViewListener listener) {
        this.listener = listener;
    }

    public void configureForDiscussion(DiscussionInfoResponse info, BaseFragment fragment) {
        if (info == null || info.generalInfo == null) {
            setVisibility(8);
            return;
        }
        setVisibility(0);
        updateGeneralBlock(info);
        processType(info, fragment);
        updateMainBlockVisibility();
    }

    public void configureForUserLikes(DiscussionInfoResponse info, List<UserInfo> likedUsers) {
        updateLikesBlock(info, likedUsers);
    }

    private void updateLikesBlock(DiscussionInfoResponse info, List<UserInfo> likedUsers) {
        int i = 8;
        DiscussionGeneralInfo generalInfo = info.generalInfo;
        if (generalInfo.type == Type.USER_FORUM || generalInfo.type == Type.SCHOOL_FORUM || generalInfo.type == Type.CITY_NEWS) {
            this.likesBlock.setVisibility(8);
            return;
        }
        if (likedUsers != null) {
            this.likedUsersView.setParticipants(likedUsers, false);
        }
        ParticipantsPreviewView participantsPreviewView = this.likedUsersView;
        if (likedUsers != null) {
            i = 0;
        }
        participantsPreviewView.setVisibility(i);
        processLikeInfo(generalInfo.getLikeInfo(), false);
    }

    private void processLikeInfo(LikeInfoContext likeInfo, boolean animateOwnLike) {
        LikeInfoContext syncedLikeInfo = this.likeManager.getLikeInfo(likeInfo);
        this.hasSelfLike = syncedLikeInfo.self;
        this.likesView.setLikeInfo(syncedLikeInfo, animateOwnLike);
        updateLikesBlockVisibility();
    }

    private void updateLikesBlockVisibility() {
        if (this.likedUsersView.getVisibility() == 0) {
            this.likedUsersView.setVisibility(this.likesView.textKlassCount.getVisibility());
        }
        View view = this.likesBlock;
        int i = (this.likesView.textKlass.getVisibility() == 0 || this.likedUsersView.getVisibility() == 0) ? 0 : 8;
        view.setVisibility(i);
    }

    public void onLikeInfoChanged(LikeInfoContext likeInfo) {
        this.listener.onLikeInfoChanged(likeInfo);
    }

    private void updateGeneralBlock(DiscussionInfoResponse info) {
        int avatarDefaultResourceId;
        String userAvatarUrl = null;
        DiscussionGeneralInfo generalInfo = info.generalInfo;
        DiscussionUser user = generalInfo.user;
        DiscussionGroup group = generalInfo.group;
        String ownerName = null;
        if (user != null) {
            avatarDefaultResourceId = user.gender == UserGenderType.MALE ? 2130838321 : 2130837927;
            userAvatarUrl = user.avatar;
            ownerName = user.name;
        } else if (group != null) {
            userAvatarUrl = group.avatar;
            avatarDefaultResourceId = 2130837663;
            ownerName = group.name;
        } else {
            avatarDefaultResourceId = 2130838290;
        }
        HappeningInfo happeningInfo = info.happeningInfo;
        if (happeningInfo != null) {
            if (!TextUtils.isEmpty(happeningInfo.avatarUrl)) {
                userAvatarUrl = happeningInfo.avatarUrl;
            }
            ownerName = happeningInfo.name;
        }
        if (TextUtils.isEmpty(ownerName)) {
            ownerName = generalInfo.message;
        }
        Utils.setTextViewTextWithVisibility(this.title, ownerName);
        updateSecondaryTitle(null);
        Utils.setImageViewUrlWithVisibility(this.authorAvatar, userAvatarUrl, avatarDefaultResourceId);
        Utils.setTextViewTextWithVisibility(this.creationDate, DateFormatter.getFormatStringFromDate(getContext(), generalInfo.creationDate));
        this.message.setText(generalInfo.title);
        updateAlbumInfo(info);
        this.authorBlock.setTag(info);
    }

    private void updateSecondaryTitle(String name) {
        if (TextUtils.isEmpty(name)) {
            this.titleSecondary.setVisibility(8);
            return;
        }
        FriendlySpannableStringBuilder sb = new FriendlySpannableStringBuilder();
        sb.append(LocalizationManager.from(getContext()).getString(2131165422)).append(" ");
        sb.append(name, new TextAppearanceSpan(getContext(), 2131296683));
        this.titleSecondary.setText(sb.build());
    }

    private void updateAlbumInfo(DiscussionInfoResponse info) {
        PhotoAlbumInfo albumInfo = info.albumInfo;
        if (albumInfo == null || TextUtils.isEmpty(albumInfo.getId()) || info.generalInfo.type == Type.USER_ALBUM) {
            this.titleThird.setVisibility(8);
            return;
        }
        this.titleThird.setVisibility(0);
        String albumString = LocalizationManager.from(getContext()).getString(2131165890);
        SpannableStringBuilder sb = new SpannableStringBuilder(albumString + " \u00ab" + albumInfo.getTitle() + "\u00bb");
        sb.setSpan(new ForegroundColorSpan(ViewCompat.MEASURED_STATE_MASK), albumString.length() + 1, sb.length(), 33);
        this.titleThird.setText(sb);
        this.titleThird.setTag(albumInfo);
    }

    private void processType(DiscussionInfoResponse info, BaseFragment fragment) {
        int i = 0;
        DiscussionState newState = createState(info, fragment);
        if (isStateChanged(newState)) {
            this.contentHolder.removeAllViews();
            this.message.setVisibility(isMessageVisible(newState, info) ? 0 : 8);
            if (newState != null) {
                this.contentHolder.addView(newState.createContentView(getContext()), new LayoutParams(-1, -2));
                FrameLayout frameLayout = this.contentHolder;
                if (newState instanceof DiscussionEmptyState) {
                    i = 8;
                }
                frameLayout.setVisibility(i);
            }
            this.currentState = newState;
        }
        if (this.currentState == null || !this.currentState.isDateVisible()) {
            this.creationDate.setVisibility(8);
        }
        updateState(info);
    }

    public void updateState(DiscussionInfoResponse discussion) {
        if (this.currentState != null && this.contentHolder.getChildCount() > 0) {
            this.currentState.configureView(this.contentHolder.getChildAt(0), discussion);
        }
    }

    private void updateMainBlockVisibility() {
        int visibility = (this.contentHolder.getVisibility() == 0 || this.message.getVisibility() == 0) ? 0 : 8;
        this.contentBlock.setVisibility(visibility);
    }

    private boolean isMessageVisible(DiscussionState state, DiscussionInfoResponse info) {
        return (state == null || !state.isMessageVisible() || info.generalInfo.type == Type.USER_ALBUM || TextUtils.isEmpty(info.generalInfo.title)) ? false : true;
    }

    private boolean isStateChanged(DiscussionState newState) {
        return (((newState == null ? 1 : 0) ^ (this.currentState == null ? 1 : 0)) == 0 && (this.currentState == null || newState == null || this.currentState.getClass() == newState.getClass())) ? false : true;
    }

    public void onClick(View v) {
        if (v == this.likedUsersView) {
            this.listener.onLikeCountClicked(this.hasSelfLike);
        } else if (v == this.contentBlock) {
            this.currentState.onContentClicked();
        }
    }

    public void onLikeClicked(View view, LikeInfoContext likeInfo) {
        this.likeManager.like(likeInfo);
    }

    public void onUnlikeClicked(View view, LikeInfoContext likeInfo) {
        this.likeManager.unlike(likeInfo);
    }

    public void onLikesCountClicked(View view, LikeInfoContext likeInfo) {
        this.listener.onLikeCountClicked(this.hasSelfLike);
    }

    public void onShow() {
        if (this.currentState != null) {
            this.currentState.onShow();
        }
    }

    public void onHide() {
        if (this.currentState != null) {
            this.currentState.onHide();
        }
    }

    private DiscussionState createState(DiscussionInfoResponse discussionInfo, BaseFragment fragment) {
        switch (C09014.f108x72d467a8[discussionInfo.generalInfo.type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                if (discussionInfo.mediaTopic == null) {
                    return new DiscussionWebState(discussionInfo, fragment.getWebLinksProcessor());
                }
                this.contentBlock.setClickable(false);
                return new DiscussionMediaTopicState(discussionInfo, fragment);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
            case Message.UUID_FIELD_NUMBER /*5*/:
                return new DiscussionPhotoState(discussionInfo, this.listener);
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return new DiscussionAlbumState(discussionInfo, this.listener);
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
            case Message.TASKID_FIELD_NUMBER /*8*/:
                return new DiscussionMovieState(discussionInfo, this.listener);
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                return new DiscussionShareState(discussionInfo);
            case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                return new DiscussionHappeningState(discussionInfo);
            default:
                return new DiscussionEmptyState();
        }
    }

    private void showAuthorBlockDialog(View v) {
        DiscussionInfoResponse info = (DiscussionInfoResponse) v.getTag();
        LocalizationManager localizationManager = LocalizationManager.from(getContext());
        String title = localizationManager.getString(2131165907);
        List<DialogItem> items = new ArrayList();
        if (info.generalInfo.group != null) {
            items.add(new DialogItem(2131625458, localizationManager.getString(2131165912)));
        }
        if (info.happeningInfo != null) {
            items.add(new DialogItem(2131625459, localizationManager.getString(2131165913)));
        }
        if (info.generalInfo.user != null) {
            items.add(new DialogItem(2131625460, localizationManager.getString(2131165909)));
        }
        if (info.albumInfo != null) {
            items.add(new DialogItem(2131625461, localizationManager.getString(2131165908)));
        }
        if (!items.isEmpty()) {
            if (items.size() == 1) {
                fireAuthorDialogClick((long) ((DialogItem) items.get(0)).id);
                return;
            }
            if (this.dialogAdapter == null) {
                this.dialogAdapter = new C08992(getContext(), C0047R.layout.md_listitem, C0176R.id.title, items);
            } else {
                this.dialogAdapter.clear();
                this.dialogAdapter.addAll(items);
            }
            MaterialDialog dialog = new Builder(getContext()).title(title).adapter(this.dialogAdapter).build();
            dialog.getListView().setOnItemClickListener(new C09003());
            dialog.show();
        }
    }

    private void fireAuthorDialogClick(long id) {
        if (this.dialogClickListener != null) {
            this.dialogClickListener.onDialogItemClick(id);
        }
    }

    public final DiscussionState getCurrentState() {
        return this.currentState;
    }

    public void setDialogClickListener(DiscussionInfoDialogClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
    }
}
