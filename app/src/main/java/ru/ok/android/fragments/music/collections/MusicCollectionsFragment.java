package ru.ok.android.fragments.music.collections;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.music.MusicPlayerInActionBarFragmentWithStub;
import ru.ok.android.ui.adapters.music.DotsCursorAdapter.OnDotsClickListener;
import ru.ok.android.ui.adapters.music.collections.MusicCollectionsCursorAdapter;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController.OnItemClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.utils.EmptyViewRecyclerDataObserver;
import ru.ok.android.utils.HideKeyboardRecyclerScrollHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.UserTrackCollection;

public abstract class MusicCollectionsFragment extends MusicPlayerInActionBarFragmentWithStub implements LoaderCallbacks<Cursor>, OnDotsClickListener<UserTrackCollection>, OnItemClickListener, OnStubButtonClickListener {
    protected MusicCollectionsCursorAdapter adapter;
    private RecyclerView collectionsListView;
    private LinearLayoutManager linearLayoutManager;
    private View mMainView;

    protected abstract void getData();

    protected abstract void onDotsClickToCollection(UserTrackCollection userTrackCollection, View view);

    protected abstract void onSelectCollection(UserTrackCollection userTrackCollection);

    public boolean isPlayFloatingButtonRequired() {
        return false;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mMainView = LocalizationManager.inflate(getContext(), getLayoutId(), null, false);
        this.collectionsListView = (RecyclerView) this.mMainView.findViewById(2131625415);
        this.linearLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.collectionsListView.setLayoutManager(this.linearLayoutManager);
        this.adapter = new MusicCollectionsCursorAdapter(getContext(), null);
        this.adapter.getItemClickListenerController().addItemClickListener(this);
        this.adapter.setOnDotsClickListener(this);
        this.collectionsListView.addOnScrollListener(new HideKeyboardRecyclerScrollHelper(getContext(), this.mMainView));
        this.emptyView = (SmartEmptyViewAnimated) this.mMainView.findViewById(C0263R.id.empty_view);
        this.emptyView.setButtonClickListener(this);
        this.adapter.registerAdapterDataObserver(new EmptyViewRecyclerDataObserver(this.emptyView, this.adapter));
        this.collectionsListView.setAdapter(this.adapter);
        return this.mMainView;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        this.adapter.swapCursor(cursor);
        dbLoadCompleted();
    }

    public void onStubButtonClick(Type type) {
        getData();
    }

    protected int getLayoutId() {
        return 2130903556;
    }

    public void onItemClick(View view, int position) {
        int headerItems = getHeaderViewsCount();
        int footerItems = getFooterViewsCount();
        position -= headerItems;
        if (position >= 0 && position < this.adapter.getItemCount() - footerItems) {
            onSelectCollection(MusicStorageFacade.cursor2UserTrackCollection((Cursor) this.adapter.getItem(position)));
        }
    }

    private int getFooterViewsCount() {
        return 0;
    }

    private int getHeaderViewsCount() {
        return 0;
    }

    public void onDotsClick(UserTrackCollection collection, View view) {
        onDotsClickToCollection(collection, view);
    }
}
