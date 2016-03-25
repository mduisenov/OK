package ru.ok.android.ui.fragments.messages;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView.LocalState;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.fragments.messages.adapter.ParticipantsAdapter;
import ru.ok.android.ui.fragments.messages.adapter.ParticipantsAdapter.ParticipantsAdapterListener;
import ru.ok.android.ui.fragments.messages.helpers.ConversationParticipantsUtils;
import ru.ok.android.ui.fragments.messages.loaders.ConversationLoader;
import ru.ok.android.ui.users.UserDisabledSelectionParams;
import ru.ok.android.ui.users.UsersSelectionParams;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.bus.BusMessagingHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.model.UserInfo;

public final class ConversationParticipantsFragment extends BaseFragment implements LoaderCallbacks<Pair<Conversation, List<UserInfo>>>, ParticipantsAdapterListener {
    private ParticipantsAdapter adapter;
    private SmartEmptyView emptyView;

    public static Bundle newArguments(String conversationId) {
        Bundle args = new Bundle();
        args.putString("conversation_id", conversationId);
        args.putBoolean("fragment_is_dialog", true);
        return args;
    }

    private String getConversationId() {
        return getArguments().getString("conversation_id");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LocalizationManager.inflate(getActivity(), getLayoutId(), container, false);
        RecyclerView list = (RecyclerView) view.findViewById(2131624731);
        Adapter participantsAdapter = new ParticipantsAdapter(getContext(), this);
        this.adapter = participantsAdapter;
        list.setAdapter(participantsAdapter);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        this.emptyView = (SmartEmptyView) view.findViewById(C0263R.id.empty_view);
        this.emptyView.setEmptyText(2131166263);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(0, null, this);
        return view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Builder(getActivity()).title(getStringLocalized(2131165635)).build();
    }

    protected int getLayoutId() {
        return 2130903369;
    }

    public void onResume() {
        super.onResume();
        BusMessagingHelper.updateConversation(getConversationId());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflateMenuLocalized(2131689479, menu);
    }

    public void onUserClicked(UserInfo user) {
        Logger.m173d("User selected: %s", user);
        NavigationHelper.showUserInfo(getActivity(), user.uid);
    }

    public void onKickUser(UserInfo user) {
        StatisticManager.getInstance().addStatisticEvent("multichat-kick-user", new Pair("place", "participants"));
        BusMessagingHelper.kickUser(getConversationId(), user.uid);
    }

    @Subscribe(on = 2131623946, to = 2131624132)
    public void onUserKicked(BusEvent event) {
        if (isNeedToShowResult(event)) {
            int textId;
            if (event.resultCode == -2) {
                textId = 2131166030;
            } else {
                textId = 2131166031;
            }
            Toast.makeText(getActivity(), getStringLocalized(textId), 0).show();
        }
    }

    private boolean isNeedToShowResult(BusEvent event) {
        if (getActivity() == null || isHidden() || !isResumed()) {
            return false;
        }
        return TextUtils.equals(event.bundleInput.getString("CONVERSATION_ID"), getConversationId());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131624926:
                Toast.makeText(getActivity(), getStringLocalized(2131166019), 1).show();
                ArrayList<String> selectedIds = ConversationParticipantsUtils.toIdsWithoutCurrentProto(this.adapter.getParticipants());
                NavigationHelper.selectFriendsFiltered(this, new UserDisabledSelectionParams(selectedIds, selectedIds, ServicesSettingsHelper.getServicesSettings().getMultichatMaxParticipantsCount()), 2, 0);
                StatisticManager.getInstance().addStatisticEvent("multichat-invite", new Pair("place", "participants"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RECEIVED_VALUE:
                if (resultCode == -1) {
                    ArrayList<String> disabledIds;
                    UsersSelectionParams params = (UsersSelectionParams) data.getParcelableExtra("selection_params");
                    ArrayList<String> selectedIds = data.getStringArrayListExtra("selected_ids");
                    if (params instanceof UserDisabledSelectionParams) {
                        disabledIds = ((UserDisabledSelectionParams) params).getDisabledIds(null);
                    } else {
                        disabledIds = new ArrayList();
                    }
                    if (!(params == null || selectedIds == null)) {
                        selectedIds.removeAll(disabledIds);
                        if (selectedIds.size() > 0) {
                            BusMessagingHelper.addParticipants(getConversationId(), selectedIds);
                            break;
                        }
                    }
                }
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (resultCode == -1) {
                    NavigationHelper.showUserInfo(getActivity(), data.getStringExtra("USER_ID"));
                    break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe(on = 2131623946, to = 2131624130)
    public void onParticipantsAdded(BusEvent event) {
        if (!isNeedToShowResult(event)) {
            return;
        }
        if (event.resultCode == -1) {
            ArrayList<String> blockedUids = event.bundleOutput.getStringArrayList("BLOCKED_USER_IDS");
            if (!blockedUids.isEmpty()) {
                UsersBlockedFragment fragment = UsersBlockedFragment.newInstance(blockedUids);
                fragment.setTargetFragment(this, 1);
                fragment.show(getFragmentManager(), "blocked-users");
                return;
            }
            return;
        }
        ErrorType error = ErrorType.from(event.bundleOutput);
        if (error != ErrorType.GENERAL) {
            showTimedToastIfVisible(error.getDefaultErrorMessage(), 0);
        }
    }

    public Loader<Pair<Conversation, List<UserInfo>>> onCreateLoader(int id, Bundle args) {
        return new ConversationLoader(getContext(), getConversationId());
    }

    public void onLoadFinished(Loader<Pair<Conversation, List<UserInfo>>> loader, Pair<Conversation, List<UserInfo>> data) {
        this.emptyView.setLocalState(LocalState.EMPTY);
        if (data != null) {
            this.adapter.setParticipants((Conversation) data.first, (List) data.second);
        }
        this.emptyView.setVisibility(this.adapter.getItemCount() > 0 ? 8 : 0);
    }

    public void onLoaderReset(Loader<Pair<Conversation, List<UserInfo>>> loader) {
    }

    protected CharSequence getTitle() {
        return getStringLocalized(2131166189);
    }
}
