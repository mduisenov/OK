package ru.ok.android.http.nio.pool;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import ru.ok.android.http.concurrent.BasicFuture;
import ru.ok.android.http.concurrent.FutureCallback;
import ru.ok.android.http.nio.reactor.ConnectingIOReactor;
import ru.ok.android.http.nio.reactor.IOReactorStatus;
import ru.ok.android.http.nio.reactor.SessionRequest;
import ru.ok.android.http.nio.reactor.SessionRequestCallback;
import ru.ok.android.http.pool.ConnPoolControl;
import ru.ok.android.http.pool.PoolEntry;
import ru.ok.android.http.pool.PoolEntryCallback;
import ru.ok.android.http.pool.PoolStats;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.Asserts;

public abstract class AbstractNIOConnPool<T, C, E extends PoolEntry<T, C>> implements ConnPoolControl<T> {
    private final SocketAddressResolver<T> addressResolver;
    private final LinkedList<E> available;
    private final ConcurrentLinkedQueue<LeaseRequest<T, C, E>> completedRequests;
    private final NIOConnFactory<T, C> connFactory;
    private volatile int defaultMaxPerRoute;
    private final ConnectingIOReactor ioreactor;
    private final AtomicBoolean isShutDown;
    private final Set<E> leased;
    private final LinkedList<LeaseRequest<T, C, E>> leasingRequests;
    private final Lock lock;
    private final Map<T, Integer> maxPerRoute;
    private volatile int maxTotal;
    private final Set<SessionRequest> pending;
    private final Map<T, RouteSpecificPool<T, C, E>> routeToPool;
    private final SessionRequestCallback sessionRequestCallback;

    protected abstract E createEntry(T t, C c);

    @Deprecated
    public AbstractNIOConnPool(ConnectingIOReactor ioreactor, NIOConnFactory<T, C> connFactory, int defaultMaxPerRoute, int maxTotal) {
        Args.notNull(ioreactor, "I/O reactor");
        Args.notNull(connFactory, "Connection factory");
        Args.positive(defaultMaxPerRoute, "Max per route value");
        Args.positive(maxTotal, "Max total value");
        this.ioreactor = ioreactor;
        this.connFactory = connFactory;
        this.addressResolver = new 1(this);
        this.sessionRequestCallback = new InternalSessionRequestCallback(this);
        this.routeToPool = new HashMap();
        this.leasingRequests = new LinkedList();
        this.pending = new HashSet();
        this.leased = new HashSet();
        this.available = new LinkedList();
        this.maxPerRoute = new HashMap();
        this.completedRequests = new ConcurrentLinkedQueue();
        this.lock = new ReentrantLock();
        this.isShutDown = new AtomicBoolean(false);
        this.defaultMaxPerRoute = defaultMaxPerRoute;
        this.maxTotal = maxTotal;
    }

    public AbstractNIOConnPool(ConnectingIOReactor ioreactor, NIOConnFactory<T, C> connFactory, SocketAddressResolver<T> addressResolver, int defaultMaxPerRoute, int maxTotal) {
        Args.notNull(ioreactor, "I/O reactor");
        Args.notNull(connFactory, "Connection factory");
        Args.notNull(addressResolver, "Address resolver");
        Args.positive(defaultMaxPerRoute, "Max per route value");
        Args.positive(maxTotal, "Max total value");
        this.ioreactor = ioreactor;
        this.connFactory = connFactory;
        this.addressResolver = addressResolver;
        this.sessionRequestCallback = new InternalSessionRequestCallback(this);
        this.routeToPool = new HashMap();
        this.leasingRequests = new LinkedList();
        this.pending = new HashSet();
        this.leased = new HashSet();
        this.available = new LinkedList();
        this.completedRequests = new ConcurrentLinkedQueue();
        this.maxPerRoute = new HashMap();
        this.lock = new ReentrantLock();
        this.isShutDown = new AtomicBoolean(false);
        this.defaultMaxPerRoute = defaultMaxPerRoute;
        this.maxTotal = maxTotal;
    }

    @Deprecated
    protected SocketAddress resolveRemoteAddress(T t) {
        return null;
    }

    @Deprecated
    protected SocketAddress resolveLocalAddress(T t) {
        return null;
    }

    protected void onLease(E e) {
    }

    protected void onRelease(E e) {
    }

    protected void onReuse(E e) {
    }

    public boolean isShutdown() {
        return this.isShutDown.get();
    }

