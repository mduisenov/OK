package ru.ok.android.ui.custom.photo.tags;

import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.facebook.drawee.view.DraweeHolder;
import ru.ok.android.ui.custom.photo.PhotoTagDrawable;
import ru.ok.android.utils.Logger;
import ru.ok.model.UserInfo;

public class UserPhotoTag {
    protected int arrowDirection;
    protected PhotoTagDrawable mDrawable;
    protected int mX;
    protected int mY;
    protected int nameDirection;
    protected OnUserNameClickListener onUserNameClickListener;
    private UserInfo userInfo;

    public interface OnUserNameClickListener {
        void onUserNameClicked(UserInfo userInfo);
    }

    public UserPhotoTag(Context context, UserInfo userInfo, int x, int y, boolean male, int nameDirection, int arrowDirection) {
        this.userInfo = userInfo;
        this.mX = x;
        this.mY = y;
        this.nameDirection = nameDirection;
        this.arrowDirection = arrowDirection;
        this.mDrawable = new PhotoTagDrawable(context.getResources(), context.getResources().getDrawable(male ? 2130838530 : 2130838532).mutate(), nameDirection, arrowDirection, true);
        this.mDrawable.setName(userInfo.firstName + " " + userInfo.lastName);
    }

    public UserPhotoTag(Context context, String text, int x, int y, int nameDirection, int arrowDirection) {
        this.mX = x;
        this.mY = y;
        this.nameDirection = nameDirection;
        this.arrowDirection = arrowDirection;
        this.mDrawable = new PhotoTagDrawable(context.getResources(), context.getResources().getDrawable(2130838531).mutate(), nameDirection, arrowDirection, false);
        this.mDrawable.setName(text);
    }

    public int getX() {
        return this.mX;
    }

    public int getY() {
        return this.mY;
    }

    public Drawable getDrawable() {
        return this.mDrawable;
    }

    public final void hideUserNamePatch() {
        this.mDrawable.hideUserNamePatch();
    }

    public final void toggleUserNamePatch() {
        this.mDrawable.toggleUserNamePatch();
    }

    public final void setAlpha(int alpha) {
        this.mDrawable.setAlpha(alpha);
    }

    public final void setSubstractAlpha(int alpha) {
        this.mDrawable.setSubstractAlpha(alpha);
    }

    public final boolean isNamePatchShowing() {
        return this.mDrawable.isNamePatchShowing();
    }

    public boolean isSelected() {
        return this.mDrawable.isNamePatchShowing();
    }

    public final UserInfo getUserInfo() {
        return this.userInfo;
    }

    public void calculateBounds(Rect rect, int x, int y) {
        this.mDrawable.calculateBounds(rect, x, y);
        Logger.m172d("BOUNDS FOR TAG CALCULATED: " + rect);
    }

    public boolean handleClickEvent(Rect tagRect, int x, int y) {
        if (this.userInfo == null) {
            return false;
        }
        Logger.m172d("USER PHOTO TAG ClICK : X - " + x + ", Y - " + y);
        if (!this.mDrawable.isNamepatchClicked(tagRect, x, y)) {
            return false;
        }
        if (this.onUserNameClickListener != null) {
            this.onUserNameClickListener.onUserNameClicked(this.userInfo);
        }
        return true;
    }

    public final void startPopOutAnimation(long delay, AnimatorListener listener) {
        this.mDrawable.startPopOutAnimation(0.1f, 1.0f, 250, delay, listener);
    }

    public final void startCaveInAnimation(long delay, AnimatorListener listener) {
        if (this.mDrawable.isNamePatchShowing()) {
            this.mDrawable.hideUserNamePatch();
        }
        this.mDrawable.startCaveInAnimation(1.0f, 0.0f, 100, delay, listener);
    }

    public void setOnUserNameClickListener(OnUserNameClickListener onUserNameClickListener) {
        this.onUserNameClickListener = onUserNameClickListener;
    }

    public void setUserPhotoHolder(DraweeHolder tagHolder) {
        this.mDrawable.setUserDrawable(tagHolder.getTopLevelDrawable());
    }
}
