package ru.ok.android.utils.localization;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.finders.ActivityViewByIdFinder;
import ru.ok.android.utils.localization.finders.ElementByIdFinder;
import ru.ok.android.utils.localization.finders.ElementTag;
import ru.ok.android.utils.localization.finders.FragmentViewByIdFinder;
import ru.ok.android.utils.localization.finders.MenuItemByIdFinder;
import ru.ok.android.utils.localization.finders.MenuSherlockItemByIdFinder;
import ru.ok.android.utils.localization.finders.PreferenceByIdFinder;
import ru.ok.android.utils.localization.finders.ViewViewByIdFinder;
import ru.ok.android.utils.localization.processors.ElementAttributeProcessor;

public class LocalizationViewWalker {
    public static void walkThroughLayout(int layoutId, Activity activity) {
        walkThroughLayout(activity, layoutId, new ActivityViewByIdFinder(activity));
    }

    public static void walkThroughLayout(int layoutId, Fragment fragment) {
        walkThroughLayout(fragment.getActivity(), layoutId, new FragmentViewByIdFinder(fragment));
    }

    public static void walkThroughLayout(int layoutId, View view) {
        walkThroughLayout(view.getContext(), layoutId, new ViewViewByIdFinder(view));
    }

    public static void walkThroughMenu(Context context, int resourceId, Menu menu) {
        walkThroughSherlockMenu(context, resourceId, new MenuSherlockItemByIdFinder(menu));
    }

    public static void walkThroughMenu(Context context, int resourceId, ContextMenu menu) {
        walkThroughMenu(context, resourceId, new MenuItemByIdFinder(menu));
    }

    public static void walkThroughPreferences(int preferencesId, PreferenceActivity activity) {
        XmlResourceParser parser;
        try {
            parser = activity.getResources().getXml(preferencesId);
            walkThroughXmlRec(activity, new PreferenceByIdFinder(activity), parser, Xml.asAttributeSet(parser));
            parser.close();
        } catch (Throwable e) {
            Logger.m179e(e, "Error processing preference with id " + preferencesId);
        } catch (Throwable th) {
            parser.close();
        }
    }

    private static void walkThroughLayout(Context context, int layoutId, ElementByIdFinder finder) {
        if (context == null) {
            Logger.m184w("Context is absent");
            return;
        }
        XmlResourceParser parser;
        try {
            parser = context.getResources().getLayout(layoutId);
            walkThroughXmlRec(context, finder, parser, Xml.asAttributeSet(parser));
            parser.close();
        } catch (Throwable e) {
            Logger.m179e(e, "Error processing layout with id " + layoutId);
        } catch (Throwable th) {
            parser.close();
        }
    }

    private static void walkThroughSherlockMenu(Context context, int menuId, ElementByIdFinder<MenuItem> finder) {
        XmlResourceParser parser;
        try {
            parser = context.getResources().getXml(menuId);
            walkThroughXmlRec(context, finder, parser, Xml.asAttributeSet(parser));
            parser.close();
        } catch (Throwable e) {
            Logger.m179e(e, "Error processing menu with id " + menuId);
        } catch (Throwable th) {
            parser.close();
        }
    }

    private static void walkThroughMenu(Context context, int menuId, ElementByIdFinder<MenuItem> finder) {
        XmlResourceParser parser;
        try {
            parser = context.getResources().getXml(menuId);
            walkThroughXmlRec(context, finder, parser, Xml.asAttributeSet(parser));
            parser.close();
        } catch (Throwable e) {
            Logger.m179e(e, "Error processing menu with id " + menuId);
        } catch (Throwable th) {
            parser.close();
        }
    }

    private static void walkThroughXmlRec(Context context, ElementByIdFinder finder, XmlResourceParser parser, AttributeSet attrs) throws IOException, XmlPullParserException {
        if (!processIncludeTagForLayout(context, finder, parser, attrs)) {
            processTag(context, finder, parser.getName(), attrs);
            int depth = parser.getDepth();
            while (true) {
                int type = parser.next();
                if ((type == 3 && parser.getDepth() <= depth) || type == 1) {
                    return;
                }
                if (type == 2) {
                    walkThroughXmlRec(context, finder, parser, attrs);
                }
            }
        }
    }

    private static boolean processIncludeTagForLayout(Context context, ElementByIdFinder finder, XmlResourceParser parser, AttributeSet attrs) throws IOException, XmlPullParserException {
        if (!"include".equals(parser.getName())) {
            return false;
        }
        String layout = attrs.getAttributeValue(null, "layout");
        if (TextUtils.isEmpty(layout)) {
            return false;
        }
        String[] chunks = layout.split("/");
        if (chunks == null || chunks.length < 2) {
            return false;
        }
        XmlResourceParser subParser = context.getResources().getLayout(context.getResources().getIdentifier(chunks[1], "layout", context.getPackageName()));
        walkThroughXmlRec(context, finder, subParser, Xml.asAttributeSet(subParser));
        return true;
    }

    private static void processTag(Context context, ElementByIdFinder<?> finder, String name, AttributeSet attrs) {
        ElementTag tag = findTag(finder, name);
        if (tag != null) {
            String id = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", tag.getIdAttribute());
            if (id == null) {
                Logger.m184w("Can't localize " + name + " tag cause id is absent");
                return;
            }
            try {
                Object view = finder.findElementById(convertId2Int(id));
                tag.processWholeTag(view);
                if (view != null && tag.getAttributes() != null) {
                    for (ElementAttributeProcessor processor : tag.getAttributes()) {
                        String text = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", processor.getAttributeName());
                        if (isTextLinkToResource(text)) {
                            processor.setAttributeValueForElement(view, processor.getResourceValueById(context, convertId2Int(text)));
                        }
                    }
                }
            } catch (NumberFormatException e) {
                Logger.m180e(e, "Tag with names'%s' not integer :(", name);
            }
        }
    }

    private static ElementTag findTag(ElementByIdFinder<?> finder, String name) {
        for (ElementTag tag : finder.getValidTags()) {
            if (tag.getTagName().equals(name)) {
                return tag;
            }
        }
        return null;
    }

    private static int convertId2Int(String id) {
        return Integer.parseInt(id.substring(1));
    }

    private static boolean isTextLinkToResource(String text) {
        return text != null && text.startsWith("@");
    }
}
