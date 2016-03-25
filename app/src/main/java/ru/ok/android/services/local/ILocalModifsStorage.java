package ru.ok.android.services.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import ru.ok.android.storage.StorageException;
import ru.ok.model.local.LocalModifs;

public interface ILocalModifsStorage<TLocal extends LocalModifs> {
    int delete(int i, int i2) throws StorageException;

    int deleteOlder(long j) throws StorageException;

    int deleteOlder(@NonNull ArrayList<String> arrayList, long j) throws StorageException;

    @NonNull
    ArrayList<TLocal> getById(@NonNull ArrayList<String> arrayList) throws StorageException;

    @NonNull
    ArrayList<TLocal> getByStatus(int... iArr) throws StorageException;

    @Nullable
    TLocal getBySyncedTime(int i) throws StorageException;

    @Nullable
    TLocal getMostRecentSynced() throws StorageException;

    int getSize() throws StorageException;

    void update(TLocal tLocal) throws StorageException;
}
