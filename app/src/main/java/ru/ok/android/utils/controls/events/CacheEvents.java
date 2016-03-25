package ru.ok.android.utils.controls.events;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import ru.ok.android.fragments.web.AppParamsManagerImpl;
import ru.ok.android.fragments.web.client.interceptor.appparams.AppParamsInterceptor.WebAppParam;
import ru.ok.android.utils.IOUtils;
import ru.ok.model.events.DiscussionOdklEvent;
import ru.ok.model.events.EventsGsonUtils;
import ru.ok.model.events.OdnkEvent;
import ru.ok.model.events.OdnkEvent.EventType;

public final class CacheEvents {
    protected OdnkEvent discussionsLastEvent;
    private volatile Map<EventType, OdnkEvent> eventMap;

    /* renamed from: ru.ok.android.utils.controls.events.CacheEvents.1 */
    class C14461 extends TypeToken<CacheEvents> {
        C14461() {
        }
    }

    /* renamed from: ru.ok.android.utils.controls.events.CacheEvents.2 */
    static class C14472 extends TypeToken<CacheEvents> {
        C14472() {
        }
    }

    public CacheEvents() {
        this.eventMap = new ConcurrentHashMap();
    }

    public void clear() {
        this.eventMap.clear();
        this.discussionsLastEvent = null;
    }

    public void setEmptyValue(EventType type) {
        if (this.eventMap.containsKey(type)) {
            OdnkEvent event = (OdnkEvent) this.eventMap.get(type);
            this.eventMap.remove(type);
            this.eventMap.put(type, new OdnkEvent(event.uid, "0", type, event.lastId, System.currentTimeMillis()));
        }
    }

    public void updateEvents(List<OdnkEvent> eventsList, boolean fromJs) {
        boolean isChanges = false;
        for (OdnkEvent event : eventsList) {
            if (this.eventMap.containsKey(event.type)) {
                OdnkEvent cacheEvent = (OdnkEvent) this.eventMap.get(event.type);
                if (cacheEvent.requestTime <= event.requestTime) {
                    long lastId = event.lastId > 0 ? event.lastId : cacheEvent.lastId;
                    if (event instanceof DiscussionOdklEvent) {
                        this.eventMap.put(event.type, new DiscussionOdklEvent(cacheEvent.uid, event.value, ((DiscussionOdklEvent) event).valueLike, ((DiscussionOdklEvent) event).valueReplay, lastId, event.requestTime));
                    } else {
                        this.eventMap.put(event.type, new OdnkEvent(cacheEvent.uid, event.value, cacheEvent.type, lastId, event.requestTime));
                    }
                    if (cacheEvent.isValueInt() && event.isValueInt() && cacheEvent.getValueInt() != event.getValueInt()) {
                        isChanges = true;
                    }
                    if (event.type == EventType.DISCUSSIONS) {
                        DiscussionOdklEvent ev = (DiscussionOdklEvent) event;
                        DiscussionOdklEvent cacheEv = (DiscussionOdklEvent) cacheEvent;
                        if (ev.getIntValueLike() != cacheEv.getIntValueLike() || cacheEv.getIntValueReply() != ev.getIntValueReply()) {
                            isChanges = true;
                        }
                    }
                }
            } else {
                this.eventMap.put(event.type, event);
                isChanges = true;
            }
        }
        if (!isChanges) {
            return;
        }
        if (fromJs) {
            EventsManager.getInstance().updateNow();
        } else {
            AppParamsManagerImpl.getInstance().pushAppParam(WebAppParam.ALL);
        }
    }

    public ArrayList<OdnkEvent> getEvents() {
        ArrayList<OdnkEvent> events = new ArrayList();
        for (EventType eventType : this.eventMap.keySet()) {
            events.add(this.eventMap.get(eventType));
        }
        return events;
    }

    public OdnkEvent getEventByType(EventType type) {
        return (OdnkEvent) this.eventMap.get(type);
    }

    public void save(File file) throws Exception {
        Throwable th;
        Closeable out = null;
        try {
            Closeable out2 = new JsonWriter(new FileWriter(file));
            try {
                out2.setIndent("  ");
                EventsGsonUtils.gson.toJson((Object) this, new C14461().getType(), (JsonWriter) out2);
                IOUtils.closeSilently(out2);
            } catch (Throwable th2) {
                th = th2;
                out = out2;
                IOUtils.closeSilently(out);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            IOUtils.closeSilently(out);
            throw th;
        }
    }

    public static CacheEvents load(File file) throws Exception {
        Throwable th;
        Closeable in = null;
        try {
            Closeable in2 = new JsonReader(new FileReader(file));
            try {
                CacheEvents cacheEvents = (CacheEvents) EventsGsonUtils.gson.fromJson((JsonReader) in2, new C14472().getType());
                IOUtils.closeSilently(in2);
                return cacheEvents;
            } catch (Throwable th2) {
                th = th2;
                in = in2;
                IOUtils.closeSilently(in);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            IOUtils.closeSilently(in);
            throw th;
        }
    }
}
