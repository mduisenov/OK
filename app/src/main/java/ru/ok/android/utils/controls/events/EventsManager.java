package ru.ok.android.utils.controls.events;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.ui.activity.main.OdklActivity;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.model.events.DiscussionOdklEvent;
import ru.ok.model.events.OdnkEvent;
import ru.ok.model.events.OdnkEvent.EventType;

public final class EventsManager {
    private static EventsManager sInstance;
    private final CacheEvents cache;
    public final String currentUserId;
    private final Handler handler;
    private Set<OnEvents> objectNotified;
    private long timeOut;

    public interface OnEvents {
        void onGetNewEvents(ArrayList<OdnkEvent> arrayList);
    }

    /* renamed from: ru.ok.android.utils.controls.events.EventsManager.1 */
    class C14481 extends Handler {
        C14481(Looper x0) {
            super(x0);
        }

        public void handleMessage(Message m) {
            GlobalBus.send(2131624053, new BusEvent());
            EventsManager.this.handler.sendEmptyMessageDelayed(0, EventsManager.this.timeOut);
        }
    }

    /* renamed from: ru.ok.android.utils.controls.events.EventsManager.2 */
    class C14492 implements Runnable {
        C14492() {
        }

        public void run() {
            if (!EventsManager.this.objectNotified.isEmpty()) {
                EventsManager.this.handler.removeMessages(0);
                EventsManager.this.handler.sendEmptyMessage(0);
            }
        }
    }

    /* renamed from: ru.ok.android.utils.controls.events.EventsManager.3 */
    class C14503 implements Runnable {
        final /* synthetic */ ArrayList val$events;

        C14503(ArrayList arrayList) {
            this.val$events = arrayList;
        }

        public void run() {
            for (OnEvents event : EventsManager.this.objectNotified) {
                event.onGetNewEvents(this.val$events);
            }
        }
    }

    static {
        sInstance = null;
    }

    public static EventsManager getInstance() {
        if (!isInstanceForCurrentUser(sInstance)) {
            synchronized (EventsManager.class) {
                if (!isInstanceForCurrentUser(sInstance)) {
                    sInstance = new EventsManager(OdnoklassnikiApplication.getCurrentUser().uid, 120000);
                }
            }
        }
        return sInstance;
    }

    private static boolean isInstanceForCurrentUser(EventsManager instance) {
        return instance != null && TextUtils.equals(OdnoklassnikiApplication.getCurrentUser().uid, sInstance.currentUserId);
    }

    public OdnkEvent getDiscussionsLastEvent() {
        return this.cache.discussionsLastEvent;
    }

    public void setDiscussionsLastEvent(OdnkEvent discussionsLastEvent) {
        this.cache.discussionsLastEvent = discussionsLastEvent;
        saveCache();
    }

    private EventsManager(String currentUserId, long timeOut) {
        this.objectNotified = new HashSet();
        this.handler = new C14481(Looper.getMainLooper());
        this.currentUserId = currentUserId;
        this.timeOut = timeOut;
        this.cache = loadCache();
    }

    public void stopEventsObserved() {
        this.handler.removeMessages(0);
    }

    public synchronized void subscribe(OnEvents o) {
        this.objectNotified.add(o);
        o.onGetNewEvents(this.cache.getEvents());
    }

    public synchronized void unSubscribe(OnEvents o) {
        this.objectNotified.remove(o);
        if (this.objectNotified.isEmpty()) {
            stopEventsObserved();
        }
    }

    public void updateIfMoreOneMinuteAfterLastUpdate() {
        updateNow();
    }

    public void updateNow() {
        ThreadUtil.executeOnMain(new C14492());
    }

    public void clearDiscussionEvents() {
        ArrayList<OdnkEvent> eventsList = new ArrayList(1);
        eventsList.add(new DiscussionOdklEvent("noneUid", "0", "0", "0", 0, System.currentTimeMillis()));
        setEvents(eventsList);
    }

    public void updateConversationsCounter(int counter) {
        ArrayList<OdnkEvent> eventsList = new ArrayList(1);
        eventsList.add(new OdnkEvent("0", String.valueOf(counter), EventType.MESSAGES, 0, System.currentTimeMillis()));
        setEvents(eventsList);
    }

    public void clearActivityCounter() {
        setEvents(Collections.singletonList(new OdnkEvent("0", "0", EventType.ACTIVITIES, 0, System.currentTimeMillis())));
    }

    public void updateNotificationCounter(int counter) {
        ArrayList<OdnkEvent> eventsList = new ArrayList(2);
        eventsList.add(new OdnkEvent("0", String.valueOf(counter), EventType.EVENTS, 0, System.currentTimeMillis()));
        setEvents(eventsList);
    }

