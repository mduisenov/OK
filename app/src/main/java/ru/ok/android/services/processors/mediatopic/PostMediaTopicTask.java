package ru.ok.android.services.processors.mediatopic;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.fragments.web.hooks.ShortLinkException;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.persistent.BaseParentPersistentTask;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskContext;
import ru.ok.android.services.persistent.PersistentTaskNotificationBuilder;
import ru.ok.android.services.persistent.PersistentTaskService;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.persistent.PersistentTaskUtils;
import ru.ok.android.services.processors.friends.FriendsFilterProcessor;
import ru.ok.android.services.processors.image.upload.UploadImagesTask;
import ru.ok.android.services.processors.image.upload.UploadImagesTask.ImageState;
import ru.ok.android.services.processors.mediatopic.MediaTopicPostState.MediaTopicPostPhase;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.activity.main.ActivityExecutor.SoftInputType;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.ui.custom.mediacomposer.EditablePhotoItem;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerData;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaTopicMessage;
import ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment.Page;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper.FragmentLocation;
import ru.ok.android.utils.NotificationsUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.json.JsonFriendsFilterParser;
import ru.ok.java.api.request.friends.FriendsFilter;
import ru.ok.java.api.request.friends.FriendsFilterRequest;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type;
import ru.ok.model.Discussion;
import ru.ok.model.mediatopics.MediaItemType;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;

public class PostMediaTopicTask extends BaseParentPersistentTask implements MediaTopicErrorListener {
    public static final Creator<PostMediaTopicTask> CREATOR;
    private static final ThreadLocal<Pattern> exceededBlockLimitPattern;
    private static float kbPerSecFactor = 0.0f;
    private static final long serialVersionUID = 2;
    private final long createdTs;
    private final String groupId;
    private MediaTopicMessage mediaTopicMessage;
    private MediaTopicPostState mediaTopicState;
    private final MediaTopicType mediaTopicType;
    private boolean toStatus;
    private String topicId;
    private int uploadPhotosSubtaskId;

    /* renamed from: ru.ok.android.services.processors.mediatopic.PostMediaTopicTask.1 */
    static class C04701 implements Creator<PostMediaTopicTask> {
        C04701() {
        }

        public PostMediaTopicTask createFromParcel(Parcel source) {
            return new PostMediaTopicTask(source);
        }

        public PostMediaTopicTask[] newArray(int size) {
            return new PostMediaTopicTask[size];
        }
    }

    /* renamed from: ru.ok.android.services.processors.mediatopic.PostMediaTopicTask.2 */
    static /* synthetic */ class C04712 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState;
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$mediatopics$MediaItemType;

