package ru.ok.android.ui.adapters.music.tuners;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import ru.ok.android.ui.fragments.messages.view.TunersMultipleImageView;

public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
    private final Context context;
    public TunersMultipleImageView image;
    private boolean isPlay;
    private final int selectedColor;
    public TextView textArtists;
    public TextView textTuner;
    private final int unSelectedColor;

    public ViewHolder(View view) {
        super(view);
        this.context = view.getContext();
        this.unSelectedColor = ContextCompat.getColor(this.context, 2131493113);
        this.selectedColor = ContextCompat.getColor(this.context, 2131493115);
    }

    public void setPlay(boolean isPlay) {
        if (this.isPlay != isPlay) {
            this.isPlay = isPlay;
            if (this.textTuner != null) {
                this.textTuner.setTextColor(isPlay ? this.selectedColor : ContextCompat.getColor(this.context, 2131492917));
            }
            if (this.textArtists != null) {
                this.textArtists.setTextColor(isPlay ? this.selectedColor : this.unSelectedColor);
            }
        }
    }
}
