package ru.ok.android.ui.custom.cards.listcard;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.adapters.friends.HeaderMusicAdapter;
import ru.ok.android.ui.adapters.friends.ItemClickListenerControllerProvider;
import ru.ok.android.ui.adapters.friends.UserMusicHeaderAdapter;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController;
import ru.ok.android.ui.custom.cards.listcard.CardItem.FilterCard;
import ru.ok.android.ui.custom.cards.search.HeaderTitleViewsHolder;
import ru.ok.android.ui.custom.cards.search.UserViewsHolder;
import ru.ok.android.ui.custom.cards.search.UserViewsHolderCardBig;
import ru.ok.android.ui.dialogs.UserDoActionBox;
import ru.ok.android.ui.fragments.messages.view.ParticipantsPreviewView;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.QuickAction;
import ru.ok.android.ui.users.fragments.FragmentGuest;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.localization.base.LocalizedActivity;
import ru.ok.android.widget.GridView;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.guest.UserInfoGuest;

public class CardListAdapter extends Adapter<CardViewHolder> implements ItemClickListenerControllerProvider, FilterCard {
    protected LocalizedActivity activity;
    protected final LayoutInflater inflater;
    protected final RecyclerItemClickListenerController itemClickListenerController;
    protected List<AbsListItem> mData;
    private List<CardItem> mDataCard;

    public static abstract class AbsListItem<T> {
        public final T object;
        public final int type;

        protected AbsListItem(T object, int type) {
            this.object = object;
            this.type = type;
        }

        public T getObject() {
            return this.object;
        }

        public int getType() {
            return this.type;
        }

        public void bindViewHolder(CardViewHolder holder, CardListAdapter cardListAdapter) {
        }
    }

    public static class BlockUsers extends AbsListItem<List<UserCardItem>> {
        private boolean isBottomBlock;
        private boolean isTopBlock;

        public BlockUsers(List<UserCardItem> items) {
            super(items, 8);
        }

        public static GridViewHolder createViewHolder(ViewGroup parent) {
            GridView gridView = new GridView(parent.getContext());
            for (int i = 0; i < 5; i++) {
                View view = UserCardItem.newViewBigCard(parent);
                view.setVisibility(8);
                gridView.addView(view);
            }
            return new GridViewHolder(gridView);
        }

        public void bindViewHolder(CardViewHolder holder, CardListAdapter cardListAdapter) {
            int i;
            super.bindViewHolder(holder, cardListAdapter);
            GridViewHolder gridViewHolder = (GridViewHolder) holder;
            GridView gridView = gridViewHolder.itemView;
            Context context = holder.itemView.getContext();
            gridView.setBottomPadding(this.isBottomBlock);
            gridView.setTopPadding(this.isTopBlock);
            int columnCount = GridView.getCountColumns(context);
            for (i = 0; i < columnCount; i++) {
                if (i >= ((List) this.object).size()) {
                    ViewUtil.gone(gridView.getChildAt(i));
                } else {
                    UserCardItem userCardItem = (UserCardItem) ((List) this.object).get(i);
                    UserViewsHolderCardBig userViewsHolder = gridViewHolder.userViewHolders[i];
                    userCardItem.bindViewHolder(userViewsHolder, cardListAdapter);
                    View viewChild = userViewsHolder.itemView;
                    viewChild.setOnClickListener((OnClickListener) ((List) this.object).get(i));
                    viewChild.setVisibility(0);
                }
            }
            for (i = columnCount; i < gridView.getChildCount(); i++) {
                ViewUtil.gone(gridView.getChildAt(i));
            }
        }

        public void setTopBlock(boolean isTopBlock) {
            this.isTopBlock = isTopBlock;
        }

        public void setBottomBlock(boolean isBottomBlock) {
            this.isBottomBlock = isBottomBlock;
        }
    }

    public static class DividerBlockItem extends AbsListItem<String> {
        public static View newView(ViewGroup parent) {
            Context context = parent.getContext();
            View view = new View(context);
            view.setMinimumHeight(context.getResources().getDimensionPixelSize(2131230912));
            return view;
        }
    }

    public static class DividerItem extends AbsListItem<String> {
        public DividerItem() {
            super("", 6);
        }

        public static View newView(ViewGroup parent) {
            Context context = parent.getContext();
            View view = new View(context);
            view.setBackgroundResource(2130837850);
            view.setMinimumHeight(context.getResources().getDimensionPixelSize(2131230899));
            return view;
        }
    }

    public static class DoActionBoxUser extends UserDoActionBox {
        private final View anchor;
        private final UserInfo info;

