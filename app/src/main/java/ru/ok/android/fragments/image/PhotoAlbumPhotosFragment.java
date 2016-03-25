package ru.ok.android.fragments.image;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.widget.AbsListView.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.image.AlbumHeaderViewController.OnHeaderActionListener;
import ru.ok.android.fragments.web.shortlinks.ShortLink;
import ru.ok.android.model.image.ImageForUpload;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.model.pagination.Page;
import ru.ok.android.model.pagination.PageList;
import ru.ok.android.model.pagination.impl.ItemIdPageAnchor;
import ru.ok.android.model.pagination.impl.PhotoInfoPage;
import ru.ok.android.model.pagination.impl.TwoWayPageAnchor;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.app.IntentUtils;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity;
import ru.ok.android.ui.adapters.photo.PhotoInfosListAdapter;
import ru.ok.android.ui.adapters.photo.PhotoInfosListAdapter.OnNearListEndListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.loadmore.LoadMoreView;
import ru.ok.android.ui.custom.loadmore.LoadMoreView.LoadMoreState;
import ru.ok.android.ui.custom.loadmore.LoadMoreViewData;
import ru.ok.android.ui.custom.photo.PhotoScaleDataProvider;
import ru.ok.android.ui.custom.photo.PhotoTileView;
import ru.ok.android.ui.custom.photo.PhotoTileView.OnPhotoTileClickListener;
import ru.ok.android.ui.custom.photo.PhotoTilesRowView;
import ru.ok.android.ui.custom.photo.TiltListView;
import ru.ok.android.ui.custom.photo.TiltListView.Rotator;
import ru.ok.android.ui.dialogs.PhotoAlbumInfoDialogFragment;
import ru.ok.android.ui.dialogs.PhotoAlbumInfoDialogFragment.PhotoAlbumInfoDialogListener;
import ru.ok.android.ui.dialogs.ProgressDialogFragment;
import ru.ok.android.ui.dialogs.photo.PhotoAlbumEditDialog;
import ru.ok.android.ui.dialogs.photo.PhotoAlbumEditDialog.Builder;
import ru.ok.android.ui.dialogs.photo.PhotoAlbumEditDialog.PhotoAlbumDialogListener;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment;
import ru.ok.android.ui.image.view.PhotoLayerAnimationHelper;
import ru.ok.android.utils.DimenUtils;
import ru.ok.android.utils.Func;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.animation.SyncBus.MessageCallback;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.image.GetPhotoInfoRequest;
import ru.ok.java.api.request.image.GetPhotosRequest.FIELDS;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.Discussion;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.AccessType;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.photo.PhotosInfo;
import ru.ok.model.stream.LikeInfo;
import ru.ok.model.stream.LikeInfoContext;

public final class PhotoAlbumPhotosFragment extends BaseFragment implements OnHeaderActionListener, OnPhotoTileClickListener, PhotoAlbumInfoDialogListener, PhotoAlbumDialogListener, MessageCallback {
    private static int instanceCount;
    private PhotoAlbumInfo albumInfo;
    private int bigPhotoPerSmall;
    private LoadMoreViewData bottomLoadData;
    private LoadMoreView bottomLoadView;
    private LinearLayout currentImageUploadedContainerView;
    private ArrayList<PhotoInfo> deletedInfoList;
    private SmartEmptyViewAnimated emptyView;
    private AlbumHeaderViewController headerViewController;
    protected boolean hideActions;
    private final int instanceId;
    private String nextPageAnchor;
    private boolean pageIsLoading;
    private PageList<PhotoInfo> photoInfoPageList;
    private PhotoOwner photoOwner;
    protected TiltListView photosGridView;
    protected PhotoInfosListAdapter photosInfoAdapter;
    private final Rect tempPhotoTileVisibleRect;
    private LinearLayout uploadedImagesContainersView;
    private List<String> uploadedPhototsIds;
    private UserInfo userInfo;

    /* renamed from: ru.ok.android.fragments.image.PhotoAlbumPhotosFragment.1 */
    class C02991 implements OnClickListener {
        C02991() {
        }

        public void onClick(View view1) {
            if (PhotoAlbumPhotosFragment.this.bottomLoadData.getCurrentState() == LoadMoreState.LOAD_POSSIBLE) {
                PhotoAlbumPhotosFragment.this.loadMore();
            }
        }
    }

    /* renamed from: ru.ok.android.fragments.image.PhotoAlbumPhotosFragment.2 */
    class C03002 implements OnGlobalLayoutListener {
        final /* synthetic */ Bundle val$savedInstanceState;

        C03002(Bundle bundle) {
            this.val$savedInstanceState = bundle;
        }

        public void onGlobalLayout() {
            if (PhotoAlbumPhotosFragment.this.photosGridView.getWidth() != 0 && PhotoAlbumPhotosFragment.this.photosGridView.getHeight() != 0) {
                PhotoAlbumPhotosFragment.this.photosGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                PhotoAlbumPhotosFragment.this.headerViewController.updateScrollPosition();
                PhotoAlbumPhotosFragment.this.prepareGridAdapter();
                PhotoAlbumPhotosFragment.this.photosGridView.setAdapter(PhotoAlbumPhotosFragment.this.photosInfoAdapter);
                if (this.val$savedInstanceState != null) {
                    PhotoAlbumPhotosFragment.this.photosGridView.setSelection(PhotoAlbumPhotosFragment.this.photosInfoAdapter.getBulkPositionForPhotoId(this.val$savedInstanceState.getString("gstt")));
                }
                PhotoAlbumPhotosFragment.this.requestAdditionalData();
            }
        }
    }

    /* renamed from: ru.ok.android.fragments.image.PhotoAlbumPhotosFragment.3 */
    class C03013 implements Rotator {
        C03013() {
        }

        public void setRowRotation(View view, float rotation, float overscrolledBy, int center) {
            if (view instanceof PhotoTilesRowView) {
                PhotoTilesRowView rowView = (PhotoTilesRowView) view;
                for (int i = 0; i < rowView.getChildCount(); i++) {
                    View child = rowView.getChildAt(i);
                    int childPivotY = child.getMeasuredHeight() / 2;
                    child.setPivotX((float) (center - child.getLeft()));
                    child.setPivotY((float) childPivotY);
                    child.setRotationX(rotation);
                }
                return;
            }
            rotate(view, rotation, overscrolledBy, center);
        }

