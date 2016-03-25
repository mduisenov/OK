package ru.ok.android.storage;

public interface IFeedBannerStatsStorage {
    boolean checkSaveFeedIsShown(String str) throws StorageException;

    boolean checkSaveFeedIsShownOnScroll(String str) throws StorageException;

    void removeOldRecords(long j) throws StorageException;
}
