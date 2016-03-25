package ru.ok.android.emoji;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.lang.reflect.Field;
import ru.ok.android.emoji.utils.DeviceUtils;
import ru.ok.android.emoji.utils.KeyBoardUtils;

public final class PanelLayoutController implements PanelPresenterSizeListener {
    private static final String TAG;
    @NonNull
    private final Activity activity;
    @NonNull
    private final EditText editText;
    private Editor editor;
    private boolean isKeyboardVisible;
    private int keyboardHeight;
    private int keyboardState;
    @Nullable
    private final PanelViewControllerListener listener;
    private FrameLayout panelViewLayout;
    private boolean panelViewNotFullscreen;
    @NonNull
    private final PanelViewPresenter panelViewPresenter;
    private SharedPreferences preferences;
    private final Rect rect;

    public interface PanelViewControllerListener {
        void onPanelViewVisibilityChanged(boolean z);
    }

    public interface PanelViewPresenter {
        void hidePanelView(View view);

        void setSizeListener(PanelPresenterSizeListener panelPresenterSizeListener);

        void showPanelView(View view, int i);
    }

    static {
        TAG = PanelLayoutController.class.getSimpleName();
    }

    public PanelLayoutController(@NonNull Activity activity, @NonNull PanelViewPresenter panelViewPresenter, @NonNull EditText editText, @Nullable PanelViewControllerListener listener) {
        this.keyboardState = 0;
        this.rect = new Rect();
        this.activity = activity;
        this.panelViewPresenter = panelViewPresenter;
        this.editText = editText;
        this.listener = listener;
    }

    private void createPanelLayout(@NonNull Context activity) {
        this.panelViewLayout = new FrameLayout(activity);
    }

