package ru.ok.android.ui.image.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.web.shortlinks.ShortLink;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.model.pagination.Page;
import ru.ok.android.model.pagination.PageAnchor;
import ru.ok.android.model.pagination.impl.ItemIdPageAnchor;
import ru.ok.android.model.pagination.impl.TwoWayPageAnchor;
import ru.ok.android.onelog.PhotoLayerLogger.PageScrollLogListener;
import ru.ok.android.services.like.LikeManager;
import ru.ok.android.services.marks.MarksManager;
import ru.ok.android.storage.Storages;
import ru.ok.android.ui.adapters.photo.PhotoLayerAdapter;
import ru.ok.android.ui.adapters.photo.PhotoLayerAdapter.PhotoAdapterListItem;
import ru.ok.android.ui.adapters.photo.PhotoLayerAdapter.TearListItem;
import ru.ok.android.ui.adapters.photo.StreamPhotoLayerAdapter;
import ru.ok.android.ui.adapters.photo.StreamPhotoLayerAdapter.PhotoInfoListItem;
import ru.ok.android.ui.custom.photo.AbstractPhotoInfoView;
import ru.ok.android.ui.custom.photo.AbstractPhotoInfoView.OnPhotoActionListener;
import ru.ok.android.ui.custom.photo.AbstractPhotoView;
import ru.ok.android.ui.custom.photo.ActionToastView;
import ru.ok.android.ui.custom.photo.ScrollBlockingViewPager;
import ru.ok.android.ui.custom.photo.ScrollBlockingViewPager.BlockingViewPagerListener;
import ru.ok.android.ui.custom.photo.StaticPhotoInfoView;
import ru.ok.android.ui.dialogs.EditTextDialogFragment.Builder;
import ru.ok.android.ui.dialogs.EditTextDialogFragment.EditTextDialogListener;
import ru.ok.android.ui.dialogs.PhotoInfoDialogFragment;
import ru.ok.android.ui.dialogs.PhotoInfoDialogFragment.PhotoInfoDialogListener;
import ru.ok.android.ui.dialogs.ProgressDialogFragment;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment;
import ru.ok.android.ui.image.PreviewDataHolder;
import ru.ok.android.ui.image.view.PhotoInfoListController.ListControllerCallback;
import ru.ok.android.ui.image.view.PhotoInfoProvider.OnBatchReceiveListener;
import ru.ok.android.ui.image.view.PhotoInfoProvider.OnFullPhotoInfoReceiveListener;
import ru.ok.android.ui.image.view.PhotoInfoProvider.OnPhotoInfoReceiveListener;
import ru.ok.android.ui.image.view.PhotoInfoProvider.OnPhotoInfosReceiveListener;
import ru.ok.android.ui.image.view.PhotoInfoProvider.OnPhotoTagsReceivedListener;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.animation.SimpleAnimationListener;
import ru.ok.android.utils.animation.SimpleAnimatorListener;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.pagetransformer.ControllablePageTransformer.PageTransformerCallback;
import ru.ok.android.utils.pagetransformer.RemoveBackwardsTransformer;
import ru.ok.android.utils.pagetransformer.RemoveForwardPageTransformer;
import ru.ok.android.utils.pagetransformer.ZoomOutPageTransformer;
import ru.ok.java.api.request.image.MarkPhotoSpamRequest.PhotoType;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type;
import ru.ok.model.Discussion;
import ru.ok.model.GroupInfo;
import ru.ok.model.Identifiable;
import ru.ok.model.UserInfo;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.photo.PhotoTag;
import ru.ok.model.photo.PhotosInfo;
import ru.ok.model.stream.LikeInfoContext;

public final class StreamPhotosLayerActivity extends PhotoLayerActivity implements OnPhotoActionListener, EditTextDialogListener, PhotoInfoDialogListener, ListControllerCallback, OnBatchReceiveListener, OnFullPhotoInfoReceiveListener, OnPhotoInfoReceiveListener, OnPhotoInfosReceiveListener, OnPhotoTagsReceivedListener, PageTransformerCallback {
    protected FrameLayout actionToastContainerView;
    private String albumId;
    private boolean batchDataReceived;
    protected int blockPagerOffset;
    protected Context context;
    private boolean fromNativeAlbum;
    protected StreamPhotoLayerAdapter imagesPagerAdapter;
    private int initialRealPosition;
    private LikeManager likeManager;
    private MarksManager marksManager;
    private PhotoInfo photoInfo;
    private PhotoInfoProvider photoInfoProvider;
    private PhotoInfoListController photoListController;
    private PhotoOwner photoOwner;
    private String[] spids;

    /* renamed from: ru.ok.android.ui.image.view.StreamPhotosLayerActivity.10 */
    class AnonymousClass10 implements OnClickListener {
        final /* synthetic */ PhotoInfo val$photoInfo;

