package ru.ok.android.http.conn;

import ru.ok.android.http.HttpClientConnection;

@Deprecated
public interface ManagedClientConnection extends HttpClientConnection, HttpRoutedConnection, ManagedHttpClientConnection {
}