        public DoActionBoxUser(View anchor, UserInfo user, boolean canWrite, boolean canCall) {
            super(anchor.getContext(), user, anchor);
            this.anchor = anchor;
            this.info = user;
            this.quickAction = new QuickAction(anchor.getContext());
            this.quickAction.setOnActionItemClickListener(this);
            if (canWrite) {
                this.quickAction.addActionItem(new ActionItem(0, 2131166192, 2130838576));
            }
            if (canCall) {
                this.quickAction.addActionItem(new ActionItem(1, 2131165468, 2130838571));
            }
            if (user instanceof UserInfoGuest) {
                this.quickAction.addActionItem(new ActionItem(2, 2131165671, 2130838574));
            }
        }

        public void onItemClick(QuickAction source, int pos, int actionId) {
            switch (actionId) {
                case RECEIVED_VALUE:
                    NavigationHelper.showMessagesForUser((Activity) this.anchor.getContext(), this.info.uid);
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    NavigationHelper.onCallUser(this.anchor.getContext(), this.info.uid);
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    Bundle bundle = new Bundle();
                    bundle.putString("key_uid", this.info.uid);
                    GlobalBus.send(2131624240, new BusEvent(null, bundle));
                    GlobalBus.send(2131624096, new BusEvent(null, bundle));
                default:
            }
        }
    }

    public static class FooterCardItem extends AbsListItem<String> {
        public FooterCardItem() {
            super("", 2);
        }

        public static View newView(ViewGroup parent) {
            Context context = parent.getContext();
            View view = new View(context);
            view.setBackgroundResource(2131492988);
            view.setMinimumHeight(context.getResources().getDimensionPixelSize(2131230905));
            return view;
        }
    }

    public static class GridViewHolder extends CardViewHolder {
        public UserViewsHolderCardBig[] userViewHolders;

        public GridViewHolder(View itemView) {
            super(itemView);
            this.userViewHolders = new UserViewsHolderCardBig[5];
            GridView gridView = (GridView) itemView;
            for (int i = 0; i < gridView.getChildCount(); i++) {
                this.userViewHolders[i] = new UserViewsHolderCardBig(gridView.getChildAt(i));
            }
        }
    }

    public static class HeaderCardItem extends AbsListItem<CharSequence> {
        public HeaderCardItem(CharSequence s) {
            super(s, 1);
        }

        public void bindViewHolder(CardViewHolder holder, CardListAdapter cardListAdapter) {
            ((HeaderTitleViewsHolder) holder).titleView.setText((CharSequence) this.object);
        }

        public static CardViewHolder createViewHolder(ViewGroup parent) {
            return new HeaderTitleViewsHolder(HeaderTitleViewsHolder.newView(parent));
        }
    }

    public static class MusicNewInterest extends AbsListItem<Void> {
        public MusicNewInterest() {
            super(null, 9);
        }

        public static View newView(ViewGroup parent) {
            return new HeaderMusicAdapter(parent.getContext(), 2131166253, 2130838135, true).getView(parent);
        }
    }

    public static class MyMusic extends AbsListItem<Void> {
        public MyMusic() {
            super(null, 11);
        }

        public static View newView(ViewGroup parent) {
            return new UserMusicHeaderAdapter(parent.getContext(), 2131166246, OdnoklassnikiApplication.getCurrentUser().genderType == UserGenderType.MALE ? 2130838321 : 2130837927, false).getView(parent);
        }
    }

    public static class ProgressBarCardItem extends AbsListItem<String> {
        public ProgressBarCardItem() {
            super(ProgressBarCardItem.class.getName(), 5);
        }

        public static View newView(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(2130903121, parent, false);
        }
    }

    public static class Radio extends AbsListItem<Void> {
        public Radio() {
            super(null, 10);
        }

        public static View newView(ViewGroup parent) {
            return new HeaderMusicAdapter(parent.getContext(), 2131166738, 2130838136, true).getView(parent);
        }
    }

    public static class UserBlockCardItem extends AbsListItem<List<UserInfo>> {
        private final CharSequence mTitle;
        private final List<UserInfo> users;

        public static class Holder extends CardViewHolder {
            public final ParticipantsPreviewView preview;
            public final TextView textTitle;

            public Holder(View view) {
                super(view);
                this.preview = (ParticipantsPreviewView) view.findViewById(2131624879);
                this.textTitle = (TextView) view.findViewById(2131625412);
            }
        }

        public UserBlockCardItem(List<UserInfo> users, CharSequence title) {
            super(users, 3);
            this.mTitle = title;
            this.users = users;
        }

