package ru.ok.android.ui.video.fragments.movies;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import java.util.ArrayList;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.video.OneLogVideo;
import ru.ok.android.ui.video.activity.VideoActivity;
import ru.ok.android.ui.video.activity.VideoPlayBack;
import ru.ok.android.ui.video.fragments.movies.SimilarMoviesRecycleAdapter.Listener;
import ru.ok.android.utils.bus.BusVideoHelper;
import ru.ok.java.api.response.video.VideoGetResponse;
import ru.ok.model.video.MovieInfo;

public class SimilarMoviesFragment extends MoviesFragment {
    private VideoGetResponse movie;

    /* renamed from: ru.ok.android.ui.video.fragments.movies.SimilarMoviesFragment.1 */
    class C13691 implements Listener {
        C13691() {
        }

        public void onRepeatClick(View view, VideoGetResponse response) {
            SimilarMoviesFragment.this.onRepeatVideoClick();
        }

        public void onLikeClick(View view, VideoGetResponse response) {
            Activity activity = SimilarMoviesFragment.this.getActivity();
            if (activity != null && (activity instanceof VideoActivity)) {
                ((VideoActivity) activity).likeClickVideo();
            }
        }
    }

    public static SimilarMoviesFragment newInstance(VideoGetResponse move) {
        SimilarMoviesFragment fragment = new SimilarMoviesFragment();
        Bundle args = new Bundle();
        args.putParcelable("movie", move);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("movie")) {
            this.movie = (VideoGetResponse) savedInstanceState.getParcelable("movie");
        }
        if (this.movie == null) {
            this.movie = (VideoGetResponse) getArguments().getParcelable("movie");
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("movie", this.movie);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setContentTopClearance(0);
    }

    private VideoGetResponse getMovie() {
        return this.movie;
    }

    private String getMovieId() {
        return getMovie().id;
    }

    protected CharSequence getTitle() {
        VideoGetResponse move = getMovie();
        if (move == null || TextUtils.isEmpty(move.title)) {
            return super.getTitle();
        }
        return move.title;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestSimilarVideos();
    }

    private void requestSimilarVideos() {
        BusVideoHelper.getSimilarVideoInfos(getMovieId());
    }

    @Subscribe(on = 2131623946, to = 2131624268)
    public void onSimilarVideoFetched(BusEvent event) {
        if (getActivity() != null) {
            String videoId = event.bundleInput.getString("VIDEO_ID");
            if (videoId != null && videoId.equals(getMovieId())) {
                if (event.resultCode == -1) {
                    ArrayList<MovieInfo> data = event.bundleOutput.getParcelableArrayList("MOVIES_INFOS");
                    clearErrorType();
                    swapData(data);
                } else {
                    setErrorType(ErrorType.from(event.bundleOutput));
                }
                hideProgress();
                setRefreshing(false);
            }
        }
    }

    protected int getEmptyText() {
        return 2131166566;
    }

    protected void onRepeatVideoClick() {
        super.onRepeatVideoClick();
        Activity activity = getActivity();
        if (activity != null && (activity instanceof VideoPlayBack)) {
            ((VideoPlayBack) activity).onRepeatClick();
        }
    }

    protected MoviesRecycleAdapter createAdapter() {
        SimilarMoviesRecycleAdapter adapter = new SimilarMoviesRecycleAdapter(getActivity(), getMovie());
        adapter.setOnRepeatClickListener(new C13691());
        return adapter;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == 2) {
            setOrientation(0);
        } else {
            setOrientation(1);
        }
    }

    public void onRefresh() {
        super.onRefresh();
    }

    public void setLikeValue(boolean likeValue) {
        if (this.movie != null && this.movie.likeSummary != null && this.movie.likeSummary.isLikePossible()) {
            this.movie.likeSummary.setSelf(likeValue);
            MoviesRecycleAdapter adapter = getAdapter();
            if (adapter != null && (adapter instanceof SimilarMoviesRecycleAdapter)) {
                ((SimilarMoviesRecycleAdapter) adapter).setMovie(this.movie);
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void onSelectMovie(View view, MovieInfo data, int position) {
        super.onSelectMovie(view, data, position);
        if (data != null && !TextUtils.isEmpty(data.getId())) {
            OneLogVideo.logSelectRelated(Long.valueOf(data.getId()).longValue(), position);
        }
    }
}
