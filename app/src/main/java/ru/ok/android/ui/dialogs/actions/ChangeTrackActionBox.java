package ru.ok.android.ui.dialogs.actions;

import android.content.Context;
import android.view.View;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.dialogs.ChangeTrackStateBase;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.BaseQuickAction.OnActionItemClickListener;
import ru.ok.android.ui.quickactions.QuickAction;
import ru.ok.model.wmf.Track;

public class ChangeTrackActionBox extends ChangeTrackStateBase implements OnActionItemClickListener {
    private ActionItem addItem;
    private View anchor;
    private ActionItem deleteItem;
    private QuickAction quickAction;
    private ActionItem statusItem;

    public ChangeTrackActionBox(Context context, View anchor, DialogType type, Track track) {
        super(context, type, track);
        this.anchor = anchor;
        this.quickAction = new QuickAction(context);
        this.quickAction.setOnActionItemClickListener(this);
        if (type == DialogType.ADD) {
            this.addItem = new ActionItem(2, 2131165341, 2130838570);
            this.quickAction.addActionItem(this.addItem);
        } else if (type == DialogType.DELETE) {
            this.deleteItem = new ActionItem(3, 2131165688, 2130838574);
            this.quickAction.addActionItem(this.deleteItem);
        }
        this.statusItem = new ActionItem(1, 2131166545, 2130838582);
        this.quickAction.addActionItem(this.statusItem);
    }

    public static ChangeTrackActionBox createDeleteTrackBox(Context context, Track track, View anchor) {
        return new ChangeTrackActionBox(context, anchor, DialogType.DELETE, track);
    }

    public static ChangeTrackActionBox createAddTrackBox(Context context, Track track, View anchor) {
        return new ChangeTrackActionBox(context, anchor, DialogType.ADD, track);
    }

    public void onItemClick(QuickAction source, int pos, int actionId) {
        switch (actionId) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (this.listener != null) {
                    this.listener.onSetStatusTrack(this.track);
                }
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (this.listener != null) {
                    this.listener.onAddTrack(this.track);
                }
            case Message.TYPE_FIELD_NUMBER /*3*/:
                if (this.listener != null) {
                    this.listener.onDeleteTrack(this.track);
                }
            default:
        }
    }

    public void show() {
        this.quickAction.show(this.anchor);
    }
}
