package ru.ok.model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.support.v4.app.NotificationCompat;
import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class ContactInfo implements Serializable {
    private Integer birthDay;
    private Integer birthMonth;
    private Integer birthYear;
    private String city;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    public static String normalizePhone(String phone) {
        return phone.replaceAll("\\D+", "");
    }

    public static ContactInfo getContactInfoByCursor(ContentResolver contentResolver, Cursor cursor) {
        String firstName = null;
        String lastName = null;
        String phone = null;
        String email = null;
        String id = cursor.getString(cursor.getColumnIndex("_id"));
        int emailIndex = cursor.getColumnIndex("data1");
        if (emailIndex != -1) {
            email = cursor.getString(emailIndex);
        }
        String[] whereNameParams = new String[]{"vnd.android.cursor.item/name", id};
        ContentResolver contentResolver2 = contentResolver;
        Cursor nameCursor = contentResolver2.query(Data.CONTENT_URI, null, "mimetype = ? AND contact_id = ?", whereNameParams, "data2");
        if (nameCursor.moveToNext()) {
            firstName = nameCursor.getString(nameCursor.getColumnIndex("data2"));
            lastName = nameCursor.getString(nameCursor.getColumnIndex("data3"));
        }
        nameCursor.close();
        ContentResolver contentResolver3 = contentResolver;
        Cursor phoneCursor = contentResolver3.query(Phone.CONTENT_URI, null, "contact_id = ?", new String[]{id}, null);
        if (phoneCursor.moveToNext()) {
            phone = normalizePhone(phoneCursor.getString(phoneCursor.getColumnIndex("data1")));
        }
        phoneCursor.close();
        return new ContactInfo(firstName, lastName, email, phone);
    }

    public ContactInfo(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstName", this.firstName);
            jsonObject.put("lastName", this.lastName);
            jsonObject.put(NotificationCompat.CATEGORY_EMAIL, this.email);
            jsonObject.put("phone", this.phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String toString() {
        return getJsonObject().toString();
    }
}
