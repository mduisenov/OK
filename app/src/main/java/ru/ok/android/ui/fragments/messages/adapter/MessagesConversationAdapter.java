package ru.ok.android.ui.fragments.messages.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.custom.text.OdklUrlsTextView;
import ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.MessagesAdapterListener;
import ru.ok.android.ui.fragments.messages.loaders.data.MessagesBundle;
import ru.ok.android.utils.Utils;
import ru.ok.model.Conversation;
import ru.ok.model.messages.MessageConversation;
import ru.ok.model.messages.MessageConversation.Type;

public final class MessagesConversationAdapter extends MessagesBaseAdapter<MessageConversation, Conversation> {

    private static class ViewSystemHolder {
        private final OdklUrlsTextView message;

        @SuppressLint({"WrongViewCast"})
        public ViewSystemHolder(View view) {
            this.message = (OdklUrlsTextView) view.findViewById(2131624887);
        }
    }

    public MessagesConversationAdapter(Context context, String userUid, MessagesAdapterListener listener) {
        super(context, userUid, listener);
    }

    protected boolean isCommentingAllowed() {
        return ((Conversation) getData().generalInfo).capabilities.canPost;
    }

    public int getViewTypeCount() {
        return super.getViewTypeCount() + 1;
    }

    public int getItemViewType(int position) {
        if (getItem(position).message.type != Type.SYSTEM) {
            return super.getItemViewType(position);
        }
        return super.getViewTypeCount() + 0;
    }

    public boolean isWantToShowNames() {
        MessagesBundle<MessageConversation, Conversation> data = getData();
        return (data == null || data.generalInfo == null || ((Conversation) data.generalInfo).type == Conversation.Type.PRIVATE) ? false : true;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItem(position).message.type != Type.SYSTEM) {
            return super.getView(position, convertView, parent);
        }
        if (convertView == null) {
            convertView = newSystemView(parent);
        }
        bindSystemView((MessageConversation) getItem(position).message, (ViewSystemHolder) convertView.getTag());
        return convertView;
    }

    private void bindSystemView(MessageConversation item, ViewSystemHolder holder) {
        holder.message.setText(Utils.processTextBetweenBraces(getContext(), item.text));
    }

    private View newSystemView(ViewGroup parent) {
        View result = LayoutInflater.from(getContext()).inflate(2130903326, parent, false);
        result.setTag(new ViewSystemHolder(result));
        return result;
    }

    public boolean isReplyDisallowed(boolean callFromAdapter, MessageConversation message) {
        if (callFromAdapter) {
            return true;
        }
        Conversation conversation = getData().generalInfo;
        if (conversation == null || conversation.type == Conversation.Type.PRIVATE || super.isReplyDisallowed(callFromAdapter, message)) {
            return true;
        }
        return false;
    }

    public boolean isUnlikeAllowed() {
        return false;
    }

    public boolean isLikeAllowed() {
        return false;
    }
}
