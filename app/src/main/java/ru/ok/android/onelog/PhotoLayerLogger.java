package ru.ok.android.onelog;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.Logger;
import ru.ok.onelog.app.photo.PhotoLayerEventFactory;
import ru.ok.onelog.app.photo.PhotoLayerEventType;
import ru.ok.onelog.app.photo.PhotoLayerSourceType;

public final class PhotoLayerLogger implements PhotoLayerScrollLogger {
    private final PhotoLayerSourceType photoLayerSourceType;

    public static final class PageScrollLogListener extends SimpleOnPageChangeListener {
        private int currentPosition;
        private boolean isDragging;
        private final PhotoLayerScrollLogger scrollLogger;

        public PageScrollLogListener(@NonNull PhotoLayerScrollLogger scrollLogger) {
            this.currentPosition = -1;
            this.isDragging = false;
            this.scrollLogger = scrollLogger;
        }

        public void onPageSelected(int position) {
            if (this.isDragging && this.currentPosition != position) {
                this.scrollLogger.logScroll();
            }
            this.currentPosition = position;
        }

        public void onPageScrollStateChanged(int state) {
            if (state == 1) {
                this.isDragging = true;
            } else if (state == 0) {
                this.isDragging = false;
            }
        }
    }

    public PhotoLayerLogger(int sourceId) {
        this.photoLayerSourceType = toPhotoLayerSourceType(sourceId);
    }

    public void logOpen() {
        log(PhotoLayerEventType.open);
    }

    public void logClickLike() {
        log(PhotoLayerEventType.like);
    }

    public void logClickUnlike() {
        log(PhotoLayerEventType.unlike);
    }

    public void logClickComment() {
        log(PhotoLayerEventType.comment);
    }

    public void logClickSave() {
        log(PhotoLayerEventType.save);
    }

    public void logClickDelete() {
        log(PhotoLayerEventType.delete);
    }

    public void logClickCopyLink() {
        log(PhotoLayerEventType.copy_link);
    }

    public void logClickChangeDescription() {
        log(PhotoLayerEventType.change_description);
    }

    public void logScroll() {
        log(PhotoLayerEventType.scroll);
    }

    private void log(@NonNull PhotoLayerEventType eventType) {
        OneLog.log(PhotoLayerEventFactory.get(eventType, this.photoLayerSourceType));
    }

    @NonNull
    private static PhotoLayerSourceType toPhotoLayerSourceType(int sourceId) {
        switch (sourceId) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return PhotoLayerSourceType.stream_feed;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return PhotoLayerSourceType.photo_album;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return PhotoLayerSourceType.photo_feed;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return PhotoLayerSourceType.conversation;
            case Message.UUID_FIELD_NUMBER /*5*/:
                return PhotoLayerSourceType.discussion_comments;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return PhotoLayerSourceType.image_upload;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                return PhotoLayerSourceType.user_profile;
            case Message.TASKID_FIELD_NUMBER /*8*/:
                return PhotoLayerSourceType.group_profile;
            default:
                Logger.m185w("Unknown type of photo layer source (%s)", Integer.valueOf(sourceId));
                return PhotoLayerSourceType.unknown;
        }
    }
}
