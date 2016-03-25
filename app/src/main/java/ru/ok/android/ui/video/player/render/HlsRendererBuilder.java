package ru.ok.android.ui.video.player.render;

import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.chunk.MultiTrackChunkSource;
import com.google.android.exoplayer.hls.HlsChunkSource;
import com.google.android.exoplayer.hls.HlsPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylistParser;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.metadata.Id3Parser;
import com.google.android.exoplayer.metadata.MetadataTrackRenderer;
import com.google.android.exoplayer.text.eia608.Eia608TrackRenderer;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.UriDataSource;
import com.google.android.exoplayer.util.ManifestFetcher;
import com.google.android.exoplayer.util.ManifestFetcher.ManifestCallback;
import java.io.IOException;
import java.util.Map;
import ru.ok.android.ui.video.player.ExoHandlePlayer;
import ru.ok.android.ui.video.player.ExoHandlePlayer.RendererBuilder;
import ru.ok.android.ui.video.player.ExoHandlePlayer.RendererBuilderCallback;

public class HlsRendererBuilder implements ManifestCallback<HlsPlaylist>, RendererBuilder {
    private RendererBuilderCallback callback;
    private final String contentId;
    private ExoHandlePlayer player;
    private final String url;
    private final String userAgent;

    public HlsRendererBuilder(String userAgent, String url, String contentId) {
        this.userAgent = userAgent;
        this.url = url;
        this.contentId = contentId;
    }

    public void buildRenderers(ExoHandlePlayer player, RendererBuilderCallback callback) {
        this.player = player;
        this.callback = callback;
        new ManifestFetcher(new HlsPlaylistParser(), this.contentId, this.url, this.userAgent).singleLoad(player.getMainHandler().getLooper(), this);
    }

    public void onManifestError(String contentId, IOException e) {
        this.callback.onRenderersError(e);
    }

    public void onManifest(String contentId, HlsPlaylist manifest) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        HlsSampleSource sampleSource = new HlsSampleSource(new HlsChunkSource(new UriDataSource(this.userAgent, bandwidthMeter), this.url, manifest, bandwidthMeter, null, 1), true, 3);
        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(sampleSource, 1, 5000, this.player.getMainHandler(), this.player, 50);
        MediaCodecAudioTrackRenderer mediaCodecAudioTrackRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
        MetadataTrackRenderer<Map<String, Object>> metadataTrackRenderer = new MetadataTrackRenderer(sampleSource, new Id3Parser(), this.player.getId3MetadataRenderer(), this.player.getMainHandler().getLooper());
        Eia608TrackRenderer eia608TrackRenderer = new Eia608TrackRenderer(sampleSource, this.player, this.player.getMainHandler().getLooper());
        String[][] trackNames = new String[5][];
        trackNames[0] = new String[]{"Auto"};
        multiTrackChunkSources = new MultiTrackChunkSource[5];
        TrackRenderer[] renderers = new TrackRenderer[5];
        renderers[0] = videoRenderer;
        renderers[1] = mediaCodecAudioTrackRenderer;
        renderers[3] = metadataTrackRenderer;
        renderers[2] = eia608TrackRenderer;
        this.callback.onRenderers(trackNames, multiTrackChunkSources, renderers);
    }
}
