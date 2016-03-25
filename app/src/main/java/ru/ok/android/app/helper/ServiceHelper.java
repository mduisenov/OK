package ru.ok.android.app.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.app.OdnoklassnikiService;
import ru.ok.android.services.processors.discussions.DiscussionCommentLikesProcessor;
import ru.ok.android.services.processors.discussions.DiscussionLikesProcessor;
import ru.ok.android.services.processors.discussions.DiscussionSubscribeProcessor;
import ru.ok.android.services.processors.discussions.DiscussionUnSubscribeProcessor;
import ru.ok.android.services.processors.login.LoginByTokenProcessorNew;
import ru.ok.android.services.processors.login.LoginProcessorNew;
import ru.ok.android.services.processors.login.LogoutProcessorNew;
import ru.ok.android.services.processors.messaging.MessageLikesProcessor;
import ru.ok.android.services.processors.music.GetAlbumInfoCommandProcessor;
import ru.ok.android.services.processors.music.GetArtistInfoCommandProcessor;
import ru.ok.android.services.processors.music.GetCollectionInfoCommandProcessor;
import ru.ok.android.services.processors.music.GetPlayListInfoCommandProcessor;
import ru.ok.android.services.processors.music.StatusMusicProcessor;
import ru.ok.android.services.processors.offline.discussions.DiscussionCommentsSendAllProcessor;
import ru.ok.android.services.processors.offline.discussions.DiscussionCommentsSendSingleProcessor;
import ru.ok.android.services.processors.registration.CheckPhoneProcessor;
import ru.ok.android.services.processors.registration.CompleteUserInfoProcessor;
import ru.ok.android.services.processors.registration.ConfirmUserProcessor;
import ru.ok.android.services.processors.registration.GetExistingUserBySmsProcessor;
import ru.ok.android.services.processors.registration.ProfileActivityProcessor;
import ru.ok.android.services.processors.registration.RecoverUserBySmsProcessor;
import ru.ok.android.services.processors.registration.RegainUserProcessor;
import ru.ok.android.services.processors.registration.RegisterUserProcessor;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;
import ru.ok.model.Discussion;
import ru.ok.model.UserInfo;

public final class ServiceHelper {
    private final Context _context;
    private final Handler _handler;
    private final List<CommandListener> _listeners;
    private final Map<String, RequestStatusInfo> _statuses;

    /* renamed from: ru.ok.android.app.helper.ServiceHelper.1 */
    class C02261 extends ResultReceiver {
        final /* synthetic */ String commandName;
        final /* synthetic */ boolean isOnlyDirectListener;
        final /* synthetic */ WeakReference weakListener;

        C02261(Handler handler, String str, WeakReference weakReference, boolean isOnlyDirectListener) {
            this.commandName = str;
            this.weakListener = weakReference;
            this.isOnlyDirectListener = isOnlyDirectListener;
            super(handler);
        }

        protected void onReceiveResult(int resultCode, Bundle resultData) {
            ResultCode result = ResultCode.byServiceResult(resultCode);
            RequestStatusInfo info = ServiceHelper.this.getStatusInfo(this.commandName);
            info.status = resultCode == 1 ? RequestStatus.FINISHED : RequestStatus.FAILED;
            info.lastResultTime = System.currentTimeMillis();
            CommandListener singleListener = this.weakListener != null ? (CommandListener) this.weakListener.get() : null;
            if (singleListener != null) {
                singleListener.onCommandResult(this.commandName, result, resultData);
            }
            if (!this.isOnlyDirectListener) {
                for (Object o : new ArrayList(ServiceHelper.this._listeners)) {
                    ((CommandListener) o).onCommandResult(this.commandName, result, resultData);
                }
            }
        }
    }

    public interface CommandListener {
        void onCommandResult(String str, ResultCode resultCode, Bundle bundle);
    }

    public enum ResultCode {
        SUCCESS,
        FAILURE;

