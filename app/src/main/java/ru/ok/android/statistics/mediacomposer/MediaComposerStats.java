package ru.ok.android.statistics.mediacomposer;

import android.text.TextUtils;
import android.util.Pair;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.processors.mediatopic.MediaTopicPostException;
import ru.ok.android.services.processors.mediatopic.MediaTopicPostState;
import ru.ok.android.services.processors.mediatopic.MediaTopicPostState.MediaTopicPostPhase;
import ru.ok.android.services.processors.photo.upload.ImageUploadException;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.statistics.StatsUtils;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaTopicMessage;
import ru.ok.android.ui.custom.mediacomposer.MediaTopicMessage.Stats;
import ru.ok.android.ui.custom.mediacomposer.MusicItem;
import ru.ok.android.ui.custom.mediacomposer.PhotoBlockItem;
import ru.ok.android.ui.custom.mediacomposer.PollItem;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.java.api.utils.ObjectUtils;
import ru.ok.model.mediatopics.MediaItemType;
import ru.ok.model.wmf.Track;

public final class MediaComposerStats {
    private static StatisticManager statManager;

    /* renamed from: ru.ok.android.statistics.mediacomposer.MediaComposerStats.1 */
    static /* synthetic */ class C05271 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$mediatopics$MediaItemType;

