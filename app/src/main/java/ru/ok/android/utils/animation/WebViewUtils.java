package ru.ok.android.utils.animation;

import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;
import android.widget.Scroller;

public final class WebViewUtils {

    private static class AnimationHandler extends Handler {
        private final Scroller scroller;
        private final WebView webView;

        private AnimationHandler(WebView view, Scroller scroller) {
            this.scroller = scroller;
            this.webView = view;
        }

        public void handleMessage(Message msg) {
            boolean goOn = this.scroller.computeScrollOffset();
            this.webView.scrollTo(this.scroller.getCurrX(), this.scroller.getCurrY());
            if (goOn) {
                sendEmptyMessage(0);
            }
        }
    }

    public static void scrollPositionToTop(WebView view) {
        Scroller scroller = new Scroller(view.getContext());
        scroller.startScroll(0, view.getScrollY(), 0, -view.getScrollY());
        new AnimationHandler(scroller, null).sendEmptyMessage(0);
    }
}
