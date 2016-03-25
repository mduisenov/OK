package ru.ok.android.db.access.fillers;

import android.content.ContentValues;
import ru.ok.java.api.request.groups.GroupInfoRequest.FIELDS;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.GroupInfo;
import ru.ok.model.GroupSubCategory;

public enum GroupInfoValueFiller implements BaseValuesFiller<GroupInfo> {
    ALL {
        public void fillValues(ContentValues cv, GroupInfo groupInfo) {
            cv.put("g_id", groupInfo.getId());
            cv.put("g_descr", groupInfo.getDescription());
            cv.put("g_name", groupInfo.getName());
            cv.put("g_mmbr_cnt", Integer.valueOf(groupInfo.getMembersCount()));
            cv.put("g_avatar_url", groupInfo.getPicUrl());
            cv.put("g_flags", Integer.valueOf(groupInfo.getFlags()));
            cv.put("g_photo_id", groupInfo.getPhotoId());
            cv.put("g_big_photo_url", groupInfo.getBigPicUrl());
            cv.put("g_admin_uid", groupInfo.getAdminUid());
            cv.put("g_created", Long.valueOf(groupInfo.getCreatedMs()));
            cv.put("g_category", Integer.valueOf(groupInfo.getType().categoryId));
            if (groupInfo.getAddress() != null) {
                cv.put("g_city", groupInfo.getAddress().city);
                cv.put("g_address", groupInfo.getAddress().street);
            }
            if (groupInfo.getLocation() != null) {
                cv.put("g_lat", Double.valueOf(groupInfo.getLocation().getLatitude()));
                cv.put("g_lng", Double.valueOf(groupInfo.getLocation().getLongitude()));
            }
            cv.put("g_scope", groupInfo.getScope());
            cv.put("g_start_date", Long.valueOf(groupInfo.getStartDate()));
            cv.put("g_end_date", Long.valueOf(groupInfo.getEndDate()));
            cv.put("g_home_page_url", groupInfo.getWebUrl());
            cv.put("g_phone", groupInfo.getPhone());
            cv.put("g_business", Integer.valueOf(groupInfo.isBusiness() ? 1 : 0));
            GroupSubCategory subCategory = groupInfo.getSubCategory();
            if (subCategory != null) {
                cv.put("g_subcategory_id", subCategory.getId());
                cv.put("g_subcategory_name", subCategory.getName());
            }
            cv.put("is_all_info_available", Integer.valueOf(1));
            cv.put("g_status", groupInfo.getStatus());
        }

        public String getRequestFields() {
            return new RequestFieldsBuilder().addFields(FIELDS.GROUP_ID, FIELDS.GROUP_NAME, FIELDS.GROUP_DESCRIPTION, FIELDS.GROUP_PIC_AVATAR, FIELDS.GROUP_PHOTO, FIELDS.GROUP_PHOTO_ID, FIELDS.GROUP_ADMIN_ID, FIELDS.GROUP_PREMIUM, FIELDS.GROUP_PRIVATE, FIELDS.GROUP_MAIN_PHOTO, FIELDS.GROUP_CREATED, FIELDS.GROUP_INVITE_ALLOWED, FIELDS.GROUP_THEME_ALLOWED, FIELDS.GROUP_SUGGEST_THEME_ALLOWED, FIELDS.GROUP_PUBLISH_DELAYED_THEME_ALLOWED, FIELDS.GROUP_CATEGORY, FIELDS.GROUP_LOCATION_LNG, FIELDS.GROUP_LOCATION_LAT, FIELDS.GROUP_ADDRESS, FIELDS.GROUP_COUNTRY, FIELDS.GROUP_CITY, FIELDS.GROUP_SCOPE, FIELDS.GROUP_START_DATE, FIELDS.GROUP_END_DATE, FIELDS.GROUP_BUSINESS, FIELDS.GROUP_PHONE, FIELDS.GROUP_WEB_URL, FIELDS.GROUP_SUBCATEGORY, FIELDS.GROUP_STATUS).build();
        }
    };
}
