package ru.ok.android.ui.presents.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.web.shortlinks.SendPresentShortLinkBuilder;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.custom.CompositePresentView;
import ru.ok.android.ui.custom.imageview.RoundAvatarImageView;
import ru.ok.android.ui.presents.BusWithEventStates;
import ru.ok.android.ui.presents.controller.AcceptAnimationController;
import ru.ok.android.ui.presents.controller.AcceptCardAnimationController;
import ru.ok.android.ui.presents.controller.OnAcceptAnimationComplete;
import ru.ok.android.ui.presents.controller.ReplyAnimationController;
import ru.ok.android.ui.presents.controller.ToastAnimationController;
import ru.ok.android.ui.stream.music.PlayerStateHolder;
import ru.ok.android.ui.stream.view.ProfilePresentTrackView;
import ru.ok.android.ui.stream.view.ProfilePresentTrackView.OnPlayTrackListener;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.bus.BusMusicHelper;
import ru.ok.android.utils.bus.BusReceivePresentHelper;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.java.api.response.presents.PresentNotificationResponse;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.presents.PresentInfo;
import ru.ok.model.presents.PresentType;
import ru.ok.model.wmf.Track;
import ru.ok.onelog.app.clicks.ReceivePresentClickEventFactory;
import ru.ok.onelog.app.clicks.ReceivePresentClickType;
import ru.ok.onelog.app.clicks.ReceivePresentScreenState;

public class PresentReceivedActivity extends BasePresentActivity implements OnAcceptAnimationComplete, OnPlayTrackListener {
    private AcceptAnimationController acceptAnimationController;
    private View acceptBtn;
    private AcceptCardAnimationController acceptCardAnimationController;
    private View background;
    private final BusWithEventStates busWithEventStates;
    private View buttons;
    private View buttonsLayout;
    private View cardBackground;
    private final Runnable changeStateToReplyRunnable;
    private View firstStateLayout;
    private final ImageViewManager imageViewManager;
    private CompositePresentView img1;
    private CompositePresentView img2;
    private CompositePresentView img3;
    private CompositePresentView img4;
    private String keyAcceptPresent;
    private String keyRejectPresent;
    private TextView messageTxt;
    private PresentNotificationResponse notificationResponse;
    private final OnClickListener onAcceptClicked;
    private final OnClickListener onImageClicked;
    private final OnTouchListener onMusicTouchListener;
    private final OnClickListener onRejectClicked;
    private final OnClickListener onRootLayoutClicked;
    private final OnClickListener onSenderImgClicked;
    private final OnClickListener onShowAllClicked;
    private PlayerStateHolder playerStateHolder;
    private CompositePresentView presentImg;
    private String presentNotificationId;
    private View rejectBtn;
    private ReplyAnimationController replyAnimationController;
    private ArrayList<PresentInfo> replyPresents;
    private View rootLayout;
    private RoundAvatarImageView senderAvatarImg;
    private TextView senderNameTxt;
    private View showAllBtn;
    private State state;
    private ProfilePresentTrackView trackView;
    private RoundAvatarImageView userAvatarImg;
    private CompositePresentView userPresentImg;

    /* renamed from: ru.ok.android.ui.presents.activity.PresentReceivedActivity.1 */
    class C11471 implements OnClickListener {
        C11471() {
        }

        public void onClick(@NonNull View v) {
            PresentReceivedActivity.this.log(ReceivePresentClickType.accept_clicked);
            PresentReceivedActivity.this.changeStateToAccepting();
            PresentReceivedActivity.this.keyAcceptPresent = BusReceivePresentHelper.acceptPresent(PresentReceivedActivity.this.presentNotificationId);
        }
    }

    /* renamed from: ru.ok.android.ui.presents.activity.PresentReceivedActivity.2 */
    class C11482 implements OnClickListener {
        C11482() {
        }

        public void onClick(@NonNull View v) {
            PresentReceivedActivity.this.log(ReceivePresentClickType.reject_clicked);
            PresentReceivedActivity.this.changeStateToAccepting();
            PresentReceivedActivity.this.keyRejectPresent = BusReceivePresentHelper.declinePresent(PresentReceivedActivity.this.presentNotificationId);
        }
    }

    /* renamed from: ru.ok.android.ui.presents.activity.PresentReceivedActivity.3 */
    class C11493 implements OnClickListener {
        C11493() {
        }

