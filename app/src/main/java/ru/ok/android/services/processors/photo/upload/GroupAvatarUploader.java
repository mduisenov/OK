package ru.ok.android.services.processors.photo.upload;

import android.content.Context;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.java.api.request.image.SetGroupMainPhotoRequest;

public class GroupAvatarUploader extends AlbumImageUploader {

    protected final class SetGroupMainPhotoSubTask extends AbsUploaderSubTask {
        protected SetGroupMainPhotoSubTask() {
            super();
        }

        protected int getStatusId() {
            return 10;
        }

        protected void doTask() throws ImageUploadException {
            try {
                GroupAvatarUploader.this.mSessionTransportProvider.execJsonHttpMethod(new SetGroupMainPhotoRequest(GroupAvatarUploader.this.mEditedImage.getAlbumInfo().getGroupId(), GroupAvatarUploader.this.mToken));
            } catch (Throwable exc) {
                throw new ImageUploadException(6, 14, exc);
            } catch (Throwable exc2) {
                throw new ImageUploadException(6, 4, exc2);
            } catch (Throwable exc22) {
                throw new ImageUploadException(6, 11, exc22);
            } catch (Throwable exc222) {
                throw new ImageUploadException(5, 999, exc222);
            }
        }
    }

    public GroupAvatarUploader(Context context, ImageEditInfo editedImage) {
        super(context, editedImage);
    }

    protected AbsUploaderSubTask getChainedEntryPoint() {
        AbsUploaderSubTask firstTask = new GetUrlSubTask();
        firstTask.setNextTask(new PrepareSubTask()).setNextTask(new UploadSubTask()).setNextTask(new SetGroupMainPhotoSubTask());
        return firstTask;
    }
}
