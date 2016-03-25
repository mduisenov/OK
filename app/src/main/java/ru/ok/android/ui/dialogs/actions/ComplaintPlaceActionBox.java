package ru.ok.android.ui.dialogs.actions;

import android.content.Context;
import android.view.View;
import ru.ok.android.ui.dialogs.ComplaintPlaceBase;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.BaseQuickAction.OnActionItemClickListener;
import ru.ok.android.ui.quickactions.QuickAction;
import ru.ok.model.places.Place;

public class ComplaintPlaceActionBox extends ComplaintPlaceBase implements OnActionItemClickListener {
    private View anchor;
    private ActionItem complaintItem;
    private QuickAction quickAction;

    public ComplaintPlaceActionBox(Context context, View anchor, Place place) {
        super(place);
        this.anchor = anchor;
        this.quickAction = new QuickAction(context);
        this.quickAction.setOnActionItemClickListener(this);
        this.complaintItem = new ActionItem(0, 2131166356);
        this.quickAction.addActionItem(this.complaintItem);
    }

    public void onItemClick(QuickAction source, int pos, int actionId) {
        if (actionId == 0 && this.listener != null) {
            this.listener.onComplaintSelectedItem(this.place);
        }
    }

    public void show() {
        this.quickAction.show(this.anchor, true);
    }
}