        public void onClick(@NonNull View v) {
            PresentReceivedActivity.this.log(ReceivePresentClickType.show_more_presents_clicked);
            PresentReceivedActivity.this.finish();
            PresentReceivedActivity.this.openMorePresentsActivity(PresentReceivedActivity.this.notificationResponse.presentInfo.sender.getId());
        }
    }

    /* renamed from: ru.ok.android.ui.presents.activity.PresentReceivedActivity.4 */
    class C11504 implements OnClickListener {
        C11504() {
        }

        public void onClick(@NonNull View v) {
            PresentType presentType = (PresentType) v.getTag();
            if (presentType != null) {
                PresentReceivedActivity.this.log(PresentReceivedActivity.this.getLogType(v));
                PresentReceivedActivity.this.finish();
                NavigationHelper.makePresent(PresentReceivedActivity.this, SendPresentShortLinkBuilder.sendPresent(PresentReceivedActivity.this.notificationResponse.presentInfo.sender.getId(), presentType.id).setOrigin("Y"));
            }
        }
    }

    /* renamed from: ru.ok.android.ui.presents.activity.PresentReceivedActivity.5 */
    class C11515 implements OnClickListener {
        C11515() {
        }

        public void onClick(@NonNull View v) {
            if (PresentReceivedActivity.this.notificationResponse.presentInfo.sender != null && PresentReceivedActivity.this.notificationResponse.presentInfo.sender.getId() != null) {
                PresentReceivedActivity.this.log(ReceivePresentClickType.sender_avatar_clicked);
                PresentReceivedActivity.this.finish();
                NavigationHelper.showUserInfo(PresentReceivedActivity.this, PresentReceivedActivity.this.notificationResponse.presentInfo.sender.getId());
            }
        }
    }

    /* renamed from: ru.ok.android.ui.presents.activity.PresentReceivedActivity.6 */
    class C11526 implements OnClickListener {
        C11526() {
        }

        public void onClick(@NonNull View v) {
            PresentReceivedActivity.this.log(ReceivePresentClickType.hide_screen);
            PresentReceivedActivity.this.finish();
        }
    }

    /* renamed from: ru.ok.android.ui.presents.activity.PresentReceivedActivity.7 */
    class C11537 implements Runnable {
        C11537() {
        }

        public void run() {
            PresentReceivedActivity.this.changeStateToReply(false);
        }
    }

    /* renamed from: ru.ok.android.ui.presents.activity.PresentReceivedActivity.8 */
    class C11548 implements OnTouchListener {
        C11548() {
        }

        public boolean onTouch(@NonNull View v, @NonNull MotionEvent event) {
            if (event.getAction() == 1) {
                PresentReceivedActivity.this.log(ReceivePresentClickType.music_clicked);
            }
            return false;
        }
    }

    private enum State {
        ACCEPTING,
        ACCEPTED,
        REPLY
    }

    public PresentReceivedActivity() {
        this.busWithEventStates = BusWithEventStates.getInstance();
        this.imageViewManager = ImageViewManager.getInstance();
        this.onAcceptClicked = new C11471();
        this.onRejectClicked = new C11482();
        this.onShowAllClicked = new C11493();
        this.onImageClicked = new C11504();
        this.onSenderImgClicked = new C11515();
        this.onRootLayoutClicked = new C11526();
        this.changeStateToReplyRunnable = new C11537();
        this.onMusicTouchListener = new C11548();
    }

