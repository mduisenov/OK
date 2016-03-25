package ru.ok.android.widget.attach;

import android.content.Context;
import android.support.annotation.NonNull;

public final class PhotoAttachAdapter extends ImageAttachAdapter {
    public PhotoAttachAdapter() {
        super(2130903378);
    }

    @NonNull
    protected ConversationUploadBaseAttachView createUploadView(@NonNull Context context) {
        return new ConversationUploadPhotoAttachView(context);
    }
}
