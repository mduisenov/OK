package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.mail.libverify.C0176R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.model.stream.entities.FeedAchievementTypeEntity;

public class StreamAchievementsItem extends StreamItem {
    private final int achievementSize;
    final List<FeedAchievementTypeEntity> achvmnts;
    final List<String> urls;

    static class AchievementsViewHolder extends ViewHolder {
        final LinearLayout container;

        public AchievementsViewHolder(View view) {
            super(view);
            this.container = (LinearLayout) view.findViewById(2131625337);
        }
    }

    protected StreamAchievementsItem(FeedWithState feed, List<FeedAchievementTypeEntity> achvmnts, int achievementSize) {
        super(30, 3, 1, feed);
        this.achvmnts = achvmnts;
        this.achievementSize = achievementSize;
        this.urls = new ArrayList(achvmnts.size());
        for (FeedAchievementTypeEntity entity : achvmnts) {
            this.urls.add(entity.getLargestPicUrl());
        }
    }

    public void prefetch() {
        int i = 0;
        for (String s : this.urls) {
            if (i != 5) {
                PrefetchUtils.prefetchUrl(s);
                i++;
            } else {
                return;
            }
        }
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903464, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof AchievementsViewHolder) {
            AchievementsViewHolder viewHolder = (AchievementsViewHolder) holder;
            streamItemViewController.getViewCache().collectAndClearChildViews(viewHolder.container);
            int position = 0;
            int hSpacing = streamItemViewController.getActivity().getResources().getDimensionPixelOffset(2131231199);
            int layoutId = this.achvmnts.size() == 1 ? 2130903450 : 2130903449;
            for (FeedAchievementTypeEntity achvmnt : this.achvmnts) {
                ViewGroup achievementContainer = (ViewGroup) streamItemViewController.getViewCache().getViewWithLayoutId(layoutId, viewHolder.container);
                UrlImageView image = (UrlImageView) achievementContainer.findViewById(C0263R.id.image);
                image.setIsAlpha(true);
                TextView title = (TextView) achievementContainer.findViewById(C0176R.id.title);
                ImageViewManager.getInstance().displayImage(achvmnt.getLargestPicUrl(), image, 2130838049, streamItemViewController.getImageLoadBlocker());
                title.setText(achvmnt.getTitle());
                MarginLayoutParams lp = (MarginLayoutParams) image.getLayoutParams();
                lp.height = this.achievementSize;
                lp.width = this.achievementSize;
                image.setLayoutParams(lp);
                if (this.achvmnts.size() > 1) {
                    MarginLayoutParams lpC = (MarginLayoutParams) achievementContainer.getLayoutParams();
                    lpC.leftMargin = position > 0 ? hSpacing : 0;
                    achievementContainer.setLayoutParams(lpC);
                }
                viewHolder.container.addView(achievementContainer);
                position++;
            }
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new AchievementsViewHolder(view);
    }
}
