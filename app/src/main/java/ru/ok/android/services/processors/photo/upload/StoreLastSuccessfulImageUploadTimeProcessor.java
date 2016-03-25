package ru.ok.android.services.processors.photo.upload;

import android.support.annotation.NonNull;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.model.image.ImageForUpload;
import ru.ok.android.utils.settings.Settings;

public class StoreLastSuccessfulImageUploadTimeProcessor {
    @Subscribe(on = 2131623944, to = 2131624225)
    public void onImageUploadEvent(BusEvent event) {
        if (event.resultCode == 1) {
            ImageForUpload image = (ImageForUpload) event.bundleOutput.getParcelable("img");
            if (image != null && 5 == image.getCurrentStatus()) {
                long time = System.currentTimeMillis();
                storeLatestSuccessfulUploadTimeForSource(event.bundleOutput.getInt("upload_source_id", 0), time);
                storeLatestSuccessfulUploadTime(time);
            }
        }
    }

    private void storeLatestSuccessfulUploadTimeForSource(int uploadSourceId, long time) {
        Settings.storeLongValue(OdnoklassnikiApplication.getContext(), getKeyForUploadSourceId(uploadSourceId), time);
    }

    @NonNull
    public static String getKeyForUploadSourceId(int uploadSourceId) {
        return "upload_source_id-" + uploadSourceId;
    }

    private void storeLatestSuccessfulUploadTime(long time) {
        Settings.storeLongValue(OdnoklassnikiApplication.getContext(), "last_successful_upload_time", time);
    }
}
