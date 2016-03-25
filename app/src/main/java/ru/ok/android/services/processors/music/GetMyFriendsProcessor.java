package ru.ok.android.services.processors.music;

import android.os.Message;
import android.os.Messenger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.fillers.UserInfoValuesFiller;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.services.app.Messages;
import ru.ok.android.services.processors.users.GetUserInfoProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.request.param.RequestCollectionParam;
import ru.ok.java.api.request.users.UserInfoRequest;
import ru.ok.java.api.wmf.http.HttpGetMyFriendsRequest;
import ru.ok.java.api.wmf.json.JsonGetMusicFriendsParser;
import ru.ok.model.UserInfo;
import ru.ok.model.music.MusicUserInfo;
import ru.ok.model.wmf.WmfUserInfo;

public final class GetMyFriendsProcessor {
    @Subscribe(on = 2131623944, to = 2131624059)
    public void getMyFriends(BusEvent event) {
        Messenger messenger = GlobalBus.eventToMessage(event).replyTo;
        Logger.m172d("visit get my music friends processor");
        getMyFriends(messenger);
    }

    private void getMyFriends(Messenger messenger) {
        try {
            List<MusicUserInfo> users = getMyFriendsValue();
            MusicStorageFacade.insertMusicFriends(OdnoklassnikiApplication.getContext(), users);
            Message mes = Message.obtain(null, 143, 0, 0);
            mes.obj = users.toArray(new MusicUserInfo[users.size()]);
            Messages.safeSendMessage(mes, messenger);
            Logger.m172d("Get my music " + users.toString());
        } catch (Exception e) {
            Logger.m172d("Error get friends link " + e.getMessage());
            Message msg = Message.obtain(null, 144, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, messenger);
        }
    }

    private List<MusicUserInfo> getMyFriendsValue() throws Exception {
        List<WmfUserInfo> usersList = new JsonGetMusicFriendsParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetMyFriendsRequest(0, 500, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
        Map<String, WmfUserInfo> usersMap = new HashMap();
        List<String> uids = new ArrayList();
        for (WmfUserInfo wmfUserInfo : usersList) {
            uids.add(wmfUserInfo.uid);
            usersMap.put(wmfUserInfo.uid, wmfUserInfo);
        }
        List<UserInfo> users = getUserValueInfo(uids, UserInfoValuesFiller.MUSIC.getRequestFields(), true);
        List<MusicUserInfo> returnList = new ArrayList();
        for (UserInfo user : users) {
            WmfUserInfo info = (WmfUserInfo) usersMap.get(user.uid);
            List<MusicUserInfo> list = returnList;
            list.add(new MusicUserInfo(user.uid, user.firstName, user.lastName, user.name, user.picUrl, user.age, user.location, user.online, user.lastOnline, user.genderType, Utils.userCanCall(user), Utils.canSendVideoMailTo(user), user.getTag(), info.tracksCount, info.lastAddTime, user.pid));
        }
        return returnList;
    }

    public static List<UserInfo> getUserValueInfo(List<String> uids, String fields, boolean emptyPic) throws BaseApiException {
        List<UserInfo> usersOut = new ArrayList();
        int i = 0;
        while (i < uids.size()) {
            usersOut.addAll(GetUserInfoProcessor.processGetUserInfoResult(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new UserInfoRequest(new RequestCollectionParam(uids.subList(i, i + 100 < uids.size() ? i + 100 : uids.size())), fields, emptyPic))));
            i += 100;
        }
        return usersOut;
    }
}
