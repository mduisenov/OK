package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerStyleParams;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.android.ui.custom.mediacomposer.MusicItem;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.Track;

public class MusicItemAdapterHandler extends MediaItemAdapterHandler<MusicItem> {
    protected MusicItemAdapterHandler(Context context, LocalizationManager localizationManager, FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType, MediaComposerStyleParams styleParams, ImageHandler imageHandler) {
        super(context, localizationManager, styleParams, mediaComposerController, fragmentBridge, mediaTopicType, imageHandler);
    }

    MediaItemActionProvider createActionProvider() {
        return new MusicItemActionProvider(this.fragmentBridge, this.mediaComposerController, this.mediaTopicType);
    }

    public View createView(MusicItem musicItem, ViewGroup parent, boolean isEditable, int viewId) {
        LocalizationManager localizationManager = this.localizationManager;
        View view = LocalizationManager.inflate(this.context, 2130903310, parent, false);
        ViewGroup tracksList = (LinearLayout) view.findViewById(2131625061);
        List<Track> tracks = musicItem.getTracks();
        int tracksCount = tracks.size();
        for (int i = 0; i < tracksCount; i++) {
            Track track = (Track) tracks.get(i);
            localizationManager = this.localizationManager;
            TextView trackView = (TextView) LocalizationManager.inflate(this.context, 2130903311, tracksList, false);
            trackView.setText(getSpannableText(this.context, track));
            if (i == tracksCount - 1) {
                ((MarginLayoutParams) trackView.getLayoutParams()).bottomMargin = this.context.getResources().getDimensionPixelSize(2131231072);
            }
            tracksList.addView(trackView);
        }
        View itemView = createDecoratedViewWithActions(musicItem, view, parent, null);
        updateViewIsEditable(itemView, musicItem, parent, isEditable);
        itemView.setId(viewId);
        return itemView;
    }

    public void updateViewIsEditable(View view, MusicItem musicItem, ViewGroup parent, boolean isEditable) {
        super.updateViewIsEditable(view, musicItem, parent, isEditable);
        view.setClickable(isEditable);
    }

    private static Spannable getSpannableText(Context context, Track trackParcelable) {
        String trackText;
        String trackTitle = trackParcelable.name;
        Artist artist = trackParcelable.artist;
        String artistName = artist == null ? null : artist.name;
        if (artistName == null) {
            trackText = trackTitle;
        } else {
            trackText = trackTitle + " - " + artistName;
        }
        SpannableString trackSpannable = new SpannableString(trackText);
        trackSpannable.setSpan(new TextAppearanceSpan(context, 2131296736), 0, trackTitle.length(), 33);
        if (artistName != null) {
            trackSpannable.setSpan(new TextAppearanceSpan(context, 2131296737), trackTitle.length(), trackText.length(), 33);
        }
        return trackSpannable;
    }
}
