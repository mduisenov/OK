package ru.ok.android.http.impl.cookie;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import ru.ok.android.http.cookie.CommonCookieAttributeHandler;
import ru.ok.android.http.cookie.CookieAttributeHandler;
import ru.ok.android.http.cookie.CookieSpec;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.Asserts;

public abstract class AbstractCookieSpec implements CookieSpec {
    private final Map<String, CookieAttributeHandler> attribHandlerMap;

    public AbstractCookieSpec() {
        this.attribHandlerMap = new ConcurrentHashMap(10);
    }

    protected AbstractCookieSpec(HashMap<String, CookieAttributeHandler> map) {
        Asserts.notNull(map, "Attribute handler map");
        this.attribHandlerMap = new ConcurrentHashMap(map);
    }

    protected AbstractCookieSpec(CommonCookieAttributeHandler... handlers) {
        this.attribHandlerMap = new ConcurrentHashMap(handlers.length);
        for (CommonCookieAttributeHandler handler : handlers) {
            this.attribHandlerMap.put(handler.getAttributeName(), handler);
        }
    }

    @Deprecated
    public void registerAttribHandler(String name, CookieAttributeHandler handler) {
        Args.notNull(name, "Attribute name");
        Args.notNull(handler, "Attribute handler");
        this.attribHandlerMap.put(name, handler);
    }

    protected CookieAttributeHandler findAttribHandler(String name) {
        return (CookieAttributeHandler) this.attribHandlerMap.get(name);
    }

    protected CookieAttributeHandler getAttribHandler(String name) {
        CookieAttributeHandler handler = findAttribHandler(name);
        Asserts.check(handler != null, "Handler not registered for " + name + " attribute");
        return handler;
    }

    protected Collection<CookieAttributeHandler> getAttribHandlers() {
        return this.attribHandlerMap.values();
    }
}
