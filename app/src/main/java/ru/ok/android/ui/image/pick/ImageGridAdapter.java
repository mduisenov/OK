package ru.ok.android.ui.image.pick;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.ok.android.ui.custom.photo.DevicePhotoTileView;
import ru.ok.android.ui.custom.photo.DevicePhotoTileView.OnImageSelectionListener;
import ru.ok.android.utils.localization.LocalizationManager;

public class ImageGridAdapter extends Adapter<DevicePhotoTileViewHolder> {
    private final int choiceMode;
    private Context context;
    private final ArrayList<GalleryImageInfo> items;
    private final List<GalleryImageInfo> selectedPhotos;
    protected OnSelectionChangeListener selectionChangeListener;

    /* renamed from: ru.ok.android.ui.image.pick.ImageGridAdapter.1 */
    class C09901 implements OnImageSelectionListener {
        C09901() {
        }

        public void onImageSelection(DevicePhotoTileView view, boolean selected) {
            GalleryImageInfo photo = view.getPhoto();
            if (photo.isBroken()) {
                Toast.makeText(ImageGridAdapter.this.context, LocalizationManager.getString(ImageGridAdapter.this.context, 2131166503), 0).show();
                return;
            }
            if (ImageGridAdapter.this.choiceMode == 1) {
                ImageGridAdapter.this.selectedPhotos.clear();
                if (selected) {
                    ImageGridAdapter.this.selectedPhotos.add(photo);
                }
                ImageGridAdapter.this.notifyDataSetChanged();
            } else if (selected) {
                ImageGridAdapter.this.selectedPhotos.add(photo);
            } else {
                ImageGridAdapter.this.selectedPhotos.remove(photo);
            }
            if (ImageGridAdapter.this.selectionChangeListener != null) {
                ImageGridAdapter.this.selectionChangeListener.onSelectionChange();
            }
        }
    }

    public static class DevicePhotoTileViewHolder extends ViewHolder {
        public DevicePhotoTileViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnSelectionChangeListener {
        void onSelectionChange();
    }

    public ImageGridAdapter(Context context, List<GalleryImageInfo> selectedPhotos, int choiceMode) {
        this.items = new ArrayList();
        this.context = context.getApplicationContext();
        this.selectedPhotos = selectedPhotos;
        this.choiceMode = choiceMode;
    }

    public DevicePhotoTileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DevicePhotoTileView tileView = new DevicePhotoTileView(getContext(), viewType);
        tileView.setId(2131624287);
        tileView.setOnImageSelectionListener(new C09901());
        return new DevicePhotoTileViewHolder(tileView);
    }

    public int getItemViewType(int position) {
        if (((GalleryImageInfo) this.items.get(position)).isBroken()) {
            return 2;
        }
        return 1;
    }

    public void onBindViewHolder(DevicePhotoTileViewHolder holder, int position) {
        DevicePhotoTileView tileView = holder.itemView;
        GalleryImageInfo photo = (GalleryImageInfo) this.items.get(position);
        if (photo.isBroken()) {
            tileView.setErrorImageResId(photo, 2130838172);
            tileView.setPhotoSelected(false);
            this.selectedPhotos.remove(photo);
            return;
        }
        tileView.setPhoto(photo);
        tileView.setPhotoSelected(this.selectedPhotos.contains(photo));
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getItemCount() {
        return this.items.size();
    }

    public void clear() {
        this.items.clear();
    }

    public void addAll(Collection<GalleryImageInfo> photos) {
        this.items.addAll(photos);
    }

    protected Context getContext() {
        return this.context;
    }

    public void setOnSelectionChangeListener(OnSelectionChangeListener selectionChangeListener) {
        this.selectionChangeListener = selectionChangeListener;
    }
}
