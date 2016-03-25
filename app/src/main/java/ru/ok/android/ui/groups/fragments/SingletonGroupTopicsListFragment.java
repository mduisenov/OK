package ru.ok.android.ui.groups.fragments;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import ru.ok.android.ui.groups.data.MediaTopicsListLoader;
import ru.ok.android.ui.groups.data.MediaTopicsListLoaderResult;
import ru.ok.android.utils.localization.LocalizationManager;

public class SingletonGroupTopicsListFragment extends GroupTopicsListFragment {
    private String topicId;

    public static Bundle newArguments(String groupId, String topicId) {
        Bundle bundle = new Bundle();
        bundle.putString("group_id", groupId);
        bundle.putString("topic_id", topicId);
        return bundle;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.topicId = getArguments().getString("topic_id");
        setTitle(getTitle());
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(123, getArguments(), this).forceLoad();
    }

    protected CharSequence getTitle() {
        return LocalizationManager.getString(getContext(), 2131165953);
    }

    public Loader<MediaTopicsListLoaderResult> onCreateLoader(int id, Bundle args) {
        return new MediaTopicsListLoader(getActivity(), this.groupId, this.topicId);
    }
}
