package ru.ok.android.proto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.base.OfflineTable;
import ru.ok.android.emoji.smiles.SmileTextProcessor;
import ru.ok.android.model.cache.ram.MessageModel;
import ru.ok.android.model.cache.ram.UsersCache;
import ru.ok.android.proto.ConversationProto.Capabilities;
import ru.ok.android.proto.ConversationProto.Capabilities.Builder;
import ru.ok.android.proto.ConversationProto.Participant;
import ru.ok.android.proto.ConversationProto.Participant.Gender;
import ru.ok.android.proto.ConversationProto.Participant.OnlineType;
import ru.ok.android.proto.MessagesProto.Attach;
import ru.ok.android.proto.MessagesProto.Attach.Audio;
import ru.ok.android.proto.MessagesProto.Attach.Photo;
import ru.ok.android.proto.MessagesProto.Attach.Photo.Size;
import ru.ok.android.proto.MessagesProto.Attach.Status;
import ru.ok.android.proto.MessagesProto.Attach.Video;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.proto.MessagesProto.Message.EditInfo;
import ru.ok.android.proto.MessagesProto.Message.ReplyTo;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineData;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.utils.Logger;
import ru.ok.model.Conversation;
import ru.ok.model.ConversationCapabilities;
import ru.ok.model.ConversationParticipant;
import ru.ok.model.ConversationParticipantCapabilities;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.UserInfo.UserOnlineType;
import ru.ok.model.messages.Attachment;
import ru.ok.model.messages.Attachment.AttachmentType;
import ru.ok.model.messages.MessageBase.Flags;
import ru.ok.model.messages.MessageBase.RepliedTo;
import ru.ok.model.messages.MessageConversation;
import ru.ok.model.messages.MessageConversation.Type;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.stickers.Sticker;

public final class ProtoProxy {

    /* renamed from: ru.ok.android.proto.ProtoProxy.1 */
    static /* synthetic */ class C04091 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Status;
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Type;
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Status;
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Type;
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$messages$MessageConversation$Type;

