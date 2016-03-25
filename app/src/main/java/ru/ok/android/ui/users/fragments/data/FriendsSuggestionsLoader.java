package ru.ok.android.ui.users.fragments.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.GeneralDataLoader;
import java.util.List;
import ru.ok.android.services.processors.friends.GetFriendsProcessor;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.UserInfo;

public final class FriendsSuggestionsLoader extends GeneralDataLoader<List<UserInfo>> {
    private final int count;
    private RequestFieldsBuilder fields;
    private BroadcastReceiver networkReceiver;

    /* renamed from: ru.ok.android.ui.users.fragments.data.FriendsSuggestionsLoader.1 */
    class C13161 extends BroadcastReceiver {
        C13161() {
        }

        public void onReceive(Context context, Intent intent) {
            if (NetUtils.isConnectionAvailable(FriendsSuggestionsLoader.this.getContext(), false)) {
                FriendsSuggestionsLoader.this.unregisterReceiver();
                FriendsSuggestionsLoader.this.onContentChanged();
            }
        }
    }

    public FriendsSuggestionsLoader(Context context, int count) {
        super(context);
        this.count = count;
    }

    protected void onReset() {
        super.onReset();
        unregisterReceiver();
    }

    private void unregisterReceiver() {
        if (this.networkReceiver != null) {
            getContext().unregisterReceiver(this.networkReceiver);
            this.networkReceiver = null;
        }
    }

    protected List<UserInfo> loadData() {
        try {
            List<UserInfo> result = GetFriendsProcessor.getFriendsSuggestions(this.count, this.fields != null ? this.fields : GetFriendsProcessor.getDefaultFriendsSuggestionsFields());
            unregisterReceiver();
            return result;
        } catch (Throwable e) {
            registerReceiver();
            Logger.m186w(e, "Failed to fetch suggestion friends");
            return null;
        } catch (Throwable e2) {
            Logger.m179e(e2, "Failed to fetch suggestion friends");
            return null;
        }
    }

    private void registerReceiver() {
        if (this.networkReceiver == null) {
            this.networkReceiver = new C13161();
            getContext().registerReceiver(this.networkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }
}
