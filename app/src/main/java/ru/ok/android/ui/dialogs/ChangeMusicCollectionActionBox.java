package ru.ok.android.ui.dialogs;

import android.content.Context;
import android.view.View;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.BaseQuickAction.OnActionItemClickListener;
import ru.ok.android.ui.quickactions.QuickAction;
import ru.ok.model.wmf.UserTrackCollection;

public class ChangeMusicCollectionActionBox implements OnActionItemClickListener {
    private ActionItem addItem;
    private View anchor;
    private UserTrackCollection collection;
    private Context context;
    private ActionItem deleteItem;
    private OnAddTrackCollectionsListener listenerAdd;
    private OnDeleteTrackCollectionsListener listenerDelete;
    private QuickAction quickAction;

    public interface OnAddTrackCollectionsListener {
        void onAddCollection(UserTrackCollection userTrackCollection);
    }

    public interface OnDeleteTrackCollectionsListener {
        void onDeleteCollection(UserTrackCollection userTrackCollection);
    }

    protected enum DialogType {
        DELETE,
        ADD
    }

    public static ChangeMusicCollectionActionBox createDeleteCollectionBox(Context context, UserTrackCollection collection, View anchor) {
        return new ChangeMusicCollectionActionBox(anchor, context, collection, DialogType.DELETE);
    }

    public static ChangeMusicCollectionActionBox createAddCollectionBox(Context context, UserTrackCollection collection, View anchor) {
        return new ChangeMusicCollectionActionBox(anchor, context, collection, DialogType.ADD);
    }

    public ChangeMusicCollectionActionBox(View anchor, Context context, UserTrackCollection collection, DialogType type) {
        this.anchor = anchor;
        this.context = context;
        this.collection = collection;
        this.quickAction = new QuickAction(context);
        this.quickAction.setOnActionItemClickListener(this);
        if (type == DialogType.ADD) {
            this.addItem = new ActionItem(2, 2131166659, 2130838570);
            this.quickAction.addActionItem(this.addItem);
        } else if (type == DialogType.DELETE) {
            this.deleteItem = new ActionItem(1, 2131166739, 2130838574);
            this.quickAction.addActionItem(this.deleteItem);
        }
    }

    public void onItemClick(QuickAction source, int pos, int actionId) {
        switch (actionId) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (this.listenerDelete != null) {
                    this.listenerDelete.onDeleteCollection(this.collection);
                }
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (this.listenerAdd != null) {
                    this.listenerAdd.onAddCollection(this.collection);
                }
            default:
        }
    }

    public void setListenerAdd(OnAddTrackCollectionsListener listenerAdd) {
        this.listenerAdd = listenerAdd;
    }

    public void setListenerDelete(OnDeleteTrackCollectionsListener listenerDelete) {
        this.listenerDelete = listenerDelete;
    }

    public void show() {
        this.quickAction.show(this.anchor);
    }
}
