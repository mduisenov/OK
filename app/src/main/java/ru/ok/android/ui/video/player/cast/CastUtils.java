package ru.ok.android.ui.video.player.cast;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaInfo.Builder;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.common.images.WebImage;
import com.google.android.libraries.cast.companionlibrary.utils.Utils;
import java.util.Iterator;
import java.util.List;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.video.player.PlayerUtil;
import ru.ok.android.ui.video.player.quality.VideoQuality;
import ru.ok.java.api.response.video.VideoGetResponse;
import ru.ok.model.photo.PhotoSize;

public final class CastUtils {
    public static MediaInfo responseToMediaInfo(@NonNull VideoGetResponse videoGetResponse) throws MediaInfoException {
        MediaMetadata metaData = new MediaMetadata(1);
        metaData.putString("com.google.android.gms.cast.metadata.TITLE", videoGetResponse.title);
        if (videoGetResponse.thumbnails != null) {
            Iterator i$ = videoGetResponse.thumbnails.iterator();
            while (i$.hasNext()) {
                metaData.addImage(new WebImage(Uri.parse(((PhotoSize) i$.next()).getUrl())));
            }
        }
        if (videoGetResponse.isLiveStreamResponse()) {
            String contentType = "application/x-mpegurl";
            if (!TextUtils.isEmpty(videoGetResponse.urlLiveHls)) {
                return new Builder(videoGetResponse.urlLiveHls).setContentType("application/x-mpegurl").setStreamType(2).setStreamDuration((long) videoGetResponse.duration).setMetadata(metaData).build();
            }
            throw new MediaInfoException(videoGetResponse);
        } else if (videoGetResponse.duration > 20000) {
            List<VideoQuality> videoQualityList = PlayerUtil.collectionVideoUrls(videoGetResponse);
            if (videoQualityList.size() > 0) {
                VideoQuality quality = (VideoQuality) videoQualityList.get(0);
                return new Builder(quality.getUrl()).setContentType(getContentType(quality)).setStreamType(1).setStreamDuration((long) videoGetResponse.duration).setMetadata(metaData).build();
            }
            throw new MediaInfoException(videoGetResponse);
        } else {
            String mp4Url = getAvailableUrl(videoGetResponse);
            if (!TextUtils.isEmpty(mp4Url)) {
                return new Builder(mp4Url).setContentType("video/mp4").setStreamType(1).setStreamDuration((long) videoGetResponse.duration).setMetadata(metaData).build();
            }
            throw new MediaInfoException(videoGetResponse);
        }
    }

    @NonNull
    private static String getContentType(@NonNull VideoQuality videoQuality) {
        switch (videoQuality.getType()) {
            case RECEIVED_VALUE:
                return "application/dash+xml";
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return "application/vnd.ms-sstr+xml";
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return "application/x-mpegurl";
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return "application/x-mpegurl";
            default:
                return "video/mp4";
        }
    }

    @Nullable
    private static String getAvailableUrl(@NonNull VideoGetResponse videoGetResponse) {
        if (!TextUtils.isEmpty(videoGetResponse.url480p)) {
            return videoGetResponse.url480p;
        }
        if (!TextUtils.isEmpty(videoGetResponse.url360p)) {
            return videoGetResponse.url360p;
        }
        if (!TextUtils.isEmpty(videoGetResponse.url720p)) {
            return videoGetResponse.url720p;
        }
        if (!TextUtils.isEmpty(videoGetResponse.url2160p)) {
            return videoGetResponse.url2160p;
        }
        if (!TextUtils.isEmpty(videoGetResponse.url1440p)) {
            return videoGetResponse.url1440p;
        }
        if (!TextUtils.isEmpty(videoGetResponse.url1080p)) {
            return videoGetResponse.url1080p;
        }
        if (!TextUtils.isEmpty(videoGetResponse.url240p)) {
            return videoGetResponse.url240p;
        }
        if (TextUtils.isEmpty(videoGetResponse.url144p)) {
            return null;
        }
        return videoGetResponse.url144p;
    }

    public static Bundle getExtraFromMediaInfo(MediaInfo mediaInfo, int position, boolean shouldStart) {
        Bundle outExtra = new Bundle();
        outExtra.putBundle("media", Utils.mediaInfoToBundle(mediaInfo));
        outExtra.putInt("startPoint", position);
        outExtra.putBoolean("shouldStart", shouldStart);
        return outExtra;
    }
}
