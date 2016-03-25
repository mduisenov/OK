package ru.ok.android.ui.presents.adapter;

import android.app.Activity;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.CompositePresentView;
import ru.ok.android.ui.custom.imageview.CircledBorderDrawable;
import ru.ok.android.ui.custom.imageview.RoundAvatarImageView;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.QuickActionList;
import ru.ok.android.ui.quickactions.QuickActionList.OnActionItemClickListener;
import ru.ok.android.ui.stream.music.PlayerStateHolder;
import ru.ok.android.ui.stream.view.ProfilePresentTrackView;
import ru.ok.android.ui.stream.view.ProfilePresentTrackView.OnPlayTrackListener;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.presents.PresentsRequest.Direction;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.presents.PresentInfo;
import ru.ok.model.presents.PresentType;

public class UserPresentsAdapter extends Adapter<ViewHolder> {
    private final Activity activity;
    private final Direction direction;
    private boolean friendMode;
    private final Set<String> ids;
    private final Listener listener;
    private final OnClickListener onActionClicked;
    private final OnTouchListener onMessageTouchListener;
    private OnClickListener onOptionBtnClicked;
    private final OnPlayTrackListener onPlayTrackListener;
    private final OnClickListener onPresentClicked;
    private final OnClickListener onUserClicked;
    private final PlayerStateHolder playerStateHolder;
    private final List<PresentInfo> presents;

    /* renamed from: ru.ok.android.ui.presents.adapter.UserPresentsAdapter.1 */
    class C11621 implements OnClickListener {
        C11621() {
        }

