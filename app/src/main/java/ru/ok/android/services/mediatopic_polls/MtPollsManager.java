package ru.ok.android.services.mediatopic_polls;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.json.JSONException;
import ru.ok.android.services.local.LocalModifsManager;
import ru.ok.android.services.local.LocalModifsStorageConfig;
import ru.ok.android.services.local.LocalModifsStorageInitListener;
import ru.ok.android.services.local.LocalSyncConflictResolver;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.json.mediatopics.MediaTopicPollParser;
import ru.ok.java.api.request.mediatopic.MediaTopicVoteRequest;
import ru.ok.model.mediatopics.MediaTopicPollResponse;
import ru.ok.model.stream.ActionCountInfo;
import ru.ok.model.stream.entities.FeedPollEntity;
import ru.ok.model.stream.entities.FeedPollEntity.Answer;

public class MtPollsManager extends LocalModifsManager<LocalMtPollVotes> implements LocalSyncConflictResolver<LocalMtPollVotes> {
    private final ArrayList<WeakReference<PollAnswersChangeListener>> pollChangeListeners;

    public interface PollAnswersChangeListener {
        void onPollAnswersChanged(String str);
    }

    public MtPollsManager(Context context, String currentUserId, LocalModifsStorageInitListener initListener) {
        super(context, currentUserId, new SqliteMtPollsStorage(context, currentUserId), new LocalModifsStorageConfig(20, 10), initListener);
        this.pollChangeListeners = new ArrayList();
        setConflictResolver(this);
        setSyncDelayMs(5000);
    }

    public void addWeakPollAnswersChangeListener(PollAnswersChangeListener listener) {
        synchronized (this.pollChangeListeners) {
            this.pollChangeListeners.add(new WeakReference(listener));
        }
    }

    private void notifyPollAnswersChanged(String pollId) {
        synchronized (this.pollChangeListeners) {
            for (int i = this.pollChangeListeners.size() - 1; i >= 0; i--) {
                PollAnswersChangeListener l = (PollAnswersChangeListener) ((WeakReference) this.pollChangeListeners.get(i)).get();
                if (l == null) {
                    this.pollChangeListeners.remove(i);
                } else {
                    l.onPollAnswersChanged(pollId);
                }
            }
        }
    }

    public Answer getAnswerInfo(@NonNull FeedPollEntity poll, @NonNull Answer answer) {
        LocalMtPollVotes local = (LocalMtPollVotes) getLocalModification(poll.id);
        Logger.m173d("getAnswerCount: local=%s", local);
        if (local == null) {
            return answer;
        }
        int savedCount;
        boolean savedSelf;
        long savedLastDate;
        if (answer.voteInfo != null) {
            savedCount = answer.voteInfo.count;
            savedSelf = answer.voteInfo.self;
            savedLastDate = answer.voteInfo.lastDate;
        } else {
            savedCount = 0;
            savedSelf = false;
            savedLastDate = 0;
        }
        int count = savedCount;
        boolean self = savedSelf;
        boolean localSelf = local.allVotes.contains(answer.id);
        if (localSelf && !savedSelf) {
            count++;
        } else if (!localSelf && savedSelf) {
            count = Math.max(0, count - 1);
        }
        return new Answer(answer.id, answer.text, new ActionCountInfo(count, localSelf, savedLastDate));
    }

