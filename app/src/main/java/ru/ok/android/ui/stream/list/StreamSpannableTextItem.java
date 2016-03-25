package ru.ok.android.ui.stream.list;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;
import android.widget.Toast;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.SpannableUtils;
import ru.ok.android.utils.Utils;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.message.FeedEntitySpan;

public class StreamSpannableTextItem extends AbsStreamTextItem<SpannableStringBuilder> {
    private static final OnLongClickListener longClickListener;
    final int blockIndex;
    final boolean editable;
    final EntitySpanStyle style;
    final String topicId;

    /* renamed from: ru.ok.android.ui.stream.list.StreamSpannableTextItem.1 */
    static class C12411 implements OnLongClickListener {
        C12411() {
        }

        public boolean onLongClick(View v) {
            Utils.addToClipBoard(v.getContext(), v.getContext().getString(2131165393), ((TextView) v).getText());
            Toast.makeText(v.getContext(), 2131166718, 1).show();
            return true;
        }
    }

    static class ClickableEntitySpan extends ClickableSpan {
        private final BaseEntity entity;
        private EntityClickListener entityClickListener;
        private final boolean fakeBoldText;
        private final FeedWithState feed;
        private final int textColor;
        private final boolean underline;

        ClickableEntitySpan(FeedWithState feed, BaseEntity entity, int textColor, boolean underline, boolean fakeBoldText) {
            this.feed = feed;
            this.entity = entity;
            this.textColor = textColor;
            this.underline = underline;
            this.fakeBoldText = fakeBoldText;
        }

        void setEntityClickListener(EntityClickListener listener) {
            this.entityClickListener = listener;
        }

        public void onClick(View view) {
            if (this.entityClickListener != null) {
                this.entityClickListener.onClick(this.feed.position, this.feed.feed, this.entity, view);
                view.performHapticFeedback(1);
            }
        }

        public void updateDrawState(TextPaint ds) {
            ds.setColor(this.textColor);
            ds.setUnderlineText(this.underline);
            ds.setFakeBoldText(this.fakeBoldText);
        }
    }

    public interface EntityClickListener {
        void onClick(int i, Feed feed, BaseEntity baseEntity, View view);
    }

    static {
        longClickListener = new C12411();
    }

    protected StreamSpannableTextItem(FeedWithState feed, SpannableStringBuilder text, boolean editable, String topicId, int blockIndex, DiscussionSummary discussionSummary, EntitySpanStyle style) {
        super(4, 3, 3, feed, (CharSequence) text, discussionSummary);
        this.style = style;
        this.editable = editable;
        this.topicId = topicId;
        this.blockIndex = blockIndex;
        initSpans();
    }

    private void initSpans() {
        Feed feed = this.feedWithState.feed;
        for (FeedEntitySpan span : (FeedEntitySpan[]) SpannableUtils.getSpans((Spanned) this.text, FeedEntitySpan.class)) {
            BaseEntity entity = feed.getEntity(span.getRef());
            if (entity == null) {
                Logger.m185w("Entity not found for ref=%s in feed=%s", span.getRef(), feed);
            } else {
                SpannableUtils.setSpanOverSpan((Spannable) this.text, span, new ClickableEntitySpan(this.feedWithState, entity, this.style.userTextColor, this.style.underlineUser, this.style.fakeBoldText));
            }
        }
        if (this.editable) {
            int padding = (int) Utils.dipToPixels(6.0f);
            Drawable drawable = OdnoklassnikiApplication.getContext().getResources().getDrawable(2130838113);
            drawable.setBounds(padding, 0, drawable.getIntrinsicWidth() + padding, drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(drawable, 1);
            ((SpannableStringBuilder) this.text).append(" ");
            ((SpannableStringBuilder) this.text).setSpan(imageSpan, ((SpannableStringBuilder) this.text).length() - 1, ((SpannableStringBuilder) this.text).length(), 0);
        }
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        super.bindView(holder, streamItemViewController, layoutConfig);
        EntityClickListener listener = streamItemViewController.getSpanClickListener();
        for (ClickableEntitySpan span : (ClickableEntitySpan[]) SpannableUtils.getSpans((Spanned) this.text, ClickableEntitySpan.class)) {
            span.setEntityClickListener(listener);
        }
        if (this.editable) {
            holder.itemView.setTag(2131624351, this.topicId);
            holder.itemView.setTag(2131623943, Integer.valueOf(this.blockIndex));
            holder.itemView.setTag(C0263R.id.text, ((SpannableStringBuilder) this.text).toString().trim());
            holder.itemView.setOnClickListener(streamItemViewController.getTextEditClickListener());
        }
        holder.itemView.setOnLongClickListener(longClickListener);
    }
}
