package ru.ok.android.proto;

import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessage.BuilderParent;
import com.google.protobuf.GeneratedMessage.FieldAccessorTable;
import com.google.protobuf.Internal.EnumLiteMap;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RepeatedFieldBuilder;
import com.google.protobuf.SingleFieldBuilder;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.ok.android.C0206R;

public final class ConversationProto {
    private static FileDescriptor descriptor;
    private static Descriptor internal_static_ru_ok_android_proto_Capabilities_descriptor;
    private static FieldAccessorTable f74xeb513e99;
    private static Descriptor internal_static_ru_ok_android_proto_Conversation_descriptor;
    private static FieldAccessorTable f75xd4f59cac;
    private static Descriptor internal_static_ru_ok_android_proto_Participant_descriptor;
    private static FieldAccessorTable f76x41a9c696;

    /* renamed from: ru.ok.android.proto.ConversationProto.1 */
    static class C03871 implements InternalDescriptorAssigner {
        C03871() {
        }

        public ExtensionRegistry assignDescriptors(FileDescriptor root) {
            ConversationProto.descriptor = root;
            return null;
        }
    }

    public interface CapabilitiesOrBuilder extends MessageOrBuilder {
        boolean getCanDelete();

        boolean getCanPost();

        boolean getCanSendAudio();

        boolean getCanSendVideo();

        boolean getCantPostBecauseOnlyFriendsAllowed();
    }

    public static final class Capabilities extends GeneratedMessage implements CapabilitiesOrBuilder {
        public static final int CANDELETE_FIELD_NUMBER = 1;
        public static final int CANPOST_FIELD_NUMBER = 2;
        public static final int CANSENDAUDIO_FIELD_NUMBER = 7;
        public static final int CANSENDVIDEO_FIELD_NUMBER = 8;
        public static final int CANTPOSTBECAUSEONLYFRIENDSALLOWED_FIELD_NUMBER = 3;
        private static final Capabilities DEFAULT_INSTANCE;
        public static final Parser<Capabilities> PARSER;
        private static final long serialVersionUID = 0;
        private boolean canDelete_;
        private boolean canPost_;
        private boolean canSendAudio_;
        private boolean canSendVideo_;
        private boolean cantPostBecauseOnlyFriendsAllowed_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;

        /* renamed from: ru.ok.android.proto.ConversationProto.Capabilities.1 */
        static class C03881 extends AbstractParser<Capabilities> {
            C03881() {
            }

            public Capabilities parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                try {
                    return new Capabilities(extensionRegistry, null);
                } catch (RuntimeException e) {
                    if (e.getCause() instanceof InvalidProtocolBufferException) {
                        throw ((InvalidProtocolBufferException) e.getCause());
                    }
                    throw e;
                }
            }
        }

