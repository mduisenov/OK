package ru.ok.android.fragments.music;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.util.List;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.utils.controls.music.MusicMultiDeleteAddControl;
import ru.ok.android.utils.controls.music.MusicMultiDeleteAddControl.OnAddTrackListener;
import ru.ok.model.wmf.Track;

public abstract class AddTracksFragment extends BaseTracksFragment implements OnAddTrackListener {
    private MusicMultiDeleteAddControl multiDeleteAddControl;

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareAddControl();
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (inflateMenuLocalized(2131689472, menu)) {
            this.item = menu.findItem(2131625438);
        }
        return true;
    }

    private void prepareAddControl() {
        this.multiDeleteAddControl = new MusicMultiDeleteAddControl();
        this.multiDeleteAddControl.setAddTrackListener(this);
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case 2131625438:
                List<Track> list = getSelectionTracks();
                if (list.isEmpty()) {
                    TimeToast.show(getContext(), 2131166504, 0);
                } else {
                    this.multiDeleteAddControl.addTracks((Track[]) list.toArray(new Track[list.size()]));
                }
                return true;
            default:
                return false;
        }
    }

    public void onAddTracksSuccessful(Track[] tracks) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), getStringLocalized(2131165342), 0).show();
            hideSelectedMode();
        }
    }

    public void onAddTracksFailed() {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), getStringLocalized(2131165797), 0).show();
            hideSelectedMode();
        }
    }
}
