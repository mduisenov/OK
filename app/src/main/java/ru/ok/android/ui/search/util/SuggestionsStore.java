package ru.ok.android.ui.search.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import ru.ok.android.utils.settings.Settings;

public class SuggestionsStore {
    private static SuggestionsStore instance;
    private LinkedList<String> suggestions;

    public static SuggestionsStore getInstance(Context context) {
        if (instance == null) {
            instance = new SuggestionsStore(context);
        }
        return instance;
    }

    private SuggestionsStore(Context context) {
        this.suggestions = new LinkedList();
        loadFromPreferences(context);
    }

    private void loadFromPreferences(Context context) {
        String stored = Settings.getStrValueInvariable(context, "srcsggstns", null);
        if (!TextUtils.isEmpty(stored)) {
            for (String suggestion : stored.split(";")) {
                this.suggestions.add(URLDecoder.decode(suggestion));
            }
        }
    }

    private void storeToPreferences(Context context) {
        Editor editor = Settings.getEditorInvariable(context);
        if (this.suggestions.isEmpty()) {
            editor.remove("srcsggstns");
        } else {
            StringBuilder builder = new StringBuilder();
            Iterator i$ = this.suggestions.iterator();
            while (i$.hasNext()) {
                builder.append(URLEncoder.encode((String) i$.next())).append(";");
            }
            builder.setLength(builder.length() - 1);
        }
        Settings.commitEditor(editor);
    }

    public List<String> getSuggestions() {
        return this.suggestions;
    }

    public void addSuggestion(String suggestion) {
        if (!this.suggestions.remove(suggestion) && this.suggestions.size() == 5) {
            this.suggestions.removeFirst();
        }
        this.suggestions.addFirst(suggestion);
    }

    public void removeSuggestion(String suggestion) {
        this.suggestions.remove(suggestion);
    }

    public void save(Context context) {
        storeToPreferences(context);
    }

    public static void destroyInstance() {
        instance = null;
    }
}
