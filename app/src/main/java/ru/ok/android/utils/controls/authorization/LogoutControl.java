package ru.ok.android.utils.controls.authorization;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.app.helper.AccountsHelper;
import ru.ok.android.app.helper.ServiceHelper.CommandListener;
import ru.ok.android.app.helper.ServiceHelper.ResultCode;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.DataBaseHelper;
import ru.ok.android.db.access.AuthorizedUsersStorageFacade;
import ru.ok.android.fragments.web.WebBaseFragment;
import ru.ok.android.model.cache.ram.ConversationsCache;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.model.cache.ram.UsersCache;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.services.app.messaging.OdklMessagingEventsService;
import ru.ok.android.services.persistent.PersistentTaskService;
import ru.ok.android.services.processors.login.LogoutProcessorNew;
import ru.ok.android.services.processors.settings.PhotoRollSettingsHelper;
import ru.ok.android.services.processors.stickers.StickersManager;
import ru.ok.android.services.transport.AuthSessionDataStore;
import ru.ok.android.services.utils.users.OnlineUsersManager;
import ru.ok.android.utils.LibverifyUtil;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.android.utils.settings.Settings;
import ru.ok.android.widget.music.MusicBaseWidget;

public final class LogoutControl {
    private CommandCallBack commandCallBack;
    private Context context;
    private OnLogoutListener listener;

    /* renamed from: ru.ok.android.utils.controls.authorization.LogoutControl.1 */
    static /* synthetic */ class C14441 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode;

        static {
            $SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode = new int[ResultCode.values().length];
            try {
                $SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[ResultCode.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[ResultCode.FAILURE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    class CommandCallBack implements CommandListener {
        private OnLogoutListener listener;

        CommandCallBack(OnLogoutListener listener) {
            this.listener = listener;
        }

        public void onCommandResult(String commandName, ResultCode resultCode, Bundle data) {
            if (LogoutProcessorNew.isIt(commandName)) {
                switch (C14441.$SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[resultCode.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        LogoutControl.onLogoutSuccessful(this.listener);
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        LogoutControl.onLogoutError(data, this.listener);
                    default:
                }
            }
        }
    }

    public LogoutControl(Context context) {
        this.context = context;
    }

    private void tryToLogout(CommandListener commandListener) {
        Logger.m172d("");
        Utils.getServiceHelper().tryToLogout(commandListener);
        Bundle bundleInput = new Bundle();
        bundleInput.putInt("impldract", 8);
        GlobalBus.send(2131624084, new BusEvent(bundleInput));
        generalLogoutLogic(this.context, OdnoklassnikiApplication.getCurrentUser().uid);
        if (this.listener != null) {
            this.listener.onStartLogout();
        }
    }

    public void tryToLogout(OnLogoutListener listener) {
        tryToLogout(createCommandCallBack(listener));
    }

    private static void onLogoutSuccessful(OnLogoutListener listener) {
        Logger.m172d("");
        if (listener != null) {
            listener.onLogoutSuccessful();
        }
    }

    private static void onLogoutError(Bundle data, OnLogoutListener listener) {
        Logger.m172d("");
        if (listener != null) {
            listener.onLogoutError((Exception) data.getSerializable(LogoutProcessorNew.KEY_ERROR));
        }
    }

    public static void generalLogoutLogic(Context context, String userId) {
        EventsManager.getInstance().stopEventsObserved();
        EventsManager.getInstance().clear();
        DataBaseHelper.clearDbAsync(context);
        PersistentTaskService.reset(context);
        clearLoginState(context);
        MusicBaseWidget.requestAllWidgetsUpdate(context);
        OnlineUsersManager.getInstance().clear();
        AuthorizedUsersStorageFacade.logOutCurrentUser(userId);
        MusicService.finishPlayMusic(context);
        WebBaseFragment.clearCookie();
        EventsManager.updateAppIconBadget(0);
        AuthSessionDataStore.clearDefault(context);
        context.stopService(new Intent(context, OdklMessagingEventsService.class));
        AccountsHelper.deleteAccounts(context);
        UsersCache.getInstance().clear();
        ConversationsCache.getInstance().clear();
        MessagesCache.getInstance().clear();
        StickersManager.clear(context);
        LibverifyUtil.logoutDevice(context, new Handler());
        resetSettingsForDebug(context);
        PhotoRollSettingsHelper.resetSettingsOnLogout();
    }

    private static void resetSettingsForDebug(Context context) {
    }

    public static void clearLoginState(Context context) {
        OdnoklassnikiApplication.setCurrentUser(null);
        Settings.setNoLoginState(context);
    }

    private CommandCallBack createCommandCallBack(OnLogoutListener listener) {
        this.listener = listener;
        this.commandCallBack = new CommandCallBack(listener);
        return this.commandCallBack;
    }
}
