package ru.ok.android.ui.stream.list;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.android.utils.URLUtil;
import ru.ok.model.ImageUrl;
import ru.ok.model.mediatopics.MediaItemLink;

public class StreamLinkItem extends StreamItem {
    private final CharSequence descr;
    private final String linkUrl;
    private final CharSequence linkUrlText;
    private final TemplateOptions templateOptions;
    private final CharSequence title;

    /* renamed from: ru.ok.android.ui.stream.list.StreamLinkItem.1 */
    static /* synthetic */ class C12391 {
        static final /* synthetic */ int[] f119xcb3b1035;

        static {
            f119xcb3b1035 = new int[TemplateType.values().length];
            try {
                f119xcb3b1035[TemplateType.IMAGE_NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f119xcb3b1035[TemplateType.IMAGE_LEFT_SMALL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f119xcb3b1035[TemplateType.IMAGE_DOWN_BIG.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    static class LinkHeaderHolder extends ViewHolder {
        final TextView descrView;
        final ViewGroup imageContainer;
        final ViewGroup imageContainerDown;
        final SimpleDraweeView imageView;
        final SimpleDraweeView imageViewDown;
        final TextView titleView;
        final TextView urlView;

        public LinkHeaderHolder(View view) {
            super(view);
            this.imageView = (SimpleDraweeView) view.findViewById(2131625351);
            this.titleView = (TextView) view.findViewById(2131625352);
            this.urlView = (TextView) view.findViewById(2131625355);
            this.descrView = (TextView) view.findViewById(2131625354);
            this.imageContainer = (ViewGroup) view.findViewById(2131625350);
            this.imageContainerDown = (ViewGroup) view.findViewById(2131625356);
            this.imageViewDown = (SimpleDraweeView) view.findViewById(2131625357);
        }
    }

    public interface TemplateChooser {
        CharSequence getDescription();

        String getLinkUrl();

        CharSequence getLinkUrlText();

        TemplateOptions getTemplateOptions();

        CharSequence getTitle();
    }

    public static class SimpleTemplateChooser implements TemplateChooser {
        private final CharSequence description;
        private final String linkUrl;
        private final CharSequence linkUrlText;
        private final TemplateOptions templateOptions;
        private final CharSequence title;

        public enum ImageType {
            NO_DPI(0, "", 0, TemplateType.IMAGE_NONE),
            LOW_MDPI(64, "API_64", 160, TemplateType.IMAGE_LEFT_SMALL),
            LOW_HDPI(96, "API_96", 240, TemplateType.IMAGE_LEFT_SMALL),
            LOW_XHDPI(NotificationCompat.FLAG_HIGH_PRIORITY, "API_128", 320, TemplateType.IMAGE_LEFT_SMALL),
            LOW_XXHDPI(192, "API_192", 480, TemplateType.IMAGE_LEFT_SMALL),
            HIGH_MDPI(320, "API_320", 160, TemplateType.IMAGE_DOWN_BIG),
            HIGH_HDPI(480, "API_480", 240, TemplateType.IMAGE_DOWN_BIG),
            HIGH_XHDPI(720, "API_720", 320, TemplateType.IMAGE_DOWN_BIG),
            HIGH_XXHDPI(1080, "API_1080", 480, TemplateType.IMAGE_DOWN_BIG);
            
            private final int densityDpi;
            private final int size;
            private final TemplateType templateType;
            private final String urlType;

            private ImageType(int size, String urlType, int densityDpi, TemplateType templateType) {
                this.size = size;
                this.urlType = urlType;
                this.densityDpi = densityDpi;
                this.templateType = templateType;
            }

            public String getUrlType() {
                return this.urlType;
            }

            public static ImageType getImageType(TemplateType templateType, int imageWidth, int deviceDensityDpi) {
                ImageType minImageType = HIGH_XXHDPI;
                for (ImageType imageType : values()) {
                    if (imageType.templateType == templateType && imageWidth <= imageType.size && imageType.size <= minImageType.size) {
                        minImageType = imageType;
                    }
                }
                if ((minImageType == LOW_MDPI || minImageType == HIGH_MDPI) && imageWidth < minImageType.size) {
                    return NO_DPI;
                }
                if (minImageType.densityDpi >= deviceDensityDpi || minImageType == HIGH_XXHDPI || minImageType == LOW_XXHDPI) {
                    return minImageType;
                }
                return NO_DPI;
            }
        }

        public SimpleTemplateChooser(DisplayMetrics metrics, MediaItemLink itemLink) {
            this.title = itemLink.getTitle();
            this.description = itemLink.getDescription();
            this.linkUrl = itemLink.getUrl();
            this.linkUrlText = URLUtil.prepareDisplayableLink(itemLink.getUrl());
            this.templateOptions = evaluateTemplateType(metrics, itemLink.getImageUrls());
        }

        private TemplateOptions evaluateTemplateType(DisplayMetrics metrics, List<ImageUrl> imageUrls) {
            if (imageUrls.isEmpty()) {
                return new TemplateOptions(null, 0.0f, null);
            }
            ImageUrl imageUrl = (ImageUrl) imageUrls.get(0);
            if (TextUtils.isEmpty(imageUrl.getUrlPrefix())) {
                return new TemplateOptions(null, 0.0f, null);
            }
            if (imageUrl.getWidth() == 0) {
                return new TemplateOptions(Uri.parse(imageUrl.getUrlPrefix()), 1.0f, null);
            }
            ImageType imageType;
            float aspectRatio = (((float) imageUrl.getWidth()) * 1.0f) / ((float) imageUrl.getHeight());
            if (aspectRatio <= 2.0f && aspectRatio >= 1.3333334f) {
                imageType = ImageType.getImageType(TemplateType.IMAGE_DOWN_BIG, imageUrl.getWidth(), metrics.densityDpi);
                if (imageType != ImageType.NO_DPI) {
                    return new TemplateOptions(generateUri(imageUrl.getUrlPrefix(), imageType), aspectRatio, null);
                }
            }
            imageType = ImageType.getImageType(TemplateType.IMAGE_LEFT_SMALL, imageUrl.getWidth(), metrics.densityDpi);
            if (imageType != ImageType.NO_DPI) {
                return new TemplateOptions(generateUri(imageUrl.getUrlPrefix(), imageType), 1.0f, null);
            }
            return new TemplateOptions(null, 0.0f, null);
        }

        private Uri generateUri(String urlPrefix, ImageType imageType) {
            return Uri.parse(urlPrefix + imageType.getUrlType());
        }

        public CharSequence getTitle() {
            return this.title;
        }

        public CharSequence getDescription() {
            return this.description;
        }

        public String getLinkUrl() {
            return this.linkUrl;
        }

        public CharSequence getLinkUrlText() {
            return this.linkUrlText;
        }

        public TemplateOptions getTemplateOptions() {
            return this.templateOptions;
        }
    }

    public static class TemplateOptions {
        public float aspectRatio;
        public TemplateType templateType;
        public Uri uri;

        private TemplateOptions(TemplateType templateType, Uri uri, float aspectRatio) {
            this.uri = uri;
            this.aspectRatio = aspectRatio;
            this.templateType = templateType;
        }
    }

    public enum TemplateType {
        IMAGE_NONE,
        IMAGE_LEFT_SMALL,
        IMAGE_DOWN_BIG
    }

    protected StreamLinkItem(FeedWithState feed, TemplateChooser chooser) {
        super(6, 2, 2, feed);
        this.title = chooser.getTitle();
        this.descr = chooser.getDescription();
        this.linkUrlText = chooser.getLinkUrlText();
        this.linkUrl = chooser.getLinkUrl();
        this.templateOptions = chooser.getTemplateOptions();
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903482, parent, false);
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        LinkHeaderHolder holder = new LinkHeaderHolder(view);
        holder.itemView.setOnClickListener(streamItemViewController.getLinkClickListener());
        return holder;
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof LinkHeaderHolder) {
            LinkHeaderHolder linkViewHolder = (LinkHeaderHolder) holder;
            switch (C12391.f119xcb3b1035[this.templateOptions.templateType.ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    linkViewHolder.imageView.setVisibility(8);
                    linkViewHolder.imageContainerDown.setVisibility(8);
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    linkViewHolder.imageContainerDown.setVisibility(8);
                    linkViewHolder.imageView.setImageURI(this.templateOptions.uri);
                    linkViewHolder.imageView.setVisibility(0);
                    break;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    linkViewHolder.imageView.setVisibility(8);
                    linkViewHolder.imageViewDown.setImageURI(this.templateOptions.uri);
                    linkViewHolder.imageViewDown.setAspectRatio(this.templateOptions.aspectRatio);
                    linkViewHolder.imageContainerDown.setVisibility(0);
                    break;
            }
            linkViewHolder.titleView.setText(this.title);
            linkViewHolder.urlView.setText(this.linkUrlText);
            if (TextUtils.isEmpty(this.descr)) {
                linkViewHolder.descrView.setVisibility(8);
            } else {
                linkViewHolder.descrView.setText(this.descr);
                linkViewHolder.descrView.setVisibility(0);
            }
            if (TextUtils.isEmpty(this.linkUrl)) {
                linkViewHolder.itemView.setClickable(false);
            } else {
                linkViewHolder.itemView.setTag(2131624327, this.linkUrl);
                linkViewHolder.itemView.setClickable(true);
            }
            linkViewHolder.itemView.setTag(2131624322, this.feedWithState);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    int getVSpacingBottom(Context context) {
        return context.getResources().getDimensionPixelOffset(2131230980);
    }

    int getVSpacingTop(Context context) {
        return context.getResources().getDimensionPixelOffset(2131230980);
    }

    public void prefetch() {
        PrefetchUtils.prefetchUrl(this.templateOptions.uri);
    }
}
