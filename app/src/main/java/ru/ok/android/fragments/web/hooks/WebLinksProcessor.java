package ru.ok.android.fragments.web.hooks;

import android.app.Activity;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.WebHttpLoader;
import ru.ok.android.app.WebHttpLoader.LoadUrlTaskCommon;
import ru.ok.android.app.WebHttpLoader.RequestType;
import ru.ok.android.fragments.web.client.WebClientUtils;
import ru.ok.android.fragments.web.client.interceptor.UrlInterceptor;
import ru.ok.android.fragments.web.client.interceptor.hooks.AppHooksBridge;
import ru.ok.android.fragments.web.client.interceptor.hooks.BaseAppHooksInterceptor;
import ru.ok.android.fragments.web.client.interceptor.shortlinks.DefaultShortLinksInterceptor;
import ru.ok.android.fragments.web.client.interceptor.shortlinks.ShortLinksBridge;
import ru.ok.android.fragments.web.hooks.HookSessionFailedProcessor.OnSessionFailedListener;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.android.utils.controls.WebLoginHandler;
import ru.ok.android.utils.controls.WebLoginHandler.WebLoginListener;

public final class WebLinksProcessor {
    private final Activity activity;
    private final boolean fromLeftMenu;
    private final List<UrlInterceptor> interceptors;
    private final boolean processExternalUrls;

    /* renamed from: ru.ok.android.fragments.web.hooks.WebLinksProcessor.1 */
    class C03381 implements OnSessionFailedListener {
        final /* synthetic */ ManageCallback val$callback;

        C03381(ManageCallback manageCallback) {
            this.val$callback = manageCallback;
        }

        public void onSessionFailed(String goToUrl) {
            Logger.m173d("Session failed for url=%s", goToUrl);
            WebLinksProcessor.this.processWebSessionFailure(goToUrl, this.val$callback);
        }
    }

    /* renamed from: ru.ok.android.fragments.web.hooks.WebLinksProcessor.2 */
    class C03392 implements WebLoginListener {
        final /* synthetic */ ManageCallback val$callback;

        C03392(ManageCallback manageCallback) {
            this.val$callback = manageCallback;
        }

        public void onLoginSuccessful(String goToUrl) {
            Logger.m173d("Following redirect after successful login: %s", goToUrl);
            WebLinksProcessor.this.processUrlInternal(goToUrl, true, false, this.val$callback);
        }

        public void onLoginFailed(String goToUrl, String message, int type, int errorCode) {
            Logger.m177e("Failed to login when processing url=%s: %s, errorCode=%d, type=%d", goToUrl, message, Integer.valueOf(type), Integer.valueOf(errorCode));
        }
    }

    public interface ManageCallback {
        void onContinueProcess();

        void onCreateTask(ValidateTask validateTask);

        void onFinishProcess();
    }

    public class ValidateTask extends LoadUrlTaskCommon {
        private final ManageCallback callback;
        private volatile boolean cancel;

        public ValidateTask(String url, ManageCallback callback) {
            super(url, RequestType.HEAD);
            this.cancel = false;
            this.callback = callback;
            if (this.callback != null) {
                this.callback.onCreateTask(this);
            }
        }

        public boolean isCancel() {
            return this.cancel;
        }

        public void cancel() {
            Logger.m172d("");
            this.cancel = true;
        }

        public void onFailed(int errorCode) {
            Logger.m173d("errorCode=%d", Integer.valueOf(errorCode));
            if (!isCancel()) {
                WebLinksProcessor.this.openNoManageUrl(this.url);
            }
            if (this.callback != null) {
                Logger.m172d("callback.onFinishProcess()");
                this.callback.onFinishProcess();
            }
        }

        public void onRedirect(String newUrl) {
            Logger.m173d("newUrl=%s", newUrl);
            if (!isCancel()) {
                WebLinksProcessor.this.processUrlInternal(newUrl, false, false, this.callback);
            } else if (this.callback != null) {
                Logger.m172d("callback.onFinishProcess()");
                this.callback.onFinishProcess();
            }
        }

        public void onLoadedContent(String url) {
            Logger.m172d("url=%s");
            if (!isCancel()) {
                WebLinksProcessor.this.openNoManageUrl(url);
            }
            if (this.callback != null) {
                Logger.m172d("callback.onFinishProcess()");
                this.callback.onFinishProcess();
            }
        }
    }

