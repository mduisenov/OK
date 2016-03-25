package ru.ok.android.ui.video.fragments.movies;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.View;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.video.fragments.BaseRecycleFragment;
import ru.ok.android.ui.video.fragments.movies.MoviesRecycleAdapter.OnSelectMovieListener;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.Logger;
import ru.ok.model.video.MovieInfo;

public abstract class MoviesFragment<T extends MoviesRecycleAdapter> extends BaseRecycleFragment implements OnRefreshListener, OnSelectMovieListener {
    protected T adapter;
    private GridLayoutManager layoutManager;
    final AdapterDataObserver observer;
    private List<OnScrollListener> scrollListeners;

    public interface OnSelectMovieCallback {
        void onSelectMovie(MovieInfo movieInfo);
    }

    /* renamed from: ru.ok.android.ui.video.fragments.movies.MoviesFragment.1 */
    class C13671 extends AdapterDataObserver {
        C13671() {
        }

        public void onChanged() {
            super.onChanged();
            MoviesFragment.this.checkIfEmpty();
        }
    }

    /* renamed from: ru.ok.android.ui.video.fragments.movies.MoviesFragment.2 */
    static /* synthetic */ class C13682 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$utils$DeviceUtils$DeviceLayoutType;

        static {
            $SwitchMap$ru$ok$android$utils$DeviceUtils$DeviceLayoutType = new int[DeviceLayoutType.values().length];
            try {
                $SwitchMap$ru$ok$android$utils$DeviceUtils$DeviceLayoutType[DeviceLayoutType.SMALL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$DeviceUtils$DeviceLayoutType[DeviceLayoutType.BIG.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$DeviceUtils$DeviceLayoutType[DeviceLayoutType.LARGE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private class ScrollListenerIpl extends OnScrollListener {
        private ScrollListenerIpl() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            for (OnScrollListener listener : MoviesFragment.this.scrollListeners) {
                listener.onScrollStateChanged(recyclerView, newState);
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            for (OnScrollListener listener : MoviesFragment.this.scrollListeners) {
                listener.onScrolled(recyclerView, dx, dy);
            }
        }
    }

    protected abstract T createAdapter();

    public MoviesFragment() {
        this.scrollListeners = new ArrayList();
        this.observer = new C13671();
    }

    protected int getEmptyText() {
        return 2131165565;
    }

    protected T getAdapter() {
        return this.adapter;
    }

    protected int getOrientation() {
        if (getActivity() == null) {
            return 1;
        }
        int orientation = DeviceUtils.getScreenOrientation(getActivity());
        if (orientation == 1 || orientation == 9) {
            return 1;
        }
        return 0;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.layoutManager = new GridLayoutManager(getActivity(), getColumnCount(), getOrientation(), false);
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.adapter = createAdapter();
        this.adapter.setListener(this);
        this.recyclerView.setAdapter(this.adapter);
        this.adapter.registerAdapterDataObserver(this.observer);
        this.recyclerView.setOnScrollListener(new ScrollListenerIpl());
    }

    public void setContentTopClearance(int clearance) {
        this.recyclerView.setPadding(this.recyclerView.getPaddingLeft(), clearance, this.recyclerView.getPaddingRight(), this.recyclerView.getPaddingBottom());
        this.spinner.setPadding(this.spinner.getPaddingLeft(), clearance, this.spinner.getPaddingRight(), this.spinner.getPaddingBottom());
    }

    protected int getColumnCount() {
        Activity activity = getActivity();
        if (activity == null) {
            return 1;
        }
        switch (C13682.$SwitchMap$ru$ok$android$utils$DeviceUtils$DeviceLayoutType[DeviceUtils.getType(activity).ordinal()]) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return 2;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return 3;
            default:
                return 1;
        }
    }

    protected void swapData(Collection<MovieInfo> channels) {
        this.adapter.swapData(channels);
    }

    protected void setOrientation(int orientation) {
        this.layoutManager.setOrientation(orientation);
        this.recyclerView.setLayoutManager(this.layoutManager);
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.adapter.unregisterAdapterDataObserver(this.observer);
    }

    public void onSelectMovie(View view, MovieInfo data, int position) {
        Activity activity = getActivity();
        if (activity != null && (activity instanceof OnSelectMovieCallback)) {
            ((OnSelectMovieCallback) activity).onSelectMovie(data);
        }
    }

    protected void checkIfEmpty() {
        if (this.adapter.getItemCount() == 0) {
            Logger.m172d("List is empty");
            showEmpty();
            return;
        }
        Logger.m172d("List is not empty");
        hideEmpty();
    }
}
