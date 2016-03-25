package ru.ok.android.ui.messaging.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import java.util.ArrayList;
import ru.ok.android.fragments.ConversationsFriendsFragment;
import ru.ok.android.fragments.ConversationsFriendsFragment.ConversationsFriendsFragmentListener;
import ru.ok.android.services.processors.video.MediaInfo;
import ru.ok.android.ui.activity.ShowDialogFragmentActivity;
import ru.ok.android.utils.NavigationHelper;

public final class SelectConversationForSendMediaActivity extends ShowDialogFragmentActivity implements ConversationsFriendsFragmentListener {
    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        if (!startLoginIfNeeded()) {
            setContentView();
            FragmentManager fragmentManager = getSupportFragmentManager();
            ConversationsFriendsFragment fragment = (ConversationsFriendsFragment) fragmentManager.findFragmentById(2131624639);
            if (fragment == null) {
                Bundle args = ConversationsFriendsFragment.newArguments(null, null, true, false, false);
                fragment = new ConversationsFriendsFragment();
                fragment.setArguments(args);
                fragmentManager.beginTransaction().replace(2131624639, fragment).commit();
            }
            fragment.setListener(this);
        }
    }

    private ArrayList<MediaInfo> getMediaInfosFromIntent() {
        return getIntent().getParcelableArrayListExtra("media_infos");
    }

    public void onConversationSelected(String conversationId, String userId) {
        if (TextUtils.isEmpty(conversationId)) {
            NavigationHelper.showMessagesForUser(this, userId, getMediaInfosFromIntent());
        } else {
            NavigationHelper.showMessagesForConversation(this, conversationId, userId, getMediaInfosFromIntent());
        }
        finish();
    }
}
