package ru.ok.android.ui;

import android.content.Context;
import android.util.AttributeSet;

public class StreamRecyclerView extends RecyclerViewSizeListenable {
    public StreamRecyclerView(Context context) {
        this(context, null);
    }

    public StreamRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StreamRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean fling(int velocityX, int velocityY) {
        int newVelocityY = Math.min((int) (0.8f * ((float) Math.abs(velocityY))), 14000);
        if (velocityY < 0) {
            newVelocityY *= -1;
        }
        return super.fling(velocityX, newVelocityY);
    }
}
