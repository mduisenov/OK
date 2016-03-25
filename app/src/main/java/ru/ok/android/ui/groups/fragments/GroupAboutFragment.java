package ru.ok.android.ui.groups.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.fragments.web.WebExternalUrlManager;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.groups.data.GroupProfileInfo;
import ru.ok.android.ui.groups.data.GroupProfileLoader;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.Address;
import ru.ok.model.GroupInfo;
import ru.ok.model.Location;

public class GroupAboutFragment extends BaseFragment implements LoaderCallbacks<GroupProfileInfo> {
    private TextView address;
    private ViewGroup addressHolder;
    private TextView admin;
    private ViewGroup adminHolder;
    private TextView date;
    private TextView dateCreate;
    private TextView description;
    private ViewGroup phoneHolder;
    private TextView phoneTextView;
    private TextView scope;
    private ViewGroup scopeHolders;
    private ViewGroup webHomeHolder;
    private TextView webHomeTextView;

    class DefaultAddressClickListener implements OnClickListener {
        private Address address;
        private Location location;

        DefaultAddressClickListener(Location location, Address address) {
            this.location = location;
            this.address = address;
        }

        public void onClick(View v) {
            if (GroupAboutFragment.this.getActivity() != null && this.location != null) {
                NavigationHelper.showAddressLocation(GroupAboutFragment.this.getActivity(), this.location, this.address, null);
            }
        }
    }

    class DefaultAdminClickListener implements OnClickListener {
        private String uid;

        DefaultAdminClickListener(String uid) {
            this.uid = uid;
        }

        public void onClick(View v) {
            if (GroupAboutFragment.this.getActivity() != null && !TextUtils.isEmpty(this.uid)) {
                NavigationHelper.showUserInfo(GroupAboutFragment.this.getActivity(), this.uid);
            }
        }
    }

    class DefaultPhoneClickListener implements OnClickListener {
        private String phoneNumber;

        DefaultPhoneClickListener(String number) {
            this.phoneNumber = number;
        }

