package ru.ok.android.statistics;

import android.content.Context;
import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.VersionCodeCrushHandler;
import ru.ok.android.statistics.flurry.FlurryStatisticAgent;
import ru.ok.android.statistics.local.LocalStatisticAgent;
import ru.ok.android.utils.Logger;

public class StatisticManager {
    private static final StatisticManager instance;
    private List<StatisticAgent> agents;

    static {
        instance = new StatisticManager();
    }

    public static StatisticManager getInstance() {
        return instance;
    }

    public StatisticManager() {
        this.agents = new ArrayList();
        registerAgent(new FlurryStatisticAgent());
        registerAgent(new LocalStatisticAgent());
    }

    public void registerAgent(StatisticAgent agent) {
        this.agents.add(agent);
    }

    public void startSession(Context context) {
        for (StatisticAgent agent : this.agents) {
            agent.startSession(context);
        }
    }

    public void endSession(Context context) {
        for (StatisticAgent agent : this.agents) {
            agent.endSession(context);
        }
    }

    public void addStatisticEvent(String event, Pair<String, String>... params) {
        addStatisticEvent(event, false, params);
    }

    public void addStatisticEvent(String event, boolean storeLocally, Pair<String, String>... params) {
        if (Logger.isLoggingEnable()) {
            StringBuilder sb = new StringBuilder();
            if (params != null) {
                for (Pair<String, String> param : params) {
                    if (param != null) {
                        if (sb.length() != 0) {
                            sb.append(", ");
                        }
                        sb.append((String) param.first).append("=").append((String) param.second);
                    }
                }
            }
            Logger.m173d("event=%s (%s) storeLocally=%s manageStatistics=%s localAgent=%s", event, sb, Boolean.valueOf(storeLocally), Boolean.valueOf(true), Boolean.valueOf(true));
        }
        for (StatisticAgent agent : this.agents) {
            if (!(agent instanceof LocalStatisticAgent) || storeLocally) {
                agent.addEvent(event, params);
            }
        }
    }

    public void reportError(String errorId, String message, Throwable cause) {
        Logger.m173d("errorId=%s message=%s cause=%s", errorId, message, cause);
        cause.setStackTrace(VersionCodeCrushHandler.addVersionCodeInfo(cause.getStackTrace(), 182));
        for (StatisticAgent agent : this.agents) {
            agent.reportError(errorId, message, cause);
        }
    }

    public void setUserId(String userId) {
        for (StatisticAgent agent : this.agents) {
            agent.setUserId(userId);
        }
    }
}
