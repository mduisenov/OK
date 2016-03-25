package ru.ok.android.http.impl.nio.reactor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import ru.ok.android.http.nio.reactor.IOReactorException;
import ru.ok.android.http.nio.reactor.IOReactorStatus;
import ru.ok.android.http.nio.reactor.ListenerEndpoint;
import ru.ok.android.http.nio.reactor.ListeningIOReactor;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Asserts;

public class DefaultListeningIOReactor extends AbstractMultiworkerIOReactor implements ListeningIOReactor {
    private final Set<ListenerEndpointImpl> endpoints;
    private volatile boolean paused;
    private final Set<SocketAddress> pausedEndpoints;
    private final Queue<ListenerEndpointImpl> requestQueue;

    public DefaultListeningIOReactor(IOReactorConfig config, ThreadFactory threadFactory) throws IOReactorException {
        super(config, threadFactory);
        this.requestQueue = new ConcurrentLinkedQueue();
        this.endpoints = Collections.synchronizedSet(new HashSet());
        this.pausedEndpoints = new HashSet();
    }

    public DefaultListeningIOReactor(IOReactorConfig config) throws IOReactorException {
        this(config, null);
    }

    public DefaultListeningIOReactor() throws IOReactorException {
        this(null, null);
    }

    @Deprecated
    public DefaultListeningIOReactor(int workerCount, ThreadFactory threadFactory, HttpParams params) throws IOReactorException {
        this(AbstractMultiworkerIOReactor.convert(workerCount, params), threadFactory);
    }

    @Deprecated
    public DefaultListeningIOReactor(int workerCount, HttpParams params) throws IOReactorException {
        this(AbstractMultiworkerIOReactor.convert(workerCount, params), null);
    }

    protected void cancelRequests() throws IOReactorException {
        while (true) {
            ListenerEndpointImpl request = (ListenerEndpointImpl) this.requestQueue.poll();
            if (request != null) {
                request.cancel();
            } else {
                return;
            }
        }
    }

