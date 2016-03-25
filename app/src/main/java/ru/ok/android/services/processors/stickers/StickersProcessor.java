package ru.ok.android.services.processors.stickers;

import android.content.Context;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.stickers.StickersSetsParser;
import ru.ok.java.api.request.stickers.StickersSetRequest;
import ru.ok.model.stickers.StickersResponse;

public final class StickersProcessor {
    private final Context context;

    public StickersProcessor(Context context) {
        this.context = context;
    }

    @Subscribe(on = 2131623944, to = 2131624103)
    public void updateStickerSets() {
        try {
            StickersResponse current = StickersManager.getCurrentSet(this.context);
            int currentVersion = current == null ? 0 : current.version;
            StickersResponse stickersResponse = StickersSetsParser.parse(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new StickersSetRequest(currentVersion)));
            if (currentVersion != stickersResponse.version) {
                StickersManager.updateStickersSet(this.context, stickersResponse);
                GlobalBus.send(2131624245, null);
            }
            StickersManager.updatePaymentEndDate(this.context, stickersResponse.expirationDeltaMs);
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }
}
