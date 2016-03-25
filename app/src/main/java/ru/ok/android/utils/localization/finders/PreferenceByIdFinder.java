package ru.ok.android.utils.localization.finders;

import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import com.google.android.gms.plus.PlusShare;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import ru.ok.android.utils.IntListPreference;
import ru.ok.android.utils.localization.processors.ElementAttributeProcessor;
import ru.ok.android.utils.localization.processors.ElementAttributeStringArrayProcessor;
import ru.ok.android.utils.localization.processors.ElementAttributeStringProcessor;

public final class PreferenceByIdFinder implements ElementByIdFinder<Preference> {
    private static final List<ElementAttributeProcessor<? extends Preference, ?>> ATTRIBUTES;
    private static final List<ElementTag<?>> PREFERENCES_TAGS;
    private final PreferenceActivity _activity;

    private static class EntriesAttributeProcessor extends ElementAttributeStringArrayProcessor<Preference> {
        private EntriesAttributeProcessor() {
        }

        public String getAttributeName() {
            return "entries";
        }

        public void setAttributeValueForElement(Preference element, String[] tagValue) {
            ((ListPreference) element).setEntries(tagValue);
        }
    }

    private static class SummaryAttributeProcessor extends ElementAttributeStringProcessor<Preference> {
        private SummaryAttributeProcessor() {
        }

        public String getAttributeName() {
            return "summary";
        }

        public void setAttributeValueForElement(Preference element, String tagValue) {
            element.setSummary(tagValue);
        }
    }

    private static class TitleAttributeProcessor extends ElementAttributeStringProcessor<Preference> {
        private TitleAttributeProcessor() {
        }

        public String getAttributeName() {
            return PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE;
        }

        public void setAttributeValueForElement(Preference element, String tagValue) {
            element.setTitle(tagValue);
            if (element instanceof DialogPreference) {
                ((DialogPreference) element).setDialogTitle(tagValue);
            }
        }
    }

    static {
        ATTRIBUTES = Arrays.asList(new ElementAttributeProcessor[]{new EntriesAttributeProcessor(), new TitleAttributeProcessor(), new SummaryAttributeProcessor()});
        PREFERENCES_TAGS = Arrays.asList(new ElementTag[]{new ElementTag(ListPreference.class, "key", ATTRIBUTES), new ElementTag(Preference.class, "key", ATTRIBUTES), new ElementTag(CheckBoxPreference.class, "key", ATTRIBUTES), new ElementTag(PreferenceCategory.class, "key", ATTRIBUTES), new ElementTag(IntListPreference.class.getName(), "key", ATTRIBUTES)});
    }

    public PreferenceByIdFinder(PreferenceActivity activity) {
        this._activity = activity;
    }

    public Preference findElementById(int resourceId) {
        return this._activity.findPreference(this._activity.getResources().getString(resourceId));
    }

    public Collection<ElementTag<?>> getValidTags() {
        return PREFERENCES_TAGS;
    }
}
