package ru.ok.android.ui.dialogs.photo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;
import ru.mail.libverify.C0176R;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.photo.PhotoAlbumInfo.AccessType;

public class PhotoAlbumEditDialog extends DialogFragment {
    protected CheckBox accClassmatesView;
    protected CheckBox accCloseFriendsView;
    protected CheckBox accColleaguesView;
    protected CheckBox accCompanionsView;
    protected CheckBox accCoursematesView;
    protected CheckBox accFriendsView;
    protected CheckBox accLoveView;
    protected CheckBox accPublicView;
    protected CheckBox accRelativesView;
    protected View accessControlsView;
    protected CheckBox[] accessViews;
    protected EditText titleView;

    public interface PhotoAlbumDialogListener {
        void onAlbumEditSubmit(PhotoAlbumEditDialog photoAlbumEditDialog, CharSequence charSequence, List<AccessType> list);
    }

    /* renamed from: ru.ok.android.ui.dialogs.photo.PhotoAlbumEditDialog.1 */
    class C07931 implements OnClickListener {
        C07931() {
        }

        public void onClick(DialogInterface dialog, int which) {
            PhotoAlbumDialogListener listener = null;
            if (PhotoAlbumEditDialog.this.getActivity() != null) {
                if (PhotoAlbumEditDialog.this.getActivity() != null && PhotoAlbumDialogListener.class.isAssignableFrom(PhotoAlbumEditDialog.this.getActivity().getClass())) {
                    listener = (PhotoAlbumDialogListener) PhotoAlbumEditDialog.this.getActivity();
                } else if (PhotoAlbumEditDialog.this.getParentFragment() != null && PhotoAlbumDialogListener.class.isAssignableFrom(PhotoAlbumEditDialog.this.getParentFragment().getClass())) {
                    listener = (PhotoAlbumDialogListener) PhotoAlbumEditDialog.this.getParentFragment();
                }
                KeyBoardUtils.hideKeyBoard(PhotoAlbumEditDialog.this.titleView.getContext(), PhotoAlbumEditDialog.this.titleView.getWindowToken());
                listener.onAlbumEditSubmit(PhotoAlbumEditDialog.this, PhotoAlbumEditDialog.this.titleView.getText(), PhotoAlbumEditDialog.this.getSelectedAccessTypes());
            }
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.photo.PhotoAlbumEditDialog.2 */
    class C07942 implements OnClickListener {
        C07942() {
        }

        public void onClick(DialogInterface dialog, int which) {
            KeyBoardUtils.hideKeyBoard(PhotoAlbumEditDialog.this.titleView.getContext(), PhotoAlbumEditDialog.this.titleView.getWindowToken());
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.photo.PhotoAlbumEditDialog.3 */
    class C07953 implements OnCheckedChangeListener {
        C07953() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                for (CompoundButton accessView : PhotoAlbumEditDialog.this.accessViews) {
                    if (accessView != buttonView) {
                        accessView.setChecked(false);
                    }
                }
            }
            PhotoAlbumEditDialog.this.ensureUnselectableAccess();
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.photo.PhotoAlbumEditDialog.4 */
    class C07964 implements OnCheckedChangeListener {
        C07964() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                PhotoAlbumEditDialog.this.accPublicView.setChecked(false);
                for (CompoundButton accessView : PhotoAlbumEditDialog.this.accessViews) {
                    if (!(accessView == buttonView || accessView == PhotoAlbumEditDialog.this.accPublicView)) {
                        accessView.setChecked(true);
                    }
                }
            }
            PhotoAlbumEditDialog.this.ensureUnselectableAccess();
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.photo.PhotoAlbumEditDialog.5 */
    class C07975 implements OnCheckedChangeListener {
        C07975() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                PhotoAlbumEditDialog.this.accPublicView.setChecked(false);
            } else {
                PhotoAlbumEditDialog.this.accFriendsView.setChecked(false);
            }
            PhotoAlbumEditDialog.this.ensureUnselectableAccess();
        }
    }

    public static final class Builder {
        private List<AccessType> albumAccessTypes;
        private String albumTitle;
        private final Context context;
        private String dialogTitle;
        private boolean showAccessControls;
        private String submitBtnText;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setDialogTitle(int resId) {
            this.dialogTitle = LocalizationManager.getString(this.context, resId);
            return this;
        }

        public Builder setSubmitBtnText(int resId) {
            this.submitBtnText = LocalizationManager.getString(this.context, resId);
            return this;
        }

        public Builder setAlbumTitle(String albumTitle) {
            this.albumTitle = albumTitle;
            return this;
        }

        public Builder setAlbumAccessTypes(List<AccessType> accessTypes) {
            this.albumAccessTypes = accessTypes;
            return this;
        }

        public Builder setShowAccessControls(boolean showAccessControls) {
            this.showAccessControls = showAccessControls;
            return this;
        }

        public PhotoAlbumEditDialog build() {
            return PhotoAlbumEditDialog.newInstance(this.dialogTitle, this.submitBtnText, this.showAccessControls, this.albumTitle, this.albumAccessTypes);
        }

        public PhotoAlbumEditDialog show(FragmentManager fragmentManager, String tag) {
            PhotoAlbumEditDialog dialogFragment = build();
            dialogFragment.show(fragmentManager, tag);
            return dialogFragment;
        }
    }

    public PhotoAlbumEditDialog() {
        this.accessViews = new CheckBox[9];
    }

    public static PhotoAlbumEditDialog newInstance(String title, String submitBtnText, boolean showAccessControls, String defAlbumTitle, List<AccessType> defAlbumAccessTypes) {
        PhotoAlbumEditDialog fragment = new PhotoAlbumEditDialog();
        Bundle args = new Bundle();
        args.putBoolean("shwacc", showAccessControls);
        args.putString("albmttl", defAlbumTitle);
        args.putIntArray("acctpes", AccessType.asIntArray(defAlbumAccessTypes));
        args.putString("ttl", title);
        args.putString("sbmttxt", submitBtnText);
        fragment.setArguments(args);
        return fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LocalizationManager.inflate(getActivity(), 2130903153, null, false);
        prepareUI(rootView);
        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity());
        builder.setView(rootView);
        builder.setTitle(getArguments().getString("ttl"));
        builder.setPositiveButton(getArguments().getString("sbmttxt"), new C07931());
        builder.setNegativeButton(LocalizationManager.getString(getActivity(), 2131165476), new C07942());
        return builder.create();
    }

