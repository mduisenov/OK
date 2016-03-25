package ru.ok.android.db;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import ru.ok.android.benchmark.DBBenchmark;
import ru.ok.android.db.base.BaseTable;
import ru.ok.android.db.discussions.DiscussionsTable;
import ru.ok.android.db.group.GroupCountersTable;
import ru.ok.android.db.group.GroupInfoTable;
import ru.ok.android.db.group.GroupMembersTable;
import ru.ok.android.db.group.GroupSubscribeStreamTable;
import ru.ok.android.db.group.GroupUserStatusTable;
import ru.ok.android.db.messages.ConversationTable;
import ru.ok.android.db.messages.DiscussionCommentsTable;
import ru.ok.android.db.messages.MessageTable;
import ru.ok.android.db.messages.UserPrivacySettingsTable;
import ru.ok.android.db.music.AlbumsTable;
import ru.ok.android.db.music.ArtistsTable;
import ru.ok.android.db.music.Collection2UserTable;
import ru.ok.android.db.music.CollectionTracksTable;
import ru.ok.android.db.music.CollectionsTable;
import ru.ok.android.db.music.ExtensionMusicTable;
import ru.ok.android.db.music.FriendsMusicTable;
import ru.ok.android.db.music.HistoryMusicTable;
import ru.ok.android.db.music.PlayListTable;
import ru.ok.android.db.music.PopCollectionsTable;
import ru.ok.android.db.music.PopMusicTable;
import ru.ok.android.db.music.TracksTable;
import ru.ok.android.db.music.Tuner2ArtistTable;
import ru.ok.android.db.music.Tuner2TracksTable;
import ru.ok.android.db.music.TunersTable;
import ru.ok.android.db.music.UserMusicTable;
import ru.ok.android.db.photos.ImageUrlsTable;
import ru.ok.android.db.provider.DBFailureError;
import ru.ok.android.db.provider.OdklContract.Banners;
import ru.ok.android.db.provider.OdklContract.Groups;
import ru.ok.android.db.provider.OdklContract.PromoLinks;
import ru.ok.android.db.provider.OdklContract.Users;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.db.relatives.RelativesTable;
import ru.ok.android.db.stream.AdStatsTable;
import ru.ok.android.db.stream.BannersTable;
import ru.ok.android.db.stream.PromoLinksTable;
import ru.ok.android.db.stream.VideoBannerDataTable;
import ru.ok.android.db.stream.VideoStatsTable;
import ru.ok.android.db.users.AuthorizedUsersTable;
import ru.ok.android.db.users.FriendTable;
import ru.ok.android.db.users.MutualFriendsTable;
import ru.ok.android.db.users.UserCommunitiesTable;
import ru.ok.android.db.users.UserCountersTable;
import ru.ok.android.db.users.UserInterestsTable;
import ru.ok.android.db.users.UserPresentsTable;
import ru.ok.android.db.users.UserRelationInfoTable;
import ru.ok.android.db.users.UserSubscribeStreamTable;
import ru.ok.android.db.users.UserTable;
import ru.ok.android.db.users.UsersRelationsTable;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final Collection<BaseTable> baseTables;
    private static final Collection<String> tables2Delete;
    private final Context context;
    private volatile boolean firstCallToOpenDB;
    private volatile boolean onCreateOrOnUpgradeCalled;
    private volatile boolean retryToOpenCalled;

    /* renamed from: ru.ok.android.db.DataBaseHelper.1 */
    static class C02471 implements Runnable {
        final /* synthetic */ Context val$clearContext;

        C02471(Context context) {
            this.val$clearContext = context;
        }

        public void run() {
            DataBaseHelper.clearDB(this.val$clearContext);
        }
    }

    static {
        tables2Delete = Arrays.asList(new String[]{"discussion_comment_likes", "normalize", "attachment_to_message", "groups_skins", "users_skins", "conversations2users", "conversations_temporary", "attachments"});
        baseTables = Arrays.asList(new BaseTable[]{new DiscussionCommentsTable(), new UserTable(), new FriendTable(), new UsersRelationsTable(), new MessageTable(), new ConversationTable(), new ImageUrlsTable(), new UserPrivacySettingsTable(), new UserCountersTable(), new UserPresentsTable(), new UserRelationInfoTable(), new GroupCountersTable(), new UserCommunitiesTable(), new UserInterestsTable(), new GroupUserStatusTable(), new GroupMembersTable(), new RelativesTable(), new FriendsSuggestTable(), new CollectionsTable(), new Collection2UserTable(), new PlayListTable(), new TracksTable(), new ArtistsTable(), new AlbumsTable(), new GroupInfoTable(), new UserMusicTable(), new CollectionTracksTable(), new PopCollectionsTable(), new FriendsMusicTable(), new TunersTable(), new Tuner2ArtistTable(), new HistoryMusicTable(), new Tuner2TracksTable(), new PopMusicTable(), new ExtensionMusicTable(), new BannersTable(), new PromoLinksTable(), new AdStatsTable(), new UserSubscribeStreamTable(), new GroupSubscribeStreamTable(), new MutualFriendsTable(), new VideoBannerDataTable(), new VideoStatsTable(), new AuthorizedUsersTable(), new DiscussionsTable()});
    }

    public DataBaseHelper(Context context) {
        super(context, "odnklassniki.db", null, 81);
        this.firstCallToOpenDB = true;
        this.retryToOpenCalled = false;
        this.onCreateOrOnUpgradeCalled = false;
        this.context = context;
    }

    public synchronized SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase writableDatabase;
        Logger.m172d("");
        if (this.firstCallToOpenDB) {
            checkOpenDatabase();
            this.firstCallToOpenDB = false;
        }
        try {
            this.onCreateOrOnUpgradeCalled = false;
            writableDatabase = super.getWritableDatabase();
        } catch (Exception e) {
            reportDBFailure(this.context, "getWritableDatabase failed", e);
            writableDatabase = retryOpenDBOrFail(e, true);
        }
        return writableDatabase;
    }

    public synchronized SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase readableDatabase;
        Logger.m172d("");
        if (this.firstCallToOpenDB) {
            checkOpenDatabase();
            this.firstCallToOpenDB = false;
        }
        try {
            this.onCreateOrOnUpgradeCalled = false;
            readableDatabase = super.getReadableDatabase();
        } catch (Exception e) {
            reportDBFailure(this.context, "getReadableDatabase failed", e);
            readableDatabase = retryOpenDBOrFail(e, false);
        }
        return readableDatabase;
    }

    public void onOpen(SQLiteDatabase db) {
        Logger.m172d("");
        super.onOpen(db);
        if (VERSION.SDK_INT < 16) {
            onConfigure(db);
        }
    }

    public void onConfigure(SQLiteDatabase db) {
        Logger.m172d("");
        if (db == null) {
            Logger.m184w("DB is null");
            return;
        }
        superOnConfigure(db);
        enableWriteAheadLogging(db);
        enableForeignKeys(db);
    }

    @SuppressLint({"NewApi"})
    public void superOnConfigure(SQLiteDatabase db) {
        if (VERSION.SDK_INT >= 16) {
            super.onConfigure(db);
        }
    }

    private void enableWriteAheadLogging(SQLiteDatabase db) {
        Logger.m172d("");
        if (db != null) {
            db.enableWriteAheadLogging();
            try {
                IOUtils.closeSilently(db.rawQuery("PRAGMA wal_autocheckpoint=1", null));
            } catch (Throwable e) {
                Logger.m179e(e, "Failed to execute WAL pragma");
            }
        }
    }

    private void enableForeignKeys(SQLiteDatabase db) {
        Logger.m172d("");
        if (db == null) {
            return;
        }
        if (VERSION.SDK_INT >= 16) {
            setForeignKeyConstraintsEnabled(db, true);
            return;
        }
        try {
            IOUtils.closeSilently(db.rawQuery("PRAGMA foreign_keys=ON", null));
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to configure DB");
        }
    }

    @SuppressLint({"NewApi"})
    private static void setForeignKeyConstraintsEnabled(SQLiteDatabase db, boolean isEnabled) {
        if (VERSION.SDK_INT >= 16) {
            db.setForeignKeyConstraintsEnabled(isEnabled);
        }
    }

    private SQLiteDatabase retryOpenDBOrFail(Exception e, boolean writable) {
        Logger.m180e(e, "Failed to open DB, writable=%s", Boolean.valueOf(writable));
        if (this.retryToOpenCalled || !this.onCreateOrOnUpgradeCalled) {
            throw new RuntimeException("Failed to retry to open DB", e);
        }
        this.retryToOpenCalled = true;
        deleteDb();
        return writable ? super.getWritableDatabase() : super.getReadableDatabase();
    }

    private boolean deleteDb() {
        boolean z = false;
        if (this.context == null) {
            Logger.m184w("null context");
        } else {
            try {
                close();
            } catch (Exception e) {
                Logger.m185w("Failed to close DB: %s", e);
            }
            Logger.m173d("Deleting  database file: %s", this.context.getDatabasePath("odnklassniki.db"));
            z = false;
            try {
                z = false | this.context.getDatabasePath("odnklassniki.db").delete();
            } catch (Throwable e2) {
                Logger.m178e(e2);
            }
            if (!z) {
                Logger.m184w("Failed to deleted database file");
            }
        }
        return z;
    }

    private void checkOpenDatabase() {
        File databaseFile = this.context.getDatabasePath("odnklassniki.db");
        if (databaseFile.exists()) {
            Logger.m173d("Checking database file: %s", databaseFile);
            SQLiteDatabase db = null;
            try {
                db = SQLiteDatabase.openDatabase(databaseFile.getPath(), null, 0);
                Logger.m172d("Database is OK");
                if (db != null) {
                    try {
                        db.close();
                    } catch (Exception e) {
                    }
                }
            } catch (Throwable e2) {
                Logger.m179e(e2, "Failed to check database file");
                reportDBFailure(this.context, "Failed to open DB file for read/write", e2);
                deleteDb();
                if (db != null) {
                    try {
                        db.close();
                    } catch (Exception e3) {
                    }
                }
            } catch (Throwable th) {
                if (db != null) {
                    try {
                        db.close();
                    } catch (Exception e4) {
                    }
                }
            }
        }
    }

    public synchronized void onCreate(SQLiteDatabase db) {
        int benchmarkId = DBBenchmark.startCreate(81);
        Logger.m172d("");
        this.onCreateOrOnUpgradeCalled = true;
        doCreate(db);
        DBBenchmark.finishCreate(benchmarkId);
    }

    private void doCreate(SQLiteDatabase db) {
        Logger.m172d(">>> Creating tables...");
        List<String> afterCreate = new ArrayList();
        for (BaseTable table : baseTables) {
            String sqlCreate = table.createBaseTableCreateScript();
            List<String> indexesScripts = table.createIndexesCreateScript();
            table.getOnAfterCreateStatements(afterCreate);
            Logger.m173d("Creating table '%s': %s", table.getTableName(), sqlCreate);
            db.execSQL(sqlCreate);
            for (String indexScript : indexesScripts) {
                Logger.m173d("Creating index for table '%s': %s", table.getTableName(), indexScript);
                db.execSQL(indexScript);
            }
        }
        for (String sql : afterCreate) {
            Logger.m173d("Executing onAfterCreate statement: %s", sql);
            db.execSQL(sql);
        }
        Logger.m172d("<<<");
    }

    public static String createBaseTableDeleteScript(BaseTable table) {
        return createDeleteTableScript(table.getTableName());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int benchmarkId = DBBenchmark.startUpgrade(oldVersion, newVersion);
        this.onCreateOrOnUpgradeCalled = true;
        Logger.m173d(">>> oldVersion=%d newVersion=%d", Integer.valueOf(oldVersion), Integer.valueOf(newVersion));
        if (oldVersion < 48 || newVersion < oldVersion) {
            recreate(db, benchmarkId);
        } else {
            List<String> afterUpgrade = new ArrayList();
            doBasicUpgrade(db, oldVersion, newVersion, afterUpgrade);
            DBBenchmark.finishedBasicUpgrade(benchmarkId);
            deleteOldTables(db, oldVersion, newVersion);
            DBBenchmark.finishedDropOldTables(benchmarkId);
            doAfterUpgrade(db, oldVersion, newVersion, afterUpgrade);
            DBBenchmark.finishedAfterUpgrade(benchmarkId);
        }
        boolean isSchemaValid = new DBSchemaValidator(baseTables).isSchemaValid(db);
        DBBenchmark.upgradeCheck(benchmarkId, isSchemaValid);
        if (!isSchemaValid) {
            Logger.m184w("DB is invalid after upgrade. Re-creating...");
            reportDBFailure(this.context, "Failed to upgrade DB from " + oldVersion + " to " + newVersion, null);
            recreate(db, benchmarkId);
        }
        Logger.m172d("<<<");
        DBBenchmark.finishUpgrade(benchmarkId);
    }

    private void doBasicUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, List<String> outAfterUpgrade) {
        for (BaseTable table : baseTables) {
            List<String> upgradeScripts = new ArrayList();
            table.fillUpgradeScript(db, upgradeScripts, oldVersion, newVersion);
            table.getOnAfterUpgradeStatements(outAfterUpgrade, oldVersion, newVersion);
            Logger.m173d("upgrading table %s...", table.getTableName());
            for (String sql : upgradeScripts) {
                Logger.m173d("executing sql: %s", sql);
                db.execSQL(sql);
            }
        }
    }

    private void deleteOldTables(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion >= 51) {
            for (String tableName : tables2Delete) {
                Logger.m173d("executing sql: %s", createDeleteTableScript(tableName));
                db.execSQL(sql);
            }
        }
    }

    private void doAfterUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, List<String> afterUpgrade) {
        for (String sql : afterUpgrade) {
            Logger.m173d("Executing onAfterUpgrade: %s", sql);
            db.execSQL(sql);
        }
        for (BaseTable table : baseTables) {
            table.onAfterUpgrade(this.context, db, oldVersion, newVersion);
        }
    }

    private static String createDeleteTableScript(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }

    private void recreate(SQLiteDatabase db, int benchmarkId) {
        for (BaseTable table : baseTables) {
            Logger.m173d("deleting table '%s': %s", table.getTableName(), createBaseTableDeleteScript(table));
            db.execSQL(sqlDelete);
        }
        DBBenchmark.finishedDropOnRecreate(benchmarkId);
        doCreate(db);
        DBBenchmark.finishRecreateOnUpgrade(benchmarkId);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.m173d("oldVersion=%d newVersion=%d", Integer.valueOf(oldVersion), Integer.valueOf(newVersion));
        onUpgrade(db, oldVersion, newVersion);
    }

    public static void clearDB(Context context) {
        ContentResolver cr = context.getContentResolver();
        cr.delete(OdklProvider.friendsUri(), null, null);
        cr.delete(Users.getContentUri(), null, null);
        cr.delete(OdklProvider.commentsUri(), null, null);
        cr.delete(OdklProvider.tracksUri(), null, null);
        cr.delete(OdklProvider.artistsUri(), null, null);
        cr.delete(OdklProvider.albumsUri(), null, null);
        cr.delete(OdklProvider.playListUri(), null, null);
        cr.delete(OdklProvider.collectionsUri(), null, null);
        cr.delete(OdklProvider.popTracksSilentUri(), null, null);
        cr.delete(OdklProvider.musicFriendsUri(), null, null);
        cr.delete(OdklProvider.tunersArtistsUri(), null, null);
        cr.delete(OdklProvider.tunersUri(), null, null);
        cr.delete(OdklProvider.popCollectionsUri(), null, null);
        cr.delete(OdklProvider.musicHistoryUri(), null, null);
        cr.delete(OdklProvider.tunersTracksUri(), null, null);
        cr.delete(OdklProvider.musicExtensionUri(), null, null);
        cr.delete(OdklProvider.userTracksUri(), null, null);
        cr.delete(OdklProvider.friendsSuggest(), null, null);
        cr.delete(OdklProvider.relativesUri(), null, null);
        cr.delete(Groups.getContentUri(), null, null);
        cr.delete(Banners.getContentUri(), null, null);
        cr.delete(PromoLinks.getContentUri(), null, null);
        cr.delete(OdklProvider.userStreamSubscribeUri(), null, null);
    }

    public static void clearDbAsync(Context context) {
        new Thread(new C02471(context.getApplicationContext())).start();
    }

    public static void reportDBFailure(Context context, String message, Throwable cause) {
        try {
            DBFailureError error = new DBFailureError(message, cause);
            StatisticManager sm = StatisticManager.getInstance();
            sm.startSession(context);
            StatisticManager.getInstance().reportError(DBFailureError.class.getSimpleName(), message, error);
            sm.endSession(context);
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to report DB failure");
        }
    }
}
