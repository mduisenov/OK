package ru.mail.android.mytarget.ads;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import ru.mail.android.mytarget.core.providers.AbstractFPDataProvider;
import ru.mail.android.mytarget.core.utils.ArrayUtils;

public final class CustomParams extends AbstractFPDataProvider {
    private String[] emails;
    private int[] icqIds;
    private String[] okIds;
    private String[] vkIds;

    public void setMrgsAppId(String appId) {
        addParam("mrgs_app_id", appId);
    }

    public void setMrgsUserId(String userId) {
        addParam("mrgs_user_id", userId);
    }

    public void setMrgsId(String mrgsId) {
        addParam("mrgs_device_id", mrgsId);
    }

    public String getMrgsAppId() {
        return getParam("mrgs_app_id");
    }

    public String getMrgsUserId() {
        return getParam("mrgs_user_id");
    }

    public String getMrgsId() {
        return getParam("mrgs_device_id");
    }

    public void setEmail(String email) {
        this.emails = new String[]{email};
        addParam(NotificationCompat.CATEGORY_EMAIL, email);
    }

    public void setEmails(String[] emails) {
        this.emails = emails;
        addParam(NotificationCompat.CATEGORY_EMAIL, ArrayUtils.toString(emails));
    }

    public void setIcqId(int id) {
        this.icqIds = new int[]{id};
        addParam("icq_id", "" + id);
    }

    public void setIcqIds(int[] ids) {
        this.icqIds = ids;
        addParam("icq_id", ArrayUtils.toString(ids));
    }

    public void setOkId(String id) {
        this.okIds = new String[]{id};
        addParam("ok_id", id);
    }

    public void setOkIds(String[] ids) {
        this.okIds = ids;
        addParam("ok_id", ArrayUtils.toString(ids));
    }

    public void setVKId(String id) {
        this.vkIds = new String[]{id};
        addParam("vk_id", id);
    }

    public void setVKIds(String[] ids) {
        this.vkIds = ids;
        addParam("vk_id", ArrayUtils.toString(ids));
    }

    public String getEmail() {
        if (this.emails == null || this.emails.length <= 0) {
            return null;
        }
        return this.emails[0];
    }

    public String[] getEmails() {
        return this.emails;
    }

    public int getIcqId() {
        if (this.icqIds == null || this.icqIds.length <= 0) {
            return -1;
        }
        return this.icqIds[0];
    }

    public int[] getIcqIds() {
        return this.icqIds;
    }

    public String getOkId() {
        if (this.okIds == null || this.okIds.length <= 0) {
            return null;
        }
        return this.okIds[0];
    }

    public String[] getOkIds() {
        return this.okIds;
    }

    public String getVKId() {
        if (this.vkIds == null || this.vkIds.length <= 0) {
            return null;
        }
        return this.vkIds[0];
    }

    public String[] getVKIds() {
        return this.vkIds;
    }

    public void collectData(Context context) {
    }

    public void setLang(String lang) {
        addParam("lang", lang);
    }

    public String getLang(String lang) {
        return getParam("lang");
    }

    public void setGender(int gender) {
        addParam("g", String.valueOf(gender));
    }

    public int getGender() {
        int i = 0;
        String genderString = getParam("g");
        if (genderString != null) {
            try {
                i = Integer.parseInt(genderString);
            } catch (NumberFormatException e) {
            }
        }
        return i;
    }

    public void setAge(int age) {
        addParam("a", String.valueOf(age));
    }

    public int getAge() {
        int i = 0;
        String ageString = getParam("a");
        if (ageString != null) {
            try {
                i = Integer.parseInt(ageString);
            } catch (NumberFormatException e) {
            }
        }
        return i;
    }

    public void setCustomParam(String key, String value) {
        addParam(key, value);
    }

    public String getCustomParam(String key) {
        return getParam(key);
    }
}