    private void prepareUI(View rootView) {
        int visibility = 0;
        this.titleView = (EditText) rootView.findViewById(C0176R.id.title);
        this.accessControlsView = rootView.findViewById(2131624762);
        this.accPublicView = findAndPrepareAccessView(rootView, 2131624763, new C07953(), 0);
        this.accFriendsView = findAndPrepareAccessView(rootView, 2131624764, new C07964(), 1);
        OnCheckedChangeListener accessChangeListener = new C07975();
        this.accRelativesView = findAndPrepareAccessView(rootView, 2131624765, accessChangeListener, 2);
        this.accLoveView = findAndPrepareAccessView(rootView, 2131624766, accessChangeListener, 3);
        this.accCloseFriendsView = findAndPrepareAccessView(rootView, 2131624767, accessChangeListener, 4);
        this.accColleaguesView = findAndPrepareAccessView(rootView, 2131624768, accessChangeListener, 5);
        this.accClassmatesView = findAndPrepareAccessView(rootView, 2131624769, accessChangeListener, 6);
        this.accCoursematesView = findAndPrepareAccessView(rootView, 2131624770, accessChangeListener, 7);
        this.accCompanionsView = findAndPrepareAccessView(rootView, 2131624771, accessChangeListener, 8);
        this.titleView.setText(getArguments().getString("albmttl"));
        boolean showAccessControls = getArguments().getBoolean("shwacc");
        if (!showAccessControls) {
            visibility = 8;
        }
        this.accessControlsView.setVisibility(visibility);
        if (showAccessControls) {
            updateAccessViewsState(getArguments().getIntArray("acctpes"));
            ensureUnselectableAccess();
        }
    }

    private CheckBox findAndPrepareAccessView(View rootView, int resId, OnCheckedChangeListener listener, int index) {
        CheckBox accessView = (CheckBox) rootView.findViewById(resId);
        accessView.setOnCheckedChangeListener(listener);
        this.accessViews[index] = accessView;
        return accessView;
    }

    private void updateAccessViewsState(int[] accessTypes) {
        if (accessTypes != null && accessTypes.length > 0) {
            this.accPublicView.setChecked(false);
            for (int accessType : accessTypes) {
                this.accessViews[accessType].setChecked(true);
            }
        }
    }

    protected void ensureUnselectableAccess() {
        CheckBox ensuredView = null;
        for (CheckBox accessView : this.accessViews) {
            if (!accessView.isChecked()) {
                accessView.setEnabled(true);
            } else if (ensuredView == null) {
                ensuredView = accessView;
                ensuredView.setEnabled(false);
            } else {
                ensuredView.setEnabled(true);
                accessView.setEnabled(true);
            }
        }
    }

    protected List<AccessType> getSelectedAccessTypes() {
        List<AccessType> accessTypes = new ArrayList();
        int size = this.accessViews.length;
        for (int i = 0; i < size; i++) {
            if (this.accessViews[i].isChecked()) {
                accessTypes.add(AccessType.values()[i]);
            }
        }
        return accessTypes;
    }
}
