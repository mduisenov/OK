package ru.ok.android.db.access;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.db.provider.OdklContract.PromoLinks;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.banner.BannerBuilder;
import ru.ok.model.stream.banner.PromoLinkBuilder;

public class PromoLinkStorageFacade {
    private static final String[] PROJECTION_STATS;
    private static int promoLinkFetchedTimeIndex;
    private static int promoLinkIdIndex;
    private static int promoLinkTypeIndex;
    private static String[] promoLinksColumns;

    static {
        PROJECTION_STATS = new String[]{"adst_type", "adst_url", "adst_trgt_id"};
    }

    public static void getInsertPromoLinkOps(PromoLinkBuilder promoLink, List<ContentProviderOperation> outOps) {
        Logger.m173d("promoLink=%s", promoLink);
        BannerBuilder banner = promoLink == null ? null : promoLink.getBanner();
        if (promoLink == null || outOps == null || banner == null) {
            Logger.m185w("Invalid parameters: promoLink=%s outOps=%s banner=%s", promoLink, outOps, banner);
            return;
        }
        BannerStorageFacade.getInsertBannerOps(promoLink.getBanner(), outOps);
        Builder insertPromoLink = ContentProviderOperation.newInsert(PromoLinks.getSilentContentUri());
        insertPromoLink.withValue("pmlk_type", Integer.valueOf(promoLink.getType()));
        String fid = promoLink.getFriendId();
        if (fid != null) {
            insertPromoLink.withValue("pmlk_id", fid);
        }
        insertPromoLink.withValue("pmlk_banner_id", banner.getId());
        outOps.add(insertPromoLink.build());
    }

    public static void remove(ContentResolver cr, int type, String id) {
        String selection;
        String[] selectionArgs;
        if (id == null) {
            selection = "pmlk_type=?";
            selectionArgs = new String[]{Integer.toString(type)};
        } else {
            selection = "pmlk_type=? AND pmlk_id=?";
            selectionArgs = new String[]{Integer.toString(type), id};
        }
        cr.delete(PromoLinks.getSilentContentUri(), selection, selectionArgs);
    }

