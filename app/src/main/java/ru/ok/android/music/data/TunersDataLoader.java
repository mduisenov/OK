package ru.ok.android.music.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.GeneralDataLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.Tuner;

public class TunersDataLoader extends GeneralDataLoader<List<Tuner>> {
    public TunersDataLoader(Context context) {
        super(context);
    }

    protected List<Tuner> loadData() {
        List<Tuner> list = queryTuners();
        loadArtists(createTunersMap(list));
        return list;
    }

    private List<Tuner> queryTuners() {
        Cursor cursor = getContext().getContentResolver().query(OdklProvider.tunersUri(), null, null, null, "_id");
        try {
            List<Tuner> tuners = new ArrayList();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String data = cursor.getString(cursor.getColumnIndex("data"));
                ArrayList<Artist> artists = new ArrayList();
                TunerUtils.getArtists(getContext(), data);
                tuners.add(new Tuner(id, name, data, artists));
            }
            return tuners;
        } finally {
            cursor.close();
        }
    }

    private Map<String, Tuner> createTunersMap(List<Tuner> tuners) {
        Map<String, Tuner> map = new HashMap();
        for (Tuner tuner : tuners) {
            map.put(tuner.data, tuner);
        }
        return map;
    }

    private void loadArtists(Map<String, Tuner> map) {
        List<String> projections = MusicStorageFacade.getProjectionForTunerArtists();
        Cursor cursor = getContext().getContentResolver().query(OdklProvider.tunersArtistsUri(), (String[]) projections.toArray(new String[projections.size()]), null, null, null);
        while (cursor.moveToNext()) {
            try {
                Artist artist = MusicStorageFacade.cursor2Artist(cursor);
                Tuner tuner = (Tuner) map.get(cursor.getString(cursor.getColumnIndex("tuner2artist_tuner_data")));
                if (!(tuner == null || artist == null)) {
                    tuner.artists.add(artist);
                }
            } finally {
                cursor.close();
            }
        }
    }

    protected List<Uri> observableUris(List<Tuner> list) {
        return Arrays.asList(new Uri[]{OdklProvider.tunersUri(), OdklProvider.tunersArtistsUri()});
    }
}
