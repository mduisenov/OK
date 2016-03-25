package ru.ok.android.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.photo.upload.ImageUploadException;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;
import ru.ok.android.ui.custom.imageview.GifMarkerDrawableHelper;
import ru.ok.android.utils.localization.LocalizationManager;

public class ImageUploadStatusView extends RelativeLayout {
    private ImageUploadException mCurrentError;
    private int mCurrentStatus;
    private ProgressBar mProgressView;
    private TextView mStatusView;
    private UploadDraweeView mThumbView;

    public static final class UploadDraweeView extends AsyncDraweeView {
        private boolean error;
        private final GifMarkerDrawableHelper gifMarkerDrawableHelper;

        public UploadDraweeView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
        }

        public UploadDraweeView(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
        }

        public UploadDraweeView(Context context) {
            super(context);
            this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
        }

        public void setUri(Uri uri) {
            super.setUri(uri);
        }

        protected void onImageLoaded(boolean animate) {
            this.error = false;
            setScaleType(ScaleType.CENTER_CROP);
            super.onImageLoaded(animate);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            this.gifMarkerDrawableHelper.drawGifMarkerIfNecessary(this, canvas);
        }

        protected void onImageFailed(boolean stub) {
            super.onImageFailed(stub);
            setScaleType(ScaleType.CENTER_INSIDE);
            this.error = true;
        }

        public void setShouldDrawGifMarker(boolean shouldDrawGifMarker) {
            this.gifMarkerDrawableHelper.setShouldDrawGifMarker(shouldDrawGifMarker);
        }
    }

    public ImageUploadStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCurrentStatus = -1;
        build(context);
    }

    public ImageUploadStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCurrentStatus = -1;
        build(context);
    }

    public ImageUploadStatusView(Context context) {
        super(context);
        this.mCurrentStatus = -1;
        build(context);
    }

    private final void build(Context context) {
        LocalizationManager.inflate(context, 2130903243, (ViewGroup) this, true);
        this.mThumbView = (UploadDraweeView) findViewById(2131624951);
        this.mThumbView.setErrorImageResId(2130838538);
        this.mStatusView = (TextView) findViewById(2131624952);
        this.mProgressView = (ProgressBar) findViewById(2131624548);
    }

    public final void setStatus(int status, ImageUploadException error) {
        if (this.mCurrentStatus != status || ((this.mCurrentError == null && error != null) || !(this.mCurrentError == null || this.mCurrentError.equals(error)))) {
            this.mStatusView.setText(null);
            this.mStatusView.setVisibility(8);
            this.mStatusView.setBackgroundColor(0);
            this.mProgressView.setVisibility(8);
            int errorCode = error == null ? 0 : error.getErrorCode();
            switch (status) {
                case RECEIVED_VALUE:
                    this.mStatusView.setBackgroundColor(getResources().getColor(2131493035));
                    this.mStatusView.setVisibility(0);
                    break;
                case Message.UUID_FIELD_NUMBER /*5*/:
                    break;
                case Message.REPLYTO_FIELD_NUMBER /*6*/:
                    this.mStatusView.setText(2131165482);
                    this.mStatusView.setTextColor(-1);
                    this.mStatusView.setBackgroundColor(getResources().getColor(2131493034));
                    this.mStatusView.setVisibility(0);
                    break;
                case Message.ATTACHES_FIELD_NUMBER /*7*/:
                    this.mStatusView.setBackgroundColor(getResources().getColor(2131493035));
                    this.mStatusView.setVisibility(0);
                    break;
                case Message.TASKID_FIELD_NUMBER /*8*/:
                    if (errorCode != 1 && errorCode != 14) {
                        this.mStatusView.setText(2131165791);
                        this.mStatusView.setTextColor(-1);
                        this.mStatusView.setBackgroundColor(getResources().getColor(2131493034));
                        this.mStatusView.setVisibility(0);
                        break;
                    }
                    this.mStatusView.setBackgroundColor(getResources().getColor(2131493035));
                    this.mStatusView.setVisibility(0);
                    break;
                    break;
                default:
                    this.mStatusView.setBackgroundColor(getResources().getColor(2131493035));
                    this.mStatusView.setVisibility(0);
                    this.mProgressView.setVisibility(0);
                    break;
            }
            if (errorCode == 4 && error.getServerErrorCode() == 454) {
                this.mStatusView.setBackgroundColor(getResources().getColor(2131492947));
                this.mStatusView.setText(LocalizationManager.from(getContext()).getString(2131165601));
                this.mStatusView.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                this.mStatusView.setVisibility(0);
            }
        }
    }

    public void setImage(Uri uri, int rotation) {
        this.mThumbView.setUri(uri);
    }

    public final void setScaleType(ScaleType scaleType) {
        this.mThumbView.setScaleType(scaleType);
    }

    public final void setContentSize(int width, int height) {
        LayoutParams lp = this.mStatusView.getLayoutParams();
        lp.width = width;
        lp.height = height;
        this.mStatusView.setLayoutParams(lp);
        lp = this.mThumbView.getLayoutParams();
        lp.width = width;
        lp.height = height;
        this.mThumbView.setLayoutParams(lp);
    }

    public void setShouldDrawGifMarker(boolean shouldDrawGifMarker) {
        this.mThumbView.setShouldDrawGifMarker(shouldDrawGifMarker);
    }
}
