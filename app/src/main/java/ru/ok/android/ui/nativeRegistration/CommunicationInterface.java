package ru.ok.android.ui.nativeRegistration;

import java.util.ArrayList;
import ru.ok.android.model.UpdateProfileFieldsFlags;
import ru.ok.android.model.UserWithLogin;
import ru.ok.android.services.processors.registration.Location;
import ru.ok.android.utils.CountryUtil.Country;
import ru.ok.model.UserInfo;

public interface CommunicationInterface {
    void goBackToRegistration();

    void goToCheckPhone(String str, String str2, long j, boolean z, long j2);

    void goToCheckPhone(String str, String str2, String str3, boolean z, long j);

    void goToCheckPhoneForRecovery(String str, String str2, long j);

    void goToEnterNewLogin(String str, String str2);

    void goToEnterPassword(String str);

    void goToExistingUser(UserWithLogin userWithLogin, String str, String str2, String str3);

    void goToFaq();

    void goToFeedback();

    void goToNewLogin();

    void goToOdklActivity();

    void goToRecoverPassword(UserWithLogin userWithLogin, String str);

    void goToRegainUser(UserWithLogin userWithLogin, String str, String str2);

    void goToRegistration();

    void goToUpdateUserInfo();

    void goToUpdateUserInfo(String str, ArrayList<Location> arrayList, UpdateProfileFieldsFlags updateProfileFieldsFlags);

    void goToUpdateUserInfo(String str, ArrayList<Location> arrayList, UserInfo userInfo, UpdateProfileFieldsFlags updateProfileFieldsFlags);

    void goToUserList(String str, String str2, String str3, ArrayList<UserWithLogin> arrayList, boolean z);

    void storeCountry(Country country);
}
