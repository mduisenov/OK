package ru.ok.android.utils.localization.processors;

import android.content.Context;
import ru.ok.android.utils.localization.LocalizationManager;

public abstract class ElementAttributeStringProcessor<T> implements ElementAttributeProcessor<T, String> {
    public String getResourceValueById(Context context, int resourceId) {
        return LocalizationManager.getString(context, resourceId);
    }
}
