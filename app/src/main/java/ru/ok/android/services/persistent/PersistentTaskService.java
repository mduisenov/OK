package ru.ok.android.services.persistent;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import java.util.ArrayList;
import java.util.Iterator;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.persistent.provider.PersistentTasksProvider;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;
import ru.ok.model.UserInfo;

public class PersistentTaskService extends Service implements PersistentTaskContext {
    public static final Uri CONTENT_URI;
    private ConnectivityManager connectivityManager;
    private boolean isInsideStatisticSession;
    private boolean isServiceForeground;
    private LocalBinder localBinder;
    private PersistentTaskNotificationBuilder notificationBuilder;
    private NotificationHandler notificationHandler;
    private PersistentLocalObserversHelper observersHelper;
    private PersistentTaskQueue persistenStorage;
    private TaskHandler taskHandler;
    private UriMatcher uriMatcher;

    /* renamed from: ru.ok.android.services.persistent.PersistentTaskService.1 */
    static /* synthetic */ class C04441 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState;

        static {
            $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState = new int[PersistentTaskState.values().length];
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.EXECUTING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.WAIT_EXTERNAL_STORAGE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.WAIT_INTERNET.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.WAIT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.FAILED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.PAUSED.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.CANCELED.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.ERROR.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    public class LocalBinder extends Binder implements ILocalPersistentTaskService {
        public void registerObserver(PersistentTaskObserver observer) {
            PersistentTaskService.this.observersHelper.registerObserver(observer);
        }

        public void unregisterObserver(PersistentTaskObserver observer) {
            PersistentTaskService.this.observersHelper.unregisterObserver(observer);
        }

        public PersistentTask getTask(int taskId) {
            return PersistentTaskService.this.getTask(taskId);
        }

        public void update(PersistentTask task) {
            Logger.m173d("%s", task);
            try {
                PersistentTaskService.this.persistenStorage.update(task);
            } catch (Throwable e) {
                Logger.m176e("Persistent storage failure: " + e);
                Logger.m178e(e);
            }
        }

        public void resume(PersistentTask task) {
            Logger.m173d("%s", task);
            PersistentTaskService.this.resumeTask(task);
            PersistentTaskService.restart(PersistentTaskService.this.getContext());
        }

        public int submit(PersistentTask task) {
            Logger.m173d("%s", task);
            int taskId = PersistentTaskService.this.submitTask(task);
            if (taskId != 0) {
                task.setId(taskId);
            }
            PersistentTaskService.restart(PersistentTaskService.this.getContext());
            return taskId;
        }

        public PersistentTaskContext getPersistentContext() {
            return PersistentTaskService.this;
        }
    }

    class NotificationHandler extends Handler {
        private long nextNotificationTime;

        NotificationHandler(Looper looper) {
            super(looper);
            this.nextNotificationTime = System.currentTimeMillis();
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1 && PersistentTaskService.this.isServiceForeground) {
                Notification notification = msg.obj;
                Logger.m172d("showing notification for PersistentTaskService");
                PersistentTaskService.this.startForeground(2131624285, notification);
                this.nextNotificationTime = System.currentTimeMillis() + 1000;
            }
        }

        void postShowNotification(Notification notification) {
            long delay;
            long now = System.currentTimeMillis();
            if (now >= this.nextNotificationTime) {
                delay = 0;
            } else {
                delay = this.nextNotificationTime - now;
            }
            removeMessages(1);
            sendMessageDelayed(Message.obtain(this, 1, notification), delay);
        }
    }

    class TaskHandler extends Handler {
        private volatile String currentUserUid;
        private volatile PersistentTask currentlyExecutingTask;

        TaskHandler(Looper looper) {
            super(looper);
        }

        void setCurrentUserUid(String uid) {
            if (uid == null) {
                PersistentTask task = this.currentlyExecutingTask;
                if (task != null) {
                    PersistentTaskService.this.pauseTask(task);
                }
            }
            this.currentUserUid = uid;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    processQueue();
                case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                    setNewTaskParams(msg.obj, msg.getData());
                default:
            }
        }

        void postProcessQueue() {
            if (!hasMessages(1)) {
                sendMessage(Message.obtain(this, 1));
            }
        }

        void postNewTaskParams(PersistentTask task, Bundle params) {
            PersistentTask currentTask = this.currentlyExecutingTask;
            if (currentTask == null || currentTask.getId() != task.getId()) {
                Message m = Message.obtain(this, 3, task);
                m.setData(params);
                sendMessageAtFrontOfQueue(m);
                return;
            }
            currentTask.onNewParams(PersistentTaskService.this, params);
        }

