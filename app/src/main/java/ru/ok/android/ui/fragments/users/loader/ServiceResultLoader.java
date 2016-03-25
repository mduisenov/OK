package ru.ok.android.ui.fragments.users.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import ru.ok.android.app.helper.ServiceHelper;
import ru.ok.android.app.helper.ServiceHelper.CommandListener;
import ru.ok.android.app.helper.ServiceHelper.ResultCode;
import ru.ok.android.utils.Utils;

public abstract class ServiceResultLoader<D> extends Loader<D> implements CommandListener {
    protected D result;
    private int startedCount;

    protected abstract D convertBundle(D d, Bundle bundle);

    protected abstract boolean isRightCommand(String str);

    protected abstract void sendServiceCommand();

    public void onCommandResult(String commandName, ResultCode resultCode, Bundle data) {
        if (isRightCommand(commandName)) {
            unsubscribe();
            if (!isAbandoned()) {
                this.result = convertBundle(this.result, data);
                deliverResult(this.result);
            }
        }
    }

    private void subscribe() {
        if (this.startedCount == 0) {
            Utils.getServiceHelper().addListener(this);
        }
        this.startedCount++;
    }

    private void unsubscribe() {
        this.startedCount--;
        if (this.startedCount == 0) {
            Utils.getServiceHelper().removeListener(this);
        }
    }

    public ServiceResultLoader(Context context) {
        super(context);
    }

    protected void onForceLoad() {
        super.onForceLoad();
        cancelLoad();
        executeCommand();
    }

    public boolean cancelLoad() {
        if (this.startedCount > 0) {
            unsubscribe();
        }
        return false;
    }

    protected void executeCommand() {
        subscribe();
        sendServiceCommand();
    }

    protected ServiceHelper getServiceHelper() {
        return Utils.getServiceHelper();
    }

    protected void onStartLoading() {
        if (this.result != null) {
            deliverResult(this.result);
        } else {
            forceLoad();
        }
    }
}
