package ru.ok.android.ui.messaging.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.PopupWindow;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.services.app.IntentUtils;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NavigationHelper.Tag;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;

public final class MessagingPromptController implements OnClickListener {
    final Activity activity;
    private PopupWindow popupWindow;

    MessagingPromptController(Activity activity) {
        this.activity = activity;
    }

    void showIfNeeded() {
        Window window = this.activity.getWindow();
        if (window == null) {
            Logger.m184w("Impossible: activity window is null");
        } else if (this.popupWindow != null && this.popupWindow.isShowing()) {
            Logger.m184w("Popup already showing");
        } else if (Settings.getLongValueInvariable(this.activity, "messaging-prompt-show-time", 0) != 0) {
            Logger.m172d("We have record about show time, do nothing...");
        } else {
            Logger.m172d("Show install messaging shortcut window");
            View rootView = LocalizationManager.inflate(this.activity, 2130903357, null, false);
            this.popupWindow = new PopupWindow(rootView, -1, -2);
            if (VERSION.SDK_INT >= 22) {
                this.popupWindow.setAttachedInDecor(true);
            }
            this.popupWindow.showAtLocation(window.getDecorView(), 80, 0, 0);
            rootView.findViewById(2131625154).setOnClickListener(this);
            rootView.findViewById(C0263R.id.cancel).setOnClickListener(this);
            StatisticManager.getInstance().addStatisticEvent("messaging-shortcut-prompt-show", new Pair[0]);
        }
    }

    void hide() {
        if (hidePopup()) {
            logHide("view-destroyed");
        }
    }

    boolean handleBack() {
        if (!hidePopup()) {
            return false;
        }
        logHide("back");
        return true;
    }

    private static void logHide(String reason) {
        StatisticManager.getInstance().addStatisticEvent("messaging-shortcut-prompt-hide", new Pair("reason", reason));
    }

    public void onClick(View v) {
        boolean storeResult = false;
        if (v.getId() == 2131625154) {
            logAdd("banner");
            installShortcut();
            storeResult = true;
        } else if (v.getId() == C0263R.id.cancel) {
            logHide("cancel");
        }
        Settings.storeBoolValueInvariable(this.activity, "messaging-prompt-is-added", storeResult);
        hidePopup();
        Settings.storeLongValueInvariable(this.activity, "messaging-prompt-show-time", System.currentTimeMillis());
    }

    void installShortcut() {
        installShortcut(this.activity);
    }

    public static void installShortcut(Context context) {
        if (context != null) {
            Drawable drawable = context.getResources().getDrawable(2130838018);
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                Intent intent = NavigationHelper.createIntentForTag(context, Tag.conversation);
                intent.putExtra("source-shortcut", true);
                IntentUtils.installShortcut(context, LocalizationManager.getString(context, 2131166311), bitmap, intent);
                return;
            }
            Logger.m184w("Icon drawable is not bitmap drawable :|");
        }
    }

    private boolean hidePopup() {
        if (this.popupWindow == null || !this.popupWindow.isShowing()) {
            return false;
        }
        this.popupWindow.dismiss();
        this.popupWindow = null;
        return true;
    }

    public static void logAdd(String source) {
        StatisticManager.getInstance().addStatisticEvent("messaging-shortcut-added", new Pair("source", source));
    }
}
