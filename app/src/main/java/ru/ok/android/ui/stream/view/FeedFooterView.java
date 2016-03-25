package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.Locale;
import ru.ok.android.C0206R;
import ru.ok.android.ui.custom.layout.LinearSetPressedLayout;
import ru.ok.android.ui.custom.photo.LikesView.OnLikesActionListener;
import ru.ok.android.ui.custom.photo.LikesViewSynced;
import ru.ok.model.stream.ActionCountInfo;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.LikeInfoContext;

public class FeedFooterView extends LinearSetPressedLayout implements OnClickListener, OnLikesActionListener {
    private FeedFooterInfo info;
    private final LikesViewSynced likesView;
    private final NumberFormat numberFormat;
    private OnCommentsClickListener onCommentsClickListener;
    private OnLikeListener onLikeListener;
    private OnReshareClickListener onReshareClickListener;
    private final TextView textComments;
    private final TextView textShare;

    public interface OnLikeListener {
        void onLikeClicked(FeedFooterView feedFooterView, FeedFooterInfo feedFooterInfo, LikeInfoContext likeInfoContext);

        void onLikeCountClicked(FeedFooterView feedFooterView, FeedFooterInfo feedFooterInfo);
    }

    public interface OnCommentsClickListener {
        void onCommentsClicked(FeedFooterView feedFooterView, FeedFooterInfo feedFooterInfo);
    }

    public interface OnReshareClickListener {
        void onReshareClicked(FeedFooterView feedFooterView, FeedFooterInfo feedFooterInfo);
    }

    public FeedFooterView(Context context) {
        this(context, null);
    }

    public FeedFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 2130771973, 2131296519);
    }

    public FeedFooterView(Context context, AttributeSet attrs, int defThemeAttr, int defStyle) {
        super(context, attrs);
        this.numberFormat = NumberFormat.getIntegerInstance(Locale.FRENCH);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.FeedFooterView, defThemeAttr, defStyle);
        int commentsIconResId = a.getResourceId(0, 0);
        int shareIconResId = a.getResourceId(1, 0);
        int layoutResId = a.getResourceId(2, 2130903184);
        a.recycle();
        inflate(context, layoutResId, this);
        this.likesView = (LikesViewSynced) findViewById(2131624794);
        this.textComments = (TextView) findViewById(2131624810);
        if (this.textComments != null) {
            this.textComments.setCompoundDrawablesWithIntrinsicBounds(commentsIconResId, 0, 0, 0);
            this.textComments.setOnClickListener(this);
        }
        this.textShare = (TextView) findViewById(2131624811);
        if (this.textShare != null) {
            this.textShare.setCompoundDrawablesWithIntrinsicBounds(shareIconResId, 0, 0, 0);
            this.textShare.setOnClickListener(this);
        }
        setGravity(21);
    }

    public void setInfo(FeedFooterInfo info) {
        this.info = info;
        this.likesView.setLikeInfo(info == null ? null : info.klassInfo, false);
        this.likesView.setOnLikesActionListener(this);
        bindInfo(this.textComments, info == null ? null : info.discussionSummary);
        bindInfo(this.textShare, info == null ? null : info.shareInfo);
        if (info != null) {
            if (!(this.likesView == null || this.likesView.textKlass == null)) {
                this.likesView.textKlass.setTag(2131624318, info.feed);
            }
            if (this.textComments != null) {
                this.textComments.setTag(2131624318, info.feed);
            }
        }
    }

    private void bindInfo(TextView textView, ActionCountInfo info) {
        if (textView != null) {
            textView.setVisibility(info == null ? 8 : 0);
            if (info != null) {
                textView.setText(this.numberFormat.format((long) info.count));
                textView.setActivated(info.self);
            }
        }
    }

    private void bindInfo(TextView textView, DiscussionSummary discussionSummary) {
        if (textView != null) {
            textView.setVisibility(discussionSummary == null ? 8 : 0);
            if (discussionSummary != null) {
                textView.setText(this.numberFormat.format((long) discussionSummary.commentsCount));
            }
        }
    }

    public void setOnLikeListener(OnLikeListener listener) {
        this.onLikeListener = listener;
    }

    public void setOnCommentsClickListener(OnCommentsClickListener listener) {
        this.onCommentsClickListener = listener;
    }

    public void setOnReshareClickListener(OnReshareClickListener listener) {
        this.onReshareClickListener = listener;
    }

    public void onClick(View view) {
        if (view.getId() == 2131624810) {
            if (this.onCommentsClickListener != null && this.info != null && this.info.discussionSummary != null) {
                this.onCommentsClickListener.onCommentsClicked(this, this.info);
            }
        } else if (view.getId() == 2131624811 && this.onReshareClickListener != null && this.info != null && this.info.shareInfo != null) {
            this.onReshareClickListener.onReshareClicked(this, this.info);
        }
    }

    public void onLikeClicked(View view, LikeInfoContext likeInfo) {
        notifyLikeClicked(view, likeInfo);
    }

    public void onUnlikeClicked(View view, LikeInfoContext likeInfo) {
        notifyLikeClicked(view, likeInfo);
    }

    public void onLikesCountClicked(View view, LikeInfoContext likeInfo) {
        if (this.onLikeListener != null && this.info != null && this.info.discussionSummary != null) {
            this.onLikeListener.onLikeCountClicked(this, this.info);
        }
    }

    private void notifyLikeClicked(View view, LikeInfoContext likeInfo) {
        if (this.onLikeListener != null && this.info != null && this.info.klassInfo != null) {
            this.onLikeListener.onLikeClicked(this, this.info, likeInfo);
        }
    }
}
