package ru.ok.android.ui.search.fragment;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.UserCardItem;
import ru.ok.android.ui.custom.cards.listcard.CardViewHolder;
import ru.ok.android.ui.custom.cards.search.CommunityViewsHolder;
import ru.ok.android.ui.custom.cards.search.ExpandViewsHolder;
import ru.ok.android.ui.custom.cards.search.GroupViewsHolder;
import ru.ok.android.ui.custom.cards.search.HeaderTitleViewsHolder;
import ru.ok.android.ui.custom.cards.search.UserViewsHolder;
import ru.ok.model.search.SearchResult.SearchScope;
import ru.ok.model.search.SearchResultCommunity;
import ru.ok.model.search.SearchResultGroup;
import ru.ok.model.search.SearchResultUser;
import ru.ok.model.search.SearchType;

abstract class SearchAdapterItem implements Parcelable {

    static final class BlocksDividerItem extends SearchAdapterItem {
        public static final Creator<BlocksDividerItem> CREATOR;
        public static final int TYPE;

        /* renamed from: ru.ok.android.ui.search.fragment.SearchAdapterItem.BlocksDividerItem.1 */
        static class C11821 implements Creator<BlocksDividerItem> {
            C11821() {
            }

            public BlocksDividerItem createFromParcel(Parcel src) {
                return new BlocksDividerItem(src);
            }

            public BlocksDividerItem[] newArray(int count) {
                return new BlocksDividerItem[count];
            }
        }

        static {
            TYPE = BlocksDividerItem.class.hashCode();
            CREATOR = new C11821();
        }

        BlocksDividerItem() {
        }

        BlocksDividerItem(Parcel src) {
        }

        public int getType() {
            return TYPE;
        }

        public void writeToParcel(Parcel dest, int flags) {
        }
    }

    static final class CommunityItem extends SearchAdapterItem {
        public static final Creator<CommunityItem> CREATOR;
        public static final int TYPE;
        public SearchResultCommunity searchResult;

        /* renamed from: ru.ok.android.ui.search.fragment.SearchAdapterItem.CommunityItem.1 */
        static class C11831 implements Creator<CommunityItem> {
            C11831() {
            }

            public CommunityItem createFromParcel(Parcel src) {
                return new CommunityItem(src);
            }

            public CommunityItem[] newArray(int count) {
                return new CommunityItem[count];
            }
        }

        static {
            TYPE = CommunityItem.class.hashCode();
            CREATOR = new C11831();
        }

        CommunityItem() {
        }

        CommunityItem(Parcel src) {
            this.searchResult = (SearchResultCommunity) src.readParcelable(SearchResultCommunity.class.getClassLoader());
        }

        public int getType() {
            return TYPE;
        }

        public void setCommunitySearchResult(SearchResultCommunity searchResult) {
            this.searchResult = searchResult;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.searchResult, 0);
        }

        public static View newView(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(2130903117, parent, false);
        }

