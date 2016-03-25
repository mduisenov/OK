package ru.mail.libverify.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/* renamed from: ru.mail.libverify.utils.c */
public final class C0203c {
    public static String m126a(@NonNull Throwable th, @Nullable Thread thread) {
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        if (thread != null) {
            try {
                printStream.append(thread.getName()).append("\n");
            } catch (Exception e) {
                printStream.close();
                return null;
            } catch (Throwable th2) {
                printStream.close();
            }
        }
        th.printStackTrace(printStream);
        String byteArrayOutputStream2 = byteArrayOutputStream.toString();
        printStream.close();
        return byteArrayOutputStream2;
    }

    public static void m127a(String str, String str2, Throwable th) {
        C0204d.m130a(str, str2, th);
        th.printStackTrace();
    }
}
