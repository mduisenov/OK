package ru.ok.android.ui.image.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.image.PhotoAlbumPhotosFragment;
import ru.ok.android.fragments.image.PhotoAlbumPhotosFragment.OnPhotoClickListener;
import ru.ok.android.fragments.image.PhotoAlbumsFragment;
import ru.ok.android.fragments.image.PhotoAlbumsFragment.OnAlbumSelectedListener;
import ru.ok.android.fragments.image.PhotoAlbumsTabFragment;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.model.pagination.Page;
import ru.ok.android.services.app.IntentUtils;
import ru.ok.android.ui.activity.ShowFragmentActivity;
import ru.ok.android.ui.custom.photo.ActionToastView;
import ru.ok.android.ui.custom.photo.PhotoTileView;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoInfo;

public class PhotoAlbumsActivity extends ShowFragmentActivity implements OnPhotoClickListener, OnAlbumSelectedListener {
    protected static final int MESSAGE_UNBLOCK_CLICK;
    private FrameLayout actionToastContainerView;
    private boolean albumChangeLocked;
    private boolean clickBlocked;
    protected Handler clickHandler;
    private int mode;
    private boolean openedFromSlidingMenu;
    private PhotoAlbumPhotosFragment photoAlbumPhotosFragment;
    private Fragment photoAlbumsFragment;

    /* renamed from: ru.ok.android.ui.image.view.PhotoAlbumsActivity.1 */
    class C10031 extends Handler {
        C10031() {
        }

