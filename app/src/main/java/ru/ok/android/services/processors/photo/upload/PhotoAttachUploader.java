package ru.ok.android.services.processors.photo.upload;

import android.content.Context;
import ru.ok.android.model.image.ImageEditInfo;

public class PhotoAttachUploader extends AlbumImageUploader {

    protected final class FinilizeAttachTask extends AbsUploaderSubTask {
        protected FinilizeAttachTask() {
            super();
        }

        protected int getStatusId() {
            return 12;
        }

        protected void doTask() throws ImageUploadException {
        }
    }

    protected class PrepareAttachUploadTask extends AbsUploaderSubTask {
        protected PrepareAttachUploadTask() {
            super();
        }

        protected int getStatusId() {
            return 13;
        }

        protected void doTask() throws ImageUploadException {
        }
    }

    public PhotoAttachUploader(Context context, ImageEditInfo editedImage) {
        super(context, editedImage);
    }

    protected AbsUploaderSubTask getChainedEntryPoint() {
        AbsUploaderSubTask firstTask = new PrepareAttachUploadTask();
        firstTask.setNextTask(new GetUrlSubTask()).setNextTask(new PrepareSubTask()).setNextTask(new UploadSubTask()).setNextTask(new FinilizeAttachTask());
        return firstTask;
    }

    public void onException(AbsUploaderSubTask task, Exception exc) {
        super.onException(task, exc);
    }
}
