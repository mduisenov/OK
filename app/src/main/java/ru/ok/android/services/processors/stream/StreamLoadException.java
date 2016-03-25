package ru.ok.android.services.processors.stream;

import android.os.Bundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class StreamLoadException extends Exception {
    private static final long serialVersionUID = 1;
    private transient Bundle errorBundle;

    public StreamLoadException(Throwable throwable, Bundle errorBundle) {
        super(throwable);
        this.errorBundle = errorBundle;
    }

    public StreamLoadException(String message) {
        super(message);
    }

    public Bundle getErrorBundle() {
        return this.errorBundle;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeBoolean(this.errorBundle != null);
        if (this.errorBundle != null) {
            stream.writeInt(this.errorBundle.size());
            for (String key : this.errorBundle.keySet()) {
                Object value = this.errorBundle.get(key);
                boolean isValueSerializable = value instanceof Serializable;
                stream.writeBoolean(isValueSerializable);
                if (isValueSerializable) {
                    stream.writeUTF(key);
                    stream.writeObject(value);
                }
            }
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        if (stream.readBoolean()) {
            this.errorBundle = new Bundle();
            int size = stream.readInt();
            for (int i = 0; i < size; i++) {
                if (stream.readBoolean()) {
                    this.errorBundle.putSerializable(stream.readUTF(), (Serializable) stream.readObject());
                }
            }
        }
    }
}
