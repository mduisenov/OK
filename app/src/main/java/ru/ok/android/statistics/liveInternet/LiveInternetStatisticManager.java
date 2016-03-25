package ru.ok.android.statistics.liveInternet;

import android.content.Context;
import ru.ok.android.app.WebHttpLoader;
import ru.ok.android.app.WebHttpLoader.LoadUrlTaskCommon;
import ru.ok.android.app.WebHttpLoader.RequestType;
import ru.ok.android.utils.Logger;

public class LiveInternetStatisticManager {
    private static LiveInternetStatisticManager sInstance;

    private class ChainedLoadUrlTask extends LoadUrlTaskCommon {
        private final Context context;
        private final ChainedLoadUrlTask nextTask;

        public ChainedLoadUrlTask(Context context, String url, ChainedLoadUrlTask nextTask) {
            super(url, RequestType.GET, false);
            this.context = context.getApplicationContext();
            this.nextTask = nextTask;
        }

        public void onFailed(int errorCode) {
            Logger.m173d("error load url: %d", Integer.valueOf(errorCode));
        }

        public void onRedirect(String newUrl) {
            Logger.m173d("redirect load url: %s", newUrl);
            WebHttpLoader.from(this.context).postLoadUrl(new ChainedLoadUrlTask(this.context, newUrl, this.nextTask));
        }

        public void onLoadedContent(String url) {
            if (this.nextTask != null) {
                WebHttpLoader.from(this.context).postLoadUrl(this.nextTask);
            }
        }
    }

    static {
        sInstance = null;
    }

    public static LiveInternetStatisticManager getInstance() {
        if (sInstance == null) {
            synchronized (LiveInternetStatisticManager.class) {
                if (sInstance == null) {
                    sInstance = new LiveInternetStatisticManager();
                }
            }
        }
        return sInstance;
    }

    public void addEvent(Context context) {
        Logger.m172d("");
        WebHttpLoader.from(context).postLoadUrl(new ChainedLoadUrlTask(context, "http://counter.yadro.ru/hit?uhttp://m.odnoklassniki.ru/", new ChainedLoadUrlTask(context, "http://www.tns-counter.ru/V13a****odnoklassniki_ru/ru/UTF-8/tmsec=odnoklassniki_mobile/", null)));
    }
}
