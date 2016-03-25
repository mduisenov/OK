package ru.ok.android.ui.adapters.music.collections;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.adapters.friends.ItemClickListenerControllerProvider;
import ru.ok.android.ui.adapters.music.DotsCursorRecyclerAdapter;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.UserTrackCollection;

public class MusicCollectionsCursorAdapter extends DotsCursorRecyclerAdapter implements OnClickListener, ItemClickListenerControllerProvider {
    protected final RecyclerItemClickListenerController itemClickListenerController;

    public MusicCollectionsCursorAdapter(Context context, Cursor c) {
        super(context, c);
        this.itemClickListenerController = new RecyclerItemClickListenerController();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LocalizationManager.inflate(parent.getContext(), 2130903257, parent, false));
        onCreateDotsView(holder.dots);
        return holder;
    }

    public void onBindViewHolder(ViewHolder recyclerHolder, int position) {
        ViewHolder holder = (ViewHolder) recyclerHolder;
        Context context = holder.itemView.getContext();
        UserTrackCollection collection = MusicStorageFacade.cursor2UserTrackCollection(getItemCursor(position));
        holder.textCollectionName.setText(collection.name);
        boolean hasTracks = collection.tracksCount >= 0;
        ViewUtil.setVisibility(holder.textCount, hasTracks);
        if (hasTracks) {
            holder.textCount.setText(collection.tracksCount + " " + LocalizationManager.getString(context, StringUtils.plural((long) collection.tracksCount, 2131166608, 2131166609, 2131166610)));
        }
        if (!holder.image.equalsUrl(collection.imageUrl)) {
            if (collection.imageUrl == null || collection.imageUrl.length() <= 0) {
                holder.image.setPlaceholderResource(2130837791);
            } else {
                holder.image.setPlaceholderResource(2130837791);
                ImageViewManager.getInstance().displayImage(collection.imageUrl, holder.image, null);
            }
        }
        bindDots(holder.dots, collection);
        this.itemClickListenerController.onBindViewHolder(holder, position);
    }

    public RecyclerItemClickListenerController getItemClickListenerController() {
        return this.itemClickListenerController;
    }
}
