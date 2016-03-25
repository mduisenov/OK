package ru.ok.android.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import ru.ok.android.C0206R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskObserver;
import ru.ok.android.services.persistent.PersistentTaskService;
import ru.ok.android.services.persistent.PersistentTaskService.LocalBinder;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.processors.video.MediaInfo;
import ru.ok.android.services.processors.video.VideoGroupUploadTask;
import ru.ok.android.services.processors.video.VideoUploadException;
import ru.ok.android.services.processors.video.VideoUploadTask;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.custom.PaddingBorderedBitmapView;
import ru.ok.android.ui.dialogs.ConfirmationDialog;
import ru.ok.android.ui.dialogs.ConfirmationDialog.Builder;
import ru.ok.android.ui.dialogs.ConfirmationDialog.OnConfirmationDialogListener;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Storage.External;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.localization.base.LocalizedActivity;
import ru.ok.java.api.request.video.VideoDeleteRequest;

public class VideoUploadStatusActivity extends LocalizedActivity implements OnConfirmationDialogListener {
    private TextView acceptTermsText;
    private MediaInfo displayedMediaInfo;
    private View editLayout;
    private TextView errorButton;
    private RadioButton friendsOnlyRadio;
    private boolean paused;
    LinearLayout privacyBox;
    private View removeBtn;
    private View rootContainer;
    private TextView saveButton;
    private ServiceConnection serviceConn;
    private VideoUploadTask task;
    private int thumbImageHeight;
    private int thumbImageWidth;
    private PaddingBorderedBitmapView thumbnailImage;
    private EditText titleEdit;

    /* renamed from: ru.ok.android.ui.activity.VideoUploadStatusActivity.1 */
    class C05641 implements OnGlobalLayoutListener {
        C05641() {
        }

        public void onGlobalLayout() {
            VideoUploadStatusActivity.this.repositionRemoveButton();
            VideoUploadStatusActivity.this.adjustLayouts();
        }
    }

    /* renamed from: ru.ok.android.ui.activity.VideoUploadStatusActivity.2 */
    class C05652 implements OnClickListener {
        C05652() {
        }

        public void onClick(View v) {
            VideoUploadStatusActivity.this.removeMovie();
        }
    }

    /* renamed from: ru.ok.android.ui.activity.VideoUploadStatusActivity.3 */
    class C05663 implements OnClickListener {
        C05663() {
        }

