package ru.ok.android.ui.stream.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.C0206R;
import ru.ok.android.fresco.FrescoGifMarkerView;
import ru.ok.android.ui.image.pick.GalleryImageInfo;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.MimeTypes;
import ru.ok.android.utils.ViewUtil;

public class PhotoRollView extends LinearLayout {
    private TextView bottomText;
    private PhotoRollViewCallbacks callbacks;
    private ImageView closeButton;
    private int currentStyleResId;
    private final int paddingInner;
    private final int paddingTotal;
    private int photoItemSize;
    private MyAdapter photosAdapter;
    private RecyclerView photosView;
    private final int spacing;
    private TextView upperText;

    public interface PhotoRollViewCallbacks {
        void onCloseClick();

        void onPhotoClicked(@NonNull GalleryImageInfo galleryImageInfo);
    }

    /* renamed from: ru.ok.android.ui.stream.view.PhotoRollView.1 */
    class C12621 implements OnClickListener {
        C12621() {
        }

        public void onClick(View v) {
            PhotoRollView.this.close();
        }
    }

    /* renamed from: ru.ok.android.ui.stream.view.PhotoRollView.2 */
    class C12632 extends AnimatorListenerAdapter {
        C12632() {
        }

        public void onAnimationEnd(Animator animation) {
            PhotoRollView.this.setVisibility(8);
        }
    }

    private class MyAdapter extends Adapter<MyViewHolder> implements OnClickListener {
        private final LayoutInflater layoutInflater;
        private final ResizeOptions photoResizeOptions;
        private final List<GalleryImageInfo> photos;

        public MyAdapter(Context context) {
            this.layoutInflater = LayoutInflater.from(context);
            this.photos = new ArrayList();
            this.photoResizeOptions = new ResizeOptions(PhotoRollView.this.photoItemSize, PhotoRollView.this.photoItemSize);
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = this.layoutInflater.inflate(2130903381, parent, false);
            LayoutParams lp = itemView.getLayoutParams();
            lp.width = PhotoRollView.this.photoItemSize;
            lp.height = PhotoRollView.this.photoItemSize;
            itemView.setOnClickListener(this);
            return new MyViewHolder(itemView);
        }

