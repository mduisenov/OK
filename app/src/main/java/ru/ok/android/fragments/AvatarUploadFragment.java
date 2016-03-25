package ru.ok.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.model.image.ImageForUpload;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.ui.custom.UploadAvatarRoundedImageView;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.users.fragments.profiles.statistics.UserProfileStatisticsManager;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.onelog.registration.AvatarUploadEventFactory;
import ru.ok.onelog.registration.AvatarUploadEventSource;

public class AvatarUploadFragment extends BaseFragment implements OnClickListener {
    private UploadAvatarRoundedImageView avatarImageView;
    private View continueBtn;
    private TextView hintTitle;
    private View removeAvatarBtn;
    private View skipBtn;
    private View uploadBtn;
    private ImageForUpload uploadedImage;

    public interface OnAvatarUploadListener {
        void onAvatarRemoved(ImageForUpload imageForUpload);

        void onAvatarUploadFragmentClose();

        void onAvatarUploaded(ImageForUpload imageForUpload);
    }

    protected int getLayoutId() {
        return 2130903194;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.uploadedImage = (ImageForUpload) savedInstanceState.getParcelable("pic");
        } else {
            this.uploadedImage = (ImageForUpload) getArguments().getParcelable("pic");
        }
        View view = LocalizationManager.inflate(getContext(), getLayoutId(), container, false);
        this.avatarImageView = (UploadAvatarRoundedImageView) view.findViewById(2131624657);
        this.avatarImageView.setOnClickListener(this);
        this.removeAvatarBtn = view.findViewById(2131624833);
        this.removeAvatarBtn.setOnClickListener(this);
        this.uploadBtn = view.findViewById(2131624834);
        this.uploadBtn.setOnClickListener(this);
        this.skipBtn = view.findViewById(2131624691);
        this.skipBtn.setOnClickListener(this);
        this.continueBtn = view.findViewById(2131624690);
        this.continueBtn.setOnClickListener(this);
        this.hintTitle = (TextView) view.findViewById(2131624688);
        if (this.uploadedImage != null) {
            onAvatarUploaded();
            this.avatarImageView.setAvatar(this.uploadedImage.getUri().toString());
        }
        return view;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("pic", this.uploadedImage);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case 2131624657:
            case 2131624834:
                onUpload();
            case 2131624690:
            case 2131624691:
                onClose();
            case 2131624833:
                onRemove();
            default:
        }
    }

    private void onRemove() {
        this.continueBtn.setClickable(false);
        this.avatarImageView.showAvatarProgress();
        Bundle bundleInput = new Bundle();
        bundleInput.putString("pid", this.uploadedImage.getRemoteId());
        bundleInput.putString("aid", this.uploadedImage.getAlbumInfo().getId());
        bundleInput.putString("oid", OdnoklassnikiApplication.getCurrentUser().getId());
        GlobalBus.send(2131623977, new BusEvent(bundleInput));
    }

    private void onClose() {
        ((OnAvatarUploadListener) getActivity()).onAvatarUploadFragmentClose();
    }

    private void onUpload() {
        NavigationHelper.startPhotoUploadSequence(getActivity(), null, 1, 2);
    }

    private void onAvatarUploaded() {
        this.removeAvatarBtn.setVisibility(0);
        this.uploadBtn.setClickable(true);
        this.skipBtn.setVisibility(8);
        this.uploadBtn.setVisibility(8);
        this.continueBtn.setVisibility(0);
        this.avatarImageView.setClickable(false);
        this.hintTitle.setText(LocalizationManager.getString(getContext(), 2131166408));
    }

    private void onAvatarRemoved() {
        this.avatarImageView.setPlaceholderVisibility(true);
        this.removeAvatarBtn.setVisibility(8);
        this.avatarImageView.setClickable(true);
        this.uploadedImage = null;
        this.skipBtn.setVisibility(0);
        this.uploadBtn.setVisibility(0);
        this.continueBtn.setVisibility(8);
        this.continueBtn.setClickable(true);
        this.hintTitle.setText(LocalizationManager.getString(getContext(), 2131165351));
    }

    private void onUploadInProgress() {
        this.avatarImageView.setPlaceholderVisibility(false);
        this.uploadBtn.setClickable(false);
        this.avatarImageView.showAvatarProgress();
    }

    @Subscribe(on = 2131623946, to = 2131624225)
    public void onImageUploaded(BusEvent event) {
        if (event.resultCode == 1) {
            this.uploadedImage = (ImageForUpload) event.bundleOutput.getParcelable("img");
            if (this.uploadedImage == null || this.uploadedImage.getCurrentStatus() != 5) {
                onUploadInProgress();
                return;
            }
            this.avatarImageView.hideAvatarProgress();
            if (this.uploadedImage.getUploadTarget() == 2) {
                OneLog.log(AvatarUploadEventFactory.get(AvatarUploadEventSource.profile_screen));
                ((OnAvatarUploadListener) getActivity()).onAvatarUploaded(this.uploadedImage);
                UserProfileStatisticsManager.sendStatEventForCurrentUser(UserProfileStatisticsManager.ACTION_UPLOAD_AVATAR);
                this.avatarImageView.setAvatar(this.uploadedImage.getUri().toString());
                onAvatarUploaded();
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624157)
    public void onPhotoDeleted(BusEvent event) {
        this.avatarImageView.hideAvatarProgress();
        if (event.resultCode == -1) {
            this.avatarImageView.clearAvatar();
            onAvatarRemoved();
            ((OnAvatarUploadListener) getActivity()).onAvatarRemoved(this.uploadedImage);
            return;
        }
        Toast.makeText(getContext(), getStringLocalized(2131165691), 1).show();
    }
}
