package ru.ok.android.services.processors.stickers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.emoji.EmojiPanelView;
import ru.ok.android.emoji.EmojiViewController;
import ru.ok.android.emoji.EmojiViewController.EmojiControllerListener;
import ru.ok.android.emoji.PanelLayoutController;
import ru.ok.android.emoji.PanelLayoutController.PanelViewControllerListener;
import ru.ok.android.emoji.PanelLayoutController.PanelViewPresenter;
import ru.ok.android.emoji.StickerPanelView;
import ru.ok.android.emoji.stickers.StickerQuickItem;
import ru.ok.android.emoji.stickers.StickersSet;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.messaging.activity.PayStickersActivity;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.Logger;
import ru.ok.model.messages.MessageBase;
import ru.ok.model.stickers.Sticker;

public final class StickersHelper implements OnCheckedChangeListener, EmojiControllerListener, PanelViewControllerListener {
    private final Activity activity;
    private final CheckBox checkBox;
    private boolean checkChangedAuto;
    private final EditText editText;
    private EmojiPanelView emojiPanelView;
    private EmojiViewController emojiViewController;
    private final StickerHelperListener listener;
    private boolean manualKeyboardState;
    private MessageBase pendingReplyToMessage;
    private String pendingStickerText;
    private PanelLayoutController popupController;
    private StickerPanelView stickerPanelView;

    public interface StickerHelperListener {
        void onSendText(String str, MessageBase messageBase);

        void onShowNewSet(boolean z);

        void startActivityForResult(Intent intent);
    }

    public StickersHelper(Activity activity, EditText editText, CheckBox checkBox, StickerHelperListener listener, PanelViewPresenter container, boolean manualKeyboardState) {
        this.activity = activity;
        this.editText = editText;
        this.checkBox = checkBox;
        this.listener = listener;
        this.manualKeyboardState = manualKeyboardState;
        checkBox.setOnCheckedChangeListener(this);
        this.popupController = new PanelLayoutController(activity, container, editText, this);
        container.setSizeListener(this.popupController);
    }

    @NonNull
    private EmojiViewController getEmojiViewController() {
        if (this.emojiViewController == null) {
            List<StickersSet> set = StickersManager.getCurrentSet4Lib(this.activity);
            this.emojiViewController = new EmojiViewController(this.activity, this.editText, set, this);
            this.listener.onShowNewSet(StickersSet.hasNew(set));
        }
        return this.emojiViewController;
    }

    private EmojiPanelView getEmojiPanelView() {
        if (this.emojiPanelView == null) {
            this.emojiPanelView = new EmojiPanelView(this.activity, getEmojiViewController(), StickersManager.isStickersEnabled(this.activity));
            this.popupController.addPanelView(this.emojiPanelView);
        }
        return this.emojiPanelView;
    }

    @NonNull
    private StickerPanelView getStickerPanelView() {
        if (this.stickerPanelView == null) {
            this.stickerPanelView = new StickerPanelView(this.activity, getEmojiViewController());
            this.popupController.addPanelView(this.stickerPanelView);
        }
        return this.stickerPanelView;
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (this.checkChangedAuto) {
            this.checkChangedAuto = false;
            return;
        }
        if (isChecked) {
            StatisticManager.getInstance().addStatisticEvent("emoji-panel-open", new Pair[0]);
            this.popupController.showPanelView(getEmojiPanelView());
            if (this.manualKeyboardState) {
                KeyBoardUtils.hideKeyBoard(this.activity);
            }
        } else {
            StatisticManager.getInstance().addStatisticEvent("emoji-panel-open", new Pair("reason", "checkbox"));
            if (this.manualKeyboardState) {
                this.popupController.hidePanelView();
                KeyBoardUtils.showKeyBoard(this.editText);
            } else {
                this.popupController.hidePanelViewOpenKeyboard();
            }
        }
        this.pendingReplyToMessage = null;
    }

    public void onStickerClicked(int setId, String stickerText) {
        if (StickersManager.isStickersEnabled(this.activity)) {
            boolean canSend;
            if (StickersManager.isSetFree(this.activity, setId) || StickersManager.isServicePaid(this.activity)) {
                canSend = true;
            } else {
                canSend = false;
            }
            if (canSend) {
                this.listener.onSendText(stickerText, null);
                return;
            }
            this.pendingStickerText = stickerText;
            this.listener.startActivityForResult(new Intent(this.activity, PayStickersActivity.class));
            StatisticManager.getInstance().addStatisticEvent("smile-stickers-payment-started", new Pair[0]);
            return;
        }
        Logger.m184w("Service disabled");
    }

