package ru.ok.android.ui.presents.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import ru.mail.libverify.C0176R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.ExternalUrlWebFragment;
import ru.ok.android.fragments.web.shortlinks.SendPresentShortLinkBuilder;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.custom.CompositePresentView;
import ru.ok.android.ui.custom.imageview.RoundAvatarImageView;
import ru.ok.android.ui.presents.BusWithEventStates;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.bus.BusSendPresentHelper;
import ru.ok.java.api.response.presents.SendInfoResponse;
import ru.ok.java.api.response.presents.UserBalancesResponse;
import ru.ok.model.UserInfo;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.presents.PresentInfo;
import ru.ok.model.presents.PresentType;
import ru.ok.onelog.app.clicks.SendPresentClickEventFactory;
import ru.ok.onelog.app.clicks.SendPresentClickType;

public class SendPresentActivity extends BasePresentActivity {
    private final BusWithEventStates busWithEventStates;
    private View cancelBtn;
    private TextView costTxt;
    private boolean isBadge;
    private String keySend;
    private SendPresentShortLinkBuilder linkBuilder;
    private EditText messageTxt;
    private CheckBox mysteryCheck;
    private final OnClickListener onCancelClicked;
    private final OnCheckedChangeListener onCheckedChangeListener;
    private final OnClickListener onMysteryCheckClicked;
    private final OnClickListener onOutsideClicked;
    private final OnClickListener onPrivateCheckClicked;
    private final OnClickListener onSendClicked;
    private final OnClickListener onSenderImgClicked;
    private CompositePresentView presentImg;
    private CheckBox privateCheck;
    private View sendBtn;
    private SendInfoResponse sendInfoResponse;
    private TextView titleTxt;
    private String userId;
    private RoundAvatarImageView userImg;

    /* renamed from: ru.ok.android.ui.presents.activity.SendPresentActivity.1 */
    class C11551 implements OnClickListener {
        C11551() {
        }

        public void onClick(@NonNull View v) {
            SendPresentActivity.this.log(SendPresentClickType.hide_screen);
            SendPresentActivity.this.finish();
        }
    }

    /* renamed from: ru.ok.android.ui.presents.activity.SendPresentActivity.2 */
    class C11562 implements OnClickListener {
        C11562() {
        }

