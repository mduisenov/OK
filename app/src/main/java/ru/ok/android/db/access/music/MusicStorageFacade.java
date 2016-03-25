package ru.ok.android.db.access.music;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.db.access.fillers.UserInfoValuesFiller;
import ru.ok.android.db.base.BaseTable;
import ru.ok.android.db.music.AlbumsTable;
import ru.ok.android.db.music.ArtistsTable;
import ru.ok.android.db.music.CollectionsTable;
import ru.ok.android.db.music.ExtensionMusicTable;
import ru.ok.android.db.music.HistoryMusicTable;
import ru.ok.android.db.music.PopMusicTable;
import ru.ok.android.db.music.TracksTable;
import ru.ok.android.db.music.Tuner2ArtistTable;
import ru.ok.android.db.music.UserMusicTable;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.utils.Logger;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.UserInfo.UserOnlineType;
import ru.ok.model.music.MusicUserInfo;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.HistoryTrack;
import ru.ok.model.wmf.Track;
import ru.ok.model.wmf.Tuner;
import ru.ok.model.wmf.UserTrackCollection;

public class MusicStorageFacade {
    private static ArrayList<ContentProviderOperation> createTracksOperations(Track[] tracks) {
        Uri tracksUri = OdklProvider.tracksSilentUri();
        Uri albumsUri = OdklProvider.albumsSilentUri();
        Uri artistsUri = OdklProvider.artistsSilentUri();
        Set<Long> addedAlbums = new HashSet();
        Set<Long> addedArtists = new HashSet();
        ArrayList<ContentProviderOperation> result = new ArrayList();
        for (Track track : tracks) {
            result.add(ContentProviderOperation.newInsert(tracksUri).withValues(getContentValuesForTrack(track)).build());
            Album album = track.album;
            if (album != null) {
                long albumId = album.id;
                if (!addedAlbums.contains(Long.valueOf(albumId))) {
                    ContentValues albumCV = getContentValuesForAlbum(album);
                    if (albumCV != null) {
                        addedAlbums.add(Long.valueOf(albumId));
                        result.add(ContentProviderOperation.newInsert(albumsUri).withValues(albumCV).build());
                    }
                }
            }
            Artist artist = track.artist;
            if (artist != null) {
                long artistId = artist.id;
                if (!addedArtists.contains(Long.valueOf(artistId))) {
                    ContentValues artistCV = getContentValuesForArtist(artist);
                    if (artistCV != null) {
                        addedArtists.add(Long.valueOf(artistId));
                        result.add(ContentProviderOperation.newInsert(artistsUri).withValues(artistCV).build());
                    }
                }
            }
        }
        return result;
    }

    public static void savePlayList(Context context, Track[] tracks, int token) {
        ArrayList<ContentProviderOperation> operations = createTracksOperations(tracks);
        Uri playlistUri = OdklProvider.playListSilentUri();
        operations.add(ContentProviderOperation.newDelete(playlistUri).build());
        for (int i = 0; i < tracks.length; i++) {
            operations.add(ContentProviderOperation.newInsert(playlistUri).withValues(getContentValuesForTrackPlayList(tracks[i], i)).build());
        }
        Uri notifyUri = ContentUris.withAppendedId(OdklProvider.playListUri(), (long) token);
        applyBatch(context, operations, notifyUri);
    }

    public static void insertUserMusicTracks(Context context, String userId, List<Pair<Track, Integer>> tracks) {
        Track[] tracksArray = new Track[tracks.size()];
        for (int i = 0; i < tracks.size(); i++) {
            tracksArray[i] = (Track) ((Pair) tracks.get(i)).first;
        }
        ArrayList<ContentProviderOperation> operations = createTracksOperations(tracksArray);
        Uri userTracksUri = OdklProvider.userTracksSilentUri();
        for (Pair<Track, Integer> trackPair : tracks) {
            operations.add(ContentProviderOperation.newInsert(userTracksUri).withValues(getContentValuesForUserTracks(trackPair.first, userId, ((Integer) trackPair.second).intValue())).build());
        }
        applyBatch(context, operations, OdklProvider.userTracksUri());
    }

