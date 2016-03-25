package ru.ok.android.ui.video.player.render;

import android.annotation.TargetApi;
import android.media.UnsupportedSchemeException;
import android.os.Handler;
import android.util.Pair;
import android.view.Display;
import android.view.WindowManager;
import com.google.android.exoplayer.DefaultLoadControl;
import com.google.android.exoplayer.LoadControl;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecUtil;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.chunk.ChunkSampleSource;
import com.google.android.exoplayer.chunk.ChunkSource;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.chunk.FormatEvaluator;
import com.google.android.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import com.google.android.exoplayer.chunk.FormatEvaluator.FixedEvaluator;
import com.google.android.exoplayer.chunk.MultiTrackChunkSource;
import com.google.android.exoplayer.dash.DashChunkSource;
import com.google.android.exoplayer.dash.mpd.AdaptationSet;
import com.google.android.exoplayer.dash.mpd.MediaPresentationDescription;
import com.google.android.exoplayer.dash.mpd.MediaPresentationDescriptionParser;
import com.google.android.exoplayer.dash.mpd.Period;
import com.google.android.exoplayer.dash.mpd.Representation;
import com.google.android.exoplayer.drm.DrmSessionManager;
import com.google.android.exoplayer.drm.MediaDrmCallback;
import com.google.android.exoplayer.drm.StreamingDrmSessionManager;
import com.google.android.exoplayer.text.SubtitleParser;
import com.google.android.exoplayer.text.TextTrackRenderer;
import com.google.android.exoplayer.text.ttml.TtmlParser;
import com.google.android.exoplayer.text.webvtt.WebvttParser;
import com.google.android.exoplayer.upstream.BufferPool;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.UriDataSource;
import com.google.android.exoplayer.util.ManifestFetcher;
import com.google.android.exoplayer.util.ManifestFetcher.ManifestCallback;
import com.google.android.exoplayer.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.ui.video.player.ExoHandlePlayer;
import ru.ok.android.ui.video.player.ExoHandlePlayer.RendererBuilder;
import ru.ok.android.ui.video.player.ExoHandlePlayer.RendererBuilderCallback;
import ru.ok.android.ui.video.player.PlayerUtil;
import ru.ok.android.ui.video.player.UnsupportedDrmException;

public class DashRendererBuilder implements ManifestCallback<MediaPresentationDescription>, RendererBuilder {
    private RendererBuilderCallback callback;
    private final String contentId;
    private final MediaDrmCallback drmCallback;
    private ManifestFetcher<MediaPresentationDescription> manifestFetcher;
    private ExoHandlePlayer player;
    private final String url;
    private final String userAgent;

    @TargetApi(18)
    private static class V18Compat {
        public static Pair<DrmSessionManager, Boolean> getDrmSessionManagerData(ExoHandlePlayer player, MediaDrmCallback drmCallback) throws UnsupportedDrmException {
            try {
                StreamingDrmSessionManager streamingDrmSessionManager = new StreamingDrmSessionManager(PlayerUtil.WIDEVINE_UUID, player.getPlaybackLooper(), drmCallback, null, player.getMainHandler(), player);
                return Pair.create(streamingDrmSessionManager, Boolean.valueOf(getWidevineSecurityLevel(streamingDrmSessionManager) == 1));
            } catch (UnsupportedSchemeException e) {
                throw new UnsupportedDrmException(1);
            } catch (Exception e2) {
                throw new UnsupportedDrmException(2, e2);
            }
        }

        private static int getWidevineSecurityLevel(StreamingDrmSessionManager sessionManager) {
            String securityLevelProperty = sessionManager.getPropertyString("securityLevel");
            if (securityLevelProperty.equals("L1")) {
                return 1;
            }
            return securityLevelProperty.equals("L3") ? 3 : -1;
        }
    }

    public DashRendererBuilder(String userAgent, String url, String contentId, MediaDrmCallback drmCallback) {
        this.userAgent = userAgent;
        this.url = url;
        this.contentId = contentId;
        this.drmCallback = drmCallback;
    }

    public void buildRenderers(ExoHandlePlayer player, RendererBuilderCallback callback) {
        this.player = player;
        this.callback = callback;
        this.manifestFetcher = new ManifestFetcher(new MediaPresentationDescriptionParser(), this.contentId, this.url, this.userAgent);
        this.manifestFetcher.singleLoad(player.getMainHandler().getLooper(), this);
    }

    public void onManifestError(String contentId, IOException e) {
        this.callback.onRenderersError(e);
    }

