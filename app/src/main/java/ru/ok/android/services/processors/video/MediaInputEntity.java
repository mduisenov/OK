package ru.ok.android.services.processors.video;

import android.content.ContentResolver;
import android.support.v4.app.FragmentTransaction;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import ru.ok.android.http.entity.AbstractHttpEntity;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.image.ObservableInputStream;
import ru.ok.java.api.request.image.ObservableInputStream.InputStreamObserver;

public class MediaInputEntity extends AbstractHttpEntity {
    private static int instanceCount;
    private final ContentResolver contentResolver;
    private final InputStreamObserver inputStreamObserver;
    private final int instanceId;
    private final MediaInfo media;
    private final long skipBytes;

    public MediaInputEntity(ContentResolver contentResolver, MediaInfo media, long skipBytes, InputStreamObserver inputStreamObserver) {
        int i = instanceCount + 1;
        instanceCount = i;
        this.instanceId = i;
        this.contentResolver = contentResolver;
        this.media = media;
        this.skipBytes = skipBytes;
        this.inputStreamObserver = inputStreamObserver;
    }

    public boolean isRepeatable() {
        return true;
    }

    public long getContentLength() {
        return this.media.getSizeBytes();
    }

    public InputStream getContent() throws IOException, IllegalStateException {
        Logger.m173d("[%d] Opening for upload: %s", Integer.valueOf(this.instanceId), this.media.getUri());
        try {
            InputStream in = this.media.open(this.contentResolver);
            if (this.skipBytes > 0) {
                try {
                    in.skip(this.skipBytes);
                } catch (IOException e) {
                    Logger.m180e(e, "Failed to skip to startPosition: %s", e);
                    throw new RuntimeException(new VideoUploadException(22, "Error occurred while reading " + this.media.getUri(), e));
                }
            }
            if (this.inputStreamObserver != null) {
                return new ObservableInputStream(in, this.inputStreamObserver);
            }
            return in;
        } catch (Exception e2) {
            throw new RuntimeException(new VideoUploadException(22, "Failed to open for upload: " + this.media.getUri(), e2));
        }
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        Closeable closeable = null;
        try {
            closeable = getContent();
            byte[] buffer = new byte[FragmentTransaction.TRANSIT_ENTER_MASK];
            while (true) {
                int readBytes = closeable.read(buffer);
                if (readBytes < 0) {
                    break;
                }
                outputStream.write(buffer, 0, readBytes);
            }
            outputStream.flush();
        } finally {
            IOUtils.closeSilently(closeable);
        }
    }

    public boolean isStreaming() {
        return false;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
