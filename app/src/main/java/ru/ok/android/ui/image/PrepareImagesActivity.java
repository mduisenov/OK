package ru.ok.android.ui.image;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.jakewharton.disklrucache.DiskLruCache;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.onelog.PhotoRollLog;
import ru.ok.android.services.processors.settings.PhotoRollSettingsHelper;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.ui.adapters.EfficientFragmentAdapter;
import ru.ok.android.ui.adapters.photo.SelectAlbumSpinnerAdapter;
import ru.ok.android.ui.custom.BlockableViewPager;
import ru.ok.android.ui.dialogs.ProgressDialogFragment;
import ru.ok.android.ui.dialogs.photo.PhotoAlbumEditDialog;
import ru.ok.android.ui.dialogs.photo.PhotoAlbumEditDialog.Builder;
import ru.ok.android.ui.dialogs.photo.PhotoAlbumEditDialog.PhotoAlbumDialogListener;
import ru.ok.android.ui.fragments.image.ImageEditFragment;
import ru.ok.android.ui.fragments.image.ImageEditFragment.OnRemoveClickedListener;
import ru.ok.android.ui.image.pick.GalleryImageInfo;
import ru.ok.android.ui.image.pick.PickImagesActivity;
import ru.ok.android.utils.Constants.Image;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.MimeTypes;
import ru.ok.android.utils.Storage.External.Application;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.animation.SimpleAnimationListener;
import ru.ok.android.utils.animation.SimpleAnimatorListener;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.pagetransformer.RemoveBackwardsTransformer;
import ru.ok.android.utils.pagetransformer.RemoveForwardPageTransformer;
import ru.ok.android.utils.pagetransformer.ZoomOutPageTransformer;
import ru.ok.java.api.request.image.GetPhotoAlbumsRequest.FILEDS;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.AccessType;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.photo.PhotoAlbumsInfo;

public class PrepareImagesActivity extends BaseActivity implements OnTouchListener, PhotoAlbumDialogListener, OnRemoveClickedListener {
    protected static ExecutorService CACHE_EXECUTOR;
    protected static ExecutorService LOAD_EXECUTOR;
    protected boolean canCreateAlbum;
    protected boolean canSelectAlbum;
    protected String cancelAlertText;
    protected boolean commentEnabled;
    protected int editedImagesCount;
    protected boolean exiting;
    boolean giveUpOnAlbums;
    private BroadcastReceiver imageEditReceiver;
    protected View mAddControlsView;
    protected PhotoAlbumInfo mAlbumInfo;
    protected int mChoiceMode;
    protected final Context mContext;
    private View mCropBtn;
    protected ImageEditFragmentsAdapter mEditFragmentsAdapter;
    protected DiskLruCache mImageCache;
    protected final ArrayList<ImageEditInfo> mImages;
    protected TextView mImagesCountView;
    protected String mMobileAlbumTitle;
    protected BlockableViewPager mPagerView;
    protected String mPersonalAlbumTitle;
    protected boolean mPopulatedAlbums;
    private View mRotateCcwBtn;
    private View mRotateCwBtn;
    private TextView mUploadBtn;
    protected int mUploadTarget;
    protected ArrayList<PhotoAlbumInfo> mUserAlbumsInfos;
    private SelectAlbumSpinnerAdapter mUserAlbumsListAdapter;
    protected boolean silentCancelIfNotEdited;
    private boolean uploadFromPhotoRoll;

    /* renamed from: ru.ok.android.ui.image.PrepareImagesActivity.12 */
    class AnonymousClass12 extends SimpleAnimatorListener {
        final /* synthetic */ ImageEditFragment val$fragment;
        final /* synthetic */ int val$position;

        AnonymousClass12(ImageEditFragment imageEditFragment, int i) {
            this.val$fragment = imageEditFragment;
            this.val$position = i;
        }

        public void onAnimationEnd(Animator animation) {
            PrepareImagesActivity.this.mPagerView.endFakeDrag();
            PrepareImagesActivity.this.mPagerView.setPageTransformer(true, new ZoomOutPageTransformer());
            this.val$fragment.setRemoveButtonEnabled(true);
            PrepareImagesActivity.this.doRemovePage(this.val$position);
        }
    }

    /* renamed from: ru.ok.android.ui.image.PrepareImagesActivity.13 */
    class AnonymousClass13 extends SimpleAnimationListener {
        final /* synthetic */ ImageEditFragment val$fragment;
        final /* synthetic */ int val$position;

        /* renamed from: ru.ok.android.ui.image.PrepareImagesActivity.13.1 */
        class C09591 implements Runnable {
            C09591() {
            }

            public void run() {
                PrepareImagesActivity.this.doRemovePage(AnonymousClass13.this.val$position);
            }
        }

        AnonymousClass13(ImageEditFragment imageEditFragment, int i) {
            this.val$fragment = imageEditFragment;
            this.val$position = i;
        }

        public void onAnimationEnd(Animation animation) {
            this.val$fragment.getView().setVisibility(4);
            this.val$fragment.getView().clearAnimation();
            this.val$fragment.getView().postDelayed(new C09591(), 1);
        }
    }

