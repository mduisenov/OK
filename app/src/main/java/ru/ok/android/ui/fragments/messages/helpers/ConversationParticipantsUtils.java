package ru.ok.android.ui.fragments.messages.helpers;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.proto.ConversationProto.Participant;
import ru.ok.model.UserInfo;

public final class ConversationParticipantsUtils {
    public static int computeNonCurrentUsers(List<UserInfo> users) {
        return users.size() - (hasCurrentUser(users) ? 1 : 0);
    }

    public static UserInfo findNonCurrentUser(Collection<UserInfo> users) {
        String currentUid = OdnoklassnikiApplication.getCurrentUser().uid;
        for (UserInfo user : users) {
            if (!TextUtils.equals(currentUid, user.uid)) {
                return user;
            }
        }
        return null;
    }

    public static String findNonCurrentUserIdProto(List<Participant> participants) {
        String currentUid = OdnoklassnikiApplication.getCurrentUser().uid;
        for (Participant participant : participants) {
            String id = participant.getId();
            if (!TextUtils.equals(currentUid, id)) {
                return id;
            }
        }
        return null;
    }

    private static boolean hasCurrentUser(List<UserInfo> users) {
        String currentUid = OdnoklassnikiApplication.getCurrentUser().uid;
        for (UserInfo user : users) {
            if (TextUtils.equals(currentUid, user.uid)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String> toIdsWithoutCurrentProto(List<Participant> participants) {
        ArrayList<String> result = new ArrayList();
        if (participants != null) {
            String currentId = OdnoklassnikiApplication.getCurrentUser().uid;
            for (Participant participant : participants) {
                String id = participant.getId();
                if (!TextUtils.equals(currentId, id)) {
                    result.add(id);
                }
            }
        }
        return result;
    }

    public static UserInfo findUserById(List<UserInfo> users, String id) {
        if (users != null) {
            for (UserInfo user : users) {
                if (TextUtils.equals(user.uid, id)) {
                    return user;
                }
            }
        }
        return null;
    }
}