        public void onClick(View view) {
            PresentInfo presentInfo = (PresentInfo) view.getTag();
            if (presentInfo != null) {
                QuickActionList actionList = new QuickActionList(view.getContext());
                actionList.addActionItem(new ActionItem(0, 2131165972, 2130838270));
                actionList.setOnActionItemClickListener(new QuickActionListener(presentInfo));
                actionList.show(view);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.presents.adapter.UserPresentsAdapter.2 */
    class C11632 implements OnClickListener {
        C11632() {
        }

        public void onClick(View view) {
            UserInfo userInfo = (UserInfo) view.getTag();
            if (userInfo != null) {
                UserPresentsAdapter.this.listener.clickUser(userInfo.uid);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.presents.adapter.UserPresentsAdapter.3 */
    class C11643 implements OnClickListener {
        C11643() {
        }

        public void onClick(View view) {
            PresentInfo present = (PresentInfo) view.getTag();
            if (present == null) {
                return;
            }
            if (!present.isWrapped || present.presentType.isOverlay) {
                UserPresentsAdapter.this.listener.clickPresent(present.presentType.id, present.holidayId);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.presents.adapter.UserPresentsAdapter.4 */
    class C11654 implements OnClickListener {
        C11654() {
        }

        public void onClick(View view) {
            PresentInfo present = (PresentInfo) view.getTag();
            if (present != null) {
                if (UserPresentsAdapter.this.direction != Direction.ACCEPTED || UserPresentsAdapter.this.friendMode) {
                    UserPresentsAdapter.this.listener.chooseUser(present.presentType.id);
                } else {
                    UserPresentsAdapter.this.listener.choosePresent(present.sender.getId());
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.presents.adapter.UserPresentsAdapter.5 */
    class C11665 implements OnTouchListener {
        C11665() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            boolean isLarger;
            if (((TextView) v).getLineHeight() * ((TextView) v).getLineCount() > v.getHeight()) {
                isLarger = true;
            } else {
                isLarger = false;
            }
            if (event.getAction() == 2 && isLarger) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
            } else {
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        }
    }

    public interface Listener {
        void choosePresent(@NonNull String str);

        void chooseUser(@NonNull String str);

        void clickPresent(@NonNull String str, @Nullable String str2);

        void clickUser(@NonNull String str);

        void hidePresent(@NonNull String str);
    }

    private class QuickActionListener implements OnActionItemClickListener {
        private final PresentInfo presentInfo;

        public QuickActionListener(PresentInfo presentInfo) {
            this.presentInfo = presentInfo;
        }

        public void onItemClick(QuickActionList source, int pos, int actionId) {
            if (actionId == 0) {
                UserPresentsAdapter.this.listener.hidePresent(this.presentInfo.id);
            }
        }
    }

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public final TextView actionTxt;
        public final View background;
        public final TextView dateTxt;
        public final TextView message;
        public final View optionsBtn;
        public final CompositePresentView presentImg;
        public final View separator;
        public final ProfilePresentTrackView trackView;
        public final RoundAvatarImageView userImg;
        public final TextView userNameTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.presentImg = (CompositePresentView) itemView.findViewById(2131624987);
            this.userNameTxt = (TextView) itemView.findViewById(C0263R.id.name);
            this.optionsBtn = itemView.findViewById(2131624989);
            this.dateTxt = (TextView) itemView.findViewById(2131624541);
            this.message = (TextView) itemView.findViewById(2131624538);
            this.actionTxt = (TextView) itemView.findViewById(2131624990);
            this.background = itemView.findViewById(2131624511);
            this.userImg = (RoundAvatarImageView) itemView.findViewById(2131624988);
            this.trackView = (ProfilePresentTrackView) itemView.findViewById(2131624557);
            this.separator = itemView.findViewById(2131624718);
        }
    }

    public UserPresentsAdapter(@NonNull Direction direction, @NonNull Activity activity, @NonNull Listener listener, @NonNull PlayerStateHolder playerStateHolder, @NonNull OnPlayTrackListener onPlayTrackListener) {
        this.ids = new HashSet();
        this.presents = new ArrayList();
        this.onOptionBtnClicked = new C11621();
        this.onUserClicked = new C11632();
        this.onPresentClicked = new C11643();
        this.onActionClicked = new C11654();
        this.onMessageTouchListener = new C11665();
        this.direction = direction;
        this.activity = activity;
        this.listener = listener;
        this.playerStateHolder = playerStateHolder;
        this.onPlayTrackListener = onPlayTrackListener;
    }

    public void setFriendMode(boolean friendMode) {
        this.friendMode = friendMode;
    }

    public void appendPresents(@NonNull List<PresentInfo> newPresents) {
        for (PresentInfo present : newPresents) {
            if (!this.ids.contains(present.id)) {
                this.presents.add(present);
                this.ids.add(present.id);
            }
        }
        notifyDataSetChanged();
    }

    public void setPresents(@NonNull List<PresentInfo> newPresents) {
        this.presents.clear();
        this.ids.clear();
        for (PresentInfo present : newPresents) {
            this.ids.add(present.id);
            this.presents.add(present);
        }
        notifyDataSetChanged();
    }

    public void deletePresent(@NonNull String presentId) {
        for (int i = 0; i < this.presents.size(); i++) {
            if (TextUtils.equals(((PresentInfo) this.presents.get(i)).id, presentId)) {
                this.presents.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    @NonNull
    public List<PresentInfo> getPresents() {
        return this.presents;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false));
        if (viewType == 2) {
            holder.background.setBackgroundDrawable(new CircledBorderDrawable(this.activity));
        }
        if (holder.message != null) {
            holder.message.setMovementMethod(new ScrollingMovementMethod());
            holder.message.setOnTouchListener(this.onMessageTouchListener);
        }
        if (this.friendMode) {
            holder.optionsBtn.setVisibility(4);
        } else {
            holder.optionsBtn.setOnClickListener(this.onOptionBtnClicked);
        }
        holder.actionTxt.setOnClickListener(this.onActionClicked);
        holder.presentImg.setOnClickListener(this.onPresentClicked);
        holder.userImg.setOnClickListener(this.onUserClicked);
        holder.userNameTxt.setOnClickListener(this.onUserClicked);
        return holder;
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        boolean isWrappedPresent;
        boolean isOverlayPresent;
        boolean isMysteryPresent;
        boolean isPrivatePresent;
        boolean isSendPresentBtnHidden;
        long trackId;
        LayoutParams lp;
        PresentInfo present = (PresentInfo) this.presents.get(position);
        UserInfo userInfo = getUserInfo(present);
        if (present.isWrapped) {
            if (!present.presentType.isOverlay) {
                isWrappedPresent = true;
                isOverlayPresent = present.presentType.isOverlay;
                boolean isBadgePresent = (present.receiver != null || present.sender == null) ? false : TextUtils.equals(present.sender.getId(), present.receiver.getId());
                isMysteryPresent = present.isMystery;
                isPrivatePresent = present.isPrivate;
                boolean isAcceptedPresent = this.direction != Direction.ACCEPTED;
                boolean isFriendPrivatePresent = isPrivatePresent && this.friendMode;
                boolean isAcceptedMysteryPresent = isMysteryPresent && isAcceptedPresent;
                boolean isBtnThanksState = this.friendMode && isAcceptedPresent;
                boolean isUserTitleHidden = isFriendPrivatePresent || isAcceptedMysteryPresent || userInfo == null;
                isSendPresentBtnHidden = (isBtnThanksState && (isBadgePresent || isUserTitleHidden)) || (!isBtnThanksState && (isWrappedPresent || isOverlayPresent));
                holder.dateTxt.setText(getDate(present));
                setTextSafely(holder.message, present.message);
                if (userInfo != null || isUserTitleHidden) {
                    holder.userNameTxt.setPadding((int) Utils.dipToPixels(12.0f), 0, 0, 0);
                    if (!isPrivatePresent || isMysteryPresent) {
                        if (present.presentType.isLive) {
                            setBoldText(holder.userNameTxt, LocalizationManager.getString(this.activity, 2131166534));
                        } else {
                            setBoldText(holder.userNameTxt, LocalizationManager.getString(this.activity, 2131166531));
                        }
                        holder.userNameTxt.setVisibility(0);
                        holder.dateTxt.setPadding(0, 0, 0, 0);
                    } else if (present.senderLabel != null) {
                        setBoldText(holder.userNameTxt, present.senderLabel);
                        holder.userNameTxt.setVisibility(0);
                        holder.dateTxt.setPadding(0, 0, 0, 0);
                    } else {
                        holder.userNameTxt.setVisibility(8);
                        holder.dateTxt.setPadding(0, (int) Utils.dipToPixels(6.0f), 0, 0);
                    }
                    holder.userImg.setVisibility(8);
                } else {
                    setBoldText(holder.userNameTxt, userInfo.name);
                    holder.userNameTxt.setVisibility(0);
                    holder.userNameTxt.setPadding((int) Utils.dipToPixels(8.0f), 0, 0, 0);
                    holder.userImg.setVisibility(0);
                    ImageViewManager.getInstance().displayImage(userInfo.getPicUrl(), holder.userImg, userInfo.genderType == UserGenderType.MALE, null);
                    holder.dateTxt.setPadding(0, 0, 0, 0);
                }
                if (isSendPresentBtnHidden) {
                    holder.presentImg.setPadding(0, 0, 0, 0);
                    holder.actionTxt.setVisibility(0);
                    holder.actionTxt.setText(getActionBtnText(present));
                    if (holder.separator != null) {
                        holder.separator.setVisibility(0);
                    }
                } else {
                    holder.presentImg.setPadding(0, 0, 0, present.presentType.isLive ? (int) Utils.dipToPixels(14.0f) : 0);
                    holder.actionTxt.setVisibility(8);
                    if (holder.separator != null) {
                        holder.separator.setVisibility(8);
                    }
                }
                if (getItemViewType(position) == 1) {
                    setPresentSize(present.presentType, holder.presentImg);
                    trackId = getTrackId(present);
                    if (trackId != 0 || present.isWrapped) {
                        holder.trackView.setVisibility(8);
                    } else {
                        holder.trackView.setPlayState();
                        holder.trackView.setPlayerStateHolder(this.playerStateHolder);
                        holder.trackView.setTrackId(trackId);
                        holder.trackView.setOnPlayTrackListener(this.onPlayTrackListener);
                        holder.trackView.setVisibility(0);
                    }
                }
                lp = holder.presentImg.getLayoutParams();
                holder.presentImg.setPresentType(present.presentType, new Point(lp.width, lp.height));
                holder.optionsBtn.setTag(present);
                holder.presentImg.setTag(present);
                holder.userNameTxt.setTag(userInfo);
                holder.userImg.setTag(userInfo);
                holder.actionTxt.setTag(present);
            }
        }
        isWrappedPresent = false;
        isOverlayPresent = present.presentType.isOverlay;
        if (present.receiver != null) {
        }
        isMysteryPresent = present.isMystery;
        isPrivatePresent = present.isPrivate;
        if (this.direction != Direction.ACCEPTED) {
        }
        if (!isPrivatePresent) {
        }
        if (!isMysteryPresent) {
        }
        if (this.friendMode) {
        }
        if (!isFriendPrivatePresent) {
        }
        if (!isBtnThanksState) {
        }
        holder.dateTxt.setText(getDate(present));
        setTextSafely(holder.message, present.message);
        if (userInfo != null) {
        }
        holder.userNameTxt.setPadding((int) Utils.dipToPixels(12.0f), 0, 0, 0);
        if (isPrivatePresent) {
        }
        if (present.presentType.isLive) {
            setBoldText(holder.userNameTxt, LocalizationManager.getString(this.activity, 2131166531));
        } else {
            setBoldText(holder.userNameTxt, LocalizationManager.getString(this.activity, 2131166534));
        }
        holder.userNameTxt.setVisibility(0);
        holder.dateTxt.setPadding(0, 0, 0, 0);
        holder.userImg.setVisibility(8);
        if (isSendPresentBtnHidden) {
            holder.presentImg.setPadding(0, 0, 0, 0);
            holder.actionTxt.setVisibility(0);
            holder.actionTxt.setText(getActionBtnText(present));
            if (holder.separator != null) {
                holder.separator.setVisibility(0);
            }
        } else {
            if (present.presentType.isLive) {
            }
            holder.presentImg.setPadding(0, 0, 0, present.presentType.isLive ? (int) Utils.dipToPixels(14.0f) : 0);
            holder.actionTxt.setVisibility(8);
            if (holder.separator != null) {
                holder.separator.setVisibility(8);
            }
        }
        if (getItemViewType(position) == 1) {
            setPresentSize(present.presentType, holder.presentImg);
            trackId = getTrackId(present);
            if (trackId != 0) {
            }
            holder.trackView.setVisibility(8);
        }
        lp = holder.presentImg.getLayoutParams();
        holder.presentImg.setPresentType(present.presentType, new Point(lp.width, lp.height));
        holder.optionsBtn.setTag(present);
        holder.presentImg.setTag(present);
        holder.userNameTxt.setTag(userInfo);
        holder.userImg.setTag(userInfo);
        holder.actionTxt.setTag(present);
    }

    public int getItemCount() {
        return this.presents.size();
    }

    public int getItemViewType(int position) {
        if (((PresentInfo) this.presents.get(position)).presentType.isLive) {
            return 2;
        }
        return 1;
    }

    @Nullable
    private UserInfo getUserInfo(@NonNull PresentInfo present) {
        if (this.direction == Direction.ACCEPTED) {
            if (present.sender != null) {
                return present.sender;
            }
            return null;
        } else if (present.receiver != null) {
            return present.receiver;
        } else {
            return null;
        }
    }

    @NonNull
    private String getDate(@NonNull PresentInfo present) {
        Date date = present.presentTime;
        if (date == null) {
            return "";
        }
        return StringUtils.uppercaseFirst(DateFormatter.formatDeltaTimePast(this.activity, date.getTime(), false, false));
    }

    @NonNull
    private String getActionBtnText(@NonNull PresentInfo present) {
        if (this.direction == Direction.ACCEPTED && !this.friendMode) {
            return LocalizationManager.getString(this.activity, 2131166401);
        }
        if (present.presentType.isLive) {
            return LocalizationManager.getString(this.activity, 2131166399);
        }
        return LocalizationManager.getString(this.activity, 2131166398);
    }

    public long getItemId(int position) {
        String strId = ((PresentInfo) this.presents.get(position)).id;
        try {
            return Long.valueOf(strId).longValue();
        } catch (NumberFormatException e) {
            Logger.m176e("can't parse id: '" + strId + "'");
            return 0;
        }
    }

    private int getLayoutId(int viewType) {
        if (viewType == 1) {
            return 2130903260;
        }
        return 2130903261;
    }

    private long getTrackId(@NonNull PresentInfo presentInfo) {
        long j = 0;
        if (!TextUtils.isEmpty(presentInfo.trackId)) {
            try {
                j = Long.parseLong(presentInfo.trackId);
            } catch (NumberFormatException e) {
            }
        }
        return j;
    }

    private void setPresentSize(@NonNull PresentType presentType, @NonNull CompositePresentView presentImg) {
        LayoutParams lp = presentImg.getLayoutParams();
        if (Math.min(presentType.photoSize.getWidth(), presentType.photoSize.getHeight()) <= 70) {
            lp.width = this.activity.getResources().getDimensionPixelOffset(2131231156);
            lp.height = this.activity.getResources().getDimensionPixelOffset(2131231156);
        } else {
            lp.width = this.activity.getResources().getDimensionPixelOffset(2131231136);
            lp.height = this.activity.getResources().getDimensionPixelOffset(2131231136);
        }
        presentImg.setLayoutParams(lp);
    }

    private static void setTextSafely(@Nullable TextView textView, @Nullable String text) {
        if (textView == null) {
            return;
        }
        if (text != null) {
            textView.setText(text);
        } else {
            textView.setText("");
        }
    }

    private static void setBoldText(@Nullable TextView textView, @Nullable String text) {
        if (textView == null) {
            return;
        }
        if (text != null) {
            textView.setText(Html.fromHtml(String.format("<b>%s</b>", new Object[]{text})));
            return;
        }
        textView.setText("");
    }
}
