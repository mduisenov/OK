package ru.ok.android.ui.adapters.friends;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController.OnItemClickListener;
import ru.ok.android.ui.custom.cards.listcard.CardItem;
import ru.ok.android.ui.custom.cards.listcard.CardItem.Type;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.AbsListItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.UserCardItem.ItemType;
import ru.ok.android.ui.custom.cards.listcard.CardViewHolder;
import ru.ok.android.ui.custom.cards.search.UserViewsHolder;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.localization.base.LocalizedActivity;
import ru.ok.model.music.MusicUserInfo;

public class UsersMusicCardAdapter extends CardListAdapter implements OnItemClickListener {
    private final List<AbsListItem> absListItems;
    private final boolean mAutoReQuery;
    protected ChangeObserver mChangeObserver;
    private Cursor mCursor;
    private final MusicFragmentMode mode;
    private String selectionUserId;

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        public boolean deliverSelfNotifications() {
            return true;
        }

        public void onChange(boolean selfChange) {
            UsersMusicCardAdapter.this.onContentChanged();
        }
    }

    public UsersMusicCardAdapter(Context context, Cursor cursor, boolean autoReQuery, List<AbsListItem> absListItems, MusicFragmentMode mode) {
        super((LocalizedActivity) context);
        this.mChangeObserver = new ChangeObserver();
        this.absListItems = absListItems;
        this.mode = mode;
        this.mAutoReQuery = autoReQuery;
        swapCursor(cursor);
        this.itemClickListenerController.addItemClickListener(this);
    }

    private void updateListFromCursor() {
        List usersInfo = new ArrayList();
        if (this.mCursor == null) {
            setData(new ArrayList(0));
            return;
        }
        this.mCursor.moveToFirst();
        while (!this.mCursor.isAfterLast()) {
            usersInfo.add(MusicStorageFacade.cursor2MusicUser(this.mCursor));
            this.mCursor.moveToNext();
        }
        List<CardItem> cardItems = new ArrayList(2);
        cardItems.add(new CardItem().setAbsItemList(this.absListItems).setType(Type.list_abs));
        cardItems.add(new CardItem().setInfoList(usersInfo, ItemType.music).setTitle(this.activity.getStringLocalized(2131165379)));
        setData(cardItems);
    }

    public void setSelectionUserId(String selectionUserId) {
        this.selectionUserId = selectionUserId;
        notifyDataSetChanged();
    }

    public void onItemClick(View view, int i) {
        switch (getItemViewType(i)) {
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                NavigationHelper.showNewMusicPlaylist(this.activity, this.mode);
                break;
            case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                NavigationHelper.showMusicTuners(this.activity);
                break;
            case Message.EDITINFO_FIELD_NUMBER /*11*/:
                NavigationHelper.showMyMusicPage(this.activity, 0, this.mode);
                break;
            case Message.REPLYSTICKERS_FIELD_NUMBER /*12*/:
                NavigationHelper.showUserMusicPage(this.activity, ((MusicUserInfo) getItem(i)).uid, this.mode);
                break;
        }
        if (!DeviceUtils.isSmall(this.activity) && DeviceUtils.isLargeTablet(this.activity)) {
            setSelectionPosition(i);
        }
    }

    private void setTracksCount(UserViewsHolder holder, int count) {
        Context context = OdnoklassnikiApplication.getContext();
        if (count == 0) {
            holder.getInfoView().setText(LocalizationManager.getString(context, 2131166257) + " " + LocalizationManager.getString(context, 2131166610));
        } else {
            holder.getInfoView().setText(count + " " + LocalizationManager.getString(context, StringUtils.plural((long) count, 2131166608, 2131166609, 2131166610)));
        }
    }

    public void onBindViewHolder(CardViewHolder holder, int position) {
        boolean z = true;
        super.onBindViewHolder(holder, position);
        int itemViewType = getItemViewType(position);
        View view;
        if (itemViewType == 0 || itemViewType == 12) {
            MusicUserInfo musicUserInfo = (MusicUserInfo) getItem(position);
            view = holder.itemView;
            if (this.selectionUserId == null || musicUserInfo == null || !musicUserInfo.uid.equals(this.selectionUserId)) {
                z = false;
            }
            view.setActivated(z);
            setTracksCount((UserViewsHolder) holder, musicUserInfo.tracksCount);
        } else if (itemViewType == 10) {
            view = holder.itemView;
            if (this.selectionUserId == null || !this.selectionUserId.equals("-64")) {
                z = false;
            }
            view.setActivated(z);
        } else if (itemViewType == 9) {
            view = holder.itemView;
            if (this.selectionUserId == null || !this.selectionUserId.equals("-32")) {
                z = false;
            }
            view.setActivated(z);
        } else if (itemViewType == 11) {
            view = holder.itemView;
            if (this.selectionUserId == null || !this.selectionUserId.equals("-128")) {
                z = false;
            }
            view.setActivated(z);
        }
    }

    public void swapCursor(Cursor newCursor) {
        if (newCursor != this.mCursor) {
            Cursor oldCursor = this.mCursor;
            if (!(oldCursor == null || this.mChangeObserver == null)) {
                oldCursor.unregisterContentObserver(this.mChangeObserver);
            }
            this.mCursor = newCursor;
            if (!(newCursor == null || this.mChangeObserver == null)) {
                newCursor.registerContentObserver(this.mChangeObserver);
            }
            updateListFromCursor();
        }
    }

    public void clearSelection() {
        setSelectionUserId(null);
    }

    public void setSelectionPosition(int position) {
        Pair<String, String> pair = new Pair("selection", String.valueOf(position));
        StatisticManager.getInstance().addStatisticEvent("music-playlist_item_touch", pair);
        String oldSelectionUserId = this.selectionUserId;
        switch (getItemViewType(position)) {
            case RECEIVED_VALUE:
            case Message.REPLYSTICKERS_FIELD_NUMBER /*12*/:
                this.selectionUserId = ((MusicUserInfo) getItem(position)).uid;
                break;
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                this.selectionUserId = "-32";
                break;
            case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                this.selectionUserId = "-64";
                break;
            case Message.EDITINFO_FIELD_NUMBER /*11*/:
                this.selectionUserId = "-128";
                break;
        }
        if (oldSelectionUserId != null) {
            notifyItemChangedUid(oldSelectionUserId);
        }
        notifyItemChanged(position);
    }

    public void notifyItemChangedUid(String uid) {
        if (this.mData != null) {
            int size = this.mData.size();
            for (int position = 0; position < size; position++) {
                int itemViewType = getItemViewType(position);
                if (itemViewType == 0 || itemViewType == 12) {
                    MusicUserInfo musicUserInfo = (MusicUserInfo) getItem(position);
                    if (musicUserInfo != null && uid.equals(musicUserInfo.uid)) {
                        notifyItemChanged(position);
                        return;
                    }
                } else if (itemViewType == 10) {
                    if (uid.equals("-64")) {
                        notifyItemChanged(position);
                        return;
                    }
                } else if (itemViewType == 9) {
                    if (uid.equals("-32")) {
                        notifyItemChanged(position);
                        return;
                    }
                } else if (itemViewType == 11 && uid.equals("-128")) {
                    notifyItemChanged(position);
                    return;
                }
            }
        }
    }

    public void setSelectionMyMusic() {
        this.selectionUserId = "-128";
        notifyDataSetChanged();
    }

    protected void onContentChanged() {
        if (this.mAutoReQuery && this.mCursor != null && !this.mCursor.isClosed()) {
            this.mCursor.requery();
            updateListFromCursor();
        }
    }
}