        public void handleMessage(Message msg) {
            if (msg.what == PhotoAlbumsActivity.MESSAGE_UNBLOCK_CLICK) {
                PhotoAlbumsActivity.this.clickBlocked = false;
            } else {
                super.handleMessage(msg);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.image.view.PhotoAlbumsActivity.2 */
    class C10042 implements OnClickListener {
        final /* synthetic */ PhotoAlbumInfo val$album;

        C10042(PhotoAlbumInfo photoAlbumInfo) {
            this.val$album = photoAlbumInfo;
        }

        public void onClick(View view) {
            ActionToastManager.hideToastFrom(PhotoAlbumsActivity.this.actionToastContainerView, (ActionToastView) view);
            if (PhotoAlbumsActivity.this.photoAlbumPhotosFragment == null) {
                PhotoAlbumsActivity.this.onAlbumClickListener(this.val$album, (PhotoOwner) PhotoAlbumsActivity.this.getIntent().getExtras().getParcelable("ownrnfo"));
            } else if (!TextUtils.equals(this.val$album.getId(), PhotoAlbumsActivity.this.photoAlbumPhotosFragment.getAlbumId())) {
                PhotoAlbumsActivity.this.navigateToPhotoAlbumsFragment();
            }
        }
    }

    private static class PhotoAlbumsFragmentHelper {
        public static Fragment newInstance(boolean openedFromSlidingMenu, PhotoOwner photoOwner, boolean hideActions) {
            if (photoOwner == null || photoOwner.getType() == 1) {
                return PhotoAlbumsFragment.newInstance(photoOwner);
            }
            return PhotoAlbumsTabFragment.newInstance(photoOwner, openedFromSlidingMenu, hideActions);
        }

        @Nullable
        public static PhotoAlbumInfo findAlbumById(@Nullable Fragment fragment, @NonNull String albumId) {
            if (fragment == null || !(fragment instanceof AlbumFinder)) {
                return null;
            }
            return ((AlbumFinder) fragment).findAlbumById(albumId);
        }
    }

    public PhotoAlbumsActivity() {
        this.clickHandler = new C10031();
    }

    static {
        MESSAGE_UNBLOCK_CLICK = PhotoAlbumsActivity.class.hashCode();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!this.albumChangeLocked && this.photoAlbumPhotosFragment != null && this.photoAlbumPhotosFragment.isVisible()) {
            navigateToPhotoAlbumsFragment();
        }
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setSupportProgressBarIndeterminateVisibility(false);
        setContentView(2130903084);
        showTabbar(false);
        this.actionToastContainerView = (FrameLayout) findViewById(2131624546);
        this.mode = getIntent().getIntExtra("mode", 0);
        this.openedFromSlidingMenu = getIntent().getBooleanExtra("key_activity_from_menu", false);
        displayRequiredFragment();
        if (savedInstanceState != null) {
            this.mode = savedInstanceState.getInt("mode", 0);
        }
        this.albumChangeLocked = getIntent().getBooleanExtra("aclckd", false);
        if (savedInstanceState != null) {
            this.albumChangeLocked = savedInstanceState.getBoolean("aclckd");
        }
        if (this.mode == 1) {
            setResult(0, null);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mode", this.mode);
        outState.putBoolean("aclckd", this.albumChangeLocked);
    }

    private void displayRequiredFragment() {
        this.photoAlbumsFragment = getSupportFragmentManager().findFragmentByTag("phtalbms");
        boolean displayPhotoAlbumsFragment = true;
        this.photoAlbumPhotosFragment = (PhotoAlbumPhotosFragment) getSupportFragmentManager().findFragmentByTag("albumphotos");
        if (this.photoAlbumPhotosFragment != null) {
            showPhotoAlbumPhotosFragmentIfNotVisible();
            displayPhotoAlbumsFragment = false;
        } else if (getIntent().getIntExtra("show", 0) == 1) {
            boolean z;
            displayPhotoAlbumsFragment = false;
            String aid = getIntent().getExtras().getString("aid");
            PhotoOwner owner = (PhotoOwner) getIntent().getExtras().getParcelable("ownrnfo");
            if (this.mode == 1) {
                z = true;
            } else {
                z = false;
            }
            this.photoAlbumPhotosFragment = PhotoAlbumPhotosFragment.newInstance(aid, owner, z);
            showPhotoAlbumPhotosFragment(false);
        }
        if (displayPhotoAlbumsFragment) {
            showPhotoAlbumsFragment();
            showTabbar(true);
        }
    }

    private void addPhotoAlbumsFragmentToTransaction(FragmentTransaction transaction) {
        boolean z = true;
        PhotoOwner photoOwner = (PhotoOwner) getIntent().getExtras().getParcelable("ownrnfo");
        this.photoAlbumsFragment = getSupportFragmentManager().findFragmentByTag("phtalbms");
        if (this.photoAlbumsFragment == null) {
            boolean z2 = this.openedFromSlidingMenu;
            if (this.mode != 1) {
                z = false;
            }
            this.photoAlbumsFragment = PhotoAlbumsFragmentHelper.newInstance(z2, photoOwner, z);
            transaction.add(2131624544, this.photoAlbumsFragment, "phtalbms");
        } else if (!this.photoAlbumsFragment.isVisible()) {
            transaction.show(this.photoAlbumsFragment);
        }
    }

    private void showPhotoAlbumsFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        addPhotoAlbumsFragmentToTransaction(transaction);
        transaction.commit();
    }

    private void showPhotoAlbumPhotosFragmentIfNotVisible() {
        if (!this.photoAlbumPhotosFragment.isVisible()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.show(this.photoAlbumPhotosFragment);
            if (this.photoAlbumsFragment != null) {
                transaction.hide(this.photoAlbumsFragment);
            }
            transaction.commit();
        }
    }

    private void showPhotoAlbumPhotosFragment(boolean animate) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (animate) {
            transaction.setCustomAnimations(2130968637, 2130968626);
        }
        transaction.add(2131624545, this.photoAlbumPhotosFragment, "albumphotos");
        if (this.photoAlbumsFragment != null) {
            transaction.hide(this.photoAlbumsFragment);
        }
        transaction.commit();
        showTabbar(true);
    }

    private void removePhotoAlbumPhotosFragmentFromTransaction(FragmentTransaction transaction) {
        if (this.photoAlbumPhotosFragment != null) {
            transaction.remove(this.photoAlbumPhotosFragment);
        }
    }

    public void onPhotoClicked(PhotoTileView view, String aid, PhotoInfo photo, Page<PhotoInfo> photoInfoPage, PhotoOwner photoOwner) {
        if (!this.clickBlocked) {
            if (this.mode == 0) {
                onViewModeClick(view, aid, photo, photoInfoPage, photoOwner);
            } else if (this.mode == 1) {
                onPickModeClicked(photo);
            }
            temporaryBlockClicks();
        }
    }

    protected final void onViewModeClick(PhotoTileView view, String aid, PhotoInfo photo, Page<PhotoInfo> photoInfoPage, PhotoOwner photoOwner) {
        Intent intent = IntentUtils.createIntentForPhotoView((Context) this, photoOwner, aid, photo, (Page) photoInfoPage, toPhotoLayerSourceId(aid));
        intent.putExtra("fromNativeAlbum", true);
        NavigationHelper.showPhoto(this, intent, GifAsMp4PlayerHelper.shouldShowGifAsMp4(photo) ? null : PhotoLayerAnimationHelper.makeScaleUpAnimationBundle(view.getContext(), view));
    }