        static {
            $SwitchMap$ru$ok$model$mediatopics$MediaItemType = new int[MediaItemType.values().length];
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.PHOTO_BLOCK.ordinal()] = 1;
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
        }
    }

    public static void open(String path, MediaTopicType type) {
        String mtType = type == MediaTopicType.USER ? "user" : "group";
        statManager.addStatisticEvent("mc_open", true, Pair.create("path", path), Pair.create("type", mtType));
    }

    public static void draft(String action) {
        statManager.addStatisticEvent("mc_draft", true, Pair.create("action", action));
    }

    public static void toolbarStatus(String mode) {
        statManager.addStatisticEvent("mc_tb_status", true, new Pair[0]);
    }

    public static void toolbarCheckFriends(int checkedCount, String mode) {
        statManager.addStatisticEvent("mc_tb_check_friends", true, Pair.create("friends_count", Integer.toString(checkedCount)), Pair.create("mode", mode));
    }

    public static void toolbarCheckPlace(String mode) {
        statManager.addStatisticEvent("mc_tb_place", true, Pair.create("mode", mode));
    }

    public static void toolbarPhoto(int photoCount, String mode) {
        statManager.addStatisticEvent("mc_tb_photo", true, Pair.create("photo_count", Integer.toString(photoCount)), Pair.create("mode", mode));
    }

    public static void toolbarMusic(int trackCount, String mode) {
        statManager.addStatisticEvent("mc_tb_music", true, Pair.create("track_count", Integer.toString(trackCount)), Pair.create("mode", mode));
    }

    public static void toolbarPoll(int pollCount, String mode) {
        statManager.addStatisticEvent("mc_tb_poll", true, Pair.create("poll_count", Integer.toString(pollCount)), Pair.create("mode", mode));
    }

    public static void actionbarPost(MediaTopicMessage mt, boolean setStatus, String mode) {
        Stats stats = mt.getStats();
        statManager.addStatisticEvent("mc_ab_post", true, Pair.create("text_volume", Integer.toString(StatsUtils.getRangedValue(stats.textVolume))), Pair.create("photo_count", Integer.toString(stats.photoCount)), Pair.create("poll_count", Integer.toString(stats.pollCount)), Pair.create("track_count", Integer.toString(stats.trackCount)), Pair.create("friends_count", Integer.toString(stats.friendsCount)), Pair.create("set_status", Boolean.toString(setStatus)), Pair.create("mode", mode));
    }

    public static void addPhoto(int photoCount, String mode) {
        statManager.addStatisticEvent("mc_add_photo", true, Pair.create("block_size", Integer.toString(photoCount)), Pair.create("mode", mode));
    }

    public static void addMusic(MusicItem musicItem, String mode) {
        List<Track> tracks = musicItem.getTracks();
        int trackCount = tracks == null ? 0 : tracks.size();
        statManager.addStatisticEvent("mc_add_music", true, Pair.create("block_size", Integer.toString(trackCount)), Pair.create("mode", mode));
    }

    public static void addPoll(PollItem poll, String mode) {
        if (poll != null) {
            boolean singleChoice;
            if (poll.isMultiAnswersAllowed()) {
                singleChoice = false;
            } else {
                singleChoice = true;
            }
            int textVolume = 0;
            int answersCount = 0;
            List<String> answers = poll.getAnswers();
            if (answers != null) {
                answersCount = answers.size();
                for (String answer : answers) {
                    textVolume += TextUtils.getTrimmedLength(answer);
                }
            }
            String title = poll.getTitle();
            if (title != null) {
                textVolume += TextUtils.getTrimmedLength(title);
            }
            statManager.addStatisticEvent("mc_add_poll", true, Pair.create("block_size", Integer.toString(answersCount)), Pair.create("text_volume", Integer.toString(StatsUtils.getRangedValue(textVolume))), Pair.create("single_choice", Boolean.toString(singleChoice)), Pair.create("mode", mode));
        }
    }

    public static void checkFriends(Collection<String> originalCheckedUids, Collection<String> editedCheckedUid, String mode) {
        Set<String> uids1;
        Set<String> uids2;
        if (originalCheckedUids == null) {
            uids1 = Collections.emptySet();
        } else {
            uids1 = new HashSet(originalCheckedUids);
        }
        if (editedCheckedUid == null) {
            uids2 = Collections.emptySet();
        } else {
            uids2 = new HashSet(editedCheckedUid);
        }
        checkFriends(uids2.size() - uids1.size(), !ObjectUtils.setsEqual(uids1, uids2), mode);
    }

    public static void checkFriends(int deltaCheckedCount, boolean hasChanges, String mode) {
        statManager.addStatisticEvent("mc_check_friends", true, Pair.create("delta_size", Integer.toString(deltaCheckedCount)), Pair.create("has_changes", Boolean.toString(hasChanges)), Pair.create("mode", mode));
    }

    public static void swipeToDismiss(MediaItem removedItem, String mode) {
        if (removedItem != null && removedItem.type != null) {
            String blockType = removedItem.type.name();
            int blockSize = getBlockSize(removedItem);
            statManager.addStatisticEvent("mc_swipe_to_dismiss", true, Pair.create("block_type", blockType), Pair.create("block_size", Integer.toString(blockSize)), Pair.create("mode", mode));
        }
    }

    public static void pinchOutInsert(String mode, MediaItemType... blockTypes) {
        statManager.addStatisticEvent("mc_pinch_out_insert", true, Pair.create("block_type", getBlockTypes(blockTypes)), Pair.create("mode", mode));
    }

    public static void pinchInDelete(String mode, MediaItemType... blockTypes) {
        statManager.addStatisticEvent("mc_pinch_in_delete", true, Pair.create("block_type", getBlockTypes(blockTypes)), Pair.create("mode", mode));
    }

    public static void popupEdit(MediaItem block, String mode) {
        if (block != null && block.type != null) {
            statManager.addStatisticEvent("mc_popup_edit", true, Pair.create("block_type", block.type.name()), Pair.create("block_size", Integer.toString(getBlockSize(block))), Pair.create("mode", mode));
        }
    }

    public static void popupDelete(MediaItem block, String mode) {
        if (block != null && block.type != null) {
            statManager.addStatisticEvent("mc_popup_delete", true, Pair.create("block_type", block.type.name()), Pair.create("block_size", Integer.toString(getBlockSize(block))), Pair.create("mode", mode));
        }
    }

    public static void popupInsertText(MediaItem block, String mode) {
        if (block != null && block.type != null) {
            statManager.addStatisticEvent("mc_popup_insert_text", true, Pair.create("block_type", block.type.name()), Pair.create("block_size", Integer.toString(getBlockSize(block))), Pair.create("mode", mode));
        }
    }

    public static void editPhoto(String mode) {
        statManager.addStatisticEvent("mc_edit_photo", true, Pair.create("mode", mode));
    }

    public static void editMusic(MusicItem originalItem, MusicItem editedItem, String mode) {
        boolean hasChanges;
        List<Track> tracks1 = originalItem.getTracks();
        if (tracks1 == null) {
            tracks1 = Collections.emptyList();
        }
        List<Track> tracks2 = editedItem.getTracks();
        if (tracks2 == null) {
            tracks2 = Collections.emptyList();
        }
        int deltaTrackCount = tracks2.size() - tracks1.size();
        if (ObjectUtils.setsEqual(new HashSet(tracks1), new HashSet(tracks2))) {
            hasChanges = false;
        } else {
            hasChanges = true;
        }
        statManager.addStatisticEvent("mc_edit_music", true, Pair.create("delta_size", Integer.toString(deltaTrackCount)), Pair.create("has_changes", Boolean.toString(hasChanges)), Pair.create("mode", mode));
    }

    public static void editPoll(PollItem originalItem, PollItem editedItem, String mode) {
        boolean questionChanged;
        boolean answersChanged;
        boolean settingsChanged;
        List<String> answers1 = originalItem.getAnswers();
        List<String> answers2 = editedItem.getAnswers();
        if (answers1 == null) {
            answers1 = Collections.emptyList();
        }
        if (answers2 == null) {
            answers2 = Collections.emptyList();
        }
        int deltaAnswerCount = answers2.size() - answers1.size();
        if (TextUtils.equals(originalItem.getTitle(), editedItem.getTitle())) {
            questionChanged = false;
        } else {
            questionChanged = true;
        }
        if (ObjectUtils.setsEqual(new HashSet(answers1), new HashSet(answers2))) {
            answersChanged = false;
        } else {
            answersChanged = true;
        }
        if (originalItem.isMultiAnswersAllowed() != editedItem.isMultiAnswersAllowed()) {
            settingsChanged = true;
        } else {
            settingsChanged = false;
        }
        statManager.addStatisticEvent("mc_edit_poll", true, Pair.create("delta_size", Integer.toString(deltaAnswerCount)), Pair.create("question_changed", Boolean.toString(questionChanged)), Pair.create("answers_changed", Boolean.toString(answersChanged)), Pair.create("settings_changed", Boolean.toString(settingsChanged)), Pair.create("mode", mode));
    }

    public static void uploaded(MediaTopicMessage mediaTopic, MediaTopicType type, boolean setStatus, long delayMs, int attempts, int uploadRateKbsec) {
        String event = mediaTopic.getStats().photoCount > 0 ? "mc_uploaded_with_photos" : "mc_uploaded_no_photos";
        String mtType = type == MediaTopicType.USER ? "user" : "group";
        statManager.addStatisticEvent(event, true, Pair.create("type", mtType), Pair.create("text_volume", Integer.toString(StatsUtils.getRangedValue(stats.textVolume))), Pair.create("poll_count", Integer.toString(stats.pollCount)), Pair.create("photo_count", Integer.toString(stats.photoCount)), Pair.create("track_count", Integer.toString(stats.trackCount)), Pair.create("friends_count", Integer.toString(stats.friendsCount)), Pair.create("set_status", Boolean.toString(setStatus)), Pair.create(DelayInformation.ELEMENT, Integer.toString(StatsUtils.getDelayValue(delayMs))), Pair.create("attempts", Integer.toString(attempts)), Pair.create("upload_rate", Integer.toString(StatsUtils.getRangedValue(uploadRateKbsec))));
    }

    public static void error(int attempts, MediaTopicPostState state, long delayMs, int photoCount) {
        int subErrorCode;
        MediaTopicPostPhase phase = state == null ? null : state.getPhase();
        MediaTopicPostException error = state == null ? null : (MediaTopicPostException) state.getError();
        int errorCode = error == null ? 0 : error.getErrorCode();
        if (errorCode == 11) {
            Throwable cause = error.getCause();
            if (cause instanceof ImageUploadException) {
                ImageUploadException subError = (ImageUploadException) cause;
                if (subError.getErrorCode() == 4) {
                    subErrorCode = subError.getServerErrorCode();
                } else {
                    subErrorCode = subError.getErrorCode();
                }
            } else {
                subErrorCode = 0;
            }
        } else if (errorCode == 4) {
            subErrorCode = error.getServerErrorCode();
        } else {
            subErrorCode = 0;
        }
        statManager.addStatisticEvent("mc_upload_error_2", true, Pair.create("attempts", Integer.toString(attempts)), Pair.create(DelayInformation.ELEMENT, Integer.toString(StatsUtils.getDelayValue(delayMs))), Pair.create("photo_count", Integer.toString(photoCount)), Pair.create("phase", String.valueOf(phase)), Pair.create("error_code", Integer.toString(errorCode)), Pair.create("sub_error_code", Integer.toString(subErrorCode)));
    }

    public static void cancel(int attempts, MediaTopicPostState state, long delayMs, int photoCount) {
        PersistentTaskState uploadState = null;
        MediaTopicPostPhase phase = state == null ? null : state.getPhase();
        if (state != null) {
            uploadState = state.getExecutionState();
        }
        statManager.addStatisticEvent("mc_upload_cancel", true, Pair.create("attempts", Integer.toString(attempts)), Pair.create(DelayInformation.ELEMENT, Integer.toString(StatsUtils.getDelayValue(delayMs))), Pair.create("photo_count", Integer.toString(photoCount)), Pair.create("state", String.valueOf(uploadState)), Pair.create("phase", String.valueOf(phase)));
    }

    public static void openStatus(MediaTopicPostState state) {
        PersistentTaskState uploadState = null;
        MediaTopicPostPhase phase = state == null ? null : state.getPhase();
        if (state != null) {
            uploadState = state.getExecutionState();
        }
        statManager.addStatisticEvent("mc_open_status", true, Pair.create("state", String.valueOf(uploadState)), Pair.create("phase", String.valueOf(phase)));
    }

    public static void hitLimit(MediaTopicType type, String limitType) {
        statManager.addStatisticEvent("mc_hit_limit", false, Pair.create("type", String.valueOf(type)), Pair.create("limit_type", limitType));
    }

    private static String getBlockTypes(MediaItemType[] types) {
        if (types == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (MediaItemType type : types) {
            if (type != null) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(type.name());
            }
        }
        return sb.toString();
    }

    private static int getBlockSize(MediaItem item) {
        MediaItemType type = item == null ? null : item.type;
        if (type == null) {
            return 0;
        }
        switch (C05271.$SwitchMap$ru$ok$model$mediatopics$MediaItemType[type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return ((PhotoBlockItem) item).size();
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                List<Track> tracks = ((MusicItem) item).getTracks();
                if (tracks != null) {
                    return tracks.size();
                }
                return 0;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                List<String> answers = ((PollItem) item).getAnswers();
                if (answers != null) {
                    return answers.size();
                }
                return 0;
            default:
                return 1;
        }
    }

    static {
        statManager = StatisticManager.getInstance();
    }
}
