package ru.ok.android.ui.stream;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.google.android.gms.plus.PlusShare;
import java.util.ArrayList;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.adapters.ScrollLoadBlocker;
import ru.ok.android.ui.custom.imageview.RoundAvatarImageView;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public class FeedHeaderActionsDialog extends DialogFragment {
    private InfoAdapter adapter;
    private Context context;
    private final ScrollLoadBlocker imageBlocker;
    private FeedHeaderActionsDialogListener listener;

    public interface FeedHeaderActionsDialogListener {
        void onFeedHeaderActionSelected(GeneralUserInfo generalUserInfo, String str);
    }

    class InfoAdapter extends BaseAdapter implements OnItemClickListener {
        private final ArrayList<GeneralUserInfo> infos;

        public InfoAdapter(ArrayList<GeneralUserInfo> infos) {
            this.infos = infos;
        }

        public int getCount() {
            return this.infos.size();
        }

        public Object getItem(int position) {
            return this.infos.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ItemViewHolder viewHolder;
            if (convertView == null) {
                convertView = LocalizationManager.inflate(FeedHeaderActionsDialog.this.context, 2130903186, parent, false);
                viewHolder = new ItemViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ItemViewHolder) convertView.getTag();
            }
            GeneralUserInfo info = (GeneralUserInfo) this.infos.get(position);
            ImageViewManager.getInstance().displayImage(info.getPicUrl(), viewHolder.avatarImageView, FeedHeaderActionsDialog.getDefaultResourceId(info), FeedHeaderActionsDialog.this.imageBlocker);
            viewHolder.textView.setText(info.getName());
            if (position == this.infos.size() - 1) {
                convertView.setPadding(viewHolder.paddingLeft, viewHolder.paddingTop, viewHolder.paddingRight, viewHolder.paddingLeft);
            } else {
                convertView.setPadding(viewHolder.paddingLeft, viewHolder.paddingTop, viewHolder.paddingRight, viewHolder.paddingBottom);
            }
            return convertView;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int which, long id) {
            if (which >= 0 && which < FeedHeaderActionsDialog.this.adapter.getCount()) {
                FeedHeaderActionsDialog.this.notifyActionSelected((GeneralUserInfo) FeedHeaderActionsDialog.this.adapter.getItem(which));
                FeedHeaderActionsDialog.this.dismiss();
            }
        }
    }

    static class ItemViewHolder {
        final RoundAvatarImageView avatarImageView;
        final int paddingBottom;
        final int paddingLeft;
        final int paddingRight;
        final int paddingTop;
        final TextView textView;

        ItemViewHolder(View view) {
            this.textView = (TextView) view.findViewById(C0263R.id.text);
            this.avatarImageView = (RoundAvatarImageView) view.findViewById(2131624657);
            this.paddingBottom = view.getPaddingBottom();
            this.paddingLeft = view.getPaddingLeft();
            this.paddingRight = view.getPaddingRight();
            this.paddingTop = view.getPaddingTop();
        }
    }

    public FeedHeaderActionsDialog() {
        this.imageBlocker = ScrollLoadBlocker.forIdleAndTouchIdle();
    }

    public static FeedHeaderActionsDialog newInstance(ArrayList<? extends GeneralUserInfo> infos, String source) {
        return newInstance(infos, null, source);
    }

    public static FeedHeaderActionsDialog newInstance(ArrayList<? extends GeneralUserInfo> infos, String title, String source) {
        FeedHeaderActionsDialog fragment = new FeedHeaderActionsDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList("infos", infos);
        args.putString("source", source);
        args.putString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    private String getSource() {
        return getArguments().getString("source");
    }

    public void setListener(FeedHeaderActionsDialogListener listener) {
        this.listener = listener;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<GeneralUserInfo> infos = getArguments().getParcelableArrayList("infos");
        this.context = getActivity();
        this.adapter = new InfoAdapter(infos);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        MaterialDialog ret = new Builder(context).title(getDialogTitle(context)).adapter(this.adapter).cancelable(true).build();
        ret.getListView().setOnItemClickListener(this.adapter);
        return ret;
    }

    private String getDialogTitle(Context context) {
        String title = getArguments().getString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE);
        return !TextUtils.isEmpty(title) ? title : LocalizationManager.from(context).getString(2131165862);
    }

    private void notifyActionSelected(GeneralUserInfo info) {
        if (this.listener != null) {
            this.listener.onFeedHeaderActionSelected(info, getSource());
            return;
        }
        Fragment fragment = getTargetFragment();
        if (fragment instanceof FeedHeaderActionsDialogListener) {
            ((FeedHeaderActionsDialogListener) fragment).onFeedHeaderActionSelected(info, getSource());
            return;
        }
        Activity activity = getActivity();
        if (activity instanceof FeedHeaderActionsDialogListener) {
            ((FeedHeaderActionsDialogListener) activity).onFeedHeaderActionSelected(info, getSource());
        }
    }

    private static int getDefaultResourceId(GeneralUserInfo userInfo) {
        if (userInfo instanceof GroupInfo) {
            return 2130837663;
        }
        if (!(userInfo instanceof UserInfo) || ((UserInfo) userInfo).genderType == UserGenderType.MALE) {
            return 2130838321;
        }
        return 2130837927;
    }
}
