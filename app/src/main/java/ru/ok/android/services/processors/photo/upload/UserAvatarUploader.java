package ru.ok.android.services.processors.photo.upload;

import android.content.Context;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.widget.MenuView;
import ru.ok.java.api.request.image.SetUserMainPhotoRequest;

public class UserAvatarUploader extends AlbumImageUploader {

    protected final class SetUserMainPhotoSubTask extends AbsUploaderSubTask {
        protected SetUserMainPhotoSubTask() {
            super();
        }

        protected int getStatusId() {
            return 9;
        }

        protected void doTask() throws ImageUploadException {
            try {
                UserAvatarUploader.this.mSessionTransportProvider.execJsonHttpMethod(new SetUserMainPhotoRequest(UserAvatarUploader.this.mPhotoId));
            } catch (Throwable exc) {
                throw new ImageUploadException(5, 14, exc);
            } catch (Throwable exc2) {
                throw new ImageUploadException(5, 4, exc2);
            } catch (Throwable exc22) {
                throw new ImageUploadException(5, 11, exc22);
            } catch (Throwable exc222) {
                throw new ImageUploadException(5, 999, exc222);
            }
        }
    }

    protected final class UpdateUserSubTask extends AbsUploaderSubTask {
        protected UpdateUserSubTask() {
            super();
        }

        protected int getStatusId() {
            return 11;
        }

        protected void doTask() throws ImageUploadException {
            MenuView.updateAvatar();
        }
    }

    public UserAvatarUploader(Context context, ImageEditInfo editedImage) {
        super(context, editedImage);
    }

    protected AbsUploaderSubTask getChainedEntryPoint() {
        AbsUploaderSubTask entryTask = super.getChainedEntryPoint();
        entryTask.getLastInChain().setNextTask(new SetUserMainPhotoSubTask()).setNextTask(new UpdateUserSubTask());
        return entryTask;
    }
}
