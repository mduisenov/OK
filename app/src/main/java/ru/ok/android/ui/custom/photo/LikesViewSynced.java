package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.services.like.LikeManager;
import ru.ok.android.services.like.LikeManager.LikeListener;
import ru.ok.android.storage.Storages;
import ru.ok.model.stream.LikeInfoContext;

public class LikesViewSynced extends LikesView implements LikeListener {
    private final LikeManager likeManager;
    private LikesInfoChangeListener likesInfoChangeListener;

    public interface LikesInfoChangeListener {
        void onLikeInfoChanged(LikeInfoContext likeInfoContext);
    }

    public LikesViewSynced(Context context) {
        this(context, null);
    }

    public LikesViewSynced(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikesViewSynced(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LikesViewSynced(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.likeManager = Storages.getInstance(context, OdnoklassnikiApplication.getCurrentUser().getId()).getLikeManager();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.likeManager.registerListener(this);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.likeManager.unregisterListener(this);
    }

    public void setLikeInfo(@Nullable LikeInfoContext likeInfo, boolean animateOwnLikeChange) {
        this.likeInfo = this.likeManager.getLikeInfo(likeInfo);
        super.setLikeInfo(this.likeInfo, animateOwnLikeChange);
    }

    public void onLikeChanged(String likeId) {
        if (this.likeInfo != null && TextUtils.equals(this.likeInfo.likeId, likeId)) {
            setLikeInfo(this.likeInfo, true);
            if (this.likesInfoChangeListener != null) {
                this.likesInfoChangeListener.onLikeInfoChanged(this.likeInfo);
            }
        }
    }

    public void setLikesInfoChangeListener(LikesInfoChangeListener likesInfoChangeListener) {
        this.likesInfoChangeListener = likesInfoChangeListener;
    }
}
