package ru.ok.android.utils.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat.Builder;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.URLUtil;
import ru.ok.android.utils.UserMedia;
import ru.ok.android.utils.UserMedia.OnImageAddedListener;
import ru.ok.android.utils.download.DownloadManager.Request;
import ru.ok.android.utils.localization.LocalizationManager;

class DownloadManagerBase extends DownloadManager {
    private ExecutorService executorService;

    /* renamed from: ru.ok.android.utils.download.DownloadManagerBase.1 */
    class C14661 implements DownloadRunnableListener {
        final /* synthetic */ File val$destFile;
        final /* synthetic */ Request val$request;

        /* renamed from: ru.ok.android.utils.download.DownloadManagerBase.1.1 */
        class C14651 implements OnImageAddedListener {
            final /* synthetic */ DownloadRunnable val$runnable;

            C14651(DownloadRunnable downloadRunnable) {
                this.val$runnable = downloadRunnable;
            }

            public void onImageAdded(String path, Uri uri) {
                DownloadManagerBase.this.getNotificationManager().notify(this.val$runnable.getId(), DownloadManagerBase.this.getEndNotification(C14661.this.val$destFile, C14661.this.val$request));
            }
        }

        C14661(File file, Request request) {
            this.val$destFile = file;
            this.val$request = request;
        }

        public void onDownloadStarted(DownloadRunnable runnable) {
            DownloadManagerBase.this.getNotificationManager().notify(runnable.getId(), DownloadManagerBase.this.getStartNotification(this.val$destFile));
        }

        public void onDownloadProgress(DownloadRunnable runnable, long total, long completed) {
        }

        public void onDownloadFinished(DownloadRunnable runnable) {
            if (this.val$request.isAllowMediaRescan()) {
                UserMedia.addImageToMedia(this.val$destFile, DownloadManagerBase.this.getContext(), new C14651(runnable));
            }
        }

        public void onDownloadError(DownloadRunnable runnable) {
            DownloadManagerBase.this.getNotificationManager().notify(runnable.getId(), DownloadManagerBase.this.getErrorNotification(this.val$destFile));
        }
    }

    protected static final class DownloadRunnable implements Runnable {
        private final int id;
        private final DownloadRunnableListener listener;
        private final Request request;

        public interface DownloadRunnableListener {
            void onDownloadError(DownloadRunnable downloadRunnable);

            void onDownloadFinished(DownloadRunnable downloadRunnable);

            void onDownloadProgress(DownloadRunnable downloadRunnable, long j, long j2);

            void onDownloadStarted(DownloadRunnable downloadRunnable);
        }

        public DownloadRunnable(Request request, DownloadRunnableListener listener) {
            this.request = request;
            this.listener = listener;
            this.id = (int) (((double) System.currentTimeMillis()) * 1.0E-5d);
        }

        public void run() {
            Closeable is;
            Closeable fos;
            if (this.listener != null) {
                this.listener.onDownloadStarted(this);
            }
            try {
                URL imageUrl = new URL(this.request.getUri().toString());
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection(NetUtils.getProxyForUrl(imageUrl));
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(false);
                conn.connect();
                if (conn.getResponseCode() != 302 || !URLUtil.isStubUrl(conn.getHeaderField("Location"))) {
                    long fileLength = (long) conn.getContentLength();
                    long loadedLength = 0;
                    is = conn.getInputStream();
                    File file = new File(this.request.getDestinationUri().getPath());
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    fos = new FileOutputStream(file);
                    byte[] bytes = new byte[1024];
                    while (true) {
                        int count = is.read(bytes, 0, 1024);
                        if (count == -1) {
                            break;
                        }
                        fos.write(bytes, 0, count);
                        if (this.listener != null) {
                            loadedLength += (long) count;
                            this.listener.onDownloadProgress(this, fileLength, loadedLength);
                        }
                    }
                    if (this.listener != null) {
                        this.listener.onDownloadFinished(this);
                    }
                    IOUtils.closeSilently(is);
                    IOUtils.closeSilently(fos);
                } else if (this.listener != null) {
                    this.listener.onDownloadError(this);
                }
            } catch (Exception e) {
                if (this.listener != null) {
                    this.listener.onDownloadError(this);
                }
            } catch (Throwable th) {
                IOUtils.closeSilently(is);
                IOUtils.closeSilently(fos);
            }
        }

        public int getId() {
            return this.id;
        }
    }

    public DownloadManagerBase(Context context) {
        super(context);
        this.executorService = Executors.newCachedThreadPool();
    }

    public long enqueue(Context context, Request request) {
        DownloadRunnable runnable = new DownloadRunnable(request, new C14661(new File(request.getDestinationUri().getPath()), request));
        this.executorService.submit(runnable);
        return (long) runnable.getId();
    }

    private Notification getStartNotification(File destFile) {
        String tickerText = LocalizationManager.getString(getContext(), 2131165718);
        Builder builder = new Builder(getContext());
        builder.setSmallIcon(2130838506);
        builder.setTicker(tickerText);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(tickerText);
        builder.setContentText(destFile.getName());
        builder.setAutoCancel(true);
        builder.setContentIntent(getDummyIntent());
        return builder.build();
    }

    private Notification getEndNotification(File destFile, Request request) {
        String tickerText = LocalizationManager.getString(getContext(), 2131165720);
        Builder builder = new Builder(getContext());
        builder.setSmallIcon(2130838517);
        builder.setTicker(tickerText);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(tickerText);
        builder.setContentText(destFile.getName());
        builder.setAutoCancel(true);
        if (request.getIntent() == null) {
            builder.setContentIntent(getDummyIntent());
        } else {
            builder.setContentIntent(getPendingIntent(request.getIntent()));
        }
        return builder.build();
    }

    private Notification getErrorNotification(File destFile) {
        String tickerText = LocalizationManager.getString(getContext(), 2131165719);
        Builder builder = new Builder(getContext());
        builder.setSmallIcon(2130838516);
        builder.setTicker(tickerText);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(tickerText);
        builder.setContentText(destFile.getName());
        builder.setAutoCancel(true);
        builder.setContentIntent(getDummyIntent());
        return builder.build();
    }

    private PendingIntent getPendingIntent(Intent intent) {
        return PendingIntent.getActivity(getContext(), 0, intent, 134217728);
    }

    private PendingIntent getDummyIntent() {
        return PendingIntent.getActivity(getContext(), 0, new Intent(), 268435456);
    }

    private Context getContext() {
        return OdnoklassnikiApplication.getContext();
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getContext().getSystemService("notification");
    }
}