        public void onClick(@NonNull View v) {
            if (SendPresentActivity.this.userId != null) {
                SendPresentActivity.this.log(SendPresentClickType.receiver_clicked);
                SendPresentActivity.this.finish();
                NavigationHelper.showUserInfo(SendPresentActivity.this, SendPresentActivity.this.userId);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.presents.activity.SendPresentActivity.3 */
    class C11573 implements OnClickListener {
        C11573() {
        }

        public void onClick(@NonNull View v) {
            SendPresentActivity.this.log(SendPresentClickType.send_clicked);
            SendPresentActivity.this.sendPresent();
        }
    }

    /* renamed from: ru.ok.android.ui.presents.activity.SendPresentActivity.4 */
    class C11584 implements OnClickListener {
        C11584() {
        }

        public void onClick(@NonNull View v) {
            SendPresentActivity.this.log(SendPresentClickType.cancel_clicked);
            SendPresentActivity.this.hideActivity(2130968617);
        }
    }

    /* renamed from: ru.ok.android.ui.presents.activity.SendPresentActivity.5 */
    class C11595 implements OnClickListener {
        C11595() {
        }

        public void onClick(@NonNull View v) {
            if (SendPresentActivity.this.privateCheck.isChecked()) {
                SendPresentActivity.this.log(SendPresentClickType.private_checked);
            } else {
                SendPresentActivity.this.log(SendPresentClickType.private_unchecked);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.presents.activity.SendPresentActivity.6 */
    class C11606 implements OnClickListener {
        C11606() {
        }

        public void onClick(@NonNull View v) {
            if (SendPresentActivity.this.mysteryCheck.isChecked()) {
                SendPresentActivity.this.log(SendPresentClickType.mystery_checked);
            } else {
                SendPresentActivity.this.log(SendPresentClickType.mystery_unchecked);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.presents.activity.SendPresentActivity.7 */
    class C11617 implements OnCheckedChangeListener {
        C11617() {
        }

        public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
            if (buttonView == SendPresentActivity.this.privateCheck) {
                if (isChecked && SendPresentActivity.this.mysteryCheck != null) {
                    SendPresentActivity.this.mysteryCheck.setChecked(false);
                }
            } else if (isChecked) {
                SendPresentActivity.this.privateCheck.setChecked(false);
            }
        }
    }

    public SendPresentActivity() {
        this.busWithEventStates = BusWithEventStates.getInstance();
        this.onOutsideClicked = new C11551();
        this.onSenderImgClicked = new C11562();
        this.onSendClicked = new C11573();
        this.onCancelClicked = new C11584();
        this.onPrivateCheckClicked = new C11595();
        this.onMysteryCheckClicked = new C11606();
        this.onCheckedChangeListener = new C11617();
    }

    public void onCreateLocalized(@Nullable Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        this.sendInfoResponse = (SendInfoResponse) getIntent().getParcelableExtra("sendInfo");
        this.linkBuilder = (SendPresentShortLinkBuilder) getIntent().getParcelableExtra("link_builder");
        this.userId = this.linkBuilder.getUserId();
        if (TextUtils.equals(this.userId, OdnoklassnikiApplication.getCurrentUser().uid)) {
            this.isBadge = true;
        }
        restoreState(savedInstanceState);
    }

    public void onBackPressed() {
        super.onBackPressed();
        log(SendPresentClickType.hide_screen);
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.keySend != null) {
            outState.putString("key_send", this.keySend);
        }
    }

    protected void showButtonsProgress() {
        findViewById(C0158R.id.buttons).setVisibility(8);
        findViewById(2131624520).setVisibility(0);
    }

    @Subscribe(on = 2131623946, to = 2131624242)
    public void onPresentSent(@NonNull BusEvent event) {
        if (!this.busWithEventStates.isResultForKey(event, this.keySend)) {
            return;
        }
        if (event.resultCode == -1) {
            String resultCode = event.bundleOutput.getString("EXTRA_RESULT");
            hideActivity(2130968618);
            openPresentSentActivity(resultCode);
            return;
        }
        changeStateToNormal();
        showToast(ErrorType.from(event.bundleOutput).getDefaultErrorMessage());
    }

    private void initUI(int resId) {
        setContentView(resId);
        this.userImg = (RoundAvatarImageView) findViewById(2131624521);
        this.presentImg = (CompositePresentView) findViewById(2131624513);
        this.messageTxt = (EditText) findViewById(2131624538);
        this.privateCheck = (CheckBox) findViewById(2131624514);
        this.mysteryCheck = (CheckBox) findViewById(2131624524);
        this.sendBtn = findViewById(2131624519);
        this.cancelBtn = findViewById(C0263R.id.cancel);
        this.costTxt = (TextView) findViewById(2131624516);
        this.titleTxt = (TextView) findViewById(C0176R.id.title);
        this.userImg.setOnClickListener(this.onSenderImgClicked);
        this.sendBtn.setOnClickListener(this.onSendClicked);
        this.cancelBtn.setOnClickListener(this.onCancelClicked);
        findViewById(2131624509).setOnClickListener(this.onOutsideClicked);
        findViewById(2131624510).setOnClickListener(this.onOutsideClicked);
        this.privateCheck.setOnCheckedChangeListener(this.onCheckedChangeListener);
        this.privateCheck.setOnClickListener(this.onPrivateCheckClicked);
        ViewUtil.expandChildClickAreaToParentClickArea(this.privateCheck);
        ((GenericDraweeHierarchy) this.userImg.getHierarchy()).setFadeDuration(0);
        if (this.mysteryCheck != null) {
            this.mysteryCheck.setOnCheckedChangeListener(this.onCheckedChangeListener);
            this.mysteryCheck.setOnClickListener(this.onMysteryCheckClicked);
            ViewUtil.expandChildClickAreaToParentClickArea(this.mysteryCheck);
        }
        this.userImg.setShadowStroke((float) getResources().getDimensionPixelOffset(2131231161));
        this.userImg.setEnabled(false);
    }

    private void setSendInfoResponse(@NonNull SendInfoResponse sendInfoResponse) {
        initUI(getLayoutId(sendInfoResponse.presentInfo.presentType));
        setPresentInfo(sendInfoResponse.presentInfo.presentType);
        setOks(sendInfoResponse.presentInfo, sendInfoResponse.balancesResponse);
        setUserInfo(sendInfoResponse.userInfo);
        this.titleTxt.setText(getReceiverTitle(sendInfoResponse.localizedName, sendInfoResponse.presentInfo.presentType));
    }

    private void setBadgeSize(@NonNull PresentType presentType) {
        PhotoSize photoSize = presentType.isAnimated ? presentType.sprites.isEmpty() ? null : (PhotoSize) presentType.sprites.first() : presentType.photoSize;
        if (photoSize != null && Math.min(photoSize.getWidth(), photoSize.getHeight()) <= 70) {
            LayoutParams lp = this.presentImg.getLayoutParams();
            lp.width = getResources().getDimensionPixelOffset(2131231156);
            lp.height = getResources().getDimensionPixelOffset(2131231156);
            this.presentImg.setLayoutParams(lp);
        }
    }

    private void restoreState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.keySend = savedInstanceState.getString("key_send");
        }
        setSendInfoResponse(this.sendInfoResponse);
        if (this.busWithEventStates.isProcessing(this.keySend)) {
            changeStateToSending();
        }
    }

    private void sendPresent() {
        changeStateToSending();
        this.keySend = BusSendPresentHelper.sendPresent(this.linkBuilder.getPresentId(), this.linkBuilder.getUserId(), this.linkBuilder.getToken(), getMessage(), this.linkBuilder.getHolidayId(), getPresentType());
    }

    @Nullable
    private String getMessage() {
        if (this.messageTxt != null) {
            return this.messageTxt.getText().toString();
        }
        return null;
    }

    private int getLayoutId(@NonNull PresentType presentType) {
        if (!presentType.isLive && !this.isBadge) {
            return 2130903089;
        }
        if (presentType.isLive && !this.isBadge) {
            return 2130903078;
        }
        if (presentType.isLive || !this.isBadge) {
            return 2130903077;
        }
        return 2130903088;
    }

    @NonNull
    private String getPresentType() {
        if (this.privateCheck.isChecked()) {
            return "PRIVATE";
        }
        if (this.mysteryCheck == null || !this.mysteryCheck.isChecked()) {
            return "PUBLIC";
        }
        return "ANONYMOUS";
    }

    @NonNull
    private String getReceiverTitle(@NonNull String localizedName, @NonNull PresentType presentType) {
        if (!presentType.isLive && this.isBadge) {
            return getStringLocalized(2131166519);
        }
        if (presentType.isLive && this.isBadge) {
            return getStringLocalized(2131166520);
        }
        return localizedName;
    }

    private void setPresentInfo(@NonNull PresentType presentType) {
        if (presentType.isLive) {
            setCircledBackground();
        } else if (this.isBadge) {
            setBadgeSize(presentType);
        }
        this.presentImg.setPresentType(presentType, getPresentSize(presentType.isLive, this.isBadge));
    }

    private void setUserInfo(@NonNull UserInfo userInfo) {
        this.userImg.setImageResource(getUserAvatarStub(userInfo));
        this.userImg.setUrl(userInfo.getPic288());
        this.userImg.setEnabled(true);
    }

    private void setOks(PresentInfo presentInfo, UserBalancesResponse balancesResponse) {
        String text;
        if (presentInfo.priceInFreePresents <= 0 || balancesResponse.freeGiftsCount - presentInfo.priceInFreePresents < 0) {
            text = getStringLocalized(2131166521, Integer.valueOf(presentInfo.price), Integer.valueOf(balancesResponse.userBalanceInOks));
        } else if (presentInfo.priceInFreePresents > 1) {
            text = getStringLocalized(2131166522, Integer.valueOf(presentInfo.priceInFreePresents), Integer.valueOf(balancesResponse.freeGiftsCount));
        } else {
            text = getStringLocalized(2131166523, Integer.valueOf(balancesResponse.freeGiftsCount));
        }
        this.costTxt.setText(text);
    }

    private void hideActivity(@AnimRes int animId) {
        finish();
        overridePendingTransition(0, animId);
    }

    private void openPresentSentActivity(@NonNull String resultCode) {
        this.linkBuilder.setResult(resultCode);
        ActivityExecutor builder = new ActivityExecutor(this, ExternalUrlWebFragment.class);
        builder.setArguments(ExternalUrlWebFragment.newArguments(this.linkBuilder.build()));
        builder.setNeedToolbar(true);
        builder.setDefaultAnimationEnabled(false);
        builder.setActivityFromMenu(false);
        builder.execute();
    }

    private void changeStateToSending() {
        this.privateCheck.setEnabled(false);
        if (this.mysteryCheck != null) {
            this.mysteryCheck.setEnabled(false);
        }
        if (this.messageTxt != null) {
            this.messageTxt.setEnabled(false);
        }
        showButtonsProgress();
    }

    private void changeStateToNormal() {
        this.privateCheck.setEnabled(true);
        if (this.mysteryCheck != null) {
            this.mysteryCheck.setEnabled(true);
        }
        if (this.messageTxt != null) {
            this.messageTxt.setEnabled(true);
        }
        showButtons();
    }

    private void log(@NonNull SendPresentClickType type) {
        OneLog.log(SendPresentClickEventFactory.get(type));
    }

    @NonNull
    public static Point getPresentSize(@NonNull boolean isLive, boolean isBadge) {
        Resources resources = OdnoklassnikiApplication.getContext().getResources();
        if (isLive) {
            return new Point(resources.getDimensionPixelOffset(2131231158), resources.getDimensionPixelOffset(2131231157));
        }
        if (isBadge) {
            return new Point(resources.getDimensionPixelOffset(2131231155), resources.getDimensionPixelOffset(2131231155));
        }
        return new Point(resources.getDimensionPixelOffset(2131231162), resources.getDimensionPixelOffset(2131231162));
    }

    @NonNull
    static Intent createIntent(@NonNull Context context, @NonNull SendInfoResponse sendInfo, @NonNull SendPresentShortLinkBuilder linkBuilder) {
        Intent intent = new Intent(context, SendPresentActivity.class);
        intent.putExtra("sendInfo", sendInfo);
        intent.putExtra("link_builder", linkBuilder);
        return intent;
    }
}