    protected void onCreateLocalized(@Nullable Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setContentView(2130903086);
        this.playerStateHolder = new PlayerStateHolder();
        this.playerStateHolder.init();
        this.acceptAnimationController = new AcceptAnimationController(this, this);
        this.acceptCardAnimationController = new AcceptCardAnimationController(this, this);
        this.replyAnimationController = new ReplyAnimationController(this);
        this.senderAvatarImg = (RoundAvatarImageView) findViewById(2131624574);
        this.userAvatarImg = (RoundAvatarImageView) findViewById(2131624559);
        this.senderNameTxt = (TextView) findViewById(2131624553);
        this.messageTxt = (TextView) findViewById(2131624538);
        this.presentImg = (CompositePresentView) findViewById(2131624556);
        this.userPresentImg = (CompositePresentView) findViewById(2131624561);
        this.acceptBtn = findViewById(2131624570);
        this.rejectBtn = findViewById(2131624569);
        this.background = findViewById(2131624511);
        this.firstStateLayout = findViewById(2131624554);
        this.buttonsLayout = findViewById(2131624568);
        this.showAllBtn = findViewById(2131624571);
        this.trackView = (ProfilePresentTrackView) findViewById(2131624557);
        this.cardBackground = findViewById(2131624555);
        this.img1 = (CompositePresentView) findViewById(2131624564);
        this.img2 = (CompositePresentView) findViewById(2131624565);
        this.img3 = (CompositePresentView) findViewById(2131624566);
        this.img4 = (CompositePresentView) findViewById(2131624567);
        this.rootLayout = findViewById(2131624552);
        this.buttons = findViewById(C0158R.id.buttons);
        this.showAllBtn.setOnClickListener(this.onShowAllClicked);
        this.acceptBtn.setOnClickListener(this.onAcceptClicked);
        this.rejectBtn.setOnClickListener(this.onRejectClicked);
        this.img1.setOnClickListener(this.onImageClicked);
        this.img2.setOnClickListener(this.onImageClicked);
        this.img3.setOnClickListener(this.onImageClicked);
        if (this.img4 != null) {
            this.img4.setOnClickListener(this.onImageClicked);
        }
        this.senderAvatarImg.setOnClickListener(this.onSenderImgClicked);
        this.rootLayout.setOnClickListener(this.onRootLayoutClicked);
        this.trackView.setOnTouchListener(this.onMusicTouchListener);
        this.messageTxt.setMovementMethod(new ScrollingMovementMethod());
        this.senderAvatarImg.setShadowStroke((float) getResources().getDimensionPixelOffset(2131231161));
        this.senderAvatarImg.setEnabled(false);
        this.presentNotificationId = getIntent().getStringExtra("present_notification_id");
        this.notificationResponse = (PresentNotificationResponse) getIntent().getParcelableExtra("present_notification");
        restoreState(savedInstanceState);
    }

    public void onBackPressed() {
        super.onBackPressed();
        log(ReceivePresentClickType.hide_screen);
    }

