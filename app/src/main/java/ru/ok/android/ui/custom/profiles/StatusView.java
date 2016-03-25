package ru.ok.android.ui.custom.profiles;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.services.processors.music.StatusPlayMusicProcessor;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.QuickAction;
import ru.ok.android.ui.stream.view.AbstractOptionsPopupWindow;
import ru.ok.android.ui.stream.view.PlayingProgressButton;
import ru.ok.model.UserStatus;

public class StatusView extends LinearLayout implements OnClickListener {
    private View moreView;
    private OnStatusListener onStatusListener;
    private QuickAction optionsWindow;
    private PlayingProgressButton playPauseView;
    private UserStatus status;
    private TextView statusText;

    /* renamed from: ru.ok.android.ui.custom.profiles.StatusView.1 */
    class C07461 extends AbstractOptionsPopupWindow {
        C07461(Context x0) {
            super(x0);
        }

        public void onItemClick(QuickAction source, int pos, int actionId) {
            if (StatusView.this.onStatusListener != null && StatusView.this.status != null) {
                StatusView.this.onStatusListener.onDeleteStatus(StatusView.this.status);
            }
        }

        protected List<ActionItem> getActionItems() {
            return Arrays.asList(new ActionItem[]{new ActionItem(0, 2131165695, 2130838574)});
        }
    }

    public interface OnStatusListener {
        void onDeleteStatus(UserStatus userStatus);

        void onOpenStatus(UserStatus userStatus);
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(2130903443, this, true);
        this.statusText = (TextView) findViewById(C0263R.id.text);
        this.moreView = findViewById(2131624874);
        this.moreView.setOnClickListener(this);
        this.playPauseView = (PlayingProgressButton) findViewById(2131625312);
        setOnClickListener(this);
        this.playPauseView.setOnClickListener(this);
        this.playPauseView.setEnabled(true);
    }

    public void setShowMore(boolean value) {
        this.moreView.setVisibility(value ? 0 : 8);
    }

    public void setStatus(UserStatus status) {
        this.status = status;
        this.statusText.setText(status.text);
        this.playPauseView.setVisibility(status.isMusicStatus() ? 0 : 8);
    }

    public void setOnOpenStatusListener(OnStatusListener onStatusListener) {
        this.onStatusListener = onStatusListener;
    }

    public UserStatus getStatus() {
        return this.status;
    }

    public void setPlay() {
        this.playPauseView.setBuffering(false);
        this.playPauseView.setPlaying(true);
    }

    public void setPause() {
        this.playPauseView.setBuffering(false);
        this.playPauseView.setPlaying(false);
    }

    public void onClick(View v) {
        if (getContext() == null) {
            return;
        }
        if (v == this) {
            openTextStatus();
        } else if (v == this.moreView) {
            showOptionWindow(v);
        } else if (v.getId() != 2131625312) {
        } else {
            if (this.playPauseView.isPlaying()) {
                getContext().startService(MusicService.getTogglePlayIntent(getContext()));
                return;
            }
            this.playPauseView.setBuffering(true);
            playMusicStatus();
        }
    }

    private void openTextStatus() {
        if (this.onStatusListener != null && this.status != null) {
            this.onStatusListener.onOpenStatus(this.status);
        }
    }

    private void playMusicStatus() {
        GlobalBus.send(2131624087, new BusEvent(StatusPlayMusicProcessor.fillBundle(this.status.trackId, String.valueOf(this.status.trackId), "")));
    }

    private void showOptionWindow(View anchor) {
        if (this.optionsWindow == null) {
            this.optionsWindow = new C07461(getContext());
        } else if (this.optionsWindow.isShowing()) {
            this.optionsWindow.dismiss();
            return;
        }
        this.optionsWindow.show(anchor);
    }
}
