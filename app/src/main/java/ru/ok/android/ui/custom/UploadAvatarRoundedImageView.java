package ru.ok.android.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import ru.ok.android.C0206R;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo.UserGenderType;

public class UploadAvatarRoundedImageView extends FrameLayout {
    private View avatarProgress;
    private TextView avatarTitle;
    private Drawable femalePlaceHolder;
    private UserGenderType genderType;
    private SimpleDraweeView imageView;
    private Drawable malePlaceHolder;

    /* renamed from: ru.ok.android.ui.custom.UploadAvatarRoundedImageView.1 */
    class C06281 extends BaseControllerListener<ImageInfo> {
        C06281() {
        }

        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            UploadAvatarRoundedImageView.this.avatarTitle.setVisibility(8);
        }
    }

    public UploadAvatarRoundedImageView(Context context) {
        this(context, null);
    }

    public UploadAvatarRoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UploadAvatarRoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LocalizationManager.inflate(context, 2130903549, (ViewGroup) this, true);
        this.avatarTitle = (TextView) findViewById(2131625410);
        this.imageView = (SimpleDraweeView) findViewById(2131624969);
        this.avatarProgress = findViewById(2131625411);
        TypedArray a = getContext().obtainStyledAttributes(attrs, C0206R.styleable.UploadAvatarRoundedImageView, defStyleAttr, 0);
        initPlaceHolders(context, a);
        int progressRadius = a.getDimensionPixelSize(3, -1);
        LayoutParams layoutParams = this.avatarProgress.getLayoutParams();
        layoutParams.height = progressRadius;
        layoutParams.width = progressRadius;
        this.avatarProgress.setLayoutParams(layoutParams);
        if (a.getBoolean(2, false)) {
            this.avatarTitle.setVisibility(0);
        }
        ((GenericDraweeHierarchy) this.imageView.getHierarchy()).setActualImageScaleType(ScaleType.CENTER_CROP);
        a.recycle();
        setGender(UserGenderType.MALE);
    }

    private void initPlaceHolders(Context context, TypedArray a) {
        int maleRes = a.getResourceId(0, 0);
        if (maleRes != 0) {
            this.malePlaceHolder = context.getResources().getDrawable(maleRes);
        }
        int femaleRes = a.getResourceId(1, 0);
        if (femaleRes != 0) {
            this.femalePlaceHolder = getResources().getDrawable(femaleRes);
        }
    }

    public void setGender(UserGenderType genderType) {
        this.genderType = genderType;
        ((GenericDraweeHierarchy) this.imageView.getHierarchy()).setPlaceholderImage(genderType == UserGenderType.FEMALE ? this.femalePlaceHolder : this.malePlaceHolder, ScaleType.CENTER_INSIDE);
    }

    public void setAvatar(String picUrl) {
        try {
            this.imageView.setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setRetainImageOnFailure(true)).setUri(Uri.parse(picUrl)).setAutoPlayAnimations(true)).setControllerListener(new C06281())).build());
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }

    public void showAvatarProgress() {
        this.imageView.setAlpha(0.4f);
        this.avatarProgress.setVisibility(0);
        setClickable(false);
    }

    public void hideAvatarProgress() {
        this.imageView.setAlpha(1.0f);
        this.avatarProgress.setVisibility(4);
        setClickable(true);
    }

    public void setPlaceholderVisibility(boolean visible) {
        if (visible) {
            setGender(this.genderType);
        } else {
            ((GenericDraweeHierarchy) this.imageView.getHierarchy()).setPlaceholderImage(null);
        }
    }

    public void clearAvatar() {
        this.imageView.setImageURI(null);
    }
}
