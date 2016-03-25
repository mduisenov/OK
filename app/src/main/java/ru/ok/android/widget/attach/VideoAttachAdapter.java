package ru.ok.android.widget.attach;

import android.content.Context;
import android.graphics.drawable.ScaleDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.utils.Utils;
import ru.ok.model.messages.Attachment;

public final class VideoAttachAdapter extends ImageAttachAdapter {

    private static class VideoViewHolder extends RemoteHolder {
        @NonNull
        private final TextView videoName;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.videoName = (TextView) itemView.findViewById(C0263R.id.name);
        }
    }

    public VideoAttachAdapter() {
        super(2130903566);
    }

    @NonNull
    protected RemoteHolder doCreateRemoteHolder(@NonNull View itemView) {
        processScaleDrawableHack(itemView);
        return new VideoViewHolder(itemView);
    }

    protected void bindRemoteHolder(@NonNull RemoteHolder holder, @NonNull Attachment attach) {
        super.bindRemoteHolder(holder, attach);
        Utils.setTextViewTextWithVisibility(((VideoViewHolder) holder).videoName, getCount() != 1 ? null : attach.name);
    }

    @NonNull
    protected ConversationUploadBaseAttachView createUploadView(@NonNull Context context) {
        ConversationUploadVideoAttachView result = new ConversationUploadVideoAttachView(context);
        processScaleDrawableHack(result);
        return result;
    }

    private static void processScaleDrawableHack(View view) {
        ImageView imageView = (ImageView) view.findViewById(2131625421);
        ScaleDrawable scaleDrawable = (ScaleDrawable) imageView.getDrawable();
        scaleDrawable.getDrawable().setLevel(1);
        imageView.setImageDrawable(scaleDrawable);
    }
}
