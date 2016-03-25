package ru.ok.model.stream;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Calendar;
import ru.ok.model.UserInfo;

public class Holiday implements Parcelable, Comparable<Holiday> {
    public static final Creator<Holiday> CREATOR;
    int day;
    @NonNull
    String id;
    boolean isBirthday;
    boolean isNameday;
    @NonNull
    String message;
    int month;
    @NonNull
    ArrayList<String> userIds;
    @NonNull
    ArrayList<UserInfo> users;

    /* renamed from: ru.ok.model.stream.Holiday.1 */
    static class C15971 implements Creator<Holiday> {
        C15971() {
        }

        public Holiday createFromParcel(Parcel in) {
            return new Holiday(in);
        }

        public Holiday[] newArray(int size) {
            return new Holiday[size];
        }
    }

    Holiday() {
        this.users = new ArrayList();
        this.userIds = new ArrayList();
        this.message = "";
        this.id = "";
    }

    public Holiday(@NonNull ArrayList<String> userIds, @NonNull String message, @NonNull String id, int day, int month, boolean isNameday, boolean isBirthday) {
        this.userIds = userIds;
        this.message = message;
        this.id = id;
        this.day = day;
        this.month = month;
        this.isNameday = isNameday;
        this.isBirthday = isBirthday;
        this.users = new ArrayList();
    }

    protected Holiday(Parcel in) {
        boolean z;
        boolean z2 = true;
        ClassLoader classLoader = Holiday.class.getClassLoader();
        this.id = in.readString();
        this.message = in.readString();
        this.day = in.readInt();
        this.month = in.readInt();
        this.users = in.readArrayList(classLoader);
        this.userIds = in.readArrayList(classLoader);
        if (in.readInt() == 1) {
            z = true;
        } else {
            z = false;
        }
        this.isNameday = z;
        if (in.readInt() != 1) {
            z2 = false;
        }
        this.isBirthday = z2;
    }

    public int getType() {
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(5, -1);
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(5, 1);
        if (today.get(2) == this.month - 1 && today.get(5) == this.day) {
            return 1;
        }
        if (yesterday.get(2) == this.month - 1 && yesterday.get(5) == this.day) {
            return 3;
        }
        if (tomorrow.get(2) == this.month - 1 && tomorrow.get(5) == this.day) {
            return 2;
        }
        return 0;
    }

    @NonNull
    public ArrayList<UserInfo> getUsers() {
        return this.users;
    }

    @NonNull
    public String getMessage() {
        return this.message;
    }

    @NonNull
    public String getId() {
        return this.id;
    }

    public boolean isNameday() {
        return this.isNameday;
    }

    public boolean isBirthday() {
        return this.isBirthday;
    }

    public void writeToParcel(@NonNull Parcel dest, int flags) {
        int i;
        int i2 = 1;
        dest.writeString(this.id);
        dest.writeString(this.message);
        dest.writeInt(this.day);
        dest.writeInt(this.month);
        dest.writeList(this.users);
        dest.writeList(this.userIds);
        if (this.isNameday) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (!this.isBirthday) {
            i2 = 0;
        }
        dest.writeInt(i2);
    }

    public int describeContents() {
        return 0;
    }

    public int compareTo(Holiday holiday) {
        return getType() - holiday.getType();
    }

    static {
        CREATOR = new C15971();
    }
}
