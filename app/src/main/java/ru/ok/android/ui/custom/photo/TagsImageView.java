package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import ru.ok.android.ui.custom.photo.tags.UserPhotoTag;
import ru.ok.model.UserInfo;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

public class TagsImageView extends PinchZoomImageView {
    protected OnPhotoTapListener onPhotoTapListener;
    protected OnTagClickedListener onTagClickedListener;
    protected boolean showTags;
    protected HashMap<UserPhotoTag, Rect> tagAreas;
    protected final Callback tagInvalidateCallback;
    protected ArrayList<UserPhotoTag> tags;
    private int tagsAlpha;
    private ArrayList<UserPhotoTag> tagsCollector;
    private OnPhotoTapListener tagsPhotoTapListener;

    public interface OnTagClickedListener {
        void onTagClicked(UserPhotoTag userPhotoTag, boolean z);
    }

    /* renamed from: ru.ok.android.ui.custom.photo.TagsImageView.1 */
    class C07361 implements Callback {
        C07361() {
        }

        public void unscheduleDrawable(Drawable who, Runnable what) {
        }

        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        public void invalidateDrawable(Drawable who) {
            TagsImageView.this.postInvalidate();
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.TagsImageView.2 */
    class C07372 implements OnPhotoTapListener {
        private final Rect tagAreaRect;

        C07372() {
            this.tagAreaRect = new Rect();
        }

        public void onPhotoTap(View view, float pX, float pY) {
            boolean consumed = false;
            if (TagsImageView.this.tagAreas != null && TagsImageView.this.showTags) {
                Iterator i$;
                UserPhotoTag tag;
                boolean clickHandled = false;
                TagsImageView.this.tagsCollector.clear();
                RectF rect = TagsImageView.this.getDisplayRect();
                int rectX = (int) ((rect.width() * pX) + rect.left);
                int rectY = (int) ((rect.height() * pY) + rect.top);
                for (Entry<UserPhotoTag, Rect> entry : TagsImageView.this.tagAreas.entrySet()) {
                    Rect area = (Rect) entry.getValue();
                    tag = (UserPhotoTag) entry.getKey();
                    tag.calculateBounds(this.tagAreaRect, area.left, area.top);
                    if (this.tagAreaRect.contains(rectX, rectY)) {
                        consumed = true;
                        clickHandled = tag.handleClickEvent(this.tagAreaRect, rectX, rectY);
                        if (clickHandled) {
                            break;
                        }
                        TagsImageView.this.tagsCollector.add(entry.getKey());
                    }
                }
                if (!(TagsImageView.this.tagsCollector.isEmpty() || clickHandled)) {
                    UserPhotoTag toSelect = null;
                    boolean searchNextClosed = true;
                    i$ = TagsImageView.this.tagsCollector.iterator();
                    while (i$.hasNext()) {
                        tag = (UserPhotoTag) i$.next();
                        if (tag.isSelected()) {
                            searchNextClosed = true;
                        } else if (searchNextClosed) {
                            toSelect = tag;
                        }
                    }
                    if (toSelect == null) {
                        toSelect = (UserPhotoTag) TagsImageView.this.tagsCollector.get(0);
                    }
                    TagsImageView.this.tags.remove(toSelect);
                    TagsImageView.this.tags.add(toSelect);
                    if (TagsImageView.this.onTagClickedListener != null) {
                        TagsImageView.this.onTagClickedListener.onTagClicked(toSelect, false);
                        consumed = true;
                    }
                }
            }
            if (!consumed && TagsImageView.this.onPhotoTapListener != null) {
                TagsImageView.this.onPhotoTapListener.onPhotoTap(view, pX, pY);
            }
        }
    }

    public TagsImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.showTags = false;
        this.tagInvalidateCallback = new C07361();
        this.tagsCollector = new ArrayList();
        onCreate();
    }

    public TagsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.showTags = false;
        this.tagInvalidateCallback = new C07361();
        this.tagsCollector = new ArrayList();
        onCreate();
    }

    public TagsImageView(Context context) {
        super(context);
        this.showTags = false;
        this.tagInvalidateCallback = new C07361();
        this.tagsCollector = new ArrayList();
        onCreate();
    }

    public void createPhotoAttacher() {
        super.createPhotoAttacher();
        super.setOnPhotoTapListener(this.tagsPhotoTapListener);
    }

    private final void onCreate() {
        this.tagsPhotoTapListener = new C07372();
        super.setOnPhotoTapListener(this.tagsPhotoTapListener);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.showTags && this.tags != null && !this.tags.isEmpty()) {
            RectF displayRect = getDisplayRect();
            float[] values = new float[9];
            getImageMatrix().getValues(values);
            float scale = values[0];
            Iterator i$ = this.tags.iterator();
            while (i$.hasNext()) {
                UserPhotoTag tag = (UserPhotoTag) i$.next();
                tag.setSubstractAlpha(this.tagsAlpha);
                int x = (int) ((((float) tag.getX()) * scale) + displayRect.left);
                int y = (int) ((((float) tag.getY()) * scale) + displayRect.top);
                Drawable drawable = tag.getDrawable();
                Rect tagRect = (Rect) this.tagAreas.get(tag);
                tagRect.left = x;
                tagRect.top = y;
                tagRect.right = drawable.getIntrinsicWidth() + x;
                tagRect.bottom = drawable.getIntrinsicHeight() + y;
                drawable.setBounds(tagRect);
                drawable.draw(canvas);
            }
        }
    }

    public final void addTag(UserPhotoTag tag) {
        populateContainers();
        addTagInternal(tag);
    }

    public final void removeTagForUser(UserInfo userInfo) {
        if (this.tags != null && userInfo != null) {
            Iterator i$ = this.tags.iterator();
            while (i$.hasNext()) {
                UserPhotoTag tag = (UserPhotoTag) i$.next();
                if (tag.getUserInfo() != null && userInfo.uid.equals(tag.getUserInfo().uid)) {
                    this.tags.remove(tag);
                    invalidate();
                    return;
                }
            }
        }
    }

    private final void populateContainers() {
        if (this.tags == null) {
            this.tags = new ArrayList();
        }
        if (this.tagAreas == null) {
            this.tagAreas = new HashMap();
        }
    }

    private final void addTagInternal(UserPhotoTag tag) {
        tag.getDrawable().setCallback(this.tagInvalidateCallback);
        this.tags.add(tag);
        this.tagAreas.put(tag, new Rect());
    }

    public final void removeTags() {
        if (this.tags != null) {
            this.tags.clear();
        }
        if (this.tagAreas != null) {
            this.tagAreas.clear();
        }
    }

    public List<UserPhotoTag> getTags() {
        return this.tags;
    }

    public void setShowTags(boolean showTags) {
        this.showTags = showTags;
        postInvalidate();
    }

    public final boolean areTagsShown() {
        return this.showTags;
    }

    public final void setTagsSubstractAlpha(int alpha) {
        this.tagsAlpha = alpha;
        invalidate();
    }

    public void setOnTagClickedListener(OnTagClickedListener onTagClickedListener) {
        this.onTagClickedListener = onTagClickedListener;
    }

    public void setOnPhotoTapListener(OnPhotoTapListener onPhotoTapListener) {
        this.onPhotoTapListener = onPhotoTapListener;
    }
}
