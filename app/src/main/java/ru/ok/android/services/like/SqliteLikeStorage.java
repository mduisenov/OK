package ru.ok.android.services.like;

import android.content.Context;
import android.support.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.ok.android.services.local.LocalModifsSerializer;
import ru.ok.android.services.local.LocalModifsSqliteStorage;
import ru.ok.model.local.like.LocalLike;

public class SqliteLikeStorage extends LocalModifsSqliteStorage<LocalLike> {

    /* renamed from: ru.ok.android.services.like.SqliteLikeStorage.1 */
    class C04371 extends LocalModifsSerializer<LocalLike> {
        C04371() {
        }

        protected boolean hasCustomData(LocalLike item) {
            return true;
        }

        protected LocalLike createItem(String id, int status, int attempts, long syncedTs, @Nullable DataInputStream dataIn) throws IOException {
            boolean self = dataIn != null && dataIn.readBoolean();
            return new LocalLike(id, self, status, attempts, syncedTs);
        }

        protected void writeCustomData(LocalLike item, DataOutputStream out) throws IOException {
            out.writeBoolean(item.self);
        }
    }

    protected SqliteLikeStorage(Context context, String currentUserId) {
        super(context, currentUserId, "likes", new C04371());
    }
}
