package ru.ok.android.ui.messaging.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import ru.ok.android.emoji.EmojiPanelView;
import ru.ok.android.emoji.EmojiViewController;
import ru.ok.android.emoji.EmojiViewController.EmojiControllerListener;
import ru.ok.android.emoji.PanelLayoutController;
import ru.ok.android.emoji.PanelLayoutController.PanelViewControllerListener;
import ru.ok.android.emoji.container.RelativePanelLayout;
import ru.ok.android.emoji.stickers.StickersSet;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.model.messages.MessageBase;

public final class MessageEditActivity extends BaseActivity implements TextWatcher, OnCheckedChangeListener, EmojiControllerListener, PanelViewControllerListener {
    private CheckBox emojiCheckbox;
    private EmojiPanelView emojiPanelView;
    @Nullable
    private String initialText;
    private PanelLayoutController popupController;
    private MenuItem sendItem;
    private EditText text;

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        Logger.m173d("message: %s", getMessage());
        setContentView(2130903081);
        this.text = (EditText) findViewById(2131624538);
        this.text.addTextChangedListener(this);
        this.emojiCheckbox = (CheckBox) findViewById(2131624539);
        this.emojiCheckbox.setOnCheckedChangeListener(this);
        RelativePanelLayout root = (RelativePanelLayout) findViewById(2131624537);
        this.popupController = new PanelLayoutController(this, root, this.text, this);
        root.setSizeListener(this.popupController);
        int maxLength = ServicesSettingsHelper.getServicesSettings().getMultichatMaxTextLength();
        this.text.setFilters(new InputFilter[]{new LengthFilter(maxLength)});
        this.initialText = message.message.getActualText();
        if (this.initialText != null) {
            this.initialText = this.initialText.trim();
        }
        if (savedInstanceState == null) {
            this.text.setText(this.initialText);
            this.text.setSelection(this.text.length());
        }
        setTitle(getStringLocalized(getIntent().getIntExtra("title_id", 0)));
        if (this.popupController.onRestoreInstanceState(savedInstanceState)) {
            KeyBoardUtils.hideKeyBoard(this);
            return;
        }
        this.text.requestFocus();
        KeyBoardUtils.showKeyBoard(this.text);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        LocalizationManager.inflate((Context) this, getMenuInflater(), 2131689507, menu);
        this.sendItem = menu.findItem(2131624519);
        updateSendItemVisibility();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131624519:
                sendNewText(this.text.getText().toString().trim());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendNewText(String newText) {
        Logger.m173d("New message text: '%s' for message: %s", newText, getMessage());
        Intent data = new Intent();
        data.putExtra(Stanza.TEXT, newText);
        data.putExtra(Message.ELEMENT, getMessage());
        setResult(-1, data);
        finish();
    }

    private OfflineMessage<? extends MessageBase> getMessage() {
        Intent intent = getIntent();
        return intent != null ? (OfflineMessage) intent.getParcelableExtra(Message.ELEMENT) : null;
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateSendItemVisibility();
    }

    public void afterTextChanged(Editable s) {
    }

    public void onPanelViewVisibilityChanged(boolean isVisible) {
        this.emojiCheckbox.setChecked(isVisible);
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            this.popupController.showPanelView(getEmojiPanelView());
        } else {
            this.popupController.hidePanelViewOpenKeyboard();
        }
    }

    private EmojiPanelView getEmojiPanelView() {
        if (this.emojiPanelView == null) {
            this.emojiPanelView = new EmojiPanelView(this, new EmojiViewController(this, this.text, null, this), false);
            this.popupController.addPanelView(this.emojiPanelView);
        }
        return this.emojiPanelView;
    }

    private void updateSendItemVisibility() {
        if (this.sendItem != null) {
            String trimmedText = this.text.getText().toString().trim();
            MenuItem menuItem = this.sendItem;
            boolean z = trimmedText.length() > 0 && !TextUtils.equals(trimmedText, this.initialText);
            menuItem.setEnabled(z);
        }
    }

    protected void onPause() {
        super.onPause();
        this.popupController.onPause();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.popupController.onSaveInstanceState(outState);
    }

    public void onBackPressed() {
        if (!this.popupController.hidePanelView()) {
            super.onBackPressed();
        }
    }

    public void onStickerClicked(int setId, String stickerText) {
        sendNewText(stickerText);
    }

    public void onQuickStickerClicked(String code, String stickerText) {
    }

    public void onStickersPageSelected() {
    }

    public void onStickerSetSelected(StickersSet set) {
    }

    public void onShowStickersTab() {
    }

    public void hideStickersPanel() {
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
