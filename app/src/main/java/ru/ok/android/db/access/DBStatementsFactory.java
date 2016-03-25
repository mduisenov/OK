package ru.ok.android.db.access;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import java.util.HashMap;
import java.util.Map;

public final class DBStatementsFactory {
    private static final Map<String, ThreadLocal<SQLiteStatement>> statements;

    static {
        statements = new HashMap();
    }

    public static SQLiteStatement getStatement(SQLiteDatabase db, String sql) {
        ThreadLocal<SQLiteStatement> threadLocal = (ThreadLocal) statements.get(sql);
        if (threadLocal == null) {
            threadLocal = new ThreadLocal();
            statements.put(sql, threadLocal);
        }
        SQLiteStatement statement = (SQLiteStatement) threadLocal.get();
        if (statement != null) {
            return statement;
        }
        statement = db.compileStatement(sql);
        threadLocal.set(statement);
        return statement;
    }
}
