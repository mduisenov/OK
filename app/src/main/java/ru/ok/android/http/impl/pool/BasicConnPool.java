package ru.ok.android.http.impl.pool;

import java.util.concurrent.atomic.AtomicLong;
import ru.ok.android.http.HttpClientConnection;
import ru.ok.android.http.HttpHost;
import ru.ok.android.http.config.ConnectionConfig;
import ru.ok.android.http.config.SocketConfig;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.pool.AbstractConnPool;
import ru.ok.android.http.pool.ConnFactory;

public class BasicConnPool extends AbstractConnPool<HttpHost, HttpClientConnection, BasicPoolEntry> {
    private static final AtomicLong COUNTER;

    static {
        COUNTER = new AtomicLong();
    }

    public BasicConnPool(ConnFactory<HttpHost, HttpClientConnection> connFactory) {
        super(connFactory, 2, 20);
    }

    @Deprecated
    public BasicConnPool(HttpParams params) {
        super(new BasicConnFactory(params), 2, 20);
    }

    public BasicConnPool(SocketConfig sconfig, ConnectionConfig cconfig) {
        super(new BasicConnFactory(sconfig, cconfig), 2, 20);
    }

    public BasicConnPool() {
        super(new BasicConnFactory(SocketConfig.DEFAULT, ConnectionConfig.DEFAULT), 2, 20);
    }

    protected BasicPoolEntry createEntry(HttpHost host, HttpClientConnection conn) {
        return new BasicPoolEntry(Long.toString(COUNTER.getAndIncrement()), host, conn);
    }

    protected boolean validate(BasicPoolEntry entry) {
        return !((HttpClientConnection) entry.getConnection()).isStale();
    }
}
