package ru.ok.android.fragments.music.users;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import java.util.List;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.music.AddTracksFragment;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.ui.adapters.friends.BaseCursorRecyclerAdapter;
import ru.ok.android.ui.adapters.section.RecyclerSectionizer;
import ru.ok.android.ui.adapters.section.SimpleSectionRecyclerAdapter;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.model.wmf.HistoryTrack;

public class HistoryTracksFragment extends AddTracksFragment {
    protected Messenger mMessenger;
    private SimpleSectionRecyclerAdapter<BaseCursorRecyclerAdapter> sectionAdapter;

    /* renamed from: ru.ok.android.fragments.music.users.HistoryTracksFragment.1 */
    class C03271 extends Handler {
        C03271() {
        }

        public void handleMessage(Message msg) {
            if (HistoryTracksFragment.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    private class DateSectionizer implements RecyclerSectionizer<BaseCursorRecyclerAdapter> {
        private DateSectionizer() {
        }

        public String getSectionTitleForItem(BaseCursorRecyclerAdapter adapter, int index) {
            Cursor cursor = (Cursor) adapter.getItem(index);
            if (cursor.getColumnIndex("music_history_time") <= 0) {
                return "no";
            }
            return StringUtils.uppercaseFirst(DateFormatter.getFormatStringFromDateNoTime(HistoryTracksFragment.this.getContext(), cursor.getLong(cursor.getColumnIndex("music_history_time"))));
        }
    }

    public HistoryTracksFragment() {
        this.mMessenger = new Messenger(new C03271());
    }

    public static Bundle newArguments(MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    public static Fragment newInstance(MusicFragmentMode mode) {
        Fragment result = new HistoryTracksFragment();
        result.setArguments(newArguments(mode));
        return result;
    }

    public Adapter createWrapperAdapter(BaseCursorRecyclerAdapter adapter) {
        this.sectionAdapter = new SimpleSectionRecyclerAdapter(getContext(), adapter, 2130903430, C0263R.id.text, new DateSectionizer());
        return this.sectionAdapter;
    }

    protected MusicListType getType() {
        return MusicListType.HISTORY_MUSIC;
    }

    protected MusicFragmentMode getMode() {
        return (MusicFragmentMode) getArguments().getParcelable("music-fragment-mode");
    }

    protected void requestTracks() {
        Message msg = Message.obtain(null, 2131624056, 0, 0);
        msg.replyTo = this.mMessenger;
        GlobalBus.sendMessage(msg);
        showProgressStub();
    }

    protected int getListPosition4DataPosition(int dataPosition) {
        return this.sectionAdapter.getSectionsCountPriorDataPosition(dataPosition) + dataPosition;
    }

    public void postListViewInit(RecyclerView listView) {
        super.postListViewInit(listView);
        if (this.sectionAdapter != null) {
            this.sectionAdapter.finalInit();
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestTracks();
        getLoaderManager().initLoader(0, null, this);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case RECEIVED_VALUE:
                List<String> projections = MusicStorageFacade.getProjectionForHistory();
                return new CursorLoader(getActivity(), OdklProvider.musicHistoryUri(), (String[]) projections.toArray(new String[projections.size()]), null, null, "music_history.time DESC");
            default:
                return null;
        }
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case RECEIVED_VALUE:
                this.adapter.swapCursor(data);
                dbLoadCompleted();
            default:
        }
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 249:
                onWebLoadSuccess(Type.MUSIC_HISTORY_TRACKS, ((HistoryTrack[]) ((HistoryTrack[]) msg.obj)).length != 0);
                return false;
            case 250:
                onWebLoadError(msg.obj);
                return false;
            default:
                return true;
        }
    }
}