        public void bindViewHolder(CardViewHolder holder, SearchResultsAdapter adapter) {
            CommunityViewsHolder.bind((CommunityViewsHolder) holder, this.searchResult);
        }
    }

    static final class DividerItem extends SearchAdapterItem {
        public static final Creator<DividerItem> CREATOR;
        public static final int TYPE;

        /* renamed from: ru.ok.android.ui.search.fragment.SearchAdapterItem.DividerItem.1 */
        static class C11841 implements Creator<DividerItem> {
            C11841() {
            }

            public DividerItem createFromParcel(Parcel src) {
                return new DividerItem(src);
            }

            public DividerItem[] newArray(int count) {
                return new DividerItem[count];
            }
        }

        static {
            TYPE = DividerItem.class.hashCode();
            CREATOR = new C11841();
        }

        DividerItem() {
        }

        DividerItem(Parcel src) {
        }

        public int getType() {
            return TYPE;
        }

        public void writeToParcel(Parcel dest, int flags) {
        }
    }

    static final class ExpandItem extends SearchAdapterItem {
        public static final Creator<ExpandItem> CREATOR;
        public static final int TYPE;
        public String text;
        public SearchType type;

        /* renamed from: ru.ok.android.ui.search.fragment.SearchAdapterItem.ExpandItem.1 */
        static class C11851 implements Creator<ExpandItem> {
            C11851() {
            }

            public ExpandItem createFromParcel(Parcel src) {
                return new ExpandItem(src);
            }

            public ExpandItem[] newArray(int count) {
                return new ExpandItem[count];
            }
        }

        static {
            TYPE = ExpandItem.class.hashCode();
            CREATOR = new C11851();
        }

        ExpandItem() {
        }

        ExpandItem(Parcel src) {
            this.text = src.readString();
            this.type = SearchType.values()[src.readInt()];
        }

        public int getType() {
            return TYPE;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.text);
            dest.writeInt(this.type == null ? SearchType.ALL.ordinal() : this.type.ordinal());
        }

        public static View newView(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(2130903118, parent, false);
        }

        public void bindViewHolder(CardViewHolder holder, SearchResultsAdapter adapter) {
            ((ExpandViewsHolder) holder).update(this.text);
        }
    }

    static final class FooterItem extends SearchAdapterItem {
        public static final Creator<FooterItem> CREATOR;
        public static final int TYPE;

        /* renamed from: ru.ok.android.ui.search.fragment.SearchAdapterItem.FooterItem.1 */
        static class C11861 implements Creator<FooterItem> {
            C11861() {
            }

            public FooterItem createFromParcel(Parcel src) {
                return new FooterItem(src);
            }

            public FooterItem[] newArray(int count) {
                return new FooterItem[count];
            }
        }

        static {
            TYPE = FooterItem.class.hashCode();
            CREATOR = new C11861();
        }

        FooterItem() {
        }

        FooterItem(Parcel src) {
        }

        public int getType() {
            return TYPE;
        }

        public void writeToParcel(Parcel dest, int flags) {
        }
    }

    static final class GroupItem extends SearchAdapterItem {
        public static final Creator<GroupItem> CREATOR;
        public static final int TYPE;
        public SearchResultGroup groupSearchResult;

        /* renamed from: ru.ok.android.ui.search.fragment.SearchAdapterItem.GroupItem.1 */
        static class C11871 implements Creator<GroupItem> {
            C11871() {
            }

            public GroupItem createFromParcel(Parcel src) {
                return new GroupItem(src);
            }

            public GroupItem[] newArray(int count) {
                return new GroupItem[count];
            }
        }

        static {
            TYPE = GroupItem.class.hashCode();
            CREATOR = new C11871();
        }

        GroupItem() {
        }

        GroupItem(Parcel src) {
            this.groupSearchResult = (SearchResultGroup) src.readParcelable(SearchResultGroup.class.getClassLoader());
        }

        public int getType() {
            return TYPE;
        }

        public void setGroupSearchResult(SearchResultGroup groupSearchResult) {
            this.groupSearchResult = groupSearchResult;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.groupSearchResult, 0);
        }

        public static View newView(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(2130903119, parent, false);
        }

        public void bindViewHolder(CardViewHolder holder, SearchResultsAdapter adapter) {
            ((GroupViewsHolder) holder).update(this.groupSearchResult.getGroupInfo());
        }
    }

    static final class HeaderTitleItem extends SearchAdapterItem {
        public static final Creator<HeaderTitleItem> CREATOR;
        public static final int TYPE;
        public String title;

        /* renamed from: ru.ok.android.ui.search.fragment.SearchAdapterItem.HeaderTitleItem.1 */
        static class C11881 implements Creator<HeaderTitleItem> {
            C11881() {
            }

            public HeaderTitleItem createFromParcel(Parcel src) {
                return new HeaderTitleItem(src);
            }

            public HeaderTitleItem[] newArray(int count) {
                return new HeaderTitleItem[count];
            }
        }

        static {
            TYPE = HeaderTitleItem.class.hashCode();
            CREATOR = new C11881();
        }

        HeaderTitleItem() {
        }

        HeaderTitleItem(Parcel src) {
            this.title = src.readString();
        }

        public int getType() {
            return TYPE;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.title);
        }

        public static View newView(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(2130903120, parent, false);
        }

        public void bindViewHolder(CardViewHolder holder, SearchResultsAdapter adapter) {
            ((HeaderTitleViewsHolder) holder).update(this.title);
        }
    }

    static final class UserItem extends SearchAdapterItem {
        public static final Creator<UserItem> CREATOR;
        public static final int TYPE;
        public SearchResultUser userSearchResult;

        /* renamed from: ru.ok.android.ui.search.fragment.SearchAdapterItem.UserItem.1 */
        static class C11891 implements Creator<UserItem> {
            C11891() {
            }

            public UserItem createFromParcel(Parcel src) {
                return new UserItem(src);
            }

            public UserItem[] newArray(int count) {
                return new UserItem[count];
            }
        }

        static {
            TYPE = UserItem.class.hashCode();
            CREATOR = new C11891();
        }

        UserItem() {
        }

        UserItem(Parcel src) {
            this.userSearchResult = (SearchResultUser) src.readParcelable(SearchResultUser.class.getClassLoader());
        }

        public int getType() {
            return TYPE;
        }

        public void setUserSearchResult(SearchResultUser userSearchResult) {
            this.userSearchResult = userSearchResult;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.userSearchResult, 0);
        }

        public void bindViewHolder(CardViewHolder holder, SearchResultsAdapter adapter) {
            Context context = holder.itemView.getContext();
            UserViewsHolder userViewsHolder = (UserViewsHolder) holder;
            if (this.userSearchResult.getScope() == SearchScope.OWN) {
                UserCardItem.bindViewFriend(context, userViewsHolder, this.userSearchResult.getUserInfo());
            } else {
                UserCardItem.bindViewNotFriend(context, userViewsHolder, this.userSearchResult.getUserInfo());
            }
        }
    }

    public abstract int getType();

    SearchAdapterItem() {
    }

    public int describeContents() {
        return 0;
    }

    public void bindViewHolder(CardViewHolder holder, SearchResultsAdapter adapter) {
    }
}
