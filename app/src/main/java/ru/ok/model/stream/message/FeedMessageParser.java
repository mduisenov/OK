package ru.ok.model.stream.message;

import org.jivesoftware.smack.packet.Stanza;
import org.json.JSONArray;
import org.json.JSONObject;

public final class FeedMessageParser {

    public interface FeedMessageParserCallback {
        void addApp(String str, String str2, String str3);

        void addGroup(String str, String str2, String str3);

        void addPlaylist(String str, String str2, String str3);

        void addText(String str);

        void addUser(String str, String str2, String str3);

        void addUserAlbum(String str, String str2, String str3);
    }

    public static void parseFeedMessage(JSONArray message, FeedMessageParserCallback callback) {
        if (message != null) {
            int size = message.length();
            for (int i = 0; i < size; i++) {
                JSONObject jsonToken = message.optJSONObject(i);
                if (jsonToken != null) {
                    parseToken(jsonToken.isNull("ref") ? null : jsonToken.optString("ref"), jsonToken.isNull(Stanza.TEXT) ? null : jsonToken.optString(Stanza.TEXT), callback);
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void parseToken(java.lang.String r6, java.lang.String r7, ru.ok.model.stream.message.FeedMessageParser.FeedMessageParserCallback r8) {
        /*
        r3 = 0;
        r4 = -1;
        if (r6 == 0) goto L_0x0021;
    L_0x0004:
        r5 = 58;
        r1 = r6.indexOf(r5);
        if (r1 == r4) goto L_0x0021;
    L_0x000c:
        r2 = r6.substring(r3, r1);
        r5 = r1 + 1;
        r0 = r6.substring(r5);
        r5 = r2.hashCode();
        switch(r5) {
            case -1581392212: goto L_0x0052;
            case 96801: goto L_0x0047;
            case 3599307: goto L_0x0027;
            case 98629247: goto L_0x0031;
            case 1917946107: goto L_0x003c;
            default: goto L_0x001d;
        };
    L_0x001d:
        r3 = r4;
    L_0x001e:
        switch(r3) {
            case 0: goto L_0x005d;
            case 1: goto L_0x0061;
            case 2: goto L_0x0065;
            case 3: goto L_0x0069;
            case 4: goto L_0x006d;
            default: goto L_0x0021;
        };
    L_0x0021:
        if (r7 == 0) goto L_0x0026;
    L_0x0023:
        r8.addText(r7);
    L_0x0026:
        return;
    L_0x0027:
        r5 = "user";
        r5 = r2.equals(r5);
        if (r5 == 0) goto L_0x001d;
    L_0x0030:
        goto L_0x001e;
    L_0x0031:
        r3 = "group";
        r3 = r2.equals(r3);
        if (r3 == 0) goto L_0x001d;
    L_0x003a:
        r3 = 1;
        goto L_0x001e;
    L_0x003c:
        r3 = "user_album";
        r3 = r2.equals(r3);
        if (r3 == 0) goto L_0x001d;
    L_0x0045:
        r3 = 2;
        goto L_0x001e;
    L_0x0047:
        r3 = "app";
        r3 = r2.equals(r3);
        if (r3 == 0) goto L_0x001d;
    L_0x0050:
        r3 = 3;
        goto L_0x001e;
    L_0x0052:
        r3 = "music_playlist";
        r3 = r2.equals(r3);
        if (r3 == 0) goto L_0x001d;
    L_0x005b:
        r3 = 4;
        goto L_0x001e;
    L_0x005d:
        r8.addUser(r7, r0, r6);
        goto L_0x0026;
    L_0x0061:
        r8.addGroup(r7, r0, r6);
        goto L_0x0026;
    L_0x0065:
        r8.addUserAlbum(r7, r0, r6);
        goto L_0x0026;
    L_0x0069:
        r8.addApp(r7, r0, r6);
        goto L_0x0026;
    L_0x006d:
        r8.addPlaylist(r7, r0, r6);
        goto L_0x0026;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.model.stream.message.FeedMessageParser.parseToken(java.lang.String, java.lang.String, ru.ok.model.stream.message.FeedMessageParser$FeedMessageParserCallback):void");
    }
}
