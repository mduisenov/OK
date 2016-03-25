package ru.ok.android.services.processors.registration;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.UpdateProfileFieldsFlags;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.registration.GetCountryListRequest;
import ru.ok.java.api.request.registration.ProfileFieldsFlagsRequest;

public class ProfileActivityProcessor extends CommandProcessor {
    public static final String COMMAND_NAME;
    public static final String KEY_FIELDS_FLAGS;
    public static final String KEY_LOCATION_LIST;
    public static final String KEY_TYPE_ERROR;
    public static final String KEY_TYPE_MESSAGE;

    static {
        COMMAND_NAME = ProfileActivityProcessor.class.getName();
        KEY_TYPE_MESSAGE = COMMAND_NAME + ":key_type_message";
        KEY_TYPE_ERROR = COMMAND_NAME + ":key_type_error";
        KEY_LOCATION_LIST = COMMAND_NAME + ":key_type_locations";
        KEY_FIELDS_FLAGS = COMMAND_NAME + ":key_type_fields_flags";
    }

    public ProfileActivityProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        return onPrepareProfileActivity(context, outBundle);
    }

    public static boolean isIt(String command) {
        return COMMAND_NAME.equals(command);
    }

    public static String commandName() {
        return COMMAND_NAME;
    }

    private int onPrepareProfileActivity(Context context, Bundle outBundle) {
        try {
            Bundle bundle = getData();
            outBundle.putParcelableArrayList(KEY_LOCATION_LIST, bundle.getParcelableArrayList(KEY_LOCATION_LIST));
            outBundle.putParcelable(KEY_FIELDS_FLAGS, bundle.getParcelable(KEY_FIELDS_FLAGS));
            return 1;
        } catch (Exception e) {
            Logger.m172d("Error " + e.getMessage());
            outBundle.clear();
            outBundle.putString("errorMessage", e.getMessage());
            CommandProcessor.fillErrorBundle(outBundle, e, true);
            return 2;
        }
    }

    private Bundle getData() throws BaseApiException, JSONException, NameNotFoundException {
        ArrayList<Location> locations = new ArrayList();
        Context context = OdnoklassnikiApplication.getContext();
        int packageVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        BatchRequests requests = new BatchRequests();
        BaseRequest countryListRequest = new GetCountryListRequest(Settings.getCurrentLocale(context));
        BaseRequest passwordVisibilityRequest = new ProfileFieldsFlagsRequest(packageVersionCode);
        requests.addRequest(countryListRequest);
        requests.addRequest(passwordVisibilityRequest);
        BaseRequest batchRequest = new BatchRequest(requests);
        JSONObject jsonObject = this._transportProvider.execJsonHttpMethod(batchRequest).getResultAsObject();
        JSONArray jsonArray = jsonObject.getJSONObject("system_getLocations_response").getJSONArray("countries");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject JSONLocation = jsonArray.getJSONObject(i);
            locations.add(new Location(JSONLocation.getString("name"), JSONLocation.getString("id"), JSONLocation.getString("code")));
        }
        UpdateProfileFieldsFlags updateProfileFieldsFlags = new UpdateProfileFieldsFlags();
        JSONObject fieldsKeys = jsonObject.optJSONObject("settings_get_response");
        if (fieldsKeys != null) {
            updateProfileFieldsFlags.isBirthdayRequired = fieldsKeys.optBoolean("users.setProfileData.birthdayRequired", false);
            updateProfileFieldsFlags.isFirstNameLastNameRequired = fieldsKeys.optBoolean("users.setProfileData.firstLastNameRequired", false);
            updateProfileFieldsFlags.isBackButtonDisabled = fieldsKeys.optBoolean("users.setProfileData.isBackButtonDisabled", false);
            updateProfileFieldsFlags.isAvatarVisible = fieldsKeys.optBoolean("registration.avatar.visible", false);
            updateProfileFieldsFlags.isAvatarSeparately = fieldsKeys.optBoolean("registration.avatar.separate", false);
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_FIELDS_FLAGS, updateProfileFieldsFlags);
        bundle.putParcelableArrayList(KEY_LOCATION_LIST, locations);
        return bundle;
    }
}