    protected void onDestroy() {
        super.onDestroy();
        this.playerStateHolder.close();
    }

    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        finish();
        startActivity(intent);
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.keyAcceptPresent != null) {
            outState.putString("key_accept_present", this.keyAcceptPresent);
        }
        if (this.keyRejectPresent != null) {
            outState.putString("key_reject_present", this.keyRejectPresent);
        }
        if (this.replyPresents != null) {
            outState.putParcelableArrayList("acceptResult", this.replyPresents);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624126)
    public void onPresentAccepted(@NonNull BusEvent event) {
        if (this.busWithEventStates.isResultForKey(event, this.keyAcceptPresent)) {
            hideButtonsProgress();
            if (event.resultCode == -1) {
                this.replyPresents = event.bundleOutput.getParcelableArrayList("PRESENT_INFOS");
                setThanksPresents(this.replyPresents);
                changeStateToAccept(false);
                return;
            }
            changeStateToNormal();
            showToast(getErrorMessage(event));
        }
    }

    @Subscribe(on = 2131623946, to = 2131624141)
    public void onPresentDeclined(@NonNull BusEvent event) {
        if (this.busWithEventStates.isResultForKey(event, this.keyRejectPresent)) {
            hideButtonsProgress();
            if (event.resultCode == -1) {
                finish();
                overridePendingTransition(0, 2130968617);
                return;
            }
            changeStateToNormal();
            showToast(getErrorMessage(event));
        }
    }

    public void onPlayTrack(long trackId) {
        BusMusicHelper.getCustomTrack(trackId);
    }

    @Subscribe(on = 2131623946, to = 2131624199)
    public void onGetCustomTrack(@NonNull BusEvent event) {
        if (event != null) {
            Track[] tracks = (Track[]) event.bundleOutput.getParcelableArray("key_places_complaint_result");
            if (tracks != null && tracks.length > 0) {
                MusicService.startPlayMusic(OdnoklassnikiApplication.getContext(), 0, new ArrayList(Arrays.asList(tracks)), MusicListType.NO_DIRECTION);
            }
        }
    }

    public void onAnimationAcceptComplete() {
        if (this.notificationResponse.presentInfo.sender != null) {
            this.background.postDelayed(this.changeStateToReplyRunnable, 1500);
        }
    }

    protected void showToast(int textResId) {
        new ToastAnimationController(this).showToast(getStringLocalized(textResId));
    }

    private void restoreState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.keyAcceptPresent = savedInstanceState.getString("key_accept_present");
            this.keyRejectPresent = savedInstanceState.getString("key_reject_present");
            this.replyPresents = savedInstanceState.getParcelableArrayList("acceptResult");
        }
        setResponse(this.notificationResponse);
        changeStateToNormal();
        if (this.replyPresents != null) {
            setThanksPresents(this.replyPresents);
            changeStateToReply(true);
        } else if (this.busWithEventStates.isProcessing(this.keyAcceptPresent) || this.busWithEventStates.isProcessing(this.keyRejectPresent)) {
            changeStateToAccepting();
        }
    }

    private void setResponse(@NonNull PresentNotificationResponse response) {
        setUserAvatar(response.presentInfo.sender, this.senderAvatarImg);
        setUserAvatar(OdnoklassnikiApplication.getCurrentUser(), this.userAvatarImg);
        setPresentSize(getPresentSize(response.presentInfo.presentType));
        setPresentLabel(response);
        setPresentImage(response.presentInfo.presentType);
        setPresentTrack(response.presentInfo.trackId);
        setBackground(response.presentInfo.presentType.isLive);
        setSenderText(response);
    }

    private void setPresentSize(@NonNull Point point) {
        this.presentImg.setLayoutParams(new LayoutParams(point.x, point.y));
    }

    private void setPresentLabel(@NonNull PresentNotificationResponse response) {
        this.senderNameTxt.setText(response.receiveText);
    }

    private void setPresentImage(@NonNull PresentType presentType) {
        this.presentImg.setPresentPlaceholder(2130838614);
        this.presentImg.setPresentType(presentType, getPresentSize(presentType));
        this.userPresentImg.setPresentPlaceholder(2130838614);
        this.userPresentImg.setPresentType(presentType, getPresentSize(presentType));
    }

    private void setPresentTrack(@Nullable String trackId) {
        if (TextUtils.isEmpty(trackId)) {
            this.trackView.setVisibility(4);
            return;
        }
        this.trackView.setPlayState();
        this.trackView.setPlayerStateHolder(this.playerStateHolder);
        this.trackView.setTrackId(Long.parseLong(trackId));
        this.trackView.setOnPlayTrackListener(this);
        this.trackView.setVisibility(0);
    }

    private void setBackground(boolean circled) {
        if (circled) {
            setCircledBackground();
            this.cardBackground.setBackgroundResource(2130837763);
            return;
        }
        this.background.setBackgroundResource(2130838586);
        this.cardBackground.setBackgroundDrawable(null);
    }

    private void setUserAvatar(@Nullable UserInfo userInfo, @NonNull RoundAvatarImageView imageView) {
        boolean z = false;
        if (userInfo == null) {
            imageView.setImageResource(2130837658);
            imageView.setEnabled(false);
            return;
        }
        String picUrl;
        if (userInfo == OdnoklassnikiApplication.getCurrentUser()) {
            picUrl = userInfo.getPic600();
        } else {
            picUrl = userInfo.getPicUrl();
        }
        if (picUrl != null) {
            ImageViewManager imageViewManager = this.imageViewManager;
            String picUrl2 = userInfo.getPicUrl();
            if (userInfo.genderType == UserGenderType.MALE) {
                z = true;
            }
            imageViewManager.displayImage(picUrl2, imageView, z, null);
        } else {
            imageView.setImageResource(getUserAvatarStub(userInfo));
        }
        imageView.setEnabled(true);
    }

    private void setThanksPresents(@NonNull List<PresentInfo> presentInfos) {
        if (presentInfos.size() >= 1) {
            PresentType presentType = ((PresentInfo) presentInfos.get(0)).presentType;
            this.img1.setPresentPlaceholder(2130838614);
            this.img1.setPresentType(presentType, getThanksPresentSize());
            this.img1.setTag(presentType);
        }
        if (presentInfos.size() >= 2) {
            presentType = ((PresentInfo) presentInfos.get(1)).presentType;
            this.img2.setPresentPlaceholder(2130838614);
            this.img2.setPresentType(presentType, getThanksPresentSize());
            this.img2.setTag(presentType);
        }
        if (presentInfos.size() >= 3) {
            presentType = ((PresentInfo) presentInfos.get(2)).presentType;
            this.img3.setPresentPlaceholder(2130838614);
            this.img3.setPresentType(presentType, getThanksPresentSize());
            this.img3.setTag(presentType);
        }
        if (presentInfos.size() >= 4 && this.img4 != null) {
            presentType = ((PresentInfo) presentInfos.get(3)).presentType;
            this.img4.setPresentPlaceholder(2130838614);
            this.img4.setPresentType(presentType, getThanksPresentSize());
            this.img4.setTag(presentType);
        }
    }

    private void setSenderText(@NonNull PresentNotificationResponse response) {
        if (TextUtils.isEmpty(response.presentInfo.message) || response.presentInfo.presentType.isLive) {
            this.messageTxt.setVisibility(8);
            return;
        }
        this.messageTxt.setText(response.presentInfo.message);
        this.messageTxt.setVisibility(0);
    }

    private void changeStateToAccept(boolean immediately) {
        this.state = State.ACCEPTED;
        if (this.notificationResponse.presentInfo.presentType.isLive) {
            this.acceptCardAnimationController.startAnimation(immediately);
        } else {
            this.acceptAnimationController.startAnimation(immediately, !TextUtils.isEmpty(this.notificationResponse.presentInfo.trackId));
        }
    }

    private void changeStateToReply(boolean immediately) {
        this.state = State.REPLY;
        this.replyAnimationController.startAnimation(this.notificationResponse, immediately);
    }

    private void changeStateToAccepting() {
        changeStateToNormal();
        disableButtons();
        showButtonsProgress();
        this.state = State.ACCEPTING;
    }

    private void changeStateToNormal() {
        this.state = null;
        enableButtons();
        this.firstStateLayout.setVisibility(0);
        this.buttonsLayout.setVisibility(0);
        this.buttons.setVisibility(0);
    }

    private void enableButtons() {
        this.acceptBtn.setEnabled(true);
        this.rejectBtn.setEnabled(true);
    }

    private void disableButtons() {
        this.acceptBtn.setEnabled(false);
        this.rejectBtn.setEnabled(false);
    }

    private void openMorePresentsActivity(@NonNull String userId) {
        NavigationHelper.showExternalUrlPage((Activity) this, SendPresentShortLinkBuilder.choosePresentWithSelectedUser(userId, "thankYou").setOrigin("Y").build(), false);
    }

    private int getErrorMessage(@NonNull BusEvent event) {
        ErrorType errorType = ErrorType.from(event.bundleOutput);
        if (errorType == ErrorType.GENERAL) {
            return 2131166434;
        }
        return errorType.getDefaultErrorMessage();
    }

    @NonNull
    private ReceivePresentScreenState getLogState() {
        if (this.state == null) {
            return ReceivePresentScreenState.state_normal;
        }
        return ReceivePresentScreenState.state_accepted;
    }

    @NonNull
    private ReceivePresentClickType getLogType(@NonNull View view) {
        if (view == this.img1) {
            return ReceivePresentClickType.thanks_1_clicked;
        }
        if (view == this.img2) {
            return ReceivePresentClickType.thanks_2_clicked;
        }
        if (view == this.img3) {
            return ReceivePresentClickType.thanks_3_clicked;
        }
        return ReceivePresentClickType.thanks_4_clicked;
    }

    private void log(@NonNull ReceivePresentClickType type) {
        OneLog.log(ReceivePresentClickEventFactory.get(type, getLogState()));
    }

    @NonNull
    public static Point getPresentSize(@NonNull PresentType presentType) {
        Resources resources = OdnoklassnikiApplication.getContext().getResources();
        if (presentType.isLive) {
            return new Point(resources.getDimensionPixelOffset(2131231145), resources.getDimensionPixelOffset(2131231144));
        }
        return new Point(resources.getDimensionPixelOffset(2131230735), resources.getDimensionPixelOffset(2131230735));
    }

    @NonNull
    public static Point getThanksPresentSize() {
        Resources resources = OdnoklassnikiApplication.getContext().getResources();
        return new Point(resources.getDimensionPixelOffset(2131231148), resources.getDimensionPixelOffset(2131231148));
    }

    @NonNull
    static Intent createIntent(@NonNull Context context, @NonNull String presentNotificationId, @NonNull PresentNotificationResponse notification) {
        Intent intent = new Intent(context, PresentReceivedActivity.class);
        intent.putExtra("present_notification_id", presentNotificationId);
        intent.putExtra("present_notification", notification);
        return intent;
    }
}
