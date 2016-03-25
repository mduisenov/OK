package ru.ok.android.ui.adapters.friends;

import android.content.Context;

public final class UserMusicHeaderAdapter extends HeaderMusicAdapter {
    public UserMusicHeaderAdapter(Context context, int textRes, int imageRes, boolean showDivider) {
        super(context, textRes, imageRes, showDivider);
    }

    protected void initViewHandler(ViewHandler handler) {
        super.initViewHandler(handler);
        handler.image.setImageResource(2130838134);
    }
}
