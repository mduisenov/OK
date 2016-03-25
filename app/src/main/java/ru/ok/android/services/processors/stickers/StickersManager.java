package ru.ok.android.services.processors.stickers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.emoji.recents.SmileRecents;
import ru.ok.android.emoji.stickers.StickersSet;
import ru.ok.model.stickers.Sticker;
import ru.ok.model.stickers.StickerSet;
import ru.ok.model.stickers.StickersResponse;

public final class StickersManager {
    private static StickersPreferences prefs;
    private static volatile StickersResponse response;

    /* renamed from: ru.ok.android.services.processors.stickers.StickersManager.1 */
    static class C04931 implements Runnable {
        final /* synthetic */ Context val$context;
        final /* synthetic */ StickersResponse val$stickers;

        C04931(Context context, StickersResponse stickersResponse) {
            this.val$context = context;
            this.val$stickers = stickersResponse;
        }

        public void run() {
            StickersStorage.storeStickers(this.val$context, this.val$stickers);
        }
    }

    @Nullable
    public static StickersResponse getCurrentSet(Context context) {
        if (response == null) {
            response = StickersStorage.readStickers(context);
        }
        return response;
    }

    public static void updateStickersSet(Context context, StickersResponse stickers) {
        response = stickers;
        GlobalBus.post(new C04931(context, stickers), 2131623945);
    }

    @NonNull
    public static List<StickersSet> getCurrentSet4Lib(Context context) {
        StickersResponse stickers = getCurrentSet(context);
        if (stickers == null) {
            return Collections.emptyList();
        }
        List<StickersSet> result = new ArrayList();
        int lastSeenVersion = getLastSeenVersion(context);
        for (StickerSet set : stickers.sets) {
            List<String> codes = new ArrayList();
            for (Sticker sticker : set.stickers) {
                codes.add(sticker.code);
            }
            int i = set.id;
            String str = set.name;
            String str2 = set.iconUrl;
            int i2 = set.price;
            boolean z = lastSeenVersion > 0 && set.sinceVersion > lastSeenVersion;
            result.add(new StickersSet(i, str, str2, i2, z, codes, set.width, set.height));
        }
        return result;
    }

    public static int getLastSeenVersion(Context context) {
        return getPreferences(context).getLastSeenVersion();
    }

    public static void updateLastSeenVersion(Context context) {
        getPreferences(context).setLastSeenVersion(getCurrentSet(context).version);
    }

    public static void updatePaymentEndDate(Context context, long expirationDeltaMs) {
        getPreferences(context).getEditor().putLong("key-payment-end-date", System.currentTimeMillis() + expirationDeltaMs).putLong("last-set-update-ms", System.currentTimeMillis()).apply();
    }

    public static boolean isServicePaid(Context context) {
        return getPreferences(context).getPaymentEndDate() > System.currentTimeMillis() + 600000;
    }

    @NonNull
    private static StickersPreferences getPreferences(Context context) {
        if (prefs == null) {
            prefs = new StickersPreferences(context);
        }
        return prefs;
    }

    public static boolean isStickersEnabled(Context context) {
        return StickersSettingsHandler.isStickersEnabled(context);
    }

    public static boolean isSetFree(Context context, int setId) {
        StickersResponse sets = getCurrentSet(context);
        if (sets == null) {
            return false;
        }
        for (StickerSet set : sets.sets) {
            if (set.id == setId) {
                if (set.price == 0) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public static void clear(Context context) {
        response = null;
        StickersStorage.removeStickers(context);
        getPreferences(context).clear();
        prefs = null;
        SmileRecents.remove(context);
    }

    public static void callUpdateStickersSet(Context context) {
        if (System.currentTimeMillis() - getPreferences(context).getLastUpdateSetTimeMs() > 14400000) {
            GlobalBus.send(2131624103, null);
        }
    }
}
