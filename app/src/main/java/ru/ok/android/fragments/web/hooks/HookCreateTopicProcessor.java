package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import android.text.TextUtils;
import ru.ok.android.utils.Logger;

public class HookCreateTopicProcessor extends HookBaseProcessor {
    private final OnCreateTopicListener listener;

    public interface OnCreateTopicListener {
        void onCreateGroupTopic(String str);

        void onCreateUserTopic();
    }

    public HookCreateTopicProcessor(OnCreateTopicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/createTopic";
    }

    protected void onHookExecute(Uri uri) {
        String groupId = uri.getQueryParameter("gid");
        if (TextUtils.isEmpty(groupId)) {
            Logger.m172d("gid parameter is empty, create user topic");
            notifyOnCreateUserTopic();
            return;
        }
        Logger.m173d("Create group topic with gid=%s", groupId);
        notifyOnCreateGroupTopic(groupId);
    }

    protected void notifyOnCreateGroupTopic(String groupId) {
        if (this.listener != null) {
            this.listener.onCreateGroupTopic(groupId);
        }
    }

    protected void notifyOnCreateUserTopic() {
        if (this.listener != null) {
            this.listener.onCreateUserTopic();
        }
    }
}
