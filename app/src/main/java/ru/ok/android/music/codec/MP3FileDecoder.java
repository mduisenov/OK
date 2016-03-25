package ru.ok.android.music.codec;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import ru.ok.android.proto.MessagesProto.Message;

public class MP3FileDecoder {
    private ByteBuffer byteBuffer;
    private int handle;
    private ShortBuffer shortBuffer;

    private native void closeFile(int i);

    private native int getAudioMode(int i);

    private native int getBitRate(int i);

    private native int getChannelsCount(int i);

    private native int getPcmLength(int i);

    private native int getSampleRate(int i);

    private native int openFile(String str, long j);

    public native int readSamples(int i, FloatBuffer floatBuffer, int i2);

    public native int readSamples(int i, ShortBuffer shortBuffer, int i2);

    static {
        System.loadLibrary("mp3-tool");
    }

    public MP3FileDecoder(String file, long offset) {
        this.handle = openFile(file, offset);
        if (this.handle == -1) {
            throw new IllegalArgumentException("Couldn't open file '" + file + "'");
        }
    }

    public int getAudioSampleRate(int defValue) {
        int rate = getSampleRate(this.handle);
        return rate > 0 ? rate : defValue;
    }

    public int getAudioBitRate(int defValue) {
        int rate = getBitRate(this.handle);
        return rate > 0 ? rate : defValue;
    }

    public int getPcmSize() {
        return getPcmLength(this.handle);
    }

    public int getChannelsFormat(int defValue) {
        switch (getAudioMode(this.handle)) {
            case RECEIVED_VALUE:
                return 4;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return 12;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return 12;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return 12;
            default:
                return defValue;
        }
    }

    public int getAudioChannelsCount(int defValue) {
        int count = getChannelsCount(this.handle);
        return count > 0 ? count : defValue;
    }

    public int readSamples(int size) {
        ShortBuffer shortBuffer = getShortBuffer(size);
        int readSamples = readSamples(this.handle, shortBuffer, shortBuffer.capacity());
        if (readSamples != 0) {
            return readSamples * 2;
        }
        closeFile(this.handle);
        return 0;
    }

    public byte[] getBytesArray(int size) {
        if (this.byteBuffer.hasArray()) {
            return this.byteBuffer.array();
        }
        byte[] outArray = new byte[size];
        this.byteBuffer.position(0);
        this.byteBuffer.get(outArray);
        return outArray;
    }

    public int getByteOffset() {
        if (this.byteBuffer.hasArray()) {
            return this.byteBuffer.arrayOffset();
        }
        return 0;
    }

    private ShortBuffer getShortBuffer(int sizeInBytes) {
        if (this.shortBuffer == null || this.shortBuffer.capacity() != sizeInBytes / 2) {
            this.byteBuffer = getByteBuffer(sizeInBytes);
            this.shortBuffer = this.byteBuffer.asShortBuffer();
        }
        return this.shortBuffer;
    }

    private ByteBuffer getByteBuffer(int size) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(size);
        byteBuffer.order(ByteOrder.nativeOrder());
        return byteBuffer;
    }

    public void dispose() {
        closeFile(this.handle);
    }
}