        public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<Builder> implements CapabilitiesOrBuilder {
            private boolean canDelete_;
            private boolean canPost_;
            private boolean canSendAudio_;
            private boolean canSendVideo_;
            private boolean cantPostBecauseOnlyFriendsAllowed_;

            public static final Descriptor getDescriptor() {
                return ConversationProto.internal_static_ru_ok_android_proto_Capabilities_descriptor;
            }

            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ConversationProto.f74xeb513e99.ensureFieldAccessorsInitialized(Capabilities.class, Builder.class);
            }

            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (!Capabilities.alwaysUseFieldBuilders) {
                }
            }

            public Builder clear() {
                super.clear();
                this.canDelete_ = false;
                this.canPost_ = false;
                this.cantPostBecauseOnlyFriendsAllowed_ = false;
                this.canSendAudio_ = false;
                this.canSendVideo_ = false;
                return this;
            }

            public Descriptor getDescriptorForType() {
                return ConversationProto.internal_static_ru_ok_android_proto_Capabilities_descriptor;
            }

            public Capabilities getDefaultInstanceForType() {
                return Capabilities.getDefaultInstance();
            }

            public Capabilities build() {
                Capabilities result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public Capabilities buildPartial() {
                Capabilities result = new Capabilities(null);
                result.canDelete_ = this.canDelete_;
                result.canPost_ = this.canPost_;
                result.cantPostBecauseOnlyFriendsAllowed_ = this.cantPostBecauseOnlyFriendsAllowed_;
                result.canSendAudio_ = this.canSendAudio_;
                result.canSendVideo_ = this.canSendVideo_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof Capabilities) {
                    return mergeFrom((Capabilities) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Capabilities other) {
                if (other != Capabilities.getDefaultInstance()) {
                    if (other.getCanDelete()) {
                        setCanDelete(other.getCanDelete());
                    }
                    if (other.getCanPost()) {
                        setCanPost(other.getCanPost());
                    }
                    if (other.getCantPostBecauseOnlyFriendsAllowed()) {
                        setCantPostBecauseOnlyFriendsAllowed(other.getCantPostBecauseOnlyFriendsAllowed());
                    }
                    if (other.getCanSendAudio()) {
                        setCanSendAudio(other.getCanSendAudio());
                    }
                    if (other.getCanSendVideo()) {
                        setCanSendVideo(other.getCanSendVideo());
                    }
                    onChanged();
                }
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Capabilities parsedMessage = null;
                try {
                    parsedMessage = (Capabilities) Capabilities.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    parsedMessage = (Capabilities) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            public boolean getCanDelete() {
                return this.canDelete_;
            }

            public Builder setCanDelete(boolean value) {
                this.canDelete_ = value;
                onChanged();
                return this;
            }

            public Builder clearCanDelete() {
                this.canDelete_ = false;
                onChanged();
                return this;
            }

            public boolean getCanPost() {
                return this.canPost_;
            }

            public Builder setCanPost(boolean value) {
                this.canPost_ = value;
                onChanged();
                return this;
            }

            public Builder clearCanPost() {
                this.canPost_ = false;
                onChanged();
                return this;
            }

            public boolean getCantPostBecauseOnlyFriendsAllowed() {
                return this.cantPostBecauseOnlyFriendsAllowed_;
            }

            public Builder setCantPostBecauseOnlyFriendsAllowed(boolean value) {
                this.cantPostBecauseOnlyFriendsAllowed_ = value;
                onChanged();
                return this;
            }

            public Builder clearCantPostBecauseOnlyFriendsAllowed() {
                this.cantPostBecauseOnlyFriendsAllowed_ = false;
                onChanged();
                return this;
            }

            public boolean getCanSendAudio() {
                return this.canSendAudio_;
            }

            public Builder setCanSendAudio(boolean value) {
                this.canSendAudio_ = value;
                onChanged();
                return this;
            }

            public Builder clearCanSendAudio() {
                this.canSendAudio_ = false;
                onChanged();
                return this;
            }

            public boolean getCanSendVideo() {
                return this.canSendVideo_;
            }

            public Builder setCanSendVideo(boolean value) {
                this.canSendVideo_ = value;
                onChanged();
                return this;
            }

            public Builder clearCanSendVideo() {
                this.canSendVideo_ = false;
                onChanged();
                return this;
            }

            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return this;
            }
        }

        private Capabilities(com.google.protobuf.GeneratedMessage.Builder builder) {
            super(builder);
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
        }

        private Capabilities() {
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
            this.canDelete_ = false;
            this.canPost_ = false;
            this.cantPostBecauseOnlyFriendsAllowed_ = false;
            this.canSendAudio_ = false;
            this.canSendVideo_ = false;
        }

        public final UnknownFieldSet getUnknownFields() {
            return UnknownFieldSet.getDefaultInstance();
        }

        private Capabilities(CodedInputStream input, ExtensionRegistryLite extensionRegistry) {
            this();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    switch (tag) {
                        case RECEIVED_VALUE:
                            done = true;
                            break;
                        case CANSENDVIDEO_FIELD_NUMBER /*8*/:
                            this.canDelete_ = input.readBool();
                            break;
                        case C0206R.styleable.Toolbar_titleMarginBottom /*16*/:
                            this.canPost_ = input.readBool();
                            break;
                        case C0206R.styleable.Toolbar_subtitleTextColor /*24*/:
                            this.cantPostBecauseOnlyFriendsAllowed_ = input.readBool();
                            break;
                        case C0206R.styleable.Theme_dividerHorizontal /*56*/:
                            this.canSendAudio_ = input.readBool();
                            break;
                        case C0206R.styleable.Theme_textAppearanceSearchResultTitle /*64*/:
                            this.canSendVideo_ = input.readBool();
                            break;
                        default:
                            if (!input.skipField(tag)) {
                                done = true;
                                break;
                            }
                            break;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e.setUnfinishedMessage(this));
                } catch (IOException e2) {
                    throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                } catch (Throwable th) {
                    makeExtensionsImmutable();
                }
            }
            makeExtensionsImmutable();
        }

        public static final Descriptor getDescriptor() {
            return ConversationProto.internal_static_ru_ok_android_proto_Capabilities_descriptor;
        }

        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ConversationProto.f74xeb513e99.ensureFieldAccessorsInitialized(Capabilities.class, Builder.class);
        }

        public boolean getCanDelete() {
            return this.canDelete_;
        }

        public boolean getCanPost() {
            return this.canPost_;
        }

        public boolean getCantPostBecauseOnlyFriendsAllowed() {
            return this.cantPostBecauseOnlyFriendsAllowed_;
        }

        public boolean getCanSendAudio() {
            return this.canSendAudio_;
        }

        public boolean getCanSendVideo() {
            return this.canSendVideo_;
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == (byte) 1) {
                return true;
            }
            if (isInitialized == null) {
                return false;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if (this.canDelete_) {
                output.writeBool(CANDELETE_FIELD_NUMBER, this.canDelete_);
            }
            if (this.canPost_) {
                output.writeBool(CANPOST_FIELD_NUMBER, this.canPost_);
            }
            if (this.cantPostBecauseOnlyFriendsAllowed_) {
                output.writeBool(CANTPOSTBECAUSEONLYFRIENDSALLOWED_FIELD_NUMBER, this.cantPostBecauseOnlyFriendsAllowed_);
            }
            if (this.canSendAudio_) {
                output.writeBool(CANSENDAUDIO_FIELD_NUMBER, this.canSendAudio_);
            }
            if (this.canSendVideo_) {
                output.writeBool(CANSENDVIDEO_FIELD_NUMBER, this.canSendVideo_);
            }
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if (this.canDelete_) {
                size = 0 + CodedOutputStream.computeBoolSize(CANDELETE_FIELD_NUMBER, this.canDelete_);
            }
            if (this.canPost_) {
                size += CodedOutputStream.computeBoolSize(CANPOST_FIELD_NUMBER, this.canPost_);
            }
            if (this.cantPostBecauseOnlyFriendsAllowed_) {
                size += CodedOutputStream.computeBoolSize(CANTPOSTBECAUSEONLYFRIENDSALLOWED_FIELD_NUMBER, this.cantPostBecauseOnlyFriendsAllowed_);
            }
            if (this.canSendAudio_) {
                size += CodedOutputStream.computeBoolSize(CANSENDAUDIO_FIELD_NUMBER, this.canSendAudio_);
            }
            if (this.canSendVideo_) {
                size += CodedOutputStream.computeBoolSize(CANSENDVIDEO_FIELD_NUMBER, this.canSendVideo_);
            }
            this.memoizedSerializedSize = size;
            return size;
        }

        public static Capabilities parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (Capabilities) PARSER.parseFrom(data);
        }

        public static Capabilities parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Capabilities) PARSER.parseFrom(data, extensionRegistry);
        }

        public static Capabilities parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (Capabilities) PARSER.parseFrom(data);
        }

        public static Capabilities parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Capabilities) PARSER.parseFrom(data, extensionRegistry);
        }

        public static Capabilities parseFrom(InputStream input) throws IOException {
            return (Capabilities) PARSER.parseFrom(input);
        }

        public static Capabilities parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Capabilities) PARSER.parseFrom(input, extensionRegistry);
        }

        public static Capabilities parseDelimitedFrom(InputStream input) throws IOException {
            return (Capabilities) PARSER.parseDelimitedFrom(input);
        }

        public static Capabilities parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Capabilities) PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static Capabilities parseFrom(CodedInputStream input) throws IOException {
            return (Capabilities) PARSER.parseFrom(input);
        }

        public static Capabilities parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Capabilities) PARSER.parseFrom(input, extensionRegistry);
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Capabilities prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        protected Builder newBuilderForType(BuilderParent parent) {
            return new Builder(null);
        }

        static {
            DEFAULT_INSTANCE = new Capabilities();
            PARSER = new C03881();
        }

        public static Capabilities getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public Parser<Capabilities> getParserForType() {
            return PARSER;
        }

        public Capabilities getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public interface ConversationOrBuilder extends MessageOrBuilder {
        String getBuiltTopic();

        ByteString getBuiltTopicBytes();

        Capabilities getCapabilities();

        CapabilitiesOrBuilder getCapabilitiesOrBuilder();

        String getId();

        ByteString getIdBytes();

        String getLastMessage();

        String getLastMessageAuthorId();

        ByteString getLastMessageAuthorIdBytes();

        ByteString getLastMessageBytes();

        long getLastMsgTime();

        long getLastViewTime();

        String getLastViewedMessageId();

        ByteString getLastViewedMessageIdBytes();

        int getNewMessagesCount();

        String getOwnerId();

        ByteString getOwnerIdBytes();

        Participant getParticipants(int i);

        int getParticipantsCount();

        List<Participant> getParticipantsList();

        ParticipantOrBuilder getParticipantsOrBuilder(int i);

        List<? extends ParticipantOrBuilder> getParticipantsOrBuilderList();

        String getTopic();

        ByteString getTopicBytes();

        Type getType();

        int getTypeValue();

        boolean hasCapabilities();
    }

    public static final class Conversation extends GeneratedMessage implements ConversationOrBuilder {
        public static final int BUILTTOPIC_FIELD_NUMBER = 3;
        public static final int CAPABILITIES_FIELD_NUMBER = 10;
        private static final Conversation DEFAULT_INSTANCE;
        public static final int ID_FIELD_NUMBER = 1;
        public static final int LASTMESSAGEAUTHORID_FIELD_NUMBER = 5;
        public static final int LASTMESSAGE_FIELD_NUMBER = 4;
        public static final int LASTMSGTIME_FIELD_NUMBER = 7;
        public static final int LASTVIEWEDMESSAGEID_FIELD_NUMBER = 11;
        public static final int LASTVIEWTIME_FIELD_NUMBER = 8;
        public static final int NEWMESSAGESCOUNT_FIELD_NUMBER = 9;
        public static final int OWNERID_FIELD_NUMBER = 13;
        public static final Parser<Conversation> PARSER;
        public static final int PARTICIPANTS_FIELD_NUMBER = 12;
        public static final int TOPIC_FIELD_NUMBER = 2;
        public static final int TYPE_FIELD_NUMBER = 6;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private volatile Object builtTopic_;
        private Capabilities capabilities_;
        private volatile Object id_;
        private volatile Object lastMessageAuthorId_;
        private volatile Object lastMessage_;
        private long lastMsgTime_;
        private long lastViewTime_;
        private volatile Object lastViewedMessageId_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private int newMessagesCount_;
        private volatile Object ownerId_;
        private List<Participant> participants_;
        private volatile Object topic_;
        private int type_;

        /* renamed from: ru.ok.android.proto.ConversationProto.Conversation.1 */
        static class C03891 extends AbstractParser<Conversation> {
            C03891() {
            }

            public Conversation parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                try {
                    return new Conversation(extensionRegistry, null);
                } catch (RuntimeException e) {
                    if (e.getCause() instanceof InvalidProtocolBufferException) {
                        throw ((InvalidProtocolBufferException) e.getCause());
                    }
                    throw e;
                }
            }
        }

        public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<Builder> implements ConversationOrBuilder {
            private int bitField0_;
            private Object builtTopic_;
            private SingleFieldBuilder<Capabilities, Builder, CapabilitiesOrBuilder> capabilitiesBuilder_;
            private Capabilities capabilities_;
            private Object id_;
            private Object lastMessageAuthorId_;
            private Object lastMessage_;
            private long lastMsgTime_;
            private long lastViewTime_;
            private Object lastViewedMessageId_;
            private int newMessagesCount_;
            private Object ownerId_;
            private RepeatedFieldBuilder<Participant, Builder, ParticipantOrBuilder> participantsBuilder_;
            private List<Participant> participants_;
            private Object topic_;
            private int type_;

            public static final Descriptor getDescriptor() {
                return ConversationProto.internal_static_ru_ok_android_proto_Conversation_descriptor;
            }

            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ConversationProto.f75xd4f59cac.ensureFieldAccessorsInitialized(Conversation.class, Builder.class);
            }

            private Builder() {
                this.id_ = "";
                this.topic_ = "";
                this.builtTopic_ = "";
                this.lastMessage_ = "";
                this.lastMessageAuthorId_ = "";
                this.type_ = 0;
                this.capabilities_ = null;
                this.lastViewedMessageId_ = "";
                this.participants_ = Collections.emptyList();
                this.ownerId_ = "";
                maybeForceBuilderInitialization();
            }

            private Builder(BuilderParent parent) {
                super(parent);
                this.id_ = "";
                this.topic_ = "";
                this.builtTopic_ = "";
                this.lastMessage_ = "";
                this.lastMessageAuthorId_ = "";
                this.type_ = 0;
                this.capabilities_ = null;
                this.lastViewedMessageId_ = "";
                this.participants_ = Collections.emptyList();
                this.ownerId_ = "";
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (Conversation.alwaysUseFieldBuilders) {
                    getParticipantsFieldBuilder();
                }
            }

            public Builder clear() {
                super.clear();
                this.id_ = "";
                this.topic_ = "";
                this.builtTopic_ = "";
                this.lastMessage_ = "";
                this.lastMessageAuthorId_ = "";
                this.type_ = 0;
                this.lastMsgTime_ = 0;
                this.lastViewTime_ = 0;
                this.newMessagesCount_ = 0;
                if (this.capabilitiesBuilder_ == null) {
                    this.capabilities_ = null;
                } else {
                    this.capabilities_ = null;
                    this.capabilitiesBuilder_ = null;
                }
                this.lastViewedMessageId_ = "";
                if (this.participantsBuilder_ == null) {
                    this.participants_ = Collections.emptyList();
                    this.bitField0_ &= -2049;
                } else {
                    this.participantsBuilder_.clear();
                }
                this.ownerId_ = "";
                return this;
            }

            public Descriptor getDescriptorForType() {
                return ConversationProto.internal_static_ru_ok_android_proto_Conversation_descriptor;
            }

            public Conversation getDefaultInstanceForType() {
                return Conversation.getDefaultInstance();
            }

            public Conversation build() {
                Conversation result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public Conversation buildPartial() {
                Conversation result = new Conversation(null);
                int from_bitField0_ = this.bitField0_;
                result.id_ = this.id_;
                result.topic_ = this.topic_;
                result.builtTopic_ = this.builtTopic_;
                result.lastMessage_ = this.lastMessage_;
                result.lastMessageAuthorId_ = this.lastMessageAuthorId_;
                result.type_ = this.type_;
                result.lastMsgTime_ = this.lastMsgTime_;
                result.lastViewTime_ = this.lastViewTime_;
                result.newMessagesCount_ = this.newMessagesCount_;
                if (this.capabilitiesBuilder_ == null) {
                    result.capabilities_ = this.capabilities_;
                } else {
                    result.capabilities_ = (Capabilities) this.capabilitiesBuilder_.build();
                }
                result.lastViewedMessageId_ = this.lastViewedMessageId_;
                if (this.participantsBuilder_ == null) {
                    if ((this.bitField0_ & 2048) == 2048) {
                        this.participants_ = Collections.unmodifiableList(this.participants_);
                        this.bitField0_ &= -2049;
                    }
                    result.participants_ = this.participants_;
                } else {
                    result.participants_ = this.participantsBuilder_.build();
                }
                result.ownerId_ = this.ownerId_;
                result.bitField0_ = 0;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof Conversation) {
                    return mergeFrom((Conversation) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Conversation other) {
                RepeatedFieldBuilder repeatedFieldBuilder = null;
                if (other != Conversation.getDefaultInstance()) {
                    if (!other.getId().isEmpty()) {
                        this.id_ = other.id_;
                        onChanged();
                    }
                    if (!other.getTopic().isEmpty()) {
                        this.topic_ = other.topic_;
                        onChanged();
                    }
                    if (!other.getBuiltTopic().isEmpty()) {
                        this.builtTopic_ = other.builtTopic_;
                        onChanged();
                    }
                    if (!other.getLastMessage().isEmpty()) {
                        this.lastMessage_ = other.lastMessage_;
                        onChanged();
                    }
                    if (!other.getLastMessageAuthorId().isEmpty()) {
                        this.lastMessageAuthorId_ = other.lastMessageAuthorId_;
                        onChanged();
                    }
                    if (other.type_ != 0) {
                        setTypeValue(other.getTypeValue());
                    }
                    if (other.getLastMsgTime() != 0) {
                        setLastMsgTime(other.getLastMsgTime());
                    }
                    if (other.getLastViewTime() != 0) {
                        setLastViewTime(other.getLastViewTime());
                    }
                    if (other.getNewMessagesCount() != 0) {
                        setNewMessagesCount(other.getNewMessagesCount());
                    }
                    if (other.hasCapabilities()) {
                        mergeCapabilities(other.getCapabilities());
                    }
                    if (!other.getLastViewedMessageId().isEmpty()) {
                        this.lastViewedMessageId_ = other.lastViewedMessageId_;
                        onChanged();
                    }
                    if (this.participantsBuilder_ == null) {
                        if (!other.participants_.isEmpty()) {
                            if (this.participants_.isEmpty()) {
                                this.participants_ = other.participants_;
                                this.bitField0_ &= -2049;
                            } else {
                                ensureParticipantsIsMutable();
                                this.participants_.addAll(other.participants_);
                            }
                            onChanged();
                        }
                    } else if (!other.participants_.isEmpty()) {
                        if (this.participantsBuilder_.isEmpty()) {
                            this.participantsBuilder_.dispose();
                            this.participantsBuilder_ = null;
                            this.participants_ = other.participants_;
                            this.bitField0_ &= -2049;
                            if (Conversation.alwaysUseFieldBuilders) {
                                repeatedFieldBuilder = getParticipantsFieldBuilder();
                            }
                            this.participantsBuilder_ = repeatedFieldBuilder;
                        } else {
                            this.participantsBuilder_.addAllMessages(other.participants_);
                        }
                    }
                    if (!other.getOwnerId().isEmpty()) {
                        this.ownerId_ = other.ownerId_;
                        onChanged();
                    }
                    onChanged();
                }
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Conversation parsedMessage = null;
                try {
                    parsedMessage = (Conversation) Conversation.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    parsedMessage = (Conversation) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            public String getId() {
                ByteString ref = this.id_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                ByteString bs = ref;
                String s = bs.toStringUtf8();
                if (!bs.isValidUtf8()) {
                    return s;
                }
                this.id_ = s;
                return s;
            }

            public ByteString getIdBytes() {
                Object ref = this.id_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.id_ = b;
                return b;
            }

            public Builder setId(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.id_ = value;
                onChanged();
                return this;
            }

            public Builder clearId() {
                this.id_ = Conversation.getDefaultInstance().getId();
                onChanged();
                return this;
            }

            public Builder setIdBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.id_ = value;
                onChanged();
                return this;
            }

            public String getTopic() {
                ByteString ref = this.topic_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                ByteString bs = ref;
                String s = bs.toStringUtf8();
                if (!bs.isValidUtf8()) {
                    return s;
                }
                this.topic_ = s;
                return s;
            }

            public ByteString getTopicBytes() {
                Object ref = this.topic_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.topic_ = b;
                return b;
            }

            public Builder setTopic(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.topic_ = value;
                onChanged();
                return this;
            }

            public Builder clearTopic() {
                this.topic_ = Conversation.getDefaultInstance().getTopic();
                onChanged();
                return this;
            }

            public Builder setTopicBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.topic_ = value;
                onChanged();
                return this;
            }

            public String getBuiltTopic() {
                ByteString ref = this.builtTopic_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                ByteString bs = ref;
                String s = bs.toStringUtf8();
                if (!bs.isValidUtf8()) {
                    return s;
                }
                this.builtTopic_ = s;
                return s;
            }

            public ByteString getBuiltTopicBytes() {
                Object ref = this.builtTopic_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.builtTopic_ = b;
                return b;
            }

            public Builder setBuiltTopic(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.builtTopic_ = value;
                onChanged();
                return this;
            }

            public Builder clearBuiltTopic() {
                this.builtTopic_ = Conversation.getDefaultInstance().getBuiltTopic();
                onChanged();
                return this;
            }

            public Builder setBuiltTopicBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.builtTopic_ = value;
                onChanged();
                return this;
            }

            public String getLastMessage() {
                ByteString ref = this.lastMessage_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                ByteString bs = ref;
                String s = bs.toStringUtf8();
                if (!bs.isValidUtf8()) {
                    return s;
                }
                this.lastMessage_ = s;
                return s;
            }

            public ByteString getLastMessageBytes() {
                Object ref = this.lastMessage_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.lastMessage_ = b;
                return b;
            }

            public Builder setLastMessage(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.lastMessage_ = value;
                onChanged();
                return this;
            }

            public Builder clearLastMessage() {
                this.lastMessage_ = Conversation.getDefaultInstance().getLastMessage();
                onChanged();
                return this;
            }

            public Builder setLastMessageBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.lastMessage_ = value;
                onChanged();
                return this;
            }

            public String getLastMessageAuthorId() {
                ByteString ref = this.lastMessageAuthorId_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                ByteString bs = ref;
                String s = bs.toStringUtf8();
                if (!bs.isValidUtf8()) {
                    return s;
                }
                this.lastMessageAuthorId_ = s;
                return s;
            }

            public ByteString getLastMessageAuthorIdBytes() {
                Object ref = this.lastMessageAuthorId_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.lastMessageAuthorId_ = b;
                return b;
            }

            public Builder setLastMessageAuthorId(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.lastMessageAuthorId_ = value;
                onChanged();
                return this;
            }

            public Builder clearLastMessageAuthorId() {
                this.lastMessageAuthorId_ = Conversation.getDefaultInstance().getLastMessageAuthorId();
                onChanged();
                return this;
            }

            public Builder setLastMessageAuthorIdBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.lastMessageAuthorId_ = value;
                onChanged();
                return this;
            }

            public int getTypeValue() {
                return this.type_;
            }

            public Builder setTypeValue(int value) {
                this.type_ = value;
                onChanged();
                return this;
            }

            public Type getType() {
                Type result = Type.valueOf(this.type_);
                return result == null ? Type.UNRECOGNIZED : result;
            }

            public Builder setType(Type value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.type_ = value.getNumber();
                onChanged();
                return this;
            }

            public Builder clearType() {
                this.type_ = 0;
                onChanged();
                return this;
            }

            public long getLastMsgTime() {
                return this.lastMsgTime_;
            }

            public Builder setLastMsgTime(long value) {
                this.lastMsgTime_ = value;
                onChanged();
                return this;
            }

            public Builder clearLastMsgTime() {
                this.lastMsgTime_ = 0;
                onChanged();
                return this;
            }

            public long getLastViewTime() {
                return this.lastViewTime_;
            }

            public Builder setLastViewTime(long value) {
                this.lastViewTime_ = value;
                onChanged();
                return this;
            }

            public Builder clearLastViewTime() {
                this.lastViewTime_ = 0;
                onChanged();
                return this;
            }

            public int getNewMessagesCount() {
                return this.newMessagesCount_;
            }

            public Builder setNewMessagesCount(int value) {
                this.newMessagesCount_ = value;
                onChanged();
                return this;
            }

            public Builder clearNewMessagesCount() {
                this.newMessagesCount_ = 0;
                onChanged();
                return this;
            }

            public boolean hasCapabilities() {
                return (this.capabilitiesBuilder_ == null && this.capabilities_ == null) ? false : true;
            }

            public Capabilities getCapabilities() {
                if (this.capabilitiesBuilder_ == null) {
                    return this.capabilities_ == null ? Capabilities.getDefaultInstance() : this.capabilities_;
                } else {
                    return (Capabilities) this.capabilitiesBuilder_.getMessage();
                }
            }

            public Builder setCapabilities(Capabilities value) {
                if (this.capabilitiesBuilder_ != null) {
                    this.capabilitiesBuilder_.setMessage(value);
                } else if (value == null) {
                    throw new NullPointerException();
                } else {
                    this.capabilities_ = value;
                    onChanged();
                }
                return this;
            }

            public Builder setCapabilities(Builder builderForValue) {
                if (this.capabilitiesBuilder_ == null) {
                    this.capabilities_ = builderForValue.build();
                    onChanged();
                } else {
                    this.capabilitiesBuilder_.setMessage(builderForValue.build());
                }
                return this;
            }

            public Builder mergeCapabilities(Capabilities value) {
                if (this.capabilitiesBuilder_ == null) {
                    if (this.capabilities_ != null) {
                        this.capabilities_ = Capabilities.newBuilder(this.capabilities_).mergeFrom(value).buildPartial();
                    } else {
                        this.capabilities_ = value;
                    }
                    onChanged();
                } else {
                    this.capabilitiesBuilder_.mergeFrom(value);
                }
                return this;
            }

            public Builder clearCapabilities() {
                if (this.capabilitiesBuilder_ == null) {
                    this.capabilities_ = null;
                    onChanged();
                } else {
                    this.capabilities_ = null;
                    this.capabilitiesBuilder_ = null;
                }
                return this;
            }

            public Builder getCapabilitiesBuilder() {
                onChanged();
                return (Builder) getCapabilitiesFieldBuilder().getBuilder();
            }

            public CapabilitiesOrBuilder getCapabilitiesOrBuilder() {
                if (this.capabilitiesBuilder_ != null) {
                    return (CapabilitiesOrBuilder) this.capabilitiesBuilder_.getMessageOrBuilder();
                }
                return this.capabilities_ == null ? Capabilities.getDefaultInstance() : this.capabilities_;
            }

            private SingleFieldBuilder<Capabilities, Builder, CapabilitiesOrBuilder> getCapabilitiesFieldBuilder() {
                if (this.capabilitiesBuilder_ == null) {
                    this.capabilitiesBuilder_ = new SingleFieldBuilder(getCapabilities(), getParentForChildren(), isClean());
                    this.capabilities_ = null;
                }
                return this.capabilitiesBuilder_;
            }

            public String getLastViewedMessageId() {
                ByteString ref = this.lastViewedMessageId_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                ByteString bs = ref;
                String s = bs.toStringUtf8();
                if (!bs.isValidUtf8()) {
                    return s;
                }
                this.lastViewedMessageId_ = s;
                return s;
            }

            public ByteString getLastViewedMessageIdBytes() {
                Object ref = this.lastViewedMessageId_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.lastViewedMessageId_ = b;
                return b;
            }

            public Builder setLastViewedMessageId(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.lastViewedMessageId_ = value;
                onChanged();
                return this;
            }

            public Builder clearLastViewedMessageId() {
                this.lastViewedMessageId_ = Conversation.getDefaultInstance().getLastViewedMessageId();
                onChanged();
                return this;
            }

            public Builder setLastViewedMessageIdBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.lastViewedMessageId_ = value;
                onChanged();
                return this;
            }

            private void ensureParticipantsIsMutable() {
                if ((this.bitField0_ & 2048) != 2048) {
                    this.participants_ = new ArrayList(this.participants_);
                    this.bitField0_ |= 2048;
                }
            }

            public List<Participant> getParticipantsList() {
                if (this.participantsBuilder_ == null) {
                    return Collections.unmodifiableList(this.participants_);
                }
                return this.participantsBuilder_.getMessageList();
            }

            public int getParticipantsCount() {
                if (this.participantsBuilder_ == null) {
                    return this.participants_.size();
                }
                return this.participantsBuilder_.getCount();
            }

            public Participant getParticipants(int index) {
                if (this.participantsBuilder_ == null) {
                    return (Participant) this.participants_.get(index);
                }
                return (Participant) this.participantsBuilder_.getMessage(index);
            }

            public Builder setParticipants(int index, Participant value) {
                if (this.participantsBuilder_ != null) {
                    this.participantsBuilder_.setMessage(index, value);
                } else if (value == null) {
                    throw new NullPointerException();
                } else {
                    ensureParticipantsIsMutable();
                    this.participants_.set(index, value);
                    onChanged();
                }
                return this;
            }

            public Builder setParticipants(int index, Builder builderForValue) {
                if (this.participantsBuilder_ == null) {
                    ensureParticipantsIsMutable();
                    this.participants_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    this.participantsBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addParticipants(Participant value) {
                if (this.participantsBuilder_ != null) {
                    this.participantsBuilder_.addMessage(value);
                } else if (value == null) {
                    throw new NullPointerException();
                } else {
                    ensureParticipantsIsMutable();
                    this.participants_.add(value);
                    onChanged();
                }
                return this;
            }

            public Builder addParticipants(int index, Participant value) {
                if (this.participantsBuilder_ != null) {
                    this.participantsBuilder_.addMessage(index, value);
                } else if (value == null) {
                    throw new NullPointerException();
                } else {
                    ensureParticipantsIsMutable();
                    this.participants_.add(index, value);
                    onChanged();
                }
                return this;
            }

            public Builder addParticipants(Builder builderForValue) {
                if (this.participantsBuilder_ == null) {
                    ensureParticipantsIsMutable();
                    this.participants_.add(builderForValue.build());
                    onChanged();
                } else {
                    this.participantsBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addParticipants(int index, Builder builderForValue) {
                if (this.participantsBuilder_ == null) {
                    ensureParticipantsIsMutable();
                    this.participants_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    this.participantsBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllParticipants(Iterable<? extends Participant> values) {
                if (this.participantsBuilder_ == null) {
                    ensureParticipantsIsMutable();
                    com.google.protobuf.AbstractMessageLite.Builder.addAll(values, this.participants_);
                    onChanged();
                } else {
                    this.participantsBuilder_.addAllMessages(values);
                }
                return this;
            }

            public Builder clearParticipants() {
                if (this.participantsBuilder_ == null) {
                    this.participants_ = Collections.emptyList();
                    this.bitField0_ &= -2049;
                    onChanged();
                } else {
                    this.participantsBuilder_.clear();
                }
                return this;
            }

            public Builder removeParticipants(int index) {
                if (this.participantsBuilder_ == null) {
                    ensureParticipantsIsMutable();
                    this.participants_.remove(index);
                    onChanged();
                } else {
                    this.participantsBuilder_.remove(index);
                }
                return this;
            }

            public Builder getParticipantsBuilder(int index) {
                return (Builder) getParticipantsFieldBuilder().getBuilder(index);
            }

            public ParticipantOrBuilder getParticipantsOrBuilder(int index) {
                if (this.participantsBuilder_ == null) {
                    return (ParticipantOrBuilder) this.participants_.get(index);
                }
                return (ParticipantOrBuilder) this.participantsBuilder_.getMessageOrBuilder(index);
            }

            public List<? extends ParticipantOrBuilder> getParticipantsOrBuilderList() {
                if (this.participantsBuilder_ != null) {
                    return this.participantsBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.participants_);
            }

            public Builder addParticipantsBuilder() {
                return (Builder) getParticipantsFieldBuilder().addBuilder(Participant.getDefaultInstance());
            }

            public Builder addParticipantsBuilder(int index) {
                return (Builder) getParticipantsFieldBuilder().addBuilder(index, Participant.getDefaultInstance());
            }

            public List<Builder> getParticipantsBuilderList() {
                return getParticipantsFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<Participant, Builder, ParticipantOrBuilder> getParticipantsFieldBuilder() {
                if (this.participantsBuilder_ == null) {
                    this.participantsBuilder_ = new RepeatedFieldBuilder(this.participants_, (this.bitField0_ & 2048) == 2048, getParentForChildren(), isClean());
                    this.participants_ = null;
                }
                return this.participantsBuilder_;
            }

            public String getOwnerId() {
                ByteString ref = this.ownerId_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                ByteString bs = ref;
                String s = bs.toStringUtf8();
                if (!bs.isValidUtf8()) {
                    return s;
                }
                this.ownerId_ = s;
                return s;
            }

            public ByteString getOwnerIdBytes() {
                Object ref = this.ownerId_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.ownerId_ = b;
                return b;
            }

            public Builder setOwnerId(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ownerId_ = value;
                onChanged();
                return this;
            }

            public Builder clearOwnerId() {
                this.ownerId_ = Conversation.getDefaultInstance().getOwnerId();
                onChanged();
                return this;
            }

            public Builder setOwnerIdBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ownerId_ = value;
                onChanged();
                return this;
            }

            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return this;
            }
        }

        public enum Type implements ProtocolMessageEnum {
            PRIVATE(0, 0),
            CHAT(CHAT_VALUE, CHAT_VALUE),
            UNRECOGNIZED(-1, -1);
            
            public static final int CHAT_VALUE = 1;
            public static final int PRIVATE_VALUE = 0;
            private static final Type[] VALUES;
            private static EnumLiteMap<Type> internalValueMap;
            private final int index;
            private final int value;

            /* renamed from: ru.ok.android.proto.ConversationProto.Conversation.Type.1 */
            static class C03901 implements EnumLiteMap<Type> {
                C03901() {
                }

                public Type findValueByNumber(int number) {
                    return Type.valueOf(number);
                }
            }

            static {
                internalValueMap = new C03901();
                VALUES = values();
            }

            public final int getNumber() {
                if (this.index != -1) {
                    return this.value;
                }
                throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
            }

            public static Type valueOf(int value) {
                switch (value) {
                    case RECEIVED_VALUE:
                        return PRIVATE;
                    case CHAT_VALUE:
                        return CHAT;
                    default:
                        return null;
                }
            }

            public static EnumLiteMap<Type> internalGetValueMap() {
                return internalValueMap;
            }

            public final EnumValueDescriptor getValueDescriptor() {
                return (EnumValueDescriptor) getDescriptor().getValues().get(this.index);
            }

            public final EnumDescriptor getDescriptorForType() {
                return getDescriptor();
            }

            public static final EnumDescriptor getDescriptor() {
                return (EnumDescriptor) Conversation.getDescriptor().getEnumTypes().get(0);
            }

            public static Type valueOf(EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                } else if (desc.getIndex() == -1) {
                    return UNRECOGNIZED;
                } else {
                    return VALUES[desc.getIndex()];
                }
            }

            private Type(int index, int value) {
                this.index = index;
                this.value = value;
            }
        }

        private Conversation(com.google.protobuf.GeneratedMessage.Builder builder) {
            super(builder);
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
        }

        private Conversation() {
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
            this.id_ = "";
            this.topic_ = "";
            this.builtTopic_ = "";
            this.lastMessage_ = "";
            this.lastMessageAuthorId_ = "";
            this.type_ = 0;
            this.lastMsgTime_ = 0;
            this.lastViewTime_ = 0;
            this.newMessagesCount_ = 0;
            this.lastViewedMessageId_ = "";
            this.participants_ = Collections.emptyList();
            this.ownerId_ = "";
        }

        public final UnknownFieldSet getUnknownFields() {
            return UnknownFieldSet.getDefaultInstance();
        }

        private Conversation(CodedInputStream input, ExtensionRegistryLite extensionRegistry) {
            this();
            int mutable_bitField0_ = 0;
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    switch (tag) {
                        case RECEIVED_VALUE:
                            done = true;
                            break;
                        case CAPABILITIES_FIELD_NUMBER /*10*/:
                            this.id_ = input.readBytes();
                            break;
                        case C0206R.styleable.Toolbar_collapseIcon /*18*/:
                            this.topic_ = input.readBytes();
                            break;
                        case C0206R.styleable.Theme_actionMenuTextColor /*26*/:
                            this.builtTopic_ = input.readBytes();
                            break;
                        case C0206R.styleable.Theme_actionModePasteDrawable /*34*/:
                            this.lastMessage_ = input.readBytes();
                            break;
                        case C0206R.styleable.Theme_dialogTheme /*42*/:
                            this.lastMessageAuthorId_ = input.readBytes();
                            break;
                        case C0206R.styleable.Theme_homeAsUpIndicator /*48*/:
                            this.type_ = input.readEnum();
                            break;
                        case C0206R.styleable.Theme_dividerHorizontal /*56*/:
                            this.lastMsgTime_ = input.readInt64();
                            break;
                        case C0206R.styleable.Theme_textAppearanceSearchResultTitle /*64*/:
                            this.lastViewTime_ = input.readInt64();
                            break;
                        case C0206R.styleable.Theme_listPreferredItemPaddingRight /*72*/:
                            this.newMessagesCount_ = input.readInt32();
                            break;
                        case C0206R.styleable.Theme_colorPrimaryDark /*82*/:
                            Builder subBuilder = null;
                            if (this.capabilities_ != null) {
                                subBuilder = this.capabilities_.toBuilder();
                            }
                            this.capabilities_ = (Capabilities) input.readMessage(Capabilities.PARSER, extensionRegistry);
                            if (subBuilder == null) {
                                break;
                            }
                            subBuilder.mergeFrom(this.capabilities_);
                            this.capabilities_ = subBuilder.buildPartial();
                            break;
                        case C0206R.styleable.Theme_alertDialogStyle /*90*/:
                            this.lastViewedMessageId_ = input.readBytes();
                            break;
                        case C0206R.styleable.Theme_autoCompleteTextViewStyle /*98*/:
                            if ((mutable_bitField0_ & 2048) != 2048) {
                                this.participants_ = new ArrayList();
                                mutable_bitField0_ |= 2048;
                            }
                            this.participants_.add(input.readMessage(Participant.PARSER, extensionRegistry));
                            break;
                        case C0206R.styleable.Theme_spinnerStyle /*106*/:
                            this.ownerId_ = input.readBytes();
                            break;
                        default:
                            if (!input.skipField(tag)) {
                                done = true;
                                break;
                            }
                            break;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e.setUnfinishedMessage(this));
                } catch (IOException e2) {
                    throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 2048) == 2048) {
                        this.participants_ = Collections.unmodifiableList(this.participants_);
                    }
                    makeExtensionsImmutable();
                }
            }
            if ((mutable_bitField0_ & 2048) == 2048) {
                this.participants_ = Collections.unmodifiableList(this.participants_);
            }
            makeExtensionsImmutable();
        }

        public static final Descriptor getDescriptor() {
            return ConversationProto.internal_static_ru_ok_android_proto_Conversation_descriptor;
        }

        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ConversationProto.f75xd4f59cac.ensureFieldAccessorsInitialized(Conversation.class, Builder.class);
        }

        public String getId() {
            ByteString ref = this.id_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.id_ = s;
            }
            return s;
        }

        public ByteString getIdBytes() {
            Object ref = this.id_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.id_ = b;
            return b;
        }

        public String getTopic() {
            ByteString ref = this.topic_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.topic_ = s;
            }
            return s;
        }

        public ByteString getTopicBytes() {
            Object ref = this.topic_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.topic_ = b;
            return b;
        }

        public String getBuiltTopic() {
            ByteString ref = this.builtTopic_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.builtTopic_ = s;
            }
            return s;
        }

        public ByteString getBuiltTopicBytes() {
            Object ref = this.builtTopic_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.builtTopic_ = b;
            return b;
        }

        public String getLastMessage() {
            ByteString ref = this.lastMessage_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.lastMessage_ = s;
            }
            return s;
        }

        public ByteString getLastMessageBytes() {
            Object ref = this.lastMessage_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.lastMessage_ = b;
            return b;
        }

        public String getLastMessageAuthorId() {
            ByteString ref = this.lastMessageAuthorId_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.lastMessageAuthorId_ = s;
            }
            return s;
        }

        public ByteString getLastMessageAuthorIdBytes() {
            Object ref = this.lastMessageAuthorId_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.lastMessageAuthorId_ = b;
            return b;
        }

        public int getTypeValue() {
            return this.type_;
        }

        public Type getType() {
            Type result = Type.valueOf(this.type_);
            return result == null ? Type.UNRECOGNIZED : result;
        }

        public long getLastMsgTime() {
            return this.lastMsgTime_;
        }

        public long getLastViewTime() {
            return this.lastViewTime_;
        }

        public int getNewMessagesCount() {
            return this.newMessagesCount_;
        }

        public boolean hasCapabilities() {
            return this.capabilities_ != null;
        }

        public Capabilities getCapabilities() {
            return this.capabilities_ == null ? Capabilities.getDefaultInstance() : this.capabilities_;
        }

        public CapabilitiesOrBuilder getCapabilitiesOrBuilder() {
            return getCapabilities();
        }

        public String getLastViewedMessageId() {
            ByteString ref = this.lastViewedMessageId_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.lastViewedMessageId_ = s;
            }
            return s;
        }

        public ByteString getLastViewedMessageIdBytes() {
            Object ref = this.lastViewedMessageId_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.lastViewedMessageId_ = b;
            return b;
        }

        public List<Participant> getParticipantsList() {
            return this.participants_;
        }

        public List<? extends ParticipantOrBuilder> getParticipantsOrBuilderList() {
            return this.participants_;
        }

        public int getParticipantsCount() {
            return this.participants_.size();
        }

        public Participant getParticipants(int index) {
            return (Participant) this.participants_.get(index);
        }

        public ParticipantOrBuilder getParticipantsOrBuilder(int index) {
            return (ParticipantOrBuilder) this.participants_.get(index);
        }

        public String getOwnerId() {
            ByteString ref = this.ownerId_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.ownerId_ = s;
            }
            return s;
        }

        public ByteString getOwnerIdBytes() {
            Object ref = this.ownerId_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.ownerId_ = b;
            return b;
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == (byte) 1) {
                return true;
            }
            if (isInitialized == null) {
                return false;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if (!getIdBytes().isEmpty()) {
                output.writeBytes(ID_FIELD_NUMBER, getIdBytes());
            }
            if (!getTopicBytes().isEmpty()) {
                output.writeBytes(TOPIC_FIELD_NUMBER, getTopicBytes());
            }
            if (!getBuiltTopicBytes().isEmpty()) {
                output.writeBytes(BUILTTOPIC_FIELD_NUMBER, getBuiltTopicBytes());
            }
            if (!getLastMessageBytes().isEmpty()) {
                output.writeBytes(LASTMESSAGE_FIELD_NUMBER, getLastMessageBytes());
            }
            if (!getLastMessageAuthorIdBytes().isEmpty()) {
                output.writeBytes(LASTMESSAGEAUTHORID_FIELD_NUMBER, getLastMessageAuthorIdBytes());
            }
            if (this.type_ != Type.PRIVATE.getNumber()) {
                output.writeEnum(TYPE_FIELD_NUMBER, this.type_);
            }
            if (this.lastMsgTime_ != 0) {
                output.writeInt64(LASTMSGTIME_FIELD_NUMBER, this.lastMsgTime_);
            }
            if (this.lastViewTime_ != 0) {
                output.writeInt64(LASTVIEWTIME_FIELD_NUMBER, this.lastViewTime_);
            }
            if (this.newMessagesCount_ != 0) {
                output.writeInt32(NEWMESSAGESCOUNT_FIELD_NUMBER, this.newMessagesCount_);
            }
            if (this.capabilities_ != null) {
                output.writeMessage(CAPABILITIES_FIELD_NUMBER, getCapabilities());
            }
            if (!getLastViewedMessageIdBytes().isEmpty()) {
                output.writeBytes(LASTVIEWEDMESSAGEID_FIELD_NUMBER, getLastViewedMessageIdBytes());
            }
            for (int i = 0; i < this.participants_.size(); i += ID_FIELD_NUMBER) {
                output.writeMessage(PARTICIPANTS_FIELD_NUMBER, (MessageLite) this.participants_.get(i));
            }
            if (!getOwnerIdBytes().isEmpty()) {
                output.writeBytes(OWNERID_FIELD_NUMBER, getOwnerIdBytes());
            }
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if (!getIdBytes().isEmpty()) {
                size = 0 + CodedOutputStream.computeBytesSize(ID_FIELD_NUMBER, getIdBytes());
            }
            if (!getTopicBytes().isEmpty()) {
                size += CodedOutputStream.computeBytesSize(TOPIC_FIELD_NUMBER, getTopicBytes());
            }
            if (!getBuiltTopicBytes().isEmpty()) {
                size += CodedOutputStream.computeBytesSize(BUILTTOPIC_FIELD_NUMBER, getBuiltTopicBytes());
            }
            if (!getLastMessageBytes().isEmpty()) {
                size += CodedOutputStream.computeBytesSize(LASTMESSAGE_FIELD_NUMBER, getLastMessageBytes());
            }
            if (!getLastMessageAuthorIdBytes().isEmpty()) {
                size += CodedOutputStream.computeBytesSize(LASTMESSAGEAUTHORID_FIELD_NUMBER, getLastMessageAuthorIdBytes());
            }
            if (this.type_ != Type.PRIVATE.getNumber()) {
                size += CodedOutputStream.computeEnumSize(TYPE_FIELD_NUMBER, this.type_);
            }
            if (this.lastMsgTime_ != 0) {
                size += CodedOutputStream.computeInt64Size(LASTMSGTIME_FIELD_NUMBER, this.lastMsgTime_);
            }
            if (this.lastViewTime_ != 0) {
                size += CodedOutputStream.computeInt64Size(LASTVIEWTIME_FIELD_NUMBER, this.lastViewTime_);
            }
            if (this.newMessagesCount_ != 0) {
                size += CodedOutputStream.computeInt32Size(NEWMESSAGESCOUNT_FIELD_NUMBER, this.newMessagesCount_);
            }
            if (this.capabilities_ != null) {
                size += CodedOutputStream.computeMessageSize(CAPABILITIES_FIELD_NUMBER, getCapabilities());
            }
            if (!getLastViewedMessageIdBytes().isEmpty()) {
                size += CodedOutputStream.computeBytesSize(LASTVIEWEDMESSAGEID_FIELD_NUMBER, getLastViewedMessageIdBytes());
            }
            for (int i = 0; i < this.participants_.size(); i += ID_FIELD_NUMBER) {
                size += CodedOutputStream.computeMessageSize(PARTICIPANTS_FIELD_NUMBER, (MessageLite) this.participants_.get(i));
            }
            if (!getOwnerIdBytes().isEmpty()) {
                size += CodedOutputStream.computeBytesSize(OWNERID_FIELD_NUMBER, getOwnerIdBytes());
            }
            this.memoizedSerializedSize = size;
            return size;
        }

        public static Conversation parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (Conversation) PARSER.parseFrom(data);
        }

        public static Conversation parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Conversation) PARSER.parseFrom(data, extensionRegistry);
        }

        public static Conversation parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (Conversation) PARSER.parseFrom(data);
        }

        public static Conversation parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Conversation) PARSER.parseFrom(data, extensionRegistry);
        }

        public static Conversation parseFrom(InputStream input) throws IOException {
            return (Conversation) PARSER.parseFrom(input);
        }

        public static Conversation parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Conversation) PARSER.parseFrom(input, extensionRegistry);
        }

        public static Conversation parseDelimitedFrom(InputStream input) throws IOException {
            return (Conversation) PARSER.parseDelimitedFrom(input);
        }

        public static Conversation parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Conversation) PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static Conversation parseFrom(CodedInputStream input) throws IOException {
            return (Conversation) PARSER.parseFrom(input);
        }

        public static Conversation parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Conversation) PARSER.parseFrom(input, extensionRegistry);
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Conversation prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        protected Builder newBuilderForType(BuilderParent parent) {
            return new Builder(null);
        }

        static {
            DEFAULT_INSTANCE = new Conversation();
            PARSER = new C03891();
        }

        public static Conversation getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public Parser<Conversation> getParserForType() {
            return PARSER;
        }

        public Conversation getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public interface ParticipantOrBuilder extends MessageOrBuilder {
        boolean getCanKick();

        Gender getGender();

        int getGenderValue();

        String getId();

        ByteString getIdBytes();

        long getLastOnline();

        long getLastViewTime();

        String getName();

        ByteString getNameBytes();

        OnlineType getOnline();

        int getOnlineValue();

        String getPicUrl();

        ByteString getPicUrlBytes();
    }

    public static final class Participant extends GeneratedMessage implements ParticipantOrBuilder {
        public static final int CANKICK_FIELD_NUMBER = 5;
        private static final Participant DEFAULT_INSTANCE;
        public static final int GENDER_FIELD_NUMBER = 7;
        public static final int ID_FIELD_NUMBER = 1;
        public static final int LASTONLINE_FIELD_NUMBER = 8;
        public static final int LASTVIEWTIME_FIELD_NUMBER = 3;
        public static final int NAME_FIELD_NUMBER = 2;
        public static final int ONLINE_FIELD_NUMBER = 6;
        public static final Parser<Participant> PARSER;
        public static final int PICURL_FIELD_NUMBER = 4;
        private static final long serialVersionUID = 0;
        private boolean canKick_;
        private int gender_;
        private volatile Object id_;
        private long lastOnline_;
        private long lastViewTime_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private volatile Object name_;
        private int online_;
        private volatile Object picUrl_;

        /* renamed from: ru.ok.android.proto.ConversationProto.Participant.1 */
        static class C03911 extends AbstractParser<Participant> {
            C03911() {
            }

            public Participant parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                try {
                    return new Participant(extensionRegistry, null);
                } catch (RuntimeException e) {
                    if (e.getCause() instanceof InvalidProtocolBufferException) {
                        throw ((InvalidProtocolBufferException) e.getCause());
                    }
                    throw e;
                }
            }
        }

        public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<Builder> implements ParticipantOrBuilder {
            private boolean canKick_;
            private int gender_;
            private Object id_;
            private long lastOnline_;
            private long lastViewTime_;
            private Object name_;
            private int online_;
            private Object picUrl_;

            public static final Descriptor getDescriptor() {
                return ConversationProto.internal_static_ru_ok_android_proto_Participant_descriptor;
            }

            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ConversationProto.f76x41a9c696.ensureFieldAccessorsInitialized(Participant.class, Builder.class);
            }

            private Builder() {
                this.id_ = "";
                this.name_ = "";
                this.picUrl_ = "";
                this.online_ = 0;
                this.gender_ = 0;
                maybeForceBuilderInitialization();
            }

            private Builder(BuilderParent parent) {
                super(parent);
                this.id_ = "";
                this.name_ = "";
                this.picUrl_ = "";
                this.online_ = 0;
                this.gender_ = 0;
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (!Participant.alwaysUseFieldBuilders) {
                }
            }

            public Builder clear() {
                super.clear();
                this.id_ = "";
                this.name_ = "";
                this.lastViewTime_ = 0;
                this.picUrl_ = "";
                this.canKick_ = false;
                this.online_ = 0;
                this.gender_ = 0;
                this.lastOnline_ = 0;
                return this;
            }

            public Descriptor getDescriptorForType() {
                return ConversationProto.internal_static_ru_ok_android_proto_Participant_descriptor;
            }

            public Participant getDefaultInstanceForType() {
                return Participant.getDefaultInstance();
            }

            public Participant build() {
                Participant result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public Participant buildPartial() {
                Participant result = new Participant(null);
                result.id_ = this.id_;
                result.name_ = this.name_;
                result.lastViewTime_ = this.lastViewTime_;
                result.picUrl_ = this.picUrl_;
                result.canKick_ = this.canKick_;
                result.online_ = this.online_;
                result.gender_ = this.gender_;
                result.lastOnline_ = this.lastOnline_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof Participant) {
                    return mergeFrom((Participant) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Participant other) {
                if (other != Participant.getDefaultInstance()) {
                    if (!other.getId().isEmpty()) {
                        this.id_ = other.id_;
                        onChanged();
                    }
                    if (!other.getName().isEmpty()) {
                        this.name_ = other.name_;
                        onChanged();
                    }
                    if (other.getLastViewTime() != 0) {
                        setLastViewTime(other.getLastViewTime());
                    }
                    if (!other.getPicUrl().isEmpty()) {
                        this.picUrl_ = other.picUrl_;
                        onChanged();
                    }
                    if (other.getCanKick()) {
                        setCanKick(other.getCanKick());
                    }
                    if (other.online_ != 0) {
                        setOnlineValue(other.getOnlineValue());
                    }
                    if (other.gender_ != 0) {
                        setGenderValue(other.getGenderValue());
                    }
                    if (other.getLastOnline() != 0) {
                        setLastOnline(other.getLastOnline());
                    }
                    onChanged();
                }
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Participant parsedMessage = null;
                try {
                    parsedMessage = (Participant) Participant.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    parsedMessage = (Participant) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            public String getId() {
                ByteString ref = this.id_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                ByteString bs = ref;
                String s = bs.toStringUtf8();
                if (!bs.isValidUtf8()) {
                    return s;
                }
                this.id_ = s;
                return s;
            }

            public ByteString getIdBytes() {
                Object ref = this.id_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.id_ = b;
                return b;
            }

            public Builder setId(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.id_ = value;
                onChanged();
                return this;
            }

            public Builder clearId() {
                this.id_ = Participant.getDefaultInstance().getId();
                onChanged();
                return this;
            }

            public Builder setIdBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.id_ = value;
                onChanged();
                return this;
            }

            public String getName() {
                ByteString ref = this.name_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                ByteString bs = ref;
                String s = bs.toStringUtf8();
                if (!bs.isValidUtf8()) {
                    return s;
                }
                this.name_ = s;
                return s;
            }

            public ByteString getNameBytes() {
                Object ref = this.name_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.name_ = b;
                return b;
            }

            public Builder setName(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.name_ = value;
                onChanged();
                return this;
            }

            public Builder clearName() {
                this.name_ = Participant.getDefaultInstance().getName();
                onChanged();
                return this;
            }

            public Builder setNameBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.name_ = value;
                onChanged();
                return this;
            }

            public long getLastViewTime() {
                return this.lastViewTime_;
            }

            public Builder setLastViewTime(long value) {
                this.lastViewTime_ = value;
                onChanged();
                return this;
            }

            public Builder clearLastViewTime() {
                this.lastViewTime_ = 0;
                onChanged();
                return this;
            }

            public String getPicUrl() {
                ByteString ref = this.picUrl_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                ByteString bs = ref;
                String s = bs.toStringUtf8();
                if (!bs.isValidUtf8()) {
                    return s;
                }
                this.picUrl_ = s;
                return s;
            }

            public ByteString getPicUrlBytes() {
                Object ref = this.picUrl_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.picUrl_ = b;
                return b;
            }

            public Builder setPicUrl(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.picUrl_ = value;
                onChanged();
                return this;
            }

            public Builder clearPicUrl() {
                this.picUrl_ = Participant.getDefaultInstance().getPicUrl();
                onChanged();
                return this;
            }

            public Builder setPicUrlBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.picUrl_ = value;
                onChanged();
                return this;
            }

            public boolean getCanKick() {
                return this.canKick_;
            }

            public Builder setCanKick(boolean value) {
                this.canKick_ = value;
                onChanged();
                return this;
            }

            public Builder clearCanKick() {
                this.canKick_ = false;
                onChanged();
                return this;
            }

            public int getOnlineValue() {
                return this.online_;
            }

            public Builder setOnlineValue(int value) {
                this.online_ = value;
                onChanged();
                return this;
            }

            public OnlineType getOnline() {
                OnlineType result = OnlineType.valueOf(this.online_);
                return result == null ? OnlineType.UNRECOGNIZED : result;
            }

            public Builder setOnline(OnlineType value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.online_ = value.getNumber();
                onChanged();
                return this;
            }

            public Builder clearOnline() {
                this.online_ = 0;
                onChanged();
                return this;
            }

            public int getGenderValue() {
                return this.gender_;
            }

            public Builder setGenderValue(int value) {
                this.gender_ = value;
                onChanged();
                return this;
            }

            public Gender getGender() {
                Gender result = Gender.valueOf(this.gender_);
                return result == null ? Gender.UNRECOGNIZED : result;
            }

            public Builder setGender(Gender value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.gender_ = value.getNumber();
                onChanged();
                return this;
            }

            public Builder clearGender() {
                this.gender_ = 0;
                onChanged();
                return this;
            }

            public long getLastOnline() {
                return this.lastOnline_;
            }

            public Builder setLastOnline(long value) {
                this.lastOnline_ = value;
                onChanged();
                return this;
            }

            public Builder clearLastOnline() {
                this.lastOnline_ = 0;
                onChanged();
                return this;
            }

            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return this;
            }
        }

        public enum Gender implements ProtocolMessageEnum {
            MALE(0, 0),
            FEMALE(FEMALE_VALUE, FEMALE_VALUE),
            UNRECOGNIZED(-1, -1);
            
            public static final int FEMALE_VALUE = 1;
            public static final int MALE_VALUE = 0;
            private static final Gender[] VALUES;
            private static EnumLiteMap<Gender> internalValueMap;
            private final int index;
            private final int value;

            /* renamed from: ru.ok.android.proto.ConversationProto.Participant.Gender.1 */
            static class C03921 implements EnumLiteMap<Gender> {
                C03921() {
                }

                public Gender findValueByNumber(int number) {
                    return Gender.valueOf(number);
                }
            }

            static {
                internalValueMap = new C03921();
                VALUES = values();
            }

            public final int getNumber() {
                if (this.index != -1) {
                    return this.value;
                }
                throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
            }

            public static Gender valueOf(int value) {
                switch (value) {
                    case RECEIVED_VALUE:
                        return MALE;
                    case FEMALE_VALUE:
                        return FEMALE;
                    default:
                        return null;
                }
            }

            public static EnumLiteMap<Gender> internalGetValueMap() {
                return internalValueMap;
            }

            public final EnumValueDescriptor getValueDescriptor() {
                return (EnumValueDescriptor) getDescriptor().getValues().get(this.index);
            }

            public final EnumDescriptor getDescriptorForType() {
                return getDescriptor();
            }

            public static final EnumDescriptor getDescriptor() {
                return (EnumDescriptor) Participant.getDescriptor().getEnumTypes().get(FEMALE_VALUE);
            }

            public static Gender valueOf(EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                } else if (desc.getIndex() == -1) {
                    return UNRECOGNIZED;
                } else {
                    return VALUES[desc.getIndex()];
                }
            }

            private Gender(int index, int value) {
                this.index = index;
                this.value = value;
            }
        }

        public enum OnlineType implements ProtocolMessageEnum {
            OFFLINE(OFFLINE_VALUE, OFFLINE_VALUE),
            WEB(WEB_VALUE, WEB_VALUE),
            MOBILE(MOBILE_VALUE, MOBILE_VALUE),
            UNRECOGNIZED(-1, -1);
            
            public static final int MOBILE_VALUE = 2;
            public static final int OFFLINE_VALUE = 0;
            private static final OnlineType[] VALUES;
            public static final int WEB_VALUE = 1;
            private static EnumLiteMap<OnlineType> internalValueMap;
            private final int index;
            private final int value;

            /* renamed from: ru.ok.android.proto.ConversationProto.Participant.OnlineType.1 */
            static class C03931 implements EnumLiteMap<OnlineType> {
                C03931() {
                }

                public OnlineType findValueByNumber(int number) {
                    return OnlineType.valueOf(number);
                }
            }

            static {
                internalValueMap = new C03931();
                VALUES = values();
            }

            public final int getNumber() {
                if (this.index != -1) {
                    return this.value;
                }
                throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
            }

            public static OnlineType valueOf(int value) {
                switch (value) {
                    case OFFLINE_VALUE:
                        return OFFLINE;
                    case WEB_VALUE:
                        return WEB;
                    case MOBILE_VALUE:
                        return MOBILE;
                    default:
                        return null;
                }
            }

            public static EnumLiteMap<OnlineType> internalGetValueMap() {
                return internalValueMap;
            }

            public final EnumValueDescriptor getValueDescriptor() {
                return (EnumValueDescriptor) getDescriptor().getValues().get(this.index);
            }

            public final EnumDescriptor getDescriptorForType() {
                return getDescriptor();
            }

            public static final EnumDescriptor getDescriptor() {
                return (EnumDescriptor) Participant.getDescriptor().getEnumTypes().get(OFFLINE_VALUE);
            }

            public static OnlineType valueOf(EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                } else if (desc.getIndex() == -1) {
                    return UNRECOGNIZED;
                } else {
                    return VALUES[desc.getIndex()];
                }
            }

            private OnlineType(int index, int value) {
                this.index = index;
                this.value = value;
            }
        }

        private Participant(com.google.protobuf.GeneratedMessage.Builder builder) {
            super(builder);
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
        }

        private Participant() {
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
            this.id_ = "";
            this.name_ = "";
            this.lastViewTime_ = 0;
            this.picUrl_ = "";
            this.canKick_ = false;
            this.online_ = 0;
            this.gender_ = 0;
            this.lastOnline_ = 0;
        }

        public final UnknownFieldSet getUnknownFields() {
            return UnknownFieldSet.getDefaultInstance();
        }

        private Participant(CodedInputStream input, ExtensionRegistryLite extensionRegistry) {
            this();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    switch (tag) {
                        case RECEIVED_VALUE:
                            done = true;
                            break;
                        case MessagesProto.Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                            this.id_ = input.readBytes();
                            break;
                        case C0206R.styleable.Toolbar_collapseIcon /*18*/:
                            this.name_ = input.readBytes();
                            break;
                        case C0206R.styleable.Toolbar_subtitleTextColor /*24*/:
                            this.lastViewTime_ = input.readInt64();
                            break;
                        case C0206R.styleable.Theme_actionModePasteDrawable /*34*/:
                            this.picUrl_ = input.readBytes();
                            break;
                        case C0206R.styleable.Theme_textAppearanceLargePopupMenu /*40*/:
                            this.canKick_ = input.readBool();
                            break;
                        case C0206R.styleable.Theme_homeAsUpIndicator /*48*/:
                            this.online_ = input.readEnum();
                            break;
                        case C0206R.styleable.Theme_dividerHorizontal /*56*/:
                            this.gender_ = input.readEnum();
                            break;
                        case C0206R.styleable.Theme_textAppearanceSearchResultTitle /*64*/:
                            this.lastOnline_ = input.readInt64();
                            break;
                        default:
                            if (!input.skipField(tag)) {
                                done = true;
                                break;
                            }
                            break;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e.setUnfinishedMessage(this));
                } catch (IOException e2) {
                    throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                } catch (Throwable th) {
                    makeExtensionsImmutable();
                }
            }
            makeExtensionsImmutable();
        }

        public static final Descriptor getDescriptor() {
            return ConversationProto.internal_static_ru_ok_android_proto_Participant_descriptor;
        }

        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ConversationProto.f76x41a9c696.ensureFieldAccessorsInitialized(Participant.class, Builder.class);
        }

        public String getId() {
            ByteString ref = this.id_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.id_ = s;
            }
            return s;
        }

        public ByteString getIdBytes() {
            Object ref = this.id_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.id_ = b;
            return b;
        }

        public String getName() {
            ByteString ref = this.name_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }

        public ByteString getNameBytes() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.name_ = b;
            return b;
        }

        public long getLastViewTime() {
            return this.lastViewTime_;
        }

        public String getPicUrl() {
            ByteString ref = this.picUrl_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.picUrl_ = s;
            }
            return s;
        }

        public ByteString getPicUrlBytes() {
            Object ref = this.picUrl_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.picUrl_ = b;
            return b;
        }

        public boolean getCanKick() {
            return this.canKick_;
        }

        public int getOnlineValue() {
            return this.online_;
        }

        public OnlineType getOnline() {
            OnlineType result = OnlineType.valueOf(this.online_);
            return result == null ? OnlineType.UNRECOGNIZED : result;
        }

        public int getGenderValue() {
            return this.gender_;
        }

        public Gender getGender() {
            Gender result = Gender.valueOf(this.gender_);
            return result == null ? Gender.UNRECOGNIZED : result;
        }

        public long getLastOnline() {
            return this.lastOnline_;
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == (byte) 1) {
                return true;
            }
            if (isInitialized == null) {
                return false;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if (!getIdBytes().isEmpty()) {
                output.writeBytes(ID_FIELD_NUMBER, getIdBytes());
            }
            if (!getNameBytes().isEmpty()) {
                output.writeBytes(NAME_FIELD_NUMBER, getNameBytes());
            }
            if (this.lastViewTime_ != 0) {
                output.writeInt64(LASTVIEWTIME_FIELD_NUMBER, this.lastViewTime_);
            }
            if (!getPicUrlBytes().isEmpty()) {
                output.writeBytes(PICURL_FIELD_NUMBER, getPicUrlBytes());
            }
            if (this.canKick_) {
                output.writeBool(CANKICK_FIELD_NUMBER, this.canKick_);
            }
            if (this.online_ != OnlineType.OFFLINE.getNumber()) {
                output.writeEnum(ONLINE_FIELD_NUMBER, this.online_);
            }
            if (this.gender_ != Gender.MALE.getNumber()) {
                output.writeEnum(GENDER_FIELD_NUMBER, this.gender_);
            }
            if (this.lastOnline_ != 0) {
                output.writeInt64(LASTONLINE_FIELD_NUMBER, this.lastOnline_);
            }
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if (!getIdBytes().isEmpty()) {
                size = 0 + CodedOutputStream.computeBytesSize(ID_FIELD_NUMBER, getIdBytes());
            }
            if (!getNameBytes().isEmpty()) {
                size += CodedOutputStream.computeBytesSize(NAME_FIELD_NUMBER, getNameBytes());
            }
            if (this.lastViewTime_ != 0) {
                size += CodedOutputStream.computeInt64Size(LASTVIEWTIME_FIELD_NUMBER, this.lastViewTime_);
            }
            if (!getPicUrlBytes().isEmpty()) {
                size += CodedOutputStream.computeBytesSize(PICURL_FIELD_NUMBER, getPicUrlBytes());
            }
            if (this.canKick_) {
                size += CodedOutputStream.computeBoolSize(CANKICK_FIELD_NUMBER, this.canKick_);
            }
            if (this.online_ != OnlineType.OFFLINE.getNumber()) {
                size += CodedOutputStream.computeEnumSize(ONLINE_FIELD_NUMBER, this.online_);
            }
            if (this.gender_ != Gender.MALE.getNumber()) {
                size += CodedOutputStream.computeEnumSize(GENDER_FIELD_NUMBER, this.gender_);
            }
            if (this.lastOnline_ != 0) {
                size += CodedOutputStream.computeInt64Size(LASTONLINE_FIELD_NUMBER, this.lastOnline_);
            }
            this.memoizedSerializedSize = size;
            return size;
        }

        public static Participant parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (Participant) PARSER.parseFrom(data);
        }

        public static Participant parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Participant) PARSER.parseFrom(data, extensionRegistry);
        }

        public static Participant parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (Participant) PARSER.parseFrom(data);
        }

        public static Participant parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Participant) PARSER.parseFrom(data, extensionRegistry);
        }

        public static Participant parseFrom(InputStream input) throws IOException {
            return (Participant) PARSER.parseFrom(input);
        }

        public static Participant parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Participant) PARSER.parseFrom(input, extensionRegistry);
        }

        public static Participant parseDelimitedFrom(InputStream input) throws IOException {
            return (Participant) PARSER.parseDelimitedFrom(input);
        }

        public static Participant parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Participant) PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static Participant parseFrom(CodedInputStream input) throws IOException {
            return (Participant) PARSER.parseFrom(input);
        }

        public static Participant parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Participant) PARSER.parseFrom(input, extensionRegistry);
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Participant prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        protected Builder newBuilderForType(BuilderParent parent) {
            return new Builder(null);
        }

        static {
            DEFAULT_INSTANCE = new Participant();
            PARSER = new C03911();
        }

        public static Participant getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public Parser<Participant> getParserForType() {
            return PARSER;
        }

        public Participant getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    private ConversationProto() {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
    }

    public static FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        FileDescriptor.internalBuildGeneratedFileFrom(new String[]{"\nGodnoklassniki-android/proto/ru/ok/android/proto/ConversationProto.proto\u0012\u0013ru.ok.android.proto\"\u00a8\u0003\n\fConversation\u0012\n\n\u0002id\u0018\u0001 \u0001(\t\u0012\r\n\u0005topic\u0018\u0002 \u0001(\t\u0012\u0012\n\nbuiltTopic\u0018\u0003 \u0001(\t\u0012\u0013\n\u000blastMessage\u0018\u0004 \u0001(\t\u0012\u001b\n\u0013lastMessageAuthorId\u0018\u0005 \u0001(\t\u00124\n\u0004type\u0018\u0006 \u0001(\u000e2&.ru.ok.android.proto.Conversation.Type\u0012\u0013\n\u000blastMsgTime\u0018\u0007 \u0001(\u0003\u0012\u0014\n\flastViewTime\u0018\b \u0001(\u0003\u0012\u0018\n\u0010newMessagesCount\u0018\t \u0001(\u0005\u00127\n\fcapabilities\u0018\n \u0001(\u000b2!.ru.ok.android.proto.Capabilities\u0012\u001b\n\u0013lastView", "edMessageId\u0018\u000b \u0001(\t\u00126\n\fparticipants\u0018\f \u0003(\u000b2 .ru.ok.android.proto.Participant\u0012\u000f\n\u0007ownerId\u0018\r \u0001(\t\"\u001d\n\u0004Type\u0012\u000b\n\u0007PRIVATE\u0010\u0000\u0012\b\n\u0004CHAT\u0010\u0001\"\u0089\u0001\n\fCapabilities\u0012\u0011\n\tcanDelete\u0018\u0001 \u0001(\b\u0012\u000f\n\u0007canPost\u0018\u0002 \u0001(\b\u0012)\n!cantPostBecauseOnlyFriendsAllowed\u0018\u0003 \u0001(\b\u0012\u0014\n\fcanSendAudio\u0018\u0007 \u0001(\b\u0012\u0014\n\fcanSendVideo\u0018\b \u0001(\b\"\u00b8\u0002\n\u000bParticipant\u0012\n\n\u0002id\u0018\u0001 \u0001(\t\u0012\f\n\u0004name\u0018\u0002 \u0001(\t\u0012\u0014\n\flastViewTime\u0018\u0003 \u0001(\u0003\u0012\u000e\n\u0006picUrl\u0018\u0004 \u0001(\t\u0012\u000f\n\u0007canKick\u0018\u0005 \u0001(\b\u0012;\n\u0006online\u0018\u0006 \u0001(\u000e2+.ru.ok.android.proto.Par", "ticipant.OnlineType\u00127\n\u0006gender\u0018\u0007 \u0001(\u000e2'.ru.ok.android.proto.Participant.Gender\u0012\u0012\n\nlastOnline\u0018\b \u0001(\u0003\".\n\nOnlineType\u0012\u000b\n\u0007OFFLINE\u0010\u0000\u0012\u0007\n\u0003WEB\u0010\u0001\u0012\n\n\u0006MOBILE\u0010\u0002\"\u001e\n\u0006Gender\u0012\b\n\u0004MALE\u0010\u0000\u0012\n\n\u0006FEMALE\u0010\u0001b\u0006proto3"}, new FileDescriptor[0], new C03871());
        internal_static_ru_ok_android_proto_Conversation_descriptor = (Descriptor) getDescriptor().getMessageTypes().get(0);
        f75xd4f59cac = new FieldAccessorTable(internal_static_ru_ok_android_proto_Conversation_descriptor, new String[]{"Id", "Topic", "BuiltTopic", "LastMessage", "LastMessageAuthorId", "Type", "LastMsgTime", "LastViewTime", "NewMessagesCount", "Capabilities", "LastViewedMessageId", "Participants", "OwnerId"});
        internal_static_ru_ok_android_proto_Capabilities_descriptor = (Descriptor) getDescriptor().getMessageTypes().get(1);
        f74xeb513e99 = new FieldAccessorTable(internal_static_ru_ok_android_proto_Capabilities_descriptor, new String[]{"CanDelete", "CanPost", "CantPostBecauseOnlyFriendsAllowed", "CanSendAudio", "CanSendVideo"});
        internal_static_ru_ok_android_proto_Participant_descriptor = (Descriptor) getDescriptor().getMessageTypes().get(2);
        f76x41a9c696 = new FieldAccessorTable(internal_static_ru_ok_android_proto_Participant_descriptor, new String[]{"Id", "Name", "LastViewTime", "PicUrl", "CanKick", "Online", "Gender", "LastOnline"});
    }
}
