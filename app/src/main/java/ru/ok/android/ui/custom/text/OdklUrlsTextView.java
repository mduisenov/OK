package ru.ok.android.ui.custom.text;

import android.content.Context;
import android.net.Uri;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView.BufferType;
import ru.ok.android.emoji.view.EmojiTextView;
import ru.ok.android.fragments.web.client.WebClientUtils;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;

public class OdklUrlsTextView extends EmojiTextView {
    private boolean inSetText;
    private OnSelectOdklLinkListener listener;

    public interface OnSelectOdklLinkListener {
        void onSelectOdklLink(String str);
    }

    private class MyUrlSpan extends URLSpan implements OnLongClickListener {
        private OnSelectOdklLinkListener listener;

        public MyUrlSpan(String string) {
            super(string);
        }

        public void setListener(OnSelectOdklLinkListener listener) {
            this.listener = listener;
        }

        public void onClick(View v) {
            String url = getURL();
            if (!isUrlOdkl(url)) {
                super.onClick(v);
            } else if (this.listener != null) {
                this.listener.onSelectOdklLink(getNewUrl(url));
            }
        }

        public boolean onLongClick(View v) {
            return false;
        }

        private boolean isUrlOdkl(String url) {
            Uri uri = TextUtils.isEmpty(url) ? null : Uri.parse(url);
            return (uri == null || uri.getHost() == null || !WebClientUtils.isOkHost(uri)) ? false : true;
        }

        private String getNewUrl(String url) {
            Uri uri = TextUtils.isEmpty(url) ? null : Uri.parse(url);
            if (uri == null || uri.getHost() == null || !WebClientUtils.isOkHost(uri)) {
                return url;
            }
            String[] elems = url.split(uri.getHost());
            if (elems.length <= 0) {
                return url;
            }
            if (elems.length == 1) {
                return getBaseUrl();
            }
            return getBaseUrl() + elems[1];
        }

        private String getBaseUrl() {
            String url = JsonSessionTransportProvider.getInstance().getWebBaseUrl();
            if (TextUtils.isEmpty(url)) {
                return "http://m.odnoklassniki.ru";
            }
            if (url.endsWith("/")) {
                return url.substring(0, url.length() - 1);
            }
            return url;
        }
    }

    public OdklUrlsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLinkListener(OnSelectOdklLinkListener listener) {
        this.listener = listener;
    }

    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (!this.inSetText) {
            this.inSetText = true;
            try {
                Linkify.addLinks(this, 15);
            } catch (Throwable e) {
                Logger.m178e(e);
            }
            setMovementMethod(null);
            if (getText() instanceof Spannable) {
                Spannable spannable = (Spannable) getText();
                URLSpan[] uspans = (URLSpan[]) spannable.getSpans(0, length(), URLSpan.class);
                if (uspans.length > 0) {
                    for (URLSpan uspan : uspans) {
                        int start = spannable.getSpanStart(uspan);
                        int end = spannable.getSpanEnd(uspan);
                        spannable.removeSpan(uspan);
                        MyUrlSpan myUrlSpan = new MyUrlSpan(uspan.getURL());
                        myUrlSpan.setListener(this.listener);
                        spannable.setSpan(myUrlSpan, start, end, 33);
                    }
                    super.setText(spannable, type);
                }
            }
            this.inSetText = false;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        CharSequence text = getText();
        Layout layout = getLayout();
        if ((text instanceof Spanned) && layout != null) {
            Spanned buffer = (Spanned) text;
            int action = event.getActionMasked();
            if (action == 1 || action == 0 || action == 3) {
                int off = layout.getOffsetForHorizontal(layout.getLineForVertical((((int) event.getY()) - getTotalPaddingTop()) + getScrollY()), (float) ((((int) event.getX()) - getTotalPaddingLeft()) + getScrollX()));
                ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                if (link.length != 0) {
                    if (action == 1) {
                        link[0].onClick(this);
                    } else if (action == 0) {
                        return true;
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