    public static void deleteUserTracks(Context context, String userId, Track[] tracks) {
        if (tracks.length != 0) {
            StringBuilder inBuilder = new StringBuilder();
            for (Track track : tracks) {
                if (inBuilder.length() > 0) {
                    inBuilder.append(",");
                }
                inBuilder.append(track.id);
            }
            context.getContentResolver().delete(OdklProvider.userTracksUri(), "user_music.user_id = ? AND user_music.track_id IN ( " + inBuilder + ")", new String[]{userId});
        }
    }

    public static void syncUserTracks(Context context, String userId, Track[] tracks) {
        new UserTrackSync(context, userId).syncData(tracks);
    }

    public static void deleteUserCollectionRelation(Context context, String userId, long collectionId) {
        context.getContentResolver().delete(OdklProvider.collectionRelationsUri(), "collections2users.user_id = ? and collections2users.collection_id = ?", new String[]{userId, String.valueOf(collectionId)});
    }

    public static void insertHistoryTracks(Context context, HistoryTrack[] tracks) {
        ArrayList<ContentProviderOperation> operations = createTracksOperations(tracks);
        Uri musicHistoryUri = OdklProvider.musicHistorySilentUri();
        operations.add(ContentProviderOperation.newDelete(musicHistoryUri).build());
        for (HistoryTrack track : tracks) {
            operations.add(ContentProviderOperation.newInsert(musicHistoryUri).withValues(getContentValuesForHistory(track)).build());
        }
        applyBatch(context, operations, OdklProvider.musicHistoryUri());
    }

    public static void insertExtensionTracks(Context context, Track[] tracks) {
        ArrayList<ContentProviderOperation> operations = createTracksOperations(tracks);
        Uri musicExtensionUri = OdklProvider.musicExtensionSilentUri();
        operations.add(0, ContentProviderOperation.newDelete(musicExtensionUri).build());
        for (int i = 0; i < tracks.length; i++) {
            operations.add(ContentProviderOperation.newInsert(musicExtensionUri).withValues(getContentValuesForExtensionMusic(tracks[i], i)).build());
        }
        applyBatch(context, operations, OdklProvider.musicExtensionUri());
    }

    public static void insertPopTracks(Context context, Track[] tracks) {
        ArrayList<ContentProviderOperation> operations = createTracksOperations(tracks);
        Uri popTracksUri = OdklProvider.popTracksSilentUri();
        for (int i = 0; i < tracks.length; i++) {
            operations.add(ContentProviderOperation.newInsert(popTracksUri).withValues(getContentValuesForPopMusic(tracks[i], i)).build());
        }
        applyBatch(context, operations, OdklProvider.popTracksUri());
    }

    public static void insertMusicFriends(Context context, Collection<MusicUserInfo> users) {
        UsersStorageFacade.insertUsers(users, UserInfoValuesFiller.MUSIC);
        insertMusicFriendsCountInfo(context, users);
    }

    public static void insertUserMusicCollections(Context context, String userId, UserTrackCollection[] collections) {
        insertMusicCollections(context, collections);
        insertUserCollectionRelations(context, userId, collections);
    }

    public static void insertPopMusicCollections(Context context, UserTrackCollection[] collections) {
        insertMusicCollections(context, collections);
        addPopCollectionRelations(context, collections);
    }

    public static void insertMusicCollections(Context context, UserTrackCollection[] collections) {
        ArrayList<ContentValues> cvsCollections = new ArrayList();
        for (UserTrackCollection collection : collections) {
            cvsCollections.add(getContentValuesForCollection(collection));
        }
        context.getContentResolver().bulkInsert(OdklProvider.collectionsUri(), (ContentValues[]) cvsCollections.toArray(new ContentValues[cvsCollections.size()]));
    }

    public static void insertCollectionTracks(Context context, long collectionId, Track[] tracks) {
        ArrayList<ContentProviderOperation> operations = createTracksOperations(tracks);
        Uri collectionTracksUri = OdklProvider.collectionTracksSilentUri();
        for (Track track : tracks) {
            operations.add(ContentProviderOperation.newInsert(collectionTracksUri).withValues(getContentValuesForCollectionTrack(track, collectionId)).build());
        }
        applyBatch(context, operations, OdklProvider.collectionTracksUri());
    }

