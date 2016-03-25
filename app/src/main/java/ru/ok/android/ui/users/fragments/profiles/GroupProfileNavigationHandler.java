package ru.ok.android.ui.users.fragments.profiles;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.groups.data.GroupSectionItem;
import ru.ok.android.ui.users.fragments.data.ProfileSectionsAdapter;
import ru.ok.android.ui.users.fragments.profiles.statistics.GroupsProfileStatisticsManager;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.java.api.exceptions.NotSessionKeyException;
import ru.ok.java.api.response.groups.GroupCounters;

public class GroupProfileNavigationHandler implements OnItemClickListener {
    private final Activity activity;
    private String groupId;
    private ProfileSectionsAdapter<GroupSectionItem, GroupCounters> sectionsAdapter;

    /* renamed from: ru.ok.android.ui.users.fragments.profiles.GroupProfileNavigationHandler.1 */
    static /* synthetic */ class C13371 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$ui$groups$data$GroupSectionItem;

        static {
            $SwitchMap$ru$ok$android$ui$groups$data$GroupSectionItem = new int[GroupSectionItem.values().length];
            try {
                $SwitchMap$ru$ok$android$ui$groups$data$GroupSectionItem[GroupSectionItem.THEMES.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$groups$data$GroupSectionItem[GroupSectionItem.PHOTO_ALBUMS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$groups$data$GroupSectionItem[GroupSectionItem.LINKS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    GroupProfileNavigationHandler(Activity activity) {
        this.activity = activity;
        this.sectionsAdapter = new ProfileSectionsAdapter(activity);
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    protected String getGroupId() {
        return this.groupId;
    }

    public ProfileSectionsAdapter<GroupSectionItem, GroupCounters> getSectionsAdapter() {
        return this.sectionsAdapter;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (this.sectionsAdapter.getData() != null && this.sectionsAdapter.getData().size() > position) {
            onSectionClicked((GroupSectionItem) this.sectionsAdapter.getData().get(position));
        }
    }

    private void onSectionClicked(GroupSectionItem item) {
        switch (C13371.$SwitchMap$ru$ok$android$ui$groups$data$GroupSectionItem[item.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                NavigationHelper.showGroupTopics(this.activity, getGroupId());
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                NavigationHelper.showGroupPhotoAlbums(this.activity, getGroupId());
                GroupsProfileStatisticsManager.sendStatEvent(GroupsProfileStatisticsManager.SECTION_PHOTO_ALBUMS);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                showLinks();
                GroupsProfileStatisticsManager.sendStatEvent(GroupsProfileStatisticsManager.SECTION_LINKS);
            default:
                NavigationHelper.showExternalUrlPage(this.activity, WebUrlCreator.getUrl(item.getMethodName(), getGroupId(), null), false);
        }
    }

    private void showLinks() {
        try {
            NavigationHelper.showExternalUrlPage(this.activity, WebUrlCreator.getGroupLinksPageUrl(getGroupId(), true), false);
        } catch (NotSessionKeyException e) {
            Logger.m172d("no session key error");
        }
    }
}
