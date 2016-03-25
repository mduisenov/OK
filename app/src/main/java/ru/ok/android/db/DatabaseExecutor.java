package ru.ok.android.db;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.utils.Logger;

public final class DatabaseExecutor {
    private static final Executor executor;
    private static final DatabaseExecutor instance;
    private final SQLiteDatabase db;

    /* renamed from: ru.ok.android.db.DatabaseExecutor.1 */
    class C02481 implements Runnable {
        final /* synthetic */ Runnable val$completeCallback;
        final /* synthetic */ DatabaseOperation val$operation;

        C02481(DatabaseOperation databaseOperation, Runnable runnable) {
            this.val$operation = databaseOperation;
            this.val$completeCallback = runnable;
        }

        public void run() {
            DatabaseExecutor.this.performOperation(this.val$operation, this.val$completeCallback);
        }
    }

    /* renamed from: ru.ok.android.db.DatabaseExecutor.2 */
    class C02492 implements Runnable {
        final /* synthetic */ CountDownLatch val$latch;
        final /* synthetic */ DatabaseOperation val$operation;

        C02492(DatabaseOperation databaseOperation, CountDownLatch countDownLatch) {
            this.val$operation = databaseOperation;
            this.val$latch = countDownLatch;
        }

        public void run() {
            try {
                DatabaseExecutor.this.performOperation(this.val$operation, null);
            } finally {
                this.val$latch.countDown();
            }
        }
    }

    public interface DatabaseOperation {
        void performOperation(SQLiteDatabase sQLiteDatabase);
    }

    static {
        instance = new DatabaseExecutor();
        executor = Executors.newSingleThreadExecutor();
    }

    public static DatabaseExecutor getInstance() {
        return instance;
    }

    private DatabaseExecutor() {
        this.db = OdnoklassnikiApplication.getDatabase(OdnoklassnikiApplication.getContext());
    }

    public void addOperation(DatabaseOperation operation) {
        addOperation(operation, null);
    }

    public void addOperation(DatabaseOperation operation, @Nullable Runnable completeCallback) {
        executor.execute(new C02481(operation, completeCallback));
    }

    public void addOperationSync(DatabaseOperation operation) {
        CountDownLatch latch = new CountDownLatch(1);
        executor.execute(new C02492(operation, latch));
        try {
            latch.await();
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void performOperation(ru.ok.android.db.DatabaseExecutor.DatabaseOperation r4, @android.support.annotation.Nullable java.lang.Runnable r5) {
        /*
        r3 = this;
        r1 = r3.db;
        ru.ok.android.db.SQLiteUtils.beginTransaction(r1);
        r1 = r3.db;	 Catch:{ Exception -> 0x001a }
        r4.performOperation(r1);	 Catch:{ Exception -> 0x001a }
        r1 = r3.db;	 Catch:{ Exception -> 0x001a }
        r1.setTransactionSuccessful();	 Catch:{ Exception -> 0x001a }
        if (r5 == 0) goto L_0x0014;
    L_0x0011:
        r5.run();	 Catch:{ Exception -> 0x001a }
    L_0x0014:
        r1 = r3.db;
        r1.endTransaction();
    L_0x0019:
        return;
    L_0x001a:
        r0 = move-exception;
        r1 = "Failed to perform database operation";
        ru.ok.android.utils.Logger.m179e(r0, r1);	 Catch:{ all -> 0x0027 }
        r1 = r3.db;
        r1.endTransaction();
        goto L_0x0019;
    L_0x0027:
        r1 = move-exception;
        r2 = r3.db;
        r2.endTransaction();
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.db.DatabaseExecutor.performOperation(ru.ok.android.db.DatabaseExecutor$DatabaseOperation, java.lang.Runnable):void");
    }
}