        public void bindViewHolder(CardViewHolder holder, CardListAdapter cardListAdapter) {
            super.bindViewHolder(holder, cardListAdapter);
            Holder blocksHolder = (Holder) holder;
            blocksHolder.preview.setParticipants(this.users, false);
            blocksHolder.textTitle.setText(this.mTitle);
        }

        public static View newView(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(2130903551, null);
        }
    }

    public static class UserCardItem extends AbsListItem<UserInfo> implements OnClickListener {
        private ItemRelationType itemRelationType;
        private final ItemType itemType;

        public enum ItemRelationType {
            friend,
            portal
        }

        public enum ItemType {
            standard,
            music,
            guest
        }

        private UserCardItem(UserInfo userInfo, ItemType itemType) {
            super(userInfo, 0);
            this.itemType = itemType;
        }

        public UserCardItem(UserInfo userInfo, ItemType itemType, ItemRelationType itemRelationType) {
            this(userInfo, itemType);
            this.itemRelationType = itemRelationType;
        }

        protected UserCardItem(UserInfo userInfo, int type) {
            super(userInfo, type);
            this.itemType = ItemType.standard;
        }

        public static View newViewTypeMusic(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(2130903122, parent, false);
        }

        public static View newViewTypeGuest(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(2130903552, parent, false);
        }

        public static View newViewTypeSearch(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(2130903123, parent, false);
        }

        public static View newViewBigCard(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(2130903218, parent, false);
        }

        private static void bindViewMusic(Context context, UserViewsHolder userViewsHolder, UserInfo userInfo) {
            userViewsHolder.update(userInfo);
            userViewsHolder.infoView.setText(DateFormatter.formatDeltaTimePast(context, userInfo.lastOnline, false, false));
            ViewUtil.visible(userViewsHolder.rightButton, userViewsHolder.infoView);
        }

        public static void bindViewFriend(Context context, UserViewsHolder userViewsHolder, UserInfo userInfo) {
            userViewsHolder.update(userInfo);
            bindInfoViewTimestamp(context, userViewsHolder, userInfo.lastOnline);
            ViewUtil.setVisibility(userViewsHolder.privateProfileView, userInfo.isPrivateProfile());
            ViewUtil.visible(userViewsHolder.rightButton);
        }

        public static void bindViewNotFriend(Context context, UserViewsHolder userViewsHolder, UserInfo userInfo) {
            userViewsHolder.update(userInfo);
            bindInfoViewAgeLocation(context, userViewsHolder, userInfo);
        }

        private static void bindViewGuest(Context context, UserViewsHolder userViewsHolder, UserInfoGuest guestInfo) {
            userViewsHolder.update(guestInfo);
            if (guestInfo.commons >= 0) {
                userViewsHolder.infoView.setText(LocalizationManager.getString(context, 2131165613, Integer.toString(guestInfo.commons)));
            } else {
                bindInfoViewTimestamp(context, userViewsHolder, guestInfo.date);
            }
            userViewsHolder.infoView.setTextColor(guestInfo.isNew ? -65536 : FragmentGuest.COLOR_GRAY);
        }

        private static void bindInfoViewTimestamp(Context context, UserViewsHolder userViewsHolder, long ts) {
            String tsFormatted = DateFormatter.formatDeltaTimePast(context, ts, false, false);
            if (TextUtils.isEmpty(tsFormatted)) {
                userViewsHolder.infoView.setText(null);
                userViewsHolder.infoView.setVisibility(8);
                return;
            }
            userViewsHolder.infoView.setText(tsFormatted);
            userViewsHolder.infoView.setVisibility(0);
        }

        private static void bindInfoViewAgeLocation(Context context, UserViewsHolder userViewsHolder, UserInfo userInfo) {
            StringBuilder infoBuilder = userViewsHolder.infoBuilder;
            if (userInfo.age != -1) {
                infoBuilder.append(LocalizationManager.getString(context, StringUtils.plural((long) userInfo.age, 2131165364, 2131165365, 2131165366), Integer.valueOf(userInfo.age)));
            }
            if (!(userInfo.location == null || userInfo.location.city == null)) {
                if (infoBuilder.length() != 0) {
                    infoBuilder.append(", ");
                }
                infoBuilder.append(userInfo.location.city);
            }
            if (infoBuilder.length() == 0) {
                userViewsHolder.infoView.setText(null);
                userViewsHolder.infoView.setVisibility(8);
                return;
            }
            userViewsHolder.infoView.setText(userViewsHolder.infoBuilder.toString());
            userViewsHolder.infoView.setVisibility(0);
            userViewsHolder.infoBuilder.setLength(0);
        }

