package ru.ok.android.ui.custom.cards.listcard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.AbsListItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.BlockUsers;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.DividerItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.FooterCardItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.HeaderCardItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.ProgressBarCardItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.UserBlockCardItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.UserCardItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.UserCardItem.ItemRelationType;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.UserCardItem.ItemType;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.UserSearchCardItem;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.widget.GridView;
import ru.ok.model.UserInfo;

public class CardItem {
    private static final FilterCard FILTER_CARD_DEFAULT;
    private boolean isEnable;
    private List<AbsListItem> mAbsListItems;
    private List<UserInfo> mInfoList;
    private ItemRelationType mInfoListRelationType;
    private CharSequence mTitle;
    private Type mType;
    private ItemType mUserItemType;

    public interface FilterCard {
        boolean headerIsEnable();

        boolean isNeed(AbsListItem absListItem);
    }

    /* renamed from: ru.ok.android.ui.custom.cards.listcard.CardItem.1 */
    static class C06451 implements FilterCard {
        C06451() {
        }

        public boolean isNeed(AbsListItem absListItem) {
            return true;
        }

        public boolean headerIsEnable() {
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.custom.cards.listcard.CardItem.2 */
    static /* synthetic */ class C06462 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$ui$custom$cards$listcard$CardItem$Type;

        static {
            $SwitchMap$ru$ok$android$ui$custom$cards$listcard$CardItem$Type = new int[Type.values().length];
            try {
                $SwitchMap$ru$ok$android$ui$custom$cards$listcard$CardItem$Type[Type.progressBar.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$custom$cards$listcard$CardItem$Type[Type.block.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$custom$cards$listcard$CardItem$Type[Type.list_search.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$custom$cards$listcard$CardItem$Type[Type.list_abs.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$custom$cards$listcard$CardItem$Type[Type.list.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public enum Type {
        block,
        list,
        list_search,
        progressBar,
        list_abs
    }

    public CardItem() {
        this.mUserItemType = ItemType.standard;
        this.mType = Type.list;
        this.isEnable = true;
    }

    static {
        FILTER_CARD_DEFAULT = new C06451();
    }

    public boolean isEnable() {
        return this.isEnable;
    }

    public CardItem setEnable(boolean isEnable) {
        this.isEnable = isEnable;
        return this;
    }

    public CardItem setInfoList(List<UserInfo> infoList) {
        this.mInfoList = infoList;
        return this;
    }

    public CardItem setInfoList(List<UserInfo> infoList, ItemRelationType infoListRelationType) {
        this.mInfoList = infoList;
        this.mInfoListRelationType = infoListRelationType;
        return this;
    }

    public CardItem setInfoList(List<UserInfo> infoList, ItemType itemType) {
        this.mInfoList = infoList;
        this.mUserItemType = itemType;
        return this;
    }

    public CardItem setAbsItemList(List<AbsListItem> absItemList) {
        this.mAbsListItems = absItemList;
        return this;
    }

    public CardItem setType(Type type) {
        this.mType = type;
        return this;
    }

    public CardItem setTitle(CharSequence title) {
        this.mTitle = title;
        return this;
    }

    public int size() {
        return this.mInfoList == null ? 0 : this.mInfoList.size();
    }

    public static List<AbsListItem> mergeCard(List<CardItem> dataCard, FilterCard filterCard) {
        List<AbsListItem> list = new ArrayList();
        for (int i = 0; i < dataCard.size(); i++) {
            list.addAll(((CardItem) dataCard.get(i)).createData(filterCard));
        }
        return list;
    }

    public List<AbsListItem> createData(FilterCard filterCard) {
        if (filterCard == null) {
            filterCard = FILTER_CARD_DEFAULT;
        }
        List<AbsListItem> list;
        boolean headerIsAdd;
        AbsListItem userSearchCardItem;
        switch (C06462.$SwitchMap$ru$ok$android$ui$custom$cards$listcard$CardItem$Type[this.mType.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                AbsListItem item = new ProgressBarCardItem();
                if (isEnable() && filterCard.isNeed(item)) {
                    list = Arrays.asList(new AbsListItem[]{item});
                } else {
                    list = new ArrayList();
                }
                return list;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (filterCard.isNeed(new UserBlockCardItem(this.mInfoList, this.mTitle))) {
                    list = Arrays.asList(new AbsListItem[]{new UserBlockCardItem(this.mInfoList, this.mTitle)});
                } else {
                    list = new ArrayList();
                }
                return list;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                list = new ArrayList();
                headerIsAdd = false;
                for (UserInfo userSearchCardItem2 : this.mInfoList) {
                    userSearchCardItem = new UserSearchCardItem(userSearchCardItem2);
                    if (filterCard.isNeed(userSearchCardItem)) {
                        if (!headerIsAdd && filterCard.headerIsEnable()) {
                            list.add(new HeaderCardItem(this.mTitle));
                            headerIsAdd = true;
                        } else if (!list.isEmpty()) {
                            list.add(new DividerItem());
                        }
                        list.add(userSearchCardItem);
                    }
                }
                if (!list.isEmpty()) {
                    list.add(new FooterCardItem());
                }
                return list;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                list = new ArrayList();
                headerIsAdd = false;
                for (AbsListItem absListItem : this.mAbsListItems) {
                    if (!headerIsAdd && filterCard.headerIsEnable()) {
                        headerIsAdd = true;
                    }
                    if (!list.isEmpty()) {
                        list.add(new DividerItem());
                    }
                    list.add(absListItem);
                }
                if (!list.isEmpty()) {
                    list.add(new FooterCardItem());
                }
                return list;
            default:
                if (filterCard.headerIsEnable() || DeviceUtils.getType(OdnoklassnikiApplication.getContext()) == DeviceLayoutType.SMALL) {
                    list = new ArrayList();
                    headerIsAdd = false;
                    for (UserInfo userInfo : this.mInfoList) {
                        userSearchCardItem = new UserCardItem(userInfo, this.mUserItemType, this.mInfoListRelationType);
                        if (filterCard.isNeed(userSearchCardItem)) {
                            if (!headerIsAdd && filterCard.headerIsEnable()) {
                                list.add(new HeaderCardItem(this.mTitle));
                                headerIsAdd = true;
                            } else if (!list.isEmpty()) {
                                list.add(new DividerItem());
                            }
                            list.add(userSearchCardItem);
                        }
                    }
                    if (!list.isEmpty()) {
                        list.add(new FooterCardItem());
                    }
                    return list;
                }
                List<UserCardItem> userCardItems = new ArrayList();
                for (UserInfo info : this.mInfoList) {
                    UserCardItem cardItem = new UserCardItem(info, this.mUserItemType, this.mInfoListRelationType);
                    if (filterCard.isNeed(cardItem)) {
                        userCardItems.add(cardItem);
                    }
                }
                int columnCount = GridView.getCountColumns(OdnoklassnikiApplication.getContext());
                list = new ArrayList();
                int countRow = (userCardItems.size() / columnCount) + (userCardItems.size() % columnCount == 0 ? 0 : 1);
                for (int i = 0; i < countRow; i++) {
                    BlockUsers blockUsers;
                    List<UserCardItem> itemsRow = new ArrayList();
                    int j = 0;
                    while (j < columnCount) {
                        int index = (i * columnCount) + j;
                        if (index >= userCardItems.size()) {
                            blockUsers = new BlockUsers(itemsRow);
                            if (i == 0) {
                                blockUsers.setTopBlock(true);
                            }
                            if (i == countRow - 1) {
                                blockUsers.setBottomBlock(true);
                            }
                            list.add(blockUsers);
                        } else {
                            itemsRow.add(userCardItems.get(index));
                            j++;
                        }
                    }
                    blockUsers = new BlockUsers(itemsRow);
                    if (i == 0) {
                        blockUsers.setTopBlock(true);
                    }
                    if (i == countRow - 1) {
                        blockUsers.setBottomBlock(true);
                    }
                    list.add(blockUsers);
                }
                return list;
        }
    }
}
