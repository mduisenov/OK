package ru.ok.android.ui.video.fragments.target;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import ru.mail.android.mytarget.ads.MyTargetVideoView;
import ru.mail.android.mytarget.ads.MyTargetVideoView$MyTargetVideoViewListener;
import ru.mail.android.mytarget.ads.MyTargetVideoView.BannerInfo;
import ru.ok.android.target.TargetUtils;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.video.OneLogVideo;
import ru.ok.android.ui.video.activity.VideoActivity;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.video.Advertisement;
import ru.ok.onelog.video.player.AdvParam;

public class VideoTargetFragment extends BaseFragment implements MyTargetVideoView$MyTargetVideoViewListener {
    private Button buttonClose;
    private int closeSec;
    private Handler handler;
    private ImageButton playButton;
    private Fragment playerFragment;
    private ProgressBar progressBar;
    private ProgressBar spinnerView;
    private long startTargetVideoTime;
    private MyTargetVideoView targetVideoView;
    private TextView textTarget;
    private View viewRoot;

    public interface TargetListener {
        void onTargetFinish(VideoTargetFragment videoTargetFragment, Fragment fragment);

        void onTargetStart(VideoTargetFragment videoTargetFragment, Fragment fragment);
    }

    /* renamed from: ru.ok.android.ui.video.fragments.target.VideoTargetFragment.1 */
    class C13721 implements OnClickListener {
        C13721() {
        }

        public void onClick(View v) {
            VideoTargetFragment.this.targetVideoView.closedByUser();
            VideoTargetFragment.this.notifyFinish();
        }
    }

    /* renamed from: ru.ok.android.ui.video.fragments.target.VideoTargetFragment.2 */
    class C13732 implements OnClickListener {
        C13732() {
        }

        public void onClick(View v) {
            VideoTargetFragment.this.targetVideoView.resume();
            VideoTargetFragment.this.playButton.setVisibility(8);
        }
    }

    /* renamed from: ru.ok.android.ui.video.fragments.target.VideoTargetFragment.3 */
    class C13743 implements Runnable {
        final /* synthetic */ float val$pr;
        final /* synthetic */ int val$sec;

        C13743(float f, int i) {
            this.val$pr = f;
            this.val$sec = i;
        }

        public void run() {
            Context activity = VideoTargetFragment.this.getActivity();
            if (activity != null) {
                VideoTargetFragment.this.progressBar.setProgress((int) this.val$pr);
                if (this.val$sec >= VideoTargetFragment.this.closeSec) {
                    if (VideoTargetFragment.this.buttonClose.getVisibility() == 0 && !VideoTargetFragment.this.buttonClose.isEnabled()) {
                        VideoTargetFragment.this.buttonClose.setEnabled(true);
                        VideoTargetFragment.this.buttonClose.setText(LocalizationManager.getString(activity, 2131166571));
                        VideoTargetFragment.this.textTarget.setVisibility(8);
                    }
                } else if (VideoTargetFragment.this.buttonClose.getVisibility() == 0) {
                    if (VideoTargetFragment.this.buttonClose.isEnabled()) {
                        VideoTargetFragment.this.buttonClose.setEnabled(false);
                    }
                    VideoTargetFragment.this.buttonClose.setText(LocalizationManager.getString(activity, 2131166572) + " " + (VideoTargetFragment.this.closeSec - this.val$sec));
                }
            }
        }
    }

    public VideoTargetFragment() {
        this.closeSec = 5;
        this.handler = new Handler();
    }

    public static VideoTargetFragment newInstance(Advertisement advertisement, String videoId) {
        VideoTargetFragment result = new VideoTargetFragment();
        Bundle args = new Bundle();
        args.putParcelable("adv", advertisement);
        args.putString("videoId", videoId);
        result.setArguments(args);
        return result;
    }

    public void setPlayerFragment(Fragment fragment) {
        this.playerFragment = fragment;
    }

    public Advertisement getAdvertisement() {
        return (Advertisement) getArguments().getParcelable("adv");
    }

    public String getExtraVideoId() {
        return getArguments().getString("videoId");
    }

    protected int getLayoutId() {
        return 2130903206;
    }

    public void onPause() {
        super.onPause();
        this.targetVideoView.pause();
        this.playButton.setVisibility(0);
    }