        public void bindViewHolder(CardViewHolder holder, CardListAdapter cardListAdapter) {
            super.bindViewHolder(holder, cardListAdapter);
            UserViewsHolder userViewsHolder = (UserViewsHolder) holder;
            UserInfo userInfo = this.object;
            Context context = holder.itemView.getContext();
            userViewsHolder.update(userInfo);
            if (this.itemType == ItemType.music) {
                bindViewMusic(context, userViewsHolder, userInfo);
            } else {
                if (this.itemType == ItemType.guest && (userInfo instanceof UserInfoGuest)) {
                    bindViewGuest(context, userViewsHolder, (UserInfoGuest) userInfo);
                } else if (this.itemRelationType == ItemRelationType.friend) {
                    bindViewFriend(context, userViewsHolder, userInfo);
                } else {
                    bindViewNotFriend(context, userViewsHolder, userInfo);
                }
                if (this.itemRelationType == ItemRelationType.friend) {
                    bindViewFriend(context, userViewsHolder, userInfo);
                }
            }
            cardListAdapter.postProcessItem(holder, getType(), this.object);
        }

        public void onClick(View view) {
            if (this.object != null) {
                NavigationHelper.showUserInfo((Activity) view.getContext(), ((UserInfo) this.object).uid);
            }
        }
    }

    public static class UserSearchCardItem extends UserCardItem {
        public UserSearchCardItem(UserInfo userInfo) {
            super(userInfo, 4);
        }
    }

    public CardListAdapter(LocalizedActivity context) {
        this.mDataCard = Arrays.asList(new Object[0]);
        this.mData = Arrays.asList(new Object[0]);
        this.itemClickListenerController = new RecyclerItemClickListenerController();
        this.inflater = LayoutInflater.from(context);
        this.activity = context;
    }

    public void setData(List<CardItem> dataCard) {
        this.mDataCard = dataCard;
        filter();
    }

    public void filter() {
        this.mData = CardItem.mergeCard(this.mDataCard, this);
        notifyDataSetChanged();
    }

    public void postProcessItem(CardViewHolder viewHolder, int typeItem, Object object) {
    }

    public boolean isNeed(AbsListItem absListItem) {
        return true;
    }

    public boolean headerIsEnable() {
        return true;
    }

    public Object getItem(int i) {
        return ((AbsListItem) this.mData.get(i)).object;
    }

    public long getItemId(int i) {
        return 0;
    }

    public int getItemCount() {
        return this.mData.size();
    }

    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case RECEIVED_VALUE:
                return new UserViewsHolder(UserCardItem.newViewTypeSearch(parent));
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return HeaderCardItem.createViewHolder(parent);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return new CardViewHolder(FooterCardItem.newView(parent));
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return new Holder(UserBlockCardItem.newView(parent));
            case Message.UUID_FIELD_NUMBER /*5*/:
                return new CardViewHolder(ProgressBarCardItem.newView(parent));
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return new CardViewHolder(DividerItem.newView(parent));
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                return new CardViewHolder(DividerBlockItem.newView(parent));
            case Message.TASKID_FIELD_NUMBER /*8*/:
                return BlockUsers.createViewHolder(parent);
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                return new CardViewHolder(MusicNewInterest.newView(parent));
            case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                return new CardViewHolder(Radio.newView(parent));
            case Message.EDITINFO_FIELD_NUMBER /*11*/:
                return new CardViewHolder(MyMusic.newView(parent));
            case Message.REPLYSTICKERS_FIELD_NUMBER /*12*/:
                return new UserViewsHolder(UserCardItem.newViewTypeMusic(parent));
            case Conversation.OWNERID_FIELD_NUMBER /*13*/:
                return new UserViewsHolder(UserCardItem.newViewTypeGuest(parent));
            default:
                return null;
        }
    }

    public void onBindViewHolder(CardViewHolder holder, int position) {
        ((AbsListItem) this.mData.get(position)).bindViewHolder(holder, this);
        this.itemClickListenerController.onBindViewHolder(holder, position);
    }

    public int getItemViewType(int position) {
        AbsListItem item = (AbsListItem) this.mData.get(position);
        int type = item.getType();
        if (type == 0 && ((UserCardItem) item).itemType == ItemType.music) {
            return 12;
        }
        return type;
    }

    public RecyclerItemClickListenerController getItemClickListenerController() {
        return this.itemClickListenerController;
    }
}
