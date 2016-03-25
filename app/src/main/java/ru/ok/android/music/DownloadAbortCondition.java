package ru.ok.android.music;

public final class DownloadAbortCondition {
    private int retryCount;

    public DownloadAbortCondition() {
        this.retryCount = 0;
    }

    public int getReadTimeout() {
        return (int) (5000.0d + (Math.pow(2.0d, (double) this.retryCount) * 2000.0d));
    }

    public void reset() {
        this.retryCount = 0;
    }

    public boolean isTrue() {
        return this.retryCount >= 5;
    }

    public void newAttempt() {
        this.retryCount++;
    }
}