    public static void insertTuners(Context context, Tuner[] tuners) {
        ArrayList<ContentProviderOperation> operations = new ArrayList();
        Set<Long> addedArtists = new HashSet();
        Uri artistsUri = OdklProvider.artistsSilentUri();
        Uri tunerArtistsCV = OdklProvider.tunersArtistsSilentUri();
        Uri tunersUri = OdklProvider.tunersSilentUri();
        operations.add(ContentProviderOperation.newDelete(tunersUri).build());
        int i = 0;
        while (true) {
            int length = tuners.length;
            if (i < r0) {
                Tuner tuner = tuners[i];
                int j = 0;
                while (true) {
                    if (j >= tuner.artists.size()) {
                        break;
                    }
                    Artist artist = (Artist) tuner.artists.get(j);
                    if (artist != null) {
                        long artistId = artist.id;
                        if (!addedArtists.contains(Long.valueOf(artistId))) {
                            ContentValues artistCV = getContentValuesForArtist(artist);
                            if (artistCV != null) {
                                addedArtists.add(Long.valueOf(artistId));
                                operations.add(ContentProviderOperation.newInsert(artistsUri).withValues(artistCV).build());
                            }
                        }
                        ContentValues tuner2ArtistCV = getContentValuesTunerToArtist(tuner, artist);
                        operations.add(ContentProviderOperation.newInsert(tunerArtistsCV).withValues(tuner2ArtistCV).build());
                    }
                    j++;
                }
                ContentValues cv = getContentValuesForTuner(tuner);
                operations.add(ContentProviderOperation.newInsert(tunersUri).withValues(cv).build());
                i++;
            } else {
                applyBatch(context, operations, OdklProvider.tunersUri());
                return;
            }
        }
    }

    public static void insertTunerTracks(Context context, String tunerHash, Track[] tracks) {
        ArrayList<ContentProviderOperation> operations = createTracksOperations(tracks);
        Uri uri = OdklProvider.tunersTracksSilentUri(tunerHash);
        for (int i = 0; i < tracks.length; i++) {
            operations.add(ContentProviderOperation.newInsert(uri).withValues(getContentValuesTunerToTrack(tracks[i], tunerHash, i)).build());
        }
        applyBatch(context, operations, OdklProvider.tunersTracksUri());
    }

