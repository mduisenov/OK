package ru.ok.android.utils.localization.finders;

import java.util.List;
import ru.ok.android.utils.localization.processors.ElementAttributeProcessor;

public class ElementTag<T> {
    private final List<ElementAttributeProcessor<? extends T, ?>> _attributes;
    private final String _idAttribute;
    private final String _tagName;

    public ElementTag(String tagName, String idAttribute, List<ElementAttributeProcessor<? extends T, ?>> attributes) {
        this._tagName = tagName;
        this._idAttribute = idAttribute;
        this._attributes = attributes;
    }

    public ElementTag(Class<? extends T> tagClass, List<ElementAttributeProcessor<? extends T, ?>> attributes) {
        this((Class) tagClass, "id", (List) attributes);
    }

    public ElementTag(Class<? extends T> tagClass, String idAttribute, List<ElementAttributeProcessor<? extends T, ?>> attributes) {
        this(tagClass.getSimpleName(), idAttribute, (List) attributes);
    }

    public String getTagName() {
        return this._tagName;
    }

    public String getIdAttribute() {
        return this._idAttribute;
    }

    public List<ElementAttributeProcessor<? extends T, ?>> getAttributes() {
        return this._attributes;
    }

    public void processWholeTag(T t) {
    }

    public String toString() {
        return "tag: " + this._tagName + ", id: " + this._idAttribute;
    }
}
