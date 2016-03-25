package ru.ok.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.io.Serializable;

public class Address implements Parcelable, Serializable {
    public static final Creator<Address> CREATOR;
    public static StringAddressStrategy DEFAULT_LANG_STRATEGY = null;
    private static final long serialVersionUID = 1;
    public String city;
    public String cityId;
    public String country;
    public String countryISO;
    public String house;
    public String street;

    /* renamed from: ru.ok.model.Address.1 */
    static class C15061 implements Creator<Address> {
        C15061() {
        }

        public Address createFromParcel(Parcel parcel) {
            return new Address(null);
        }

        public Address[] newArray(int count) {
            return new Address[count];
        }
    }

    public interface StringAddressStrategy {
        String createString(Address address);
    }

    public static class RuStringAddressStrategy implements StringAddressStrategy {
        public String createString(Address address) {
            StringBuilder builder = new StringBuilder();
            if (!TextUtils.isEmpty(address.street)) {
                append(builder, address.street);
            }
            if (!TextUtils.isEmpty(address.house)) {
                if (builder.length() > 0) {
                    builder.append(" ");
                }
                builder.append(address.house);
            }
            if (!TextUtils.isEmpty(address.city)) {
                append(builder, address.city);
            }
            if (!TextUtils.isEmpty(address.country)) {
                append(builder, address.country);
            }
            return builder.toString();
        }

        private void append(StringBuilder builder, String value) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(value);
        }
    }

    static {
        DEFAULT_LANG_STRATEGY = new RuStringAddressStrategy();
        CREATOR = new C15061();
    }

    public static Address createFromSystemAddress(android.location.Address sAddress) {
        Address address = new Address();
        address.city = sAddress.getLocality();
        address.countryISO = sAddress.getCountryCode();
        address.country = sAddress.getCountryName();
        address.house = sAddress.getFeatureName();
        address.street = sAddress.getThoroughfare();
        return address;
    }

    public Address(String country, String countryISO, String city, String street, String house, String cityId) {
        this.country = country;
        this.countryISO = countryISO;
        this.city = city;
        this.street = street;
        this.house = house;
        this.cityId = cityId;
    }

    public String getStringAddress() {
        return DEFAULT_LANG_STRATEGY.createString(this);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.country);
        parcel.writeString(this.countryISO);
        parcel.writeString(this.city);
        parcel.writeString(this.street);
        parcel.writeString(this.house);
        parcel.writeString(this.cityId);
    }

    private Address(Parcel parcel) {
        this.country = parcel.readString();
        this.countryISO = parcel.readString();
        this.city = parcel.readString();
        this.street = parcel.readString();
        this.house = parcel.readString();
        this.cityId = parcel.readString();
    }
}