    public void onQuickStickerClicked(String code, String stickerText) {
        boolean canSend = true;
        this.popupController.hidePanelView();
        if (this.pendingReplyToMessage == null) {
            Logger.m184w("pendingReplyToMessage is null");
            return;
        }
        Sticker found = null;
        for (Sticker sticker : this.pendingReplyToMessage.replyStickers) {
            if (TextUtils.equals(sticker.code, code)) {
                found = sticker;
                break;
            }
        }
        if (found == null) {
            Logger.m185w("Sticker with code %s not found in message: %s", code, this.pendingReplyToMessage);
            return;
        }
        StatisticManager.getInstance().addStatisticEvent("smile-quick-reply-clicked", new Pair[0]);
        if (found.price > 0 && !StickersManager.isServicePaid(this.activity)) {
            canSend = false;
        }
        if (canSend) {
            this.listener.onSendText(stickerText, this.pendingReplyToMessage);
            this.pendingReplyToMessage = null;
            return;
        }
        this.pendingStickerText = stickerText;
        StatisticManager.getInstance().addStatisticEvent("smile-stickers-payment-started", new Pair[0]);
        this.listener.startActivityForResult(new Intent(this.activity, PayStickersActivity.class));
    }

    public void onStickersPageSelected() {
        StickersManager.updateLastSeenVersion(this.activity);
    }

    public void onStickerSetSelected(StickersSet set) {
        if (this.emojiPanelView != null && set.isNew) {
            List<StickersSet> stickers = StickersManager.getCurrentSet4Lib(this.activity);
            this.emojiViewController.updateStickers(stickers);
            this.listener.onShowNewSet(StickersSet.hasNew(stickers));
        }
    }

    public void onShowStickersTab() {
        EmojiPanelView emojiPanelView = getEmojiPanelView();
        this.popupController.showPanelView(emojiPanelView);
        emojiPanelView.showStickersPage();
        this.pendingReplyToMessage = null;
    }

    public void hideStickersPanel() {
        this.popupController.hidePanelView();
        this.pendingReplyToMessage = null;
    }

    public void onPause() {
        if (this.popupController != null) {
            this.popupController.onPause();
        }
    }

    public void onPanelViewVisibilityChanged(boolean isVisible) {
        if (isVisible != this.checkBox.isChecked()) {
            this.checkChangedAuto = true;
        }
        this.checkBox.setChecked(isVisible);
    }

    public boolean onBackPressed() {
        if (this.popupController == null || !this.popupController.hidePanelView()) {
            return false;
        }
        StatisticManager.getInstance().addStatisticEvent("emoji-panel-hide", new Pair("reason", "back"));
        return true;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.pendingStickerText = savedInstanceState.getString("pending-sticker-text", null);
            this.pendingReplyToMessage = (MessageBase) savedInstanceState.getParcelable("pending-sticker-text");
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString("pending-sticker-text", this.pendingStickerText);
        outState.putParcelable("pending-reply-to-message", this.pendingReplyToMessage);
    }

    public void onActivityResult(int resultCode) {
        boolean success;
        if (resultCode == -1) {
            success = true;
        } else {
            success = false;
        }
        if (success) {
            this.listener.onSendText(this.pendingStickerText, this.pendingReplyToMessage);
        }
        StatisticManager instance = StatisticManager.getInstance();
        String str = "smile-stickers-payment-finished";
        Pair[] pairArr = new Pair[1];
        pairArr[0] = new Pair("success", success ? "true" : "false");
        instance.addStatisticEvent(str, pairArr);
        this.pendingStickerText = null;
        this.pendingReplyToMessage = null;
    }

    public void onStickersUpdated() {
        List<StickersSet> stickers = StickersManager.getCurrentSet4Lib(this.activity);
        if (this.emojiPanelView != null) {
            this.emojiViewController.updateStickers(stickers);
        }
        this.listener.onShowNewSet(StickersSet.hasNew(stickers));
    }

    public void onMessageStickersClicked(MessageBase message) {
        StatisticManager.getInstance().addStatisticEvent("smile-quick-reply-opened", new Pair[0]);
        Logger.m173d("messageId = %s, stickers = %s", message.id, message.replyStickers);
        this.pendingReplyToMessage = message;
        List<StickerQuickItem> stickers = new ArrayList();
        for (Sticker sticker : message.replyStickers) {
            stickers.add(new StickerQuickItem(sticker.code, sticker.width, sticker.height));
        }
        StickerPanelView stickerPanelView = getStickerPanelView();
        stickerPanelView.setStickers(stickers);
        this.popupController.showPanelView(stickerPanelView);
    }

    public void onConfigurationChanged() {
        if (this.popupController.isShowingPanelView()) {
            this.popupController.showPanelView();
        }
    }
}
