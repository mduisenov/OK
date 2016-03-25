package ru.ok.android.ui.image;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.video.MediaInfo;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.ui.image.pick.GalleryImageInfo;
import ru.ok.android.ui.image.pick.PickFromCameraActivity;
import ru.ok.android.ui.image.pick.PickImagesActivity;
import ru.ok.android.utils.Func2;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.UserMedia;
import ru.ok.model.photo.PhotoAlbumInfo;

public class AddImagesActivity extends BaseActivity {
    private static final Func2<GalleryImageInfo, Boolean, ImageEditInfo> FUNC_EXTERNAL_IMAGE_TO_IMAGE_EDIT_INFO;
    private static final Func2<MediaInfo, Boolean, ImageEditInfo> FUNC_MEDIA_INFO_TO_IMAGE_EDIT_INFO;
    private boolean doUpload;
    private boolean editImages;
    private PhotoAlbumInfo mAlbumInfo;
    private int mChoiceMode;
    private boolean mInitCamera;
    private int mUploadTarget;
    private int maxCount;
    private boolean shouldMoveToBack;

    /* renamed from: ru.ok.android.ui.image.AddImagesActivity.1 */
    static class C09461 extends ArrayList<ImageEditInfo> {
        final /* synthetic */ GalleryImageInfo val$cameraImage;

        C09461(int x0, GalleryImageInfo galleryImageInfo) {
            this.val$cameraImage = galleryImageInfo;
            super(x0);
            add(AddImagesActivity.FUNC_EXTERNAL_IMAGE_TO_IMAGE_EDIT_INFO.apply(this.val$cameraImage, Boolean.valueOf(true)));
        }
    }

    /* renamed from: ru.ok.android.ui.image.AddImagesActivity.2 */
    static class C09472 implements Func2<MediaInfo, Boolean, ImageEditInfo> {
        C09472() {
        }

        public ImageEditInfo apply(MediaInfo mediaInfo, Boolean isTemporary) {
            return AddImagesActivity.toImageEditInfo(mediaInfo, isTemporary.booleanValue());
        }
    }

    /* renamed from: ru.ok.android.ui.image.AddImagesActivity.3 */
    static class C09483 implements Func2<GalleryImageInfo, Boolean, ImageEditInfo> {
        C09483() {
        }

        public ImageEditInfo apply(GalleryImageInfo externalImage, Boolean isTemporary) {
            return AddImagesActivity.toImageEditInfo(externalImage, isTemporary.booleanValue());
        }
    }

    private static class NextActivityInfo {
        final boolean finishPrevious;
        final Intent intent;
        final int requestCode;

        public NextActivityInfo(@NonNull Intent intent, int requestCode, boolean finishPrevious) {
            this.intent = intent;
            this.requestCode = requestCode;
            this.finishPrevious = finishPrevious;
        }
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        if (savedInstanceState != null) {
            restoreSavedState(savedInstanceState);
        } else if (!startLoginIfNeeded()) {
            proceedWithIntent(getIntent());
        }
    }

    public final void proceedWithIntent(Intent intent) {
        if (intent.getExtras() == null) {
            finish();
        } else {
            initStateAndGoFurther();
        }
    }

    private void initStateAndGoFurther() {
        Intent intent = getIntent();
        this.doUpload = intent.getBooleanExtra("do_upload", false);
        this.editImages = intent.getBooleanExtra("edit_images", true);
        this.maxCount = intent.getIntExtra("max_count", 0);
        NextActivityInfo nextActivityInfo = getNextActivityInfo();
        if (nextActivityInfo.requestCode != 0) {
            nextActivityInfo.intent.putExtras(intent);
            startActivityForResult(nextActivityInfo.intent, nextActivityInfo.requestCode);
        }
        if (nextActivityInfo.finishPrevious) {
            finish();
        }
    }

    @NonNull
    private NextActivityInfo getNextActivityInfo() {
        ArrayList<MediaInfo> mediaInfos = getIntent().getParcelableArrayListExtra("media_infos");
        if (mediaInfos == null) {
            return getNextActivityInfoForPickImages();
        }
        this.shouldMoveToBack = true;
        this.doUpload = true;
        return getNextActivityInfoForImagesUpload(mediaInfos);
    }