    protected CharSequence getTitle() {
        return "";
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.viewRoot = LocalizationManager.inflate(getContext(), getLayoutId(), container, false);
        setHasOptionsMenu(true);
        this.spinnerView = (ProgressBar) this.viewRoot.findViewById(2131624536);
        this.buttonClose = (Button) this.viewRoot.findViewById(2131624868);
        this.buttonClose.setTextColor(getResources().getColor(2131493208));
        this.buttonClose.setOnClickListener(new C13721());
        this.playButton = (ImageButton) this.viewRoot.findViewById(2131624795);
        this.playButton.setOnClickListener(new C13732());
        this.progressBar = (ProgressBar) this.viewRoot.findViewById(2131624548);
        this.textTarget = (TextView) this.viewRoot.findViewById(2131624867);
        this.targetVideoView = (MyTargetVideoView) this.viewRoot.findViewById(2131624866);
        TargetUtils.initMyTargetVideo(this.targetVideoView, getAdvertisement());
        this.targetVideoView.setListener(this);
        String videoId = getExtraVideoId();
        if (!TextUtils.isEmpty(videoId)) {
            OneLogVideo.logAdvertisement(Long.parseLong(videoId), AdvParam.slot_request_preroll);
        }
        this.startTargetVideoTime = SystemClock.elapsedRealtime();
        this.targetVideoView.load();
        return this.viewRoot;
    }

    public void onLoad(MyTargetVideoView myTargetVideoView) {
        Logger.m172d("onLoad");
        String videoId = getExtraVideoId();
        if (!TextUtils.isEmpty(videoId)) {
            long videoIdLong = Long.parseLong(videoId);
            if (this.startTargetVideoTime > 0) {
                OneLogVideo.logAdvertisementStartTime(videoIdLong, SystemClock.elapsedRealtime() - this.startTargetVideoTime);
            }
            OneLogVideo.logAdvertisement(videoIdLong, AdvParam.preroll);
        }
        if (getActivity() != null) {
            notifyStart();
            this.targetVideoView.startPreroll();
        }
    }

    public void onSuspenseBanner(MyTargetVideoView myTargetVideoView, BannerInfo bannerInfo) {
    }

    public void onResumptionBanner(MyTargetVideoView myTargetVideoView, BannerInfo bannerInfo) {
    }

    public void onNoAd(String s, MyTargetVideoView myTargetVideoView) {
        Logger.m172d("onNoAd");
        String videoId = getExtraVideoId();
        if (!TextUtils.isEmpty(videoId) && this.startTargetVideoTime > 0) {
            OneLogVideo.logNoAdvertisementStartTime(Long.parseLong(videoId), SystemClock.elapsedRealtime() - this.startTargetVideoTime);
        }
        this.spinnerView.setVisibility(8);
        notifyFinish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625505:
                this.targetVideoView.handleClick();
                return true;
            default:
                return false;
        }
    }

    public void onStartBanner(MyTargetVideoView myTargetVideoView, BannerInfo bannerInfo) {
        Logger.m172d("onStartBanner: " + bannerInfo.ctaText);
        VideoActivity activity = (VideoActivity) getActivity();
        activity.setVisibilityMenuItem(false);
        if (!TextUtils.isEmpty(bannerInfo.ctaText)) {
            activity.setVisibilityTargetClickText(true, bannerInfo.ctaText);
        }
        if (bannerInfo.allowClose) {
            this.buttonClose.setVisibility(0);
            this.closeSec = (int) bannerInfo.allowCloseDelay;
        } else {
            this.buttonClose.setVisibility(8);
        }
        this.spinnerView.setVisibility(8);
        this.textTarget.setVisibility(0);
        this.progressBar.setVisibility(0);
    }

    public void onCompleteBanner(MyTargetVideoView myTargetVideoView, BannerInfo bannerInfo, String var) {
        Logger.m172d("onCompleteBanner");
    }

    public void onComplete(String s, MyTargetVideoView myTargetVideoView, String var) {
        Logger.m172d("onComplete");
        notifyFinish();
    }

    public void onError(String s, MyTargetVideoView myTargetVideoView) {
        Logger.m172d("onError");
        String videoId = getExtraVideoId();
        if (!TextUtils.isEmpty(videoId)) {
            OneLogVideo.logAdvertisementError(Long.parseLong(videoId), s);
        }
        this.spinnerView.setVisibility(8);
        notifyFinish();
    }

    public void onTimeLeftChange(float timeLeft, float duration, MyTargetVideoView myTargetVideoView) {
        Logger.m172d("onTimeLeftChange");
        this.handler.post(new C13743(100.0f - (timeLeft / (duration / 100.0f)), (int) (duration - timeLeft)));
    }

    private void notifyFinish() {
        Activity activity = getActivity();
        if (activity instanceof TargetListener) {
            ((TargetListener) activity).onTargetFinish(this, this.playerFragment);
        }
    }

    private void notifyStart() {
        Activity activity = getActivity();
        if (activity instanceof TargetListener) {
            ((TargetListener) activity).onTargetStart(this, this.playerFragment);
        }
    }
}
