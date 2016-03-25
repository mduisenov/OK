package ru.ok.android.http.message;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import ru.ok.android.http.HeaderElement;
import ru.ok.android.http.NameValuePair;
import ru.ok.android.http.ParseException;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;

public class BasicHeaderValueParser implements HeaderValueParser {
    @Deprecated
    public static final BasicHeaderValueParser DEFAULT;
    public static final BasicHeaderValueParser INSTANCE;
    private static final BitSet TOKEN_DELIMS;
    private static final BitSet VALUE_DELIMS;
    private final TokenParser tokenParser;

    static {
        DEFAULT = new BasicHeaderValueParser();
        INSTANCE = new BasicHeaderValueParser();
        TOKEN_DELIMS = TokenParser.INIT_BITSET(new int[]{61, 59, 44});
        VALUE_DELIMS = TokenParser.INIT_BITSET(new int[]{59, 44});
    }

    public BasicHeaderValueParser() {
        this.tokenParser = TokenParser.INSTANCE;
    }

    public static HeaderElement[] parseElements(String value, HeaderValueParser parser) throws ParseException {
        Args.notNull(value, "Value");
        CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        ParserCursor cursor = new ParserCursor(0, value.length());
        if (parser == null) {
            parser = INSTANCE;
        }
        return parser.parseElements(buffer, cursor);
    }

    public HeaderElement[] parseElements(CharArrayBuffer buffer, ParserCursor cursor) {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        List<HeaderElement> elements = new ArrayList();
        while (!cursor.atEnd()) {
            HeaderElement element = parseHeaderElement(buffer, cursor);
            if (element.getName().length() != 0 || element.getValue() != null) {
                elements.add(element);
            }
        }
        return (HeaderElement[]) elements.toArray(new HeaderElement[elements.size()]);
    }

    public HeaderElement parseHeaderElement(CharArrayBuffer buffer, ParserCursor cursor) {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        NameValuePair nvp = parseNameValuePair(buffer, cursor);
        NameValuePair[] params = null;
        if (!(cursor.atEnd() || buffer.charAt(cursor.getPos() - 1) == ',')) {
            params = parseParameters(buffer, cursor);
        }
        return createHeaderElement(nvp.getName(), nvp.getValue(), params);
    }

    protected HeaderElement createHeaderElement(String name, String value, NameValuePair[] params) {
        return new BasicHeaderElement(name, value, params);
    }

    public NameValuePair[] parseParameters(CharArrayBuffer buffer, ParserCursor cursor) {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        this.tokenParser.skipWhiteSpace(buffer, cursor);
        List<NameValuePair> params = new ArrayList();
        while (!cursor.atEnd()) {
            params.add(parseNameValuePair(buffer, cursor));
            if (buffer.charAt(cursor.getPos() - 1) == ',') {
                break;
            }
        }
        return (NameValuePair[]) params.toArray(new NameValuePair[params.size()]);
    }

    public NameValuePair parseNameValuePair(CharArrayBuffer buffer, ParserCursor cursor) {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        String name = this.tokenParser.parseToken(buffer, cursor, TOKEN_DELIMS);
        if (cursor.atEnd()) {
            return new BasicNameValuePair(name, null);
        }
        int delim = buffer.charAt(cursor.getPos());
        cursor.updatePos(cursor.getPos() + 1);
        if (delim != 61) {
            return createNameValuePair(name, null);
        }
        String value = this.tokenParser.parseValue(buffer, cursor, VALUE_DELIMS);
        if (!cursor.atEnd()) {
            cursor.updatePos(cursor.getPos() + 1);
        }
        return createNameValuePair(name, value);
    }

    protected NameValuePair createNameValuePair(String name, String value) {
        return new BasicNameValuePair(name, value);
    }
}
