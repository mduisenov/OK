package ru.ok.android.ui.activity.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Window;
import ru.ok.android.fragments.music.AlbumFragment;
import ru.ok.android.fragments.music.ArtistFragment;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.fragments.music.SimilarPlayListFragment;
import ru.ok.android.fragments.music.collections.MusicCollectionFragment;
import ru.ok.android.fragments.music.tuners.MusicTunersFragment;
import ru.ok.android.fragments.registr.NotLoggedInWebFragment;
import ru.ok.android.fragments.registr.NotLoggedInWebFragment.Page;
import ru.ok.android.slidingmenu.SlidingMenuStrategy;
import ru.ok.android.ui.activity.ShowDialogFragmentActivity;
import ru.ok.android.ui.activity.ShowFragmentActivity;
import ru.ok.android.ui.activity.main.ActivityExecutor.SoftInputType;
import ru.ok.android.ui.fragments.MusicUsersFragment;
import ru.ok.android.ui.fragments.SearchMusicFragment;
import ru.ok.android.ui.fragments.users.UserTopicsFragment;
import ru.ok.android.ui.users.activity.ProfileUserActivity;
import ru.ok.android.ui.users.fragments.profiles.ProfileUserFragment;
import ru.ok.android.ui.utils.HomeButtonUtils;
import ru.ok.android.ui.video.VideoWebFragment;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.NavigationHelper.FragmentLocation;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;

public class OdklSubActivity extends ShowFragmentActivity {
    public static final String FLAG_LAUNCH_NEW_ACTIVITY_ON_NEW_INTENT;
    private Type selectedType;

    static {
        FLAG_LAUNCH_NEW_ACTIVITY_ON_NEW_INTENT = OdklSubActivity.class.getSimpleName() + ".LAUNCH_NEW_ACTIVITY";
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        int i = 16;
        super.onCreateLocalized(savedInstanceState);
        setContentView(2130903355);
        Intent intent = getIntent();
        Class clazz = (Class) intent.getSerializableExtra("key_class_name");
        if (clazz != null) {
            Bundle bundle = intent.getBundleExtra("key_argument_name");
            if (bundle == null) {
                bundle = new Bundle();
            }
            String value = intent.getStringExtra("key_location_type");
            FragmentLocation fragmentLocation = value == null ? FragmentLocation.center : FragmentLocation.valueOf(value);
            boolean isNeedToolbar = intent.getBooleanExtra("key_toolbar_visible", false);
            String tag = intent.getStringExtra("key_fragment_tag");
            ActivityExecutor activityExecutor = new ActivityExecutor(this, clazz);
            activityExecutor.setArguments(bundle).setFragmentLocation(fragmentLocation).setAddToBackStack(false).setNeedToolbar(isNeedToolbar).setTag(tag);
            showFragment(activityExecutor);
        }
        if (isHideHomeButton()) {
            HomeButtonUtils.hideHomeButton(this);
        } else {
            HomeButtonUtils.showHomeButton(this);
        }
        if (getIntent().hasExtra("key_soft_input_type")) {
            SoftInputType softInputType = SoftInputType.valueOf(getIntent().getStringExtra("key_soft_input_type"));
            Window window = getWindow();
            if (softInputType == SoftInputType.PAN) {
                i = 32;
            }
            window.setSoftInputMode(i);
        } else {
            getWindow().setSoftInputMode(16);
        }
        if (SlidingMenuStrategy.isFromLeftMenu(this)) {
            HomeButtonUtils.hideHomeButton(this);
        }
        updateSlidingMenuSelection();
    }

    public boolean isNeedShowLeftMenu() {
        return getIntent().getBooleanExtra("key_sliding_menu_enable", true);
    }

    protected boolean isHideHomeButton() {
        return getIntent().getBooleanExtra("key_hide_home_buttom", false);
    }

    public Fragment showFragment(ActivityExecutor activityExecutor) {
        if (activityExecutor.isNeedToolbar()) {
            showTabbar(false);
            getWindow().setSoftInputMode(32);
        } else {
            setNeedShowTabbar(false);
        }
        processMusicFragments(activityExecutor);
        Fragment fragment = super.showFragment(activityExecutor);
        if (fragment != null) {
            updateSelectedType(fragment);
        }
        return fragment;
    }

