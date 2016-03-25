package ru.ok.android.ui.nativeRegistration;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.utils.controls.nativeregistration.RegistrationConstants;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public class DeleteUserDialog extends DialogFragment {

    /* renamed from: ru.ok.android.ui.nativeRegistration.DeleteUserDialog.1 */
    class C10761 implements OnClickListener {
        final /* synthetic */ UserInfo val$user;
        final /* synthetic */ int val$userItem;

        C10761(int i, UserInfo userInfo) {
            this.val$userItem = i;
            this.val$user = userInfo;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Fragment targetFragment = DeleteUserDialog.this.getTargetFragment();
            if (targetFragment != null && (targetFragment instanceof DeleteUserClickListener)) {
                ((DeleteUserClickListener) targetFragment).onDeleteUserClicked(this.val$userItem, this.val$user);
            }
            DeleteUserDialog.this.dismiss();
        }
    }

    public interface DeleteUserClickListener {
        void onDeleteUserClicked(int i, UserInfo userInfo);
    }

    public static DeleteUserDialog newInstance(UserInfo userInfo, int position, Fragment fragment) {
        DeleteUserDialog deleteUserDialog = new DeleteUserDialog();
        Bundle args = new Bundle();
        args.putParcelable("user_info", userInfo);
        args.putInt(RegistrationConstants.KEY_USER_ITEM, position);
        deleteUserDialog.setTargetFragment(fragment, 0);
        deleteUserDialog.setArguments(args);
        return deleteUserDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        boolean z;
        View v = LocalizationManager.inflate(getActivity(), 2130903143, null, false);
        int userItem = getArguments().getInt(RegistrationConstants.KEY_USER_ITEM);
        UserInfo user = (UserInfo) getArguments().getParcelable("user_info");
        ((TextView) v.findViewById(2131624676)).setText(user.firstName + " " + user.lastName);
        AvatarImageView avatar = (AvatarImageView) v.findViewById(2131624559);
        ImageViewManager instance = ImageViewManager.getInstance();
        String picUrl = user.getPicUrl();
        if (UserGenderType.MALE == user.genderType) {
            z = true;
        } else {
            z = false;
        }
        instance.displayImage(picUrl, avatar, z, null);
        return new Builder(getContext()).setView(v).setTitle(LocalizationManager.getString(getContext(), 2131166459)).setPositiveButton(LocalizationManager.getString(getContext(), 2131165671), new C10761(userItem, user)).setNegativeButton(LocalizationManager.getString(getContext(), 2131165697), null).create();
    }
}
