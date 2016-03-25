package ru.ok.android.services.processors.stream;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.jivesoftware.smack.util.StringUtils;
import ru.ok.android.utils.IntegralToString;
import ru.ok.model.stream.Feed;

final class FeedDigestCalculator {
    private static AtomicReference<MessageDigestBuffer> mdBufferRef;

    static class MessageDigestBuffer {
        final byte[] buffer;
        final MessageDigest md;

        MessageDigestBuffer() {
            try {
                this.md = MessageDigest.getInstance(StringUtils.MD5);
                this.buffer = new byte[64];
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("MD5 digest algorithm not provided", e);
            }
        }
    }

    static void calculateFeedDigests(List<Feed> feeds) {
        MessageDigestBuffer mdBuffer = obtainMessageDigestBuffer();
        for (Feed feed : feeds) {
            calculateFeedDigest(feed, mdBuffer.md, mdBuffer.buffer);
        }
        releaseMessageDigestBuffer(mdBuffer);
    }

    private static void calculateFeedDigest(Feed feed, MessageDigest md, byte[] buffer) {
        md.reset();
        feed.digest(md, buffer);
        feed.digest = IntegralToString.bytesToHexString(md.digest(), false);
    }

    private static MessageDigestBuffer obtainMessageDigestBuffer() {
        MessageDigestBuffer mdBuffer = (MessageDigestBuffer) mdBufferRef.getAndSet(null);
        if (mdBuffer == null) {
            return new MessageDigestBuffer();
        }
        return mdBuffer;
    }

    private static void releaseMessageDigestBuffer(MessageDigestBuffer mdBuffer) {
        mdBufferRef.set(mdBuffer);
    }

    static {
        mdBufferRef = new AtomicReference();
    }
}
