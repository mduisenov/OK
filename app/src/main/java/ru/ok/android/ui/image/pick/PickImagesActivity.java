package ru.ok.android.ui.image.pick;

import android.annotation.TargetApi;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.ui.image.pick.ImageGridAdapter.OnSelectionChangeListener;
import ru.ok.android.ui.utils.FabHelper;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.UIUtils;
import ru.ok.android.utils.localization.LocalizationManager;

public final class PickImagesActivity extends BaseActivity implements OnNavigationListener, LoaderCallbacks<ArrayList<DeviceGalleryInfo>>, ActionBar.OnNavigationListener, OnClickListener, OnGlobalLayoutListener, OnSelectionChangeListener {
    private String actionText;
    private int currentColumnsCount;
    private int currentGalleryId;
    private View emptyView;
    private View errorView;
    private FloatingActionButton fabCamera;
    private ArrayList<DeviceGalleryInfo> galleries;
    private ImageGridAdapter gridAdapter;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView gridView;
    private int maxCount;
    private View progressView;
    protected ArrayList<GalleryImageInfo> selectedPhotos;
    private int state;

    /* renamed from: ru.ok.android.ui.image.pick.PickImagesActivity.1 */
    class C09941 extends ArrayList<GalleryImageInfo> {
        final /* synthetic */ GalleryImageInfo val$cameraImage;

        C09941(GalleryImageInfo galleryImageInfo) {
            this.val$cameraImage = galleryImageInfo;
            add(this.val$cameraImage);
        }
    }

    /* renamed from: ru.ok.android.ui.image.pick.PickImagesActivity.2 */
    class C09952 implements OnClickListener {
        C09952() {
        }

        public void onClick(View view) {
            int size = PickImagesActivity.this.selectedPhotos.size();
            if (PickImagesActivity.this.maxCount <= 0 || size <= PickImagesActivity.this.maxCount) {
                PickImagesActivity.this.setResult(-1, new Intent().putParcelableArrayListExtra("gallery_images", PickImagesActivity.this.selectedPhotos));
                PickImagesActivity.this.addStatEvent("pick-photos-gallery-clicked");
                PickImagesActivity.this.finish();
                return;
            }
            Toast.makeText(view.getContext(), PickImagesActivity.this.getStringLocalized(2131166070, Integer.valueOf(PickImagesActivity.this.maxCount)), 0).show();
        }
    }

    public PickImagesActivity() {
        this.selectedPhotos = new ArrayList();
        this.state = -1;
    }