        private void processQueue() {
            PersistentTaskService.this.dumpQueue();
            String currentUid = this.currentUserUid;
            if (currentUid == null) {
                Logger.m172d("Not logged in, pause queue");
            }
            PersistentTask task = currentUid == null ? null : getNextTask(currentUid);
            Logger.m173d("task: %s", task);
            if (task == null || currentUid == null || !currentUid.equals(task.getUid())) {
                Logger.m172d("Empty queue: remove notification");
                PersistentTaskService.this.isServiceForeground = false;
                PersistentTaskService.this.stopForeground(true);
                PersistentTaskService.this.checkEndStatisticSession();
                PersistentTaskService.this.stopSelf();
                return;
            }
            PersistentTaskState initialState = task.getState();
            if (task.isPausing() && initialState != PersistentTaskState.PAUSED) {
                Logger.m173d("pausing: %s", task);
                task.setState(PersistentTaskService.this, PersistentTaskState.PAUSED);
                postProcessQueue();
            } else if (!task.isCanceled() || initialState == PersistentTaskState.CANCELED) {
                if (initialState == PersistentTaskState.SUBMITTED) {
                    task.setState(PersistentTaskService.this, PersistentTaskState.EXECUTING);
                } else if (initialState == PersistentTaskState.WAIT_INTERNET) {
                    if (NetUtils.isConnectionAvailable(PersistentTaskService.this.getContext(), true)) {
                        Logger.m173d("Internet is available. Switching task state from WAIT_INTERNET to EXECUTING: %s", task);
                        task.setState(PersistentTaskService.this, PersistentTaskState.EXECUTING);
                    } else {
                        Logger.m185w("Internet is NOT available. Task waits for Internet: %s", task);
                    }
                } else if (initialState == PersistentTaskState.WAIT_EXTERNAL_STORAGE) {
                    if (PersistentTaskUtils.checkHasExternalStorage(PersistentTaskService.this.getContext())) {
                        Logger.m173d("External storage is mounted. Switching task state from WAIT_EXTERNAL_STORAGE to EXECUTING: %s", task);
                        task.setState(PersistentTaskService.this, PersistentTaskState.EXECUTING);
                    } else {
                        Logger.m185w("External storage is unmounted. Task waits for external storage: %s", task);
                    }
                }
                switch (C04441.$SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[task.getState().ordinal()]) {
                    case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                        PersistentTaskService.this.checkStartStatisticSession();
                        this.currentlyExecutingTask = task;
                        showNotification(task);
                        executeTask(task);
                        this.currentlyExecutingTask = null;
                        postProcessQueue();
                    case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                    case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                    case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    case MessagesProto.Message.UUID_FIELD_NUMBER /*5*/:
                    case MessagesProto.Message.REPLYTO_FIELD_NUMBER /*6*/:
                        showNotification(task);
                    case MessagesProto.Message.ATTACHES_FIELD_NUMBER /*7*/:
                    case MessagesProto.Message.TASKID_FIELD_NUMBER /*8*/:
                        Logger.m185w("Deleting task in %s state: %s", task.getState(), task);
                        removeTask(task);
                        postProcessQueue();
                    default:
                }
            } else {
                Logger.m173d("canceling: %s", task);
                task.setState(PersistentTaskService.this, PersistentTaskState.CANCELED);
                postProcessQueue();
            }
        }

        private PersistentTask getNextTask(String uid) {
            try {
                return PersistentTaskService.this.persistenStorage.firstNotCompleted(uid);
            } catch (Throwable e) {
                Logger.m176e("Persistent queue error: " + e);
                Logger.m178e(e);
                return null;
            }
        }

        private void removeTask(PersistentTask task) {
            Logger.m172d("taskId=" + task.getId());
            task.detachFromParent(PersistentTaskService.this);
            try {
                PersistentTaskService.this.persistenStorage.remove(task);
            } catch (Throwable e) {
                Logger.m176e("Persistent queue error: " + e);
                Logger.m178e(e);
            }
            Iterator i$ = new ArrayList(task.getSubTaskIds()).iterator();
            while (i$.hasNext()) {
                PersistentTask subTask = PersistentTaskService.this.getTask(((Integer) i$.next()).intValue());
                if (subTask != null) {
                    removeTask(subTask);
                }
            }
        }

