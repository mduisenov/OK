package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;
import com.google.android.gms.plus.PlusShare;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.jivesoftware.smack.util.StringUtils;

public class HookOutLinkProcessor extends HookBaseProcessor {
    private OnOutLinkOpenListener listener;

    public interface OnOutLinkOpenListener {
        void onOutLinkOpenInBrowser(String str);
    }

    public HookOutLinkProcessor(OnOutLinkOpenListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/outlink";
    }

    protected void onHookExecute(Uri uri) {
        String new_url = uri.getQueryParameter(PlusShare.KEY_CALL_TO_ACTION_URL);
        if (TextUtils.isEmpty(new_url)) {
            String[] urls = uri.toString().split("/apphook/outlink/");
            if (urls.length > 1) {
                new_url = urls[1];
            }
        } else {
            int i = 0;
            while (i < 3) {
                try {
                    if (URLUtil.isValidUrl(new_url)) {
                        break;
                    }
                    new_url = URLEncoder.encode(new_url, StringUtils.UTF8);
                    i++;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!TextUtils.isEmpty(new_url)) {
            notifyOpenInBrowser(new_url);
        }
    }

    private void notifyOpenInBrowser(String url) {
        if (this.listener != null) {
            this.listener.onOutLinkOpenInBrowser(url);
        }
    }
}
