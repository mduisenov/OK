package ru.ok.android.fragments.image;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.utils.DimenUtils;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.photo.PhotoAlbumInfo;

public final class AlbumHeaderViewController implements OnScrollListener {
    private View authorContainerView;
    private TextView commentsCountView;
    private View countsContainerView;
    private View dividerView;
    protected ListView gridView;
    private TextView groupView;
    private ViewGroup headerContainerView;
    protected boolean hidden;
    protected boolean hiddenByData;
    private TextView likeAdditionalView;
    private View likeDividerView;
    private TextView likeIconedView;
    private View listHeaderView;
    private OnHeaderActionListener onHeaderActionListener;
    private OnClickListener onLikeClickListener;
    private OnClickListener onLikesCountClickListener;
    private TextView userView;

    /* renamed from: ru.ok.android.fragments.image.AlbumHeaderViewController.1 */
    class C02931 implements OnClickListener {
        C02931() {
        }

        public void onClick(View view) {
            if (AlbumHeaderViewController.this.onHeaderActionListener != null) {
                AlbumHeaderViewController.this.onHeaderActionListener.onLikesCountClicked(view);
            }
        }
    }

    /* renamed from: ru.ok.android.fragments.image.AlbumHeaderViewController.2 */
    class C02942 implements OnClickListener {
        C02942() {
        }

        public void onClick(View view) {
            if (AlbumHeaderViewController.this.onHeaderActionListener != null) {
                AlbumHeaderViewController.this.onHeaderActionListener.onLikeClicked(view);
            }
        }
    }

    /* renamed from: ru.ok.android.fragments.image.AlbumHeaderViewController.3 */
    class C02953 implements OnClickListener {
        C02953() {
        }

        public void onClick(View view) {
            if (AlbumHeaderViewController.this.onHeaderActionListener != null) {
                AlbumHeaderViewController.this.onHeaderActionListener.onInfoHeaderClicked();
            }
        }
    }

    /* renamed from: ru.ok.android.fragments.image.AlbumHeaderViewController.4 */
    class C02964 implements OnClickListener {
        C02964() {
        }

        public void onClick(View view) {
            if (AlbumHeaderViewController.this.onHeaderActionListener != null) {
                AlbumHeaderViewController.this.onHeaderActionListener.onCommentsClicked(view);
            }
        }
    }

    /* renamed from: ru.ok.android.fragments.image.AlbumHeaderViewController.5 */
    class C02975 implements OnGlobalLayoutListener {
        C02975() {
        }

