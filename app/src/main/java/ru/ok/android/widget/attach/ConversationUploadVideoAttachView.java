package ru.ok.android.widget.attach;

import android.content.Context;
import android.graphics.drawable.ScaleDrawable;
import android.widget.ImageView;
import ru.ok.android.proto.MessagesProto.Message;

public class ConversationUploadVideoAttachView extends ConversationUploadBaseAttachView {
    public ConversationUploadVideoAttachView(Context context) {
        super(context, 2130903567, 1.7777778f);
        ImageView play = (ImageView) findViewById(2131625421);
        ScaleDrawable scaleDrawable = (ScaleDrawable) play.getDrawable();
        scaleDrawable.getDrawable().setLevel(1);
        play.setImageDrawable(scaleDrawable);
        getImageView().setEmptyImageResId(2130837771);
    }

    protected float getStateAlpha(int state) {
        switch (state) {
            case RECEIVED_VALUE:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return 0.5f;
            default:
                return 1.0f;
        }
    }
}
