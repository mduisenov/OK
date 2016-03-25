package ru.ok.android.services.mediatopic_polls;

import android.content.Context;
import android.support.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import ru.ok.android.services.local.LocalModifsSerializer;
import ru.ok.android.services.local.LocalModifsSqliteStorage;

public class SqliteMtPollsStorage extends LocalModifsSqliteStorage<LocalMtPollVotes> {

    /* renamed from: ru.ok.android.services.mediatopic_polls.SqliteMtPollsStorage.1 */
    class C04411 extends LocalModifsSerializer<LocalMtPollVotes> {
        C04411() {
        }

        protected boolean hasCustomData(LocalMtPollVotes item) {
            return true;
        }

        protected void writeCustomData(LocalMtPollVotes item, DataOutputStream out) throws IOException {
            SqliteMtPollsStorage.write(item.allVotes, out);
            SqliteMtPollsStorage.write(item.notAddedVotes, out);
            SqliteMtPollsStorage.write(item.notRemovedVotes, out);
            out.writeBoolean(item.logContext != null);
            if (item.logContext != null) {
                out.writeUTF(item.logContext);
            }
        }

        protected LocalMtPollVotes createItem(String id, int status, int attempts, long syncedTs, @Nullable DataInputStream dataIn) throws IOException {
            return new LocalMtPollVotes(id, SqliteMtPollsStorage.readHashSet(dataIn), SqliteMtPollsStorage.readHashSet(dataIn), SqliteMtPollsStorage.readHashSet(dataIn), dataIn.readBoolean() ? dataIn.readUTF() : null, status, attempts, syncedTs);
        }
    }

    protected SqliteMtPollsStorage(Context context, String currentUserId) {
        super(context, currentUserId, "mt_polls", new C04411());
    }

    private static void write(Collection<String> a, DataOutputStream out) throws IOException {
        boolean z;
        if (a != null) {
            z = true;
        } else {
            z = false;
        }
        out.writeBoolean(z);
        if (a != null) {
            out.writeInt(a.size());
            for (String s : a) {
                if (s != null) {
                    z = true;
                } else {
                    z = false;
                }
                out.writeBoolean(z);
                if (s != null) {
                    out.writeUTF(s);
                }
            }
        }
    }

    private static HashSet<String> readHashSet(DataInputStream in) throws IOException {
        HashSet<String> a = null;
        if (in.readBoolean()) {
            int length = in.readInt();
            a = new HashSet(length);
            for (int i = 0; i < length; i++) {
                String s = null;
                if (in.readBoolean()) {
                    s = in.readUTF();
                }
                a.add(s);
            }
        }
        return a;
    }
}
