package ru.ok.model.stickers;

import java.util.List;

public final class StickersResponse {
    public final long expirationDeltaMs;
    public final List<StickerSet> sets;
    public final int version;

    public StickersResponse(int version, long expirationDeltaMs, List<StickerSet> sets) {
        this.version = version;
        this.expirationDeltaMs = expirationDeltaMs;
        this.sets = sets;
    }
}
