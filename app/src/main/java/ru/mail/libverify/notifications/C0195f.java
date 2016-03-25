package ru.mail.libverify.notifications;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager.TaskDescription;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import ru.mail.libverify.C0176R;

/* renamed from: ru.mail.libverify.notifications.f */
final class C0195f {
    @TargetApi(21)
    static void m80a(@NonNull Activity activity, int i, String str) {
        activity.setTitle(str);
        if (VERSION.SDK_INT >= 21) {
            TypedValue typedValue = new TypedValue();
            activity.getTheme().resolveAttribute(C0176R.attr.colorPrimary, typedValue, true);
            int i2 = typedValue.data;
            Bitmap decodeResource = BitmapFactory.decodeResource(activity.getResources(), i);
            activity.setTaskDescription(new TaskDescription(str, decodeResource, i2));
            decodeResource.recycle();
        }
    }
}
