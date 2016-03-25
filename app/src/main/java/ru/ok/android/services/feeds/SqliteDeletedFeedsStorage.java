package ru.ok.android.services.feeds;

import android.content.Context;
import android.support.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.ok.android.services.local.LocalModifsSerializer;
import ru.ok.android.services.local.LocalModifsSqliteStorage;

public class SqliteDeletedFeedsStorage extends LocalModifsSqliteStorage<LocalDeletedFeed> {

    /* renamed from: ru.ok.android.services.feeds.SqliteDeletedFeedsStorage.1 */
    class C04351 extends LocalModifsSerializer<LocalDeletedFeed> {
        C04351() {
        }

        protected boolean hasCustomData(LocalDeletedFeed item) {
            return (item.logContext == null || item.spamId == null) ? false : true;
        }

        protected void writeCustomData(LocalDeletedFeed item, DataOutputStream out) throws IOException {
            boolean z = true;
            out.writeBoolean(item.logContext != null);
            if (item.logContext != null) {
                out.writeUTF(item.logContext);
            }
            if (item.spamId == null) {
                z = false;
            }
            out.writeBoolean(z);
            if (item.spamId != null) {
                out.writeUTF(item.spamId);
            }
        }

        protected LocalDeletedFeed createItem(String id, int status, int attempts, long syncedTs, @Nullable DataInputStream dataIn) throws IOException {
            String logContext = null;
            String spamId = null;
            if (dataIn != null) {
                if (dataIn.readBoolean()) {
                    logContext = dataIn.readUTF();
                }
                if (dataIn.readBoolean()) {
                    spamId = dataIn.readUTF();
                }
            }
            return new LocalDeletedFeed(id, logContext, spamId, status, attempts, syncedTs);
        }
    }

    protected SqliteDeletedFeedsStorage(Context context, String currentUserId) {
        super(context, currentUserId, "deleted_feeds", new C04351());
    }
}
