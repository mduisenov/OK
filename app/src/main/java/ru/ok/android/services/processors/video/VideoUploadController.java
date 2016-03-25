package ru.ok.android.services.processors.video;

import android.app.Activity;
import android.content.Intent;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.ui.activity.VideoUploadStatusActivity;
import ru.ok.android.utils.Logger;
import ru.ok.model.UserInfo;

public final class VideoUploadController {
    public static void startVideoUploadTaskForGroup(Activity activity, MediaInfo mediaInfo, String groupId) {
        UserInfo userInfo = OdnoklassnikiApplication.getCurrentUser();
        String uid = userInfo == null ? null : userInfo.uid;
        if (uid != null) {
            startUploadActivity(activity, new VideoGroupUploadTask(uid, mediaInfo, groupId));
        } else {
            Logger.m176e("Not currently logged, cannot start video upload task!");
        }
    }

    public static void startVideoUploadTaskForUser(Activity activity, MediaInfo mediaInfo) {
        UserInfo userInfo = OdnoklassnikiApplication.getCurrentUser();
        String uid = userInfo == null ? null : userInfo.uid;
        if (uid != null) {
            startUploadActivity(activity, new VideoUserUploadTask(uid, mediaInfo, 0));
        }
    }

    public static void startUploadActivity(Activity activity, VideoUploadTask task) {
        Intent intent = new Intent(activity, VideoUploadStatusActivity.class);
        intent.putExtra("video_upload_task", task);
        Logger.m172d("Setting task: " + task);
        activity.startActivity(intent);
    }
}
