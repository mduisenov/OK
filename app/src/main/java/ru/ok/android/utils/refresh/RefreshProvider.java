package ru.ok.android.utils.refresh;

public interface RefreshProvider {
    boolean isRefreshing();

    void refreshCompleted();

    void refreshStart();

    void setOnRefreshListener(RefreshProviderOnRefreshListener refreshProviderOnRefreshListener);

    void setRefreshEnabled(boolean z);

    void setRefreshing(boolean z);
}
