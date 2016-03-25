package ru.ok.android.ui.messaging.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import ru.ok.android.app.helper.AccountsHelper;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.ui.fragments.messages.MessagesFragment;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NavigationHelper.FragmentLocation;

public final class MessagesActivity extends OdklSubActivity {
    private String conversationId;

    protected void postProcessView() {
        super.postProcessView();
        toolbarLockScroll();
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bundle arguments = null;
        String mimeType = getString(2131166207);
        if (DeviceUtils.isSonyDevice() || TextUtils.equals(intent.getType(), mimeType)) {
            Logger.m173d("Oh! %s found!", mimeType);
            String userId = AccountsHelper.extractUserIdFromContactUri(this, intent);
            if (!TextUtils.isEmpty(userId)) {
                AppLaunchLog.contacts();
                arguments = MessagesFragment.newArgumentsUser(userId, true, null);
                if (savedInstanceState == null) {
                    StatisticManager.getInstance().addStatisticEvent("app-launched-from-contacts", new Pair("source", "messages"));
                }
            }
        }
        if (arguments == null) {
            this.conversationId = getIntent().getStringExtra("CONVERSATION_ID");
            if (!TextUtils.isEmpty(this.conversationId)) {
                arguments = MessagesFragment.newArgumentsConversation(this.conversationId, null, false, null);
            }
        }
        if (arguments != null) {
            intent.putExtra("key_class_name", MessagesFragment.class);
            intent.putExtra("key_argument_name", arguments);
            intent.putExtra("key_toolbar_visible", false);
            intent.putExtra("key_location_type", FragmentLocation.right.name());
        }
        super.onCreateLocalized(savedInstanceState);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String conversationId = intent.getStringExtra("CONVERSATION_ID");
        if (!TextUtils.isEmpty(conversationId) && !TextUtils.equals(conversationId, this.conversationId)) {
            startActivity(NavigationHelper.smartLaunchMessagesIntent(this, conversationId));
        }
    }

    protected void onResume() {
        super.onResume();
        startLoginIfNeeded();
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
