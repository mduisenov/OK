package ru.ok.android.services.presents;

import android.content.Context;
import android.support.annotation.NonNull;
import org.json.JSONException;
import ru.ok.android.services.local.LocalModifsManager;
import ru.ok.android.services.local.LocalModifsStorageConfig;
import ru.ok.android.services.local.LocalModifsStorageInitListener;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.request.presents.DeleteUserPresentRequest;

public class DeletedPresentsManager extends LocalModifsManager<LocalDeletedPresent> {

    /* renamed from: ru.ok.android.services.presents.DeletedPresentsManager.1 */
    class C04471 implements LocalModifsStorageInitListener {
        C04471() {
        }

        public void onInitializedLocalModifsStorage(long trimTs) {
        }
    }

    public DeletedPresentsManager(Context context, String currentUserId) {
        super(context, currentUserId, new SqliteDeletedPresentsStorage(context, currentUserId), new LocalModifsStorageConfig(20, 10), new C04471());
    }

    public void deletePresent(@NonNull String presentId) {
        updateLocalModification(new LocalDeletedPresent(presentId));
    }

    public boolean isPresentDeleted(@NonNull String presentId) {
        return ((LocalDeletedPresent) getLocalModification(presentId)) != null;
    }

    protected LocalDeletedPresent performSyncRequest(LocalDeletedPresent unsyncedItem) throws BaseApiException {
        boolean wasDeleted;
        try {
            wasDeleted = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new DeleteUserPresentRequest(unsyncedItem.id)).getResultAsObject().optBoolean("success");
        } catch (JSONException e) {
            wasDeleted = false;
        }
        return wasDeleted ? unsyncedItem.synced(System.currentTimeMillis()) : unsyncedItem.failedAttempt(5);
    }
}