    @TargetApi(23)
    protected void onCreateLocalized(Bundle savedInstanceState) {
        int state;
        super.onCreateLocalized(savedInstanceState);
        supportInvalidateOptionsMenu();
        setResult(0);
        setContentView(2130903085);
        initStateFromIntent();
        if (savedInstanceState != null) {
            state = savedInstanceState.getInt("pickstate");
            restoreSavedState(savedInstanceState);
        } else {
            if (VERSION.SDK_INT >= 16) {
                if (PermissionUtils.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
                    state = -2;
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 2);
                }
            }
            state = 0;
        }
        initGridView();
        initCameraFab();
        setState(state);
        if (state != -2) {
            getSupportLoaderManager().initLoader(1337, null, this);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionUtils.getGrantResult(grantResults) == 0) {
            setState(0);
            getSupportLoaderManager().initLoader(1337, null, this);
            return;
        }
        finish();
    }

    private void initStateFromIntent() {
        this.maxCount = getIntent().getIntExtra("max_count", 0);
        this.actionText = getIntent().getStringExtra("action_text");
        if (TextUtils.isEmpty(this.actionText)) {
            this.actionText = getStringLocalized(2131166498);
        }
    }

    private void restoreSavedState(Bundle savedInstanceState) {
        this.galleries = savedInstanceState.getParcelableArrayList("pickgalrs");
        this.selectedPhotos = savedInstanceState.getParcelableArrayList("pickphts");
        this.currentGalleryId = savedInstanceState.getInt("pickgal");
        this.maxCount = savedInstanceState.getInt("max_count");
        this.actionText = savedInstanceState.getString("action_text");
    }

    private void initCameraFab() {
        this.fabCamera = FabHelper.createCameraFab(getContext(), getCoordinatorManager().coordinatorLayout);
        this.fabCamera.setOnClickListener(this);
        getCoordinatorManager().ensureFab(this.fabCamera);
    }

    private void initGridView() {
        this.gridView = (RecyclerView) findViewById(C0263R.id.grid);
        this.gridLayoutManager = new GridLayoutManager(this, 3);
        this.gridView.setLayoutManager(this.gridLayoutManager);
        int choiceMode = getIntent().getIntExtra("choice_mode", 0);
        this.gridView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        this.gridAdapter = new ImageGridAdapter(this, this.selectedPhotos, choiceMode);
        this.gridAdapter.setOnSelectionChangeListener(this);
        this.gridView.setAdapter(this.gridAdapter);
        this.progressView = findViewById(2131624548);
        this.emptyView = findViewById(2131624434);
        findViewById(2131624549).setOnClickListener(this);
        this.errorView = findViewById(2131624551);
    }

    public void onGlobalLayout() {
        int columnsCount = Math.max(this.gridView.getWidth() / getResources().getDimensionPixelSize(2131230747), 3);
        if (columnsCount != this.currentColumnsCount) {
            int position = this.gridLayoutManager.findFirstVisibleItemPosition();
            this.gridLayoutManager.setSpanCount(columnsCount);
            this.currentColumnsCount = columnsCount;
            this.gridLayoutManager.scrollToPosition(position);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pickstate", this.state);
        outState.putParcelableArrayList("pickgalrs", this.galleries);
        outState.putParcelableArrayList("pickphts", this.selectedPhotos);
        outState.putInt("pickgal", this.currentGalleryId);
        outState.putInt("max_count", this.maxCount);
        outState.putString("action_text", this.actionText);
    }

    private void setState(int state) {
        if (this.state != state) {
            this.state = state;
            setProgressBarIndeterminateVisibility(false);
            this.gridView.setVisibility(4);
            this.progressView.setVisibility(4);
            this.emptyView.setVisibility(4);
            this.errorView.setVisibility(4);
            setActionBarEmpty();
            int fabCameraVisibility = 8;
            switch (state) {
                case PagerAdapter.POSITION_NONE /*-2*/:
                case RECEIVED_VALUE:
                    setProgressBarIndeterminateVisibility(true);
                    this.progressView.setVisibility(0);
                    break;
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    this.errorView.setVisibility(0);
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    this.emptyView.setVisibility(0);
                    break;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    this.gridView.setVisibility(0);
                    setActionBarAlbums();
                    fabCameraVisibility = 0;
                    break;
            }
            this.fabCamera.setVisibility(fabCameraVisibility);
        }
    }

    private void setActionBarEmpty() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(0);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    private void setActionBarAlbums() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(1);
        actionBar.setListNavigationCallbacks(new GallerySpinnerAdapter(actionBar.getThemedContext(), this.galleries), this);
        int toSelect = 0;
        int buckedToFind = this.currentGalleryId;
        for (int i = 0; i < this.galleries.size(); i++) {
            if (((DeviceGalleryInfo) this.galleries.get(i)).id == buckedToFind) {
                toSelect = i;
                break;
            }
        }
        actionBar.setSelectedNavigationItem(toSelect);
    }

    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        selectGallery((DeviceGalleryInfo) this.galleries.get(itemPosition));
        return false;
    }

    private void selectGallery(DeviceGalleryInfo gallery) {
        if (this.currentGalleryId != gallery.id) {
            this.currentGalleryId = gallery.id;
            this.selectedPhotos.clear();
        }
        this.gridAdapter.clear();
        this.gridAdapter.addAll(gallery.photos);
        this.gridAdapter.notifyDataSetChanged();
        onSelectionChange();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case 2131624549:
            case 2131624808:
                addStatEvent("pick-photos-camera-clicked");
                startCameraActivity();
            default:
        }
    }

    private String getStatisticsPrefix() {
        Intent intent = getIntent();
        return intent != null ? intent.getStringExtra("statistics_prefix") : null;
    }

    private void startCameraActivity() {
        startActivityForResult(new Intent(this, PickFromCameraActivity.class), 1);
    }

    public void onSelectionChange() {
        supportInvalidateOptionsMenu();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            onCameraResult(resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onCameraResult(int resultCode, Intent cameraData) {
        if (-1 == resultCode) {
            setResult(-1, new Intent().putParcelableArrayListExtra("gallery_images", new C09941((GalleryImageInfo) cameraData.getParcelableExtra("camera_image"))).putExtra("temp", true));
            addStatEvent("pick-photos-camera-received");
            finish();
        }
    }

    private void addStatEvent(String event) {
        String statPrefix = getStatisticsPrefix();
        if (!TextUtils.isEmpty(statPrefix)) {
            StatisticManager.getInstance().addStatisticEvent(statPrefix + event, new Pair[0]);
        }
    }

    public Loader<ArrayList<DeviceGalleryInfo>> onCreateLoader(int id, Bundle bundle) {
        if (id == 1337) {
            return new GalleriesLoader(this);
        }
        return null;
    }

    public void onLoadFinished(Loader<ArrayList<DeviceGalleryInfo>> loader, ArrayList<DeviceGalleryInfo> galleries) {
        if (loader.getId() == 1337) {
            this.galleries = galleries;
            onGalleriesLoaded();
        }
    }

    private void onGalleriesLoaded() {
        if (this.galleries == null) {
            setState(1);
        } else if (this.galleries.isEmpty()) {
            setState(2);
        } else {
            setState(3);
        }
    }

    public void onLoaderReset(Loader<ArrayList<DeviceGalleryInfo>> loader) {
        this.galleries = null;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        LocalizationManager.inflate((Context) this, getMenuInflater(), 2131689515, menu);
        MenuItem item = menu.findItem(2131625502);
        if (item != null) {
            TextView view = (TextView) MenuItemCompat.getActionView(item);
            item.setTitle(this.actionText);
            view.setText(this.actionText);
            LocalizationManager.from(this).registerView(view, 2130903387);
            view.setOnClickListener(new C09952());
        }
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(2131625502);
        if (item != null) {
            MenuItemCompat.getActionView(item).setEnabled(!this.selectedPhotos.isEmpty());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    protected void onDestroy() {
        super.onDestroy();
        UIUtils.removeOnGlobalLayoutListener(this.gridView, this);
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
