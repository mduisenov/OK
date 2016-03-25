package ru.ok.android.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.utils.Utils;
import ru.ok.model.messages.MessageBase;

public final class ReplyToCommentView extends LinearLayout implements OnClickListener {
    private final TextView _authorView;
    private MessageBase _comment;
    private ReplyToCommentListener _listener;

    public interface ReplyToCommentListener {
        void onReplyToCloseClicked();
    }

    public ReplyToCommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(2130903421, this, true);
        setOrientation(0);
        setGravity(17);
        setBackgroundResource(2131492945);
        findViewById(C0263R.id.cancel).setOnClickListener(this);
        this._authorView = (TextView) findViewById(2131624696);
        int padding = (int) Utils.dipToPixels(4.0f);
        setPadding(padding, padding, padding, padding);
    }

    public void setComment(MessageBase comment, String authorName) {
        this._authorView.setText(authorName);
        this._comment = comment;
    }

    public MessageBase getComment() {
        return getVisibility() == 0 ? this._comment : null;
    }

    public MessageBase getOriginalComment() {
        return this._comment;
    }

    public void setListener(ReplyToCommentListener listener) {
        this._listener = listener;
    }

    public void onClick(View view) {
        this._listener.onReplyToCloseClicked();
    }
}