    private static void applyBatch(Context context, ArrayList<ContentProviderOperation> operations, Uri... notifyUris) {
        try {
            ContentResolver cr = context.getContentResolver();
            cr.applyBatch(OdklProvider.AUTHORITY, operations);
            if (notifyUris != null) {
                for (Uri notifyUri : notifyUris) {
                    cr.notifyChange(notifyUri, null);
                }
            }
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }

    private static void insertMusicFriendsCountInfo(Context context, Collection<MusicUserInfo> users) {
        ArrayList<ContentValues> cvsUsers = new ArrayList();
        for (MusicUserInfo user : users) {
            cvsUsers.add(getContentValuesForFriendsMusic(user));
        }
        context.getContentResolver().bulkInsert(OdklProvider.musicFriendsUri(), (ContentValues[]) cvsUsers.toArray(new ContentValues[cvsUsers.size()]));
    }

    public static void insertUserCollectionRelations(Context context, String userId, UserTrackCollection[] collections) {
        ArrayList<ContentValues> cvsRelations = new ArrayList();
        for (int i = 0; i < collections.length; i++) {
            cvsRelations.add(getContentValuesForRelations(userId, collections[i].id, i));
        }
        context.getContentResolver().bulkInsert(OdklProvider.collectionRelationsUri(userId), (ContentValues[]) cvsRelations.toArray(new ContentValues[cvsRelations.size()]));
    }

    public static void addUserCollectionRelation(Context context, String userId, long collectionId) {
        context.getContentResolver().insert(OdklProvider.collectionRelationsUri(), getContentValuesForRelations(userId, collectionId, getMaxMyCollectionPosition(OdnoklassnikiApplication.getContext(), OdnoklassnikiApplication.getCurrentUser().uid) + 1));
    }

    private static void addPopCollectionRelations(Context context, UserTrackCollection[] collections) {
        ArrayList<ContentValues> cvsRelations = new ArrayList();
        for (UserTrackCollection collection : collections) {
            cvsRelations.add(getContentValuesForPopRelations(collection));
        }
        context.getContentResolver().bulkInsert(OdklProvider.popCollectionsUri(), (ContentValues[]) cvsRelations.toArray(new ContentValues[cvsRelations.size()]));
    }

    private static ContentValues getContentValuesForRelations(String userId, long collectionId, int index) {
        ContentValues cv = new ContentValues();
        cv.put("collection_id", Long.valueOf(collectionId));
        cv.put("user_id", userId);
        cv.put("_index", Integer.valueOf(index));
        return cv;
    }

    private static ContentValues getContentValuesForPopRelations(UserTrackCollection collection) {
        ContentValues cv = new ContentValues();
        cv.put("collection_id", Long.valueOf(collection.id));
        return cv;
    }

    private static ContentValues getContentValuesForCollection(UserTrackCollection collection) {
        ContentValues cv = new ContentValues();
        cv.put("_id", Long.valueOf(collection.id));
        cv.put("name", collection.name);
        cv.put("image_url", collection.imageUrl);
        cv.put("tracks_count", Integer.valueOf(collection.tracksCount));
        return cv;
    }

    private static ContentValues getContentValuesForTrack(Track track) {
        ContentValues cv = new ContentValues();
        cv.put("_id", Long.valueOf(track.id));
        cv.put("name", track.name);
        cv.put("image_url", track.imageUrl);
        cv.put("ensemble", track.ensemble);
        cv.put("duration", Integer.valueOf(track.duration));
        cv.put("explicit", Boolean.valueOf(track.explicit));
        if (track.album == null) {
            cv.putNull("album");
        } else {
            cv.put("album", Long.valueOf(track.album.id));
        }
        if (track.artist == null) {
            cv.putNull("artist");
        } else {
            cv.put("artist", Long.valueOf(track.artist.id));
        }
        if (track.fullName == null) {
            cv.putNull("full_name");
        } else {
            cv.put("full_name", track.fullName);
        }
        return cv;
    }

    private static ContentValues getContentValuesForUserTracks(Track track, String userId, int position) {
        ContentValues cv = new ContentValues();
        cv.put("track_id", Long.valueOf(track.id));
        cv.put("user_id", userId);
        cv.put("_index", Integer.valueOf(position));
        return cv;
    }

    private static ContentValues getContentValuesForArtist(Artist artist) {
        if (artist == null) {
            return null;
        }
        ContentValues cv = new ContentValues();
        cv.put("_id", Long.valueOf(artist.id));
        cv.put("name", artist.name);
        cv.put("image_url", artist.imageUrl);
        return cv;
    }

    private static ContentValues getContentValuesForAlbum(Album album) {
        if (album == null) {
            return null;
        }
        ContentValues cv = new ContentValues();
        cv.put("_id", Long.valueOf(album.id));
        cv.put("name", album.name);
        cv.put("ensemble", album.ensemble);
        cv.put("image_url", album.imageUrl);
        return cv;
    }

    private static ContentValues getContentValuesForTrackPlayList(Track track, int position) {
        ContentValues cv = new ContentValues();
        cv.put("_id", Long.valueOf(track.id));
        cv.put("_index", Integer.valueOf(position));
        return cv;
    }

    private static ContentValues getContentValuesForCollectionTrack(Track track, long collectionId) {
        ContentValues cv = new ContentValues();
        cv.put("collection_id", Long.valueOf(collectionId));
        cv.put("track_id", Long.valueOf(track.id));
        return cv;
    }

    private static ContentValues getContentValuesForFriendsMusic(MusicUserInfo user) {
        ContentValues cv = new ContentValues();
        cv.put("user_id", user.uid);
        cv.put("count", Integer.valueOf(user.tracksCount));
        cv.put("add_time", Long.valueOf(user.lastAddTime));
        return cv;
    }

    private static ContentValues getContentValuesForTuner(Tuner tuner) {
        ContentValues cv = new ContentValues();
        cv.put("name", tuner.name);
        cv.put("data", tuner.data);
        return cv;
    }

    private static ContentValues getContentValuesTunerToArtist(Tuner tuner, Artist artist) {
        ContentValues cv = new ContentValues();
        cv.put("tuner_data", tuner.data);
        cv.put("artist_id", Long.valueOf(artist.id));
        return cv;
    }

    private static ContentValues getContentValuesForHistory(HistoryTrack track) {
        ContentValues cv = new ContentValues();
        cv.put("track_id", Long.valueOf(track.id));
        cv.put("time", Long.valueOf(track.time));
        return cv;
    }

    private static ContentValues getContentValuesForPopMusic(Track track, int position) {
        ContentValues cv = new ContentValues();
        cv.put("track_id", Long.valueOf(track.id));
        cv.put("_index", Integer.valueOf(position));
        return cv;
    }

    private static ContentValues getContentValuesForExtensionMusic(Track track, int position) {
        ContentValues cv = new ContentValues();
        cv.put("track_id", Long.valueOf(track.id));
        cv.put("_index", Integer.valueOf(position));
        return cv;
    }

    private static ContentValues getContentValuesTunerToTrack(Track track, String tunerHash, int position) {
        ContentValues cv = new ContentValues();
        cv.put("track_id", Long.valueOf(track.id));
        cv.put("tuner_data", tunerHash);
        cv.put("_index", Integer.valueOf(position));
        return cv;
    }

    public static MusicUserInfo cursor2MusicUser(Cursor cursor) {
        if (cursor == null) {
            return new MusicUserInfo(null, null, null, null, null, 0, null, UserOnlineType.OFFLINE, 0, UserGenderType.MALE, false, false, "", 0, 0, null);
        }
        return new MusicUserInfo(cursor.getString(cursor.getColumnIndex("user_id")), cursor.getString(cursor.getColumnIndex("user_first_name")), cursor.getString(cursor.getColumnIndex("user_last_name")), cursor.getString(cursor.getColumnIndex("user_name")), cursor.getString(cursor.getColumnIndex("user_avatar_url")), 0, null, UserOnlineType.safeValueOf(cursor.getString(cursor.getColumnIndex("user_online"))), cursor.getLong(cursor.getColumnIndex("user_last_online")), UserGenderType.byInteger(cursor.getInt(cursor.getColumnIndex("user_gender"))), cursor.getInt(cursor.getColumnIndex("user_can_call")) > 0, cursor.getInt(cursor.getColumnIndex("can_vmail")) > 0, "", cursor.getInt(cursor.getColumnIndex("count")), cursor.getLong(cursor.getColumnIndex("add_time")), null);
    }

    public static UserTrackCollection cursor2UserTrackCollection(Cursor cursor) {
        return new UserTrackCollection(cursor.getLong(cursor.getColumnIndex("collections_id")), cursor.getString(cursor.getColumnIndex("collections_name")), cursor.getString(cursor.getColumnIndex("collections_image_url")), cursor.getInt(cursor.getColumnIndex("collections_tracks_count")));
    }

    public static Track cursor2Track(Cursor cursor) {
        return new Track(cursor.getLong(cursor.getColumnIndex("tracks_id")), cursor.getString(cursor.getColumnIndex("tracks_name")), cursor.getString(cursor.getColumnIndex("tracks_ensemble")), cursor.getString(cursor.getColumnIndex("tracks_image_url")), cursor.getString(cursor.getColumnIndex("tracks_full_name")), cursor2Album(cursor), cursor2Artist(cursor), cursor.getInt(cursor.getColumnIndex("tracks_explicit")) != 0, cursor.getInt(cursor.getColumnIndex("tracks_duration")));
    }

    public static Artist cursor2Artist(Cursor cursor) {
        if (cursor.isNull(cursor.getColumnIndex("artists_id"))) {
            return null;
        }
        return new Artist(cursor.getLong(cursor.getColumnIndex("artists_id")), cursor.getString(cursor.getColumnIndex("artists_name")), cursor.getString(cursor.getColumnIndex("artists_image_url")));
    }

    private static Album cursor2Album(Cursor cursor) {
        if (cursor.isNull(cursor.getColumnIndex("albums_id"))) {
            return null;
        }
        return new Album(cursor.getLong(cursor.getColumnIndex("albums_id")), cursor.getString(cursor.getColumnIndex("albums_name")), cursor.getString(cursor.getColumnIndex("albums_image_url")), cursor.getString(cursor.getColumnIndex("albums_ensemble")));
    }

    public static ArrayList<Track> getTracksFromCursor(Cursor cursor) {
        ArrayList<Track> result = new ArrayList();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            result.add(cursor2Track(cursor));
        }
        return result;
    }

