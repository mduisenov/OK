package ru.ok.android.music.data;

import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.model.wmf.Artist;

public class TunerUtils {
    public static ArrayList<Artist> getArtists(Context context, String data) {
        List<String> projections = MusicStorageFacade.getProjectionForTunerArtists();
        String[] selectionArgs = new String[]{data};
        Cursor cursor = context.getContentResolver().query(OdklProvider.tunersArtistsUri(), (String[]) projections.toArray(new String[projections.size()]), "tuner2artist.tuner_data = ?", selectionArgs, null);
        try {
            ArrayList<Artist> artists = new ArrayList(cursor.getCount());
            while (cursor.moveToNext()) {
                Artist artist = MusicStorageFacade.cursor2Artist(cursor);
                if (artist != null) {
                    artists.add(artist);
                }
            }
            return artists;
        } finally {
            cursor.close();
        }
    }
}
