package ru.ok.android.services.processors;

import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.fillers.UserInfoValuesFiller;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.batch.SupplierRequest;
import ru.ok.java.api.request.guests.GetGuestRequest;
import ru.ok.java.api.request.guests.RemoveGuestRequest;
import ru.ok.java.api.request.param.RequestJSONParam;
import ru.ok.java.api.request.users.UserInfoRequest;

public final class GuestProcessor {
    @Subscribe(on = 2131623944, to = 2131624096)
    public final void removeGuest(BusEvent busEvent) {
        try {
            JsonHttpResult result = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new RemoveGuestRequest(busEvent.bundleOutput.getString("key_uid")));
            GlobalBus.send(2131624240, new BusEvent(busEvent.bundleInput, busEvent.bundleOutput, -1));
        } catch (Exception e) {
            GlobalBus.send(2131624240, new BusEvent(busEvent.bundleInput, busEvent.bundleOutput, -2));
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @ru.ok.android.bus.annotation.Subscribe(on = 2131623944, to = 2131623984)
    public final void loadGuest(ru.ok.android.bus.BusEvent r21) {
        /*
        r20 = this;
        r0 = r21;
        r2 = r0.bundleInput;
        r3 = "key_anchor";
        r10 = r2.getString(r3);
        r13 = new android.os.Bundle;
        r13.<init>();
        r2 = "forward";
        r15 = createBatchRequest(r2, r10);	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r2 = ru.ok.android.services.transport.JsonSessionTransportProvider.getInstance();	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r16 = r2.execJsonHttpMethod(r15);	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r2 = new ru.ok.java.api.json.JsonGuestBatchParser;	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r0 = r16;
        r2.<init>(r0);	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r14 = r2.parse();	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r14 = (ru.ok.model.guest.UsersResult) r14;	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r11 = new android.os.Bundle;	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r11.<init>();	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r2 = "key_guest_result";
        r11.putParcelable(r2, r14);	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r2 = 2131624236; // 0x7f0e012c float:1.8875646E38 double:1.053162305E-314;
        r3 = new ru.ok.android.bus.BusEvent;	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r0 = r21;
        r4 = r0.bundleInput;	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r5 = -1;
        r3.<init>(r4, r11, r5);	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        ru.ok.android.bus.GlobalBus.send(r2, r3);	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r6 = java.lang.System.currentTimeMillis();	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r17 = ru.ok.android.utils.controls.events.EventsManager.getInstance();	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r2 = 1;
        r0 = new ru.ok.model.events.OdnkEvent[r2];	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r18 = r0;
        r19 = 0;
        r2 = new ru.ok.model.events.OdnkEvent;	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r3 = "";
        r4 = "0";
        r5 = ru.ok.model.events.OdnkEvent.EventType.GUESTS;	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r8 = r6;
        r2.<init>(r3, r4, r5, r6, r8);	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r18[r19] = r2;	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r2 = java.util.Arrays.asList(r18);	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r0 = r17;
        r0.setEvents(r2);	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r2 = ru.ok.android.utils.controls.events.EventsManager.getInstance();	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
        r2.updateNow();	 Catch:{ NoConnectionException -> 0x0077, Exception -> 0x0090 }
    L_0x0076:
        return;
    L_0x0077:
        r12 = move-exception;
        r2 = "key_guest_error_type";
        r3 = 1;
        r13.putBoolean(r2, r3);
    L_0x007f:
        r2 = 2131624236; // 0x7f0e012c float:1.8875646E38 double:1.053162305E-314;
        r3 = new ru.ok.android.bus.BusEvent;
        r0 = r21;
        r4 = r0.bundleInput;
        r5 = -2;
        r3.<init>(r4, r13, r5);
        ru.ok.android.bus.GlobalBus.send(r2, r3);
        goto L_0x0076;
    L_0x0090:
        r12 = move-exception;
        r2 = "key_guest_error_type";
        r3 = 0;
        r13.putBoolean(r2, r3);
        goto L_0x007f;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.processors.GuestProcessor.loadGuest(ru.ok.android.bus.BusEvent):void");
    }

    private static BaseRequest createBatchRequest(String pagingDirection, String pagingAnchor) {
        GetGuestRequest guestRequest = new GetGuestRequest(pagingDirection, pagingAnchor);
        return new BatchRequest(new BatchRequests().addRequest(guestRequest).addRequest(new UserInfoRequest(new RequestJSONParam(new SupplierRequest(guestRequest.getUserIdsSupplier())), UserInfoValuesFiller.GUESTS.getRequestFields(), false)));
    }
}
