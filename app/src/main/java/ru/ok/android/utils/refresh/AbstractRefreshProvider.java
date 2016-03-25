package ru.ok.android.utils.refresh;

public abstract class AbstractRefreshProvider implements RefreshProvider {
    protected RefreshProviderOnRefreshListener refreshListener;

    public void setOnRefreshListener(RefreshProviderOnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }
}
