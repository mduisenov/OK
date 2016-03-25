package ru.ok.android.services.local;

import android.support.annotation.NonNull;
import ru.ok.model.local.LocalModifs;

public interface LocalSyncConflictResolver<TLocal extends LocalModifs> {
    @NonNull
    TLocal onConflictInSync(@NonNull TLocal tLocal, @NonNull TLocal tLocal2);
}