        public void onClick(View v) {
            if (GroupAboutFragment.this.getActivity() != null && !TextUtils.isEmpty(this.phoneNumber)) {
                GroupAboutFragment.this.startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + this.phoneNumber)));
            }
        }
    }

    class DefaultWebClickListener implements OnClickListener {
        private String webAddress;

        DefaultWebClickListener(String address) {
            this.webAddress = address;
        }

        public void onClick(View v) {
            if (GroupAboutFragment.this.getActivity() != null && !TextUtils.isEmpty(this.webAddress)) {
                WebExternalUrlManager.onOutLinkOpenInBrowser(GroupAboutFragment.this.getActivity(), this.webAddress);
            }
        }
    }

    public static Bundle newArguments(String groupId) {
        Bundle bundle = new Bundle();
        bundle.putString("GROUP_ID", groupId);
        return bundle;
    }

    public static GroupAboutFragment newInstance(String groupId) {
        GroupAboutFragment fragment = new GroupAboutFragment();
        fragment.setArguments(newArguments(groupId));
        return fragment;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    private String getGroupId() {
        return getArguments().getString("GROUP_ID");
    }

    protected int getLayoutId() {
        return 2130903231;
    }

    protected CharSequence getTitle() {
        return getStringLocalized(2131165304);
    }

    public View createView(ViewGroup container) {
        View result = inflateViewLocalized(getLayoutId(), container, false);
        this.description = (TextView) result.findViewById(2131624899);
        this.adminHolder = (ViewGroup) result.findViewById(2131624902);
        this.admin = (TextView) result.findViewById(2131624903);
        this.date = (TextView) result.findViewById(2131624541);
        this.dateCreate = (TextView) result.findViewById(2131624910);
        this.addressHolder = (ViewGroup) result.findViewById(2131624904);
        this.address = (TextView) result.findViewById(2131624905);
        this.scopeHolders = (ViewGroup) result.findViewById(2131624900);
        this.scope = (TextView) result.findViewById(2131624901);
        this.webHomeHolder = (ViewGroup) result.findViewById(2131624906);
        this.webHomeTextView = (TextView) result.findViewById(2131624907);
        this.phoneHolder = (ViewGroup) result.findViewById(2131624908);
        this.phoneTextView = (TextView) result.findViewById(2131624909);
        return result;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return buildDialog().create();
    }

    protected Builder buildDialog() {
        return new Builder(getActivity()).setTitle(getTitle()).setView(createView(null)).setPositiveButton(LocalizationManager.getString(getActivity(), 2131165595), null);
    }

    public Loader<GroupProfileInfo> onCreateLoader(int id, Bundle args) {
        return new GroupProfileLoader(getActivity(), getGroupId());
    }

    public void onLoadFinished(Loader<GroupProfileInfo> loader, GroupProfileInfo profile) {
        if (profile != null && profile.groupInfo != null) {
            GroupInfo groupInfo = profile.groupInfo;
            getDialog().setTitle(groupInfo.getName());
            Utils.setTextViewTextWithVisibility(this.description, groupInfo.getDescription());
            long ms = groupInfo.getCreatedMs();
            if (ms > 0) {
                this.dateCreate.setVisibility(0);
                this.dateCreate.setText(LocalizationManager.getString(getActivity(), 2131165941) + " " + DateFormatter.formatTodayTimeOrOlderDate(getActivity(), ms));
            } else {
                this.dateCreate.setVisibility(8);
            }
            if (groupInfo.getStartDate() > 0) {
                this.date.setVisibility(0);
                String value = DateFormatter.getFormatStringFromDate(getActivity(), groupInfo.getStartDate());
                if (groupInfo.getEndDate() > 0) {
                    value = value + " - " + DateFormatter.getFormatStringFromDate(getActivity(), groupInfo.getEndDate());
                }
                this.date.setText(value);
            } else {
                this.date.setVisibility(8);
            }
            if (profile.admin != null) {
                this.adminHolder.setVisibility(0);
                this.admin.setText(LocalizationManager.getString(getActivity(), 2131165930) + " " + profile.admin.getConcatName());
                this.adminHolder.setOnClickListener(new DefaultAdminClickListener(profile.admin.uid));
            } else {
                this.adminHolder.setVisibility(8);
            }
            if (groupInfo.getAddress() != null) {
                this.addressHolder.setVisibility(0);
                this.address.setText(LocalizationManager.getString(getActivity(), 2131165929) + " " + groupInfo.getAddress().getStringAddress());
                this.addressHolder.setOnClickListener(new DefaultAddressClickListener(groupInfo.getLocation(), groupInfo.getAddress()));
            } else {
                this.addressHolder.setVisibility(8);
            }
            if (groupInfo.getSubCategory() != null && !TextUtils.isEmpty(groupInfo.getSubCategory().getName())) {
                this.scopeHolders.setVisibility(0);
                this.scope.setText(LocalizationManager.getString(getActivity(), 2131166478) + " " + groupInfo.getSubCategory().getName());
            } else if (groupInfo.getScope() != null) {
                this.scopeHolders.setVisibility(0);
                this.scope.setText(LocalizationManager.getString(getActivity(), 2131166478) + " " + groupInfo.getScope());
            } else {
                this.scopeHolders.setVisibility(8);
            }
            if (TextUtils.isEmpty(groupInfo.getWebUrl())) {
                this.webHomeHolder.setVisibility(8);
            } else {
                this.webHomeHolder.setVisibility(0);
                this.webHomeTextView.setText(LocalizationManager.getString(getActivity(), 2131165960) + " " + groupInfo.getWebUrl());
                this.webHomeHolder.setOnClickListener(new DefaultWebClickListener(groupInfo.getWebUrl()));
            }
            if (TextUtils.isEmpty(groupInfo.getPhone())) {
                this.phoneHolder.setVisibility(8);
                return;
            }
            this.phoneHolder.setVisibility(0);
            this.phoneTextView.setText(LocalizationManager.getString(getActivity(), 2131165949) + " " + groupInfo.getPhone());
            this.phoneHolder.setOnClickListener(new DefaultPhoneClickListener(groupInfo.getPhone()));
        }
    }

    public void onLoaderReset(Loader<GroupProfileInfo> loader) {
    }
}
