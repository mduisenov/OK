package ru.ok.android.music;

import android.content.Context;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Pair;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import ru.ok.android.model.cache.music.MusicBaseFileCache;
import ru.ok.android.music.data.BufferedMusicFile;
import ru.ok.android.music.io.AnyAndHttpPartialStream;
import ru.ok.android.music.io.CloseConnectionInputStream;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.Storage.External.Application;

public final class MusicUtils {

    public static class SnapshotInputStream extends InputStream {
        InputStream baseStream;
        Snapshot snapshot;

        public static InputStream create(Snapshot snapshot, int numberStream) {
            return new SnapshotInputStream(snapshot, numberStream);
        }

        private SnapshotInputStream(Snapshot snapshot, int numberStream) {
            this.snapshot = snapshot;
            this.baseStream = snapshot.getInputStream(numberStream);
        }

        public int read() throws IOException {
            return this.baseStream.read();
        }

        public int read(byte[] buffer) throws IOException {
            return this.baseStream.read(buffer);
        }

        public int read(byte[] buffer, int offset, int length) throws IOException {
            return this.baseStream.read(buffer, offset, length);
        }

        public long skip(long byteCount) throws IOException {
            return this.baseStream.skip(byteCount);
        }

        public void close() throws IOException {
            super.close();
            if (this.snapshot != null) {
                this.snapshot.close();
            }
        }
    }

    public static Pair<InputStream, Long> initHttpInputStream(String mediaUrl, long offset, int readTimeout) throws IOException {
        HttpURLConnection mp3Connection = createHttpConnection(mediaUrl, offset, readTimeout);
        InputStream stream = new CloseConnectionInputStream(mp3Connection.getInputStream(), mp3Connection);
        mp3Connection.connect();
        int responseCode = mp3Connection.getResponseCode();
        if (responseCode == 206 || (responseCode == 200 && offset == 0)) {
            return new Pair(stream, Long.valueOf((long) mp3Connection.getContentLength()));
        }
        return null;
    }

    public static Pair<InputStream, Long> initFileInputStream(File file, long offset) {
        try {
            long size = file.length();
            if (size <= offset) {
                return null;
            }
            FileInputStream stream = new FileInputStream(file);
            stream.skip(offset);
            return new Pair(stream, Long.valueOf(size - offset));
        } catch (IOException e) {
            return null;
        }
    }

    public static Pair<InputStream, Long> initFileAndHttpInputStream(BufferedMusicFile file, String mediaUrl, long offset, int readTimeout) throws IOException {
        long fileLength = file.getFile().length();
        if (fileLength <= offset) {
            return null;
        }
        FileInputStream firstStream = new FileInputStream(file.getFile());
        firstStream.skip(offset);
        HttpURLConnection mp3Connection = createHttpConnection(mediaUrl, fileLength, readTimeout);
        return new Pair(new CloseConnectionInputStream(new AnyAndHttpPartialStream(firstStream, mp3Connection), mp3Connection), Long.valueOf(file.getExpectedLength() - offset));
    }

    private static HttpURLConnection createHttpConnection(String mp3Url, long startByteRead, int readTimeout) throws IOException {
        HttpURLConnection mp3Connection = (HttpURLConnection) new URL(mp3Url).openConnection(NetUtils.getProxyForUrl(mp3Url));
        mp3Connection.setRequestMethod("GET");
        mp3Connection.addRequestProperty("Range", "bytes=" + startByteRead + "-");
        mp3Connection.setDoInput(true);
        mp3Connection.setRequestProperty("Accept-Encoding", "identity");
        mp3Connection.setReadTimeout(readTimeout);
        return mp3Connection;
    }

    public static Pair<InputStream, Long> initCacheInputStream(MusicBaseFileCache fileCache, String mediaUrl, long offset) {
        Snapshot snapshot = fileCache.getInputSnapshot(MusicBaseFileCache.buildFileName(mediaUrl));
        if (snapshot == null) {
            return null;
        }
        long size = snapshot.getLength(0) - PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
        InputStream stream = SnapshotInputStream.create(snapshot, 0);
        try {
            stream.skip(PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID + offset);
            return new Pair(stream, Long.valueOf(size - offset));
        } catch (IOException e) {
            snapshot.close();
            return null;
        }
    }

    public static OutputStream initOutStream(File downloadingMediaFile) {
        try {
            downloadingMediaFile.delete();
            return new FileOutputStream(downloadingMediaFile);
        } catch (IOException e) {
            return null;
        }
    }

    public static File getCache(Context context) {
        File cache = Application.getCacheDir(context);
        if (cache == null) {
            return context.getCacheDir();
        }
        return cache;
    }
}
