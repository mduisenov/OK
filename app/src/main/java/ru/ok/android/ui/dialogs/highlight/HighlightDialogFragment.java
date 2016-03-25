package ru.ok.android.ui.dialogs.highlight;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import java.util.Map;
import java.util.WeakHashMap;
import ru.ok.android.ui.PopupDialogsSyncUtils;
import ru.ok.android.ui.custom.highlight.SimpleHighlightView;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.UIUtils;
import ru.ok.android.utils.animation.SimpleAnimatorListener;

public class HighlightDialogFragment extends DialogFragment implements OnGlobalLayoutListener {
    private static final Map<View, AnchorLayoutListener> LISTENER_REFERENCES;
    private boolean animated;
    private int centerX;
    private int centerY;
    private SimpleHighlightView highlightView;
    private String key;
    private int radius;

    /* renamed from: ru.ok.android.ui.dialogs.highlight.HighlightDialogFragment.1 */
    static class C07871 implements Runnable {
        final /* synthetic */ View val$anchorView;
        final /* synthetic */ int val$contentGravity;
        final /* synthetic */ String val$key;
        final /* synthetic */ FragmentManager val$manager;
        final /* synthetic */ String val$message;
        final /* synthetic */ String val$title;

        C07871(String str, FragmentManager fragmentManager, String str2, String str3, int i, View view) {
            this.val$key = str;
            this.val$manager = fragmentManager;
            this.val$title = str2;
            this.val$message = str3;
            this.val$contentGravity = i;
            this.val$anchorView = view;
        }

