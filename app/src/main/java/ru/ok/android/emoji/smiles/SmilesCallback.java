package ru.ok.android.emoji.smiles;

import android.graphics.drawable.Drawable;

public interface SmilesCallback {
    void executeRunnable(Runnable runnable, boolean z);

    Drawable getDrawableByUrl(String str, int i);

    String getTranslatedString(int i);

    void logEvent(String str, String... strArr);
}