        static {
            $SwitchMap$ru$ok$model$mediatopics$MediaItemType = new int[MediaItemType.values().length];
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.TEXT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.MUSIC.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.POLL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState = new int[PersistentTaskState.values().length];
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.SUBMITTED.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.EXECUTING.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.WAIT_INTERNET.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.FAILED.ordinal()] = 4;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.ERROR.ordinal()] = 5;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.COMPLETED.ordinal()] = 6;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.PAUSED.ordinal()] = 7;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    private ru.ok.android.services.persistent.PersistentTaskState extractBlockLimitAndFail(ru.ok.android.services.persistent.PersistentTaskContext r12, ru.ok.android.services.processors.mediatopic.MediaTopicPostException r13) throws ru.ok.android.services.processors.mediatopic.MediaTopicPostException {
        /* JADX: method processing error */
/*
        Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.ssa.SSATransform.placePhi(SSATransform.java:82)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:50)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r11 = this;
        r7 = r13;
        r10 = r13.getServerErrorMessage();	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        if (r10 == 0) goto L_0x0077;	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
    L_0x0007:
        r1 = "Trying to parse the block limit from message: \"%s\"";	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r2 = 1;	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r3 = 0;	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r2[r3] = r10;	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        ru.ok.android.utils.Logger.m173d(r1, r2);	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r1 = getExceededBlockLimitPattern();	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r2 = r10.trim();	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r9 = r1.matcher(r2);	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r1 = r9.matches();	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        if (r1 == 0) goto L_0x0077;	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
    L_0x0025:
        r1 = 1;	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r8 = r9.group(r1);	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r5 = java.lang.Integer.parseInt(r8);	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r1 = "Parsed block limit: %d";	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r2 = 1;	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r3 = 0;	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r4 = java.lang.Integer.valueOf(r5);	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        ru.ok.android.utils.Logger.m173d(r1, r2);	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r0 = new ru.ok.android.services.processors.mediatopic.MediaTopicPostException;	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r1 = r13.getErrorCode();	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r2 = r13.getMessage();	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r3 = r13.getCause();	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r4 = r13.getPrivacyRestrictionUids();	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r0.<init>(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
    L_0x0053:
        r1 = r11.mediaTopicState;
        r1.failed(r0);
    L_0x0058:
        throw r0;
    L_0x0059:
        r6 = move-exception;
        r1 = "Failed to parse block limit: %s";	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r2 = 1;	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r3 = 0;	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r2[r3] = r6;	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        ru.ok.android.utils.Logger.m177e(r1, r2);	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        ru.ok.android.utils.Logger.m178e(r6);	 Catch:{ Throwable -> 0x0059, all -> 0x0070 }
        r1 = r11.mediaTopicState;
        r1.failed(r7);
        r0 = r7;
        goto L_0x0058;
    L_0x0070:
        r1 = move-exception;
        r2 = r11.mediaTopicState;
        r2.failed(r7);
        throw r1;
    L_0x0077:
        r0 = r7;
        goto L_0x0053;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.processors.mediatopic.PostMediaTopicTask.extractBlockLimitAndFail(ru.ok.android.services.persistent.PersistentTaskContext, ru.ok.android.services.processors.mediatopic.MediaTopicPostException):ru.ok.android.services.persistent.PersistentTaskState");
    }

    public PostMediaTopicTask(String uid, MediaComposerData mediaComposerData) throws MediaTopicPostException {
        boolean hasPhotos = false;
        super(uid, false, 0);
        if (mediaComposerData == null) {
            throw new MediaTopicPostException(10, "null media topic");
        } else if (mediaComposerData.isValid()) {
            this.mediaTopicMessage = mediaComposerData.mediaTopicMessage;
            this.toStatus = mediaComposerData.toStatus;
            this.mediaTopicType = mediaComposerData.mediaTopicType == null ? MediaTopicType.USER : mediaComposerData.mediaTopicType;
            this.groupId = mediaComposerData.groupId;
            List<EditablePhotoItem> photos = this.mediaTopicMessage.getPhotos();
            if (photos != null && photos.size() > 0) {
                hasPhotos = true;
            }
            this.mediaTopicState = new MediaTopicPostState(hasPhotos);
            this.mediaTopicState.setErrorListener(this);
            this.createdTs = System.currentTimeMillis();
        } else {
            throw new MediaTopicPostException(10, "empty media topic");
        }
    }

    public long getCreatedTime() {
        return this.createdTs;
    }

    public void updateMediaTopic(PersistentTaskContext persistentContext, MediaComposerData editedData) {
        Logger.m173d("editedData=%s", editedData);
        MediaTopicPostState state = getMediaTopicState();
        PersistentTaskState executionState = state.getExecutionState();
        MediaTopicPostPhase phase = state.getPhase();
        if (executionState != PersistentTaskState.PAUSED && executionState != PersistentTaskState.FAILED && executionState != PersistentTaskState.ERROR) {
            throw new IllegalStateException("Can't modify the content while task is in running state: " + executionState);
        } else if (phase == MediaTopicPostPhase.COMPLETED || executionState == PersistentTaskState.COMPLETED) {
            throw new IllegalStateException("Too late to modify mediatopic - it's already being uploaded.");
        } else {
            this.toStatus = editedData.toStatus;
            try {
                MediaTopicMessage editedMessage = editedData.mediaTopicMessage;
                boolean newHasPhotos = editedMessage.hasPhotos();
                this.mediaTopicState = new MediaTopicPostState(newHasPhotos);
                this.mediaTopicState.setErrorListener(this);
                if (this.uploadPhotosSubtaskId == 0) {
                    this.mediaTopicMessage = editedMessage;
                    return;
                }
                PersistentTask oldImagesTask = (UploadImagesTask) getSubTask(persistentContext, this.uploadPhotosSubtaskId);
                this.uploadPhotosSubtaskId = 0;
                if (oldImagesTask == null) {
                    Logger.m185w("Upload images subtask id not found: %d", Integer.valueOf(this.uploadPhotosSubtaskId));
                    this.mediaTopicMessage = editedMessage;
                    persist(persistentContext);
                } else if (newHasPhotos) {
                    ArrayList<ImageEditInfo> newImageEditInfos = MediaTopicPostUtils.toImageEditInfos(editedMessage.getPhotos(), createAlbum(persistentContext.getContext()));
                    UploadImagesTask uploadImagesTask = new UploadImagesTask(getUid(), true, getId(), newImageEditInfos, false, this.mediaTopicType == MediaTopicType.USER ? null : this.groupId);
                    Iterator i$ = oldImagesTask.getImages().iterator();
                    while (i$.hasNext()) {
                        ImageEditInfo oldImage = (ImageEditInfo) i$.next();
                        Logger.m172d("oldImage=" + oldImage + " oldState=" + oldImagesTask.getImageState(oldImage));
                    }
                    i$ = newImageEditInfos.iterator();
                    while (i$.hasNext()) {
                        ImageEditInfo newImage = (ImageEditInfo) i$.next();
                        ImageState imageState = oldImagesTask.getImageState(newImage);
                        Logger.m172d("newImage=" + newImage + " oldState=" + imageState);
                        if (imageState != null) {
                            oldImagesTask.takeoverImage(persistentContext, newImage);
                            uploadImagesTask.presetImageState(newImage, imageState.copy());
                        }
                    }
                    persistentContext.cancelSubTask(oldImagesTask);
                    this.uploadPhotosSubtaskId = submitSubTask(persistentContext, uploadImagesTask);
                    this.mediaTopicMessage = editedMessage;
                    persist(persistentContext);
                } else {
                    this.mediaTopicMessage = editedMessage;
                    persist(persistentContext);
                }
            } finally {
                persist(persistentContext);
            }
        }
    }

    public MediaTopicMessage getMediaTopicMessage() {
        return this.mediaTopicMessage;
    }

    public boolean isSetToStatus() {
        return this.toStatus;
    }

    public MediaTopicPostState getMediaTopicState() {
        return this.mediaTopicState;
    }

    public MediaTopicType getMediaTopicType() {
        return this.mediaTopicType;
    }

    public String getGroupId() {
        return this.groupId;
    }

    private void throwIfNoInternet(Context context) throws MediaTopicPostException {
        if (!PersistentTaskUtils.checkForInternetConnection(context)) {
            throw new MediaTopicPostException(1);
        }
    }

    public PersistentTaskState execute(PersistentTaskContext persistentContext, Context context) throws MediaTopicPostException {
        this.mediaTopicState.executing();
        ArrayList<String> photoTokens = null;
        if (this.mediaTopicState.hasPhotos()) {
            boolean uploadCompleted = false;
            UploadImagesTask uploadImagesTask = null;
            if (this.uploadPhotosSubtaskId != 0) {
                uploadImagesTask = (UploadImagesTask) getSubTask(persistentContext, this.uploadPhotosSubtaskId);
            }
            if (uploadImagesTask == null) {
                startUploadPhotosSubTask(persistentContext, context, this.mediaTopicMessage.getPhotos());
            } else if (uploadImagesTask.getState() == PersistentTaskState.COMPLETED) {
                uploadCompleted = true;
                photoTokens = extractPhotoTokens(uploadImagesTask);
            }
            if (!uploadCompleted) {
                return PersistentTaskState.EXECUTING;
            }
        }
        this.mediaTopicState.posting();
        persist(persistentContext);
        try {
            throwIfNoInternet(context);
            this.topicId = MediaTopicPostUtils.postMediaTopic(JsonSessionTransportProvider.getInstance(), this.mediaTopicMessage, photoTokens, this.toStatus, this.mediaTopicType, this.groupId);
            this.mediaTopicState.complete();
            sendBusEventTopicUploadCompleted();
            reportStatCompleted(persistentContext);
            showCompletedNotification(context);
            return PersistentTaskState.COMPLETED;
        } catch (MediaTopicPostException e) {
            Logger.m177e("postMediaTopic failed: " + e, e);
            this.mediaTopicState.failed(e);
            if (e.getServerErrorCode() == 458) {
                return getRestrictedUidsAndFail(persistentContext, e);
            }
            if (e.getServerErrorCode() == 600) {
                return extractBlockLimitAndFail(persistentContext, e);
            }
            throw e;
        }
    }

    private void sendBusEventTopicUploadCompleted() {
        Logger.m172d("Sending topic load request");
        Bundle bundle = new Bundle();
        bundle.putInt("mediatopic_type", this.mediaTopicType.ordinal());
        if (this.mediaTopicType == MediaTopicType.USER) {
            bundle.putString("user_id", getUid());
            GlobalBus.send(2131624227, new BusEvent(bundle));
            return;
        }
        bundle.putString("group_id", getGroupId());
        GlobalBus.send(2131624226, new BusEvent(bundle));
    }

    private void showCompletedNotification(Context context) {
        Bitmap imageThumb = null;
        LocalizationManager localizationManager = LocalizationManager.from(context);
        String title = getTitleText(context, LocalizationManager.from(context));
        String text = localizationManager.getString(MediaTopicPostUtils.getMediaTopicIsCompletedTextResId(this.mediaTopicType));
        List<EditablePhotoItem> photos = this.mediaTopicMessage.getPhotos();
        EditablePhotoItem aPhoto = (photos == null || photos.size() == 0) ? null : (EditablePhotoItem) photos.get(0);
        Uri imageUri = aPhoto == null ? null : aPhoto.getImageUri();
        if (imageUri != null) {
            imageThumb = NotificationsUtils.createThumbnailForLargeIcon(context, imageUri, aPhoto.getOrientation());
        }
        Builder builder = new Builder(context);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setSmallIcon(2130838517);
        if (imageThumb != null) {
            builder.setLargeIcon(imageThumb);
        }
        if (this.mediaTopicType == MediaTopicType.USER) {
            builder.setContentIntent(createOpenMyNotesIntent(context));
        } else if (this.mediaTopicType == MediaTopicType.GROUP_THEME) {
            builder.setContentIntent(createOpenDiscussionCommentsIntent(context, this.groupId, this.topicId));
        } else if (this.mediaTopicType == MediaTopicType.GROUP_SUGGESTED) {
            builder.setContentIntent(createOpenGroupTopicsIntent(context, this.groupId, "suggested"));
        }
        builder.setAutoCancel(true);
        ((NotificationManager) context.getSystemService("notification")).notify(2131624288, builder.build());
    }

    public void onMediaTopicError() {
        Logger.m184w("");
        MediaComposerStats.error(getFailureCount(), getMediaTopicState(), System.currentTimeMillis() - this.createdTs, this.mediaTopicMessage == null ? 0 : this.mediaTopicMessage.getStats().photoCount);
    }

    private static PendingIntent createOpenMyNotesIntent(Context context) {
        String url = null;
        try {
            url = ShortLinkUtils.createUserTopicsShortLink(OdnoklassnikiApplication.getCurrentUser().uid);
        } catch (ShortLinkException e) {
            Logger.m185w("Failed to create url for user topics: %s", e);
        }
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        intent.setPackage(context.getPackageName());
        intent.putExtra("FORCE_PROCESS_INTENT", true);
        intent.setFlags(67239936);
        return PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 0);
    }

    private static PendingIntent createOpenGroupTopicsIntent(Context context, String groupId, String urlFilter) {
        Uri uri = null;
        try {
            uri = Uri.parse(ShortLinkUtils.createGroupTopicsShortLink(groupId, urlFilter));
        } catch (ShortLinkException e) {
            Logger.m185w("Failed to create url for group: %s", e);
        }
        Intent openGroup = new Intent("android.intent.action.VIEW", uri);
        openGroup.setPackage(context.getPackageName());
        openGroup.putExtra("FORCE_PROCESS_INTENT", true);
        openGroup.setFlags(67239936);
        return PendingIntent.getActivity(context.getApplicationContext(), 0, openGroup, 0);
    }

    private static PendingIntent createOpenDiscussionCommentsIntent(Context context, String groupId, String topicId) {
        Intent intent = new Intent(context, OdklSubActivity.class);
        Discussion discussion = new Discussion(topicId, Type.GROUP_TOPIC.name());
        String topicUrl = null;
        try {
            topicUrl = ShortLinkUtils.createGroupTopicShortLink(groupId, topicId);
        } catch (ShortLinkException e) {
            Logger.m185w("Failed to create url for group topic: %s", e);
        }
        Bundle args = DiscussionCommentsFragment.newArguments(discussion, Page.INFO, topicUrl);
        intent.putExtra("key_class_name", DiscussionCommentsFragment.class);
        intent.putExtra("key_argument_name", args);
        intent.putExtra("key_location_type", FragmentLocation.right.name());
        intent.putExtra("key_toolbar_visible", false);
        intent.putExtra("key_sliding_menu_enable", false);
        intent.putExtra("key_action_bar_visible", true);
        intent.putExtra("key_soft_input_type", SoftInputType.RESIZE.toString());
        intent.putExtra("key_activity_from_menu", false);
        intent.putExtra("key_hide_home_buttom", false);
        return PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 134217728);
    }

    private void reportStatCompleted(PersistentTaskContext persistentContext) {
        long delayMs = System.currentTimeMillis() - this.createdTs;
        int attempts = getFailureCountWithSubtasks(persistentContext);
        long totalPhotosSizeBytes = 0;
        if (this.uploadPhotosSubtaskId != 0) {
            PersistentTask task = persistentContext.getTask(this.uploadPhotosSubtaskId);
            if (task instanceof UploadImagesTask) {
                totalPhotosSizeBytes = ((UploadImagesTask) task).getUploadState().getTotalUploadSize();
            }
        }
        int uploadRateKbsec = 0;
        if (delayMs > 0) {
            uploadRateKbsec = (int) ((((float) totalPhotosSizeBytes) * kbPerSecFactor) / ((float) delayMs));
        }
        MediaComposerStats.uploaded(this.mediaTopicMessage, this.mediaTopicType, this.toStatus, delayMs, attempts, uploadRateKbsec);
    }

    static {
        kbPerSecFactor = 0.9765625f;
        exceededBlockLimitPattern = new ThreadLocal();
        CREATOR = new C04701();
    }

    private static Pattern getExceededBlockLimitPattern() {
        Pattern pattern = (Pattern) exceededBlockLimitPattern.get();
        if (pattern != null) {
            return pattern;
        }
        pattern = Pattern.compile("MEDIA_TOPIC_BLOCK_LIMIT : Too many media parameters, max (\\d+)");
        exceededBlockLimitPattern.set(pattern);
        return pattern;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private ru.ok.android.services.persistent.PersistentTaskState getRestrictedUidsAndFail(ru.ok.android.services.persistent.PersistentTaskContext r11, ru.ok.android.services.processors.mediatopic.MediaTopicPostException r12) throws ru.ok.android.services.processors.mediatopic.MediaTopicPostException {
        /*
        r10 = this;
        r9 = r12;
        r1 = r10.mediaTopicMessage;	 Catch:{ Throwable -> 0x004e }
        r7 = r1.getWithFriendsUids();	 Catch:{ Throwable -> 0x004e }
        if (r7 == 0) goto L_0x0048;
    L_0x0009:
        r1 = r7.isEmpty();	 Catch:{ Throwable -> 0x004e }
        if (r1 != 0) goto L_0x0048;
    L_0x000f:
        r6 = r10.requestAllowedUIDs();	 Catch:{ Throwable -> 0x004e }
        if (r6 == 0) goto L_0x0048;
    L_0x0015:
        r4 = getRestrictedUids(r7, r6);	 Catch:{ Throwable -> 0x004e }
        if (r4 == 0) goto L_0x0048;
    L_0x001b:
        r0 = new ru.ok.android.services.processors.mediatopic.MediaTopicPostException;	 Catch:{ Throwable -> 0x004e }
        r1 = r12.getErrorCode();	 Catch:{ Throwable -> 0x004e }
        r2 = r12.getMessage();	 Catch:{ Throwable -> 0x004e }
        r3 = r12.getCause();	 Catch:{ Throwable -> 0x004e }
        r5 = -1;
        r0.<init>(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x004e }
        r1 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x004e }
        r1.<init>();	 Catch:{ Throwable -> 0x004e }
        r2 = "Got restricted uids: ";
        r1 = r1.append(r2);	 Catch:{ Throwable -> 0x004e }
        r1 = r1.append(r4);	 Catch:{ Throwable -> 0x004e }
        r1 = r1.toString();	 Catch:{ Throwable -> 0x004e }
        ru.ok.android.utils.Logger.m176e(r1);	 Catch:{ Throwable -> 0x004e }
        r9 = r0;
        r10.storePrivacySettings(r11, r6);	 Catch:{ Throwable -> 0x004e }
    L_0x0048:
        r1 = r10.mediaTopicState;
        r1.failed(r9);
    L_0x004d:
        throw r9;
    L_0x004e:
        r8 = move-exception;
        r1 = "Failed to get precised error information: %s";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ all -> 0x0064 }
        r3 = 0;
        r2[r3] = r8;	 Catch:{ all -> 0x0064 }
        ru.ok.android.utils.Logger.m177e(r1, r2);	 Catch:{ all -> 0x0064 }
        ru.ok.android.utils.Logger.m178e(r8);	 Catch:{ all -> 0x0064 }
        r1 = r10.mediaTopicState;
        r1.failed(r9);
        goto L_0x004d;
    L_0x0064:
        r1 = move-exception;
        r2 = r10.mediaTopicState;
        r2.failed(r9);
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.processors.mediatopic.PostMediaTopicTask.getRestrictedUidsAndFail(ru.ok.android.services.persistent.PersistentTaskContext, ru.ok.android.services.processors.mediatopic.MediaTopicPostException):ru.ok.android.services.persistent.PersistentTaskState");
    }

    private void storePrivacySettings(PersistentTaskContext context, Collection<String> allowedUids) {
        FriendsFilterProcessor.storeResult(context.getContext().getContentResolver(), FriendsFilter.MARK_IN_TOPICS, allowedUids);
    }

    private Set<String> requestAllowedUIDs() {
        Set<String> uids = null;
        try {
            return (Set) new JsonFriendsFilterParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new FriendsFilterRequest(FriendsFilter.MARK_IN_TOPICS))).parse();
        } catch (Throwable e) {
            Logger.m177e("Failed to execute request: %s", e);
            Logger.m178e(e);
            return uids;
        }
    }

    private static List<String> getRestrictedUids(List<String> checkedUids, Set<String> allowedUids) {
        List<String> restrictedUids = null;
        for (String uid : checkedUids) {
            if (!allowedUids.contains(uid)) {
                if (restrictedUids == null) {
                    restrictedUids = new ArrayList();
                }
                restrictedUids.add(uid);
            }
        }
        return restrictedUids;
    }

    private ArrayList<String> extractPhotoTokens(UploadImagesTask uploadImagesTask) {
        ArrayList<ImageEditInfo> images = uploadImagesTask.getImages();
        ArrayList<String> tokens = new ArrayList(images.size());
        Iterator i$ = images.iterator();
        while (i$.hasNext()) {
            ImageEditInfo image = (ImageEditInfo) i$.next();
            String token = null;
            ImageState imageState = uploadImagesTask.getImageState(image);
            if (imageState == null) {
                Logger.m184w("image not found: " + image);
            } else {
                token = imageState.getToken();
            }
            tokens.add(token);
        }
        return tokens;
    }

    public void onPausing(PersistentTaskContext persistentContext) {
        this.mediaTopicState.pausing();
    }

    private void startUploadPhotosSubTask(PersistentTaskContext persistentContext, Context context, List<EditablePhotoItem> photos) {
        PhotoAlbumInfo albumInfo;
        if (this.mediaTopicType == MediaTopicType.USER) {
            albumInfo = createAlbum(context);
        } else {
            albumInfo = null;
        }
        this.uploadPhotosSubtaskId = submitSubTask(persistentContext, new UploadImagesTask(getUid(), true, getId(), MediaTopicPostUtils.toImageEditInfos(photos, albumInfo), false, this.mediaTopicType == MediaTopicType.USER ? null : this.groupId));
        persist(persistentContext);
    }

    protected void onStateChanged(PersistentTaskContext persistentContext) {
        this.mediaTopicState.onStateChanged(getState());
    }

    protected void onSubTaskStateChanged(PersistentTaskContext persistentContext, PersistentTask subTask) {
        this.mediaTopicState.onSubTaskStateChanged(subTask);
        persistentContext.notifyOnChanged(this);
    }

    protected void persist(PersistentTaskContext persistentContext) {
        super.persist(persistentContext);
        Logger.m172d("" + this.mediaTopicState);
    }

    protected PendingIntent getTaskDetailsIntent(PersistentTaskContext persistentContext) {
        Uri mediaTopicUri = Uri.parse("content://ru.ok.android/persistent_task/" + getId());
        Intent intent = createMediaTopicStatusIntent(persistentContext.getContext(), this.mediaTopicType);
        intent.setData(mediaTopicUri);
        intent.putExtra("media_topic_post", this);
        intent.putExtra("upload_state", getMediaTopicState());
        AppLaunchLog.fillLocalMediaTopicInProgress(intent);
        Logger.m172d("Setting task: " + this);
        return PendingIntent.getActivity(persistentContext.getContext(), 0, intent, 134217728);
    }

    public void createNotification(PersistentTaskContext persistentContext, PersistentTask activeSubTask, PersistentTaskNotificationBuilder notificationBuilder) {
        Context context = persistentContext.getContext();
        LocalizationManager localizationManager = LocalizationManager.from(context);
        if (canCancel()) {
            addCancelAction(context, localizationManager, notificationBuilder);
        }
        if (activeSubTask instanceof UploadImagesTask) {
            activeSubTask.createNotification(persistentContext, notificationBuilder);
            notificationBuilder.setTitle(getTitleText(context, localizationManager));
            return;
        }
        String text;
        int iconResId = 2130838515;
        String title = getTitleText(context, localizationManager);
        boolean isUserType = this.mediaTopicType == MediaTopicType.USER;
        switch (C04712.$SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[getState().ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                text = localizationManager.getString(isUserType ? 2131166136 : 2131166134);
                iconResId = 2130838515;
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                text = localizationManager.getString(2131166773);
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
            case Message.UUID_FIELD_NUMBER /*5*/:
                MediaTopicPostException error = (MediaTopicPostException) this.mediaTopicState.getError();
                if (error != null) {
                    switch (error.getErrorCode()) {
                        case Message.TEXT_FIELD_NUMBER /*1*/:
                            text = localizationManager.getString(2131166773);
                            break;
                        case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                            switch (error.getServerErrorCode()) {
                                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                                    text = localizationManager.getString(2131166142);
                                    break;
                                case 458:
                                    text = localizationManager.getString(2131166164);
                                    break;
                                case 600:
                                    text = localizationManager.getString(2131166154);
                                    break;
                                case 601:
                                    text = localizationManager.getString(2131166169);
                                    break;
                                case 602:
                                    text = localizationManager.getString(2131166162);
                                    break;
                                case 603:
                                    text = localizationManager.getString(2131166160);
                                    break;
                                case 604:
                                    text = localizationManager.getString(2131166158);
                                    break;
                                case 605:
                                    text = localizationManager.getString(2131166171);
                                    break;
                                case 606:
                                    text = localizationManager.getString(2131166173);
                                    break;
                                default:
                                    text = localizationManager.getString(2131166153);
                                    break;
                            }
                        case Message.REPLYSTICKERS_FIELD_NUMBER /*12*/:
                            text = localizationManager.getString(2131166777);
                            break;
                        default:
                            Object formatArg;
                            String msgFormat = localizationManager.getString(isUserType ? 2131166129 : 2131166128);
                            String errorMsg = error.getMessage();
                            if (errorMsg == null) {
                                formatArg = error;
                            } else {
                                String formatArg2 = errorMsg;
                            }
                            text = String.format(msgFormat, new Object[]{formatArg});
                            break;
                    }
                }
                text = localizationManager.getString(isUserType ? 2131166127 : 2131166126);
                iconResId = 2130838516;
                break;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                text = localizationManager.getString(MediaTopicPostUtils.getMediaTopicIsCompletedTextResId(this.mediaTopicType));
                iconResId = 2130838517;
                break;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                text = localizationManager.getString(isUserType ? 2131166138 : 2131166137);
                iconResId = 2130838512;
                notificationBuilder.addResumeAction(context, localizationManager, this);
                break;
            default:
                text = "";
                break;
        }
        notificationBuilder.setSmallIcon(iconResId);
        notificationBuilder.setTitle(title);
        notificationBuilder.setText(text);
    }

    public String getTitleText(Context context, LocalizationManager localizationManager) {
        boolean isUserType;
        String sampleText = getMediatopicSampleText();
        if (this.mediaTopicType == MediaTopicType.USER) {
            isUserType = true;
        } else {
            isUserType = false;
        }
        if (!TextUtils.isEmpty(sampleText)) {
            return String.format(Locale.getDefault(), localizationManager.getString(isUserType ? 2131166146 : 2131166145), new Object[]{sampleText});
        } else if (this.mediaTopicMessage.getPhotos().size() > 0) {
            return localizationManager.getString(isUserType ? 2131166148 : 2131166147);
        } else {
            return localizationManager.getString(isUserType ? 2131166144 : 2131166143);
        }
    }

    private String getMediatopicSampleText() {
        MediaTopicMessage mediaTopicMessage = this.mediaTopicMessage;
        if (mediaTopicMessage == null || mediaTopicMessage.isEmpty()) {
            return "";
        }
        int size = mediaTopicMessage.getItemsCount();
        String text = null;
        String music = null;
        String poll = null;
        String other = null;
        for (int i = 0; i < size; i++) {
            MediaItem mediaItem = mediaTopicMessage.getItem(i);
            String sampleText = mediaItem.getSampleText();
            if (!TextUtils.isEmpty(sampleText)) {
                switch (C04712.$SwitchMap$ru$ok$model$mediatopics$MediaItemType[mediaItem.type.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        text = sampleText;
                        break;
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        music = sampleText;
                        continue;
                    case Message.TYPE_FIELD_NUMBER /*3*/:
                        poll = sampleText;
                        continue;
                    default:
                        other = sampleText;
                        continue;
                }
                if (text == null) {
                    return text;
                }
                if (poll != null) {
                    return poll;
                }
                if (music != null) {
                    return music;
                }
                if (other == null) {
                    return other;
                }
                return null;
            }
        }
        if (text == null) {
            return text;
        }
        if (poll != null) {
            return poll;
        }
        if (music != null) {
            return music;
        }
        if (other == null) {
            return null;
        }
        return other;
    }

    private boolean canCancel() {
        MediaTopicPostPhase phase = getMediaTopicState().getPhase();
        return (phase == MediaTopicPostPhase.COMPLETED || phase == MediaTopicPostPhase.UPLOADING_MEDIA_TOPIC) ? false : true;
    }

    private void addCancelAction(Context context, LocalizationManager localizationManager, PersistentTaskNotificationBuilder notificationBuilder) {
        Intent cancel = createMediaTopicStatusIntent(context, this.mediaTopicType);
        cancel.setData(ContentUris.withAppendedId(PersistentTaskService.CONTENT_URI, (long) getId()));
        cancel.putExtra("media_topic_post", this);
        cancel.putExtra("cancel", true);
        cancel.putExtra("upload_state", getMediaTopicState());
        notificationBuilder.addCancelAction(context, localizationManager, PendingIntent.getActivity(context, 0, cancel, 134217728));
    }

    private PhotoAlbumInfo createAlbum(Context context) {
        String mobileAlbumTitle = LocalizationManager.getString(context, 2131166213);
        PhotoAlbumInfo mobileAlbum = new PhotoAlbumInfo();
        mobileAlbum.setId("application");
        mobileAlbum.setTitle(mobileAlbumTitle);
        mobileAlbum.setOwnerType(OwnerType.USER);
        return mobileAlbum;
    }

    public String toString() {
        return "PostMediaTopicTask[id=" + getId() + " state=" + getState() + " parentId=" + getParentId() + " isCanceled=" + isCanceled() + " isPausing=" + isPausing() + " toStatus=" + this.toStatus + " subTaskIds=" + getSubTaskIds() + " postState=" + getMediaTopicState() + " createdTs=" + this.createdTs + " topicId=" + this.topicId + "]";
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.mediaTopicState != null) {
            this.mediaTopicState.setErrorListener(this);
        }
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.mediaTopicMessage, flags);
        dest.writeInt(this.toStatus ? 1 : 0);
        dest.writeParcelable(this.mediaTopicState, flags);
        dest.writeInt(this.uploadPhotosSubtaskId);
        dest.writeInt(this.mediaTopicType.ordinal());
        dest.writeString(this.groupId);
        dest.writeLong(this.createdTs);
        dest.writeString(this.topicId);
    }

    protected PostMediaTopicTask(Parcel src) {
        super(src);
        ClassLoader cl = PostMediaTopicTask.class.getClassLoader();
        this.mediaTopicMessage = (MediaTopicMessage) src.readParcelable(cl);
        this.toStatus = src.readInt() != 0;
        this.mediaTopicState = (MediaTopicPostState) src.readParcelable(cl);
        this.uploadPhotosSubtaskId = src.readInt();
        this.mediaTopicType = MediaTopicType.values()[src.readInt()];
        this.groupId = src.readString();
        this.createdTs = src.readLong();
        this.topicId = src.readString();
        if (this.mediaTopicState != null) {
            this.mediaTopicState.setErrorListener(this);
        }
    }

    public PersistentTask copy() {
        Parcel parcel = toParcel();
        PersistentTask copy = new PostMediaTopicTask(parcel);
        parcel.recycle();
        return copy;
    }

    private static Intent createMediaTopicStatusIntent(Context context, MediaTopicType mediaTopicType) {
        Intent intent = new Intent();
        if (mediaTopicType == MediaTopicType.USER) {
            intent.setClassName(context, "ru.ok.android.ui.activity.MediaTopicUserStatusActivity");
        } else {
            intent.setClassName(context, "ru.ok.android.ui.activity.MediaTopicGroupStatusActivity");
        }
        return intent;
    }
}