    @NonNull
    public Answer toggle(@NonNull FeedPollEntity poll, @NonNull Answer answer, String logContext) {
        HashSet<String> newAllVotes;
        boolean isSingleAnswer = poll.options.contains("SingleChoice");
        boolean isUnvote = answer.voteInfo != null && answer.voteInfo.self;
        Logger.m173d("toggle >>> poll.id=%s answer.id=%s isSingleAnswer=%s isUnvote=%s", poll.getId(), answer.id, Boolean.valueOf(isSingleAnswer), Boolean.valueOf(isUnvote));
        LocalMtPollVotes local = (LocalMtPollVotes) getLocalModification(poll.id);
        Logger.m173d("toggle: local=%s", local);
        HashSet<String> notAddedVotes = null;
        HashSet<String> notRemovedVotes = null;
        if (local == null || local.allVotes == null) {
            newAllVotes = getSelectedAnswerIds(poll);
            if (isUnvote) {
                newAllVotes.remove(answer.id);
                notRemovedVotes = new HashSet();
                notRemovedVotes.add(answer.id);
            } else {
                newAllVotes.add(answer.id);
                notAddedVotes = new HashSet();
                notAddedVotes.add(answer.id);
                if (isSingleAnswer) {
                    notRemovedVotes = removePreviouslySelected(poll, answer.id);
                    if (notRemovedVotes != null) {
                        newAllVotes.removeAll(notRemovedVotes);
                    }
                }
            }
        } else {
            newAllVotes = new HashSet(local.allVotes);
            if (isUnvote) {
                newAllVotes.remove(answer.id);
                notRemovedVotes = new HashSet();
                notRemovedVotes.add(answer.id);
                if (local.notRemovedVotes != null) {
                    notRemovedVotes.addAll(local.notRemovedVotes);
                }
                if (local.notAddedVotes != null && (local.notAddedVotes.size() > 1 || !local.notAddedVotes.contains(answer.id))) {
                    notAddedVotes = new HashSet();
                    notAddedVotes.addAll(local.notAddedVotes);
                    notAddedVotes.remove(answer.id);
                }
            } else {
                newAllVotes.add(answer.id);
                notAddedVotes = new HashSet();
                notAddedVotes.add(answer.id);
                if (!(isSingleAnswer || local.notAddedVotes == null)) {
                    notAddedVotes.addAll(local.notAddedVotes);
                }
                if (local.notRemovedVotes != null && (local.notRemovedVotes.size() > 1 || local.notRemovedVotes.contains(answer.id))) {
                    notRemovedVotes = new HashSet();
                    notRemovedVotes.addAll(local.notRemovedVotes);
                    notRemovedVotes.remove(answer.id);
                }
                if (isSingleAnswer && newAllVotes.size() > 1) {
                    if (notRemovedVotes == null) {
                        notRemovedVotes = new HashSet();
                    }
                    notRemovedVotes.addAll(newAllVotes);
                    notRemovedVotes.remove(answer.id);
                    newAllVotes.removeAll(notRemovedVotes);
                }
            }
        }
        Logger.m173d("toggle <<< %s", new LocalMtPollVotes(poll.id, newAllVotes, notAddedVotes, notRemovedVotes, logContext));
        updateLocalModification(votes);
        notifyPollAnswersChanged(poll.id);
        return getAnswerInfo(poll, answer);
    }

