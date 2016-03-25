package ru.ok.android.ui.custom.mediacomposer;

import android.view.ViewGroup;

public class LinearLayoutInsertTransitionAnimator extends LinearLayoutTransitionAnimator {
    public LinearLayoutInsertTransitionAnimator(ViewGroup parent, int insertPosition, long animationDurationMs) {
        super(parent, insertPosition - 1, insertPosition + 1, animationDurationMs);
    }
}
