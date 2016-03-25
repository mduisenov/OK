package ru.ok.android.db.access.music;

import android.content.Context;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ru.ok.model.wmf.Track;

public class AsyncStorageOperations {
    private static ExecutorService executor;

    /* renamed from: ru.ok.android.db.access.music.AsyncStorageOperations.1 */
    static class C02601 implements Runnable {
        final /* synthetic */ Context val$context;
        final /* synthetic */ int val$token;
        final /* synthetic */ Track[] val$tracks;

        C02601(Context context, Track[] trackArr, int i) {
            this.val$context = context;
            this.val$tracks = trackArr;
            this.val$token = i;
        }

        public void run() {
            MusicStorageFacade.savePlayList(this.val$context, this.val$tracks, this.val$token);
        }
    }

    /* renamed from: ru.ok.android.db.access.music.AsyncStorageOperations.2 */
    static class C02612 implements Runnable {
        final /* synthetic */ Context val$context;
        final /* synthetic */ String val$hash;
        final /* synthetic */ Track[] val$tracks;

        C02612(Context context, String str, Track[] trackArr) {
            this.val$context = context;
            this.val$hash = str;
            this.val$tracks = trackArr;
        }

        public void run() {
            MusicStorageFacade.insertTunerTracks(this.val$context, this.val$hash, this.val$tracks);
        }
    }

    static {
        executor = Executors.newSingleThreadExecutor();
    }

    public static void savePlaylist(Context context, Track[] tracks, int token) {
        executor.submit(new C02601(context, tracks, token));
    }

    public static int insertTunerTracks(Context context, String hash, Track[] tracks) {
        int token = new Random().nextInt(Integer.MAX_VALUE);
        executor.submit(new C02612(context, hash, tracks));
        return token;
    }
}
