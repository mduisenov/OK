package ru.mail.libverify.notifications;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import ru.mail.libverify.C0176R;
import ru.mail.libverify.api.C0184q.C0182a;
import ru.mail.libverify.api.VerificationFactory;
import ru.mail.libverify.utils.C0204d;
import ru.mail.libverify.utils.C0205j;

public class SettingsActivity extends AppCompatActivity {

    /* renamed from: ru.mail.libverify.notifications.SettingsActivity.a */
    public static class C0189a extends PreferenceFragment implements C0188b {
        private String f20a;
        private String f21b;
        private String f22c;

        static /* synthetic */ AlertDialog m72a(C0189a c0189a) {
            Context activity = c0189a.getActivity();
            if (activity == null) {
                return null;
            }
            Builder builder = new Builder(activity);
            builder.setTitle(c0189a.f21b);
            Drawable wrap = DrawableCompat.wrap(c0189a.getResources().getDrawable(C0176R.drawable.libverify_ic_sms_white));
            DrawableCompat.setTint(wrap, c0189a.getResources().getColor(C0176R.color.libverify_secondary_icon_color));
            builder.setIcon(wrap);
            builder.setMessage(String.format(c0189a.getResources().getString(C0176R.string.report_reuse_text_confirmation), new Object[]{c0189a.f20a}));
            builder.setPositiveButton(c0189a.getString(C0176R.string.notification_event_confirm), new 3(c0189a));
            builder.setNegativeButton(c0189a.getString(C0176R.string.notification_event_cancel), new 4(c0189a));
            return builder.create();
        }

        private void m73a() {
            if (this.f22c == null) {
                this.f22c = getArguments().getString("notification_id");
                if (TextUtils.isEmpty(this.f22c)) {
                    getActivity().finish();
                } else {
                    VerificationFactory.getUIControlsApi(getActivity()).m56a(this.f22c, new C0194a(this));
                }
            }
        }

        static /* synthetic */ boolean m74b(C0189a c0189a) {
            Context activity = c0189a.getActivity();
            if (activity == null) {
                return false;
            }
            activity.finish();
            Toast.makeText(activity, c0189a.getResources().getString(C0176R.string.setting_saved_toast_text), 1).show();
            return true;
        }

        public final void m76a(C0182a c0182a) {
            if (c0182a == null || getActivity() == null || !TextUtils.equals(c0182a.f14f, this.f22c)) {
                C0204d.m131a("SettingsActivity", "no such notification with id %s or activity has been finished", this.f22c);
                if (getActivity() != null) {
                    getActivity().finish();
                    return;
                }
                return;
            }
            VerificationFactory.getUIControlsApi(getActivity()).m58e(this.f22c);
            this.f20a = C0205j.m160g(c0182a.f13e);
            this.f21b = c0182a.f10b;
            addPreferencesFromResource(C0176R.xml.notification_settings);
            Preference findPreference = findPreference("preference_report_reuse");
            findPreference.setTitle(String.format(getResources().getString(C0176R.string.report_reuse_text), new Object[]{this.f20a}));
            findPreference.setOnPreferenceClickListener(new 1(this));
            findPreference("preference_block_notifications").setOnPreferenceClickListener(new 2(this));
            getActivity().setTitle(String.format("%s (%s)", new Object[]{getResources().getString(C0176R.string.title_activity_settings), this.f21b}));
        }

        public final void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            m73a();
        }

        public final void setArguments(Bundle bundle) {
            super.setArguments(bundle);
            m73a();
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getIntent() == null) {
            finish();
            return;
        }
        C0204d.m141c("SettingsActivity", "create with %s", C0205j.m144a(getIntent().getExtras()));
        Fragment c0189a = new C0189a();
        c0189a.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().replace(16908290, c0189a).commit();
        C0195f.m80a(this, C0176R.drawable.libverify_ic_sms_white, getResources().getString(C0176R.string.title_activity_settings));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return super.onOptionsItemSelected(menuItem);
    }
}
