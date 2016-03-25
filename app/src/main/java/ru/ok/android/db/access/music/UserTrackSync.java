package ru.ok.android.db.access.music;

import android.content.Context;
import android.database.Cursor;
import android.util.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.model.wmf.Track;

public class UserTrackSync extends BaseTrackSync {
    private Context context;
    private String userId;

    public UserTrackSync(Context context, String userId) {
        this.userId = userId;
        this.context = context;
    }

    protected Map<Track, Integer> getSavedData() {
        String selectionTracks = "user_music.user_id = " + this.userId;
        List<String> projections = MusicStorageFacade.getProjectionForUserMusic();
        Cursor cursor = this.context.getContentResolver().query(OdklProvider.userTracksUri(), (String[]) projections.toArray(new String[projections.size()]), selectionTracks, null, "_index");
        HashMap<Track, Integer> map = new HashMap();
        while (cursor.moveToNext()) {
            try {
                map.put(MusicStorageFacade.cursor2Track(cursor), Integer.valueOf(cursor.getInt(cursor.getColumnIndex("user_music__index"))));
            } finally {
                cursor.close();
            }
        }
        return map;
    }

    protected void updateDB(Track track, int position) {
        this.addTracks.add(new Pair(track, Integer.valueOf(position)));
    }

    protected void syncDB(List<Track> deleteTracks, List<Pair<Track, Integer>> addTracks) {
        if (deleteTracks.size() > 0) {
            MusicStorageFacade.deleteUserTracks(this.context, this.userId, (Track[]) deleteTracks.toArray(new Track[deleteTracks.size()]));
        }
        if (addTracks.size() > 0) {
            MusicStorageFacade.insertUserMusicTracks(this.context, this.userId, addTracks);
        }
    }
}
