package ru.ok.android.ui.nativeRegistration;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import ru.ok.android.ui.custom.imageview.AvatarImageView;

public class UserAvatar extends RelativeLayout {
    public AvatarImageView avatar;
    public ImageView cancel;
    private boolean isCancelVisible;
    public ImageView ok;

    public void setCancelVisible(boolean isCancelVisible) {
        this.isCancelVisible = isCancelVisible;
    }

    public UserAvatar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UserAvatar(Context context) {
        super(context);
        init(context);
    }

    public boolean isCancelVisible() {
        return this.isCancelVisible;
    }

    public UserAvatar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(2130903418, this, true);
        this.cancel = (ImageView) findViewById(2131625290);
        this.ok = (ImageView) findViewById(2131625024);
        this.avatar = (AvatarImageView) findViewById(2131624657);
    }

    public void selectView() {
        setAlpha(1.0f);
        if (isCancelVisible()) {
            this.cancel.setVisibility(0);
        }
    }

    public void unselectView() {
        setAlpha(0.4f);
        if (isCancelVisible()) {
            this.cancel.setVisibility(8);
        }
    }
}
