package ru.ok.android.ui.adapters.music.tuners;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.adapters.ScrollLoadBlocker;
import ru.ok.android.ui.adapters.friends.ItemClickListenerControllerProvider;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController;
import ru.ok.android.ui.fragments.messages.view.TunersMultipleImageView;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.Tuner;

public class MusicTunersAdapter extends Adapter implements ItemClickListenerControllerProvider {
    private Context context;
    private int currentTunerId;
    private List<Tuner> data;
    private final ScrollLoadBlocker imageLoadBlocker;
    private RecyclerItemClickListenerController itemClickListenerController;
    private final Blocker scrollBlocker;

    public class Blocker implements OnScrollListener {
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            MusicTunersAdapter.this.imageLoadBlocker.onScrollStateChanged(view, scrollState);
        }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            MusicTunersAdapter.this.imageLoadBlocker.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public void setCurrentTunerId(int currentTunerId) {
        if (this.currentTunerId != currentTunerId) {
            this.currentTunerId = currentTunerId;
            notifyDataSetChanged();
        }
    }

    public int getCurrentTunerId() {
        return this.currentTunerId;
    }

    public MusicTunersAdapter(Context context) {
        this.data = new ArrayList();
        this.imageLoadBlocker = ScrollLoadBlocker.forIdleAndTouchIdle();
        this.scrollBlocker = new Blocker();
        this.itemClickListenerController = new RecyclerItemClickListenerController();
        this.context = context;
    }

    public void setData(List<Tuner> tuners) {
        this.data = tuners;
    }

    public List<Tuner> getData() {
        return this.data;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LocalizationManager.inflate(this.context, 2130903259, parent, false);
        ViewHolder holder = new ViewHolder(convertView);
        holder.textTuner = (TextView) convertView.findViewById(2131624984);
        holder.textArtists = (TextView) convertView.findViewById(2131624985);
        holder.image = (TunersMultipleImageView) convertView.findViewById(C0263R.id.image);
        return holder;
    }

    public void onBindViewHolder(ViewHolder recyclerHolder, int position) {
        ViewHolder holder = (ViewHolder) recyclerHolder;
        Tuner tuner = (Tuner) this.data.get(position);
        holder.textTuner.setText(tuner.getFriendlyName());
        List<Artist> artists = holder.image.getArtists();
        artists.clear();
        StringBuilder builder = new StringBuilder();
        Iterator i$ = tuner.artists.iterator();
        while (i$.hasNext()) {
            Artist artist = (Artist) i$.next();
            if (artist != null) {
                artists.add(artist);
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(artist.name);
            }
        }
        holder.textArtists.setText(builder.toString());
        holder.image.configure(false);
        holder.image.setClickable(false);
        if (this.currentTunerId == tuner.id) {
            holder.setPlay(true);
        } else {
            holder.setPlay(false);
        }
        this.itemClickListenerController.onBindViewHolder(recyclerHolder, position);
    }

    public int getItemCount() {
        return this.data.size();
    }

    public RecyclerItemClickListenerController getItemClickListenerController() {
        return this.itemClickListenerController;
    }
}
