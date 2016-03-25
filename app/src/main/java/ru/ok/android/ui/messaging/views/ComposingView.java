package ru.ok.android.ui.messaging.views;

import android.animation.Animator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.ConversationProto.Conversation.Type;
import ru.ok.android.services.app.messaging.OdklMessagingEventsService;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.ui.fragments.messages.adapter.IChatStateHandler;
import ru.ok.android.ui.fragments.messages.adapter.IChatStateProvider;
import ru.ok.android.ui.fragments.messages.helpers.DecodedChatId;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.URLUtil;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.animation.SimpleAnimatorListener;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.messages.MessageAuthor;

public final class ComposingView extends LinearLayout implements IChatStateHandler {
    private static final int AVATAR_SIZE;
    public static final int COMPOSING_VIEW_HEIGHT;
    private static final int HIDDEN_TRANSLATION_Y;
    private LinearLayout avatarsContainer;
    private volatile IChatStateProvider chatStateProvider;
    private ComposingPencilView composingPencilView;
    private TextView composingText;
    private Conversation conversation;
    private DecodedChatId decodedChat;
    private HandleBlocker imageLoadBlocker;
    private boolean isMultichat;
    boolean isUpdating;
    private final Map<String, UserInfo> userInfos;
    ArrayList<Long> usersComposing;

    /* renamed from: ru.ok.android.ui.messaging.views.ComposingView.10 */
    class AnonymousClass10 extends SimpleAnimatorListener {
        final /* synthetic */ Runnable val$endRunnable;

        AnonymousClass10(Runnable runnable) {
            this.val$endRunnable = runnable;
        }