    public void shutdown(long waitMs) throws IOException {
        if (this.isShutDown.compareAndSet(false, true)) {
            fireCallbacks();
            this.lock.lock();
            try {
                for (SessionRequest sessionRequest : this.pending) {
                    sessionRequest.cancel();
                }
                Iterator i$ = this.available.iterator();
                while (i$.hasNext()) {
                    ((PoolEntry) i$.next()).close();
                }
                for (PoolEntry entry : this.leased) {
                    entry.close();
                }
                for (RouteSpecificPool<T, C, E> pool : this.routeToPool.values()) {
                    pool.shutdown();
                }
                this.routeToPool.clear();
                this.leased.clear();
                this.pending.clear();
                this.available.clear();
                this.leasingRequests.clear();
                this.ioreactor.shutdown(waitMs);
            } finally {
                this.lock.unlock();
            }
        }
    }

    private RouteSpecificPool<T, C, E> getPool(T route) {
        RouteSpecificPool<T, C, E> pool = (RouteSpecificPool) this.routeToPool.get(route);
        if (pool != null) {
            return pool;
        }
        pool = new 2(this, route);
        this.routeToPool.put(route, pool);
        return pool;
    }

    public Future<E> lease(T route, Object state, long connectTimeout, TimeUnit tunit, FutureCallback<E> callback) {
        return lease(route, state, connectTimeout, connectTimeout, tunit, callback);
    }

    public Future<E> lease(T route, Object state, long connectTimeout, long leaseTimeout, TimeUnit tunit, FutureCallback<E> callback) {
        long timeout;
        Args.notNull(route, "Route");
        Args.notNull(tunit, "Time unit");
        Asserts.check(!this.isShutDown.get(), "Connection pool shut down");
        BasicFuture<E> future = new BasicFuture(callback);
        this.lock.lock();
        if (connectTimeout > 0) {
            try {
                timeout = tunit.toMillis(connectTimeout);
            } catch (Throwable th) {
                this.lock.unlock();
            }
        } else {
            timeout = 0;
        }
        LeaseRequest<T, C, E> request = new LeaseRequest(route, state, timeout, leaseTimeout, future);
        boolean completed = processPendingRequest(request);
        if (!(request.isDone() || completed)) {
            this.leasingRequests.add(request);
        }
        if (request.isDone()) {
            this.completedRequests.add(request);
        }
        this.lock.unlock();
        fireCallbacks();
        return future;
    }

    public Future<E> lease(T route, Object state, FutureCallback<E> callback) {
        return lease(route, state, -1, TimeUnit.MICROSECONDS, callback);
    }

    public Future<E> lease(T route, Object state) {
        return lease(route, state, -1, TimeUnit.MICROSECONDS, null);
    }

    public void release(E entry, boolean reusable) {
        if (entry != null && !this.isShutDown.get()) {
            this.lock.lock();
            try {
                if (this.leased.remove(entry)) {
                    getPool(entry.getRoute()).free(entry, reusable);
                    if (reusable) {
                        this.available.addFirst(entry);
                        onRelease(entry);
                    } else {
                        entry.close();
                    }
                    processNextPendingRequest();
                }
                this.lock.unlock();
                fireCallbacks();
            } catch (Throwable th) {
                this.lock.unlock();
            }
        }
    }

    private void processPendingRequests() {
        ListIterator<LeaseRequest<T, C, E>> it = this.leasingRequests.listIterator();
        while (it.hasNext()) {
            LeaseRequest<T, C, E> request = (LeaseRequest) it.next();
            boolean completed = processPendingRequest(request);
            if (request.isDone() || completed) {
                it.remove();
            }
            if (request.isDone()) {
                this.completedRequests.add(request);
            }
        }
    }

    private void processNextPendingRequest() {
        ListIterator<LeaseRequest<T, C, E>> it = this.leasingRequests.listIterator();
        while (it.hasNext()) {
            LeaseRequest<T, C, E> request = (LeaseRequest) it.next();
            boolean completed = processPendingRequest(request);
            if (request.isDone() || completed) {
                it.remove();
            }
            if (request.isDone()) {
                this.completedRequests.add(request);
                continue;
            }
            if (completed) {
                return;
            }
        }
    }

