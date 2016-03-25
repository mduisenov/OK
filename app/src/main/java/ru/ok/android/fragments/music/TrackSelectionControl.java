package ru.ok.android.fragments.music;

import ru.ok.model.wmf.Track;

public interface TrackSelectionControl {

    public interface TrackSelectionListener {
        void onTrackSelectionChanged(Track track, boolean z);
    }

    void addTrackSelectionListener(TrackSelectionListener trackSelectionListener);

    Track[] getSelectedTracks();

    void removeTrackSelectionListener(TrackSelectionListener trackSelectionListener);

    void setTrackSelection(Track track, boolean z);
}