        public void onAnimationEnd(Animator animation) {
            ComposingView.this.invalidateComposingAnimation();
            ComposingView.this.setVisibility(8);
            if (this.val$endRunnable != null) {
                this.val$endRunnable.run();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.views.ComposingView.11 */
    class AnonymousClass11 implements Runnable {
        final /* synthetic */ long val$decodedUserId;

        AnonymousClass11(long j) {
            this.val$decodedUserId = j;
        }

        public void run() {
            ComposingView.this.addUserToComposing(this.val$decodedUserId);
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.views.ComposingView.12 */
    class AnonymousClass12 implements Runnable {
        final /* synthetic */ long val$decodedUserId;

        AnonymousClass12(long j) {
            this.val$decodedUserId = j;
        }

        public void run() {
            ComposingView.this.removeUserFromComposing(this.val$decodedUserId);
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.views.ComposingView.1 */
    class C10511 implements Runnable {
        final /* synthetic */ Long val$composingUsersId;

        C10511(Long l) {
            this.val$composingUsersId = l;
        }

        public void run() {
            ComposingView.this.addUserToComposing(this.val$composingUsersId.longValue());
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.views.ComposingView.2 */
    class C10522 implements Runnable {
        C10522() {
        }

        public void run() {
            ComposingView.this.stopIsUpdatingAndUpdateUI();
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.views.ComposingView.3 */
    class C10533 implements Runnable {
        C10533() {
        }

        public void run() {
            ComposingView.this.stopIsUpdatingAndUpdateUI();
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.views.ComposingView.4 */
    class C10554 implements Runnable {
        final /* synthetic */ ArrayList val$shownUserViewsToRemove;

        /* renamed from: ru.ok.android.ui.messaging.views.ComposingView.4.1 */
        class C10541 implements Runnable {
            C10541() {
            }

            public void run() {
                ComposingView.this.stopIsUpdatingAndUpdateUI();
            }
        }

        C10554(ArrayList arrayList) {
            this.val$shownUserViewsToRemove = arrayList;
        }

        public void run() {
            ComposingView.this.removeUserAvatarsViewsForMultichat(this.val$shownUserViewsToRemove, new C10541());
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.views.ComposingView.5 */
    class C10565 implements Runnable {
        final /* synthetic */ Runnable val$endRunnable;
        final /* synthetic */ ArrayList val$userIdsToAdd;

        C10565(ArrayList arrayList, Runnable runnable) {
            this.val$userIdsToAdd = arrayList;
            this.val$endRunnable = runnable;
        }

        public void run() {
            ComposingView.this.addUserAvatarsViewsForMultichat(this.val$userIdsToAdd, this.val$endRunnable);
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.views.ComposingView.6 */
    class C10576 extends SimpleAnimatorListener {
        final /* synthetic */ Runnable val$endRunnable;
        final /* synthetic */ ArrayList val$userIdsToAdd;

        C10576(ArrayList arrayList, Runnable runnable) {
            this.val$userIdsToAdd = arrayList;
            this.val$endRunnable = runnable;
        }

        public void onAnimationEnd(Animator animation) {
            ComposingView.this.invalidateComposingAnimation();
            ComposingView.this.addUserAvatarsViewsForMultichat(this.val$userIdsToAdd, this.val$endRunnable);
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.views.ComposingView.7 */
    class C10587 implements Runnable {
        final /* synthetic */ Runnable val$endRunnable;
        final /* synthetic */ View val$userViewToRemove;
        final /* synthetic */ ArrayList val$userViewsToRemove;

        C10587(View view, ArrayList arrayList, Runnable runnable) {
            this.val$userViewToRemove = view;
            this.val$userViewsToRemove = arrayList;
            this.val$endRunnable = runnable;
        }

        public void run() {
            ComposingView.this.removeUserAvatarFromContainer(this.val$userViewToRemove, this.val$userViewsToRemove, this.val$endRunnable);
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.views.ComposingView.8 */
    class C10598 extends SimpleAnimatorListener {
        final /* synthetic */ Runnable val$endRunnable;
        final /* synthetic */ View val$userViewToRemove;
        final /* synthetic */ ArrayList val$userViewsToRemove;

        C10598(View view, ArrayList arrayList, Runnable runnable) {
            this.val$userViewToRemove = view;
            this.val$userViewsToRemove = arrayList;
            this.val$endRunnable = runnable;
        }

        public void onAnimationEnd(Animator animation) {
            ComposingView.this.removeUserAvatarFromContainer(this.val$userViewToRemove, this.val$userViewsToRemove, this.val$endRunnable);
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.views.ComposingView.9 */
    class C10609 extends SimpleAnimatorListener {
        final /* synthetic */ Runnable val$endRunnable;

        C10609(Runnable runnable) {
            this.val$endRunnable = runnable;
        }

        public void onAnimationEnd(Animator animation) {
            ComposingView.this.invalidateComposingAnimation();
            if (this.val$endRunnable != null) {
                this.val$endRunnable.run();
            }
        }
    }

    static {
        COMPOSING_VIEW_HEIGHT = (int) Utils.dipToPixels(29.0f);
        AVATAR_SIZE = (int) Utils.dipToPixels(20.0f);
        HIDDEN_TRANSLATION_Y = COMPOSING_VIEW_HEIGHT;
    }

    public ComposingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.userInfos = new HashMap();
        this.isMultichat = false;
        this.isUpdating = false;
        this.usersComposing = new ArrayList();
        init(context);
    }

    private void init(Context context) {
        LocalizationManager.inflate(context, 2130903134, (ViewGroup) this, true);
        setTranslationY((float) HIDDEN_TRANSLATION_Y);
        setVisibility(8);
        setPadding(0, (int) Utils.dipToPixels(3.0f), 0, (int) Utils.dipToPixels(3.0f));
        setClipChildren(false);
        setClipToPadding(false);
        setLayoutParams(new LayoutParams(-1, -2));
        setGravity(17);
        setBackgroundResource(2130837825);
        this.avatarsContainer = (LinearLayout) findViewById(2131624728);
        this.composingPencilView = (ComposingPencilView) findViewById(2131624726);
        this.composingText = (TextView) findViewById(2131624727);
    }

    private void invalidateComposingAnimation() {
        if (this.usersComposing.size() > 0) {
            if (!this.composingPencilView.hasStarted()) {
                this.composingPencilView.startAnimation();
            }
        } else if (this.composingPencilView.hasStarted()) {
            this.composingPencilView.stopAnimation();
        }
    }

    public void setUserInfos(Conversation chat, Collection<UserInfo> users) {
        this.conversation = chat;
        this.isMultichat = chat.getType() != Type.PRIVATE;
        this.userInfos.clear();
        if (users != null) {
            for (UserInfo user : users) {
                this.userInfos.put(user.uid, user);
            }
        }
        if (chat != null) {
            this.decodedChat = OdklMessagingEventsService.decodeChatId(chat.getId());
            if (this.chatStateProvider != null) {
                List<Long> composingUsersIds = this.chatStateProvider.getServerState(this.conversation.getId());
                if (composingUsersIds != null) {
                    for (Long composingUsersId : composingUsersIds) {
                        ThreadUtil.executeOnMain(new C10511(composingUsersId));
                    }
                }
            }
        }
    }

    public void setImageLoadBlocker(HandleBlocker imageLoadBlocker) {
        this.imageLoadBlocker = imageLoadBlocker;
    }

    public void addUserToComposing(long userId) {
        if (!this.usersComposing.contains(Long.valueOf(userId))) {
            this.usersComposing.add(Long.valueOf(userId));
            updateUI();
        }
    }

    public void removeUserFromComposing(long userId) {
        if (this.usersComposing.contains(Long.valueOf(userId))) {
            this.usersComposing.remove(Long.valueOf(userId));
            updateUI();
        }
    }

    private void updateUI() {
        if (!this.isUpdating) {
            this.isUpdating = true;
            if (this.isMultichat) {
                ArrayList<Long> userIdsToAdd = new ArrayList(this.usersComposing);
                ArrayList<View> shownUserViewsToRemove = new ArrayList();
                for (int i = 0; i < this.avatarsContainer.getChildCount(); i++) {
                    View view = this.avatarsContainer.getChildAt(i);
                    Long shownUserId = (Long) view.getTag();
                    if (!userIdsToAdd.contains(shownUserId)) {
                        shownUserViewsToRemove.add(view);
                    }
                    userIdsToAdd.remove(shownUserId);
                }
                if (userIdsToAdd.size() == 0 && shownUserViewsToRemove.size() == 0) {
                    this.isUpdating = false;
                } else {
                    addUserAvatarsViewsForMultichat(userIdsToAdd, new C10554(shownUserViewsToRemove));
                }
            } else if (this.usersComposing.size() > 0 && getTranslationY() == ((float) HIDDEN_TRANSLATION_Y)) {
                expand(new C10522());
            } else if (this.usersComposing.size() == 0 && getTranslationY() == 0.0f) {
                collapse(new C10533());
            } else {
                this.isUpdating = false;
            }
        }
    }

    private void stopIsUpdatingAndUpdateUI() {
        this.isUpdating = false;
        updateUI();
    }

    private void addUserAvatarsViewsForMultichat(ArrayList<Long> userIdsToAdd, Runnable endRunnable) {
        if (userIdsToAdd.size() == 0 || !this.isMultichat) {
            endRunnable.run();
            return;
        }
        Long userIdToAdd = (Long) userIdsToAdd.get(0);
        userIdsToAdd.remove(0);
        AvatarImageView avatarImageView = new AvatarImageView(getContext());
        avatarImageView.setPadding((int) Utils.dipToPixels(2.0f), 0, (int) Utils.dipToPixels(2.0f), 0);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((AVATAR_SIZE + avatarImageView.getPaddingLeft()) + avatarImageView.getPaddingRight(), AVATAR_SIZE);
        updateAvatar(userIdToAdd.longValue(), avatarImageView);
        avatarImageView.setTag(userIdToAdd);
        this.avatarsContainer.addView(avatarImageView, params);
        updateComposingText();
        if (getTranslationY() == ((float) HIDDEN_TRANSLATION_Y)) {
            expand(new C10565(userIdsToAdd, endRunnable));
            return;
        }
        avatarImageView.setTranslationY((float) HIDDEN_TRANSLATION_Y);
        avatarImageView.animate().translationYBy((float) (-HIDDEN_TRANSLATION_Y)).setDuration(400).setInterpolator(new OvershootInterpolator(2.2f)).setListener(new C10576(userIdsToAdd, endRunnable)).start();
    }

    private void updateComposingText() {
        this.composingText.setText(LocalizationManager.getString(getContext(), this.avatarsContainer.getChildCount() > 1 ? 2131165569 : 2131165570));
    }

    private void removeUserAvatarsViewsForMultichat(ArrayList<View> userViewsToRemove, Runnable endRunnable) {
        if (userViewsToRemove.size() == 0) {
            endRunnable.run();
            return;
        }
        View userViewToRemove = (View) userViewsToRemove.get(0);
        userViewsToRemove.remove(0);
        if (this.avatarsContainer.getChildCount() == 1) {
            collapse(new C10587(userViewToRemove, userViewsToRemove, endRunnable));
        } else {
            userViewToRemove.animate().setDuration(400).setInterpolator(new AnticipateOvershootInterpolator(2.2f)).setListener(new C10598(userViewToRemove, userViewsToRemove, endRunnable)).translationYBy((float) HIDDEN_TRANSLATION_Y).start();
        }
    }

    private void removeUserAvatarFromContainer(View userViewToRemove, ArrayList<View> userViewsToRemove, Runnable endRunnable) {
        this.avatarsContainer.removeView(userViewToRemove);
        updateComposingText();
        removeUserAvatarsViewsForMultichat(userViewsToRemove, endRunnable);
    }

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

    private void updateAvatar(long userId, AvatarImageView avatarImageView) {
        if (avatarImageView != null) {
            String url = null;
            boolean isMan = true;
            UserInfo userInfo = getUser(String.valueOf(userId));
            if (userInfo != null) {
                url = userInfo.picUrl;
                isMan = userInfo.genderType == UserGenderType.MALE;
            }
            if (URLUtil.isStubUrl(url)) {
                url = null;
            }
            ImageViewManager.getInstance().displayImage(url, avatarImageView, isMan, this.imageLoadBlocker);
            avatarImageView.setTag(new MessageAuthor(String.valueOf(userId), ""));
        }
    }

    private void expand(Runnable endRunnable) {
        invalidateComposingAnimation();
        setTranslationY((float) HIDDEN_TRANSLATION_Y);
        setVisibility(0);
        animate().setDuration(400).setInterpolator(new DecelerateInterpolator()).translationYBy((float) (-HIDDEN_TRANSLATION_Y)).setListener(new C10609(endRunnable)).start();
    }

    private void collapse(Runnable endRunnable) {
        animate().setDuration(400).setInterpolator(new AccelerateInterpolator()).translationYBy((float) HIDDEN_TRANSLATION_Y).setListener(new AnonymousClass10(endRunnable)).start();
    }

    public void notifyComposing(long decodedChatId, long decodedUserId) {
        if (this.decodedChat != null && this.decodedChat.chatId == decodedChatId && this.decodedChat.isMultichat == this.isMultichat) {
            ThreadUtil.executeOnMain(new AnonymousClass11(decodedUserId));
        }
    }

    public void notifyPaused(long decodedChatId, long decodedUserId) {
        if (this.decodedChat != null && this.decodedChat.chatId == decodedChatId && this.decodedChat.isMultichat == this.isMultichat) {
            ThreadUtil.executeOnMain(new AnonymousClass12(decodedUserId));
        }
    }

    public void setChatStateProvider(IChatStateProvider chatStateProvider) {
        this.chatStateProvider = chatStateProvider;
    }
}
