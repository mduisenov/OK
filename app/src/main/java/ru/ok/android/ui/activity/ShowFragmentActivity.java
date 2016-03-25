package ru.ok.android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import java.util.List;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.fragments.SameFragmentWithoutBundleEqualCheck;
import ru.ok.android.ui.fragments.StubFragment;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper.FragmentLocation;
import ru.ok.android.utils.NavigationHelper.FragmentsPresenter;
import ru.ok.android.utils.Utils;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;

public class ShowFragmentActivity extends OdklSlidingMenuFragmentActivity implements FragmentsPresenter {

    /* renamed from: ru.ok.android.ui.activity.ShowFragmentActivity.1 */
    static /* synthetic */ class C05591 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$utils$NavigationHelper$FragmentLocation;

        static {
            $SwitchMap$ru$ok$android$utils$NavigationHelper$FragmentLocation = new int[FragmentLocation.values().length];
            try {
                $SwitchMap$ru$ok$android$utils$NavigationHelper$FragmentLocation[FragmentLocation.left.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$NavigationHelper$FragmentLocation[FragmentLocation.right.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$NavigationHelper$FragmentLocation[FragmentLocation.right_small.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$NavigationHelper$FragmentLocation[FragmentLocation.top.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        boolean actionBarIsVisible = getIntent().getBooleanExtra("key_action_bar_visible", true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        if (actionBarIsVisible) {
            actionBar.show();
        } else {
            actionBar.hide();
        }
    }

    public void hideAll(boolean onActivityResult) {
        if (!onActivityResult) {
            getSupportFragmentManager().popBackStack(null, 1);
        }
        FragmentTransaction transaction = null;
        if (getSupportFragmentManager().getFragments() != null) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (!(fragment == null || fragment.isHidden())) {
                    if (transaction == null) {
                        transaction = getSupportFragmentManager().beginTransaction();
                    }
                    transaction.hide(fragment);
                }
            }
        }
        if (transaction != null) {
            if (onActivityResult) {
                transaction.commitAllowingStateLoss();
            } else {
                transaction.commit();
            }
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    @Nullable
    public Fragment showFragment(ActivityExecutor activityExecutor) {
        Logger.m173d(">>> fragment=%s requestedLocation=%s addToBackStack=%s", activityExecutor.getFragmentClass().getSimpleName(), activityExecutor.getFragmentLocation(), Boolean.valueOf(activityExecutor.isAddToBackStack()));
        boolean addToBackStack = activityExecutor.isAddToBackStack();
        try {
            Fragment fragment = (Fragment) activityExecutor.getFragmentClass().newInstance();
            fragment.setArguments(activityExecutor.getArguments());
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            int containerResId = getSuitableContainerResId(activityExecutor.getFragmentLocation());
            String str = "destination containerId=%s";
            Object[] objArr = new Object[1];
            objArr[0] = Logger.isLoggingEnable() ? getResources().getResourceName(containerResId) : Integer.toString(containerResId);
            Logger.m173d(str, objArr);
            Fragment sameFragment = null;
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragmentBuf : fragments) {
                    if (fragmentBuf != null && fragmentBuf.getClass().equals(activityExecutor.getFragmentClass())) {
                        if (fragmentBuf.getClass().getAnnotation(SameFragmentWithoutBundleEqualCheck.class) != null || Utils.equalBundles(activityExecutor.getArguments(), fragmentBuf.getArguments())) {
                            sameFragment = fragmentBuf;
                            break;
                        }
                    }
                }
            }
            if (sameFragment == null && !TextUtils.isEmpty(activityExecutor.getTag())) {
                sameFragment = getSupportFragmentManager().findFragmentByTag(activityExecutor.getTag());
                if (!(sameFragment == null || fragment.getClass().isAssignableFrom(sameFragment.getClass()))) {
                    sameFragment = null;
                }
                if (!(sameFragment == null || Utils.equalBundles(sameFragment.getArguments(), fragment.getArguments()))) {
                    sameFragment = null;
                }
            }
            str = "previous fragment by tag: %s";
            objArr = new Object[1];
            objArr[0] = sameFragment == null ? null : sameFragment.getClass().getSimpleName();
            Logger.m173d(str, objArr);
            if (sameFragment == null || !sameFragment.isAdded()) {
                String resourceName;
                Fragment currentFragmentInContainer = fragmentManager.findFragmentById(containerResId);
                addToBackStack &= !StubFragment.class.isAssignableFrom(activityExecutor.getFragmentClass()) ? 1 : 0;
                if (containerResId == 2131625150 || containerResId == 2131625152) {
                    if (currentFragmentInContainer != null) {
                        Logger.m173d("new fragment will replace current fragment: %s", currentFragmentInContainer.getClass().getSimpleName());
                    }
                    transaction.replace(containerResId, fragment, activityExecutor.getTag());
                } else if (containerResId == 2131625148) {
                    if (currentFragmentInContainer != null) {
                        Logger.m173d("will add new fragment on top of current fragment: %s", currentFragmentInContainer.getClass().getSimpleName());
                    }
                    transaction.replace(containerResId, fragment, activityExecutor.getTag());
                } else {
                    if (currentFragmentInContainer != null) {
                        Logger.m173d("will add new fragment on top of current fragment: %s", currentFragmentInContainer.getClass().getSimpleName());
                    }
                    transaction.add(containerResId, fragment, activityExecutor.getTag());
                }
                if (addToBackStack) {
                    Logger.m173d("adding new fragment to back stack: %s", activityExecutor.getFragmentClass().getSimpleName());
                    transaction.addToBackStack(null);
                }
                str = "<<< new fragment %s is displayed in container %s";
                objArr = new Object[2];
                objArr[0] = activityExecutor.getFragmentClass().getSimpleName();
                if (Logger.isLoggingEnable()) {
                    resourceName = getResources().getResourceName(containerResId);
                } else {
                    resourceName = Integer.toString(containerResId);
                }
                objArr[1] = resourceName;
                Logger.m173d(str, objArr);
            } else {
                for (Fragment fragmentBuf2 : fragments) {
                    if (!(fragmentBuf2 == null || fragmentBuf2 == sameFragment || fragmentBuf2.isHidden() || containerResId != fragmentBuf2.getId())) {
                        Logger.m173d("hiding fragment: %s", fragmentBuf2.getClass().getSimpleName());
                        transaction.hide(fragmentBuf2);
                    }
                }
                Logger.m172d("<<< showing previously hidden fragment by tag.");
                transaction.show(sameFragment);
            }
            try {
                if (activityExecutor.isActivityResult()) {
                    transaction.commitAllowingStateLoss();
                } else {
                    transaction.commit();
                }
                getSupportFragmentManager().executePendingTransactions();
                return fragment;
            } catch (Throwable e) {
                Logger.m178e(e);
                return fragment;
            }
        } catch (Throwable e2) {
            Logger.m177e("<<< Failed to instantiate fragment: %s", e2);
            Logger.m178e(e2);
            return null;
        }
    }

    public int getSuitableContainerResId(FragmentLocation requestedFragmentLocation) {
        int res = 2131624880;
        switch (C05591.$SwitchMap$ru$ok$android$utils$NavigationHelper$FragmentLocation[requestedFragmentLocation.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                res = 2131625149;
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                res = 2131625150;
                if (findViewById(2131625150) == null) {
                    res = 2131625149;
                    break;
                }
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                res = 2131625152;
                if (findViewById(2131625152) == null) {
                    return 0;
                }
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                res = 2131625148;
                break;
        }
        if (findViewById(res) == null) {
            res = 2131624880;
        }
        return res;
    }

    public boolean canShowFragmentOnLocation(FragmentLocation fragmentLocation) {
        boolean z = false;
        if (fragmentLocation == FragmentLocation.top) {
            return true;
        }
        boolean canShowBuf = true;
        if (fragmentLocation == FragmentLocation.left) {
            if (findViewById(getSuitableContainerResId(FragmentLocation.center)) == null || getSupportFragmentManager().findFragmentById(getSuitableContainerResId(FragmentLocation.center)) != null) {
                canShowBuf = false;
            } else {
                canShowBuf = true;
            }
        } else if (fragmentLocation == FragmentLocation.center) {
            canShowBuf = findViewById(getSuitableContainerResId(FragmentLocation.left)) != null && getSupportFragmentManager().findFragmentById(getSuitableContainerResId(FragmentLocation.left)) == null;
        }
        int id = getSuitableContainerResId(fragmentLocation);
        if (((findViewById(id) != null && getSupportFragmentManager().findFragmentById(id) == null) || ((id == 2131625150 || id == 2131625152) && DeviceUtils.getType(this) == DeviceLayoutType.LARGE)) && canShowBuf) {
            z = true;
        }
        return z;
    }

    protected Type getSlidingMenuSelectedItem() {
        return null;
    }
}
