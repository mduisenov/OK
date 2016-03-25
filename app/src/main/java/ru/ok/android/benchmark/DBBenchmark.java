package ru.ok.android.benchmark;

import java.util.concurrent.TimeUnit;
import ru.ok.android.benchmark.Benchmarks.Callback;
import ru.ok.android.onelog.OneLog;
import ru.ok.onelog.android.db.DbCreateOperationFactory;
import ru.ok.onelog.android.db.DbUpgradeOperationFactory;
import ru.ok.onelog.android.db.DbUpgradeOperationType;
import ru.ok.onelog.builtin.DurationInterval;

public class DBBenchmark {
    private static final String[] CREATE_SEQUENCE;
    private static final String[][] FINISH_CREATE_SEQS;
    private static final String[][] FINISH_UPGRADE_SEQS;
    private static final String[] UPGRADE_SEQUENCE_DEFAULT_ABNORMAL;
    private static final String[] UPGRADE_SEQUENCE_DEFAULT_OK;
    private static final String[] UPGRADE_SEQUENCE_OLD_ABNORMAL;
    private static final String[] UPGRADE_SEQUENCE_OLD_OK;
    private static final Callback createCallback;
    private static final Callback upgradeCallback;

    /* renamed from: ru.ok.android.benchmark.DBBenchmark.1 */
    static class C02271 implements Runnable {
        final /* synthetic */ CheckPoint val$finish;

        C02271(CheckPoint checkPoint) {
            this.val$finish = checkPoint;
        }

        public void run() {
            Benchmarks.findSequences(this.val$finish, DBBenchmark.FINISH_CREATE_SEQS, DBBenchmark.createCallback);
        }
    }

    /* renamed from: ru.ok.android.benchmark.DBBenchmark.2 */
    static class C02282 implements Runnable {
        final /* synthetic */ CheckPoint val$finish;

        C02282(CheckPoint checkPoint) {
            this.val$finish = checkPoint;
        }

        public void run() {
            Benchmarks.findSequences(this.val$finish, DBBenchmark.FINISH_UPGRADE_SEQS, DBBenchmark.upgradeCallback);
        }
    }

    /* renamed from: ru.ok.android.benchmark.DBBenchmark.3 */
    static class C02293 implements Callback {
        C02293() {
        }

        public void onFoundSequence(String[] sequence, CheckPoint[] checkPoints) {
            if (sequence == DBBenchmark.CREATE_SEQUENCE) {
                Integer version;
                CheckPoint startCheckPoint = (checkPoints == null || checkPoints.length <= 0) ? null : checkPoints[0];
                Object extra = startCheckPoint == null ? null : startCheckPoint.extra;
                if (extra instanceof Integer) {
                    version = (Integer) extra;
                } else {
                    version = null;
                }
                if (version != null) {
                    long durationNano = checkPoints[1].time - checkPoints[0].time;
                    OneLog.log(DbCreateOperationFactory.get(durationNano, DurationInterval.valueOf(durationNano, TimeUnit.NANOSECONDS), version.intValue()));
                }
            }
        }
    }

    /* renamed from: ru.ok.android.benchmark.DBBenchmark.4 */
    static class C02304 implements Callback {
        C02304() {
        }

