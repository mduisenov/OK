package ru.ok.android.music.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class CloseConnectionInputStream extends FilterInputStream {
    private final HttpURLConnection urlConnection;

    public CloseConnectionInputStream(InputStream source, HttpURLConnection urlConnection) {
        super(source);
        this.urlConnection = urlConnection;
    }

    public void close() throws IOException {
        super.close();
        this.urlConnection.disconnect();
    }
}
