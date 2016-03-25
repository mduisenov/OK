package ru.ok.android.ui.stream.list;

import android.view.View;
import ru.ok.model.stream.banner.Banner;
import ru.ok.model.stream.banner.StatPixelHolder;

public class BannerClickPromoUserAction extends BannerClickAction {
    private final String userId;

    public BannerClickPromoUserAction(Banner banner, StatPixelHolder pixelHolder, String userId) {
        super(banner, pixelHolder);
        this.userId = userId;
    }

    public void setTags(View view) {
        super.setTags(view);
        view.setTag(2131624339, this.userId);
    }
}