    protected LocalMtPollVotes performSyncRequest(LocalMtPollVotes unsyncedItem) throws BaseApiException {
        Logger.m173d(">>> %s", unsyncedItem);
        try {
            Logger.m173d("received response: %s", new MediaTopicPollParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MediaTopicVoteRequest(unsyncedItem.id, unsyncedItem.notAddedVotes, unsyncedItem.notRemovedVotes, unsyncedItem.logContext)).getResultAsObject()).parse());
            Logger.m173d("<<< %s", makeUpdatedInfo(unsyncedItem, new MediaTopicPollParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MediaTopicVoteRequest(unsyncedItem.id, unsyncedItem.notAddedVotes, unsyncedItem.notRemovedVotes, unsyncedItem.logContext)).getResultAsObject()).parse()));
            return makeUpdatedInfo(unsyncedItem, new MediaTopicPollParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MediaTopicVoteRequest(unsyncedItem.id, unsyncedItem.notAddedVotes, unsyncedItem.notRemovedVotes, unsyncedItem.logContext)).getResultAsObject()).parse());
        } catch (JSONException e) {
            throw new ResultParsingException(e);
        }
    }

    private static LocalMtPollVotes makeUpdatedInfo(LocalMtPollVotes unsyncedItem, MediaTopicPollResponse mtResponse) {
        HashSet<String> allVotes = new HashSet();
        int size = mtResponse.answers.size();
        for (int i = 0; i < size; i++) {
            Answer answer = (Answer) mtResponse.answers.get(i);
            if (answer.voteInfo != null && answer.voteInfo.self) {
                allVotes.add(answer.id);
            }
        }
        HashSet<String> notAddedVotes = getNotAddedVotes(unsyncedItem, allVotes);
        HashSet<String> notRemovedVotes = getNotRemovedVotes(unsyncedItem, allVotes);
        if (notAddedVotes == null && notRemovedVotes == null) {
            return new LocalMtPollVotes(unsyncedItem.id, allVotes, null, null, unsyncedItem.logContext, 3, unsyncedItem.failedAttemptsCount, System.currentTimeMillis());
        }
        if (notRemovedVotes != null) {
            allVotes.removeAll(notRemovedVotes);
        }
        if (notAddedVotes != null) {
            allVotes.addAll(notAddedVotes);
        }
        return new LocalMtPollVotes(unsyncedItem.id, allVotes, notAddedVotes, notRemovedVotes, unsyncedItem.logContext, 1, unsyncedItem.failedAttemptsCount, 0);
    }

    @NonNull
    public LocalMtPollVotes onConflictInSync(@NonNull LocalMtPollVotes newLocalItem, @NonNull LocalMtPollVotes syncedItem) {
        LocalMtPollVotes result;
        Logger.m173d("newLocalItem=%s", newLocalItem);
        Logger.m173d("syncedItem=%s", syncedItem);
        HashSet<String> notAddedVotes = getNotAddedVotes(newLocalItem, syncedItem.allVotes);
        HashSet<String> notRemovedVotes = getNotRemovedVotes(newLocalItem, syncedItem.notRemovedVotes);
        if (notAddedVotes == null && notRemovedVotes == null) {
            result = syncedItem;
        } else {
            HashSet<String> newAllVotes = new HashSet(syncedItem.allVotes);
            if (notRemovedVotes != null) {
                newAllVotes.removeAll(notRemovedVotes);
            }
            if (notAddedVotes != null) {
                newAllVotes.addAll(notAddedVotes);
            }
            result = new LocalMtPollVotes(syncedItem.id, newAllVotes, notAddedVotes, notRemovedVotes, syncedItem.logContext, 1, syncedItem.failedAttemptsCount, syncedItem.syncedTs);
        }
        Logger.m173d("result=%s", result);
        return result;
    }

    @Nullable
    private static HashSet<String> getNotAddedVotes(@NonNull LocalMtPollVotes unsyncedItem, @Nullable HashSet<String> allVotes) {
        HashSet<String> notAddedVotes = null;
        if (unsyncedItem.notAddedVotes != null) {
            Iterator i$ = unsyncedItem.notAddedVotes.iterator();
            while (i$.hasNext()) {
                String answerId = (String) i$.next();
                if (allVotes == null || !allVotes.contains(answerId)) {
                    if (notAddedVotes == null) {
                        notAddedVotes = new HashSet();
                    }
                    notAddedVotes.add(answerId);
                }
            }
        }
        return notAddedVotes;
    }

    @Nullable
    private static HashSet<String> getNotRemovedVotes(@NonNull LocalMtPollVotes unsyncedItem, @Nullable HashSet<String> allVotes) {
        HashSet<String> notRemovedVotes = null;
        if (unsyncedItem.notRemovedVotes != null) {
            Iterator i$ = unsyncedItem.notRemovedVotes.iterator();
            while (i$.hasNext()) {
                String answerId = (String) i$.next();
                if (allVotes != null && allVotes.contains(answerId)) {
                    if (notRemovedVotes == null) {
                        notRemovedVotes = new HashSet();
                    }
                    notRemovedVotes.add(answerId);
                }
            }
        }
        return notRemovedVotes;
    }

    @NonNull
    private static HashSet<String> getSelectedAnswerIds(@NonNull FeedPollEntity poll) {
        HashSet<String> answerIds = new HashSet();
        int size = poll.answers.size();
        for (int i = 0; i < size; i++) {
            Answer answer = (Answer) poll.answers.get(i);
            if (answer.voteInfo != null && answer.voteInfo.self) {
                answerIds.add(answer.id);
            }
        }
        return answerIds;
    }

    private static HashSet<String> removePreviouslySelected(@NonNull FeedPollEntity poll, @NonNull String newAnswerId) {
        HashSet<String> answerIds = null;
        int size = poll.answers.size();
        for (int i = 0; i < size; i++) {
            Answer answer = (Answer) poll.answers.get(i);
            if (!(answer.voteInfo == null || !answer.voteInfo.self || TextUtils.equals(answer.id, newAnswerId))) {
                if (answerIds == null) {
                    answerIds = new HashSet();
                }
                answerIds.add(answer.id);
            }
        }
        return answerIds;
    }
}
