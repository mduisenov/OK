package ru.ok.android.statistics;

import android.content.Context;
import android.util.Pair;

public interface StatisticAgent {
    void addEvent(String str, Pair<String, String>[] pairArr);

    void endSession(Context context);

    void reportError(String str, String str2, Throwable th);

    void setUserId(String str);

    void startSession(Context context);
}
