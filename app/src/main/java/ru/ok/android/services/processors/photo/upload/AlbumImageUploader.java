package ru.ok.android.services.processors.photo.upload;

import android.content.Context;
import android.text.TextUtils;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.services.processors.image.upload.ImageUploadMethods;
import ru.ok.android.services.processors.image.upload.ImageUploadMethods.GetUrlResult;
import ru.ok.android.services.processors.photo.upload.ImageUploader.PhotoCommitResponse;

public class AlbumImageUploader extends ImageUploader {

    protected class CommitSubTask extends AbsUploaderSubTask {
        protected CommitSubTask() {
            super();
        }

        protected int getStatusId() {
            return 4;
        }

        protected void doTask() throws ImageUploadException {
            PhotoCommitResponse response = ImageUploadMethods.commit(AlbumImageUploader.this.mUploadPhotoId, AlbumImageUploader.this.mToken, AlbumImageUploader.this.mEditedImage.getComment(), AlbumImageUploader.this.mSessionTransportProvider);
            String realAlbumId = response == null ? null : response.albumId;
            if (!TextUtils.isEmpty(realAlbumId)) {
                AlbumImageUploader.this.getEditedImage().getAlbumInfo().setId(realAlbumId);
            }
            AlbumImageUploader.this.mPhotoId = response.assignedPhotoId;
        }
    }

    protected class GetUrlSubTask extends AbsUploaderSubTask {
        protected GetUrlSubTask() {
            super();
        }

        protected int getStatusId() {
            return 2;
        }

        protected void doTask() throws ImageUploadException {
            GetUrlResult result = ImageUploadMethods.getUrlWithMaxQualitySettings(AlbumImageUploader.this.mEditedImage.getAlbumInfo(), AlbumImageUploader.this.mSessionTransportProvider);
            AlbumImageUploader.this.mUploadUrl = result.uploadUrl;
            AlbumImageUploader.this.mUploadPhotoId = result.uploadId;
        }
    }

    protected class PrepareSubTask extends AbsUploaderSubTask {
        protected PrepareSubTask() {
            super();
        }

        protected int getStatusId() {
            return 1;
        }

        protected void doTask() throws ImageUploadException {
            AlbumImageUploader.this.mImage = ImageUploadMethods.prepareImageToBytes(AlbumImageUploader.this.mContext, AlbumImageUploader.this.mEditedImage);
        }
    }

    protected class UploadSubTask extends AbsUploaderSubTask {
        protected UploadSubTask() {
            super();
        }

        protected int getStatusId() {
            return 3;
        }

        protected void doTask() throws ImageUploadException {
            AlbumImageUploader.this.mToken = ImageUploadMethods.uploadImage(AlbumImageUploader.this.mUploadUrl, AlbumImageUploader.this.mUploadPhotoId, AlbumImageUploader.this.mImage, AlbumImageUploader.this.mTransportProvider);
        }
    }

    public AlbumImageUploader(Context context, ImageEditInfo editedImage) {
        super(context, editedImage);
    }

    protected AbsUploaderSubTask getChainedEntryPoint() {
        AbsUploaderSubTask firstTask = new GetUrlSubTask();
        firstTask.setNextTask(new PrepareSubTask()).setNextTask(new UploadSubTask()).setNextTask(new CommitSubTask());
        return firstTask;
    }
}
