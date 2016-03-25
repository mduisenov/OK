package ru.ok.android.ui.video.player;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.ui.video.player.quality.VideoQuality;
import ru.ok.java.api.response.video.VideoGetResponse;

public class PlayerUtil {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final UUID WIDEVINE_UUID;

    static {
        $assertionsDisabled = !PlayerUtil.class.desiredAssertionStatus();
        WIDEVINE_UUID = new UUID(-1301668207276963122L, -6645017420763422227L);
    }

    public static String getUserAgent(Context context) {
        String versionName;
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            versionName = "?";
        }
        return "ExoPlayerOk/" + versionName + " (Linux;Android " + VERSION.RELEASE + ") " + "ExoPlayerLib/" + "1.2.4";
    }

    public static byte[] executePost(String url, byte[] data, Map<String, String> requestProperties) throws MalformedURLException, IOException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(data != null);
            urlConnection.setDoInput(true);
            if (requestProperties != null) {
                for (Entry<String, String> requestProperty : requestProperties.entrySet()) {
                    urlConnection.setRequestProperty((String) requestProperty.getKey(), (String) requestProperty.getValue());
                }
            }
            if (data != null) {
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                out.write(data);
                out.close();
            }
            byte[] convertInputStreamToByteArray = convertInputStreamToByteArray(new BufferedInputStream(urlConnection.getInputStream()));
            return convertInputStreamToByteArray;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private static byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        while (true) {
            int count = inputStream.read(data);
            if (count != -1) {
                bos.write(data, 0, count);
            } else {
                bos.flush();
                bos.close();
                inputStream.close();
                return bos.toByteArray();
            }
        }
    }

    public static ArrayList<VideoQuality> collectionVideoUrls(VideoGetResponse info) {
        ArrayList<VideoQuality> result = new ArrayList();
        addIfNotEmpty(result, info.urlDash, 2131166829, 0);
        addIfNotEmpty(result, info.urlHls, 2131166830, 3);
        addIfNotEmpty(result, info.urlLiveHls, 2131166830, 4);
        addIfNotEmpty(result, info.url2160p, 2131166824, 2);
        addIfNotEmpty(result, info.url1440p, 2131166823, 2);
        addIfNotEmpty(result, info.url1080p, 2131166821, 2);
        addIfNotEmpty(result, info.url720p, 2131166828, 2);
        addIfNotEmpty(result, info.url480p, 2131166827, 2);
        addIfNotEmpty(result, info.url360p, 2131166826, 2);
        addIfNotEmpty(result, info.url144p, 2131166822, 2);
        return result;
    }

    private static void addIfNotEmpty(List<VideoQuality> list, String url, int nameResId, int type) {
        if (!TextUtils.isEmpty(url)) {
            list.add(new VideoQuality(nameResId, url, type));
        }
    }

    public static Quality getDashQualityFromString(String qualityString) {
        Object obj = -1;
        switch (qualityString.hashCode()) {
            case 48753:
                if (qualityString.equals("144")) {
                    obj = null;
                    break;
                }
                break;
            case 49710:
                if (qualityString.equals("240")) {
                    obj = 1;
                    break;
                }
                break;
            case 50733:
                if (qualityString.equals("360")) {
                    obj = 2;
                    break;
                }
                break;
            case 51756:
                if (qualityString.equals("480")) {
                    obj = 3;
                    break;
                }
                break;
            case 54453:
                if (qualityString.equals("720")) {
                    obj = 4;
                    break;
                }
                break;
            case 1507671:
                if (qualityString.equals("1080")) {
                    obj = 5;
                    break;
                }
                break;
            case 1511391:
                if (qualityString.equals("1440")) {
                    obj = 6;
                    break;
                }
                break;
            case 1538361:
                if (qualityString.equals("2160")) {
                    obj = 7;
                    break;
                }
                break;
            case 2052559:
                if (qualityString.equals("Auto")) {
                    obj = 8;
                    break;
                }
                break;
        }
        switch (obj) {
            case RECEIVED_VALUE:
                return Quality._144p;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return Quality._240p;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return Quality._360p;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return Quality._480p;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return Quality._720p;
            case Message.UUID_FIELD_NUMBER /*5*/:
                return Quality._1080p;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return Quality._1440p;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                return Quality._2160p;
            case Message.TASKID_FIELD_NUMBER /*8*/:
                return Quality.Auto;
            default:
                return null;
        }
    }

    public static boolean videoLiked(BaseActivity activity, BusEvent event, VideoGetResponse currentResponse) {
        Bundle bundleInput = event.bundleInput;
        if (bundleInput == null) {
            return false;
        }
        String lid = bundleInput.getString("like_id");
        if (currentResponse == null || currentResponse.likeSummary == null || TextUtils.isEmpty(lid) || !currentResponse.likeSummary.getLikeId().equals(lid)) {
            return false;
        }
        if (event.resultCode == -2) {
            ErrorType error = ErrorType.from(event.bundleOutput);
            int errorRes = 2131165791;
            if (error != null) {
                errorRes = error.getDefaultErrorMessage();
            }
            TimeToast.show((Context) activity, activity.getStringLocalized(errorRes), 0);
            return false;
        }
        TimeToast.show((Context) activity, activity.getStringLocalized(2131166038), 0);
        return true;
    }

    public static boolean videoUnlike(BaseActivity activity, BusEvent event, VideoGetResponse currentResponse) {
        if (event.resultCode != -2) {
            return true;
        }
        ErrorType error = ErrorType.from(event.bundleOutput);
        int errorRes = 2131165791;
        if (error != null) {
            errorRes = error.getDefaultErrorMessage();
        }
        TimeToast.show((Context) activity, activity.getStringLocalized(errorRes), 0);
        return false;
    }
}
