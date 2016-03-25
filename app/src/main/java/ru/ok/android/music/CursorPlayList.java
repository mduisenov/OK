package ru.ok.android.music;

import android.content.Context;
import android.database.Cursor;
import java.util.List;
import java.util.Random;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.android.utils.settings.Settings;
import ru.ok.model.wmf.Track;

public class CursorPlayList {
    private CacheTrackCursor cacheTrackCursor;
    private Context context;
    private boolean tracksReady;

    class CacheTrackCursorImp implements CacheTrackCursor {
        private Track cacheTrack;
        private Cursor cursor;

        CacheTrackCursorImp(Cursor cursor) throws CursorIsNullException {
            if (cursor == null) {
                throw new CursorIsNullException();
            }
            this.cursor = cursor;
        }

        public boolean next() {
            if (this.cursor.isClosed() || !this.cursor.moveToNext()) {
                return false;
            }
            this.cacheTrack = null;
            return true;
        }

        public boolean prev() {
            if (this.cursor.isClosed() || !this.cursor.moveToPrevious()) {
                return false;
            }
            this.cacheTrack = null;
            return true;
        }

        public Track getTrack() {
            if (this.cursor.isAfterLast() || this.cursor.isBeforeFirst()) {
                return null;
            }
            if (this.cacheTrack == null) {
                this.cacheTrack = CursorPlayList.getTrackFromCursor(this.cursor);
            }
            return this.cacheTrack;
        }

        public long getTrackId() {
            if (this.cacheTrack == null) {
                return this.cursor.getLong(this.cursor.getColumnIndex("tracks_id"));
            }
            return this.cacheTrack.id;
        }

        public void moveToStart() {
            this.cursor.moveToPosition(-1);
            this.cacheTrack = null;
        }

        public boolean moveToPosition(int pos) {
            boolean value = this.cursor.moveToPosition(pos);
            this.cacheTrack = null;
            return value;
        }

        public void moveToFirst() {
            this.cursor.moveToFirst();
            this.cacheTrack = null;
        }

        public void moveToLast() {
            this.cursor.moveToLast();
            this.cacheTrack = null;
        }

        public boolean isLastTrack() {
            return this.cursor.isLast();
        }

        public boolean isAfterLastTrack() {
            return this.cursor.isAfterLast();
        }

        public boolean isFirstTrack() {
            return this.cursor.isFirst();
        }

        public int getPosition() {
            return this.cursor.getPosition();
        }

        public void close() {
            this.cursor.close();
        }
    }

    public static class CursorIsNullException extends Exception {
    }

    public CursorPlayList(Context context, Track[] tmpTracks, int playPosition) throws CursorIsNullException {
        this.context = context;
        this.tracksReady = false;
        boolean shuffle = isShuffle();
        this.cacheTrackCursor = new TempTrackCursor(tmpTracks, shuffle);
        if (shuffle) {
            long id = tmpTracks[playPosition].id;
            while (this.cacheTrackCursor.next()) {
                if (this.cacheTrackCursor.getTrack().id == id) {
                    return;
                }
            }
            this.cacheTrackCursor.moveToStart();
            return;
        }
        this.cacheTrackCursor.moveToPosition(playPosition);
    }

    public CursorPlayList(Context context) throws CursorIsNullException {
        this.context = context;
        this.tracksReady = true;
        this.cacheTrackCursor = new CacheTrackCursorImp(isShuffle() ? getShufflePlayListCursor(context) : getPlayListCursor(context));
        long uid = getTrackId(context);
        while (this.cacheTrackCursor.next()) {
            if (this.cacheTrackCursor.getTrack().id == uid) {
                return;
            }
        }
        this.cacheTrackCursor.moveToStart();
    }

    public synchronized boolean moveToNext() {
        return this.cacheTrackCursor.next();
    }

    public synchronized boolean moveToPrev() {
        return this.cacheTrackCursor.prev();
    }

    public synchronized void moveToFirst() {
        this.cacheTrackCursor.moveToFirst();
    }

    public synchronized void moveToLast() {
        this.cacheTrackCursor.moveToLast();
    }

    public synchronized boolean isAfterLastTrack() {
        return this.cacheTrackCursor.isAfterLastTrack();
    }

    public synchronized boolean isLastTrack() {
        return this.cacheTrackCursor.isLastTrack();
    }

