package ru.ok.android.ui.video.fragments.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import ru.mail.libverify.C0176R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.utils.NumberFormatUtil;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.video.MovieInfo;

public class MoviesRecycleAdapter extends Adapter {
    protected final Context context;
    private List<MovieInfo> data;
    private OnSelectMovieListener listener;

    public interface OnSelectMovieListener {
        void onSelectMovie(View view, MovieInfo movieInfo, int i);
    }

    protected class MovieViewHolder extends ViewHolder implements OnClickListener {
        TextView countTextView;
        UrlImageView thumbnailView;
        TextView timeTextView;
        TextView titleTextView;

        MovieViewHolder(View convertView) {
            super(convertView);
            this.titleTextView = (TextView) convertView.findViewById(C0263R.id.name);
            this.thumbnailView = (UrlImageView) convertView.findViewById(2131625108);
            this.timeTextView = (TextView) convertView.findViewById(C0176R.id.time);
            this.countTextView = (TextView) convertView.findViewById(2131624446);
            convertView.setOnClickListener(this);
        }

        public void onClick(View v) {
            if (MoviesRecycleAdapter.this.listener != null) {
                MoviesRecycleAdapter.this.listener.onSelectMovie(v, MoviesRecycleAdapter.this.getDataMovie(getPosition()), getPosition());
            }
        }
    }

    protected MovieInfo getDataMovie(int position) {
        return (MovieInfo) this.data.get(position);
    }

    public MoviesRecycleAdapter(Context context) {
        this.data = new ArrayList();
        this.context = context;
    }

    public void setListener(OnSelectMovieListener listener) {
        this.listener = listener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(2130903333, parent, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        MovieInfo movieInfo = (MovieInfo) this.data.get(position);
        updateViewHolder((MovieViewHolder) viewHolder, movieInfo.title, movieInfo.duration, movieInfo.getTotalViews(), movieInfo.getThumbnails());
    }

    protected void updateViewHolder(MovieViewHolder holder, String title, int duration, int totalViews, TreeSet<PhotoSize> thumbnails) {
        holder.titleTextView.setText(title);
        holder.timeTextView.setText(msToString(duration));
        holder.countTextView.setText(LocalizationManager.getString(this.context, StringUtils.plural(totalViews, 2131166861, 2131166860, 2131166858, 2131166859), NumberFormatUtil.getFormatFrenchText(totalViews)));
        if (thumbnails != null && thumbnails.size() > 0 && !TextUtils.isEmpty(((PhotoSize) thumbnails.first()).getUrl())) {
            ImageViewManager.getInstance().displayImage(((PhotoSize) thumbnails.first()).getUrl(), holder.thumbnailView, 2130838495, null);
        }
    }

    protected static String msToString(int millis) {
        long hour = TimeUnit.MILLISECONDS.toHours((long) millis);
        long second = TimeUnit.MILLISECONDS.toSeconds((((long) millis) - TimeUnit.HOURS.toMillis(hour)) - TimeUnit.MINUTES.toMillis(TimeUnit.MILLISECONDS.toMinutes(((long) millis) - TimeUnit.HOURS.toMillis(hour))));
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", new Object[]{Long.valueOf(hour), Long.valueOf(minute), Long.valueOf(second)});
        }
        return String.format("%02d:%02d", new Object[]{Long.valueOf(minute), Long.valueOf(second)});
    }

    public int getItemCount() {
        return this.data.size();
    }

    public void swapData(Collection<MovieInfo> data) {
        if (data != null) {
            this.data.clear();
            this.data.addAll(data);
        }
        notifyDataSetChanged();
    }
}