        public void onFoundSequence(String[] sequence, CheckPoint[] checkPoints) {
            CheckPoint startCheckPoint = (checkPoints == null || checkPoints.length <= 0) ? null : checkPoints[0];
            Object extra = startCheckPoint == null ? null : startCheckPoint.extra;
            int[] versions = extra instanceof int[] ? (int[]) extra : null;
            if (versions != null && versions.length == 2) {
                int oldVersion = versions[0];
                int newVersion = versions[1];
                if (sequence == DBBenchmark.UPGRADE_SEQUENCE_DEFAULT_OK) {
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_basic, checkPoints, 0, 1, oldVersion, newVersion);
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_delete_old, checkPoints, 1, 2, oldVersion, newVersion);
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_after_upgrade, checkPoints, 2, 3, oldVersion, newVersion);
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_check_ok, checkPoints, 3, 4, oldVersion, newVersion);
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_normal, checkPoints, 0, 5, oldVersion, newVersion);
                } else if (sequence == DBBenchmark.UPGRADE_SEQUENCE_DEFAULT_ABNORMAL) {
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_basic, checkPoints, 0, 1, oldVersion, newVersion);
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_delete_old, checkPoints, 1, 2, oldVersion, newVersion);
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_after_upgrade, checkPoints, 2, 3, oldVersion, newVersion);
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_check_failed, checkPoints, 3, 4, oldVersion, newVersion);
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_drop_all, checkPoints, 4, 5, oldVersion, newVersion);
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_recreate, checkPoints, 5, 6, oldVersion, newVersion);
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_abnormal, checkPoints, 0, 7, oldVersion, newVersion);
                } else if (sequence == DBBenchmark.UPGRADE_SEQUENCE_OLD_OK) {
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_check_ok, checkPoints, 2, 3, oldVersion, newVersion);
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_normal_47, checkPoints, 0, 4, oldVersion, newVersion);
                } else if (sequence == DBBenchmark.UPGRADE_SEQUENCE_OLD_ABNORMAL) {
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_check_failed, checkPoints, 2, 3, oldVersion, newVersion);
                    DBBenchmark.reportUpgradeOperation(DbUpgradeOperationType.android_db_upgrade_abnormal_47, checkPoints, 0, 6, oldVersion, newVersion);
                }
            }
        }
    }

    public static int startCreate(int dbVersion) {
        return Benchmarks.checkPoint("db.create.start", Integer.valueOf(dbVersion)).sequenceId;
    }

    public static void finishCreate(int sequenceId) {
        Benchmarks.benchmarkBgExecutor.execute(new C02271(Benchmarks.checkPoint("db.create.finish", sequenceId)));
    }

    public static int startUpgrade(int oldVersion, int newVersion) {
        return Benchmarks.checkPoint("db.upgrade.start", new int[]{oldVersion, newVersion}).sequenceId;
    }

    public static void finishedBasicUpgrade(int sequenceId) {
        Benchmarks.checkPoint("db.upgrade.basic", sequenceId);
    }

    public static void finishedAfterUpgrade(int sequenceId) {
        Benchmarks.checkPoint("db.upgrade.after.upgrade", sequenceId);
    }

    public static void finishedDropOldTables(int sequenceId) {
        Benchmarks.checkPoint("db.upgrade.drop.old", sequenceId);
    }

    public static void finishedDropOnRecreate(int sequenceId) {
        Benchmarks.checkPoint("db.upgrade.recreate.drop", sequenceId);
    }

    public static void finishRecreateOnUpgrade(int sequenceId) {
        Benchmarks.checkPoint("db.upgrade.recreate.finish", sequenceId);
    }

    public static void upgradeCheck(int sequenceId, boolean isSchemaValid) {
        Benchmarks.checkPoint(isSchemaValid ? "db.upgrade.check.ok" : "db.upgrade.check.failed", sequenceId);
    }

    public static void finishUpgrade(int sequenceId) {
        Benchmarks.benchmarkBgExecutor.execute(new C02282(Benchmarks.checkPoint("db.upgrade.finish", sequenceId)));
    }

    static {
        createCallback = new C02293();
        upgradeCallback = new C02304();
        CREATE_SEQUENCE = new String[]{"db.create.start", "db.create.finish"};
        UPGRADE_SEQUENCE_DEFAULT_OK = new String[]{"db.upgrade.start", "db.upgrade.basic", "db.upgrade.drop.old", "db.upgrade.after.upgrade", "db.upgrade.check.ok", "db.upgrade.finish"};
        UPGRADE_SEQUENCE_DEFAULT_ABNORMAL = new String[]{"db.upgrade.start", "db.upgrade.basic", "db.upgrade.drop.old", "db.upgrade.after.upgrade", "db.upgrade.check.failed", "db.upgrade.recreate.drop", "db.upgrade.recreate.finish", "db.upgrade.finish"};
        UPGRADE_SEQUENCE_OLD_OK = new String[]{"db.upgrade.start", "db.upgrade.recreate.drop", "db.upgrade.recreate.finish", "db.upgrade.check.ok", "db.upgrade.finish"};
        UPGRADE_SEQUENCE_OLD_ABNORMAL = new String[]{"db.upgrade.start", "db.upgrade.recreate.drop", "db.upgrade.recreate.finish", "db.upgrade.check.failed", "db.upgrade.recreate.drop", "db.upgrade.recreate.finish", "db.upgrade.finish"};
        FINISH_CREATE_SEQS = new String[][]{CREATE_SEQUENCE};
        FINISH_UPGRADE_SEQS = new String[][]{UPGRADE_SEQUENCE_DEFAULT_OK, UPGRADE_SEQUENCE_DEFAULT_ABNORMAL, UPGRADE_SEQUENCE_OLD_OK, UPGRADE_SEQUENCE_OLD_ABNORMAL};
    }

    private static void reportUpgradeOperation(DbUpgradeOperationType op, CheckPoint[] checkPoints, int startIndex, int endIndex, int oldVersion, int newVersion) {
        long durationNano = checkPoints[endIndex].time - checkPoints[startIndex].time;
        OneLog.log(DbUpgradeOperationFactory.get(op, durationNano, DurationInterval.valueOf(durationNano, TimeUnit.NANOSECONDS), newVersion, oldVersion));
    }
}
