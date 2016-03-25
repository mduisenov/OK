package ru.ok.android.utils.localization;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Pair;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.response.translations.TranslationsResponse;

final class TranslationData implements Serializable {
    private static final Pattern quotesForPaddingPattern;
    private static final long serialVersionUID = 1;
    private Map<String, String[]> arrayTranslations;
    private String modified;
    private Map<String, String> stringTranslations;

    static {
        quotesForPaddingPattern = Pattern.compile("\"\\s(.*)\\s\"");
    }

    public TranslationData(String modified) {
        this.stringTranslations = new HashMap();
        this.arrayTranslations = new ArrayMap();
        this.modified = modified;
    }

    Map<String, String> getStringTranslations() {
        return this.stringTranslations;
    }

    Map<String, String[]> getArrayTranslations() {
        return this.arrayTranslations;
    }

    void inject(TranslationsResponse translations) {
        String arrayName;
        this.modified = translations.modified;
        Map<String, Map<Integer, String>> arrays = new HashMap();
        for (Pair<String, String> translation : translations.translations) {
            String key = ((String) translation.first).toLowerCase();
            if (!TextUtils.isEmpty(key)) {
                String value = translation.second;
                if (quotesForPaddingPattern.matcher(value).matches()) {
                    value = value.substring(1, value.length() - 1);
                }
                value = value.replaceAll("\\\\n", "\n").replaceAll("\\\\\"", "\"");
                String[] chunks = key.split("-");
                switch (chunks.length) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        this.stringTranslations.put(key, value);
                        break;
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        arrayName = chunks[0];
                        try {
                            int arrayIndex = Integer.parseInt(chunks[1]);
                            Map<Integer, String> arrayValues = (Map) arrays.get(arrayName);
                            if (arrayValues == null) {
                                arrayValues = new TreeMap();
                                arrays.put(arrayName, arrayValues);
                            }
                            arrayValues.put(Integer.valueOf(arrayIndex), value);
                            break;
                        } catch (NumberFormatException ex) {
                            Logger.m180e(ex, "Non-integer index: %s", chunks[1]);
                            break;
                        }
                    default:
                        Logger.m185w("Translation key has unexpected number of '-' chars: %s", key);
                        break;
                }
            }
            Logger.m184w("TranslationResponse has empty key!");
        }
        for (String arrayName2 : arrays.keySet()) {
            Collection<String> arrayValues2 = ((Map) arrays.get(arrayName2)).values();
            this.arrayTranslations.put(arrayName2, arrayValues2.toArray(new String[arrayValues2.size()]));
        }
    }

    private void writeObject(ObjectOutputStream os) throws IOException {
        os.writeUTF(this.modified);
        os.writeInt(this.stringTranslations.size());
        for (Entry<String, String> entry : this.stringTranslations.entrySet()) {
            os.writeUTF((String) entry.getKey());
            os.writeUTF((String) entry.getValue());
        }
        os.writeInt(this.arrayTranslations.size());
        for (Entry<String, String[]> entry2 : this.arrayTranslations.entrySet()) {
            os.writeUTF((String) entry2.getKey());
            String[] array = (String[]) entry2.getValue();
            os.writeInt(array.length);
            for (String s : array) {
                os.writeUTF(s);
            }
        }
    }

    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        int i;
        this.modified = is.readUTF();
        int strings = is.readInt();
        this.stringTranslations = new HashMap();
        for (i = 0; i < strings; i++) {
            this.stringTranslations.put(is.readUTF(), is.readUTF());
        }
        int arrays = is.readInt();
        this.arrayTranslations = new ArrayMap();
        for (i = 0; i < arrays; i++) {
            String key = is.readUTF();
            int arrayLength = is.readInt();
            String[] values = new String[arrayLength];
            for (int j = 0; j < arrayLength; j++) {
                values[j] = is.readUTF();
            }
            this.arrayTranslations.put(key, values);
        }
    }
}
