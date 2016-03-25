package ru.ok.android.emoji;

import ru.ok.android.emoji.stickers.StickersSet;

public interface EmojiClickListener {
    void onEmojiClicked(long j);

    void onStickerClicked(StickersSet stickersSet, String str);
}
