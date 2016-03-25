package ru.ok.android.ui.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import ru.ok.android.fragments.ExternalUrlWebFragment;
import ru.ok.android.fragments.PaymentWebFragment;
import ru.ok.android.fragments.RecommendedUsersFragment;
import ru.ok.android.fragments.adman.AdmanBannersFragment;
import ru.ok.android.fragments.marks.MarksWebFragment;
import ru.ok.android.fragments.notification.NotificationWebFragment;
import ru.ok.android.ui.activity.AdmanBannersActivity;
import ru.ok.android.ui.activity.ExternalUrlActivity;
import ru.ok.android.ui.activity.FriendsActivity;
import ru.ok.android.ui.activity.GuestsActivity;
import ru.ok.android.ui.activity.MarksActivity;
import ru.ok.android.ui.activity.NotificationsActivity;
import ru.ok.android.ui.activity.PaymentActivity;
import ru.ok.android.ui.activity.ShowDialogFragmentActivity;
import ru.ok.android.ui.activity.ShowFragmentActivity;
import ru.ok.android.ui.fragments.messages.MessagesFragment;
import ru.ok.android.ui.fragments.users.UsersLikedBaseFragment;
import ru.ok.android.ui.messaging.activity.MessagesActivity;
import ru.ok.android.ui.nativeRegistration.CountryCodeListActivity;
import ru.ok.android.ui.nativeRegistration.CountryCodeListFragment;
import ru.ok.android.ui.nativeRegistration.TallTitleFullHeightShowDialogFragmentActivity;
import ru.ok.android.ui.nativeRegistration.UserAgreementFragment;
import ru.ok.android.ui.users.activity.RecommendedUsersActivity;
import ru.ok.android.ui.users.activity.UsersLikesActivity;
import ru.ok.android.ui.users.fragments.FragmentGuest;
import ru.ok.android.ui.users.fragments.FriendsTabFragment;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper.FragmentLocation;
import ru.ok.android.utils.NavigationHelper.FragmentsPresenter;
import ru.ok.android.utils.ThreadUtil;

public class ActivityExecutor {
    private boolean actionBarIsVisible;
    private Activity activity;
    private boolean addToBackStack;
    private Bundle arguments;
    private Class<? extends Fragment> clazz;
    private String customTitle;
    private FragmentLocation fragmentLocation;
    private boolean isActivityFromMenu;
    private boolean isActivityResult;
    private boolean isDefaultAnimationEnabled;
    private boolean isHideHomeButton;
    private boolean isNeedToolbar;
    private Bundle options;
    private int requestCode;
    private Fragment resultFragment;
    private boolean slidingMenuEnable;
    private SoftInputType softInputType;
    private String tag;

    /* renamed from: ru.ok.android.ui.activity.main.ActivityExecutor.1 */
    class C05731 implements Runnable {
        C05731() {
        }