    protected void processMusicFragments(ActivityExecutor activityExecutor) {
        if (DeviceUtils.getType(this) == DeviceLayoutType.LARGE && getSupportFragmentManager().findFragmentByTag("MUSIC_TAG") == null) {
            boolean isCollection;
            boolean isSearchMusic;
            int i;
            Class<? extends Fragment> clazz = activityExecutor.getFragmentClass();
            boolean isRadio = MusicTunersFragment.class.isAssignableFrom(clazz);
            boolean shouldShowMusicLeftPane = false | isRadio;
            if (shouldShowMusicLeftPane || !MusicCollectionFragment.class.isAssignableFrom(clazz)) {
                isCollection = false;
            } else {
                isCollection = true;
            }
            shouldShowMusicLeftPane |= isCollection;
            if (shouldShowMusicLeftPane || !SearchMusicFragment.class.isAssignableFrom(clazz)) {
                isSearchMusic = false;
            } else {
                isSearchMusic = true;
            }
            shouldShowMusicLeftPane |= isSearchMusic;
            if (shouldShowMusicLeftPane || !(ArtistFragment.class.isAssignableFrom(clazz) || AlbumFragment.class.isAssignableFrom(clazz) || SimilarPlayListFragment.class.isAssignableFrom(clazz))) {
                i = 0;
            } else {
                i = 1;
            }
            if (shouldShowMusicLeftPane | i) {
                String selectionUid = null;
                if (isRadio) {
                    selectionUid = "-64";
                } else if (isCollection) {
                    selectionUid = null;
                } else if (isSearchMusic) {
                    selectionUid = null;
                }
                super.showFragment(new ActivityExecutor(this, MusicUsersFragment.class).setArguments(MusicUsersFragment.newArguments(true, selectionUid, MusicFragmentMode.STANDARD)).setFragmentLocation(FragmentLocation.left).setActivityResult(false).setTag("MUSIC_TAG").setSoftInputType(SoftInputType.PAN).setAddToBackStack(false));
            }
        }
    }

    public static void startActivityShowFragment(Activity activity, Class<? extends Fragment> aClass, Bundle arguments, boolean fromLeftMenu, boolean isNeedToolbar) {
        ActivityExecutor builder = new ActivityExecutor(activity, aClass);
        builder.setActivityFromMenu(true);
        builder.setArguments(arguments);
        builder.setActivityFromMenu(fromLeftMenu);
        builder.setNeedToolbar(isNeedToolbar);
        builder.execute();
    }

    public static void startActivityShowFragment(Activity activity, Class<? extends Fragment> aClass, Bundle arguments, boolean fromLeftMenu) {
        startActivityShowFragment(activity, (Class) aClass, arguments, fromLeftMenu, true);
    }

    public static void startActivityShowFragment(Activity activity, Class<? extends Fragment> aClass, Bundle arguments) {
        startActivityShowFragment(activity, (Class) aClass, arguments, false);
    }

    public static void startActivityShowFragment(Activity activity, Class<? extends Fragment> aClass, Bundle arguments, Bundle options) {
        ActivityExecutor builder = new ActivityExecutor(activity, aClass);
        builder.setArguments(arguments);
        builder.setActivityCompatOption(options);
        builder.execute();
    }

    public static void startActivityShowFragment(Activity activity, Class<? extends Fragment> aClass, Bundle arguments, FragmentLocation fragmentLocation) {
        startActivityShowFragment(activity, (Class) aClass, arguments, fragmentLocation, SoftInputType.RESIZE);
    }

    public static void startActivityShowFragment(Activity activity, Class<? extends Fragment> aClass, Bundle arguments, SoftInputType softInputType) {
        ActivityExecutor builder = new ActivityExecutor(activity, aClass);
        builder.setArguments(arguments);
        builder.setSoftInputType(softInputType);
        builder.execute();
    }

    public static void startActivityShowFragment(Activity activity, Class<? extends Fragment> aClass, Bundle arguments, FragmentLocation fragmentLocation, SoftInputType softInputType) {
        ActivityExecutor builder = new ActivityExecutor(activity, aClass);
        builder.setArguments(arguments);
        builder.setSoftInputType(softInputType);
        builder.setFragmentLocation(fragmentLocation);
        builder.execute();
    }

    public static Intent createIntent(Context context, Class<? extends Fragment> aClass, Bundle arguments, FragmentLocation fragmentLocation) {
        Class activityClass;
        if (DeviceUtils.getType(context) == DeviceLayoutType.LARGE && arguments != null && arguments.getBoolean("fragment_is_dialog", false)) {
            activityClass = ShowDialogFragmentActivity.class;
        } else if (ProfileUserFragment.class.isAssignableFrom(aClass)) {
            activityClass = ProfileUserActivity.class;
        } else {
            activityClass = OdklSubActivity.class;
        }
        Intent intent = new Intent(context, activityClass);
        intent.putExtra("key_class_name", aClass);
        intent.putExtra("key_argument_name", arguments);
        intent.putExtra("key_location_type", fragmentLocation.toString());
        return intent;
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(FLAG_LAUNCH_NEW_ACTIVITY_ON_NEW_INTENT, false)) {
            intent.setFlags((intent.getFlags() & -536870913) & -67108865);
            startActivity(intent);
        }
    }

    public boolean isUseTabbar() {
        return true;
    }

    private void updateSelectedType(@NonNull Fragment fragment) {
        Class<? extends Fragment> aClass = fragment.getClass();
        if (UserTopicsFragment.class.equals(aClass)) {
            this.selectedType = Type.share;
        } else if (VideoWebFragment.class.equals(aClass)) {
            this.selectedType = Type.videos;
        } else if (NotLoggedInWebFragment.class.equals(aClass)) {
            Page page = ((NotLoggedInWebFragment) fragment).getPage();
            if (page == Page.FeedBack) {
                this.selectedType = Type.feedback;
            } else if (page == Page.Faq) {
                this.selectedType = Type.faq;
            }
        }
    }

    protected Type getSlidingMenuSelectedItem() {
        return this.selectedType != null ? this.selectedType : super.getSlidingMenuSelectedItem();
    }
}
