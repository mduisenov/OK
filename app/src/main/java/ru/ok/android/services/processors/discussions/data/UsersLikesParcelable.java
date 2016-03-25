package ru.ok.android.services.processors.discussions.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ru.ok.java.api.response.discussion.UsersLikesResponse;
import ru.ok.model.UserInfo;

public final class UsersLikesParcelable implements Parcelable {
    public static final Creator<UsersLikesParcelable> CREATOR;
    private final boolean _allLoaded;
    private final String _anchorId;
    private final List<UserInfo> _users;

    /* renamed from: ru.ok.android.services.processors.discussions.data.UsersLikesParcelable.1 */
    static class C04531 implements Creator<UsersLikesParcelable> {
        C04531() {
        }

        public UsersLikesParcelable createFromParcel(Parcel parcel) {
            return new UsersLikesParcelable(null);
        }

        public UsersLikesParcelable[] newArray(int count) {
            return new UsersLikesParcelable[count];
        }
    }

    public UsersLikesParcelable(String anchorId, List<UserInfo> list, boolean allLoaded) {
        List arrayList;
        this._anchorId = anchorId;
        if (list == null) {
            arrayList = new ArrayList();
        }
        this._users = arrayList;
        this._allLoaded = allLoaded;
    }

    private UsersLikesParcelable(Parcel parcel) {
        this(parcel.readString(), parcel.readArrayList(UsersLikesParcelable.class.getClassLoader()), parcel.readByte() > null);
    }

    public String getAnchorId() {
        return this._anchorId;
    }

    public List<UserInfo> getUsers() {
        return this._users;
    }

    public boolean isAllLoaded() {
        return this._allLoaded;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this._anchorId);
        parcel.writeTypedList(this._users);
        parcel.writeByte(this._allLoaded ? (byte) 1 : (byte) 0);
    }

    static {
        CREATOR = new C04531();
    }

    public static UsersLikesParcelable fromResponse(UsersLikesResponse likes, int requestedItems) {
        List<UserInfo> userInfos = new ArrayList(likes.getUsers().size());
        Iterator i$ = likes.getUsers().iterator();
        while (i$.hasNext()) {
            userInfos.add((UserInfo) i$.next());
        }
        return new UsersLikesParcelable(likes.getAnchor(), userInfos, likes.getUsers().size() == 0);
    }

    public void setUsers(List<UserInfo> users) {
        this._users.clear();
        this._users.addAll(users);
    }
}
