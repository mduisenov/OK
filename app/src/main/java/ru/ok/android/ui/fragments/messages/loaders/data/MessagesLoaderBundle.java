package ru.ok.android.ui.fragments.messages.loaders.data;

import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.model.messages.MessageBase;

public final class MessagesLoaderBundle<M extends MessageBase, G> {
    public final MessagesBundle<M, G> bundle;
    public final ErrorType errorType;
    public final boolean hasNewData;
    public final boolean hasUnreadData;
    public boolean processed;
    public final ChangeReason reason;
    public final boolean skipAnimation;

    public enum ChangeReason {
        FIRST,
        PREVIOUS,
        NEW,
        NEXT,
        ADDED,
        UPDATED,
        SPAM
    }

    public MessagesLoaderBundle(MessagesBundle<M, G> bundle, ChangeReason reason, boolean hasNewData, boolean hasUnreadData, boolean skipAnimation) {
        this.bundle = bundle;
        this.reason = reason;
        this.errorType = null;
        this.hasNewData = hasNewData;
        this.hasUnreadData = hasUnreadData;
        this.skipAnimation = skipAnimation;
    }

    public MessagesLoaderBundle(MessagesBundle<M, G> bundle, ChangeReason reason, ErrorType errorType) {
        this.bundle = bundle;
        this.reason = reason;
        this.errorType = errorType;
        this.hasUnreadData = false;
        this.hasNewData = false;
        this.skipAnimation = true;
    }
}
