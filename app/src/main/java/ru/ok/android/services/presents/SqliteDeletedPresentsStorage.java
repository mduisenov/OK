package ru.ok.android.services.presents;

import android.content.Context;
import android.support.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.ok.android.services.local.LocalModifsSerializer;
import ru.ok.android.services.local.LocalModifsSqliteStorage;

public class SqliteDeletedPresentsStorage extends LocalModifsSqliteStorage<LocalDeletedPresent> {

    /* renamed from: ru.ok.android.services.presents.SqliteDeletedPresentsStorage.1 */
    class C04481 extends LocalModifsSerializer<LocalDeletedPresent> {
        C04481() {
        }

        protected boolean hasCustomData(LocalDeletedPresent item) {
            return false;
        }

        protected void writeCustomData(LocalDeletedPresent item, DataOutputStream out) throws IOException {
        }

        protected LocalDeletedPresent createItem(String presentId, int status, int attempts, long syncedTs, @Nullable DataInputStream dataIn) throws IOException {
            return new LocalDeletedPresent(presentId, status, attempts, syncedTs);
        }
    }

    protected SqliteDeletedPresentsStorage(Context context, String currentUserId) {
        super(context, currentUserId, "deleted_presents", new C04481());
    }
}