        private void executeTask(PersistentTask task) {
            Logger.m172d("task: " + task);
            PersistentTaskState finalState;
            int parentId;
            PersistentTask parent;
            try {
                task.setError(null);
                task.setState(PersistentTaskService.this, task.execute(PersistentTaskService.this, PersistentTaskService.this.getContext()));
                finalState = task.getState();
                Logger.m172d("final state: " + finalState);
                if (finalState == PersistentTaskState.COMPLETED) {
                    parentId = task.getParentId();
                    if (parentId == 0) {
                        removeTask(task);
                        return;
                    }
                    parent = PersistentTaskService.this.getTask(parentId);
                    if (parent != null) {
                        parent.onSubTaskCompleted(PersistentTaskService.this, task);
                    }
                }
            } catch (TaskException e) {
                Logger.m180e(e, "Task failed with handled exception: %s", e);
                switch (e.getErrorCode()) {
                    case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                        finalState = PersistentTaskState.WAIT_INTERNET;
                        break;
                    case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                        finalState = PersistentTaskState.WAIT_EXTERNAL_STORAGE;
                        break;
                    case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                        finalState = PersistentTaskState.CANCELED;
                        break;
                    default:
                        finalState = PersistentTaskState.FAILED;
                        break;
                }
                task.setError(e);
                task.incrementFailureCount();
                task.setState(PersistentTaskService.this, finalState);
                finalState = task.getState();
                Logger.m172d("final state: " + finalState);
                if (finalState == PersistentTaskState.COMPLETED) {
                    parentId = task.getParentId();
                    if (parentId == 0) {
                        removeTask(task);
                        return;
                    }
                    parent = PersistentTaskService.this.getTask(parentId);
                    if (parent != null) {
                        parent.onSubTaskCompleted(PersistentTaskService.this, task);
                    }
                }
            } catch (Throwable e2) {
                Logger.m180e(e2, "Task failed with unhandled error: %s", e2);
                Logger.m178e(e2);
                task.setState(PersistentTaskService.this, PersistentTaskState.ERROR);
                finalState = task.getState();
                Logger.m172d("final state: " + finalState);
                if (finalState == PersistentTaskState.COMPLETED) {
                    parentId = task.getParentId();
                    if (parentId == 0) {
                        removeTask(task);
                        return;
                    }
                    parent = PersistentTaskService.this.getTask(parentId);
                    if (parent != null) {
                        parent.onSubTaskCompleted(PersistentTaskService.this, task);
                    }
                }
            } catch (Throwable th) {
                finalState = task.getState();
                Logger.m172d("final state: " + finalState);
                if (finalState == PersistentTaskState.COMPLETED) {
                    parentId = task.getParentId();
                    if (parentId == 0) {
                        removeTask(task);
                    } else {
                        parent = PersistentTaskService.this.getTask(parentId);
                        if (parent != null) {
                            parent.onSubTaskCompleted(PersistentTaskService.this, task);
                        }
                    }
                }
            }
        }

        void showNotification(PersistentTask task) {
            Logger.m173d("task: %s", task);
            PersistentTaskService.this.isServiceForeground = true;
            PersistentTask foregroundTask = task;
            while (foregroundTask != null && foregroundTask.isHidden()) {
                foregroundTask = PersistentTaskService.this.getTask(foregroundTask.getParentId());
            }
            if (foregroundTask != null) {
                Logger.m173d("foreground task: %s", foregroundTask);
                Notification notification = PersistentTaskService.this.createNotification(foregroundTask);
                NotificationHandler notificationHandler = PersistentTaskService.this.notificationHandler;
                if (notificationHandler != null) {
                    notificationHandler.postShowNotification(notification);
                }
            }
        }

        void setNewTaskParams(PersistentTask task, Bundle params) {
            try {
                task = PersistentTaskService.this.persistenStorage.getTask(task.getId());
                if (task == null) {
                    Logger.m185w("Task with id %d could not be found already", Integer.valueOf(taskId));
                    return;
                }
                task.onNewParams(PersistentTaskService.this, params);
                if (task.getState() == PersistentTaskState.PAUSED) {
                    PersistentTaskService.this.resumeTask(task);
                    postProcessQueue();
                }
            } catch (Throwable e) {
                Logger.m179e(e, "Failed to get task from persistent storage");
            }
        }

