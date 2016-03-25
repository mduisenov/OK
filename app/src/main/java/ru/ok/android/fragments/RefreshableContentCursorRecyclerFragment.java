package ru.ok.android.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import ru.ok.android.ui.adapters.ImageBlockerRecyclerProvider;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.users.CursorSwapper;
import ru.ok.android.utils.Logger;

public abstract class RefreshableContentCursorRecyclerFragment<TAdapter extends Adapter & CursorSwapper & ImageBlockerRecyclerProvider> extends RefreshableContentRecyclerFragment<TAdapter, Cursor> {
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.emptyView != null) {
            this.emptyView.setState(State.LOADING);
        }
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int i = 0;
        String str = "[%s] count=%d";
        Object[] objArr = new Object[2];
        objArr[0] = getClass().getSimpleName();
        if (cursor != null) {
            i = cursor.getCount();
        }
        objArr[1] = Integer.valueOf(i);
        Logger.m173d(str, objArr);
        if (this.adapter != null) {
            ((CursorSwapper) this.adapter).swapCursor(cursor);
        }
        this.emptyView.setState(State.LOADED);
        onContentChanged();
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        Logger.m173d("[%s]", getClass().getSimpleName());
        if (this.adapter != null) {
            ((CursorSwapper) this.adapter).swapCursor(null);
        }
        onContentChanged();
    }
}
