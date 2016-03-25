package ru.ok.android.ui.stream.list;

import android.view.View;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.model.stream.banner.Banner;
import ru.ok.model.stream.banner.StatPixelHolder;

public class BannerClickAction implements ClickAction {
    private final Banner banner;
    private final StatPixelHolder statPixelHolder;

    public BannerClickAction(Banner banner, StatPixelHolder statPixelHolder) {
        this.banner = banner;
        this.statPixelHolder = statPixelHolder;
    }

    public void setClickListener(View view, StreamItemViewController streamItemViewController) {
        view.setOnClickListener(streamItemViewController.getBannerClickListener());
    }

    public void setTags(View view) {
        view.setTag(2131624313, this.banner);
        view.setTag(2131624342, this.statPixelHolder);
    }
}
