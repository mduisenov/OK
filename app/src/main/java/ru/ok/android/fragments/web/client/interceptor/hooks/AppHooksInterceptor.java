package ru.ok.android.fragments.web.client.interceptor.hooks;

import java.util.ArrayList;
import java.util.List;
import ru.ok.android.fragments.web.client.interceptor.UrlInterceptor;

public final class AppHooksInterceptor implements UrlInterceptor {
    private final List<HookUrlProcessor> hookProcessors;

    public interface HookUrlProcessor {
        boolean handleWebHookEvent(String str);
    }

    public AppHooksInterceptor() {
        this.hookProcessors = new ArrayList();
    }

    public AppHooksInterceptor addHookProcessor(HookUrlProcessor... processors) {
        for (HookUrlProcessor processor : processors) {
            this.hookProcessors.add(processor);
        }
        return this;
    }

    public boolean handleUrl(String url) {
        boolean result = false;
        for (HookUrlProcessor observer : this.hookProcessors) {
            result |= observer.handleWebHookEvent(url);
        }
        return result;
    }
}
