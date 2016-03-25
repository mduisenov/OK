package ru.ok.android.ui.custom.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import java.io.IOException;
import org.jivesoftware.smack.packet.Stanza;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import ru.ok.android.C0206R;
import ru.ok.android.proto.MessagesProto.Message;

public class ToolbarMenuInflater extends MenuInflater {
    private final Context context;

    public ToolbarMenuInflater(Context context) {
        super(context);
        this.context = context;
    }

    public void inflate(int menuRes, Menu menu) {
        super.inflate(menuRes, menu);
        if (menu instanceof ToolbarMenu) {
            inflateToolbarSpecificAttrs(menuRes, (ToolbarMenu) menu);
        }
    }

    protected void inflateToolbarSpecificAttrs(int menuRes, ToolbarMenu menu) {
        XmlResourceParser parser = null;
        try {
            parser = this.context.getResources().getLayout(menuRes);
            parseMenu(parser, Xml.asAttributeSet(parser), menu);
            if (parser != null) {
                parser.close();
            }
        } catch (XmlPullParserException e) {
            throw new InflateException("Error inflating menu XML", e);
        } catch (IOException e2) {
            throw new InflateException("Error inflating menu XML", e2);
        } catch (Throwable th) {
            if (parser != null) {
                parser.close();
            }
        }
    }

    private void parseMenu(XmlPullParser parser, AttributeSet attrs, ToolbarMenu menu) throws XmlPullParserException, IOException {
        int toolbarItemIndex = 0;
        ToolbarMenuItem currentItem = null;
        int eventType = parser.getEventType();
        boolean lookingForEndOfUnknownTag = false;
        String unknownTagName = null;
        while (eventType != 2) {
            eventType = parser.next();
            if (eventType == 1) {
                break;
            }
        }
        String tagName = parser.getName();
        if (tagName.equals("menu")) {
            eventType = parser.next();
            boolean reachedEndOfMenu = false;
            while (!reachedEndOfMenu) {
                switch (eventType) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        throw new RuntimeException("Unexpected end of document");
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        if (!lookingForEndOfUnknownTag) {
                            tagName = parser.getName();
                            if (!tagName.equals("group")) {
                                if (!tagName.equals(Stanza.ITEM)) {
                                    if (!tagName.equals("menu")) {
                                        lookingForEndOfUnknownTag = true;
                                        unknownTagName = tagName;
                                        break;
                                    }
                                    parseMenu(parser, attrs, currentItem != null ? currentItem.subMenu : null);
                                    break;
                                } else if (menu == null) {
                                    break;
                                } else {
                                    currentItem = menu.getItem(toolbarItemIndex);
                                    parseItem(attrs, currentItem);
                                    toolbarItemIndex++;
                                    break;
                                }
                            }
                            break;
                        }
                        break;
                    case Message.TYPE_FIELD_NUMBER /*3*/:
                        tagName = parser.getName();
                        if (!lookingForEndOfUnknownTag || !tagName.equals(unknownTagName)) {
                            if (!tagName.equals("menu")) {
                                break;
                            }
                            reachedEndOfMenu = true;
                            break;
                        }
                        lookingForEndOfUnknownTag = false;
                        unknownTagName = null;
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
            return;
        }
        throw new RuntimeException("Expecting menu, got " + tagName);
    }

    protected void parseItem(AttributeSet attrs, ToolbarMenuItem item) {
        TypedArray a = this.context.obtainStyledAttributes(attrs, C0206R.styleable.ToolbarMenuItem);
        item.align = a.getInt(1, 0);
        int actionLayoutId = a.getResourceId(0, 0);
        if (actionLayoutId != 0) {
            item.setActionView(actionLayoutId);
        }
        a.recycle();
    }
}
