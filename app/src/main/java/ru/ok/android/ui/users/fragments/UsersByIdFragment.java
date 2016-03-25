package ru.ok.android.ui.users.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import java.util.ArrayList;
import java.util.List;
import org.solovyev.android.views.llm.LinearLayoutManager;
import ru.ok.android.db.provider.OdklContract.Users;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.statistics.stream.StreamStats;
import ru.ok.android.ui.adapters.friends.UserInfosController.SelectionsMode;
import ru.ok.android.ui.adapters.friends.UsersInfoCursorAdapter;
import ru.ok.android.ui.adapters.friends.UsersInfoCursorAdapter.UserInfoItemClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView.LocalState;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.utils.EmptyViewRecyclerDataObserver;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.model.UserInfo;

public class UsersByIdFragment extends BaseFragment implements LoaderCallbacks<Cursor>, UserInfoItemClickListener {
    private UsersInfoCursorAdapter adapter;
    private SmartEmptyView emptyView;
    private RecyclerView list;

    public static UsersByIdFragment newInstanceCommonFriends(ArrayList<String> userIds, int titleResourceId) {
        return newInstance(userIds, titleResourceId, false);
    }

    public static UsersByIdFragment newInstanceCommonFriends(ArrayList<String> userIds, int titleResourceId, String streamStatSource) {
        return newInstance(userIds, titleResourceId, false, streamStatSource);
    }

    public static UsersByIdFragment newInstance(ArrayList<String> userIds, int titleResourceId, boolean dotsEnabled) {
        return newInstance(userIds, titleResourceId, dotsEnabled, null);
    }

    public static UsersByIdFragment newInstance(ArrayList<String> userIds, int titleResourceId, boolean dotsEnabled, String streamStatSource) {
        Bundle args = new Bundle();
        fillArguments(args, userIds, titleResourceId, dotsEnabled, streamStatSource);
        UsersByIdFragment result = new UsersByIdFragment();
        result.setArguments(args);
        return result;
    }

    protected static void fillArguments(Bundle args, ArrayList<String> userIds, int titleResourceId, boolean dotsEnabled, String streamStatSource) {
        args.putStringArrayList("USER_IDS", userIds);
        args.putInt("TITLE_ID", titleResourceId);
        args.putBoolean("DOTS_ENABLED", dotsEnabled);
        args.putString("STREAM_STAT_SOURCE", streamStatSource);
    }

    private List<String> getUserIds() {
        return getArguments().getStringArrayList("USER_IDS");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        NavigationHelper.showUserInfo(getActivity(), data.getStringExtra("USER_ID"));
    }

    private int getTitleId() {
        return getArguments().getInt("TITLE_ID");
    }

    private boolean isDotsEnabled() {
        return getArguments().getBoolean("DOTS_ENABLED");
    }

    private String getStreamStatSource() {
        return getArguments().getString("STREAM_STAT_SOURCE");
    }

    public View createView(ViewGroup container) {
        View view = LayoutInflater.from(getContext()).inflate(getLayoutId(), container);
        this.adapter = new UsersInfoCursorAdapter(getActivity(), null, false, SelectionsMode.SINGLE, null, null, null, null, isDotsEnabled(), false, true);
        this.list = (RecyclerView) view.findViewById(2131624731);
        this.list.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        this.list.setAdapter(this.adapter);
        this.adapter.setUserInfoItemClickListener(this);
        this.emptyView = (SmartEmptyView) view.findViewById(C0263R.id.empty_view);
        this.adapter.registerAdapterDataObserver(new EmptyViewRecyclerDataObserver(this.emptyView, this.adapter));
        getLoaderManager().initLoader(0, null, this);
        return view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Builder(getActivity()).title(getStringLocalized(getTitleId())).customView(createView(null), false).build();
    }

    protected int getLayoutId() {
        return 2130903369;
    }

    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        return new CursorLoader(getActivity(), Users.getContentUri(), null, "user_id IN ('" + TextUtils.join("','", getUserIds()) + "')", null, "user_n_first_name, user_n_last_name");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        boolean isEmpty = true;
        int i = 0;
        String str = "count=%d";
        Object[] objArr = new Object[1];
        objArr[0] = Integer.valueOf(cursor == null ? 0 : cursor.getCount());
        Logger.m173d(str, objArr);
        this.adapter.swapCursor(cursor);
        if (this.adapter.getItemCount() != 0) {
            isEmpty = false;
        }
        this.emptyView.setLocalState(isEmpty ? LocalState.EMPTY : LocalState.HAS_DATA);
        SmartEmptyView smartEmptyView = this.emptyView;
        if (!isEmpty) {
            i = 8;
        }
        smartEmptyView.setVisibility(i);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        Logger.m173d("[%s]", getClass().getSimpleName());
        this.adapter.swapCursor(null);
    }

    private void processUserInfoClick(UserInfo user) {
        String uid = user.uid;
        Intent data = new Intent();
        data.putExtra("USER_ID", uid);
        Fragment fragment = getTargetFragment();
        Activity activity = getActivity();
        if (fragment == null && activity != null) {
            NavigationHelper.showUserInfo(activity, uid);
        } else if (fragment != null) {
            fragment.onActivityResult(getTargetRequestCode(), -1, data);
        }
        String streamStatSource = getStreamStatSource();
        if (streamStatSource != null) {
            StreamStats.clickUser(streamStatSource);
        }
    }

    public void onUserItemClick(View view, int position, UserInfo user) {
        if (user != null) {
            processUserInfoClick(user);
        }
    }
}
