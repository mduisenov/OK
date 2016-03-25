package ru.ok.android.ui.stream;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.onelog.PhotoRollLog;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.processors.photo.upload.StoreLastSuccessfulImageUploadTimeProcessor;
import ru.ok.android.services.processors.settings.PhotoRollSettingsHelper;
import ru.ok.android.services.processors.settings.PhotoRollSettingsHelper.SettingsHolder;
import ru.ok.android.ui.image.GalleryScanner;
import ru.ok.android.ui.image.PrepareImagesActivity;
import ru.ok.android.ui.image.pick.GalleryImageInfo;
import ru.ok.android.ui.stream.list.PhotoRollViewHolder;
import ru.ok.android.ui.stream.list.StreamLayoutConfig;
import ru.ok.android.ui.stream.view.PhotoRollView;
import ru.ok.android.ui.stream.view.PhotoRollView.PhotoRollViewCallbacks;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.settings.Settings;

class PhotoRollController implements PhotoRollViewCallbacks {
    private final PhotoRollActivationHandler activationHandler;
    private final Context context;
    private GalleryObserver galleryObserver;
    private GalleryScanHandler galleryScanHandler;
    private HandlerThread galleryScanThread;
    private final GalleryScanner galleryScanner;
    private SettingsHolder photoRollSettings;
    private PhotoRollView photoRollView;
    private final PhotoRollViewHolder viewHolder;

    /* renamed from: ru.ok.android.ui.stream.PhotoRollController.1 */
    class C12241 implements Runnable {
        final /* synthetic */ List val$scannedPhotos;

        C12241(List list) {
            this.val$scannedPhotos = list;
        }

        public void run() {
            PhotoRollController.this.tryToBindPhotoRoll(this.val$scannedPhotos);
        }
    }

