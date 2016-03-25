package ru.ok.android.ui.stream.list.controller;

import android.app.Activity;
import ru.ok.android.fragments.web.hooks.WebLinksProcessor;
import ru.ok.android.ui.stream.list.StreamItemAdapter.StreamAdapterListener;

public abstract class AbsStreamLinksProcessorsViewController extends AbsStreamItemListenersViewController {
    private WebLinksProcessor externalWebLinksProcessor;
    private WebLinksProcessor webLinksProcessor;

    public AbsStreamLinksProcessorsViewController(Activity activity, StreamAdapterListener listener, String logContext) {
        super(activity, listener, logContext);
    }

    public WebLinksProcessor getExternalWebLinksProcessor() {
        if (this.externalWebLinksProcessor == null) {
            this.externalWebLinksProcessor = new WebLinksProcessor(getActivity(), false, true);
        }
        return this.externalWebLinksProcessor;
    }

    public WebLinksProcessor getWebLinksProcessor() {
        if (this.webLinksProcessor == null) {
            this.webLinksProcessor = new WebLinksProcessor(getActivity(), false);
        }
        return this.webLinksProcessor;
    }
}
