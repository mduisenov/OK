package ru.ok.android.services.app.messaging;

import java.io.IOException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import ru.ok.android.utils.Logger;

public class MessageReadExtensionElement implements ExtensionElement {
    public final long timestamp;
    public final long userId;

    public static class Provider extends ExtensionElementProvider<MessageReadExtensionElement> {
        public MessageReadExtensionElement parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
            long userId = 0;
            long timestamp = 0;
            for (int i = 0; i < parser.getAttributeCount(); i++) {
                String currentAttribute = parser.getAttributeName(i);
                if (currentAttribute != null) {
                    if (currentAttribute.equals("userid")) {
                        try {
                            userId = Long.valueOf(parser.getAttributeValue(i)).longValue();
                        } catch (NumberFormatException e) {
                            Logger.m176e("Failed to parse userid in a custom messageread XMPP extension");
                            return null;
                        }
                    } else if (currentAttribute.equals("timestamp")) {
                        try {
                            timestamp = Long.valueOf(parser.getAttributeValue(i)).longValue();
                        } catch (NumberFormatException e2) {
                            Logger.m176e("Failed to parse timestamp in a custom messageread XMPP extension");
                            return null;
                        }
                    } else {
                        continue;
                    }
                }
            }
            if (timestamp != 0) {
                return new MessageReadExtensionElement(userId, timestamp);
            }
            return null;
        }
    }

    public String getNamespace() {
        return "http://ok.ru/messageread";
    }

    public String getElementName() {
        return "messageread";
    }

    public CharSequence toXML() {
        return String.format("<%s %s='%s' %s='%s'/>", new Object[]{"messageread", "userid", Long.valueOf(this.userId), "timestamp", Long.valueOf(this.timestamp)});
    }

    public MessageReadExtensionElement(long userId, long timestamp) {
        this.userId = userId;
        this.timestamp = timestamp;
    }
}
