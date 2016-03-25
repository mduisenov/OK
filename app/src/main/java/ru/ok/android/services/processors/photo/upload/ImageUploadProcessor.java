package ru.ok.android.services.processors.photo.upload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.model.ServiceManager;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.model.image.ImageForUpload;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.BackgroundProcessor;
import ru.ok.android.services.processors.photo.upload.ImageUploader.ImageUploadListener;
import ru.ok.android.utils.Constants.Image;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.controls.events.EventsManager;

public final class ImageUploadProcessor extends BackgroundProcessor {
    private final BroadcastReceiver connectionChangeReceiver;
    protected final IntentFilter filter;
    protected final ImageUploadListener imageUploadListener;
    protected ImageUploader mCurrentUploader;
    protected final List<ImageUploader> mProcessed;
    protected final LinkedList<ImageUploader> mQueue;
    protected int mStatus;
    protected final ServiceManager serviceManager;

    /* renamed from: ru.ok.android.services.processors.photo.upload.ImageUploadProcessor.1 */
    class C04851 implements Runnable {
        C04851() {
        }

        public void run() {
            ImageUploadProcessor.this.populateQueueFromPreferences();
            if (!ImageUploadProcessor.this.mQueue.isEmpty()) {
                ImageUploadProcessor.this.mStatus = 6;
            }
        }
    }

    /* renamed from: ru.ok.android.services.processors.photo.upload.ImageUploadProcessor.2 */
    class C04862 implements ImageUploadListener {
        C04862() {
        }

        public void onStatusChanged(int newStatus, int prevStatus) {
            Logger.m172d("Upload status changed. New status: " + newStatus);
            if (ImageUploadProcessor.this.mStatus != 9) {
                Bundle bundle = ImageUploadProcessor.this.createBaseResponseBundle();
                bundle.putParcelable("img", ImageUploadProcessor.this.buildImageForUpload(ImageUploadProcessor.this.mCurrentUploader));
                bundle.putInt("prcsd", ImageUploadProcessor.this.mProcessed.size());
                bundle.putInt("total", ImageUploadProcessor.this.getTotalUploadsCount());
                bundle.putInt("upload_source_id", ImageUploadProcessor.this.mCurrentUploader.getUploadSourceId());
                GlobalBus.send(2131624225, new BusEvent(bundle, 1));
                Logger.m172d("Sent upload status update message");
                switch (newStatus) {
                    case Message.UUID_FIELD_NUMBER /*5*/:
                        Logger.m172d("Upload finished");
                        ImageUploadProcessor.this.mProcessed.add(ImageUploadProcessor.this.mCurrentUploader);
                        ImageUploadProcessor.this.mCurrentUploader = null;
                        EventsManager.getInstance().changePhotoCounter(1);
                        ImageUploadProcessor.this.processNextUpload();
                    case Message.TASKID_FIELD_NUMBER /*8*/:
                        ImageUploadException error = ImageUploadProcessor.this.mCurrentUploader.getError();
                        int errorCode = error == null ? 0 : error.getErrorCode();
                        Logger.m173d("Upload error. Error code: %d", Integer.valueOf(errorCode));
                        if (errorCode == 11) {
                            ImageUploadProcessor.this.changeUploaderStatus(4);
                        } else if (errorCode == 14) {
                            ImageUploadProcessor.this.changeUploaderStatus(5);
                        } else if (errorCode == 3 && error.getServerErrorCode() == 454) {
                            Logger.m172d("Retrying upload without comment");
                            ImageUploadProcessor.this.mCurrentUploader.getEditedImage().setComment(null);
                            ImageUploadProcessor.this.mCurrentUploader.run();
                        } else {
                            Logger.m172d("Fatal error. Skipping current upload");
                            ImageUploadProcessor.this.mProcessed.add(ImageUploadProcessor.this.mCurrentUploader);
                            ImageUploadProcessor.this.mCurrentUploader = null;
                            ImageUploadProcessor.this.processNextUpload();
                        }
                    default:
                        if (ImageUploadProcessor.this.mStatus == 2) {
                            Logger.m172d("Uploader is pausing. Current upload is also moved to paused state");
                            ImageUploadProcessor.this.changeUploaderStatus(3);
                        }
                }
            }
        }
    }

