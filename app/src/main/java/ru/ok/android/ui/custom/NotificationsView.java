package ru.ok.android.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import ru.ok.android.C0206R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.ViewUtil;

public class NotificationsView extends FrameLayout {
    private int count;
    private ImageView imageView;
    private TextView textView;
    private BorderType type;

    /* renamed from: ru.ok.android.ui.custom.NotificationsView.1 */
    static /* synthetic */ class C06211 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$ui$custom$NotificationsView$BorderType;

        static {
            $SwitchMap$ru$ok$android$ui$custom$NotificationsView$BorderType = new int[BorderType.values().length];
            try {
                $SwitchMap$ru$ok$android$ui$custom$NotificationsView$BorderType[BorderType.empty.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$custom$NotificationsView$BorderType[BorderType.actionBar.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$custom$NotificationsView$BorderType[BorderType.tabbar.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$custom$NotificationsView$BorderType[BorderType.actionBarGray.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$custom$NotificationsView$BorderType[BorderType.small.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$custom$NotificationsView$BorderType[BorderType.big.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    enum BorderType {
        empty,
        actionBar,
        tabbar,
        actionBarGray,
        small,
        big
    }

    public NotificationsView(Context context) {
        super(context);
        this.type = BorderType.empty;
        this.count = 0;
        init(null);
    }

    public NotificationsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.type = BorderType.empty;
        this.count = 0;
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(2130903352, this, true);
        this.textView = (TextView) findViewById(C0263R.id.text);
        this.imageView = (ImageView) findViewById(C0263R.id.image);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, C0206R.styleable.NotificationView);
            switch (a.getInt(0, 0)) {
                case RECEIVED_VALUE:
                    this.type = BorderType.empty;
                    break;
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    this.type = BorderType.actionBar;
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    this.type = BorderType.tabbar;
                    break;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    this.type = BorderType.actionBarGray;
                    break;
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    this.type = BorderType.small;
                    break;
                case Message.UUID_FIELD_NUMBER /*5*/:
                    this.type = BorderType.big;
                    break;
            }
            if (this.type == BorderType.actionBar && a.getBoolean(1, false)) {
                this.type = BorderType.actionBarGray;
            }
            a.recycle();
        }
        boolean textVisible = true;
        switch (C06211.$SwitchMap$ru$ok$android$ui$custom$NotificationsView$BorderType[this.type.ordinal()]) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                setBackgroundResource(2130837743);
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                setBackgroundResource(2130837757);
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                setBackgroundResource(2130837744);
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                setBackgroundResource(2130837756);
                textVisible = false;
                break;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                setBackgroundResource(2130837746);
                break;
            default:
                setBackgroundResource(2130837747);
                break;
        }
        ViewUtil.setVisibility(this.textView, textVisible);
        if (textVisible) {
            this.textView.setGravity(17);
            this.textView.setTextColor(-1);
            float density = (float) getContext().getResources().getDisplayMetrics().densityDpi;
            if (DeviceUtils.getType(getContext()) != DeviceLayoutType.SMALL) {
                this.textView.setTextSize(12.0f);
                this.textView.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                this.textView.setTextSize(density == 120.0f ? 12.0f : 10.0f);
            }
            this.textView.setIncludeFontPadding(false);
        }
        int padding = getResources().getDimensionPixelOffset(2131231094);
        setPadding(padding, 0, padding, 0);
    }

    public void setValue(int count) {
        this.count = count;
        setNotificationText(count > 99 ? "99+" : Integer.toString(count));
    }

    public int getValue() {
        return this.count;
    }

    public void setImage(int resId) {
        this.imageView.setImageResource(resId);
    }

    public void setNotificationText(CharSequence text) {
        this.textView.setText(text);
    }

    public void showText() {
        this.textView.setTextColor(-1);
    }

    public final void hideImage() {
        this.imageView.setImageDrawable(null);
    }

    public void setSimpleBubble() {
        this.textView.setTextColor(0);
        switch (C06211.$SwitchMap$ru$ok$android$ui$custom$NotificationsView$BorderType[this.type.ordinal()]) {
            case Message.TYPE_FIELD_NUMBER /*3*/:
                setBackgroundResource(2130837748);
                break;
            default:
                setBackgroundResource(2130837749);
                break;
        }
        setMinimumWidth(getResources().getDrawable(2130837748).getMinimumWidth());
        setMinimumHeight(getResources().getDrawable(2130837748).getMinimumHeight());
    }
}
