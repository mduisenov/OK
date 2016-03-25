package ru.ok.android.ui.adapters.music.artists;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import ru.ok.android.ui.adapters.friends.ItemClickListenerControllerProvider;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController;
import ru.ok.android.utils.BitmapRender;
import ru.ok.model.wmf.Artist;

public class ArtistsAdapter extends Adapter implements ItemClickListenerControllerProvider {
    private static int IMG_SIZE;
    protected static LayoutInflater inflater;
    private List<Artist> data;
    private RecyclerItemClickListenerController itemClickListenerController;

    static {
        inflater = null;
        IMG_SIZE = 50;
    }

    public ArtistsAdapter(Context context, Artist[] artists) {
        this.itemClickListenerController = new RecyclerItemClickListenerController();
        IMG_SIZE = BitmapRender.getImageSize(context, 50);
        inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.data = Collections.synchronizedList(new LinkedList());
        addArtists(artists);
    }

    public Object getItem(int i) {
        return i < this.data.size() ? (Artist) this.data.get(i) : null;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(2130903249, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        ((ViewHolder) holder).setArtist((Artist) this.data.get(position));
        this.itemClickListenerController.onBindViewHolder(holder, position);
    }

    public long getItemId(int i) {
        return i < this.data.size() ? ((Artist) this.data.get(i)).id : 0;
    }

    public int getItemCount() {
        return this.data.size();
    }

    public void clear() {
        this.data.clear();
    }

    public void addArtists(Artist[] artists) {
        for (Artist artist : artists) {
            this.data.add(artist);
        }
    }

    public void setArtists(Artist[] artists) {
        clear();
        for (Artist artist : artists) {
            this.data.add(artist);
        }
    }

    public Artist[] getArtists() {
        return (Artist[]) this.data.toArray(new Artist[this.data.size()]);
    }

    public RecyclerItemClickListenerController getItemClickListenerController() {
        return this.itemClickListenerController;
    }
}
