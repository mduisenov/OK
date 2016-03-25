package ru.ok.android.ui.adapters.music.playlist;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.DateFormatter;
import ru.ok.model.wmf.Track;

public final class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements OnClickListener {
    private static final int[] STATE_CHECKED;
    private static final int[] STATE_UNCHECKED;
    Context context;
    public View convertView;
    public int dataPosition;
    public View divider;
    public View dots;
    public ImageView playImage;
    private boolean playing;
    public ImageView selectedCheckBox;
    private int selectedColor;
    private List<OnSelectionChangeListener> selectionChangeListeners;
    public TextView textArtistName;
    public TextView textTime;
    public TextView textTrackName;
    public long trackId;
    private int unSelectedColor;

    public interface OnSelectionChangeListener {
        void onSelectChange(boolean z, long j, int i);
    }

    /* renamed from: ru.ok.android.ui.adapters.music.playlist.ViewHolder.1 */
    static /* synthetic */ class C05891 {
        static final /* synthetic */ int[] f90x5c43016f;

        static {
            f90x5c43016f = new int[AnimateType.values().length];
            try {
                f90x5c43016f[AnimateType.TRANSLATE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f90x5c43016f[AnimateType.FADE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public enum AnimateType {
        NONE,
        TRANSLATE,
        FADE
    }

    static {
        STATE_CHECKED = new int[]{16842912};
        STATE_UNCHECKED = new int[]{-16842912};
    }

    public void clearListeners() {
        this.selectionChangeListeners.clear();
    }

    ViewHolder(Context context, View view) {
        super(view);
        this.selectionChangeListeners = new ArrayList(3);
        this.playing = false;
        this.context = context;
        this.unSelectedColor = context.getResources().getColor(2131493113);
        this.selectedColor = context.getResources().getColor(2131493115);
    }

    public void onClick(View view) {
    }

    public void addSelectionChangeListener(OnSelectionChangeListener selectionChangeListener) {
        this.selectionChangeListeners.add(selectionChangeListener);
    }

    public void showCheckBox() {
        this.selectedCheckBox.setVisibility(0);
    }

    public void hideCheckBox() {
        this.selectedCheckBox.setVisibility(8);
    }

    public boolean isPlayingState() {
        return this.playing;
    }

    public boolean isCheckMode() {
        return this.selectedCheckBox.getVisibility() == 0;
    }

    public void setPlayValue(boolean value, AnimateType type) {
        if (value) {
            setPlayState(type);
        } else {
            setUnPlayState(type);
        }
    }

    public void setPlayState(AnimateType type) {
        this.textTrackName.setTextColor(this.selectedColor);
        this.textArtistName.setTextColor(this.selectedColor);
        if (!(isCheckMode() || isPlayingState())) {
            switch (C05891.f90x5c43016f[type.ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    showAnimateTranslatePlay();
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    showAnimateFadePlay();
                    break;
                default:
                    this.textTime.setVisibility(8);
                    this.playImage.setVisibility(0);
                    break;
            }
        }
        this.playing = true;
    }

    private void showAnimateTranslatePlay() {
        this.playImage.startAnimation(AnimationUtils.loadAnimation(this.context, 2130968610));
        this.playImage.setVisibility(0);
        this.textTime.startAnimation(AnimationUtils.loadAnimation(this.context, 17432577));
        this.textTime.setVisibility(4);
    }

    private void showAnimateFadePlay() {
        this.playImage.startAnimation(AnimationUtils.loadAnimation(this.context, 2130968598));
        this.playImage.setVisibility(0);
        this.textTime.startAnimation(AnimationUtils.loadAnimation(this.context, 17432577));
        this.textTime.setVisibility(4);
    }

    public void setUnPlayState(AnimateType type) {
        this.textTrackName.setTextColor(this.context.getResources().getColor(2131492917));
        this.textArtistName.setTextColor(this.unSelectedColor);
        if (!isCheckMode() && isPlayingState()) {
            switch (C05891.f90x5c43016f[type.ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    showAnimateTranslateUnPlay();
                    break;
                default:
                    this.textTime.setVisibility(0);
                    this.playImage.setVisibility(8);
                    break;
            }
        }
        this.playing = false;
    }

    private void showAnimateTranslateUnPlay() {
        this.playImage.startAnimation(AnimationUtils.loadAnimation(this.context, 17432579));
        this.playImage.setVisibility(8);
        this.textTime.startAnimation(AnimationUtils.loadAnimation(this.context, 17432576));
        this.textTime.setVisibility(0);
    }

    public void setTrackValue(Track track) {
        this.trackId = track.id;
        this.textTrackName.setText(track.name);
        if (track.artist != null && track.album != null) {
            this.textArtistName.setText(track.artist.name + " - " + track.album.name);
        } else if (track.artist != null) {
            this.textArtistName.setText(track.artist.name);
        } else if (track.album != null) {
            this.textArtistName.setText(track.album.name);
        } else {
            this.textArtistName.setText("");
        }
        this.textTime.setText(DateFormatter.getTimeStringFromSec(track.duration));
        this.textTime.setTextColor(this.context.getResources().getColor(2131492965));
    }

    public void setDataPosition(int dataPosition) {
        this.dataPosition = dataPosition;
    }

    public void setTrackInCache() {
        this.textTime.setTextColor(ViewCompat.MEASURED_STATE_MASK);
    }

    public void setModePlayingState(MusicFragmentMode mode) {
        if (mode == MusicFragmentMode.MULTI_SELECTION) {
            setMultiSelectionMode();
        } else {
            setSimpleMode();
        }
    }

    private void setSimpleMode() {
        this.selectedCheckBox.setVisibility(8);
        this.dots.setVisibility(0);
        this.dots.setClickable(true);
        if (this.playing) {
            this.textTime.setVisibility(8);
            this.playImage.setVisibility(0);
            return;
        }
        this.playImage.setVisibility(8);
        this.textTime.setVisibility(0);
    }

    private void setMultiSelectionMode() {
        this.dots.setVisibility(8);
        this.dots.setClickable(false);
        this.selectedCheckBox.setVisibility(0);
        this.textTime.setVisibility(8);
        this.playImage.setVisibility(8);
    }

    public void setSelected(boolean selected) {
        this.selectedCheckBox.setImageState(selected ? STATE_CHECKED : STATE_UNCHECKED, true);
        this.selectedCheckBox.refreshDrawableState();
        for (OnSelectionChangeListener listener : this.selectionChangeListeners) {
            listener.onSelectChange(selected, this.trackId, this.dataPosition);
        }
    }

    protected static ViewHolder createViewHolder(Context context, View convertView) {
        ViewHolder holder = new ViewHolder(context, convertView);
        holder.convertView = convertView;
        holder.textTrackName = (TextView) convertView.findViewById(2131625001);
        holder.textArtistName = (TextView) convertView.findViewById(2131625002);
        holder.textTime = (TextView) convertView.findViewById(2131625000);
        holder.playImage = (ImageView) convertView.findViewById(2131624999);
        holder.selectedCheckBox = (ImageView) convertView.findViewById(2131624976);
        holder.divider = convertView.findViewById(2131624602);
        holder.dots = convertView.findViewById(2131624874);
        return holder;
    }
}
