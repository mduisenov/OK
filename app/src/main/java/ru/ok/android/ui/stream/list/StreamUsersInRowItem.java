package ru.ok.android.ui.stream.list;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import java.util.List;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.model.stream.entities.FeedUserEntity;

public class StreamUsersInRowItem extends StreamManyInRowItem<FeedUserEntity, UsersRecyclerAdapter> {
    private final int vSpacingBottom;

    protected StreamUsersInRowItem(FeedWithState feedWithState, List<FeedUserEntity> feedUserEntities, int layoutHeight, int vSpacingBottom) {
        super(12, 3, 3, feedWithState, feedUserEntities, layoutHeight);
        this.vSpacingBottom = vSpacingBottom;
    }

    int getVSpacingBottom(Context context) {
        return this.vSpacingBottom;
    }

    private static UsersRecyclerAdapter newAdapter(StreamItemViewController streamItemViewController) {
        return new UsersRecyclerAdapter(streamItemViewController.getActivity(), streamItemViewController.getLayoutInflater(), streamItemViewController.getImageLoadBlocker(), streamItemViewController.getUserClickListener());
    }

    public static RecyclerViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        UsersRecyclerAdapter recyclerAdapter = newAdapter(streamItemViewController);
        RecyclerViewHolder<UsersRecyclerAdapter> viewHolder = new RecyclerViewHolder(view, recyclerAdapter);
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), 0, false));
        viewHolder.recyclerView.setAdapter(recyclerAdapter);
        return viewHolder;
    }

    protected void setData(UsersRecyclerAdapter adapter, List<FeedUserEntity> feedUserEntities) {
        adapter.setUsers(feedUserEntities);
        adapter.notifyDataSetChanged();
    }
}
