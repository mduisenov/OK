package ru.ok.android.widget.attach;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.model.messages.Attachment;

public abstract class ImageAttachAdapter extends BaseAttachAdapter<Holder> {

    protected static class RemoteHolder extends Holder {
        @NonNull
        public final BaseAttachDraweeView imageView;
        @NonNull
        public final View progressView;
        @NonNull
        public final Button reloadButton;

        public RemoteHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = (BaseAttachDraweeView) itemView.findViewById(C0263R.id.image);
            this.progressView = itemView.findViewById(2131624548);
            this.reloadButton = (Button) itemView.findViewById(2131624679);
        }
    }

    private static class UploadHolder extends Holder {
        @NonNull
        public final ConversationUploadBaseAttachView progressView;

        public UploadHolder(@NonNull View itemView) {
            super(itemView);
            this.progressView = (ConversationUploadBaseAttachView) itemView;
        }
    }

    @NonNull
    protected abstract ConversationUploadBaseAttachView createUploadView(@NonNull Context context);

    public ImageAttachAdapter(int remoteItemLayoutResourceId) {
        super(remoteItemLayoutResourceId);
    }

    public int getItemViewType(int position) {
        Attachment attach = getItem(position);
        return (attach.getLargestSize() == null && TextUtils.isEmpty(attach.id)) ? 0 : 1;
    }

    public int getViewTypeCount() {
        return 2;
    }

    @NonNull
    protected Holder createViewHolder(ViewGroup parent, int position) {
        switch (getItemViewType(position)) {
            case RECEIVED_VALUE:
                return createUploadHolder(parent);
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return createRemoteHolder(parent);
            default:
                throw new IllegalArgumentException(String.format("Not supported view type %s", new Object[]{Integer.valueOf(getItemViewType(position))}));
        }
    }

    @NonNull
    private RemoteHolder createRemoteHolder(@NonNull ViewGroup parent) {
        return doCreateRemoteHolder(LayoutInflater.from(parent.getContext()).inflate(this.remoteItemLayoutResourceId, null));
    }

    @NonNull
    protected RemoteHolder doCreateRemoteHolder(@NonNull View itemView) {
        return new RemoteHolder(itemView);
    }

    @NonNull
    protected UploadHolder createUploadHolder(@NonNull ViewGroup parent) {
        return new UploadHolder(createUploadView(parent.getContext()));
    }

    protected void bindViewHolder(@NonNull Holder holder, int position) {
        int itemViewType = getItemViewType(position);
        Attachment item = getItem(position);
        switch (itemViewType) {
            case RECEIVED_VALUE:
                bindUploadHolder((UploadHolder) holder, item);
            case Message.TEXT_FIELD_NUMBER /*1*/:
                bindRemoteHolder((RemoteHolder) holder, item);
            default:
                throw new IllegalArgumentException(String.format("Not supported view type %s", new Object[]{Integer.valueOf(itemViewType)}));
        }
    }

    protected void bindRemoteHolder(@NonNull RemoteHolder holder, @NonNull Attachment attach) {
        boolean isSingleItem = true;
        if (getCount() != 1) {
            isSingleItem = false;
        }
        int width = isSingleItem ? this.oneColumnSize : this.twoColumnsSize;
        int height = this.twoColumnsSize;
        holder.imageView.setTag(2131624331, String.valueOf(attach._id));
        holder.imageView.setAttachPhoto(holder.progressView, holder.reloadButton, attach, width, height, false);
        holder.imageView.setWidthHeightRatio(getAspectRatio(attach));
        holder.itemView.setFocusable(false);
    }

    private float getAspectRatio(@NonNull Attachment attach) {
        boolean isSingleItem = true;
        if (getCount() != 1) {
            isSingleItem = false;
        }
        if (!isSingleItem || attach.standard_width <= 0 || attach.standard_height <= 0) {
            return 1.0f;
        }
        int rotation = attach.getRotation();
        if (rotation == 0 || rotation == 180) {
            return ((float) attach.standard_width) / ((float) attach.standard_height);
        }
        return ((float) attach.standard_height) / ((float) attach.standard_width);
    }

    protected void bindUploadHolder(@NonNull UploadHolder holder, @NonNull Attachment attachment) {
        int state = 1;
        String status = attachment.getStatus();
        if (status != null) {
            Object obj = -1;
            switch (status.hashCode()) {
                case -1948348832:
                    if (status.equals("UPLOADED")) {
                        obj = null;
                        break;
                    }
                    break;
                case -1107307769:
                    if (status.equals("RECOVERABLE_ERROR")) {
                        obj = 1;
                        break;
                    }
                    break;
                case -269267423:
                    if (status.equals("UPLOADING")) {
                        obj = 3;
                        break;
                    }
                    break;
                case 66247144:
                    if (status.equals("ERROR")) {
                        obj = 2;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case RECEIVED_VALUE:
                    state = 2;
                    break;
                case Message.TEXT_FIELD_NUMBER /*1*/:
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    state = 3;
                    break;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    state = 0;
                    break;
            }
        }
        holder.progressView.setState(state);
        holder.progressView.setAttach(attachment);
        holder.progressView.setAspectRatio(getAspectRatio(attachment));
        holder.progressView.getImageView().setTag(2131624331, String.valueOf(attachment._id));
    }
}