    public static ArrayList<PromoLinkBuilder> queryPromoLinks(ContentResolver cr, int... types) {
        String selection;
        String[] selectionArgs;
        String str = ">>> types=%s";
        Object[] objArr = new Object[1];
        objArr[0] = Logger.isLoggingEnable() ? Arrays.toString(types) : null;
        Logger.m173d(str, objArr);
        if (types == null || types.length <= 0) {
            selection = null;
            selectionArgs = null;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("pmlk_type").append(" IN (");
            selectionArgs = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append('?');
                selectionArgs[i] = Integer.toString(types[i]);
            }
            sb.append(')');
            selection = sb.toString();
        }
        return queryPromoLinks(cr, selection, selectionArgs);
    }

    public static ArrayList<PromoLinkBuilder> queryPromoLinks(ContentResolver cr, int type, String id) {
        String selection;
        String[] selectionArgs;
        Logger.m173d(">>> type=%d id=%s", Integer.valueOf(type), id);
        if (id == null) {
            selection = "pmlk_type=?";
            selectionArgs = new String[]{Integer.toString(type)};
        } else {
            selection = "pmlk_type=? AND pmlk_id=?";
            selectionArgs = new String[]{Integer.toString(type), id};
        }
        return queryPromoLinks(cr, selection, selectionArgs);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.ArrayList<ru.ok.model.stream.banner.PromoLinkBuilder> queryPromoLinks(android.content.ContentResolver r20, java.lang.String r21, java.lang.String[] r22) {
        /*
        r9 = 0;
        r17 = 0;
        r3 = ru.ok.android.db.provider.OdklContract.PromoLinks.getContentUri();	 Catch:{ Exception -> 0x00aa }
        r4 = getPromoLinksColumns();	 Catch:{ Exception -> 0x00aa }
        r7 = 0;
        r2 = r20;
        r5 = r21;
        r6 = r22;
        r9 = r2.query(r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x00aa }
        if (r9 == 0) goto L_0x006c;
    L_0x0018:
        r2 = r9.moveToFirst();	 Catch:{ Exception -> 0x00aa }
        if (r2 == 0) goto L_0x006c;
    L_0x001e:
        r18 = r17;
    L_0x0020:
        r2 = r9.isAfterLast();	 Catch:{ Exception -> 0x00db, all -> 0x00d7 }
        if (r2 != 0) goto L_0x006a;
    L_0x0026:
        r14 = new ru.ok.model.stream.banner.PromoLinkBuilder;	 Catch:{ FeedObjectException -> 0x005e }
        r14.<init>();	 Catch:{ FeedObjectException -> 0x005e }
        r2 = promoLinkFetchedTimeIndex;	 Catch:{ FeedObjectException -> 0x005e }
        r2 = r9.getLong(r2);	 Catch:{ FeedObjectException -> 0x005e }
        r14.setFetchedTime(r2);	 Catch:{ FeedObjectException -> 0x005e }
        r2 = promoLinkTypeIndex;	 Catch:{ FeedObjectException -> 0x005e }
        r16 = r9.getInt(r2);	 Catch:{ FeedObjectException -> 0x005e }
        r0 = r16;
        r14.setType(r0);	 Catch:{ FeedObjectException -> 0x005e }
        r2 = promoLinkIdIndex;	 Catch:{ FeedObjectException -> 0x005e }
        r2 = r9.getString(r2);	 Catch:{ FeedObjectException -> 0x005e }
        r14.setFriendId(r2);	 Catch:{ FeedObjectException -> 0x005e }
        r8 = ru.ok.android.db.access.BannerStorageFacade.parseBannerWithPics(r9);	 Catch:{ FeedObjectException -> 0x005e }
        r14.setBanner(r8);	 Catch:{ FeedObjectException -> 0x005e }
        if (r18 != 0) goto L_0x00e1;
    L_0x0051:
        r17 = new java.util.ArrayList;	 Catch:{ FeedObjectException -> 0x005e }
        r17.<init>();	 Catch:{ FeedObjectException -> 0x005e }
    L_0x0056:
        r0 = r17;
        r0.add(r14);	 Catch:{ FeedObjectException -> 0x00df }
        r18 = r17;
        goto L_0x0020;
    L_0x005e:
        r12 = move-exception;
        r17 = r18;
    L_0x0061:
        r2 = "Failed to parse banner";
        ru.ok.android.utils.Logger.m179e(r12, r2);	 Catch:{ Exception -> 0x00aa }
        r18 = r17;
        goto L_0x0020;
    L_0x006a:
        r17 = r18;
    L_0x006c:
        ru.ok.android.utils.IOUtils.closeSilently(r9);
    L_0x006f:
        if (r17 == 0) goto L_0x00c3;
    L_0x0071:
        r19 = new java.lang.StringBuilder;
        r19.<init>();
        r15 = new android.util.SparseArray;
        r15.<init>();
        r13 = r17.iterator();
    L_0x007f:
        r2 = r13.hasNext();
        if (r2 == 0) goto L_0x00ba;
    L_0x0085:
        r14 = r13.next();
        r14 = (ru.ok.model.stream.banner.PromoLinkBuilder) r14;
        r2 = r19.length();
        if (r2 <= 0) goto L_0x0098;
    L_0x0091:
        r2 = 44;
        r0 = r19;
        r0.append(r2);
    L_0x0098:
        r2 = r14.getBanner();
        r10 = r2.getDbId();
        r0 = r19;
        r0.append(r10);
        r2 = (int) r10;
        r15.put(r2, r14);
        goto L_0x007f;
    L_0x00aa:
        r12 = move-exception;
    L_0x00ab:
        r2 = "Failed to query promo link";
        ru.ok.android.utils.Logger.m179e(r12, r2);	 Catch:{ all -> 0x00b5 }
        ru.ok.android.utils.IOUtils.closeSilently(r9);
        goto L_0x006f;
    L_0x00b5:
        r2 = move-exception;
    L_0x00b6:
        ru.ok.android.utils.IOUtils.closeSilently(r9);
        throw r2;
    L_0x00ba:
        r2 = r19.toString();
        r0 = r20;
        queryAdStats(r0, r2, r15);
    L_0x00c3:
        if (r17 != 0) goto L_0x00ca;
    L_0x00c5:
        r17 = new java.util.ArrayList;
        r17.<init>();
    L_0x00ca:
        r2 = "<<< promoLinks=%s";
        r3 = 1;
        r3 = new java.lang.Object[r3];
        r4 = 0;
        r3[r4] = r17;
        ru.ok.android.utils.Logger.m173d(r2, r3);
        return r17;
    L_0x00d7:
        r2 = move-exception;
        r17 = r18;
        goto L_0x00b6;
    L_0x00db:
        r12 = move-exception;
        r17 = r18;
        goto L_0x00ab;
    L_0x00df:
        r12 = move-exception;
        goto L_0x0061;
    L_0x00e1:
        r17 = r18;
        goto L_0x0056;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.db.access.PromoLinkStorageFacade.queryPromoLinks(android.content.ContentResolver, java.lang.String, java.lang.String[]):java.util.ArrayList<ru.ok.model.stream.banner.PromoLinkBuilder>");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void queryAdStats(android.content.ContentResolver r16, java.lang.String r17, android.util.SparseArray<ru.ok.model.stream.banner.PromoLinkBuilder> r18) {
        /*
        r8 = 0;
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0049 }
        r2.<init>();	 Catch:{ Exception -> 0x0049 }
        r3 = "adst_trgt_type=2 AND adst_trgt_id IN (";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x0049 }
        r0 = r17;
        r2 = r2.append(r0);	 Catch:{ Exception -> 0x0049 }
        r3 = ")";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x0049 }
        r5 = r2.toString();	 Catch:{ Exception -> 0x0049 }
        r3 = ru.ok.android.db.provider.OdklContract.AdStatistics.getContentUri();	 Catch:{ Exception -> 0x0049 }
        r4 = PROJECTION_STATS;	 Catch:{ Exception -> 0x0049 }
        r6 = 0;
        r7 = 0;
        r2 = r16;
        r8 = r2.query(r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0049 }
        if (r8 == 0) goto L_0x0073;
    L_0x002e:
        r2 = r8.moveToFirst();	 Catch:{ Exception -> 0x0049 }
        if (r2 == 0) goto L_0x0073;
    L_0x0034:
        r2 = r8.isAfterLast();	 Catch:{ Exception -> 0x0049 }
        if (r2 != 0) goto L_0x0073;
    L_0x003a:
        r2 = 0;
        r14 = r8.getInt(r2);	 Catch:{ Exception -> 0x0049 }
        if (r14 < 0) goto L_0x0045;
    L_0x0041:
        r2 = 29;
        if (r14 < r2) goto L_0x0054;
    L_0x0045:
        r8.moveToNext();	 Catch:{ Exception -> 0x0049 }
        goto L_0x0034;
    L_0x0049:
        r9 = move-exception;
        r2 = "Failed to query ad stats";
        ru.ok.android.utils.Logger.m179e(r9, r2);	 Catch:{ all -> 0x006e }
        ru.ok.android.utils.IOUtils.closeSilently(r8);
    L_0x0053:
        return;
    L_0x0054:
        r12 = r14;
        r2 = 1;
        r15 = r8.getString(r2);	 Catch:{ Exception -> 0x0049 }
        r2 = 2;
        r10 = r8.getLong(r2);	 Catch:{ Exception -> 0x0049 }
        r2 = (int) r10;	 Catch:{ Exception -> 0x0049 }
        r0 = r18;
        r13 = r0.get(r2);	 Catch:{ Exception -> 0x0049 }
        r13 = (ru.ok.model.stream.banner.PromoLinkBuilder) r13;	 Catch:{ Exception -> 0x0049 }
        if (r13 == 0) goto L_0x0045;
    L_0x006a:
        r13.addStatPixel(r12, r15);	 Catch:{ Exception -> 0x0049 }
        goto L_0x0045;
    L_0x006e:
        r2 = move-exception;
        ru.ok.android.utils.IOUtils.closeSilently(r8);
        throw r2;
    L_0x0073:
        ru.ok.android.utils.IOUtils.closeSilently(r8);
        goto L_0x0053;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.db.access.PromoLinkStorageFacade.queryAdStats(android.content.ContentResolver, java.lang.String, android.util.SparseArray):void");
    }

    private static String[] getPromoLinksColumns() {
        if (promoLinksColumns == null) {
            List<String> columns = new ArrayList(Arrays.asList(BannerStorageFacade.PROJECTION_BANNER));
            promoLinkFetchedTimeIndex = columns.size();
            columns.add("_last_update");
            promoLinkTypeIndex = columns.size();
            columns.add("pmlk_type");
            promoLinkIdIndex = columns.size();
            columns.add("pmlk_id");
            promoLinksColumns = (String[]) columns.toArray(new String[columns.size()]);
        }
        return promoLinksColumns;
    }
}
