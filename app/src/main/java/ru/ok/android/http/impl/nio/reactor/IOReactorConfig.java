package ru.ok.android.http.impl.nio.reactor;

import ru.ok.android.http.util.Args;

public final class IOReactorConfig implements Cloneable {
    private static final int AVAIL_PROCS;
    public static final IOReactorConfig DEFAULT;
    private int backlogSize;
    private int connectTimeout;
    private boolean interestOpQueued;
    private int ioThreadCount;
    private int rcvBufSize;
    private long selectInterval;
    private long shutdownGracePeriod;
    private int sndBufSize;
    private boolean soKeepAlive;
    private int soLinger;
    private boolean soReuseAddress;
    private int soTimeout;
    private boolean tcpNoDelay;

    static {
        AVAIL_PROCS = Runtime.getRuntime().availableProcessors();
        DEFAULT = new Builder().build();
    }

    @Deprecated
    public IOReactorConfig() {
        this.selectInterval = 1000;
        this.shutdownGracePeriod = 500;
        this.interestOpQueued = false;
        this.ioThreadCount = AVAIL_PROCS;
        this.soTimeout = 0;
        this.soReuseAddress = false;
        this.soLinger = -1;
        this.soKeepAlive = false;
        this.tcpNoDelay = true;
        this.connectTimeout = 0;
        this.sndBufSize = 0;
        this.rcvBufSize = 0;
        this.backlogSize = 0;
    }

    IOReactorConfig(long selectInterval, long shutdownGracePeriod, boolean interestOpQueued, int ioThreadCount, int soTimeout, boolean soReuseAddress, int soLinger, boolean soKeepAlive, boolean tcpNoDelay, int connectTimeout, int sndBufSize, int rcvBufSize, int backlogSize) {
        this.selectInterval = selectInterval;
        this.shutdownGracePeriod = shutdownGracePeriod;
        this.interestOpQueued = interestOpQueued;
        this.ioThreadCount = ioThreadCount;
        this.soTimeout = soTimeout;
        this.soReuseAddress = soReuseAddress;
        this.soLinger = soLinger;
        this.soKeepAlive = soKeepAlive;
        this.tcpNoDelay = tcpNoDelay;
        this.connectTimeout = connectTimeout;
        this.sndBufSize = sndBufSize;
        this.rcvBufSize = rcvBufSize;
        this.backlogSize = backlogSize;
    }

    public long getSelectInterval() {
        return this.selectInterval;
    }

    @Deprecated
    public void setSelectInterval(long selectInterval) {
        Args.positive(selectInterval, "Select internal");
        this.selectInterval = selectInterval;
    }

    public long getShutdownGracePeriod() {
        return this.shutdownGracePeriod;
    }

    @Deprecated
    public void setShutdownGracePeriod(long gracePeriod) {
        Args.positive(gracePeriod, "Shutdown grace period");
        this.shutdownGracePeriod = gracePeriod;
    }

    public boolean isInterestOpQueued() {
        return this.interestOpQueued;
    }

    @Deprecated
    public void setInterestOpQueued(boolean interestOpQueued) {
        this.interestOpQueued = interestOpQueued;
    }

    public int getIoThreadCount() {
        return this.ioThreadCount;
    }

    @Deprecated
    public void setIoThreadCount(int ioThreadCount) {
        Args.positive(ioThreadCount, "I/O thread count");
        this.ioThreadCount = ioThreadCount;
    }

    public int getSoTimeout() {
        return this.soTimeout;
    }

    @Deprecated
    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public boolean isSoReuseAddress() {
        return this.soReuseAddress;
    }

    @Deprecated
    public void setSoReuseAddress(boolean soReuseAddress) {
        this.soReuseAddress = soReuseAddress;
    }

    public int getSoLinger() {
        return this.soLinger;
    }

    @Deprecated
    public void setSoLinger(int soLinger) {
        this.soLinger = soLinger;
    }

    public boolean isSoKeepalive() {
        return this.soKeepAlive;
    }

    @Deprecated
    public void setSoKeepalive(boolean soKeepAlive) {
        this.soKeepAlive = soKeepAlive;
    }

    public boolean isTcpNoDelay() {
        return this.tcpNoDelay;
    }

    @Deprecated
    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    @Deprecated
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSndBufSize() {
        return this.sndBufSize;
    }

    @Deprecated
    public void setSndBufSize(int sndBufSize) {
        this.sndBufSize = sndBufSize;
    }

    public int getRcvBufSize() {
        return this.rcvBufSize;
    }

    @Deprecated
    public void setRcvBufSize(int rcvBufSize) {
        this.rcvBufSize = rcvBufSize;
    }

    public int getBacklogSize() {
        return this.backlogSize;
    }

    protected IOReactorConfig clone() throws CloneNotSupportedException {
        return (IOReactorConfig) super.clone();
    }

    public static Builder custom() {
        return new Builder();
    }

    public static Builder copy(IOReactorConfig config) {
        Args.notNull(config, "I/O reactor config");
        return new Builder().setSelectInterval(config.getSelectInterval()).setShutdownGracePeriod(config.getShutdownGracePeriod()).setInterestOpQueued(config.isInterestOpQueued()).setIoThreadCount(config.getIoThreadCount()).setSoTimeout(config.getSoTimeout()).setSoReuseAddress(config.isSoReuseAddress()).setSoLinger(config.getSoLinger()).setSoKeepAlive(config.isSoKeepalive()).setTcpNoDelay(config.isTcpNoDelay()).setConnectTimeout(config.getConnectTimeout()).setSndBufSize(config.getSndBufSize()).setRcvBufSize(config.getRcvBufSize()).setBacklogSize(config.getBacklogSize());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[selectInterval=").append(this.selectInterval).append(", shutdownGracePeriod=").append(this.shutdownGracePeriod).append(", interestOpQueued=").append(this.interestOpQueued).append(", ioThreadCount=").append(this.ioThreadCount).append(", soTimeout=").append(this.soTimeout).append(", soReuseAddress=").append(this.soReuseAddress).append(", soLinger=").append(this.soLinger).append(", soKeepAlive=").append(this.soKeepAlive).append(", tcpNoDelay=").append(this.tcpNoDelay).append(", connectTimeout=").append(this.connectTimeout).append(", sndBufSize=").append(this.sndBufSize).append(", rcvBufSize=").append(this.rcvBufSize).append(", backlogSize=").append(this.backlogSize).append("]");
        return builder.toString();
    }
}
