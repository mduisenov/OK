package ru.ok.android.widget.menuitems;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.MenuView;
import ru.ok.android.widget.MenuView.MenuItem;
import ru.ok.android.widget.MenuView.ViewHolder;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;

public class StandardItem extends MenuItem {
    protected static int icon_size;
    private final OdklSlidingMenuFragmentActivity activity;
    protected boolean isLike;
    protected boolean isReply;
    protected BubbleState mBubbleState;
    protected int mCounter;
    protected int mCounterTwo;
    private final int mIconRes;
    private final int mNameRes;

    class Holder extends ViewHolder {
        public TextView counter;
        public TextView greenCounter;
        public ImageView icon;
        public TextView name;

        public Holder(int type, int position) {
            super(type, position);
        }
    }

    /* renamed from: ru.ok.android.widget.menuitems.StandardItem.1 */
    static /* synthetic */ class C15011 {
        static final /* synthetic */ int[] f125x51b30c38;

        static {
            f125x51b30c38 = new int[BubbleState.values().length];
            try {
                f125x51b30c38[BubbleState.gray.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f125x51b30c38[BubbleState.green_phone.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f125x51b30c38[BubbleState.green_tablet.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public enum BubbleState {
        gray,
        green_tablet,
        green_phone
    }

    static {
        icon_size = 0;
    }

    public StandardItem(OdklSlidingMenuFragmentActivity activity, int resIcon, int nameRes, Type type, int height, BubbleState bubbleState) {
        super(height, type);
        this.isReply = false;
        this.isLike = false;
        this.mCounter = 0;
        this.mCounterTwo = 0;
        this.mBubbleState = BubbleState.gray;
        this.activity = activity;
        this.mIconRes = resIcon;
        this.mNameRes = nameRes;
        this.mBubbleState = bubbleState;
    }

    public void onClick(MenuView menuView, MenuItem item) {
        if (SlidingMenuHelper.processClickItemAndReturnNeededCloseMenu(this.activity, this.activity.getSlidingMenuWebLinksProcessor(), this.type, menuView, item)) {
            super.onClick(menuView, item);
        }
    }

    public void setCounter(int counter, int counterTwo) {
        this.mCounter = counter;
        this.mCounterTwo = counterTwo;
    }

    public void setCounter(int counter, int counterTwo, boolean isReply, boolean isLike) {
        this.mCounter = counter;
        this.mCounterTwo = counterTwo;
        this.isReply = isReply;
        this.isLike = isLike;
    }

    public int getType() {
        return 0;
    }

    public View getView(LocalizationManager inflater, View view, int position, Type selectedItem) {
        Holder holder;
        boolean isSelected = false;
        if (icon_size == 0) {
            icon_size = inflater.getContext().getResources().getDrawable(2130838405).getMinimumWidth();
        }
        if (view == null) {
            view = LocalizationManager.inflate(inflater.getContext(), 2130903318, null, false);
            holder = createViewHolder(getType(), position);
            holder.name = (TextView) view.findViewById(2131625064);
            holder.counter = (TextView) view.findViewById(2131625065);
            holder.icon = (ImageView) view.findViewById(2131625062);
            holder.greenCounter = (TextView) view.findViewById(2131625076);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
            holder.position = position;
        }
        setCounterText(this.mCounter, this.mCounterTwo, holder.counter, holder.greenCounter, this.mBubbleState);
        setText(holder.name);
        holder.icon.setImageResource(this.mIconRes != 0 ? this.mIconRes : 17170445);
        if (selectedItem == this.type) {
            isSelected = true;
        }
        holder.name.setSelected(isSelected);
        holder.icon.setSelected(isSelected);
        return view;
    }

    private static void setGreenModeTablet(TextView view, boolean isLike, boolean isReply) {
        if (view != null) {
            if (isLike || isReply) {
                view.setBackgroundResource(isReply ? 2130837755 : 2130837754);
            } else {
                view.setBackgroundResource(2130837753);
            }
            view.setTextColor(-1);
        }
    }

    private static void setGreenModePhone(TextView view, boolean isLike, boolean isReply) {
        if (view != null) {
            if (isLike || isReply) {
                view.setBackgroundResource(isReply ? 2130837752 : 2130837751);
            } else {
                view.setBackgroundResource(2130837750);
            }
            setPadding2(view);
            if (view.getLayoutParams() != null) {
                view.getLayoutParams().height = (int) view.getResources().getDimension(2131230889);
                view.requestLayout();
            }
            view.setTextColor(-1);
        }
    }

    private static void setGrayMode(TextView view) {
        if (view != null) {
            view.setBackgroundDrawable(null);
            setPadding2(view);
            if (view.getLayoutParams() != null) {
                view.getLayoutParams().height = (int) view.getResources().getDimension(2131230887);
                view.requestLayout();
            }
            view.setTextColor(view.getResources().getColor(2131493162));
        }
    }

    private static void setPadding2(View view) {
        view.setPadding((int) Utils.dipToPixels(4.0f), view.getPaddingTop(), (int) Utils.dipToPixels(4.0f), view.getPaddingBottom());
    }

    private void setVisibilityView(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    protected void setCounterText(int counter, int counterTwo, TextView greyText, TextView greenText, BubbleState bubbleState) {
        TextView textView;
        int i = 0;
        int i2 = 8;
        switch (C15011.f125x51b30c38[bubbleState.ordinal()]) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                setVisibilityView(greenText, 8);
                if (this.isReply || this.isLike || counter > 0 || counterTwo > 0) {
                    i2 = 0;
                }
                setVisibilityView(greyText, i2);
                textView = greyText;
                setGreenModePhone(textView, this.isLike, this.isReply);
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                setVisibilityView(greyText, 8);
                if (this.isReply || this.isLike || counter > 0 || counterTwo > 0) {
                    i2 = 0;
                }
                setVisibilityView(greenText, i2);
                textView = greenText;
                setGreenModeTablet(textView, this.isLike, this.isReply);
                break;
            default:
                setVisibilityView(greenText, 8);
                if ((counter <= 0 && counterTwo <= 0) || bubbleState == BubbleState.green_tablet) {
                    i = 8;
                }
                setVisibilityView(greyText, i);
                textView = greyText;
                setGrayMode(textView);
                break;
        }
        if (textView != null) {
            if (this.isReply || this.isLike) {
                textView.setText(null);
            } else if (this.type == Type.holidays) {
                textView.setText(Html.fromHtml("<font color=#ed812b>" + MenuItem.getCounterText(counter, bubbleState) + "</font>"));
            } else if (this.type == Type.friends) {
                textView.setText(Html.fromHtml("<font color=#ed812b>" + MenuItem.getCounterText(counter, bubbleState) + "</font><font color=#999999> / " + MenuItem.getCounterText(counterTwo, bubbleState) + "</font>"));
            } else {
                textView.setText(MenuItem.getCounterText(counter, bubbleState));
            }
        }
    }

    protected void setText(TextView textView) {
        textView.setText(LocalizationManager.getString(this.activity, this.mNameRes));
    }

    public OdklSlidingMenuFragmentActivity getActivity() {
        return this.activity;
    }

    public Holder createViewHolder(int type, int position) {
        return new Holder(type, position);
    }
}
