package ru.ok.android.bus;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import java.util.Locale;
import java.util.concurrent.Executor;

public final class Bus {
    private final SparseArray<Executor> execs;
    private final SparseArray<SubscriberRecord> recs;
    @Nullable
    private Resources res;
    private String resPackagePrefix;

    static final class Delivery implements Runnable {
        @NonNull
        private final Object ev;
        @AnyRes
        private final int kind;
        @Nullable
        Delivery next;
        @AnyRes
        final int on;
        @NonNull
        private final Subscriber sub;

        public Delivery(@NonNull Subscriber sub, @AnyRes int on, @AnyRes int kind, @NonNull Object ev) {
            this.sub = sub;
            this.on = on;
            this.kind = kind;
            this.ev = ev;
        }

        public void run() {
            this.sub.consume(this.kind, this.ev);
        }
    }

    static final class SubscriberRecord {
        @Nullable
        volatile SubscriberRecord next;
        @AnyRes
        final int on;
        @NonNull
        final Subscriber sub;
        @NonNull
        final Object tag;

        public SubscriberRecord(@NonNull Subscriber sub, @NonNull Object tag, @AnyRes int on) {
            this.sub = sub;
            this.tag = tag;
            this.on = on;
        }
    }

    public Bus() {
        this.execs = new SparseArray();
        this.recs = new SparseArray();
    }

    public Bus(int initialExecutorsSize, int initialSubscribersSize) {
        this.execs = new SparseArray(initialExecutorsSize);
        this.recs = new SparseArray(initialSubscribersSize);
    }

    public void register(@NonNull Object target) {
        BusReflector.INSTANCE.register(this, target);
    }

    public void unregister(@NonNull Object target) {
        BusReflector.INSTANCE.unregister(this, target);
    }

    public void subscribe(@AnyRes int kind, @NonNull Subscriber sub, @AnyRes int on) {
        subscribeProxy(kind, sub, sub, on);
    }

    public void subscribeProxy(@AnyRes int kind, @NonNull Subscriber proxy, @NonNull Object target, @AnyRes int on) {
        SubscriberRecord rec = new SubscriberRecord(proxy, target, on);
        synchronized (this.recs) {
            int idx = this.recs.indexOfKey(kind);
            if (idx >= 0) {
                rec.next = (SubscriberRecord) this.recs.valueAt(idx);
                this.recs.setValueAt(idx, rec);
            } else {
                this.recs.put(kind, rec);
            }
        }
    }

    public boolean unsubscribe(@AnyRes int kind, @NonNull Object target) {
        boolean z = false;
        synchronized (this.recs) {
            int idx = this.recs.indexOfKey(kind);
            if (idx < 0) {
            } else {
                SubscriberRecord prev = null;
                SubscriberRecord cur = (SubscriberRecord) this.recs.valueAt(idx);
                while (cur != null) {
                    SubscriberRecord next = cur.next;
                    if (cur.tag == target) {
                        if (prev == null) {
                            this.recs.setValueAt(idx, next);
                        } else {
                            prev.next = next;
                        }
                        z = true;
                    } else {
                        prev = cur;
                        cur = next;
                    }
                }
            }
        }
        return z;
    }

    public void send(@AnyRes int kind, @NonNull Object event) {
        Delivery deli;
        Delivery firstDeli = null;
        synchronized (this.recs) {
            Delivery prevDeli = null;
            for (SubscriberRecord rec = (SubscriberRecord) this.recs.get(kind); rec != null; rec = rec.next) {
                deli = new Delivery(rec.sub, rec.on, kind, event);
                if (firstDeli == null) {
                    firstDeli = deli;
                } else {
                    prevDeli.next = deli;
                }
                prevDeli = deli;
            }
        }
        deli = firstDeli;
        while (deli != null) {
            int on = deli.on;
            Delivery nextDeli = deli.next;
            deli.next = null;
            if (on == 0) {
                deli.run();
            } else {
                post(deli, on);
            }
            deli = nextDeli;
        }
    }

    public void registerExecutor(@AnyRes int on, @NonNull Executor exec) {
        if (on == 0) {
            throw new IllegalArgumentException("Cannot register executor with id 0, id reserved for direct calls");
        }
        synchronized (this.execs) {
            if (this.execs.indexOfKey(on) >= 0) {
                throw new IllegalStateException("Executor already registered for id " + stringify(on));
            }
            this.execs.put(on, exec);
        }
    }

    public void post(@NonNull Runnable command, @AnyRes int on) {
        Executor exec;
        synchronized (this.execs) {
            int idx = this.execs.indexOfKey(on);
            if (idx < 0) {
                throw new IllegalStateException("Executor not registered for id " + stringify(on));
            }
            exec = (Executor) this.execs.valueAt(idx);
        }
        exec.execute(command);
    }

    private String stringify(@AnyRes int resid) {
        if (this.res == null) {
            return stringifyDefault(resid);
        }
        try {
            return "@" + this.res.getResourceName(resid).replace("id/", "").replace(this.resPackagePrefix, "");
        } catch (NotFoundException e) {
            return stringifyDefault(resid);
        }
    }

    private String stringifyDefault(@AnyRes int resid) {
        if (resid == 0) {
            return "0";
        }
        return String.format(Locale.US, "0x%08x", new Object[]{Integer.valueOf(resid)});
    }
}
