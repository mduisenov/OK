package ru.ok.android.ui.image.view;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import java.io.File;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.web.shortlinks.ShortLink;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.ui.adapters.photo.StreamPhotoLayerAdapter;
import ru.ok.android.utils.Storage.External.User;
import ru.ok.android.utils.UserMedia;
import ru.ok.android.utils.download.DownloadManager;
import ru.ok.android.utils.download.DownloadManager.Request;
import ru.ok.android.utils.download.DownloadManagerCompat;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.photo.PhotoInfo;

public class ViewPhotosOptionsMenuHelper {
    public static void prepareOptionsMenu(Menu menu, String albumId, PhotoInfo photoInfo, PhotoOwner photoOwner) {
        boolean isOwner;
        boolean z;
        boolean z2 = true;
        if (photoInfo == null || !OdnoklassnikiApplication.getCurrentUser().uid.equals(photoInfo.getOwnerId())) {
            isOwner = false;
        } else {
            isOwner = true;
        }
        MenuItem item = menu.findItem(2131625495);
        boolean itemVisible = false;
        if (!(item == null || photoInfo == null)) {
            int photoInfoState = StreamPhotoLayerAdapter.getPhotoInfoState(photoInfo);
            if (photoInfoState == 1) {
                item.setTitle(2131166730);
                itemVisible = true;
            } else if (photoInfoState == 2) {
                item.setTitle(2131166729);
                itemVisible = true;
            }
        }
        item.setVisible(itemVisible);
        item = menu.findItem(2131625496);
        if (!(item == null || photoInfo == null)) {
            TextView tv = (TextView) MenuItemCompat.getActionView(item);
            if (photoInfo.getTagCount() > 0) {
                item.setVisible(true);
                tv.setText(String.valueOf(photoInfo.getTagCount()));
            } else {
                item.setVisible(false);
                tv.setText(null);
            }
        }
        item = menu.findItem(2131625497);
        if (!(item == null || photoInfo == null)) {
            item.setVisible(photoInfo.isModifyAllowed());
        }
        item = menu.findItem(2131625499);
        if (!(item == null || photoInfo == null || albumId == null || photoInfo.isBlocked())) {
            if (TextUtils.equals("tags", albumId) || !isOwner || GifAsMp4PlayerHelper.shouldShowGifAsMp4(photoInfo)) {
                z = false;
            } else {
                z = true;
            }
            item.setVisible(z);
        }
        item = menu.findItem(2131625498);
        if (!(item == null || photoInfo == null)) {
            if (isOwner && TextUtils.isEmpty(albumId) && photoOwner.getType() == 0 && !GifAsMp4PlayerHelper.shouldShowGifAsMp4(photoInfo)) {
                z = true;
            } else {
                z = false;
            }
            item.setVisible(z);
        }
        item = menu.findItem(2131625500);
        if (!(item == null || photoInfo == null)) {
            item.setVisible(photoInfo.isMarkAsSpamAllowed());
        }
        item = menu.findItem(C0263R.id.delete);
        if (!(item == null || photoInfo == null)) {
            item.setVisible(photoInfo.isDeleteAllowed());
        }
        item = menu.findItem(2131625501);
        if (item != null) {
            z = TextUtils.equals(albumId, "tags") && photoOwner.isCurrentUser();
            item.setVisible(z);
        }
        item = menu.findItem(2131624799);
        if (!(item == null || photoInfo == null)) {
            item.setVisible(true);
        }
        item = menu.findItem(2131625454);
        if (photoInfo == null) {
            item.setVisible(false);
            return;
        }
        if (ShortLink.createPhotoLink(photoInfo, photoOwner).isEmpty()) {
            z2 = false;
        }
        item.setVisible(z2);
    }

    public static void savePhotoToFile(Context context, String url, String fileToSaveExtension, int position) {
        File userStorage = User.getUserPicturesDirectory(context);
        if (userStorage == null) {
            Toast.makeText(context, LocalizationManager.getString(context, 2131166093), 1).show();
            return;
        }
        DownloadManager downloadManager = DownloadManagerCompat.getDownloadManager(context);
        Request request = new Request(Uri.parse(url));
        Uri dest = Uri.fromFile(new File(userStorage.getAbsolutePath() + File.separator + UserMedia.generateFileName("IMG", String.valueOf(position), fileToSaveExtension)));
        request.setDestinationUri(dest);
        request.allowScanningByMediaScanner();
        if (VERSION.SDK_INT > 13) {
            request.setNotificationVisibility(1);
        } else {
            request.setNotificationVisibility(0);
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(dest, "image/*");
        request.setIntent(intent);
        downloadManager.enqueue(context, request);
    }

    public static void requestPhotoInfo(Context context, PhotoInfoProvider infoProvider, PhotoInfo photoInfo, PhotoOwner photoOwner) {
        String userId;
        boolean requestUserInfo = true;
        boolean requestGroupInfo = true;
        String groupId = null;
        if (photoOwner.getType() == 0) {
            requestGroupInfo = false;
            userId = photoInfo.getOwnerId();
            if (photoOwner.isCurrentUser() && TextUtils.equals(photoOwner.getId(), userId)) {
                requestUserInfo = false;
            }
        } else {
            groupId = photoOwner.getId();
            userId = photoInfo.getOwnerId();
        }
        infoProvider.requestFullPhotoInfo(photoInfo.getId(), photoInfo.getAlbumId(), groupId, userId, requestUserInfo, true, requestGroupInfo);
    }

    public static void setMainAlbumPhoto(Context context, OnClickListener onPositive) {
        new Builder(context).setTitle(getString(context, 2131165290)).setMessage(getString(context, 2131165290)).setPositiveButton(getString(context, 2131165292), onPositive).setNegativeButton(getString(context, 2131165476), null).show();
    }

    public static void setMainPhoto(Context context, OnClickListener onPositive) {
        new Builder(context).setTitle(getString(context, 2131165291)).setMessage(getString(context, 2131166062)).setPositiveButton(getString(context, 2131165292), onPositive).setNegativeButton(getString(context, 2131165476), null).show();
    }

    public static void deletePhoto(Context context, PhotoInfo photoInfo, OnClickListener onPositive) {
        new Builder(context).setTitle(getString(context, 2131165274)).setMessage(getString(context, 2131165692)).setPositiveButton(getString(context, 2131165671), onPositive).setNegativeButton(getString(context, 2131165476), null).show();
    }

    public static void markPhotoAsSpam(Context context, OnClickListener onPositive) {
        new Builder(context).setTitle(getString(context, 2131165280)).setMessage(getString(context, 2131166066)).setPositiveButton(getString(context, 2131166612), onPositive).setNegativeButton(getString(context, 2131165476), null).show();
    }

    public static void deleteUserPhotoTag(Context context, OnClickListener onPositive) {
        new Builder(context).setMessage(getString(context, 2131165694)).setPositiveButton(getString(context, 2131165671), onPositive).setNegativeButton(getString(context, 2131165476), null).show();
    }

    private static String getString(Context context, int resId) {
        return LocalizationManager.getString(context, resId);
    }
}
