package ru.ok.android.ui.custom.mediacomposer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import java.util.ArrayList;

public class FragmentBridge {
    private final ArrayList<ResultCallback> callbacks;
    private final Fragment fragment;

    public interface ResultCallback {
        void onActivityResult(int i, int i2, Intent intent);
    }

    public FragmentBridge(Fragment fragment) {
        this.callbacks = new ArrayList();
        this.fragment = fragment;
    }

    public Context getContext() {
        return this.fragment.getActivity();
    }

    public Fragment getFragment() {
        return this.fragment;
    }

    public void startActivity(Intent intent) {
        this.fragment.startActivity(intent);
    }

    public void startActivityForResult(Intent intent, int requestCode, int callbackId) {
        if ((requestCode & -256) != 0) {
            throw new IllegalArgumentException("Can only use lower 8 bits for requestCode");
        } else if ((callbackId & -64) != 0) {
            throw new IllegalArgumentException("Illegal callback Id: " + callbackId + ". Must use lower 6 bits");
        } else {
            this.fragment.startActivityForResult(intent, ((callbackId << 8) | requestCode) | 49152);
        }
    }

    public boolean willHandleRequestCodeFromResult(int requestCode) {
        return (-65536 & requestCode) == 0 && (requestCode & 49152) == 49152;
    }

    public void onFragmentActivityResult(int requestCode, int resultCode, Intent data) {
        requestCode &= 16383;
        int callbackId = requestCode >> 8;
        if (callbackId >= 0 && callbackId < this.callbacks.size()) {
            ResultCallback callback = (ResultCallback) this.callbacks.get(callbackId);
            if (callback != null) {
                callback.onActivityResult(requestCode & MotionEventCompat.ACTION_MASK, resultCode, data);
            }
        }
    }

    public int addCallback(ResultCallback callback) {
        int callbackId = this.callbacks.size();
        if ((callbackId & -64) != 0) {
            throw new IllegalArgumentException("Callback capacity is exceeded. Max 64 callbacks");
        }
        this.callbacks.add(callback);
        return callbackId;
    }
}