        private void rotate(View view, float rotation, float overscrolledBy, int center) {
            int pivotX;
            int pivotY;
            view.setRotationX(rotation);
            if (overscrolledBy == 0.0f) {
                pivotX = view.getRight() - ((int) (((double) view.getMeasuredWidth()) * 0.5d));
                pivotY = view.getBottom() - ((int) (((double) view.getMeasuredHeight()) * 0.5d));
            } else {
                pivotX = center - view.getLeft();
                pivotY = view.getMeasuredHeight() / 2;
            }
            view.setPivotX((float) pivotX);
            view.setPivotY((float) pivotY);
        }
    }

    /* renamed from: ru.ok.android.fragments.image.PhotoAlbumPhotosFragment.4 */
    class C03024 implements OnStubButtonClickListener {
        C03024() {
        }

        public void onStubButtonClick(Type type) {
            PhotoAlbumPhotosFragment.this.requestAdditionalData();
            PhotoAlbumPhotosFragment.this.setState(0);
        }
    }

    /* renamed from: ru.ok.android.fragments.image.PhotoAlbumPhotosFragment.5 */
    class C03035 implements OnNearListEndListener {
        C03035() {
        }

        public void onNearListEnd() {
            PhotoAlbumPhotosFragment.this.loadMore();
        }
    }

    /* renamed from: ru.ok.android.fragments.image.PhotoAlbumPhotosFragment.6 */
    class C03046 implements Func<PhotoInfo, Boolean> {
        final /* synthetic */ String[] val$photoIds;

        C03046(String[] strArr) {
            this.val$photoIds = strArr;
        }

        public Boolean apply(PhotoInfo input) {
            String photoId = input.getId();
            for (String deleted : this.val$photoIds) {
                if (TextUtils.equals(photoId, deleted)) {
                    return Boolean.valueOf(true);
                }
            }
            return Boolean.valueOf(false);
        }
    }

    /* renamed from: ru.ok.android.fragments.image.PhotoAlbumPhotosFragment.7 */
    class C03057 implements Func<PhotoInfo, Boolean> {
        final /* synthetic */ String val$removedAlbumId;

        C03057(String str) {
            this.val$removedAlbumId = str;
        }

        public Boolean apply(PhotoInfo input) {
            return Boolean.valueOf(TextUtils.equals(input.getAlbumId(), this.val$removedAlbumId));
        }
    }

    /* renamed from: ru.ok.android.fragments.image.PhotoAlbumPhotosFragment.8 */
    class C03068 implements DialogInterface.OnClickListener {
        C03068() {
        }

        public void onClick(DialogInterface dialog, int which) {
            PhotoAlbumPhotosFragment.this.onAlbumDelete();
        }
    }

    public interface OnPhotoClickListener {
        void onPhotoClicked(PhotoTileView photoTileView, String str, PhotoInfo photoInfo, Page<PhotoInfo> page, PhotoOwner photoOwner);
    }

    public PhotoAlbumPhotosFragment() {
        this.deletedInfoList = new ArrayList();
        this.uploadedPhototsIds = new ArrayList();
        this.tempPhotoTileVisibleRect = new Rect();
        int i = instanceCount + 1;
        instanceCount = i;
        this.instanceId = i;
    }

    public static PhotoAlbumPhotosFragment newInstance(String aid, PhotoOwner photoOwner, boolean hideActions) {
        if (TextUtils.equals(aid, "pins") || TextUtils.equals(aid, "tags")) {
            return newInstance(createTagsAlbumInfo(), photoOwner, hideActions);
        }
        PhotoAlbumPhotosFragment fragment = new PhotoAlbumPhotosFragment();
        Bundle args = new Bundle();
        args.putString("aid", aid);
        args.putParcelable("wnrnfo", photoOwner);
        args.putBoolean("hdactns", hideActions);
        fragment.setArguments(args);
        return fragment;
    }

    public static PhotoAlbumInfo createTagsAlbumInfo() {
        return PhotoAlbumsHelper.createVirtualAlbum("tags", LocalizationManager.getString(OdnoklassnikiApplication.getContext(), 2131165285), null);
    }

    public static PhotoAlbumPhotosFragment newInstance(PhotoAlbumInfo albumInfo, PhotoOwner photoOwner, boolean hideActions) {
        PhotoAlbumPhotosFragment fragment = new PhotoAlbumPhotosFragment();
        Bundle args = new Bundle();
        args.putParcelable("anfo", albumInfo);
        args.putParcelable("wnrnfo", photoOwner);
        args.putBoolean("hdactns", hideActions);
        fragment.setArguments(args);
        return fragment;
    }

    protected int getLayoutId() {
        return 2130903374;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Logger.m173d("(%d) onCreate fragment", Integer.valueOf(this.instanceId));
        this.bigPhotoPerSmall = getResources().getInteger(2131427337);
        prepareData(savedInstanceState);
        getActivity().supportInvalidateOptionsMenu();
    }

