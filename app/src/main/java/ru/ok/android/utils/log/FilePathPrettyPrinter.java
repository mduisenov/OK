package ru.ok.android.utils.log;

import android.support.annotation.NonNull;
import java.io.File;

public class FilePathPrettyPrinter {
    public static String getFileModifiersWithParents(File file) {
        if (file == null) {
            return "File is null";
        }
        StringBuilder sb = new StringBuilder();
        do {
            appendFileModifiersInternal(file, sb).append("\n");
            file = file.getParentFile();
        } while (file != null);
        return sb.toString();
    }

    @NonNull
    private static StringBuilder appendFileModifiersInternal(File file, StringBuilder stringBuilder) {
        return appendModifiers(file, stringBuilder).append(" ").append(file);
    }

    private static StringBuilder appendModifiers(File file, StringBuilder builder) {
        char c = '-';
        if (!file.exists()) {
            return builder.append("[not_exists]");
        }
        StringBuilder append = builder.append('[').append(file.isDirectory() ? 'd' : '-').append(file.canRead() ? 'r' : '-').append(file.canWrite() ? 'w' : '-');
        if (file.canExecute()) {
            c = 'e';
        }
        append.append(c).append(']');
        return builder;
    }
}
