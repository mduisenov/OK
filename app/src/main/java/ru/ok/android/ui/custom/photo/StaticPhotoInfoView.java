package ru.ok.android.ui.custom.photo;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView.ScaleType;
import com.facebook.common.references.CloseableReference;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.drawee.view.MultiDraweeHolder;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.ui.custom.photo.TagsImageView.OnTagClickedListener;
import ru.ok.android.ui.custom.photo.tags.UserPhotoTag;
import ru.ok.android.ui.custom.photo.tags.UserPhotoTag.OnUserNameClickListener;
import ru.ok.android.ui.image.view.PhotoInfoProvider.OnPhotoTagsReceivedListener;
import ru.ok.android.utils.URLUtil;
import ru.ok.android.utils.animation.SimpleAnimatorListener;
import ru.ok.model.UserInfo;
import ru.ok.model.photo.PhotoTag;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

public class StaticPhotoInfoView extends AbstractPhotoInfoView implements StaticPhoto, OnPhotoTagsReceivedListener {
    private DraweeHolder holder;
    private TagsImageView mImageView;
    private MultiDraweeHolder multiTagsHolder;
    private int placeholderHeight;
    private int placeholderWidth;
    private Uri previewUri;
    private boolean readyForAnimation;
    private Uri uri;

    /* renamed from: ru.ok.android.ui.custom.photo.StaticPhotoInfoView.1 */
    class C07311 implements OnPhotoTapListener {
        C07311() {
        }