    /* renamed from: ru.ok.android.ui.image.PrepareImagesActivity.1 */
    class C09601 implements OnClickListener {
        C09601() {
        }

        public void onClick(View view) {
            Intent intent = new Intent(PrepareImagesActivity.this, PickImagesActivity.class);
            intent.putExtras(PrepareImagesActivity.this.getIntent());
            intent.putExtra("choice_mode", PrepareImagesActivity.this.mChoiceMode);
            PrepareImagesActivity.this.startActivityForResult(intent, 1);
        }
    }

    /* renamed from: ru.ok.android.ui.image.PrepareImagesActivity.2 */
    class C09612 implements OnClickListener {
        C09612() {
        }

        public void onClick(View view) {
            ImageEditFragment fragment = (ImageEditFragment) PrepareImagesActivity.this.mEditFragmentsAdapter.geCurrentFragmentAtPosition(PrepareImagesActivity.this.mPagerView.getCurrentItem());
            if (fragment != null) {
                fragment.rotate(false);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.image.PrepareImagesActivity.3 */
    class C09623 implements OnClickListener {
        C09623() {
        }

        public void onClick(View view) {
            ImageEditFragment fragment = (ImageEditFragment) PrepareImagesActivity.this.mEditFragmentsAdapter.geCurrentFragmentAtPosition(PrepareImagesActivity.this.mPagerView.getCurrentItem());
            if (fragment != null) {
                fragment.rotate(true);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.image.PrepareImagesActivity.4 */
    class C09634 implements OnClickListener {
        C09634() {
        }

        public void onClick(View view) {
            ImageEditFragment fragment = (ImageEditFragment) PrepareImagesActivity.this.mEditFragmentsAdapter.geCurrentFragmentAtPosition(PrepareImagesActivity.this.mPagerView.getCurrentItem());
            if (fragment != null) {
                fragment.crop();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.image.PrepareImagesActivity.5 */
    class C09645 implements OnClickListener {
        C09645() {
        }

        public void onClick(View view) {
            KeyBoardUtils.hideKeyBoard(PrepareImagesActivity.this, view.getApplicationWindowToken());
            PrepareImagesActivity.this.returnSuccess();
        }
    }

    /* renamed from: ru.ok.android.ui.image.PrepareImagesActivity.6 */
    class C09656 extends SimpleOnPageChangeListener {
        C09656() {
        }

        public void onPageSelected(int position) {
            setRemoveButtonVisibility(position, true);
            setRemoveButtonVisibility(position + 1, false);
            setRemoveButtonVisibility(position - 1, false);
            PrepareImagesActivity.this.updateControlsState();
        }

        private void setRemoveButtonVisibility(int position, boolean visible) {
            ImageEditFragment fragment = (ImageEditFragment) PrepareImagesActivity.this.mEditFragmentsAdapter.geCurrentFragmentAtPosition(position);
            if (fragment != null) {
                fragment.setRemoveButtonVisibility(visible);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.image.PrepareImagesActivity.7 */
    class C09667 implements OnNavigationListener {
        C09667() {
        }

        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            PrepareImagesActivity.this.mAlbumInfo = (PhotoAlbumInfo) PrepareImagesActivity.this.mUserAlbumsInfos.get(itemPosition);
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.image.PrepareImagesActivity.8 */
    class C09678 implements DialogInterface.OnClickListener {
        C09678() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    /* renamed from: ru.ok.android.ui.image.PrepareImagesActivity.9 */
    class C09689 implements DialogInterface.OnClickListener {
        final /* synthetic */ boolean val$toHome;

        C09689(boolean z) {
            this.val$toHome = z;
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            PrepareImagesActivity.this.goBack(this.val$toHome);
            PrepareImagesActivity.this.exiting = true;
        }
    }

    private final class ImageEditFragmentsAdapter extends EfficientFragmentAdapter {
        private final ExecutorService mCacheExecutorService;
        private final Context mContext;
        private final ExecutorService mExecutorService;
        private final DiskLruCache mImageCache;
        private final List<ImageEditInfo> mImages;

        public ImageEditFragmentsAdapter(Context context, FragmentManager fragmentManager, ExecutorService executorService, ExecutorService cacheExecutorService, DiskLruCache imageCache, List<ImageEditInfo> images) {
            super(fragmentManager);
            this.mContext = context;
            this.mImages = images;
            this.mExecutorService = executorService;
            this.mCacheExecutorService = cacheExecutorService;
            this.mImageCache = imageCache;
        }

        public Fragment instantiateFragment(int position) {
            ImageEditInfo image = (ImageEditInfo) this.mImages.get(position);
            ImageEditFragment fragment = new ImageEditFragment();
            fragment.setUri(image.getUri(), this.mContext);
            fragment.setOutDirPath(PrepareImagesActivity.this.getIntent().getStringExtra("out_dir"));
            fragment.setImageId(image.getId());
            fragment.setTemporary(image.isTemporary());
            fragment.setRotation(image.getRotation());
            fragment.setExecutionService(this.mExecutorService);
            fragment.setCacheExecutionService(this.mCacheExecutorService);
            fragment.setImageCache(this.mImageCache);
            fragment.setCommentEnabled(PrepareImagesActivity.this.commentEnabled);
            fragment.setImageMimeType(image.getMimeType());
            image.resetFlags();
            if (position == 0) {
                fragment.setRemoveButtonVisibility(true);
            }
            return fragment;
        }

        public int getCount() {
            return this.mImages.size();
        }

        public int getItemPosition(Object object) {
            ImageEditFragment fragment = (ImageEditFragment) object;
            int size = this.mImages.size();
            for (int i = 0; i < size; i++) {
                if (fragment.getImageId().equals(((ImageEditInfo) this.mImages.get(i)).getId())) {
                    return i;
                }
            }
            return -2;
        }

        public ImageEditInfo getImage(String imageId) {
            if (!(imageId == null || this.mImages == null)) {
                for (ImageEditInfo image : this.mImages) {
                    if (imageId.equals(image.getId())) {
                        return image;
                    }
                }
            }
            return null;
        }
    }

    public PrepareImagesActivity() {
        this.mContext = this;
        this.mImages = new ArrayList();
        this.mUserAlbumsInfos = new ArrayList();
        this.editedImagesCount = 0;
        this.imageEditReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                boolean orientationHasChanged = true;
                String action = intent.getStringExtra("action");
                ImageEditInfo image = PrepareImagesActivity.this.mEditFragmentsAdapter.getImage(intent.getStringExtra("img_id"));
                if (image != null) {
                    PrepareImagesActivity prepareImagesActivity;
                    if ("change_uri".equals(action)) {
                        Uri uri = (Uri) intent.getParcelableExtra("new_uri");
                        boolean temporary = intent.getBooleanExtra("temporary", true);
                        image.setUri(uri);
                        image.setTemporary(temporary);
                        prepareImagesActivity = PrepareImagesActivity.this;
                        prepareImagesActivity.editedImagesCount += PrepareImagesActivity.getImageChangedCount(image.getFlags(), ImageEditInfo.INDEX_FLAG_URI_CHANGED, true);
                        image.setWasEdited(true);
                    } else if ("before_rotation".equals(action)) {
                        image.setRotation(intent.getIntExtra("rotation", 0));
                    } else if ("change_rotation".equals(action)) {
                        int rotation = intent.getIntExtra("rotation", 0);
                        image.setRotation(rotation);
                        if (rotation == image.getOriginalRotation()) {
                            orientationHasChanged = false;
                        }
                        prepareImagesActivity = PrepareImagesActivity.this;
                        prepareImagesActivity.editedImagesCount += PrepareImagesActivity.getImageChangedCount(image.getFlags(), ImageEditInfo.INDEX_FLAG_ORIENTATION_CHANGED, orientationHasChanged);
                        image.setWasEdited(PrepareImagesActivity.or(image.getFlags()));
                    } else if (PrepareImagesActivity.this.commentEnabled && "change_comment".equals(action)) {
                        boolean commentHasChanged;
                        String comment = intent.getStringExtra("comment");
                        image.setComment(comment);
                        if (TextUtils.equals(image.getOriginalComment(), comment)) {
                            commentHasChanged = false;
                        } else {
                            commentHasChanged = true;
                        }
                        prepareImagesActivity = PrepareImagesActivity.this;
                        prepareImagesActivity.editedImagesCount += PrepareImagesActivity.getImageChangedCount(image.getFlags(), ImageEditInfo.INDEX_FLAG_COMMENT_CHANGED, commentHasChanged);
                        image.setWasEdited(PrepareImagesActivity.or(image.getFlags()));
                    }
                }
            }
        };
    }

    static {
        LOAD_EXECUTOR = Executors.newSingleThreadExecutor();
        CACHE_EXECUTOR = Executors.newSingleThreadExecutor();
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setContentView(LocalizationManager.inflate((Context) this, 2130903244, null, false));
        prepareCache();
        initStateFromIntent(getIntent());
        initViews();
        if (savedInstanceState != null) {
            restoreSavedState(savedInstanceState);
        } else {
            parseImagesToEdit(getIntent());
        }
        prepareActionBar();
        requestAlbumListIfNecessary();
        registerImageEditReceiver();
    }

    private void initStateFromIntent(Intent intent) {
        int i;
        this.uploadFromPhotoRoll = intent.getBooleanExtra("upload_from_photo_roll", false);
        this.mChoiceMode = intent.getIntExtra("choice_mode", 0);
        this.mUploadTarget = intent.getIntExtra("upload_tgt", 0);
        this.mAlbumInfo = (PhotoAlbumInfo) intent.getParcelableExtra("album");
        this.mMobileAlbumTitle = LocalizationManager.getString((Context) this, 2131166213);
        this.mPersonalAlbumTitle = LocalizationManager.getString((Context) this, 2131166341);
        if (this.mAlbumInfo == null) {
            this.mAlbumInfo = createApplicationAlbum();
        }
        boolean z = intent.getBooleanExtra("can_select_album", true) || this.mAlbumInfo == null;
        this.canSelectAlbum = z;
        boolean z2 = this.mPopulatedAlbums;
        if (this.canSelectAlbum) {
            i = 0;
        } else {
            i = 1;
        }
        this.mPopulatedAlbums = i | z2;
        this.commentEnabled = intent.getBooleanExtra("comments_enabled", false);
        this.canCreateAlbum = intent.getBooleanExtra("can_create_album", true);
        this.cancelAlertText = intent.getStringExtra("cancel_alert_text");
        this.silentCancelIfNotEdited = intent.getBooleanExtra("silent_cancel_if_not_edited", false);
    }

    private void initViews() {
        this.mImagesCountView = (TextView) findViewById(2131624957);
        this.mAddControlsView = findViewById(2131624958);
        initPagerViewWithAdapter();
        initButtons();
        findViewById(2131624959).setOnClickListener(new C09601());
    }

    private void initButtons() {
        this.mRotateCcwBtn = findViewById(2131624954);
        this.mRotateCcwBtn.setOnClickListener(new C09612());
        this.mRotateCwBtn = findViewById(2131624956);
        this.mRotateCwBtn.setOnClickListener(new C09623());
        this.mCropBtn = findViewById(2131624955);
        this.mCropBtn.setOnClickListener(new C09634());
        this.mUploadBtn = (TextView) findViewById(2131624834);
        this.mUploadBtn.setOnClickListener(new C09645());
    }

    private void initPagerViewWithAdapter() {
        this.mPagerView = (BlockableViewPager) findViewById(C0263R.id.pager);
        this.mPagerView.setPageTransformer(true, new ZoomOutPageTransformer());
        this.mPagerView.setOffscreenPageLimit(2);
        if (getResources().getConfiguration().orientation == 1) {
            this.mPagerView.setPageMargin(getResources().getDimensionPixelSize(2131231135));
        } else {
            this.mPagerView.setPageMargin(0);
        }
        this.mEditFragmentsAdapter = new ImageEditFragmentsAdapter(this.mContext, getSupportFragmentManager(), LOAD_EXECUTOR, CACHE_EXECUTOR, this.mImageCache, this.mImages);
        this.mPagerView.setAdapter(this.mEditFragmentsAdapter);
        this.mPagerView.setOnPageChangeListener(new C09656());
    }

    private void restoreSavedState(Bundle savedInstanceState) {
        this.mAlbumInfo = (PhotoAlbumInfo) savedInstanceState.getParcelable("album");
        ArrayList<PhotoAlbumInfo> albums = savedInstanceState.getParcelableArrayList("albms");
        this.mUserAlbumsInfos.clear();
        if (albums != null) {
            this.mUserAlbumsInfos.addAll(albums);
        }
        this.mPopulatedAlbums = savedInstanceState.getBoolean("ppltd");
        ArrayList<ImageEditInfo> imageEditInfos = savedInstanceState.getParcelableArrayList("imgs");
        if (imageEditInfos != null) {
            this.mImages.clear();
            Iterator i$ = imageEditInfos.iterator();
            while (i$.hasNext()) {
                this.mImages.add((ImageEditInfo) i$.next());
            }
            this.mEditFragmentsAdapter.notifyDataSetChanged();
        }
        this.canCreateAlbum = savedInstanceState.getBoolean("can_create_album");
        this.editedImagesCount = savedInstanceState.getInt("edited_images_count");
    }

    private PhotoAlbumInfo createApplicationAlbum() {
        PhotoAlbumInfo album = new PhotoAlbumInfo();
        album.setId("application");
        album.setTitle(this.mMobileAlbumTitle);
        album.setOwnerType(OwnerType.USER);
        return album;
    }

    private void requestAlbumListIfNecessary() {
        if (!this.mPopulatedAlbums || this.mUserAlbumsInfos == null || this.mUserAlbumsInfos.size() < 3) {
            requestAlbumList();
        }
    }

    private void registerImageEditReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(this.imageEditReceiver, new IntentFilter("INTENT_FILTER_IMAGE_EDIT"));
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        String uploadBtnText = getIntent().getStringExtra("upload_btn_text");
        if (uploadBtnText != null) {
            this.mUploadBtn.setText(uploadBtnText);
        }
    }

    private void prepareActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        CharSequence title = getIntent().getStringExtra("actionbar_title");
        if (getIntent().getBooleanExtra("can_select_album", true) || TextUtils.isEmpty(title)) {
            actionBar.setNavigationMode(1);
            actionBar.setDisplayShowTitleEnabled(false);
            if (this.mUserAlbumsInfos.isEmpty() && this.canSelectAlbum) {
                setInitialAlbumsList();
            }
            this.mUserAlbumsListAdapter = new SelectAlbumSpinnerAdapter(actionBar.getThemedContext(), this.mUserAlbumsInfos);
            actionBar.setListNavigationCallbacks(this.mUserAlbumsListAdapter, new C09667());
            selectAlbumInActionBar();
            return;
        }
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }

    protected final void selectAlbumInActionBar() {
        if (this.mUserAlbumsListAdapter != null) {
            int selected = 0;
            int size = this.mUserAlbumsInfos.size();
            for (int i = 0; i < size; i++) {
                if (this.mAlbumInfo.equals((PhotoAlbumInfo) this.mUserAlbumsInfos.get(i))) {
                    selected = i;
                    break;
                }
            }
            getSupportActionBar().setSelectedNavigationItem(selected);
        }
    }

    protected void setInitialAlbumsList() {
        if (!this.mUserAlbumsInfos.isEmpty()) {
            this.mUserAlbumsInfos.clear();
        }
        if (this.canSelectAlbum) {
            this.mUserAlbumsInfos.add(getPersonalAlbum());
            this.mUserAlbumsInfos.add(getMobileAlbum());
            if (this.mAlbumInfo.getOwnerType() == OwnerType.GROUP) {
                this.mUserAlbumsInfos.add(this.mAlbumInfo);
            } else if (!TextUtils.equals(this.mAlbumInfo.getId(), "application") && !TextUtils.isEmpty(this.mAlbumInfo.getId())) {
                this.mUserAlbumsInfos.add(this.mAlbumInfo);
            }
        } else if (this.mAlbumInfo == null || (this.mAlbumInfo.getOwnerType() != OwnerType.GROUP && TextUtils.isEmpty(this.mAlbumInfo.getId()))) {
            this.mUserAlbumsInfos.add(getMobileAlbum());
        } else {
            this.mUserAlbumsInfos.add(this.mAlbumInfo);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        boolean z;
        boolean z2 = false;
        LocalizationManager.inflate((Context) this, getMenuInflater(), 2131689521, menu);
        MenuItem findItem = menu.findItem(2131625509);
        if (this.canCreateAlbum && this.mUploadTarget == 0) {
            z = true;
        } else {
            z = false;
        }
        findItem.setVisible(z);
        MenuItem findItem2 = menu.findItem(2131625215);
        if (this.mImages.isEmpty() || this.mChoiceMode != 1) {
            z2 = true;
        }
        findItem2.setVisible(z2);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625215:
                startActivityForResult(new Intent(this, PickImagesActivity.class), 1);
                return true;
            case 2131625509:
                new Builder(this).setDialogTitle(2131165652).setSubmitBtnText(2131165651).setShowAccessControls(true).show(getSupportFragmentManager(), null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected final void prepareCache() {
        try {
            if (Application.getCacheDir(this) != null) {
                File cacheDir = new File(Image.getUploaderChacheDir(this), "preview");
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
                if (cacheDir.exists()) {
                    StatFs stats = new StatFs(cacheDir.getPath());
                    long cacheMaxSize = (((long) stats.getBlockSize()) * ((long) stats.getAvailableBlocks())) / 6;
                    if (cacheMaxSize > 0) {
                        this.mImageCache = DiskLruCache.open(cacheDir, 1, 1, cacheMaxSize);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    protected final void hideProgressDialog() {
        DialogFragment fragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag("prgrdlg");
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    public void onAlbumEditSubmit(PhotoAlbumEditDialog dialog, CharSequence title, List<AccessType> accessTypes) {
        ProgressDialogFragment.createInstance(LocalizationManager.getString((Context) this, 2131166864), false).show(getSupportFragmentManager(), "prgrdlg");
        Bundle inData = new Bundle();
        inData.putString("ttl", title.toString());
        inData.putIntArray("accss", AccessType.asIntArray(accessTypes));
        GlobalBus.send(2131623961, new BusEvent(inData));
    }

    protected final void showQuitDialog(boolean toHome) {
        if (this.mImages.isEmpty() || (this.editedImagesCount == 0 && this.silentCancelIfNotEdited)) {
            goBack(toHome);
            return;
        }
        AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(this);
        builder.setTitle(getStringLocalized(2131165414));
        if (TextUtils.isEmpty(this.cancelAlertText)) {
            builder.setMessage(getStringLocalized(2131166419));
        } else {
            builder.setMessage(this.cancelAlertText);
        }
        builder.setNegativeButton(getStringLocalized(2131166257), new C09678());
        builder.setPositiveButton(getStringLocalized(2131166881), new C09689(toHome)).show();
    }

    public final void goBack(boolean toHome) {
        doCleanUp();
        if (toHome) {
            setResult(0, new Intent().putExtra("toHome", true));
        }
        finish();
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("album", this.mAlbumInfo);
        outState.putParcelableArrayList("imgs", this.mImages);
        if (this.mPopulatedAlbums) {
            outState.putParcelableArrayList("albms", this.mUserAlbumsInfos);
            outState.putBoolean("ppltd", this.mPopulatedAlbums);
        }
        outState.putBoolean("can_create_album", this.canCreateAlbum);
        outState.putInt("edited_images_count", this.editedImagesCount);
        super.onSaveInstanceState(outState);
    }

    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.imageEditReceiver);
        IOUtils.closeSilently(this.mImageCache);
        super.onDestroy();
    }

    private void requestAlbumList() {
        Bundle bundleInput = new Bundle();
        bundleInput.putBoolean("gtll", true);
        bundleInput.putInt("cnt", 100);
        OwnerType ownerType = this.mAlbumInfo == null ? OwnerType.UNKNOWN : this.mAlbumInfo.getOwnerType();
        if (ownerType == OwnerType.USER || ownerType == OwnerType.GROUP) {
            int owner;
            String ownerId;
            if (ownerType == OwnerType.USER) {
                owner = 0;
                ownerId = this.mAlbumInfo.getUserId();
            } else {
                owner = 1;
                ownerId = this.mAlbumInfo.getGroupId();
            }
            bundleInput.putInt("ownr", owner);
            bundleInput.putString("ownrid", ownerId);
        }
        bundleInput.putString("flds", new RequestFieldsBuilder().addFields(FILEDS.ALBUM_AID, FILEDS.ALBUM_PHOTOS_COUNT, FILEDS.ALBUM_TITLE, FILEDS.ALBUM_TYPE, FILEDS.ALBUM_USER_ID, FILEDS.PHOTO_PIC_640).build());
        GlobalBus.send(2131624002, new BusEvent(bundleInput));
        setProgressBarIndeterminateVisibility(true);
    }

    @Subscribe(on = 2131623946, to = 2131624140)
    public final void onPhotoAlbumCreateEvent(BusEvent event) {
        hideProgressDialog();
        Bundle data = event.bundleOutput;
        CharSequence title = data.getString("ttl");
        List<AccessType> accessTypes = AccessType.asList(data.getIntArray("accss"));
        if (event.resultCode == -1) {
            String aid = data.getString("aid");
            PhotoAlbumInfo album = new PhotoAlbumInfo();
            album.setTitle(title);
            album.setId(aid);
            album.setTypes(accessTypes);
            this.mUserAlbumsInfos.add(album);
            getSupportActionBar().setTitle(title);
            this.mAlbumInfo = album;
            sortAlbums();
            selectAlbumInActionBar();
            this.mUserAlbumsListAdapter.notifyDataSetChanged();
            return;
        }
        int errorTextRes = 2131165836;
        if (event.resultCode == 1) {
            errorTextRes = 2131166248;
        } else if (TextUtils.isEmpty(title) || event.resultCode == 2) {
            errorTextRes = 2131165839;
        }
        Toast.makeText(this.mContext, LocalizationManager.getString((Context) this, errorTextRes), 1).show();
        new Builder(this.mContext).setDialogTitle(2131165652).setSubmitBtnText(2131165651).setShowAccessControls(true).setAlbumAccessTypes(accessTypes).setAlbumTitle(title).show(getSupportFragmentManager(), null);
    }

    @Subscribe(on = 2131623946, to = 2131624179)
    public void onPhotoAlbumsRecieved(BusEvent event) {
        setProgressBarIndeterminateVisibility(false);
        if (event.resultCode == -1 && this.canSelectAlbum) {
            PhotoAlbumsInfo albumsInfo = (PhotoAlbumsInfo) event.bundleOutput.getParcelable("albmsnfo");
            if (albumsInfo != null) {
                List<PhotoAlbumInfo> albums = albumsInfo.getAlbums();
                if (albums != null) {
                    boolean hasMobileAlbum = false;
                    for (PhotoAlbumInfo album : albums) {
                        if (TextUtils.equals(album.getTitle(), this.mMobileAlbumTitle)) {
                            hasMobileAlbum = true;
                        }
                        if (TextUtils.equals(album.getTitle(), this.mAlbumInfo.getTitle())) {
                            this.mAlbumInfo = album;
                        }
                    }
                    if (!hasMobileAlbum) {
                        albums.add(getMobileAlbum());
                    }
                    albums.add(getPersonalAlbum());
                    if (this.mAlbumInfo != null && this.mAlbumInfo.getOwnerType() == OwnerType.GROUP) {
                        albums.add(this.mAlbumInfo);
                    }
                    this.mUserAlbumsInfos.clear();
                    this.mUserAlbumsInfos.addAll(albums);
                    sortAlbums();
                    this.mPopulatedAlbums = true;
                    if (this.mUserAlbumsListAdapter != null) {
                        this.mUserAlbumsListAdapter.notifyDataSetChanged();
                    }
                    selectAlbumInActionBar();
                }
            }
        } else if (!this.giveUpOnAlbums) {
            requestAlbumList();
            this.giveUpOnAlbums = true;
        }
    }

    protected final void sortAlbums() {
        Collections.sort(this.mUserAlbumsInfos, new PhotoAlbumsComporator(this.mPersonalAlbumTitle, this.mMobileAlbumTitle));
    }

    protected final PhotoAlbumInfo getMobileAlbum() {
        if (TextUtils.equals(this.mAlbumInfo.getId(), "application")) {
            return this.mAlbumInfo;
        }
        PhotoAlbumInfo mobileAlbum = new PhotoAlbumInfo();
        mobileAlbum.setId("application");
        mobileAlbum.setTitle(this.mMobileAlbumTitle);
        return mobileAlbum;
    }

    protected final PhotoAlbumInfo getPersonalAlbum() {
        PhotoAlbumInfo personalAlbum = new PhotoAlbumInfo();
        personalAlbum.setTitle(this.mPersonalAlbumTitle);
        personalAlbum.setId(null);
        return personalAlbum;
    }

    protected final void updateControlsState() {
        boolean z;
        boolean enabled = false;
        int current = 0;
        ImageEditInfo currentImageEditInfo = null;
        if (this.mImages.size() > 0) {
            int currentItemIndex = this.mPagerView.getCurrentItem();
            currentImageEditInfo = (ImageEditInfo) this.mImages.get(currentItemIndex);
            current = currentItemIndex + 1;
            enabled = true;
        }
        boolean isCurrentItemGif;
        if (currentImageEditInfo == null || !MimeTypes.isGif(currentImageEditInfo.getMimeType())) {
            isCurrentItemGif = false;
        } else {
            isCurrentItemGif = true;
        }
        this.mUploadBtn.setEnabled(enabled);
        View view = this.mCropBtn;
        if (!enabled || isCurrentItemGif) {
            z = false;
        } else {
            z = true;
        }
        view.setEnabled(z);
        view = this.mRotateCcwBtn;
        if (!enabled || isCurrentItemGif) {
            z = false;
        } else {
            z = true;
        }
        view.setEnabled(z);
        view = this.mRotateCwBtn;
        if (!enabled || isCurrentItemGif) {
            z = false;
        } else {
            z = true;
        }
        view.setEnabled(z);
        if (enabled) {
            this.mAddControlsView.setVisibility(8);
            this.mUploadBtn.setTextColor(getResources().getColor(2131493013));
            this.mImagesCountView.setText(getStringLocalized(2131165996, Integer.valueOf(current), Integer.valueOf(total)));
            return;
        }
        this.mAddControlsView.setVisibility(0);
        this.mUploadBtn.setTextColor(-7829368);
        this.mImagesCountView.setText(null);
    }

    protected void onResume() {
        super.onResume();
        updateControlsState();
        adjustToOrientation();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adjustToOrientation();
    }

    private void adjustToOrientation() {
        int screenLayout = getResources().getConfiguration().screenLayout & 15;
        int density = getResources().getDisplayMetrics().densityDpi;
        if (screenLayout == 1 || (screenLayout == 2 && density < 240)) {
            getWindow().setSoftInputMode(32);
        } else if (screenLayout == 4) {
            getWindow().setSoftInputMode(17);
        } else if (getResources().getConfiguration().orientation == 2) {
            getWindow().setSoftInputMode(33);
        } else {
            getWindow().setSoftInputMode(17);
        }
    }

    protected void onNewIntent(Intent intent) {
        parseImagesToEdit(intent);
    }

    private void parseImagesToEdit(Intent intent) {
        addImagesToEdit(intent.getParcelableArrayListExtra("imgs"));
    }

    private void addImagesToEdit(@NonNull ArrayList<ImageEditInfo> imagesToEdit) {
        if (imagesToEdit.size() > 0) {
            this.mImages.addAll(imagesToEdit);
            if (this.mEditFragmentsAdapter != null) {
                this.mEditFragmentsAdapter.notifyDataSetChanged();
            }
            supportInvalidateOptionsMenu();
        }
    }

    private void parseExternalImages(Intent data) {
        addImagesToEdit(toImageEditInfos(data.getParcelableArrayListExtra("gallery_images"), data.getBooleanExtra("temp", false)));
    }

    @NonNull
    private ArrayList<ImageEditInfo> toImageEditInfos(@NonNull ArrayList<GalleryImageInfo> externalImages, boolean temporaryImages) {
        ArrayList<ImageEditInfo> imagesToEdit = new ArrayList(externalImages.size());
        Iterator i$ = externalImages.iterator();
        while (i$.hasNext()) {
            imagesToEdit.add(toImageEditInfo((GalleryImageInfo) i$.next(), temporaryImages));
        }
        return imagesToEdit;
    }

    @NonNull
    private static ImageEditInfo toImageEditInfo(@NonNull GalleryImageInfo externalImage, boolean isTemporary) {
        ImageEditInfo imageEditInfo = new ImageEditInfo();
        imageEditInfo.setUri(externalImage.uri);
        imageEditInfo.setMimeType(externalImage.mimeType);
        imageEditInfo.setOriginalRotation(externalImage.rotation);
        imageEditInfo.setRotation(externalImage.rotation);
        imageEditInfo.setWidth(externalImage.width);
        imageEditInfo.setHeight(externalImage.height);
        imageEditInfo.setTemporary(isTemporary);
        imageEditInfo.setWasEdited(false);
        return imageEditInfo;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int positionToScroll = -1;
        if (1 != requestCode) {
            super.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode == -1) {
            positionToScroll = this.mImages.size();
            parseExternalImages(data);
        }
        if (positionToScroll != -1) {
            this.mPagerView.setCurrentItem(positionToScroll, true);
        }
    }

    protected final void doCleanUp() {
        ThreadUtil.execute(new Runnable() {
            public void run() {
                Iterator i$ = PrepareImagesActivity.this.mImages.iterator();
                while (i$.hasNext()) {
                    ImageEditInfo image = (ImageEditInfo) i$.next();
                    if (image.isTemporary()) {
                        FileUtils.deleteFileAtUri(image.getUri());
                    }
                }
            }
        });
    }

    public void onBackPressed() {
        showQuitDialog(false);
    }

    protected final void returnSuccess() {
        Iterator i$ = this.mImages.iterator();
        while (i$.hasNext()) {
            ImageEditInfo image = (ImageEditInfo) i$.next();
            image.setUploadTarget(this.mUploadTarget);
            image.setAlbumInfo(this.mAlbumInfo);
            String comment = image.getComment();
            if (comment != null) {
                comment = comment.trim();
                if (comment.length() < 1) {
                    comment = null;
                }
                image.setComment(comment);
            }
        }
        if (this.uploadFromPhotoRoll) {
            Bundle inputBundle = new Bundle();
            inputBundle.putParcelableArrayList("imgs", this.mImages);
            inputBundle.putInt("impldract", 1);
            inputBundle.putInt("upload_source_id", 1);
            GlobalBus.send(2131624084, new BusEvent(inputBundle));
            PhotoRollSettingsHelper.setUploadAttempt();
            PhotoRollLog.logUploadAttempt();
        } else {
            setResult(-1, new Intent().putExtras(getIntent()).putParcelableArrayListExtra("imgs", this.mImages));
        }
        finish();
    }

    public void onRemoveClicked(ImageEditFragment fragment) {
        fragment.setRemoveButtonEnabled(false);
        int position = this.mEditFragmentsAdapter.getItemPosition(fragment);
        View view = fragment.getView();
        if (this.mImages.size() > 1) {
            PageTransformer mRemoveTransformer;
            int dragWidth = (view.getMeasuredWidth() + this.mPagerView.getPageMargin()) - 1;
            if (position == this.mImages.size() - 1) {
                dragWidth = -dragWidth;
                mRemoveTransformer = new RemoveBackwardsTransformer();
            } else {
                mRemoveTransformer = new RemoveForwardPageTransformer();
            }
            this.mPagerView.setPageTransformer(true, mRemoveTransformer);
            ValueAnimator animator = ObjectAnimator.ofInt(new int[]{0, dragWidth});
            animator.addUpdateListener(new AnimatorUpdateListener() {
                private int prev;

                {
                    this.prev = 0;
                }

                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = ((Integer) animation.getAnimatedValue()).intValue();
                    PrepareImagesActivity.this.mPagerView.fakeDragBy((float) (-(value - this.prev)));
                    this.prev = value;
                }
            });
            animator.addListener(new AnonymousClass12(fragment, position));
            this.mPagerView.beginFakeDrag();
            animator.start();
            return;
        }
        if (this.mImages.size() > 1) {
            int newPosition = position + 1;
            if (this.mImages.size() == newPosition) {
                newPosition = position - 1;
            }
            this.mPagerView.setCurrentItem(newPosition, true);
        }
        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(250);
        animation.setFillAfter(true);
        animation.setAnimationListener(new AnonymousClass13(fragment, position));
        view.startAnimation(animation);
    }

    protected final void doRemovePage(int position) {
        this.mImages.remove(position);
        this.mEditFragmentsAdapter.removeItem(position);
        this.mEditFragmentsAdapter.notifyDataSetChanged();
        ((ImageEditFragment) this.mEditFragmentsAdapter.getCurrentPrimaryItem()).setRemoveButtonVisibility(true);
        this.mPagerView.requestFocus();
        updateControlsState();
        invalidateOptionsMenu();
    }

    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        if (action == 0) {
            this.mPagerView.setBlocked(true);
        } else if (action != 2) {
            this.mPagerView.setBlocked(false);
        }
        return false;
    }

    private static int getImageChangedCount(AtomicBoolean[] flags, int flagIndex, boolean newValue) {
        int setFlagCountBefore = 0;
        int setFlagCountAfter = 0;
        for (int i = 0; i < flags.length; i++) {
            if (flags[i].get()) {
                setFlagCountBefore++;
                if (i != flagIndex) {
                    setFlagCountAfter++;
                }
            }
            if (i == flagIndex) {
                flags[i].set(newValue);
                if (newValue) {
                    setFlagCountAfter++;
                }
            }
        }
        if (setFlagCountBefore == 0 && setFlagCountAfter > 0) {
            return 1;
        }
        if (setFlagCountBefore <= 0 || setFlagCountAfter != 0) {
            return 0;
        }
        return -1;
    }

    private static boolean or(AtomicBoolean... flags) {
        for (AtomicBoolean flag : flags) {
            if (flag.get()) {
                return true;
            }
        }
        return false;
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
