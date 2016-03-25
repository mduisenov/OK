package ru.ok.android.ui.custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;
import ru.ok.android.C0206R;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.localization.LocalizationManager;

public final class NetworkStatusView extends TextView {
    private ExpandCollapseYAnimation animation;
    private boolean connected;
    private final Handler handler;
    private BroadcastReceiver receiver;

    /* renamed from: ru.ok.android.ui.custom.NetworkStatusView.1 */
    class C06201 extends Handler {
        C06201() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECEIVED_VALUE:
                    NetworkStatusView.this.updateState();
                default:
                    throw new IllegalArgumentException("Wrong message: " + msg.what);
            }
        }
    }

    private class ConnectionReceiver extends BroadcastReceiver {
        private ConnectionReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            NetworkStatusView.this.handler.removeMessages(0);
            boolean systemState = NetUtils.isConnectionAvailable(NetworkStatusView.this.getContext(), false);
            if (systemState == NetworkStatusView.this.connected) {
                return;
            }
            if (systemState) {
                NetworkStatusView.this.handler.sendEmptyMessageDelayed(0, 1000);
            } else {
                NetworkStatusView.this.handler.sendEmptyMessage(0);
            }
        }
    }

    private class ExpandCollapseYAnimation extends Animation {
        private int delta;
        private final int expandedHeight;
        private int initialHeight;

        private ExpandCollapseYAnimation(int expandedHeight) {
            this.expandedHeight = expandedHeight;
            setDuration(200);
        }

        protected void applyTransformation(float interpolatedTime, Transformation t) {
            NetworkStatusView.this.getLayoutParams().height = this.initialHeight - ((int) (((float) this.delta) * interpolatedTime));
            NetworkStatusView.this.requestLayout();
        }

        public boolean willChangeBounds() {
            return true;
        }

        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            this.initialHeight = height;
            this.delta = this.initialHeight - (NetworkStatusView.this.connected ? 0 : this.expandedHeight);
            if (this.delta == 0) {
                cancel();
                reset();
            }
        }
    }

    public NetworkStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.handler = new C06201();
        setText(LocalizationManager.getString(context, 2131166735));
        TypedArray typedArray = context.obtainStyledAttributes(attrs, C0206R.styleable.NetworkStatusView);
        this.animation = new ExpandCollapseYAnimation(typedArray.getDimensionPixelSize(0, 0), null);
        typedArray.recycle();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.receiver == null) {
            Context context = getContext();
            BroadcastReceiver connectionReceiver = new ConnectionReceiver();
            this.receiver = connectionReceiver;
            context.registerReceiver(connectionReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            updateState();
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.receiver != null) {
            getContext().unregisterReceiver(this.receiver);
            this.receiver = null;
        }
    }

    private void updateState() {
        this.connected = NetUtils.isConnectionAvailable(getContext(), false);
        clearAnimation();
        startAnimation(this.animation);
    }
}
