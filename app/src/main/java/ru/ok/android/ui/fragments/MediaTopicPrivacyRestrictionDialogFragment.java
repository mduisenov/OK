package ru.ok.android.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.ui.adapters.friends.UserInfosController.SelectionsMode;
import ru.ok.android.ui.adapters.friends.UsersInfoCursorAdapter;
import ru.ok.android.ui.dialogs.ConfirmationDialog;
import ru.ok.android.ui.dialogs.ConfirmationDialog.Builder;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo.UserGenderType;

public class MediaTopicPrivacyRestrictionDialogFragment extends ConfirmationDialog implements LoaderCallbacks<Cursor> {
    private static final String[] PROJECTION;
    private UsersInfoCursorAdapter adapter;
    private LocalizationManager localizationManager;
    private List<String> restrictedUids;

    private static class RestrictedFriendsLoader extends CursorLoader {
        public RestrictedFriendsLoader(Context context, List<String> restrictedUids) {
            super(context, OdklProvider.friendsUri(), MediaTopicPrivacyRestrictionDialogFragment.PROJECTION, MediaTopicPrivacyRestrictionDialogFragment.createSelectionUidIn(restrictedUids), null, null);
        }
    }

    public static MediaTopicPrivacyRestrictionDialogFragment newInstance(Context context, List<String> restrictedUids, int requestCode) {
        int titleResId;
        int positiveResId;
        if ((restrictedUids == null ? 0 : restrictedUids.size()) == 1) {
            titleResId = 2131166167;
            positiveResId = 2131166221;
        } else {
            titleResId = 2131166165;
            positiveResId = 2131166219;
        }
        Bundle args = new Builder().withTitle(LocalizationManager.getString(context, titleResId)).withPositiveText(LocalizationManager.getString(context, positiveResId)).withNegativeText(LocalizationManager.getString(context, 2131165595)).withRequestCode(requestCode).buildArgs();
        args.putStringArrayList("restricted-uids", restrictedUids instanceof ArrayList ? (ArrayList) restrictedUids : new ArrayList(restrictedUids));
        MediaTopicPrivacyRestrictionDialogFragment fragment = new MediaTopicPrivacyRestrictionDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.localizationManager = LocalizationManager.from(getActivity());
        this.restrictedUids = getArguments().getStringArrayList("restricted-uids");
        if (this.restrictedUids == null) {
            this.restrictedUids = Collections.emptyList();
        }
        this.adapter = new UsersInfoCursorAdapter(getActivity(), null, false, SelectionsMode.SINGLE, null, null, null, null);
        getLoaderManager().initLoader(25, null, this);
    }

    public List<String> getRestrictedUids() {
        return this.restrictedUids;
    }

    protected AlertDialogWrapper.Builder buildDialog() {
        AlertDialogWrapper.Builder builder = super.buildDialog();
        RecyclerView recyclerView = createView();
        recyclerView.setAdapter(this.adapter);
        builder.setView(recyclerView);
        return builder;
    }

    public RecyclerView createView() {
        RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(getContext()).inflate(2130903415, null, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        return recyclerView;
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 25) {
            return new RestrictedFriendsLoader(getActivity(), this.restrictedUids);
        }
        return null;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.adapter.swapCursor(data);
        updateDialogText(data);
    }

    private void updateDialogText(Cursor cursor) {
        int positiveResId;
        int titleResId;
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            if (UserGenderType.MALE.toInteger() == cursor.getInt(6)) {
                positiveResId = 2131166221;
                titleResId = 2131166167;
            } else {
                positiveResId = 2131166220;
                titleResId = 2131166166;
            }
        } else {
            positiveResId = 2131166219;
            titleResId = 2131166165;
        }
        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(-1).setText(this.localizationManager.getString(positiveResId));
        dialog.setTitle(this.localizationManager.getString(titleResId));
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    static {
        PROJECTION = new String[]{"_id", "user_id", "user_name", "user_first_name", "user_last_name", "user_avatar_url", "user_gender"};
    }

    private static String createSelectionUidIn(Collection<String> uids) {
        StringBuilder sb = new StringBuilder((uids.size() + 1) * 15);
        sb.append("user_id").append(" IN (");
        boolean addComma = false;
        for (String uid : uids) {
            if (addComma) {
                sb.append(',');
            } else {
                addComma = true;
            }
            sb.append('\'').append(uid).append('\'');
        }
        sb.append(')');
        return sb.toString();
    }
}
