package ru.ok.android.utils.controls;

import android.content.Context;
import ru.ok.android.ui.PopupDialogsSyncUtils;
import ru.ok.android.ui.dialogs.WhatNewDialog;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.settings.Settings;

public class WhatNewControl {

    /* renamed from: ru.ok.android.utils.controls.WhatNewControl.1 */
    static class C14411 implements Runnable {
        final /* synthetic */ Context val$context;
        final /* synthetic */ int val$versionNumber;
        final /* synthetic */ int val$versionSave;

        C14411(Context context, int i, int i2) {
            this.val$context = context;
            this.val$versionNumber = i;
            this.val$versionSave = i2;
        }

        public void run() {
            Settings.storeIntValueInvariable(this.val$context, "app_code_version", this.val$versionNumber);
            if (this.val$versionSave < 174) {
                WhatNewControl.showWhatIsNew(this.val$context);
            }
        }
    }

    public static boolean testVersion(Context context) {
        int versionNumber = Utils.getVersionCode(context);
        int versionSave = Settings.getIntValueInvariable(context, "app_code_version", 0);
        if (versionNumber > versionSave) {
            return PopupDialogsSyncUtils.atomicCheckAndShow(context, new C14411(context, versionNumber, versionSave));
        }
        return false;
    }

    private static void showWhatIsNew(Context context) {
        new WhatNewDialog(context).show();
    }
}