        public static ResultCode byServiceResult(int resultCode) {
            switch (resultCode) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    return SUCCESS;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    return FAILURE;
                default:
                    throw new IllegalArgumentException("Don't know " + resultCode + " code");
            }
        }
    }

    public ServiceHelper(Context context) {
        this._handler = new Handler();
        this._statuses = new ConcurrentHashMap();
        this._listeners = new LinkedList();
        this._context = context;
    }

    public static ServiceHelper from() {
        return Utils.getServiceHelper();
    }

    public void addListener(CommandListener listener) {
        this._listeners.add(listener);
    }

    public void removeListener(CommandListener listener) {
        this._listeners.remove(listener);
    }

    public void loadCommentLikes(Discussion discussion, String commentId, String anchor) {
        Intent intent = createIntent(DiscussionCommentLikesProcessor.commandName(commentId));
        DiscussionCommentLikesProcessor.fillIntent(intent, discussion, commentId, anchor);
        sendCommand(intent);
    }

    public void loadDiscussionLikes(Discussion discussion, String anchor) {
        Intent intent = createIntent(DiscussionLikesProcessor.commandName(discussion));
        DiscussionLikesProcessor.fillIntent(intent, discussion, anchor);
        sendCommand(intent);
    }

    public void loadMessageLikes(String conversationId, String messageId, String anchor) {
        Intent intent = createIntent(MessageLikesProcessor.commandName(messageId));
        MessageLikesProcessor.fillIntent(intent, conversationId, messageId, anchor);
        sendCommand(intent);
    }

    public void subscribeToDiscussion(Discussion discussion) {
        Intent intent = createIntent(DiscussionSubscribeProcessor.commandName(discussion));
        DiscussionSubscribeProcessor.fillIntent(intent, discussion);
        sendCommand(intent);
    }

    public void unSubscribeFromDiscussion(Discussion discussion) {
        Intent intent = createIntent(DiscussionUnSubscribeProcessor.commandName(discussion));
        DiscussionUnSubscribeProcessor.fillIntent(intent, discussion);
        sendCommand(intent);
    }

    public void getArtistInfo(long artistId, CommandListener listener) {
        Intent intent = createIntent(GetArtistInfoCommandProcessor.commandName(artistId), listener);
        GetArtistInfoCommandProcessor.fillIntent(intent, artistId);
        sendCommand(intent);
    }

    public void getAlbumInfo(long albumId, CommandListener listener) {
        Intent intent = createIntent(GetAlbumInfoCommandProcessor.commandName(albumId), listener);
        GetAlbumInfoCommandProcessor.fillIntent(intent, albumId);
        sendCommand(intent);
    }

    public void getCollectionInfo(long id, CommandListener listener) {
        Intent intent = createIntent(GetCollectionInfoCommandProcessor.commandName(id), listener);
        GetCollectionInfoCommandProcessor.fillIntent(intent, id);
        sendCommand(intent);
    }

    public void getPlayListInfo(long id, CommandListener listener) {
        Intent intent = createIntent(GetPlayListInfoCommandProcessor.commandName(id), listener);
        GetPlayListInfoCommandProcessor.fillIntent(intent, id);
        sendCommand(intent);
    }

    public void sendUndeliveredDiscussionComments() {
        sendCommand(createIntent(DiscussionCommentsSendAllProcessor.commandName()));
    }

    public void sendDiscussionComment(int commentId) {
        Intent intent = createIntent(DiscussionCommentsSendSingleProcessor.commandName(commentId));
        DiscussionCommentsSendSingleProcessor.fillIntent(intent, commentId);
        sendCommand(intent);
    }

    public void getStatusMusic(long trackId) {
        Intent intent = createIntent(StatusMusicProcessor.commandName());
        StatusMusicProcessor.fillIntent(intent, trackId);
        sendCommand(intent);
    }

    public void tryToRegisterUser(String login, CommandListener commandListener) {
        Intent intent = createIntent(RegisterUserProcessor.commandName(), commandListener);
        RegisterUserProcessor.fillIntent(intent, login);
        sendCommand(intent, commandListener != null);
    }

    public void tryToGetUsers(String uid, String phone, String pin, CommandListener commandListener) {
        Intent intent = createIntent(CheckPhoneProcessor.commandName(), commandListener);
        CheckPhoneProcessor.fillIntent(intent, uid, phone, pin);
        sendCommand(intent, commandListener != null);
    }

    public void getExistingUserBySms(String phone, String pin, CommandListener commandListener) {
        Intent intent = createIntent(GetExistingUserBySmsProcessor.commandName(), commandListener);
        GetExistingUserBySmsProcessor.fillIntent(intent, phone, pin);
        sendCommand(intent, commandListener != null);
    }

    public void tryToConfirmUser(String uid, String login, String pin, String newPassword, CommandListener commandListener) {
        Intent intent = createIntent(ConfirmUserProcessor.commandName(), commandListener);
        ConfirmUserProcessor.fillIntent(intent, uid, login, pin, newPassword);
        sendCommand(intent, commandListener != null);
    }

    public void tryToRecoverUserBySms(String uid, String pin, String password, CommandListener commandListener) {
        Intent intent = createIntent(RecoverUserBySmsProcessor.commandName(), commandListener);
        RecoverUserBySmsProcessor.fillIntent(intent, uid, pin, password);
        sendCommand(intent, commandListener != null);
    }

    public void tryToRegainUser(String uid, String regainUid, String pin, String newPassword, CommandListener commandListener) {
        Intent intent = createIntent(RegainUserProcessor.commandName(), commandListener);
        RegainUserProcessor.fillIntent(intent, uid, regainUid, pin, newPassword);
        sendCommand(intent, commandListener != null);
    }

    public void tryToUpdateUserInfo(String oldPassword, String newPassword, UserInfo person, CommandListener commandListener) {
        Intent intent = createIntent(CompleteUserInfoProcessor.commandName(), commandListener);
        CompleteUserInfoProcessor.fillIntent(intent, oldPassword, newPassword, person);
        sendCommand(intent, commandListener != null);
    }

    public void prepareProfileActivity(CommandListener commandListener) {
        sendCommand(createIntent(ProfileActivityProcessor.commandName(), commandListener), commandListener != null);
    }

    public void tryToLogin(String login, String passwd, CommandListener commandListener, boolean isReLogin, String verificationToken) {
        Intent intent = createIntent(LoginProcessorNew.commandName(), commandListener, isReLogin);
        LoginProcessorNew.fillIntent(intent, login, passwd, verificationToken);
        sendCommand(intent, commandListener != null);
    }

    public void tryToLogin(String token, boolean forceLogin, CommandListener commandListener, boolean isReLogin, String verificationToken) {
        Intent intent = createIntent(LoginByTokenProcessorNew.commandName(), commandListener, isReLogin);
        LoginByTokenProcessorNew.fillIntent(intent, token, forceLogin, verificationToken);
        sendCommand(intent, commandListener != null);
    }

    public void tryToLogout(CommandListener commandListener) {
        sendCommand(createIntent(LogoutProcessorNew.commandName(), commandListener), commandListener != null);
    }

    private void sendCommand(Intent intent) {
        sendCommand(intent, false);
    }

    private void sendCommand(Intent intent, boolean hasDirectListener) {
        RequestStatusInfo info = getStatusInfo(intent.getStringExtra("COMMAND_NAME"));
        if (hasDirectListener || info.status != RequestStatus.LOADING) {
            info.status = RequestStatus.LOADING;
            info.startTime = System.currentTimeMillis();
            this._context.startService(intent);
            return;
        }
        Logger.m173d("Command '%s' not sent [%s, %s]", commandName, Boolean.valueOf(hasDirectListener), info.status);
    }

    private Intent createIntent(String commandName) {
        return createIntent(commandName, null, false);
    }

    private Intent createIntent(String commandName, CommandListener directListener) {
        return createIntent(commandName, directListener, false);
    }

    private Intent createIntent(String commandName, CommandListener directListener, boolean isOnlyDirectListener) {
        Intent intent = new Intent(this._context, OdnoklassnikiService.class);
        intent.putExtra("COMMAND_NAME", commandName);
        intent.putExtra("RESULT_RECEIVER", new C02261(this._handler, commandName, new WeakReference(directListener), isOnlyDirectListener));
        return intent;
    }

    private RequestStatusInfo getStatusInfo(String commandName) {
        RequestStatusInfo info = (RequestStatusInfo) this._statuses.get(commandName);
        if (info != null) {
            return info;
        }
        info = new RequestStatusInfo();
        this._statuses.put(commandName, info);
        return info;
    }
}
