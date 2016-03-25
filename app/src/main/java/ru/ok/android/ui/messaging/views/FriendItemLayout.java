package ru.ok.android.ui.messaging.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.TouchFloatingDelegate;
import ru.ok.android.ui.custom.imageview.MultiUserAvatar;
import ru.ok.android.ui.fragments.messages.view.ParticipantsPreviewView;
import ru.ok.android.utils.localization.LocalizationManager;

public final class FriendItemLayout extends ViewGroup {
    private MultiUserAvatar avatar;
    private View background;
    private View contextMenuBtn;
    private View date;
    private TextView lastMessageMeText;
    private ParticipantsPreviewView lastMessageUserAvatars;
    private View name;
    private View notifications;
    private ImageView online;
    private View text;

    public FriendItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.avatar = (MultiUserAvatar) findViewById(2131624969);
        this.online = (ImageView) findViewById(2131624634);
        this.notifications = findViewById(2131624798);
        this.date = findViewById(2131624541);
        this.name = findViewById(C0263R.id.name);
        this.lastMessageUserAvatars = (ParticipantsPreviewView) findViewById(2131624970);
        this.lastMessageUserAvatars.setIsBorderEnabled(false);
        this.lastMessageMeText = (TextView) findViewById(2131624971);
        this.lastMessageMeText.setText(LocalizationManager.getString(getContext(), 2131166193));
        this.text = findViewById(C0263R.id.text);
        this.contextMenuBtn = findViewById(2131624874);
        this.background = findViewById(2131624511);
        setTouchDelegate(new TouchFloatingDelegate(this.contextMenuBtn, getResources().getDimensionPixelSize(2131230952)));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int unspecifiedSpec = MeasureSpec.makeMeasureSpec(0, 0);
        LayoutParams avatarImageLp = this.avatar.getLayoutParams();
        this.avatar.measure(MeasureSpec.makeMeasureSpec(avatarImageLp.width, 1073741824), MeasureSpec.makeMeasureSpec(avatarImageLp.height, 1073741824));
        LayoutParams vcallLp = this.online.getLayoutParams();
        this.online.measure(MeasureSpec.makeMeasureSpec(vcallLp.width, 0), MeasureSpec.makeMeasureSpec(vcallLp.height, 0));
        int height = (this.avatar.getMeasuredHeight() + getPaddingTop()) + getPaddingBottom();
        int heightWrapSpec = MeasureSpec.makeMeasureSpec(height, LinearLayoutManager.INVALID_OFFSET);
        if (this.notifications.getVisibility() == 0) {
            this.notifications.measure(unspecifiedSpec, unspecifiedSpec);
        }
        this.date.measure(MeasureSpec.makeMeasureSpec(width, LinearLayoutManager.INVALID_OFFSET), heightWrapSpec);
        this.contextMenuBtn.measure(unspecifiedSpec, unspecifiedSpec);
        MarginLayoutParams contextMenuBtnLp = (MarginLayoutParams) this.contextMenuBtn.getLayoutParams();
        int textMaxWidth = ((((((width - getPaddingLeft()) - getPaddingRight()) - this.avatar.getMeasuredWidth()) - ((MarginLayoutParams) this.avatar.getLayoutParams()).rightMargin) - this.contextMenuBtn.getMeasuredWidth()) - contextMenuBtnLp.leftMargin) - contextMenuBtnLp.rightMargin;
        if (this.notifications.getVisibility() == 0) {
            MarginLayoutParams notificationsLp = (MarginLayoutParams) this.notifications.getLayoutParams();
            textMaxWidth -= (this.notifications.getMeasuredWidth() + notificationsLp.rightMargin) + notificationsLp.leftMargin;
        }
        this.name.measure(MeasureSpec.makeMeasureSpec(textMaxWidth, LinearLayoutManager.INVALID_OFFSET), heightMeasureSpec);
        MarginLayoutParams lastMessageUserAvatarsLp = (MarginLayoutParams) this.lastMessageUserAvatars.getLayoutParams();
        if (this.lastMessageMeText.getVisibility() == 0) {
            MarginLayoutParams lastMessageMeTextLp = (MarginLayoutParams) this.lastMessageMeText.getLayoutParams();
            this.lastMessageMeText.measure(MeasureSpec.makeMeasureSpec(textMaxWidth, LinearLayoutManager.INVALID_OFFSET), MeasureSpec.makeMeasureSpec(lastMessageMeTextLp.height, 1073741824));
            textMaxWidth -= (this.lastMessageMeText.getMeasuredWidth() - lastMessageMeTextLp.leftMargin) - lastMessageMeTextLp.rightMargin;
        } else if (this.lastMessageUserAvatars.getVisibility() == 0) {
            this.lastMessageUserAvatars.measure(MeasureSpec.makeMeasureSpec(textMaxWidth, LinearLayoutManager.INVALID_OFFSET), MeasureSpec.makeMeasureSpec(lastMessageUserAvatarsLp.height, 1073741824));
            textMaxWidth -= (this.lastMessageUserAvatars.getMeasuredWidth() - lastMessageUserAvatarsLp.leftMargin) - lastMessageUserAvatarsLp.rightMargin;
        }
        this.text.measure(MeasureSpec.makeMeasureSpec(textMaxWidth, LinearLayoutManager.INVALID_OFFSET), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        int avatarY = (height - this.avatar.getMeasuredHeight()) / 2;
        int avatarImageBottom = avatarY + this.avatar.getMeasuredHeight();
        this.avatar.layout(getPaddingLeft(), avatarY, getPaddingLeft() + this.avatar.getMeasuredWidth(), avatarImageBottom);
        MarginLayoutParams onlineLp = (MarginLayoutParams) this.online.getLayoutParams();
        int onlineLeft = getPaddingLeft() + onlineLp.leftMargin;
        int onlineBottom = avatarImageBottom - onlineLp.bottomMargin;
        this.online.layout(onlineLeft, onlineBottom - this.online.getMeasuredHeight(), this.online.getMeasuredWidth() + onlineLeft, onlineBottom);
        MultiUserAvatar multiUserAvatar = this.avatar;
        int nameX = r0.getRight() + ((MarginLayoutParams) this.avatar.getLayoutParams()).rightMargin;
        MarginLayoutParams nameLp = (MarginLayoutParams) this.name.getLayoutParams();
        MarginLayoutParams textLp = (MarginLayoutParams) this.text.getLayoutParams();
        MarginLayoutParams dateLp = (MarginLayoutParams) this.date.getLayoutParams();
        int i = nameLp.topMargin;
        View view = this.name;
        int i2 = nameLp.bottomMargin;
        i2 = textLp.topMargin;
        view = this.text;
        i2 = textLp.bottomMargin;
        i2 = dateLp.topMargin;
        view = this.date;
        i2 = dateLp.bottomMargin;
        int nameY = ((height - ((((((((r0 + r0.getMeasuredHeight()) + r0) + r0) + r0.getMeasuredHeight()) + r0) + r0) + r0.getMeasuredHeight()) + r0)) / 2) + nameLp.topMargin;
        this.name.layout(nameX, nameY, this.name.getMeasuredWidth() + nameX, this.name.getMeasuredHeight() + nameY);
        View view2 = this.name;
        int lastAuthorAndTextY = (r0.getMeasuredHeight() + nameY) + nameLp.bottomMargin;
        MarginLayoutParams lastMessageUserAvatarsLp = (MarginLayoutParams) this.lastMessageUserAvatars.getLayoutParams();
        int textOffsetX = 0;
        int textOffsetY = 0;
        int lastAuthorAndTextBottomY = 0;
        int offsetToCenterVertically;
        if (this.lastMessageMeText.getVisibility() == 0) {
            offsetToCenterVertically = getOffsetToBottom(this.lastMessageMeText.getMeasuredHeight(), this.text.getMeasuredHeight());
            if (offsetToCenterVertically < 0) {
                textOffsetY = -offsetToCenterVertically;
                offsetToCenterVertically = 0;
            }
            int lastMessageTextWidth = this.lastMessageMeText.getMeasuredWidth();
            lastAuthorAndTextBottomY = (this.lastMessageMeText.getMeasuredHeight() + lastAuthorAndTextY) + offsetToCenterVertically;
            this.lastMessageMeText.layout(nameX, lastAuthorAndTextY + offsetToCenterVertically, nameX + lastMessageTextWidth, lastAuthorAndTextBottomY);
            textOffsetX = ((MarginLayoutParams) this.lastMessageMeText.getLayoutParams()).rightMargin + lastMessageTextWidth;
        } else {
            if (this.lastMessageUserAvatars.getVisibility() == 0) {
                offsetToCenterVertically = getOffsetToBottom(this.lastMessageUserAvatars.getMeasuredHeight(), this.text.getMeasuredHeight());
                if (offsetToCenterVertically < 0) {
                    textOffsetY = -offsetToCenterVertically;
                    offsetToCenterVertically = 0;
                }
                lastAuthorAndTextBottomY = (this.lastMessageUserAvatars.getMeasuredHeight() + lastAuthorAndTextY) + offsetToCenterVertically;
                this.lastMessageUserAvatars.layout(nameX, lastAuthorAndTextY + offsetToCenterVertically, this.lastMessageUserAvatars.getMeasuredWidth() + nameX, lastAuthorAndTextBottomY);
                textOffsetX = lastMessageUserAvatarsLp.rightMargin + this.lastMessageUserAvatars.getMeasuredWidth();
            }
        }
        int lastTextBottomY = (this.text.getMeasuredHeight() + lastAuthorAndTextY) + textOffsetY;
        if (lastTextBottomY > lastAuthorAndTextBottomY) {
            lastAuthorAndTextBottomY = lastTextBottomY;
        }
        this.text.layout(nameX + textOffsetX, lastAuthorAndTextY + textOffsetY, (this.text.getMeasuredWidth() + nameX) + textOffsetX, lastTextBottomY);
        int dateY = lastAuthorAndTextBottomY + dateLp.topMargin;
        this.date.layout(nameX, dateY, this.date.getMeasuredWidth() + nameX, this.date.getMeasuredHeight() + dateY);
        MarginLayoutParams contextMenuLp = (MarginLayoutParams) this.contextMenuBtn.getLayoutParams();
        view2 = this.name;
        view = this.contextMenuBtn;
        int contextMenuBtnY = Math.max(((r0.getMeasuredHeight() - r0.getMeasuredHeight()) / 2) + nameY, contextMenuLp.topMargin);
        view2 = this.contextMenuBtn;
        int contextMenuBtnX = ((width - r0.getMeasuredWidth()) - contextMenuLp.rightMargin) - getPaddingRight();
        this.contextMenuBtn.layout(contextMenuBtnX, contextMenuBtnY, this.contextMenuBtn.getMeasuredWidth() + contextMenuBtnX, this.contextMenuBtn.getMeasuredHeight() + contextMenuBtnY);
        if (this.notifications.getVisibility() == 0) {
            MarginLayoutParams notificationsLp = (MarginLayoutParams) this.notifications.getLayoutParams();
            int notificationsY = contextMenuBtnY;
            view2 = this.notifications;
            i2 = notificationsLp.rightMargin;
            int notificationsX = ((contextMenuBtnX - r0.getMeasuredWidth()) - r0) - contextMenuLp.leftMargin;
            this.notifications.layout(notificationsX, notificationsY, this.notifications.getMeasuredWidth() + notificationsX, this.notifications.getMeasuredHeight() + notificationsY);
        }
        this.background.layout(0, 0, width, height);
    }

    private int getOffsetToBottom(int measuredHeight1, int measuredHeight2) {
        if (measuredHeight1 == 0 || measuredHeight2 == 0) {
            return 0;
        }
        return Math.round((float) (measuredHeight2 - measuredHeight1));
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    protected void dispatchSetPressed(boolean pressed) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!(pressed && (child.isClickable() || child.isLongClickable()))) {
                child.setPressed(pressed);
            }
        }
    }
}
