package ru.ok.android.ui.stream.list;

import android.view.View;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.view.PhotoRollView;

public class PhotoRollViewHolder extends ViewHolder {
    private StreamLayoutConfig layoutConfig;

    public PhotoRollViewHolder(View view) {
        super(view);
    }

    public void updateForLayoutSize(StreamLayoutConfig layoutConfig) {
        this.layoutConfig = layoutConfig;
        doUpdateForLayoutSize();
    }

    private void doUpdateForLayoutSize() {
        if (this.layoutConfig != null) {
            int extraMargin = this.layoutConfig.getExtraMarginForLandscapeAsInPortrait(true);
            StreamItem.applyExtraMarginsToBg(this.itemView, extraMargin, extraMargin);
            this.itemView.setPadding(this.originalLeftPadding + extraMargin, this.originalTopPadding, this.originalRightPadding + extraMargin, this.originalBottomPadding);
        }
    }

    public void updateStyleForLayoutSize(int styleResId) {
        PhotoRollView photoRollView = this.itemView;
        if (photoRollView.getCurrentStyle() != styleResId) {
            resetItemViewPaddingsToOriginal();
            photoRollView.setStyle(styleResId);
            doUpdateForLayoutSize();
        }
    }

    private void resetItemViewPaddingsToOriginal() {
        this.itemView.setPadding(this.originalLeftPadding, this.originalTopPadding, this.originalRightPadding, this.originalBottomPadding);
    }
}
