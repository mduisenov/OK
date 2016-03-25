package ru.ok.android.bus;

import android.support.annotation.NonNull;
import android.util.SparseArray;

final class BusReflector {
    static final BusReflector INSTANCE;

    BusReflector() {
    }

    static {
        INSTANCE = new BusReflector();
    }

    public void register(@NonNull Bus bus, @NonNull Object target) {
        ReflectedClassInfo info = ReflectedClassInfo.obtain(target.getClass());
        ReflectedSubscriber proxy = new ReflectedSubscriber(target, info, null);
        SparseArray<ReflectedSubscribeMethodInfo> subs = info.subs;
        int subsLen = subs.size();
        for (int i = 0; i < subsLen; i++) {
            bus.subscribeProxy(subs.keyAt(i), proxy, target, ((ReflectedSubscribeMethodInfo) subs.valueAt(i)).on);
        }
    }

    public void unregister(@NonNull Bus bus, @NonNull Object target) {
        SparseArray<ReflectedSubscribeMethodInfo> subs = ReflectedClassInfo.obtain(target.getClass()).subs;
        for (int i = subs.size() - 1; i >= 0; i--) {
            bus.unsubscribe(subs.keyAt(i), target);
        }
    }
}
