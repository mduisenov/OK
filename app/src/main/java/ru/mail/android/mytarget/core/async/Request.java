package ru.mail.android.mytarget.core.async;

import android.content.Context;

public interface Request {
    void execute(Context context);

    ExecuteListener getExecuteListener();

    int getFailExecutions();

    int getRepeatsOnFail();

    int getSuccessExecutions();

    int getTotalExecutions();

    boolean isSuccess();

    void setExecuteListener(ExecuteListener executeListener);

    void setRepeatsOnFail(int i);
}
