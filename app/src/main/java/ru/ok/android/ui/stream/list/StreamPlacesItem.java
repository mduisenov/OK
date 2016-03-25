package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.model.stream.entities.FeedPlaceEntity;

public class StreamPlacesItem extends StreamItem {
    private final List<FeedPlaceEntity> places;
    private final CharSequence text;

    static class PlacesViewHolder extends ViewHolder {
        final TextView placesTextView;

        public PlacesViewHolder(View view, StreamItemViewController streamItemViewController) {
            super(view);
            this.placesTextView = (TextView) view.findViewById(2131625364);
            this.placesTextView.setOnClickListener(streamItemViewController.getPlacesClickListener());
        }
    }

    protected StreamPlacesItem(FeedWithState feed, List<FeedPlaceEntity> places, CharSequence text) {
        super(20, 3, 1, feed);
        this.places = places;
        this.text = text;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903492, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof PlacesViewHolder) {
            PlacesViewHolder placesViewHolder = (PlacesViewHolder) holder;
            placesViewHolder.placesTextView.setText(this.text);
            placesViewHolder.placesTextView.setTag(2131624328, this.places);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new PlacesViewHolder(view, streamItemViewController);
    }
}
