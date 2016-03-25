package ru.ok.android.services.processors.image.upload;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.http.client.methods.HttpUriRequest;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.services.processors.photo.upload.ImageUploadException;
import ru.ok.android.services.processors.photo.upload.ImageUploader;
import ru.ok.android.services.processors.photo.upload.ImageUploader.PhotoCommitResponse;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.JsonTransportProvider;
import ru.ok.android.services.transport.TransportUtils;
import ru.ok.android.services.transport.exception.TransportLevelException;
import ru.ok.android.utils.BitmapRender;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.MimeTypes;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.HttpSessionCreateException;
import ru.ok.java.api.exceptions.HttpStatusException;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.exceptions.ServerReturnErrorException;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.SettingsGetRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.image.CommitSingleImageRequest;
import ru.ok.java.api.request.image.GetImageUploadUrlRequest;
import ru.ok.java.api.request.image.UploadSingleImageProgressRequest;
import ru.ok.java.api.request.image.UploadSingleImageProgressRequest.UploadProgressListener;
import ru.ok.java.api.request.image.UploadSingleImageRequest;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.response.ServicesSettings;
import ru.ok.model.photo.PhotoAlbumInfo;

public final class ImageUploadMethods {

    public static class GetUrlResult {
        public final String uploadId;
        public final String uploadUrl;

        GetUrlResult(String uploadUrl, String uploadId) {
            this.uploadUrl = uploadUrl;
            this.uploadId = uploadId;
        }

        public boolean equals(Object o) {
            if (!(o instanceof GetUrlResult)) {
                return false;
            }
            GetUrlResult other = (GetUrlResult) o;
            if (TextUtils.equals(this.uploadUrl, other.uploadUrl) && TextUtils.equals(this.uploadId, other.uploadId)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = this.uploadUrl == null ? 0 : this.uploadUrl.hashCode();
            if (this.uploadId != null) {
                i = this.uploadId.hashCode();
            }
            return hashCode + i;
        }

        public String toString() {
            return "GetUrlResult[uploadUrl=" + this.uploadUrl + " uploadId" + this.uploadId + "]";
        }
    }

    static class UploadImageHandle {
        final File file;
        volatile HttpUriRequest httpRequest;
        final InputStream in;
        final UploadSingleImageRequest request;
        final String uploadId;

        UploadImageHandle(File file, InputStream in, UploadSingleImageRequest request, String uploadId) {
            this.file = file;
            this.in = in;
            this.request = request;
            this.uploadId = uploadId;
        }
    }

    public static void prepareImageToFile(Context context, ImageEditInfo editedImage, File destFile) throws ImageUploadException {
        try {
            if (MimeTypes.isGif(editedImage.getMimeType())) {
                IOUtils.copyStreams(new FileOutputStream(destFile), context.getContentResolver().openInputStream(editedImage.getUri()));
            } else if (!justCopyBitmapFile(context.getContentResolver(), editedImage, destFile)) {
                resizeOnFly(context, editedImage, new FileOutputStream(destFile));
            }
        } catch (Throwable e) {
            Logger.m176e("Failed to save image to file: " + e);
            Logger.m178e(e);
            ImageUploadException convertException = convertException(e, 1, editedImage.getUri(), null, destFile);
        }
    }

    private static boolean justCopyBitmapFile(ContentResolver cr, ImageEditInfo editedImage, File destFile) {
        if (editedImage.getRotation() != 0) {
            return false;
        }
        Options options = BitmapRender.getBitmapInfo(cr, editedImage.getUri()).options;
        ServicesSettings settings = ServicesSettingsHelper.getServicesSettings();
        int maxWidth = settings.getUploadPhotoMaxWidth();
        int maxHeight = settings.getUploadPhotoMaxHeight();
        if ((options.outWidth > maxWidth || options.outHeight > maxHeight) && (options.outWidth > maxHeight || options.outHeight > maxWidth)) {
            return false;
        }
        try {
            IOUtils.copyStreams(new FileOutputStream(destFile), cr.openInputStream(editedImage.getUri()));
            return true;
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to copy bitmap");
            return false;
        }
    }

