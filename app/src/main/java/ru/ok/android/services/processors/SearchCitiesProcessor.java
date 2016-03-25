package ru.ok.android.services.processors;

import android.support.annotation.Nullable;
import java.util.ArrayList;
import org.json.JSONException;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.search.SearchCitiesParser;
import ru.ok.java.api.request.search.SearchCitiesRequest;
import ru.ok.model.search.SearchCityResult;

public final class SearchCitiesProcessor {
    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @ru.ok.android.bus.annotation.Subscribe(on = 2131623944, to = 2131624112)
    public void process(ru.ok.android.bus.BusEvent r12) {
        /*
        r11 = this;
        r10 = 2131624256; // 0x7f0e0140 float:1.8875687E38 double:1.0531623147E-314;
        r6 = r12.bundleInput;
        r7 = "query";
        r5 = r6.getString(r7);
        r6 = r12.bundleInput;
        r7 = "locale";
        r3 = r6.getString(r7);
        r0 = searchCities(r5, r3);	 Catch:{ BaseApiException -> 0x0047, JSONException -> 0x0033 }
        r4 = new android.os.Bundle;	 Catch:{ BaseApiException -> 0x0047, JSONException -> 0x0033 }
        r4.<init>();	 Catch:{ BaseApiException -> 0x0047, JSONException -> 0x0033 }
        r6 = "cities";
        r4.putParcelableArrayList(r6, r0);	 Catch:{ BaseApiException -> 0x0047, JSONException -> 0x0033 }
        r6 = 2131624256; // 0x7f0e0140 float:1.8875687E38 double:1.0531623147E-314;
        r7 = new ru.ok.android.bus.BusEvent;	 Catch:{ BaseApiException -> 0x0047, JSONException -> 0x0033 }
        r8 = r12.bundleInput;	 Catch:{ BaseApiException -> 0x0047, JSONException -> 0x0033 }
        r9 = -1;
        r7.<init>(r8, r4, r9);	 Catch:{ BaseApiException -> 0x0047, JSONException -> 0x0033 }
        ru.ok.android.bus.GlobalBus.send(r6, r7);	 Catch:{ BaseApiException -> 0x0047, JSONException -> 0x0033 }
    L_0x0032:
        return;
    L_0x0033:
        r1 = move-exception;
    L_0x0034:
        ru.ok.android.utils.Logger.m178e(r1);
        r2 = ru.ok.android.services.processors.base.CommandProcessor.createErrorBundle(r1);
        r6 = new ru.ok.android.bus.BusEvent;
        r7 = r12.bundleInput;
        r8 = -2;
        r6.<init>(r7, r2, r8);
        ru.ok.android.bus.GlobalBus.send(r10, r6);
        goto L_0x0032;
    L_0x0047:
        r1 = move-exception;
        goto L_0x0034;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.processors.SearchCitiesProcessor.process(ru.ok.android.bus.BusEvent):void");
    }

    public static ArrayList<SearchCityResult> searchCities(String query, @Nullable String locale) throws BaseApiException, JSONException {
        return SearchCitiesParser.parseCities(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new SearchCitiesRequest(query, locale)).getResultAsObject());
    }
}
