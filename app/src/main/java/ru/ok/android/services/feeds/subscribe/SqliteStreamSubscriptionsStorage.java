package ru.ok.android.services.feeds.subscribe;

import android.content.Context;
import android.support.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.ok.android.services.local.LocalModifsSerializer;
import ru.ok.android.services.local.LocalModifsSqliteStorage;

public class SqliteStreamSubscriptionsStorage extends LocalModifsSqliteStorage<LocalStreamSubscription> {

    /* renamed from: ru.ok.android.services.feeds.subscribe.SqliteStreamSubscriptionsStorage.1 */
    class C04361 extends LocalModifsSerializer<LocalStreamSubscription> {
        C04361() {
        }

        protected boolean hasCustomData(LocalStreamSubscription item) {
            return true;
        }

        protected void writeCustomData(LocalStreamSubscription item, DataOutputStream out) throws IOException {
            boolean z = true;
            out.writeBoolean(item.isSubscribed);
            out.writeInt(item.ownerType);
            out.writeBoolean(item.ownerId != null);
            if (item.ownerId != null) {
                out.writeUTF(item.ownerId);
            }
            if (item.logContext == null) {
                z = false;
            }
            out.writeBoolean(z);
            if (item.logContext != null) {
                out.writeUTF(item.logContext);
            }
        }

        protected LocalStreamSubscription createItem(String id, int status, int attempts, long syncedTs, @Nullable DataInputStream dataIn) throws IOException {
            boolean isSubscribed = dataIn.readBoolean();
            int type = dataIn.readInt();
            if (LocalStreamSubscription.isValidEntityType(type)) {
                int ownerType = type;
                String ownerId = dataIn.readBoolean() ? dataIn.readUTF() : null;
                if (LocalStreamSubscription.createId(ownerType, ownerId).equals(id)) {
                    return new LocalStreamSubscription(id, status, attempts, syncedTs, isSubscribed, ownerType, ownerId, dataIn.readBoolean() ? dataIn.readUTF() : null);
                }
                throw new IOException("Invalid id=" + id + " for type=" + type + " ownerId=" + ownerId);
            }
            throw new IOException("Invalid type: " + type);
        }
    }

    protected SqliteStreamSubscriptionsStorage(Context context, String currentUserId) {
        super(context, currentUserId, "stream-subscriptions", new C04361());
    }
}