    public static List<String> getProjectionForPlayList() {
        List<String> listColumns = new ArrayList();
        TracksTable tracksTable = new TracksTable();
        AlbumsTable albumsTable = new AlbumsTable();
        ArtistsTable artistsTable = new ArtistsTable();
        BaseTable.serializeTable(tracksTable, listColumns);
        BaseTable.serializeTable(albumsTable, listColumns);
        BaseTable.serializeTable(artistsTable, listColumns);
        listColumns.add("tracks._id as _id");
        return listColumns;
    }

    public static List<String> getProjectionForCollection() {
        return getProjectionForPlayList();
    }

    public static List<String> getProjectionForUserMusic() {
        List<String> listColumns = getProjectionForPlayList();
        BaseTable.serializeTable(new UserMusicTable(), listColumns);
        return listColumns;
    }

    public static List<String> getProjectionForCollections() {
        List<String> listColumns = new ArrayList();
        BaseTable.serializeTable(new CollectionsTable(), listColumns);
        listColumns.add("collections._id as _id");
        return listColumns;
    }

    public static List<String> getProjectionForTunerArtists() {
        List<String> listColumns = new ArrayList();
        Tuner2ArtistTable tuner2artistTable = new Tuner2ArtistTable();
        ArtistsTable artistsTable = new ArtistsTable();
        BaseTable.serializeTable(tuner2artistTable, listColumns);
        BaseTable.serializeTable(artistsTable, listColumns);
        return listColumns;
    }

