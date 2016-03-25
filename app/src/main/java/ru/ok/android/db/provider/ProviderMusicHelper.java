package ru.ok.android.db.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import ru.ok.android.db.SQLiteUtils;

final class ProviderMusicHelper {
    static Cursor queryPlayList(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "playlist OUTER LEFT JOIN tracks ON playlist._id=tracks._id OUTER LEFT JOIN albums ON tracks.album=albums._id OUTER LEFT JOIN artists ON tracks.artist=artists._id", uri, projection, selection, selectionArgs, sortOrder, false);
    }

    static Cursor queryCollections(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "collections OUTER LEFT JOIN collections2users ON collections._id = collections2users.collection_id", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Cursor queryPopCollections(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "pop_collections INNER JOIN collections ON collections._id = pop_collections.collection_id", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Cursor queryUserTracks(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "user_music OUTER LEFT JOIN tracks ON tracks._id = user_music.track_id OUTER LEFT JOIN albums ON tracks.album=albums._id OUTER LEFT JOIN artists ON tracks.artist=artists._id", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Cursor queryMusicFriends(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "users INNER JOIN friends_music ON users.user_id = friends_music.user_id", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Cursor queryCollectionTracks(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "collection_tracks OUTER LEFT JOIN tracks ON collection_tracks.track_id=tracks._id OUTER LEFT JOIN albums ON tracks.album=albums._id OUTER LEFT JOIN artists ON tracks.artist=artists._id", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Cursor queryRelationCollectionsUsers(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "collections2users OUTER LEFT JOIN collections ON collections._id = collections2users.collection_id", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Cursor queryTuners(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "tuners", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Cursor queryTunersArtists(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "artists OUTER LEFT JOIN tuner2artist ON artists._id = tuner2artist.artist_id", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Cursor queryTunersTracks(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "tuner2tracks OUTER LEFT JOIN tracks ON tracks._id = tuner2tracks.track_id OUTER LEFT JOIN albums ON tracks.album=albums._id OUTER LEFT JOIN artists ON tracks.artist=artists._id", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Cursor queryHistoryTracks(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "music_history OUTER LEFT JOIN tracks ON tracks._id = music_history.track_id OUTER LEFT JOIN albums ON tracks.album=albums._id OUTER LEFT JOIN artists ON tracks.artist=artists._id", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Cursor queryPopTracks(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "pop_music INNER JOIN tracks ON tracks._id = pop_music.track_id OUTER LEFT JOIN albums ON tracks.album=albums._id OUTER LEFT JOIN artists ON tracks.artist=artists._id", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Cursor queryExtensionTracks(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "extension_music OUTER LEFT JOIN tracks ON tracks._id = extension_music.track_id OUTER LEFT JOIN albums ON tracks.album=albums._id OUTER LEFT JOIN artists ON tracks.artist=artists._id", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Cursor queryMaxPositionTracks(Context context, SQLiteDatabase db, Uri uri, String userId) {
        String str = "user_music";
        String[] strArr = new String[]{"MAX(_index) as MAX"};
        SQLiteDatabase sQLiteDatabase = db;
        Cursor cursor = sQLiteDatabase.query(str, strArr, "user_id = ?", new String[]{userId}, null, null, null);
        cursor.setNotificationUri(context.getContentResolver(), uri);
        return cursor;
    }

    static Cursor queryMaxPositionCollections(Context context, SQLiteDatabase db, Uri uri, String userId) {
        String str = "collections2users";
        String[] strArr = new String[]{"MAX(_index) as MAX"};
        SQLiteDatabase sQLiteDatabase = db;
        Cursor cursor = sQLiteDatabase.query(str, strArr, "user_id = ?", new String[]{userId}, null, null, null);
        cursor.setNotificationUri(context.getContentResolver(), uri);
        return cursor;
    }

    static Uri insertTrack(SQLiteDatabase db, ContentValues cv) {
        return OdklProvider.trackUri(ProviderUtils.insert(db, "tracks", cv));
    }

    static Uri insertArtist(SQLiteDatabase db, ContentValues cv) {
        return OdklProvider.artistUri(ProviderUtils.insert(db, "artists", cv));
    }

    static Uri insertAlbum(SQLiteDatabase db, ContentValues cv) {
        return OdklProvider.albumUri(ProviderUtils.insert(db, "albums", cv));
    }

    static Uri insertPlayListTrack(SQLiteDatabase db, ContentValues cv) {
        ProviderUtils.insert(db, "playlist", cv);
        return OdklProvider.playListUri();
    }

    static Uri insertCollection(SQLiteDatabase db, ContentValues cv) {
        return OdklProvider.collectionUri(ProviderUtils.insert(db, "collections", cv));
    }

    static Uri insertCollectionRelation(SQLiteDatabase db, ContentValues cv) {
        String selection = "collection_id = ? and user_id = ?";
        String[] values = new String[]{cv.getAsString("collection_id"), cv.getAsString("user_id")};
        if (ProviderUtils.isRowExist(db, "collections2users", selection, values)) {
            ProviderUtils.update(db, "collections2users", cv, selection, values);
        } else {
            ProviderUtils.insert(db, "collections2users", cv);
        }
        return OdklProvider.collectionRelationsUri(userId);
    }

    static Uri insertPopCollectionRelation(SQLiteDatabase db, ContentValues cv) {
        long id;
        String selection = "collection_id = ?";
        String[] values = new String[]{cv.getAsString("collection_id")};
        if (ProviderUtils.isRowExist(db, "pop_collections", selection, values)) {
            id = (long) ProviderUtils.update(db, "pop_collections", cv, selection, values);
        } else {
            id = ProviderUtils.insert(db, "pop_collections", cv);
        }
        return OdklProvider.popCollectionRelationUri(id);
    }

    static Uri insertUserMusic(SQLiteDatabase db, ContentValues cv) {
        String uid = cv.getAsString("user_id");
        String trackId = cv.getAsString("track_id");
        String selection = "user_id = ? and track_id = ?";
        String[] values = new String[]{uid, trackId};
        if (ProviderUtils.isRowExist(db, "user_music", selection, values)) {
            if (ProviderUtils.update(db, "user_music", cv, selection, values) > 0) {
                return OdklProvider.userTracksUri(uid);
            }
        } else if (ProviderUtils.insert(db, "user_music", cv) >= 0) {
            return OdklProvider.userTracksUri(uid);
        }
        return null;
    }

    static Uri insertTunerTracks(SQLiteDatabase db, ContentValues cv, String tunerHash) {
        if (ProviderUtils.insert(db, "tuner2tracks", cv) >= 0) {
            return OdklProvider.tunersTracksUri(tunerHash);
        }
        return null;
    }

    static Uri insertMusicFriend(SQLiteDatabase db, ContentValues cv) {
        String uid = cv.getAsString("user_id");
        String selection = "user_id = ?";
        String[] values = new String[]{cv.getAsString("user_id")};
        if (!ProviderUtils.isRowExist(db, "friends_music", selection, values)) {
            if (ProviderUtils.insert(db, "friends_music", cv) >= 0) {
            }
            return OdklProvider.musicFriendsUri(uid);
        } else if (ProviderUtils.update(db, "friends_music", cv, selection, values) > 0) {
            return OdklProvider.musicFriendsUri(uid);
        } else {
            return null;
        }
    }

    static Uri insertCollectionTrack(SQLiteDatabase db, ContentValues cv) {
        long id;
        String selection = "track_id = ? and collection_id = ?";
        String[] values = new String[]{cv.getAsString("track_id"), cv.getAsString("collection_id")};
        if (ProviderUtils.isRowExist(db, "collection_tracks", selection, values)) {
            id = (long) ProviderUtils.update(db, "collection_tracks", cv, selection, values);
        } else {
            id = ProviderUtils.insert(db, "collection_tracks", cv);
        }
        return OdklProvider.collectionTracksUri(id);
    }

    static Uri insertTuner(SQLiteDatabase db, ContentValues cv) {
        if (ProviderUtils.isRowExist(db, "tuners", "data = ?", new String[]{cv.getAsString("data")})) {
            ProviderUtils.update(db, "tuners", cv, "data = ?", new String[]{cv.getAsString("data")});
        } else {
            ProviderUtils.insert(db, "tuners", cv);
        }
        return OdklProvider.tunersUri(id);
    }

    static Uri insertTunerArtist(SQLiteDatabase db, ContentValues cv) {
        String selection = "tuner_data = ? and artist_id = ?";
        String[] values = new String[]{cv.getAsString("tuner_data"), cv.getAsString("artist_id")};
        if (ProviderUtils.isRowExist(db, "tuner2artist", selection, values)) {
            ProviderUtils.update(db, "tuner2artist", cv, selection, values);
        } else {
            ProviderUtils.insert(db, "tuner2artist", cv);
        }
        return OdklProvider.tunersArtistsUri(tunerId);
    }

    static Uri insertHistoryMusic(SQLiteDatabase db, ContentValues cv) {
        return OdklProvider.musicHistoryUri(ProviderUtils.insert(db, "music_history", cv));
    }

    static Uri insertPopMusic(SQLiteDatabase db, ContentValues cv) {
        return OdklProvider.popTracksUri(ProviderUtils.insert(db, "pop_music", cv));
    }

    static Uri insertExtensionMusic(SQLiteDatabase db, ContentValues cv) {
        return OdklProvider.musicExtensionUri(ProviderUtils.insert(db, "extension_music", cv));
    }

    static int insertAlbums(SQLiteDatabase db, ContentValues[] values) {
        SQLiteUtils.beginTransaction(db);
        try {
            for (ContentValues contentValues : values) {
                insertAlbum(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            return 0;
        } finally {
            db.endTransaction();
        }
    }

    static int insertArtists(SQLiteDatabase db, ContentValues[] values) {
        SQLiteUtils.beginTransaction(db);
        try {
            for (ContentValues contentValues : values) {
                insertArtist(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            return 0;
        } finally {
            db.endTransaction();
        }
    }

    static int insertTracks(SQLiteDatabase db, ContentValues[] values) {
        SQLiteUtils.beginTransaction(db);
        try {
            for (ContentValues contentValues : values) {
                insertTrack(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            return 0;
        } finally {
            db.endTransaction();
        }
    }

    static int insertPlayList(SQLiteDatabase db, ContentValues[] values) {
        SQLiteUtils.beginTransaction(db);
        try {
            for (ContentValues contentValues : values) {
                insertPlayListTrack(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            return 0;
        } finally {
            db.endTransaction();
        }
    }

    static int insertCollections(SQLiteDatabase db, ContentValues[] values) {
        SQLiteUtils.beginTransaction(db);
        try {
            for (ContentValues contentValues : values) {
                insertCollection(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            return 0;
        } finally {
            db.endTransaction();
        }
    }

    static int insertCollectionRelations(SQLiteDatabase db, ContentValues[] values, String deleteUserId) {
        SQLiteUtils.beginTransaction(db);
        if (deleteUserId != null) {
            deleteUser2Collections(db, "user_id = ?", new String[]{deleteUserId});
        }
        try {
            for (ContentValues contentValues : values) {
                insertCollectionRelation(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            return 0;
        } finally {
            db.endTransaction();
        }
    }

    static int insertPopCollectionRelations(SQLiteDatabase db, ContentValues[] values) {
        SQLiteUtils.beginTransaction(db);
        int i = null;
        try {
            deletePopCollections(db, null, null);
            for (ContentValues contentValues : values) {
                insertPopCollectionRelation(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            i = 0;
            return i;
        } finally {
            db.endTransaction();
        }
    }

    static int insertUserTracks(SQLiteDatabase db, ContentValues[] values, String uid) {
        SQLiteUtils.beginTransaction(db);
        if (uid != null) {
            deleteUserTracks(db, "user_music.user_id =?", new String[]{uid});
        }
        try {
            for (ContentValues contentValues : values) {
                insertUserMusic(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            return 0;
        } finally {
            db.endTransaction();
        }
    }

    static int insertMusicFriends(SQLiteDatabase db, ContentValues[] values) {
        SQLiteUtils.beginTransaction(db);
        int i = null;
        try {
            deleteFriendsInfo(db, null, null);
            for (ContentValues contentValues : values) {
                insertMusicFriend(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            i = 0;
            return i;
        } finally {
            db.endTransaction();
        }
    }

    static int insertCollectionTracks(SQLiteDatabase db, ContentValues[] values) {
        SQLiteUtils.beginTransaction(db);
        try {
            for (ContentValues contentValues : values) {
                insertCollectionTrack(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            return 0;
        } finally {
            db.endTransaction();
        }
    }

    static int insertMusicTuners(SQLiteDatabase db, ContentValues[] values) {
        int i = null;
        SQLiteUtils.beginTransaction(db);
        deleteTuners(db, null, null);
        try {
            for (ContentValues contentValues : values) {
                insertTuner(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            i = 0;
            return i;
        } finally {
            db.endTransaction();
        }
    }

    static int insertTunersArtists(SQLiteDatabase db, ContentValues[] values) {
        SQLiteUtils.beginTransaction(db);
        try {
            for (ContentValues contentValues : values) {
                insertTunerArtist(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            return 0;
        } finally {
            db.endTransaction();
        }
    }

    static int insertTunerTracks(SQLiteDatabase db, ContentValues[] values, String tunerId) {
        SQLiteUtils.beginTransaction(db);
        deleteTunerTracks(db, "tuner_data = ?", new String[]{tunerId});
        try {
            for (ContentValues contentValues : values) {
                insertTunerTracks(db, contentValues, tunerId);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            return 0;
        } finally {
            db.endTransaction();
        }
    }

    static int insertHistoryMusic(SQLiteDatabase db, ContentValues[] values) {
        int i = null;
        SQLiteUtils.beginTransaction(db);
        deleteHistoryMusic(db, null, null);
        try {
            for (ContentValues contentValues : values) {
                insertHistoryMusic(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            i = 0;
            return i;
        } finally {
            db.endTransaction();
        }
    }

    static int insertPopMusic(SQLiteDatabase db, ContentValues[] values) {
        int i = null;
        SQLiteUtils.beginTransaction(db);
        deletePopMusic(db, null, null);
        try {
            for (ContentValues contentValues : values) {
                insertPopMusic(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            i = 0;
            return i;
        } finally {
            db.endTransaction();
        }
    }

    static int insertExtensionMusic(SQLiteDatabase db, ContentValues[] values) {
        int i = null;
        SQLiteUtils.beginTransaction(db);
        deleteExtensionMusic(db, null, null);
        try {
            for (ContentValues contentValues : values) {
                insertExtensionMusic(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            i = 0;
            return i;
        } finally {
            db.endTransaction();
        }
    }

    static int updateUserTracks(SQLiteDatabase db, ContentValues cv, String selection, String[] selectionArgs) {
        return ProviderUtils.update(db, "user_music", cv, selection, selectionArgs);
    }

    static int updateTunerTracks(SQLiteDatabase db, ContentValues cv, String selection, String[] selectionArgs) {
        return ProviderUtils.update(db, "tuner2tracks", cv, selection, selectionArgs);
    }

    public static int deleteAlbums(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("albums", selection, selectionArgs);
    }

    public static int deleteArtists(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("artists", selection, selectionArgs);
    }

    public static int deletePlayList(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("playlist", selection, selectionArgs);
    }

    public static int deleteTracks(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("tracks", selection, selectionArgs);
    }

    public static int deleteCollections(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("collections", selection, selectionArgs);
    }

    public static int deleteUser2Collections(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("collections2users", selection, selectionArgs);
    }

    public static int deleteUserTracks(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("user_music", selection, selectionArgs);
    }

    public static int deleteFriendsInfo(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("friends_music", selection, selectionArgs);
    }

    public static int deleteTuners(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("tuners", selection, selectionArgs);
    }

    public static int deleteTunersToArtist(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("tuner2artist", selection, selectionArgs);
    }

    public static int deletePopCollections(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("pop_collections", selection, selectionArgs);
    }

    public static int deleteHistoryMusic(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("music_history", selection, selectionArgs);
    }

    public static int deleteTunerTracks(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("tuner2tracks", selection, selectionArgs);
    }

    public static int deletePopMusic(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("pop_music", selection, selectionArgs);
    }

    public static int deleteExtensionMusic(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("extension_music", selection, selectionArgs);
    }
}