        public void onBindViewHolder(MyViewHolder holder, int position) {
            GalleryImageInfo photo = (GalleryImageInfo) this.photos.get(position);
            FrescoGifMarkerView photoItemView = holder.itemView;
            photoItemView.setShouldDrawGifMarker(MimeTypes.isGif(photo.mimeType));
            photoItemView.setUri(photo.uri);
            photoItemView.setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setOldController(photoItemView.getController())).setImageRequest(ImageRequestBuilder.newBuilderWithSource(photo.uri).setResizeOptions(this.photoResizeOptions).build())).build());
            photoItemView.setTag(photo);
        }

        public int getItemCount() {
            return this.photos.size();
        }

        public void setPhotos(@NonNull List<GalleryImageInfo> photos) {
            this.photos.clear();
            this.photos.addAll(photos);
            notifyDataSetChanged();
        }

        public void removeAllPhotos() {
            this.photos.clear();
        }

        public void onClick(View v) {
            if (PhotoRollView.this.callbacks != null) {
                PhotoRollView.this.callbacks.onPhotoClicked((GalleryImageInfo) v.getTag());
            }
        }
    }

    private static class MyViewHolder extends ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class SpaceItemDecoration extends ItemDecoration {
        private final int spacing;

        public SpaceItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView recyclerView, State state) {
            int i = 0;
            int access$300 = isFirstItem(recyclerView, view) ? getTotalItemCount(recyclerView) <= 4 ? 0 : PhotoRollView.this.paddingInner : 0;
            outRect.left = access$300;
            if (!isLastItem(recyclerView, view)) {
                i = this.spacing;
            }
            outRect.right = i;
        }

        private boolean isFirstItem(RecyclerView recyclerView, View view) {
            return recyclerView.getChildAdapterPosition(view) == 0;
        }

        private boolean isLastItem(RecyclerView recyclerView, View view) {
            return recyclerView.getChildAdapterPosition(view) == getTotalItemCount(recyclerView) + -1;
        }

        private int getTotalItemCount(RecyclerView parent) {
            return parent.getAdapter().getItemCount();
        }
    }

    public PhotoRollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.spacing = getResources().getDimensionPixelSize(2131230991);
        this.paddingInner = getResources().getDimensionPixelSize(2131230965);
        this.paddingTotal = getResources().getDimensionPixelSize(2131230966);
        calculatePhotoItemSize();
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
    }

    private void initViews() {
        initUpperText();
        initBottomText();
        initCloseButton();
        initPhotosView();
    }

    private void calculatePhotoItemSize() {
        this.photoItemSize = ((this.spacing + (DeviceUtils.getStreamHighQualityPhotoWidth() - (this.paddingTotal * 2))) / 4) - this.spacing;
    }

    public void setStyle(int styleResId) {
        if (this.currentStyleResId != styleResId) {
            this.currentStyleResId = styleResId;
            TypedArray a = getContext().obtainStyledAttributes(styleResId, C0206R.styleable.PhotoRollView);
            Drawable background = a.getDrawable(0);
            int upperTextColor = a.getColor(1, 0);
            int bottomTextColor = a.getColor(2, 0);
            Drawable closeDrawable = a.getDrawable(4);
            Drawable lockDrawable = a.getDrawable(3);
            a.recycle();
            if (background != null) {
                setBackgroundWithKeepingPaddings(background);
            }
            if (upperTextColor != 0) {
                this.upperText.setTextColor(upperTextColor);
            }
            if (bottomTextColor != 0) {
                this.bottomText.setTextColor(bottomTextColor);
            }
            if (closeDrawable != null) {
                this.closeButton.setImageDrawable(closeDrawable);
            }
            if (lockDrawable != null) {
                this.bottomText.setCompoundDrawablesWithIntrinsicBounds(lockDrawable, null, null, null);
            }
        }
    }

    public int getCurrentStyle() {
        return this.currentStyleResId;
    }

    private void setBackgroundWithKeepingPaddings(@NonNull Drawable background) {
        int pLeft = getPaddingLeft();
        int pRight = getPaddingRight();
        int pTop = getPaddingTop();
        int pBottom = getPaddingBottom();
        ViewUtil.setBackgroundCompat(this, background);
        setPadding(pLeft, pTop, pRight, pBottom);
    }

    private void initUpperText() {
        this.upperText = (TextView) findViewById(2131625332);
    }

    private void initBottomText() {
        this.bottomText = (TextView) findViewById(2131625335);
    }

    private void initCloseButton() {
        this.closeButton = (ImageView) findViewById(2131625333);
        this.closeButton.setOnClickListener(new C12621());
    }

    private void initPhotosView() {
        this.photosView = (RecyclerView) findViewById(2131625334);
        this.photosView.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
        this.photosView.addItemDecoration(new SpaceItemDecoration(this.spacing));
        this.photosAdapter = new MyAdapter(getContext());
        this.photosView.setAdapter(this.photosAdapter);
        setPhotosViewHeight();
    }

    private void setPhotosViewHeight() {
        this.photosView.getLayoutParams().height = this.photoItemSize;
    }

    public void setCallbacks(@Nullable PhotoRollViewCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void setPhotos(@NonNull List<GalleryImageInfo> photos) {
        this.photosAdapter.setPhotos(photos);
        updatePhotosViewPaddingsIfNecessary(photos.size());
    }

    private void updatePhotosViewPaddingsIfNecessary(int newPhotoCount) {
        if (newPhotoCount >= 4) {
            if (newPhotoCount == 4) {
                this.photosView.setPadding(this.paddingInner, 0, this.paddingInner, 0);
            } else {
                this.photosView.setPadding(0, 0, 0, 0);
            }
        }
    }

    private void close() {
        this.photosAdapter.removeAllPhotos();
        if (this.callbacks != null) {
            this.callbacks.onCloseClick();
        }
        closeWithAnimation();
    }

    private void closeWithAnimation() {
        ValueAnimator animator = ViewUtil.createHeightAnimator(this, getHeight(), 0, 200);
        animator.addListener(new C12632());
        animator.start();
    }
}
