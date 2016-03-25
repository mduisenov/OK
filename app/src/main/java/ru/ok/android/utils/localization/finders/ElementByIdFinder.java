package ru.ok.android.utils.localization.finders;

import java.util.Collection;

public interface ElementByIdFinder<T> {
    T findElementById(int i);

    Collection<ElementTag<?>> getValidTags();
}