    protected void processEvents(int readyCount) throws IOReactorException {
        if (!this.paused) {
            processSessionRequests();
        }
        if (readyCount > 0) {
            Set<SelectionKey> selectedKeys = this.selector.selectedKeys();
            for (SelectionKey key : selectedKeys) {
                processEvent(key);
            }
            selectedKeys.clear();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void processEvent(java.nio.channels.SelectionKey r8) throws ru.ok.android.http.nio.reactor.IOReactorException {
        /*
        r7 = this;
        r5 = r8.isAcceptable();	 Catch:{ CancelledKeyException -> 0x002a }
        if (r5 == 0) goto L_0x0013;
    L_0x0006:
        r3 = r8.channel();	 Catch:{ CancelledKeyException -> 0x002a }
        r3 = (java.nio.channels.ServerSocketChannel) r3;	 Catch:{ CancelledKeyException -> 0x002a }
    L_0x000c:
        r4 = 0;
        r4 = r3.accept();	 Catch:{ IOException -> 0x0014 }
    L_0x0011:
        if (r4 != 0) goto L_0x003b;
    L_0x0013:
        return;
    L_0x0014:
        r2 = move-exception;
        r5 = r7.exceptionHandler;	 Catch:{ CancelledKeyException -> 0x002a }
        if (r5 == 0) goto L_0x0021;
    L_0x0019:
        r5 = r7.exceptionHandler;	 Catch:{ CancelledKeyException -> 0x002a }
        r5 = r5.handle(r2);	 Catch:{ CancelledKeyException -> 0x002a }
        if (r5 != 0) goto L_0x0011;
    L_0x0021:
        r5 = new ru.ok.android.http.nio.reactor.IOReactorException;	 Catch:{ CancelledKeyException -> 0x002a }
        r6 = "Failure accepting connection";
        r5.<init>(r6, r2);	 Catch:{ CancelledKeyException -> 0x002a }
        throw r5;	 Catch:{ CancelledKeyException -> 0x002a }
    L_0x002a:
        r2 = move-exception;
        r0 = r8.attachment();
        r0 = (ru.ok.android.http.nio.reactor.ListenerEndpoint) r0;
        r5 = r7.endpoints;
        r5.remove(r0);
        r5 = 0;
        r8.attach(r5);
        goto L_0x0013;
    L_0x003b:
        r5 = r4.socket();	 Catch:{ IOException -> 0x004b }
        r7.prepareSocket(r5);	 Catch:{ IOException -> 0x004b }
    L_0x0042:
        r1 = new ru.ok.android.http.impl.nio.reactor.ChannelEntry;	 Catch:{ CancelledKeyException -> 0x002a }
        r1.<init>(r4);	 Catch:{ CancelledKeyException -> 0x002a }
        r7.addChannel(r1);	 Catch:{ CancelledKeyException -> 0x002a }
        goto L_0x000c;
    L_0x004b:
        r2 = move-exception;
        r5 = r7.exceptionHandler;	 Catch:{ CancelledKeyException -> 0x002a }
        if (r5 == 0) goto L_0x0058;
    L_0x0050:
        r5 = r7.exceptionHandler;	 Catch:{ CancelledKeyException -> 0x002a }
        r5 = r5.handle(r2);	 Catch:{ CancelledKeyException -> 0x002a }
        if (r5 != 0) goto L_0x0042;
    L_0x0058:
        r5 = new ru.ok.android.http.nio.reactor.IOReactorException;	 Catch:{ CancelledKeyException -> 0x002a }
        r6 = "Failure initalizing socket";
        r5.<init>(r6, r2);	 Catch:{ CancelledKeyException -> 0x002a }
        throw r5;	 Catch:{ CancelledKeyException -> 0x002a }
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.http.impl.nio.reactor.DefaultListeningIOReactor.processEvent(java.nio.channels.SelectionKey):void");
    }

    private ListenerEndpointImpl createEndpoint(SocketAddress address) {
        return new ListenerEndpointImpl(address, new 1(this));
    }

    public ListenerEndpoint listen(SocketAddress address) {
        Asserts.check(this.status.compareTo(IOReactorStatus.ACTIVE) <= 0, "I/O reactor has been shut down");
        ListenerEndpointImpl request = createEndpoint(address);
        this.requestQueue.add(request);
        this.selector.wakeup();
        return request;
    }

    private void processSessionRequests() throws IOReactorException {
        while (true) {
            ListenerEndpointImpl request = (ListenerEndpointImpl) this.requestQueue.poll();
            if (request != null) {
                SocketAddress address = request.getAddress();
                try {
                    ServerSocketChannel serverChannel = ServerSocketChannel.open();
                    try {
                        ServerSocket socket = serverChannel.socket();
                        socket.setReuseAddress(this.config.isSoReuseAddress());
                        if (this.config.getSoTimeout() > 0) {
                            socket.setSoTimeout(this.config.getSoTimeout());
                        }
                        if (this.config.getRcvBufSize() > 0) {
                            socket.setReceiveBufferSize(this.config.getRcvBufSize());
                        }
                        serverChannel.configureBlocking(false);
                        socket.bind(address, this.config.getBacklogSize());
                        try {
                            SelectionKey key = serverChannel.register(this.selector, 16);
                            key.attach(request);
                            request.setKey(key);
                            this.endpoints.add(request);
                            request.completed(serverChannel.socket().getLocalSocketAddress());
                        } catch (IOException ex) {
                            AbstractMultiworkerIOReactor.closeChannel(serverChannel);
                            throw new IOReactorException("Failure registering channel with the selector", ex);
                        }
                    } catch (IOException ex2) {
                        AbstractMultiworkerIOReactor.closeChannel(serverChannel);
                        request.failed(ex2);
                        if (this.exceptionHandler == null || !this.exceptionHandler.handle(ex2)) {
                            throw new IOReactorException("Failure binding socket to address " + address, ex2);
                        }
                        return;
                    }
                } catch (IOException ex22) {
                    throw new IOReactorException("Failure opening server socket", ex22);
                }
            }
            return;
        }
    }

    public Set<ListenerEndpoint> getEndpoints() {
        Set<ListenerEndpoint> set = new HashSet();
        synchronized (this.endpoints) {
            Iterator<ListenerEndpointImpl> it = this.endpoints.iterator();
            while (it.hasNext()) {
                ListenerEndpoint endpoint = (ListenerEndpoint) it.next();
                if (endpoint.isClosed()) {
                    it.remove();
                } else {
                    set.add(endpoint);
                }
            }
        }
        return set;
    }

    public void pause() throws IOException {
        if (!this.paused) {
            this.paused = true;
            synchronized (this.endpoints) {
                for (ListenerEndpointImpl endpoint : this.endpoints) {
                    if (!endpoint.isClosed()) {
                        endpoint.close();
                        this.pausedEndpoints.add(endpoint.getAddress());
                    }
                }
                this.endpoints.clear();
            }
        }
    }

    public void resume() throws IOException {
        if (this.paused) {
            this.paused = false;
            for (SocketAddress address : this.pausedEndpoints) {
                this.requestQueue.add(createEndpoint(address));
            }
            this.pausedEndpoints.clear();
            this.selector.wakeup();
        }
    }
}
