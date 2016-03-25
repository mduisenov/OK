package ru.ok.android.utils.log;

import android.content.Context;
import android.text.format.Time;
import ru.ok.android.proto.MessagesProto.Message;

public class FileLogger {
    private static volatile FileLogger instance;
    private static final ThreadLocal<Time> timeLocal;
    private final LineAppender appender;
    private ThreadLocal<Holder> holderThreadLocal;

    private static class Holder {
        final Time date;
        long lastTime;
        String lastTimeFormatted;
        final StringBuilder sb;

        private Holder() {
            this.date = new Time();
            this.sb = new StringBuilder();
        }
    }

    public static FileLogger from(Context context) {
        if (instance == null) {
            synchronized (FileLogger.class) {
                if (instance == null) {
                    instance = new FileLogger(context);
                }
            }
        }
        return instance;
    }

    private FileLogger(Context context) {
        this.holderThreadLocal = new ThreadLocal();
        this.appender = new SDCardFileAppender(getLogFilePath(context));
    }

    public static String getLogsDirPath(Context context) {
        return "Android/data/" + context.getPackageName() + "/logs";
    }

    public static String getLogFilePath(Context context) {
        return getLogsDirPath(context) + "/ok.log";
    }

    public void m188d(String tag, String message) {
        this.appender.append(buildLogLine(System.currentTimeMillis(), 3, tag, message));
    }

    public void m190v(String tag, String message) {
        this.appender.append(buildLogLine(System.currentTimeMillis(), 2, tag, message));
    }

    public void m191w(String tag, String message) {
        this.appender.append(buildLogLine(System.currentTimeMillis(), 5, tag, message));
    }

    public void m192w(String tag, String message, Throwable e) {
        long time = System.currentTimeMillis();
        Holder holder = getHolder();
        format(holder, time);
        this.appender.append(buildLogLine(holder, 5, tag, message));
        log(holder, 5, tag, e);
    }

    public void m189e(String tag, String message, Throwable e) {
        long time = System.currentTimeMillis();
        Holder holder = getHolder();
        format(holder, time);
        this.appender.append(buildLogLine(holder, 5, tag, message));
        log(holder, 5, tag, e);
    }

    private void log(Holder holder, int level, String tag, Throwable e) {
        boolean isNested = false;
        do {
            this.appender.append(buildLogLine(holder.lastTime, level, tag, (isNested ? "Caused by: " : "") + e));
            StackTraceElement[] stackTraceElements = e == null ? null : e.getStackTrace();
            if (stackTraceElements != null) {
                for (StackTraceElement element : stackTraceElements) {
                    this.appender.append(buildLogLine(holder.lastTime, level, tag, element.toString()));
                }
            }
            if (e != null) {
                e = e.getCause();
                isNested = true;
                continue;
            }
        } while (e != null);
    }

    private Holder getHolder() {
        Holder holder = (Holder) this.holderThreadLocal.get();
        if (holder != null) {
            return holder;
        }
        holder = new Holder();
        this.holderThreadLocal.set(holder);
        return holder;
    }

    private String format(Holder holder, long time) {
        if (holder.lastTime == time && holder.lastTimeFormatted != null) {
            return holder.lastTimeFormatted;
        }
        Time date = holder.date;
        date.set(time);
        String timeFormatted = formatForLog(date, time, holder.sb);
        holder.lastTimeFormatted = timeFormatted;
        holder.lastTime = time;
        return timeFormatted;
    }

    private String buildLogLine(long time, int level, String tag, String message) {
        Holder holder = getHolder();
        format(holder, time);
        return buildLogLine(holder, level, tag, message);
    }

    private String buildLogLine(Holder holder, int level, String tag, String message) {
        StringBuilder sb = holder.sb;
        sb.setLength(0);
        sb.append(holder.lastTimeFormatted);
        switch (level) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                sb.append(" V/");
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                sb.append(" I/");
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                sb.append(" W/");
                break;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                sb.append(" E/");
                break;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                sb.append(" A/");
                break;
            default:
                sb.append(" D/");
                break;
        }
        sb.append(tag).append(": ").append(message);
        return sb.toString();
    }

    public static final String formatForLog(Time time, long timeMs, StringBuilder sb) {
        if (time == null) {
            return "";
        }
        if (sb == null) {
            sb = new StringBuilder();
        }
        sb.setLength(0);
        int millis = (int) (timeMs % 1000);
        sb.append(time.year).append('-');
        append2Digits(sb, time.month + 1).append('-');
        append2Digits(sb, time.monthDay).append(' ');
        append2Digits(sb, time.hour).append(':');
        append2Digits(sb, time.minute).append(':');
        append2Digits(sb, time.second).append('.');
        append3Digits(sb, millis);
        return sb.toString();
    }

    static {
        timeLocal = new ThreadLocal();
    }

    private static StringBuilder append2Digits(StringBuilder sb, int value) {
        if (value < 10) {
            sb.append('0');
        }
        sb.append(value);
        return sb;
    }

    private static StringBuilder append3Digits(StringBuilder sb, int value) {
        if (value < 10) {
            sb.append('0');
        }
        if (value < 100) {
            sb.append('0');
        }
        sb.append(value);
        return sb;
    }
}
