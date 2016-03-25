package ru.ok.android.ui.fragments.messages.helpers;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import java.util.List;
import ru.ok.android.proto.ConversationProto.Capabilities;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.ConversationProto.Conversation.Type;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.model.UserInfo;

public final class MenuItemsVisibilityHelper {
    private final MenuItem addParticipantsItem;
    private final MenuItem call;
    private final MenuItem createShortcut;
    private final MenuItem deleteChat;
    private final MenuItem deleteMessages;
    private final MenuItem editMessages;
    private final MenuItem leaveChat;
    private final MenuItem participantsItem;

    public MenuItemsVisibilityHelper(Menu menu) {
        this.addParticipantsItem = menu.findItem(2131624926);
        this.participantsItem = menu.findItem(2131624879);
        this.deleteChat = menu.findItem(2131625486);
        this.deleteMessages = menu.findItem(2131625485);
        this.leaveChat = menu.findItem(2131625487);
        this.editMessages = menu.findItem(2131625485);
        this.call = menu.findItem(2131625261);
        this.createShortcut = menu.findItem(2131625488);
    }

    public void updateVisibility(Context context, Conversation conversation, List<UserInfo> users) {
        boolean z = true;
        if (conversation != null) {
            boolean isPrivate;
            boolean z2;
            Capabilities capabilities = conversation.getCapabilities();
            if (conversation.getType() != Type.CHAT) {
                isPrivate = true;
            } else {
                isPrivate = false;
            }
            MenuItem menuItem = this.addParticipantsItem;
            if (isPrivate || conversation.getParticipantsCount() < ServicesSettingsHelper.getServicesSettings().getMultichatMaxParticipantsCount()) {
                z2 = true;
            } else {
                z2 = false;
            }
            menuItem.setVisible(z2);
            menuItem = this.participantsItem;
            if (isPrivate) {
                z2 = false;
            } else {
                z2 = true;
            }
            menuItem.setVisible(z2);
            menuItem = this.participantsItem;
            String str = "%1$s %2$d";
            Object[] objArr = new Object[2];
            Object[] objArr2 = new Object[1];
            objArr2[0] = conversation != null ? Integer.valueOf(conversation.getParticipantsCount()) : "";
            objArr[0] = LocalizationManager.getString(context, 2131165635, objArr2);
            objArr[1] = Integer.valueOf(conversation.getParticipantsCount());
            menuItem.setTitle(String.format(str, objArr));
            this.deleteChat.setVisible(capabilities.getCanDelete());
            menuItem = this.leaveChat;
            if (isPrivate) {
                z2 = false;
            } else {
                z2 = true;
            }
            menuItem.setVisible(z2);
            this.editMessages.setVisible(true);
            this.deleteMessages.setVisible(isPrivate);
            UserInfo user = ConversationParticipantsUtils.findUserById(users, ConversationParticipantsUtils.findNonCurrentUserIdProto(conversation.getParticipantsList()));
            menuItem = this.call;
            if (isPrivate && Utils.userCanCall(user)) {
                z2 = true;
            } else {
                z2 = false;
            }
            menuItem.setVisible(z2);
            MenuItem menuItem2 = this.createShortcut;
            if (DeviceUtils.isSonyDevice()) {
                z = false;
            }
            menuItem2.setVisible(z);
        }
    }
}
