package ru.ok.android.services.processors.mediatopic;

import android.text.TextUtils;
import com.google.android.gms.plus.PlusShare;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.packet.Stanza;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.mediacomposer.LinkItem;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaTopicMessage;
import ru.ok.android.ui.custom.mediacomposer.MusicItem;
import ru.ok.android.ui.custom.mediacomposer.PhotoBlockItem;
import ru.ok.android.ui.custom.mediacomposer.PollItem;
import ru.ok.android.ui.custom.mediacomposer.TextItem;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.geo.HttpValidatePlaceRequest;
import ru.ok.model.mediatopics.MediaItemType;
import ru.ok.model.places.Place;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.Track;

public final class MediaPayloadBuilder {
    private boolean isFinished;
    private MediaItemType lastItemType;
    private JSONArray mediaPayload;
    private final Iterator<String> photoTokens;
    private Place place;
    private List<String> withFriendsUids;

    /* renamed from: ru.ok.android.services.processors.mediatopic.MediaPayloadBuilder.1 */
    static /* synthetic */ class C04641 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$mediatopics$MediaItemType;

        static {
            $SwitchMap$ru$ok$model$mediatopics$MediaItemType = new int[MediaItemType.values().length];
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.TEXT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.LINK.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.PHOTO.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.MUSIC.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.POLL.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public static int getBlockCount(MediaTopicMessage mediaTopic) {
        int blockCount = 0;
        MediaItemType lastType = null;
        int itemsCount = mediaTopic == null ? 0 : mediaTopic.getItemsCount();
        for (int i = 0; i < itemsCount; i++) {
            MediaItemType type = mediaTopic.getItem(i).type;
            if (!((lastType == MediaItemType.PHOTO && (type == MediaItemType.PHOTO || type == MediaItemType.PHOTO_BLOCK)) || (lastType == MediaItemType.MUSIC && type == MediaItemType.MUSIC))) {
                blockCount++;
            }
            lastType = type;
            if (lastType == MediaItemType.PHOTO_BLOCK) {
                lastType = MediaItemType.PHOTO;
            }
        }
        return blockCount;
    }

    public static JSONObject buildMediaPayload(MediaTopicMessage mediaTopicMessage, List<String> photoTokens) throws MediaTopicPostException {
        int count = mediaTopicMessage == null ? 0 : mediaTopicMessage.getItemsCount();
        MediaPayloadBuilder builder = new MediaPayloadBuilder(photoTokens);
        for (int i = 0; i < count; i++) {
            MediaItem item = mediaTopicMessage.getItem(i);
            if (!item.isEmpty()) {
                if (item.type == MediaItemType.PHOTO_BLOCK) {
                    PhotoBlockItem photoBlock = (PhotoBlockItem) item;
                    int size = photoBlock.size();
                    for (int j = 0; j < size; j++) {
                        builder.add(photoBlock.getPhotoItem(j));
                    }
                } else {
                    builder.add(mediaTopicMessage.getItem(i));
                }
            }
        }
        builder.withFriends(mediaTopicMessage.getWithFriendsUids());
        builder.withPlace(mediaTopicMessage.getWithPlace());
        return builder.build();
    }

    private MediaPayloadBuilder(List<String> photoTokens) {
        Iterator it = null;
        this.lastItemType = null;
        this.mediaPayload = new JSONArray();
        this.withFriendsUids = null;
        this.isFinished = false;
        if (photoTokens != null) {
            it = photoTokens.iterator();
        }
        this.photoTokens = it;
    }

    private JSONObject build() throws MediaTopicPostException {
        this.isFinished = true;
        JSONObject media = new JSONObject();
        try {
            media.put("media", this.mediaPayload);
            if (!(this.withFriendsUids == null || this.withFriendsUids.isEmpty())) {
                JSONArray uids = new JSONArray();
                for (String uid : this.withFriendsUids) {
                    uids.put(uid);
                }
                media.put("with_friends", uids);
            }
            addPlaceToMedia(media, this.place);
            return media;
        } catch (Throwable e) {
            throw new MediaTopicPostException(10, e);
        }
    }

    private static void addPlaceToMedia(JSONObject media, Place place) throws JSONException {
        if (place == null) {
            return;
        }
        if (TextUtils.isEmpty(place.id)) {
            media.put("place", HttpValidatePlaceRequest.getPlaceObject(place.location.getLatitude(), place.location.getLongitude(), place.address.countryISO, place.address.cityId, place.address.city, place.address.street, place.address.house, place.name, place.category.id));
            return;
        }
        media.put("place_id", place.id);
    }

    private void withFriends(List<String> withFriendsUids) {
        this.withFriendsUids = withFriendsUids;
    }

    private void withPlace(Place place) {
        this.place = place;
    }

    private void add(MediaItem item) throws MediaTopicPostException {
        Logger.m172d(" " + item);
        if (item != null && !item.isEmpty()) {
            if (this.isFinished) {
                JSONArray newPayload = new JSONArray();
                int size = this.mediaPayload.length();
                int i = 0;
                while (i < size) {
                    try {
                        newPayload.put(this.mediaPayload.get(i));
                        i++;
                    } catch (Throwable e) {
                        throw new MediaTopicPostException(10, e);
                    }
                }
                this.mediaPayload = newPayload;
                this.isFinished = false;
            }
            MediaItemType mediaType = item.type;
            try {
                JSONArray list;
                switch (C04641.$SwitchMap$ru$ok$model$mediatopics$MediaItemType[mediaType.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        JSONObject text = new JSONObject();
                        text.put("type", Stanza.TEXT);
                        text.put(Stanza.TEXT, ((TextItem) item).getText());
                        this.mediaPayload.put(text);
                        break;
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        JSONObject link = new JSONObject();
                        link.put("type", "link");
                        link.put(PlusShare.KEY_CALL_TO_ACTION_URL, ((LinkItem) item).getLinkUrl());
                        this.mediaPayload.put(link);
                        break;
                    case Message.TYPE_FIELD_NUMBER /*3*/:
                        String photoToken = obtainNextPhotoToken();
                        if (photoToken == null) {
                            Logger.m184w("No more photo tokens for next item");
                            break;
                        }
                        list = getMediaList(mediaType);
                        JSONObject photo = new JSONObject();
                        photo.put("id", photoToken);
                        list.put(photo);
                        break;
                    case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                        MusicItem music = (MusicItem) item;
                        list = getMediaList(mediaType);
                        for (Track track : music.getTracks()) {
                            JSONObject trackJson = new JSONObject();
                            trackJson.put("id", track.id);
                            Artist artist = track.artist;
                            trackJson.put("artistName", artist == null ? null : artist.name);
                            trackJson.put(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, track.name);
                            Album album = track.album;
                            String albumName = album == null ? null : album.name;
                            if (albumName != null) {
                                trackJson.put("albumName", albumName);
                            }
                            list.put(trackJson);
                        }
                        break;
                    case Message.UUID_FIELD_NUMBER /*5*/:
                        PollItem pollItem = (PollItem) item;
                        JSONObject pollJson = new JSONObject();
                        pollJson.put("type", mediaType.getApiName());
                        pollJson.put("question", pollItem.getTitle());
                        JSONArray answers = new JSONArray();
                        for (String answer : pollItem.getAnswers()) {
                            JSONObject answerJson = new JSONObject();
                            answerJson.put(Stanza.TEXT, answer);
                            answers.put(answerJson);
                        }
                        pollJson.put("answers", answers);
                        if (!pollItem.isMultiAnswersAllowed()) {
                            pollJson.put("options", "SingleChoice");
                        }
                        this.mediaPayload.put(pollJson);
                        break;
                    default:
                        Logger.m185w("Media type not supported: %s", item.getClass().getSimpleName());
                        break;
                }
                this.lastItemType = mediaType;
            } catch (Throwable e2) {
                throw new MediaTopicPostException(10, e2);
            }
        }
    }

    private JSONArray getMediaList(MediaItemType type) throws JSONException {
        if (type == this.lastItemType) {
            return this.mediaPayload.getJSONObject(this.mediaPayload.length() - 1).getJSONArray("list");
        }
        JSONObject newMedia = new JSONObject();
        newMedia.put("type", type.getApiName());
        JSONArray list = new JSONArray();
        newMedia.put("list", list);
        this.mediaPayload.put(newMedia);
        return list;
    }

    private String obtainNextPhotoToken() {
        if (this.photoTokens == null || !this.photoTokens.hasNext()) {
            return null;
        }
        return (String) this.photoTokens.next();
    }
}
