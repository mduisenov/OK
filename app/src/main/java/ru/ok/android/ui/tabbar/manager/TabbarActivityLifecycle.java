package ru.ok.android.ui.tabbar.manager;

import android.os.Bundle;

public interface TabbarActivityLifecycle {
    void onPause();

    void onRestoreInstanceState(Bundle bundle);

    void onResume();

    void onSaveInstanceState(Bundle bundle);
}