        public void onGlobalLayout() {
            AlbumHeaderViewController.this.headerContainerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    /* renamed from: ru.ok.android.fragments.image.AlbumHeaderViewController.6 */
    class C02986 implements Runnable {
        C02986() {
        }

        public void run() {
            AlbumHeaderViewController.this.updateScrollPosition();
        }
    }

    public interface OnHeaderActionListener {
        void onCommentsClicked(View view);

        void onInfoHeaderClicked();

        void onLikeClicked(View view);

        void onLikesCountClicked(View view);
    }

    public AlbumHeaderViewController(View rootView) {
        this.hiddenByData = true;
        this.onLikesCountClickListener = new C02931();
        this.onLikeClickListener = new C02942();
        this.headerContainerView = (ViewGroup) rootView.findViewById(2131625190);
        this.countsContainerView = rootView.findViewById(2131625194);
        this.authorContainerView = rootView.findViewById(2131625191);
        OnClickListener infoClickListener = new C02953();
        this.countsContainerView.setOnClickListener(infoClickListener);
        this.authorContainerView.setOnClickListener(infoClickListener);
        this.userView = (TextView) rootView.findViewById(2131625192);
        this.groupView = (TextView) rootView.findViewById(2131625193);
        this.commentsCountView = (TextView) rootView.findViewById(2131625195);
        this.commentsCountView.setOnClickListener(new C02964());
        this.dividerView = rootView.findViewById(2131624602);
        this.gridView = (ListView) rootView.findViewById(C0263R.id.grid);
        this.gridView.setOnScrollListener(this);
        this.likeIconedView = (TextView) rootView.findViewById(2131625196);
        this.likeAdditionalView = (TextView) rootView.findViewById(2131625198);
        this.likeDividerView = rootView.findViewById(2131625197);
        updateScrollPosition();
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        View firstChild = this.gridView.getChildAt(0);
        if (firstChild != null) {
            updateForScrollPosition(-((-firstChild.getTop()) + (this.gridView.getFirstVisiblePosition() * firstChild.getHeight())));
        }
    }

    public final void setInfo(PhotoOwner photoOwner, PhotoAlbumInfo albumInfo, UserInfo authorInfo) {
        this.hiddenByData = true;
        this.dividerView.setVisibility(8);
        this.userView.setVisibility(8);
        this.groupView.setVisibility(8);
        this.commentsCountView.setVisibility(8);
        if (!(albumInfo == null || albumInfo.isVirtual())) {
            this.dividerView.setVisibility(0);
            this.hiddenByData = false;
            formatLikeBlock(albumInfo);
            if (photoOwner.getType() == 0) {
                this.commentsCountView.setVisibility(0);
                this.commentsCountView.setText(String.valueOf(albumInfo.getCommentsCount()));
            }
        }
        if (photoOwner.getType() == 0) {
            this.authorContainerView.setVisibility(8);
            this.dividerView.setVisibility(8);
        } else {
            this.authorContainerView.setVisibility(0);
            setUserInfo(authorInfo);
            GroupInfo groupInfo = (GroupInfo) photoOwner.getOwnerInfo();
            if (groupInfo != null) {
                this.groupView.setText(buildOwnerLine(LocalizationManager.getString(this.groupView.getContext(), 2131165276), groupInfo.getName()), BufferType.SPANNABLE);
                this.groupView.setVisibility(0);
            }
        }
        if (!(photoOwner.getOwnerInfo() == null && albumInfo == null)) {
            this.headerContainerView.setVisibility(0);
            this.headerContainerView.getViewTreeObserver().addOnGlobalLayoutListener(new C02975());
        }
        updateVisibility();
        ThreadUtil.queueOnMain(new C02986());
    }

    private void setUserInfo(UserInfo userInfo) {
        if (userInfo != null) {
            this.userView.setText(buildOwnerLine(LocalizationManager.getString(this.userView.getContext(), 2131165422), userInfo.firstName + " " + userInfo.lastName), BufferType.SPANNABLE);
            this.userView.setVisibility(0);
        }
    }

    private void formatLikeBlock(PhotoAlbumInfo albumInfo) {
        boolean viewerCanLike;
        Context context = this.likeIconedView.getContext();
        boolean viewerLiked = albumInfo.isViewerLiked();
        if (!albumInfo.isCanLike() || viewerLiked) {
            viewerCanLike = false;
        } else {
            viewerCanLike = true;
        }
        int displayLikesCount = viewerLiked ? albumInfo.getLikesCount() - 1 : albumInfo.getLikesCount();
        updateViewState(this.likeIconedView, null);
        this.likeIconedView.setText(null);
        setLikeAdittionalViewState(false);
        this.likeAdditionalView.setPadding(DimenUtils.getRealDisplayPixels(8, context), this.likeAdditionalView.getPaddingTop(), this.likeAdditionalView.getPaddingRight(), this.likeAdditionalView.getPaddingBottom());
        if (displayLikesCount == 0) {
            this.likeAdditionalView.setVisibility(8);
            if (viewerLiked) {
                this.likeIconedView.setText(LocalizationManager.getString(context, 2131166888));
                updateViewState(this.likeIconedView, this.onLikeClickListener);
                this.likeDividerView.setVisibility(8);
                return;
            } else if (viewerCanLike) {
                this.likeIconedView.setText(LocalizationManager.getString(context, 2131166038));
                updateViewState(this.likeIconedView, this.onLikeClickListener);
                this.likeDividerView.setVisibility(8);
                return;
            } else {
                this.likeIconedView.setText("0");
                updateViewState(this.likeIconedView, null);
                this.likeIconedView.setEnabled(true);
                this.likeDividerView.setVisibility(8);
                return;
            }
        }
        this.likeIconedView.setText(String.valueOf(displayLikesCount));
        updateViewState(this.likeIconedView, this.onLikesCountClickListener);
        setLikeAdittionalViewState(true);
        if (viewerLiked) {
            this.likeAdditionalView.setPadding(0, this.likeAdditionalView.getPaddingTop(), this.likeAdditionalView.getPaddingRight(), this.likeAdditionalView.getPaddingBottom());
            this.likeAdditionalView.setText(LocalizationManager.getString(context, 2131165388));
            this.likeAdditionalView.setVisibility(0);
            this.likeDividerView.setVisibility(8);
        } else if (viewerCanLike) {
            this.likeAdditionalView.setText(LocalizationManager.getString(context, 2131166038));
            updateViewState(this.likeAdditionalView, this.onLikeClickListener);
            this.likeDividerView.setVisibility(0);
        } else {
            this.likeAdditionalView.setVisibility(8);
            this.likeDividerView.setVisibility(8);
        }
    }

    private void setLikeAdittionalViewState(boolean enabled) {
        int i;
        int i2 = 0;
        TextView textView = this.likeAdditionalView;
        if (enabled) {
            i = 0;
        } else {
            i = 8;
        }
        textView.setVisibility(i);
        View view = this.likeDividerView;
        if (!enabled) {
            i2 = 8;
        }
        view.setVisibility(i2);
        updateViewState(this.likeAdditionalView, enabled ? this.onLikeClickListener : null);
    }

    private void updateViewState(View view, OnClickListener onClickListener) {
        boolean enabled = onClickListener != null;
        view.setOnClickListener(onClickListener);
        view.setEnabled(enabled);
        view.setClickable(enabled);
        view.setAlpha(enabled ? 1.0f : 0.75f);
    }

    private CharSequence buildOwnerLine(CharSequence title, String value) {
        SpannableString spannable = new SpannableString(title + " " + value);
        spannable.setSpan(new StyleSpan(1), title.length(), spannable.length(), 18);
        return spannable;
    }

    public final void updateScrollPosition() {
        if (this.gridView.getHeaderViewsCount() == 0) {
            this.listHeaderView = new View(this.gridView.getContext());
            this.listHeaderView.setMinimumWidth(1);
            this.gridView.addHeaderView(this.listHeaderView);
        }
        if (this.listHeaderView != null && this.listHeaderView.getMeasuredHeight() != this.headerContainerView.getMeasuredHeight()) {
            this.listHeaderView.setMinimumHeight(this.headerContainerView.getMeasuredHeight());
            this.listHeaderView.requestLayout();
        }
    }

    protected final void updateForScrollPosition(int scrollY) {
        int toScroll = Math.min(0, Math.max(-(this.headerContainerView.getMeasuredHeight() - this.countsContainerView.getMeasuredHeight()), (int) (((float) scrollY) / 1.5f)));
        if (((float) this.headerContainerView.getScrollY()) != ((float) toScroll)) {
            this.headerContainerView.setScrollY(-toScroll);
        }
    }

    public final void hide() {
        this.hidden = true;
        updateVisibility();
    }

    protected final void updateVisibility() {
        ViewGroup viewGroup = this.headerContainerView;
        int i = (this.hidden || this.hiddenByData) ? 8 : 0;
        viewGroup.setVisibility(i);
    }

    public void setOnHeaderActionListener(OnHeaderActionListener onHeaderActionListener) {
        this.onHeaderActionListener = onHeaderActionListener;
    }
}
