package ru.ok.android.ui.nativeRegistration;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import java.util.ArrayList;
import ru.ok.android.db.access.AuthorizedUsersStorageFacade;
import ru.ok.android.model.AuthorizedUser;

public class AuthorizedUsersLoader extends AsyncTaskLoader<ArrayList<AuthorizedUser>> {
    ArrayList<AuthorizedUser> users;

    public AuthorizedUsersLoader(Context context) {
        super(context);
    }

    public ArrayList<AuthorizedUser> loadInBackground() {
        this.users = AuthorizedUsersStorageFacade.getUsers();
        return this.users;
    }

    protected void onStartLoading() {
        if (this.users != null) {
            deliverResult(this.users);
        } else {
            forceLoad();
        }
    }

    public void deliverResult(ArrayList<AuthorizedUser> data) {
        super.deliverResult(data);
        this.users = data;
    }
}
