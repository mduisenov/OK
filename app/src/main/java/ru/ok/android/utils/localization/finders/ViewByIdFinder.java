package ru.ok.android.utils.localization.finders;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.jivesoftware.smack.packet.Stanza;
import ru.ok.android.utils.localization.processors.ElementAttributeProcessor;
import ru.ok.android.utils.localization.processors.ElementAttributeStringProcessor;

abstract class ViewByIdFinder implements ElementByIdFinder<View> {
    private static final List<ElementAttributeProcessor<? extends TextView, ?>> ATTRIBUTES;
    private static final List<ElementTag<?>> VIEWS_TAGS;

    static final class HintAttributeProcessor extends ElementAttributeStringProcessor<TextView> {
        HintAttributeProcessor() {
        }

        public String getAttributeName() {
            return "hint";
        }

        public void setAttributeValueForElement(TextView view, String tagValue) {
            view.setHint(tagValue);
        }
    }

    static final class TextAttributeProcessor extends ElementAttributeStringProcessor<TextView> {
        TextAttributeProcessor() {
        }

        public String getAttributeName() {
            return Stanza.TEXT;
        }

        public void setAttributeValueForElement(TextView view, String tagValue) {
            view.setText(tagValue);
        }
    }

    ViewByIdFinder() {
    }

    static {
        ATTRIBUTES = Arrays.asList(new ElementAttributeProcessor[]{new TextAttributeProcessor(), new HintAttributeProcessor()});
        VIEWS_TAGS = Arrays.asList(new ElementTag[]{new ElementTag(TextView.class, ATTRIBUTES), new ElementTag(AutoCompleteTextView.class, ATTRIBUTES), new ElementTag(EditText.class, ATTRIBUTES), new ElementTag(Button.class, ATTRIBUTES), new ElementTag(RadioButton.class, ATTRIBUTES), new ElementTag(CheckBox.class, ATTRIBUTES), new ElementTag(Switch.class, ATTRIBUTES), new ElementTag("ru.ok.android.widget.SearchInput", "id", ATTRIBUTES), new ElementTag("ru.ok.android.widget.JellyBeanSpanFixTextView", "id", ATTRIBUTES)});
    }

    public Collection<ElementTag<?>> getValidTags() {
        return VIEWS_TAGS;
    }
}
