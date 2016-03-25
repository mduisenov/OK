package ru.ok.android.db.access.fillers;

import android.content.ContentValues;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Utils;
import ru.ok.java.api.request.users.UserInfoRequest.FIELDS;
import ru.ok.java.api.utils.DateUtils;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.Location;
import ru.ok.model.UserInfo.UserOnlineType;
import ru.ok.model.UserStatus;

public enum UserInfoValuesFiller implements BaseValuesFiller<UserInfo> {
    ALL {
        public void fillValues(ContentValues cv, UserInfo info) {
            int i;
            int i2 = 1;
            ONLINE.fillValues(cv, info);
            SET_A.fillValues(cv, info);
            STATUS.fillValues(cv, info);
            cv.put("age", Integer.valueOf(info.age));
            cv.put("photo_id", info.pid);
            cv.put("big_pic_url", info.bigPicUrl);
            cv.put("private", Integer.valueOf(info.privateProfile ? 1 : 0));
            String str = "premium";
            if (info.premiumProfile) {
                i = 1;
            } else {
                i = 0;
            }
            cv.put(str, Integer.valueOf(i));
            str = "invisible";
            if (info.hasServiceInvisible) {
                i = 1;
            } else {
                i = 0;
            }
            cv.put(str, Integer.valueOf(i));
            str = "show_lock";
            if (info.showLock) {
                i = 1;
            } else {
                i = 0;
            }
            cv.put(str, Integer.valueOf(i));
            cv.put("birthday", info.birthday == null ? null : DateUtils.getBirthdayFormat().format(info.birthday));
            String str2 = "can_vmail";
            if (!info.getAvailableVMail()) {
                i2 = 0;
            }
            cv.put(str2, Integer.valueOf(i2));
        }

        public String getRequestFields() {
            return ONLINE.getRequestFields() + ',' + SET_A.getRequestFields() + ',' + STATUS.getRequestFields() + ',' + new RequestFieldsBuilder().addFields(FIELDS.AGE, FIELDS.PHOTO_ID, FIELDS.BIG_PIC, FIELDS.PRIVATE, FIELDS.PREMIUM, FIELDS.HAS_INVISIBLE, FIELDS.BIRTHDAY, FIELDS.SHOW_LOCK).build();
        }
    },
    ALL_FOR_PROFILE {
        public void fillValues(ContentValues cv, UserInfo info) {
            ALL.fillValues(cv, info);
            LOCATION.fillValues(cv, info);
            cv.put("is_all_info_available", Integer.valueOf(1));
        }

        public String getRequestFields() {
            return ALL.getRequestFields() + "," + LOCATION.getRequestFields() + ',' + new RequestFieldsBuilder().addFields(FIELDS.RELATIONSHIP, FIELDS.RELATIONSHIP_ALL).build();
        }
    },
    ONLINE {
        public void fillValues(ContentValues cv, UserInfo info) {
            NAMES.fillValues(cv, info);
            cv.put("user_last_online", Long.valueOf(info.lastOnline));
            cv.put("user_online", info.online.name());
            cv.put("user_avatar_url", info.picUrl);
        }

        public String getRequestFields() {
            return NAMES.getRequestFields() + ',' + new RequestFieldsBuilder().addFields(FIELDS.LAST_ONLINE, FIELDS.ONLINE, DeviceUtils.getUserAvatarPicFieldName()).build();
        }
    },
    ONLINE_ONLY {
        public void fillValues(ContentValues cv, UserInfo info) {
            int i;
            int i2 = 1;
            cv.put("user_online", info.online.name());
            String str = "user_can_call";
            if (info.getAvailableCall()) {
                i = 1;
            } else {
                i = 0;
            }
            cv.put(str, Integer.valueOf(i));
            String str2 = "can_vmail";
            if (!info.getAvailableVMail()) {
                i2 = 0;
            }
            cv.put(str2, Integer.valueOf(i2));
            cv.put("user_last_online", Long.valueOf(info.lastOnline));
            cv.put("user_id", info.uid);
        }

        public String getRequestFields() {
            return new RequestFieldsBuilder().addField(FIELDS.UID).addField(FIELDS.ONLINE).addField(FIELDS.CAN_VIDEO_CALL).addField(FIELDS.CAN_VIDEO_MAIL).addField(FIELDS.LAST_ONLINE).build();
        }
    },
    NAMES {
        public void fillValues(ContentValues cv, UserInfo info) {
            cv.put("user_id", info.uid);
            cv.put("user_first_name", info.firstName);
            cv.put("user_last_name", info.lastName);
            cv.put("user_name", info.name);
        }

        public String getRequestFields() {
            return new RequestFieldsBuilder().addFields(FIELDS.FIRST_NAME, FIELDS.LAST_NAME, FIELDS.NAME).build();
        }
    },
    CONVERSATIONS_LIST {
        public void fillValues(ContentValues cv, UserInfo info) {
            NAMES.fillValues(cv, info);
            SET_A.fillValues(cv, info);
        }

        public String getRequestFields() {
            return NAMES.getRequestFields() + ',' + SET_A.getRequestFields();
        }
    },
    MESSAGES {
        public void fillValues(ContentValues cv, UserInfo info) {
            ONLINE.fillValues(cv, info);
            if (info.genderType != null) {
                cv.put("user_gender", Integer.valueOf(info.genderType.toInteger()));
            }
            cv.put("user_id", info.uid);
        }

        public String getRequestFields() {
            return ONLINE.getRequestFields() + ',' + new RequestFieldsBuilder().addFields(FIELDS.GENDER).build();
        }
    },
    STREAM {
        public void fillValues(ContentValues cv, UserInfo info) {
            NAMES.fillValues(cv, info);
            if (info.genderType != null) {
                cv.put("user_gender", Integer.valueOf(info.genderType.toInteger()));
            }
            cv.put("user_avatar_url", info.picUrl);
            cv.put("age", Integer.valueOf(info.age));
            Location location = info.location;
            if (location != null) {
                cv.put("location_city", location.city);
                cv.put("location_code", location.countryCode);
                return;
            }
            cv.put("location_city", (String) null);
            cv.put("location_code", (String) null);
        }

        public String getRequestFields() {
            return NAMES.getRequestFields() + ',' + new RequestFieldsBuilder().addFields(FIELDS.GENDER, FIELDS.AGE, FIELDS.LOCATION, FIELDS.CITY, FIELDS.COUNTRY, DeviceUtils.getUserAvatarPicFieldName()).build();
        }
    },
    SET_A {
        public void fillValues(ContentValues cv, UserInfo info) {
            int i;
            int i2 = 1;
            if (info.genderType != null) {
                cv.put("user_gender", Integer.valueOf(info.genderType.toInteger()));
            }
            cv.put("user_avatar_url", info.picUrl);
            String str = "user_can_call";
            if (Utils.userCanCall(info)) {
                i = 1;
            } else {
                i = 0;
            }
            cv.put(str, Integer.valueOf(i));
            String str2 = "can_vmail";
            if (!Utils.canSendVideoMailTo(info)) {
                i2 = 0;
            }
            cv.put(str2, Integer.valueOf(i2));
            UserOnlineType onlineType = info.online;
            if (onlineType != null) {
                cv.put("user_online", onlineType.name());
            } else {
                cv.put("user_online", (String) null);
            }
        }

        public String getRequestFields() {
            return new RequestFieldsBuilder().addFields(FIELDS.ONLINE, DeviceUtils.getUserAvatarPicFieldName(), FIELDS.GENDER, FIELDS.CAN_VIDEO_CALL, FIELDS.CAN_VIDEO_MAIL, FIELDS.LAST_ONLINE).build();
        }
    },
    STATUS {
        public void fillValues(ContentValues cv, UserInfo info) {
            UserStatus status = info.status;
            cv.put("user_id", info.uid);
            if (status != null) {
                cv.put("status_id", status.id);
                cv.put("status_text", status.text);
                cv.put("status_date", Long.valueOf(status.date));
                cv.put("status_track_id", Long.valueOf(status.trackId));
                return;
            }
            cv.put("status_id", (String) null);
            cv.put("status_text", (String) null);
            cv.put("status_date", (String) null);
            cv.put("status_track_id", (String) null);
        }

        public String getRequestFields() {
            return new RequestFieldsBuilder().addFields(FIELDS.STATUS_ID, FIELDS.STATUS_DATE, FIELDS.STATUS, FIELDS.STATUS_TRACK_ID).build();
        }
    },
    LOCATION {
        public void fillValues(ContentValues cv, UserInfo info) {
            Location location = info.location;
            if (location != null) {
                cv.put("location_city", location.city);
                cv.put("location_code", location.countryCode);
                cv.put("location_country", location.country);
                return;
            }
            cv.put("location_city", (String) null);
            cv.put("location_code", (String) null);
            cv.put("location_country", (String) null);
        }

        public String getRequestFields() {
            return new RequestFieldsBuilder().addFields(FIELDS.LOCATION).build();
        }
    },
    MUTUAL_FRIENDS {
        public void fillValues(ContentValues cv, UserInfo info) {
            NAMES.fillValues(cv, info);
            cv.put("user_gender", Integer.valueOf(info.genderType.toInteger()));
            cv.put("user_avatar_url", info.picUrl);
        }

        public String getRequestFields() {
            return NAMES.getRequestFields() + ',' + new RequestFieldsBuilder().addFields(DeviceUtils.getUserAvatarPicFieldName(), FIELDS.GENDER).build();
        }
    },
    GUESTS {
        public void fillValues(ContentValues cv, UserInfo info) {
            NAMES.fillValues(cv, info);
            cv.put("user_gender", Integer.valueOf(info.genderType.toInteger()));
            cv.put("user_avatar_url", info.picUrl);
            cv.put("user_last_online", Long.valueOf(info.lastOnline));
            cv.put("user_can_call", Boolean.valueOf(info.getAvailableCall()));
            cv.put("can_vmail", Boolean.valueOf(info.getAvailableVMail()));
        }

        public String getRequestFields() {
            return NAMES.getRequestFields() + ',' + new RequestFieldsBuilder().addFields(DeviceUtils.getUserAvatarPicFieldName(), FIELDS.GENDER, FIELDS.ONLINE, FIELDS.LAST_ONLINE, FIELDS.CAN_VIDEO_CALL, FIELDS.CAN_VIDEO_MAIL);
        }
    },
    FRIENDS {
        public void fillValues(ContentValues cv, UserInfo info) {
            int i;
            int i2 = 1;
            ONLINE.fillValues(cv, info);
            cv.put("user_gender", Integer.valueOf(info.genderType.toInteger()));
            cv.put("user_can_call", Boolean.valueOf(info.getAvailableCall()));
            cv.put("can_vmail", Boolean.valueOf(info.getAvailableVMail()));
            String str = "private";
            if (info.privateProfile) {
                i = 1;
            } else {
                i = 0;
            }
            cv.put(str, Integer.valueOf(i));
            String str2 = "show_lock";
            if (!info.showLock) {
                i2 = 0;
            }
            cv.put(str2, Integer.valueOf(i2));
            cv.put("big_pic_url", info.bigPicUrl);
        }

        public String getRequestFields() {
            return ONLINE.getRequestFields() + ',' + new RequestFieldsBuilder().addFields(DeviceUtils.getUserAvatarPicFieldName(), FIELDS.GENDER, FIELDS.CAN_VIDEO_CALL, FIELDS.CAN_VIDEO_MAIL, FIELDS.PRIVATE, FIELDS.BIG_PIC, FIELDS.SHOW_LOCK);
        }
    },
    DISCUSSIONS {
        public void fillValues(ContentValues cv, UserInfo info) {
            GUESTS.fillValues(cv, info);
        }

        public String getRequestFields() {
            return GUESTS.getRequestFields();
        }
    },
    MUSIC {
        public void fillValues(ContentValues cv, UserInfo info) {
            NAMES.fillValues(cv, info);
            cv.put("user_gender", Integer.valueOf(info.genderType.toInteger()));
            cv.put("user_avatar_url", info.picUrl);
        }

        public String getRequestFields() {
            return NAMES.getRequestFields() + ',' + new RequestFieldsBuilder().addFields(DeviceUtils.getUserAvatarPicFieldName(), FIELDS.GENDER);
        }
    };
}
