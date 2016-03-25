package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import java.util.HashSet;
import java.util.Set;
import ru.ok.android.C0206R;
import ru.ok.android.ui.custom.imageview.MultiUserAvatar;
import ru.ok.android.ui.stream.LineSpacingSpan;
import ru.ok.android.utils.ViewUtil;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.StreamPageKey;

public class FeedHeaderView extends RelativeLayout implements OnClickListener {
    private final MultiUserAvatar avatarView;
    private boolean debugMode;
    private final Set<View> disabledDrawableStateChange;
    private FeedHeaderInfo info;
    private FeedHeaderViewListener listener;
    private final FeedMessageSpanFormatter messageSpanFormatter;
    private final View pinnedView;
    private final View promoLabel;
    private final TextView textView;
    private final int timeTextAppearance;
    private final int vSpacingSmall;

    public interface FeedHeaderViewListener {
        void onClickedAvatar(FeedHeaderInfo feedHeaderInfo);

        void onClickedFeedHeader(FeedHeaderInfo feedHeaderInfo);
    }

    public FeedHeaderView(Context context) {
        this(context, null);
    }

    public FeedHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 2130771974, 2131296523);
    }

    public FeedHeaderView(Context context, AttributeSet attrs, int defThemeAttr, int defStyle) {
        super(context, attrs, defThemeAttr);
        this.disabledDrawableStateChange = new HashSet();
        LayoutInflater li = LayoutInflater.from(context);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.FeedHeaderView, defThemeAttr, defStyle);
        int layoutResId = 0;
        if (null == null) {
            layoutResId = a.getResourceId(2, 2130903455);
        }
        li.inflate(layoutResId, this);
        this.avatarView = (MultiUserAvatar) findViewById(2131624657);
        if (this.avatarView != null) {
            this.avatarView.setOnClickListener(this);
        }
        TextView textView = (TextView) findViewById(2131625330);
        if (textView == null) {
            throw new AssertionError("Text view not found");
        }
        this.textView = textView;
        this.pinnedView = findViewById(2131625326);
        this.promoLabel = findViewById(2131625327);
        this.messageSpanFormatter = new FeedMessageSpanFormatter(context, attrs, defStyle);
        int messageTextAppearance = this.messageSpanFormatter.getMessageTextAppearance();
        this.timeTextAppearance = a.getResourceId(1, messageTextAppearance);
        a.recycle();
        this.vSpacingSmall = getResources().getDimensionPixelOffset(2131231001);
        textView.setTextAppearance(getContext(), messageTextAppearance);
        setOnClickListener(this);
    }

    public void setListener(FeedHeaderViewListener listener) {
        this.listener = listener;
    }

    public void setFeedHeaderInfo(FeedHeaderInfo info) {
        this.info = info;
        if (info == null) {
            clear();
        } else {
            bindInfo(info);
        }
    }

    private void bindInfo(FeedHeaderInfo info) {
        Feed feed = info.feed.feed;
        if (this.avatarView != null) {
            this.avatarView.setUsers(info.avatars, null, String.valueOf(feed.getId()));
            if (info.avatars == null || info.avatars.isEmpty()) {
                this.avatarView.setVisibility(8);
                ViewUtil.resetLayoutParams(this.textView, -1, -2, 0, 0);
            } else {
                this.avatarView.setVisibility(0);
                this.avatarView.setTag(2131624346, info.avatars);
                ViewUtil.resetLayoutParams(this.textView, -1, -2, this.vSpacingSmall, 0);
            }
        }
        if (this.pinnedView != null) {
            this.pinnedView.setVisibility(feed.isPinned() ? 0 : 8);
        }
        if (this.promoLabel != null) {
            this.promoLabel.setVisibility(info.isPromo ? 0 : 8);
        }
        SpannableStringBuilder sb = new SpannableStringBuilder();
        int messageStartOffset = sb.length();
        if (info.message != null) {
            sb.append(info.message);
            this.messageSpanFormatter.applyStyle(info.message, sb, messageStartOffset);
        }
        if (!TextUtils.isEmpty(info.dateFormatted)) {
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
                sb.append('\n');
            }
            int paragraphStartOffset = sb.length();
            sb.append(" \n");
            int dateStartOffset = sb.length();
            sb.append(info.dateFormatted);
            sb.setSpan(new TextAppearanceSpan(getContext(), this.timeTextAppearance), dateStartOffset, sb.length(), 17);
            sb.setSpan(new LineSpacingSpan(-this.vSpacingSmall), paragraphStartOffset, dateStartOffset, 17);
        }
        this.textView.setText(sb, BufferType.SPANNABLE);
        if (this.debugMode) {
            TextView debugTextView = (TextView) findViewById(2131625331);
            if (debugTextView != null) {
                StreamPageKey pageKey = feed.getPageKey();
                if (pageKey == null) {
                    debugTextView.setText(null);
                    return;
                }
                String anchorText = pageKey.getAnchor() == null ? null : Integer.toString(pageKey.getAnchor().hashCode());
                if (anchorText != null) {
                    anchorText = anchorText.substring(Math.max(anchorText.length() - 4, 0), anchorText.length());
                }
                debugTextView.setText(anchorText + "-" + pageKey.getCount() + " ft" + feed.getFeedType());
            }
        }
    }

    public void clear() {
        this.textView.setText(null);
    }

    public void disableDrawableStateChange(View child) {
        this.disabledDrawableStateChange.add(child);
    }

    public void onClick(View v) {
        if (this.listener != null) {
            if (v == this.avatarView) {
                if (this.info == null) {
                    return;
                }
                if (this.info.avatars != null && this.info.avatars.size() == 1) {
                    this.listener.onClickedAvatar(this.info);
                } else if (this.info.referencedUsers != null && !this.info.referencedUsers.isEmpty()) {
                    this.listener.onClickedFeedHeader(this.info);
                }
            } else if (this.info != null) {
                this.listener.onClickedFeedHeader(this.info);
            }
        }
    }

    public void childDrawableStateChanged(View child) {
        if (!this.disabledDrawableStateChange.contains(child)) {
            super.childDrawableStateChanged(child);
        }
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}
