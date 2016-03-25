package ru.ok.android.ui.groups;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.custom.imageview.RoundedBitmapDrawable;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.bus.BusGroupsHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.groups.GroupCreateType;

public class GroupUtils {
    public static final GroupCreateType DEFAULT_GROUP_CREATE_TYPE;

    /* renamed from: ru.ok.android.ui.groups.GroupUtils.1 */
    static class C09081 extends ButtonCallback {
        private EditText descriptionEdit;
        private EditText nameEdit;
        private TextInputLayout nameInputLayout;
        final /* synthetic */ Context val$context;
        final /* synthetic */ GroupCreateRequestedListener val$listener;

        C09081(Context context, GroupCreateRequestedListener groupCreateRequestedListener) {
            this.val$context = context;
            this.val$listener = groupCreateRequestedListener;
        }

        public void onPositive(MaterialDialog dialog) {
            if (this.nameEdit == null) {
                this.nameEdit = (EditText) dialog.getCustomView().findViewById(2131624890);
            }
            String name = this.nameEdit.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                if (this.nameInputLayout == null) {
                    this.nameInputLayout = (TextInputLayout) dialog.getCustomView().findViewById(2131624889);
                }
                this.nameInputLayout.setError(LocalizationManager.getString(this.val$context, 2131165939));
                return;
            }
            if (this.descriptionEdit == null) {
                this.descriptionEdit = (EditText) dialog.getCustomView().findViewById(2131624892);
            }
            BusGroupsHelper.createGroup(GroupUtils.DEFAULT_GROUP_CREATE_TYPE, name, this.descriptionEdit.getText().toString().trim(), true);
            if (this.val$listener != null) {
                this.val$listener.onGroupCreateRequested();
            }
            dialog.dismiss();
        }

        public void onNegative(MaterialDialog dialog) {
            dialog.dismiss();
        }
    }

    /* renamed from: ru.ok.android.ui.groups.GroupUtils.2 */
    static class C09092 implements OnFocusChangeListener {
        final /* synthetic */ int val$colorActivated;
        final /* synthetic */ int val$colorNormal;

        C09092(int i, int i2) {
            this.val$colorActivated = i;
            this.val$colorNormal = i2;
        }

        public void onFocusChange(View v, boolean hasFocus) {
            v.getBackground().setColorFilter(hasFocus ? this.val$colorActivated : this.val$colorNormal, Mode.SRC_IN);
        }
    }

    public interface GroupCreateRequestedListener {
        void onGroupCreateRequested();
    }

    static {
        DEFAULT_GROUP_CREATE_TYPE = GroupCreateType.INTEREST;
    }

    public static void showCreateGroupDialog(Context context, GroupCreateRequestedListener listener) {
        new Builder(context).title(2131165936).customView(getCreateGroupDialogView(context), true).positiveText(2131166474).negativeText(2131165476).autoDismiss(false).callback(new C09081(context, listener)).build().show();
    }

    private static View getCreateGroupDialogView(Context context) {
        View view = LayoutInflater.from(context).inflate(2130903227, null);
        int colorNormal = context.getResources().getColor(2131492988);
        OnFocusChangeListener focusChangeListener = new C09092(context.getResources().getColor(2131493081), colorNormal);
        prepareEdit(view, 2131624890, colorNormal, focusChangeListener);
        prepareEdit(view, 2131624892, colorNormal, focusChangeListener);
        return view;
    }

    private static void prepareEdit(View view, int id, int colorNormal, OnFocusChangeListener focusChangeListener) {
        View edit = view.findViewById(id);
        edit.getBackground().setColorFilter(colorNormal, Mode.SRC_IN);
        edit.setOnFocusChangeListener(focusChangeListener);
    }

    public static void processCreateGroupFail(Context context, BusEvent event) {
        ErrorType errorType = ErrorType.from(event.bundleOutput);
        if (errorType == ErrorType.GENERAL) {
            Toast.makeText(context, 2131165937, 1).show();
        } else if (errorType == ErrorType.CENSOR_MATCH) {
            Toast.makeText(context, 2131165935, 1).show();
        } else if (errorType == ErrorType.INVALID_SYMBOLS) {
            Toast.makeText(context, 2131165938, 1).show();
        } else {
            Toast.makeText(context, errorType.getDefaultErrorMessage(), 1).show();
        }
    }

    public static int groupsVerticalItemsPageCount(Context context) {
        return groupsVerticalItemsPageCount(context, 80);
    }

    public static int groupsVerticalBigItemsPageCount(Context context) {
        return groupsVerticalItemsPageCount(context, AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR);
    }

    public static int groupsVerticalItemsPageCount(Context context, int itemMinHeight) {
        int bigSizeDp;
        int smallSizeDp;
        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.screenHeightDp > configuration.screenWidthDp) {
            bigSizeDp = configuration.screenHeightDp;
            smallSizeDp = configuration.screenWidthDp;
        } else {
            bigSizeDp = configuration.screenWidthDp;
            smallSizeDp = configuration.screenHeightDp;
        }
        int bigSizeCount = (bigSizeDp / itemMinHeight) + 1;
        int smallSizeCount = (smallSizeDp / itemMinHeight) + 1;
        if (DeviceUtils.isTablet(context)) {
            return bigSizeCount * 2;
        }
        return Math.max(bigSizeCount, smallSizeCount * 2);
    }

    public static int groupsHorizontalItemsPageCount(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return (Math.max(configuration.screenHeightDp, configuration.screenWidthDp) / 72) + 1;
    }

    public static RoundedBitmapDrawable getRoundedDrawable(Context context, @DrawableRes int drawableResId, int pixelSize) {
        Drawable drawable = context.getResources().getDrawable(drawableResId);
        Bitmap bitmap = Bitmap.createBitmap(pixelSize, pixelSize, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, pixelSize, pixelSize);
        drawable.draw(canvas);
        return new RoundedBitmapDrawable(bitmap, 0);
    }

    public static String shortenedCountString(long count) {
        if (count >= 1000000) {
            return (count / 1000000) + "M";
        }
        if (count >= 1000) {
            return (count / 1000) + "K";
        }
        return null;
    }
}
