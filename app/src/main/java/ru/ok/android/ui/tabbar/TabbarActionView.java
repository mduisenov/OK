package ru.ok.android.ui.tabbar;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import ru.mail.libverify.C0176R;
import ru.ok.android.ui.custom.NotificationsView;
import ru.ok.android.utils.localization.LocalizationManager;

public class TabbarActionView extends RelativeLayout {
    private ViewGroup contentContainer;
    private final Action mAction;
    private ImageView mIconView;
    private NotificationsView mNotificationsView;
    private boolean mSelected;
    private View mSelectionView;
    private TextView mTitleView;

    public TabbarActionView(Context context, Action action) {
        super(context);
        this.mAction = action;
        LocalizationManager.inflate(context, 2130903533, (ViewGroup) this, true);
        this.mSelectionView = findViewById(2131625397);
        this.mTitleView = (TextView) findViewById(C0176R.id.title);
        this.mIconView = (ImageView) findViewById(C0176R.id.icon);
        this.mNotificationsView = (NotificationsView) findViewById(2131625396);
        updateText();
        this.mIconView.setImageResource(action.getDrawable());
        this.contentContainer = (ViewGroup) findViewById(2131625395);
        setBackgroundResource(2130838643);
        setSelected(false);
    }

    public void updateText() {
        if (this.mTitleView != null) {
            this.mTitleView.setText(LocalizationManager.getString(getContext(), this.mAction.getTextRes()));
        }
    }

    public boolean isSelected() {
        return this.mSelected;
    }

    public void setSelected(boolean selected) {
        if (this.mAction != null) {
            selected = this.mAction.canBeSelected() && selected;
        }
        this.mSelected = selected;
        this.mIconView.setSelected(this.mSelected);
        this.mTitleView.setSelected(this.mSelected);
        if (this.mSelected) {
            this.mSelectionView.setVisibility(0);
        } else {
            this.mSelectionView.setVisibility(4);
        }
    }

    public final Action getAction() {
        return this.mAction;
    }

    public final void setTypeface(Typeface typeface) {
        this.mTitleView.setTypeface(typeface, 1);
    }

    public NotificationsView getNotificationsView() {
        return this.mNotificationsView;
    }

    public void setSelectorPosition(int position) {
        LayoutParams lp = (LayoutParams) this.mSelectionView.getLayoutParams();
        lp.addRule(9, 0);
        lp.addRule(10);
        lp.addRule(11, 0);
        lp.addRule(12, 0);
        this.mSelectionView.setLayoutParams(lp);
    }

    public ViewGroup getContentContainer() {
        return this.contentContainer;
    }
}
