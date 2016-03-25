package ru.ok.android.ui.messaging.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.access.QueriesUsers.NameAvatarGender;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.emoji.smiles.SmileTextProcessor;
import ru.ok.android.model.cache.ram.MessageModel;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.onelog.AppLaunchLogHelper;
import ru.ok.android.services.messages.MessagesService;
import ru.ok.android.services.processors.stickers.StickersHelper;
import ru.ok.android.services.processors.stickers.StickersHelper.StickerHelperListener;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;
import ru.ok.android.ui.messaging.views.FrameEmojiQuickReplyLayout;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NotificationsUtils;
import ru.ok.android.utils.URLUtil;
import ru.ok.android.utils.bus.BusMessagingHelper;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase;
import ru.ok.model.messages.MessageBase.RepliedTo;

public final class MessageQuickReplyActivity extends Activity implements TextWatcher, OnClickListener, StickerHelperListener {
    private String authorId;
    private String authorName;
    private String conversationId;
    private CheckBox emojiCheckbox;
    private EditText message;
    private String messageId;
    private String messageText;
    private View sendButton;
    private StickersHelper stikersHelper;

    @NonNull
    public static Intent quickReplyIntent(Context context, String conversationId, String messageId, String message, String authorId, String authorName) {
        Intent intent = new Intent(context, MessageQuickReplyActivity.class);
        intent.addFlags(276856832);
        intent.putExtra("message_id", messageId);
        intent.putExtra("conversation_id", conversationId);
        intent.putExtra(Message.ELEMENT, message);
        intent.putExtra("sender_id", authorId);
        intent.putExtra("user-name", authorName);
        return intent;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLaunchLogHelper.logIntent(getIntent());
        setContentView(2130903082);
        AsyncDraweeView avatar = (AsyncDraweeView) findViewById(2131624657);
        TextView nameView = (TextView) findViewById(C0263R.id.name);
        TextView incomingMessage = (TextView) findViewById(2131624542);
        TextView date = (TextView) findViewById(2131624541);
        this.message = (EditText) findViewById(2131624538);
        this.message.addTextChangedListener(this);
        this.emojiCheckbox = (CheckBox) findViewById(2131624539);
        this.sendButton = findViewById(2131624543);
        this.sendButton.setOnClickListener(this);
        this.sendButton.setEnabled(false);
        Intent intent = getIntent();
        this.conversationId = intent.getStringExtra("conversation_id");
        this.messageId = intent.getStringExtra("message_id");
        this.messageText = intent.getStringExtra(Message.ELEMENT);
        this.authorId = intent.getStringExtra("sender_id");
        this.authorName = intent.getStringExtra("user-name");
        NotificationsUtils.hideNotificationForConversation(this, this.conversationId);
        SQLiteDatabase db = OdnoklassnikiApplication.getDatabase(this);
        findMessageDate(incomingMessage, date);
        findUserData(db, avatar, nameView);
        this.stikersHelper = new StickersHelper(this, this.message, this.emojiCheckbox, this, (FrameEmojiQuickReplyLayout) findViewById(2131624537), true);
        this.stikersHelper.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            startService(MessagesService.markAsReadIntent(this.conversationId, this.messageId, this, false));
            StatisticManager.getInstance().addStatisticEvent("quick-reply-shown", new Pair[0]);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RECEIVED_VALUE:
                this.stikersHelper.onActivityResult(resultCode);
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.stikersHelper.onSaveInstanceState(outState);
    }

    private void findUserData(SQLiteDatabase db, AsyncDraweeView avatar, TextView nameView) {
        Cursor cursor = db.rawQuery(NameAvatarGender.QUERY, new String[]{this.authorId});
        Uri uri = null;
        int emptyResourceId = 2130838321;
        try {
            if (cursor.moveToFirst()) {
                String name = cursor.getString(NameAvatarGender.INDEX_NAME);
                String picUrl = cursor.getString(NameAvatarGender.INDEX_PIC_URL);
                int gender = cursor.getInt(NameAvatarGender.INDEX_GENDER);
                if (!URLUtil.isStubUrl(picUrl)) {
                    uri = Uri.parse(picUrl);
                }
                if (gender == UserGenderType.FEMALE.toInteger()) {
                    emptyResourceId = 2130837927;
                }
                nameView.setText(name);
            } else {
                nameView.setText(this.authorName);
            }
            cursor.close();
            avatar.setEmptyImageResId(emptyResourceId);
            avatar.setUri(uri);
        } catch (Throwable th) {
            cursor.close();
        }
    }

    private void findMessageDate(TextView incomingMessage, TextView date) {
        MessageModel message = MessagesCache.getInstance().getMessageByServerId(this.conversationId, this.messageId);
        CharSequence incomingString = null;
        if (message != null) {
            incomingString = message.message.getText();
            date.setText(DateFormatter.formatHHmm(message.date));
        }
        if (TextUtils.isEmpty(incomingString)) {
            incomingString = this.messageText;
        }
        incomingMessage.setText(SmileTextProcessor.trimSmileSizes(incomingString));
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
        this.stikersHelper.onPause();
    }

    public void onClick(View v) {
        onSendText(this.message.getText().toString(), null);
    }

    public void onHeaderClicked(View view) {
        Logger.m172d("");
        NavigationHelper.showMessagesForConversation(this, this.conversationId, null);
        StatisticManager.getInstance().addStatisticEvent("quick-reply-header-clicked", new Pair[0]);
        finish();
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void afterTextChanged(Editable s) {
        this.sendButton.setEnabled(TextUtils.getTrimmedLength(s) > 0);
    }

    public void onBackPressed() {
        if (!this.stikersHelper.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public void onSendText(String text, MessageBase message) {
        RepliedTo repliedTo;
        if (message != null) {
            repliedTo = new RepliedTo(message.id, message.authorId, message.authorType);
        } else {
            repliedTo = null;
        }
        BusMessagingHelper.addMessage(this.conversationId, text, repliedTo, new MessageAuthor(OdnoklassnikiApplication.getCurrentUser().uid, null));
        StatisticManager.getInstance().addStatisticEvent("quick-reply-message-send", new Pair[0]);
        finish();
    }

    public void onShowNewSet(boolean hasNew) {
    }

    public void startActivityForResult(Intent intent) {
        startActivityForResult(intent, 0);
    }
}
