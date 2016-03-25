package ru.ok.android.fragments.music;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import ru.ok.android.fragments.music.TrackSelectionControl.TrackSelectionListener;
import ru.ok.android.utils.Logger;
import ru.ok.model.wmf.Track;

public class TrackSelectionControlImpl implements TrackSelectionControl {
    private final LinkedList<TrackSelectionListener> listeners;
    private final Map<Long, Track> selectedTracksById;

    public TrackSelectionControlImpl() {
        this(null);
    }

    public TrackSelectionControlImpl(Collection<? extends Track> selectedTracks) {
        this.listeners = new LinkedList();
        this.selectedTracksById = new LinkedHashMap();
        if (selectedTracks != null) {
            for (Track track : selectedTracks) {
                this.selectedTracksById.put(Long.valueOf(track.id), track);
            }
        }
    }

    public void addTrackSelectionListener(TrackSelectionListener listener) {
        if (this.listeners.contains(listener)) {
            Logger.m185w("Leaked TrackSelectionListener: %s", listener);
            return;
        }
        this.listeners.add(listener);
        Logger.m173d("Registered listener (total count=%d): %s", Integer.valueOf(this.listeners.size()), listener);
    }

    public void removeTrackSelectionListener(TrackSelectionListener listener) {
        if (this.listeners.remove(listener)) {
            Logger.m173d("Unregistered listener (total count=%d): %s", Integer.valueOf(this.listeners.size()), listener);
            return;
        }
        Logger.m184w("Listener not found: " + listener);
    }

    public void setTrackSelection(Track track, boolean isSelected) {
        Logger.m173d("track=%s isSelectionMode=%s", track, Boolean.valueOf(isSelected));
        if (track != null) {
            if (isSelected) {
                this.selectedTracksById.put(Long.valueOf(track.id), track);
            } else {
                this.selectedTracksById.remove(Long.valueOf(track.id));
            }
            notifyTrackSelectionChanged(track, isSelected);
        }
    }

    public void getSelectedTracks(Collection<Track> outTracks) {
        if (outTracks != null) {
            outTracks.addAll(this.selectedTracksById.values());
        }
    }

    public Track[] getSelectedTracks() {
        Collection<Track> tracks = this.selectedTracksById.values();
        return (Track[]) tracks.toArray(new Track[tracks.size()]);
    }

    protected void notifyTrackSelectionChanged(Track track, boolean isSelected) {
        Iterator i$ = this.listeners.iterator();
        while (i$.hasNext()) {
            ((TrackSelectionListener) i$.next()).onTrackSelectionChanged(track, isSelected);
        }
    }
}
