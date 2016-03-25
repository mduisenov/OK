package ru.ok.onelog.app.clicks;

import java.io.Serializable;

public final class ReceivePresentClickEventFactory {
    public static <I extends ReceivePresentClickEvent & Serializable> I get(ReceivePresentClickType clickType, ReceivePresentScreenState screenState) {
        return new ReceivePresentClickEventImpl(clickType, screenState);
    }
}
