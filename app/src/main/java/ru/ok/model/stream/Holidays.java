package ru.ok.model.stream;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import ru.ok.model.UserInfo;

public class Holidays implements Parcelable {
    public static final Creator<Holidays> CREATOR;
    @NonNull
    ArrayList<Holiday> holidays;
    @NonNull
    HashMap<String, UserInfo> userEntities;

    /* renamed from: ru.ok.model.stream.Holidays.1 */
    static class C15981 implements Creator<Holidays> {
        C15981() {
        }

        public Holidays createFromParcel(Parcel in) {
            return new Holidays(in);
        }

        public Holidays[] newArray(int size) {
            return new Holidays[size];
        }
    }

    Holidays() {
        this.holidays = new ArrayList();
        this.userEntities = new HashMap();
    }

    public Holidays(@NonNull ArrayList<Holiday> holidays, @NonNull HashMap<String, UserInfo> userEntities) {
        this.holidays = holidays;
        this.userEntities = userEntities;
    }

    protected Holidays(@NonNull Parcel in) {
        ClassLoader classLoader = Holidays.class.getClassLoader();
        this.holidays = in.readArrayList(classLoader);
        this.userEntities = in.readHashMap(classLoader);
    }

    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeList(this.holidays);
        dest.writeMap(this.userEntities);
    }

    public int describeContents() {
        return 0;
    }

    public void prepareHolidays() {
        removeInvalidHolidays();
        resolveRefs();
        Collections.sort(this.holidays);
    }

    @NonNull
    public ArrayList<Holiday> getHolidays() {
        return this.holidays;
    }

    private void removeInvalidHolidays() {
        Iterator<Holiday> i = this.holidays.iterator();
        while (i.hasNext()) {
            if (((Holiday) i.next()).getType() == 0) {
                i.remove();
            }
        }
    }

    private void resolveRefs() {
        Iterator it = this.holidays.iterator();
        while (it.hasNext()) {
            Holiday holiday = (Holiday) it.next();
            Iterator i$ = holiday.userIds.iterator();
            while (i$.hasNext()) {
                UserInfo userInfo = (UserInfo) this.userEntities.get((String) i$.next());
                if (userInfo != null) {
                    holiday.users.add(userInfo);
                }
            }
        }
    }

    static {
        CREATOR = new C15981();
    }
}
