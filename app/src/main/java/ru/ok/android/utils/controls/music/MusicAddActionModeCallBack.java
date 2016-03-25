package ru.ok.android.utils.controls.music;

import android.content.Context;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.utils.localization.LocalizationManager;

public abstract class MusicAddActionModeCallBack implements Callback {
    protected final Context context;
    protected MenuItem item;

    protected abstract void onClickItemActionMode();

    protected MusicAddActionModeCallBack(Context context) {
        this.context = context;
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        LocalizationManager.inflate(this.context, mode.getMenuInflater(), 2131689473, menu);
        this.item = menu.findItem(2131625438);
        return true;
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case C0263R.id.delete /*2131624801*/:
                onClickItemActionMode();
                return true;
            case 2131625438:
                onClickItemActionMode();
                return true;
            default:
                return false;
        }
    }

    public void onDestroyActionMode(ActionMode mode) {
        this.item = null;
    }

    public MenuItem getItem() {
        return this.item;
    }
}
