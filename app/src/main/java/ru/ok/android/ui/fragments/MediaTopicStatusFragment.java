package ru.ok.android.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.persistent.ILocalPersistentTaskService;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskObserver;
import ru.ok.android.services.persistent.PersistentTaskService;
import ru.ok.android.services.persistent.PersistentTaskService.LocalBinder;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.processors.image.upload.UploadImagesState;
import ru.ok.android.services.processors.image.upload.UploadImagesState.UploadImagesPhase;
import ru.ok.android.services.processors.mediatopic.MediaTopicPostException;
import ru.ok.android.services.processors.mediatopic.MediaTopicPostState;
import ru.ok.android.services.processors.mediatopic.MediaTopicPostState.MediaTopicPostPhase;
import ru.ok.android.services.processors.mediatopic.MediaTopicPostUtils;
import ru.ok.android.services.processors.mediatopic.PostMediaTopicTask;
import ru.ok.android.services.processors.photo.upload.ImageUploadException;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.activity.MediaTopicStatusActivity;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerData;
import ru.ok.android.ui.custom.mediacomposer.MediaTopicMessage;
import ru.ok.android.ui.dialogs.AlertFragmentDialog;
import ru.ok.android.ui.dialogs.AlertFragmentDialog.OnAlertDismissListener;
import ru.ok.android.ui.dialogs.ConfirmationDialog;
import ru.ok.android.ui.dialogs.ConfirmationDialog.OnConfirmationDialogListener;
import ru.ok.android.ui.fragments.MediaComposerFragment.MediaComposerFragmentListener;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.exceptions.ServerReturnErrorException;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class MediaTopicStatusFragment extends MediaComposerFragment implements OnAlertDismissListener, OnConfirmationDialogListener {
    private static final String[] allDialogFragmentTags;
    private boolean doCancel;
    private boolean errorIsAcknowledged;
    private ViewGroup fadingContainer;
    private InfoLayerType infoLayerType;
    private MediaTopicStatusFragmentListener listener;
    private LocalizationManager localizationManager;
    private PostMediaTopicTask mediaTopicTask;
    private PersistentServiceFragment persistentServiceFragment;
    final UIHandler uiHandler;
    private boolean wasPausedForCancel;

    public interface MediaTopicStatusFragmentListener extends MediaComposerFragmentListener {
        void onCancelledUpload();

        void onClose();

        void onProgressChanged(int i, int i2);
    }

    interface PersistentServiceRunnable {
        void run(ILocalPersistentTaskService iLocalPersistentTaskService);
    }

    /* renamed from: ru.ok.android.ui.fragments.MediaTopicStatusFragment.1 */
    class C08001 implements PersistentServiceRunnable {
        final /* synthetic */ MediaComposerData val$data;

        C08001(MediaComposerData mediaComposerData) {
            this.val$data = mediaComposerData;
        }

        public void run(ILocalPersistentTaskService service) {
            MediaTopicStatusFragment.this.mediaTopicTask.updateMediaTopic(service.getPersistentContext(), this.val$data);
            service.update(MediaTopicStatusFragment.this.mediaTopicTask);
            service.resume(MediaTopicStatusFragment.this.mediaTopicTask);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.MediaTopicStatusFragment.2 */
    static /* synthetic */ class C08012 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState;

        static {
            $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState = new int[PersistentTaskState.values().length];
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.SUBMITTED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.EXECUTING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.PAUSED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.COMPLETED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.FAILED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.ERROR.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    private enum InfoLayerType {
        NONE,
        PROGRESS_INDETERMINATE
    }

    public static class PersistentServiceFragment extends Fragment {
        private ServiceConnection connection;
        private boolean isConnected;
        private MediaTopicTaskObserver mediaTopicObserver;
        private final Queue<Message> queue;
        private ILocalPersistentTaskService service;
        private int taskId;

        /* renamed from: ru.ok.android.ui.fragments.MediaTopicStatusFragment.PersistentServiceFragment.1 */
        class C08021 implements ServiceConnection {
            C08021() {
            }

            public void onServiceConnected(ComponentName name, IBinder binder) {
                Logger.m172d("");
                PersistentServiceFragment.this.service = (LocalBinder) binder;
                PersistentServiceFragment.this.processQueue();
                PersistentServiceFragment.this.service.registerObserver(PersistentServiceFragment.this.mediaTopicObserver);
                try {
                    PostMediaTopicTask task = (PostMediaTopicTask) PersistentServiceFragment.this.service.getTask(PersistentServiceFragment.this.taskId);
                    if (task != null) {
                        PersistentServiceFragment.this.mediaTopicObserver.onTaskUpdated(task);
                    }
                } catch (Throwable e) {
                    Logger.m176e("Failed to get task from service: " + e);
                    Logger.m178e(e);
                }
            }

            public void onServiceDisconnected(ComponentName name) {
                Logger.m172d("");
                PersistentServiceFragment.this.isConnected = false;
                PersistentServiceFragment.this.service = null;
            }
        }

        class MediaTopicTaskObserver extends PersistentTaskObserver {
            protected MediaTopicTaskObserver(int taskId) {
                super(taskId);
            }

            protected void onTaskUpdated(PersistentTask persistentTask) {
                ((MediaTopicStatusFragment) PersistentServiceFragment.this.getTargetFragment()).uiHandler.postUpdateTask((PostMediaTopicTask) persistentTask);
            }
        }

        public PersistentServiceFragment() {
            this.isConnected = false;
            this.queue = new LinkedList();
            this.connection = new C08021();
        }

        public static PersistentServiceFragment newInstance(int taskId) {
            PersistentServiceFragment fragment = new PersistentServiceFragment();
            Bundle args = new Bundle();
            args.putInt("task_id", taskId);
            fragment.setArguments(args);
            return fragment;
        }

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.taskId = getArguments().getInt("task_id");
            this.mediaTopicObserver = new MediaTopicTaskObserver(this.taskId);
        }

        void run(PersistentServiceRunnable runnable) {
            if (this.service != null) {
                doRun(runnable);
                return;
            }
            synchronized (this.queue) {
                this.queue.add(Message.obtain(null, 2, runnable));
            }
            if (!this.isConnected) {
                connectToService();
            }
        }

        private void doRun(PersistentServiceRunnable runnable) {
            try {
                runnable.run(this.service);
            } catch (Throwable e) {
                Logger.m176e("Service runnable failure: " + e);
                Logger.m178e(e);
            }
        }

        public void onResume() {
            super.onResume();
            Logger.m172d("");
            if (!this.isConnected) {
                connectToService();
            }
        }

        private void connectToService() {
            this.isConnected = true;
            Activity activity = getActivity();
            if (activity == null) {
                Logger.m184w("activity is null");
                return;
            }
            Logger.m172d("connecting to service...");
            activity.bindService(new Intent(activity, PersistentTaskService.class), this.connection, 0);
        }

        public void onPause() {
            super.onPause();
            Logger.m172d("");
            if (this.service != null) {
                Logger.m172d("disconnecting from service...");
                this.service.unregisterObserver(this.mediaTopicObserver);
                this.service = null;
                Activity activity = getActivity();
                if (activity != null) {
                    activity.unbindService(this.connection);
                }
                this.isConnected = false;
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void processQueue() {
            /*
            r4 = this;
            r2 = r4.queue;
            monitor-enter(r2);
        L_0x0003:
            r1 = r4.queue;	 Catch:{ all -> 0x001d }
            r0 = r1.poll();	 Catch:{ all -> 0x001d }
            r0 = (android.os.Message) r0;	 Catch:{ all -> 0x001d }
            if (r0 == 0) goto L_0x0025;
        L_0x000d:
            r1 = r0.what;	 Catch:{ all -> 0x0020 }
            r3 = 2;
            if (r1 != r3) goto L_0x0019;
        L_0x0012:
            r1 = r0.obj;	 Catch:{ all -> 0x0020 }
            r1 = (ru.ok.android.ui.fragments.MediaTopicStatusFragment.PersistentServiceRunnable) r1;	 Catch:{ all -> 0x0020 }
            r4.doRun(r1);	 Catch:{ all -> 0x0020 }
        L_0x0019:
            r0.recycle();	 Catch:{ all -> 0x001d }
            goto L_0x0003;
        L_0x001d:
            r1 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x001d }
            throw r1;
        L_0x0020:
            r1 = move-exception;
            r0.recycle();	 Catch:{ all -> 0x001d }
            throw r1;	 Catch:{ all -> 0x001d }
        L_0x0025:
            monitor-exit(r2);	 Catch:{ all -> 0x001d }
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.fragments.MediaTopicStatusFragment.PersistentServiceFragment.processQueue():void");
        }
    }

    private class UIHandler extends Handler {
        private MediaTopicPostState lastDisplayedState;

        private UIHandler() {
        }

        public void handleMessage(Message msg) {
            FragmentActivity activity = MediaTopicStatusFragment.this.getActivity();
            if (activity == null) {
                Logger.m184w("activity is null");
                return;
            }
            switch (msg.what) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    updateTask(activity, (PostMediaTopicTask) msg.obj);
                default:
            }
        }

        void postUpdateTask(PostMediaTopicTask task) {
            sendMessage(Message.obtain(this, 1, task));
        }

        void updateTask(FragmentActivity activity, PostMediaTopicTask task) {
            Logger.m172d("" + task.getMediaTopicState());
            MediaTopicStatusFragment.this.mediaTopicTask = task;
            showTask(activity, task, false);
        }

        void showTask(FragmentActivity activity, PostMediaTopicTask mediaTopicTask, boolean forceRenew) {
            boolean stateHasChanged;
            MediaTopicPostState mediaTopicState = mediaTopicTask.getMediaTopicState();
            if (this.lastDisplayedState == null || this.lastDisplayedState.equals(mediaTopicState)) {
                stateHasChanged = false;
            } else {
                stateHasChanged = true;
            }
            if (stateHasChanged) {
                MediaTopicStatusFragment.this.errorIsAcknowledged = false;
            }
            if (forceRenew || this.lastDisplayedState == null || stateHasChanged) {
                showState(activity, mediaTopicState);
                showWindowProgress(mediaTopicState);
                Logger.m172d("calling invalidateOptionsMenu");
                activity.supportInvalidateOptionsMenu();
            }
            this.lastDisplayedState = mediaTopicState;
        }

        void showState(Activity activity, MediaTopicPostState state) {
            int textResId;
            boolean showIndeterminateProgress;
            boolean isUserType = MediaTopicStatusFragment.this.mediaTopicType == MediaTopicType.USER;
            if (!state.isPausing()) {
                switch (C08012.$SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[state.getExecutionState().ordinal()]) {
                    case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                        textResId = isUserType ? 2131166138 : 2131166137;
                        showIndeterminateProgress = false;
                        break;
                    case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                        textResId = MediaTopicPostUtils.getMediaTopicIsCompletedTextResId(MediaTopicStatusFragment.this.mediaTopicType);
                        showIndeterminateProgress = false;
                        break;
                    case MessagesProto.Message.UUID_FIELD_NUMBER /*5*/:
                    case MessagesProto.Message.REPLYTO_FIELD_NUMBER /*6*/:
                        showError(state);
                        return;
                    default:
                        textResId = isUserType ? 2131166135 : 2131166133;
                        showIndeterminateProgress = true;
                        break;
                }
            }
            textResId = 2131166139;
            showIndeterminateProgress = true;
            MediaTopicStatusFragment.this.showIndeterminateProgress(activity, showIndeterminateProgress, MediaTopicStatusFragment.this.localizationManager.getString(textResId));
        }

        void showError(MediaTopicPostState mediaTopicState) {
            if (!MediaTopicStatusFragment.this.doCancel) {
                int i = MediaTopicStatusFragment.this.mode;
                if (r0 != 1) {
                    if (!MediaTopicStatusFragment.this.errorIsAcknowledged) {
                        PersistentTaskState executionState = mediaTopicState.getExecutionState();
                        if (executionState == PersistentTaskState.FAILED || executionState == PersistentTaskState.ERROR) {
                            int serverErrorCode;
                            String serverErrorMessage;
                            String message;
                            List<String> restrictedUids = null;
                            MediaTopicPostException error = (MediaTopicPostException) mediaTopicState.getError();
                            int errorCode = error == null ? 999 : error.getErrorCode();
                            Throwable cause = error == null ? null : error.getCause();
                            int imageUploadErrorCode = (errorCode == 11 && (cause instanceof ImageUploadException)) ? ((ImageUploadException) cause).getErrorCode() : 0;
                            boolean isUserType = MediaTopicStatusFragment.this.mediaTopicType == MediaTopicType.USER;
                            ServerReturnErrorException serverError;
                            if (errorCode == 4 && (cause instanceof ServerReturnErrorException)) {
                                serverError = (ServerReturnErrorException) cause;
                                serverErrorCode = serverError.getErrorCode();
                                serverErrorMessage = serverError.getErrorMessage();
                            } else if (errorCode == 11 && imageUploadErrorCode == 4) {
                                Throwable imageCause = cause.getCause();
                                if (imageCause instanceof ServerReturnErrorException) {
                                    serverError = (ServerReturnErrorException) imageCause;
                                    serverErrorCode = serverError.getErrorCode();
                                    serverErrorMessage = serverError.getErrorMessage();
                                } else {
                                    serverErrorCode = 0;
                                    serverErrorMessage = null;
                                }
                            } else {
                                serverErrorCode = 0;
                                serverErrorMessage = null;
                            }
                            if (errorCode == 1 || (errorCode == 11 && imageUploadErrorCode == 1)) {
                                message = MediaTopicStatusFragment.this.localizationManager.getString(2131166140);
                            } else if (errorCode == 11 && imageUploadErrorCode == 2) {
                                message = MediaTopicStatusFragment.this.localizationManager.getString(2131166141);
                            } else if (errorCode == 12 || (errorCode == 11 && imageUploadErrorCode == 14)) {
                                message = MediaTopicStatusFragment.this.localizationManager.getString(2131166142);
                            } else if (errorCode == 4 || (errorCode == 11 && imageUploadErrorCode == 4)) {
                                switch (serverErrorCode) {
                                    case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                                        message = MediaTopicStatusFragment.this.localizationManager.getString(2131166142);
                                        break;
                                    case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                                        if (serverErrorMessage != null && serverErrorMessage.contains("error.mediatopic.withFriendsLimitReached")) {
                                            message = MediaTopicStatusFragment.this.localizationManager.getString(2131166172);
                                            break;
                                        } else {
                                            message = MediaTopicStatusFragment.this.localizationManager.getString(isUserType ? 2131166157 : 2131166156);
                                            break;
                                        }
                                    case 454:
                                        message = MediaTopicStatusFragment.this.localizationManager.getString(isUserType ? 2131166107 : 2131166106);
                                        break;
                                    case 458:
                                        restrictedUids = error.getPrivacyRestrictionUids();
                                        if (restrictedUids != null && !restrictedUids.isEmpty()) {
                                            message = null;
                                            break;
                                        }
                                        message = MediaTopicStatusFragment.this.localizationManager.getString(2131166168);
                                        restrictedUids = null;
                                        break;
                                    case 600:
                                        message = MediaTopicStatusFragment.this.localizationManager.getString(2131166155);
                                        break;
                                    case 601:
                                        message = MediaTopicStatusFragment.this.localizationManager.getString(2131166170);
                                        break;
                                    case 602:
                                        message = MediaTopicStatusFragment.this.localizationManager.getString(2131166163);
                                        break;
                                    case 603:
                                        message = MediaTopicStatusFragment.this.localizationManager.getString(2131166161);
                                        break;
                                    case 604:
                                        message = MediaTopicStatusFragment.this.localizationManager.getString(2131166159);
                                        break;
                                    case 605:
                                        message = MediaTopicStatusFragment.this.localizationManager.getString(2131166172);
                                        break;
                                    case 606:
                                        message = MediaTopicStatusFragment.this.localizationManager.getString(2131166174);
                                        break;
                                    default:
                                        message = MediaTopicStatusFragment.this.localizationManager.getString(isUserType ? 2131166157 : 2131166156);
                                        break;
                                }
                            } else if (errorCode == 11 && imageUploadErrorCode == 15) {
                                message = MediaTopicStatusFragment.this.localizationManager.getString(isUserType ? 2131166125 : 2131166124);
                            } else if (errorCode == 11 && imageUploadErrorCode == 16) {
                                message = MediaTopicStatusFragment.this.localizationManager.getString(isUserType ? 2131166150 : 2131166149);
                            } else if (errorCode != 999 && errorCode == 11 && imageUploadErrorCode == 999) {
                                if (cause == null) {
                                    if (isUserType) {
                                    }
                                    message = MediaTopicStatusFragment.this.localizationManager.getString(isUserType ? 2131166127 : 2131166126);
                                } else {
                                    if (isUserType) {
                                    }
                                    format = MediaTopicStatusFragment.this.localizationManager.getString(isUserType ? 2131166129 : 2131166128);
                                    causeMessage = cause.getLocalizedMessage();
                                    if (causeMessage == null) {
                                        causeMessage = cause.toString();
                                    }
                                    message = String.format(MediaTopicStatusFragment.this.getResources().getConfiguration().locale, format, new Object[]{causeMessage});
                                }
                            } else if (cause == null) {
                                format = MediaTopicStatusFragment.this.localizationManager.getString(isUserType ? 2131166129 : 2131166128);
                                causeMessage = cause.getLocalizedMessage();
                                if (causeMessage == null) {
                                    causeMessage = cause.toString();
                                }
                                message = String.format(MediaTopicStatusFragment.this.getResources().getConfiguration().locale, format, new Object[]{causeMessage});
                            } else {
                                message = MediaTopicStatusFragment.this.localizationManager.getString(isUserType ? 2131166127 : 2131166126);
                            }
                            MediaTopicStatusFragment.this.hideInfoLayer();
                            if (restrictedUids != null) {
                                showPrivacyRestrictionDialog(restrictedUids);
                                return;
                            } else {
                                showErrorDialog(message);
                                return;
                            }
                        }
                        Logger.m185w("Media topic not in error state: %s", mediaTopicState);
                    }
                }
            }
        }

        void showErrorDialog(String message) {
            FragmentTransaction fragmentTransaction = MediaTopicStatusFragment.this.hideDialogs();
            AlertFragmentDialog dialog = AlertFragmentDialog.newInstance(null, message, 3);
            dialog.setTargetFragment(MediaTopicStatusFragment.this, 3);
            dialog.show(fragmentTransaction, "error");
        }

        void showPrivacyRestrictionDialog(List<String> restrictedUids) {
            FragmentTransaction fragmentTransaction = MediaTopicStatusFragment.this.hideDialogs();
            DialogFragment dialog = MediaTopicPrivacyRestrictionDialogFragment.newInstance(MediaTopicStatusFragment.this.getActivity(), restrictedUids, 4);
            dialog.setTargetFragment(MediaTopicStatusFragment.this, 4);
            dialog.show(fragmentTransaction, "confirm_privacy_settings");
        }

        void showWindowProgress(MediaTopicPostState mediaTopicState) {
            int progress = 0;
            MediaTopicPostPhase phase;
            if (mediaTopicState.hasPhotos()) {
                phase = mediaTopicState.getPhase();
                if (phase == MediaTopicPostPhase.UPLOADING_IMAGES) {
                    UploadImagesState uploadState = mediaTopicState.getUploadImagesState();
                    UploadImagesPhase uploadPhase = uploadState.getPhase();
                    if (uploadPhase == UploadImagesPhase.PREPARE) {
                        progress = (uploadState.getPreparedPhotos() * 2000) / uploadState.getPhotoCount();
                    } else if (uploadPhase == UploadImagesPhase.UPLOAD) {
                        progress = ((int) ((6000 * uploadState.getUploadedSize()) / uploadState.getTotalUploadSize())) + 2000;
                    }
                } else if (phase == MediaTopicPostPhase.UPLOADING_MEDIA_TOPIC) {
                    progress = 8000;
                } else if (phase == MediaTopicPostPhase.COMPLETED) {
                    progress = 10000;
                }
            } else {
                phase = mediaTopicState.getPhase();
                if (phase == MediaTopicPostPhase.UPLOADING_MEDIA_TOPIC) {
                    progress = 50;
                } else if (phase == MediaTopicPostPhase.COMPLETED) {
                    progress = 100;
                }
            }
            MediaTopicStatusFragment.this.notifyOnProgress(progress, 10000);
        }
    }

    public MediaTopicStatusFragment() {
        this.infoLayerType = InfoLayerType.NONE;
        this.doCancel = false;
        this.wasPausedForCancel = false;
        this.errorIsAcknowledged = false;
        this.uiHandler = new UIHandler();
    }

    static {
        allDialogFragmentTags = new String[]{"confirm_cancel_edit", "confirm_cancel_upload", "confirm_privacy_settings", "error"};
    }

    public static MediaTopicStatusFragment newInstance(PostMediaTopicTask mediaTopicTask, boolean doCancel, Bundle extras) {
        MediaTopicStatusFragment fragment = new MediaTopicStatusFragment();
        fragment.setArguments(createArgs(mediaTopicTask, doCancel, extras));
        return fragment;
    }

    protected static Bundle createArgs(PostMediaTopicTask mediaTopicTask, boolean doCancel, Bundle extras) {
        MediaComposerData mediaComposerData;
        MediaTopicType mediaTopicType = mediaTopicTask.getMediaTopicType();
        if (mediaTopicType == MediaTopicType.USER) {
            mediaComposerData = MediaComposerData.user(mediaTopicTask.getMediaTopicMessage(), mediaTopicTask.isSetToStatus());
        } else if (mediaTopicType == MediaTopicType.GROUP_THEME) {
            mediaComposerData = MediaComposerData.group(mediaTopicTask.getGroupId(), mediaTopicTask.getMediaTopicMessage());
        } else if (mediaTopicType == MediaTopicType.GROUP_SUGGESTED) {
            mediaComposerData = MediaComposerData.groupSuggested(mediaTopicTask.getGroupId(), mediaTopicTask.getMediaTopicMessage());
        } else {
            mediaComposerData = null;
        }
        Bundle args = MediaComposerFragment.createArgs(mediaComposerData, extras);
        args.putParcelable("media_topic_post", mediaTopicTask);
        args.putBoolean("cancel", doCancel);
        return args;
    }

    public void setListener(MediaTopicStatusFragmentListener listener) {
        super.setListener(listener);
        this.listener = listener;
    }

    protected void notifyOnProgress(int progress, int maxProgress) {
        if (this.listener != null) {
            this.listener.onProgressChanged(progress, maxProgress);
        }
    }

    protected void notifyOnCancelledUpload() {
        if (this.listener != null) {
            this.listener.onCancelledUpload();
        }
    }

    protected void notifyOnClose() {
        if (this.listener != null) {
            this.listener.onClose();
        }
    }

    public PostMediaTopicTask getTask() {
        return this.mediaTopicTask;
    }

    public void cancelEdit() {
        Logger.m172d("");
        if (MediaTopicMessage.equal(this.mediaTopicTask.getMediaTopicMessage(), this.mediaComposerController.getMediaTopicMessage())) {
            onConfirmCancelEditResult(true, false);
            return;
        }
        FragmentTransaction transaction = hideDialogs();
        ConfirmationDialog dialog = ConfirmationDialog.newInstance(0, 2131166108, 2131166310, 2131165476, 2);
        dialog.setTargetFragment(this, 2);
        dialog.show(transaction, "confirm_cancel_edit");
    }

    public void setDoCancel(boolean doCancel) {
        this.doCancel = doCancel;
    }

    public void onCreate(Bundle savedInstanceState) {
        this.localizationManager = LocalizationManager.from(getActivity());
        Bundle args = getArguments();
        boolean doCancel = args == null ? false : args.getBoolean("cancel");
        if (savedInstanceState != null) {
            this.mediaTopicTask = (PostMediaTopicTask) savedInstanceState.getParcelable("media_topic_post");
            this.errorIsAcknowledged = savedInstanceState.getBoolean("error_is_acknowledged");
        } else {
            this.mediaTopicTask = args == null ? null : (PostMediaTopicTask) args.getParcelable("media_topic_post");
        }
        setDoCancel(doCancel);
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getFragmentManager();
        this.persistentServiceFragment = (PersistentServiceFragment) fragmentManager.findFragmentByTag("persistent_service");
        if (this.persistentServiceFragment == null) {
            this.persistentServiceFragment = PersistentServiceFragment.newInstance(this.mediaTopicTask.getId());
            this.persistentServiceFragment.setTargetFragment(this, 0);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(this.persistentServiceFragment, "persistent_service");
            transaction.commit();
        }
        if (savedInstanceState == null) {
            setMode(2);
        }
        setHasOptionsMenu(true);
    }

    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            if (this.mode != 1) {
                this.uiHandler.showTask(activity, this.mediaTopicTask, true);
            }
            if (this.doCancel) {
                startCancel(activity);
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("media_topic_post", this.mediaTopicTask);
        outState.putBoolean("cancel", this.doCancel);
        outState.putBoolean("error_is_acknowledged", this.errorIsAcknowledged);
    }

    protected void updateMode() {
        super.updateMode();
        if (this.fadingContainer == null) {
            return;
        }
        if (this.mode == 1) {
            Logger.m172d("Hide fading container");
            this.fadingContainer.setVisibility(8);
        } else if (this.mode == 2) {
            Logger.m172d("Show fading container");
            this.fadingContainer.setVisibility(0);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(getActivity());
        frameLayout.setLayoutParams(new LayoutParams(-1, -1));
        frameLayout.addView(super.onCreateView(inflater, container, savedInstanceState));
        inflater.inflate(2130903182, frameLayout, true);
        this.fadingContainer = (ViewGroup) frameLayout.findViewById(2131624809);
        return frameLayout;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(2131689503, menu);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        boolean isPaused;
        boolean isError;
        boolean isDone;
        boolean isEditMode;
        boolean z;
        boolean z2 = false;
        super.onPrepareOptionsMenu(menu);
        MediaTopicPostState state = this.mediaTopicTask.getMediaTopicState();
        Logger.m173d("state=%s", state);
        PersistentTaskState execState = state.getExecutionState();
        if (execState == PersistentTaskState.PAUSED) {
            isPaused = true;
        } else {
            isPaused = false;
        }
        boolean isPausing = state.isPausing();
        if (execState == PersistentTaskState.ERROR || execState == PersistentTaskState.FAILED) {
            isError = true;
        } else {
            isError = false;
        }
        if (execState == PersistentTaskState.COMPLETED) {
            isDone = true;
        } else {
            isDone = false;
        }
        if (getMode() == 1) {
            isEditMode = true;
        } else {
            isEditMode = false;
        }
        Logger.m173d("isPaused=%s isPausing=%s isError=%s isDone=%s isEditMode=%s", Boolean.valueOf(isPaused), Boolean.valueOf(isPausing), Boolean.valueOf(isError), Boolean.valueOf(isDone), Boolean.valueOf(isEditMode));
        MenuItem item = menu.findItem(2131625479);
        if (isEditMode || isDone) {
            z = false;
        } else {
            z = true;
        }
        item.setVisible(z);
        if (isPausing) {
            z = false;
        } else {
            z = true;
        }
        item.setEnabled(z);
        item = menu.findItem(2131625480);
        if (isPaused || isError || isEditMode || isDone) {
            z = false;
        } else {
            z = true;
        }
        item.setVisible(z);
        if (isPausing) {
            z = false;
        } else {
            z = true;
        }
        item.setEnabled(z);
        item = menu.findItem(2131625481);
        if (!isPaused || isEditMode) {
            z = false;
        } else {
            z = true;
        }
        item.setVisible(z);
        if (isPausing) {
            z = false;
        } else {
            z = true;
        }
        item.setEnabled(z);
        item = menu.findItem(2131625482);
        if (!isError || isEditMode) {
            z = false;
        } else {
            z = true;
        }
        item.setVisible(z);
        if (isPausing) {
            z = false;
        } else {
            z = true;
        }
        item.setEnabled(z);
        MenuItem findItem = menu.findItem(2131625483);
        if (!((!isPaused && !isError) || isEditMode || isDone)) {
            z2 = true;
        }
        findItem.setVisible(z2);
        menu.findItem(2131625484).setVisible(isDone);
        item = menu.findItem(2131625475);
        if (item != null) {
            item.setVisible(isEditMode);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Logger.m184w("activity is null");
            return false;
        }
        switch (item.getItemId()) {
            case 2131625479:
                startCancel(activity);
                return true;
            case 2131625480:
                startPause(activity);
                return true;
            case 2131625481:
            case 2131625482:
                startResume(activity);
                return true;
            case 2131625483:
                startEdit(activity);
                MediaComposerStats.open(this.mediaTopicTask.getState() == PersistentTaskState.PAUSED ? "mt_pause_edit" : "mt_error_edit", this.mediaTopicTask.getMediaTopicType());
                return true;
            case 2131625484:
                notifyOnClose();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void resetExtraErrorIsAcknowledged() {
        this.errorIsAcknowledged = false;
    }

    private void startPause(Activity activity) {
        Logger.m172d("");
        activity.startService(PersistentTaskService.createPauseTaskIntent(activity, this.mediaTopicTask));
    }

    private void startResume(Activity activity) {
        Logger.m172d("");
        activity.startService(PersistentTaskService.createResumeTaskIntent(activity, this.mediaTopicTask));
    }

    private void startCancel(Activity activity) {
        Logger.m172d("");
        if (this.mediaTopicTask.getMediaTopicState().getExecutionState() == PersistentTaskState.EXECUTING && !this.mediaTopicTask.isPausing()) {
            this.wasPausedForCancel = true;
            activity.startService(PersistentTaskService.createPauseTaskIntent(activity, this.mediaTopicTask));
        }
        cancelUpload();
    }

    private void cancelUpload() {
        Logger.m172d("");
        int messageResId = this.mediaTopicType == MediaTopicType.USER ? 2131166113 : 2131166110;
        FragmentTransaction transaction = hideDialogs();
        ConfirmationDialog dialog = ConfirmationDialog.newInstance(2131166112, messageResId, 2131166111, 2131166109, 1);
        dialog.setTargetFragment(this, 1);
        dialog.show(transaction, "confirm_cancel_upload");
    }

    private void startEdit(FragmentActivity activity) {
        Logger.m172d("");
        setMode(1);
        Logger.m172d("calling invalidateOptionsMenu");
        activity.supportInvalidateOptionsMenu();
    }

    public void showIndeterminateProgress(Activity activity, boolean progressVisible, String text) {
        int i;
        int i2 = 0;
        String str = "progressVisible=%d text=%s";
        Object[] objArr = new Object[2];
        if (progressVisible) {
            i = 1;
        } else {
            i = 0;
        }
        objArr[0] = Integer.valueOf(i);
        objArr[1] = text;
        Logger.m173d(str, objArr);
        if (this.infoLayerType != InfoLayerType.PROGRESS_INDETERMINATE) {
            this.fadingContainer.removeAllViews();
            LayoutInflater.from(activity).inflate(2130903245, this.fadingContainer, true);
            ((ProgressBar) this.fadingContainer.findViewById(2131624548)).setIndeterminate(true);
        }
        this.infoLayerType = InfoLayerType.PROGRESS_INDETERMINATE;
        ProgressBar progressBar = (ProgressBar) this.fadingContainer.findViewById(2131624548);
        TextView textView = (TextView) this.fadingContainer.findViewById(C0263R.id.text);
        if (!progressVisible) {
            i2 = 8;
        }
        progressBar.setVisibility(i2);
        textView.setText(text);
    }

    public void hideInfoLayer() {
        if (this.fadingContainer != null) {
            this.fadingContainer.removeAllViews();
        }
        this.infoLayerType = InfoLayerType.NONE;
    }

    protected void notifyMediaComposerCompleted(MediaComposerData data) {
        Logger.m172d("");
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Logger.m184w("activity is null");
            return;
        }
        this.persistentServiceFragment.run(new C08001(data));
        setMode(2);
        Logger.m172d("calling invalidateOptionsMenu");
        activity.supportInvalidateOptionsMenu();
        super.notifyMediaComposerCompleted(data);
    }

    protected FragmentTransaction hideDialogs() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = super.hideDialogs();
        for (String fragmentTag : allDialogFragmentTags) {
            Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
            if (fragment != null) {
                fragmentTransaction.remove(fragment);
            }
        }
        return fragmentTransaction;
    }

    public void onConfirmCancelUploadResult(boolean isPositive) {
        Logger.m173d("isPositive=%s", Boolean.valueOf(isPositive));
        Context activity = getActivity();
        if (activity == null) {
            Logger.m184w("activity is null");
        } else if (isPositive) {
            activity.startService(PersistentTaskService.createCancelTaskIntent(activity, this.mediaTopicTask));
            notifyOnCancelledUpload();
            reportStatEventCancel();
        } else if (this.wasPausedForCancel) {
            activity.startService(PersistentTaskService.createResumeTaskIntent(activity, this.mediaTopicTask));
            this.wasPausedForCancel = false;
        }
    }

    private void reportStatEventCancel() {
        PostMediaTopicTask mediaTopicTask = this.mediaTopicTask;
        if (mediaTopicTask != null) {
            Activity activity = getActivity();
            if (activity instanceof MediaTopicStatusActivity) {
                Intent intent = activity.getIntent();
                if (intent != null) {
                    MediaTopicPostState state = (MediaTopicPostState) intent.getParcelableExtra("upload_state");
                    if (state != null) {
                        long delayMs = System.currentTimeMillis() - mediaTopicTask.getCreatedTime();
                        MediaTopicMessage mediaTopicMessage = mediaTopicTask.getMediaTopicMessage();
                        MediaComposerStats.cancel(mediaTopicTask.getFailureCount(), state, delayMs, mediaTopicMessage == null ? 0 : mediaTopicMessage.getStats().photoCount);
                    }
                }
            }
        }
    }

    private void onConfirmCancelEditResult(boolean isPositive, boolean hasChanges) {
        Logger.m173d("isPositive=%s", Boolean.valueOf(isPositive));
        Activity activity = getActivity();
        if (activity == null) {
            Logger.m184w("activity is null");
        } else if (isPositive) {
            setMode(2);
            if (hasChanges) {
                replaceMediaTopicMessage(this.mediaTopicTask.getMediaTopicMessage());
            }
            Logger.m172d("calling invalidateOptionsMenu");
            ((FragmentActivity) activity).supportInvalidateOptionsMenu();
        }
    }

    private void onConfirmedPrivacyRestriction(List<String> restrictedUids) {
        Logger.m173d("restrictedUids=%s", restrictedUids);
        ArrayList<String> checkedFriends = this.mediaComposerController.getWithFriendsUids();
        for (String uid : restrictedUids) {
            checkedFriends.remove(uid);
        }
        this.mediaComposerController.setWithFriends(checkedFriends);
        updateWithFriendsCounter();
        complete();
    }

    public void onConfirmationDialogResult(boolean isPositive, int requestCode) {
        switch (requestCode) {
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                onConfirmCancelUploadResult(isPositive);
            case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                onConfirmCancelEditResult(isPositive, true);
            case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                if (isPositive) {
                    MediaTopicPrivacyRestrictionDialogFragment fragment = (MediaTopicPrivacyRestrictionDialogFragment) getFragmentManager().findFragmentByTag("confirm_privacy_settings");
                    if (fragment != null) {
                        onConfirmedPrivacyRestriction(fragment.getRestrictedUids());
                    }
                }
            default:
        }
    }

    public void onConfirmationDialogDismissed(int requestCode) {
        Logger.m173d("requestCode=%d", Integer.valueOf(requestCode));
    }

    public void onAlertDismiss(int requestCode) {
        if (requestCode == 3) {
            this.errorIsAcknowledged = true;
        }
    }
}