        public void onClick(View v) {
            if (!VideoUploadStatusActivity.this.updateMovie(false, true, false)) {
                VideoUploadStatusActivity.this.finish();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.activity.VideoUploadStatusActivity.4 */
    class C05674 implements OnClickListener {
        C05674() {
        }

        public void onClick(View v) {
            Object tag = VideoUploadStatusActivity.this.errorButton.getTag();
            VideoUploadStatusActivity.this.processErrorClick(tag instanceof VideoUploadException ? (VideoUploadException) tag : null);
        }
    }

    /* renamed from: ru.ok.android.ui.activity.VideoUploadStatusActivity.5 */
    class C05705 implements ServiceConnection {

        /* renamed from: ru.ok.android.ui.activity.VideoUploadStatusActivity.5.1 */
        class C05691 extends PersistentTaskObserver {

            /* renamed from: ru.ok.android.ui.activity.VideoUploadStatusActivity.5.1.1 */
            class C05681 implements Runnable {
                final /* synthetic */ PersistentTask val$persistentTask;

                C05681(PersistentTask persistentTask) {
                    this.val$persistentTask = persistentTask;
                }

                public void run() {
                    VideoUploadStatusActivity.this.notifyTaskUpdated(this.val$persistentTask);
                }
            }

            C05691(int x0) {
                super(x0);
            }

            protected void onTaskUpdated(PersistentTask persistentTask) {
                VideoUploadStatusActivity.this.rootContainer.post(new C05681(persistentTask));
            }
        }

        C05705() {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            int taskId = VideoUploadStatusActivity.this.task.getId();
            if (taskId == 0) {
                taskId = binder.submit(VideoUploadStatusActivity.this.task);
            }
            VideoUploadStatusActivity.this.notifyTaskUpdated(binder.getTask(taskId));
            binder.registerObserver(new C05691(taskId));
        }

        public void onServiceDisconnected(ComponentName name) {
        }
    }

    /* renamed from: ru.ok.android.ui.activity.VideoUploadStatusActivity.6 */
    class C05716 extends AsyncTask<Long, Void, Void> {
        C05716() {
        }

        protected Void doInBackground(Long... params) {
            try {
                JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new VideoDeleteRequest(params[0]));
            } catch (Exception ex) {
                Logger.m172d("Failed to delete movie: " + ex.getMessage());
            }
            return null;
        }
    }

    class MovieInfo {
        private boolean isPrivate;
        private String title;
        private long videoId;

        public MovieInfo(String title, boolean isPrivate, long videoId) {
            this.title = title;
            this.isPrivate = isPrivate;
            this.videoId = videoId;
        }

        private String getTitle() {
            return this.title;
        }

        private boolean isPrivate() {
            return this.isPrivate;
        }
    }

    public VideoUploadStatusActivity() {
        this.thumbImageWidth = 0;
        this.thumbImageHeight = 0;
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(3);
        if (savedInstanceState != null) {
            this.task = savedInstanceState.getParcelable("video_upload_task");
        } else {
            this.task = getIntent().getParcelableExtra("video_upload_task");
        }
        if (this.task == null) {
            finish();
            return;
        }
        setContentView(2130903572);
        this.rootContainer = findViewById(C0263R.id.container);
        this.titleEdit = (EditText) findViewById(2131625428);
        if (TextUtils.isEmpty(this.titleEdit.getText().toString())) {
            this.titleEdit.setText(this.task.getTitle());
        }
        this.friendsOnlyRadio = (RadioButton) findViewById(2131625432);
        this.privacyBox = (LinearLayout) findViewById(2131625429);
        this.acceptTermsText = (TextView) findViewById(2131625433);
        this.acceptTermsText.setText(Html.fromHtml(getStringLocalized(2131165307)));
        this.acceptTermsText.setMovementMethod(LinkMovementMethod.getInstance());
        this.saveButton = (TextView) findViewById(2131624834);
        this.errorButton = (TextView) findViewById(2131624551);
        this.thumbnailImage = (PaddingBorderedBitmapView) findViewById(2131625426);
        this.removeBtn = findViewById(2131624939);
        this.rootContainer.getViewTreeObserver().addOnGlobalLayoutListener(new C05641());
        this.editLayout = findViewById(2131625427);
        bindTaskService();
        this.removeBtn.setOnClickListener(new C05652());
        this.saveButton.setOnClickListener(new C05663());
        this.errorButton.setOnClickListener(new C05674());
        this.titleEdit.setImeOptions(6);
        if (this.task.isVideoMail()) {
            getSupportActionBar().setTitle(getStringLocalized(2131166856));
        } else {
            getSupportActionBar().setTitle(getStringLocalized(2131166854));
        }
        if (savedInstanceState == null && getIntent().getBooleanExtra("cancel", false)) {
            removeMovie();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    private void adjustLayouts() {
        if (this.privacyBox.getOrientation() == LinearLayout.HORIZONTAL) {
            Layout l = this.friendsOnlyRadio.getLayout();
            if (l != null) {
                int lines = l.getLineCount();
                if (lines > 0 && l.getEllipsisCount(lines - 1) > 0) {
                    this.privacyBox.setOrientation(LinearLayout.VERTICAL);
                    ((TextView) findViewById(2131625430)).setTextColor(getResources().getColor(2131493005));
                    this.rootContainer.requestLayout();
                }
            }
        }
        this.thumbImageWidth = this.thumbnailImage.getWidth();
        this.thumbImageHeight = this.thumbnailImage.getHeight();
        updateThumbnail();
    }

    protected void onPause() {
        super.onPause();
        updateMovie(true, false, false);
        this.paused = true;
    }

    protected void onResume() {
        super.onResume();
        setProgressBarIndeterminateVisibility(false);
        setProgressBarVisibility(false);
        this.paused = false;
        updateUI();
    }

    protected void onStop() {
        super.onStop();
        unbindTaskService();
    }

    private void bindTaskService() {
        if (this.serviceConn == null) {
            this.serviceConn = new C05705();
            bindService(new Intent(this, PersistentTaskService.class), this.serviceConn, 1);
        }
    }

    private void processErrorClick(VideoUploadException error) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment alreadyShowingDialog = fragmentManager.findFragmentByTag("dialog");
        Fragment errorDialog = new Builder().withTitle(2131166835).withMessage(getLongErrorMessage(this, error)).withPositiveText(2131166470).withNegativeText(2131165303).withRequestCode(2).withCancelable(true).build();
        if (alreadyShowingDialog != null) {
            transaction.remove(alreadyShowingDialog);
        }
        transaction.add(errorDialog, "dialog");
        transaction.commit();
    }

    private void unbindTaskService() {
        if (this.serviceConn != null) {
            unbindService(this.serviceConn);
            this.serviceConn = null;
        }
    }

    private void removeMovie() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment alreadyShowingDialog = fragmentManager.findFragmentByTag("dialog");
        Fragment confirmationDialog = ConfirmationDialog.newInstance(2131165480, 2131165481, 2131166881, 2131166257, 1);
        if (alreadyShowingDialog != null) {
            transaction.remove(alreadyShowingDialog);
        }
        transaction.add(confirmationDialog, "dialog");
        transaction.commit();
    }

    public void onConfirmationDialogResult(boolean isPositive, int requestCode) {
        Logger.m173d("isPositive=%s requestCode=%d", isPositive, requestCode);
        if (requestCode == 1) {
            if (isPositive) {
                doRemoveMovie();
            }
        } else if (requestCode != 2) {
        } else {
            if (isPositive) {
                updateMovie(false, true, true);
                Intent retry = new Intent("ru.ok.android.action.RESUME_TASK");
                retry.setClass(this, PersistentTaskService.class);
                retry.putExtra("task_id", this.task.getId());
                startService(retry);
                finish();
                return;
            }
            doRemoveMovie();
        }
    }

    public void onConfirmationDialogDismissed(int requestCode) {
        Logger.m173d("requestCode=%d", requestCode);
    }

    private void doRemoveMovie() {
        Logger.m172d(">>>");
        startService(PersistentTaskService.createCancelTaskIntent(this, this.task));
        if (!(this.task == null || this.task.getId() == 0 || this.task.isVideoMail())) {
            new C05716().execute(this.task.getVideoId());
        }
        Logger.m172d("<<< finishing activity");
        finish();
    }

    private boolean updateMovie(boolean isPausing, boolean fromUserInput, boolean retry) {
        if (this.task.isVideoMail()) {
            finish();
            return false;
        } else if (isPausing || retry || !TextUtils.isEmpty(this.titleEdit.getText())) {
            updateUploadTaskWithTitleAndPrivacy(new MovieInfo(this.titleEdit.getText().toString(), this.friendsOnlyRadio.isChecked(), this.task.getVideoId()), fromUserInput, retry);
            return false;
        } else {
            new AlertDialogWrapper.Builder(this).setTitle(LocalizationManager.getString(this, 2131165737)).setMessage(LocalizationManager.getString(this, 2131166831)).setPositiveButton(LocalizationManager.getString(this, 2131165595), null).show();
            return true;
        }
    }

    private void updateUploadTaskWithTitleAndPrivacy(MovieInfo movieInfo, boolean fromUserInput, boolean retry) {
        Bundle params = new Bundle();
        params.putString("task_param_title", movieInfo.getTitle());
        params.putBoolean("task_param_privacy", movieInfo.isPrivate());
        params.putBoolean("task_param_retry", retry);
        startService(PersistentTaskService.createSendParamsIntent(this, this.task, params));
        if (!fromUserInput) {
            updateUI();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("video_upload_task", this.task);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.task = intent.getParcelableExtra("video_upload_task");
        if (this.task == null) {
            finish();
            return;
        }
        notifyTaskUpdated(this.task);
        if (intent.getBooleanExtra("cancel", false)) {
            removeMovie();
        }
    }

    private void notifyTaskUpdated(PersistentTask persistentTask) {
        if (persistentTask != null) {
            this.task = (VideoUploadTask) persistentTask;
            updateUI();
        }
    }

    private void setItemVisibility(View item, boolean visible) {
        item.setVisibility(visible ? 0 : 8);
    }

    private void updateUI() {
        boolean z = true;
        if (!this.paused) {
            boolean errorState;
            boolean complete;
            boolean z2;
            PersistentTaskState state = this.task.getState();
            errorState = state == PersistentTaskState.FAILED || state == PersistentTaskState.ERROR || state == PersistentTaskState.WAIT_INTERNET || state == PersistentTaskState.WAIT_EXTERNAL_STORAGE;
            complete = state == PersistentTaskState.COMPLETED;
            View view = this.saveButton;
            z2 = !errorState;
            setItemVisibility(view, z2);
            VideoUploadException error = this.task.getError(VideoUploadException.class);
            setItemVisibility(this.errorButton, errorState);
            if (errorState) {
                TextView textView = this.errorButton;
                int i = (error == null || error.getErrorCode() != 1) ? 2131166835 : 2131165812;
                textView.setText(getStringLocalized(i));
            }
            TextView textView2 = this.errorButton;
            if (!errorState) {
                error = null;
            }
            textView2.setTag(error);
            view = this.privacyBox;
            z2 = !(this.task instanceof VideoGroupUploadTask);
            setItemVisibility(view, z2);
            view = this.editLayout;
            z2 = !this.task.isVideoMail();
            setItemVisibility(view, z2);
            view = this.acceptTermsText;
            z2 = !this.task.isVideoMail();
            setItemVisibility(view, z2);
            if (this.task.isVideoMail()) {
                if (complete) {
                    this.saveButton.setText(getStringLocalized(2131166862));
                    View view2 = this.removeBtn;
                    if (complete) {
                        z = false;
                    }
                    setItemVisibility(view2, z);
                } else {
                    this.saveButton.setText(getStringLocalized(2131166863));
                }
            }
            updateThumbnail();
        }
    }

    private void updateThumbnail() {
        boolean z = true;
        if (this.thumbImageWidth != 0 && this.thumbImageHeight != 0) {
            MediaInfo mediaInfo = this.task.getMediaInfo();
            if (!(mediaInfo == null || mediaInfo.equals(this.displayedMediaInfo))) {
                boolean z2;
                Bitmap thumb = null;
                try {
                    thumb = mediaInfo.getThumbnail(getContentResolver(), this.thumbImageWidth, this.thumbImageHeight);
                } catch (Exception ex) {
                    Logger.m184w("Failed to load thumbnail: " + ex.getMessage());
                }
                if (thumb == null) {
                    thumb = ((BitmapDrawable) getResources().getDrawable(2130838292)).getBitmap();
                }
                this.thumbnailImage.setImageBitmap(thumb);
                View view = this.thumbnailImage;
                z2 = thumb != null;
                setItemVisibility(view, z2);
                View view2 = this.removeBtn;
                if (thumb == null) {
                    z = false;
                }
                setItemVisibility(view2, z);
                this.displayedMediaInfo = mediaInfo;
            }
            repositionRemoveButton();
        }
    }

    public final void repositionRemoveButton() {
        int leftMargin;
        int topMargin;
        Rect rect = this.thumbnailImage.updateBitmapMetrics();
        if (rect.right == 0 || rect.bottom == 0) {
            leftMargin = this.removeBtn.getRight() - (this.removeBtn.getMeasuredWidth() / 2);
            topMargin = this.removeBtn.getTop() - (this.removeBtn.getMeasuredHeight() / 2);
        } else {
            leftMargin = (this.thumbnailImage.getLeft() + rect.right) - (this.removeBtn.getMeasuredWidth() / 2);
            topMargin = (this.thumbnailImage.getTop() + rect.top) - (this.removeBtn.getMeasuredHeight() / 2);
        }
        LayoutParams layoutParams = (LayoutParams) this.removeBtn.getLayoutParams();
        if (leftMargin != layoutParams.leftMargin || topMargin != layoutParams.topMargin) {
            layoutParams.leftMargin = leftMargin;
            layoutParams.topMargin = topMargin;
            this.removeBtn.setLayoutParams(layoutParams);
        }
    }

    private static String getLongErrorMessage(Context context, VideoUploadException error) {
        LocalizationManager localizationManager = LocalizationManager.from(context);
        if (error == null) {
            return localizationManager.getString(2131166846);
        }
        switch (error.getErrorCode()) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return localizationManager.getString(2131166842);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                int serverErrorCode = error.getServerErrorCode();
                String serverMessage = error.getServerErrorMessage();
                String msg = null;
                if (!TextUtils.isEmpty(serverMessage)) {
                    if (serverMessage.contains("FileToSmall")) {
                        msg = localizationManager.getString(2131166840);
                    } else if (serverMessage.contains("FileToLarge")) {
                        msg = localizationManager.getString(2131166839);
                    } else if (serverMessage.contains("BadFormat")) {
                        msg = localizationManager.getString(2131166836);
                    } else if (serverMessage.contains("DvdMenu")) {
                        msg = localizationManager.getString(2131166837);
                    } else if (serverMessage.contains("Text") && serverErrorCode == 454) {
                        msg = localizationManager.getString(2131166833);
                    }
                }
                if (msg == null) {
                    msg = localizationManager.getString(2131166844, Integer.valueOf(serverErrorCode), serverMessage);
                }
                return msg;
            case C0206R.styleable.Toolbar_navigationContentDescription /*21*/:
                if (External.externalMemoryAvailable()) {
                    return localizationManager.getString(2131166838);
                }
                return localizationManager.getString(2131166843);
            case C0206R.styleable.Toolbar_titleTextColor /*23*/:
                return localizationManager.getString(2131166841);
            case C0206R.styleable.Theme_actionMenuTextAppearance /*25*/:
                return localizationManager.getString(2131166845);
            case C0206R.styleable.Theme_actionMenuTextColor /*26*/:
                return localizationManager.getString(2131166848);
            default:
                if (error.getCause() == null) {
                    return localizationManager.getString(2131166846);
                }
                return localizationManager.getString(2131166847, error.getCause());
        }
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
