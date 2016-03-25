package ru.ok.android.utils.localization;

import java.io.Closeable;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;

public final class LocalizationParser {
    static TranslationData parseLocalizationFile(InputStream is) throws Exception {
        if (is == null) {
            return null;
        }
        try {
            TranslationData translationData = (TranslationData) new ObjectInputStream(is).readObject();
            return translationData;
        } finally {
            IOUtils.closeSilently((Closeable) is);
        }
    }

    static void saveLocalizationFile(OutputStream os, TranslationData data) throws Exception {
        if (os == null) {
            Logger.m184w("OutputStream is null");
            return;
        }
        try {
            new ObjectOutputStream(os).writeObject(data);
        } finally {
            IOUtils.closeSilently((Closeable) os);
        }
    }
}
