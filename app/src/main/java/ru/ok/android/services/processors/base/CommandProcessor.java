package ru.ok.android.services.processors.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import ru.ok.android.C0206R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.exception.NoConnectionException;
import ru.ok.android.services.transport.exception.ServerNotFoundException;
import ru.ok.android.services.transport.exception.TransportLevelException;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.exceptions.ServerReturnErrorException;

public abstract class CommandProcessor {
    protected final JsonSessionTransportProvider _transportProvider;

    public enum ErrorType {
        GENERAL(2131165791),
        TRANSPORT(2131166539),
        YOU_ARE_IN_BLACK_LIST(2131166513),
        YOU_ARE_IN_GROUP_BLACK_LIST(2131166886),
        RESTRICTED_ACCESS_FOR_NON_FRIENDS(2131166515),
        DISCUSSIONS_TOO_MANY_CONVERSATIONS(2131166509),
        RESTRICTED_ACCESS_FOR_NON_MEMBERS(2131166002),
        USER_BLOCKED(2131166790),
        DISCUSSION_DELETED_OR_BLOCKED(2131165711),
        TOO_MANY_USERS(2131165840),
        USER_DO_NOT_RECEIVE_MESSAGES(2131166514),
        CENSOR_MATCH(2131165559),
        TOO_MANY_GROUPS_RECENTLY_CREATED(2131166734),
        RESTRICTED_GROUPS_ACCESS(2131166466),
        NO_INTERNET_TOO_LONG(2131166273),
        NO_INTERNET(2131166735),
        SERVER_NOT_FOUND(2131166735),
        TEXT_TOO_LONG(2131166721),
        CREATE_TOO_MANY_CHATS(2131165802),
        RESTRICTED_ACCESS_SECTION_FOR_FRIENDS(2131165831),
        RESTRICTED_ACCESS_ACTION_BLOCKED(2131165793),
        JOIN_ALREADY_SEND(2131165942),
        GROUP_DUPLICATE_JOIN_SEND(2131165942),
        TIMEOUT_EXCEEDED(2131166723),
        CURRENT_USER_NOT_ACTIVATED(2131165659),
        CHAT_MAX_PARTICIPANT_COUNT_LIMIT(2131165572),
        CHAT_PARTICIPANTS_EMPTY_BLOCKED_USERS(2131165573),
        PASSWORD(2131165846),
        MAX_LENGTH(2131165825),
        INVALID_SYMBOLS(2131165821),
        REGISTRATION_NOT_AVAILABLE(2131166442),
        SEND_SMS(2131165835),
        PHONE_WRONG(2131165828),
        ACTIVITY_RESTRICTED(2131165796),
        LOCATION_UNKNOWN(2131165823),
        SMS_CODE_WRONG(2131165844),
        SMS_ACTIVATION_RETRIES_EXHAUSTED(2131165795),
        SMS_ACTIVATION_EXPIRED(2131165794),
        USER_EXISTS(2131166791),
        USERNAME_WRONG(2131166054),
        USER_PASSWORD_YET(2131165826),
        STICKER_INVALID_MESSAGE(2131166196),
        STICKER_SERVICE_UNAVAILABLE(2131166540);
        
        private final int defaultErrorMessage;

        private ErrorType(int defaultErrorMessage) {
            this.defaultErrorMessage = defaultErrorMessage;
        }

        public int getDefaultErrorMessage() {
            return this.defaultErrorMessage;
        }

        public static ErrorType safeValueOf(String name) {
            ErrorType errorType = null;
            if (!TextUtils.isEmpty(name)) {
                try {
                    errorType = valueOf(name);
                } catch (Exception e) {
                }
            }
            return errorType;
        }

        @NonNull
        public static ErrorType from(Bundle errorBundle) {
            ErrorType errorType = null;
            if (errorBundle != null) {
                errorType = safeValueOf(errorBundle.getString("ERROR_TYPE"));
            }
            if (errorType == null) {
                return GENERAL;
            }
            return errorType;
        }

        public static ErrorType fromException(Exception e) {
            return fromException(e, false);
        }

