package ru.mail.android.mytarget.core.async;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.URLUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jivesoftware.smack.util.StringUtils;
import ru.mail.android.mytarget.Tracer;
import ru.mail.android.mytarget.core.factories.RequestsFactory;
import ru.mail.android.mytarget.core.models.ProgressStat;
import ru.mail.android.mytarget.core.models.Stat;

public class Sender {
    private static ExecutorService executorService;
    private static Handler handler;

    static {
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    public static void addStat(String url, String type, float value, Context context) {
        Exception e;
        try {
            url = URLDecoder.decode(url, StringUtils.UTF8);
        } catch (UnsupportedEncodingException e2) {
            e = e2;
            Tracer.m35d(e.toString());
            if (url == null) {
            }
            Tracer.m35d("invalid stat url: " + url);
        } catch (IllegalArgumentException e3) {
            e = e3;
            Tracer.m35d(e.toString());
            if (url == null) {
            }
            Tracer.m35d("invalid stat url: " + url);
        }
        if (url == null && URLUtil.isNetworkUrl(url)) {
            Tracer.m35d("add stat type: " + type + (value == -1.0f ? "" : " value: " + value) + " url: " + url);
            addRequest(RequestsFactory.getStatRequest(url), context);
            return;
        }
        Tracer.m35d("invalid stat url: " + url);
    }

    public static void addStat(String url, Context context) {
        addStat(url, null, -1.0f, context);
    }

    public static void addStat(Stat stat, Context context) {
        if (stat instanceof ProgressStat) {
            addStat(stat.getUrl(), stat.getType(), ((ProgressStat) stat).getValue(), context);
        } else {
            addStat(stat.getUrl(), stat.getType(), -1.0f, context);
        }
    }

    public static void addStat(List<Stat> stats, String type, Context context) {
        for (Stat stat : stats) {
            if (stat.getType().equals(type)) {
                addStat(stat.getUrl(), stat.getType(), -1.0f, context);
            }
        }
    }

    public static void addStat(List<String> stats, Context context) {
        for (String stat : stats) {
            addStat(stat, context);
        }
    }

    public static void addMessage(String message, String loggerClass, int logLevel, String culprit, String url, Context context) {
        Tracer.m35d("add log message level: " + logLevel);
        addRequest(RequestsFactory.getLogRequest(message, loggerClass, logLevel, culprit, null, url), context);
    }

    public static void addException(String message, String loggerClass, int logLevel, Throwable exception, String url, Context context) {
        Tracer.m35d("add log message level: " + logLevel);
        addRequest(RequestsFactory.getLogRequest(message, loggerClass, logLevel, null, exception, url), context);
    }

    public static void addRequest(Request request, Context context) {
        Tracer.m35d("add request to queue");
        executorService.execute(new ExecutorRunnable(request, context.getApplicationContext(), null));
    }

    private static void onExecutorComplete(ExecutorRunnable executorRunnable) {
        Request request = ExecutorRunnable.access$100(executorRunnable);
        if (!request.isSuccess() && request.getFailExecutions() <= request.getRepeatsOnFail()) {
            executorService.execute(executorRunnable);
        }
    }

    private Sender() {
    }
}
