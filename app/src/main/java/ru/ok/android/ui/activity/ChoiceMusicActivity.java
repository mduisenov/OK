package ru.ok.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.fragments.music.TrackSelectionControl;
import ru.ok.android.fragments.music.TrackSelectionControl.TrackSelectionListener;
import ru.ok.android.fragments.music.TrackSelectionControlImpl;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.ui.custom.mediacomposer.MusicItem;
import ru.ok.android.ui.fragments.MusicUsersFragment;
import ru.ok.android.ui.fragments.StubFragment;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.NavigationHelper.FragmentLocation;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.Track;

public class ChoiceMusicActivity extends OdklSubActivity implements TrackSelectionControl, TrackSelectionListener {
    private MenuItem musicItem;
    private TrackSelectionControlImpl trackSelectionControl;

    public ChoiceMusicActivity() {
        this.musicItem = null;
    }

    public boolean canShowFragmentOnLocation(FragmentLocation fragmentLocation) {
        return true;
    }

    public boolean isNeedShowLeftMenu() {
        return false;
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        MusicItem musicItem = (MusicItem) getIntent().getParcelableExtra("music_item_key");
        this.trackSelectionControl = new TrackSelectionControlImpl(musicItem == null ? null : musicItem.getTracks());
        this.trackSelectionControl.addTrackSelectionListener(this);
        super.onCreateLocalized(savedInstanceState);
        showMusicHome();
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean ret = super.onPrepareOptionsMenu(menu);
        this.musicItem = menu.findItem(2131625442);
        onTrackSelectionChanged(null, false);
        return ret;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 2131625442) {
            return super.onOptionsItemSelected(item);
        }
        MusicItem musicItem = (MusicItem) getIntent().getParcelableExtra("music_item_key");
        if (musicItem == null) {
            musicItem = new MusicItem();
        }
        musicItem.getTracks().clear();
        List<Track> trackParcelables = new ArrayList();
        this.trackSelectionControl.getSelectedTracks(trackParcelables);
        musicItem.setTracks(trackParcelables);
        Intent intent = new Intent();
        intent.putExtra("music_item_key", musicItem);
        setResult(-1, intent);
        finish();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        LocalizationManager.inflate((Context) this, getMenuInflater(), 2131689480, menu);
        return true;
    }

    private void showMusicHome() {
        Bundle args = MusicUsersFragment.newArguments(false, null, MusicFragmentMode.MULTI_SELECTION);
        ActivityExecutor executor = new ActivityExecutor(this, MusicUsersFragment.class);
        executor.setFragmentLocation(FragmentLocation.left);
        if (DeviceUtils.getType(this) == DeviceLayoutType.LARGE) {
            showMusicStub();
        }
        showFragment(executor.setArguments(args).setAddToBackStack(false).setActivityResult(false));
    }

    private void showMusicStub() {
        Bundle stubArgs = StubFragment.newArguments(LocalizationManager.getString((Context) this, 2131166500), LocalizationManager.getString((Context) this, 2131166223));
        ActivityExecutor builder = new ActivityExecutor(this, StubFragment.class);
        builder.setArguments(stubArgs);
        builder.setFragmentLocation(FragmentLocation.right);
        builder.execute();
    }

    public void addTrackSelectionListener(TrackSelectionListener listener) {
        this.trackSelectionControl.addTrackSelectionListener(listener);
    }

    public void removeTrackSelectionListener(TrackSelectionListener listener) {
        this.trackSelectionControl.removeTrackSelectionListener(listener);
    }

    public void setTrackSelection(Track track, boolean isSelected) {
        this.trackSelectionControl.setTrackSelection(track, isSelected);
    }

    public Track[] getSelectedTracks() {
        return this.trackSelectionControl.getSelectedTracks();
    }

    public void onTrackSelectionChanged(Track track, boolean isSelected) {
        if (this.musicItem != null) {
            MenuItem menuItem = this.musicItem;
            boolean z = getSelectedTracks() != null && getSelectedTracks().length > 0;
            menuItem.setEnabled(z);
        }
    }
}