    private boolean processPendingRequest(LeaseRequest<T, C, E> request) {
        T route = request.getRoute();
        Object state = request.getState();
        if (System.currentTimeMillis() > request.getDeadline()) {
            request.failed(new TimeoutException());
            return false;
        }
        RouteSpecificPool<T, C, E> pool = getPool(route);
        while (true) {
            E entry = pool.getFree(state);
            if (entry == null) {
                break;
            }
            if (!entry.isClosed()) {
                if (!entry.isExpired(System.currentTimeMillis())) {
                    break;
                }
            }
            entry.close();
            this.available.remove(entry);
            pool.free(entry, false);
        }
        if (entry != null) {
            this.available.remove(entry);
            this.leased.add(entry);
            request.completed(entry);
            onReuse(entry);
            onLease(entry);
            return true;
        }
        int maxPerRoute = getMax(route);
        int excess = Math.max(0, (pool.getAllocatedCount() + 1) - maxPerRoute);
        if (excess > 0) {
            for (int i = 0; i < excess; i++) {
                E lastUsed = pool.getLastUsed();
                if (lastUsed == null) {
                    break;
                }
                lastUsed.close();
                this.available.remove(lastUsed);
                pool.remove(lastUsed);
            }
        }
        if (pool.getAllocatedCount() >= maxPerRoute) {
            return false;
        }
        int totalUsed = this.pending.size() + this.leased.size();
        int freeCapacity = Math.max(this.maxTotal - totalUsed, 0);
        if (freeCapacity == 0) {
            return false;
        }
        if (this.available.size() > freeCapacity - 1) {
            if (!this.available.isEmpty()) {
                PoolEntry lastUsed2 = (PoolEntry) this.available.removeLast();
                lastUsed2.close();
                getPool(lastUsed2.getRoute()).remove(lastUsed2);
            }
        }
        try {
            int timout;
            SocketAddress remoteAddress = this.addressResolver.resolveRemoteAddress(route);
            SocketAddress localAddress = this.addressResolver.resolveLocalAddress(route);
            SessionRequest sessionRequest = this.ioreactor.connect(remoteAddress, localAddress, route, this.sessionRequestCallback);
            if (request.getConnectTimeout() < 2147483647L) {
                timout = (int) request.getConnectTimeout();
            } else {
                timout = Integer.MAX_VALUE;
            }
            sessionRequest.setConnectTimeout(timout);
            this.pending.add(sessionRequest);
            pool.addPending(sessionRequest, request.getFuture());
            return true;
        } catch (IOException ex) {
            request.failed(ex);
            return false;
        }
    }

    private void fireCallbacks() {
        while (true) {
            LeaseRequest<T, C, E> request = (LeaseRequest) this.completedRequests.poll();
            if (request != null) {
                BasicFuture<E> future = request.getFuture();
                Exception ex = request.getException();
                E result = request.getResult();
                if (ex != null) {
                    future.failed(ex);
                } else if (result != null) {
                    future.completed(result);
                } else {
                    future.cancel();
                }
            } else {
                return;
            }
        }
    }

    public void validatePendingRequests() {
        this.lock.lock();
        try {
            long now = System.currentTimeMillis();
            ListIterator<LeaseRequest<T, C, E>> it = this.leasingRequests.listIterator();
            while (it.hasNext()) {
                LeaseRequest<T, C, E> request = (LeaseRequest) it.next();
                if (now > request.getDeadline()) {
                    it.remove();
                    request.failed(new TimeoutException());
                    this.completedRequests.add(request);
                }
            }
            fireCallbacks();
        } finally {
            this.lock.unlock();
        }
    }

    protected void requestCompleted(SessionRequest request) {
        RouteSpecificPool<T, C, E> pool;
        if (!this.isShutDown.get()) {
            T route = request.getAttachment();
            this.lock.lock();
            try {
                this.pending.remove(request);
                pool = getPool(route);
                E entry = pool.createEntry(request, this.connFactory.create(route, request.getSession()));
                this.leased.add(entry);
                pool.completed(request, entry);
                onLease(entry);
            } catch (IOException ex) {
                pool.failed(request, ex);
            } catch (Throwable th) {
                this.lock.unlock();
            }
            this.lock.unlock();
            fireCallbacks();
        }
    }

    protected void requestCancelled(SessionRequest request) {
        if (!this.isShutDown.get()) {
            T route = request.getAttachment();
            this.lock.lock();
            try {
                this.pending.remove(request);
                getPool(route).cancelled(request);
                if (this.ioreactor.getStatus().compareTo(IOReactorStatus.ACTIVE) <= 0) {
                    processNextPendingRequest();
                }
                this.lock.unlock();
                fireCallbacks();
            } catch (Throwable th) {
                this.lock.unlock();
            }
        }
    }

    protected void requestFailed(SessionRequest request) {
        if (!this.isShutDown.get()) {
            T route = request.getAttachment();
            this.lock.lock();
            try {
                this.pending.remove(request);
                getPool(route).failed(request, request.getException());
                processNextPendingRequest();
                fireCallbacks();
            } finally {
                this.lock.unlock();
            }
        }
    }

