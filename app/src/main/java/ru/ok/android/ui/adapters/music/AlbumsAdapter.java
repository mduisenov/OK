package ru.ok.android.ui.adapters.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.model.wmf.Album;

public final class AlbumsAdapter extends BaseAdapter {
    private final List<Album> data;
    private final LayoutInflater inflater;

    private static class ViewHolder {
        final UrlImageView image;
        final TextView textAlbumName;
        final TextView textEnsembleName;

        ViewHolder(View view) {
            this.textAlbumName = (TextView) view.findViewById(2131624961);
            this.textEnsembleName = (TextView) view.findViewById(2131624962);
            this.image = (UrlImageView) view.findViewById(C0263R.id.image);
        }
    }

    public AlbumsAdapter(Context context, Album[] albums) {
        this.data = new ArrayList();
        this.inflater = LayoutInflater.from(context);
        addAlbums(albums);
    }

    public int getCount() {
        return this.data.size();
    }

    public Object getItem(int i) {
        return this.data.get(i);
    }

    public long getItemId(int i) {
        return ((Album) this.data.get(i)).id;
    }

    public void clear() {
        this.data.clear();
    }

    public void addAlbums(Album[] albums) {
        for (Album album : albums) {
            this.data.add(album);
        }
    }

    public void setAlbums(Album[] albums) {
        clear();
        for (Album album : albums) {
            this.data.add(album);
        }
    }

    public Album[] getAlbums() {
        return (Album[]) this.data.toArray(new Album[this.data.size()]);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = this.inflater.inflate(2130903247, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Album album = (Album) this.data.get(position);
        holder.textAlbumName.setText(album.name);
        holder.textEnsembleName.setText(album.ensemble);
        ImageViewManager.getInstance().displayImage(album.imageUrl, holder.image, 2130837620, null);
        return convertView;
    }
}