    public WebLinksProcessor(Activity activity, boolean fromLeftMenu) {
        this(activity, fromLeftMenu, false);
    }

    public WebLinksProcessor(Activity activity, boolean fromLeftMenu, boolean processExternalUrls) {
        this.interceptors = new ArrayList();
        this.activity = activity;
        this.interceptors.add(new BaseAppHooksInterceptor(new AppHooksBridge(activity)));
        this.interceptors.add(new DefaultShortLinksInterceptor(new ShortLinksBridge(activity)));
        this.fromLeftMenu = fromLeftMenu;
        this.processExternalUrls = processExternalUrls;
    }

    public void processUrl(String inputUrl) {
        processUrl(inputUrl, null);
    }

    public void processUrl(String inputUrl, ManageCallback callback) {
        Logger.m173d("inputUrl=%s callback=%s", inputUrl, callback);
        processUrlInternal(inputUrl, true, false, callback);
    }

    public void processUrlWithoutGoTo(String inputUrl) {
        processUrlWithoutGoTo(inputUrl, null);
    }

    private void processUrlWithoutGoTo(String inputUrl, ManageCallback callback) {
        processUrlInternal(inputUrl, false, false, callback);
    }

    private boolean processByInterceptors(String url, ManageCallback callback) {
        Logger.m173d("url=%s callback=%s", url, callback);
        for (UrlInterceptor interceptor : this.interceptors) {
            if (interceptor.handleUrl(url)) {
                Logger.m173d("handled by interceptor: %s", (UrlInterceptor) i$.next());
                if (callback == null) {
                    return true;
                }
                Logger.m172d("callback.onFinishProcess()");
                callback.onFinishProcess();
                return true;
            }
        }
        if (!interceptSessionFailed(url, callback)) {
            Logger.m173d("not handled by interceptors: %s", url);
            return false;
        } else if (callback == null) {
            return true;
        } else {
            callback.onContinueProcess();
            return true;
        }
    }

    private boolean interceptSessionFailed(String url, ManageCallback callback) {
        if (!new HookSessionFailedNewProcessor(new C03381(callback)).handleWebHookEvent(url)) {
            return false;
        }
        Logger.m173d("intercepted session failed apphook: %s", url);
        return true;
    }

    private void processUrlInternal(String inputUrl, boolean useGoTo, boolean ignoreInterceptors, ManageCallback callback) {
        Logger.m173d("inputUrl=%s useGoTo=%s ignoreInterceptors=%s callback=%s", inputUrl, Boolean.valueOf(useGoTo), Boolean.valueOf(ignoreInterceptors), callback);
        Uri uri = Uri.parse(inputUrl);
        Logger.m173d("processExternalUrls=%s useGoTo=%s isOkHost=%s", Boolean.valueOf(this.processExternalUrls), Boolean.valueOf(useGoTo), Boolean.valueOf(WebClientUtils.isOkHost(uri)));
        if (this.processExternalUrls || isOkHost || useGoTo) {
            String url;
            Logger.m172d("start processing...");
            if (WebClientUtils.isShortLink(uri)) {
                url = WebClientUtils.createValidShortLink(uri).toString();
            } else {
                url = inputUrl;
            }
            Logger.m173d("isShortLink=%s url=%s", Boolean.valueOf(isShortLink), url);
            if (ignoreInterceptors || !processByInterceptors(url, callback)) {
                if (callback != null) {
                    Logger.m172d("callback.onContinueProcess()");
                    callback.onContinueProcess();
                }
                if (useGoTo) {
                    url = WebUrlCreator.getGoToUrl(url, null);
                    Logger.m173d("goTo url=%s", url);
                }
                WebHttpLoader.from(this.activity).postLoadUrl(new ValidateTask(url, callback));
                return;
            }
            return;
        }
        openNoManageUrl(inputUrl);
        if (callback != null) {
            Logger.m172d("callback.onFinishProcess()");
            callback.onFinishProcess();
        }
    }

    private void openNoManageUrl(String url) {
        Logger.m173d("url=%s", url);
        NavigationHelper.showExternalUrlPage(this.activity, url, this.fromLeftMenu, WebClientUtils.isOkHost(Uri.parse(url)));
    }

    private void processWebSessionFailure(String goToUrl, ManageCallback callback) {
        Logger.m173d("goToUrl=%s callback=%s", goToUrl, callback);
        new WebLoginHandler().reCreateSession(new C03392(callback), goToUrl);
    }
}
