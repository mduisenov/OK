package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import ru.ok.android.C0206R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.fresco.postprocessors.ImageRoundShadowPostprocessor;
import ru.ok.android.utils.Utils;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserOnlineType;

public class AvatarImageView extends FrameLayout implements OnClickListener {
    private OnClickToUserImageListener clickListener;
    private UrlImageView image;
    private OnSetImageUriListener onSetImageUriListener;
    private ImageView onlineView;
    private Stroke stroke;
    private ImageType type;
    public UserInfo user;

    public interface OnClickToUserImageListener {
        void onClickToUserImage(UserInfo userInfo, View view);
    }

    public enum ImageType {
        IMAGE,
        MALE,
        FEMALE
    }

    public interface OnSetImageUriListener {
        void onSetImageBitmapUri(Uri uri);
    }

    public static class Stroke {
        public int color;
        public float width;

        public Stroke(float width, int color) {
            this.width = width;
            this.color = color;
        }
    }

    public AvatarImageView(Context context) {
        super(context);
        this.type = ImageType.IMAGE;
        init();
        requestDisallowInterceptTouchEvent(true);
    }

    public AvatarImageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.type = ImageType.IMAGE;
        parseAttrs(attrs, 0);
        init();
    }

    public AvatarImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.type = ImageType.IMAGE;
        parseAttrs(attrs, defStyle);
        init();
    }

    private void parseAttrs(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, C0206R.styleable.AvatarImageView, defStyle, 0);
        if (a.hasValue(0)) {
            this.stroke = new Stroke(a.getDimension(0, 0.0f), a.getColor(1, -1));
        }
        a.recycle();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(2130903108, this, true);
        this.image = (UrlImageView) findViewById(C0263R.id.image);
        this.onlineView = (ImageView) findViewById(2131624634);
        setImageResource(2130838321);
    }

    public void setUser(UserInfo user) {
        this.user = user;
        Utils.updateOnlineView(this.onlineView, Utils.onlineStatus(user));
    }

    public ImageType getType() {
        return this.type;
    }

    public void setImageUrl(Uri uri) {
        float strokeWidth = 0.0f;
        int strokeColor = 0;
        if (this.stroke != null) {
            strokeWidth = this.stroke.width;
            strokeColor = this.stroke.color;
        }
        this.image.setImageRequest(ImageRequestBuilder.newBuilderWithSource(uri).setPostprocessor(new ImageRoundShadowPostprocessor(uri, strokeWidth, false, strokeColor)).build());
        this.type = ImageType.IMAGE;
        if (this.onSetImageUriListener != null) {
            this.onSetImageUriListener.onSetImageBitmapUri(uri);
        }
    }

    public void setAvatarMaleImage() {
        setImageResource(2130838321);
        this.type = ImageType.MALE;
    }

    public void setAvatarFemaleImage() {
        setImageResource(2130837927);
        this.type = ImageType.FEMALE;
    }

    public void setImageResource(int resId) {
        this.image.setImageResource(resId);
        if (this.onSetImageUriListener != null) {
            this.onSetImageUriListener.onSetImageBitmapUri(FrescoOdkl.uriFromResId(resId));
        }
    }

    public void setOnClickToImageListener(OnClickToUserImageListener listener) {
        if (listener != null) {
            setOnClickListener(this);
            this.clickListener = listener;
        }
    }

    public void onClick(View view) {
        if (this.clickListener != null) {
            this.clickListener.onClickToUserImage(this.user, view);
        }
    }

    public UrlImageView getImage() {
        return this.image;
    }

    public void updateOnlineViewForMessages(UserOnlineType online) {
        Utils.updateOnlineViewForMessages(this.onlineView, online);
    }

    public void setOnSetImageUriListener(OnSetImageUriListener onSetImageUriListener) {
        this.onSetImageUriListener = onSetImageUriListener;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }
}
