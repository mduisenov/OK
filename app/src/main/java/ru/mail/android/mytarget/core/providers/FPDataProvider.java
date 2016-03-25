package ru.mail.android.mytarget.core.providers;

import android.content.Context;
import java.util.Map;

public interface FPDataProvider {
    void collectData(Context context);

    Map<String, String> getData();

    void putDataTo(Map<String, String> map);
}
