package ru.ok.android.ui.stream;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import java.util.Arrays;
import java.util.Collection;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.groups.data.GroupProfileInfo;
import ru.ok.android.ui.mediatopic.view.MediaComposerPanel;
import ru.ok.android.ui.stream.data.StreamContext;
import ru.ok.android.ui.users.fragments.profiles.ProfileGroupFragment;
import ru.ok.android.ui.users.fragments.profiles.ProfileGroupFragment.OnLeaveGroupListener;
import ru.ok.android.ui.users.fragments.profiles.ProfileLoadCallBack.ProfileAccessInfo;
import ru.ok.android.ui.users.fragments.profiles.ProfileLoadCallBack.ProfileType;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.GroupInfo;

public class StreamGroupFragment extends BaseProfilesStreamListFragment<ProfileGroupFragment> implements OnLeaveGroupListener {
    private boolean mediaPostPanelRequired;

    /* renamed from: ru.ok.android.ui.stream.StreamGroupFragment.1 */
    static class C12251 implements OnClickListener {
        final /* synthetic */ boolean val$canPostTopic;
        final /* synthetic */ boolean val$canSuggestTopic;
        final /* synthetic */ Context val$context;
        final /* synthetic */ String val$groupId;

        C12251(Context context, String str, boolean z, boolean z2) {
            this.val$context = context;
            this.val$groupId = str;
            this.val$canPostTopic = z;
            this.val$canSuggestTopic = z2;
        }

        public void onClick(View v) {
            StreamGroupFragment.createMediaTopic(this.val$context, this.val$groupId, this.val$canPostTopic, this.val$canSuggestTopic);
        }
    }

    public StreamGroupFragment() {
        this.mediaPostPanelRequired = false;
    }

    protected String getTitle() {
        if (this.profileFragment != null) {
            String groupName = ((ProfileGroupFragment) this.profileFragment).getGroupName();
            if (groupName != null) {
                return groupName;
            }
        }
        return "";
    }

    public static Bundle newArguments(String groupId) {
        Bundle args = new Bundle();
        String str = "GID";
        if (TextUtils.isEmpty(groupId)) {
            groupId = null;
        }
        args.putString(str, groupId);
        return args;
    }

    public static void initMedia(Context context, String groupId, boolean canPostTopic, boolean canSuggestTopic, MediaComposerPanel mediaComposerPanel) {
        if (mediaComposerPanel != null) {
            mediaComposerPanel.setMainImage(2130838038);
            mediaComposerPanel.setOnMainButtonClickListener(new C12251(context, groupId, canPostTopic, canSuggestTopic));
        }
    }

    public static void createMediaTopic(Context context, String groupId, boolean userCanPostTopic, boolean userCanSuggestTopic) {
        if (context != null && !TextUtils.isEmpty(groupId)) {
            Intent createTopic = new Intent();
            createTopic.setClassName(context, "ru.ok.android.ui.activity.MediaComposerGroupActivity");
            createTopic.putExtra("media_topic_gid", groupId);
            createTopic.putExtra("media_topic_group_user_can_post", userCanPostTopic);
            createTopic.putExtra("media_topic_group_user_can_suggest", userCanSuggestTopic);
            context.startActivity(createTopic);
            MediaComposerStats.open("profile", MediaTopicType.GROUP_THEME);
        }
    }

    protected int getLayoutId() {
        return 2130903412;
    }

    public String getGroupId() {
        return getArguments().getString("GID");
    }

    protected ProfileGroupFragment createProfileFragment() {
        return ProfileGroupFragment.newInstance(getGroupId());
    }

    protected void addProfileFragment(ProfileGroupFragment profileGroupFragment, int containerViewId) {
        profileGroupFragment.setLoadCallBack(this);
        profileGroupFragment.setOnLeaveGroupListener(this);
        super.addProfileFragment(profileGroupFragment, containerViewId);
    }

    public void onDestroyView() {
        super.onDestroyView();
        ((ProfileGroupFragment) this.profileFragment).removeViewChangeObserver(this.profileViewChangeObserver);
        ((ProfileGroupFragment) this.profileFragment).removeMeasureViewChangeObserver(this.profileViewMeasureObserver);
    }

    protected StreamContext createStreamContext() {
        return StreamContext.groupProfile(getGroupId());
    }

    protected boolean isMediaPostPanelRequired() {
        return this.mediaPostPanelRequired;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (this.profileFragment != null) {
            ((ProfileGroupFragment) this.profileFragment).onCreateOptionsMenu(menu, inflater);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        if (this.profileFragment != null) {
            ((ProfileGroupFragment) this.profileFragment).onPrepareOptionsMenu(menu);
        }
        super.onPrepareOptionsMenu(menu);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.profileFragment != null) {
            ((ProfileGroupFragment) this.profileFragment).onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.profileFragment == null || !((ProfileGroupFragment) this.profileFragment).onOptionsItemSelected(item)) {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void onGroupLeave() {
    }

    public void onProfileInfoLoad(ProfileType type, ProfileAccessInfo accessInfo) {
        super.onProfileInfoLoad(type, accessInfo);
        GroupProfileInfo groupProfileInfo = ((ProfileGroupFragment) this.profileFragment).getGroupProfileInfo();
        boolean canPostTopic = false;
        boolean canSuggestTopic = false;
        if (!(groupProfileInfo == null || groupProfileInfo.groupInfo == null)) {
            canPostTopic = groupProfileInfo.groupInfo.isCanPostMediaTopic();
            canSuggestTopic = groupProfileInfo.groupInfo.isCanSuggestMediaTopic();
        }
        boolean z = canPostTopic || canSuggestTopic;
        this.mediaPostPanelRequired = z;
        updateMediaPostPanel(getView());
        initMedia(getContext(), getGroupId(), canPostTopic, canSuggestTopic, this.mediaComposerHidePanel);
        ensureFab();
    }

    protected Collection<? extends GeneralUserInfo> getFilteredUsers() {
        new GroupInfo().setId(getGroupId());
        return Arrays.asList(new GroupInfo[]{group});
    }
}
