package ru.ok.android.storage;

import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import ru.ok.android.ui.stream.data.StreamContext;
import ru.ok.model.stream.StreamPageKey;

public interface IStreamMetaStorage {
    @Nullable
    HashMap<StreamContext, ArrayList<StreamPageKey>> getOlder(long j) throws StorageException;

    void put(StreamContext streamContext, StreamPageKey streamPageKey, long j) throws StorageException;

    void remove(HashMap<StreamContext, ArrayList<StreamPageKey>> hashMap) throws StorageException;

    void remove(StreamContext streamContext, StreamPageKey streamPageKey) throws StorageException;
}
