package ru.ok.android.ui.dialogs.actions;

import android.content.Context;
import android.view.View;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.dialogs.SelectAlbumArtistBase;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.BaseQuickAction.OnActionItemClickListener;
import ru.ok.android.ui.quickactions.QuickAction;

public class SelectAlbumArtistActionBox extends SelectAlbumArtistBase implements OnActionItemClickListener {
    private ActionItem albumItem;
    private View anchor;
    private ActionItem artistItem;
    private QuickAction quickAction;

    public SelectAlbumArtistActionBox(Context context, View anchor) {
        super(context);
        this.anchor = anchor;
        this.quickAction = new QuickAction(context);
        this.quickAction.setOnActionItemClickListener(this);
        this.albumItem = new ActionItem(0, getItems()[0]);
        this.artistItem = new ActionItem(1, getItems()[1]);
    }

    public void addAlbumAction() {
        this.quickAction.addActionItem(this.albumItem);
    }

    public void addArtistAction() {
        this.quickAction.addActionItem(this.artistItem);
    }

    public void onItemClick(QuickAction source, int pos, int actionId) {
        switch (actionId) {
            case RECEIVED_VALUE:
                if (this.listener != null) {
                    this.listener.onSelectAlbum();
                }
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (this.listener != null) {
                    this.listener.onSelectArtist();
                }
            default:
        }
    }

    public void show() {
        if (this.quickAction.getActionItemsCount() > 0) {
            this.quickAction.show(this.anchor);
        }
    }
}
