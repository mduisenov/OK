package ru.ok.android.ui.video.player;

import android.content.Context;
import android.util.AttributeSet;

public class LiveVideoControllerView extends VideoControllerView {
    public LiveVideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveVideoControllerView(Context context) {
        super(context);
    }

    protected int getLayoutId() {
        return 2130903273;
    }
}
