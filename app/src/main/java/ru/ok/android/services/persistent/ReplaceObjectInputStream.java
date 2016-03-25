package ru.ok.android.services.persistent;

import android.util.Pair;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.List;

public class ReplaceObjectInputStream extends ObjectInputStream {
    private static final List<Pair<String, String>> replacements;
    private ClassLoader cl;

    static {
        replacements = new ArrayList();
    }

    public ReplaceObjectInputStream(InputStream in, ClassLoader cl) throws IOException {
        super(in);
        this.cl = cl;
        replacements.add(new Pair("ru.ok.java.api.response.messages.Attachment", "ru.ok.model.messages.Attachment"));
    }

    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        for (Pair<String, String> replace : replacements) {
            if (name.equals(replace.first)) {
                return Class.forName((String) replace.second, false, this.cl);
            }
        }
        return super.resolveClass(desc);
    }
}
