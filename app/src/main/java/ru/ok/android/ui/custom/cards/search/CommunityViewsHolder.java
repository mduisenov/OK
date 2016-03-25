package ru.ok.android.ui.custom.cards.search;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.cards.listcard.CardViewHolder;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GroupInfo;
import ru.ok.model.search.SearchResultCommunity;
import ru.ok.model.search.SearchResultCommunity.CommunityType;

public class CommunityViewsHolder extends CardViewHolder {
    private AsyncDraweeView avatarView;
    private TextView infoView;
    private TextView nameView;
    private View officialView;
    private View privateGroupView;
    private View rootView;

    /* renamed from: ru.ok.android.ui.custom.cards.search.CommunityViewsHolder.1 */
    static /* synthetic */ class C06471 {
        static final /* synthetic */ int[] f91xd6b45389;

        static {
            f91xd6b45389 = new int[CommunityType.values().length];
            try {
                f91xd6b45389[CommunityType.SCHOOL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f91xd6b45389[CommunityType.COMMUNITY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f91xd6b45389[CommunityType.ARMY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f91xd6b45389[CommunityType.COLLEAGUE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f91xd6b45389[CommunityType.UNIVERSITY.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f91xd6b45389[CommunityType.WORKPLACE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    public static void bind(CommunityViewsHolder holder, SearchResultCommunity community) {
        Context context = holder.itemView.getContext();
        GroupInfo groupInfo = community.getGroupInfo();
        holder.update(groupInfo);
        String typeTitle = getTitleForType(context, community.getCommunityType());
        holder.infoView.setText(typeTitle);
        if (!TextUtils.isEmpty(typeTitle)) {
            holder.infoView.append(", ");
        }
        int avatar = getAvatarForType(context, community.getCommunityType());
        if (avatar != 0) {
            holder.avatarView.setErrorImageResId(avatar);
            holder.avatarView.setEmptyImageResId(avatar);
        }
        int membersCountRes = StringUtils.plural((long) groupInfo.getMembersCount(), 2131166186, 2131166187, 2131166188);
        holder.infoView.append(LocalizationManager.getString(context, membersCountRes, Integer.valueOf(count)));
    }

    public CommunityViewsHolder(View view) {
        super(view);
        this.rootView = view;
        this.avatarView = (AsyncDraweeView) this.rootView.findViewById(2131624657);
        this.avatarView.setErrorImageResId(2130837663);
        this.avatarView.setEmptyImageResId(2130837663);
        this.officialView = this.rootView.findViewById(2131624658);
        this.nameView = (TextView) this.rootView.findViewById(C0263R.id.name);
        this.infoView = (TextView) this.rootView.findViewById(C0263R.id.info);
        this.privateGroupView = this.rootView.findViewById(2131624659);
    }

    public void update(GroupInfo groupInfo) {
        int i = 0;
        this.officialView.setVisibility(groupInfo.isPremium() ? 0 : 8);
        this.nameView.setText(groupInfo.getName());
        if (groupInfo.getAvatarUrl() == null) {
            this.avatarView.setImageResource(2130837663);
        } else {
            this.avatarView.setUri(groupInfo.getAvatarUrl());
        }
        View view = this.privateGroupView;
        if (!groupInfo.isPrivateGroup()) {
            i = 8;
        }
        view.setVisibility(i);
    }

    private static String getTitleForType(Context context, CommunityType type) {
        switch (C06471.f91xd6b45389[type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return LocalizationManager.getString(context, 2131165620);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return LocalizationManager.getString(context, 2131165617);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return LocalizationManager.getString(context, 2131165615);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return LocalizationManager.getString(context, 2131165616);
            case Message.UUID_FIELD_NUMBER /*5*/:
                return LocalizationManager.getString(context, 2131165621);
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return LocalizationManager.getString(context, 2131165622);
            default:
                return null;
        }
    }

    private static int getAvatarForType(Context context, CommunityType type) {
        switch (C06471.f91xd6b45389[type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return 2130837822;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return 2130837821;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return 2130837819;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return 2130837820;
            case Message.UUID_FIELD_NUMBER /*5*/:
                return 2130837823;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return 2130837824;
            default:
                return 0;
        }
    }
}
