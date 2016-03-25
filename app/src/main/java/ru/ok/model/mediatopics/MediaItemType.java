package ru.ok.model.mediatopics;

import org.jivesoftware.smack.packet.Stanza;

public enum MediaItemType {
    TEXT(Stanza.TEXT),
    PHOTO("photo"),
    MUSIC("music"),
    POLL("poll"),
    LINK("link"),
    VIDEO("movie"),
    PHOTO_BLOCK(null),
    PLACE("place"),
    TOPIC("topic"),
    APP("app"),
    STUB("stub");
    
    private final String apiName;

    private MediaItemType(String apiName) {
        this.apiName = apiName;
    }

    public String getApiName() {
        return this.apiName;
    }
}
