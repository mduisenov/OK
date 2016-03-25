package ru.ok.android.ui.adapters.places;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.location.LocationStatusCodes;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.places.Place;

public class PlacesAdapter extends BaseAdapter implements OnClickListener {
    protected final Context context;
    private List<Place> data;
    private MenuPlaceListener menuPlaceListener;

    public interface MenuPlaceListener {
        void onPlaceMenuClick(View view, Place place);
    }

    private static class ViewHolder {
        public TextView address;
        public TextView category;
        public UrlImageView image;
        public ImageView menu;
        public TextView name;

        private ViewHolder() {
        }

        public static ViewHolder createViewHolder(View convertView) {
            ViewHolder holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(2131624977);
            holder.category = (TextView) convertView.findViewById(2131624994);
            holder.address = (TextView) convertView.findViewById(2131624995);
            holder.image = (UrlImageView) convertView.findViewById(C0263R.id.image);
            holder.menu = (ImageView) convertView.findViewById(2131624993);
            return holder;
        }
    }

    public PlacesAdapter(Context context) {
        this.data = new ArrayList();
        this.context = context;
    }

    public int getCount() {
        return this.data.size();
    }

    public Object getItem(int position) {
        return this.data.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public void addPlaces(List<Place> places) {
        this.data.addAll(places);
    }

    public boolean deletePlace(Place place) {
        return this.data.remove(place);
    }

    public void clearData() {
        this.data.clear();
    }

    public void setPlaceMenuListener(MenuPlaceListener listener) {
        this.menuPlaceListener = listener;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newView(parent);
        }
        bindView((ViewHolder) convertView.getTag(), (Place) getItem(position));
        return convertView;
    }

    protected View newView(ViewGroup parent) {
        View convertView = LocalizationManager.inflate(this.context, 2130903263, parent, false);
        convertView.setTag(ViewHolder.createViewHolder(convertView));
        return convertView;
    }

    protected void bindView(ViewHolder holder, Place place) {
        holder.name.setText(place.name);
        if (place.address != null) {
            holder.address.setText(place.address.getStringAddress());
        }
        if (place.category != null) {
            if (place.distance > 0) {
                holder.category.setText(StringUtils.uppercaseFirst(place.category.text) + ", " + distanceString(place.distance));
            } else {
                holder.category.setText(StringUtils.uppercaseFirst(place.category.text));
            }
        }
        ImageViewManager.getInstance().displayImage(getImageUrl(place), holder.image, 2130837933, null);
        if (TextUtils.isEmpty(place.id)) {
            holder.name.setVisibility(4);
            return;
        }
        holder.name.setVisibility(0);
        holder.menu.setTag(place);
        holder.menu.setOnClickListener(this);
    }

    private String distanceString(int value) {
        if (value < LocationStatusCodes.GEOFENCE_NOT_AVAILABLE) {
            return this.context.getString(2131166361, new Object[]{Integer.valueOf(value)});
        }
        return this.context.getString(2131166360, new Object[]{Integer.valueOf(value / LocationStatusCodes.GEOFENCE_NOT_AVAILABLE)});
    }

    private String getImageUrl(Place place) {
        return "http://stg.odnoklassniki.ru/static/mobapp-android/1-0-0/img/places/ic_" + place.category.id + ".png";
    }

    public void onClick(View v) {
        Place place = (Place) v.getTag();
        if (this.menuPlaceListener != null) {
            this.menuPlaceListener.onPlaceMenuClick(v, place);
        }
    }
}
