package ru.ok.android.ui.stream.data;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import ru.ok.android.utils.SharedPreferenceFormatException;
import ru.ok.model.stream.StreamPageKey;

final class StreamListPositionPrefsHelper {
    static void toSharedPrefs(@NonNull StreamListPosition slp, Editor editor, String prefix) {
        editor.putString(prefix + ":page_key", slp.pageKey.getKey());
        editor.putInt(prefix + ":page_number", slp.pageKey.getPageNumber());
        editor.putLong(prefix + ":item_id", slp.itemId);
        editor.putInt(prefix + ":view_top", slp.viewTop);
        editor.putInt(prefix + ":adapter_position", slp.adapterPosition);
    }

    @Nullable
    static StreamListPosition fromSharedPrefs(SharedPreferences prefs, String prefix) throws SharedPreferenceFormatException {
        try {
            String pageKeyStr = prefs.getString(prefix + ":page_key", null);
            int pageNumber = prefs.getInt(prefix + ":page_number", -1);
            long itemId = prefs.getLong(prefix + ":item_id", -1);
            int viewTop = prefs.getInt(prefix + ":view_top", LinearLayoutManager.INVALID_OFFSET);
            int adapterPosition = prefs.getInt(prefix + ":adapter_position", -1);
            if (pageKeyStr == null && itemId == -1 && viewTop == LinearLayoutManager.INVALID_OFFSET && adapterPosition == -1) {
                return null;
            }
            if (pageKeyStr == null || itemId == -1 || viewTop == LinearLayoutManager.INVALID_OFFSET) {
                throw new SharedPreferenceFormatException("Missing values for stream list location: pageKey=" + pageKeyStr + " itemId=" + itemId + " viewTop=" + viewTop);
            }
            if (adapterPosition == -1) {
                adapterPosition = 0;
            }
            try {
                return new StreamListPosition(StreamPageKey.fromKeyAndPageNumber(pageKeyStr, pageNumber), itemId, viewTop, adapterPosition);
            } catch (Exception e) {
                throw new SharedPreferenceFormatException("Failed to parse StreamPageKey", e);
            }
        } catch (ClassCastException e2) {
            throw new SharedPreferenceFormatException("Failed to parse stream list position from prefs: " + e2, e2);
        }
    }

    static void cleanFromPrefs(Editor editor, String prefix) {
        editor.remove(prefix + ":page_key");
        editor.remove(prefix + ":page_number");
        editor.remove(prefix + ":item_id");
        editor.remove(prefix + ":view_top");
        editor.remove(prefix + ":adapter_position");
    }
}
