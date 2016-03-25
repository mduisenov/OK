package ru.ok.android.bus;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.bus.exec.HandlerExecutor;
import ru.ok.android.utils.NamedThreadFactory;

public final class GlobalBus {
    private static final int BG_CORE_POOL_SIZE;
    private static final int BG_MAX_POOL_SIZE;
    private static final int CPU_COUNT;
    private static final AtomicReference<Bus> INSTANCE;

    static {
        INSTANCE = new AtomicReference();
        CPU_COUNT = Math.min(4, Runtime.getRuntime().availableProcessors());
        BG_MAX_POOL_SIZE = (CPU_COUNT * 2) + 1;
        BG_CORE_POOL_SIZE = CPU_COUNT + 1;
    }

    public static void register(@NonNull Object client) {
        getInstance().register(client);
    }

    public static void unregister(@NonNull Object client) {
        getInstance().unregister(client);
    }

    public static void send(@AnyRes int kind, @NonNull BusEvent event) {
        getInstance().send(kind, event);
    }

    public static void sendMessage(Message msg) {
        send(msg.what, messageToEvent(msg));
    }

    public static void post(@NonNull Runnable command, @AnyRes int on) {
        getInstance().post(command, on);
    }

    @NonNull
    public static Bus getInstance() {
        Bus cachedInstance = (Bus) INSTANCE.get();
        if (cachedInstance != null) {
            return cachedInstance;
        }
        Bus instance = createInstance();
        if (INSTANCE.compareAndSet(null, instance)) {
            return instance;
        }
        return (Bus) INSTANCE.get();
    }

    private static Bus createInstance() {
        Bus bus = new Bus(2, 200);
        bus.registerExecutor(2131623946, HandlerExecutor.main());
        bus.registerExecutor(2131623944, createBackgroundExecutor());
        bus.registerExecutor(2131623945, createDatabaseExecutor());
        return bus;
    }

    private static Executor createBackgroundExecutor() {
        return new ThreadPoolExecutor(BG_CORE_POOL_SIZE, BG_MAX_POOL_SIZE, 30, TimeUnit.SECONDS, new LinkedBlockingQueue(), new NamedThreadFactory("Bus"));
    }

    private static Executor createDatabaseExecutor() {
        return Executors.newSingleThreadExecutor(new NamedThreadFactory("Database"));
    }

    public static Message eventToMessage(BusEvent event) {
        return (Message) event.bundleInput.getParcelable("msg_key");
    }

    public static BusEvent messageToEvent(Message message) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("msg_key", message);
        return new BusEvent(bundle);
    }
}
