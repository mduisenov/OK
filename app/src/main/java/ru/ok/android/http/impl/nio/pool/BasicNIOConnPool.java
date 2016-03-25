package ru.ok.android.http.impl.nio.pool;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import ru.ok.android.http.HttpHost;
import ru.ok.android.http.concurrent.FutureCallback;
import ru.ok.android.http.config.ConnectionConfig;
import ru.ok.android.http.nio.NHttpClientConnection;
import ru.ok.android.http.nio.pool.AbstractNIOConnPool;
import ru.ok.android.http.nio.pool.NIOConnFactory;
import ru.ok.android.http.nio.reactor.ConnectingIOReactor;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;

public class BasicNIOConnPool extends AbstractNIOConnPool<HttpHost, NHttpClientConnection, BasicNIOPoolEntry> {
    private static final AtomicLong COUNTER;
    private final int connectTimeout;

    static {
        COUNTER = new AtomicLong();
    }

    @Deprecated
    public BasicNIOConnPool(ConnectingIOReactor ioreactor, NIOConnFactory<HttpHost, NHttpClientConnection> connFactory, HttpParams params) {
        super(ioreactor, connFactory, 2, 20);
        Args.notNull(params, "HTTP parameters");
        this.connectTimeout = params.getIntParameter("http.connection.timeout", 0);
    }

    @Deprecated
    public BasicNIOConnPool(ConnectingIOReactor ioreactor, HttpParams params) {
        this(ioreactor, new BasicNIOConnFactory(params), params);
    }

    public BasicNIOConnPool(ConnectingIOReactor ioreactor, NIOConnFactory<HttpHost, NHttpClientConnection> connFactory, int connectTimeout) {
        super(ioreactor, connFactory, new BasicAddressResolver(), 2, 20);
        this.connectTimeout = connectTimeout;
    }

    public BasicNIOConnPool(ConnectingIOReactor ioreactor, int connectTimeout, ConnectionConfig config) {
        this(ioreactor, new BasicNIOConnFactory(config), connectTimeout);
    }

    public BasicNIOConnPool(ConnectingIOReactor ioreactor, ConnectionConfig config) {
        this(ioreactor, new BasicNIOConnFactory(config), 0);
    }

    public BasicNIOConnPool(ConnectingIOReactor ioreactor) {
        this(ioreactor, new BasicNIOConnFactory(ConnectionConfig.DEFAULT), 0);
    }

    @Deprecated
    protected SocketAddress resolveRemoteAddress(HttpHost host) {
        return new InetSocketAddress(host.getHostName(), host.getPort());
    }

    @Deprecated
    protected SocketAddress resolveLocalAddress(HttpHost host) {
        return null;
    }

    protected BasicNIOPoolEntry createEntry(HttpHost host, NHttpClientConnection conn) {
        BasicNIOPoolEntry entry = new BasicNIOPoolEntry(Long.toString(COUNTER.getAndIncrement()), host, conn);
        entry.setSocketTimeout(conn.getSocketTimeout());
        return entry;
    }

    public Future<BasicNIOPoolEntry> lease(HttpHost route, Object state, FutureCallback<BasicNIOPoolEntry> callback) {
        return super.lease(route, state, (long) this.connectTimeout, TimeUnit.MILLISECONDS, callback);
    }

    public Future<BasicNIOPoolEntry> lease(HttpHost route, Object state) {
        return super.lease(route, state, (long) this.connectTimeout, TimeUnit.MILLISECONDS, null);
    }

    protected void onLease(BasicNIOPoolEntry entry) {
        ((NHttpClientConnection) entry.getConnection()).setSocketTimeout(entry.getSocketTimeout());
    }

    protected void onRelease(BasicNIOPoolEntry entry) {
        NHttpClientConnection conn = (NHttpClientConnection) entry.getConnection();
        entry.setSocketTimeout(conn.getSocketTimeout());
        conn.setSocketTimeout(0);
    }
}
