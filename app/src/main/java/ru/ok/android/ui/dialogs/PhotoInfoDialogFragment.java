package ru.ok.android.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.fragments.image.PhotoAlbumsHelper;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.photo.PhotoInfo;

public class PhotoInfoDialogFragment extends DialogFragment {
    private PhotoAlbumInfo albumInfo;
    private TextView albumView;
    private TextView dateView;
    private GroupInfo groupInfo;
    private TextView groupView;
    private int linkColor;
    private TextView ownerView;
    private PhotoInfo photoInfo;
    private UserInfo userInfo;

    /* renamed from: ru.ok.android.ui.dialogs.PhotoInfoDialogFragment.1 */
    class C07781 implements OnClickListener {
        C07781() {
        }

        public void onClick(View view) {
            PhotoInfoDialogFragment.this.dismiss();
            ((PhotoInfoDialogListener) PhotoInfoDialogFragment.this.getActivity()).onOwnerInfoClicked(PhotoInfoDialogFragment.this.userInfo);
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.PhotoInfoDialogFragment.2 */
    class C07792 implements OnClickListener {
        C07792() {
        }

        public void onClick(View view) {
            PhotoInfoDialogFragment.this.dismiss();
            ((PhotoInfoDialogListener) PhotoInfoDialogFragment.this.getActivity()).onGroupInfoClicked(PhotoInfoDialogFragment.this.groupInfo);
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.PhotoInfoDialogFragment.3 */
    class C07803 implements OnClickListener {
        C07803() {
        }

        public void onClick(View view) {
            PhotoInfoDialogFragment.this.dismiss();
            ((PhotoInfoDialogListener) PhotoInfoDialogFragment.this.getActivity()).onAlbumInfoClicked(PhotoInfoDialogFragment.this.albumInfo);
        }
    }

    public interface PhotoInfoDialogListener {
        void onAlbumInfoClicked(PhotoAlbumInfo photoAlbumInfo);

        void onGroupInfoClicked(GroupInfo groupInfo);

        void onOwnerInfoClicked(UserInfo userInfo);
    }

    public static PhotoInfoDialogFragment newInstance(PhotoAlbumInfo albumInfo, UserInfo userInfo, GroupInfo groupInfo, PhotoInfo photoInfo) {
        PhotoInfoDialogFragment fragment = new PhotoInfoDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("phalbm", albumInfo);
        args.putParcelable("usr", userInfo);
        args.putParcelable("grp", groupInfo);
        args.putParcelable("pht", photoInfo);
        fragment.setArguments(args);
        return fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        populateDataFromArguments();
        View rootView = LocalizationManager.inflate(getActivity(), 2130903160, null, false);
        this.ownerView = (TextView) rootView.findViewById(2131624782);
        this.ownerView.setOnClickListener(new C07781());
        this.groupView = (TextView) rootView.findViewById(2131624777);
        this.groupView.setOnClickListener(new C07792());
        this.albumView = (TextView) rootView.findViewById(2131624776);
        this.albumView.setOnClickListener(new C07803());
        this.dateView = (TextView) rootView.findViewById(2131624541);
        this.linkColor = getActivity().getResources().getColor(2131493013);
        fillViews();
        Builder builder = new Builder(getActivity());
        builder.setPositiveButton(LocalizationManager.getString(getActivity(), 2131165595), null);
        builder.setView(rootView);
        return builder.create();
    }

    private void populateDataFromArguments() {
        this.userInfo = (UserInfo) getArguments().getParcelable("usr");
        this.groupInfo = (GroupInfo) getArguments().getParcelable("grp");
        this.photoInfo = (PhotoInfo) getArguments().getParcelable("pht");
        this.albumInfo = (PhotoAlbumInfo) getArguments().getParcelable("phalbm");
        if (this.albumInfo == null && this.groupInfo == null) {
            this.albumInfo = PhotoAlbumsHelper.createEmptyAlbum(null, LocalizationManager.getString(getActivity(), 2131166341));
            this.albumInfo.setOwnerType(OwnerType.USER);
            this.albumInfo.setUserId(this.userInfo.uid);
        }
    }

    private void fillViews() {
        if (!(this.albumInfo == null || TextUtils.isEmpty(this.albumInfo.getTitle()))) {
            setText(this.albumView, 2131165369, this.albumInfo.getTitle());
            this.albumView.setVisibility(0);
        }
        if (this.userInfo != null) {
            setText(this.ownerView, 2131165422, this.userInfo.getConcatName());
            this.ownerView.setVisibility(0);
        }
        if (this.groupInfo != null) {
            setText(this.groupView, 2131165276, this.groupInfo.getName());
            this.groupView.setVisibility(0);
        }
        if (this.photoInfo != null) {
            this.dateView.setText(LocalizationManager.getString(this.dateView.getContext(), 2131166344));
            this.dateView.append(" ");
            this.dateView.append(DateFormatter.getPhotoTimeString(getActivity(), this.photoInfo.getCreatedMs()));
            this.dateView.setVisibility(0);
        }
    }

    private void setText(TextView where, int prefixId, CharSequence linkText) {
        SpannableString spannable = new SpannableString(linkText);
        spannable.setSpan(new ForegroundColorSpan(this.linkColor), 0, linkText.length(), 17);
        where.setText(LocalizationManager.getString(getActivity(), prefixId));
        where.append(" ");
        where.append(spannable);
    }
}
