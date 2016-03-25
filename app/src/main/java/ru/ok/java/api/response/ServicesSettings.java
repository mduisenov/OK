package ru.ok.java.api.response;

import android.support.v4.app.FragmentTransaction;

public final class ServicesSettings {
    public final int audioAttachRecordingMaxDuration;
    public final long messageEditTimeoutMs;
    public final int multichatMaxParticipantsCount;
    public final int multichatMaxTextLength;
    public final int multichatMaxThemeLength;
    public final int uploadPhotoMaxHeight;
    public final int uploadPhotoMaxQuality;
    public final int uploadPhotoMaxWidth;
    public final int videoAttachRecordingMaxDuration;

    public ServicesSettings(int multichatMaxParticipantsCount, int multichatMaxTextLength, int multichatMaxThemeLength, int uploadPhotoMaxWidth, int uploadPhotoMaxHeight, int uploadPhotoMaxQuality, int audioAttachRecordingMaxDuration, int videoAttachRecordingMaxDuration, long messageEditTimeoutMs) {
        this.multichatMaxParticipantsCount = multichatMaxParticipantsCount;
        this.multichatMaxTextLength = multichatMaxTextLength;
        this.multichatMaxThemeLength = multichatMaxThemeLength;
        this.uploadPhotoMaxWidth = uploadPhotoMaxWidth;
        this.uploadPhotoMaxHeight = uploadPhotoMaxHeight;
        this.uploadPhotoMaxQuality = uploadPhotoMaxQuality;
        this.audioAttachRecordingMaxDuration = audioAttachRecordingMaxDuration;
        this.videoAttachRecordingMaxDuration = videoAttachRecordingMaxDuration;
        this.messageEditTimeoutMs = messageEditTimeoutMs;
    }

    public int getMultichatMaxParticipantsCount() {
        return (this.multichatMaxParticipantsCount != 0 ? this.multichatMaxParticipantsCount : 20) - 1;
    }

    public int getMultichatMaxTextLength() {
        return this.multichatMaxTextLength != 0 ? this.multichatMaxTextLength : FragmentTransaction.TRANSIT_ENTER_MASK;
    }

    public int getMultichatMaxThemeLength() {
        return this.multichatMaxThemeLength != 0 ? this.multichatMaxThemeLength : 200;
    }

    public int getUploadPhotoMaxWidth() {
        return this.uploadPhotoMaxWidth != 0 ? this.uploadPhotoMaxWidth : 1024;
    }

    public int getUploadPhotoMaxHeight() {
        return this.uploadPhotoMaxHeight != 0 ? this.uploadPhotoMaxHeight : 768;
    }

    public int getUploadPhotoMaxQuality() {
        return this.uploadPhotoMaxQuality != 0 ? this.uploadPhotoMaxQuality : 80;
    }

    public int getVideoAttachRecordingMaxDuration() {
        return this.videoAttachRecordingMaxDuration != 0 ? this.videoAttachRecordingMaxDuration : 180;
    }

    public int getAudioAttachRecordingMaxDuration() {
        return this.audioAttachRecordingMaxDuration != 0 ? this.audioAttachRecordingMaxDuration : 180;
    }

    public long getMessageEditTimeoutMs() {
        return this.messageEditTimeoutMs != 0 ? this.messageEditTimeoutMs : 3600000;
    }
}
