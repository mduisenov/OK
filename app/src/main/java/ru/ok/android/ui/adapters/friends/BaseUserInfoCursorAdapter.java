package ru.ok.android.ui.adapters.friends;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView.ViewHolder;
import ru.ok.android.ui.adapters.ImageBlockerRecyclerProvider;
import ru.ok.android.ui.dialogs.UsersDoBase.OnCallUserSelectListener;
import ru.ok.android.ui.dialogs.UsersDoBase.OnGoToMainPageSelectListener;

public abstract class BaseUserInfoCursorAdapter<VH extends ViewHolder> extends BaseCursorRecyclerAdapter<VH> implements ImageBlockerRecyclerProvider {
    protected OnCallUserSelectListener onCallUserSelectListener;
    protected OnGoToMainPageSelectListener onGoToMainPageSelectListener;

    public BaseUserInfoCursorAdapter(Cursor cursor, boolean autoRequery) {
        super(cursor, autoRequery);
    }

    public void setOnGoToMainPageSelectListener(OnGoToMainPageSelectListener onGoToMainPageSelectListener) {
        this.onGoToMainPageSelectListener = onGoToMainPageSelectListener;
    }

    public void setOnCallUserSelectListener(OnCallUserSelectListener onCallUserSelectListener) {
        this.onCallUserSelectListener = onCallUserSelectListener;
    }
}
