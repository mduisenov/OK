package ru.ok.android.ui.custom.mediacomposer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import java.util.Collections;
import java.util.List;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.BaseQuickAction.OnActionItemClickListener;
import ru.ok.android.ui.quickactions.QuickAction;

public class MediaItemActionsPopup implements OnActionItemClickListener {
    private final View anchor;
    private final Bundle extras;
    private final MediaItem mediaItem;
    private final MediaItemActionProvider mediaItemActionProvider;
    private final QuickAction quickAction;

    public MediaItemActionsPopup(Context context, MediaItem mediaItem, View anchor, MediaItemActionProvider mediaItemActionProvider, Bundle extras) {
        List<ActionItem> actions;
        this.anchor = anchor;
        this.mediaItem = mediaItem;
        this.mediaItemActionProvider = mediaItemActionProvider;
        this.extras = extras;
        if (extras == null) {
            actions = Collections.emptyList();
        } else {
            actions = extras.getParcelableArrayList("actions");
        }
        this.quickAction = new QuickAction(context);
        this.quickAction.setOnActionItemClickListener(this);
        if (actions != null) {
            for (ActionItem action : actions) {
                if (mediaItemActionProvider.onPrepareAction(action, mediaItem, extras)) {
                    this.quickAction.addActionItem(action);
                }
            }
        }
    }

    public void onItemClick(QuickAction source, int pos, int actionId) {
        this.mediaItemActionProvider.onActionSelected(actionId, this.mediaItem, this.extras);
    }

    public void show() {
        this.quickAction.show(this.anchor);
    }
}
