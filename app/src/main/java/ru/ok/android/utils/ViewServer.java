package ru.ok.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ViewServer implements Runnable {
    private static ViewServer sServer;
    private final ReentrantReadWriteLock mFocusLock;
    private View mFocusedWindow;
    private final List<WindowListener> mListeners;
    private final int mPort;
    private ServerSocket mServer;
    private Thread mThread;
    private ExecutorService mThreadPool;
    private final HashMap<View, String> mWindows;
    private final ReentrantReadWriteLock mWindowsLock;

    private static class NoopViewServer extends ViewServer {
        private NoopViewServer() {
            super();
        }

        public boolean start() throws IOException {
            return false;
        }

        public boolean isRunning() {
            return false;
        }

        public void setFocusedWindow(Activity activity) {
        }

        public void setFocusedWindow(View view) {
        }

        public void run() {
        }
    }

    private static class UncloseableOuputStream extends OutputStream {
        private final OutputStream mStream;

        UncloseableOuputStream(OutputStream stream) {
            this.mStream = stream;
        }

        public void close() throws IOException {
        }

        public boolean equals(Object o) {
            return this.mStream.equals(o);
        }

        public void flush() throws IOException {
            this.mStream.flush();
        }

        public int hashCode() {
            return this.mStream.hashCode();
        }

        public String toString() {
            return this.mStream.toString();
        }

        public void write(byte[] buffer, int offset, int count) throws IOException {
            this.mStream.write(buffer, offset, count);
        }

        public void write(byte[] buffer) throws IOException {
            this.mStream.write(buffer);
        }

        public void write(int oneByte) throws IOException {
            this.mStream.write(oneByte);
        }
    }

    private interface WindowListener {
        void focusChanged();
    }

    private class ViewServerWorker implements Runnable, WindowListener {
        private Socket mClient;
        private final Object[] mLock;
        private boolean mNeedFocusedWindowUpdate;
        private boolean mNeedWindowListUpdate;

        public ViewServerWorker(Socket client) {
            this.mLock = new Object[0];
            this.mClient = client;
            this.mNeedWindowListUpdate = false;
            this.mNeedFocusedWindowUpdate = false;
        }

        public void run() {
            IOException e;
            Throwable th;
            BufferedReader in = null;
            try {
                BufferedReader in2 = new BufferedReader(new InputStreamReader(this.mClient.getInputStream()), 1024);
                try {
                    String command;
                    String parameters;
                    boolean result;
                    String request = in2.readLine();
                    int index = request.indexOf(32);
                    if (index == -1) {
                        command = request;
                        parameters = "";
                    } else {
                        command = request.substring(0, index);
                        parameters = request.substring(index + 1);
                    }
                    if ("PROTOCOL".equalsIgnoreCase(command)) {
                        result = ViewServer.writeValue(this.mClient, "4");
                    } else if ("SERVER".equalsIgnoreCase(command)) {
                        result = ViewServer.writeValue(this.mClient, "4");
                    } else if ("LIST".equalsIgnoreCase(command)) {
                        result = listWindows(this.mClient);
                    } else if ("GET_FOCUS".equalsIgnoreCase(command)) {
                        result = getFocusedWindow(this.mClient);
                    } else if ("AUTOLIST".equalsIgnoreCase(command)) {
                        result = windowManagerAutolistLoop();
                    } else {
                        result = windowCommand(this.mClient, command, parameters);
                    }
                    if (!result) {
                        Log.w("ViewServer", "An error occurred with the command: " + command);
                    }
                    if (in2 != null) {
                        try {
                            in2.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    if (this.mClient != null) {
                        try {
                            this.mClient.close();
                            in = in2;
                            return;
                        } catch (IOException e22) {
                            e22.printStackTrace();
                            in = in2;
                            return;
                        }
                    }
                } catch (IOException e3) {
                    e22 = e3;
                    in = in2;
                    try {
                        Log.w("ViewServer", "Connection error: ", e22);
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e222) {
                                e222.printStackTrace();
                            }
                        }
                        if (this.mClient != null) {
                            try {
                                this.mClient.close();
                            } catch (IOException e2222) {
                                e2222.printStackTrace();
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e22222) {
                                e22222.printStackTrace();
                            }
                        }
                        if (this.mClient != null) {
                            try {
                                this.mClient.close();
                            } catch (IOException e222222) {
                                e222222.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    in = in2;
                    if (in != null) {
                        in.close();
                    }
                    if (this.mClient != null) {
                        this.mClient.close();
                    }
                    throw th;
                }
            } catch (IOException e4) {
                e222222 = e4;
                Log.w("ViewServer", "Connection error: ", e222222);
                if (in != null) {
                    in.close();
                }
                if (this.mClient != null) {
                    this.mClient.close();
                }
            }
        }

        private boolean windowCommand(Socket client, String command, String parameters) {
            Exception e;
            Throwable th;
            boolean success = true;
            BufferedWriter out = null;
            try {
                int index = parameters.indexOf(32);
                if (index == -1) {
                    index = parameters.length();
                }
                int hashCode = (int) Long.parseLong(parameters.substring(0, index), 16);
                if (index < parameters.length()) {
                    parameters = parameters.substring(index + 1);
                } else {
                    parameters = "";
                }
                if (findWindow(hashCode) != null) {
                    Method dispatch = ViewDebug.class.getDeclaredMethod("dispatchCommand", new Class[]{View.class, String.class, String.class, OutputStream.class});
                    dispatch.setAccessible(true);
                    dispatch.invoke(null, new Object[]{window, command, parameters, new UncloseableOuputStream(client.getOutputStream())});
                    if (!client.isOutputShutdown()) {
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                        try {
                            out2.write("DONE\n");
                            out2.flush();
                            out = out2;
                        } catch (Exception e2) {
                            e = e2;
                            out = out2;
                            try {
                                Log.w("ViewServer", "Could not send command " + command + " with parameters " + parameters, e);
                                success = false;
                                if (out != null) {
                                    try {
                                        out.close();
                                    } catch (IOException e3) {
                                        success = false;
                                    }
                                }
                                return success;
                            } catch (Throwable th2) {
                                th = th2;
                                if (out != null) {
                                    try {
                                        out.close();
                                    } catch (IOException e4) {
                                    }
                                }
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            out = out2;
                            if (out != null) {
                                out.close();
                            }
                            throw th;
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e5) {
                            success = false;
                        }
                    }
                    return success;
                } else if (out == null) {
                    return false;
                } else {
                    try {
                        out.close();
                        return false;
                    } catch (IOException e6) {
                        return false;
                    }
                }
            } catch (Exception e7) {
                e = e7;
                Log.w("ViewServer", "Could not send command " + command + " with parameters " + parameters, e);
                success = false;
                if (out != null) {
                    out.close();
                }
                return success;
            }
        }

        private View findWindow(int hashCode) {
            if (hashCode == -1) {
                View view = null;
                ViewServer.this.mWindowsLock.readLock().lock();
                try {
                    view = ViewServer.this.mFocusedWindow;
                    return view;
                } finally {
                    ViewServer.this.mWindowsLock.readLock().unlock();
                }
            } else {
                ViewServer.this.mWindowsLock.readLock().lock();
                try {
                    for (Entry<View, String> entry : ViewServer.this.mWindows.entrySet()) {
                        if (System.identityHashCode(entry.getKey()) == hashCode) {
                            View view2 = (View) entry.getKey();
                            return view2;
                        }
                    }
                    ViewServer.this.mWindowsLock.readLock().unlock();
                    return null;
                } finally {
                    ViewServer.this.mWindowsLock.readLock().unlock();
                }
            }
        }

        private boolean listWindows(Socket client) {
            Throwable th;
            BufferedWriter out = null;
            try {
                ViewServer.this.mWindowsLock.readLock().lock();
                BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()), FragmentTransaction.TRANSIT_EXIT_MASK);
                try {
                    for (Entry<View, String> entry : ViewServer.this.mWindows.entrySet()) {
                        out2.write(Integer.toHexString(System.identityHashCode(entry.getKey())));
                        out2.write(32);
                        out2.append((CharSequence) entry.getValue());
                        out2.write(10);
                    }
                    out2.write("DONE.\n");
                    out2.flush();
                    ViewServer.this.mWindowsLock.readLock().unlock();
                    if (out2 != null) {
                        try {
                            out2.close();
                            out = out2;
                            return true;
                        } catch (IOException e) {
                            out = out2;
                            return false;
                        }
                    }
                    return true;
                } catch (Exception e2) {
                    out = out2;
                    ViewServer.this.mWindowsLock.readLock().unlock();
                    if (out != null) {
                        return false;
                    }
                    try {
                        out.close();
                        return false;
                    } catch (IOException e3) {
                        return false;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    out = out2;
                    ViewServer.this.mWindowsLock.readLock().unlock();
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e4) {
                        }
                    }
                    throw th;
                }
            } catch (Exception e5) {
                ViewServer.this.mWindowsLock.readLock().unlock();
                if (out != null) {
                    return false;
                }
                out.close();
                return false;
            } catch (Throwable th3) {
                th = th3;
                ViewServer.this.mWindowsLock.readLock().unlock();
                if (out != null) {
                    out.close();
                }
                throw th;
            }
        }

        private boolean getFocusedWindow(Socket client) {
            Throwable th;
            BufferedWriter out = null;
            try {
                BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()), FragmentTransaction.TRANSIT_EXIT_MASK);
                try {
                    ViewServer.this.mFocusLock.readLock().lock();
                    View focusedWindow = ViewServer.this.mFocusedWindow;
                    ViewServer.this.mFocusLock.readLock().unlock();
                    if (focusedWindow != null) {
                        ViewServer.this.mWindowsLock.readLock().lock();
                        String focusName = (String) ViewServer.this.mWindows.get(ViewServer.this.mFocusedWindow);
                        ViewServer.this.mWindowsLock.readLock().unlock();
                        out2.write(Integer.toHexString(System.identityHashCode(focusedWindow)));
                        out2.write(32);
                        out2.append(focusName);
                    }
                    out2.write(10);
                    out2.flush();
                    if (out2 != null) {
                        try {
                            out2.close();
                            out = out2;
                            return true;
                        } catch (IOException e) {
                            out = out2;
                            return false;
                        }
                    }
                    return true;
                } catch (Exception e2) {
                    out = out2;
                    if (out != null) {
                        return false;
                    }
                    try {
                        out.close();
                        return false;
                    } catch (IOException e3) {
                        return false;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    out = out2;
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e4) {
                        }
                    }
                    throw th;
                }
            } catch (Exception e5) {
                if (out != null) {
                    return false;
                }
                out.close();
                return false;
            } catch (Throwable th3) {
                th = th3;
                if (out != null) {
                    out.close();
                }
                throw th;
            }
        }

        public void focusChanged() {
            synchronized (this.mLock) {
                this.mNeedFocusedWindowUpdate = true;
                this.mLock.notifyAll();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean windowManagerAutolistLoop() {
            /*
            r7 = this;
            r5 = ru.ok.android.utils.ViewServer.this;
            r5.addWindowListener(r7);
            r3 = 0;
            r4 = new java.io.BufferedWriter;	 Catch:{ Exception -> 0x0092 }
            r5 = new java.io.OutputStreamWriter;	 Catch:{ Exception -> 0x0092 }
            r6 = r7.mClient;	 Catch:{ Exception -> 0x0092 }
            r6 = r6.getOutputStream();	 Catch:{ Exception -> 0x0092 }
            r5.<init>(r6);	 Catch:{ Exception -> 0x0092 }
            r4.<init>(r5);	 Catch:{ Exception -> 0x0092 }
        L_0x0016:
            r5 = java.lang.Thread.interrupted();	 Catch:{ Exception -> 0x0032, all -> 0x0071 }
            if (r5 != 0) goto L_0x007e;
        L_0x001c:
            r2 = 0;
            r1 = 0;
            r6 = r7.mLock;	 Catch:{ Exception -> 0x0032, all -> 0x0071 }
            monitor-enter(r6);	 Catch:{ Exception -> 0x0032, all -> 0x0071 }
        L_0x0021:
            r5 = r7.mNeedWindowListUpdate;	 Catch:{ all -> 0x002f }
            if (r5 != 0) goto L_0x0049;
        L_0x0025:
            r5 = r7.mNeedFocusedWindowUpdate;	 Catch:{ all -> 0x002f }
            if (r5 != 0) goto L_0x0049;
        L_0x0029:
            r5 = r7.mLock;	 Catch:{ all -> 0x002f }
            r5.wait();	 Catch:{ all -> 0x002f }
            goto L_0x0021;
        L_0x002f:
            r5 = move-exception;
            monitor-exit(r6);	 Catch:{ all -> 0x002f }
            throw r5;	 Catch:{ Exception -> 0x0032, all -> 0x0071 }
        L_0x0032:
            r0 = move-exception;
            r3 = r4;
        L_0x0034:
            r5 = "ViewServer";
            r6 = "Connection error: ";
            android.util.Log.w(r5, r6, r0);	 Catch:{ all -> 0x0090 }
            if (r3 == 0) goto L_0x0042;
        L_0x003f:
            r3.close();	 Catch:{ IOException -> 0x008c }
        L_0x0042:
            r5 = ru.ok.android.utils.ViewServer.this;
            r5.removeWindowListener(r7);
        L_0x0047:
            r5 = 1;
            return r5;
        L_0x0049:
            r5 = r7.mNeedWindowListUpdate;	 Catch:{ all -> 0x002f }
            if (r5 == 0) goto L_0x0051;
        L_0x004d:
            r5 = 0;
            r7.mNeedWindowListUpdate = r5;	 Catch:{ all -> 0x002f }
            r2 = 1;
        L_0x0051:
            r5 = r7.mNeedFocusedWindowUpdate;	 Catch:{ all -> 0x002f }
            if (r5 == 0) goto L_0x0059;
        L_0x0055:
            r5 = 0;
            r7.mNeedFocusedWindowUpdate = r5;	 Catch:{ all -> 0x002f }
            r1 = 1;
        L_0x0059:
            monitor-exit(r6);	 Catch:{ all -> 0x002f }
            if (r2 == 0) goto L_0x0065;
        L_0x005c:
            r5 = "LIST UPDATE\n";
            r4.write(r5);	 Catch:{ Exception -> 0x0032, all -> 0x0071 }
            r4.flush();	 Catch:{ Exception -> 0x0032, all -> 0x0071 }
        L_0x0065:
            if (r1 == 0) goto L_0x0016;
        L_0x0067:
            r5 = "FOCUS UPDATE\n";
            r4.write(r5);	 Catch:{ Exception -> 0x0032, all -> 0x0071 }
            r4.flush();	 Catch:{ Exception -> 0x0032, all -> 0x0071 }
            goto L_0x0016;
        L_0x0071:
            r5 = move-exception;
            r3 = r4;
        L_0x0073:
            if (r3 == 0) goto L_0x0078;
        L_0x0075:
            r3.close();	 Catch:{ IOException -> 0x008e }
        L_0x0078:
            r6 = ru.ok.android.utils.ViewServer.this;
            r6.removeWindowListener(r7);
            throw r5;
        L_0x007e:
            if (r4 == 0) goto L_0x0083;
        L_0x0080:
            r4.close();	 Catch:{ IOException -> 0x008a }
        L_0x0083:
            r5 = ru.ok.android.utils.ViewServer.this;
            r5.removeWindowListener(r7);
            r3 = r4;
            goto L_0x0047;
        L_0x008a:
            r5 = move-exception;
            goto L_0x0083;
        L_0x008c:
            r5 = move-exception;
            goto L_0x0042;
        L_0x008e:
            r6 = move-exception;
            goto L_0x0078;
        L_0x0090:
            r5 = move-exception;
            goto L_0x0073;
        L_0x0092:
            r0 = move-exception;
            goto L_0x0034;
            */
            throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.utils.ViewServer.ViewServerWorker.windowManagerAutolistLoop():boolean");
        }
    }

    public static ViewServer get(Context context) {
        ApplicationInfo info = context.getApplicationInfo();
        if (!"user".equals(Build.TYPE) || (info.flags & 2) == 0) {
            sServer = new NoopViewServer();
        } else {
            if (sServer == null) {
                sServer = new ViewServer(4939);
            }
            if (!sServer.isRunning()) {
                try {
                    sServer.start();
                } catch (IOException e) {
                    Log.d("ViewServer", "Error:", e);
                }
            }
        }
        return sServer;
    }

    private ViewServer() {
        this.mListeners = new CopyOnWriteArrayList();
        this.mWindows = new HashMap();
        this.mWindowsLock = new ReentrantReadWriteLock();
        this.mFocusLock = new ReentrantReadWriteLock();
        this.mPort = -1;
    }

    private ViewServer(int port) {
        this.mListeners = new CopyOnWriteArrayList();
        this.mWindows = new HashMap();
        this.mWindowsLock = new ReentrantReadWriteLock();
        this.mFocusLock = new ReentrantReadWriteLock();
        this.mPort = port;
    }

    public boolean start() throws IOException {
        if (this.mThread != null) {
            return false;
        }
        this.mThread = new Thread(this, "Local View Server [port=" + this.mPort + "]");
        this.mThreadPool = Executors.newFixedThreadPool(10);
        this.mThread.start();
        return true;
    }

    public boolean isRunning() {
        return this.mThread != null && this.mThread.isAlive();
    }

    public void setFocusedWindow(Activity activity) {
        setFocusedWindow(activity.getWindow().getDecorView());
    }

    public void setFocusedWindow(View view) {
        this.mFocusLock.writeLock().lock();
        try {
            this.mFocusedWindow = view == null ? null : view.getRootView();
            fireFocusChangedEvent();
        } finally {
            this.mFocusLock.writeLock().unlock();
        }
    }

    public void run() {
        try {
            this.mServer = new ServerSocket(this.mPort, 10, InetAddress.getLocalHost());
        } catch (Exception e) {
            Log.w("ViewServer", "Starting ServerSocket error: ", e);
        }
        while (this.mServer != null && Thread.currentThread() == this.mThread) {
            try {
                Socket client = this.mServer.accept();
                if (this.mThreadPool != null) {
                    this.mThreadPool.submit(new ViewServerWorker(client));
                } else {
                    try {
                        client.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            } catch (Exception e3) {
                Log.w("ViewServer", "Connection error: ", e3);
            }
        }
    }

    private static boolean writeValue(Socket client, String value) {
        Throwable th;
        BufferedWriter out = null;
        try {
            BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()), FragmentTransaction.TRANSIT_EXIT_MASK);
            try {
                out2.write(value);
                out2.write("\n");
                out2.flush();
                if (out2 != null) {
                    try {
                        out2.close();
                        out = out2;
                        return true;
                    } catch (IOException e) {
                        out = out2;
                        return false;
                    }
                }
                return true;
            } catch (Exception e2) {
                out = out2;
                if (out != null) {
                    return false;
                }
                try {
                    out.close();
                    return false;
                } catch (IOException e3) {
                    return false;
                }
            } catch (Throwable th2) {
                th = th2;
                out = out2;
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        } catch (Exception e5) {
            if (out != null) {
                return false;
            }
            out.close();
            return false;
        } catch (Throwable th3) {
            th = th3;
            if (out != null) {
                out.close();
            }
            throw th;
        }
    }

    private void fireFocusChangedEvent() {
        for (WindowListener listener : this.mListeners) {
            listener.focusChanged();
        }
    }

    private void addWindowListener(WindowListener listener) {
        if (!this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    private void removeWindowListener(WindowListener listener) {
        this.mListeners.remove(listener);
    }
}