    public static List<String> getProjectionForHistory() {
        List<String> listColumns = getProjectionForPlayList();
        BaseTable.serializeTable(new HistoryMusicTable(), listColumns);
        return listColumns;
    }

    public static List<String> getProjectionForPopMusic() {
        List<String> listColumns = getProjectionForPlayList();
        BaseTable.serializeTable(new PopMusicTable(), listColumns);
        return listColumns;
    }

    public static List<String> getProjectionForExtensionMusic() {
        List<String> listColumns = getProjectionForPlayList();
        BaseTable.serializeTable(new ExtensionMusicTable(), listColumns);
        return listColumns;
    }

    public static int getMaxTracksPosition(Context context, String userId) {
        Cursor cursor = context.getContentResolver().query(OdklProvider.userMaxPositionTracksUri(userId), null, null, null, null);
        try {
            if (cursor.getCount() <= 0 || cursor.getColumnIndex("MAX") < 0 || !cursor.moveToNext()) {
                cursor.close();
                return 0;
            }
            int i = cursor.getInt(cursor.getColumnIndex("MAX"));
            return i;
        } finally {
            cursor.close();
        }
    }

    public static int getMaxMyCollectionPosition(Context context, String userId) {
        Cursor cursor = context.getContentResolver().query(OdklProvider.maxPositionCollectionsUri(userId), null, null, null, null);
        try {
            if (cursor.getCount() <= 0 || cursor.getColumnIndex("MAX") < 0 || !cursor.moveToNext()) {
                cursor.close();
                return -1;
            }
            int i = cursor.getInt(cursor.getColumnIndex("MAX"));
            return i;
        } finally {
            cursor.close();
        }
    }
}
