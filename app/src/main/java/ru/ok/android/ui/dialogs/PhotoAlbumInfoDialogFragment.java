package ru.ok.android.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.AccessType;

public class PhotoAlbumInfoDialogFragment extends DialogFragment {
    private static final SparseIntArray ACCESS_TYPE_NAME_MATCHER;
    private TextView accessTypesView;
    private View albumContainerView;
    private PhotoAlbumInfo albumInfo;
    private TextView albumPhotoCountView;
    private TextView albumView;
    private UserInfo authorInfo;
    private TextView authorView;
    private TextView creationDateView;
    private GroupInfo groupInfo;
    private TextView groupView;
    private TextView photoCountView;
    private PhotoOwner photoOwner;

    public interface PhotoAlbumInfoDialogListener {
        void onAuthorInfoClicked(UserInfo userInfo);

        void onGroupInfoClicked(GroupInfo groupInfo);
    }

    /* renamed from: ru.ok.android.ui.dialogs.PhotoAlbumInfoDialogFragment.1 */
    static class C07741 extends SparseIntArray {
        C07741() {
            put(AccessType.PUBLIC.ordinal(), 2131165315);
            put(AccessType.CLASSMATE.ordinal(), 2131165308);
            put(AccessType.CLOSE_FRIEND.ordinal(), 2131165309);
            put(AccessType.COLLEAGUE.ordinal(), 2131165310);
            put(AccessType.COMPANION_IN_ARMS.ordinal(), 2131165311);
            put(AccessType.COURSEMATE.ordinal(), 2131165312);
            put(AccessType.FRIENDS.ordinal(), 2131165313);
            put(AccessType.LOVE.ordinal(), 2131165314);
            put(AccessType.RELATIVE.ordinal(), 2131165316);
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.PhotoAlbumInfoDialogFragment.2 */
    class C07752 implements OnClickListener {
        C07752() {
        }

        public void onClick(View view) {
            PhotoAlbumInfoDialogFragment.this.dismiss();
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.PhotoAlbumInfoDialogFragment.3 */
    class C07763 implements OnClickListener {
        C07763() {
        }

        public void onClick(View view) {
            if (PhotoAlbumInfoDialogFragment.this.getParentFragment() != null) {
                UserInfo ownerInfo = PhotoAlbumInfoDialogFragment.this.authorInfo;
                if (ownerInfo == null && PhotoAlbumInfoDialogFragment.this.photoOwner.getType() == 0) {
                    ownerInfo = (UserInfo) PhotoAlbumInfoDialogFragment.this.photoOwner.getOwnerInfo();
                }
                if (ownerInfo != null) {
                    ((PhotoAlbumInfoDialogListener) PhotoAlbumInfoDialogFragment.this.getParentFragment()).onAuthorInfoClicked(ownerInfo);
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.PhotoAlbumInfoDialogFragment.4 */
    class C07774 implements OnClickListener {
        C07774() {
        }

        public void onClick(View view) {
            if (PhotoAlbumInfoDialogFragment.this.getParentFragment() != null) {
                ((PhotoAlbumInfoDialogListener) PhotoAlbumInfoDialogFragment.this.getParentFragment()).onGroupInfoClicked(PhotoAlbumInfoDialogFragment.this.groupInfo);
            }
        }
    }

    static {
        ACCESS_TYPE_NAME_MATCHER = new C07741();
    }

    public static final PhotoAlbumInfoDialogFragment createInstance(PhotoOwner photoOwner, PhotoAlbumInfo albumInfo, UserInfo authorInfo) {
        PhotoAlbumInfoDialogFragment fragment = new PhotoAlbumInfoDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("albmnfo", albumInfo);
        args.putParcelable("authornfo", authorInfo);
        args.putParcelable("phwner", photoOwner);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.albumInfo = (PhotoAlbumInfo) getArguments().getParcelable("albmnfo");
        this.authorInfo = (UserInfo) getArguments().getParcelable("authornfo");
        this.photoOwner = (PhotoOwner) getArguments().getParcelable("phwner");
        if (this.photoOwner.getType() == 1) {
            this.groupInfo = (GroupInfo) this.photoOwner.getOwnerInfo();
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        builder.setPositiveButton(LocalizationManager.getString(getActivity(), 2131165476), null);
        View rootView = LocalizationManager.inflate(getActivity(), 2130903159, null, false);
        builder.setView(rootView);
        Dialog dialog = builder.create();
        this.albumContainerView = rootView.findViewById(2131624774);
        this.albumContainerView.setOnClickListener(new C07752());
        this.albumView = (TextView) rootView.findViewById(2131624776);
        this.albumPhotoCountView = (TextView) rootView.findViewById(2131624775);
        this.authorView = (TextView) rootView.findViewById(2131624696);
        this.authorView.setOnClickListener(new C07763());
        this.groupView = (TextView) rootView.findViewById(2131624777);
        this.groupView.setOnClickListener(new C07774());
        this.photoCountView = (TextView) rootView.findViewById(2131624778);
        this.creationDateView = (TextView) rootView.findViewById(2131624779);
        this.accessTypesView = (TextView) rootView.findViewById(2131624780);
        updateViewsData();
        return dialog;
    }

    private void updateViewsData() {
        Context context = this.groupView.getContext();
        if (this.albumInfo != null) {
            this.albumView.setText(this.albumInfo.getTitle());
            this.albumView.setText(buildCompositeLine(context, LocalizationManager.getString(this.albumView.getContext(), 2131165369) + " ", this.albumInfo.getTitle()), BufferType.SPANNABLE);
            this.albumView.setVisibility(0);
        }
        if (this.photoOwner.getType() == 0) {
            this.groupView.setVisibility(8);
            this.authorView.setVisibility(0);
            UserInfo userInfo = (UserInfo) this.photoOwner.getOwnerInfo();
            if (userInfo != null) {
                this.authorView.setText(buildCompositeLine(context, LocalizationManager.getString(this.authorView.getContext(), 2131165422) + " ", userInfo.firstName + " " + userInfo.lastName), BufferType.SPANNABLE);
                this.authorView.setVisibility(0);
            }
        } else {
            if (this.authorInfo == null) {
                this.authorView.setVisibility(8);
            } else {
                this.authorView.setText(buildCompositeLine(context, LocalizationManager.getString(this.authorView.getContext(), 2131165422) + " ", this.authorInfo.firstName + " " + this.authorInfo.lastName), BufferType.SPANNABLE);
                this.authorView.setVisibility(0);
            }
            this.groupView.setVisibility(0);
            GroupInfo groupInfo = (GroupInfo) this.photoOwner.getOwnerInfo();
            if (groupInfo != null) {
                this.groupView.setText(buildCompositeLine(context, LocalizationManager.getString(this.groupView.getContext(), 2131165276) + " ", groupInfo.getName()), BufferType.SPANNABLE);
                this.groupView.setVisibility(0);
            }
        }
        this.photoCountView.setText(LocalizationManager.getString(context, 2131165286) + " " + this.albumInfo.getPhotoCount());
        this.albumPhotoCountView.setText(String.valueOf(this.albumInfo.getPhotoCount()));
        this.creationDateView.setText(LocalizationManager.getString(context, 2131165272) + " " + this.albumInfo.getCreated());
        if (this.albumInfo.getTypes() == null || this.albumInfo.getTypes().isEmpty()) {
            this.accessTypesView.setVisibility(8);
            return;
        }
        StringBuilder str = new StringBuilder(LocalizationManager.getString(this.accessTypesView.getContext(), 2131165317)).append(" ");
        if (this.albumInfo.getTypes().contains(AccessType.FRIENDS)) {
            str.append(getAccessTypeLocalizedName(this.accessTypesView.getContext(), AccessType.FRIENDS).toLowerCase());
        } else {
            boolean first = true;
            for (AccessType accessType : this.albumInfo.getTypes()) {
                if (first) {
                    first = false;
                } else {
                    str.append(", ");
                }
                str.append(getAccessTypeLocalizedName(this.accessTypesView.getContext(), accessType).toLowerCase());
            }
        }
        this.accessTypesView.setText(str.toString());
    }

    private CharSequence buildCompositeLine(Context context, CharSequence title, String value) {
        SpannableString spannable = new SpannableString(title + " " + value);
        spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(2131493013)), title.length(), spannable.length(), 18);
        return spannable;
    }

    private String getAccessTypeLocalizedName(Context context, AccessType accessType) {
        return LocalizationManager.getString(context, ACCESS_TYPE_NAME_MATCHER.get(accessType.ordinal()));
    }
}
