package ru.ok.android.utils.localization.finders;

import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.plus.PlusShare;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.jivesoftware.smack.packet.Stanza;
import ru.ok.android.utils.localization.processors.ElementAttributeProcessor;
import ru.ok.android.utils.localization.processors.ElementAttributeStringProcessor;

public final class MenuItemByIdFinder implements ElementByIdFinder<MenuItem> {
    private static final List<ElementAttributeProcessor<? extends MenuItem, ?>> ATTRIBUTES;
    private static final List<ElementTag<?>> MENU_TAGS;
    private final Menu menu;

    private static class TitleAttributeProcessor extends ElementAttributeStringProcessor<MenuItem> {
        private TitleAttributeProcessor() {
        }

        public String getAttributeName() {
            return PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE;
        }

        public void setAttributeValueForElement(MenuItem element, String tagValue) {
            element.setTitle(tagValue);
        }
    }

    static {
        ATTRIBUTES = Arrays.asList(new ElementAttributeProcessor[]{new TitleAttributeProcessor()});
        MENU_TAGS = Arrays.asList(new ElementTag[]{new ElementTag(Stanza.ITEM, "id", ATTRIBUTES)});
    }

    public MenuItemByIdFinder(Menu menu) {
        this.menu = menu;
    }

    public MenuItem findElementById(int itemId) {
        return this.menu.findItem(itemId);
    }

    public Collection<ElementTag<?>> getValidTags() {
        return MENU_TAGS;
    }
}