        public void run() {
            HighlightDialogFragment.highlightInternal(this.val$key, this.val$manager, this.val$title, this.val$message, this.val$contentGravity, this.val$anchorView);
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.highlight.HighlightDialogFragment.2 */
    class C07882 implements OnClickListener {
        C07882() {
        }

        public void onClick(View view) {
            HighlightDialogFragment.this.hideHighlight(true, true, null);
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.highlight.HighlightDialogFragment.3 */
    class C07893 extends Dialog {
        C07893(Context x0, int x1) {
            super(x0, x1);
        }

        public boolean dispatchTouchEvent(MotionEvent event) {
            if (event.getAction() != 1 || !HighlightDialogFragment.this.highlightView.touchesHighlightedArea(event)) {
                return super.dispatchTouchEvent(event);
            }
            HighlightDialogFragment.this.hideHighlight(true, true, null);
            return false;
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.highlight.HighlightDialogFragment.4 */
    class C07904 implements OnKeyListener {
        C07904() {
        }

        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode != 4 || event.getAction() != 1) {
                return false;
            }
            HighlightDialogFragment.this.hideHighlight(true, true, null);
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.dialogs.highlight.HighlightDialogFragment.5 */
    class C07915 extends SimpleAnimatorListener {
        final /* synthetic */ Runnable val$onHiddenAction;

        C07915(Runnable runnable) {
            this.val$onHiddenAction = runnable;
        }

        public void onAnimationEnd(Animator animation) {
            HighlightDialogFragment.this.dismissWithRunnable(this.val$onHiddenAction);
        }
    }

    private static final class AnchorLayoutListener implements OnGlobalLayoutListener {
        private View anchorView;
        private FragmentManager fmanager;
        private int gravity;
        private String key;
        private String message;
        private String title;

        /* renamed from: ru.ok.android.ui.dialogs.highlight.HighlightDialogFragment.AnchorLayoutListener.1 */
        class C07921 implements Runnable {
            final /* synthetic */ AnchorLayoutListener val$listener;

            C07921(AnchorLayoutListener anchorLayoutListener) {
                this.val$listener = anchorLayoutListener;
            }

            public void run() {
                AnchorLayoutListener.this.anchorView.getViewTreeObserver().addOnGlobalLayoutListener(this.val$listener);
                HighlightDialogFragment.LISTENER_REFERENCES.put(AnchorLayoutListener.this.anchorView, this.val$listener);
            }
        }

        private AnchorLayoutListener(String key, FragmentManager fmanager, String title, String message, int gravity, View anchorView) {
            this.key = key;
            this.fmanager = fmanager;
            this.title = title;
            this.message = message;
            this.gravity = gravity;
            this.anchorView = anchorView;
        }

        public void onGlobalLayout() {
            UIUtils.removeOnGlobalLayoutListener(this.anchorView, this);
            HighlightDialogFragment.LISTENER_REFERENCES.remove(this.anchorView);
            if (HighlightsStateStore.needsToShowHighlight(this.anchorView.getContext(), this.key)) {
                HighlightDialogFragment.showHighlight(this.key, this.fmanager, this.title, this.message, this.gravity, this.anchorView);
                ThreadUtil.queueOnMain(new C07921(this));
            }
        }
    }

    static {
        LISTENER_REFERENCES = new WeakHashMap();
    }

    public static void highlightIfNecessary(String key, FragmentManager manager, String title, String message, int contentGravity, View anchorView, boolean sync) {
        if (!sync) {
            highlightInternal(key, manager, title, message, contentGravity, anchorView);
        } else if (HighlightsStateStore.needsToShowHighlight(anchorView.getContext(), key) && !LISTENER_REFERENCES.containsKey(anchorView)) {
            PopupDialogsSyncUtils.atomicCheckAndShow(anchorView.getContext(), new C07871(key, manager, title, message, contentGravity, anchorView));
        }
    }

    protected static final void highlightInternal(String key, FragmentManager manager, String title, String message, int contentGravity, View anchorView) {
        if (HighlightsStateStore.needsToShowHighlight(anchorView.getContext(), key) && !LISTENER_REFERENCES.containsKey(anchorView)) {
            AnchorLayoutListener listener = new AnchorLayoutListener(manager, title, message, contentGravity, anchorView, null);
            anchorView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
            LISTENER_REFERENCES.put(anchorView, listener);
            anchorView.invalidate();
        }
    }

    protected static HighlightDialogFragment showHighlight(String key, FragmentManager manager, String title, String message, int gravity, View anchorView) {
        HighlightDialogFragment fragment = (HighlightDialogFragment) manager.findFragmentByTag("hghlghtdlg");
        int[] location;
        if (fragment == null) {
            fragment = createInstance(key, title, message, gravity);
            location = getAnchorLocation(anchorView);
            fragment.setHighlightPosition(location[0], location[1], location[2]);
            fragment.show(manager.beginTransaction(), "hghlghtdlg");
            return fragment;
        } else if (fragment.isVisible()) {
            return fragment;
        } else {
            location = getAnchorLocation(anchorView);
            fragment.setHighlightPosition(location[0], location[1], location[2]);
            return fragment;
        }
    }

    protected static HighlightDialogFragment createInstance(String key, String title, String message, int gravity) {
        HighlightDialogFragment fragment = new HighlightDialogFragment();
        Bundle args = new Bundle();
        args.putString("ttl", title);
        args.putString(NotificationCompat.CATEGORY_MESSAGE, message);
        args.putString("hkey", key);
        args.putInt("grvt", gravity);
        fragment.setArguments(args);
        return fragment;
    }

    private static int[] getAnchorLocation(View anchorView) {
        int radius = Math.max(anchorView.getMeasuredWidth(), anchorView.getMeasuredHeight());
        location = new int[3];
        anchorView.getLocationInWindow(location);
        location[0] = location[0] + (radius / 2);
        location[1] = location[1] + (radius / 2);
        location[2] = radius;
        return location;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.key = getArguments().getString("hkey");
        setCancelable(false);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.highlightView = new SimpleHighlightView(getActivity());
        this.highlightView.setTitle(getArguments().getCharSequence("ttl"));
        this.highlightView.setMessage(getArguments().getCharSequence(NotificationCompat.CATEGORY_MESSAGE));
        this.highlightView.setGravity(getArguments().getInt("grvt"));
        this.highlightView.setOnCloseButtonListener(new C07882());
        Dialog dialog = new C07893(getActivity(), 16973840);
        dialog.setContentView(this.highlightView);
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new C07904());
        this.highlightView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        return dialog;
    }

    protected final void setHighlightPosition(int centerX, int centerY, int radius) {
        if (valuesValid(centerX, centerY, radius)) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
            if (this.highlightView != null) {
                this.highlightView.getViewTreeObserver().addOnGlobalLayoutListener(this);
                this.highlightView.invalidate();
            }
        }
    }

    private boolean valuesValid(int centerX, int centerY, int radius) {
        if (radius == 0) {
            return false;
        }
        if (this.centerX == centerX && this.centerY == centerY && this.radius == radius) {
            return false;
        }
        return true;
    }

    public void onGlobalLayout() {
        UIUtils.removeOnGlobalLayoutListener(this.highlightView, this);
        int[] location = new int[2];
        this.highlightView.getLocationInWindow(location);
        this.highlightView.setHighlightArea(this.centerX - location[0], this.centerY - location[1], this.radius);
        if (!this.animated) {
            this.animated = true;
            this.highlightView.animateShow(null);
        }
    }

    protected final void hideHighlight(boolean animate, boolean markAsShown, Runnable onHiddenAction) {
        if (animate) {
            this.highlightView.animateHide(new C07915(onHiddenAction));
        } else {
            dismissWithRunnable(onHiddenAction);
        }
        if (markAsShown) {
            HighlightsStateStore.markHighlightAsShown(this.highlightView.getContext(), this.key);
        }
    }

    protected void dismissWithRunnable(Runnable onDismissedAction) {
        dismiss();
        if (onDismissedAction != null) {
            onDismissedAction.run();
        }
    }
}
