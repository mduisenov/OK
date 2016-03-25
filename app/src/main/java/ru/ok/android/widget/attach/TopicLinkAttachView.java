package ru.ok.android.widget.attach;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Collections;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;
import ru.ok.android.ui.stream.list.StreamLinkItem.SimpleTemplateChooser.ImageType;
import ru.ok.android.utils.Utils;
import ru.ok.android.widget.attach.BaseAttachGridView.OnAttachClickListener;
import ru.ok.model.ImageUrl;
import ru.ok.model.messages.Attachment;

public class TopicLinkAttachView extends RelativeLayout implements OnClickListener {
    private OnAttachClickListener attachClickListener;
    private Attachment attachment;
    private TextView description;
    private AsyncDraweeView image;
    private TextView title;
    private TextView url;

    public TopicLinkAttachView(Context context) {
        super(context);
    }

    public TopicLinkAttachView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopicLinkAttachView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAttachClickListener(OnAttachClickListener attachClickListener) {
        this.attachClickListener = attachClickListener;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.title = (TextView) findViewById(2131625352);
        this.description = (TextView) findViewById(2131625354);
        this.url = (TextView) findViewById(2131625355);
        this.image = (AsyncDraweeView) findViewById(2131625351);
        setOnClickListener(this);
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
        Utils.setTextViewTextWithVisibility(this.title, attachment.linkTitle);
        Utils.setTextViewTextWithVisibility(this.description, attachment.linkDescription);
        Utils.setTextViewTextWithVisibility(this.url, attachment.linkUrl);
        if (attachment.linkUrlImages == null || attachment.linkUrlImages.size() <= 0) {
            this.image.setVisibility(8);
            return;
        }
        String imageUrl;
        ImageUrl url = (ImageUrl) attachment.linkUrlImages.get(0);
        if (url.getWidth() == 0) {
            imageUrl = url.getUrlPrefix();
        } else {
            imageUrl = url.getUrlPrefix() + ImageType.LOW_XHDPI.getUrlType();
        }
        this.image.setUri(Uri.parse(imageUrl));
        this.image.setVisibility(0);
    }

    public void onClick(View v) {
        if (this.attachClickListener != null) {
            this.attachClickListener.onAttachClick(this, Collections.singletonList(this.attachment), this.attachment);
        }
    }
}
