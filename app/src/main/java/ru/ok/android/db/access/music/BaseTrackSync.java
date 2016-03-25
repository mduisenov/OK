package ru.ok.android.db.access.music;

import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import ru.ok.model.wmf.Track;

public abstract class BaseTrackSync {
    protected List<Pair<Track, Integer>> addTracks;
    protected List<Track> deleteTracks;

    protected abstract Map<Track, Integer> getSavedData();

    protected abstract void syncDB(List<Track> list, List<Pair<Track, Integer>> list2);

    protected abstract void updateDB(Track track, int i);

    public BaseTrackSync() {
        this.addTracks = new ArrayList();
        this.deleteTracks = new ArrayList();
    }

    protected void prepareData(Track[] tracks, Map<Track, Integer> map) {
        for (int i = 0; i < tracks.length; i++) {
            Track track = tracks[i];
            if (map.containsKey(track)) {
                if (((Integer) map.get(track)).intValue() != (tracks.length - i) - 1) {
                    updateDB(track, (tracks.length - i) - 1);
                }
                map.remove(track);
            } else {
                this.addTracks.add(new Pair(track, Integer.valueOf((tracks.length - i) - 1)));
            }
        }
        for (Entry<Track, Integer> entry : map.entrySet()) {
            this.deleteTracks.add(entry.getKey());
        }
    }

    public void syncData(Track[] tracks) {
        prepareData(tracks, getSavedData());
        syncDB(this.deleteTracks, this.addTracks);
    }
}