        AnonymousClass10(PhotoInfo photoInfo) {
            this.val$photoInfo = photoInfo;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            StreamPhotosLayerActivity.this.showProgressDialog();
            Bundle bundleInput = new Bundle();
            bundleInput.putString("pid", this.val$photoInfo.getId());
            bundleInput.putString("aid", this.val$photoInfo.getAlbumId());
            bundleInput.putString("oid", this.val$photoInfo.getOwnerId());
            if (StreamPhotosLayerActivity.this.photoOwner.getType() == 1) {
                bundleInput.putString("gid", StreamPhotosLayerActivity.this.photoOwner.getId());
            }
            GlobalBus.send(2131623977, new BusEvent(bundleInput));
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.StreamPhotosLayerActivity.11 */
    class AnonymousClass11 implements OnClickListener {
        final /* synthetic */ PhotoInfo val$photoInfo;

        AnonymousClass11(PhotoInfo photoInfo) {
            this.val$photoInfo = photoInfo;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            StreamPhotosLayerActivity.this.showProgressDialog();
            Bundle inData = new Bundle();
            inData.putString("pid", this.val$photoInfo.getId());
            inData.putString("aid", this.val$photoInfo.getAlbumId());
            if (this.val$photoInfo.getOwnerType() == OwnerType.GROUP) {
                inData.putString("gid", this.val$photoInfo.getOwnerId());
            }
            inData.putParcelable("pnfo", this.val$photoInfo);
            GlobalBus.send(2131624114, new BusEvent(inData));
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.StreamPhotosLayerActivity.12 */
    class AnonymousClass12 implements OnClickListener {
        final /* synthetic */ PhotoInfo val$photoInfo;

        AnonymousClass12(PhotoInfo photoInfo) {
            this.val$photoInfo = photoInfo;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            StreamPhotosLayerActivity.this.showProgressDialog();
            Bundle bundleInput = new Bundle();
            bundleInput.putString("pid", this.val$photoInfo.getId());
            GlobalBus.send(2131624115, new BusEvent(bundleInput));
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.StreamPhotosLayerActivity.13 */
    class AnonymousClass13 implements OnClickListener {
        final /* synthetic */ PhotoInfo val$photoInfo;

        AnonymousClass13(PhotoInfo photoInfo) {
            this.val$photoInfo = photoInfo;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            StreamPhotosLayerActivity.this.showProgressDialog();
            Bundle bundleInput = new Bundle();
            bundleInput.putStringArray("pids", new String[]{this.val$photoInfo.getId()});
            bundleInput.putString("aid", StreamPhotosLayerActivity.this.albumId);
            GlobalBus.send(2131623978, new BusEvent(bundleInput));
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.StreamPhotosLayerActivity.1 */
    class C10241 extends SimpleOnPageChangeListener {
        private final PageScrollLogListener scrollLogger;

        C10241() {
            this.scrollLogger = new PageScrollLogListener(StreamPhotosLayerActivity.this.photoLayerLogger);
        }

        public void onPageSelected(int position) {
            this.scrollLogger.onPageSelected(position);
            StreamPhotosLayerActivity.this.onPageSelected(position);
        }

        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            this.scrollLogger.onPageScrollStateChanged(state);
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            StreamPhotosLayerActivity.this.onPageScrolled(position, positionOffsetPixels);
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.StreamPhotosLayerActivity.2 */
    class C10252 implements BlockingViewPagerListener {
        C10252() {
        }

        public boolean shouldNavigateToPosition(int position) {
            return !(StreamPhotosLayerActivity.this.photoListController.getItemByPosition(StreamPhotosLayerActivity.this.imagesPagerAdapter.getRealPosition(position)) instanceof TearListItem);
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.StreamPhotosLayerActivity.3 */
    class C10263 implements Identifiable {
        final /* synthetic */ String val$finalPhotoId;

        C10263(String str) {
            this.val$finalPhotoId = str;
        }

        public String getId() {
            return this.val$finalPhotoId;
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.StreamPhotosLayerActivity.4 */
    class C10274 implements View.OnClickListener {
        final /* synthetic */ int val$photoPos;

        C10274(int i) {
            this.val$photoPos = i;
        }

        public void onClick(View view) {
            ActionToastManager.hideToastFrom(StreamPhotosLayerActivity.this.actionToastContainerView, (ActionToastView) view);
            StreamPhotosLayerActivity.this.setPagerRealPosition(this.val$photoPos, true);
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.StreamPhotosLayerActivity.5 */
    class C10285 implements AnimatorUpdateListener {
        private int prev;
        final /* synthetic */ ViewPager val$viewPager;

        C10285(ViewPager viewPager) {
            this.val$viewPager = viewPager;
            this.prev = 0;
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            int value = ((Integer) animation.getAnimatedValue()).intValue();
            int dragBy = value - this.prev;
            if (this.val$viewPager.isFakeDragging()) {
                this.val$viewPager.fakeDragBy((float) (-dragBy));
            }
            this.prev = value;
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.StreamPhotosLayerActivity.6 */
    class C10296 extends SimpleAnimatorListener {
        final /* synthetic */ int val$currentPosition;
        final /* synthetic */ ViewPager val$viewPager;

        C10296(ViewPager viewPager, int i) {
            this.val$viewPager = viewPager;
            this.val$currentPosition = i;
        }

        public void onAnimationEnd(Animator animation) {
            if (this.val$viewPager.isFakeDragging()) {
                this.val$viewPager.endFakeDrag();
            }
            this.val$viewPager.setPageTransformer(true, null);
            int newPosition = StreamPhotosLayerActivity.this.photoListController.removeItemByPosition(this.val$currentPosition);
            if (newPosition == -1) {
                StreamPhotosLayerActivity.this.finish();
                return;
            }
            StreamPhotosLayerActivity.this.onPagerDataUpdated(newPosition, false);
            this.val$viewPager.setPageTransformer(true, new ZoomOutPageTransformer(StreamPhotosLayerActivity.this));
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.StreamPhotosLayerActivity.7 */
    class C10317 extends SimpleAnimationListener {
        final /* synthetic */ int val$currentPosition;
        final /* synthetic */ View val$view;

        /* renamed from: ru.ok.android.ui.image.view.StreamPhotosLayerActivity.7.1 */
        class C10301 implements Runnable {
            C10301() {
            }

            public void run() {
                int newPosition = StreamPhotosLayerActivity.this.photoListController.removeItemByPosition(C10317.this.val$currentPosition);
                if (newPosition == -1) {
                    StreamPhotosLayerActivity.this.finish();
                } else {
                    StreamPhotosLayerActivity.this.onPagerDataUpdated(newPosition, false);
                }
            }
        }

        C10317(View view, int i) {
            this.val$view = view;
            this.val$currentPosition = i;
        }

        public void onAnimationEnd(Animation animation) {
            this.val$view.setVisibility(4);
            this.val$view.clearAnimation();
            this.val$view.postDelayed(new C10301(), 1);
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.StreamPhotosLayerActivity.8 */
    class C10328 implements View.OnClickListener {
        final /* synthetic */ MenuItem val$tagsItem;

        C10328(MenuItem menuItem) {
            this.val$tagsItem = menuItem;
        }

        public void onClick(View view) {
            StreamPhotosLayerActivity.this.onOptionsItemSelected(this.val$tagsItem);
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.StreamPhotosLayerActivity.9 */
    class C10339 implements OnClickListener {
        final /* synthetic */ PhotoInfo val$photoInfo;

        C10339(PhotoInfo photoInfo) {
            this.val$photoInfo = photoInfo;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            StreamPhotosLayerActivity.this.showProgressDialog();
            Bundle bundleInput = new Bundle();
            bundleInput.putString("pid", this.val$photoInfo.getId());
            bundleInput.putInt("ptype", (StreamPhotosLayerActivity.this.photoOwner.getType() == 0 ? PhotoType.USER : PhotoType.GROUP).ordinal());
            GlobalBus.send(2131624089, new BusEvent(bundleInput));
        }
    }

    public StreamPhotosLayerActivity() {
        this.context = this;
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        this.blockPagerOffset = getResources().getDimensionPixelSize(2131231216);
        initActionToastContainerView();
        initState(savedInstanceState);
        startAnimation(savedInstanceState);
        this.likeManager = Storages.getInstance(this.context, OdnoklassnikiApplication.getCurrentUser().getId()).getLikeManager();
    }

    private void initState(Bundle savedInstanceState) {
        initPhotoListController();
        initPhotoInfoProvider();
        if (savedInstanceState != null) {
            this.initialRealPosition = savedInstanceState.getInt("position");
            this.photoListController.onRestoreInstanceState(savedInstanceState);
            this.photoOwner = (PhotoOwner) savedInstanceState.getParcelable("ownerInfo");
            this.albumId = savedInstanceState.getString("albumId");
            this.spids = savedInstanceState.getStringArray("sequenceIds");
            return;
        }
        this.fromNativeAlbum = getIntent().getBooleanExtra("fromNativeAlbum", false);
        this.photoOwner = (PhotoOwner) getIntent().getParcelableExtra("ownerInfo");
        this.photoOwner.tryPopulateOwner();
        this.albumId = getIntent().getStringExtra("albumId");
        this.photoInfo = (PhotoInfo) getIntent().getParcelableExtra("photoInfo");
        this.spids = getIntent().getStringArrayExtra("sequenceIds");
    }

    protected int getInitialRealPosition() {
        return this.initialRealPosition;
    }

    @Nullable
    private PageAnchor buildPageAnchor(String photoId) {
        if (photoId != null) {
            return new ItemIdPageAnchor(photoId, photoId);
        }
        if (this.spids != null && this.spids.length > 0) {
            return new ItemIdPageAnchor(this.spids[0], this.spids[this.spids.length - 1]);
        }
        Logger.m185w("Not possible to construct page anchor for owner (%s) and albumId (%s)", this.photoOwner, this.albumId);
        return null;
    }

    private void initPhotoInfoProvider() {
        this.photoInfoProvider = PhotoInfoProvider.findOrCreateRetainFragment(getSupportFragmentManager());
    }

    private void initPhotoListController() {
        this.photoListController = new PhotoInfoListController();
        this.photoListController.setListControllerCallback(this);
    }

    private void initActionToastContainerView() {
        this.actionToastContainerView = (FrameLayout) findViewById(2131624546);
    }

    protected void doPreparePager() {
        ScrollBlockingViewPager sbViewPager = getPagerView();
        sbViewPager.setOnPageChangeListener(new C10241());
        sbViewPager.setBlockingViewPagerListener(new C10252());
        super.doPreparePager();
    }

    private void onPageScrolled(int virtualPosition, int positionOffsetPixels) {
        boolean z = true;
        if (this.imagesPagerAdapter != null) {
            int realPosition = this.imagesPagerAdapter.getRealPosition(virtualPosition);
            ScrollBlockingViewPager pagerView;
            if (this.photoListController.getItemByPosition(realPosition).getType() == 1) {
                int offset = getPagerView().getWidth() - positionOffsetPixels;
                pagerView = getPagerView();
                if (offset < this.blockPagerOffset) {
                    z = false;
                }
                pagerView.setBlockScrollToRight(z);
                return;
            }
            realPosition++;
            if (this.photoListController.getCount() > realPosition && this.photoListController.getItemByPosition(realPosition).getType() == 1) {
                pagerView = getPagerView();
                if (positionOffsetPixels < this.blockPagerOffset) {
                    z = false;
                }
                pagerView.setBlockScrollToLeft(z);
            }
        }
    }

    private void onPageSelected(int virtualPosition) {
        int realPosition = this.imagesPagerAdapter.getRealPosition(virtualPosition);
        this.photoListController.checkNearTearPosition(realPosition);
        onPhotoSelected(realPosition);
    }

    protected void onAnimationNotExists(int realPosition) {
        onEnter(realPosition, null);
    }

    private void onEnter(int realPosition, Uri previewUri) {
        Logger.m172d("onEnter");
        if (this.photoListController.hasItems()) {
            Logger.m172d("Has items");
            preparePager();
            onPagerDataUpdated(realPosition, false);
            return;
        }
        Logger.m172d("No items");
        requestData(addFirstPageIfNecessaryAndGetItsAnchor(previewUri));
    }

    private void requestData(@Nullable PageAnchor anchor) {
        String photoId = getIdentifiableFromIntent().getId();
        if (anchor == null) {
            anchor = buildPageAnchor(photoId);
        }
        this.photoInfoProvider.requestInfoBatch(false, this.albumId, true, photoId, this.spids, this.photoOwner, anchor);
    }

    protected void onAnimationEnd(Uri previewUri) {
        onEnter(getInitialRealPosition(), previewUri);
        clearAfterAnimation();
    }

    protected Identifiable getIdentifiableFromIntent() {
        String photoId = null;
        if (this.photoInfo != null) {
            photoId = this.photoInfo.getId();
        }
        return new C10263(photoId != null ? photoId : getIntent().getStringExtra("photoId"));
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("ownerInfo", this.photoOwner);
        outState.putString("albumId", this.albumId);
        outState.putStringArray("sequenceIds", this.spids);
        if (this.photoListController != null) {
            this.photoListController.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    protected PhotoLayerAdapter getViewImagesAdapter() {
        return this.imagesPagerAdapter;
    }

    public void onPhotosRequired(String anchor, boolean forward, boolean forceLoad) {
        if (!this.batchDataReceived) {
            return;
        }
        if (this.photoListController.getCount() > 3 || TextUtils.isEmpty(anchor) || forceLoad) {
            this.photoInfoProvider.requestUserAlbumPhotosInfos(this.albumId, anchor, forward, this.photoOwner);
        }
    }

    public void onBatchReceived(@Nullable PageAnchor anchor, PhotoInfo photoInfo, PhotosInfo backwardPhotosInfo, PhotosInfo forwardPhotosInfo, PhotoAlbumInfo albumInfo, PhotoOwner photoOwner, List<PhotoInfo> spinfos) {
        this.batchDataReceived = true;
        int trackPosition = Math.max(0, getCurrentRealPosition());
        boolean hasMoreItems = hasMorePhotos() || hasPhotos(backwardPhotosInfo) || hasPhotos(forwardPhotosInfo);
        boolean hasMoreInBackward = backwardPhotosInfo != null && backwardPhotosInfo.hasMore();
        boolean hasMoreInForward = forwardPhotosInfo != null && forwardPhotosInfo.hasMore();
        if (photoInfo != null) {
            this.photoInfo = photoInfo;
            if (this.photoListController.updatePhotoInfo(photoInfo)) {
                this.imagesPagerAdapter.notifyItemChanged(photoInfo.getId());
            } else {
                trackPosition = this.photoListController.addInitialPage(photoInfo, hasMoreItems);
            }
        } else if (!(spinfos == null || spinfos.isEmpty())) {
            boolean update = true;
            for (PhotoInfo photoInfo1 : spinfos) {
                boolean singleUpdate = this.photoListController.updatePhotoInfo(photoInfo1);
                if (singleUpdate) {
                    this.imagesPagerAdapter.notifyItemChanged(photoInfo1.getId());
                }
                update &= singleUpdate;
            }
            if (!update) {
                if (anchor == null) {
                    PageAnchor itemIdPageAnchor = new ItemIdPageAnchor(((PhotoInfo) spinfos.get(0)).getId(), ((PhotoInfo) spinfos.get(spinfos.size() - 1)).getId());
                }
                trackPosition = this.photoListController.addInitialPage(spinfos, anchor, trackPosition, hasMoreItems);
            }
        }
        if (backwardPhotosInfo != null) {
            List<PhotoInfo> normalizedBackwardPhotoInfos = removeExistingPhotosIfNecessary(backwardPhotosInfo);
            String backwardPhotosAnchor = backwardPhotosInfo.getPagingAnchor();
            trackPosition = this.photoListController.addPageInDirection(normalizedBackwardPhotoInfos, anchor != null ? anchor.getBackwardAnchor() : null, new TwoWayPageAnchor(backwardPhotosAnchor), PagingDirection.BACKWARD, hasMoreInBackward, trackPosition);
            if ((normalizedBackwardPhotoInfos == null || normalizedBackwardPhotoInfos.isEmpty()) && hasMoreInBackward) {
                onPhotosRequired(backwardPhotosAnchor, false, true);
            }
        }
        if (!hasMoreInBackward && hasMoreInForward) {
            onPhotosRequired(null, false, false);
        }
        if (forwardPhotosInfo != null) {
            List<PhotoInfo> normalizedForwardPhotoInfos = removeExistingPhotosIfNecessary(forwardPhotosInfo);
            String forwardPhotosAnchor = forwardPhotosInfo.getPagingAnchor();
            trackPosition = this.photoListController.addPageInDirection(normalizedForwardPhotoInfos, anchor != null ? anchor.getForwardAnchor() : null, new TwoWayPageAnchor(forwardPhotosAnchor), PagingDirection.FORWARD, forwardPhotosInfo.hasMore(), trackPosition);
            if ((normalizedForwardPhotoInfos == null || normalizedForwardPhotoInfos.isEmpty()) && hasMoreInForward) {
                onPhotosRequired(forwardPhotosAnchor, true, true);
            }
        }
        if (hasMoreInBackward && !hasMoreInForward) {
            onPhotosRequired(null, true, false);
        }
        if (!(hasMoreInBackward || hasMoreInForward)) {
            trackPosition = this.photoListController.removeTearItemsIfNecessary(trackPosition);
        }
        if (!isOpenDecorOnChildLayout()) {
            getDecorViewsHandler().setVisibilityChangeLocked(false);
            getDecorViewsHandler().setDecorVisibility(true, true);
        }
        preparePager();
        onPagerDataUpdated(trackPosition, false);
        this.photoOwner = photoOwner;
    }

    private boolean hasPhotos(@Nullable PhotosInfo photosInfo) {
        return photosInfo != null && photosInfo.hasPhotos();
    }

    public void onBatchRequestFailed(String anchorId, int error) {
        if (error != 4 || this.imagesPagerAdapter == null) {
            showError(error);
        }
    }

    @Deprecated
    public void onPhotoInfoReceived(PhotoInfo photoInfo) {
    }

    public void onPhotoInfosRequestFailed(String anchorId, String aid, boolean forward) {
    }

    public void onPhotosInfoReceived(PhotosInfo photosInfo, boolean forward, String requestAnchor) {
        if (hasPhotos(photosInfo) || TextUtils.isEmpty(requestAnchor)) {
            onPagerDataUpdated(this.photoListController.addPageInDirection(removeExistingPhotosIfNecessary(photosInfo), requestAnchor, new TwoWayPageAnchor(photosInfo.getPagingAnchor()), forward ? PagingDirection.FORWARD : PagingDirection.BACKWARD, photosInfo.hasMore(), Math.max(0, getCurrentRealPosition())), false);
            return;
        }
        onPhotosRequired(null, forward, false);
    }

    private List<PhotoInfo> removeExistingPhotosIfNecessary(PhotosInfo photosInfo) {
        if (!photosInfo.hasPhotos()) {
            return null;
        }
        Set<String> photoIdsToRemove = new HashSet();
        if (this.photoInfo != null) {
            photoIdsToRemove.add(this.photoInfo.getId());
        }
        if (this.spids != null && this.spids.length > 0) {
            photoIdsToRemove.addAll(Arrays.asList(this.spids));
        }
        List<PhotoInfo> resultPhotoInfos = new ArrayList(photosInfo.getPhotos());
        Iterator<PhotoInfo> it = resultPhotoInfos.iterator();
        while (it.hasNext()) {
            if (photoIdsToRemove.contains(((PhotoInfo) it.next()).getId())) {
                it.remove();
            }
        }
        return resultPhotoInfos;
    }

    public void onPhotoInfoRequestFailed(String photoId) {
    }

    public void onFullPhotoInfoReceived(PhotoInfo photoInfo, PhotoAlbumInfo albumInfo, UserInfo userInfo, GroupInfo groupInfo) {
        hideProgressDialog();
        if (this.photoOwner.getType() == 0 && this.photoOwner.isCurrentUser() && TextUtils.equals(photoInfo.getOwnerId(), this.photoOwner.getId())) {
            userInfo = (UserInfo) this.photoOwner.getOwnerInfo();
        }
        PhotoInfoDialogFragment.newInstance(albumInfo, userInfo, groupInfo, photoInfo).show(getSupportFragmentManager(), null);
    }

    public void onFullPhotoRequestFailed(String photoId) {
        hideProgressDialog();
    }

    public void onOwnerInfoClicked(UserInfo authorInfo) {
        NavigationHelper.showUserInfo(this, authorInfo.uid);
    }

    public void onGroupInfoClicked(GroupInfo groupInfo) {
        NavigationHelper.showGroupInfo(this, groupInfo.getId());
    }

    public void onAlbumInfoClicked(PhotoAlbumInfo albumInfo) {
        int ownerType;
        String ownerId;
        if (albumInfo.getOwnerType() == OwnerType.USER) {
            ownerType = 0;
            ownerId = albumInfo.getUserId();
        } else {
            ownerType = 1;
            ownerId = albumInfo.getGroupId();
        }
        goToAlbum(albumInfo.getId(), ownerType, ownerId, false);
    }

    private void goToAlbum(String aid, int ownerType, String ownerId, boolean exiting) {
        if (this.fromNativeAlbum && TextUtils.equals(aid, this.albumId)) {
            transitBack(true);
            return;
        }
        if (ownerType == 1) {
            NavigationHelper.showGroupPhotoAlbum(this, ownerId, aid);
        } else {
            NavigationHelper.showUserPhotoAlbum(this, ownerId, aid);
        }
        if (exiting) {
            finish();
        }
    }

    protected void throwAway(AbstractPhotoView photoView, boolean throwUp) {
        super.throwAway(photoView, throwUp);
        if (this.actionToastContainerView != null) {
            this.actionToastContainerView.setVisibility(8);
        }
    }

    protected void setContentViewsVisibility(boolean visible) {
        super.setContentViewsVisibility(visible);
        int visibility = visible ? 0 : 4;
        if (this.actionToastContainerView != null) {
            this.actionToastContainerView.setVisibility(visibility);
        }
    }

    private PageAnchor addFirstPageIfNecessaryAndGetItsAnchor(Uri previewUri) {
        boolean z = true;
        String str = "Preview uri is not null: %s";
        Object[] objArr = new Object[1];
        if (previewUri == null) {
            z = false;
        }
        objArr[0] = Boolean.valueOf(z);
        Logger.m173d(str, objArr);
        PageAnchor pageAnchor = null;
        boolean hasMorePhotos = hasMorePhotos();
        Page<PhotoInfo> photoInfoPage = (Page) getIntent().getParcelableExtra("photoInfoPage");
        int trackPosition = 0;
        if (photoInfoPage != null) {
            Logger.m172d("Set preview uri first!");
            trackPosition = photoInfoPage.getElementOffset(this.photoInfo);
            ((PhotoInfo) photoInfoPage.getElements().get(trackPosition)).setPreviewUri(previewUri);
            pageAnchor = photoInfoPage.getAnchor();
            trackPosition = this.photoListController.addInitialPage(photoInfoPage.getElements(), photoInfoPage.getAnchor(), trackPosition, hasMorePhotos);
        } else if (this.photoInfo != null) {
            Logger.m172d("Set preview uri second!");
            this.photoInfo.setPreviewUri(previewUri);
            trackPosition = this.photoListController.addInitialPage(this.photoInfo, hasMorePhotos);
            pageAnchor = new ItemIdPageAnchor(this.photoInfo.getId(), this.photoInfo.getId());
        }
        this.initialRealPosition = trackPosition;
        if (pageAnchor != null) {
            preparePager();
            onPagerDataUpdated(trackPosition, false);
        }
        return pageAnchor;
    }

    private boolean hasMorePhotos() {
        return this.photoOwner.getType() == 0 || !TextUtils.isEmpty(this.albumId);
    }

    protected void onPagerDataUpdated() {
        this.imagesPagerAdapter.setItems(this.photoListController.getItems());
    }

    protected PhotoLayerAdapter createViewImageAdapter(@NonNull DecorHandler decorHandler, @NonNull ProgressSyncHelper syncHelper, @Nullable PreviewDataHolder previewDataHolder) {
        if (this.imagesPagerAdapter != null) {
            return this.imagesPagerAdapter;
        }
        getPagerView().setPageTransformer(true, new ZoomOutPageTransformer(this));
        if (DeviceUtils.getMemoryClass(this) < 24) {
            getPagerView().setOffscreenPageLimit(0);
        }
        this.imagesPagerAdapter = new StreamPhotoLayerAdapter(this, this.photoInfoProvider, decorHandler, this.photoListController.getItems(), this.photoOwner, syncHelper, previewDataHolder);
        this.imagesPagerAdapter.setOnPhotoActionListener(this);
        return this.imagesPagerAdapter;
    }

    public boolean shouldApplyTransformation(View page, float position) {
        if (!(page instanceof AbstractPhotoInfoView)) {
            return false;
        }
        int prevPosition = getPositionForId(((AbstractPhotoInfoView) page).getPhotoId()) - 1;
        if (prevPosition < 0) {
            return !(this.photoListController.getItemByPosition(this.photoListController.getCount() + -1) instanceof TearListItem);
        } else if (this.photoListController.getItemByPosition(prevPosition) instanceof TearListItem) {
            return false;
        } else {
            return true;
        }
    }

    protected void onPhotoSelected(int realPosition) {
        updateTagsViewForItem(realPosition);
        supportInvalidateOptionsMenu();
        updateUnderlyingFragment(realPosition);
    }

    private void updateUnderlyingFragment(int photoRealPosition) {
        if (!isFinishing() && this.photoListController != null && this.photoListController.getItemByPosition(photoRealPosition) != null) {
            PhotoAdapterListItem listItem = this.photoListController.getItemByPosition(photoRealPosition);
            if (listItem.getType() == 2) {
                notifyPhotoSelected(((PhotoInfoListItem) listItem).getPhotoInfo());
            }
        }
    }

    protected final void notifyPhotoSelected(Identifiable identifiable) {
        String id = null;
        Message msg = Message.obtain(null, 1);
        if (identifiable != null) {
            id = identifiable.getId();
        }
        msg.getData().putString("id", id);
        PhotoLayerAnimationHelper.sendMessage(msg);
    }

    private void updateTagsViewForItem(int realPosition) {
        if (realPosition > 0 && realPosition < this.photoListController.getCount() && this.photoListController.getItemByPosition(realPosition).getType() == 2) {
            setProgressBarIndeterminateVisibility(false);
        }
    }

    public void onMark(String photoId, int mark) {
        PhotoInfo photoInfo = getPhotoInfoForId(photoId);
        int markPrevious = photoInfo.getViewerMark();
        photoInfo.setViewerMark(mark);
        Bundle bundleInput = new Bundle();
        bundleInput.putString("pid", photoId);
        bundleInput.putInt("mrk", mark);
        bundleInput.putInt("mrk_prev", markPrevious);
        GlobalBus.send(2131624088, new BusEvent(bundleInput));
    }

    public void onLikeClicked(String pid, LikeInfoContext likeInfo) {
        getPhotoInfoForId(pid).setLikeInfo(this.likeManager.like(likeInfo));
        this.photoLayerLogger.logClickLike();
    }

    public void onUnlikeClicked(String pid, LikeInfoContext likeInfo) {
        getPhotoInfoForId(pid).setLikeInfo(this.likeManager.unlike(likeInfo));
        this.photoLayerLogger.logClickUnlike();
    }

    public void onLikesCountClicked(View view, String pid, LikeInfoContext likeInfoContext) {
        NavigationHelper.showDiscussionLikes((Activity) this, new Discussion(pid, this.photoOwner.getType() == 0 ? Type.USER_PHOTO.name() : Type.GROUP_PHOTO.name()), ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight()).toBundle());
    }

    public void onUserClicked(UserInfo userInfo) {
        NavigationHelper.showUserInfo(this, userInfo.uid);
    }

    public void onCommentsClicked(View view, String pid) {
        NavigationHelper.showDiscussionCommentsFragment(this, new Discussion(pid, this.photoOwner.getType() == 0 ? Type.USER_PHOTO.name() : Type.GROUP_PHOTO.name()), MessageBaseFragment.Page.MESSAGES, "", ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight()).toBundle());
        this.photoLayerLogger.logClickComment();
    }

    public void onToTopicClicked(String photoId) {
        PhotoInfo info = getPhotoInfoForId(photoId);
        if (info != null) {
            String mediaTopicId = info.getMediaTopicId();
            if (!TextUtils.isEmpty(mediaTopicId)) {
                NavigationHelper.showDiscussionCommentsFragment(this, new Discussion(mediaTopicId, info.getOwnerType() == OwnerType.GROUP ? Type.GROUP_TOPIC.name() : Type.USER_STATUS.name()), MessageBaseFragment.Page.INFO, null);
                return;
            }
        }
        Bundle bundleInput = new Bundle();
        bundleInput.putString("pid", photoId);
        GlobalBus.send(2131624091, new BusEvent(bundleInput));
        showProgressDialog();
    }

    @Subscribe(on = 2131623946, to = 2131624231)
    public void onGotMediaTopic(BusEvent event) {
        hideProgressDialog();
        if (event.resultCode == -2) {
            Toast.makeText(this.context, getStringLocalized(2131166731), 1).show();
            hideProgressDialog();
            return;
        }
        NavigationHelper.showDiscussionCommentsFragment(this, new Discussion(event.bundleOutput.getString("tid"), this.photoOwner.getType() == 0 ? Type.USER_STATUS.name() : Type.GROUP_TOPIC.name()), MessageBaseFragment.Page.INFO, "");
    }

    @Subscribe(on = 2131623946, to = 2131624258)
    public void onSetAlbumMainPhoto(BusEvent event) {
        hideProgressDialog();
        if (event.resultCode == -2) {
            Toast.makeText(this.context, getStringLocalized(2131166542), 1).show();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624228)
    public void onMarkedPhoto(BusEvent event) {
        String photoId = event.bundleOutput.getString("pid");
        if (event.resultCode != -1) {
            int pos = getPositionForId(photoId);
            if (pos != -1) {
                AbstractPhotoInfoView photoView = findViewForPhotoId(photoId);
                if (photoView != null) {
                    photoView.setUserMark(event.bundleInput.getInt("mrk_prev"));
                }
                getPhotoInfoForId(photoId).setViewerMark(event.bundleInput.getInt("mrk_prev"));
                if (event.resultCode == -2) {
                    showErrorActionToast(pos, 2131165297);
                }
            }
            if (event.resultCode == 1) {
                NavigationHelper.showPhotoMarkPayment(this, this.albumId, photoId, this.photoOwner.getId());
                return;
            }
            return;
        }
        if (this.marksManager == null) {
            this.marksManager = Storages.getInstance(this, OdnoklassnikiApplication.getCurrentUser().getId()).getMarksManager();
        }
        this.marksManager.userPhotoMarkUpdate(photoId, event.bundleInput.getInt("mrk"));
    }

    @Subscribe(on = 2131623946, to = 2131624259)
    public void onMainPhotoSet(BusEvent event) {
        hideProgressDialog();
        if (event.resultCode == -2) {
            Toast.makeText(this.context, getStringLocalized(2131166544), 1).show();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624157)
    public void onPhotoDeleted(BusEvent event) {
        hideProgressDialog();
        if (event.resultCode == -1) {
            doDeletePhoto();
        } else {
            Toast.makeText(this.context, getStringLocalized(2131165691), 1).show();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624229)
    public void onPhotoMarkedAsSpam(BusEvent event) {
        hideProgressDialog();
        if (event.resultCode == -2) {
            Toast.makeText(this.context, getStringLocalized(2131166068), 1).show();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624160)
    public void onPhotoEdited(BusEvent event) {
        hideProgressDialog();
        if (event.resultCode == -2) {
            Toast.makeText(this.context, getStringLocalized(2131165728), 1).show();
        } else if (event.resultCode == 1) {
            Toast.makeText(this.context, getStringLocalized(2131166248), 1).show();
        } else {
            PhotoInfo photoInfo = this.photoListController.getPhotoInfoById(event.bundleOutput.getString("pid"));
            if (photoInfo != null) {
                photoInfo.setComment(event.bundleOutput.getString("descr"));
                AbstractPhotoInfoView view = findViewForPhotoId(photoInfo.getId());
                if (view != null) {
                    view.setComment(photoInfo.getComment());
                    view.invalidate();
                }
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624158)
    public void onPhotoTagDeleted(BusEvent event) {
        hideProgressDialog();
        if (event.resultCode == -2) {
            Toast.makeText(this.context, getStringLocalized(2131165693), 1).show();
            return;
        }
        View view = this.imagesPagerAdapter.getCurrentView();
        if (view instanceof StaticPhotoInfoView) {
            StaticPhotoInfoView photoView = (StaticPhotoInfoView) view;
            PhotoAdapterListItem listItem = this.photoListController.getItemByPosition(getCurrentRealPosition());
            if (listItem != null && (listItem instanceof PhotoInfoListItem)) {
                PhotoInfo photoInfo = ((PhotoInfoListItem) listItem).getPhotoInfo();
                if (photoInfo != null) {
                    photoInfo.setTagCount(photoInfo.getTagCount() - 1);
                }
            }
            photoView.removeTagForUser(OdnoklassnikiApplication.getCurrentUser());
            supportInvalidateOptionsMenu();
        }
    }

    private AbstractPhotoInfoView findViewForPhotoId(String photoId) {
        ViewPager viewPager = getPagerView();
        int count = viewPager.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = viewPager.getChildAt(i);
            if (child instanceof AbstractPhotoInfoView) {
                AbstractPhotoInfoView photoView = (AbstractPhotoInfoView) child;
                if (photoView.getPhotoId().equals(photoId)) {
                    return photoView;
                }
            }
        }
        return null;
    }

    private int getPositionForId(String photoId) {
        int size = this.photoListController.getCount();
        for (int i = 0; i < size; i++) {
            PhotoAdapterListItem item = this.photoListController.getItemByPosition(i);
            if (item.getType() == 2 && ((PhotoInfoListItem) item).getPhotoInfo().getId().equals(photoId)) {
                return i;
            }
        }
        return -1;
    }

    private PhotoInfo getPhotoInfoForId(String photoId) {
        int size = this.photoListController.getCount();
        for (int i = 0; i < size; i++) {
            PhotoAdapterListItem item = this.photoListController.getItemByPosition(i);
            if (item.getType() == 2) {
                PhotoInfo photo = ((PhotoInfoListItem) item).getPhotoInfo();
                if (photo.getId().equals(photoId)) {
                    return photo;
                }
            }
        }
        return null;
    }

    private void showErrorActionToast(int photoPos, int messageId) {
        ActionToastManager.showToastAt(this.actionToastContainerView, ActionToastManager.newToastView(this.context, getStringLocalized(messageId), new C10274(photoPos)), 0);
    }

    private void doDeletePhoto() {
        int currentPosition = getCurrentRealPosition();
        ViewPager viewPager = getPagerView();
        View view = this.imagesPagerAdapter.getCurrentView();
        if (this.photoListController.getCount() <= 1 || currentPosition < 0) {
            Animation imageRemoveAnimation = new AlphaAnimation(1.0f, 0.0f);
            imageRemoveAnimation.setFillAfter(true);
            imageRemoveAnimation.setAnimationListener(new C10317(view, currentPosition));
            view.startAnimation(imageRemoveAnimation);
            return;
        }
        PageTransformer mRemoveTransformer;
        int dragWidth = (view.getMeasuredWidth() + viewPager.getPageMargin()) - 1;
        if (currentPosition == this.photoListController.getCount() - 1) {
            dragWidth = -dragWidth;
            mRemoveTransformer = new RemoveBackwardsTransformer();
        } else {
            mRemoveTransformer = new RemoveForwardPageTransformer();
        }
        viewPager.setPageTransformer(true, mRemoveTransformer);
        ValueAnimator animator = ObjectAnimator.ofInt(new int[]{0, dragWidth});
        animator.addUpdateListener(new C10285(viewPager));
        animator.addListener(new C10296(viewPager, currentPosition));
        viewPager.beginFakeDrag();
        animator.start();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        LocalizationManager.inflate((Context) this, getMenuInflater(), 2131689514, menu);
        MenuItem tagsItem = menu.findItem(2131625496);
        TextView tv = (TextView) LocalizationManager.inflate((Context) this, 2130903535, null, false);
        tv.setCompoundDrawablesWithIntrinsicBounds(2130838163, 0, 0, 0);
        tv.setCompoundDrawablePadding(getResources().getDimensionPixelSize(2131231111));
        tv.setTextSize(16.0f);
        tagsItem.setActionView(tv);
        tv.setClickable(true);
        tv.setOnClickListener(new C10328(tagsItem));
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getCurrentErrorCode() != 0) {
            return false;
        }
        PhotoInfo photoInfo = null;
        int realPosition = getCurrentRealPosition();
        List<PhotoAdapterListItem> photosList = this.photoListController.getItems();
        if (realPosition >= 0 && realPosition < photosList.size()) {
            PhotoAdapterListItem item = (PhotoAdapterListItem) photosList.get(realPosition);
            if (item.getType() == 2) {
                photoInfo = ((PhotoInfoListItem) item).getPhotoInfo();
            }
        }
        if (photoInfo != null) {
            ViewPhotosOptionsMenuHelper.prepareOptionsMenu(menu, this.albumId, photoInfo, this.photoOwner);
        }
        setProgressBarIndeterminateVisibility(this.photoInfoProvider.hasPendingTagsRequests());
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (!this.photoListController.hasItems()) {
            Logger.m176e("Photos list not ready for options menu on position " + getCurrentRealPosition());
            return false;
        } else if (item.getItemId() == 16908332) {
            onBackPressed();
            return true;
        } else {
            PhotoAdapterListItem listItem = this.photoListController.getItemByPosition(getCurrentRealPosition());
            if (listItem == null) {
                Logger.m184w("Received null photo info!");
                return false;
            }
            PhotoInfo photoInfo = ((PhotoInfoListItem) listItem).getPhotoInfo();
            switch (item.getItemId()) {
                case C0263R.id.info /*2131624660*/:
                    showProgressDialog();
                    ViewPhotosOptionsMenuHelper.requestPhotoInfo(this, this.photoInfoProvider, photoInfo, this.photoOwner);
                    return true;
                case 2131624799:
                    savePhotoInfo(photoInfo);
                    this.photoLayerLogger.logClickSave();
                    return true;
                case C0263R.id.delete /*2131624801*/:
                    ViewPhotosOptionsMenuHelper.deletePhoto(this, photoInfo, new AnonymousClass10(photoInfo));
                    this.photoLayerLogger.logClickDelete();
                    return true;
                case 2131625454:
                    ShortLink.createPhotoLink(photoInfo, this.photoOwner).copy(getContext(), true);
                    this.photoLayerLogger.logClickCopyLink();
                    return true;
                case 2131625495:
                    onToTopicClicked(photoInfo.getId());
                    return true;
                case 2131625496:
                    onTagsMenuClicked(item);
                    return true;
                case 2131625497:
                    new Builder().setDefaultText(photoInfo.getComment()).setHintText(getStringLocalized(2131165698)).setTitle(getStringLocalized(2131165271)).setPositiveButtonText(getStringLocalized(2131165270)).show(getSupportFragmentManager(), null);
                    this.photoLayerLogger.logClickChangeDescription();
                    return true;
                case 2131625498:
                    if (photoInfo.isBlocked()) {
                        Toast.makeText(this, getStringLocalized(2131166783), 1).show();
                        return true;
                    }
                    ViewPhotosOptionsMenuHelper.setMainPhoto(this, new AnonymousClass12(photoInfo));
                    return true;
                case 2131625499:
                    ViewPhotosOptionsMenuHelper.setMainAlbumPhoto(this, new AnonymousClass11(photoInfo));
                    return true;
                case 2131625500:
                    ViewPhotosOptionsMenuHelper.markPhotoAsSpam(this, new C10339(photoInfo));
                    return true;
                case 2131625501:
                    ViewPhotosOptionsMenuHelper.deleteUserPhotoTag(this, new AnonymousClass13(photoInfo));
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }

    private void savePhotoInfo(@NonNull PhotoInfo photoInfo) {
        String url = null;
        String fileToSaveExtension = null;
        if (photoInfo.hasGif()) {
            url = photoInfo.getGifUrl();
            fileToSaveExtension = "gif";
        } else {
            PhotoSize largestSize = photoInfo.getLargestSize();
            if (largestSize != null) {
                url = largestSize.getUrl();
                fileToSaveExtension = "jpg";
            }
        }
        if (url != null) {
            ViewPhotosOptionsMenuHelper.savePhotoToFile(this, url, fileToSaveExtension, getCurrentRealPosition());
        }
    }

    public void onSubmitEditText(String description) {
        showProgressDialog();
        PhotoInfo photoInfo = ((PhotoInfoListItem) this.photoListController.getItemByPosition(getCurrentRealPosition())).getPhotoInfo();
        Bundle bundleInput = new Bundle();
        bundleInput.putString("pid", photoInfo.getId());
        bundleInput.putString("descr", description);
        if (this.photoOwner.getType() == 1) {
            bundleInput.putString("gid", this.photoOwner.getId());
        }
        GlobalBus.send(2131623980, new BusEvent(bundleInput));
    }

    private void onTagsMenuClicked(MenuItem item) {
        PhotoInfo photoInfo = ((PhotoInfoListItem) this.photoListController.getItemByPosition(getCurrentRealPosition())).getPhotoInfo();
        View currentView = this.imagesPagerAdapter.getCurrentView();
        if (currentView instanceof StaticPhotoInfoView) {
            StaticPhotoInfoView photoView = (StaticPhotoInfoView) currentView;
            if (photoView.areTagsShown()) {
                photoView.hideTags(true);
                item.setTitle(getStringLocalized(2131165293));
                return;
            }
            item.setVisible(false);
            this.photoInfoProvider.addOnPhototagsReceivedListener(photoView);
            this.photoInfoProvider.addOnPhototagsReceivedListener(this);
            this.photoInfoProvider.requestPhotoTagsInfo(photoInfo.getId());
            item.setTitle(getStringLocalized(2131165277));
            supportInvalidateOptionsMenu();
        }
    }

    public boolean onTagsReceived(String photoId, ArrayList<UserInfo> arrayList, ArrayList<PhotoTag> arrayList2) {
        PhotoAdapterListItem listItem = this.photoListController.getItemByPosition(getCurrentRealPosition());
        if (listItem.getType() == 2 && ((PhotoInfoListItem) listItem).getPhotoInfo().getId().equals(photoId)) {
            if (!this.photoInfoProvider.hasPendingTagsRequests()) {
                setProgressBarIndeterminateVisibility(false);
            }
            getDecorViewsHandler().setDecorVisibility(false, true);
            supportInvalidateOptionsMenu();
        }
        return true;
    }

    public void onTagsFailed(String photoId) {
        PhotoAdapterListItem listItem = this.photoListController.getItemByPosition(getCurrentRealPosition());
        if (listItem.getType() == 2 && ((PhotoInfoListItem) listItem).getPhotoInfo().getId().equals(photoId) && !this.photoInfoProvider.hasPendingTagsRequests()) {
            setProgressBarIndeterminateVisibility(false);
            Toast.makeText(this.context, getStringLocalized(2131165822), 1).show();
            supportInvalidateOptionsMenu();
        }
    }

    protected final void showProgressDialog() {
        ProgressDialogFragment.createInstance(getStringLocalized(2131166864), true).show(getSupportFragmentManager(), "progress_dialog_tag");
    }

    private void hideProgressDialog() {
        DialogFragment fragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag("progress_dialog_tag");
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    protected String getCurrentPhotoId() {
        return ((StaticPhotoInfoView) this.imagesPagerAdapter.getCurrentView()).getPhotoId();
    }

    protected final void transitBack(boolean throwUp) {
        if (this.photoInfoProvider != null) {
            this.photoInfoProvider.destroyProvider();
        }
        super.transitBack(throwUp);
    }

    public void finish() {
        super.finish();
        notifyPhotoSelected(null);
    }

    protected boolean isOpenDecorOnChildLayout() {
        return getIntent().getStringArrayExtra("sequenceIds") == null || getIntent().getParcelableExtra("photoInfoPage") == null;
    }
}
