package ru.ok.android.ui.quickactions;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import ru.ok.android.proto.MessagesProto.Message;

public class BaseQuickAction extends ContextPopupWindow implements OnDismissListener {
    private DismissSelectionViewCallBack dismissSelectionViewCallBack;
    private Handler handler;
    private int mAnimStyle;
    protected View mRootView;
    private ViewGroup mScroller;
    protected ViewGroup mTrack;
    private int rootWidth;

    public interface OnActionItemClickListener {
        void onItemClick(QuickAction quickAction, int i, int i2);
    }

    private class DismissSelectionViewCallBack {
        int selectionDrawableIdBack;
        View view;

        public void onExecute() {
            if (this.view == null) {
                return;
            }
            if (this.selectionDrawableIdBack == -1) {
                this.view.setBackgroundColor(0);
            } else {
                this.view.setBackgroundResource(this.selectionDrawableIdBack);
            }
        }
    }

    public BaseQuickAction(Context context) {
        super(context);
        this.handler = new Handler();
        this.rootWidth = 0;
        setRootViewId(2130903402);
        this.mAnimStyle = 5;
    }

    public void setRootViewId(int id) {
        this.mRootView = LayoutInflater.from(getContext()).inflate(id, null);
        this.mTrack = (ViewGroup) this.mRootView.findViewById(2131625254);
        this.mScroller = (ViewGroup) this.mRootView.findViewById(2131625253);
        this.mRootView.setLayoutParams(new LayoutParams(-2, -2));
        setContentView(this.mRootView);
    }

    protected void performToDismissState() {
        if (this.dismissSelectionViewCallBack != null) {
            this.dismissSelectionViewCallBack.onExecute();
            this.dismissSelectionViewCallBack = null;
        }
    }

    public void show(View anchor) {
        show(anchor, true);
    }

    protected void show(View anchor, boolean isAnimation) {
        int xPos;
        int yPos;
        this.mainAnchor = anchor;
        preShow();
        View root = anchor.getRootView().findViewById(16908290);
        int screenWidth = root.getWidth();
        int screenHeight = root.getHeight();
        this.mRootView.measure(MeasureSpec.makeMeasureSpec(screenWidth, LinearLayoutManager.INVALID_OFFSET), MeasureSpec.makeMeasureSpec(screenHeight, LinearLayoutManager.INVALID_OFFSET));
        int rootHeight = this.mRootView.getMeasuredHeight();
        this.rootWidth = this.mRootView.getMeasuredWidth();
        Rect ra = new Rect();
        Rect rr = new Rect();
        anchor.getGlobalVisibleRect(ra);
        root.getGlobalVisibleRect(rr);
        int top = ra.top - rr.top;
        int left = ra.left - rr.left;
        Rect anchorRect = new Rect(left, top, anchor.getWidth() + left, anchor.getHeight() + top);
        if (anchorRect.left + this.rootWidth > screenWidth) {
            xPos = anchorRect.left - (this.rootWidth - anchor.getWidth());
            xPos = xPos < 0 ? 0 : xPos - 5;
        } else {
            xPos = anchor.getWidth() > this.rootWidth ? anchorRect.centerX() - (this.rootWidth / 2) : anchorRect.left;
        }
        xPos = Math.max(xPos, 5);
        int dyTop = anchorRect.top;
        int dyBottom = screenHeight - anchorRect.bottom;
        boolean onTop = dyTop > dyBottom;
        if (!onTop) {
            yPos = anchorRect.bottom;
            if (rootHeight > dyBottom) {
                this.mScroller.getLayoutParams().height = dyBottom;
            }
        } else if (rootHeight > dyTop) {
            yPos = 15;
            this.mScroller.getLayoutParams().height = dyTop - anchor.getHeight();
        } else {
            yPos = anchorRect.top - rootHeight;
        }
        if (isAnimation) {
            setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
        } else {
            setAnimationStyle(0);
        }
        showAtLocation(anchor, 0, xPos, yPos);
    }

    private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
        int i = 2131296444;
        int i2 = 2131296443;
        int i3 = 2131296441;
        switch (this.mAnimStyle) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (!onTop) {
                    i = 2131296439;
                }
                setAnimationStyle(i);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                setAnimationStyle(onTop ? 2131296446 : 2131296441);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                setAnimationStyle(onTop ? 2131296443 : 2131296438);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                setAnimationStyle(onTop ? 2131296445 : 2131296440);
            case Message.UUID_FIELD_NUMBER /*5*/:
                if (requestedX <= screenWidth / 4) {
                    if (!onTop) {
                        i = 2131296439;
                    }
                    setAnimationStyle(i);
                } else if (requestedX <= screenWidth / 4 || requestedX >= (screenWidth / 4) * 3) {
                    if (onTop) {
                        i3 = 2131296446;
                    }
                    setAnimationStyle(i3);
                } else {
                    if (!onTop) {
                        i2 = 2131296438;
                    }
                    setAnimationStyle(i2);
                }
            default:
        }
    }

    public void onDismiss() {
        performToDismissState();
    }
}
