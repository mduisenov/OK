package ru.ok.android.services.processors.offline;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import ru.ok.android.db.base.OfflineTable.Status;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.exception.NoConnectionException;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.exceptions.ServerReturnErrorException;
import ru.ok.java.api.json.JsonResultParser;
import ru.ok.java.api.request.BaseRequest;

public abstract class OfflineBaseSendProcessor<T, R extends BaseRequest> extends CommandProcessor {
    private static final List<Uri> currentlyLockedMessages;
    private static final Object lock;

    public interface MessageProcessStrategy<Req, Res> {
        JsonResultParser<Res> createParser(JsonHttpResult jsonHttpResult);

        Req createRequest(Cursor cursor);

        void fillValuesByResult(ContentValues contentValues, Res res);

        void onItemPostUpdate(Context context, Map<String, String> map, Res res);

        void removeExistingDuplicates(Map<String, String> map, Res res);
    }

    private class RequestHolder {
        final long date;
        final Map<String, String> id;
        final R request;
        final MessageProcessStrategy strategy;

        public RequestHolder(MessageProcessStrategy<? extends R, ? extends T> strategy, R request, Map<String, String> id, long date) {
            this.strategy = strategy;
            this.request = request;
            this.id = id;
            this.date = date;
        }
    }

    protected abstract Uri contentUri(Intent intent);

    protected abstract MessageProcessStrategy<? extends R, ? extends T> createStrategy(Map<String, String> map);

    protected abstract void idColumns(Set<String> set);

    protected abstract boolean isMultipleSendingAllowed();

    protected abstract Uri itemUri(Map<String, String> map);

    protected abstract void onItemFailed(Context context, Map<String, String> map);

    protected abstract void onItemFailedServer(Context context, Map<String, String> map, R r, ErrorType errorType);

    protected abstract void onItemOverdue(Context context, Map<String, String> map, R r);

    protected abstract void onItemSuccess(Context context, Map<String, String> map);

    protected abstract String[] projection();

    static {
        currentlyLockedMessages = new CopyOnWriteArrayList();
        lock = new Object();
    }

