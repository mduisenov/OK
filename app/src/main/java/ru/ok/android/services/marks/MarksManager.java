package ru.ok.android.services.marks;

import android.content.Context;
import android.support.v4.util.SimpleArrayMap;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.storage.Storages;
import ru.ok.android.utils.Logger;

public class MarksManager {
    private final Context context;
    private SimpleArrayMap<String, Integer> groupPhotoMarksMap;
    private SimpleArrayMap<String, Integer> userPhotoMarksMap;

    /* renamed from: ru.ok.android.services.marks.MarksManager.1 */
    class C04401 implements Runnable {
        C04401() {
        }

        public void run() {
            try {
                Storages.getInstance(MarksManager.this.context, OdnoklassnikiApplication.getCurrentUser().getId()).getStreamCache().trim(System.currentTimeMillis());
            } catch (Throwable e) {
                Logger.m179e(e, "Error trim cache on mark update");
            }
        }
    }

    public MarksManager(Context context) {
        this.context = context.getApplicationContext();
        this.userPhotoMarksMap = new SimpleArrayMap(5);
        this.groupPhotoMarksMap = new SimpleArrayMap(5);
    }

    public void userPhotoMarkUpdate(String photoId, int mark) {
        this.userPhotoMarksMap.put(photoId, mark);
        GlobalBus.post(new C04401(), 2131623944);
    }

    public int getSyncedUserPhotoMark(String photoId, int viewerMark) {
        Integer cachedMark = (Integer) this.userPhotoMarksMap.get(photoId);
        return cachedMark == null ? viewerMark : cachedMark;
    }
}
