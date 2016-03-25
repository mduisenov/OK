package ru.ok.android.ui.presents;

import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import ru.ok.android.bus.Bus;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.Subscriber;
import ru.ok.android.utils.Logger;

public class BusWithEventStates {
    private static final String BUNDLE_CACHE_KEY;
    private static final BusWithEventStates singleton;
    private final Bus bus;
    private final Set<String> loadingKeys;
    private final Subscriber<BusEvent> subscriber;
    private final SparseArray<Integer> subscribers;

    /* renamed from: ru.ok.android.ui.presents.BusWithEventStates.1 */
    class C11451 implements Subscriber<BusEvent> {
        C11451() {
        }

        public void consume(@AnyRes int kind, @NonNull BusEvent event) {
            String cacheKey = event.bundleInput.getString(BusWithEventStates.BUNDLE_CACHE_KEY);
            if (cacheKey != null) {
                Logger.m173d("request completed: %s", cacheKey);
                BusWithEventStates.this.loadingKeys.remove(cacheKey);
                BusWithEventStates.this.unsubscribeIfNeeded(kind);
                return;
            }
            Logger.m176e("no cache key in bundle");
        }
    }

    static {
        BUNDLE_CACHE_KEY = BusWithEventStates.class.getName() + ".BUNDLE_CACHE_KEY";
        singleton = new BusWithEventStates();
    }

    private BusWithEventStates() {
        this.subscribers = new SparseArray();
        this.loadingKeys = new HashSet();
        this.bus = GlobalBus.getInstance();
        this.subscriber = new C11451();
    }

    public static BusWithEventStates getInstance() {
        return singleton;
    }

    @NonNull
    public String send(@AnyRes int requestKind, @AnyRes int responseKind, @NonNull BusEvent event) {
        String cacheKey = UUID.randomUUID().toString();
        this.loadingKeys.add(cacheKey);
        Logger.m173d("new request: %s kind: %d", cacheKey, Integer.valueOf(responseKind));
        subscribeIfNeeded(responseKind);
        event.bundleInput.putString(BUNDLE_CACHE_KEY, cacheKey);
        GlobalBus.send(requestKind, event);
        return cacheKey;
    }

    public boolean isProcessing(@Nullable String cacheKey) {
        if (cacheKey == null) {
            return false;
        }
        return this.loadingKeys.contains(cacheKey);
    }

    public boolean isResultForKey(@NonNull BusEvent event, @Nullable String cacheKey) {
        if (cacheKey == null || event.bundleInput == null) {
            return false;
        }
        return TextUtils.equals(event.bundleInput.getString(BUNDLE_CACHE_KEY), cacheKey);
    }

    private void subscribeIfNeeded(@AnyRes int kind) {
        int useCount = ((Integer) this.subscribers.get(kind, Integer.valueOf(0))).intValue() + 1;
        Logger.m173d("subscribers count: %d kind: %d", Integer.valueOf(useCount), Integer.valueOf(kind));
        if (useCount == 1) {
            Logger.m172d("subscribe");
            this.bus.subscribe(kind, this.subscriber, 2131623946);
        }
        this.subscribers.put(kind, Integer.valueOf(useCount));
    }

    private void unsubscribeIfNeeded(@AnyRes int kind) {
        int useCount = ((Integer) this.subscribers.get(kind, Integer.valueOf(1))).intValue() - 1;
        Logger.m173d("subscribers count: %d kind %d", Integer.valueOf(useCount), Integer.valueOf(kind));
        if (useCount == 0) {
            Logger.m172d("unsubscribe");
            this.bus.unsubscribe(kind, this.subscriber);
            this.subscribers.remove(kind);
            return;
        }
        this.subscribers.put(kind, Integer.valueOf(useCount));
    }
}
