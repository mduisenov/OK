package ru.ok.android.ui.quickactions;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListPopupWindow;
import java.util.ArrayList;
import java.util.List;
import ru.mail.libverify.C0176R;

public class QuickActionList implements OnItemClickListener {
    private List<ActionItem> actionItems;
    private Context context;
    private OnActionItemClickListener itemClickListener;
    private ListPopupWindow listPopupWindow;

    public interface OnActionItemClickListener {
        void onItemClick(QuickActionList quickActionList, int i, int i2);
    }

    public QuickActionList(Context context) {
        this.actionItems = new ArrayList();
        this.context = context;
        this.listPopupWindow = new ListPopupWindow(context);
        this.listPopupWindow.setModal(true);
        this.listPopupWindow.setOnItemClickListener(this);
        this.listPopupWindow.setInputMethodMode(2);
        this.listPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(C0176R.drawable.abc_popup_background_mtrl_mult));
    }

    public void addActionItem(ActionItem action) {
        this.actionItems.add(action);
    }

    public void show(View anchor, int horizontalOffset) {
        ActionsAdapter actionsAdapter = new ActionsAdapter(this.context, this.actionItems);
        this.listPopupWindow.setAdapter(actionsAdapter);
        this.listPopupWindow.setAnchorView(anchor);
        this.listPopupWindow.setContentWidth(measureContentWidth(actionsAdapter));
        this.listPopupWindow.setHorizontalOffset(horizontalOffset);
        this.listPopupWindow.show();
    }

    public void show(View anchor) {
        show(anchor, 0);
    }

    private int measureContentWidth(ActionsAdapter listAdapter) {
        int maxWidth = 0;
        View itemView = null;
        ActionsAdapter adapter = listAdapter;
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, 0);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, 0);
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            itemView = adapter.getView(i, itemView, null);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            int itemWidth = itemView.getMeasuredWidth();
            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }
        return maxWidth;
    }

    public void setOnActionItemClickListener(OnActionItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ActionItem item = (ActionItem) this.actionItems.get(position);
        if (this.itemClickListener != null) {
            this.itemClickListener.onItemClick(this, position, item.getActionId());
        }
        this.listPopupWindow.dismiss();
    }
}