    protected OfflineBaseSendProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected final int doCommand(android.content.Context r12, android.content.Intent r13, android.os.Bundle r14) throws java.lang.Exception {
        /*
        r11 = this;
        r10 = 0;
        r9 = 1;
        r6 = ru.ok.android.utils.NetUtils.isConnectionAvailable(r12, r10);
        if (r6 != 0) goto L_0x001b;
    L_0x0008:
        r6 = "Can't send anything, no connection available. %s";
        r7 = new java.lang.Object[r9];
        r8 = r11.getClass();
        r8 = r8.getSimpleName();
        r7[r10] = r8;
        ru.ok.android.utils.Logger.m185w(r6, r7);
    L_0x001a:
        return r9;
    L_0x001b:
        ru.ok.android.offline.OfflineAlarmHelper.unScheduleNextAttempt(r12);
        r0 = r11.contentUri(r13);
    L_0x0022:
        r7 = lock;
        monitor-enter(r7);
        r2 = new java.util.ArrayList;	 Catch:{ all -> 0x005a }
        r2.<init>();	 Catch:{ all -> 0x005a }
        r11.addLockedMessages(r12, r0, r2);	 Catch:{ all -> 0x005a }
        r11.addWaitingMessages(r12, r0, r2);	 Catch:{ all -> 0x005a }
        monitor-exit(r7);	 Catch:{ all -> 0x005a }
        r4 = 0;
        r3 = r2.iterator();
    L_0x0036:
        r6 = r3.hasNext();
        if (r6 == 0) goto L_0x0062;
    L_0x003c:
        r1 = r3.next();
        r1 = (ru.ok.android.services.processors.offline.OfflineBaseSendProcessor.RequestHolder) r1;
        if (r4 == 0) goto L_0x005d;
    L_0x0044:
        r6 = new android.content.ContentValues;
        r6.<init>();
        r7 = ru.ok.android.db.base.OfflineTable.Status.WAITING;
        r11.updateItemWithStatus(r12, r1, r6, r7);
    L_0x004e:
        r6 = currentlyLockedMessages;
        r7 = r1.id;
        r7 = r11.itemUri(r7);
        r6.remove(r7);
        goto L_0x0036;
    L_0x005a:
        r6 = move-exception;
        monitor-exit(r7);	 Catch:{ all -> 0x005a }
        throw r6;
    L_0x005d:
        r4 = r11.processMessage(r12, r1);
        goto L_0x004e;
    L_0x0062:
        r5 = r2.size();
        if (r5 <= 0) goto L_0x0070;
    L_0x0068:
        r6 = r11.isMultipleSendingAllowed();
        if (r6 == 0) goto L_0x0070;
    L_0x006e:
        if (r4 == 0) goto L_0x0022;
    L_0x0070:
        if (r4 == 0) goto L_0x001a;
    L_0x0072:
        ru.ok.android.offline.OfflineAlarmHelper.scheduleNextAttempt(r12);
        goto L_0x001a;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.processors.offline.OfflineBaseSendProcessor.doCommand(android.content.Context, android.content.Intent, android.os.Bundle):int");
    }

    private void addWaitingMessages(Context context, Uri contentUri, List<ru.ok.android.services.processors.offline.OfflineBaseSendProcessor$ru.ok.android.services.processors.offline.OfflineBaseSendProcessor$ru.ok.android.services.processors.offline.OfflineBaseSendProcessor.RequestHolder> holders) {
        Cursor cursorWaiting = queryWaiting(context, contentUri, isMultipleSendingAllowed());
        if (cursorWaiting != null) {
            try {
                for (RequestHolder holder : cursor2Requests(cursorWaiting)) {
                    if (processHolderAttach(context, holder.id)) {
                        holders.add(holder);
                        currentlyLockedMessages.add(itemUri(holder.id));
                        updateStatus(context, holder, Status.LOCKED);
                    }
                }
            } finally {
                cursorWaiting.close();
            }
        }
    }

    private void addLockedMessages(Context context, Uri contentUri, List<ru.ok.android.services.processors.offline.OfflineBaseSendProcessor$ru.ok.android.services.processors.offline.OfflineBaseSendProcessor$ru.ok.android.services.processors.offline.OfflineBaseSendProcessor.RequestHolder> holders) {
        Cursor cursorLocked = queryLocked(context, contentUri);
        if (cursorLocked != null) {
            try {
                for (RequestHolder request : cursor2Requests(cursorLocked)) {
                    Uri uri = itemUri(request.id);
                    if (!currentlyLockedMessages.contains(uri)) {
                        Logger.m185w("Message with LOCKED or SENDING status absent in static data. Add it to queue: %s", uri);
                        holders.add(request);
                        currentlyLockedMessages.add(uri);
                    }
                }
            } finally {
                cursorLocked.close();
            }
        }
    }

    protected boolean processHolderAttach(Context context, Map<String, String> map) {
        return true;
    }

    private boolean processMessage(Context context, ru.ok.android.services.processors.offline.OfflineBaseSendProcessor$ru.ok.android.services.processors.offline.OfflineBaseSendProcessor.RequestHolder holder) {
        updateItemWithStatus(context, holder, new ContentValues(), Status.SENDING);
        try {
            Logger.m173d("Start sending: %s", holder.id);
            JsonHttpResult result = executeHttpMethod(holder.request);
            Logger.m173d("Result ok: %s", holder.id);
            updateItemByResult(context, holder, result);
            return false;
        } catch (ServerReturnErrorException e) {
            Logger.m180e(e, "Server error: %s, %s", holder.id, e.getErrorMessage());
            updateItemByServerError(context, holder, e);
            return false;
        } catch (NoConnectionException e2) {
            Logger.m180e(e2, "No connection: %s", holder.id);
            updateItemWithStatus(context, holder, new ContentValues(), Status.WAITING);
            return true;
        } catch (Exception e3) {
            Logger.m180e(e3, "Other error: %s", holder.id);
            updateItemByFail(context, holder);
            return false;
        }
    }

    private void updateItemByFail(Context context, ru.ok.android.services.processors.offline.OfflineBaseSendProcessor$ru.ok.android.services.processors.offline.OfflineBaseSendProcessor.RequestHolder holder) {
        Status status = System.currentTimeMillis() - holder.date > 180000 ? Status.OVERDUE : Status.FAILED;
        Logger.m172d(holder.id + ", status: " + status);
        ContentValues cv = new ContentValues();
        cv.put("failure_reason", ErrorType.GENERAL.name());
        updateItemWithStatus(context, holder, cv, status);
        if (status == Status.OVERDUE) {
            onItemOverdue(context, holder.id, holder.request);
        } else {
            onItemFailed(context, holder.id);
        }
    }

    private void updateItemByServerError(Context context, ru.ok.android.services.processors.offline.OfflineBaseSendProcessor$ru.ok.android.services.processors.offline.OfflineBaseSendProcessor.RequestHolder holder, ServerReturnErrorException e) {
        ErrorType reason = ErrorType.fromServerException(e);
        ContentValues cv = new ContentValues();
        cv.put("failure_reason", reason.name());
        updateItemWithStatus(context, holder, cv, Status.SERVER_ERROR);
        onItemFailedServer(context, holder.id, holder.request, reason);
    }

    private void updateItemByResult(Context context, ru.ok.android.services.processors.offline.OfflineBaseSendProcessor$ru.ok.android.services.processors.offline.OfflineBaseSendProcessor.RequestHolder holder, JsonHttpResult result) throws ResultParsingException {
        JsonResultParser<? extends T> parser = holder.strategy.createParser(result);
        ContentValues cv = new ContentValues();
        T parsed = parser.parse();
        holder.strategy.fillValuesByResult(cv, parsed);
        cv.put("failure_reason", (String) null);
        holder.strategy.removeExistingDuplicates(holder.id, parsed);
        updateItemWithStatus(context, holder, cv, Status.SENT);
        holder.strategy.onItemPostUpdate(context, holder.id, parsed);
        onItemSuccess(context, holder.id);
    }

    private void updateItemWithStatus(Context context, ru.ok.android.services.processors.offline.OfflineBaseSendProcessor$ru.ok.android.services.processors.offline.OfflineBaseSendProcessor.RequestHolder holder, ContentValues cv, Status status) {
        cv.put(NotificationCompat.CATEGORY_STATUS, status.name());
        cv.put("_date", Long.valueOf(System.currentTimeMillis()));
        Logger.m172d(String.format("uri = %s, status: %s, id: ", new Object[]{itemUri(holder.id), status, holder.id}));
        context.getContentResolver().update(uri, cv, null, null);
    }

    private JsonHttpResult executeHttpMethod(BaseRequest requests) throws Exception {
        return this._transportProvider.execJsonHttpMethod(requests);
    }

    private List<ru.ok.android.services.processors.offline.OfflineBaseSendProcessor$ru.ok.android.services.processors.offline.OfflineBaseSendProcessor.RequestHolder> cursor2Requests(Cursor cursor) {
        List<RequestHolder> holders = new ArrayList();
        Set<String> idColumns = new HashSet();
        idColumns(idColumns);
        Logger.m173d("id columns: %s", idColumns);
        while (cursor.moveToNext()) {
            String str = "item: %s";
            Object[] objArr = new Object[1];
            objArr[0] = Logger.isLoggingEnable() ? DatabaseUtils.dumpCurrentRowToString(cursor) : "";
            Logger.m173d(str, objArr);
            Map<String, String> ids = createIds(cursor, idColumns);
            MessageProcessStrategy<? extends R, ? extends T> strategy = createStrategy(ids);
            holders.add(new RequestHolder(strategy, (BaseRequest) strategy.createRequest(cursor), ids, extractDate(cursor)));
        }
        return holders;
    }

    private Map<String, String> createIds(Cursor cursor, Set<String> idColumns) {
        Map<String, String> ids = new HashMap();
        for (String column : idColumns) {
            ids.put(column, cursor.getString(cursor.getColumnIndex(column)));
        }
        return ids;
    }

    private long extractDate(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndex("_date"));
    }

    private Cursor queryWaiting(Context context, Uri contentUri, boolean autoResend) {
        long millis = System.currentTimeMillis() - 180000;
        List<Status> allowedStates = autoResend ? Status.AUTO_RESEND_POSSIBLE : Status.RESEND_POSSIBLE;
        String[] arguments = new String[(allowedStates.size() + 1)];
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < allowedStates.size()) {
            arguments[i] = ((Status) allowedStates.get(i)).name();
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append("?");
            i++;
        }
        arguments[i] = String.valueOf(millis);
        return context.getContentResolver().query(contentUri, projection(), "status IN (" + sb.toString() + ") AND " + "_date" + " > ?", arguments, "_date");
    }

    private Cursor queryLocked(Context context, Uri contentUri) {
        return context.getContentResolver().query(contentUri, projection(), "status = ? OR status = ?", new String[]{Status.LOCKED.name(), Status.SENDING.name()}, null);
    }

    private void updateStatus(Context context, ru.ok.android.services.processors.offline.OfflineBaseSendProcessor$ru.ok.android.services.processors.offline.OfflineBaseSendProcessor.RequestHolder holder, Status status) {
        updateItemWithStatus(context, holder, new ContentValues(), status);
    }
}
