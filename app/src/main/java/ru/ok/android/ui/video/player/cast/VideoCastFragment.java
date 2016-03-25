package ru.ok.android.ui.video.player.cast;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;
import com.google.android.libraries.cast.companionlibrary.utils.Utils;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.ui.video.activity.VideoActivity;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.response.video.VideoGetResponse;

public class VideoCastFragment extends BaseVideoCastFragment {
    private View controllers;
    private TextView endText;
    private UrlImageView imageView;
    private TextView liveText;
    private TextView mLine2;
    private int mStreamType;
    private ImageView playPause;
    private SeekBar seekbar;
    private ProgressBar spinner;
    private TextView startText;

    /* renamed from: ru.ok.android.ui.video.player.cast.VideoCastFragment.1 */
    class C14071 implements OnClickListener {
        C14071() {
        }

        public void onClick(View v) {
            try {
                VideoCastFragment.this.onPlayPauseClicked(v);
            } catch (TransientNetworkDisconnectionException e) {
                Logger.m177e("Failed to toggle playback due to temporary network issue", e);
                Utils.showToast(VideoCastFragment.this.getActivity(), 2131165510);
            } catch (NoConnectionException e2) {
                Logger.m177e("Failed to toggle playback due to network issues", e2);
                Utils.showToast(VideoCastFragment.this.getActivity(), 2131165507);
            } catch (Exception e3) {
                Logger.m177e("Failed to toggle playback due to other issues", e3);
                Utils.showToast(VideoCastFragment.this.getActivity(), 2131165512);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.cast.VideoCastFragment.2 */
    class C14082 implements OnSeekBarChangeListener {
        C14082() {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            try {
                VideoCastFragment.this.onStopTrackingTouch(seekBar);
            } catch (Exception e) {
                Logger.m177e("Failed to complete seek", e);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            try {
                VideoCastFragment.this.onStartTrackingTouch(seekBar);
            } catch (Exception e) {
                Logger.m177e("Failed to start seek", e);
            }
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            VideoCastFragment.this.startText.setText(Utils.formatMillis((long) progress));
            try {
                VideoCastFragment.this.onProgressChanged(seekBar, progress, fromUser);
            } catch (Exception e) {
                Logger.m177e("Failed to set the progress result", e);
            }
        }
    }

    public static VideoCastFragment newInstance(VideoGetResponse response, int position) {
        VideoCastFragment fragment = new VideoCastFragment();
        try {
            Bundle args = CastUtils.getExtraFromMediaInfo(CastUtils.responseToMediaInfo(response), position, true);
            args.putParcelable("currentResponse", response);
            fragment.setArguments(args);
        } catch (MediaInfoException e) {
            e.printStackTrace();
        }
        return fragment;
    }

    private VideoGetResponse getCurrentResponse() {
        return (VideoGetResponse) getArguments().getParcelable("currentResponse");
    }

    protected int getLayoutId() {
        return 2130903079;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = LocalizationManager.inflate(getActivity(), getLayoutId(), container, false);
        initViews(mainView);
        return mainView;
    }

    private void initViews(View mainView) {
        this.imageView = (UrlImageView) mainView.findViewById(2131624526);
        this.playPause = (ImageView) mainView.findViewById(2131624528);
        this.liveText = (TextView) mainView.findViewById(C0158R.id.live_text);
        this.startText = (TextView) mainView.findViewById(C0158R.id.start_text);
        this.endText = (TextView) mainView.findViewById(C0158R.id.end_text);
        this.seekbar = (SeekBar) mainView.findViewById(C0158R.id.seekbar);
        this.mLine2 = (TextView) mainView.findViewById(C0158R.id.textview2);
        this.spinner = (ProgressBar) mainView.findViewById(2131624536);
        this.controllers = mainView.findViewById(C0158R.id.controllers);
        this.playPause.setOnClickListener(new C14071());
        this.seekbar.setOnSeekBarChangeListener(new C14082());
    }

    public void showLoading(boolean visible) {
        this.spinner.setVisibility(visible ? 0 : 4);
    }

    public void adjustControllersForLiveStream(boolean isLive) {
        int visibility;
        int i = 0;
        if (isLive) {
            visibility = 4;
        } else {
            visibility = 0;
        }
        TextView textView = this.liveText;
        if (!isLive) {
            i = 4;
        }
        textView.setVisibility(i);
        this.startText.setVisibility(visibility);
        this.endText.setVisibility(visibility);
        this.seekbar.setVisibility(visibility);
    }

    protected void showError(int resId) {
        super.showError(resId);
        this.spinner.setVisibility(4);
        setSubTitle(LocalizationManager.getString(getActivity(), resId));
        showToastIfVisible(resId, 0);
    }

    public void setClosedCaptionState(int status) {
        Logger.m172d("setClosedCaptionState():" + status);
    }

    public void setPlaybackStatus(int state) {
        Logger.m176e("setPlaybackStatus(): state = " + state);
        switch (state) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.spinner.setVisibility(4);
                this.playPause.setImageResource(2130838191);
                this.playPause.setVisibility(0);
                this.mLine2.setText(getString(C0158R.string.ccl_casting_to_device, getDeviceName()));
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                this.spinner.setVisibility(4);
                this.playPause.setVisibility(0);
                this.playPause.setImageResource(2130838190);
                this.mLine2.setText(getString(C0158R.string.ccl_casting_to_device, getDeviceName()));
                this.controllers.setVisibility(0);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                this.controllers.setVisibility(0);
                this.spinner.setVisibility(4);
                this.playPause.setVisibility(0);
                this.playPause.setImageResource(2130838191);
                this.mLine2.setText(getString(C0158R.string.ccl_casting_to_device, getDeviceName()));
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                this.playPause.setVisibility(4);
                this.spinner.setVisibility(0);
                this.mLine2.setText(getString(C0158R.string.ccl_loading));
            default:
        }
    }

    public void updateSeekbar(int position, int duration) {
        this.seekbar.setProgress(position);
        this.seekbar.setMax(duration);
        this.startText.setText(Utils.formatMillis((long) position));
        this.endText.setText(Utils.formatMillis((long) duration));
    }

    public void setImage(Bitmap bitmap) {
        Logger.m172d("setBitmap");
    }

    protected void showImage(Uri uri) {
        super.showImage(uri);
        if (uri != null) {
            ImageViewManager.getInstance().displayImage(uri.toString(), this.imageView, 2130838495, null);
        }
    }

    public void setTitleText(String text) {
        setTitle(text);
    }

    public void setSubTitle(String text) {
        this.mLine2.setText(text);
    }

    public void setStreamType(int streamType) {
        this.mStreamType = streamType;
    }

    public void updateControllersStatus(boolean enabled) {
        boolean z = false;
        this.controllers.setVisibility(enabled ? 0 : 4);
        if (enabled) {
            if (this.mStreamType == 2) {
                z = true;
            }
            adjustControllersForLiveStream(z);
        }
    }

    protected void onVideoFinish() {
        super.onVideoFinish();
        Activity activity = getActivity();
        if (activity == null || !(activity instanceof VideoActivity)) {
            closeActivity();
        } else {
            ((VideoActivity) activity).onVideoFinish();
        }
    }

    public void closeActivity() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    protected void onCastDisconnect(long position) {
        super.onCastDisconnect(position);
        Activity activity = getActivity();
        VideoGetResponse response = getCurrentResponse();
        if (activity == null || !(activity instanceof VideoActivity) || response == null) {
            closeActivity();
        } else {
            ((VideoActivity) activity).replacePlayer(response, (int) position);
        }
    }
}
