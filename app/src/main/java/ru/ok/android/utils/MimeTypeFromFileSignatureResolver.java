package ru.ok.android.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

public class MimeTypeFromFileSignatureResolver {
    private static final int MAX_SIGNATURE_LENGTH;
    private static final SignatureChecker[] SIGNATURE_CHECKERS;

    private static abstract class SignatureChecker {
        public abstract boolean check(@NonNull byte[] bArr, int i);

        public abstract String getMimeType(@NonNull byte[] bArr, int i);

        private SignatureChecker() {
        }

        static boolean checkPattern(byte[] bytesToCheck, int offset, byte[] pattern) {
            if (pattern.length + offset > bytesToCheck.length) {
                return false;
            }
            for (int i = 0; i < pattern.length; i++) {
                if (bytesToCheck[i + offset] != pattern[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    private static class GifSignatureChecker extends SignatureChecker {
        public static final byte[] GIF_SIGNATURE;

        private GifSignatureChecker() {
            super();
        }

        static {
            GIF_SIGNATURE = new byte[]{(byte) 71, (byte) 73, (byte) 70, (byte) 56};
        }

        public boolean check(@NonNull byte[] signatureBytes, int signatureSize) {
            return signatureSize >= GIF_SIGNATURE.length && SignatureChecker.checkPattern(signatureBytes, 0, GIF_SIGNATURE);
        }

        public String getMimeType(@NonNull byte[] signatureBytes, int signatureSize) {
            return "image/gif";
        }
    }

    private static class JpegSignatureChecker extends SignatureChecker {
        private static final byte[] JPEG_SIGNATURE;

        private JpegSignatureChecker() {
            super();
        }

        static {
            JPEG_SIGNATURE = new byte[]{(byte) -1, (byte) -40, (byte) -1, (byte) -32};
        }

        public boolean check(@NonNull byte[] signatureBytes, int signatureSize) {
            return signatureSize >= JPEG_SIGNATURE.length && SignatureChecker.checkPattern(signatureBytes, 0, JPEG_SIGNATURE);
        }

        public String getMimeType(@NonNull byte[] signatureBytes, int signatureSize) {
            return "image/jpeg";
        }
    }

    private static class PngSignatureChecker extends SignatureChecker {
        private static final byte[] PNG_SIGNATURE;

        private PngSignatureChecker() {
            super();
        }

        static {
            PNG_SIGNATURE = new byte[]{(byte) -119, (byte) 80, (byte) 78, (byte) 71, (byte) 13, (byte) 10, (byte) 26, (byte) 10};
        }

        public boolean check(@NonNull byte[] signatureBytes, int signatureSize) {
            return signatureSize >= PNG_SIGNATURE.length && SignatureChecker.checkPattern(signatureBytes, 0, PNG_SIGNATURE);
        }

        public String getMimeType(@NonNull byte[] signatureBytes, int signatureSize) {
            return "image/png";
        }
    }

    static {
        SIGNATURE_CHECKERS = new SignatureChecker[]{new JpegSignatureChecker(), new PngSignatureChecker(), new GifSignatureChecker()};
        MAX_SIGNATURE_LENGTH = MathUtils.max(new int[]{JpegSignatureChecker.JPEG_SIGNATURE.length, PngSignatureChecker.PNG_SIGNATURE.length, GifSignatureChecker.GIF_SIGNATURE.length});
    }

    @Nullable
    public static String resolveMimeTypeFromStream(@NonNull InputStream is) throws IOException {
        byte[] signatureBytes = new byte[MAX_SIGNATURE_LENGTH];
        return resolveMimeTypeFromSignature(signatureBytes, readSignatureFromStream(is, signatureBytes));
    }

    @Nullable
    private static String resolveMimeTypeFromSignature(@NonNull byte[] signatureBytes, int signatureSize) {
        for (SignatureChecker checker : SIGNATURE_CHECKERS) {
            if (checker.check(signatureBytes, signatureSize)) {
                return checker.getMimeType(signatureBytes, signatureSize);
            }
        }
        return null;
    }

    private static int readSignatureFromStream(@NonNull InputStream is, byte[] signatureBuffer) throws IOException {
        if (!is.markSupported()) {
            return IOUtils.read(is, signatureBuffer, 0, signatureBuffer.length);
        }
        try {
            is.mark(signatureBuffer.length);
            int read = IOUtils.read(is, signatureBuffer, 0, signatureBuffer.length);
            return read;
        } finally {
            is.reset();
        }
    }
}
