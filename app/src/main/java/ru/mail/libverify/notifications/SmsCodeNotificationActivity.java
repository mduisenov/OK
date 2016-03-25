package ru.mail.libverify.notifications;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import ru.mail.libverify.C0176R;
import ru.mail.libverify.api.C0184q.C0182a;
import ru.mail.libverify.api.VerificationFactory;
import ru.mail.libverify.notifications.d.b;
import ru.mail.libverify.notifications.d.c;
import ru.mail.libverify.utils.C0204d;
import ru.mail.libverify.utils.C0205j;

public class SmsCodeNotificationActivity extends AppCompatActivity implements C0188b {
    private String f27a;
    private String f28b;
    private AlertDialog f29c;
    private Drawable f30d;
    private boolean f31e;

    /* renamed from: ru.mail.libverify.notifications.SmsCodeNotificationActivity.1 */
    class C01901 implements OnClickListener {
        final /* synthetic */ SmsCodeNotificationActivity f23a;

        C01901(SmsCodeNotificationActivity smsCodeNotificationActivity) {
            this.f23a = smsCodeNotificationActivity;
        }

        public final void onClick(DialogInterface dialogInterface, int i) {
            try {
                Context context = this.f23a;
                new b(context, "action_confirm").a("notification_id", this.f23a.f27a).a().send();
            } catch (Throwable e) {
                C0204d.m130a("SmsCodeActivity", "failed to confirm notification", e);
            }
            this.f23a.finish();
        }
    }

    /* renamed from: ru.mail.libverify.notifications.SmsCodeNotificationActivity.2 */
    class C01912 implements OnClickListener {
        final /* synthetic */ SmsCodeNotificationActivity f24a;

        C01912(SmsCodeNotificationActivity smsCodeNotificationActivity) {
            this.f24a = smsCodeNotificationActivity;
        }

        public final void onClick(DialogInterface dialogInterface, int i) {
            try {
                Context context = this.f24a;
                new b(context, "action_cancel").a("notification_id", this.f24a.f27a).a().send();
            } catch (Throwable e) {
                C0204d.m130a("SmsCodeActivity", "failed to confirm notification", e);
            }
            this.f24a.finish();
        }
    }

    /* renamed from: ru.mail.libverify.notifications.SmsCodeNotificationActivity.3 */
    class C01923 implements OnClickListener {
        final /* synthetic */ SmsCodeNotificationActivity f25a;

        C01923(SmsCodeNotificationActivity smsCodeNotificationActivity) {
            this.f25a = smsCodeNotificationActivity;
        }

        public final void onClick(DialogInterface dialogInterface, int i) {
            try {
                Context context = this.f25a;
                new c(context).a("notification_id", this.f25a.f27a).a().send();
            } catch (Throwable e) {
                C0204d.m130a("SmsCodeActivity", "failed to open settings", e);
            }
            this.f25a.finish();
        }
    }

    /* renamed from: ru.mail.libverify.notifications.SmsCodeNotificationActivity.4 */
    class C01934 implements OnDismissListener {
        final /* synthetic */ SmsCodeNotificationActivity f26a;

        C01934(SmsCodeNotificationActivity smsCodeNotificationActivity) {
            this.f26a = smsCodeNotificationActivity;
        }

        public final void onDismiss(DialogInterface dialogInterface) {
            this.f26a.finish();
        }
    }

    public SmsCodeNotificationActivity() {
        this.f31e = false;
    }

    public final void m78a(C0182a c0182a) {
        if (c0182a == null || !TextUtils.equals(c0182a.f14f, this.f27a)) {
            C0204d.m131a("SmsCodeNotificationActivity", "no such notification with id %s", this.f27a);
            finish();
        } else if (this.f31e) {
            C0204d.m137b("SmsCodeNotificationActivity", "activity with id %s has been already deactivated", this.f27a);
        } else {
            this.f28b = c0182a.f10b;
            C0204d.m141c("SmsCodeNotificationActivity", "build dialog for notification %s", c0182a);
            Builder builder = new Builder(this);
            builder.setTitle(c0182a.f10b);
            if (this.f30d == null) {
                this.f30d = DrawableCompat.wrap(getResources().getDrawable(C0176R.drawable.libverify_ic_sms_white));
                DrawableCompat.setTint(this.f30d, getResources().getColor(C0176R.color.libverify_secondary_icon_color));
            }
            builder.setIcon(this.f30d);
            CharSequence charSequence = c0182a.f9a;
            CharSequence charSequence2 = c0182a.f11c;
            if (!TextUtils.isEmpty(c0182a.f15g)) {
                charSequence = String.format("%s\n%s", new Object[]{charSequence, c0182a.f15g});
            }
            if (TextUtils.isEmpty(c0182a.f11c)) {
                charSequence2 = getString(C0176R.string.notification_event_confirm);
            }
            builder.setMessage(charSequence);
            if (c0182a.f12d.booleanValue()) {
                builder.setPositiveButton(charSequence2, new C01901(this));
            }
            builder.setNegativeButton(getString(C0176R.string.notification_event_cancel), new C01912(this));
            builder.setNeutralButton(getString(C0176R.string.notification_settings), new C01923(this));
            AlertDialog create = builder.create();
            create.setOnDismissListener(new C01934(this));
            this.f29c = create;
            this.f29c.show();
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0176R.layout.activity_sms_code_notification);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        C0204d.m141c("SmsCodeNotificationActivity", "create with %s", C0205j.m144a(intent.getExtras()));
        this.f27a = intent.getStringExtra("notification_id");
        if (TextUtils.isEmpty(this.f27a)) {
            finish();
            return;
        }
        VerificationFactory.getUIControlsApi(this).m59f(this.f27a);
        VerificationFactory.getUIControlsApi(this).m56a(this.f27a, new C0194a(this));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return super.onOptionsItemSelected(menuItem);
    }

    protected void onPause() {
        C0195f.m80a(this, C0176R.drawable.libverify_ic_sms_white, this.f28b);
        super.onPause();
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
        this.f31e = true;
        if (this.f29c != null) {
            this.f29c.dismiss();
        }
    }
}
