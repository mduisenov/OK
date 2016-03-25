package ru.mail.android.mytracker.providers;

import android.content.Context;
import java.util.Map;
import ru.mail.android.mytracker.builders.JSONBuilder;

public interface FPDataProvider {
    void collectData(Context context);

    void putDataToBuilder(JSONBuilder jSONBuilder);

    @Deprecated
    void putDataToMap(Map<String, String> map);

    void storeData(Context context);
}
