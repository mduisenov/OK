package ru.ok.android.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ShareCompat.IntentReader;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import java.io.File;
import java.util.ArrayList;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.onelog.AppLaunchLogHelper;
import ru.ok.android.services.processors.video.FileLocation;
import ru.ok.android.services.processors.video.MediaInfo;
import ru.ok.android.services.processors.video.MediaInfoTempFile;
import ru.ok.android.ui.custom.mediacomposer.LinkItem;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaTopicMessage;
import ru.ok.android.ui.fragments.SaveToFileFragment;
import ru.ok.android.ui.image.AddImagesActivity;
import ru.ok.android.ui.messaging.activity.SelectConversationForSendMediaActivity;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.MediaUploadUtils;
import ru.ok.android.utils.MimeTypes;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;

public final class ChooseImageUploadActivity extends StartMediaUploadActivity {
    private ArrayList<MediaInfo> inputMediaInfos;
    private ArrayList<MediaInfo> outputMediaInfos;
    private ChooseDialogItem selectedActionItem;
    private boolean temporaryMedia;

    public static class ChooseDialog extends DialogFragment {

        /* renamed from: ru.ok.android.ui.activity.ChooseImageUploadActivity.ChooseDialog.1 */
        class C05401 extends ArrayAdapter<ChooseDialogItem> {
            C05401(Context x0, int x1, ChooseDialogItem[] x2) {
                super(x0, x1, x2);
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(17367057, parent, false);
                }
                ChooseDialog.this.bindDialogItemView(getContext(), (ChooseDialogItem) getItem(position), convertView);
                return convertView;
            }
        }

        /* renamed from: ru.ok.android.ui.activity.ChooseImageUploadActivity.ChooseDialog.2 */
        class C05412 implements OnItemClickListener {
            final /* synthetic */ ArrayAdapter val$adapter;

