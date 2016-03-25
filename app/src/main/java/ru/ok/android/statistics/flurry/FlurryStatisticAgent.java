package ru.ok.android.statistics.flurry;

import android.content.Context;
import android.util.Pair;
import com.flurry.android.FlurryAgent;
import java.util.HashMap;
import java.util.Map;
import ru.ok.android.statistics.StatisticAgent;
import ru.ok.android.utils.Logger;

public class FlurryStatisticAgent implements StatisticAgent {
    private static volatile int openSessionCount;

    static {
        openSessionCount = 0;
    }

    public static void initFlurry(Context context) {
        FlurryAgent.setLogEnabled(false);
        FlurryAgent.setLogLevel(4);
        FlurryAgent.setLogEvents(true);
        FlurryAgent.setReportLocation(false);
        FlurryAgent.init(context, "R7B66GWG3K5J9YBS6GPQ");
    }

    public void startSession(Context context) {
        Object[] objArr = new Object[1];
        int i = openSessionCount + 1;
        openSessionCount = i;
        objArr[0] = Integer.valueOf(i);
        Logger.m173d("openSessionCount=%d", objArr);
        FlurryAgent.onStartSession(context);
    }

    public void endSession(Context context) {
        Object[] objArr = new Object[1];
        int i = openSessionCount - 1;
        openSessionCount = i;
        objArr[0] = Integer.valueOf(i);
        Logger.m173d("openSessionCount=%d", objArr);
        FlurryAgent.onEndSession(context);
    }

    public void addEvent(String name, Pair<String, String>[] params) {
        if (params == null || params.length == 0) {
            addSimpleEvent(name);
            return;
        }
        Map paramsMap = new HashMap();
        for (Pair<String, String> param : params) {
            paramsMap.put(param.first, param.second);
        }
        addEvent(name, paramsMap);
    }

    private void addSimpleEvent(String name) {
        FlurryAgent.logEvent(name);
    }

    private void addEvent(String name, Map<String, String> params) {
        FlurryAgent.logEvent(name, (Map) params);
    }

    public void setUserId(String userId) {
        Logger.m172d("flurry no use this case");
    }

    public void reportError(String errorId, String message, Throwable cause) {
        FlurryAgent.onError(errorId, message, cause);
    }
}
