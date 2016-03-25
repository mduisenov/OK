package ru.ok.android.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ISerializer<T> {
    T read(InputStream inputStream) throws IOException;

    void write(T t, OutputStream outputStream) throws IOException;
}
