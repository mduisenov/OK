package ru.ok.android.utils;

import android.content.Context;
import java.io.File;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.photo.upload.ImageUploadException;
import ru.ok.android.utils.Storage.External.Application;

public final class Constants {

    public static final class Image {
        public static File getUploaderChacheDir(Context context) {
            return new File(Application.getCacheDir(context), "imgupldr");
        }

        public static final int getStringResForUpldError(ImageUploadException imageUploadException) {
            if (imageUploadException == null) {
                return 2131165841;
            }
            return getStringResForUpldError(imageUploadException.getPhase(), imageUploadException.getErrorCode(), imageUploadException.getServerErrorCode());
        }

        public static final int getStringResForUpldError(int phase, int errorCode, int serverErrorCode) {
            if (phase == 1) {
                return 2131165814;
            }
            if (errorCode == 4) {
                switch (serverErrorCode) {
                    case Message.ATTACHES_FIELD_NUMBER /*7*/:
                        return 2131165807;
                    case 454:
                        return 2131165808;
                    case 500:
                        return 2131165818;
                    case 501:
                        return 2131165819;
                    case 502:
                        return 2131165817;
                    case 503:
                        return 2131165811;
                    case 504:
                        return 2131165810;
                    case 505:
                        return 2131165813;
                    default:
                        return 2131165815;
                }
            } else if (errorCode == 14) {
                return 2131165816;
            } else {
                if (errorCode == 1) {
                    return 2131165812;
                }
                if (phase == 4) {
                    return 2131165809;
                }
                return 2131165841;
            }
        }
    }
}
