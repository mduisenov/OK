package ru.ok.android.ui.users.fragments.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsStrategy;
import ru.ok.android.ui.utils.StickyHeaderItemDecorator.HeaderViewProvider;

public final class FriendsListWithHeadersAdapter extends FriendsListAdapter implements HeaderViewProvider {

    static class ViewHolderHeader extends ru.ok.android.ui.utils.StickyHeaderItemDecorator.ViewHolderHeader {
        final TextView text;

        public ViewHolderHeader(View view) {
            super(view);
            this.text = (TextView) view.findViewById(C0263R.id.text);
        }
    }

    public FriendsListWithHeadersAdapter(Context context, int rowLayoutId, FriendsStrategy strategy, int headerTextId, boolean alwaysShowWriteMessage, boolean hidePrivateProfileIcon) {
        super(context, rowLayoutId, strategy, headerTextId, alwaysShowWriteMessage, hidePrivateProfileIcon);
    }

    public String getHeader(int position) {
        return this.strategy.getItemHeader(position);
    }

    public ru.ok.android.ui.utils.StickyHeaderItemDecorator.ViewHolderHeader newHeaderView(int position, ViewGroup parent) {
        return new ViewHolderHeader(LayoutInflater.from(this.context).inflate(2130903208, parent, false));
    }

    public int getHeaderViewType(int position) {
        return 2131624357;
    }

    public void bindHeaderView(ru.ok.android.ui.utils.StickyHeaderItemDecorator.ViewHolderHeader holder, int position) {
        ((ViewHolderHeader) holder).text.setText(getHeader(position));
    }

    public int getAnchorViewId(int position) {
        return 2131624871;
    }
}
