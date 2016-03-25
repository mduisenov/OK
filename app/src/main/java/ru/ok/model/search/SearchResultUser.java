package ru.ok.model.search;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.model.UserInfo;

public class SearchResultUser extends SearchResult {
    public static final Creator<SearchResultUser> CREATOR;
    private UserInfo userInfo;

    /* renamed from: ru.ok.model.search.SearchResultUser.1 */
    static class C15891 implements Creator<SearchResultUser> {
        C15891() {
        }

        public SearchResultUser createFromParcel(Parcel src) {
            SearchResultUser user = new SearchResultUser();
            user.readFromParcel(src);
            return user;
        }

        public SearchResultUser[] newArray(int count) {
            return new SearchResultUser[count];
        }
    }

    public UserInfo getUserInfo() {
        return this.userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public SearchType getType() {
        return SearchType.USER;
    }

    public final void readFromParcel(Parcel src) {
        super.readFromParcel(src);
        this.userInfo = (UserInfo) src.readParcelable(UserInfo.class.getClassLoader());
    }

    public final void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.userInfo, 0);
    }

    static {
        CREATOR = new C15891();
    }
}