    private class GalleryObserver extends ContentObserver {
        public GalleryObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            PhotoRollController.this.scheduleGalleryScan();
        }
    }

    private static class GalleryScanHandler extends Handler {
        final WeakReference<PhotoRollController> controlRef;

        public GalleryScanHandler(@NonNull PhotoRollController control, @NonNull Looper looper) {
            super(looper);
            this.controlRef = new WeakReference(control);
        }

        public void handleMessage(Message msg) {
            PhotoRollController control = (PhotoRollController) this.controlRef.get();
            if (control != null && msg.what == 1) {
                control.startGalleryScan();
            }
        }
    }

    private static class PhotoRollActivationHandler extends Handler {
        final WeakReference<PhotoRollController> controlRef;

        public PhotoRollActivationHandler(@NonNull PhotoRollController control) {
            this.controlRef = new WeakReference(control);
        }

        public void handleMessage(Message msg) {
            PhotoRollController control = (PhotoRollController) this.controlRef.get();
            if (control != null && msg.what == 0) {
                control.startMonitoringPhotoRoll();
            }
        }
    }

    public PhotoRollController(@NonNull Context context, @NonNull PhotoRollView photoRollView, @NonNull GalleryScanner galleryScanner) {
        this.context = context;
        this.galleryScanner = galleryScanner;
        this.photoRollView = photoRollView;
        this.viewHolder = new PhotoRollViewHolder(photoRollView);
        this.activationHandler = new PhotoRollActivationHandler(this);
    }

    public void onCloseClick() {
        long closedTime = System.currentTimeMillis();
        storeEarliestPhotoAddedDate(0);
        if (shouldDeactivatePhotoRoll(closedTime)) {
            cleanupCallbacks();
            deactivatePhotoRoll(closedTime);
            scheduleActivatePhotoRoll();
        } else {
            storePhotoRollLastClosedTime(closedTime);
        }
        logClose();
    }

    public void onPhotoClicked(@NonNull GalleryImageInfo photo) {
        this.context.startActivity(getPreparePhotoIntent(photo));
        PhotoRollLog.logClickOnPhoto();
    }

    public void updateLayout(StreamLayoutConfig layoutConfig) {
        this.viewHolder.updateForLayoutSize(layoutConfig);
    }

    @Subscribe(on = 2131623946, to = 2131624094)
    public void onPhotoRollSettingsUpdate(BusEvent event) {
        updateSettings(PhotoRollSettingsHelper.getSettings());
    }

    void onStart() {
        this.photoRollSettings = PhotoRollSettingsHelper.getSettings();
        GlobalBus.register(this);
        if (isPhotoRollEnabled()) {
            prepareForGalleryScanIfNecessary();
            startMonitoringPhotoRoll();
            return;
        }
        hidePhotoRoll();
    }

    void onStop() {
        GlobalBus.unregister(this);
        stopMonitoringPhotoRoll();
    }

    void onDestroy() {
        if (this.galleryScanThread != null) {
            this.galleryScanThread.quit();
        }
    }

    private void updateSettings(@NonNull SettingsHolder settings) {
        if (this.photoRollSettings != settings) {
            SettingsHolder oldSettings = this.photoRollSettings;
            this.photoRollSettings = settings;
            if (oldSettings.enabled != settings.enabled) {
                if (settings.enabled) {
                    prepareForGalleryScanIfNecessary();
                    startMonitoringPhotoRoll();
                } else {
                    hidePhotoRoll();
                    stopMonitoringPhotoRoll();
                }
            }
            if (settings.enabled) {
                this.viewHolder.updateStyleForLayoutSize(getPhotoRollStyleResId());
                rescheduleActivatePhotoRollIfNecessary();
            }
        }
    }

    private void prepareForGalleryScanIfNecessary() {
        if (this.galleryScanThread == null) {
            this.galleryScanThread = new HandlerThread("GalleryScanThread", 10);
            this.galleryScanThread.start();
            this.galleryScanHandler = new GalleryScanHandler(this, this.galleryScanThread.getLooper());
            this.galleryObserver = new GalleryObserver(this.galleryScanHandler);
        }
    }

    @NonNull
    PhotoRollViewHolder getViewHolder() {
        return this.viewHolder;
    }

    private void scheduleGalleryScan() {
        removeGalleryScanMessages();
        this.galleryScanHandler.sendEmptyMessageDelayed(1, 300);
    }

    private void removeGalleryScanMessages() {
        if (this.galleryScanHandler != null) {
            this.galleryScanHandler.removeMessages(1);
        }
    }

    private void startGalleryScan() {
        if (!shouldStopGalleryScanEarly()) {
            List<GalleryImageInfo> scannedPhotos = this.galleryScanner.scan(getDateToStartScanFromMs(), this.photoRollSettings.maxPhotoCountToShow);
            if (!shouldStopGalleryScanEarly()) {
                handleGalleryScanResultInMainThread(scannedPhotos);
            }
        }
    }

    private boolean shouldStopGalleryScanEarly() {
        return this.galleryScanHandler.hasMessages(1);
    }

    private long getDateToStartScanFromMs() {
        long earliestPhotoAddedDate = getEarliestPhotoAddedDate();
        return earliestPhotoAddedDate > 0 ? earliestPhotoAddedDate : Math.max(Math.max(getPhotoRollLastActivatedTime(), System.currentTimeMillis() - this.photoRollSettings.recentPhotoIntervalMs), Settings.getLongValue(this.context, "last_successful_upload_time", 0));
    }

    private void handleGalleryScanResultInMainThread(@NonNull List<GalleryImageInfo> scannedPhotos) {
        ThreadUtil.executeOnMain(new C12241(scannedPhotos));
    }

    private void tryToBindPhotoRoll(@NonNull List<GalleryImageInfo> photos) {
        if (isPhotoRollEnabled() && isPhotoRollActivated()) {
            bindPhotoRoll(photos);
        }
    }

    private void scheduleActivatePhotoRoll() {
        removeActivatePhotoRollMessages();
        long restIntervalMs = getPhotoRollRestIntervalMs();
        this.activationHandler.sendEmptyMessageDelayed(0, (restIntervalMs - System.currentTimeMillis()) + getPhotoRollLastClosedTime());
    }

    private void rescheduleActivatePhotoRollIfNecessary() {
        if (this.activationHandler.hasMessages(0)) {
            scheduleActivatePhotoRoll();
        }
    }

    private void removeActivatePhotoRollMessages() {
        this.activationHandler.removeMessages(0);
    }

    private void bindPhotoRoll(@NonNull List<GalleryImageInfo> scannedPhotos) {
        if (shouldShowPhotoRoll(scannedPhotos.size())) {
            this.photoRollView.setPhotos(scannedPhotos);
            this.viewHolder.updateStyleForLayoutSize(getPhotoRollStyleResId());
            storeEarliestPhotoAddedDate(getEarliestPhotoAddedDateMs(scannedPhotos));
            showPhotoRoll();
            return;
        }
        hidePhotoRoll();
    }

    private long getEarliestPhotoAddedDateMs(@NonNull List<GalleryImageInfo> scannedPhotos) {
        return ((GalleryImageInfo) scannedPhotos.get(scannedPhotos.size() - 1)).dateAdded * 1000;
    }

    private boolean shouldShowPhotoRoll(int totalPhotoCount) {
        return totalPhotoCount >= 4;
    }

    private void showPhotoRoll() {
        if (!isPhotoRollShown()) {
            ViewUtil.visible(this.photoRollView);
            ViewUtil.resetLayoutParams(this.photoRollView, -1, -2);
            logShow();
        }
    }

    private void logShow() {
        if (!PhotoRollSettingsHelper.hasFirstShowAfterClose()) {
            PhotoRollSettingsHelper.setFirstShowAfterClose();
            PhotoRollLog.logShow();
        }
    }

    private void logClose() {
        PhotoRollSettingsHelper.clearFirstShowAfterClose();
        if (PhotoRollSettingsHelper.hasUploadAttempt()) {
            PhotoRollLog.logCloseAfterUploadAttempt();
            PhotoRollSettingsHelper.clearUploadAttempt();
            return;
        }
        PhotoRollLog.logCloseNoUploadAttempt();
    }

    private void hidePhotoRoll() {
        if (isPhotoRollShown()) {
            ViewUtil.gone(this.photoRollView);
            ViewUtil.resetLayoutParams(this.photoRollView, 0, 0);
        }
    }

    private boolean isPhotoRollShown() {
        return this.photoRollView.getVisibility() == 0;
    }

    private void startMonitoringPhotoRoll() {
        boolean activated = isPhotoRollActivated();
        if (!activated) {
            if (shouldActivatePhotoRollImmediately()) {
                activatePhotoRoll();
                activated = true;
            } else {
                hidePhotoRoll();
                scheduleActivatePhotoRoll();
            }
        }
        if (activated) {
            setPhotoRollCallbacks(this);
            registerGalleryObserver();
            scheduleGalleryScan();
        }
    }

    private void stopMonitoringPhotoRoll() {
        removeActivatePhotoRollMessages();
        cleanupCallbacks();
    }

    private void cleanupCallbacks() {
        removeGalleryScanMessages();
        setPhotoRollCallbacks(null);
        unregisterGalleryObserver();
    }

    private void registerGalleryObserver() {
        if (this.galleryObserver != null) {
            this.context.getContentResolver().registerContentObserver(this.galleryScanner.getGalleryUri(), true, this.galleryObserver);
        }
    }

    private void unregisterGalleryObserver() {
        if (this.galleryObserver != null) {
            this.context.getContentResolver().unregisterContentObserver(this.galleryObserver);
        }
    }

    private void setPhotoRollCallbacks(@Nullable PhotoRollController callbacks) {
        this.photoRollView.setCallbacks(callbacks);
    }

    private int getPhotoRollStyleResId() {
        String style = this.photoRollSettings.style;
        Object obj = -1;
        switch (style.hashCode()) {
            case 3181155:
                if (style.equals("gray")) {
                    obj = 1;
                    break;
                }
                break;
            case 93818879:
                if (style.equals("black")) {
                    obj = 3;
                    break;
                }
                break;
            case 113101865:
                if (style.equals("white")) {
                    obj = null;
                    break;
                }
                break;
            case 1432980538:
                if (style.equals("gray_light")) {
                    obj = 2;
                    break;
                }
                break;
        }
        switch (obj) {
            case RECEIVED_VALUE:
                return 2131296609;
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                return 2131296607;
            case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                return 2131296608;
            case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                return 2131296606;
            default:
                throw new IllegalArgumentException("Unsupported photo roll style (" + style + ")");
        }
    }

    private boolean isPhotoRollActivated() {
        return getPhotoRollLastActivatedTime() > 0;
    }

    private void activatePhotoRoll() {
        storePhotoRollLastClosedTime(0);
        storePhotoRollLasActivatedTime(System.currentTimeMillis());
    }

    private void deactivatePhotoRoll(long closedTime) {
        storePhotoRollLastClosedTime(closedTime);
        storePhotoRollLasActivatedTime(0);
    }

    private boolean shouldActivatePhotoRollImmediately() {
        long lastActivatedTime = getPhotoRollLastActivatedTime();
        long lastClosedTime = getPhotoRollLastClosedTime();
        return lastActivatedTime == 0 && (lastClosedTime == 0 || System.currentTimeMillis() - lastClosedTime > getPhotoRollRestIntervalMs());
    }

    private boolean shouldDeactivatePhotoRoll(long closedTime) {
        long prevClosedTime = getPhotoRollLastClosedTime();
        long lastActivatedTime = getPhotoRollLastActivatedTime();
        long lastSuccessfulUploadTimeFromPhotoRoll = Settings.getLongValue(this.context, StoreLastSuccessfulImageUploadTimeProcessor.getKeyForUploadSourceId(1), 0);
        boolean leaveActive = closedTime > lastSuccessfulUploadTimeFromPhotoRoll && Math.max(lastActivatedTime, prevClosedTime) < lastSuccessfulUploadTimeFromPhotoRoll;
        return !leaveActive;
    }

    private boolean isPhotoRollEnabled() {
        return this.photoRollSettings.enabled;
    }

    private long getPhotoRollRestIntervalMs() {
        return this.photoRollSettings.restIntervalMs;
    }

    private long getPhotoRollLastClosedTime() {
        return PhotoRollSettingsHelper.getLastClosedTime();
    }

    private void storePhotoRollLastClosedTime(long lastClosedTime) {
        PhotoRollSettingsHelper.setLastClosedTime(lastClosedTime);
    }

    private long getPhotoRollLastActivatedTime() {
        return PhotoRollSettingsHelper.getLastActivatedTime();
    }

    private void storePhotoRollLasActivatedTime(long lastActivatedTime) {
        PhotoRollSettingsHelper.setLastActivatedTime(lastActivatedTime);
    }

    private long getEarliestPhotoAddedDate() {
        return PhotoRollSettingsHelper.getEarliestPhotoAddedDate();
    }

    private void storeEarliestPhotoAddedDate(long photoAddedDate) {
        PhotoRollSettingsHelper.setEarliestPhotoAddedDate(photoAddedDate);
    }

    @NonNull
    private Intent getPreparePhotoIntent(@NonNull GalleryImageInfo photo) {
        return new Intent(this.context, PrepareImagesActivity.class).putParcelableArrayListExtra("imgs", toImageEditInfos(photo)).putExtra("upload_from_photo_roll", true).putExtra("comments_enabled", true).putExtra("choice_mode", 0).putExtra("upload_tgt", true);
    }

    @NonNull
    private ArrayList<ImageEditInfo> toImageEditInfos(@NonNull GalleryImageInfo photo) {
        ImageEditInfo imageToEdit = new ImageEditInfo();
        imageToEdit.setUri(photo.uri);
        imageToEdit.setMimeType(photo.mimeType);
        imageToEdit.setHeight(photo.height);
        imageToEdit.setWidth(photo.width);
        imageToEdit.setOriginalRotation(photo.rotation);
        imageToEdit.setRotation(photo.rotation);
        imageToEdit.setTemporary(false);
        imageToEdit.setWasEdited(false);
        ArrayList<ImageEditInfo> imageEditInfos = new ArrayList();
        imageEditInfos.add(imageToEdit);
        return imageEditInfos;
    }
}
