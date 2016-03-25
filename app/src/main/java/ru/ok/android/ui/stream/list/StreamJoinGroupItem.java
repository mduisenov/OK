package ru.ok.android.ui.stream.list;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;
import ru.mail.libverify.C0176R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.UsersStripView;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.groups.GroupsUsersHolder;
import ru.ok.android.ui.stream.groups.GroupsUsersHolder.GroupsUsersHolderListener;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.utils.ObjectUtils;
import ru.ok.java.api.utils.ObjectUtils.UncompatibleObjectEquals;
import ru.ok.model.GroupInfo;
import ru.ok.model.GroupType;
import ru.ok.model.UserInfo;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedGroupEntity;
import ru.ok.model.stream.entities.FeedUserEntity;

public final class StreamJoinGroupItem extends StreamItemAdjustablePaddings {
    final CharSequence descriptionText;
    final FeedGroupEntity group;
    final String imageUrl;
    final CharSequence titleText;

    /* renamed from: ru.ok.android.ui.stream.list.StreamJoinGroupItem.1 */
    static /* synthetic */ class C12371 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$GroupType;

        static {
            $SwitchMap$ru$ok$model$GroupType = new int[GroupType.values().length];
            try {
                $SwitchMap$ru$ok$model$GroupType[GroupType.WORKPLACE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupType[GroupType.SCHOOL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupType[GroupType.MOIMIR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupType[GroupType.ARMY.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupType[GroupType.COLLEGE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupType[GroupType.FACULTY.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupType[GroupType.UNIVERSITY.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupType[GroupType.HOLIDAY.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupType[GroupType.CUSTOM.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupType[GroupType.OTHER.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    static class GroupViewHolder extends ViewHolder implements GroupsUsersHolderListener {
        private static UncompatibleObjectEquals<BaseEntity, UserInfo> userEntityEqual;
        final TextView descriptionView;
        private FeedGroupEntity group;
        final UrlImageView iconView;
        final TextView textView;
        final UsersStripView usersView;

        /* renamed from: ru.ok.android.ui.stream.list.StreamJoinGroupItem.GroupViewHolder.1 */
        static class C12381 implements UncompatibleObjectEquals<BaseEntity, UserInfo> {
            C12381() {
            }

            public boolean equal(BaseEntity entity, UserInfo userInfo) {
                if (entity instanceof FeedUserEntity) {
                    return ObjectUtils.equals(userInfo, ((FeedUserEntity) entity).getUserInfo());
                }
                return false;
            }
        }

        GroupViewHolder(View view) {
            super(view);
            this.iconView = (UrlImageView) view.findViewById(C0176R.id.icon);
            this.iconView.setIsAlpha(true);
            this.textView = (TextView) view.findViewById(C0263R.id.text);
            this.descriptionView = (TextView) view.findViewById(2131624899);
            this.usersView = (UsersStripView) view.findViewById(2131625336);
        }

        public void setGroup(FeedGroupEntity group, CharSequence titleText, CharSequence descriptionText, String imageUrl, HandleBlocker imageLoadBlocker, GroupsUsersHolder groupsFriendsHolder) {
            this.group = group;
            this.itemView.setTag(2131624777, group);
            this.itemView.setTag(2131624777, group);
            this.usersView.setTag(2131624777, group);
            this.textView.setText(titleText);
            Utils.setTextViewTextWithVisibility(this.descriptionView, descriptionText);
            ImageViewManager.getInstance().displayImage(imageUrl, this.iconView, StreamJoinGroupItem.getStubResId(group), imageLoadBlocker);
            Pair<List<UserInfo>, GroupInfo> info = groupsFriendsHolder.getFriends(group.getId());
            if (info == null) {
                groupsFriendsHolder.addListener(this);
                this.usersView.setVisibility(8);
                this.descriptionView.setVisibility(4);
                return;
            }
            processFriends(info);
        }

        public void onFriendsLoaded(String groupId, Pair<List<UserInfo>, GroupInfo> info) {
            if (this.group != null && TextUtils.equals(groupId, this.group.getId())) {
                processFriends(info);
            }
        }

        private void processFriends(Pair<List<UserInfo>, GroupInfo> info) {
            List<UserInfo> friendsInGroup = info != null ? (List) info.first : null;
            boolean hasUsers = (friendsInGroup == null || friendsInGroup.isEmpty()) ? false : true;
            if (this.feed == null) {
                this.usersView.setUsers(Collections.emptyList(), 0);
                return;
            }
            boolean descrVisible;
            int i;
            List<? extends BaseEntity> actors = this.feed.getActors();
            if (!hasUsers || ObjectUtils.containsAll(actors, friendsInGroup, getUserEntityEqual())) {
                this.usersView.setVisibility(8);
            } else {
                this.usersView.setUsers(friendsInGroup, friendsInGroup.size());
                this.usersView.setVisibility(0);
            }
            GroupInfo group = info != null ? (GroupInfo) info.second : null;
            int members = group == null ? 0 : group.getMembersCount();
            if (!hasUsers || members <= 0) {
                String descr = group != null ? group.getDescription() : null;
                if (TextUtils.isEmpty(descr)) {
                    descrVisible = false;
                } else {
                    this.descriptionView.setText(descr);
                    descrVisible = true;
                }
            } else {
                this.descriptionView.setText(LocalizationManager.getString(this.itemView.getContext(), StringUtils.plural((long) members, 2131166186, 2131166187, 2131166188), Integer.valueOf(members)));
                descrVisible = true;
            }
            TextView textView = this.descriptionView;
            if (descrVisible) {
                i = 0;
            } else {
                i = 4;
            }
            textView.setVisibility(i);
        }

        private static UncompatibleObjectEquals<BaseEntity, UserInfo> getUserEntityEqual() {
            if (userEntityEqual == null) {
                userEntityEqual = new C12381();
            }
            return userEntityEqual;
        }
    }

    protected StreamJoinGroupItem(FeedWithState feed, FeedGroupEntity group, CharSequence titleText, CharSequence descriptionText, String imageUrl) {
        super(10, 3, 1, feed);
        this.group = group;
        this.titleText = titleText;
        this.descriptionText = descriptionText;
        this.imageUrl = imageUrl;
    }

    public void prefetch() {
        PrefetchUtils.prefetchUrl(this.imageUrl);
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903479, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof GroupViewHolder) {
            ((GroupViewHolder) holder).setGroup(this.group, this.titleText, this.descriptionText, this.imageUrl, streamItemViewController.getImageLoadBlocker(), streamItemViewController.getGroupsFriendsHolder());
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public static GroupViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        GroupViewHolder groupViewHolder = new GroupViewHolder(view);
        groupViewHolder.itemView.setOnClickListener(streamItemViewController.getJoinGroupClickListener());
        groupViewHolder.itemView.setTag(2131624343, "join_group_card");
        groupViewHolder.iconView.setOnClickListener(streamItemViewController.getJoinGroupClickListener());
        groupViewHolder.iconView.setTag(2131624343, "join_group_avatar");
        groupViewHolder.usersView.setOnClickListener(streamItemViewController.getGroupMembersClickListener());
        return groupViewHolder;
    }

    boolean sharePressedState() {
        return false;
    }

    int getVSpacingBottom(Context context) {
        return context.getResources().getDimensionPixelSize(2131230978);
    }

    private static int getStubResId(FeedGroupEntity groupEntity) {
        GroupInfo groupInfo = groupEntity == null ? null : groupEntity.getGroupInfo();
        GroupType groupType = groupInfo == null ? GroupType.OTHER : groupInfo.getType();
        if (groupType == null) {
            groupType = GroupType.OTHER;
        }
        switch (C12371.$SwitchMap$ru$ok$model$GroupType[groupType.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return 2130838017;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return 2130838012;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return 2130838015;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return 2130838011;
            case Message.UUID_FIELD_NUMBER /*5*/:
                return 2130838014;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                return 2130838016;
            case Message.TASKID_FIELD_NUMBER /*8*/:
                return 2130838013;
            default:
                return 2130837663;
        }
    }
}
