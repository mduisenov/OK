package ru.ok.android.fragments.web.client.interceptor.hooks;

import ru.ok.android.fragments.web.hooks.HookSessionFailedNewProcessor;

public final class DefaultAppHooksInterceptor extends BaseAppHooksInterceptor {
    public DefaultAppHooksInterceptor(AppHooksBridge appHooksBridge) {
        super(appHooksBridge);
    }

    protected void addAppHooks(AppHooksInterceptor appHooks, AppHooksBridge bridge) {
        super.addAppHooks(appHooks, bridge);
        appHooks.addHookProcessor(new HookSessionFailedNewProcessor(bridge));
    }
}
