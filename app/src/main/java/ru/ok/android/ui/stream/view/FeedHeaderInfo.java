package ru.ok.android.ui.stream.view;

import android.text.Spannable;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.model.GeneralUserInfo;

public final class FeedHeaderInfo {
    public final ArrayList<GeneralUserInfo> avatars;
    public String dateFormatted;
    public final FeedWithState feed;
    public boolean isPromo;
    public final boolean isUsersAreLikeAuthors;
    public Spannable message;
    public ArrayList<GeneralUserInfo> referencedUsers;

    public FeedHeaderInfo(FeedWithState feed, boolean isUsersAreLikeAuthors) {
        this.avatars = new ArrayList(4);
        this.feed = feed;
        this.isUsersAreLikeAuthors = isUsersAreLikeAuthors;
    }

    public void addAvatar(List<GeneralUserInfo> userInfo) {
        this.avatars.addAll(userInfo);
    }

    public void setDateFormatted(String dateFormatted) {
        this.dateFormatted = dateFormatted;
    }

    public void setMessage(Spannable message) {
        this.message = message;
    }

    public void setReferencedUsers(ArrayList<GeneralUserInfo> referencedUsers) {
        this.referencedUsers = referencedUsers;
    }

    public void setIsPromo(boolean isPromo) {
        this.isPromo = isPromo;
    }
}
