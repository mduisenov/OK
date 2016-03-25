package ru.ok.android.http.impl.nio.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import ru.ok.android.http.nio.reactor.ConnectingIOReactor;
import ru.ok.android.http.nio.reactor.IOReactorException;
import ru.ok.android.http.nio.reactor.IOReactorStatus;
import ru.ok.android.http.nio.reactor.SessionRequest;
import ru.ok.android.http.nio.reactor.SessionRequestCallback;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Asserts;

public class DefaultConnectingIOReactor extends AbstractMultiworkerIOReactor implements ConnectingIOReactor {
    private long lastTimeoutCheck;
    private final Queue<SessionRequestImpl> requestQueue;

    public DefaultConnectingIOReactor(IOReactorConfig config, ThreadFactory threadFactory) throws IOReactorException {
        super(config, threadFactory);
        this.requestQueue = new ConcurrentLinkedQueue();
        this.lastTimeoutCheck = System.currentTimeMillis();
    }

    public DefaultConnectingIOReactor(IOReactorConfig config) throws IOReactorException {
        this(config, null);
    }

    public DefaultConnectingIOReactor() throws IOReactorException {
        this(null, null);
    }

    @Deprecated
    public DefaultConnectingIOReactor(int workerCount, ThreadFactory threadFactory, HttpParams params) throws IOReactorException {
        this(AbstractMultiworkerIOReactor.convert(workerCount, params), threadFactory);
    }

    @Deprecated
    public DefaultConnectingIOReactor(int workerCount, HttpParams params) throws IOReactorException {
        this(AbstractMultiworkerIOReactor.convert(workerCount, params), null);
    }

    protected void cancelRequests() throws IOReactorException {
        while (true) {
            SessionRequestImpl request = (SessionRequestImpl) this.requestQueue.poll();
            if (request != null) {
                request.cancel();
            } else {
                return;
            }
        }
    }

    protected void processEvents(int readyCount) throws IOReactorException {
        processSessionRequests();
        if (readyCount > 0) {
            Set<SelectionKey> selectedKeys = this.selector.selectedKeys();
            for (SelectionKey key : selectedKeys) {
                processEvent(key);
            }
            selectedKeys.clear();
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastTimeoutCheck >= this.selectTimeout) {
            this.lastTimeoutCheck = currentTime;
            processTimeouts(this.selector.keys());
        }
    }

    private void processEvent(SelectionKey key) {
        SessionRequestImpl sessionRequest;
        try {
            if (key.isConnectable()) {
                SocketChannel channel = (SocketChannel) key.channel();
                sessionRequest = ((SessionRequestHandle) key.attachment()).getSessionRequest();
                try {
                    channel.finishConnect();
                } catch (IOException ex) {
                    sessionRequest.failed(ex);
                }
                key.cancel();
                key.attach(null);
                if (sessionRequest.isCompleted()) {
                    try {
                        channel.close();
                        return;
                    } catch (IOException e) {
                        return;
                    }
                }
                addChannel(new ChannelEntry(channel, sessionRequest));
            }
        } catch (CancelledKeyException e2) {
            SessionRequestHandle requestHandle = (SessionRequestHandle) key.attachment();
            key.attach(null);
            if (requestHandle != null) {
                sessionRequest = requestHandle.getSessionRequest();
                if (sessionRequest != null) {
                    sessionRequest.cancel();
                }
            }
        }
    }

    private void processTimeouts(Set<SelectionKey> keys) {
        long now = System.currentTimeMillis();
        for (SelectionKey key : keys) {
            if (key.attachment() instanceof SessionRequestHandle) {
                SessionRequestHandle handle = (SessionRequestHandle) key.attachment();
                SessionRequestImpl sessionRequest = handle.getSessionRequest();
                int timeout = sessionRequest.getConnectTimeout();
                if (timeout > 0 && handle.getRequestTime() + ((long) timeout) < now) {
                    sessionRequest.timeout();
                }
            }
        }
    }

    public SessionRequest connect(SocketAddress remoteAddress, SocketAddress localAddress, Object attachment, SessionRequestCallback callback) {
        Asserts.check(this.status.compareTo(IOReactorStatus.ACTIVE) <= 0, "I/O reactor has been shut down");
        SessionRequestImpl sessionRequest = new SessionRequestImpl(remoteAddress, localAddress, attachment, callback);
        sessionRequest.setConnectTimeout(this.config.getConnectTimeout());
        this.requestQueue.add(sessionRequest);
        this.selector.wakeup();
        return sessionRequest;
    }

    private void validateAddress(SocketAddress address) throws UnknownHostException {
        if (address != null && (address instanceof InetSocketAddress)) {
            InetSocketAddress endpoint = (InetSocketAddress) address;
            if (endpoint.isUnresolved()) {
                throw new UnknownHostException(endpoint.getHostName());
            }
        }
    }

    private void processSessionRequests() throws IOReactorException {
        while (true) {
            SessionRequestImpl request = (SessionRequestImpl) this.requestQueue.poll();
            if (request == null) {
                return;
            }
            if (!request.isCompleted()) {
                try {
                    SocketChannel socketChannel = SocketChannel.open();
                    try {
                        validateAddress(request.getLocalAddress());
                        validateAddress(request.getRemoteAddress());
                        socketChannel.configureBlocking(false);
                        prepareSocket(socketChannel.socket());
                        if (request.getLocalAddress() != null) {
                            Socket sock = socketChannel.socket();
                            sock.setReuseAddress(this.config.isSoReuseAddress());
                            sock.bind(request.getLocalAddress());
                        }
                        if (socketChannel.connect(request.getRemoteAddress())) {
                            addChannel(new ChannelEntry(socketChannel, request));
                        } else {
                            try {
                                request.setKey(socketChannel.register(this.selector, 8, new SessionRequestHandle(request)));
                            } catch (IOException ex) {
                                AbstractMultiworkerIOReactor.closeChannel(socketChannel);
                                throw new IOReactorException("Failure registering channel with the selector", ex);
                            }
                        }
                    } catch (IOException ex2) {
                        AbstractMultiworkerIOReactor.closeChannel(socketChannel);
                        request.failed(ex2);
                        return;
                    }
                } catch (IOException ex22) {
                    throw new IOReactorException("Failure opening socket", ex22);
                }
            }
        }
    }
}
