package ru.ok.android.ui.video.player.cast;

import ru.ok.java.api.response.video.VideoGetResponse;

public class MediaInfoException extends Exception {
    private VideoGetResponse response;

    public MediaInfoException(VideoGetResponse response) {
        super("Ex create MediaInfo");
        this.response = response;
    }
}