    private static int toPhotoLayerSourceId(String albumId) {
        return "stream".equals(albumId) ? 3 : 2;
    }

    protected final void onPickModeClicked(PhotoInfo photo) {
        Intent intent = new Intent();
        intent.putExtra("photo", photo);
        setResult(-1, intent);
        finish();
    }

    public void onAlbumClickListener(PhotoAlbumInfo album, PhotoOwner photoOwner) {
        if (!this.clickBlocked) {
            this.photoAlbumPhotosFragment = PhotoAlbumPhotosFragment.newInstance(album, photoOwner, this.mode == 1);
            showPhotoAlbumPhotosFragment(true);
            temporaryBlockClicks();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624156)
    public void onPhotoAlbumDelete(BusEvent event) {
        String aid = event.bundleInput.getString("aid");
        if (this.photoAlbumPhotosFragment != null && TextUtils.equals(this.photoAlbumPhotosFragment.getAlbumId(), aid)) {
            navigateToPhotoAlbumsFragment();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624184)
    public void onPhotoAlbumLike(BusEvent event) {
        if (event.resultCode == -2 && this.photoAlbumsFragment != null) {
            String aid = event.bundleInput.getString("album_id");
            if (!TextUtils.isEmpty(aid)) {
                PhotoAlbumInfo album = PhotoAlbumsFragmentHelper.findAlbumById(this.photoAlbumsFragment, aid);
                if (album != null) {
                    album.setViewerLiked(false);
                    album.setLikesCount(album.getLikesCount() - 1);
                    showAlbumLikeErrorToast(album, 2131165295);
                }
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624261)
    public void onPhotoAlbumUnlike(BusEvent event) {
        if (event.resultCode == -2 && this.photoAlbumsFragment != null) {
            String aid = event.bundleInput.getString("album_id");
            if (!TextUtils.isEmpty(aid)) {
                PhotoAlbumInfo album = PhotoAlbumsFragmentHelper.findAlbumById(this.photoAlbumsFragment, aid);
                if (album != null) {
                    album.setViewerLiked(true);
                    album.setLikesCount(album.getLikesCount() + 1);
                    showAlbumLikeErrorToast(album, 2131165298);
                }
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332 || isMenuIndicatorEnable()) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }

    public void onBackPressed() {
        if (this.albumChangeLocked) {
            finishByBackPressed();
        } else if (!this.clickBlocked) {
            if (this.photoAlbumPhotosFragment == null || !this.photoAlbumPhotosFragment.isVisible() || this.photoAlbumsFragment == null) {
                finishByBackPressed();
            } else {
                navigateToPhotoAlbumsFragment();
            }
        }
    }

    private void finishByBackPressed() {
        if (this.openedFromSlidingMenu) {
            startActivity(NavigationHelper.createIntentForBackFromSlidingMenuOpenActivity(this));
        } else {
            finish();
        }
    }

    protected final void navigateToPhotoAlbumsFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(2130968625, 2130968641);
        removePhotoAlbumPhotosFragmentFromTransaction(transaction);
        addPhotoAlbumsFragmentToTransaction(transaction);
        transaction.commitAllowingStateLoss();
        showTabbar(true);
    }

    protected final void showAlbumLikeErrorToast(PhotoAlbumInfo album, int message) {
        if (this.photoAlbumPhotosFragment != null && this.photoAlbumPhotosFragment.isVisible() && album.getId().equals(this.photoAlbumPhotosFragment.getAlbumId())) {
            this.photoAlbumPhotosFragment.updatePhotoAlbumInfo(album);
        }
        ActionToastManager.showToastAt(this.actionToastContainerView, ActionToastManager.newToastView(this, LocalizationManager.getString(this, message, album.getTitle()), new C10042(album)), 0);
    }

    protected final void temporaryBlockClicks() {
        this.clickBlocked = true;
        this.clickHandler.sendEmptyMessageDelayed(MESSAGE_UNBLOCK_CLICK, 750);
    }

    protected Type getSlidingMenuSelectedItem() {
        return Type.photos;
    }

    public boolean isUseTabbar() {
        return true;
    }
}