    private void prepareData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.userInfo = (UserInfo) savedInstanceState.getParcelable("usrnfo");
        }
        tryPopulateNextPageAnchor(savedInstanceState);
        tryPopulateOwner(savedInstanceState);
        tryPopulateAlbum(savedInstanceState);
        tryPopulatePhotoList(savedInstanceState);
        populateHideActions();
        createPhotoInfoListAdapter();
    }

    private void tryPopulateNextPageAnchor(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.nextPageAnchor = savedInstanceState.getString("anchor");
        }
    }

    private void populateHideActions() {
        this.hideActions = getArguments().getBoolean("hdactns");
    }

    private void tryPopulateOwner(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.photoOwner = (PhotoOwner) savedInstanceState.getParcelable("wnrnfo");
        }
        if (this.photoOwner == null) {
            this.photoOwner = (PhotoOwner) getArguments().getParcelable("wnrnfo");
        }
        this.photoOwner.tryPopulateOwner();
    }

    private void tryPopulateAlbum(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.albumInfo = (PhotoAlbumInfo) savedInstanceState.getParcelable("anfo");
        }
        if (this.albumInfo == null && getArguments().containsKey("anfo")) {
            this.albumInfo = ((PhotoAlbumInfo) getArguments().getParcelable("anfo")).clone();
        }
    }

    private void tryPopulatePhotoList(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.photoInfoPageList = (PageList) savedInstanceState.getParcelable("photo_page_list");
        }
        if (this.photoInfoPageList == null && this.albumInfo != null && this.albumInfo.getPhotoCount() == 0 && !TextUtils.equals(this.albumInfo.getId(), "tags")) {
            this.photoInfoPageList = new PageList();
        }
    }

    private void createPhotoInfoListAdapter() {
        this.photosInfoAdapter = new PhotoInfosListAdapter(getContext(), this.deletedInfoList, this.hideActions);
    }

    protected final void requestAdditionalData() {
        if (getActivity() != null) {
            boolean requestAdditionalData = false;
            if (this.photoOwner.getOwnerInfo() == null || this.photoInfoPageList == null || this.albumInfo == null || (this.userInfo == null && this.photoOwner.getType() == 1)) {
                String albumId = getAlbumId();
                Bundle inBundle = new Bundle();
                if (this.albumInfo == null && !"stream".equals(albumId)) {
                    inBundle.putBoolean("ganfo", true);
                    requestAdditionalData = true;
                }
                if (this.photoOwner.getOwnerInfo() == null) {
                    inBundle.putBoolean("gwnrnfo", true);
                    requestAdditionalData = true;
                }
                if (this.photoInfoPageList == null) {
                    inBundle.putBoolean("reset", true);
                    populatePhotosInfosListExtras(inBundle);
                    requestAdditionalData = true;
                }
                if (this.userInfo == null && this.photoOwner.getType() == 1) {
                    if (this.albumInfo == null) {
                        inBundle.putBoolean("gunfo", true);
                    } else if (!TextUtils.isEmpty(this.albumInfo.getUserId())) {
                        inBundle.putBoolean("gunfo", true);
                        inBundle.putString("uid", this.albumInfo.getUserId());
                    }
                    requestAdditionalData = true;
                }
                if (this.albumInfo != null && TextUtils.equals(this.albumInfo.getId(), "tags")) {
                    inBundle.putBoolean("rtmp", true);
                    requestAdditionalData = true;
                }
                if (requestAdditionalData) {
                    inBundle.putString("aid", albumId);
                    inBundle.putParcelable("wnrnfo", this.photoOwner);
                    BusEvent event = new BusEvent(inBundle);
                    Logger.m173d("(%d) request album info batch", Integer.valueOf(this.instanceId));
                    GlobalBus.send(2131623998, event);
                    getActivity().setProgressBarIndeterminateVisibility(true);
                } else if (TextUtils.isEmpty(this.nextPageAnchor)) {
                    removeLoadingFooter();
                }
            } else if (TextUtils.isEmpty(this.nextPageAnchor)) {
                removeLoadingFooter();
            }
        }
    }

    protected final void populatePhotosInfosListExtras(Bundle data) {
        data.putBoolean("gplist", true);
        data.putParcelable("wnrnfo", this.photoOwner);
        data.putString("aid", getAlbumId());
        data.putBoolean("fwd", true);
        data.putInt("plcnt", (40 - (40 % this.bigPhotoPerSmall)) + this.bigPhotoPerSmall);
        RequestFieldsBuilder rfsb = new RequestFieldsBuilder().withPrefix(this.photoOwner.getType() == 1 ? "group_" : null).addField(FIELDS.ALL);
        if (this.photoOwner.getType() == 0) {
            rfsb.addField(GetPhotoInfoRequest.FIELDS.TAG_COUNT);
        }
        data.putString("plflds", rfsb.build());
    }

    public final String getAlbumId() {
        if (this.albumInfo != null) {
            return this.albumInfo.getId();
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getString("aid");
        }
        return null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.m173d("(%d) onCreateView: instanceState=%s", Integer.valueOf(this.instanceId), savedInstanceState);
        View view = inflater.inflate(getLayoutId(), container, false);
        this.headerViewController = new AlbumHeaderViewController(view);
        this.headerViewController.setInfo(this.photoOwner, this.albumInfo, this.userInfo);
        this.headerViewController.setOnHeaderActionListener(this);
        if (this.hideActions) {
            this.headerViewController.hide();
        }
        this.bottomLoadView = new LoadMoreView(getActivity());
        this.bottomLoadView.setLayoutParams(new LayoutParams(-1, DimenUtils.getRealDisplayPixels(48, getActivity())));
        this.bottomLoadData = new LoadMoreViewData();
        this.bottomLoadData.setCurrentState(LoadMoreState.IDLE);
        this.bottomLoadView.setOnClickListener(new C02991());
        this.bottomLoadView.bind(this.bottomLoadData);
        this.photosGridView = (TiltListView) view.findViewById(C0263R.id.grid);
        this.photosGridView.addFooterView(this.bottomLoadView, null, true);
        this.photosGridView.getViewTreeObserver().addOnGlobalLayoutListener(new C03002(savedInstanceState));
        this.photosGridView.setRotator(new C03013());
        this.uploadedImagesContainersView = new LinearLayout(this.photosGridView.getContext());
        this.uploadedImagesContainersView.setOrientation(1);
        this.photosGridView.addHeaderView(this.uploadedImagesContainersView);
        this.emptyView = (SmartEmptyViewAnimated) view.findViewById(2131624434);
        this.emptyView.setButtonClickListener(new C03024());
        if (this.photoInfoPageList != null) {
            if (this.photoInfoPageList.isEmpty()) {
                setState(2);
                removeLoadingFooter();
            } else {
                setState(1);
            }
        }
        return view;
    }

    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter || getParentFragment() == null) {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
        return NESTED_FRAGMENT_EXIT_DUMMY_ANIMATION;
    }

    protected final void prepareGridAdapter() {
        int columnsCount = Math.max(this.photosGridView.getWidth() / this.photosGridView.getContext().getResources().getDimensionPixelSize(2131230747), 3);
        this.photosInfoAdapter.setColumnsCount(columnsCount);
        this.photosInfoAdapter.setTileSize(this.photosGridView.getWidth() / columnsCount);
        Logger.m173d("(%d) prepareGridAdapter: adding photos %s", Integer.valueOf(this.instanceId), this.photoInfoPageList);
        if (this.photoInfoPageList != null) {
            this.photosInfoAdapter.add(this.photoInfoPageList.getAllElements());
        }
        this.photosInfoAdapter.setOnNearListEndListener(new C03035());
        this.photosInfoAdapter.setOnPhotoTileClickListener(this);
    }

    protected final void loadMore() {
        if (getActivity() != null && !this.pageIsLoading && this.nextPageAnchor != null) {
            this.pageIsLoading = true;
            Bundle inBundle = new Bundle();
            populatePhotosInfosListExtras(inBundle);
            inBundle.putString("anchr", this.nextPageAnchor);
            BusEvent event = new BusEvent(inBundle);
            Logger.m173d("(%d) request next album batch: anchor=%s", Integer.valueOf(this.instanceId), this.nextPageAnchor);
            GlobalBus.send(2131623998, event);
            getActivity().setProgressBarIndeterminateVisibility(true);
            this.bottomLoadData.setCurrentState(LoadMoreState.LOADING);
        }
    }

    protected final void setState(int state) {
        switch (state) {
            case RECEIVED_VALUE:
                this.emptyView.setState(State.LOADING);
                this.emptyView.setVisibility(0);
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.emptyView.setVisibility(8);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                this.emptyView.setType(Type.PHOTOS);
                this.emptyView.setState(State.LOADED);
                this.emptyView.setVisibility(0);
            case Message.TYPE_FIELD_NUMBER /*3*/:
            case Message.UUID_FIELD_NUMBER /*5*/:
                this.emptyView.setType(Type.PHOTO_LOAD_FAIL);
                this.emptyView.setState(State.LOADED);
                this.emptyView.setVisibility(0);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                this.emptyView.setType(Type.RESTRICTED);
                this.emptyView.setState(State.LOADED);
                this.emptyView.setVisibility(0);
            default:
        }
    }

    private void removeLoadingFooter() {
        if (this.photosGridView.getAdapter() != null && this.photosGridView.getFooterViewsCount() > 0) {
            this.photosGridView.removeFooterView(this.bottomLoadView);
        }
    }

    public void onResume() {
        Logger.m173d("(%d) onResume", Integer.valueOf(this.instanceId));
        super.onResume();
        hideSingleTile(null);
    }

    public void updateActionBarState() {
        super.updateActionBarState();
        if (getActivity() != null && getParentFragment() == null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            OdklSlidingMenuFragmentActivity.setMenuIndicatorEnable(getActivity(), false);
        }
    }

    public void onStart() {
        super.onStart();
        PhotoLayerAnimationHelper.registerCallback(1, this);
        PhotoLayerAnimationHelper.registerCallback(2, this);
        PhotoLayerAnimationHelper.registerCallback(3, this);
    }

    public void onStop() {
        super.onStop();
        PhotoLayerAnimationHelper.unregisterCallback(1, this);
        PhotoLayerAnimationHelper.unregisterCallback(2, this);
        PhotoLayerAnimationHelper.unregisterCallback(3, this);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("anfo", this.albumInfo);
        outState.putParcelable("wnrnfo", this.photoOwner);
        outState.putParcelable("photo_page_list", this.photoInfoPageList);
        outState.putString("anchor", this.nextPageAnchor);
        if (this.photosInfoAdapter != null) {
            PhotoInfo[] photoInfos = this.photosInfoAdapter.getPhotoInfosForPosition(this.photosGridView.getFirstVisiblePosition());
            if (photoInfos != null) {
                outState.putString("gstt", photoInfos[0].getId());
            }
        }
        outState.putParcelable("usrnfo", this.userInfo);
    }

    public void onPhotoTileClicked(PhotoTileView view, PhotoInfo photo) {
        if (getActivity() != null) {
            ((OnPhotoClickListener) getActivity()).onPhotoClicked(view, getAlbumId(), photo, this.photoInfoPageList.getPageForElement(photo), this.photoOwner);
        }
    }

    protected CharSequence getTitle() {
        if (this.albumInfo != null) {
            return this.albumInfo.getTitle();
        }
        if (TextUtils.isEmpty(getAlbumId())) {
            return getStringLocalized(2131166341);
        }
        return getStringLocalized(2131166597);
    }

    protected CharSequence getSubtitle() {
        if (this.albumInfo == null) {
            return null;
        }
        int count = this.albumInfo.getPhotoCount();
        if (count <= 0) {
            return getStringLocalized(2131166279);
        }
        return count + " " + getStringLocalized(StringUtils.plural((long) count, 2131166349, 2131166350, 2131166351));
    }

    public final PhotoTileView getTileForPhoto(String photoId) {
        if (this.photosGridView == null) {
            return null;
        }
        int count;
        int i;
        int innerCount;
        int j;
        if (!(this.uploadedImagesContainersView == null || this.uploadedImagesContainersView.getChildCount() == 0)) {
            count = this.uploadedImagesContainersView.getChildCount();
            for (i = 0; i < count; i++) {
                LinearLayout inner = (LinearLayout) this.uploadedImagesContainersView.getChildAt(i);
                innerCount = inner.getChildCount();
                for (j = 0; j < innerCount; j++) {
                    PhotoTileView tileView = (PhotoTileView) inner.getChildAt(j);
                    if (TextUtils.equals(tileView.getPhotoInfo().getId(), photoId)) {
                        return tileView;
                    }
                }
            }
        }
        count = this.photosGridView.getChildCount();
        for (i = 0; i < count; i++) {
            View child = this.photosGridView.getChildAt(i);
            if (child instanceof PhotoTilesRowView) {
                PhotoTilesRowView rowView = (PhotoTilesRowView) child;
                innerCount = rowView.getChildCount();
                for (j = 0; j < innerCount; j++) {
                    PhotoTileView photoView = (PhotoTileView) rowView.getChildAt(j);
                    if (TextUtils.equals(photoView.getPhotoInfo().getId(), photoId)) {
                        return photoView;
                    }
                }
                continue;
            }
        }
        return null;
    }

    public final void hideSingleTile(String photoId) {
        if (this.photosGridView != null) {
            int count;
            int i;
            int innerCount;
            int j;
            PhotoInfo info;
            if (!(this.uploadedImagesContainersView == null || this.uploadedImagesContainersView.getChildCount() == 0)) {
                count = this.uploadedImagesContainersView.getChildCount();
                for (i = 0; i < count; i++) {
                    LinearLayout inner = (LinearLayout) this.uploadedImagesContainersView.getChildAt(i);
                    innerCount = inner.getChildCount();
                    for (j = 0; j < innerCount; j++) {
                        PhotoTileView tileView = (PhotoTileView) inner.getChildAt(j);
                        info = tileView.getPhotoInfo();
                        boolean z = info == null || !info.getId().equals(photoId);
                        tileView.setImageViewVisibility(z);
                    }
                }
            }
            count = this.photosGridView.getChildCount();
            for (i = 0; i < count; i++) {
                View child = this.photosGridView.getChildAt(i);
                if (child instanceof PhotoTilesRowView) {
                    PhotoTilesRowView rowView = (PhotoTilesRowView) child;
                    innerCount = rowView.getChildCount();
                    for (j = 0; j < innerCount; j++) {
                        PhotoTileView photoView = (PhotoTileView) rowView.getChildAt(j);
                        info = photoView.getPhotoInfo();
                        boolean visible = info == null || !info.getId().equals(photoId);
                        photoView.setImageViewVisibility(visible);
                    }
                }
            }
            if (this.photosInfoAdapter != null && photoId != null) {
                for (PhotoInfo info2 : this.photosInfoAdapter.getPhotoInfos()) {
                    if (info2 != null && !TextUtils.isEmpty(info2.getId()) && info2.getId().equals(photoId)) {
                        PhotoTileView photoTileView = getTileForPhoto(photoId);
                        if (photoTileView == null || !isPhotoTileViewOnScreen(photoTileView)) {
                            int bulkPosition = this.photosInfoAdapter.getBulkPositionForPhotoId(photoId);
                            if (bulkPosition != LinearLayoutManager.INVALID_OFFSET) {
                                this.photosGridView.setSelection(this.photosGridView.getHeaderViewsCount() + bulkPosition);
                                return;
                            }
                            return;
                        }
                        return;
                    }
                }
            }
        }
    }

    private boolean isPhotoTileViewOnScreen(@NonNull PhotoTileView photoTileView) {
        return photoTileView.getGlobalVisibleRect(this.tempPhotoTileVisibleRect);
    }

    public final void updatePhotoAlbumInfo(PhotoAlbumInfo albumInfo) {
        if (albumInfo != null) {
            this.albumInfo = albumInfo.clone();
        }
        updateActionBarState();
        this.headerViewController.setInfo(this.photoOwner, this.albumInfo, this.userInfo);
    }

    public void onLikesCountClicked(View view) {
        NavigationHelper.showDiscussionLikes(getActivity(), new Discussion(this.albumInfo.getId(), (this.photoOwner.getType() == 0 ? DiscussionGeneralInfo.Type.USER_ALBUM : DiscussionGeneralInfo.Type.GROUP_ALBUM).name()), ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight()).toBundle());
    }

    public void onLikeClicked(View view) {
        if (this.albumInfo.getLikeInfo() != null) {
            if (this.albumInfo.isViewerLiked()) {
                unlike();
            } else {
                like();
            }
        }
    }

    protected void like() {
        Bundle bundle = new Bundle();
        bundle.putString("like_id", this.albumInfo.getLikeInfo().likeId);
        bundle.putString("album_id", this.albumInfo.getId());
        GlobalBus.send(2131624009, new BusEvent(bundle));
        this.albumInfo.setViewerLiked(true);
        this.albumInfo.setLikesCount(this.albumInfo.getLikesCount() + 1);
        getArguments().putParcelable("anfo", this.albumInfo);
        this.headerViewController.setInfo(this.photoOwner, this.albumInfo, this.userInfo);
    }

    protected void unlike() {
        Bundle bundle = new Bundle();
        bundle.putString("like_id", this.albumInfo.getLikeInfo().likeId);
        bundle.putString("album_id", this.albumInfo.getId());
        GlobalBus.send(2131624117, new BusEvent(bundle));
        this.albumInfo.setViewerLiked(false);
        this.albumInfo.setLikesCount(Math.max(this.albumInfo.getLikesCount() - 1, 0));
        getArguments().putParcelable("anfo", this.albumInfo);
        this.headerViewController.setInfo(this.photoOwner, this.albumInfo, this.userInfo);
    }

    public void onInfoHeaderClicked() {
        PhotoAlbumInfoDialogFragment.createInstance(this.photoOwner, this.albumInfo, this.userInfo).show(getChildFragmentManager(), null);
    }

    public void onCommentsClicked(View view) {
        NavigationHelper.showDiscussionCommentsFragment(getActivity(), new Discussion(this.albumInfo.getId(), DiscussionGeneralInfo.Type.USER_ALBUM.name()), MessageBaseFragment.Page.MESSAGES, "", ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight()).toBundle());
    }

    public void onAuthorInfoClicked(UserInfo authorInfo) {
        if (getActivity() != null) {
            NavigationHelper.showUserInfo(getActivity(), authorInfo.uid);
        }
    }

    public void onGroupInfoClicked(GroupInfo groupInfo) {
        if (getActivity() != null) {
            NavigationHelper.showGroupInfo(getActivity(), groupInfo.getId());
        }
    }

    private void startPhotoChooserActivity() {
        if (getActivity() != null) {
            startActivity(IntentUtils.createIntentToAddImages(getActivity(), this.albumInfo, 0, 0, true, true, "imgupldr"));
        }
    }

    @Subscribe(on = 2131623946, to = 2131624157)
    public void onPhotoDeleted(BusEvent event) {
        String pid = event.bundleOutput.getString("pid");
        String[] strArr = new String[]{pid};
        deletePhotoResponse(event, strArr, event.bundleOutput.getString("aid"));
    }

    private void deletePhotoResponse(BusEvent event, String[] photoIds, String albumId) {
        if (event.resultCode == -1) {
            String inputAlbumId = getAlbumId();
            if ("stream".equals(inputAlbumId) || TextUtils.equals(inputAlbumId, albumId)) {
                ensurePhotoInfoPageListInitialized();
                updatePhotosOnDelete(new C03046(photoIds));
                if (this.albumInfo != null) {
                    if (!getArguments().containsKey("anfo")) {
                        this.albumInfo.setPhotoCount(this.albumInfo.getPhotoCount() - 1);
                    }
                    this.headerViewController.setInfo(this.photoOwner, this.albumInfo, this.userInfo);
                }
                updateState();
            }
        }
    }

    private void updatePhotosOnDelete(Func<PhotoInfo, Boolean> filter) {
        Iterator<PhotoInfo> it = this.photoInfoPageList.getAllElements().iterator();
        while (it.hasNext()) {
            PhotoInfo photoInfo = (PhotoInfo) it.next();
            if (((Boolean) filter.apply(photoInfo)).booleanValue()) {
                it.remove();
                this.deletedInfoList.add(photoInfo);
                PhotoTileView tileView = getTileForPhoto(photoInfo.getId());
                if (tileView != null) {
                    tileView.setDarken(true);
                    tileView.invalidate();
                }
            }
        }
    }

    private void updateState() {
        if (this.photoInfoPageList.isEmpty()) {
            this.deletedInfoList.clear();
            this.photoInfoPageList.clear();
            setState(2);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624184)
    public void onPhotoAlbumLiked(BusEvent event) {
        if (event.resultCode == -2) {
            String aid = event.bundleOutput.getString("album_id");
            if (this.albumInfo != null && TextUtils.equals(aid, this.albumInfo.getId())) {
                this.albumInfo.setLikesCount(this.albumInfo.getLikesCount() - 1);
                this.headerViewController.setInfo(this.photoOwner, this.albumInfo, this.userInfo);
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624261)
    public void onAlbumUnliked(BusEvent event) {
        if (event.resultCode == -2) {
            String aid = event.bundleOutput.getString("album_id");
            if (this.albumInfo != null && TextUtils.equals(aid, this.albumInfo.getId())) {
                this.albumInfo.setLikesCount(this.albumInfo.getLikesCount() + 1);
                this.headerViewController.setInfo(this.photoOwner, this.albumInfo, this.userInfo);
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624156)
    public void onPhotoAlbumDelete(BusEvent event) {
        String removedAlbumId = event.bundleInput.getString("aid");
        if (this.albumInfo == null) {
            if ("stream".equals(getAlbumId())) {
                updatePhotosOnDelete(new C03057(removedAlbumId));
                updateState();
            }
        } else if (TextUtils.equals(removedAlbumId, this.albumInfo.getId())) {
            hideProgressDialog();
            if (event.resultCode == -2) {
                Toast.makeText(getActivity(), getStringLocalized(2131165803), 1).show();
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624159)
    public void onPhotoAlbumEdit(BusEvent event) {
        boolean z = true;
        if (this.albumInfo != null) {
            String aid = event.bundleInput.getString("aid");
            String title = event.bundleInput.getString("ttl");
            List<AccessType> accessTypes = AccessType.asList(event.bundleInput.getIntArray("accss"));
            if (TextUtils.equals(aid, this.albumInfo.getId())) {
                hideProgressDialog();
                if (event.resultCode == -1) {
                    this.albumInfo.setTitle(title);
                    this.albumInfo.setTypes(accessTypes);
                    onAlbumInfoRecieved(this.albumInfo);
                    return;
                }
                int errorTextRes = 2131165798;
                if (event.resultCode == 1) {
                    errorTextRes = 2131166248;
                } else if (TextUtils.isEmpty(title) || event.resultCode == 2) {
                    errorTextRes = 2131165839;
                }
                Toast.makeText(getActivity(), getStringLocalized(errorTextRes), 1).show();
                Builder builder = new Builder(getActivity());
                if (this.photoOwner.getType() != 0) {
                    z = false;
                }
                builder.setShowAccessControls(z).setDialogTitle(2131165275).setSubmitBtnText(2131166474).setAlbumAccessTypes(accessTypes).setAlbumTitle(title).show(getChildFragmentManager(), null);
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624160)
    public void onPhotoEdited(BusEvent event) {
        if (event.resultCode == -1) {
            String pid = event.bundleOutput.getString("pid");
            String description = event.bundleOutput.getString("descr");
            ensurePhotoInfoPageListInitialized();
            for (PhotoInfo photoInfo : this.photoInfoPageList.getAllElements()) {
                if (TextUtils.equals(photoInfo.getId(), pid)) {
                    photoInfo.setComment(description);
                    return;
                }
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624191)
    public void onPhotoLiked(BusEvent event) {
        if (event.resultCode == -1 && this.photoInfoPageList != null) {
            String pid = event.bundleOutput.getString("pid");
            ensurePhotoInfoPageListInitialized();
            for (PhotoInfo photoInfo : this.photoInfoPageList.getAllElements()) {
                if (TextUtils.equals(photoInfo.getId(), pid)) {
                    LikeInfoContext oldLikeInfo = photoInfo.getLikeInfo();
                    photoInfo.setLikeInfo(new LikeInfoContext(new LikeInfo.Builder(oldLikeInfo).incrementCount().setSelf(true).build(), oldLikeInfo.entityType, oldLikeInfo.entityId));
                    return;
                }
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624158)
    public void onPhotoTagDeleted(BusEvent event) {
        deletePhotoResponse(event, event.bundleOutput.getStringArray("pids"), event.bundleOutput.getString("aid"));
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        boolean z = true;
        if (!this.hideActions && inflateMenuLocalized(2131689512, menu)) {
            if (this.albumInfo == null) {
                menu.findItem(2131625454).setVisible(false);
                if ("stream".equals(getAlbumId())) {
                    boolean showPhotoItem;
                    if (this.photoOwner == null || !this.photoOwner.isCurrentUser()) {
                        showPhotoItem = false;
                    } else {
                        showPhotoItem = true;
                    }
                    menu.findItem(2131625215).setVisible(showPhotoItem);
                }
            } else if ("tags".equals(this.albumInfo.getId())) {
                menu.findItem(2131625215).setVisible(false);
            } else {
                menu.findItem(2131625215).setVisible(this.albumInfo.isCanAddPhoto());
                menu.findItem(2131625492).setVisible(this.albumInfo.isCanDelete());
                menu.findItem(2131625491).setVisible(this.albumInfo.isCanModify());
                MenuItem findItem = menu.findItem(2131625493);
                if (this.albumInfo.isVirtual()) {
                    z = false;
                }
                findItem.setVisible(z);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean z = false;
        switch (item.getItemId()) {
            case 2131625215:
                startPhotoChooserActivity();
                return true;
            case 2131625454:
                ShortLink.createAlbumLink(this.albumInfo, this.photoOwner).copy(getContext(), true);
                return true;
            case 2131625491:
                Builder builder = new Builder(getActivity());
                if (this.photoOwner.getType() == 0) {
                    z = true;
                }
                builder.setShowAccessControls(z).setDialogTitle(2131165275).setSubmitBtnText(2131166474).setAlbumAccessTypes(this.albumInfo.getTypes()).setAlbumTitle(this.albumInfo.getTitle()).show(getChildFragmentManager(), null);
                return true;
            case 2131625492:
                new AlertDialogWrapper.Builder(getActivity()).setTitle(getStringLocalized(2131165273)).setMessage(getStringLocalized(2131165288, this.albumInfo.getTitle())).setPositiveButton(getStringLocalized(2131165671), new C03068()).setNegativeButton(getStringLocalized(2131165595), null).show();
                return true;
            case 2131625493:
                onInfoHeaderClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onAlbumEditSubmit(PhotoAlbumEditDialog dialog, CharSequence title, List<AccessType> accessTypes) {
        ProgressDialogFragment.createInstance(getStringLocalized(2131166864), false).show(getChildFragmentManager(), "edtalbm");
        Bundle inData = new Bundle();
        inData.putString("aid", this.albumInfo.getId());
        inData.putString("ttl", title.toString());
        inData.putIntArray("accss", AccessType.asIntArray(accessTypes));
        if (this.photoOwner.getType() == 1) {
            inData.putString("gid", this.photoOwner.getId());
        }
        GlobalBus.send(2131623979, new BusEvent(inData));
    }

    public void onAlbumDelete() {
        ProgressDialogFragment.createInstance(getStringLocalized(2131166864), false).show(getChildFragmentManager(), "edtalbm");
        Bundle inData = new Bundle();
        inData.putString("aid", this.albumInfo.getId());
        if (this.photoOwner.getType() == 1) {
            inData.putString("gid", this.photoOwner.getId());
        }
        inData.putInt("pcount", this.albumInfo.getPhotoCount());
        GlobalBus.send(2131623976, new BusEvent(inData));
    }

    protected final void hideProgressDialog() {
        DialogFragment fragment = (DialogFragment) getChildFragmentManager().findFragmentByTag("edtalbm");
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624180)
    public void onPhotoInfoRecieved(BusEvent event) {
        if (event.resultCode == -1) {
            PhotoInfo photoInfo = (PhotoInfo) event.bundleOutput.getParcelable("xtrpi");
            if (photoInfo != null && this.uploadedPhototsIds.contains(photoInfo.getId())) {
                if (this.photoInfoPageList == null || this.photoInfoPageList.isEmpty()) {
                    removeLoadingFooter();
                }
                addUploadedPhoto(photoInfo);
                if (this.albumInfo != null) {
                    this.albumInfo.setPhotoCount(this.albumInfo.getPhotoCount() + 1);
                    this.headerViewController.setInfo(this.photoOwner, this.albumInfo, this.userInfo);
                }
                setState(1);
                this.uploadedPhototsIds.remove(photoInfo.getId());
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624175)
    public void onAlbumInfoBatchEvent(BusEvent event) {
        Logger.m173d("(%s) onAlbumInfoBatchEvent, result=%d", Integer.valueOf(this.instanceId), Integer.valueOf(event.resultCode));
        if (getActivity() != null && TextUtils.equals(event.bundleInput.getString("aid"), getAlbumId())) {
            this.pageIsLoading = false;
            if (event.resultCode == -1) {
                Bundle data = event.bundleOutput;
                onPhotosReceived((PhotosInfo) data.getParcelable("pnfo"), data.getBoolean("reset"));
                onAlbumInfoRecieved((PhotoAlbumInfo) data.getParcelable("anfo"));
                UserInfo userInfoResp = (UserInfo) data.getParcelable("unfo");
                if (userInfoResp != null) {
                    this.userInfo = userInfoResp;
                }
                PhotosInfo tagsPhotosInfo = (PhotosInfo) data.getParcelable("ptgsnfo");
                if (!(tagsPhotosInfo == null || this.albumInfo == null || !TextUtils.equals(this.albumInfo.getId(), "tags"))) {
                    this.albumInfo.setPhotoCount(tagsPhotosInfo.getTotalCount());
                }
                this.headerViewController.setInfo(this.photoOwner, this.albumInfo, this.userInfo);
            } else if (event.resultCode == 2) {
                setState(4);
                this.nextPageAnchor = null;
            } else if (event.resultCode == 3) {
                setState(5);
                this.nextPageAnchor = null;
            } else if (this.photoInfoPageList == null) {
                setState(3);
                this.nextPageAnchor = null;
            } else {
                this.bottomLoadData.setCurrentState(LoadMoreState.LOAD_POSSIBLE);
            }
            getActivity().setProgressBarIndeterminateVisibility(false);
        }
    }

    public final void onPhotosReceived(PhotosInfo photosInfo, boolean resetPhotos) {
        if (photosInfo != null) {
            if (Logger.isLoggingEnable()) {
                Logger.m173d("(%d) onPhotosReceived: %s", Integer.valueOf(this.instanceId), photosInfo);
                List<PhotoInfo> photos = photosInfo.getPhotos();
                int count = photos == null ? 0 : photos.size();
                for (int i = 0; i < count; i++) {
                    Logger.m173d("(%d) onPhotosReceived: photo[%d]=%s", Integer.valueOf(this.instanceId), Integer.valueOf(i), photos.get(i));
                }
            }
            List photos2 = photosInfo.getPhotos();
            ensurePhotoInfoPageListInitialized();
            if (photos2 != null) {
                if (resetPhotos) {
                    this.photoInfoPageList.clear();
                    this.photosInfoAdapter.clear();
                }
                this.photoInfoPageList.addLastPage(new PhotoInfoPage(photos2, new TwoWayPageAnchor(photosInfo.getPagingAnchor())));
                Logger.m173d("(%d) onPhotosReceived: adding photos to adapter: %s", Integer.valueOf(this.instanceId), photos2);
                this.photosInfoAdapter.add(photos2);
                this.photosInfoAdapter.notifyDataSetChanged();
            }
            if (this.photoInfoPageList.isEmpty()) {
                setState(2);
            } else {
                setState(1);
            }
            if (photosInfo.hasMore()) {
                this.nextPageAnchor = photosInfo.getPagingAnchor();
                return;
            }
            this.nextPageAnchor = null;
            removeLoadingFooter();
        }
    }

    private void ensurePhotoInfoPageListInitialized() {
        if (this.photoInfoPageList == null) {
            this.photoInfoPageList = new PageList();
        }
    }

    public final void onAlbumInfoRecieved(PhotoAlbumInfo albumInfo) {
        if (albumInfo != null) {
            this.albumInfo = albumInfo.clone();
            if (getActivity() != null) {
                getSupportActionBar().setTitle(albumInfo.getTitle());
                getActivity().supportInvalidateOptionsMenu();
                updateActionBarState();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Logger.m173d("(%d) onDestroy fragment", Integer.valueOf(this.instanceId));
        PhotoLayerAnimationHelper.unregisterCallback(1, this);
        PhotoLayerAnimationHelper.unregisterCallback(2, this);
        PhotoLayerAnimationHelper.unregisterCallback(3, this);
        getActivity().setProgressBarIndeterminateVisibility(false);
    }

    @Subscribe(on = 2131623946, to = 2131624225)
    public void onImageUploaded(BusEvent event) {
        if (event.resultCode == 1) {
            ImageForUpload image = (ImageForUpload) event.bundleOutput.getParcelable("img");
            if (image.getCurrentStatus() == 5) {
                String inputAlbumId = getAlbumId();
                if (!"stream".equals(getAlbumId()) && (this.albumInfo == null || !TextUtils.equals(this.albumInfo.getId(), image.getAlbumInfo().getId()))) {
                    return;
                }
                if (!TextUtils.isEmpty(inputAlbumId) || this.photoOwner.isCurrentUser()) {
                    this.uploadedPhototsIds.add(image.getRemoteId());
                    requestPhotoInfo(image.getRemoteId());
                }
            }
        }
    }

    private void requestPhotoInfo(String id) {
        Bundle input = new Bundle();
        input.putString("id", id);
        if (this.photoOwner.getType() == 0) {
            input.putString("fid", this.photoOwner.getId());
        } else {
            input.putString("gid", this.photoOwner.getId());
        }
        GlobalBus.send(2131624003, new BusEvent(input));
    }

    private void addUploadedPhoto(PhotoInfo photoInfo) {
        if (this.currentImageUploadedContainerView == null || this.currentImageUploadedContainerView.getChildCount() == this.photosInfoAdapter.getColumnsCount()) {
            this.currentImageUploadedContainerView = new LinearLayout(this.photosGridView.getContext());
            this.currentImageUploadedContainerView.setOrientation(0);
            this.uploadedImagesContainersView.addView(this.currentImageUploadedContainerView, 0);
        }
        PhotoTileView tileView = new PhotoTileView(this.photosGridView.getContext());
        tileView.setPhotoInfo(photoInfo);
        if (this.photosInfoAdapter.getColumnsCount() == 0) {
            prepareGridAdapter();
            this.photosGridView.setAdapter(this.photosInfoAdapter);
        }
        int tileWidth = this.photosGridView.getMeasuredWidth() / this.photosInfoAdapter.getColumnsCount();
        this.currentImageUploadedContainerView.addView(tileView, new LinearLayout.LayoutParams(tileWidth, tileWidth));
        PhotoSize photoSize = (PhotoSize) photoInfo.getSizes().iterator().next();
        if (!TextUtils.isEmpty(photoSize.getUrl())) {
            tileView.setImageUri(photoSize.getUri());
            tileView.setOnPhotoTileClickListener(this);
        }
        ensurePhotoInfoPageListInitialized();
        this.photoInfoPageList.addFirstPage(new PhotoInfoPage(Collections.singletonList(photoInfo), new ItemIdPageAnchor(photoInfo.getId(), photoInfo.getId())));
    }

    public Bundle onMessage(android.os.Message message) {
        String photoId = message.getData().getString("id");
        if (message.what == 1) {
            hideSingleTile(photoId);
        } else if (message.what == 2) {
            PhotoScaleDataProvider tileView = getTileForPhoto(photoId);
            if (tileView != null) {
                return PhotoLayerAnimationHelper.makeScaleDownAnimationBundle(tileView);
            }
        } else if (message.what == 3) {
            PhotoTileView tileView2 = getTileForPhoto(photoId);
            if (tileView2 != null) {
                tileView2.setImageViewVisibility(true);
            }
        }
        return null;
    }

    static {
        instanceCount = 0;
    }
}
