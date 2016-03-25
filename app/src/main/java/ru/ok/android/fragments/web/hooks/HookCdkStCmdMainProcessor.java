package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.HookRedirectProcessor.OnRedirectUrlLoadingListener;
import ru.ok.android.fragments.web.hooks.HookSessionFailedProcessor.OnSessionFailedListener;
import ru.ok.android.utils.Logger;

public class HookCdkStCmdMainProcessor extends HookBaseProcessor {
    private HookRedirectProcessor hookRedirectObserver;
    private HookSessionFailedNewProcessor hookSessionFailedNewProcessor;
    private HookSessionFailedProcessor hookSessionFailedObserver;

    public HookCdkStCmdMainProcessor(OnSessionFailedListener onSessionFailedListener, OnRedirectUrlLoadingListener onRedirectUrlLoadingListener) {
        this.hookSessionFailedObserver = new HookSessionFailedProcessor(onSessionFailedListener);
        this.hookRedirectObserver = new HookRedirectProcessor(onRedirectUrlLoadingListener);
        this.hookSessionFailedNewProcessor = new HookSessionFailedNewProcessor(onSessionFailedListener);
    }

    protected String getHookName() {
        return "/cdk/st.cmd/main";
    }

    protected void onHookExecute(Uri uri) {
        Logger.m173d("uri=%s", uri);
        if (uri.getPath().contains(this.hookSessionFailedObserver.getHookName())) {
            this.hookSessionFailedObserver.onHookExecute(uri);
        } else if (uri.getPath().contains(this.hookRedirectObserver.getHookName())) {
            this.hookRedirectObserver.onHookExecute(uri);
        } else if (uri.getPath().contains(this.hookSessionFailedNewProcessor.getHookName())) {
            this.hookSessionFailedNewProcessor.onHookExecute(uri);
        } else {
            this.hookSessionFailedObserver.onHookExecute(uri);
        }
    }
}
