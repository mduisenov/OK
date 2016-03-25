package ru.ok.android.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class GroupsRecyclerView extends RecyclerView {
    public GroupsRecyclerView(Context context) {
        this(context, null);
    }

    public GroupsRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GroupsRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean fling(int velocityX, int velocityY) {
        int newVelocityY = Math.min((int) (0.7f * ((float) Math.abs(velocityY))), 10000);
        if (velocityY < 0) {
            newVelocityY *= -1;
        }
        return super.fling(velocityX, newVelocityY);
    }
}
