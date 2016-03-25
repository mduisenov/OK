package ru.ok.android.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import java.io.File;
import java.util.List;
import ru.ok.android.services.processors.video.MediaInfo;
import ru.ok.android.ui.dialogs.AlertFragmentDialog;
import ru.ok.android.ui.dialogs.ProgressDialogFragment;
import ru.ok.android.ui.fragments.SaveToFileFragment;
import ru.ok.android.ui.fragments.SaveToFileFragment.SaveToFileFragmentListener;
import ru.ok.android.utils.Storage.External.Application;
import ru.ok.android.utils.localization.LocalizationManager;

public class MediaUploadUtils {
    public static void startCopyFile(@NonNull FragmentActivity activity, @Nullable Fragment targetFragment, boolean fromActivityResult, int requestCode, @NonNull SaveToFileFragment saveToFileFragment, @NonNull SaveToFileFragmentListener listener) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        saveToFileFragment.setListener(listener);
        fragmentTransaction.add((Fragment) saveToFileFragment, "save-file");
        Fragment progressFragment = ProgressDialogFragment.createInstance(LocalizationManager.getString((Context) activity, 2131166096), true);
        progressFragment.setTargetFragment(targetFragment, requestCode);
        Fragment oldDialog = fragmentManager.findFragmentByTag("copy-dialog");
        if (oldDialog != null) {
            fragmentTransaction.remove(oldDialog);
        }
        fragmentTransaction.add(progressFragment, "copy-dialog");
        if (fromActivityResult) {
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            fragmentTransaction.commit();
        }
    }

    private static File getExternalDirForCopiedMedia(@NonNull Context context) {
        return Application.getFilesDir(context);
    }

    private static String getCurrentTimeStampAsString() {
        return Long.toString(System.currentTimeMillis());
    }

    public static SaveToFileFragment createSaveToFileVideoFragment(@NonNull Context context, @NonNull MediaInfo videoMediaInfo, @Nullable Bundle additionalArgs) {
        InputStreamHolder[] inputs;
        File[] destFiles;
        File destDir = getExternalDirForCopiedMedia(context);
        String ext = getFileExtensionWithDotForMediaInfo(videoMediaInfo);
        String stamp = getCurrentTimeStampAsString();
        File destVideoFile = new File(destDir, "upload-video-" + stamp + ext);
        ContentUriStreamHolder inputVideo = new ContentUriStreamHolder(videoMediaInfo.getUri());
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int thumbWidth = Math.max(metrics.widthPixels, metrics.heightPixels) >> 1;
        if (videoMediaInfo.getThumbnailStreamHolder(context.getContentResolver(), thumbWidth, thumbWidth) == null) {
            inputs = new InputStreamHolder[]{inputVideo};
            destFiles = new File[]{destVideoFile};
        } else {
            inputs = new InputStreamHolder[]{inputVideo, videoMediaInfo.getThumbnailStreamHolder(context.getContentResolver(), thumbWidth, thumbWidth)};
            File destThumbFile = new File(destDir, "upload-video-thumb-" + stamp);
            destFiles = new File[]{destVideoFile, destThumbFile};
        }
        return SaveToFileFragment.newInstance(inputs, destFiles, additionalArgs);
    }

    private static String getFileExtensionWithDotForMediaInfo(@NonNull MediaInfo mediaInfo) {
        String ext = FileUtils.getFileExtension(mediaInfo.getDisplayName(), mediaInfo.getMimeType());
        return ext != null ? '.' + ext : "";
    }

    public static SaveToFileFragment createSaveToFileImagesFragment(@NonNull Context context, @NonNull List<MediaInfo> imageMediaInfos, @Nullable Bundle additionalArgs) {
        File destDir = getExternalDirForCopiedMedia(context);
        String stamp = getCurrentTimeStampAsString();
        InputStreamHolder[] inputs = new InputStreamHolder[imageMediaInfos.size()];
        File[] destFiles = new File[imageMediaInfos.size()];
        int count = imageMediaInfos.size();
        for (int i = 0; i < count; i++) {
            MediaInfo imageMediaInfo = (MediaInfo) imageMediaInfos.get(i);
            inputs[i] = new ContentUriStreamHolder(imageMediaInfo.getUri());
            destFiles[i] = new File(destDir, "upload-image-" + stamp + "_" + i + getFileExtensionWithDotForMediaInfo(imageMediaInfo));
        }
        return SaveToFileFragment.newInstance(inputs, destFiles, additionalArgs);
    }

    public static void hideDialogs(@NonNull FragmentManager fragmentManager, @Nullable SaveToFileFragment saveToFileFragment) {
        Fragment dialogFrmnt = fragmentManager.findFragmentByTag("copy-dialog");
        if (dialogFrmnt != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(dialogFrmnt);
            if (saveToFileFragment != null) {
                fragmentTransaction.remove(saveToFileFragment);
            }
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    public static void onResume(@NonNull FragmentManager fragmentManager, @NonNull SaveToFileFragmentListener listener) {
        SaveToFileFragment fragment = (SaveToFileFragment) fragmentManager.findFragmentByTag("save-file");
        if (fragment != null) {
            fragment.setListener(listener);
            if (fragment.isFinished() && !fragment.isResultDelivered()) {
                fragment.deliverResult();
            }
        }
    }

    public static void onCopyProgressCancelled(@NonNull FragmentActivity activity, @NonNull Fragment targetFragment, int alertRequestCode) {
        SaveToFileFragment fragment = (SaveToFileFragment) activity.getSupportFragmentManager().findFragmentByTag("save-file");
        if (fragment != null) {
            fragment.abort();
            activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        showAlert(activity, targetFragment, 2131166095, 2131166094, alertRequestCode);
    }

    public static void showAlert(@NonNull FragmentActivity activity, @Nullable Fragment targetFragment, int titleResId, int messageResId, int requestCode) {
        AlertFragmentDialog dialog = AlertFragmentDialog.newInstance(LocalizationManager.getString((Context) activity, titleResId), LocalizationManager.getString((Context) activity, messageResId), requestCode);
        dialog.setTargetFragment(targetFragment, requestCode);
        showDialog(activity, dialog);
    }

    private static void showDialog(@NonNull FragmentActivity activity, @NonNull DialogFragment dialogFragment) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag("copy-dialog");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.add((Fragment) dialogFragment, "copy-dialog");
        fragmentTransaction.commit();
    }
}