    @NonNull
    private NextActivityInfo getNextActivityInfoForPickImages() {
        Intent nextActivityIntent;
        int requestCode;
        Intent intent = getIntent();
        this.mUploadTarget = intent.getIntExtra("upload_tgt", 0);
        this.mChoiceMode = intent.getIntExtra("choice_mode", 0);
        this.shouldMoveToBack = intent.getBooleanExtra("moveToBack", false);
        this.mAlbumInfo = (PhotoAlbumInfo) intent.getParcelableExtra("photoAlbum");
        this.mInitCamera = intent.getBooleanExtra("camera", false);
        if (this.mInitCamera) {
            nextActivityIntent = new Intent(this, PickFromCameraActivity.class);
            requestCode = 3;
        } else {
            nextActivityIntent = new Intent(this, PickImagesActivity.class).putExtra("statistics_prefix", getStatPrefix()).putExtra("choice_mode", this.mChoiceMode).putExtra("max_count", this.maxCount);
            requestCode = 1;
        }
        return new NextActivityInfo(nextActivityIntent, requestCode, false);
    }

    @NonNull
    private NextActivityInfo getNextActivityInfoForImagesUpload(@NonNull ArrayList<MediaInfo> mediaInfosToUpload) {
        boolean temporaryMedia = getIntent().getBooleanExtra("temp", false);
        getIntent().putExtra("comments_enabled", true);
        return new NextActivityInfo(getPrepareImagesIntent(getIntent(), toImageEditInfos(mediaInfosToUpload, temporaryMedia, FUNC_MEDIA_INFO_TO_IMAGE_EDIT_INFO)), 2, false);
    }