        public void onPhotoTap(View view, float x, float y) {
            StaticPhotoInfoView.this.onViewTap();
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.StaticPhotoInfoView.2 */
    class C07322 implements OnMatrixChangedListener {
        C07322() {
        }

        public void onMatrixChanged(RectF rect) {
            if (StaticPhotoInfoView.this.mDecorViewsHandler != null) {
                StaticPhotoInfoView.this.mDecorViewsHandler.setDecorVisibility(((double) StaticPhotoInfoView.this.mImageView.getScale()) <= 1.2d, true);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.StaticPhotoInfoView.3 */
    class C07333 implements OnTagClickedListener {
        C07333() {
        }

        public void onTagClicked(UserPhotoTag tag, boolean usernameClicked) {
            if (!usernameClicked) {
                StaticPhotoInfoView.this.selectTag(tag);
            } else if (StaticPhotoInfoView.this.mOnPhotoActionListener != null) {
                StaticPhotoInfoView.this.mOnPhotoActionListener.onUserClicked(tag.getUserInfo());
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.StaticPhotoInfoView.4 */
    class C07344 implements OnUserNameClickListener {
        C07344() {
        }

        public void onUserNameClicked(UserInfo userInfo) {
            if (StaticPhotoInfoView.this.mOnPhotoActionListener != null) {
                StaticPhotoInfoView.this.mOnPhotoActionListener.onUserClicked(userInfo);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.StaticPhotoInfoView.5 */
    class C07355 extends SimpleAnimatorListener {
        C07355() {
        }

        public void onAnimationEnd(Animator animation) {
            StaticPhotoInfoView.this.mImageView.setShowTags(false);
        }
    }

    public StaticPhotoInfoView(Context context) {
        super(context);
        this.placeholderWidth = -1;
        this.placeholderHeight = -1;
    }

    public boolean isReadyForAnimation() {
        return this.readyForAnimation && this.mImageView.isValid();
    }

    protected void onCreate() {
        super.onCreate();
        this.mImageView = (TagsImageView) findViewById(C0263R.id.image);
        this.mImageView.setScaleType(ScaleType.FIT_CENTER);
        this.mImageView.setOnPhotoTapListener(new C07311());
        this.mImageView.setOnMatrixChangeListener(new C07322());
        this.mImageView.setOnTagClickedListener(new C07333());
    }

    public void initHierarchy(@Nullable CloseableReference<CloseableImage> placeholderRef) {
        super.initHierarchy(placeholderRef);
        Bitmap placeholderBitmap = null;
        if (placeholderRef != null && placeholderRef.isValid()) {
            placeholderBitmap = ((CloseableBitmap) placeholderRef.get()).getUnderlyingBitmap();
            this.placeholderWidth = placeholderBitmap.getWidth();
            this.placeholderHeight = placeholderBitmap.getHeight();
        }
        this.holder = DraweeHolder.create(FrescoOdkl.createProgressListenerHierarchy(getContext(), this, placeholderBitmap), getContext());
    }

    public void setProgressVisible(boolean visible) {
        this.mProgressView.setVisibility(visible ? 0 : 8);
    }

    public void setReadyForAnimation(boolean isReady) {
        this.readyForAnimation = isReady;
    }

    public boolean hasPlaceholder() {
        return (this.placeholderHeight == -1 || this.placeholderWidth == -1) ? false : true;
    }

    protected int getPhotoViewId() {
        return 2130903383;
    }

    protected void doUpdateScrollAlpha(int newAlpha) {
        this.mImageView.setTagsSubstractAlpha(Math.max((255 - newAlpha) * 2, 0));
    }

    public boolean onTagsReceived(String photoId, ArrayList<UserInfo> users, ArrayList<PhotoTag> tags) {
        return TextUtils.equals(this.photoInfo.getId(), photoId) && setTags(users, tags);
    }

    public void onTagsFailed(String photoId) {
    }

    protected void onViewTap() {
        if (!deselectTags()) {
            super.onViewTap();
        }
    }

    public final boolean setTags(ArrayList<UserInfo> users, ArrayList<PhotoTag> tags) {
        this.mImageView.removeTags();
        if (tags == null || tags.isEmpty() || this.mImageView.getDrawable() == null) {
            return false;
        }
        int photoHeight = this.mImageView.getDrawable().getIntrinsicHeight();
        int photoWidth = this.mImageView.getDrawable().getIntrinsicWidth();
        if (photoHeight == -1 || photoWidth == -1) {
            return false;
        }
        if (this.multiTagsHolder == null) {
            this.multiTagsHolder = new MultiDraweeHolder();
        } else {
            this.multiTagsHolder.clear();
        }
        int measureWidth = this.photoInfo.getStandartWidth();
        int measureHeight = this.photoInfo.getStandartHeight();
        if (measureWidth == 0 || measureHeight == 0) {
            float maxScale = Math.max(((float) photoWidth) / 640.0f, ((float) photoHeight) / 480.0f);
            measureWidth = (int) (((float) photoWidth) / maxScale);
            measureHeight = (int) (((float) photoHeight) / maxScale);
        }
        float tagsScale = Math.max(((float) measureWidth) > 640.0f ? ((float) measureWidth) / 640.0f : 1.0f, ((float) measureHeight) > 480.0f ? ((float) measureHeight) / 480.0f : 1.0f);
        float bitmapScale = ((float) photoWidth) / ((float) measureWidth);
        int patchWidth = getResources().getDimensionPixelSize(2131231120);
        int tagHeight = getResources().getDimensionPixelSize(2131231121);
        ArrayList<UserInfo> arrayList = new ArrayList(users);
        Iterator i$ = tags.iterator();
        while (i$.hasNext()) {
            PhotoTag tag = (PhotoTag) i$.next();
            int x = (int) ((((float) tag.getX()) * tagsScale) * bitmapScale);
            int y = (int) ((((float) tag.getY()) * tagsScale) * bitmapScale);
            int textDirection = 0;
            int arrowDirection = 0;
            int rightSpace = patchWidth - x;
            if (rightSpace < patchWidth && x > rightSpace) {
                textDirection = 1;
            }
            if (y < tagHeight) {
                arrowDirection = 1;
            }
            UserInfo userInfo = null;
            if (!TextUtils.isEmpty(tag.getUserId())) {
                userInfo = popUserInfo(arrayList, tag.getUserId());
            }
            UserPhotoTag photoTag = null;
            if (userInfo != null) {
                UserPhotoTag userPhotoTag = new UserPhotoTag(getContext(), userInfo, x, y, true, textDirection, arrowDirection);
                if (!URLUtil.isStubUrl(userInfo.picUrl)) {
                    DraweeHolder tagHolder = FrescoOdkl.createCircleDrawee(getContext());
                    tagHolder.setController(Fresco.newDraweeControllerBuilder().setUri(Uri.parse(userInfo.picUrl)).build());
                    this.multiTagsHolder.add(tagHolder);
                    userPhotoTag.setUserPhotoHolder(tagHolder);
                    userPhotoTag.setOnUserNameClickListener(new C07344());
                }
            } else if (!TextUtils.isEmpty(tag.getText())) {
                photoTag = new UserPhotoTag(getContext(), tag.getText(), x, y, textDirection, arrowDirection);
            }
            if (photoTag != null) {
                this.mImageView.addTag(photoTag);
            }
        }
        this.mImageView.postInvalidate();
        showTags(true);
        return true;
    }

    public final void showTags(boolean animate) {
        this.mImageView.setShowTags(true);
        if (animate && this.mImageView.getTags() != null) {
            int size = this.mImageView.getTags().size();
            for (int i = 0; i < size; i++) {
                ((UserPhotoTag) this.mImageView.getTags().get(i)).startPopOutAnimation((long) (i * 40), null);
            }
        }
    }

    public final void hideTags(boolean animate) {
        if (!animate || this.mImageView.getTags() == null) {
            this.mImageView.setShowTags(false);
            return;
        }
        this.mImageView.setShowTags(true);
        int size = this.mImageView.getTags().size();
        for (int i = 0; i < size; i++) {
            UserPhotoTag tag = (UserPhotoTag) this.mImageView.getTags().get(i);
            AnimatorListener listener = null;
            if (i == size - 1) {
                listener = new C07355();
            }
            tag.setAlpha(MotionEventCompat.ACTION_MASK);
            tag.startCaveInAnimation((long) (i * 40), listener);
        }
    }

    public final boolean areTagsShown() {
        return this.mImageView.areTagsShown();
    }

    public Drawable getDrawable() {
        return this.mImageView.getDrawable();
    }

    protected final boolean selectTag(UserPhotoTag tag) {
        boolean changed = false;
        UserPhotoTag photoTag = tag;
        if (photoTag != null) {
            photoTag.toggleUserNamePatch();
            photoTag.setAlpha(MotionEventCompat.ACTION_MASK);
            changed = true;
        }
        for (UserPhotoTag other : this.mImageView.getTags()) {
            if (tag != other) {
                if (photoTag == null || !photoTag.isNamePatchShowing()) {
                    other.setAlpha(MotionEventCompat.ACTION_MASK);
                } else {
                    other.setAlpha(190);
                }
                if (other.isNamePatchShowing()) {
                    other.hideUserNamePatch();
                    changed = true;
                }
            }
        }
        return changed;
    }

    private boolean deselectTags() {
        return this.mImageView.getTags() != null && selectTag(null);
    }

    public void removeTagForUser(UserInfo userInfo) {
        this.mImageView.removeTagForUser(userInfo);
    }

    private UserInfo popUserInfo(List<UserInfo> users, String id) {
        for (UserInfo user : users) {
            if (TextUtils.equals(id, user.uid)) {
                users.remove(user);
                return user;
            }
        }
        return null;
    }

    protected boolean isThrowBlocked(MotionEvent event) {
        return super.isThrowBlocked(event) || !isPhotoAtRestHit(event);
    }

    private boolean isPhotoAtRestHit(MotionEvent event) {
        RectF displayRect = this.mImageView.getDisplayRect();
        if (displayRect == null) {
            return false;
        }
        boolean scaling = displayRect.top < 0.0f || ((int) displayRect.bottom) > this.mImageView.getHeight();
        if (scaling) {
            return false;
        }
        return displayRect.contains(event.getX(), event.getY());
    }

    public final int getImageDisplayedX() {
        RectF displayRect = this.mImageView.getDisplayRect();
        if (displayRect == null) {
            return 0;
        }
        return (int) displayRect.left;
    }

    public final int getImageDisplayedY() {
        RectF displayRect = this.mImageView.getDisplayRect();
        if (displayRect == null) {
            return 0;
        }
        return (int) displayRect.top;
    }

    public final int getImageDisplayedWidth() {
        RectF displayRect = this.mImageView.getDisplayRect();
        if (displayRect == null) {
            return 0;
        }
        return (int) displayRect.width();
    }

    public final int getImageDisplayedHeight() {
        RectF displayRect = this.mImageView.getDisplayRect();
        if (displayRect == null) {
            return 0;
        }
        return (int) displayRect.height();
    }

    public float getImageScale() {
        return this.mImageView.getScale();
    }

    public RectF getImageDisplayRect() {
        return this.mImageView.getDisplayRect();
    }

    public void setImageDrawable(Drawable drawable) {
        this.mImageView.createPhotoAttacher();
        this.mImageView.setImageDrawable(drawable);
    }

    public void setPhotoUri(Uri uri, Uri previewUri) {
        if (this.uri == null || !this.uri.equals(uri)) {
            this.uri = uri;
            this.previewUri = previewUri;
            setImageConstantSize(this.holder, this, uri, previewUri);
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.holder != null) {
            this.holder.onDetach();
        }
        if (this.multiTagsHolder != null) {
            this.multiTagsHolder.onDetach();
        }
        this.mImageView.cleanup();
    }

    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        if (this.holder != null) {
            this.holder.onDetach();
        }
        if (this.multiTagsHolder != null) {
            this.multiTagsHolder.onDetach();
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.holder != null) {
            this.holder.onAttach();
        }
        if (this.multiTagsHolder != null) {
            this.multiTagsHolder.onAttach();
        }
    }

    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        if (this.holder != null) {
            this.holder.onAttach();
        }
        if (this.multiTagsHolder != null) {
            this.multiTagsHolder.onAttach();
        }
    }

    public Uri getUri() {
        return this.uri;
    }

    public Uri getPreviewUri() {
        return this.previewUri;
    }

    public int getPlaceholderWidth() {
        return this.placeholderWidth;
    }

    public int getPlaceholderHeight() {
        return this.placeholderHeight;
    }
}
