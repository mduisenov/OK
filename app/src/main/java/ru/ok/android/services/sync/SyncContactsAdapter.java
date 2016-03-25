package ru.ok.android.services.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SyncResult;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import ru.ok.android.app.helper.AccountsHelper;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.services.processors.friends.GetFriendsProcessor;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.URLUtil;
import ru.ok.android.utils.settings.Settings;
import ru.ok.model.UserInfo;

public final class SyncContactsAdapter extends AbstractThreadedSyncAdapter {

    /* renamed from: ru.ok.android.services.sync.SyncContactsAdapter.1 */
    static class C05181 extends BaseBitmapDataSubscriber {
        final /* synthetic */ CountDownLatch val$countDownLatch;
        final /* synthetic */ ContentResolver val$cr;
        final /* synthetic */ long val$rawContactId;

        C05181(ContentResolver contentResolver, long j, CountDownLatch countDownLatch) {
            this.val$cr = contentResolver;
            this.val$rawContactId = j;
            this.val$countDownLatch = countDownLatch;
        }

        protected void onNewResultImpl(Bitmap bitmap) {
            SyncContactsAdapter.writeDisplayPhoto(this.val$cr, this.val$rawContactId, bitmap);
            this.val$countDownLatch.countDown();
        }

        protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
            this.val$countDownLatch.countDown();
        }
    }

    public SyncContactsAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize, false);
    }

    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Context context = getContext();
        if (Settings.hasLoginData(context)) {
            if (PermissionUtils.checkSelfPermission(context, "android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS") != 0) {
                Logger.m184w("No permission to read/write contacts");
                Settings.clearSettingInvariableByKey(context, context.getString(2131166672));
                return;
            } else if (!hasContactsProvider(context)) {
                Logger.m184w("Contacts provider not found");
                return;
            } else if (Settings.getBoolValueInvariable(context, context.getString(2131166672), false)) {
                try {
                    GetFriendsProcessor.loadFriends(0);
                    ContentResolver cr = context.getContentResolver();
                    Uri uri = RawContacts.CONTENT_URI;
                    String[] strArr = new String[]{"sync1", "_id"};
                    r6 = new String[2];
                    r6[0] = account.type;
                    r6[1] = account.name;
                    Cursor cursor = cr.query(uri, strArr, "account_type = ? AND account_name = ?", r6, null);
                    if (cursor != null) {
                        UserInfo user;
                        Map<String, Long> existingEntries = new HashMap();
                        while (cursor.moveToNext()) {
                            try {
                                String string = cursor.getString(0);
                                existingEntries.put(uid, Long.valueOf(cursor.getLong(1)));
                            } finally {
                                cursor.close();
                            }
                        }
                        List<UserInfo> friends = UsersStorageFacade.queryFriends();
                        Map<String, Long> hashMap = new HashMap(existingEntries);
                        for (UserInfo userInfo : friends) {
                            hashMap.remove(userInfo.uid);
                        }
                        Set<UserInfo> toInsert = new HashSet();
                        Map<Long, UserInfo> toUpdate = new HashMap();
                        for (UserInfo friend : friends) {
                            if (existingEntries.containsKey(friend.uid)) {
                                toUpdate.put(existingEntries.get(friend.uid), friend);
                            } else {
                                toInsert.add(friend);
                            }
                        }
                        Logger.m173d("toDelete: %s", hashMap.values());
                        Logger.m173d("toUpdate: %s", toUpdate.values());
                        Logger.m173d("toInsert: %s", toInsert);
                        ArrayList<ContentProviderOperation> operations = new ArrayList();
                        for (Long rawContactId : hashMap.values()) {
                            Uri build = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").build();
                            operations.add(ContentProviderOperation.newDelete(ContentUris.withAppendedId(uri, rawContactId.longValue())).build());
                        }
                        Map<Integer, UserInfo> usersToFetchPhotos = new HashMap();
                        for (UserInfo user2 : toInsert) {
                            int index = operations.size();
                            usersToFetchPhotos.put(Integer.valueOf(index), user2);
                            operations.add(insertRawUser(operations.size(), user2, account));
                            operations.add(insertContactDataName(user2, index, operations.size()));
                            insertContactOkData(context, user2, operations, index);
                        }
                        for (Entry<Long, UserInfo> entry : toUpdate.entrySet()) {
                            user2 = (UserInfo) entry.getValue();
                            long rawContactId2 = ((Long) entry.getKey()).longValue();
                            operations.add(deleteContactData(rawContactId2, operations.size()));
                            operations.add(updateContactDataName(user2, rawContactId2, operations.size()));
                            updateContactOkData(context, user2, operations, rawContactId2);
                        }
                        try {
                            Logger.m172d("Applying batch...");
                            ContentProviderResult[] results = cr.applyBatch("com.android.contacts", operations);
                            Logger.m172d("Try to load photos...");
                            for (Entry<Integer, UserInfo> entry2 : usersToFetchPhotos.entrySet()) {
                                insertContactPicture(cr, ContentUris.parseId(results[((Integer) entry2.getKey()).intValue()].uri), (UserInfo) entry2.getValue());
                            }
                            for (Entry<Long, UserInfo> entry3 : toUpdate.entrySet()) {
                                insertContactPicture(cr, ((Long) entry3.getKey()).longValue(), (UserInfo) entry3.getValue());
                            }
                            Logger.m172d("Done.");
                            return;
                        } catch (Exception e) {
                            Logger.m180e(e, "Failed to apply batch", e);
                            return;
                        }
                    }
                    return;
                } catch (Throwable e2) {
                    Logger.m179e(e2, "Failed to update friends, exiting...");
                    return;
                }
            } else {
                Logger.m172d("Sync disabled by user");
                removeAccountContacts(context, account);
                return;
            }
        }
        Logger.m184w("We are not logged in, remove all accounts");
        AccountsHelper.deleteAccounts(context);
    }

    public static void removeAccountContacts(Context context, Account account) {
        if (hasContactsProvider(context)) {
            if (PermissionUtils.checkSelfPermission(context, "android.permission.WRITE_CONTACTS") != 0) {
                Logger.m184w("No permission to remove contacts");
                Settings.clearSettingInvariableByKey(context, context.getString(2131166672));
                return;
            }
            int count = context.getContentResolver().delete(RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").build(), "account_type = ? AND account_name = ?", new String[]{account.type, account.name});
            Logger.m173d("Remove contacts for account %s, %d", account, Integer.valueOf(count));
            return;
        }
        Logger.m184w("Contacts provider not found");
    }

    private static boolean hasContactsProvider(Context context) {
        if (context.getPackageManager().resolveContentProvider("com.android.contacts", 0) != null) {
            return true;
        }
        return false;
    }

    private ContentProviderOperation insertRawUser(int size, UserInfo user, Account account) {
        return ContentProviderOperation.newInsert(RawContacts.CONTENT_URI).withValue("account_type", account.type).withValue("account_name", account.name).withValue("sync1", user.uid).withYieldAllowed(size % 100 == 0).build();
    }

    private ContentProviderOperation insertContactDataName(UserInfo userInfo, int rawContactIdBackRef, int size) {
        return ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue("mimetype", "vnd.android.cursor.item/name").withValue("data1", userInfo.name).withValue("data3", userInfo.lastName).withValue("data2", userInfo.firstName).withValueBackReference("raw_contact_id", rawContactIdBackRef).withYieldAllowed(size % 100 == 0).build();
    }

    private void insertContactOkData(Context context, UserInfo userInfo, ArrayList<ContentProviderOperation> operations, int rawContactIdBackRef) {
        addDataOperations(context, userInfo, operations, (long) rawContactIdBackRef, true);
    }

    private static void addDataOperations(Context context, UserInfo userInfo, ArrayList<ContentProviderOperation> operations, long contactId, boolean isBackRef) {
        boolean z;
        boolean z2 = true;
        operations.add(addContactId(ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue("mimetype", context.getString(2131166208)).withValue("data1", userInfo.uid).withValue("data2", context.getString(2131166857)).withYieldAllowed(operations.size() % 100 == 0), contactId, isBackRef).build());
        int size = operations.size();
        Builder withValue = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue("mimetype", context.getString(2131166207)).withValue("data1", userInfo.uid).withValue("data2", context.getString(2131166879));
        if (size % 100 == 0) {
            z = true;
        } else {
            z = false;
        }
        operations.add(addContactId(withValue.withYieldAllowed(z), contactId, isBackRef).build());
        if (userInfo.birthday != null) {
            size = operations.size();
            Builder withValue2 = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue("mimetype", "vnd.android.cursor.item/contact_event").withValue("data2", Integer.valueOf(3)).withValue("data1", userInfo.birthday);
            if (size % 100 != 0) {
                z2 = false;
            }
            operations.add(addContactId(withValue2.withYieldAllowed(z2), contactId, isBackRef).build());
        }
    }

    private static Builder addContactId(Builder op, long contactId, boolean isBackRef) {
        if (isBackRef) {
            op.withValueBackReference("raw_contact_id", (int) contactId);
        } else {
            op.withValue("raw_contact_id", Long.valueOf(contactId));
        }
        return op;
    }

    private static ContentProviderOperation deleteContactData(long rawContactId, int size) {
        boolean z = true;
        Builder withSelection = ContentProviderOperation.newDelete(Data.CONTENT_URI).withSelection("raw_contact_id = ?", new String[]{String.valueOf(rawContactId)});
        if (size % 100 != 0) {
            z = false;
        }
        return withSelection.withYieldAllowed(z).build();
    }

    private ContentProviderOperation updateContactDataName(UserInfo userInfo, long rawContactId, int size) {
        return ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue("mimetype", "vnd.android.cursor.item/name").withValue("raw_contact_id", Long.valueOf(rawContactId)).withValue("data1", userInfo.name).withValue("data3", userInfo.lastName).withValue("data2", userInfo.firstName).withYieldAllowed(size % 100 == 0).build();
    }

    private void updateContactOkData(Context context, UserInfo userInfo, ArrayList<ContentProviderOperation> operations, long rawContactId) {
        addDataOperations(context, userInfo, operations, rawContactId, false);
    }

    private static void insertContactPicture(ContentResolver cr, long rawContactId, UserInfo userInfo) {
        String picUrl = !TextUtils.isEmpty(userInfo.bigPicUrl) ? userInfo.bigPicUrl : userInfo.picUrl;
        Logger.m172d("Load avatar by url: " + picUrl);
        if (URLUtil.isStubUrl(picUrl)) {
            writeDisplayPhoto(cr, rawContactId, null);
            return;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Fresco.getImagePipeline().fetchDecodedImage(ImageRequest.fromUri(picUrl), null).subscribe(new C05181(cr, rawContactId, countDownLatch), ThreadUtil.executorService);
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to wait");
        }
    }

    private static void writeDisplayPhoto(ContentResolver cr, long rawContactId, Bitmap bitmap) {
        try {
            AssetFileDescriptor fd = cr.openAssetFileDescriptor(Uri.withAppendedPath(ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId), "display_photo"), "rw");
            OutputStream os = fd.createOutputStream();
            if (bitmap != null) {
                bitmap.compress(CompressFormat.PNG, 100, os);
            }
            os.close();
            fd.close();
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to write account photo");
        }
    }
}
