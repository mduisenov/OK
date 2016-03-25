package ru.ok.android.utils.localization;

import android.content.Context;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import ru.ok.android.storage.StorageHelper;
import ru.ok.android.utils.Logger;

public final class LocalizationStorage {
    static synchronized TranslationData loadLocalizationFile(Context context, String locale) {
        TranslationData translationData = null;
        synchronized (LocalizationStorage.class) {
            try {
                translationData = LocalizationParser.parseLocalizationFile(new FileInputStream(StorageHelper.getFileByName(context, buildLocaleFileName(locale))));
            } catch (FileNotFoundException e) {
                Logger.m173d("No localization file for locale '%s'", locale);
            } catch (Throwable e2) {
                Logger.m179e(e2, "Failed to load localization file, remove it");
                removeLocaleFile(context, locale);
            }
        }
        return translationData;
    }

    static synchronized boolean saveLocalizationFile(Context context, String locale, TranslationData translation) {
        boolean z = true;
        synchronized (LocalizationStorage.class) {
            try {
                LocalizationParser.saveLocalizationFile(new FileOutputStream(StorageHelper.getFileByName(context, buildLocaleFileName(locale))), translation);
            } catch (Exception e) {
                Logger.m180e(e, "Failed to save file for locale '%s', data: %s", locale, translation);
                z = false;
            }
        }
        return z;
    }

    static synchronized boolean removeLocaleFile(Context context, String localeName) {
        boolean removeFile;
        synchronized (LocalizationStorage.class) {
            removeFile = StorageHelper.removeFile(context, buildLocaleFileName(localeName));
        }
        return removeFile;
    }

    private static String buildLocaleFileName(String locale) {
        return "translation-" + locale + ".bin";
    }
}
