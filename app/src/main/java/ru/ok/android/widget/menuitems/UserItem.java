package ru.ok.android.widget.menuitems;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Build.VERSION;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import ru.ok.android.fresco.postprocessors.ImageBlurCenterToCropPostprocessor;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity;
import ru.ok.android.slidingmenu.SlidingMenuStrategy;
import ru.ok.android.slidingmenu.SlidingMenuStrategy.StrategyType;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.adapters.ScrollLoadBlocker;
import ru.ok.android.ui.custom.NotificationsView;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.ui.custom.imageview.AvatarImageView.OnSetImageUriListener;
import ru.ok.android.utils.DimenUtils;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;
import ru.ok.android.widget.menuitems.StandardItem.BubbleState;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public final class UserItem extends StandardItem implements OnClickListener {
    private final int AVATAR_TRANSLATION_X_CLOSED;
    private final int AVATAR_TRANSLATION_Y_CLOSED;
    private final int NAME_TEXT_TRANSLATION_OFFSET_CLOSED;
    private int mNotificationsCounter;
    private boolean newUser;
    private final boolean showHamburgerIcon;
    private UserInfo userInfo;

    /* renamed from: ru.ok.android.widget.menuitems.UserItem.1 */
    class C15021 implements OnSetImageUriListener {
        final /* synthetic */ SimpleDraweeView val$bgImageView;
        final /* synthetic */ View val$finalView;

        C15021(View view, SimpleDraweeView simpleDraweeView) {
            this.val$finalView = view;
            this.val$bgImageView = simpleDraweeView;
        }

        public void onSetImageBitmapUri(Uri uri) {
            UserItem.this.onAvatarUri(this.val$finalView, this.val$bgImageView, uri);
        }
    }

    /* renamed from: ru.ok.android.widget.menuitems.UserItem.2 */
    class C15032 implements Runnable {
        final /* synthetic */ SimpleDraweeView val$imageView;
        final /* synthetic */ Uri val$uri;
        final /* synthetic */ View val$view;

        C15032(View view, SimpleDraweeView simpleDraweeView, Uri uri) {
            this.val$view = view;
            this.val$imageView = simpleDraweeView;
            this.val$uri = uri;
        }

        public void run() {
            UserItem.this.setBackground(this.val$view, this.val$imageView, this.val$uri);
        }
    }

    class UserItemViewHolder extends Holder {
        public View avatarContent;
        public SimpleDraweeView bgImageView;
        public View hamburgerMenuIcon;
        public AvatarImageView imageView;
        public NotificationsView notificationsView;

        public UserItemViewHolder(int type, int position) {
            super(type, position);
        }
    }

    public UserItem(int height, OdklSlidingMenuFragmentActivity activity) {
        super(activity, 2130838446, 2131166602, Type.user, height, BubbleState.green_tablet);
        this.userInfo = null;
        this.showHamburgerIcon = SlidingMenuStrategy.getStrategyType() == StrategyType.Custom;
        Resources resources = activity.getResources();
        this.AVATAR_TRANSLATION_X_CLOSED = -resources.getDimensionPixelSize(2131231077);
        this.AVATAR_TRANSLATION_Y_CLOSED = resources.getDimensionPixelSize(2131231078);
        this.NAME_TEXT_TRANSLATION_OFFSET_CLOSED = -resources.getDimensionPixelSize(2131231080);
    }

    protected void setText(TextView textView) {
        if (this.userInfo != null) {
            textView.setText((this.userInfo.firstName == null ? "" : this.userInfo.firstName) + " " + (this.userInfo.lastName == null ? "" : this.userInfo.lastName));
            return;
        }
        textView.setText(null);
    }

    public void setNotificationsCounter(int notificationsCounter) {
        this.mNotificationsCounter = notificationsCounter;
        View view = getCachedView();
        if (view != null) {
            getView(LocalizationManager.from(view.getContext()), view, ((Holder) view.getTag()).getPosition(), this.lastSelectedItem);
        }
    }

    public View getView(LocalizationManager inflater, View view, int position, Type selectedItem) {
        UserItemViewHolder holder;
        boolean z = true;
        if (view == null) {
            view = LocalizationManager.inflate(inflater.getContext(), 2130903319, null, false);
            holder = createViewHolder(getType(), position);
            holder.name = (TextView) view.findViewById(2131625064);
            holder.hamburgerMenuIcon = view.findViewById(2131625079);
            holder.counter = (TextView) view.findViewById(2131625065);
            holder.imageView = (AvatarImageView) view.findViewById(2131625062);
            holder.bgImageView = (SimpleDraweeView) view.findViewById(2131625077);
            holder.imageView.setClickable(false);
            holder.greenCounter = (TextView) view.findViewById(2131625076);
            holder.avatarContent = view.findViewById(2131625081);
            holder.notificationsView = (NotificationsView) view.findViewById(2131625080);
            if (VERSION.SDK_INT >= 21) {
                view.findViewById(2131625078).setMinimumHeight(DimenUtils.getStatusBarHeight(view.getContext()));
            }
            view.setTag(holder);
        } else {
            holder = (UserItemViewHolder) view.getTag();
            holder.position = position;
        }
        this.holder = holder;
        setCounterText(this.mCounter, this.mCounterTwo, holder.counter, holder.greenCounter, this.mBubbleState);
        setText(holder.name);
        ViewUtil.setTouchDelegate(holder.hamburgerMenuIcon, getActivity().getResources().getDimensionPixelSize(2131231177));
        if (this.userInfo != null) {
            boolean z2;
            View finalView = view;
            holder.imageView.setOnSetImageUriListener(new C15021(finalView, holder.bgImageView));
            ImageViewManager instance = ImageViewManager.getInstance();
            String str = this.userInfo.picUrl;
            AvatarImageView avatarImageView = holder.imageView;
            if (this.userInfo.genderType == UserGenderType.MALE) {
                z2 = true;
            } else {
                z2 = false;
            }
            instance.displayImage(str, avatarImageView, z2, ScrollLoadBlocker.forIdleAndTouchIdle(), this.height);
        }
        if (this.showHamburgerIcon) {
            holder.hamburgerMenuIcon.setOnClickListener(this);
            ViewUtil.visible(holder.hamburgerMenuIcon);
            View view2 = holder.notificationsView;
            if (this.mNotificationsCounter <= 0) {
                z = false;
            }
            ViewUtil.setVisibility(view2, z);
        }
        return view;
    }

    private void setBackground(View view, SimpleDraweeView bgImageView, Uri uri) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).setPostprocessor(new ImageBlurCenterToCropPostprocessor(uri, view.getMeasuredWidth(), view.getMeasuredHeight(), 68, 50)).build();
        bgImageView.setMinimumHeight(view.getMeasuredHeight());
        bgImageView.setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setOldController(bgImageView.getController())).setImageRequest(request)).build());
    }

    private void onAvatarUri(View view, SimpleDraweeView imageView, Uri uri) {
        if (view.getMeasuredHeight() != 0) {
            setBackground(view, imageView, uri);
        } else {
            view.post(new C15032(view, imageView, uri));
        }
    }

    public int getType() {
        return 3;
    }

    public void setCurrentUser(UserInfo userInfo) {
        this.userInfo = userInfo;
        invalidateView();
    }

    public UserItemViewHolder createViewHolder(int type, int position) {
        return new UserItemViewHolder(type, position);
    }

    private float valueTransition(float valueStart, float valueEnd, float ratio) {
        return ((valueEnd - valueStart) * ratio) + valueStart;
    }

    public void onSlidingMenuChangedOpeningRatio(float openingRatio) {
        float menuAlpha = 0.0f;
        if (this.holder != null && (this.holder instanceof UserItemViewHolder)) {
            UserItemViewHolder userItemViewHolder = this.holder;
            float scale = valueTransition(0.56f, 1.0f, openingRatio);
            userItemViewHolder.imageView.setScaleX(scale);
            userItemViewHolder.imageView.setScaleY(scale);
            userItemViewHolder.avatarContent.setTranslationX(valueTransition((float) this.AVATAR_TRANSLATION_X_CLOSED, 0.0f, openingRatio));
            userItemViewHolder.avatarContent.setTranslationY(valueTransition((float) this.AVATAR_TRANSLATION_Y_CLOSED, 0.0f, openingRatio));
            userItemViewHolder.name.setTranslationX(valueTransition((float) this.NAME_TEXT_TRANSLATION_OFFSET_CLOSED, 0.0f, openingRatio));
            userItemViewHolder.name.setAlpha(openingRatio);
            if (openingRatio <= 0.5f) {
                menuAlpha = 1.0f - (2.0f * openingRatio);
            }
            if (userItemViewHolder.hamburgerMenuIcon != null) {
                userItemViewHolder.hamburgerMenuIcon.setAlpha(menuAlpha);
            }
            if (userItemViewHolder.notificationsView != null) {
                userItemViewHolder.notificationsView.setAlpha(menuAlpha);
            }
            userItemViewHolder.bgImageView.setAlpha(openingRatio);
        }
    }

    public void onClick(View v) {
        if (v.getId() == 2131625079) {
            StatisticManager.getInstance().addStatisticEvent("left_menu-open_but_tablet", new Pair[0]);
            SlidingMenuHelper.openMenu(getActivity());
        }
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public boolean isNewUser() {
        return this.newUser;
    }
}
