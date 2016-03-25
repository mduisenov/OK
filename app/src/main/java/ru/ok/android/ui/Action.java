package ru.ok.android.ui;

import android.view.View;

public interface Action {
    int getDrawable();

    boolean performAction(View view);
}
