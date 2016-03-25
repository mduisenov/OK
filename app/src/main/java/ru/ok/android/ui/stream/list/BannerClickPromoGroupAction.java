package ru.ok.android.ui.stream.list;

import android.view.View;
import ru.ok.model.stream.banner.Banner;
import ru.ok.model.stream.banner.StatPixelHolder;

public class BannerClickPromoGroupAction extends BannerClickAction {
    private final String groupId;

    public BannerClickPromoGroupAction(Banner banner, StatPixelHolder pixelHolder, String groupId) {
        super(banner, pixelHolder);
        this.groupId = groupId;
    }

    public void setTags(View view) {
        super.setTags(view);
        view.setTag(2131624338, this.groupId);
    }
}
