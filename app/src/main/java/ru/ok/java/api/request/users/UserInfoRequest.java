package ru.ok.java.api.request.users;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.param.BaseRequestParam;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.utils.Constants.Api;
import ru.ok.java.api.utils.fields.RequestField;

public final class UserInfoRequest extends BaseRequest {
    private final boolean emptyPictures;
    private final String fields;
    private final boolean setOnline;
    private final BaseRequestParam uids;

    public enum FIELDS implements RequestField {
        UID("uid"),
        FIRST_NAME("first_name"),
        LAST_NAME("last_name"),
        NAME("name"),
        GENDER("gender"),
        BIRTHDAY("birthday"),
        AGE("age"),
        LOCALE("locale"),
        LOCATION("location"),
        CITY("city"),
        COUNTRY("country"),
        CURRENT_LOCATION("current_location"),
        LATITUDE("latitude"),
        LONGITUDE("longitude"),
        CURRENT_STATUS("current_status"),
        CURRENT_STATUS_ID("current_status_id"),
        CURRENT_STATUS_DATE("current_status_date"),
        ONLINE("online"),
        PIC_50x50("pic50x50"),
        PIC_190x190("pic190x190"),
        PIC_224x224("pic224x224"),
        PIC_288x288("pic288x288"),
        PIC_600x600("pic600x600"),
        PIC_240("pic240min"),
        PIC_320("pic320min"),
        PIC_128("pic128min"),
        PIC_MDPI("pic128x128"),
        PIC_HDPI("pic190x190"),
        PIC_XHDPI("pic240min"),
        PIC_XXHDPI("pic320min"),
        BIG_PIC("pic_full"),
        URL_PROFILE("url_profile"),
        URL_PROFILE_MOBILE("url_profile_mobile"),
        URL_CHAT("url_chat"),
        URL_CHAT_MOBILE("url_chat_mobile"),
        HAS_EMAIL("has_email"),
        ALLOWS_ANONYM_ACCESS("allows_anonym_access"),
        CAN_VIDEO_CALL("can_vcall"),
        CAN_VIDEO_MAIL("can_vmail"),
        PHOTO_ID("photo_id"),
        PRIVATE("private"),
        PREMIUM("premium"),
        LAST_ONLINE("last_online_ms"),
        HAS_INVISIBLE("has_service_invisible"),
        STATUS_ID("current_status_id"),
        STATUS("current_status"),
        STATUS_DATE("current_status_date_ms"),
        STATUS_TRACK_ID("current_status_track_id"),
        RELATIONSHIP("relationship"),
        SHOW_LOCK("show_lock"),
        RELATIONSHIP_ALL("relationship.*");
        
        private String name;

        private FIELDS(String name) {
            this.name = name;
        }

        public final String getName() {
            return this.name;
        }
    }

    public UserInfoRequest(BaseRequestParam uids, String fields, boolean emptyPictures, boolean setOnline) {
        this.uids = uids;
        this.fields = fields;
        this.emptyPictures = emptyPictures;
        this.setOnline = setOnline;
    }

    public UserInfoRequest(BaseRequestParam uids, String fields, boolean emptyPictures) {
        this(uids, fields, emptyPictures, true);
    }

    public String getMethodName() {
        return "users.getInfo";
    }

    public boolean isMakeUserOnline() {
        return this.setOnline;
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.USERS_IDS, this.uids).add(SerializeParamName.FIELDS, this.fields).add(SerializeParamName.CLIENT, Api.CLIENT_NAME).add(SerializeParamName.EMPTY_PICTURES, String.valueOf(this.emptyPictures));
    }

    public String toString() {
        return "UserInfoRequest{uids=" + this.uids + '}';
    }
}
