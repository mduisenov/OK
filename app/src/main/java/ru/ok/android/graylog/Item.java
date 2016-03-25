package ru.ok.android.graylog;

import android.os.Build.VERSION;
import android.support.annotation.Nullable;

class Item {
    private final int code;
    private final String comment;
    private final long time;

    public Item(int code, long time, @Nullable CharSequence message, @Nullable Throwable caught, @Nullable String placeThread, @Nullable StackTraceElement[] placeStack) {
        this.code = code;
        this.time = time;
        this.comment = composeComment(message, caught, placeThread, placeStack);
    }

    public int getCode() {
        return this.code;
    }

    public long getTime() {
        return this.time;
    }

    public String getComment() {
        return this.comment;
    }

    private static String composeComment(@Nullable CharSequence message, @Nullable Throwable caught, @Nullable String placeThread, @Nullable StackTraceElement[] placeStack) {
        StringBuilder out = new StringBuilder();
        if (out.length() > 0) {
            out.append("\n");
        }
        if (message != null) {
            out.append(message);
            out.append("\n");
        }
        if (caught != null) {
            out.append("Caught ");
            appendStackTrace(out, "", caught, null);
        }
        if (placeStack != null) {
            out.append("Logged");
            if (placeThread != null) {
                out.append(" in thread \"");
                out.append(placeThread);
                out.append("\"");
            }
            out.append("\n");
            int duplicates = caught != null ? countDuplicates(placeStack, caught.getStackTrace()) : 0;
            int len = placeStack.length - duplicates;
            for (int i = 0; i < len; i++) {
                appendStackTraceElement(out, "", placeStack[i]);
            }
            if (duplicates > 0) {
                appendMore(out, "", duplicates);
            }
        }
        return out.toString();
    }

    private static void appendStackTrace(StringBuilder out, String indent, Throwable exception, StackTraceElement[] parentStack) {
        out.append(exception.toString());
        out.append("\n");
        StackTraceElement[] stack = exception.getStackTrace();
        if (stack != null) {
            int duplicates = parentStack != null ? countDuplicates(stack, parentStack) : 0;
            int len = stack.length - duplicates;
            for (int i = 0; i < len; i++) {
                appendStackTraceElement(out, indent, stack[i]);
            }
            if (duplicates > 0) {
                appendMore(out, indent, duplicates);
            }
        }
        if (VERSION.SDK_INT >= 19) {
            Throwable[] suppressed = exception.getSuppressed();
            if (suppressed != null) {
                for (Throwable s : suppressed) {
                    out.append(indent);
                    out.append("\tSuppressed: ");
                    appendStackTrace(out, "\t" + indent, s, stack);
                }
            }
        }
        Throwable cause = exception.getCause();
        if (cause != null) {
            out.append(indent);
            out.append("Caused by: ");
            appendStackTrace(out, indent, cause, stack);
        }
    }

    private static void appendStackTraceElement(StringBuilder out, String indent, StackTraceElement element) {
        out.append(indent);
        out.append("\tat ");
        out.append(element.toString());
        out.append("\n");
    }

    private static void appendMore(StringBuilder out, String indent, int duplicates) {
        out.append(indent);
        out.append("\t... ");
        out.append(Integer.toString(duplicates));
        out.append(" more\n");
    }

    private static int countDuplicates(StackTraceElement[] currentStack, StackTraceElement[] parentStack) {
        int duplicates = 0;
        int c = currentStack.length - 1;
        int p = parentStack.length - 1;
        while (c >= 0 && p >= 0) {
            if (!parentStack[p].equals(currentStack[c])) {
                break;
            }
            duplicates++;
            c--;
            p--;
        }
        return duplicates;
    }
}
