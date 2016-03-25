package ru.ok.android.billingUtils;

public class Base64 {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final byte[] ALPHABET;
    private static final byte[] DECODABET;
    private static final byte[] WEBSAFE_ALPHABET;
    private static final byte[] WEBSAFE_DECODABET;

    static {
        boolean z;
        if (Base64.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
        ALPHABET = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 43, (byte) 47};
        WEBSAFE_ALPHABET = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 45, (byte) 95};
        DECODABET = new byte[]{(byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -5, (byte) -5, (byte) -9, (byte) -9, (byte) -5, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -5, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) 62, (byte) -9, (byte) -9, (byte) -9, (byte) 63, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 58, (byte) 59, (byte) 60, (byte) 61, (byte) -9, (byte) -9, (byte) -9, (byte) -1, (byte) -9, (byte) -9, (byte) -9, (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 10, (byte) 11, (byte) 12, (byte) 13, (byte) 14, (byte) 15, (byte) 16, (byte) 17, (byte) 18, (byte) 19, (byte) 20, (byte) 21, (byte) 22, (byte) 23, (byte) 24, (byte) 25, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) 26, (byte) 27, (byte) 28, (byte) 29, (byte) 30, (byte) 31, (byte) 32, (byte) 33, (byte) 34, (byte) 35, (byte) 36, (byte) 37, (byte) 38, (byte) 39, (byte) 40, (byte) 41, (byte) 42, (byte) 43, (byte) 44, (byte) 45, (byte) 46, (byte) 47, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9};
        WEBSAFE_DECODABET = new byte[]{(byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -5, (byte) -5, (byte) -9, (byte) -9, (byte) -5, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -5, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) 62, (byte) -9, (byte) -9, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 58, (byte) 59, (byte) 60, (byte) 61, (byte) -9, (byte) -9, (byte) -9, (byte) -1, (byte) -9, (byte) -9, (byte) -9, (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 10, (byte) 11, (byte) 12, (byte) 13, (byte) 14, (byte) 15, (byte) 16, (byte) 17, (byte) 18, (byte) 19, (byte) 20, (byte) 21, (byte) 22, (byte) 23, (byte) 24, (byte) 25, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) 63, (byte) -9, (byte) 26, (byte) 27, (byte) 28, (byte) 29, (byte) 30, (byte) 31, (byte) 32, (byte) 33, (byte) 34, (byte) 35, (byte) 36, (byte) 37, (byte) 38, (byte) 39, (byte) 40, (byte) 41, (byte) 42, (byte) 43, (byte) 44, (byte) 45, (byte) 46, (byte) 47, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9};
    }

    private Base64() {
    }

    private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset, byte[] decodabet) {
        if (source[srcOffset + 2] == (byte) 61) {
            destination[destOffset] = (byte) ((((decodabet[source[srcOffset]] << 24) >>> 6) | ((decodabet[source[srcOffset + 1]] << 24) >>> 12)) >>> 16);
            return 1;
        } else if (source[srcOffset + 3] == (byte) 61) {
            outBuff = (((decodabet[source[srcOffset]] << 24) >>> 6) | ((decodabet[source[srcOffset + 1]] << 24) >>> 12)) | ((decodabet[source[srcOffset + 2]] << 24) >>> 18);
            destination[destOffset] = (byte) (outBuff >>> 16);
            destination[destOffset + 1] = (byte) (outBuff >>> 8);
            return 2;
        } else {
            outBuff = ((((decodabet[source[srcOffset]] << 24) >>> 6) | ((decodabet[source[srcOffset + 1]] << 24) >>> 12)) | ((decodabet[source[srcOffset + 2]] << 24) >>> 18)) | ((decodabet[source[srcOffset + 3]] << 24) >>> 24);
            destination[destOffset] = (byte) (outBuff >> 16);
            destination[destOffset + 1] = (byte) (outBuff >> 8);
            destination[destOffset + 2] = (byte) outBuff;
            return 3;
        }
    }

    public static byte[] decode(String s) throws Base64DecoderException {
        byte[] bytes = s.getBytes();
        return decode(bytes, 0, bytes.length);
    }

    public static byte[] decode(byte[] source, int off, int len) throws Base64DecoderException {
        return decode(source, off, len, DECODABET);
    }

    public static byte[] decode(byte[] source, int off, int len, byte[] decodabet) throws Base64DecoderException {
        byte[] out;
        byte[] outBuff = new byte[(((len * 3) / 4) + 2)];
        int outBuffPosn = 0;
        byte[] b4 = new byte[4];
        int i = 0;
        int b4Posn = 0;
        while (i < len) {
            int b4Posn2;
            byte sbiCrop = (byte) (source[i + off] & 127);
            byte sbiDecode = decodabet[sbiCrop];
            if (sbiDecode >= -5) {
                if (sbiDecode < -1) {
                    b4Posn2 = b4Posn;
                } else if (sbiCrop == 61) {
                    int bytesLeft = len - i;
                    byte lastByte = (byte) (source[(len - 1) + off] & 127);
                    if (b4Posn == 0 || b4Posn == 1) {
                        throw new Base64DecoderException("invalid padding byte '=' at byte offset " + i);
                    } else if ((b4Posn != 3 || bytesLeft <= 2) && (b4Posn != 4 || bytesLeft <= 1)) {
                        if (!(lastByte == 61 || lastByte == 10)) {
                            throw new Base64DecoderException("encoded value has invalid trailing byte");
                        }
                        if (b4Posn != 0) {
                        } else if (b4Posn != 1) {
                            throw new Base64DecoderException("single trailing character at offset " + (len - 1));
                        } else {
                            b4Posn2 = b4Posn + 1;
                            b4[b4Posn] = (byte) 61;
                            outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, decodabet);
                        }
                        out = new byte[outBuffPosn];
                        System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
                        return out;
                    } else {
                        throw new Base64DecoderException("padding byte '=' falsely signals end of encoded value at offset " + i);
                    }
                } else {
                    b4Posn2 = b4Posn + 1;
                    b4[b4Posn] = sbiCrop;
                    if (b4Posn2 == 4) {
                        outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, decodabet);
                        b4Posn2 = 0;
                    }
                }
                i++;
                b4Posn = b4Posn2;
            } else {
                throw new Base64DecoderException("Bad Base64 input character at " + i + ": " + source[i + off] + "(decimal)");
            }
        }
        if (b4Posn != 0) {
        } else if (b4Posn != 1) {
            b4Posn2 = b4Posn + 1;
            b4[b4Posn] = (byte) 61;
            outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, decodabet);
        } else {
            throw new Base64DecoderException("single trailing character at offset " + (len - 1));
        }
        out = new byte[outBuffPosn];
        System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
        return out;
    }
}
