package ru.ok.android.ui.custom.profiles;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ru.ok.android.C0206R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.localization.LocalizationManager;

public final class ProfilesButton extends LinearLayout {
    private ImageView imageView;
    private Mode mode;
    private String titleText;
    private TextView titleView;

    /* renamed from: ru.ok.android.ui.custom.profiles.ProfilesButton.1 */
    static /* synthetic */ class C07451 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$ui$custom$profiles$ProfilesButton$Mode;

        static {
            $SwitchMap$ru$ok$android$ui$custom$profiles$ProfilesButton$Mode = new int[Mode.values().length];
            try {
                $SwitchMap$ru$ok$android$ui$custom$profiles$ProfilesButton$Mode[Mode.TextAndImage.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$custom$profiles$ProfilesButton$Mode[Mode.Text.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$custom$profiles$ProfilesButton$Mode[Mode.Image.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    class LongClickToastNotification implements OnLongClickListener {
        LongClickToastNotification() {
        }

        public boolean onLongClick(View v) {
            if (ProfilesButton.this.mode == Mode.Image && !TextUtils.isEmpty(ProfilesButton.this.titleText)) {
                int xOffset = 0;
                int yOffset = 0;
                Rect gvr = new Rect();
                if (((View) v.getParent()).getGlobalVisibleRect(gvr)) {
                    View root = v.getRootView();
                    int halfwayWidth = root.getRight() / 2;
                    int halfwayHeight = root.getBottom() / 2;
                    int parentCenterX = ((gvr.right - gvr.left) / 2) + gvr.left;
                    int parentCenterY = ((gvr.bottom - gvr.top) / 2) + gvr.top;
                    if (parentCenterY <= halfwayHeight) {
                        yOffset = -(halfwayHeight - parentCenterY);
                    } else {
                        yOffset = parentCenterY - halfwayHeight;
                    }
                    if (parentCenterX < halfwayWidth) {
                        xOffset = parentCenterX - halfwayWidth;
                    }
                }
                Toast toast = Toast.makeText(ProfilesButton.this.getContext(), ProfilesButton.this.titleText, 0);
                toast.setGravity(17, xOffset, yOffset);
                toast.show();
            }
            return true;
        }
    }

    public enum Mode {
        TextAndImage,
        Image,
        Text
    }

    public ProfilesButton(Context context) {
        super(context);
        this.mode = Mode.TextAndImage;
    }

    public ProfilesButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mode = Mode.TextAndImage;
        Resources res = getResources();
        int defaultTextSize = res.getDimensionPixelSize(2131230922);
        int defaultTextColor = res.getColor(2131492966);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.ProfileButton, 0, 0);
        this.titleText = LocalizationManager.getString(context, a.getResourceId(2, -1));
        int valueImageId = a.getResourceId(3, -1);
        int textColor = a.getColor(1, defaultTextColor);
        int textSize = a.getDimensionPixelSize(0, defaultTextSize);
        a.recycle();
        setOrientation(0);
        setGravity(17);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(2130903405, this, true);
        this.imageView = (ImageView) getChildAt(0);
        if (valueImageId != -1) {
            this.imageView.setImageResource(valueImageId);
        }
        this.titleView = (TextView) getChildAt(1);
        this.titleView.setText(this.titleText);
        this.titleView.setTextColor(textColor);
        this.titleView.setTextSize(0, (float) textSize);
        setOnLongClickListener(new LongClickToastNotification());
    }

    public void setText(int res) {
        this.titleView.setText(LocalizationManager.getString(getContext(), res));
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        switch (C07451.$SwitchMap$ru$ok$android$ui$custom$profiles$ProfilesButton$Mode[mode.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.titleView.setVisibility(0);
                this.imageView.setVisibility(0);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                this.titleView.setVisibility(0);
                this.imageView.setVisibility(8);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                this.titleView.setVisibility(8);
                this.imageView.setVisibility(0);
            default:
        }
    }

    public Mode getMode() {
        return this.mode;
    }
}
