package ru.ok.android.model.cache.ram;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.proto.MessagesProto.Message.Status;

public final class MessageModel {
    @NonNull
    public final String conversationId;
    public final int databaseId;
    public final long date;
    public final long dateEdited;
    @NonNull
    public final Message message;
    @Nullable
    public final String serverId;
    @NonNull
    public final Status status;
    @NonNull
    public final Status statusEdited;

    public static final class Builder {
        String conversationId;
        int databaseId;
        long date;
        long dateEdited;
        Message message;
        String serverId;
        Status status;
        Status statusEdited;

        public Builder(int databaseId, String serverId, String conversationId, long date, long dateEdited, Status status, Status statusEdited, Message message) {
            this.databaseId = databaseId;
            this.serverId = serverId;
            this.conversationId = conversationId;
            this.date = date;
            this.dateEdited = dateEdited;
            this.status = status;
            this.statusEdited = statusEdited;
            this.message = message;
        }

        public MessageModel build() {
            return new MessageModel(this.databaseId, this.serverId, this.conversationId, this.date, this.dateEdited, this.status, this.statusEdited, this.message);
        }

        public Builder setDatabaseId(int databaseId) {
            this.databaseId = databaseId;
            return this;
        }

        public Builder setServerId(String serverId) {
            this.serverId = serverId;
            return this;
        }

        public Builder setStatus(Status status) {
            this.status = status;
            return this;
        }

        public Builder setStatusEdited(Status status) {
            this.statusEdited = status;
            return this;
        }

        public Builder setMessage(Message message) {
            this.message = message;
            return this;
        }

        public Builder setDate(long date) {
            this.date = date;
            return this;
        }

        public Builder setDateEdited(long dateEdited) {
            this.dateEdited = dateEdited;
            return this;
        }

        public Builder setConversationId(String conversationId) {
            this.conversationId = conversationId;
            return this;
        }
    }

    MessageModel(int databaseId, String serverId, String conversationId, long date, long dateEdited, Status status, Status statusEdited, Message message) {
        this.databaseId = databaseId;
        this.serverId = serverId;
        this.conversationId = conversationId;
        this.date = date;
        this.dateEdited = dateEdited;
        this.status = status;
        this.statusEdited = statusEdited;
        this.message = message;
    }

    public Builder toBuilder() {
        return new Builder(this.databaseId, this.serverId, this.conversationId, this.date, this.dateEdited, this.status, this.statusEdited, this.message);
    }

    public String toString() {
        return "MessageModel{databaseId=" + this.databaseId + ", serverId='" + this.serverId + '\'' + ", conversationId='" + this.conversationId + '\'' + ", date=" + this.date + ", dateEdited=" + this.dateEdited + ", status=" + this.status + ", statusEdited=" + this.statusEdited + ", message=" + this.message + '}';
    }
}
