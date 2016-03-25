package ru.ok.android.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.google.android.gms.plus.PlusShare;
import java.util.ArrayList;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.RoundAvatarImageView;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;

public class UsersListDialog extends DialogFragment {
    protected UsersAdapter adapter;

    protected class UsersAdapter extends BaseAdapter implements OnClickListener {
        private ArrayList<UserInfo> users;

        private class ItemHolder {
            int paddingBottom;
            int paddingLeft;
            int paddingRight;
            int paddingTop;
            String userId;

            private ItemHolder() {
            }
        }

        public UsersAdapter() {
            this.users = new ArrayList();
        }

        public UsersAdapter(ArrayList<UserInfo> users) {
            this.users = users;
        }

        public void setUsers(ArrayList<UserInfo> users) {
            this.users = users;
        }

        public ArrayList<UserInfo> getUsers() {
            return this.users;
        }

        public int getCount() {
            return this.users.size();
        }

        public UserInfo getItem(int position) {
            return (UserInfo) this.users.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public void onClick(View v) {
            NavigationHelper.showUserInfo(UsersListDialog.this.getActivity(), ((ItemHolder) v.getTag()).userId);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder;
            if (convertView == null) {
                convertView = LocalizationManager.inflate(UsersListDialog.this.getActivity(), UsersListDialog.this.getItemLayoutId(), parent, false);
                convertView.setOnClickListener(this);
                holder = new ItemHolder();
                holder.paddingLeft = convertView.getPaddingLeft();
                holder.paddingBottom = convertView.getPaddingBottom();
                holder.paddingTop = convertView.getPaddingTop();
                holder.paddingRight = convertView.getPaddingRight();
                convertView.setTag(holder);
            } else {
                holder = (ItemHolder) convertView.getTag();
            }
            UserInfo user = getItem(position);
            holder.userId = user.getId();
            ((TextView) convertView.findViewById(C0263R.id.text)).setText(user.getName());
            ImageViewManager.getInstance().displayImage(user.getPicUrl(), (RoundAvatarImageView) convertView.findViewById(2131624657), user.isMan(), null);
            if (position == getCount() - 1) {
                convertView.setPadding(holder.paddingLeft, holder.paddingTop, holder.paddingRight, holder.paddingLeft);
            } else {
                convertView.setPadding(holder.paddingLeft, holder.paddingTop, holder.paddingRight, holder.paddingBottom);
            }
            return convertView;
        }
    }

    public UsersListDialog() {
        this.adapter = new UsersAdapter();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.adapter = new UsersAdapter(getArguments().getParcelableArrayList("users"));
    }

    protected static Bundle getNewInstanceArguments(ArrayList<UserInfo> users, String title) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("users", users);
        args.putString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, title);
        return args;
    }

    protected String getTitle() {
        return getArguments().getString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE);
    }

    protected int getItemLayoutId() {
        return 2130903161;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity()).adapter(this.adapter).cancelable(true);
        String title = getTitle();
        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }
        return builder.build();
    }
}
