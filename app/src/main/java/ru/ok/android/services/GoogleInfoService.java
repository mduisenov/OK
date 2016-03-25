package ru.ok.android.services;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.utils.AccountEmailFinder;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.settings.Settings;
import ru.ok.android.web.WebHelper;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.Location;
import ru.ok.model.UserInfo.UserGenderType;

public class GoogleInfoService extends Service implements ConnectionCallbacks, OnConnectionFailedListener {
    private static GoogleApiClient googleApiClient;
    private static Thread googleInfoTask;
    private static UserInfo person;
    GoogleInfoServiceBinder binder;

    /* renamed from: ru.ok.android.services.GoogleInfoService.1 */
    class C04101 implements Runnable {
        C04101() {
        }

        public void run() {
            try {
                String email = AccountEmailFinder.getGoogleEmail(GoogleInfoService.this.getApplicationContext());
                if (!StringUtils.isEmpty(email)) {
                    String id = GoogleInfoService.this.getIdByEmail(email);
                    if (!StringUtils.isEmpty(id)) {
                        UserInfo result = GoogleInfoService.this.personFromJson(new JSONObject(GoogleInfoService.this.getUserInfoById(id, "AIzaSyBRQqt9dLAs0F-t72eJ-ReVif-yWL4MmcY")));
                        GoogleInfoService.person.genderType = result.genderType;
                        GoogleInfoService.person.firstName = result.firstName;
                        GoogleInfoService.person.lastName = result.lastName;
                        GoogleInfoService.person.setBirthday(result.birthday);
                    }
                }
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        }
    }

    public class GoogleInfoServiceBinder extends Binder {
    }

    public GoogleInfoService() {
        this.binder = new GoogleInfoServiceBinder();
    }

    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    public static UserInfo getUserInfo() {
        return person;
    }

    public void onCreate() {
        super.onCreate();
        person = new UserInfo(Settings.getCurrentUser(getApplicationContext()).getId());
        if (!AuthorizationPreferences.getGoogleInfoThroughOAuth()) {
            getDataFromGoogle();
        }
    }

    public void getDataFromGoogle() {
        googleInfoTask = new Thread(new C04101(), "GoogleInfoService");
        googleInfoTask.start();
    }

    private static Location getAddress(double lat, double lng) {
        Location location = null;
        try {
            List<Address> addresses = new Geocoder(OdnoklassnikiApplication.getContext(), Locale.getDefault()).getFromLocation(lat, lng, 1);
            if (!addresses.isEmpty()) {
                Address obj = (Address) addresses.get(0);
                location = new Location(obj.getCountryCode(), obj.getCountryName(), obj.getLocality());
            }
        } catch (Throwable e) {
            Logger.m178e(e);
        }
        if (location != null) {
            Logger.m173d("Location", location.city);
        } else {
            Logger.m173d("Location", "null");
        }
        return location;
    }

    private UserInfo personFromJson(JSONObject object) {
        UserInfo person = new UserInfo(Settings.getCurrentUser(getApplicationContext()).getId());
        person.genderType = object.optString("gender", "male").toUpperCase().equals("MALE") ? UserGenderType.MALE : UserGenderType.FEMALE;
        JSONObject name = object.optJSONObject("name");
        if (name != null) {
            person.firstName = name.optString("givenName", "");
            person.lastName = name.optString("familyName", "");
        }
        try {
            person.setBirthday(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(object.optString("birthday", "")));
        } catch (Throwable e) {
            Logger.m178e(e);
            person.birthday = null;
        }
        return person;
    }

    private String getUserInfoById(String id, String key) {
        return textFromUrl("https://www.googleapis.com/plus/v1/people/" + id + "?key=" + key);
    }

    private String getIdByEmail(String email) {
        return getIdFromText(textFromUrl("https://suggestqueries.google.com/complete/search?client=es-people-picker&q=" + email));
    }

    private String textFromUrl(String textUrl) {
        String result = "";
        try {
            return new String(WebHelper.performGet(getBaseContext(), textUrl), org.jivesoftware.smack.util.StringUtils.UTF8);
        } catch (Throwable e) {
            Logger.m178e(e);
            return result;
        }
    }

    private String getIdFromText(String userInfo) {
        String result = "";
        int startSymbol = userInfo.indexOf("\"a\":\"");
        if (startSymbol > -1) {
            for (int i = startSymbol + 5; userInfo.charAt(i) != '\"'; i++) {
                result = result + userInfo.charAt(i);
            }
        }
        return result;
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (person.location == null) {
            person.location = getSimLocation();
        }
        if (person.location == null || "".equals(person.location.countryCode)) {
            person.location = new Location(getCountryCodeFromLocale(), "", "");
        }
    }

    private static Location getSimLocation() {
        TelephonyManager tm = (TelephonyManager) OdnoklassnikiApplication.getContext().getSystemService("phone");
        if (tm != null) {
            String networkCountryIso = tm.getNetworkCountryIso();
            if (networkCountryIso != null && networkCountryIso.length() > 1) {
                return new Location(networkCountryIso.substring(0, 2), "", "");
            }
        }
        return null;
    }

    public void onConnected(Bundle bundle) {
        person.location = getLocation(googleApiClient);
    }

    public static Location getLocation(GoogleApiClient googleApiClient) {
        Location location = null;
        android.location.Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            location = getAddress(lastLocation.getLatitude(), lastLocation.getLongitude());
        }
        if (location == null) {
            location = getSimLocation();
        }
        if (location == null || "".equals(location.countryCode)) {
            return new Location(getCountryCodeFromLocale(), "", "");
        }
        return location;
    }

    public void onConnectionSuspended(int i) {
    }

    public void onDestroy() {
        if (googleInfoTask != null) {
            googleInfoTask.interrupt();
        }
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onDestroy();
    }

    private static String getCountryCodeFromLocale() {
        String code = Locale.getDefault().getISO3Country();
        if (code == null || code.length() <= 1) {
            return code;
        }
        return code.substring(0, 2);
    }
}
