package ru.ok.android.ui.dialogs;

import android.content.Context;
import ru.ok.model.wmf.Track;

public class ChangeTrackStateBase {
    protected Context context;
    protected OnChangeTrackStateListener listener;
    protected Track track;
    protected DialogType type;

    public interface OnChangeTrackStateListener {
        void onAddTrack(Track track);

        void onDeleteTrack(Track track);

        void onSetStatusTrack(Track track);
    }

    protected enum DialogType {
        DELETE,
        ADD,
        ONLY_STATUS
    }

    public ChangeTrackStateBase(Context context, DialogType type, Track track) {
        this.context = context;
        this.type = type;
        this.track = track;
    }

    public void setOnChangeTrackStateListener(OnChangeTrackStateListener listener) {
        this.listener = listener;
    }
}
