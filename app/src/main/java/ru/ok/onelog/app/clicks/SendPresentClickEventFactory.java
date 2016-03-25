package ru.ok.onelog.app.clicks;

import java.io.Serializable;

public final class SendPresentClickEventFactory {
    public static <I extends SendPresentClickEvent & Serializable> I get(SendPresentClickType clickType) {
        return new SendPresentClickEventImpl(clickType);
    }
}
