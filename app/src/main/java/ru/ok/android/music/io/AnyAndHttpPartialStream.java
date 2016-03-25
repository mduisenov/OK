package ru.ok.android.music.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import ru.ok.android.music.io.DoubleSourceInputStream.SecondStreamProvider;

public class AnyAndHttpPartialStream extends DoubleSourceInputStream {

    private static class HttpPartialStreamProvider implements SecondStreamProvider {
        private final HttpURLConnection connection;

        public HttpPartialStreamProvider(HttpURLConnection connection) {
            this.connection = connection;
        }

        public InputStream getSecondStream() throws IOException {
            InputStream secondStream = this.connection.getInputStream();
            this.connection.connect();
            int responseCode = this.connection.getResponseCode();
            if (responseCode == 206) {
                return secondStream;
            }
            throw new IOException("Response code is not acceptable: " + responseCode);
        }
    }

    public AnyAndHttpPartialStream(InputStream first, HttpURLConnection connection) {
        super(first, new HttpPartialStreamProvider(connection));
    }
}
