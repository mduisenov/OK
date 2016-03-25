package ru.ok.android.ui.fragments.users.loader;

import android.content.Context;
import android.os.Bundle;
import ru.ok.android.services.processors.discussions.data.UsersLikesParcelable;

public abstract class LikesBaseLoader extends ServiceResultLoader<UsersLikesParcelable> {
    protected abstract void callService(String str);

    public LikesBaseLoader(Context context) {
        super(context);
    }

    protected void sendServiceCommand() {
        callService(this.result == null ? null : ((UsersLikesParcelable) this.result).getAnchorId());
    }

    protected UsersLikesParcelable convertBundle(UsersLikesParcelable oldResult, Bundle data) {
        UsersLikesParcelable result = (UsersLikesParcelable) data.getParcelable("USERS");
        if (result == null) {
            return oldResult;
        }
        if (oldResult == null) {
            return result;
        }
        oldResult.getUsers().addAll(result.getUsers());
        result.setUsers(oldResult.getUsers());
        return result;
    }

    public void loadPreviousPortion() {
        forceLoad();
    }

    public boolean isAllLoaded() {
        return this.result == null || ((UsersLikesParcelable) this.result).isAllLoaded();
    }
}
