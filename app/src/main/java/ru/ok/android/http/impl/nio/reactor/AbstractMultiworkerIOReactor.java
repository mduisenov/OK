package ru.ok.android.http.impl.nio.reactor;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import ru.ok.android.http.nio.reactor.IOEventDispatch;
import ru.ok.android.http.nio.reactor.IOReactor;
import ru.ok.android.http.nio.reactor.IOReactorException;
import ru.ok.android.http.nio.reactor.IOReactorExceptionHandler;
import ru.ok.android.http.nio.reactor.IOReactorStatus;
import ru.ok.android.http.params.BasicHttpParams;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.Asserts;

public abstract class AbstractMultiworkerIOReactor implements IOReactor {
    protected List<ExceptionEvent> auditLog;
    protected final IOReactorConfig config;
    private int currentWorker;
    private final BaseIOReactor[] dispatchers;
    protected IOReactorExceptionHandler exceptionHandler;
    protected final boolean interestOpsQueueing;
    @Deprecated
    protected final HttpParams params;
    protected final long selectTimeout;
    protected final Selector selector;
    protected volatile IOReactorStatus status;
    private final Object statusLock;
    private final ThreadFactory threadFactory;
    private final Thread[] threads;
    private final int workerCount;
    private final Worker[] workers;

    protected abstract void cancelRequests() throws IOReactorException;

    protected abstract void processEvents(int i) throws IOReactorException;

    public AbstractMultiworkerIOReactor(IOReactorConfig config, ThreadFactory threadFactory) throws IOReactorException {
        this.currentWorker = 0;
        if (config == null) {
            config = IOReactorConfig.DEFAULT;
        }
        this.config = config;
        this.params = new BasicHttpParams();
        try {
            this.selector = Selector.open();
            this.selectTimeout = this.config.getSelectInterval();
            this.interestOpsQueueing = this.config.isInterestOpQueued();
            this.statusLock = new Object();
            if (threadFactory != null) {
                this.threadFactory = threadFactory;
            } else {
                this.threadFactory = new DefaultThreadFactory();
            }
            this.auditLog = new ArrayList();
            this.workerCount = this.config.getIoThreadCount();
            this.dispatchers = new BaseIOReactor[this.workerCount];
            this.workers = new Worker[this.workerCount];
            this.threads = new Thread[this.workerCount];
            this.status = IOReactorStatus.INACTIVE;
        } catch (IOException ex) {
            throw new IOReactorException("Failure opening selector", ex);
        }
    }

    public AbstractMultiworkerIOReactor() throws IOReactorException {
        this(null, null);
    }

    @Deprecated
    static IOReactorConfig convert(int workerCount, HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        return IOReactorConfig.custom().setSelectInterval(params.getLongParameter("http.nio.select-interval", 1000)).setShutdownGracePeriod(params.getLongParameter("http.nio.grace-period", 500)).setInterestOpQueued(params.getBooleanParameter("http.nio.select-interval", false)).setIoThreadCount(workerCount).setSoTimeout(params.getIntParameter("http.socket.timeout", 0)).setConnectTimeout(params.getIntParameter("http.connection.timeout", 0)).setSoTimeout(params.getIntParameter("http.socket.timeout", 0)).setSoReuseAddress(params.getBooleanParameter("http.socket.reuseaddr", false)).setSoKeepAlive(params.getBooleanParameter("http.socket.keepalive", false)).setSoLinger(params.getIntParameter("http.socket.linger", -1)).setTcpNoDelay(params.getBooleanParameter("http.tcp.nodelay", true)).build();
    }

    @Deprecated
    public AbstractMultiworkerIOReactor(int workerCount, ThreadFactory threadFactory, HttpParams params) throws IOReactorException {
        this(convert(workerCount, params), threadFactory);
    }

    public IOReactorStatus getStatus() {
        return this.status;
    }

    public List<ExceptionEvent> getAuditLog() {
        List arrayList;
        synchronized (this.auditLog) {
            arrayList = new ArrayList(this.auditLog);
        }
        return arrayList;
    }

