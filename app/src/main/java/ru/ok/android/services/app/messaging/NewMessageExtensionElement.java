package ru.ok.android.services.app.messaging;

import java.io.IOException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import ru.ok.android.utils.Logger;

public class NewMessageExtensionElement implements ExtensionElement {
    public final long timestamp;
    public final long userId;

    public static class Provider extends ExtensionElementProvider<NewMessageExtensionElement> {
        public NewMessageExtensionElement parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
            long userId = 0;
            long timestamp = 0;
            for (int i = 0; i < parser.getAttributeCount(); i++) {
                String attributeName = parser.getAttributeName(i);
                if (attributeName.equals("userid")) {
                    try {
                        userId = Long.valueOf(parser.getAttributeValue(i)).longValue();
                    } catch (NumberFormatException e) {
                        Logger.m176e("Failed to parse userid in a custom newmessage XMPP extension");
                        return null;
                    }
                } else if (attributeName.equals("timestamp")) {
                    try {
                        timestamp = Long.valueOf(parser.getAttributeValue(i)).longValue();
                    } catch (NumberFormatException e2) {
                        Logger.m176e("Failed to parse timestamp in a custom newmessage XMPP extension");
                        return null;
                    }
                } else {
                    continue;
                }
            }
            return new NewMessageExtensionElement(userId, timestamp);
        }
    }

    public String getNamespace() {
        return "http://ok.ru/newmessage";
    }

    public String getElementName() {
        return "newmessage";
    }

    public CharSequence toXML() {
        return String.format("<%s %s='%s' %s='%s'/>", new Object[]{"newmessage", "userid", Long.valueOf(this.userId), "timestamp", Long.valueOf(this.timestamp)});
    }

    public NewMessageExtensionElement(long userId, long timestamp) {
        this.userId = userId;
        this.timestamp = timestamp;
    }
}
