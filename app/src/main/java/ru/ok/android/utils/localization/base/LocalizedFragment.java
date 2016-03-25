package ru.ok.android.utils.localization.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.utils.localization.LocalizationManager;

public abstract class LocalizedFragment extends DialogFragment implements LocalizationSupportingView {
    private final LocalizedViewUtils _utils;

    protected abstract int getLayoutId();

    public LocalizedFragment() {
        this._utils = new LocalizedViewUtils(this);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this._utils.setRootResourceId(getLayoutId());
        this._utils.onCreate();
    }

    public void onDestroyView() {
        super.onDestroyView();
        this._utils.onDestroy();
    }

    public void onResume() {
        if (isActionBarAffecting()) {
            restoreActionBarBehavior();
        }
        super.onResume();
    }

    public void restoreActionBarBehavior() {
        if (getActivity() != null && getParentFragment() == null && !isHidden() && ((AppCompatActivity) getActivity()).getSupportActionBar() != null && (getActivity() instanceof BaseCompatToolbarActivity) && !isHidden()) {
            ((BaseCompatToolbarActivity) getActivity()).restoreToolbarBehavior();
        }
    }

    public void onLocalizationChanged() {
    }

    public Context getContext() {
        return getActivity();
    }

    protected boolean inflateMenuLocalized(int menuId, Menu menu) {
        Context activity = getActivity();
        if (activity == null) {
            return false;
        }
        LocalizationManager.inflate(activity, activity.getMenuInflater(), menuId, menu);
        return true;
    }

    protected void inflateMenuLocalized(int menuId, ContextMenu menu) {
        Context activity = getActivity();
        if (activity != null) {
            LocalizationManager.inflate(activity, new MenuInflater(activity), menuId, menu);
        }
    }

    protected View inflateViewLocalized(int layoutId, ViewGroup container, boolean attachToParent) {
        return LocalizationManager.inflate(getActivity(), layoutId, container, attachToParent);
    }

    protected String getStringLocalized(int stringId) {
        return LocalizationManager.getString(getActivity(), stringId);
    }

    protected String[] getStringArrayLocalized(int stringId) {
        return LocalizationManager.getStringArray(getActivity(), stringId);
    }

    protected String getStringLocalized(int stringId, Object... args) {
        return LocalizationManager.getString(getActivity(), stringId, args);
    }

    protected boolean isActionBarAffecting() {
        return true;
    }
}
