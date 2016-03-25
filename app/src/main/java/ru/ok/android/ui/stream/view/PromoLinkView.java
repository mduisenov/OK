package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.mail.libverify.C0176R;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.model.stream.banner.PromoLink;

public class PromoLinkView extends LinearLayout implements OnClickListener {
    private UrlImageView bannerIconView;
    private TextView bannerTextView;
    private PromoLinkViewListener listener;
    private PromoLink promoLink;

    public interface PromoLinkViewListener {
        void onPromoLinkClicked(@NonNull PromoLink promoLink);
    }

    public PromoLinkView(@NonNull Context context) {
        super(context);
        init();
    }

    public PromoLinkView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setPromoLink(@NonNull PromoLink promoLink, @Nullable HandleBlocker handleBlocker) {
        this.promoLink = promoLink;
        this.bannerTextView.setText(promoLink.banner.header);
        this.bannerTextView.setTextColor(promoLink.banner.color);
        ImageViewManager.getInstance().displayImage(promoLink.banner.iconUrlHd, this.bannerIconView, handleBlocker);
    }

    public void setListener(@Nullable PromoLinkViewListener listener) {
        this.listener = listener;
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(2130903109, this);
        this.bannerTextView = (TextView) findViewById(2131624635);
        this.bannerIconView = (UrlImageView) findViewById(C0176R.id.icon);
        setGravity(1);
        setOrientation(0);
        setOnClickListener(this);
    }

    public void onClick(View view) {
        if (this.listener != null && this.promoLink != null) {
            this.listener.onPromoLinkClicked(this.promoLink);
        }
    }
}