    @Nullable
    private String getStatPrefix() {
        Intent intent = getIntent();
        return intent != null ? intent.getStringExtra("statistics_prefix") : null;
    }

    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        proceedWithIntent(intent);
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("album", this.mAlbumInfo);
        outState.putBoolean("camera", this.mInitCamera);
        outState.putBoolean("do_upload", this.doUpload);
        outState.putBoolean("edit_images", this.editImages);
        outState.putInt("max_count", this.maxCount);
        super.onSaveInstanceState(outState);
    }

    protected void restoreSavedState(Bundle savedInstanceState) {
        this.mAlbumInfo = (PhotoAlbumInfo) savedInstanceState.getParcelable("album");
        this.mInitCamera = savedInstanceState.getBoolean("camera");
        this.doUpload = savedInstanceState.getBoolean("do_upload");
        this.editImages = savedInstanceState.getBoolean("edit_images");
        this.maxCount = savedInstanceState.getInt("max_count");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                onGalleryResult(resultCode, data);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                onEditImagesResult(resultCode, data);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                onCameraResult(resultCode, data);
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onGalleryResult(int resultCode, Intent galleryData) {
        if (-1 == resultCode) {
            ArrayList<GalleryImageInfo> galleryImages = galleryData.getParcelableArrayListExtra("gallery_images");
            boolean areGalleryImagesTemporary = galleryData.getBooleanExtra("temp", false);
            if (this.editImages) {
                startEditGalleryImages(galleryData, galleryImages, areGalleryImagesTemporary);
                return;
            } else {
                returnImages(toImageEditInfos(galleryImages, areGalleryImagesTemporary, FUNC_EXTERNAL_IMAGE_TO_IMAGE_EDIT_INFO));
                return;
            }
        }
        finish();
    }

    private void onCameraResult(int resultCode, Intent cameraData) {
        if (-1 == resultCode) {
            GalleryImageInfo cameraImage = (GalleryImageInfo) cameraData.getParcelableExtra("camera_image");
            if (this.editImages) {
                startEditCameraImage(cameraData, cameraImage);
                return;
            } else {
                returnImages(toImageEditInfos(cameraImage));
                return;
            }
        }
        finish();
    }

    private void onEditImagesResult(int resultCode, Intent resultData) {
        if (-1 == resultCode) {
            returnImages(resultData.getParcelableArrayListExtra("imgs"));
            return;
        }
        if (resultData != null && resultData.getBooleanExtra("toHome", false)) {
            NavigationHelper.clickHomeButton(this);
        }
        finish();
    }

    private void startEditCameraImage(Intent cameraData, @NonNull GalleryImageInfo cameraImage) {
        startActivityForResult(getPrepareImagesIntent(cameraData, toImageEditInfos(cameraImage)).putExtras(getIntent()), 2);
    }

    private void startEditGalleryImages(Intent galleryData, @NonNull ArrayList<GalleryImageInfo> galleryImages, boolean areGalleryImagesTemporary) {
        startActivityForResult(getPrepareImagesIntent(galleryData, toImageEditInfos(galleryImages, areGalleryImagesTemporary, FUNC_EXTERNAL_IMAGE_TO_IMAGE_EDIT_INFO)).putExtras(getIntent()), 2);
    }

    protected void returnImages(ArrayList<ImageEditInfo> images) {
        Bundle inputBundle = new Bundle();
        inputBundle.putParcelableArrayList("imgs", images);
        inputBundle.putInt("impldract", 1);
        if (this.doUpload) {
            Logger.m172d("Sending upload photos request");
            GlobalBus.send(2131624084, new BusEvent(inputBundle));
        } else {
            Intent resultData = new Intent();
            resultData.putExtras(inputBundle);
            setResult(-1, resultData);
        }
        finish();
    }

    protected Intent getPrepareImagesIntent(Intent originalIntent, ArrayList<ImageEditInfo> imagesToEdit) {
        return new Intent(this, PrepareImagesActivity.class).putExtras(originalIntent).putParcelableArrayListExtra("imgs", imagesToEdit).putExtra("album", this.mAlbumInfo).putExtra("choice_mode", this.mChoiceMode).putExtra("upload_tgt", this.mUploadTarget);
    }

    public void finish() {
        super.finish();
        if (!this.shouldMoveToBack) {
        }
    }

    @NonNull
    private static ArrayList<ImageEditInfo> toImageEditInfos(@NonNull GalleryImageInfo cameraImage) {
        return new C09461(1, cameraImage);
    }

    @NonNull
    private static <T> ArrayList<ImageEditInfo> toImageEditInfos(@NonNull ArrayList<T> inputs, boolean areInputsTemporary, @NonNull Func2<T, Boolean, ImageEditInfo> func) {
        ArrayList<ImageEditInfo> imagesToEdit = new ArrayList(inputs.size());
        Iterator i$ = inputs.iterator();
        while (i$.hasNext()) {
            imagesToEdit.add(func.apply(i$.next(), Boolean.valueOf(areInputsTemporary)));
        }
        return imagesToEdit;
    }

    @NonNull
    private static ImageEditInfo toImageEditInfo(@NonNull MediaInfo mediaInfo, boolean isMediaTemporary) {
        int rotation = UserMedia.getImageRotation(OdnoklassnikiApplication.getContext(), mediaInfo.getUri());
        ImageEditInfo imageToEdit = new ImageEditInfo();
        imageToEdit.setUri(mediaInfo.getUri());
        imageToEdit.setMimeType(mediaInfo.getMimeType());
        imageToEdit.setOriginalRotation(rotation);
        imageToEdit.setRotation(rotation);
        imageToEdit.setTemporary(isMediaTemporary);
        imageToEdit.setWasEdited(false);
        return imageToEdit;
    }

    @NonNull
    private static ImageEditInfo toImageEditInfo(@NonNull GalleryImageInfo externalImage, boolean isImageTemporary) {
        ImageEditInfo imageToEdit = new ImageEditInfo();
        imageToEdit.setUri(externalImage.uri);
        imageToEdit.setMimeType(externalImage.mimeType);
        imageToEdit.setHeight(externalImage.height);
        imageToEdit.setWidth(externalImage.width);
        imageToEdit.setOriginalRotation(externalImage.rotation);
        imageToEdit.setRotation(externalImage.rotation);
        imageToEdit.setTemporary(isImageTemporary);
        imageToEdit.setWasEdited(false);
        return imageToEdit;
    }

    static {
        FUNC_MEDIA_INFO_TO_IMAGE_EDIT_INFO = new C09472();
        FUNC_EXTERNAL_IMAGE_TO_IMAGE_EDIT_INFO = new C09483();
    }
}