    protected synchronized void addExceptionEvent(Throwable ex, Date timestamp) {
        if (ex != null) {
            synchronized (this.auditLog) {
                List list = this.auditLog;
                if (timestamp == null) {
                    timestamp = new Date();
                }
                list.add(new ExceptionEvent(ex, timestamp));
            }
        }
    }

    protected void addExceptionEvent(Throwable ex) {
        addExceptionEvent(ex, null);
    }

    public void setExceptionHandler(IOReactorExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void execute(IOEventDispatch eventDispatch) throws InterruptedIOException, IOReactorException {
        Args.notNull(eventDispatch, "Event dispatcher");
        synchronized (this.statusLock) {
            if (this.status.compareTo(IOReactorStatus.SHUTDOWN_REQUEST) >= 0) {
                this.status = IOReactorStatus.SHUT_DOWN;
                this.statusLock.notifyAll();
                return;
            }
            int i;
            Asserts.check(this.status.compareTo(IOReactorStatus.INACTIVE) == 0, "Illegal state %s", this.status);
            this.status = IOReactorStatus.ACTIVE;
            for (i = 0; i < this.dispatchers.length; i++) {
                BaseIOReactor dispatcher = new BaseIOReactor(this.selectTimeout, this.interestOpsQueueing);
                dispatcher.setExceptionHandler(this.exceptionHandler);
                this.dispatchers[i] = dispatcher;
            }
            for (i = 0; i < this.workerCount; i++) {
                this.workers[i] = new Worker(this.dispatchers[i], eventDispatch);
                this.threads[i] = this.threadFactory.newThread(this.workers[i]);
            }
            i = 0;
            while (i < this.workerCount) {
                try {
                    if (this.status != IOReactorStatus.ACTIVE) {
                        doShutdown();
                        synchronized (this.statusLock) {
                            this.status = IOReactorStatus.SHUT_DOWN;
                            this.statusLock.notifyAll();
                        }
                        return;
                    }
                    this.threads[i].start();
                    i++;
                } catch (InterruptedIOException ex) {
                    throw ex;
                } catch (Exception ex2) {
                    throw new IOReactorException("Unexpected selector failure", ex2);
                } catch (ClosedSelectorException ex3) {
                    addExceptionEvent(ex3);
                    doShutdown();
                    synchronized (this.statusLock) {
                    }
                    this.status = IOReactorStatus.SHUT_DOWN;
                    this.statusLock.notifyAll();
                    return;
                } catch (IOReactorException ex4) {
                    if (ex4.getCause() != null) {
                        addExceptionEvent(ex4.getCause());
                    }
                    throw ex4;
                } catch (Throwable th) {
                    doShutdown();
                    synchronized (this.statusLock) {
                    }
                    this.status = IOReactorStatus.SHUT_DOWN;
                    this.statusLock.notifyAll();
                }
            }
            do {
                int readyCount = this.selector.select(this.selectTimeout);
                if (this.status.compareTo(IOReactorStatus.ACTIVE) == 0) {
                    processEvents(readyCount);
                }
                for (i = 0; i < this.workerCount; i++) {
                    Exception ex22 = this.workers[i].getException();
                    if (ex22 != null) {
                        throw new IOReactorException("I/O dispatch worker terminated abnormally", ex22);
                    }
                }
            } while (this.status.compareTo(IOReactorStatus.ACTIVE) <= 0);
            doShutdown();
            synchronized (this.statusLock) {
                this.status = IOReactorStatus.SHUT_DOWN;
                this.statusLock.notifyAll();
            }
        }
    }

    protected void doShutdown() throws InterruptedIOException {
        synchronized (this.statusLock) {
            if (this.status.compareTo(IOReactorStatus.SHUTTING_DOWN) >= 0) {
                return;
            }
            int i;
            this.status = IOReactorStatus.SHUTTING_DOWN;
            try {
                cancelRequests();
            } catch (IOReactorException ex) {
                if (ex.getCause() != null) {
                    addExceptionEvent(ex.getCause());
                }
            }
            this.selector.wakeup();
            if (this.selector.isOpen()) {
                for (SelectionKey key : this.selector.keys()) {
                    try {
                        Channel channel = key.channel();
                        if (channel != null) {
                            channel.close();
                        }
                    } catch (IOException ex2) {
                        addExceptionEvent(ex2);
                    }
                }
                try {
                    this.selector.close();
                } catch (IOException ex22) {
                    addExceptionEvent(ex22);
                }
            }
            for (i = 0; i < this.workerCount; i++) {
                this.dispatchers[i].gracefulShutdown();
            }
            long gracePeriod = this.config.getShutdownGracePeriod();
            i = 0;
            while (i < this.workerCount) {
                try {
                    BaseIOReactor dispatcher = this.dispatchers[i];
                    if (dispatcher.getStatus() != IOReactorStatus.INACTIVE) {
                        dispatcher.awaitShutdown(gracePeriod);
                    }
                    if (dispatcher.getStatus() != IOReactorStatus.SHUT_DOWN) {
                        try {
                            dispatcher.hardShutdown();
                        } catch (IOReactorException ex3) {
                            if (ex3.getCause() != null) {
                                addExceptionEvent(ex3.getCause());
                            }
                        }
                    }
                    i++;
                } catch (InterruptedException ex4) {
                    throw new InterruptedIOException(ex4.getMessage());
                }
            }
            for (i = 0; i < this.workerCount; i++) {
                Thread t = this.threads[i];
                if (t != null) {
                    t.join(gracePeriod);
                }
            }
        }
    }

    protected void addChannel(ChannelEntry entry) {
        int i = this.currentWorker;
        this.currentWorker = i + 1;
        this.dispatchers[Math.abs(i % this.workerCount)].addChannel(entry);
    }

    protected SelectionKey registerChannel(SelectableChannel channel, int ops) throws ClosedChannelException {
        return channel.register(this.selector, ops);
    }

    protected void prepareSocket(Socket socket) throws IOException {
        socket.setTcpNoDelay(this.config.isTcpNoDelay());
        socket.setKeepAlive(this.config.isSoKeepalive());
        if (this.config.getSoTimeout() > 0) {
            socket.setSoTimeout(this.config.getSoTimeout());
        }
        if (this.config.getSndBufSize() > 0) {
            socket.setSendBufferSize(this.config.getSndBufSize());
        }
        if (this.config.getRcvBufSize() > 0) {
            socket.setReceiveBufferSize(this.config.getRcvBufSize());
        }
        int linger = this.config.getSoLinger();
        if (linger >= 0) {
            socket.setSoLinger(true, linger);
        }
    }

    protected void awaitShutdown(long timeout) throws InterruptedException {
        synchronized (this.statusLock) {
            long deadline = System.currentTimeMillis() + timeout;
            long remaining = timeout;
            while (this.status != IOReactorStatus.SHUT_DOWN) {
                this.statusLock.wait(remaining);
                if (timeout > 0) {
                    remaining = deadline - System.currentTimeMillis();
                    if (remaining <= 0) {
                        break;
                    }
                }
            }
        }
    }

    public void shutdown() throws IOException {
        shutdown(2000);
    }

    public void shutdown(long waitMs) throws IOException {
        synchronized (this.statusLock) {
            if (this.status.compareTo(IOReactorStatus.ACTIVE) > 0) {
            } else if (this.status.compareTo(IOReactorStatus.INACTIVE) == 0) {
                this.status = IOReactorStatus.SHUT_DOWN;
                cancelRequests();
                this.selector.close();
            } else {
                this.status = IOReactorStatus.SHUTDOWN_REQUEST;
                this.selector.wakeup();
                try {
                    awaitShutdown(waitMs);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    static void closeChannel(Channel channel) {
        try {
            channel.close();
        } catch (IOException e) {
        }
    }
}
