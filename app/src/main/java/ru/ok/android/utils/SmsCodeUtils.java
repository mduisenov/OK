package ru.ok.android.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.regex.Pattern;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.app.OdnoklassnikiApplication;

public final class SmsCodeUtils {
    private static final Pattern endPattern;
    private static Handler handler;
    private static BroadcastReceiver smsCodeReceiver;
    private static SmsObserver smsObserver;

    public interface SmsCodeReceiverListener {
        void onCodeReceived(String str);
    }

    /* renamed from: ru.ok.android.utils.SmsCodeUtils.1 */
    static class C14261 implements Runnable {
        final /* synthetic */ String val$code;
        final /* synthetic */ SmsCodeReceiverListener val$smsCodeReceiverListener;

        C14261(SmsCodeReceiverListener smsCodeReceiverListener, String str) {
            this.val$smsCodeReceiverListener = smsCodeReceiverListener;
            this.val$code = str;
        }

        public void run() {
            this.val$smsCodeReceiverListener.onCodeReceived(this.val$code);
        }
    }

    private static class SmsObserver extends ContentObserver {
        private long lastTime;
        SmsCodeReceiverListener smsCodeReceiverListener;

        public SmsObserver(SmsCodeReceiverListener smsCodeReceiverListener) {
            super(null);
            this.smsCodeReceiverListener = smsCodeReceiverListener;
        }

        public void onChange(boolean selfChange) {
            this.lastTime = System.currentTimeMillis() - 2000;
            if (PermissionUtils.checkSelfPermission(OdnoklassnikiApplication.getContext(), "android.permission.READ_SMS") == 0) {
                Uri inboxURI = Uri.parse("content://sms/");
                String[] reqCols = new String[]{"date", Message.BODY};
                try {
                    Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(inboxURI, reqCols, "date > ? AND type LIKE ?", new String[]{String.valueOf(this.lastTime), "1"}, "date DESC");
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            if (cursor.isFirst()) {
                                this.lastTime = cursor.getLong(cursor.getColumnIndex("date"));
                            }
                            SmsCodeUtils.readSms(cursor.getString(cursor.getColumnIndex(Message.BODY)), this.smsCodeReceiverListener);
                        }
                        cursor.close();
                    }
                } catch (SQLiteException e) {
                }
            }
        }
    }

    private static class SmsReceiver extends BroadcastReceiver {
        SmsCodeReceiverListener smsCodeReceiverListener;

        public SmsReceiver(SmsCodeReceiverListener smsCodeReceiverListener) {
            this.smsCodeReceiverListener = smsCodeReceiverListener;
        }

        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                try {
                    Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (Object aPdusObj : arr$) {
                        SmsCodeUtils.readSms(SmsMessage.createFromPdu((byte[]) aPdusObj).getDisplayMessageBody(), this.smsCodeReceiverListener);
                    }
                } catch (Exception e) {
                    Log.e("SmsReceiver", "Exception smsReceiver" + e);
                }
            }
        }
    }

    static {
        endPattern = Pattern.compile("^.+\\sOK\\.RU$", 32);
    }

    public static void registerSmsCodeReceiver(Context context, SmsCodeReceiverListener smsCodeReceiverListener) {
        handler = new Handler();
        if (PermissionUtils.checkSelfPermission(context, "android.permission.RECEIVE_SMS") == 0) {
            smsCodeReceiver = new SmsReceiver(smsCodeReceiverListener);
            context.registerReceiver(smsCodeReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
            return;
        }
        if (PermissionUtils.checkSelfPermission(context, "android.permission.READ_SMS") == 0) {
            smsObserver = new SmsObserver(smsCodeReceiverListener);
            context.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);
        }
    }

    public static void unregisterSmsCodeReceiver(Context context) {
        if (smsObserver != null) {
            context.getContentResolver().unregisterContentObserver(smsObserver);
            smsObserver = null;
        }
        if (smsCodeReceiver != null) {
            context.unregisterReceiver(smsCodeReceiver);
            smsCodeReceiver = null;
        }
        handler = null;
    }

    private static void readSms(String body, SmsCodeReceiverListener smsCodeReceiverListener) {
        if (endPattern.matcher(body).find()) {
            int i = body.toLowerCase().indexOf("-");
            if (i > -1) {
                String code = body.substring(0, i - 1).trim();
                if (!StringUtils.isEmpty(code) && code.length() <= 8 && smsCodeReceiverListener != null && handler != null) {
                    handler.post(new C14261(smsCodeReceiverListener, code));
                }
            }
        }
    }
}