    public static int getViewInset(View view) {
        int i = 0;
        if (view != null && VERSION.SDK_INT >= 21) {
            try {
                Field mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
                mAttachInfoField.setAccessible(true);
                Object mAttachInfo = mAttachInfoField.get(view);
                if (mAttachInfo != null) {
                    Field mStableInsetsField = mAttachInfo.getClass().getDeclaredField("mStableInsets");
                    mStableInsetsField.setAccessible(true);
                    i = ((Rect) mStableInsetsField.get(mAttachInfo)).bottom;
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to get insets", e);
            }
        }
        return i;
    }

    public void onSizeChanged(View view) {
        if (!this.panelViewNotFullscreen) {
            boolean rootViewPortrait;
            View rootView = view.getRootView();
            boolean portrait = DeviceUtils.isPortrait(this.activity);
            if (rootView.getHeight() > rootView.getWidth()) {
                rootViewPortrait = true;
            } else {
                rootViewPortrait = false;
            }
            if (portrait != rootViewPortrait) {
                Log.d(TAG, "Orientations not equals, we are rotation now probably...");
                return;
            }
            int usableViewHeight = getUsableViewHeight(rootView);
            view.getWindowVisibleDisplayFrame(this.rect);
            this.keyboardHeight = usableViewHeight - this.rect.height();
            boolean wasVisible = this.isKeyboardVisible;
            this.isKeyboardVisible = this.keyboardHeight > 0;
            if (this.isKeyboardVisible) {
                this.keyboardHeight = (view.getHeight() * 3) / 5;
                saveKeyboardHeight();
            }
            if (this.isKeyboardVisible && (this.keyboardState == 1 || !wasVisible)) {
                hidePanelView();
                this.keyboardState = 0;
            } else if (!this.isKeyboardVisible && this.keyboardState == 2) {
                callShowPanelView(getKeyboardHeight());
                this.keyboardState = 0;
            }
        }
    }

    private int getUsableViewHeight(View rootView) {
        return (rootView.getHeight() - DeviceUtils.getStatusBarHeight(this.activity)) - getViewInset(rootView);
    }

    private void callShowPanelView(int height) {
        this.panelViewPresenter.showPanelView(this.panelViewLayout, height);
        notifyPanelShown();
    }

    private int getKeyboardHeight() {
        int result = getPreferences().getInt(getKeyboardHeightPrefKey(), 0);
        return result != 0 ? result : this.activity.getResources().getDimensionPixelSize(C0263R.dimen.keyboard_height);
    }

    private void saveKeyboardHeight() {
        getEditor().putInt(getKeyboardHeightPrefKey(), this.keyboardHeight).apply();
    }

    @NonNull
    private String getKeyboardHeightPrefKey() {
        return DeviceUtils.isPortrait(this.activity) ? "key_keyboard_height_portrait" : "key_keyboard_height_landscape";
    }

    private Editor getEditor() {
        if (this.editor == null) {
            this.editor = getPreferences().edit();
        }
        return this.editor;
    }

    private SharedPreferences getPreferences() {
        if (this.preferences == null) {
            this.preferences = this.activity.getSharedPreferences("emoji_prefs", 0);
        }
        return this.preferences;
    }

    public void addPanelView(View panelView) {
        if (this.panelViewLayout == null) {
            createPanelLayout(this.activity);
        }
        this.panelViewLayout.addView(panelView);
    }

    public void showPanelView() {
        showPanelView(null);
    }

    public void showPanelView(@Nullable PanelView panelView) {
        if (this.panelViewLayout == null) {
            createPanelLayout(this.activity);
        }
        this.editText.requestFocus();
        if (panelView != null) {
            for (int i = 0; i < this.panelViewLayout.getChildCount(); i++) {
                PanelView child = this.panelViewLayout.getChildAt(i);
                if (child == panelView) {
                    child.setVisibility(0);
                } else {
                    child.setVisibility(8);
                }
            }
        }
        if (this.panelViewNotFullscreen) {
            KeyBoardUtils.hideKeyBoard(this.activity);
            callShowPanelView(getKeyboardHeight());
        } else if (this.isKeyboardVisible) {
            this.keyboardState = 2;
            KeyBoardUtils.hideKeyBoard(this.activity);
        } else {
            callShowPanelView(getKeyboardHeight());
        }
        if (this.listener != null) {
            this.listener.onPanelViewVisibilityChanged(true);
        }
    }

    private void notifyPanelShown() {
        if (this.panelViewLayout != null) {
            for (int i = 0; i < this.panelViewLayout.getChildCount(); i++) {
                View childAt = this.panelViewLayout.getChildAt(i);
                if (childAt.getVisibility() == 0 && (childAt instanceof PanelView)) {
                    ((PanelView) childAt).onShown();
                }
            }
        }
    }

    public void hidePanelViewOpenKeyboard() {
        if (isShowingPanelView()) {
            if (this.panelViewNotFullscreen) {
                hidePanelView();
            } else {
                this.keyboardState = 1;
            }
            this.editText.requestFocus();
            KeyBoardUtils.showKeyBoard(this.editText);
        }
    }

    public boolean isShowingPanelView() {
        return this.panelViewLayout != null && this.panelViewLayout.getVisibility() == 0;
    }

    public boolean hidePanelView() {
        if (this.panelViewLayout == null || !isShowingPanelView()) {
            return false;
        }
        this.panelViewPresenter.hidePanelView(this.panelViewLayout);
        for (int i = 0; i < this.panelViewLayout.getChildCount(); i++) {
            this.panelViewLayout.getChildAt(i).setVisibility(8);
        }
        this.listener.onPanelViewVisibilityChanged(false);
        return true;
    }

    public void onPause() {
        if (this.panelViewLayout != null) {
            for (int i = 0; i < this.panelViewLayout.getChildCount(); i++) {
                View child = this.panelViewLayout.getChildAt(i);
                if (child instanceof PanelView) {
                    ((PanelView) child).onPause();
                }
            }
        }
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("emoji-panel:is-showing", isShowingPanelView());
    }

    public boolean onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null || !savedInstanceState.getBoolean("emoji-panel:is-showing", false)) {
            return false;
        }
        showPanelView();
        return true;
    }
}
