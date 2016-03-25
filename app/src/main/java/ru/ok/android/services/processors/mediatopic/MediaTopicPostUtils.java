package ru.ok.android.services.processors.mediatopic;

import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.custom.mediacomposer.EditablePhotoItem;
import ru.ok.android.ui.custom.mediacomposer.MediaTopicMessage;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.request.mediatopic.MediaTopicPostRequest;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.photo.PhotoAlbumInfo;

public final class MediaTopicPostUtils {

    /* renamed from: ru.ok.android.services.processors.mediatopic.MediaTopicPostUtils.1 */
    static /* synthetic */ class C04681 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$java$api$request$mediatopic$MediaTopicType;

        static {
            $SwitchMap$ru$ok$java$api$request$mediatopic$MediaTopicType = new int[MediaTopicType.values().length];
            try {
                $SwitchMap$ru$ok$java$api$request$mediatopic$MediaTopicType[MediaTopicType.USER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$java$api$request$mediatopic$MediaTopicType[MediaTopicType.GROUP_THEME.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$java$api$request$mediatopic$MediaTopicType[MediaTopicType.GROUP_SUGGESTED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    static ArrayList<ImageEditInfo> toImageEditInfos(List<EditablePhotoItem> editablePhotoItems, PhotoAlbumInfo album) {
        ArrayList<ImageEditInfo> imagesToEdit = new ArrayList(editablePhotoItems.size());
        for (EditablePhotoItem editableImage : editablePhotoItems) {
            ImageEditInfo imageToEdit = editableImage.getImageEditInfo();
            PhotoAlbumInfo imageToEditPhotoAlbumInfo = imageToEdit.getAlbumInfo();
            if (imageToEditPhotoAlbumInfo == null || !imageToEditPhotoAlbumInfo.equals(album)) {
                imageToEdit.setAlbumInfo(album);
            }
            imagesToEdit.add(imageToEdit);
        }
        return imagesToEdit;
    }

    static MediaTopicPostRequest prepareMediaTopicPost(MediaTopicMessage mediaTopicMessage, List<String> photoTokens, boolean toStatus, MediaTopicType mediaTopicType, String groupId) throws MediaTopicPostException {
        if (photoTokens == null) {
            photoTokens = Collections.emptyList();
        }
        if (mediaTopicType == null) {
            throw new IllegalArgumentException("Media topic type not specified");
        } else if ((mediaTopicType == MediaTopicType.GROUP_THEME || mediaTopicType == MediaTopicType.GROUP_SUGGESTED) && TextUtils.isEmpty(groupId)) {
            throw new IllegalArgumentException("group ID not specified");
        } else {
            JSONObject mediaPayload = MediaPayloadBuilder.buildMediaPayload(mediaTopicMessage, photoTokens);
            if (Logger.isLoggingEnable()) {
                try {
                    BufferedReader reader = new BufferedReader(new StringReader(mediaPayload.toString(4)));
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        Logger.m172d("payload:  " + line);
                    }
                } catch (Exception e) {
                }
            }
            switch (C04681.$SwitchMap$ru$ok$java$api$request$mediatopic$MediaTopicType[mediaTopicType.ordinal()]) {
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    return MediaTopicPostRequest.groupTheme(groupId, mediaPayload);
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    return MediaTopicPostRequest.groupThemeSuggested(groupId, mediaPayload);
                default:
                    return MediaTopicPostRequest.user(mediaPayload, toStatus);
            }
        }
    }

    static String postMediaTopic(JsonSessionTransportProvider transportProvider, MediaTopicMessage mediaTopicMessage, List<String> photoTokens, boolean toStatus, MediaTopicType mediaTopicType, String groupId) throws MediaTopicPostException {
        Throwable e;
        Logger.m173d("postMediaTopic >>> %s, %s", mediaTopicMessage.toString(), "" + photoTokens);
        try {
            Logger.m173d("postMediaTopic <<< %s", transportProvider.execJsonHttpMethod(prepareMediaTopicPost(mediaTopicMessage, photoTokens, toStatus, mediaTopicType, groupId)).getResultAsString());
            return transportProvider.execJsonHttpMethod(prepareMediaTopicPost(mediaTopicMessage, photoTokens, toStatus, mediaTopicType, groupId)).getResultAsString();
        } catch (Throwable e2) {
            if (NetUtils.isConnectionAvailable(OdnoklassnikiApplication.getContext(), true)) {
                throw new MediaTopicPostException(12, e2);
            }
            throw new MediaTopicPostException(1, e2);
        } catch (Throwable e22) {
            throw new MediaTopicPostException(4, e22);
        } catch (BaseApiException e3) {
            e22 = e3;
            throw new MediaTopicPostException(12, e22);
        } catch (JSONException e4) {
            e22 = e4;
            throw new MediaTopicPostException(12, e22);
        } catch (Throwable e222) {
            throw new MediaTopicPostException(999, e222);
        }
    }

    public static int getMediaTopicIsCompletedTextResId(MediaTopicType type) {
        switch (C04681.$SwitchMap$ru$ok$java$api$request$mediatopic$MediaTopicType[type.ordinal()]) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return 2131166130;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return 2131166131;
            default:
                return 2131166132;
        }
    }
}
