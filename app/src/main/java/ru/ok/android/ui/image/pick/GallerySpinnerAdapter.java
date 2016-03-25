package ru.ok.android.ui.image.pick;

import android.content.Context;
import java.util.Collections;
import java.util.List;
import ru.ok.android.ui.adapters.spinner.BaseNavigationSpinnerAdapter;

public final class GallerySpinnerAdapter extends BaseNavigationSpinnerAdapter {
    private List<DeviceGalleryInfo> data;

    public GallerySpinnerAdapter(Context context, List<DeviceGalleryInfo> data) {
        super(context);
        this.data = Collections.emptyList();
        this.data = data;
    }

    protected String getItemText(int position) {
        return ((DeviceGalleryInfo) this.data.get(position)).name;
    }

    protected String getCountText(int position) {
        return String.valueOf(((DeviceGalleryInfo) this.data.get(position)).photos.size());
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
}
