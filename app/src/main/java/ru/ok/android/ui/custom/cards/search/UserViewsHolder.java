package ru.ok.android.ui.custom.cards.search;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.DoActionBoxUser;
import ru.ok.android.ui.custom.cards.listcard.CardViewHolder;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.URLUtil;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.ViewUtil;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public class UserViewsHolder extends CardViewHolder implements OnClickListener {
    public final AsyncDraweeView avatarView;
    public final ViewGroup avatarViewContainer;
    public final View dots;
    public StringBuilder infoBuilder;
    public final TextView infoView;
    public final TextView nameView;
    public final View onlineView;
    public final View privateProfileView;
    public final View rightButton;
    private UserInfo userInfo;

    /* renamed from: ru.ok.android.ui.custom.cards.search.UserViewsHolder.1 */
    class C06481 extends DoActionBoxUser {
        final /* synthetic */ View val$view;

        C06481(View x0, UserInfo x1, boolean x2, boolean x3, View view) {
            this.val$view = view;
            super(x0, x1, x2, x3);
        }

        public void show() {
            this.quickAction.show(this.val$view);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.cards.search.UserViewsHolder.2 */
    static /* synthetic */ class C06492 {
        static final /* synthetic */ int[] f92xfcaaa6d7;

        static {
            f92xfcaaa6d7 = new int[RightButtonType.values().length];
            try {
                f92xfcaaa6d7[RightButtonType.message.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f92xfcaaa6d7[RightButtonType.none.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public enum RightButtonType {
        none,
        message
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case 2131624668:
                if (this.userInfo != null) {
                    NavigationHelper.showMessagesForUser((Activity) view.getContext(), this.userInfo.uid);
                }
            case 2131624874:
                if (this.userInfo != null) {
                    new C06481(view, this.userInfo, true, Utils.userCanCall(this.userInfo), view).show();
                }
            default:
        }
    }

    public TextView getInfoView() {
        return this.infoView;
    }

    public UserViewsHolder(View view) {
        super(view);
        this.userInfo = null;
        this.infoBuilder = new StringBuilder();
        this.avatarView = (AsyncDraweeView) view.findViewById(2131624657);
        this.avatarViewContainer = (ViewGroup) view.findViewById(2131624540);
        this.nameView = (TextView) view.findViewById(C0263R.id.name);
        this.infoView = (TextView) view.findViewById(C0263R.id.info);
        this.onlineView = view.findViewById(2131624634);
        this.privateProfileView = view.findViewById(2131624666);
        int dtouch = view.getResources().getDimensionPixelSize(2131230952);
        this.rightButton = view.findViewById(2131624668);
        if (this.rightButton != null) {
            ViewUtil.setTouchDelegate(this.rightButton, dtouch, dtouch, dtouch, dtouch);
            this.rightButton.setOnClickListener(this);
        }
        setRightButtonType(RightButtonType.message);
        this.dots = view.findViewById(2131624874);
        if (this.dots != null) {
            ViewUtil.setTouchDelegate(this.dots, dtouch, dtouch, dtouch, dtouch);
            this.dots.setOnClickListener(this);
        }
        if (this.avatarViewContainer != null) {
            this.avatarViewContainer.setClickable(false);
            this.avatarViewContainer.setBackgroundResource(2130838333);
        }
    }

    public void setRightButtonType(RightButtonType type) {
        switch (C06492.f92xfcaaa6d7[type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                ViewUtil.visible(this.rightButton);
            default:
                ViewUtil.gone(this.rightButton);
        }
    }

    public void update(UserInfo userInfo) {
        this.nameView.setText(userInfo.getAnyName());
        this.avatarView.setEmptyImageResId(getEmptyImageResId(userInfo));
        Uri uri = null;
        try {
            uri = !URLUtil.isStubUrl(userInfo.picUrl) ? Uri.parse(userInfo.picUrl) : null;
        } catch (Throwable e) {
            Logger.m178e(e);
        }
        this.avatarView.setUri(uri);
        Utils.updateOnlineView(this.onlineView, Utils.onlineStatus(userInfo));
        this.privateProfileView.setVisibility(userInfo.isShowLock() ? 0 : 8);
        setRightButtonType(userInfo.privateProfile ? RightButtonType.none : RightButtonType.message);
        this.userInfo = userInfo;
    }

    protected int getEmptyImageResId(UserInfo userInfo) {
        return userInfo.genderType == UserGenderType.FEMALE ? 2130837927 : 2130838321;
    }
}