        public static ErrorType fromException(Exception e, boolean showGeneralIfNonSpecified) {
            if (e instanceof NoConnectionException) {
                return NO_INTERNET;
            }
            if (e instanceof ServerReturnErrorException) {
                return fromServerException((ServerReturnErrorException) e, showGeneralIfNonSpecified);
            }
            if (e instanceof ServerNotFoundException) {
                return SERVER_NOT_FOUND;
            }
            if (e instanceof TransportLevelException) {
                return TRANSPORT;
            }
            return GENERAL;
        }

        private static ErrorType getRegistrationErrorType(String message) {
            if (message != null) {
                if (message.contains("error.registration.not.available")) {
                    return REGISTRATION_NOT_AVAILABLE;
                }
                if (message.contains("error.sending.sms")) {
                    return SEND_SMS;
                }
                if (message.contains("error.phone.lengthIncorrect") || message.contains("error.phone.wrong")) {
                    return PHONE_WRONG;
                }
                if (message.contains("errors.userActivity.restricted")) {
                    return ACTIVITY_RESTRICTED;
                }
                if (message.contains("error.location.unknown")) {
                    return LOCATION_UNKNOWN;
                }
                if (message.contains("error.wrong.code") || message.contains("errors.confirmation-code.wrong")) {
                    return SMS_CODE_WRONG;
                }
                if (message.contains("error.activation.retries.exhausted")) {
                    return SMS_ACTIVATION_RETRIES_EXHAUSTED;
                }
                if (message.contains("error.activation.expired")) {
                    return SMS_ACTIVATION_EXPIRED;
                }
                if (message.contains("errors.user-uniquename.yet-exists")) {
                    return USER_EXISTS;
                }
                if (message.contains("errors.uniqueName.wrong")) {
                    return USERNAME_WRONG;
                }
                if (message.contains("errors.user.password.yet")) {
                    return USER_PASSWORD_YET;
                }
            }
            return null;
        }

        public static ErrorType fromServerException(int errorCode, String message, boolean showGeneralIfNonSpecified) {
            ErrorType registrationErrorType = getRegistrationErrorType(message);
            if (registrationErrorType != null) {
                return registrationErrorType;
            }
            switch (errorCode) {
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    return TRANSPORT;
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                case 1200:
                    if (message != null) {
                        if (message.contains("error.friend.restricted-access") || message.contains("child-protection.blocked")) {
                            return RESTRICTED_ACCESS_FOR_NON_FRIENDS;
                        }
                        if (message.contains("errors.discussions.too-many-conversations")) {
                            return DISCUSSIONS_TOO_MANY_CONVERSATIONS;
                        }
                        if (message.contains("errors.send-message-to-multichat.too-many-messages")) {
                            return DISCUSSIONS_TOO_MANY_CONVERSATIONS;
                        }
                        if (message.contains("errors.create-multichat.too-many-chats")) {
                            return CREATE_TOO_MANY_CHATS;
                        }
                        if (message.contains("errors.send-message.too-many-users")) {
                            return TOO_MANY_USERS;
                        }
                        if (message.contains("error.groups.join.alreadymember")) {
                            return JOIN_ALREADY_SEND;
                        }
                        if (message.contains("errors.user-blocked")) {
                            return YOU_ARE_IN_BLACK_LIST;
                        }
                        if (message.contains("error.groups.common.notmember")) {
                            return RESTRICTED_ACCESS_FOR_NON_MEMBERS;
                        }
                        if (message.contains("errors.create-forum-message.user-blocked")) {
                            return YOU_ARE_IN_GROUP_BLACK_LIST;
                        }
                        if (message.contains("Exception from EJB service")) {
                            return TRANSPORT;
                        }
                        if (message.contains("errors.maxlength")) {
                            return MAX_LENGTH;
                        }
                        if (message.contains("errors.name.invalid-symbols")) {
                            return INVALID_SYMBOLS;
                        }
                    }
                    return showGeneralIfNonSpecified ? GENERAL : USER_DO_NOT_RECEIVE_MESSAGES;
                case Message.ATTACHES_FIELD_NUMBER /*7*/:
                    return RESTRICTED_ACCESS_ACTION_BLOCKED;
                case C0206R.styleable.Toolbar_logoDescription /*22*/:
                    return CURRENT_USER_NOT_ACTIVATED;
                case C0206R.styleable.Theme_buttonBarStyle /*50*/:
                case 454:
                    return CENSOR_MATCH;
                case C0206R.styleable.Theme_buttonStyleSmall /*100*/:
                    if (message.contains("is too long")) {
                        return TEXT_TOO_LONG;
                    }
                    return GENERAL;
                case 300:
                    if (message != null) {
                        if (message.contains("not.found.discussion") || message.contains("not.found.shares")) {
                            return DISCUSSION_DELETED_OR_BLOCKED;
                        }
                        if (message.contains("not.found.user")) {
                            return USER_BLOCKED;
                        }
                    }
                    return GENERAL;
                case 455:
                case 1103:
                    if (message != null && message.contains("child-protection.blocked")) {
                        return RESTRICTED_ACCESS_FOR_NON_FRIENDS;
                    }
                    if (message != null && message.contains("errors.privacy")) {
                        return RESTRICTED_ACCESS_SECTION_FOR_FRIENDS;
                    }
                    if (message == null || !message.contains("error.friend.restricted-access")) {
                        return showGeneralIfNonSpecified ? GENERAL : YOU_ARE_IN_BLACK_LIST;
                    } else {
                        return RESTRICTED_ACCESS_SECTION_FOR_FRIENDS;
                    }
                case 456:
                    return RESTRICTED_GROUPS_ACCESS;
                case 511:
                    if (message == null || !message.contains("error.group.too-many-groups-recently-created")) {
                        return GENERAL;
                    }
                    return TOO_MANY_GROUPS_RECENTLY_CREATED;
                case 610:
                    return GROUP_DUPLICATE_JOIN_SEND;
                case 704:
                    return TIMEOUT_EXCEEDED;
                case 707:
                    return STICKER_SERVICE_UNAVAILABLE;
                case 708:
                    return STICKER_INVALID_MESSAGE;
                case 800:
                    return CHAT_MAX_PARTICIPANT_COUNT_LIMIT;
                case 801:
                    if (message.contains("add.friends.which.blocks.me")) {
                        return CHAT_PARTICIPANTS_EMPTY_BLOCKED_USERS;
                    }
                    return GENERAL;
                default:
                    return GENERAL;
            }
        }