    public void onManifest(String contentId, MediaPresentationDescription manifest) {
        Period period = (Period) manifest.periods.get(0);
        Handler mainHandler = this.player.getMainHandler();
        LoadControl defaultLoadControl = new DefaultLoadControl(new BufferPool(65536));
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter(mainHandler, this.player);
        boolean hasContentProtection = false;
        int videoAdaptationSetIndex = period.getAdaptationSetIndex(0);
        int audioAdaptationSetIndex = period.getAdaptationSetIndex(1);
        AdaptationSet videoAdaptationSet = null;
        AdaptationSet audioAdaptationSet = null;
        if (videoAdaptationSetIndex != -1) {
            videoAdaptationSet = (AdaptationSet) period.adaptationSets.get(videoAdaptationSetIndex);
            hasContentProtection = false | videoAdaptationSet.hasContentProtection();
        }
        if (audioAdaptationSetIndex != -1) {
            audioAdaptationSet = (AdaptationSet) period.adaptationSets.get(audioAdaptationSetIndex);
            hasContentProtection |= audioAdaptationSet.hasContentProtection();
        }
        if (videoAdaptationSet == null && audioAdaptationSet == null) {
            this.callback.onRenderersError(new IllegalStateException("No video or audio adaptation sets"));
            return;
        }
        int i;
        Format format;
        MediaCodecVideoTrackRenderer videoRenderer;
        UriDataSource uriDataSource;
        TrackRenderer audioRenderer;
        TrackRenderer textRenderer;
        boolean filterHdContent = false;
        DrmSessionManager drmSessionManager = null;
        if (hasContentProtection) {
            if (Util.SDK_INT < 18) {
                this.callback.onRenderersError(new UnsupportedDrmException(0));
                return;
            }
            try {
                Pair<DrmSessionManager, Boolean> drmSessionManagerData = V18Compat.getDrmSessionManagerData(this.player, this.drmCallback);
                drmSessionManager = (DrmSessionManager) drmSessionManagerData.first;
                filterHdContent = (videoAdaptationSet == null || !videoAdaptationSet.hasContentProtection() || ((Boolean) drmSessionManagerData.second).booleanValue()) ? false : true;
            } catch (Exception e) {
                this.callback.onRenderersError(e);
                return;
            }
        }
        List<String> videoTrackNameList = new ArrayList();
        List<ChunkSource> videoChunkSourceList = new ArrayList();
        ArrayList<Integer> videoRepresentationIndexList = new ArrayList();
        if (videoAdaptationSet != null) {
            try {
                Display display = ((WindowManager) OdnoklassnikiApplication.getContext().getSystemService("window")).getDefaultDisplay();
                int maxDecodableFrameSize;
                if (display != null) {
                    int size = display.getWidth() * display.getHeight();
                    if (size > 0) {
                        maxDecodableFrameSize = size;
                    } else {
                        maxDecodableFrameSize = MediaCodecUtil.maxH264DecodableFrameSize();
                    }
                } else {
                    maxDecodableFrameSize = MediaCodecUtil.maxH264DecodableFrameSize();
                }
                List<Representation> videoRepresentations = videoAdaptationSet.representations;
                if (videoRepresentations.size() > 0) {
                    for (i = 0; i < videoRepresentations.size(); i++) {
                        format = ((Representation) videoRepresentations.get(i)).format;
                        if ((!filterHdContent || (format.width < 1280 && format.height < 720)) && format.width * format.height <= maxDecodableFrameSize && (format.mimeType.equals("video/mp4") || format.mimeType.equals("video/webm"))) {
                            videoRepresentationIndexList.add(Integer.valueOf(i));
                            videoTrackNameList.add(String.valueOf(format.height));
                            DataSource videoDataSource = new UriDataSource(this.userAgent, defaultBandwidthMeter);
                            videoChunkSourceList.add(new DashChunkSource(this.manifestFetcher, videoAdaptationSetIndex, new int[]{i}, videoDataSource, new AdaptiveEvaluator(defaultBandwidthMeter), 30000));
                        }
                    }
                    if (videoRepresentationIndexList.size() > 0) {
                        DataSource videoAutoDataSource = new UriDataSource(this.userAgent, defaultBandwidthMeter);
                        int[] videoRepresentationIndices = Util.toArray(videoRepresentationIndexList);
                        videoTrackNameList.add(0, "Auto");
                        videoChunkSourceList.add(0, new DashChunkSource(this.manifestFetcher, videoAdaptationSetIndex, videoRepresentationIndices, videoAutoDataSource, new AdaptiveEvaluator(defaultBandwidthMeter), 30000));
                    }
                }
            } catch (Exception e2) {
                this.callback.onRenderersError(e2);
                return;
            }
        }
        String[] videoTrackNames;
        if (videoRepresentationIndexList.isEmpty()) {
            videoRenderer = null;
            videoTrackNames = null;
            MultiTrackChunkSource videoChunkSource = null;
        } else {
            videoTrackNames = new String[videoTrackNameList.size()];
            videoTrackNameList.toArray(videoTrackNames);
            MultiTrackChunkSource multiTrackChunkSource = new MultiTrackChunkSource((List) videoChunkSourceList);
            ChunkSampleSource chunkSampleSource = new ChunkSampleSource(multiTrackChunkSource, defaultLoadControl, 13107200, true, mainHandler, this.player, 0);
            videoRenderer = new MediaCodecVideoTrackRenderer(videoSampleSource, drmSessionManager, true, 1, 5000, null, mainHandler, this.player, 50);
        }
        boolean haveAc3Tracks = false;
        List<ChunkSource> audioChunkSourceList = new ArrayList();
        List<String> audioTrackNameList = new ArrayList();
        if (audioAdaptationSet != null) {
            uriDataSource = new UriDataSource(this.userAgent, defaultBandwidthMeter);
            FormatEvaluator audioEvaluator = new FixedEvaluator();
            List<Representation> audioRepresentations = audioAdaptationSet.representations;
            for (i = 0; i < audioRepresentations.size(); i++) {
                int i2;
                format = ((Representation) audioRepresentations.get(i)).format;
                audioTrackNameList.add(format.id + " (" + format.numChannels + "ch, " + format.audioSamplingRate + "Hz)");
                audioChunkSourceList.add(new DashChunkSource(this.manifestFetcher, audioAdaptationSetIndex, new int[]{i}, uriDataSource, audioEvaluator, 30000));
                if ("ac-3".equals(format.codecs) || "ec-3".equals(format.codecs)) {
                    i2 = 1;
                } else {
                    i2 = 0;
                }
                haveAc3Tracks |= i2;
            }
            if (haveAc3Tracks) {
                for (i = audioRepresentations.size() - 1; i >= 0; i--) {
                    format = ((Representation) audioRepresentations.get(i)).format;
                    if (!("ac-3".equals(format.codecs) || "ec-3".equals(format.codecs))) {
                        audioTrackNameList.remove(i);
                        audioChunkSourceList.remove(i);
                    }
                }
            }
        }
        String[] audioTrackNames;
        if (audioChunkSourceList.isEmpty()) {
            audioTrackNames = null;
            MultiTrackChunkSource audioChunkSource = null;
            audioRenderer = null;
        } else {
            audioTrackNames = new String[audioTrackNameList.size()];
            audioTrackNameList.toArray(audioTrackNames);
            multiTrackChunkSource = new MultiTrackChunkSource((List) audioChunkSourceList);
            SampleSource chunkSampleSource2 = new ChunkSampleSource(multiTrackChunkSource, defaultLoadControl, 3932160, true, mainHandler, this.player, 1);
            audioRenderer = new MediaCodecAudioTrackRenderer(audioSampleSource, drmSessionManager, true, mainHandler, this.player);
        }
        uriDataSource = new UriDataSource(this.userAgent, defaultBandwidthMeter);
        FormatEvaluator textEvaluator = new FixedEvaluator();
        List<ChunkSource> textChunkSourceList = new ArrayList();
        List<String> textTrackNameList = new ArrayList();
        for (i = 0; i < period.adaptationSets.size(); i++) {
            AdaptationSet adaptationSet = (AdaptationSet) period.adaptationSets.get(i);
            if (adaptationSet.type == 2) {
                List<Representation> representations = adaptationSet.representations;
                for (int j = 0; j < representations.size(); j++) {
                    textTrackNameList.add(((Representation) representations.get(j)).format.id);
                    textChunkSourceList.add(new DashChunkSource(this.manifestFetcher, i, new int[]{j}, uriDataSource, textEvaluator, 30000));
                }
            }
        }
        String[] textTrackNames;
        if (textChunkSourceList.isEmpty()) {
            textTrackNames = null;
            MultiTrackChunkSource textChunkSource = null;
            textRenderer = null;
        } else {
            textTrackNames = new String[textTrackNameList.size()];
            textTrackNameList.toArray(textTrackNames);
            multiTrackChunkSource = new MultiTrackChunkSource((List) textChunkSourceList);
            TextTrackRenderer textTrackRenderer = new TextTrackRenderer(new ChunkSampleSource(multiTrackChunkSource, defaultLoadControl, 131072, true, mainHandler, this.player, 2), this.player, mainHandler.getLooper(), new SubtitleParser[]{new TtmlParser(), new WebvttParser()});
        }
        trackNames = new String[5][];
        multiTrackChunkSources = new MultiTrackChunkSource[5];
        TrackRenderer[] renderers = new TrackRenderer[5];
        renderers[0] = videoRenderer;
        renderers[1] = audioRenderer;
        renderers[2] = textRenderer;
        this.callback.onRenderers(trackNames, multiTrackChunkSources, renderers);
    }
}
