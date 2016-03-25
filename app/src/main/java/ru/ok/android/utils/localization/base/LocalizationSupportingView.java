package ru.ok.android.utils.localization.base;

import android.content.Context;

public interface LocalizationSupportingView {
    Context getContext();

    void onLocalizationChanged();
}