        public static ErrorType fromServerException(ServerReturnErrorException e) {
            return fromServerException(e, false);
        }

        public static ErrorType fromServerException(ServerReturnErrorException e, boolean showGeneralIfNonSpecified) {
            return fromServerException(e.getErrorCode(), e.getErrorMessage(), showGeneralIfNonSpecified);
        }
    }

    protected abstract int doCommand(Context context, Intent intent, Bundle bundle) throws Exception;

    public CommandProcessor(JsonSessionTransportProvider transportProvider) {
        this._transportProvider = transportProvider;
    }

    public final boolean processCommand(Context context, Intent data) {
        ResultReceiver receiver = (ResultReceiver) data.getParcelableExtra("RESULT_RECEIVER");
        try {
            Bundle outBundle = new Bundle();
            int code = doCommand(context, data, outBundle);
            if (receiver != null) {
                receiver.send(code, outBundle);
            }
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to run command " + getClass().getSimpleName());
            if (receiver != null) {
                receiver.send(2, createErrorBundle(e));
            }
        }
        return true;
    }

    public static Bundle createErrorBundle(Exception e) {
        return createErrorBundle(e, false);
    }

    public static Bundle createErrorBundle(Exception e, boolean showGeneralIfNonSpecified) {
        Bundle errorBundle = new Bundle();
        fillErrorBundle(errorBundle, e, showGeneralIfNonSpecified);
        return errorBundle;
    }

    public static void fillErrorBundle(Bundle errorBundle, Exception e) {
        fillErrorBundle(errorBundle, e, false);
    }

    public static void fillErrorBundle(Bundle errorBundle, Exception e, boolean showGeneralIfNonSpecified) {
        if (errorBundle != null) {
            errorBundle.putString("ERROR_TYPE", ErrorType.fromException(e, showGeneralIfNonSpecified).name());
        }
    }

    public static String extractProcessorName(String commandName) {
        return commandName.split("/")[0];
    }
}
