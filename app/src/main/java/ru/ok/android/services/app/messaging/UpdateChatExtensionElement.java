package ru.ok.android.services.app.messaging;

import java.io.IOException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class UpdateChatExtensionElement implements ExtensionElement {

    public static class Provider extends ExtensionElementProvider<UpdateChatExtensionElement> {
        public UpdateChatExtensionElement parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
            return new UpdateChatExtensionElement();
        }
    }

    public String getNamespace() {
        return "http://ok.ru/updatechat";
    }

    public String getElementName() {
        return "updatechat";
    }

    public CharSequence toXML() {
        return String.format("<%s />", new Object[]{"updatechat"});
    }
}
