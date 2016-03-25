package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import org.jivesoftware.smack.util.StringUtils;

public class HookMakeCallProcessor extends HookBaseProcessor {
    private OnMakeCallUrlLoadingListener onMakeCallUrlLoadingListener;

    public interface OnMakeCallUrlLoadingListener {
        void onMakeCallUrlError();

        void onMakeCallUrlLoading(String str, String str2, String str3, String str4, String str5, String str6);
    }

    public HookMakeCallProcessor(OnMakeCallUrlLoadingListener onMakeCallUrlLoadingListener) {
        this.onMakeCallUrlLoadingListener = onMakeCallUrlLoadingListener;
    }

    protected String getHookName() {
        return "/apphook/makeCall";
    }

    protected void onHookExecute(Uri uri) {
        String from = uri.getQueryParameter("from");
        String to = uri.getQueryParameter("to");
        String sid = uri.getQueryParameter("sid");
        String userPic = uri.getQueryParameter("userpic").replace(StringUtils.AMP_ENCODE, "&");
        notifyLoadingListenerOnMakeCall(from, to, sid, uri.getQueryParameter("disp"), uri.getQueryParameter("username"), userPic);
    }

    private void notifyLoadingListenerOnMakeCall(String from, String to, String sid, String disp, String userName, String userPic) {
        try {
            if (this.onMakeCallUrlLoadingListener != null) {
                this.onMakeCallUrlLoadingListener.onMakeCallUrlLoading(from, to, sid, disp, userName, userPic);
            }
        } catch (Exception e) {
            if (this.onMakeCallUrlLoadingListener != null) {
                this.onMakeCallUrlLoadingListener.onMakeCallUrlError();
            }
        }
    }
}