        public void stop() {
            PersistentTask currentTask = this.currentlyExecutingTask;
            if (currentTask != null) {
                currentTask.onCancel(PersistentTaskService.this);
            }
        }
    }

    public PersistentTaskService() {
        this.observersHelper = new PersistentLocalObserversHelper();
        this.isServiceForeground = false;
        this.isInsideStatisticSession = false;
    }

    static {
        CONTENT_URI = Uri.fromParts("content", "//ru.ok.android/persistent_task", null);
    }

    public static void submit(Context context, PersistentTask task) {
        submit(context, task, null);
    }

    public static void submit(Context context, PersistentTask task, ResultReceiver resultReceiver) {
        Intent intent = new Intent(context, PersistentTaskService.class);
        intent.setAction("ru.ok.android.action.SUBMIT_TASK");
        Bundle extras = new Bundle();
        extras.putParcelable("task", task);
        if (resultReceiver != null) {
            extras.putParcelable("RESULT_RECEIVER", resultReceiver);
        }
        intent.putExtras(extras);
        context.startService(intent);
    }

    public static void restart(Context context) {
        UserInfo currentUser = OdnoklassnikiApplication.getCurrentUser();
        restart(context, currentUser == null ? null : currentUser.uid);
    }

    public static void restart(Context context, String uid) {
        Logger.m173d("uid=%s", uid);
        Intent restartPersistentQueue = new Intent(context, PersistentTaskService.class);
        restartPersistentQueue.setAction("ru.ok.android.action.RESTART_TASKS");
        restartPersistentQueue.putExtra("uid", uid);
        context.startService(restartPersistentQueue);
    }

    public static void reset(Context context) {
        Logger.m172d("");
        Intent logout = new Intent("ru.ok.android.action.RESET");
        logout.setComponent(new ComponentName(context, PersistentTaskService.class));
        context.startService(logout);
    }

    public IBinder onBind(Intent intent) {
        if (this.localBinder == null) {
            this.localBinder = new LocalBinder();
        }
        return this.localBinder;
    }

    public void onCreate() {
        Logger.m172d("");
        super.onCreate();
        this.persistenStorage = new CachedPersistentTaskQueue(new PersistentTaskStorage(this));
        HandlerThread thread = new HandlerThread("PersistentTaskHandler");
        thread.start();
        this.taskHandler = new TaskHandler(thread.getLooper());
        UserInfo currentUser = OdnoklassnikiApplication.getCurrentUser();
        this.taskHandler.setCurrentUserUid(currentUser == null ? null : currentUser.uid);
        thread = new HandlerThread("PersistentService.Notifications");
        thread.start();
        this.notificationHandler = new NotificationHandler(thread.getLooper());
        this.connectivityManager = (ConnectivityManager) getSystemService("connectivity");
        this.notificationBuilder = new PersistentTaskNotificationBuilder(this);
        dumpQueue();
    }

    public void onDestroy() {
        Logger.m172d("");
        super.onDestroy();
        this.taskHandler.removeCallbacksAndMessages(null);
        this.taskHandler.getLooper().quit();
        this.notificationHandler.removeCallbacksAndMessages(null);
        this.notificationHandler.getLooper().quit();
        this.persistenStorage.dispose();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onStartCommand(android.content.Intent r10, int r11, int r12) {
        /*
        r9 = this;
        r6 = 1;
        r5 = 0;
        r1 = 0;
        r7 = 2;
        if (r10 != 0) goto L_0x0038;
    L_0x0006:
        r0 = r1;
    L_0x0007:
        if (r10 != 0) goto L_0x003d;
    L_0x0009:
        r3 = r1;
    L_0x000a:
        if (r3 == 0) goto L_0x0011;
    L_0x000c:
        r1 = new android.os.Bundle;
        r1.<init>();
    L_0x0011:
        r2 = 0;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r8 = "action=";
        r4 = r4.append(r8);
        r4 = r4.append(r0);
        r4 = r4.toString();
        ru.ok.android.utils.Logger.m172d(r4);
        if (r0 != 0) goto L_0x0048;
    L_0x002b:
        r4 = "null action";
        ru.ok.android.utils.Logger.m184w(r4);	 Catch:{ all -> 0x00ec }
        r2 = 2;
        if (r3 == 0) goto L_0x0037;
    L_0x0034:
        r3.send(r2, r1);
    L_0x0037:
        return r7;
    L_0x0038:
        r0 = r10.getAction();
        goto L_0x0007;
    L_0x003d:
        r4 = "RESULT_RECEIVER";
        r4 = r10.getParcelableExtra(r4);
        r4 = (android.os.ResultReceiver) r4;
        r3 = r4;
        goto L_0x000a;
    L_0x0048:
        r4 = -1;
        r8 = r0.hashCode();	 Catch:{ all -> 0x00ec }
        switch(r8) {
            case -1248971477: goto L_0x0092;
            case -1172645946: goto L_0x009d;
            case -911320264: goto L_0x0071;
            case -563731779: goto L_0x00b3;
            case -551470720: goto L_0x007c;
            case 581294437: goto L_0x0087;
            case 713858138: goto L_0x0066;
            case 1030369452: goto L_0x00a8;
            default: goto L_0x0050;
        };	 Catch:{ all -> 0x00ec }
    L_0x0050:
        switch(r4) {
            case 0: goto L_0x00be;
            case 1: goto L_0x00c9;
            case 2: goto L_0x00ce;
            case 3: goto L_0x00d3;
            case 4: goto L_0x00d8;
            case 5: goto L_0x00dd;
            case 6: goto L_0x00e2;
            case 7: goto L_0x00e7;
            default: goto L_0x0053;
        };	 Catch:{ all -> 0x00ec }
    L_0x0053:
        r4 = "Unexpected action: %s";
        r5 = 1;
        r5 = new java.lang.Object[r5];	 Catch:{ all -> 0x00ec }
        r6 = 0;
        r5[r6] = r0;	 Catch:{ all -> 0x00ec }
        ru.ok.android.utils.Logger.m185w(r4, r5);	 Catch:{ all -> 0x00ec }
        r2 = 2;
        if (r3 == 0) goto L_0x0037;
    L_0x0062:
        r3.send(r2, r1);
        goto L_0x0037;
    L_0x0066:
        r6 = "ru.ok.android.action.SUBMIT_TASK";
        r6 = r0.equals(r6);	 Catch:{ all -> 0x00ec }
        if (r6 == 0) goto L_0x0050;
    L_0x006f:
        r4 = r5;
        goto L_0x0050;
    L_0x0071:
        r5 = "ru.ok.android.action.CANCEL_TASK";
        r5 = r0.equals(r5);	 Catch:{ all -> 0x00ec }
        if (r5 == 0) goto L_0x0050;
    L_0x007a:
        r4 = r6;
        goto L_0x0050;
    L_0x007c:
        r5 = "ru.ok.android.action.PAUSE_TASK";
        r5 = r0.equals(r5);	 Catch:{ all -> 0x00ec }
        if (r5 == 0) goto L_0x0050;
    L_0x0085:
        r4 = r7;
        goto L_0x0050;
    L_0x0087:
        r5 = "ru.ok.android.action.RESUME_TASK";
        r5 = r0.equals(r5);	 Catch:{ all -> 0x00ec }
        if (r5 == 0) goto L_0x0050;
    L_0x0090:
        r4 = 3;
        goto L_0x0050;
    L_0x0092:
        r5 = "ru.ok.android.action.SEND_PARAMS";
        r5 = r0.equals(r5);	 Catch:{ all -> 0x00ec }
        if (r5 == 0) goto L_0x0050;
    L_0x009b:
        r4 = 4;
        goto L_0x0050;
    L_0x009d:
        r5 = "android.net.conn.CONNECTIVITY_CHANGE";
        r5 = r0.equals(r5);	 Catch:{ all -> 0x00ec }
        if (r5 == 0) goto L_0x0050;
    L_0x00a6:
        r4 = 5;
        goto L_0x0050;
    L_0x00a8:
        r5 = "ru.ok.android.action.RESTART_TASKS";
        r5 = r0.equals(r5);	 Catch:{ all -> 0x00ec }
        if (r5 == 0) goto L_0x0050;
    L_0x00b1:
        r4 = 6;
        goto L_0x0050;
    L_0x00b3:
        r5 = "ru.ok.android.action.RESET";
        r5 = r0.equals(r5);	 Catch:{ all -> 0x00ec }
        if (r5 == 0) goto L_0x0050;
    L_0x00bc:
        r4 = 7;
        goto L_0x0050;
    L_0x00be:
        r2 = r9.onStartCommand_Submit(r10, r1);	 Catch:{ all -> 0x00ec }
    L_0x00c2:
        if (r3 == 0) goto L_0x0037;
    L_0x00c4:
        r3.send(r2, r1);
        goto L_0x0037;
    L_0x00c9:
        r2 = r9.onStartCommand_Cancel(r10, r1);	 Catch:{ all -> 0x00ec }
        goto L_0x00c2;
    L_0x00ce:
        r2 = r9.onStartCommand_Pause(r10, r1);	 Catch:{ all -> 0x00ec }
        goto L_0x00c2;
    L_0x00d3:
        r2 = r9.onStartCommand_Resume(r10, r1);	 Catch:{ all -> 0x00ec }
        goto L_0x00c2;
    L_0x00d8:
        r2 = r9.onStartCommand_SendParams(r10, r1);	 Catch:{ all -> 0x00ec }
        goto L_0x00c2;
    L_0x00dd:
        r2 = r9.onStartCommand_Connectivity(r10, r1);	 Catch:{ all -> 0x00ec }
        goto L_0x00c2;
    L_0x00e2:
        r2 = r9.onStartCommand_Restart(r10, r1);	 Catch:{ all -> 0x00ec }
        goto L_0x00c2;
    L_0x00e7:
        r2 = r9.onStartCommand_Reset(r10, r1);	 Catch:{ all -> 0x00ec }
        goto L_0x00c2;
    L_0x00ec:
        r4 = move-exception;
        if (r3 == 0) goto L_0x00f2;
    L_0x00ef:
        r3.send(r2, r1);
    L_0x00f2:
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.persistent.PersistentTaskService.onStartCommand(android.content.Intent, int, int):int");
    }

    private int onStartCommand_Submit(Intent intent, Bundle outResult) {
        PersistentTask task = getTaskFromIntent(intent);
        if (task != null) {
            int taskId = submitTask(task);
            if (taskId != 0) {
                if (outResult != null) {
                    outResult.putInt("task_id", taskId);
                }
                return 1;
            }
        }
        Logger.m184w("null extra parameter task");
        return 2;
    }

    private int onStartCommand_Cancel(Intent intent, Bundle outResult) {
        PersistentTask task = getTaskFromIntent(intent);
        if (task != null) {
            cancelTask(task);
            this.taskHandler.postProcessQueue();
            return 1;
        }
        Logger.m184w("null extra parameter task");
        return 2;
    }

    private int onStartCommand_Pause(Intent intent, Bundle outResult) {
        PersistentTask task = getTaskFromIntent(intent);
        if (task != null) {
            pauseTask(task);
            this.taskHandler.postProcessQueue();
            return 1;
        }
        Logger.m184w("null extra parameter task");
        return 2;
    }

    private int onStartCommand_Resume(Intent intent, Bundle outResult) {
        PersistentTask task = getTaskFromIntent(intent);
        if (task != null) {
            resumeTask(task);
            this.taskHandler.postProcessQueue();
            return 1;
        }
        Logger.m184w("null extra parameter task");
        return 2;
    }

    private int onStartCommand_SendParams(Intent intent, Bundle outResult) {
        PersistentTask task = getTaskFromIntent(intent);
        Bundle params = intent.getBundleExtra("task_params");
        if (task == null) {
            Logger.m184w("SEND_PARAMS: null extra parameter task");
        } else if (params == null) {
            Logger.m184w("SEND_PARAMS: null extra parameter: task_params");
        } else {
            Logger.m172d("SEND_PARAMS: posting send_param to worker queue...");
            this.taskHandler.postNewTaskParams(task, params);
            return 1;
        }
        return 2;
    }

    private int onStartCommand_Connectivity(Intent intent, Bundle outResult) {
        this.taskHandler.postProcessQueue();
        return 1;
    }

    private int onStartCommand_Restart(Intent intent, Bundle outResult) {
        this.taskHandler.setCurrentUserUid(intent.getStringExtra("uid"));
        this.taskHandler.postProcessQueue();
        return 1;
    }

    private int onStartCommand_Reset(Intent intent, Bundle outResult) {
        PersistentTasksProvider.clearDB(getContext());
        this.persistenStorage.reset();
        this.taskHandler.stop();
        stopForeground(true);
        stopSelf();
        return 1;
    }

    private PersistentTask getTaskFromIntent(Intent intent) {
        if ("ru.ok.android.action.SUBMIT_TASK".equals(intent.getAction())) {
            return (PersistentTask) intent.getParcelableExtra("task");
        }
        int taskId = 0;
        try {
            taskId = intent.getIntExtra("task_id", 0);
        } catch (Throwable e) {
            Logger.m184w("Invalid task_id: " + e);
            Logger.m178e(e);
        }
        if (taskId == 0) {
            Uri dataUri = intent.getData();
            if (dataUri != null) {
                if (this.uriMatcher == null) {
                    this.uriMatcher = new UriMatcher(-1);
                    this.uriMatcher.addURI("ru.ok.android", "persistent_task/#", 1);
                }
                if (this.uriMatcher.match(dataUri) == 1) {
                    taskId = (int) ContentUris.parseId(dataUri);
                } else {
                    Logger.m184w("Unsupported uri: " + dataUri);
                }
            }
        }
        if (taskId != 0) {
            return getTask(taskId);
        }
        Logger.m176e("task ID not specified");
        return null;
    }

    public Context getContext() {
        return this;
    }

    private void cancelTask(PersistentTask task) {
        Logger.m173d("%s", task);
        cancelWithSubtasks(task);
    }

    private void cancelWithSubtasks(PersistentTask task) {
        Logger.m173d("canceling task: %s", task);
        task.cancel(this);
        save(task);
        notifyListeners(task);
        Iterator i$ = new ArrayList(task.getSubTaskIds()).iterator();
        while (i$.hasNext()) {
            PersistentTask subTask = getTask(((Integer) i$.next()).intValue());
            if (subTask != null) {
                cancelWithSubtasks(subTask);
            }
        }
    }

    private void pauseTask(PersistentTask task) {
        Logger.m173d("%s", task);
        pauseWithSubtasks(findTopmostParentTask(task));
    }

    private void pauseWithSubtasks(PersistentTask task) {
        Logger.m173d("pausing task: %s", task);
        task.setPausing(this, true);
        save(task);
        notifyListeners(task);
        Iterator i$ = new ArrayList(task.getSubTaskIds()).iterator();
        while (i$.hasNext()) {
            PersistentTask subTask = getTask(((Integer) i$.next()).intValue());
            if (subTask != null) {
                pauseWithSubtasks(subTask);
            }
        }
    }

    private void resumeTask(PersistentTask task) {
        Logger.m173d("%s", task);
        resumeWithSubTasks(findTopmostParentTask(task));
    }

    private void resumeWithSubTasks(PersistentTask task) {
        Logger.m173d("resuming task: %s", task);
        task.setPausing(this, false);
        PersistentTaskState state = task.getState();
        if (state == PersistentTaskState.PAUSED || state == PersistentTaskState.FAILED || state == PersistentTaskState.ERROR) {
            task.setState(this, PersistentTaskState.EXECUTING);
        } else {
            save(task);
        }
        notifyListeners(task);
        Iterator i$ = new ArrayList(task.getSubTaskIds()).iterator();
        while (i$.hasNext()) {
            PersistentTask subTask = getTask(((Integer) i$.next()).intValue());
            if (subTask != null) {
                resumeWithSubTasks(subTask);
            }
        }
    }

    private PersistentTask findTopmostParentTask(PersistentTask task) {
        try {
            int parentId = task.getParentId();
            if (parentId != 0) {
                PersistentTask parentTask = this.persistenStorage.getTask(parentId);
                if (parentTask != null) {
                    task = findTopmostParentTask(parentTask);
                }
            }
        } catch (Throwable e) {
            Logger.m176e("Persistent queue error: " + e);
            Logger.m178e(e);
        }
        return task;
    }

    public void scheduleRetry(PersistentTask task, long delayFromNowMs) {
        ((AlarmManager) getSystemService(NotificationCompat.CATEGORY_ALARM)).set(3, SystemClock.elapsedRealtime() + delayFromNowMs, PendingIntent.getService(this, task.getId(), new Intent("ru.ok.android.action.RESTART_TASKS", ContentUris.withAppendedId(CONTENT_URI, (long) task.getId())), 268435456));
    }

    public int submitTask(PersistentTask task) {
        Logger.m173d("%s", task);
        if (task.getParentId() != 0) {
            throw new IllegalArgumentException("Attempt to submit sub-task to the end of queue. Consider using submitSubTask(...)");
        }
        int taskId = 0;
        try {
            taskId = this.persistenStorage.addToQueue(task);
            this.taskHandler.postProcessQueue();
            return taskId;
        } catch (Throwable e) {
            Logger.m176e("Failed to submit new task: " + e);
            Logger.m178e(e);
            return taskId;
        }
    }

    public void submitSubTask(PersistentTask subTask) {
        Logger.m173d("%s", subTask);
        if (subTask.getParentId() == 0) {
            Logger.m184w("Submitting normal task in front of queue. Consider using submitTask(...)");
        }
        try {
            this.persistenStorage.addInFrontOfQueue(subTask);
            this.taskHandler.postProcessQueue();
        } catch (Throwable e) {
            Logger.m176e("Failed to submit new task: " + e);
            Logger.m178e(e);
        }
    }

    public void cancelSubTask(PersistentTask subTask) {
        Logger.m173d("%s", subTask);
        subTask.cancel(this);
    }

    public <T extends PersistentTask> T getTask(int taskId) {
        Logger.m173d("%d", Integer.valueOf(taskId));
        try {
            return this.persistenStorage.getTask(taskId);
        } catch (Throwable e) {
            Logger.m176e("Failed to restore task id=" + taskId + ": " + e);
            Logger.m178e(e);
            return null;
        }
    }

    public void save(PersistentTask task) {
        Logger.m173d("%s", task);
        try {
            this.persistenStorage.update(task);
        } catch (Throwable e) {
            Logger.m176e("Persistent storage failure: " + e);
            Logger.m178e(e);
        }
    }

    public void notifyOnChanged(PersistentTask task) {
        notifyListeners(task);
        int parentId = task.getParentId();
        PersistentTask parent = parentId == 0 ? null : getTask(parentId);
        if (parent != null) {
            parent.setSubTaskState(task.getId(), task.getState());
            parent.onSubTaskStateChanged(this, task);
            parent.persist(this);
        }
        if (!task.isHidden()) {
            this.taskHandler.showNotification(task);
        }
    }

    private void notifyListeners(PersistentTask task) {
        this.observersHelper.notifyTaskUpdated(task.copy());
    }

    void dumpQueue() {
        if (Logger.isLoggingEnable()) {
            try {
                ArrayList<PersistentTask> tasks = this.persistenStorage.getAllTasks();
                for (int i = 0; i < tasks.size(); i++) {
                    Logger.m173d("QUEUE[%d]: %s", Integer.valueOf(i), tasks.get(i));
                }
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        }
    }

    Notification createNotification(PersistentTask task) {
        task.createNotification(this, this.notificationBuilder);
        PendingIntent taskDetailsIntent = task.getTaskDetailsIntent(this);
        if (taskDetailsIntent != null) {
            this.notificationBuilder.setContentIntent(taskDetailsIntent);
        }
        return this.notificationBuilder.build();
    }

    private void checkStartStatisticSession() {
        if (!this.isInsideStatisticSession) {
            StatisticManager.getInstance().startSession(getContext());
            this.isInsideStatisticSession = true;
        }
    }

    private void checkEndStatisticSession() {
        if (this.isInsideStatisticSession) {
            StatisticManager.getInstance().endSession(getContext());
            this.isInsideStatisticSession = false;
        }
    }

    public static Intent createCancelTaskIntent(Context context, PersistentTask task) {
        return createTaskIntent(context, task, "ru.ok.android.action.CANCEL_TASK");
    }

    public static Intent createCancelTaskIntent(Context context, int taskId) {
        return createTaskIntent(context, taskId, "ru.ok.android.action.CANCEL_TASK");
    }

    public static Intent createPauseTaskIntent(Context context, PersistentTask task) {
        return createTaskIntent(context, task, "ru.ok.android.action.PAUSE_TASK");
    }

    public static Intent createResumeTaskIntent(Context context, PersistentTask task) {
        return createTaskIntent(context, task, "ru.ok.android.action.RESUME_TASK");
    }

    public static Intent createSendParamsIntent(Context context, PersistentTask task, Bundle params) {
        return createSendParamsIntent(context, task.getId(), params);
    }

    public static Intent createSendParamsIntent(Context context, int taskId, Bundle params) {
        Intent intent = new Intent(context, PersistentTaskService.class);
        intent.setAction("ru.ok.android.action.SEND_PARAMS");
        intent.putExtra("task_id", taskId);
        intent.putExtra("task_params", params);
        return intent;
    }

    static Intent createTaskIntent(Context context, int taskId, String action) {
        Intent intent = new Intent(context, PersistentTaskService.class);
        intent.setAction(action);
        intent.putExtra("task_id", taskId);
        return intent;
    }

    static Intent createTaskIntent(Context context, PersistentTask task, String action) {
        return createTaskIntent(context, task.getId(), action);
    }
}