        public void run() {
            Class activityClass;
            int i = 16;
            boolean friendsFragment = FriendsTabFragment.class.isAssignableFrom(ActivityExecutor.this.clazz);
            if ((ActivityExecutor.this.activity instanceof FragmentsPresenter) && !friendsFragment) {
                ShowFragmentActivity fragmentActivity = (ShowFragmentActivity) ActivityExecutor.this.activity;
                if (!ActivityExecutor.this.arguments.getBoolean("fragment_is_dialog", false) && fragmentActivity.canShowFragmentOnLocation(ActivityExecutor.this.fragmentLocation)) {
                    Logger.m173d("[showFragment] Displaying new fragment inside same activity: activity=%s, fragment=%s, fragmentLocation=%s", ActivityExecutor.this.activity.getClass().getSimpleName(), ActivityExecutor.this.clazz.getSimpleName(), ActivityExecutor.this.fragmentLocation);
                    fragmentActivity.showFragment(ActivityExecutor.this);
                    LayoutParams attrs = ActivityExecutor.this.activity.getWindow().getAttributes();
                    if (attrs.softInputMode == 0) {
                        Window window = ActivityExecutor.this.activity.getWindow();
                        if (ActivityExecutor.this.softInputType == SoftInputType.PAN) {
                            i = 32;
                        }
                        window.setSoftInputMode(i);
                        return;
                    } else if (attrs.softInputMode == 16 && ActivityExecutor.this.softInputType == SoftInputType.PAN) {
                        ActivityExecutor.this.activity.getWindow().setSoftInputMode(32);
                        return;
                    } else {
                        return;
                    }
                }
            }
            if (DeviceUtils.getType(ActivityExecutor.this.activity) == DeviceLayoutType.LARGE && ActivityExecutor.this.arguments.getBoolean("fragment_is_dialog", false)) {
                activityClass = ShowDialogFragmentActivity.class;
            } else if (friendsFragment) {
                activityClass = FriendsActivity.class;
            } else if (UsersLikedBaseFragment.class.isAssignableFrom(ActivityExecutor.this.clazz)) {
                activityClass = UsersLikesActivity.class;
            } else if (NotificationWebFragment.class.isAssignableFrom(ActivityExecutor.this.clazz)) {
                activityClass = NotificationsActivity.class;
            } else if (MarksWebFragment.class.isAssignableFrom(ActivityExecutor.this.clazz)) {
                activityClass = MarksActivity.class;
            } else if (FragmentGuest.class.isAssignableFrom(ActivityExecutor.this.clazz)) {
                activityClass = GuestsActivity.class;
            } else if (ExternalUrlWebFragment.class.isAssignableFrom(ActivityExecutor.this.clazz)) {
                activityClass = ExternalUrlActivity.class;
            } else if (PaymentWebFragment.class.isAssignableFrom(ActivityExecutor.this.clazz)) {
                activityClass = PaymentActivity.class;
            } else if (AdmanBannersFragment.class.isAssignableFrom(ActivityExecutor.this.clazz)) {
                activityClass = AdmanBannersActivity.class;
            } else if (MessagesFragment.class.isAssignableFrom(ActivityExecutor.this.clazz)) {
                activityClass = MessagesActivity.class;
            } else if (UserAgreementFragment.class.isAssignableFrom(ActivityExecutor.this.clazz)) {
                activityClass = TallTitleFullHeightShowDialogFragmentActivity.class;
            } else if (CountryCodeListFragment.class.isAssignableFrom(ActivityExecutor.this.clazz)) {
                activityClass = CountryCodeListActivity.class;
            } else if (RecommendedUsersFragment.class.isAssignableFrom(ActivityExecutor.this.clazz)) {
                activityClass = RecommendedUsersActivity.class;
            } else {
                activityClass = OdklSubActivity.class;
            }
            Intent intent = new Intent(ActivityExecutor.this.activity, activityClass);
            intent.putExtra("key_class_name", ActivityExecutor.this.clazz);
            intent.putExtra("key_argument_name", ActivityExecutor.this.arguments);
            intent.putExtra("key_location_type", ActivityExecutor.this.fragmentLocation.toString());
            intent.putExtra("key_action_bar_visible", ActivityExecutor.this.actionBarIsVisible);
            intent.putExtra("key_soft_input_type", ActivityExecutor.this.softInputType.name());
            intent.putExtra("key_toolbar_visible", ActivityExecutor.this.isNeedToolbar);
            intent.putExtra("key_activity_from_menu", ActivityExecutor.this.isActivityFromMenu);
            intent.putExtra("key_sliding_menu_enable", ActivityExecutor.this.slidingMenuEnable);
            intent.putExtra("key_hide_home_buttom", ActivityExecutor.this.isHideHomeButton);
            intent.putExtra("key_fragment_tag", ActivityExecutor.this.tag);
            intent.putExtra("key_custom_title", ActivityExecutor.this.customTitle);
            Logger.m173d("[showFragment] Starting new activity to display fragment: activity=%s, fragment=%s, fragmentLocation=%s", activityClass.getSimpleName(), ActivityExecutor.this.clazz.getSimpleName(), ActivityExecutor.this.fragmentLocation);
            if (ActivityExecutor.this.options == null && ActivityExecutor.this.isDefaultAnimationEnabled) {
                ActivityExecutor.this.options = ActivityOptionsCompat.makeCustomAnimation(ActivityExecutor.this.activity, 2130968590, 2130968591).toBundle();
            }
            try {
                if (ActivityExecutor.this.requestCode >= 0 && ActivityExecutor.this.resultFragment != null) {
                    ActivityExecutor.this.resultFragment.startActivityForResult(intent, ActivityExecutor.this.requestCode);
                } else if (ActivityExecutor.this.requestCode < 0 || ActivityExecutor.this.resultFragment != null) {
                    ActivityCompat.startActivity(ActivityExecutor.this.activity, intent, ActivityExecutor.this.options);
                } else {
                    ActivityCompat.startActivityForResult(ActivityExecutor.this.activity, intent, ActivityExecutor.this.requestCode, ActivityExecutor.this.options);
                }
            } catch (IllegalArgumentException ex) {
                Logger.m176e("Starting new activity IllegalArgumentException: " + ex.getMessage());
            }
        }
    }

