package ru.ok.android.utils.log;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SDCardFileAppender implements LineAppender {
    private static final String LOG_TAG;
    private static volatile int instanceCount;
    private final File file;
    private int instanceNum;
    private final ConcurrentLinkedQueue<String> linesBuffer;

    static {
        instanceCount = 0;
        LOG_TAG = SDCardFileAppender.class.getSimpleName();
    }

    public SDCardFileAppender(String sdCardRelativePathName) {
        int i = instanceCount + 1;
        instanceCount = i;
        this.instanceNum = i;
        this.linesBuffer = new ConcurrentLinkedQueue();
        this.file = new File(Environment.getExternalStorageDirectory(), sdCardRelativePathName);
        Thread flusher = new 1(this, "SDCardFileAppender-" + this.instanceNum);
        flusher.setPriority(1);
        flusher.start();
    }

    public void append(String line) {
        if (this.linesBuffer.size() < 100000) {
            this.linesBuffer.add(line);
        }
    }

    public synchronized void flush() {
        IOException e;
        Throwable th;
        if (!this.linesBuffer.isEmpty()) {
            File dir = this.file.getParentFile();
            if (dir.exists() || dir.mkdirs()) {
                PrintWriter printWriter = null;
                try {
                    PrintWriter out = new PrintWriter(new FileOutputStream(this.file, true));
                    while (!this.linesBuffer.isEmpty()) {
                        try {
                            out.println((String) this.linesBuffer.peek());
                            this.linesBuffer.poll();
                        } catch (IOException e2) {
                            e = e2;
                            printWriter = out;
                        } catch (Throwable th2) {
                            th = th2;
                            printWriter = out;
                        }
                    }
                    if (out != null) {
                        try {
                            out.flush();
                        } catch (Throwable th3) {
                        }
                        try {
                            out.close();
                            printWriter = out;
                        } catch (Throwable th4) {
                            printWriter = out;
                        }
                    }
                } catch (IOException e3) {
                    e = e3;
                    try {
                        Log.e(LOG_TAG, "Failed to flush to file: " + e, e);
                        if (printWriter != null) {
                            try {
                                printWriter.flush();
                            } catch (Throwable th5) {
                            }
                            try {
                                printWriter.close();
                            } catch (Throwable th6) {
                            }
                        }
                    } catch (Throwable th7) {
                        th = th7;
                        if (printWriter != null) {
                            try {
                                printWriter.flush();
                            } catch (Throwable th8) {
                            }
                            try {
                                printWriter.close();
                            } catch (Throwable th9) {
                            }
                        }
                        throw th;
                    }
                }
            } else {
                Log.e(LOG_TAG, "Failed to create directory: " + dir.getPath());
            }
        }
    }
}
