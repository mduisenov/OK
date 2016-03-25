package ru.ok.android.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.telephony.TelephonyManager;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.utils.settings.Settings;

public class CountryUtil {
    private List<Country> cache;
    PhoneNumberUtil phoneUtil;

    /* renamed from: ru.ok.android.utils.CountryUtil.1 */
    class C14191 implements Comparator<Country> {
        C14191() {
        }

        public int compare(Country lhs, Country rhs) {
            return lhs.getDisplayName().compareTo(rhs.getDisplayName());
        }
    }

    public static class Country implements Parcelable {
        public static final Creator<Country> CREATOR;
        private String countryISO;
        private String displayName;
        private int zip;

        /* renamed from: ru.ok.android.utils.CountryUtil.Country.1 */
        static class C14201 implements Creator<Country> {
            C14201() {
            }

            public Country createFromParcel(Parcel parcel) {
                return new Country(parcel);
            }

            public Country[] newArray(int count) {
                return new Country[count];
            }
        }

        public Country(String displayName, int zip, String countryISO) {
            this.displayName = displayName;
            this.zip = zip;
            this.countryISO = countryISO;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public int getZip() {
            return this.zip;
        }

        public String getCountryISO() {
            return this.countryISO;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.displayName);
            dest.writeInt(this.zip);
            dest.writeString(this.countryISO);
        }

        public Country(Parcel parcel) {
            this.displayName = parcel.readString();
            this.zip = parcel.readInt();
            this.countryISO = parcel.readString();
        }

        static {
            CREATOR = new C14201();
        }
    }

    private static class CountryUtilWrapper {
        static CountryUtil INSTANCE;

        static {
            INSTANCE = new CountryUtil();
        }
    }

    private CountryUtil() {
        this.cache = new ArrayList();
        this.phoneUtil = PhoneNumberUtil.getInstance();
        Locale locale = new Locale(Settings.getCurrentLocale(OdnoklassnikiApplication.getContext()));
        for (String countryISO : Locale.getISOCountries()) {
            this.cache.add(new Country(new Locale("", countryISO).getDisplayName(locale), this.phoneUtil.getCountryCodeForRegion(countryISO), countryISO));
        }
        Collections.sort(this.cache, new C14191());
    }

    public static CountryUtil getInstance() {
        return CountryUtilWrapper.INSTANCE;
    }

    public Country getCountryByIso(String iso) {
        for (Country country : this.cache) {
            if (country.getCountryISO().toUpperCase().contains(iso.toUpperCase())) {
                return country;
            }
        }
        return null;
    }

    public Country getCountryByZip(int zip) {
        for (Country country : this.cache) {
            if (country.getZip() == zip) {
                return country;
            }
        }
        return null;
    }

    public List<Country> getCounties() {
        return this.cache;
    }

    public static String tryGetCurrentCountryCode() {
        TelephonyManager tMgr = (TelephonyManager) OdnoklassnikiApplication.getContext().getSystemService("phone");
        String countryID = StringUtils.safeToUpperCase(tMgr.getNetworkCountryIso());
        if (!StringUtils.isEmpty(countryID)) {
            return countryID;
        }
        countryID = StringUtils.safeToUpperCase(tMgr.getSimCountryIso());
        if (StringUtils.isEmpty(countryID)) {
            return null;
        }
        return countryID;
    }
}
