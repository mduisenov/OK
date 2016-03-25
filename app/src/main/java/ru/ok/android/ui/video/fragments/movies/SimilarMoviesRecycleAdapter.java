package ru.ok.android.ui.video.fragments.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.java.api.response.video.VideoGetResponse;
import ru.ok.model.video.MovieInfo;

public class SimilarMoviesRecycleAdapter extends MoviesRecycleAdapter {
    private Listener listener;
    private VideoGetResponse movie;

    public interface Listener {
        void onLikeClick(View view, VideoGetResponse videoGetResponse);

        void onRepeatClick(View view, VideoGetResponse videoGetResponse);
    }

    public class SimilarViewHolder extends MovieViewHolder {
        View divider;
        CheckBox like;
        final Listener listener;

        /* renamed from: ru.ok.android.ui.video.fragments.movies.SimilarMoviesRecycleAdapter.SimilarViewHolder.1 */
        class C13701 implements OnClickListener {
            final /* synthetic */ SimilarMoviesRecycleAdapter val$this$0;

            C13701(SimilarMoviesRecycleAdapter similarMoviesRecycleAdapter) {
                this.val$this$0 = similarMoviesRecycleAdapter;
            }

            public void onClick(View v) {
                if (SimilarViewHolder.this.listener != null) {
                    SimilarViewHolder.this.listener.onRepeatClick(v, SimilarMoviesRecycleAdapter.this.movie);
                }
            }
        }

        /* renamed from: ru.ok.android.ui.video.fragments.movies.SimilarMoviesRecycleAdapter.SimilarViewHolder.2 */
        class C13712 implements OnClickListener {
            final /* synthetic */ SimilarMoviesRecycleAdapter val$this$0;

            C13712(SimilarMoviesRecycleAdapter similarMoviesRecycleAdapter) {
                this.val$this$0 = similarMoviesRecycleAdapter;
            }

            public void onClick(View v) {
                if (SimilarViewHolder.this.listener != null) {
                    SimilarViewHolder.this.listener.onLikeClick(v, SimilarMoviesRecycleAdapter.this.movie);
                }
            }
        }

        public /* bridge */ /* synthetic */ void onClick(View x0) {
            super.onClick(x0);
        }

        SimilarViewHolder(View convertView, Listener onRepeatClickListener) {
            super(convertView);
            this.listener = onRepeatClickListener;
            convertView.setOnClickListener(null);
            convertView.findViewById(2131624865).setOnClickListener(new C13701(SimilarMoviesRecycleAdapter.this));
            this.like = (CheckBox) convertView.findViewById(2131625084);
            if (this.like != null) {
                this.like.setOnClickListener(new C13712(SimilarMoviesRecycleAdapter.this));
            }
            this.divider = convertView.findViewById(2131625309);
        }
    }

    public SimilarMoviesRecycleAdapter(Context context, VideoGetResponse movie) {
        super(context);
        this.movie = movie;
    }

    public void setMovie(VideoGetResponse movie) {
        this.movie = movie;
    }

    public void setOnRepeatClickListener(Listener onRepeatClickListener) {
        this.listener = onRepeatClickListener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        switch (getItemViewType(position)) {
            case RECEIVED_VALUE:
                return new SimilarViewHolder(LayoutInflater.from(parent.getContext()).inflate(2130903436, parent, false), this.listener);
            default:
                return super.onCreateViewHolder(parent, position - 1);
        }
    }

    protected MovieInfo getDataMovie(int position) {
        return super.getDataMovie(position - 1);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case RECEIVED_VALUE:
                SimilarViewHolder headerHolder = (SimilarViewHolder) holder;
                updateViewHolder(headerHolder, this.movie.title, this.movie.duration, this.movie.totalViews, this.movie.thumbnails);
                if (this.movie == null || this.movie.likeSummary == null || !this.movie.likeSummary.isLikePossible()) {
                    headerHolder.like.setVisibility(8);
                    headerHolder.divider.setVisibility(8);
                } else if (this.movie.likeSummary.isSelf()) {
                    headerHolder.like.setChecked(true);
                } else {
                    headerHolder.like.setChecked(false);
                }
            case Message.TEXT_FIELD_NUMBER /*1*/:
                super.onBindViewHolder(holder, position - 1);
            default:
        }
    }

    public int getItemViewType(int position) {
        switch (position) {
            case RECEIVED_VALUE:
                return 0;
            default:
                return 1;
        }
    }

    public int getItemCount() {
        return super.getItemCount() + 1;
    }
}