    public void updateNotificationTotal(int total) {
        ArrayList<OdnkEvent> eventsList = new ArrayList(1);
        eventsList.add(new OdnkEvent("0", String.valueOf(total), EventType.EVENTS_TOTAL, 0, System.currentTimeMillis()));
        setEvents(eventsList);
    }

    public void changePhotoCounter(int diff) {
        OdnkEvent event = this.cache.getEventByType(EventType.UPLOAD_PHOTO);
        if (event != null) {
            ArrayList<OdnkEvent> eventsList = new ArrayList(1);
            eventsList.add(new OdnkEvent("0", String.valueOf(event.getValueInt() + diff), EventType.UPLOAD_PHOTO, 0, System.currentTimeMillis()));
            setEvents(eventsList);
        }
    }

    public void sendActualValue() {
        ArrayList events = this.cache.getEvents();
        Bundle bundleOutput = new Bundle();
        bundleOutput.putParcelableArrayList("odkl_events_array_list", events);
        GlobalBus.send(2131624234, new BusEvent(null, bundleOutput));
        updateAppIconBadget(events);
        ThreadUtil.executeOnMain(new C14503(events));
    }

    private static void updateAppIconBadget(ArrayList<OdnkEvent> events) {
        int eventsCount = 0;
        Iterator i$ = events.iterator();
        while (i$.hasNext()) {
            OdnkEvent event = (OdnkEvent) i$.next();
            if (event.type == EventType.EVENTS || event.type == EventType.MESSAGES) {
                eventsCount += event.getValueInt();
            }
        }
        updateAppIconBadget(eventsCount);
    }

    public static void updateAppIconBadget(int eventsCount) {
        Intent intent = new Intent();
        String activityClass = OdklActivity.class.getName();
        String packageName = OdnoklassnikiApplication.getContext().getPackageName();
        intent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", activityClass);
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", true);
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", eventsCount > 0 ? String.valueOf(eventsCount) : null);
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", packageName);
        OdnoklassnikiApplication.getContext().sendBroadcast(intent);
        intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", eventsCount);
        intent.putExtra("badge_count_package_name", packageName);
        intent.putExtra("badge_count_class_name", activityClass);
        OdnoklassnikiApplication.getContext().sendBroadcast(intent);
    }

    public void setEvents(List<OdnkEvent> eventsList, boolean fromJs) {
        this.cache.updateEvents(eventsList, fromJs);
        saveCache();
        sendActualValue();
    }

    public void setEvents(List<OdnkEvent> eventsList) {
        setEvents(eventsList, false);
    }

    public void clear() {
        this.cache.clear();
        saveCache();
    }

    public void setEmptyValue(EventType type) {
        this.cache.setEmptyValue(type);
        saveCache();
    }

    public static ArrayList<OdnkEvent> getEventsFromBusEvent(BusEvent busEvent) {
        ArrayList<OdnkEvent> list = busEvent.bundleOutput.getParcelableArrayList("odkl_events_array_list");
        ArrayList<OdnkEvent> listRet = new ArrayList();
        Iterator i$ = list.iterator();
        while (i$.hasNext()) {
            OdnkEvent event = (OdnkEvent) i$.next();
            if (event != null) {
                listRet.add(event);
            }
        }
        return listRet;
    }

    private void saveCache() {
        File cacheFile = getEventsCacheFile();
        if (cacheFile != null) {
            try {
                Logger.m173d("Saving events cache to %s", cacheFile);
                this.cache.save(cacheFile);
            } catch (Throwable e) {
                Logger.m179e(e, "Failed to save events cache");
                try {
                    cacheFile.delete();
                } catch (Exception e2) {
                }
            }
        }
    }

    private CacheEvents loadCache() {
        File cacheFile = getEventsCacheFile();
        if (cacheFile != null) {
            try {
                Logger.m173d("Loading events cache from %s", cacheFile);
                CacheEvents restoredCache = CacheEvents.load(cacheFile);
                if (restoredCache != null) {
                    return restoredCache;
                }
            } catch (FileNotFoundException e) {
                Logger.m172d("Cache file is absent");
            } catch (Throwable e2) {
                Logger.m179e(e2, "Failed to load events cache");
                try {
                    cacheFile.delete();
                } catch (Exception e3) {
                }
            }
        }
        return new CacheEvents();
    }

    private File getEventsCacheFile() {
        if (TextUtils.isEmpty(this.currentUserId)) {
            return null;
        }
        return new File(OdnoklassnikiApplication.getContext().getFilesDir(), "events-cache-" + this.currentUserId);
    }
}
