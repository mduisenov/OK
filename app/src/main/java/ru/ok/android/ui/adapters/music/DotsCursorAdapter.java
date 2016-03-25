package ru.ok.android.ui.adapters.music;

import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class DotsCursorAdapter<T> extends CursorAdapter implements OnClickListener {

    public interface OnDotsClickListener<T> {
        void onDotsClick(T t, View view);
    }
}
