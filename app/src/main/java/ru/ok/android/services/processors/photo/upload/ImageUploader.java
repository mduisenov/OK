package ru.ok.android.services.processors.photo.upload;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.graylog.GrayLog;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.JsonTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.ResultParsingException;

public abstract class ImageUploader implements Runnable {
    protected final Context mContext;
    protected int mCurrStatus;
    protected AbsUploaderSubTask mCurrentTask;
    protected final ImageEditInfo mEditedImage;
    protected ImageUploadException mError;
    protected byte[] mImage;
    protected ImageUploadListener mImageUploadListener;
    protected boolean mPaused;
    protected String mPhotoId;
    protected int mPrevStatus;
    protected boolean mRunning;
    protected final JsonSessionTransportProvider mSessionTransportProvider;
    protected File mTempFile;
    protected String mToken;
    protected final JsonTransportProvider mTransportProvider;
    protected String mUploadPhotoId;
    protected String mUploadUrl;
    private int uploadSourceId;

    protected abstract class AbsUploaderSubTask {
        private AbsUploaderSubTask mNext;

        protected abstract void doTask() throws ImageUploadException;

        protected abstract int getStatusId();

        protected AbsUploaderSubTask() {
        }

        public void run() throws ImageUploadException {
            if (ImageUploader.this.mCurrStatus != 6) {
                ImageUploader.this.mCurrentTask = this;
                if (ImageUploader.this.mPaused) {
                    ImageUploader.this.updateStatus(7, true);
                    ImageUploader.this.mRunning = false;
                    return;
                }
                ImageUploader.this.mRunning = true;
                ImageUploader.this.updateStatus(getStatusId(), true);
                doTask();
                if (ImageUploader.this.mCurrStatus == 6) {
                    return;
                }
                if (this.mNext != null) {
                    this.mNext.run();
                    return;
                }
                ImageUploader.this.mCurrentTask = null;
                ImageUploader.this.doFinilize();
                ImageUploader.this.updateStatus(5, true);
            }
        }

        public final AbsUploaderSubTask setNextTask(AbsUploaderSubTask next) {
            this.mNext = next;
            return this.mNext;
        }

        public final AbsUploaderSubTask getLastInChain() {
            AbsUploaderSubTask next = this;
            while (next.mNext != null) {
                next = next.mNext;
            }
            return next;
        }
    }

    public interface ImageUploadListener {
        void onStatusChanged(int i, int i2);
    }

    public static final class PhotoCommitResponse {
        public final String albumId;
        public final String assignedPhotoId;
        public final boolean isSuccessful;
        public final String photoId;

        PhotoCommitResponse(String photoId, boolean isSuccessful, String assignedPhotoId, String albumId) {
            this.photoId = photoId;
            this.isSuccessful = isSuccessful;
            this.assignedPhotoId = assignedPhotoId;
            this.albumId = albumId;
        }

        static PhotoCommitResponse parse(JSONObject photo) throws JSONException {
            return new PhotoCommitResponse(photo.optString("photo_id"), "SUCCESS".equals(photo.optString(NotificationCompat.CATEGORY_STATUS)), photo.optString("assigned_photo_id"), photo.optString("aid"));
        }
    }

    protected abstract AbsUploaderSubTask getChainedEntryPoint();

    public ImageUploader(Context context, ImageEditInfo editedImage) {
        this.mPrevStatus = 0;
        this.mCurrStatus = 0;
        this.mContext = context.getApplicationContext();
        this.mEditedImage = editedImage;
        this.mTransportProvider = JsonTransportProvider.getInstance(context);
        this.mSessionTransportProvider = JsonSessionTransportProvider.getInstance();
        this.mCurrentTask = getChainedEntryPoint();
        if (this.mCurrentTask == null) {
            throw new IllegalArgumentException("Entry point task cannot be null");
        }
    }

    public final void run() {
        if (this.mCurrStatus != 6) {
            try {
                if (this.mCurrentTask != null) {
                    this.mCurrentTask.run();
                }
            } catch (ImageUploadException exc) {
                this.mError = exc;
                this.mRunning = false;
                updateStatus(8, true);
                onException(this.mCurrentTask, exc);
            } catch (Throwable exc2) {
                this.mError = new ImageUploadException(0, 999, exc2);
                this.mRunning = false;
                updateStatus(8, true);
                onException(this.mCurrentTask, exc2);
            }
        }
    }

    protected final void updateStatus(int newStatus, boolean notify) {
        this.mPrevStatus = this.mCurrStatus;
        this.mCurrStatus = newStatus;
        if (notify && this.mImageUploadListener != null) {
            this.mImageUploadListener.onStatusChanged(this.mCurrStatus, this.mPrevStatus);
        }
    }

    public void onException(AbsUploaderSubTask task, Exception exc) {
        int phase = -1;
        if (exc instanceof ImageUploadException) {
            ImageUploadException imageUploadException = (ImageUploadException) exc;
            int errorCode = imageUploadException.getErrorCode();
            if (errorCode != 14 && errorCode != 1) {
                phase = imageUploadException.getPhase();
            } else {
                return;
            }
        }
        String message = "ImageUploadException.\nTask: " + (task != null ? task.getClass().getCanonicalName() : null) + "\n" + "Phase: " + phase;
        Logger.m179e((Throwable) exc, message);
        GrayLog.log(message, exc);
    }

    public void setUploadSourceId(int uploadSourceId) {
        this.uploadSourceId = uploadSourceId;
    }

    public int getUploadSourceId() {
        return this.uploadSourceId;
    }

    public final void setPaused(boolean paused) {
        this.mPaused = paused;
    }

    public final boolean isRunning() {
        return this.mRunning;
    }

    public final void cancel() {
        this.mImageUploadListener = null;
        doFinilize();
        updateStatus(6, false);
    }

    protected final void doFinilize() {
        if (this.mTempFile != null) {
            this.mTempFile.delete();
        }
    }

    public ImageEditInfo getEditedImage() {
        return this.mEditedImage;
    }

    public int getCurrentStatus() {
        return this.mCurrStatus;
    }

    public int getPreviousStatus() {
        return this.mPrevStatus;
    }

    public ImageUploadException getError() {
        return this.mError;
    }

    public String getPhotoId() {
        return this.mPhotoId;
    }

    public void setImageUploadListener(ImageUploadListener imageUploadListener) {
        this.mImageUploadListener = imageUploadListener;
    }

    public static List<PhotoCommitResponse> parseCommitResponse(JsonHttpResult result) throws ResultParsingException {
        try {
            JSONArray photos = result.getResultAsObject().getJSONArray("photos");
            int size = photos == null ? 0 : photos.length();
            ArrayList<PhotoCommitResponse> response = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                response.add(PhotoCommitResponse.parse(photos.getJSONObject(i)));
            }
            return response;
        } catch (JSONException e) {
            throw new ResultParsingException(e);
        }
    }
}
