package ru.ok.android.db.access;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import java.util.List;
import ru.ok.android.db.provider.OdklContract.AdStatistics;

public final class AdStatsStorageFacade {
    public static void getCleanAdStatOps(int targetType, int targetIdBackRef, List<ContentProviderOperation> outOps) {
        Builder cleanAdStats = ContentProviderOperation.newDelete(AdStatistics.getSilentContentUri());
        String[] args = new String[2];
        args[0] = Integer.toString(targetType);
        cleanAdStats.withSelection("adst_trgt_type=? AND adst_trgt_id=?", args);
        cleanAdStats.withSelectionBackReference(1, targetIdBackRef);
        outOps.add(cleanAdStats.build());
    }

    public static void getInsertAdStatOps(int targetType, int targetIdBackRef, int statType, List<String> urls, List<ContentProviderOperation> outOps) {
        for (String url : urls) {
            Builder insertAdStat = getInsertAdStatOp(targetType, statType, url);
            insertAdStat.withValueBackReference("adst_trgt_id", targetIdBackRef);
            outOps.add(insertAdStat.build());
        }
    }

    private static Builder getInsertAdStatOp(int targetType, int statType, String url) {
        return ContentProviderOperation.newInsert(AdStatistics.getSilentContentUri()).withValue("adst_trgt_type", Integer.valueOf(targetType)).withValue("adst_type", Integer.valueOf(statType)).withValue("adst_url", url);
    }
}
