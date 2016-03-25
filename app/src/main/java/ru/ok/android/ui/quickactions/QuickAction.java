package ru.ok.android.ui.quickactions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.ui.quickactions.BaseQuickAction.OnActionItemClickListener;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.localization.LocalizationManager;

@Deprecated
public class QuickAction extends BaseQuickAction {
    private List<ActionItem> actionItems;
    private int mChildPos;
    private boolean mDidAction;
    private int mInsertPos;
    private OnActionItemClickListener mItemClickListener;

    /* renamed from: ru.ok.android.ui.quickactions.QuickAction.1 */
    class C11781 implements OnGlobalLayoutListener {
        C11781() {
        }

        public void onGlobalLayout() {
            int i;
            int maxWidth = 0;
            int maxHeight = 0;
            for (i = 0; i < QuickAction.this.mTrack.getChildCount(); i++) {
                View icon = QuickAction.this.mTrack.getChildAt(i).findViewById(2131624492);
                if (icon != null) {
                    int width = icon.getWidth();
                    int height = icon.getHeight();
                    if (width > maxWidth) {
                        maxWidth = width;
                    }
                    if (height > maxHeight) {
                        maxHeight = height;
                    }
                }
            }
            for (i = 0; i < QuickAction.this.mTrack.getChildCount(); i++) {
                icon = QuickAction.this.mTrack.getChildAt(i).findViewById(2131624492);
                if (icon != null) {
                    icon.getLayoutParams().width = maxWidth;
                    icon.getLayoutParams().height = maxHeight;
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.quickactions.QuickAction.2 */
    class C11792 implements OnClickListener {
        final /* synthetic */ int val$actionId;
        final /* synthetic */ int val$pos;

        C11792(int i, int i2) {
            this.val$pos = i;
            this.val$actionId = i2;
        }

        public void onClick(View v) {
            if (QuickAction.this.mItemClickListener != null) {
                QuickAction.this.mItemClickListener.onItemClick(QuickAction.this, this.val$pos, this.val$actionId);
            }
            if (!QuickAction.this.getActionItem(this.val$pos).isSticky()) {
                QuickAction.this.mDidAction = true;
                QuickAction.this.dismiss();
            }
        }
    }

    public QuickAction(Context context) {
        super(context);
        this.actionItems = new ArrayList();
        this.mChildPos = 0;
        this.mTrack.getViewTreeObserver().addOnGlobalLayoutListener(new C11781());
    }

    public ActionItem getActionItem(int index) {
        return (ActionItem) this.actionItems.get(index);
    }

    public int getActionItemsCount() {
        return this.actionItems.size();
    }

    public void setOnActionItemClickListener(OnActionItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void addActionItem(ActionItem action) {
        this.actionItems.add(action);
        int titleId = action.getTitleResourceId();
        int iconId = action.getIconResourceId();
        View container = LayoutInflater.from(getContext()).inflate(2130903070, null);
        ImageView img = (ImageView) container.findViewById(2131624492);
        TextView text = (TextView) container.findViewById(2131624493);
        if (iconId != 0) {
            img.setImageDrawable(this.mContext.getResources().getDrawable(iconId));
        } else {
            img.setVisibility(8);
        }
        if (titleId != 0) {
            text.setText(LocalizationManager.getString(getContext(), titleId));
        } else {
            text.setVisibility(8);
        }
        container.setOnClickListener(new C11792(this.mChildPos, action.getActionId()));
        container.setFocusable(true);
        container.setClickable(true);
        this.mTrack.addView(container, this.mInsertPos);
        this.mChildPos++;
        this.mInsertPos++;
    }

    public void setActionItemVisibility(ActionItem actionItem, boolean visible) {
        setActionItemVisibility(this.actionItems.indexOf(actionItem), visible);
    }

    public void setActionItemVisibility(int actionItemIndex, boolean visible) {
        if (actionItemIndex != -1 && actionItemIndex < this.mTrack.getChildCount()) {
            ViewUtil.setVisibility(this.mTrack.getChildAt(actionItemIndex), visible);
        }
    }

    public void show(View anchor, boolean isAnimation) {
        this.mDidAction = false;
        super.show(anchor, isAnimation);
    }
}
