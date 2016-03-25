package ru.ok.android.ui.fragments.handlers;

import android.content.Context;
import android.widget.ListAdapter;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.ui.adapters.music.playlist.PlayListAdapter;

public class MusicPlayListHandler extends BaseMusicPlayListHandler {
    public MusicPlayListHandler(MusicFragmentMode mode, Context context) {
        super(mode, context);
    }

    protected ListAdapter createWrapperAdapter(PlayListAdapter dataAdapter) {
        return dataAdapter;
    }
}