    public enum SoftInputType {
        RESIZE,
        PAN
    }

    public ActivityExecutor(Activity activity, Class<? extends Fragment> clazz) {
        this.arguments = new Bundle();
        this.fragmentLocation = FragmentLocation.center;
        this.options = null;
        this.actionBarIsVisible = true;
        this.addToBackStack = true;
        this.slidingMenuEnable = true;
        this.softInputType = SoftInputType.RESIZE;
        this.isActivityResult = false;
        this.isNeedToolbar = true;
        this.isActivityFromMenu = false;
        this.isHideHomeButton = false;
        this.isDefaultAnimationEnabled = true;
        this.requestCode = -1;
        this.activity = activity;
        this.clazz = clazz;
    }

    public ActivityExecutor setSoftInputType(SoftInputType softInputType) {
        this.softInputType = softInputType;
        return this;
    }

    public ActivityExecutor setArguments(Bundle arguments) {
        if (arguments == null) {
            arguments = this.arguments;
        }
        this.arguments = arguments;
        return this;
    }

    public ActivityExecutor setFragmentLocation(FragmentLocation fragmentLocation) {
        this.fragmentLocation = fragmentLocation;
        return this;
    }

    public ActivityExecutor setActivityCompatOption(Bundle options) {
        this.options = options;
        return this;
    }

    public ActivityExecutor setSlidingMenuEnable(boolean slidingMenuEnable) {
        this.slidingMenuEnable = slidingMenuEnable;
        return this;
    }

    public ActivityExecutor setActionBarVisible(boolean visible) {
        this.actionBarIsVisible = visible;
        return this;
    }

    public ActivityExecutor setAddToBackStack(boolean addToBackStack) {
        this.addToBackStack = addToBackStack;
        return this;
    }

    public ActivityExecutor setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public ActivityExecutor setActivityResult(boolean activityResult) {
        this.isActivityResult = activityResult;
        return this;
    }

    public ActivityExecutor setNeedToolbar(boolean needToolbar) {
        this.isNeedToolbar = needToolbar;
        return this;
    }

    public ActivityExecutor setActivityFromMenu(boolean activityFromMenu) {
        this.isActivityFromMenu = activityFromMenu;
        return this;
    }

    public ActivityExecutor setHideHomeButton(boolean isHideHomeButton) {
        this.isHideHomeButton = isHideHomeButton;
        return this;
    }

    public ActivityExecutor setTallTitle(String customTitle) {
        this.customTitle = customTitle;
        return this;
    }

    public ActivityExecutor setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public ActivityExecutor setResultFragment(Fragment resultFragment) {
        this.resultFragment = resultFragment;
        return this;
    }

    public ActivityExecutor setDefaultAnimationEnabled(boolean isDefaultAnimationEnabled) {
        this.isDefaultAnimationEnabled = isDefaultAnimationEnabled;
        return this;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return this.clazz;
    }

    public Bundle getArguments() {
        return this.arguments;
    }

    public FragmentLocation getFragmentLocation() {
        return this.fragmentLocation;
    }

    public boolean isAddToBackStack() {
        return this.addToBackStack;
    }

    public SoftInputType getSoftInputType() {
        return this.softInputType;
    }

    public String getTag() {
        return this.tag;
    }

    public boolean isActivityResult() {
        return this.isActivityResult;
    }

    public boolean isNeedToolbar() {
        return this.isNeedToolbar;
    }

    public static final SoftInputType getSoftInputTypeFromFragment(Fragment fragment) {
        if (fragment.getArguments() == null) {
            return null;
        }
        String s = fragment.getArguments().getString("key_soft_input_type");
        if (TextUtils.isEmpty(s)) {
            return null;
        }
        return SoftInputType.valueOf(s);
    }

    public void execute() {
        if (this.activity != null) {
            this.arguments.putString("key_soft_input_type", this.softInputType.name());
            ThreadUtil.executeOnMain(new C05731());
        }
    }
}
