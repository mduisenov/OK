package ru.ok.android.services.processors;

import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract.Contacts;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.db.access.fillers.UserInfoValuesFiller;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.json.users.JsonGetUsersInfoParser;
import ru.ok.java.api.request.search.SearchByContactsBookRequest;
import ru.ok.model.ContactInfo;
import ru.ok.model.UserInfo;

public final class GetRecommendedFriendsProcessor {
    public ArrayList<ContactInfo> getContactsFromPhonebook() {
        ArrayList<ContactInfo> arrayList;
        ContentResolver contentResolver = OdnoklassnikiApplication.getContext().getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(Contacts.CONTENT_URI, null, null, null, null);
            arrayList = new ArrayList();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    arrayList.add(ContactInfo.getContactInfoByCursor(contentResolver, cursor));
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            arrayList = new ArrayList();
            return arrayList;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return arrayList;
    }

    @Subscribe(on = 2131623944, to = 2131624065)
    public void getRecommendedFriends(BusEvent event) {
        Exception e;
        try {
            ArrayList<UserInfo> users = loadRecommendedFriends(event);
            ArrayList<String> ids = new ArrayList();
            Iterator i$ = users.iterator();
            while (i$.hasNext()) {
                ids.add(((UserInfo) i$.next()).getId());
            }
            Bundle output = new Bundle();
            output.putInt("COUNT", users.size());
            output.putParcelableArrayList("USERS", users);
            output.putStringArrayList("USER_IDS", ids);
            saveUsers(users);
            GlobalBus.send(2131624219, new BusEvent(event.bundleInput, output, -1));
        } catch (BaseApiException e2) {
            e = e2;
            GlobalBus.send(2131624219, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        } catch (JSONException e3) {
            e = e3;
            GlobalBus.send(2131624219, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        } catch (RemoteException e4) {
            e = e4;
            GlobalBus.send(2131624219, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        } catch (OperationApplicationException e5) {
            e = e5;
            GlobalBus.send(2131624219, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    public ArrayList<UserInfo> getUsersFromJson(JSONObject jsonObject) {
        Exception e;
        ArrayList<UserInfo> users = new ArrayList();
        try {
            users = new JsonGetUsersInfoParser(null).parser(jsonObject.getJSONArray("users"));
        } catch (JSONException e2) {
            e = e2;
            e.printStackTrace();
            return users;
        } catch (ResultParsingException e3) {
            e = e3;
            e.printStackTrace();
            return users;
        }
        return users;
    }

    private ArrayList<UserInfo> loadRecommendedFriends(BusEvent event) throws BaseApiException, JSONException, RemoteException, OperationApplicationException {
        return getUsersFromJson(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new SearchByContactsBookRequest(getContactsFromPhonebook())).getResultAsObject());
    }

    public void saveUsers(ArrayList<UserInfo> users) throws RemoteException, OperationApplicationException {
        UsersStorageFacade.insertUsers(new ArrayList(users), UserInfoValuesFiller.FRIENDS);
    }
}
