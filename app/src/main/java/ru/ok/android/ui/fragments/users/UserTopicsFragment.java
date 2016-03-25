package ru.ok.android.ui.fragments.users;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.mediatopics.MediaTopicsListFragment;
import ru.ok.android.ui.mediatopics.MediaTopicsTabFragment;
import ru.ok.android.ui.mediatopics.MediaTopicsTabFragment.FilterPage;
import ru.ok.android.ui.mediatopics.MediaTopicsTabFragmentWithComposer;
import ru.ok.android.ui.users.fragments.UserTopicsListFragment;
import ru.ok.android.utils.bus.BusUsersHelper;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.UserInfo;

public class UserTopicsFragment extends MediaTopicsTabFragmentWithComposer {
    private static List<FilterPage> PAGES;
    private String userName;

    /* renamed from: ru.ok.android.ui.fragments.users.UserTopicsFragment.1 */
    class C09071 implements OnClickListener {
        C09071() {
        }

        public void onClick(View v) {
            UserTopicsFragment.this.onWriteNoteClicked();
        }
    }

    public static Bundle newArguments(String userId, String selectedFilter) {
        return MediaTopicsTabFragment.newArguments(null, userId, selectedFilter);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.userName = savedInstanceState.getString("key_username");
            if (this.userName != null) {
                processUserName();
            }
        }
        if (!OdnoklassnikiApplication.getCurrentUser().getId().equals(this.userId) && this.userName == null) {
            BusUsersHelper.getUserInfos(Collections.singletonList(this.userId), false);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        updateMediaPostPanel(view);
        return view;
    }

    protected CharSequence getTitle() {
        return getStringLocalized(2131166799);
    }

    protected CharSequence getSubtitle() {
        return this.userName;
    }

    static {
        PAGES = Arrays.asList(new FilterPage[]{new FilterPage("USER_ALL", 2131166800), new FilterPage("USER_SHARES", 2131166802), new FilterPage("USER_WITH", 2131166803), new FilterPage("USER_GAMES", 2131166801)});
    }

    protected List<FilterPage> getPages() {
        return PAGES;
    }

    protected MediaTopicsListFragment getPageFragment(FilterPage filterPage) {
        MediaTopicsListFragment fragment = new UserTopicsListFragment();
        fragment.setArguments(MediaTopicsListFragment.newArguments(this.userId, null, filterPage.filter));
        return fragment;
    }

    protected void initFab(FloatingActionButton fab) {
        fab.setOnClickListener(new C09071());
    }

    protected boolean isMediaPostPanelRequired() {
        return OdnoklassnikiApplication.getCurrentUser().getId().equals(this.userId);
    }

    public void onWriteNoteClicked() {
        Intent createTopic = new Intent();
        createTopic.setClassName(getContext(), "ru.ok.android.ui.activity.MediaComposerUserActivity");
        startActivity(createTopic);
        MediaComposerStats.open("hidepanel/" + getClass().getSimpleName(), MediaTopicType.USER);
    }

    @Subscribe(on = 2131623946, to = 2131624227)
    public void onUserTopicLoad(BusEvent event) {
        if (isVisible() && event.bundleInput != null) {
            String topicLoadUserId = event.bundleInput.getString("user_id");
            if (!TextUtils.isEmpty(topicLoadUserId) && topicLoadUserId.equals(this.userId)) {
                refresh();
                selectFilterAllPage();
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624221)
    public void onUserInfo(BusEvent e) {
        List<String> userIds = e.bundleInput.getStringArrayList("USER_IDS");
        if (userIds != null && userIds.contains(this.userId) && e.resultCode == -1) {
            ArrayList<UserInfo> userInfos = e.bundleOutput.getParcelableArrayList("USERS");
            if (userInfos != null && userInfos.size() > 0) {
                Iterator i$ = userInfos.iterator();
                while (i$.hasNext()) {
                    UserInfo userInfo = (UserInfo) i$.next();
                    if (this.userId.equals(userInfo.getId())) {
                        this.userName = userInfo.name;
                        processUserName();
                        return;
                    }
                }
            }
        }
    }

    private void processUserName() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(getSubtitle());
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key_username", this.userName);
    }
}
