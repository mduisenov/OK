package ru.ok.android.ui.custom.player;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.C0206R;
import ru.ok.android.drawable.PlayPauseDrawable;
import ru.ok.android.proto.MessagesProto.Message;

public class PlayPauseView extends ImageView implements OnClickListener {
    private List<OnPlayPauseCheckedChangedListener> listeners;
    private PlayPauseDrawable playPauseDrawable;
    private StatesView state;

    /* renamed from: ru.ok.android.ui.custom.player.PlayPauseView.1 */
    static /* synthetic */ class C07411 {
        static final /* synthetic */ int[] f98xa7f17b48;

        static {
            f98xa7f17b48 = new int[StatesView.values().length];
            try {
                f98xa7f17b48[StatesView.PLAY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f98xa7f17b48[StatesView.PAUSE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public interface OnPlayPauseCheckedChangedListener {
        void onPauseClick(PlayPauseView playPauseView);

        void onPlayClick(PlayPauseView playPauseView);
    }

    public enum StatesView {
        PLAY,
        PAUSE
    }

    public PlayPauseView(Context context) {
        this(context, null);
    }

    public PlayPauseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayPauseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.listeners = new ArrayList();
        TypedArray ta = context.obtainStyledAttributes(attrs, C0206R.styleable.PlayPauseView);
        try {
            this.playPauseDrawable = new PlayPauseDrawable(ta.getColor(6, -2200033), ta.getColor(5, -11053225), ta.getDimension(0, 80.0f), ta.getDimension(1, 100.0f), ta.getDimension(0, 80.0f), ta.getDimension(3, 42.0f), ta.getDimension(4, 5.0f), ta.getInt(7, 250));
            init();
        } finally {
            ta.recycle();
        }
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable("default_state"));
            this.state = (StatesView) bundle.getSerializable("play_state");
            if (this.state != null) {
                switch (C07411.f98xa7f17b48[this.state.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        this.playPauseDrawable.forcePlay();
                        return;
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        this.playPauseDrawable.forcePause();
                        return;
                    default:
                        return;
                }
            }
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("default_state", super.onSaveInstanceState());
        bundle.putSerializable("play_state", this.state);
        return bundle;
    }

    public void addOnPlayPauseCheckedChangedListener(OnPlayPauseCheckedChangedListener listener) {
        this.listeners.add(listener);
    }

    private void init() {
        setOnClickListener(this);
        setImageDrawable(this.playPauseDrawable);
        invalidate();
    }

    public void onClick(View view) {
        notifyCheckedChanged();
    }

    private void notifyCheckedChanged() {
        for (OnPlayPauseCheckedChangedListener listener : this.listeners) {
            if (this.state == StatesView.PLAY) {
                listener.onPauseClick(this);
            } else {
                listener.onPlayClick(this);
            }
        }
    }

    public void setPlay() {
        if (this.state != StatesView.PLAY) {
            this.playPauseDrawable.play();
            this.state = StatesView.PLAY;
        }
    }

    public void setPause() {
        if (this.state != StatesView.PAUSE) {
            this.playPauseDrawable.pause();
            this.state = StatesView.PAUSE;
        }
    }

    public void setWait() {
        setEnabled(false);
    }

    public void setForcePause() {
        this.state = StatesView.PAUSE;
        this.playPauseDrawable.forcePause();
    }

    public void setForcePlay() {
        this.state = StatesView.PLAY;
        this.playPauseDrawable.forcePlay();
    }
}
