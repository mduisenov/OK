package ru.ok.android.emoji;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ru.ok.android.emoji.EmojiCache.ImageType;
import ru.ok.android.emoji.EmojiPanelView.EmojiViewListener;
import ru.ok.android.emoji.recents.SmileRecents;
import ru.ok.android.emoji.smiles.SmileTextProcessor;
import ru.ok.android.emoji.stickers.StickerQuickItem;
import ru.ok.android.emoji.stickers.StickerSetListener;
import ru.ok.android.emoji.stickers.StickersSet;

public final class EmojiViewController implements EmojiClickListener, EmojiViewListener {
    private static final String TAG;
    private final Context context;
    private final EditText editText;
    private final EmojiControllerListener listener;
    private SmileRecents recents;
    private Set<StickerSetListener> stickersListeners;
    private List<StickersSet> stickersSets;

    public interface EmojiControllerListener {
        void hideStickersPanel();

        void onQuickStickerClicked(String str, String str2);

        void onShowStickersTab();

        void onStickerClicked(int i, String str);

        void onStickerSetSelected(StickersSet stickersSet);

        void onStickersPageSelected();
    }

    static {
        TAG = EmojiViewController.class.getName();
    }

    public EmojiViewController(Context context, EditText editText, List<StickersSet> stickersSets, EmojiControllerListener listener) {
        this.stickersListeners = new HashSet();
        this.context = context;
        this.editText = editText;
        this.listener = listener;
        this.stickersSets = stickersSets;
    }

    public void onEmojiClicked(long smile) {
        Log.d(TAG, String.format("onEmojiClicked(%d)", new Object[]{Long.valueOf(smile)}));
        if (this.editText != null) {
            this.editText.performHapticFeedback(3);
            processSmileClick(this.editText, smile);
        }
        getRecents().addRecent(smile);
    }

    public void onStickerClicked(StickersSet set, String code) {
        Log.d(TAG, String.format("(%d, %s)", new Object[]{Integer.valueOf(set.id), code}));
        this.editText.performHapticFeedback(3);
        getRecents().addRecentSticker(set.id, code);
        if (this.listener != null) {
            this.listener.onStickerClicked(set.id, SmileTextProcessor.buildStickerText(code, set.width, set.height));
        }
    }

    public void onQuickStickerClicked(StickerQuickItem sticker) {
        Log.d(TAG, String.format("(%s)", new Object[]{sticker.code}));
        this.editText.performHapticFeedback(3);
        if (this.listener != null) {
            this.listener.onQuickStickerClicked(sticker.code, SmileTextProcessor.buildStickerText(sticker.code, sticker.width, sticker.height));
        }
    }

    public void onStickerPageSelected() {
        if (this.listener != null) {
            this.listener.onStickersPageSelected();
        }
    }

    public void onStickerSetSelected(StickersSet set) {
        if (this.listener != null) {
            this.listener.onStickerSetSelected(set);
        }
    }

    public void showStickersTab() {
        if (this.listener != null) {
            this.listener.onShowStickersTab();
        }
    }

    public void hideStickersPanel() {
        if (this.listener != null) {
            this.listener.hideStickersPanel();
        }
    }

    public boolean onBackspace() {
        if (this.editText == null || this.editText.length() == 0) {
            return false;
        }
        this.editText.dispatchKeyEvent(new KeyEvent(0, 67));
        this.editText.performHapticFeedback(3);
        return true;
    }

    private static void processSmileClick(EditText editText, long smile) {
        SpannableStringBuilder sb;
        CharSequence smileText = Emoji.code2String(smile);
        Editable currentText = editText.getText();
        if (currentText instanceof SpannableStringBuilder) {
            sb = (SpannableStringBuilder) currentText;
        } else {
            sb = new SpannableStringBuilder(currentText);
            editText.setText(sb);
        }
        boolean addSpace = Emoji.isOkSmile(smile);
        CharSequence insertingText = smileText;
        if (addSpace) {
            insertingText = insertingText + " ";
        }
        int cursor = Math.max(0, editText.getSelectionEnd());
        int lengthBefore = editText.length();
        sb.insert(cursor, insertingText);
        if (editText.length() >= insertingText.length() + lengthBefore) {
            Drawable drawable = EmojiCache.instance(editText.getContext()).getDrawable(smile, ImageType.TEXT);
            if (drawable != null) {
                sb.setSpan(new ImageSpan(drawable), cursor, smileText.length() + cursor, 33);
            }
            editText.setSelection((addSpace ? 1 : 0) + (cursor + smileText.length()));
        }
    }

    public SmileRecents getRecents() {
        if (this.recents == null) {
            this.recents = new SmileRecents(this.context, this);
            this.stickersListeners.add(this.recents);
        }
        return this.recents;
    }

    public List<StickersSet> getStickersSets() {
        return this.stickersSets;
    }

    public void updateStickers(List<StickersSet> stickers) {
        this.stickersSets = stickers;
        for (StickerSetListener listener : this.stickersListeners) {
            listener.onStickersSetChanged();
        }
    }

    public void addStickersSetsListener(StickerSetListener listener) {
        this.stickersListeners.add(listener);
    }
}
