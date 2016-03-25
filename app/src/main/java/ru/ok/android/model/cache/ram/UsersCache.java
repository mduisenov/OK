package ru.ok.android.model.cache.ram;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.DatabaseExecutor;
import ru.ok.android.db.DatabaseExecutor.DatabaseOperation;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.model.UserInfo;

public final class UsersCache {
    private static final UsersCache instance;
    private final SQLiteDatabase db;
    private final LruCache<String, UserInfo> users;

    /* renamed from: ru.ok.android.model.cache.ram.UsersCache.1 */
    class C03651 implements DatabaseOperation {
        final /* synthetic */ List val$users;

        C03651(List list) {
            this.val$users = list;
        }

        public void performOperation(SQLiteDatabase db) {
            UsersStorageFacade.updateUsers(db, this.val$users);
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.UsersCache.2 */
    class C03662 implements DatabaseOperation {
        final /* synthetic */ List val$usersMessages;

        C03662(List list) {
            this.val$usersMessages = list;
        }

        public void performOperation(SQLiteDatabase db) {
            UsersStorageFacade.updateUsersForMessage(db, this.val$usersMessages);
        }
    }

    /* renamed from: ru.ok.android.model.cache.ram.UsersCache.3 */
    class C03673 implements DatabaseOperation {
        final /* synthetic */ List val$usersOnline;

        C03673(List list) {
            this.val$usersOnline = list;
        }

        public void performOperation(SQLiteDatabase db) {
            UsersStorageFacade.updateUsersOnline(db, this.val$usersOnline);
        }
    }

    static {
        instance = new UsersCache();
    }

    private UsersCache() {
        this.users = new LruCache(500);
        this.db = OdnoklassnikiApplication.getDatabase(OdnoklassnikiApplication.getContext());
    }

    public static UsersCache getInstance() {
        return instance;
    }

    @Nullable
    public synchronized UserInfo getUser(@NonNull String userId) {
        UserInfo user;
        user = (UserInfo) this.users.get(userId);
        if (user == null) {
            user = UsersStorageFacade.queryUser(this.db, userId);
            if (user != null) {
                this.users.put(userId, user);
            }
        }
        return user;
    }

    public synchronized void clear() {
        this.users.evictAll();
    }

    @NonNull
    public synchronized ArrayList<UserInfo> getUsers(@NonNull Collection<String> userIds, @Nullable AtomicReference<List<String>> absentUsers) {
        ArrayList<UserInfo> result;
        result = new ArrayList();
        Set<String> queryIntoDb = null;
        for (String userId : userIds) {
            UserInfo user = (UserInfo) this.users.get(userId);
            if (user == null) {
                if (queryIntoDb == null) {
                    queryIntoDb = new HashSet();
                }
                queryIntoDb.add(userId);
            } else {
                result.add(user);
            }
        }
        if (queryIntoDb != null) {
            for (UserInfo user2 : UsersStorageFacade.queryUsers(this.db, (Collection) queryIntoDb)) {
                result.add(user2);
                this.users.put(user2.uid, user2);
                queryIntoDb.remove(user2.uid);
            }
            if (!(queryIntoDb.isEmpty() || absentUsers == null)) {
                List<String> ids = (List) absentUsers.get();
                if (ids == null) {
                    ids = new ArrayList();
                    absentUsers.set(ids);
                }
                ids.addAll(queryIntoDb);
            }
        }
        return result;
    }

    public synchronized void updateUsers(List<UserInfo> users) {
        for (UserInfo user : users) {
            this.users.put(user.uid, user);
        }
        DatabaseExecutor.getInstance().addOperation(new C03651(users));
    }

    public void updateUsers4Message(List<UserInfo> usersMessages) {
        for (UserInfo userInfo : usersMessages) {
            UserInfo currentUser = (UserInfo) this.users.get(userInfo.uid);
            if (currentUser != null) {
                currentUser.lastOnline = userInfo.lastOnline;
                currentUser.online = userInfo.online;
                currentUser.picUrl = userInfo.picUrl;
                currentUser.bigPicUrl = userInfo.bigPicUrl;
                currentUser.firstName = userInfo.firstName;
                currentUser.lastName = userInfo.lastName;
                currentUser.name = userInfo.name;
                currentUser.genderType = userInfo.genderType;
            }
        }
        DatabaseExecutor.getInstance().addOperation(new C03662(usersMessages));
    }

    public synchronized void updateUsersOnline(List<UserInfo> usersOnline) {
        for (UserInfo userInfo : usersOnline) {
            UserInfo currentUser = (UserInfo) this.users.get(userInfo.uid);
            if (currentUser != null) {
                currentUser.availableCall = userInfo.availableCall;
                currentUser.availableVMail = userInfo.availableVMail;
                currentUser.lastOnline = userInfo.lastOnline;
                currentUser.online = userInfo.online;
            }
        }
        DatabaseExecutor.getInstance().addOperation(new C03673(usersOnline));
    }
}
