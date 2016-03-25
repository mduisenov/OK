package ru.ok.android.ui.video.player.render;

import android.content.Context;
import android.net.Uri;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.source.DefaultSampleSource;
import com.google.android.exoplayer.source.FrameworkSampleExtractor;
import ru.ok.android.ui.video.player.ExoHandlePlayer;
import ru.ok.android.ui.video.player.ExoHandlePlayer.RendererBuilder;
import ru.ok.android.ui.video.player.ExoHandlePlayer.RendererBuilderCallback;
import ru.ok.android.ui.video.player.Quality;

public class DefaultRendererBuilder implements RendererBuilder {
    private final Context context;
    private final Quality quality;
    private final Uri uri;

    public DefaultRendererBuilder(Context context, Uri uri, Quality quality) {
        this.context = context;
        this.uri = uri;
        this.quality = quality;
    }

    public void buildRenderers(ExoHandlePlayer player, RendererBuilderCallback callback) {
        DefaultSampleSource sampleSource = new DefaultSampleSource(new FrameworkSampleExtractor(this.context, this.uri, null), 2);
        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(sampleSource, null, true, 1, 5000, null, player.getMainHandler(), player, 50);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, null, true, player.getMainHandler(), player);
        trackNames = new String[5][];
        trackNames[0] = new String[]{String.valueOf(this.quality.height)};
        TrackRenderer[] renderers = new TrackRenderer[5];
        renderers[0] = videoRenderer;
        renderers[1] = audioRenderer;
        callback.onRenderers(trackNames, null, renderers);
    }
}
