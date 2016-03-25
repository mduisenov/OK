package ru.ok.model.stickers;

import android.support.annotation.NonNull;
import java.util.List;

public final class StickerSet {
    public final int height;
    @NonNull
    public final String iconUrl;
    public final int id;
    @NonNull
    public final String name;
    public final int price;
    public final int sinceVersion;
    public final List<Sticker> stickers;
    public final int width;

    public StickerSet(int id, String name, String iconUrl, int price, int sinceVersion, int width, int height, List<Sticker> stickers) {
        this.id = id;
        if (name == null) {
            name = "";
        }
        this.name = name;
        if (iconUrl == null) {
            iconUrl = "";
        }
        this.iconUrl = iconUrl;
        this.price = price;
        this.sinceVersion = sinceVersion;
        this.width = width;
        this.height = height;
        this.stickers = stickers;
    }
}
