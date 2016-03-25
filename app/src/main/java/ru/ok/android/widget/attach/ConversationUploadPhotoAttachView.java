package ru.ok.android.widget.attach;

import android.content.Context;
import ru.ok.android.proto.MessagesProto.Message;

public class ConversationUploadPhotoAttachView extends ConversationUploadBaseAttachView {
    public ConversationUploadPhotoAttachView(Context context) {
        super(context, 2130903379, 1.0f);
    }

    protected float getStateAlpha(int state) {
        switch (state) {
            case RECEIVED_VALUE:
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return 0.5f;
            default:
                return 1.0f;
        }
    }
}
