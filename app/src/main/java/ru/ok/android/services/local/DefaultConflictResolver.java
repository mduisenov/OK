package ru.ok.android.services.local;

import android.support.annotation.NonNull;
import ru.ok.model.local.LocalModifs;

class DefaultConflictResolver<TLocal extends LocalModifs> implements LocalSyncConflictResolver<TLocal> {
    DefaultConflictResolver() {
    }

    @NonNull
    public TLocal onConflictInSync(@NonNull TLocal newLocalItem, @NonNull TLocal tLocal) {
        return newLocalItem;
    }
}
