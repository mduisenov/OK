package ru.ok.android.services.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.ok.android.utils.Logger;
import ru.ok.model.local.LocalModifs;

public abstract class LocalModifsSerializer<TLocal extends LocalModifs> {
    protected abstract TLocal createItem(String str, int i, int i2, long j, @Nullable DataInputStream dataInputStream) throws IOException;

    protected abstract boolean hasCustomData(TLocal tLocal);

    protected abstract void writeCustomData(TLocal tLocal, DataOutputStream dataOutputStream) throws IOException;

    @Nullable
    final TLocal createItem(String id, int status, int attempts, long syncedTs, byte[] data) {
        DataInputStream dataIn = null;
        if (data != null) {
            dataIn = new DataInputStream(new ByteArrayInputStream(data));
        }
        try {
            return createItem(id, status, attempts, syncedTs, dataIn);
        } catch (IOException e) {
            Logger.m180e(e, "Failed to read custom data bytes: %s", e);
            return null;
        }
    }

    @Nullable
    final byte[] getCustomDataBytes(@NonNull TLocal item) {
        if (!hasCustomData(item)) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(out);
        try {
            writeCustomData(item, dataOut);
            dataOut.flush();
        } catch (IOException e) {
            Logger.m180e(e, "Failed to write custom data bytes: %s", e);
        }
        return out.toByteArray();
    }
}