    /* renamed from: ru.ok.android.services.processors.photo.upload.ImageUploadProcessor.3 */
    class C04873 implements Runnable {
        C04873() {
        }

        public void run() {
            File cacheDir = Image.getUploaderChacheDir(OdnoklassnikiApplication.getContext());
            if (cacheDir != null) {
                cleanDirectory(cacheDir);
            }
        }

        private final void cleanDirectory(File dir) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        if (file.isDirectory()) {
                            cleanDirectory(file);
                        }
                        file.delete();
                    } catch (Throwable exc) {
                        Logger.m178e(exc);
                    }
                }
            }
        }
    }

    /* renamed from: ru.ok.android.services.processors.photo.upload.ImageUploadProcessor.4 */
    class C04884 implements Runnable {
        final /* synthetic */ List val$all;

        C04884(List list) {
            this.val$all = list;
        }

        public void run() {
            for (ImageUploader uploader : this.val$all) {
                ImageEditInfo image = uploader.getEditedImage();
                if (image.isTemporary()) {
                    try {
                        new File(image.getUri().getPath()).delete();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    /* renamed from: ru.ok.android.services.processors.photo.upload.ImageUploadProcessor.5 */
    class C04895 extends BroadcastReceiver {
        C04895() {
        }

        public void onReceive(Context context, Intent intent) {
            NetworkInfo activeNetInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.isConnected() && ImageUploadProcessor.this.mStatus == 4) {
                boolean resume = true;
                if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("wifiupld", false) && activeNetInfo.getType() != 1) {
                    resume = false;
                }
                if (resume) {
                    ImageUploadProcessor.this.resume();
                }
            }
        }
    }

    public ImageUploadProcessor() {
        this.serviceManager = new ServiceManager();
        this.mQueue = new LinkedList();
        this.mProcessed = new ArrayList();
        this.mStatus = 0;
        this.filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        this.imageUploadListener = new C04862();
        this.connectionChangeReceiver = new C04895();
        ThreadUtil.queueOnMain(new C04851());
    }

    @Subscribe(on = 2131623944, to = 2131624084)
    public void imageUpload(BusEvent event) {
        int action = event.bundleInput.getInt("impldract");
        int uploadSourceId = event.bundleInput.getInt("upload_source_id");
        switch (action) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                ArrayList<Parcelable> parcelables = event.bundleInput.getParcelableArrayList("imgs");
                if (parcelables != null) {
                    Logger.m172d("Has some image for upload");
                    Iterator i$ = parcelables.iterator();
                    while (i$.hasNext()) {
                        ImageUploader uploader = getTargetedImageUploader((ImageEditInfo) ((Parcelable) i$.next()));
                        uploader.setUploadSourceId(uploadSourceId);
                        this.mQueue.add(uploader);
                    }
                    saveQueueToPreferences();
                    if (!isPausingOrPaused()) {
                        Logger.m172d("Uploader is active");
                        processNextUpload();
                    }
                    sendUploadsStatuses();
                }
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                pause();
            case Message.TYPE_FIELD_NUMBER /*3*/:
                resume();
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                cancel();
            case Message.UUID_FIELD_NUMBER /*5*/:
                sendUploadsStatuses();
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                if (this.mQueue.isEmpty()) {
                    finilizeUploads();
                }
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                GlobalBus.send(2131624225, new BusEvent(createBaseResponseBundle(), 4));
            case Message.TASKID_FIELD_NUMBER /*8*/:
                clearUploads(true);
                changeUploaderStatus(9);
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                cleanTempDirectory();
            case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                if (this.mStatus == 0) {
                    if (this.mQueue.isEmpty()) {
                        finilizeUploads();
                    }
                    GlobalBus.send(2131624007, new BusEvent());
                }
            default:
        }
    }

    private ImageUploader getTargetedImageUploader(ImageEditInfo imageInfo) {
        switch (imageInfo.getUploadTarget()) {
            case RECEIVED_VALUE:
                return new AlbumImageUploader(OdnoklassnikiApplication.getContext(), imageInfo);
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return new GroupAvatarUploader(OdnoklassnikiApplication.getContext(), imageInfo);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return new UserAvatarUploader(OdnoklassnikiApplication.getContext(), imageInfo);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return new PhotoAttachUploader(OdnoklassnikiApplication.getContext(), imageInfo);
            default:
                return null;
        }
    }

    protected final synchronized void processNextUpload() {
        Logger.m172d("Trying to process next upload in queue");
        if (this.mCurrentUploader == null) {
            saveQueueToPreferences();
            this.mCurrentUploader = (ImageUploader) this.mQueue.poll();
            Logger.m172d("Polled for next upload");
            if (this.mCurrentUploader != null) {
                Logger.m172d("Preparing upload");
                OdnoklassnikiApplication.getContext().registerReceiver(this.connectionChangeReceiver, this.filter);
                if (!this.serviceManager.isBound()) {
                    this.serviceManager.bindService(OdnoklassnikiApplication.getContext());
                }
                this.mCurrentUploader.setImageUploadListener(this.imageUploadListener);
                if (this.mStatus == 2) {
                    Logger.m172d("Uploader is pausing, new upload will be set to paused");
                    this.mCurrentUploader.setPaused(true);
                    changeUploaderStatus(3);
                } else {
                    Logger.m172d("Uploader is active, starting new upload");
                    changeUploaderStatus(1);
                    doAsync(this.mCurrentUploader);
                }
            } else {
                Logger.m172d("No queued uploads found, forcing uploader to sleep");
                changeUploaderStatus(0);
                try {
                    OdnoklassnikiApplication.getContext().unregisterReceiver(this.connectionChangeReceiver);
                } catch (Exception e) {
                }
                try {
                    this.serviceManager.unBindService();
                } catch (Throwable exc) {
                    Logger.m178e(exc);
                }
            }
        }
    }

    protected final void pause() {
        if (!isPausingOrPausedNotWake()) {
            if (this.mCurrentUploader != null || !this.mQueue.isEmpty()) {
                changeUploaderStatus(2);
                if (this.mCurrentUploader != null) {
                    this.mCurrentUploader.setPaused(true);
                } else {
                    changeUploaderStatus(3);
                }
            }
        }
    }

    private final boolean isPausingOrPausedNotWake() {
        return this.mStatus == 4 || this.mStatus == 5 || this.mStatus == 3 || this.mStatus == 2;
    }

    private final boolean isPausingOrPaused() {
        return isPausingOrPausedNotWake() || this.mStatus == 6;
    }

    protected final void resume() {
        if (!this.mQueue.isEmpty() || this.mCurrentUploader != null) {
            changeUploaderStatus(7);
            if (this.mCurrentUploader != null) {
                this.mCurrentUploader.setPaused(false);
                if (!this.mCurrentUploader.isRunning()) {
                    doAsync(this.mCurrentUploader);
                }
            } else {
                processNextUpload();
            }
            changeUploaderStatus(1);
        }
    }

    protected final void cancel() {
        changeUploaderStatus(8);
        clearUploads(false);
        changeUploaderStatus(0);
        sendUploadsStatuses();
    }

    protected final void clearUploads(boolean wipe) {
        if (this.mCurrentUploader != null) {
            this.mCurrentUploader.cancel();
            this.mProcessed.add(this.mCurrentUploader);
            this.mCurrentUploader = null;
        }
        Iterator i$ = this.mQueue.iterator();
        while (i$.hasNext()) {
            ((ImageUploader) i$.next()).cancel();
        }
        if (wipe) {
            this.mProcessed.clear();
        } else {
            this.mProcessed.addAll(this.mQueue);
        }
        this.mQueue.clear();
        saveQueueToPreferences();
        try {
            OdnoklassnikiApplication.getContext().unregisterReceiver(this.connectionChangeReceiver);
        } catch (Exception e) {
        }
        try {
            this.serviceManager.unBindService();
        } catch (Throwable exc) {
            Logger.m178e(exc);
        }
    }

    protected final void changeUploaderStatus(int newStatus) {
        if (this.mStatus != newStatus) {
            this.mStatus = newStatus;
            GlobalBus.send(2131624225, new BusEvent(createBaseResponseBundle(), 2));
        }
    }

    protected final void sendUploadsStatuses() {
        Logger.m172d("Sending upload statuses");
        Bundle bundle = createBaseResponseBundle();
        ArrayList<ImageForUpload> images = new ArrayList(getTotalUploadsCount());
        for (ImageUploader uploader : this.mProcessed) {
            images.add(buildImageForUpload(uploader));
        }
        if (this.mCurrentUploader != null) {
            images.add(buildImageForUpload(this.mCurrentUploader));
        }
        Iterator i$ = this.mQueue.iterator();
        while (i$.hasNext()) {
            images.add(buildImageForUpload((ImageUploader) i$.next()));
        }
        bundle.putParcelableArrayList("imgs", images);
        GlobalBus.send(2131624225, new BusEvent(bundle, 3));
    }

    protected final Bundle createBaseResponseBundle() {
        Bundle bundle = new Bundle();
        int errors = 0;
        for (ImageUploader upload : this.mProcessed) {
            if (upload.getCurrentStatus() == 8) {
                errors++;
            } else if (upload.getCurrentStatus() == 6) {
                errors++;
            }
        }
        bundle.putInt("prcsd", this.mProcessed.size());
        bundle.putInt("total", getTotalUploadsCount());
        bundle.putInt("errs", errors);
        bundle.putInt("upldrsts", this.mStatus);
        return bundle;
    }

    protected final ImageForUpload buildImageForUpload(ImageUploader imageUploader) {
        ImageForUpload imageForUpload = new ImageForUpload();
        imageForUpload.setUri(imageUploader.getEditedImage().getUri());
        imageForUpload.setRotation(imageUploader.getEditedImage().getRotation());
        imageForUpload.setId(imageUploader.getEditedImage().getId());
        imageForUpload.setCurrentStatus(imageUploader.getCurrentStatus());
        imageForUpload.setPreviousStatus(imageUploader.getPreviousStatus());
        imageForUpload.setError(imageUploader.getError());
        imageForUpload.setAlbumInfo(imageUploader.getEditedImage().getAlbumInfo());
        imageForUpload.setPhotoId(imageUploader.getPhotoId());
        imageForUpload.setComment(imageUploader.getEditedImage().getComment());
        imageForUpload.setUploadTarget(imageUploader.getEditedImage().getUploadTarget());
        imageForUpload.setMimeType(imageUploader.getEditedImage().getMimeType());
        return imageForUpload;
    }

    protected final void cleanTempDirectory() {
        doAsync(new C04873());
    }

    protected final void finilizeUploads() {
        List<ImageUploader> all = new ArrayList();
        if (this.mCurrentUploader != null) {
            all.add(this.mCurrentUploader);
        }
        all.addAll(this.mProcessed);
        all.addAll(this.mQueue);
        this.mProcessed.clear();
        this.mQueue.clear();
        doAsync(new C04884(all));
        cleanTempDirectory();
    }

    protected final int getTotalUploadsCount() {
        int totalCount = this.mProcessed.size() + this.mQueue.size();
        if (this.mCurrentUploader != null) {
            return totalCount + 1;
        }
        return totalCount;
    }

    private final void saveQueueToPreferences() {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(OdnoklassnikiApplication.getContext()).edit();
        if (this.mQueue.isEmpty()) {
            editor.remove("iuq_srl");
        } else {
            ArrayList<ImageEditInfo> toSave = new ArrayList(this.mQueue.size());
            Iterator i$ = this.mQueue.iterator();
            while (i$.hasNext()) {
                toSave.add(((ImageUploader) i$.next()).getEditedImage());
            }
            try {
                String serialized = IOUtils.serializableToBase64String(toSave);
                if (serialized != null) {
                    editor.putString("iuq_srl", serialized);
                }
            } catch (Exception exc) {
                Logger.m177e("Unable to serialize queue", exc);
            }
        }
        editor.apply();
    }

    private final void populateQueueFromPreferences() {
        String serialized = PreferenceManager.getDefaultSharedPreferences(OdnoklassnikiApplication.getContext()).getString("iuq_srl", null);
        if (serialized != null) {
            try {
                for (ImageEditInfo image : (List) IOUtils.base64SerializedToObject(serialized)) {
                    if (new File(image.getUri().getPath()).exists()) {
                        this.mQueue.add(getTargetedImageUploader(image));
                    }
                }
            } catch (Exception exc) {
                Logger.m177e("Unable to deserialize queue", exc);
            }
        }
    }
}
