package ru.ok.android.storage;

import java.util.List;
import ru.ok.model.stream.Feed;

public interface IFeedIdStorage {
    void generateFeedIds(List<Feed> list) throws StorageException;
}
