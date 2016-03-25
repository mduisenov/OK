package ru.ok.android.ui.custom;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import ru.ok.android.app.SpritesHelper;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.ui.presents.helpers.PresentSettingsHelper;
import ru.ok.android.utils.PhotoUtil;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.presents.IPresentType;
import ru.ok.sprites.SpriteDrawable.AnimationListener;
import ru.ok.sprites.SpriteView;

public class CompositePresentView extends FrameLayout {
    private int achivePlaceholder;
    private SpriteView animatedPresentView;
    private boolean animationEnabled;
    private AnimationListener animationListener;
    private int cardPlaceholder;
    private int presentPlaceholder;
    private UrlImageView staticPresentView;

    public CompositePresentView(Context context) {
        super(context);
        this.animationEnabled = true;
        this.presentPlaceholder = 2130838050;
        this.cardPlaceholder = 2130838496;
        this.achivePlaceholder = 2130838049;
    }

    public CompositePresentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.animationEnabled = true;
        this.presentPlaceholder = 2130838050;
        this.cardPlaceholder = 2130838496;
        this.achivePlaceholder = 2130838049;
    }

    public void setPresentPlaceholder(@DrawableRes int presentPlaceholder) {
        this.presentPlaceholder = presentPlaceholder;
    }

    public void setCardPlaceholder(@DrawableRes int cardPlaceholder) {
        this.cardPlaceholder = cardPlaceholder;
    }

    public void setAchivePlaceholder(@DrawableRes int achivePlaceholder) {
        this.achivePlaceholder = achivePlaceholder;
    }

    public void setAnimationEnabled(boolean animationEnabled) {
        this.animationEnabled = animationEnabled;
        if (this.animatedPresentView != null) {
            this.animatedPresentView.getHierarchy().setAnimationEnabled(animationEnabled);
        }
    }

    public void setAnimationListener(@Nullable AnimationListener animationListener) {
        this.animationListener = animationListener;
        if (this.animatedPresentView != null) {
            this.animatedPresentView.getHierarchy().setAnimationListener(animationListener);
        }
    }

    public void setPresentType(@NonNull IPresentType presentType, @NonNull Point size) {
        setPresentType(presentType, false, size);
    }

    public void setAchive(@NonNull IPresentType presentType, @NonNull Point size) {
        setPresentType(presentType, true, size);
    }

    private void setPresentType(@NonNull IPresentType presentType, boolean isAchive, @NonNull Point size) {
        boolean isAnimated = (!presentType.isAnimated() || presentType.getSprites() == null || presentType.getSprites().isEmpty() || presentType.getAnimationProperties() == null || !PresentSettingsHelper.isAnimatedPresentsEnabled()) ? false : true;
        if (!isAnimated && presentType.getStaticImage() != null) {
            if (this.animatedPresentView != null) {
                removeAllViews();
                this.animatedPresentView = null;
            }
            if (this.staticPresentView == null) {
                View urlImageView = new UrlImageView(getContext());
                this.staticPresentView = urlImageView;
                addView(urlImageView);
                this.staticPresentView.setScaleType(ScaleType.CENTER_CROP);
            }
            if (presentType.isLive()) {
                ((GenericDraweeHierarchy) this.staticPresentView.getHierarchy()).setPlaceholderImage(getResources().getDrawable(this.cardPlaceholder), ScalingUtils.ScaleType.CENTER_CROP);
            } else if (isAchive) {
                ((GenericDraweeHierarchy) this.staticPresentView.getHierarchy()).setPlaceholderImage(getResources().getDrawable(this.achivePlaceholder), ScalingUtils.ScaleType.CENTER_CROP);
            } else {
                ((GenericDraweeHierarchy) this.staticPresentView.getHierarchy()).setPlaceholderImage(getResources().getDrawable(this.presentPlaceholder), ScalingUtils.ScaleType.CENTER_CROP);
            }
            this.staticPresentView.setUrl(presentType.getStaticImage());
        } else if (isAnimated) {
            if (this.staticPresentView != null) {
                removeAllViews();
                this.staticPresentView = null;
            }
            if (this.animatedPresentView == null) {
                SpriteView spriteView = new SpriteView(getContext());
                this.animatedPresentView = spriteView;
                addView(spriteView);
                this.animatedPresentView.getHierarchy().setPlaceholder(this.presentPlaceholder);
            }
            PhotoSize photoSize = PhotoUtil.getClosestSize(size.x, size.y, presentType.getSprites());
            if (photoSize != null) {
                this.animatedPresentView.getHierarchy().setAnimationEnabled(this.animationEnabled);
                this.animatedPresentView.getHierarchy().setAnimationListener(this.animationListener);
                this.animatedPresentView.setSpriteUrl(photoSize.getUrl(), SpritesHelper.createSpriteMetadata(presentType.getAnimationProperties(), photoSize.getWidth()));
            }
        }
    }
}
