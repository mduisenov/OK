package ru.ok.java.api.request;

import android.text.TextUtils;
import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;

@NoLoginNeeded
@HttpPreamble(hasTargetUrl = true, signType = Scope.APPLICATION)
public final class TranslationsMarkerRequest extends BaseRequest implements TargetUrlGetter {
    private final String keys;
    private final String lastUpdate;
    private final String locale;
    private final String packageValue;

    public TranslationsMarkerRequest(String packageValue, String keys, String locale, String lastUpdate) {
        this.packageValue = packageValue;
        this.keys = keys;
        this.locale = locale;
        this.lastUpdate = lastUpdate;
    }

    public String getMethodName() {
        return "/api/translations/getByMarker";
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.PACKAGE, this.packageValue).add(SerializeParamName.KEYS, this.keys).add(SerializeParamName.LOCALE, this.locale);
        if (!TextUtils.isEmpty(this.lastUpdate)) {
            serializer.add(SerializeParamName.LAST_UPDATE, this.lastUpdate);
        }
    }

    public String getTargetUrl() {
        return "http://api.odnoklassniki.ru";
    }
}