    protected void requestTimeout(SessionRequest request) {
        if (!this.isShutDown.get()) {
            T route = request.getAttachment();
            this.lock.lock();
            try {
                this.pending.remove(request);
                getPool(route).timeout(request);
                processNextPendingRequest();
                fireCallbacks();
            } finally {
                this.lock.unlock();
            }
        }
    }

    private int getMax(T route) {
        Integer v = (Integer) this.maxPerRoute.get(route);
        if (v != null) {
            return v.intValue();
        }
        return this.defaultMaxPerRoute;
    }

    public void setMaxTotal(int max) {
        Args.positive(max, "Max value");
        this.lock.lock();
        try {
            this.maxTotal = max;
        } finally {
            this.lock.unlock();
        }
    }

    public int getMaxTotal() {
        this.lock.lock();
        try {
            int i = this.maxTotal;
            return i;
        } finally {
            this.lock.unlock();
        }
    }

    public void setDefaultMaxPerRoute(int max) {
        Args.positive(max, "Max value");
        this.lock.lock();
        try {
            this.defaultMaxPerRoute = max;
        } finally {
            this.lock.unlock();
        }
    }

    public int getDefaultMaxPerRoute() {
        this.lock.lock();
        try {
            int i = this.defaultMaxPerRoute;
            return i;
        } finally {
            this.lock.unlock();
        }
    }

    public void setMaxPerRoute(T route, int max) {
        Args.notNull(route, "Route");
        Args.positive(max, "Max value");
        this.lock.lock();
        try {
            this.maxPerRoute.put(route, Integer.valueOf(max));
        } finally {
            this.lock.unlock();
        }
    }

    public int getMaxPerRoute(T route) {
        Args.notNull(route, "Route");
        this.lock.lock();
        try {
            int max = getMax(route);
            return max;
        } finally {
            this.lock.unlock();
        }
    }

    public PoolStats getTotalStats() {
        this.lock.lock();
        try {
            PoolStats poolStats = new PoolStats(this.leased.size(), this.pending.size(), this.available.size(), this.maxTotal);
            return poolStats;
        } finally {
            this.lock.unlock();
        }
    }

    public PoolStats getStats(T route) {
        Args.notNull(route, "Route");
        this.lock.lock();
        try {
            RouteSpecificPool<T, C, E> pool = getPool(route);
            PoolStats poolStats = new PoolStats(pool.getLeasedCount(), pool.getPendingCount(), pool.getAvailableCount(), getMax(route));
            return poolStats;
        } finally {
            this.lock.unlock();
        }
    }

    public Set<T> getRoutes() {
        this.lock.lock();
        try {
            Set<T> hashSet = new HashSet(this.routeToPool.keySet());
            return hashSet;
        } finally {
            this.lock.unlock();
        }
    }

    protected void enumAvailable(PoolEntryCallback<T, C> callback) {
        this.lock.lock();
        try {
            Iterator<E> it = this.available.iterator();
            while (it.hasNext()) {
                PoolEntry entry = (PoolEntry) it.next();
                callback.process(entry);
                if (entry.isClosed()) {
                    getPool(entry.getRoute()).remove(entry);
                    it.remove();
                }
            }
            processPendingRequests();
            purgePoolMap();
        } finally {
            this.lock.unlock();
        }
    }

    protected void enumLeased(PoolEntryCallback<T, C> callback) {
        this.lock.lock();
        try {
            for (PoolEntry entry : this.leased) {
                callback.process(entry);
            }
            processPendingRequests();
        } finally {
            this.lock.unlock();
        }
    }

    @Deprecated
    protected void enumEntries(Iterator<E> it, PoolEntryCallback<T, C> callback) {
        while (it.hasNext()) {
            callback.process((PoolEntry) it.next());
        }
        processPendingRequests();
    }

    private void purgePoolMap() {
        Iterator<Entry<T, RouteSpecificPool<T, C, E>>> it = this.routeToPool.entrySet().iterator();
        while (it.hasNext()) {
            if (((RouteSpecificPool) ((Entry) it.next()).getValue()).getAllocatedCount() == 0) {
                it.remove();
            }
        }
    }

    public void closeIdle(long idletime, TimeUnit tunit) {
        Args.notNull(tunit, "Time unit");
        long time = tunit.toMillis(idletime);
        if (time < 0) {
            time = 0;
        }
        enumAvailable(new 3(this, System.currentTimeMillis() - time));
    }

    public void closeExpired() {
        enumAvailable(new 4(this, System.currentTimeMillis()));
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[leased: ");
        buffer.append(this.leased);
        buffer.append("][available: ");
        buffer.append(this.available);
        buffer.append("][pending: ");
        buffer.append(this.pending);
        buffer.append("]");
        return buffer.toString();
    }
}
