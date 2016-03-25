package ru.ok.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Iterator;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.ui.custom.mediacomposer.EditablePhotoItem;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaTopicMessage;
import ru.ok.android.ui.image.AddImagesActivity;
import ru.ok.android.utils.localization.LocalizationManager;

public class ShareImageTopicActivity extends AddImagesActivity {
    protected void returnImages(ArrayList<ImageEditInfo> images) {
        startActivity(new Intent().setClassName(OdnoklassnikiApplication.getContext(), "ru.ok.android.ui.activity.MediaComposerUserActivity").putExtra("media_topic", buildMediaTopicWithImages(images)).putExtra("to_status", false));
        finish();
    }

    @NonNull
    private MediaTopicMessage buildMediaTopicWithImages(ArrayList<ImageEditInfo> images) {
        MediaTopicMessage mediaTopic = new MediaTopicMessage();
        if (images != null) {
            Iterator i$ = images.iterator();
            while (i$.hasNext()) {
                mediaTopic.add(new EditablePhotoItem((ImageEditInfo) i$.next()));
            }
        }
        mediaTopic.add(MediaItem.emptyText());
        return mediaTopic;
    }

    protected Intent getPrepareImagesIntent(Intent originalIntent, ArrayList<ImageEditInfo> imagesToEdit) {
        return super.getPrepareImagesIntent(originalIntent.putExtra("comments_enabled", false), imagesToEdit).putExtra("upload_btn_text", LocalizationManager.getString((Context) this, 2131165355)).putExtra("can_create_album", false).putExtra("can_select_album", false).putExtra("actionbar_title", LocalizationManager.getString((Context) this, 2131166557));
    }
}