        static {
            $SwitchMap$ru$ok$model$messages$MessageConversation$Type = new int[Type.values().length];
            try {
                $SwitchMap$ru$ok$model$messages$MessageConversation$Type[Type.USER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$model$messages$MessageConversation$Type[Type.STICKER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$model$messages$MessageConversation$Type[Type.SYSTEM.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Type = new int[Message.Type.values().length];
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Type[Message.Type.USER.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Type[Message.Type.STICKER.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Type[Message.Type.SYSTEM.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Type = new int[Attach.Type.values().length];
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Type[Attach.Type.PHOTO.ordinal()] = 1;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Type[Attach.Type.VIDEO.ordinal()] = 2;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Type[Attach.Type.MOVIE.ordinal()] = 3;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Type[Attach.Type.AUDIO_RECORDING.ordinal()] = 4;
            } catch (NoSuchFieldError e10) {
            }
            $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Status = new int[Status.values().length];
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Status[Status.REMOTE.ordinal()] = 1;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Status[Status.WAITING.ordinal()] = 2;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Status[Status.UPLOADING.ordinal()] = 3;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Status[Status.UPLOADED.ordinal()] = 4;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Status[Status.ERROR.ordinal()] = 5;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Status[Status.RECOVERABLE_ERROR.ordinal()] = 6;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Status[Status.RETRY.ordinal()] = 7;
            } catch (NoSuchFieldError e17) {
            }
            $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Status = new int[Message.Status.values().length];
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Status[Message.Status.RECEIVED.ordinal()] = 1;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Status[Message.Status.WAITING.ordinal()] = 2;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Status[Message.Status.SENDING.ordinal()] = 3;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Status[Message.Status.SENT.ordinal()] = 4;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Status[Message.Status.FAILED.ordinal()] = 5;
            } catch (NoSuchFieldError e22) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Status[Message.Status.WAITING_ATTACHMENT.ordinal()] = 6;
            } catch (NoSuchFieldError e23) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Status[Message.Status.UPLOADING_ATTACHMENTS.ordinal()] = 7;
            } catch (NoSuchFieldError e24) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Status[Message.Status.OVERDUE.ordinal()] = 8;
            } catch (NoSuchFieldError e25) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Message$Status[Message.Status.SERVER_ERROR.ordinal()] = 9;
            } catch (NoSuchFieldError e26) {
            }
        }
    }

    public static Conversation proto2Api(ConversationProto.Conversation c) {
        Conversation.Type type = c.getType() == ConversationProto.Conversation.Type.PRIVATE ? Conversation.Type.PRIVATE : Conversation.Type.CHAT;
        Capabilities capabilities = c.getCapabilities();
        ArrayList<ConversationParticipant> participants = new ArrayList();
        for (Participant p : c.getParticipantsList()) {
            participants.add(new ConversationParticipant(p.getId(), p.getLastViewTime(), new ConversationParticipantCapabilities(p.getCanKick())));
        }
        return new Conversation(c.getId(), c.getTopic(), type, c.getOwnerId(), c.getLastMsgTime(), c.getLastViewTime(), c.getNewMessagesCount(), c.getLastMessage(), c.getLastMessageAuthorId(), participants, new ConversationCapabilities(capabilities.getCanDelete(), capabilities.getCanPost(), capabilities.getCantPostBecauseOnlyFriendsAllowed(), capabilities.getCanSendAudio(), capabilities.getCanSendVideo()));
    }

    public static List<ConversationProto.Conversation> api2Proto(List<Conversation> conversations) {
        List<ConversationProto.Conversation> result = new ArrayList();
        for (Conversation conversation : conversations) {
            result.add(api2Proto(conversation));
        }
        return result;
    }

    @NonNull
    public static ConversationProto.Conversation api2Proto(Conversation conversation) {
        String str;
        ConversationProto.Conversation.Type type;
        Builder capb = Capabilities.newBuilder();
        capb.setCanDelete(conversation.capabilities.canDelete).setCanPost(conversation.capabilities.canPost).setCantPostBecauseOnlyFriendsAllowed(conversation.capabilities.cantPostBecauseOnlyFriendsAllowed).setCanSendAudio(conversation.capabilities.canSendAudio).setCanSendVideo(conversation.capabilities.canSendVideo);
        ConversationProto.Conversation.Builder c = ConversationProto.Conversation.newBuilder();
        ConversationProto.Conversation.Builder id = c.setId(conversation.id);
        if (conversation.lastMessage == null) {
            str = "";
        } else {
            str = String.valueOf(SmileTextProcessor.trimSmileSizes(conversation.lastMessage));
        }
        id = id.setLastMessage(str);
        if (conversation.ownerId != null) {
            str = conversation.ownerId;
        } else {
            str = "";
        }
        id = id.setOwnerId(str);
        if (conversation.type == Conversation.Type.PRIVATE) {
            type = ConversationProto.Conversation.Type.PRIVATE;
        } else {
            type = ConversationProto.Conversation.Type.CHAT;
        }
        id = id.setType(type).setLastMsgTime(conversation.lastMsgTime).setLastViewTime(conversation.lastViewTime).setNewMessagesCount(conversation.newMessagesCount);
        if (conversation.lastAuthorId != null) {
            str = conversation.lastAuthorId;
        } else {
            str = "";
        }
        id = id.setLastMessageAuthorId(str).setCapabilities(capb);
        if (conversation.topic != null) {
            str = conversation.topic;
        } else {
            str = "";
        }
        id.setTopic(str);
        Iterator i$ = conversation.participants.iterator();
        while (i$.hasNext()) {
            ConversationParticipant participant = (ConversationParticipant) i$.next();
            Participant.Builder p = Participant.newBuilder();
            p.setId(participant.id).setCanKick(participant.capabilities.canKick).setLastViewTime(participant.lastViewTime);
            UserInfo user = UsersCache.getInstance().getUser(p.getId());
            if (user != null) {
                String picUrl = user.getPicUrl();
                Participant.Builder name = p.setName(user.getAnyName());
                if (picUrl == null) {
                    picUrl = "";
                }
                name.setPicUrl(picUrl).setOnline(api2Proto(user.online)).setGender(api2Proto(user.genderType));
            }
            c.addParticipants(p);
        }
        c.setBuiltTopic(buildTopic(c, conversation.topic));
        return c.build();
    }

    @NonNull
    public static String buildTopic(@NonNull ConversationProto.Conversation.Builder conversation, String topic) {
        if (!TextUtils.isEmpty(topic)) {
            return topic;
        }
        String currentUserId = OdnoklassnikiApplication.getCurrentUser().uid;
        StringBuilder sb = null;
        for (Participant participant : conversation.getParticipantsList()) {
            if (!TextUtils.equals(participant.getId(), currentUserId)) {
                String name = participant.getName();
                if (TextUtils.isEmpty(name)) {
                    continue;
                } else if (conversation.getType() == ConversationProto.Conversation.Type.PRIVATE) {
                    return name;
                } else {
                    if (sb == null) {
                        sb = new StringBuilder();
                    }
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(name);
                }
            }
        }
        return sb != null ? sb.toString() : "";
    }

    public static List<UserInfo> proto2ApiP(@Nullable List<Participant> participants) {
        if (participants == null) {
            return Collections.emptyList();
        }
        List<UserInfo> result = new ArrayList();
        UserInfo.Builder builder = new UserInfo.Builder();
        for (Participant participant : participants) {
            result.add(builder.setUid(participant.getId()).setName(participant.getName()).setPicUrl(participant.getPicUrl()).setOnline(proto2ApiOnline(participant.getOnline())).setLastOnline(participant.getLastOnline()).setGenderType(proto2ApiGender(participant.getGender())).build());
        }
        return result;
    }

    public static OnlineType api2Proto(UserOnlineType online) {
        if (online == null || online == UserOnlineType.OFFLINE) {
            return OnlineType.OFFLINE;
        }
        if (online == UserOnlineType.WEB) {
            return OnlineType.WEB;
        }
        return OnlineType.MOBILE;
    }

    public static Gender api2Proto(UserGenderType gender) {
        if (gender == null || gender == UserGenderType.MALE) {
            return Gender.MALE;
        }
        return Gender.FEMALE;
    }

    public static UserOnlineType proto2ApiOnline(OnlineType online) {
        if (online == OnlineType.OFFLINE) {
            return UserOnlineType.OFFLINE;
        }
        if (online == OnlineType.WEB) {
            return UserOnlineType.WEB;
        }
        return UserOnlineType.MOBILE;
    }

    public static UserGenderType proto2ApiGender(Gender gender) {
        if (gender == Gender.MALE) {
            return UserGenderType.MALE;
        }
        return UserGenderType.FEMALE;
    }

    public static UserInfo proto2Api(Participant p) {
        return new UserInfo.Builder().setUid(p.getId()).setName(p.getName()).setPicUrl(p.getPicUrl()).setOnline(proto2ApiOnline(p.getOnline())).setLastOnline(p.getLastOnline()).setGenderType(proto2ApiGender(p.getGender())).build();
    }

    public static ArrayList<OfflineMessage<MessageConversation>> proto2Api(List<MessageModel> messages) {
        ArrayList<OfflineMessage<MessageConversation>> result = new ArrayList();
        for (MessageModel m : messages) {
            result.add(proto2Api(m));
        }
        return result;
    }

    @NonNull
    public static OfflineMessage<MessageConversation> proto2Api(@NonNull MessageModel m) {
        OfflineTable.Status status;
        Message message = m.message;
        Type type = proto2Api(message.getType());
        Message.Status mStatus = message.hasEditInfo() ? m.statusEdited : m.status;
        if (mStatus == null) {
            mStatus = Message.Status.RECEIVED;
            Logger.m177e("Message status is null: %s", message);
        }
        switch (C04091.$SwitchMap$ru$ok$android$proto$MessagesProto$Message$Status[mStatus.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                status = OfflineTable.Status.RECEIVED;
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                status = OfflineTable.Status.WAITING;
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                status = OfflineTable.Status.SENDING;
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                status = OfflineTable.Status.SENT;
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                status = OfflineTable.Status.FAILED;
                break;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                status = OfflineTable.Status.WAITING_ATTACHMENT;
                break;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                status = OfflineTable.Status.UPLOADING_ATTACHMENTS;
                break;
            case Message.TASKID_FIELD_NUMBER /*8*/:
                status = OfflineTable.Status.OVERDUE;
                break;
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                status = OfflineTable.Status.SERVER_ERROR;
                break;
            default:
                status = OfflineTable.Status.RECEIVED;
                break;
        }
        Message.Capabilities capabilities = message.getCapabilities();
        Flags flags = new Flags(capabilities.getCanLike(), capabilities.getCanMarkAsSpam(), capabilities.getCanDelete(), capabilities.getCanBlock(), false, false, capabilities.getCantEdit());
        Attachment[] attachments = null;
        if (message.getAttachesCount() > 0) {
            List<Attachment> attaches = new ArrayList();
            for (Attach a : message.getAttachesList()) {
                Attachment attach = null;
                Attach.Type attachType = a.getType();
                if (attachType == Attach.Type.PHOTO) {
                    Photo photo = a.getPhoto();
                    attach = new Attachment(0, a.getServerId(), AttachmentType.PHOTO, photo.getWidth(), photo.getHeight(), null, 0);
                    for (Size s : photo.getSizesList()) {
                        attach.sizes.add(new PhotoSize(s.getUrl(), s.getWidth(), s.getHeight(), null));
                    }
                    attach.localId = photo.getLocalId();
                    attach.path = photo.getPath();
                    attach.rotation = photo.getRotation();
                    attach.gifUrl = photo.getGifUrl();
                    attach.mp4Url = photo.getMp4Url();
                } else if (attachType == Attach.Type.VIDEO || attachType == Attach.Type.MOVIE) {
                    attach = new Attachment(0, a.getServerId(), attachType == Attach.Type.VIDEO ? AttachmentType.VIDEO : AttachmentType.MOVIE, 0, 0, null, 0);
                    Video video = a.getVideo();
                    attach.path = video.getPath();
                    attach.thumbnailUrl = video.getThumbnailUrl();
                    attach.localId = video.getLocalId();
                    attach.mediaId = video.getServerId();
                } else if (attachType == Attach.Type.AUDIO_RECORDING) {
                    attach = new Attachment(0, a.getServerId(), AttachmentType.AUDIO_RECORDING, 0, 0, null, 0);
                    Audio audio = a.getAudio();
                    attach.mediaId = audio.getServerId();
                    attach.audioProfile = audio.getAudioProfile();
                    attach.localId = audio.getLocalId();
                    attach.duration = audio.getDuration();
                    attach.path = audio.getPath();
                }
                if (attach != null) {
                    attach.id = a.getServerId();
                    attach._id = a.getUuid();
                    String attachStatus = null;
                    switch (C04091.$SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Status[a.getStatus().ordinal()]) {
                        case Message.TEXT_FIELD_NUMBER /*1*/:
                            attachStatus = "REMOTE";
                            break;
                        case Message.AUTHORID_FIELD_NUMBER /*2*/:
                            attachStatus = "WAITING";
                            break;
                        case Message.TYPE_FIELD_NUMBER /*3*/:
                            attachStatus = "UPLOADING";
                            break;
                        case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                            attachStatus = "UPLOADED";
                            break;
                        case Message.UUID_FIELD_NUMBER /*5*/:
                            attachStatus = "ERROR";
                            break;
                        case Message.REPLYTO_FIELD_NUMBER /*6*/:
                            attachStatus = "RECOVERABLE_ERROR";
                            break;
                        case Message.ATTACHES_FIELD_NUMBER /*7*/:
                            attachStatus = "RETRY";
                            break;
                    }
                    attach.setStatus(attachStatus);
                    attaches.add(attach);
                }
            }
            if (!attaches.isEmpty()) {
                attachments = (Attachment[]) attaches.toArray(new Attachment[attaches.size()]);
            }
        }
        RepliedTo repliedToInfo = null;
        if (message.hasReplyTo()) {
            ReplyTo replyTo = message.getReplyTo();
            RepliedTo repliedTo = new RepliedTo(replyTo.getMessageId(), replyTo.getAuthorId(), null);
        }
        List<Sticker> stickers = null;
        if (m.message.getReplyStickersCount() > 0) {
            stickers = new ArrayList();
            for (Message.Sticker sticker : m.message.getReplyStickersList()) {
                stickers.add(new Sticker(sticker.getCode(), sticker.getPrice(), sticker.getWidth(), sticker.getHeight()));
            }
        }
        return new OfflineMessage(new MessageConversation(m.serverId, message.getText(), message.hasEditInfo() ? message.getEditInfo().getNewText() : null, type, message.getAuthorId(), null, m.date, message.hasEditInfo() ? m.dateEdited : 0, null, flags, repliedToInfo, null, attachments, null, m.conversationId, stickers), new OfflineData(m.databaseId, status, !TextUtils.isEmpty(message.getFailureReason()) ? ErrorType.safeValueOf(message.getFailureReason()) : null));
    }

    @NonNull
    public static List<MessageModel> api2ProtoM(List<MessageConversation> list) {
        List<MessageModel> result = new ArrayList();
        for (MessageConversation m : list) {
            String str;
            MessageModel.Builder message = new MessageModel.Builder();
            message.setConversationId(m.conversationId).setServerId(m.id).setDate(m.date);
            Message.Builder builder = Message.newBuilder();
            Message.Builder text = builder.setText(m.text != null ? m.text : "");
            if (m.authorId != null) {
                str = m.authorId;
            } else {
                str = "";
            }
            text.setAuthorId(str);
            if (!(m.repliedToInfo == null || TextUtils.isEmpty(m.repliedToInfo.messageId))) {
                ReplyTo.Builder replyTo = ReplyTo.newBuilder();
                replyTo.setMessageId(m.repliedToInfo.messageId).setAuthorId(m.repliedToInfo.authorId);
                builder.setReplyTo(replyTo);
            }
            if (m.dateEdited > 0) {
                EditInfo.Builder editInfo = EditInfo.newBuilder();
                editInfo.setNewText(m.textEdited != null ? m.textEdited : "");
                message.setDateEdited(m.dateEdited).setStatusEdited(Message.Status.RECEIVED);
                builder.setEditInfo(editInfo);
            }
            Message.Capabilities.Builder c = Message.Capabilities.newBuilder();
            c.setCanLike(m.flags.likeAllowed).setCanDelete(m.flags.deletionAllowed).setCanMarkAsSpam(m.flags.markAsSpamAllowed).setCanBlock(m.flags.blockAllowed).setCantEdit(m.flags.editDisabled);
            builder.setCapabilities(c);
            builder.setType(api2Proto(m.type));
            message.setStatus(Message.Status.RECEIVED);
            if (m.attachments != null && m.attachments.length > 0) {
                List<? extends Attach> attaches = api2Proto(m.attachments);
                if (!attaches.isEmpty()) {
                    builder.addAllAttaches(attaches);
                }
            }
            if (m.replyStickers != null) {
                for (Sticker sticker : m.replyStickers) {
                    builder.addReplyStickers(Message.Sticker.newBuilder().setCode(sticker.code).setPrice(sticker.price).setWidth(sticker.width).setHeight(sticker.height));
                }
            }
            message.setMessage(builder.build());
            result.add(message.build());
        }
        return result;
    }

    @NonNull
    public static List<Attach> api2Proto(Attachment[] attachments) {
        List<Attach> attaches = new ArrayList();
        for (Attachment a : attachments) {
            Attach attach = api2Proto(a);
            if (attach != null) {
                attaches.add(attach);
            }
        }
        return attaches;
    }

    @Nullable
    public static Attach api2Proto(Attachment a) {
        Attach.Builder attach = Attach.newBuilder();
        if (AttachmentType.PHOTO == a.typeValue) {
            attach.setType(Attach.Type.PHOTO);
            Photo.Builder photo = Photo.newBuilder();
            photo.setWidth(a.standard_width).setHeight(a.standard_height).setLocalId(a.localId != null ? a.localId : "").setPath(a.path != null ? a.path : "").setGifUrl(a.gifUrl != null ? a.gifUrl : "").setMp4Url(a.mp4Url != null ? a.mp4Url : "").setRotation(a.rotation);
            Iterator i$ = a.sizes.iterator();
            while (i$.hasNext()) {
                PhotoSize s = (PhotoSize) i$.next();
                Size.Builder size = Size.newBuilder();
                size.setUrl(s.getUrl() != null ? s.getUrl() : "").setWidth(s.getWidth()).setHeight(s.getHeight());
                photo.addSizes(size);
            }
            attach.setPhoto(photo);
        } else if (AttachmentType.AUDIO_RECORDING == a.typeValue) {
            attach.setType(Attach.Type.AUDIO_RECORDING);
            Audio.Builder audio = Audio.newBuilder();
            audio.setAudioProfile(a.audioProfile != null ? a.audioProfile : "").setDuration(a.duration).setLocalId(a.localId != null ? a.localId : "").setPath(a.path != null ? a.path : "").setServerId(a.mediaId);
            attach.setAudio(audio);
        } else if (AttachmentType.VIDEO != a.typeValue && AttachmentType.MOVIE != a.typeValue) {
            return null;
        } else {
            attach.setType(AttachmentType.VIDEO == a.typeValue ? Attach.Type.VIDEO : Attach.Type.MOVIE);
            Video.Builder video = Video.newBuilder();
            video.setPath(a.path != null ? a.path : "").setLocalId(a.localId != null ? a.localId : "").setThumbnailUrl(a.thumbnailUrl != null ? a.thumbnailUrl : "").setServerId(a.mediaId);
            attach.setVideo(video);
        }
        attach.setServerId(a.id == null ? "" : a.id).setUuid(new Random().nextLong());
        Status status = Status.WAITING;
        if (a.getStatus() != null) {
            String status2 = a.getStatus();
            Object obj = -1;
            switch (status2.hashCode()) {
                case -1948348832:
                    if (status2.equals("UPLOADED")) {
                        obj = 3;
                        break;
                    }
                    break;
                case -1881281466:
                    if (status2.equals("REMOTE")) {
                        obj = null;
                        break;
                    }
                    break;
                case -1107307769:
                    if (status2.equals("RECOVERABLE_ERROR")) {
                        obj = 5;
                        break;
                    }
                    break;
                case -269267423:
                    if (status2.equals("UPLOADING")) {
                        obj = 2;
                        break;
                    }
                    break;
                case 66247144:
                    if (status2.equals("ERROR")) {
                        obj = 4;
                        break;
                    }
                    break;
                case 77867656:
                    if (status2.equals("RETRY")) {
                        obj = 6;
                        break;
                    }
                    break;
                case 1834295853:
                    if (status2.equals("WAITING")) {
                        obj = 1;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case RECEIVED_VALUE:
                    status = Status.REMOTE;
                    break;
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    status = Status.WAITING;
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    status = Status.UPLOADING;
                    break;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    status = Status.UPLOADED;
                    break;
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    status = Status.ERROR;
                    break;
                case Message.UUID_FIELD_NUMBER /*5*/:
                    status = Status.RECOVERABLE_ERROR;
                    break;
                case Message.REPLYTO_FIELD_NUMBER /*6*/:
                    status = Status.RETRY;
                    break;
            }
        }
        attach.setStatus(status);
        return attach.build();
    }

    @Nullable
    public static AttachmentType proto2Api(Attach.Type type) {
        switch (C04091.$SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Type[type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return AttachmentType.PHOTO;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return AttachmentType.VIDEO;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return AttachmentType.MOVIE;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return AttachmentType.AUDIO_RECORDING;
            default:
                return null;
        }
    }

    @NonNull
    private static Type proto2Api(Message.Type messageType) {
        if (messageType == null) {
            return Type.SYSTEM;
        }
        switch (C04091.$SwitchMap$ru$ok$android$proto$MessagesProto$Message$Type[messageType.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return Type.USER;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return Type.STICKER;
            default:
                return Type.SYSTEM;
        }
    }

    @NonNull
    private static Message.Type api2Proto(Type type) {
        if (type == null) {
            return Message.Type.SYSTEM;
        }
        switch (C04091.$SwitchMap$ru$ok$model$messages$MessageConversation$Type[type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return Message.Type.USER;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return Message.Type.STICKER;
            default:
                return Message.Type.SYSTEM;
        }
    }
}
