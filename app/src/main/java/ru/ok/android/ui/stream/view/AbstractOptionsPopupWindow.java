package ru.ok.android.ui.stream.view;

import android.content.Context;
import java.util.List;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.BaseQuickAction.OnActionItemClickListener;
import ru.ok.android.ui.quickactions.QuickAction;

public abstract class AbstractOptionsPopupWindow extends QuickAction implements OnActionItemClickListener {
    protected abstract List<ActionItem> getActionItems();

    public AbstractOptionsPopupWindow(Context context) {
        super(context);
        for (ActionItem actionItem : getActionItems()) {
            addActionItem(actionItem);
        }
        setOnActionItemClickListener(this);
    }
}
