package ru.ok.java.api.request.registration;

import android.text.TextUtils;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasSessionKey = false, hasTargetUrl = false, hasUserId = false)
public final class UpdateUserInfoRequest extends BaseRequest {
    private String birthday;
    private String city;
    private String country;
    private String firstName;
    private String gender;
    private String lastName;

    public UpdateUserInfoRequest(String birthday, String country, String city, String firstName, String lastName, int gender) {
        this.birthday = birthday;
        if (TextUtils.isEmpty(city)) {
            city = " ";
        }
        this.city = city;
        if (TextUtils.isEmpty(country)) {
            country = " ";
        }
        this.country = country;
        if (TextUtils.isEmpty(firstName)) {
            firstName = " ";
        }
        this.firstName = firstName;
        if (TextUtils.isEmpty(lastName)) {
            lastName = " ";
        }
        this.lastName = lastName;
        this.gender = String.valueOf(gender);
    }

    public String getMethodName() {
        return "users.setProfileData";
    }

    protected void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME).add(SerializeParamName.COUNTRY, this.country).add(SerializeParamName.CITY, this.city).add(SerializeParamName.FIRST_NAME, this.firstName).add(SerializeParamName.LAST_NAME, this.lastName).add(SerializeParamName.GENDER, this.gender);
        if (!TextUtils.isEmpty(this.birthday)) {
            serializer.add(SerializeParamName.BIRTHDAY, this.birthday);
        }
    }
}
