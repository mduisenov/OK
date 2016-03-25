package ru.ok.android.services.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;
import java.util.ArrayList;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.model.pagination.Page;
import ru.ok.android.ui.image.AddImagesActivity;
import ru.ok.android.ui.image.view.AttachPhotosLayerActivity;
import ru.ok.android.ui.image.view.PhotoAlbumsActivity;
import ru.ok.android.ui.image.view.StreamPhotosLayerActivity;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.messages.Attachment;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoInfo;

public final class IntentUtils {
    static Intent createIntentForMessagesFragment(Context context, String conversationId, boolean isError) {
        Intent intent = NavigationHelper.smartLaunchMessagesIntent(context, conversationId);
        intent.putExtra("extra_notification_contains_error", isError);
        return intent;
    }

    public static Intent createIntentToAddImages(Context context, PhotoAlbumInfo albumInfo, int choiceMode, int uploadTarget, boolean doUpload, boolean enableComments, String outDirPathname) {
        Intent intent = new Intent(context, AddImagesActivity.class);
        if (albumInfo != null) {
            intent.putExtra("photoAlbum", albumInfo);
        }
        intent.putExtra("upload_tgt", uploadTarget);
        intent.putExtra("choice_mode", choiceMode);
        intent.putExtra("do_upload", doUpload);
        intent.putExtra("comments_enabled", enableComments);
        intent.putExtra("out_dir", outDirPathname);
        return intent;
    }

    public static Intent createIntentForUserAlbums(Context context, String uid, boolean fromLeftMenuActivity) {
        Intent intent = new Intent(context, PhotoAlbumsActivity.class);
        intent.putExtra("show", 0);
        intent.putExtra("ownrnfo", new PhotoOwner(uid, 0));
        intent.putExtra("key_activity_from_menu", fromLeftMenuActivity);
        return intent;
    }

    public static Intent createIntentForGroupAlbums(Context context, String gid) {
        Intent intent = new Intent(context, PhotoAlbumsActivity.class);
        intent.putExtra("show", 0);
        intent.putExtra("ownrnfo", new PhotoOwner(gid, 1));
        return intent;
    }

    public static Intent createIntentForUserAlbum(Context context, String uid, String aid) {
        return createIntentForAlbum(context, new PhotoOwner(uid, 0), aid);
    }

    public static Intent createIntentForGroupAlbum(Context context, String gid, String aid) {
        return createIntentForAlbum(context, new PhotoOwner(gid, 1), aid);
    }

    private static Intent createIntentForAlbum(Context context, PhotoOwner owner, String aid) {
        Intent intent = new Intent(context, PhotoAlbumsActivity.class);
        intent.putExtra("show", 1);
        intent.putExtra("ownrnfo", owner);
        intent.putExtra("aid", aid);
        return intent;
    }

    public static Intent createIntentForPhotoView(Context context, PhotoOwner photoOwner, String albumId, String photoId, String[] spids, int sourceId) {
        Intent intent = new Intent(context, StreamPhotosLayerActivity.class);
        intent.putExtra("ownerInfo", photoOwner);
        intent.putExtra("albumId", albumId);
        intent.putExtra("photoId", photoId);
        intent.putExtra("sequenceIds", spids);
        intent.putExtra("source", sourceId);
        return intent;
    }

    public static Intent createIntentForPhotoView(Context context, PhotoOwner photoOwner, String albumId, String[] spids, PhotoInfo photoInfo, Page<PhotoInfo> photoInfoPage, int sourceId) {
        Intent intent = new Intent(context, StreamPhotosLayerActivity.class);
        intent.putExtra("ownerInfo", photoOwner);
        intent.putExtra("albumId", albumId);
        intent.putExtra("photoId", photoInfo.getId());
        intent.putExtra("photoInfo", photoInfo);
        intent.putExtra("sequenceIds", spids);
        intent.putExtra("photoInfoPage", photoInfoPage);
        intent.putExtra("source", sourceId);
        return intent;
    }

    public static Intent createIntentForPhotoView(Context context, PhotoOwner photoOwner, String albumId, PhotoInfo photoInfo, Page<PhotoInfo> photoInfoPage, int sourceId) {
        Intent intent = new Intent(context, StreamPhotosLayerActivity.class);
        intent.putExtra("ownerInfo", photoOwner);
        intent.putExtra("albumId", albumId);
        intent.putExtra("photoInfo", photoInfo);
        intent.putExtra("photoInfoPage", photoInfoPage);
        intent.putExtra("source", sourceId);
        return intent;
    }

    public static Intent createIntentForAttachView(Context context, ArrayList<Attachment> attachments, Attachment selected, int sourceId) {
        Intent intent = new Intent(context, AttachPhotosLayerActivity.class);
        intent.putExtra("attachments", attachments);
        intent.putExtra("selected", selected);
        intent.putExtra("source", sourceId);
        return intent;
    }

    public static Intent createIntentForPickPersonalPhoto(Context context) {
        PhotoOwner photoOwner = new PhotoOwner();
        photoOwner.setType(0);
        photoOwner.setId(OdnoklassnikiApplication.getCurrentUser().uid);
        photoOwner.setOwnerInfo(OdnoklassnikiApplication.getCurrentUser());
        Intent intent = new Intent(context, PhotoAlbumsActivity.class);
        intent.putExtra("ownrnfo", photoOwner);
        intent.putExtra("show", 1);
        intent.putExtra("mode", 1);
        intent.putExtra("aclckd", true);
        return intent;
    }

    public static CharSequence createIntentExtrasString(Intent intent) {
        StringBuilder sb = new StringBuilder();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                for (String key : extras.keySet()) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(key).append(" = ").append(extras.get(key));
                }
            }
        }
        sb.insert(0, "[");
        sb.append("]");
        return sb;
    }

    public static void installShortcut(Context context, CharSequence name, Bitmap icon, Intent intent) {
        Intent addIntent = new Intent();
        addIntent.putExtra("android.intent.extra.shortcut.INTENT", intent);
        addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
        addIntent.putExtra("android.intent.extra.shortcut.ICON", icon);
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);
        Toast.makeText(context, LocalizationManager.getString(context, 2131166561), 0).show();
    }
}
