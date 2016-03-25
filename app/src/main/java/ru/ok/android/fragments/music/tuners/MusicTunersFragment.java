package ru.ok.android.fragments.music.tuners;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.C0206R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.music.MusicPlayerInActionBarFragmentWithStub;
import ru.ok.android.music.data.TunersDataLoader;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.services.app.MusicService.InformationState;
import ru.ok.android.ui.adapters.music.tuners.MusicTunersAdapter;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController.OnItemClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.utils.EmptyViewRecyclerDataObserver;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.HideKeyboardRecyclerScrollHelper;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.controls.music.MusicControlUtils;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;
import ru.ok.model.wmf.Track;
import ru.ok.model.wmf.Tuner;

public class MusicTunersFragment extends MusicPlayerInActionBarFragmentWithStub implements LoaderCallbacks<List<Tuner>>, OnItemClickListener, OnStubButtonClickListener {
    private static Tuner currentTuner;
    private MusicTunersAdapter adapter;
    private ViewGroup mMainView;
    protected Messenger mMessenger;
    private LinearLayoutManager recyclerLayoutManager;
    private RecyclerView tunersListView;

    /* renamed from: ru.ok.android.fragments.music.tuners.MusicTunersFragment.1 */
    class C03241 extends Handler {
        C03241() {
        }

        public void handleMessage(Message msg) {
            if (MusicTunersFragment.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public MusicTunersFragment() {
        this.mMessenger = new Messenger(new C03241());
    }

    protected String getTitle() {
        String title = getStringLocalized(2131166738);
        return currentTuner == null ? title : title + " " + currentTuner.getFriendlyName();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LocalizationManager.from(getActivity());
        this.mMainView = (ViewGroup) LocalizationManager.inflate(getActivity(), 2130903547, container, false);
        this.tunersListView = (RecyclerView) this.mMainView.findViewById(2131625409);
        this.recyclerLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.tunersListView.setLayoutManager(this.recyclerLayoutManager);
        this.tunersListView.addOnScrollListener(new HideKeyboardRecyclerScrollHelper(getContext(), this.mMainView));
        this.emptyView = (SmartEmptyViewAnimated) this.mMainView.findViewById(C0263R.id.empty_view);
        this.emptyView.setButtonClickListener(this);
        this.adapter = new MusicTunersAdapter(getContext());
        this.adapter.getItemClickListenerController().addItemClickListener(this);
        this.adapter.registerAdapterDataObserver(new EmptyViewRecyclerDataObserver(this.emptyView, this.adapter));
        this.tunersListView.setAdapter(this.adapter);
        getTuners();
        getLoaderManager().initLoader(0, null, this);
        return this.mMainView;
    }

    protected int getLayoutId() {
        return 2130903547;
    }

    public void onItemClick(View view, int position) {
        currentTuner = (Tuner) this.adapter.getData().get(position);
        if (currentTuner != null && getActivity() != null) {
            this.tunersListView.setEnabled(false);
            getTunerTracks(currentTuner.data);
            getActivity().setProgressBarIndeterminateVisibility(true);
        }
    }

    private void getTuners() {
        if (Settings.getPlayListType(getContext(), MusicListType.NONE) != MusicListType.TUNER) {
            this.emptyView.setState(State.LOADING);
            Message msg = Message.obtain(null, 2131624072, 0, 0);
            msg.replyTo = this.mMessenger;
            GlobalBus.sendMessage(msg);
        }
    }

    public void onMediaPlayerState(BusEvent event) {
        super.onMediaPlayerState(event);
        if (Settings.getPlayListType(getContext(), MusicListType.NONE) != MusicListType.TUNER) {
            currentTuner = null;
            changePlayState(false);
            return;
        }
        InformationState musicState = MusicService.getInformationState(event);
        if (musicState == InformationState.ERROR || musicState == InformationState.STOP || musicState == InformationState.PAUSE) {
            changePlayState(false);
        } else if (currentTuner != null && currentTuner.id != this.adapter.getCurrentTunerId()) {
            changePlayState(true);
        }
    }

    private void changePlayState(boolean show) {
        MusicTunersAdapter musicTunersAdapter = this.adapter;
        int i = (currentTuner == null || !show) ? -1 : currentTuner.id;
        musicTunersAdapter.setCurrentTunerId(i);
        updateActionBarState();
    }

    private void getTunerTracks(String data) {
        Message msg = Message.obtain(null, 2131624073, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString("tuner_data", data);
        msg.setData(bundle);
        msg.replyTo = this.mMessenger;
        GlobalBus.sendMessage(msg);
    }

    public boolean onHandleMessage(Message msg) {
        FragmentActivity activity = getActivity();
        switch (msg.what) {
            case C0206R.styleable.Theme_ratingBarStyle /*105*/:
                MusicControlUtils.onError(getContext(), msg);
                onWebLoadError(msg.obj);
                return false;
            case C0206R.styleable.Theme_spinnerStyle /*106*/:
                onWebLoadSuccess(Type.MUSIC_TUNERS, ((Tuner[]) ((Tuner[]) msg.obj)).length != 0);
                return false;
            case AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR /*108*/:
                if (currentTuner == null) {
                    return false;
                }
                MusicService.startPlayMusic(OdnoklassnikiApplication.getContext(), 0, new ArrayList(Arrays.asList((Track[]) msg.obj)), MusicListType.TUNER);
                this.tunersListView.setEnabled(true);
                if (activity == null) {
                    return false;
                }
                if (!DeviceUtils.isSmall(getContext())) {
                    NavigationHelper.showMusicPlayer(activity);
                }
                activity.setProgressBarIndeterminateVisibility(false);
                return false;
            case AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY /*109*/:
                MusicControlUtils.onError(getContext(), msg);
                if (activity != null) {
                    activity.setProgressBarIndeterminateVisibility(false);
                }
                this.tunersListView.setEnabled(false);
                return false;
            default:
                return true;
        }
    }

    public Loader<List<Tuner>> onCreateLoader(int i, Bundle bundle) {
        return new TunersDataLoader(getContext());
    }

    public void onLoadFinished(Loader<List<Tuner>> loader, List<Tuner> tuners) {
        Logger.m173d("tuners count : %d", Integer.valueOf(tuners.size()));
        this.adapter.setData(tuners);
        this.adapter.notifyDataSetChanged();
        dbLoadCompleted();
    }

    public void onLoaderReset(Loader<List<Tuner>> loader) {
        Logger.m172d("tuners count : reset");
        this.adapter.setData(null);
    }

    public void onStubButtonClick(Type type) {
        getTuners();
    }
}