    public synchronized boolean isFirstTrack() {
        return this.cacheTrackCursor.isFirstTrack();
    }

    public boolean isShuffle() {
        return Settings.getBoolValue(this.context, "playlist_shuffle", false);
    }

    public synchronized void setShuffle(boolean shuffle) throws CursorIsNullException {
        Track track = getTrack();
        Settings.storeBoolValue(this.context, "playlist_shuffle", shuffle);
        close();
        if (this.tracksReady || !(this.cacheTrackCursor instanceof TempTrackCursor)) {
            Cursor shufflePlayListCursor;
            if (shuffle) {
                shufflePlayListCursor = getShufflePlayListCursor(this.context);
            } else {
                shufflePlayListCursor = getPlayListCursor(this.context);
            }
            this.cacheTrackCursor = new CacheTrackCursorImp(shufflePlayListCursor);
        } else {
            this.cacheTrackCursor = new TempTrackCursor((TempTrackCursor) this.cacheTrackCursor, shuffle);
        }
        while (this.cacheTrackCursor.next()) {
            if (getTrack().id == track.id) {
                break;
            }
        }
    }

    public synchronized boolean moveToTrack(long trackId) {
        boolean z;
        this.cacheTrackCursor.moveToStart();
        while (this.cacheTrackCursor.next()) {
            if (this.cacheTrackCursor.getTrackId() == trackId) {
                z = true;
                break;
            }
        }
        z = false;
        return z;
    }

    public synchronized boolean moveToPosition(int position) {
        return this.cacheTrackCursor.moveToPosition(position);
    }

    public synchronized Track getTrack() {
        return this.cacheTrackCursor.getTrack();
    }

    public synchronized int getPosition() {
        return this.cacheTrackCursor.getPosition();
    }

    public static void saveType(Context context, MusicListType type) {
        Settings.setPlayListType(context, type);
    }

    private void saveTrackId(Context context, Track track) {
        if (track != null) {
            Settings.storeLongValue(context, "playlist_track_id", track.id);
        }
    }

    public static MusicListType getType(Context context) {
        return Settings.getPlayListType(context, MusicListType.MY_MUSIC);
    }

    public long getTrackId(Context context) {
        return Settings.getLongValue(context, "playlist_track_id", 0);
    }

    private static Cursor getPlayListCursor(Context context) {
        List<String> projection = MusicStorageFacade.getProjectionForPlayList();
        return context.getContentResolver().query(OdklProvider.playListUri(), (String[]) projection.toArray(new String[projection.size()]), null, null, "playlist._index");
    }

    private static Cursor getShufflePlayListCursor(Context context) {
        List<String> projection = MusicStorageFacade.getProjectionForPlayList();
        return context.getContentResolver().query(OdklProvider.playListUri(), (String[]) projection.toArray(new String[projection.size()]), null, null, "RANDOM()");
    }

    private static Track getTrackFromCursor(Cursor cursor) {
        return MusicStorageFacade.cursor2Track(cursor);
    }

    public void saveCurrentTrackId() {
        saveTrackId(this.context, this.cacheTrackCursor.getTrack());
    }

    public synchronized void close() {
        if (this.cacheTrackCursor != null) {
            this.cacheTrackCursor.close();
        }
    }

    public synchronized void onPlaylistChanged() {
        this.tracksReady = true;
        int oldPosition = -1;
        long oldTrackId = -1;
        boolean shuffle = isShuffle();
        try {
            oldPosition = this.cacheTrackCursor.getPosition();
            oldTrackId = this.cacheTrackCursor.getTrackId();
            this.cacheTrackCursor = new CacheTrackCursorImp(shuffle ? getShufflePlayListCursor(this.context) : getPlayListCursor(this.context));
        } catch (CursorIsNullException e) {
            Logger.m176e("Cursor can't be null here");
        }
        if (shuffle) {
            do {
                if (!this.cacheTrackCursor.next()) {
                    this.cacheTrackCursor.moveToStart();
                }
            } while (this.cacheTrackCursor.getTrack().id != oldTrackId);
        } else if (oldPosition == -1 || !this.cacheTrackCursor.moveToPosition(oldPosition)) {
            Logger.m176e("Can't find current track in db playlist.");
            this.cacheTrackCursor.moveToStart();
        }
    }

    public static int generateToken() {
        return new Random().nextInt(Integer.MAX_VALUE);
    }
}