            C05412(ArrayAdapter arrayAdapter) {
                this.val$adapter = arrayAdapter;
            }

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ((ChooseImageUploadActivity) ChooseDialog.this.getActivity()).performActionDialogItem((ChooseDialogItem) this.val$adapter.getItem(position));
                ChooseDialog.this.dismiss();
            }
        }

        private enum ChooseDialogItem {
            GALLERY(2131166552, 2130838208) {
                void performAction(ChooseImageUploadActivity activity) {
                    activity.openGallery();
                }
            },
            MEDIA_TOPIC(2131166554, 2130838204) {
                void performAction(ChooseImageUploadActivity activity) {
                    activity.openMediaTopic();
                }
            },
            MESSAGES(2131166553, 2130838206) {
                void performAction(ChooseImageUploadActivity activity) {
                    activity.openMessages();
                }
            };
            
            private final int iconResourceId;
            private final int nameResourceId;

            abstract void performAction(ChooseImageUploadActivity chooseImageUploadActivity);

            private ChooseDialogItem(int nameResourceId, int iconResourceId) {
                this.nameResourceId = nameResourceId;
                this.iconResourceId = iconResourceId;
            }
        }

        public static ChooseDialog newInstance() {
            Bundle args = new Bundle();
            ChooseDialog result = new ChooseDialog();
            result.setArguments(args);
            return result;
        }

        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            ArrayAdapter<ChooseDialogItem> adapter = new C05401(getActivity(), 17367057, ChooseDialogItem.values());
            MaterialDialog ret = new Builder(getActivity()).title(getActivity().getTitle()).adapter(adapter).build();
            ListView listView = ret.getListView();
            if (listView != null) {
                listView.setOnItemClickListener(new C05412(adapter));
            }
            return ret;
        }

        private void bindDialogItemView(Context context, ChooseDialogItem item, View convertView) {
            TextView textView = (TextView) convertView.findViewById(16908308);
            textView.setText(LocalizationManager.getString(context, item.nameResourceId));
            textView.setCompoundDrawablesWithIntrinsicBounds(item.iconResourceId, 0, 0, 0);
            textView.setCompoundDrawablePadding((int) Utils.dipToPixels(14.0f));
            textView.setTextAppearance(context, 2131296679);
            textView.setPadding((int) Utils.dipToPixels(24.0f), 0, 0, 0);
        }

        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(LocalizationManager.getString((Context) this, 2131166555));
        if (performShareLinkAction()) {
            finish();
            return;
        }
        if (AppLaunchLogHelper.isShareIntent(getIntent())) {
            AppLaunchLog.sharePhoto();
        }
        initState(savedInstanceState);
        performAction();
    }

    private boolean performShareLinkAction() {
        AppLaunchLog.shareLink();
        if (startLoginIfNeeded()) {
            return false;
        }
        Intent intent = getIntent();
        if (!MimeTypes.isTextPlain(intent.getType())) {
            return false;
        }
        Bundle extras = intent.getExtras();
        String textOrUrl = extras == null ? null : extras.getString("android.intent.extra.TEXT");
        if (TextUtils.isEmpty(textOrUrl)) {
            Toast.makeText(getApplicationContext(), 2131166550, 1).show();
            finish();
            return false;
        }
        MediaTopicMessage mediaTopic = new MediaTopicMessage();
        if (Patterns.WEB_URL.matcher(textOrUrl).matches()) {
            mediaTopic.add(new LinkItem(textOrUrl));
            mediaTopic.add(MediaItem.emptyText());
        } else {
            mediaTopic.add(MediaItem.text(textOrUrl));
        }
        Intent intentEdit = new Intent();
        intentEdit.setClassName(OdnoklassnikiApplication.getContext(), "ru.ok.android.ui.activity.MediaComposerUserActivity");
        intentEdit.putExtra("media_topic", mediaTopic);
        intentEdit.putExtra("to_status", false);
        startActivity(intentEdit);
        return true;
    }

    private void performAction() {
        if (this.selectedActionItem == null) {
            showChooseDialog();
        }
    }

    private void initState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            initSelectedActionItemFromSavedState(savedInstanceState);
        }
        this.inputMediaInfos = getMediaInfosFromShareIntent();
    }

    private void initSelectedActionItemFromSavedState(@NonNull Bundle savedInstanceState) {
        String selectionActionItemAsString = savedInstanceState.getString("selected-action-item");
        if (selectionActionItemAsString != null) {
            this.selectedActionItem = ChooseDialogItem.valueOf(selectionActionItemAsString);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.selectedActionItem != null) {
            outState.putString("selected-action-item", this.selectedActionItem.name());
        }
    }

    private void showChooseDialog() {
        ChooseDialog.newInstance().show(getSupportFragmentManager(), "choose-dialog");
    }

    private void openMessages() {
        startActivityWithIntent(SelectConversationForSendMediaActivity.class);
    }

    private void openMediaTopic() {
        startActivityWithIntent(ShareImageTopicActivity.class);
    }

    private void openGallery() {
        startActivityWithIntent(AddImagesActivity.class);
    }

    private void startActivityWithIntent(@NonNull Class<?> activityClass) {
        startActivity(new Intent(getIntent()).setClass(this, activityClass).putParcelableArrayListExtra("media_infos", this.outputMediaInfos).putExtra("temp", this.temporaryMedia));
    }

    public void onSaveToFileFinished(SaveToFileFragment fragment, boolean successful, Bundle additionalArgs) {
        if (successful) {
            this.outputMediaInfos = createMediaInfoFromFiles(fragment);
            this.temporaryMedia = true;
            MediaUploadUtils.hideDialogs(getSupportFragmentManager(), fragment);
            doStartMediaUpload();
            finish();
            return;
        }
        MediaUploadUtils.showAlert(this, null, 2131165991, 2131165990, 1);
    }

    @Nullable
    private ArrayList<MediaInfo> createMediaInfoFromFiles(@NonNull SaveToFileFragment saveToFileFragment) {
        File[] filesToUpload = saveToFileFragment.getDestFiles();
        if (filesToUpload == null || filesToUpload.length == 0) {
            return null;
        }
        ArrayList<MediaInfo> mediaInfos = new ArrayList(filesToUpload.length);
        for (File fileToUpload : filesToUpload) {
            mediaInfos.add(new MediaInfoTempFile(FileLocation.createFromExternalFile(fileToUpload), null, fileToUpload.getName(), fileToUpload.length()));
        }
        return mediaInfos;
    }

    protected boolean shouldCopyMediaForUpload(@NonNull Intent data) {
        if (this.inputMediaInfos == null || this.inputMediaInfos.size() == 0) {
            return false;
        }
        MediaInfo mediaInfo = (MediaInfo) this.inputMediaInfos.get(0);
        if (super.shouldCopyMediaForUpload(data) || !mediaInfo.isPersistent()) {
            return true;
        }
        return false;
    }

    protected void copyMediaForUpload() {
        MediaUploadUtils.startCopyFile(this, null, true, 2, MediaUploadUtils.createSaveToFileImagesFragment(this, this.inputMediaInfos, null), this);
    }

    protected void doStartMediaUpload() {
        this.selectedActionItem.performAction(this);
    }

    private void performActionDialogItem(@NonNull ChooseDialogItem actionItem) {
        this.selectedActionItem = actionItem;
        startMediaUpload(getIntent());
    }

    @Nullable
    private ArrayList<MediaInfo> getMediaInfosFromShareIntent() {
        IntentReader intentReader = IntentReader.from(this);
        int streamCount = intentReader.getStreamCount();
        if (streamCount == 0) {
            return null;
        }
        ArrayList<MediaInfo> mediaInfos = new ArrayList();
        for (int i = 0; i < streamCount; i++) {
            MediaInfo mediaInfo = MediaInfo.fromUri(this, intentReader.getStream(i), "");
            if (mediaInfo != null) {
                mediaInfos.add(i, mediaInfo);
            } else {
                Logger.m177e("Media info can not be created from uri (%s)", mediaUri);
            }
        }
        return mediaInfos;
    }
}
