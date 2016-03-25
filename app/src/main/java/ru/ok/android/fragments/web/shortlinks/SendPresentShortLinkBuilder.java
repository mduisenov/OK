package ru.ok.android.fragments.web.shortlinks;

import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.utils.Utils;

public class SendPresentShortLinkBuilder implements Parcelable {
    public static final Creator<SendPresentShortLinkBuilder> CREATOR;
    private String holidayId;
    private String presentId;
    private String result;
    private String section;
    private String token;
    private String userId;

    /* renamed from: ru.ok.android.fragments.web.shortlinks.SendPresentShortLinkBuilder.1 */
    static class C03401 implements Creator<SendPresentShortLinkBuilder> {
        C03401() {
        }

        public SendPresentShortLinkBuilder createFromParcel(Parcel in) {
            return new SendPresentShortLinkBuilder(in);
        }

        public SendPresentShortLinkBuilder[] newArray(int size) {
            return new SendPresentShortLinkBuilder[size];
        }
    }

    private SendPresentShortLinkBuilder() {
    }

    @NonNull
    public static SendPresentShortLinkBuilder openPresents() {
        return new SendPresentShortLinkBuilder();
    }

    @NonNull
    public static SendPresentShortLinkBuilder choosePresentWithSelectedUser(@NonNull String userId) {
        SendPresentShortLinkBuilder builder = new SendPresentShortLinkBuilder();
        builder.userId = userId;
        return builder;
    }

    @NonNull
    public static SendPresentShortLinkBuilder choosePresentWithSelectedUser(@NonNull String userId, @NonNull String section) {
        SendPresentShortLinkBuilder builder = new SendPresentShortLinkBuilder();
        builder.userId = userId;
        builder.section = section;
        return builder;
    }

    @NonNull
    public static SendPresentShortLinkBuilder chooseUserWithSelectedPresent(@NonNull String presentId) {
        SendPresentShortLinkBuilder builder = new SendPresentShortLinkBuilder();
        builder.presentId = presentId;
        return builder;
    }

    @NonNull
    public static SendPresentShortLinkBuilder sendPresent(@NonNull String userId, @NonNull String presentId) {
        SendPresentShortLinkBuilder builder = new SendPresentShortLinkBuilder();
        builder.userId = userId;
        builder.presentId = presentId;
        return builder;
    }

    @NonNull
    public SendPresentShortLinkBuilder setUser(@Nullable String userId) {
        this.userId = userId;
        return this;
    }

    @NonNull
    public SendPresentShortLinkBuilder setPresent(@Nullable String presentId) {
        this.presentId = presentId;
        return this;
    }

    @NonNull
    public SendPresentShortLinkBuilder setHoliday(@Nullable String holidayId) {
        this.holidayId = holidayId;
        return this;
    }

    @NonNull
    public SendPresentShortLinkBuilder setToken(@Nullable String token) {
        this.token = token;
        return this;
    }

    @NonNull
    public SendPresentShortLinkBuilder setOrigin(@Nullable String origin) {
        this.token = origin;
        return this;
    }

    @NonNull
    public SendPresentShortLinkBuilder setResult(@Nullable String result) {
        this.result = result;
        return this;
    }

    @NonNull
    public SendPresentShortLinkBuilder setSection(@Nullable String section) {
        this.section = section;
        return this;
    }

    @Nullable
    public String getUserId() {
        return this.userId;
    }

    @Nullable
    public String getPresentId() {
        return this.presentId;
    }

    @Nullable
    public String getHolidayId() {
        return this.holidayId;
    }

    @Nullable
    public String getToken() {
        return this.token;
    }

    @NonNull
    public String build() {
        Builder builder = Uri.parse(JsonSessionTransportProvider.getInstance().getWebBaseUrl()).buildUpon().appendPath("sendPresent");
        if (this.presentId != null) {
            builder.appendPath(Utils.getXoredIdSafe(this.presentId));
        }
        if (this.userId != null) {
            builder.appendPath("user");
            builder.appendPath(Utils.getXoredIdSafe(this.userId));
        }
        if (this.section != null) {
            builder.appendPath("section");
            builder.appendPath(this.section);
        }
        if (this.holidayId != null) {
            builder.appendQueryParameter("st.hldId", xorHoliday(this.holidayId));
        }
        if (this.token != null) {
            builder.appendQueryParameter("st.or", this.token);
        }
        if (this.result != null) {
            builder.appendQueryParameter("st.rslt", this.result);
            builder.appendQueryParameter("st.mode", "ntf");
        }
        return builder.toString();
    }

    protected SendPresentShortLinkBuilder(Parcel in) {
        this.userId = in.readString();
        this.presentId = in.readString();
        this.holidayId = in.readString();
        this.token = in.readString();
        this.result = in.readString();
        this.section = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.presentId);
        dest.writeString(this.holidayId);
        dest.writeString(this.token);
        dest.writeString(this.result);
        dest.writeString(this.section);
    }

    @NonNull
    private static String xorHoliday(@NonNull String holidayId) {
        if (holidayId.equals(String.valueOf(265224201205L))) {
            return "0";
        }
        return Utils.getXoredIdSafe(holidayId);
    }

    static {
        CREATOR = new C03401();
    }
}
