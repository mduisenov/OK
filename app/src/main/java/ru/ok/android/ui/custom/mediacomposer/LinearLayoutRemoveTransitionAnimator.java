package ru.ok.android.ui.custom.mediacomposer;

import android.view.ViewGroup;

public class LinearLayoutRemoveTransitionAnimator extends LinearLayoutTransitionAnimator {
    public LinearLayoutRemoveTransitionAnimator(ViewGroup parent, int removedPosition, long animationDurationMs) {
        super(parent, removedPosition - 1, removedPosition, animationDurationMs);
    }
}
