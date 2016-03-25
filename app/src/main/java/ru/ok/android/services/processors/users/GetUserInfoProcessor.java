package ru.ok.android.services.processors.users;

import java.util.List;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.json.users.JsonGetUsersInfoParser;
import ru.ok.java.api.request.users.UserInfoRequest.FIELDS;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.UserInfo;

public class GetUserInfoProcessor {
    public static List<UserInfo> processGetUserInfoResult(JsonHttpResult result) throws ResultParsingException {
        return new JsonGetUsersInfoParser(result).parse();
    }

    public static String getFieldsString() {
        return new RequestFieldsBuilder().addFields(FIELDS.FIRST_NAME, FIELDS.LAST_NAME, FIELDS.ONLINE, DeviceUtils.getUserAvatarPicFieldName(), FIELDS.GENDER, FIELDS.CAN_VIDEO_CALL, FIELDS.CAN_VIDEO_MAIL, FIELDS.PHOTO_ID, FIELDS.BIG_PIC, FIELDS.PIC_600x600, FIELDS.AGE).build();
    }
}
