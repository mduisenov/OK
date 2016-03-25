package ru.ok.android.ui.custom.prefs;

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.DialogPreference;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import java.util.ArrayList;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.general.RingtoneProcessor;
import ru.ok.android.ui.settings.SettingsFragment;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.utils.ObjectUtils;

public class CustomRingtonePreference extends DialogPreference implements OnActivityResultListener {
    private Ringtone currentRingtone;
    private SettingsFragment owningFragment;
    private int requestCode;
    private Uri selectedUri;
    private ArrayList<Uri> uris;

    @TargetApi(21)
    public CustomRingtonePreference(Context context) {
        super(context);
        this.uris = new ArrayList();
    }

    public CustomRingtonePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.uris = new ArrayList();
    }

    public CustomRingtonePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.uris = new ArrayList();
    }

    @TargetApi(21)
    public CustomRingtonePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.uris = new ArrayList();
    }

    protected void persistUri(Uri uri) {
        persistString(uri != null ? uri.toString() : null);
    }

    protected Uri getPersistedUri() {
        String uriString = getPersistedString(null);
        if (TextUtils.isEmpty(uriString)) {
            return null;
        }
        return Uri.parse(uriString);
    }

    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);
        Uri systemUri = RingtoneManager.getDefaultUri(2);
        Uri okUri = RingtoneProcessor.getDefaultIncomingRingtoneUri(getContext());
        this.selectedUri = getPersistedUri();
        this.uris.clear();
        this.uris.add(null);
        this.uris.add(systemUri);
        this.uris.add(okUri);
        if (getCategory(this.selectedUri) == 3) {
            if (RingtoneManager.getRingtone(getContext(), this.selectedUri) != null) {
                this.uris.add(this.selectedUri);
            } else {
                this.selectedUri = okUri;
                persistUri(okUri);
            }
        }
        ArrayList<String> titles = new ArrayList();
        int selectedIndex = 0;
        int len = this.uris.size();
        for (int i = 0; i < len; i++) {
            Uri uri = (Uri) this.uris.get(i);
            titles.add(getItemLabel(uri));
            if (ObjectUtils.equals(uri, this.selectedUri)) {
                selectedIndex = i;
            }
        }
        titles.add(getStringLocalized(2131166293));
        builder.setSingleChoiceItems((CharSequence[]) titles.toArray(new String[titles.size()]), selectedIndex, this);
        builder.setPositiveButton(17039370, this);
        builder.setNegativeButton(17039360, this);
    }

    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        if (this.currentRingtone != null && this.currentRingtone.isPlaying()) {
            this.currentRingtone.stop();
        }
        if (which < 0) {
            if (which == -1) {
                if (callChangeListener(this.selectedUri != null ? this.selectedUri.toString() : "")) {
                    persistUri(this.selectedUri);
                    setSummary(getItemLabel(this.selectedUri));
                }
            }
        } else if (which >= this.uris.size()) {
            dialog.dismiss();
            this.owningFragment.startActivityForResult(new Intent("android.intent.action.RINGTONE_PICKER").putExtra("android.intent.extra.ringtone.EXISTING_URI", getPersistedUri()).putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false).putExtra("android.intent.extra.ringtone.SHOW_SILENT", false).putExtra("android.intent.extra.ringtone.TYPE", 2).putExtra("android.intent.extra.ringtone.TITLE", getTitle()), this.requestCode);
        } else {
            this.selectedUri = (Uri) this.uris.get(which);
            if (this.selectedUri != null) {
                this.currentRingtone = RingtoneManager.getRingtone(getContext(), this.selectedUri);
                this.currentRingtone.play();
            }
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != this.requestCode) {
            return false;
        }
        if (resultCode != -1) {
            showDialog(null);
        } else {
            Uri uri = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            if (callChangeListener(uri != null ? uri.toString() : "")) {
                persistUri(uri);
                setSummary(getItemLabel(uri));
            }
        }
        return true;
    }

    public void setOwningFragment(SettingsFragment owningFragment) {
        this.owningFragment = owningFragment;
        owningFragment.registerOnActivityResultListener(this);
        this.requestCode = owningFragment.getNextRequestCode();
    }

    public CharSequence getSummary() {
        if (RingtoneManager.getRingtone(getContext(), getPersistedUri()) == null) {
            persistUri(RingtoneProcessor.getDefaultIncomingRingtoneUri(getContext()));
        }
        return getItemLabel(getPersistedUri());
    }

    private String getItemLabel(Uri uri) {
        int category = getCategory(uri);
        switch (category) {
            case RecyclerView.NO_POSITION /*-1*/:
                return uri.getLastPathSegment();
            case RECEIVED_VALUE:
                return getStringLocalized(2131166291);
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return getStringLocalized(2131166303);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return getStringLocalized(2131166292);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return RingtoneManager.getRingtone(getContext(), uri).getTitle(getContext());
            default:
                throw new AssertionError("Illegal category " + category);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getCategory(android.net.Uri r6) {
        /*
        r5 = this;
        r0 = 1;
        r2 = 0;
        r1 = -1;
        if (r6 == 0) goto L_0x000d;
    L_0x0005:
        r3 = android.net.Uri.EMPTY;
        r3 = r6.equals(r3);
        if (r3 == 0) goto L_0x000f;
    L_0x000d:
        r0 = r2;
    L_0x000e:
        return r0;
    L_0x000f:
        r3 = r6.getScheme();
        r4 = r3.hashCode();
        switch(r4) {
            case -368816979: goto L_0x0020;
            case 951530617: goto L_0x002b;
            default: goto L_0x001a;
        };
    L_0x001a:
        r3 = r1;
    L_0x001b:
        switch(r3) {
            case 0: goto L_0x0036;
            case 1: goto L_0x0038;
            default: goto L_0x001e;
        };
    L_0x001e:
        r0 = r1;
        goto L_0x000e;
    L_0x0020:
        r4 = "android.resource";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x001a;
    L_0x0029:
        r3 = r2;
        goto L_0x001b;
    L_0x002b:
        r4 = "content";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x001a;
    L_0x0034:
        r3 = r0;
        goto L_0x001b;
    L_0x0036:
        r0 = 2;
        goto L_0x000e;
    L_0x0038:
        r3 = r6.getAuthority();
        r4 = r3.hashCode();
        switch(r4) {
            case 103772132: goto L_0x004a;
            case 1434631203: goto L_0x0054;
            default: goto L_0x0043;
        };
    L_0x0043:
        r2 = r1;
    L_0x0044:
        switch(r2) {
            case 0: goto L_0x0048;
            case 1: goto L_0x000e;
            default: goto L_0x0047;
        };
    L_0x0047:
        goto L_0x001e;
    L_0x0048:
        r0 = 3;
        goto L_0x000e;
    L_0x004a:
        r4 = "media";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x0043;
    L_0x0053:
        goto L_0x0044;
    L_0x0054:
        r2 = "settings";
        r2 = r3.equals(r2);
        if (r2 == 0) goto L_0x0043;
    L_0x005d:
        r2 = r0;
        goto L_0x0044;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.custom.prefs.CustomRingtonePreference.getCategory(android.net.Uri):int");
    }

    private String getStringLocalized(@StringRes int resId) {
        return LocalizationManager.getString(getContext(), resId);
    }
}
