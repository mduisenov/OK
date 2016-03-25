package ru.ok.android.http.message;

import ru.ok.android.http.FormattedHeader;
import ru.ok.android.http.Header;
import ru.ok.android.http.ProtocolVersion;
import ru.ok.android.http.RequestLine;
import ru.ok.android.http.StatusLine;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;

public class BasicLineFormatter implements LineFormatter {
    @Deprecated
    public static final BasicLineFormatter DEFAULT;
    public static final BasicLineFormatter INSTANCE;

    static {
        DEFAULT = new BasicLineFormatter();
        INSTANCE = new BasicLineFormatter();
    }

    protected CharArrayBuffer initBuffer(CharArrayBuffer charBuffer) {
        CharArrayBuffer buffer = charBuffer;
        if (buffer == null) {
            return new CharArrayBuffer(64);
        }
        buffer.clear();
        return buffer;
    }

    public CharArrayBuffer appendProtocolVersion(CharArrayBuffer buffer, ProtocolVersion version) {
        Args.notNull(version, "Protocol version");
        CharArrayBuffer result = buffer;
        int len = estimateProtocolVersionLen(version);
        if (result == null) {
            result = new CharArrayBuffer(len);
        } else {
            result.ensureCapacity(len);
        }
        result.append(version.getProtocol());
        result.append('/');
        result.append(Integer.toString(version.getMajor()));
        result.append('.');
        result.append(Integer.toString(version.getMinor()));
        return result;
    }

    protected int estimateProtocolVersionLen(ProtocolVersion version) {
        return version.getProtocol().length() + 4;
    }

    public CharArrayBuffer formatRequestLine(CharArrayBuffer buffer, RequestLine reqline) {
        Args.notNull(reqline, "Request line");
        CharArrayBuffer result = initBuffer(buffer);
        doFormatRequestLine(result, reqline);
        return result;
    }

    protected void doFormatRequestLine(CharArrayBuffer buffer, RequestLine reqline) {
        String method = reqline.getMethod();
        String uri = reqline.getUri();
        buffer.ensureCapacity((((method.length() + 1) + uri.length()) + 1) + estimateProtocolVersionLen(reqline.getProtocolVersion()));
        buffer.append(method);
        buffer.append(' ');
        buffer.append(uri);
        buffer.append(' ');
        appendProtocolVersion(buffer, reqline.getProtocolVersion());
    }

    public CharArrayBuffer formatStatusLine(CharArrayBuffer buffer, StatusLine statline) {
        Args.notNull(statline, "Status line");
        CharArrayBuffer result = initBuffer(buffer);
        doFormatStatusLine(result, statline);
        return result;
    }

    protected void doFormatStatusLine(CharArrayBuffer buffer, StatusLine statline) {
        int len = ((estimateProtocolVersionLen(statline.getProtocolVersion()) + 1) + 3) + 1;
        String reason = statline.getReasonPhrase();
        if (reason != null) {
            len += reason.length();
        }
        buffer.ensureCapacity(len);
        appendProtocolVersion(buffer, statline.getProtocolVersion());
        buffer.append(' ');
        buffer.append(Integer.toString(statline.getStatusCode()));
        buffer.append(' ');
        if (reason != null) {
            buffer.append(reason);
        }
    }

    public CharArrayBuffer formatHeader(CharArrayBuffer buffer, Header header) {
        Args.notNull(header, "Header");
        if (header instanceof FormattedHeader) {
            return ((FormattedHeader) header).getBuffer();
        }
        CharArrayBuffer result = initBuffer(buffer);
        doFormatHeader(result, header);
        return result;
    }

    protected void doFormatHeader(CharArrayBuffer buffer, Header header) {
        String name = header.getName();
        String value = header.getValue();
        int len = name.length() + 2;
        if (value != null) {
            len += value.length();
        }
        buffer.ensureCapacity(len);
        buffer.append(name);
        buffer.append(": ");
        if (value != null) {
            buffer.append(value);
        }
    }
}