    public static byte[] prepareImageToBytes(Context context, ImageEditInfo editedImage) throws ImageUploadException {
        try {
            if (MimeTypes.isGif(editedImage.getMimeType())) {
                return IOUtils.toByteArray(context.getContentResolver().openInputStream(editedImage.getUri()));
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizeOnFly(context, editedImage, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Throwable e) {
            Logger.m176e("Failed to prepare image: " + e);
            Logger.m178e(e);
            ImageUploadException convertException = convertException(e, 1, editedImage.getUri(), null, null);
        }
    }

    public static GetUrlResult getUrlWithMaxQualitySettings(String groupId, JsonSessionTransportProvider transportProvider) throws ImageUploadException {
        return getUrlWithMaxQualitySettings(null, groupId, transportProvider);
    }

    public static GetUrlResult getUrlWithMaxQualitySettings(PhotoAlbumInfo photoAlbumInfo, JsonSessionTransportProvider transportProvider) throws ImageUploadException {
        return getUrlWithMaxQualitySettings(photoAlbumInfo, null, transportProvider);
    }

    private static GetUrlResult getUrlWithMaxQualitySettings(@Nullable PhotoAlbumInfo photoAlbumInfo, @Nullable String groupId, @NonNull JsonSessionTransportProvider transportProvider) throws ImageUploadException {
        String gAlbumId;
        String albumId = null;
        if (photoAlbumInfo != null) {
            albumId = photoAlbumInfo.getId();
            gAlbumId = photoAlbumInfo.getGroupId();
        } else {
            gAlbumId = groupId;
        }
        BaseRequest getUploadUrlRequest = new GetImageUploadUrlRequest(albumId, gAlbumId, 1);
        return executeBatchRequestToGetUrlAndSettings(transportProvider, new BatchRequest(new BatchRequests().addRequest(getUploadUrlRequest).addRequest(new SettingsGetRequest("photo.max.quality", 182))));
    }

    @NonNull
    private static GetUrlResult executeBatchRequestToGetUrlAndSettings(@NonNull JsonSessionTransportProvider transportProvider, @NonNull BaseRequest batchRequest) throws ImageUploadException {
        try {
            JSONObject jsonResponse = transportProvider.execJsonHttpMethod(batchRequest).getResultAsObject();
            JSONObject getImageUploadUrlResponse = jsonResponse.getJSONObject("photosV2_getUploadUrl_response");
            handleGetUploadSettingsResponse(jsonResponse.getJSONObject("settings_get_response"));
            return handleGetImageUploadUrlResponse(getImageUploadUrlResponse);
        } catch (Throwable e) {
            Logger.m176e("GetUrl failed: " + e);
            Logger.m178e(e);
            throw convertException(e, 2, null, null, null);
        }
    }

    @NonNull
    private static GetUrlResult handleGetImageUploadUrlResponse(@NonNull JSONObject getImageUploadUrlResponse) throws JSONException, ImageUploadException {
        JSONArray imageIds = getImageUploadUrlResponse.getJSONArray("photo_ids");
        String uploadUrl = getImageUploadUrlResponse.getString("upload_url");
        String uploadId = (String) imageIds.get(0);
        if (TextUtils.isEmpty(uploadUrl)) {
            throw new ImageUploadException(2, 14, "Failed to received a non-empty upload URL from server");
        } else if (!TextUtils.isEmpty(uploadId)) {
            return new GetUrlResult(uploadUrl, uploadId);
        } else {
            throw new ImageUploadException(2, 14, "Failed to received a non-empty upload ID from server");
        }
    }

    private static void handleGetUploadSettingsResponse(@NonNull JSONObject getUploadSettingsResponse) throws JSONException {
        String photoMaxQuality = getUploadSettingsResponse.optString("photo.max.quality");
        if (!TextUtils.isEmpty(photoMaxQuality)) {
            parseAndSaveUploadPhotoMaxQuality(photoMaxQuality);
        }
    }

    private static void parseAndSaveUploadPhotoMaxQuality(@NonNull String photoMaxQuality) {
        boolean allCorrect = false;
        if (!TextUtils.isEmpty(photoMaxQuality)) {
            String[] chunks = photoMaxQuality.split(",");
            if (chunks.length == 3) {
                try {
                    saveUploadPhotoSettings(Integer.parseInt(chunks[0]), Integer.parseInt(chunks[1]), Integer.parseInt(chunks[2]));
                    allCorrect = true;
                } catch (NumberFormatException e) {
                }
            }
        }
        if (!allCorrect) {
            Logger.m185w("Invalid format: %s=%s", "photo.max.quality", photoMaxQuality);
        }
    }

    private static void saveUploadPhotoSettings(int maxWidth, int maxHeight, int maxCompressQuality) {
        Editor editor = ServicesSettingsHelper.getPreferences(OdnoklassnikiApplication.getContext()).edit();
        editor.putInt("upload.photo.max.width", maxWidth);
        editor.putInt("upload.photo.max.height", maxHeight);
        editor.putInt("upload.photo.max.quality", maxCompressQuality);
        editor.apply();
    }

    public static String uploadImage(UploadImageHandle handle, JsonTransportProvider transportProvider) throws ImageUploadException {
        try {
            String uploadImage_Impl = uploadImage_Impl(handle, transportProvider);
            IOUtils.closeSilently(handle.in);
            return uploadImage_Impl;
        } catch (Throwable e) {
            Logger.m176e("upload image failed: " + e);
            Logger.m178e(e);
            throw convertException(e, 3, null, handle.file, null);
        } catch (Throwable th) {
            IOUtils.closeSilently(handle.in);
        }
    }

    public static UploadImageHandle createUploadImageRequest(String uploadUrl, File file, String uploadId, UploadProgressListener progressListener) throws FileNotFoundException {
        UploadSingleImageRequest request;
        FileInputStream in = new FileInputStream(file);
        long fileSize = file.length();
        if (progressListener == null) {
            request = new UploadSingleImageRequest(uploadUrl, in, fileSize);
        } else {
            request = new UploadSingleImageProgressRequest(uploadUrl, in, fileSize, progressListener);
        }
        return new UploadImageHandle(file, in, request, uploadId);
    }

    public static UploadImageHandle createUploadImageRequest(String uploadUrl, byte[] data, String uploadId, UploadProgressListener progressListener) {
        UploadSingleImageRequest request;
        InputStream in = new ByteArrayInputStream(data);
        if (progressListener == null) {
            request = new UploadSingleImageRequest(uploadUrl, in, (long) data.length);
        } else {
            request = new UploadSingleImageProgressRequest(uploadUrl, in, (long) data.length, progressListener);
        }
        return new UploadImageHandle(null, in, request, uploadId);
    }

    public static String uploadImage(String uploadUrl, String uploadId, byte[] imageData, JsonTransportProvider transportProvider) throws ImageUploadException {
        try {
            return uploadImage_Impl(createUploadImageRequest(uploadUrl, imageData, uploadId, null), transportProvider);
        } catch (Throwable e) {
            Logger.m176e("upload image failed: " + e);
            Logger.m178e(e);
            throw convertException(e, 3, null, null, null);
        }
    }

    public static PhotoCommitResponse commit(String uploadId, String token, String comment, JsonSessionTransportProvider transportProvider) throws ImageUploadException {
        try {
            List<PhotoCommitResponse> response = ImageUploader.parseCommitResponse(transportProvider.execJsonHttpMethod(new CommitSingleImageRequest(uploadId, token, comment)));
            if (response != null && response.size() != 0 && ((PhotoCommitResponse) response.get(0)).isSuccessful && !TextUtils.isEmpty(((PhotoCommitResponse) response.get(0)).assignedPhotoId)) {
                return (PhotoCommitResponse) response.get(0);
            }
            throw new ImageUploadException(4, 14);
        } catch (Throwable e) {
            throw convertException(e, 4, null, null, null);
        }
    }

    static ImageUploadException convertException(Throwable originalException, int phase, Uri srcUri, File srcFile, File destFile) {
        if (originalException instanceof IOException) {
            return convertException((IOException) originalException, phase, srcUri, srcFile, destFile);
        }
        int errorCode;
        if (originalException instanceof TransportLevelException) {
            if (NetUtils.isConnectionAvailable(OdnoklassnikiApplication.getContext(), true)) {
                errorCode = 14;
            } else {
                errorCode = 1;
            }
        } else if ((originalException instanceof HttpSessionCreateException) || (originalException instanceof HttpStatusException) || (originalException instanceof SerializeException) || (originalException instanceof JSONException) || (originalException instanceof ResultParsingException)) {
            errorCode = 14;
        } else if (originalException instanceof ServerReturnErrorException) {
            errorCode = 4;
        } else if (originalException instanceof OutOfMemoryError) {
            errorCode = 16;
        } else if (originalException instanceof Error) {
            throw ((Error) originalException);
        } else {
            errorCode = 999;
        }
        return new ImageUploadException(phase, errorCode, originalException);
    }

    private static ImageUploadException convertException(IOException e, int phase, Uri srcUri, File srcFile, File destFile) {
        int errorCode;
        String extStorageState = Environment.getExternalStorageState();
        boolean isWritable = "mounted".equals(extStorageState);
        boolean isReadable = isWritable || "mounted_ro".equals(extStorageState);
        if ((isWritable || destFile == null || !FileUtils.isExternalStoragePath(destFile)) && (isReadable || ((srcFile == null || !FileUtils.isExternalStoragePath(srcFile)) && (srcUri == null || !isOnExternalStorage(srcUri))))) {
            errorCode = 15;
        } else {
            errorCode = 2;
        }
        return new ImageUploadException(phase, errorCode, (Throwable) e);
    }

    private static String uploadImage_Impl(UploadImageHandle uploadImageHandle, JsonTransportProvider transportProvider) throws BaseApiException, FileNotFoundException {
        uploadImageHandle.httpRequest = uploadImageHandle.request.createHttpRequest();
        TransportUtils.addGeneralHeaders(uploadImageHandle.httpRequest);
        try {
            JsonHttpResult result = transportProvider.execJsonHttpMethod(uploadImageHandle.httpRequest);
            try {
                return result.getResultAsObject().getJSONObject("photos").getJSONObject(uploadImageHandle.uploadId).getString("token");
            } catch (JSONException e) {
                throw new ResultParsingException(e);
            }
        } finally {
            uploadImageHandle.httpRequest = null;
        }
    }

    private static boolean isOnExternalStorage(Uri uri) {
        if (uri == null) {
            return false;
        }
        String scheme = uri.getScheme();
        String ssp = uri.getSchemeSpecificPart();
        if ("content".equals(scheme)) {
            if (ssp == null || !ssp.startsWith("//media/external")) {
                return false;
            }
            return true;
        } else if (!"file".equals(scheme) || ssp.length() < 2) {
            return false;
        } else {
            return FileUtils.isExternalStoragePath(ssp.substring(2));
        }
    }

    static void resizeOnFly(@NonNull Context context, @NonNull ImageEditInfo editInfo, @NonNull OutputStream out) throws IOException {
        BitmapRender.resizeOnFly(context.getContentResolver(), editInfo.getUri(), out, new ResizeSettings(editInfo.getRotation()));
    }
}
