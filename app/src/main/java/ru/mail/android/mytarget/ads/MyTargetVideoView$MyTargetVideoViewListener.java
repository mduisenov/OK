package ru.mail.android.mytarget.ads;

import ru.mail.android.mytarget.ads.MyTargetVideoView.BannerInfo;

public interface MyTargetVideoView$MyTargetVideoViewListener {
    void onComplete(String str, MyTargetVideoView myTargetVideoView, String str2);

    void onCompleteBanner(MyTargetVideoView myTargetVideoView, BannerInfo bannerInfo, String str);

    void onError(String str, MyTargetVideoView myTargetVideoView);

    void onLoad(MyTargetVideoView myTargetVideoView);

    void onNoAd(String str, MyTargetVideoView myTargetVideoView);

    void onResumptionBanner(MyTargetVideoView myTargetVideoView, BannerInfo bannerInfo);

    void onStartBanner(MyTargetVideoView myTargetVideoView, BannerInfo bannerInfo);

    void onSuspenseBanner(MyTargetVideoView myTargetVideoView, BannerInfo bannerInfo);

    void onTimeLeftChange(float f, float f2, MyTargetVideoView myTargetVideoView);
}
