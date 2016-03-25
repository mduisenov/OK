package ru.ok.android.fragments.image;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.image.PhotoAlbumsHelper.AlbumsHelperCallback;
import ru.ok.android.model.image.ImageForUpload;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity;
import ru.ok.android.ui.adapters.photo.AlbumsInfoAdapter;
import ru.ok.android.ui.adapters.photo.AlbumsInfoAdapter.OnNearListEndListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.dialogs.ProgressDialogFragment;
import ru.ok.android.ui.dialogs.photo.PhotoAlbumEditDialog;
import ru.ok.android.ui.dialogs.photo.PhotoAlbumEditDialog.Builder;
import ru.ok.android.ui.dialogs.photo.PhotoAlbumEditDialog.PhotoAlbumDialogListener;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.image.view.AlbumFinder;
import ru.ok.android.ui.tabbar.HideTabbarGridView;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.java.api.request.image.GetPhotoAlbumsRequest.FILEDS;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.AccessType;
import ru.ok.model.photo.PhotoAlbumsInfo;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.photo.PhotosInfo;

public class PhotoAlbumsFragment extends BaseFragment implements AlbumsHelperCallback, PhotoAlbumDialogListener, AlbumFinder {
    protected AlbumsInfoAdapter albumsInfoAdapter;
    protected ArrayList<PhotoAlbumInfo> albumsInfoList;
    protected PhotoAlbumsHelper albumsListHelper;
    protected String currentAnchor;
    protected SmartEmptyViewAnimated emptyView;
    protected HideTabbarGridView gridView;
    private boolean isPaused;
    protected int minColumnWidth;
    protected String pendingAnchor;
    protected PhotoOwner photoOwner;
    private int state;

    /* renamed from: ru.ok.android.fragments.image.PhotoAlbumsFragment.1 */
    class C03071 implements OnStubButtonClickListener {
        C03071() {
        }

        public void onStubButtonClick(Type type) {
            PhotoAlbumsFragment.this.requestAdditionalData();
            PhotoAlbumsFragment.this.setState(0);
        }
    }

    /* renamed from: ru.ok.android.fragments.image.PhotoAlbumsFragment.2 */
    class C03082 implements OnGlobalLayoutListener {
        private int currentColumnsCount;

        C03082() {
        }

        public void onGlobalLayout() {
            int columnsCount = Math.max(PhotoAlbumsFragment.this.gridView.getWidth() / PhotoAlbumsFragment.this.minColumnWidth, 2);
            if (columnsCount != this.currentColumnsCount) {
                int position = PhotoAlbumsFragment.this.gridView.getFirstVisiblePosition();
                PhotoAlbumsFragment.this.gridView.setNumColumns(columnsCount);
                this.currentColumnsCount = columnsCount;
                PhotoAlbumsFragment.this.gridView.setSelection(position);
            }
        }
    }

    /* renamed from: ru.ok.android.fragments.image.PhotoAlbumsFragment.3 */
    class C03093 implements OnItemClickListener {
        C03093() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (PhotoAlbumsFragment.this.getActivity() != null) {
                ((OnAlbumSelectedListener) PhotoAlbumsFragment.this.getActivity()).onAlbumClickListener((PhotoAlbumInfo) PhotoAlbumsFragment.this.albumsInfoList.get(position), PhotoAlbumsFragment.this.photoOwner);
            }
        }
    }

    /* renamed from: ru.ok.android.fragments.image.PhotoAlbumsFragment.4 */
    class C03104 implements OnNearListEndListener {
        C03104() {
        }

        public void onNearListEnd() {
            PhotoAlbumsFragment.this.loadNextAlbumsChunk();
        }
    }

    public interface OnAlbumSelectedListener {
        void onAlbumClickListener(PhotoAlbumInfo photoAlbumInfo, PhotoOwner photoOwner);
    }

    public PhotoAlbumsFragment() {
        this.albumsListHelper = new PhotoAlbumsHelper(this);
        this.isPaused = true;
    }

    public static PhotoAlbumsFragment newInstance(PhotoOwner pOwner) {
        PhotoAlbumsFragment fragment = new PhotoAlbumsFragment();
        Bundle args = new Bundle();
        args.putParcelable("pwnr", pOwner);
        fragment.setArguments(args);
        return fragment;
    }

    protected int getLayoutId() {
        return 2130903375;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().supportInvalidateOptionsMenu();
        if (savedInstanceState != null) {
            this.currentAnchor = savedInstanceState.getString("anchor");
            this.state = savedInstanceState.getInt("pastate");
        }
        tryPopulateOwner(savedInstanceState);
        tryPopulateAlbumsList(savedInstanceState);
        requestAdditionalData();
    }

    private boolean tryPopulateOwner(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.photoOwner = (PhotoOwner) savedInstanceState.getParcelable("pwnr");
        }
        if (this.photoOwner == null) {
            this.photoOwner = (PhotoOwner) getArguments().getParcelable("pwnr");
        }
        this.photoOwner.tryPopulateOwner();
        return this.photoOwner.getOwnerInfo() != null;
    }

    private final boolean tryPopulateAlbumsList(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.albumsInfoList = savedInstanceState.getParcelableArrayList("ailst");
        }
        return this.albumsInfoList != null;
    }

    protected final void populateAlbumsInfosListExtras(Bundle data) {
        data.putBoolean("ganfo", true);
        data.putParcelable("wnrnfo", this.photoOwner);
        data.putInt("cnt", 40);
        data.putString("flds", new RequestFieldsBuilder().withPrefix(this.photoOwner.getType() == 1 ? "group_" : null).addFields(FILEDS.ALBUM_ALL, FILEDS.PHOTO_ALL).build());
    }

    private void requestAdditionalData() {
        if (getActivity() != null) {
            boolean requestOwnerInfo;
            boolean requestAlbumsInfo;
            if (this.photoOwner.getOwnerInfo() == null) {
                requestOwnerInfo = true;
            } else {
                requestOwnerInfo = false;
            }
            if (this.albumsInfoList == null) {
                requestAlbumsInfo = true;
            } else {
                requestAlbumsInfo = false;
            }
            if (requestOwnerInfo || requestAlbumsInfo) {
                Bundle inBundle = new Bundle();
                if (requestOwnerInfo) {
                    inBundle.putBoolean("gwnrnfo", true);
                    inBundle.putParcelable("wnrnfo", this.photoOwner);
                }
                if (requestAlbumsInfo) {
                    populateAlbumsInfosListExtras(inBundle);
                    if (this.photoOwner.getType() == 0) {
                        inBundle.putBoolean("rtfp", true);
                        inBundle.putBoolean("rtmp", true);
                    }
                }
                GlobalBus.send(2131624001, new BusEvent(inBundle));
                getActivity().setProgressBarIndeterminateVisibility(true);
            }
        }
    }

    public void onResume() {
        this.isPaused = false;
        super.onResume();
        updateTitle();
    }

    public void onPause() {
        this.isPaused = true;
        super.onPause();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.minColumnWidth = getResources().getDimensionPixelSize(2131230740);
        View view = inflater.inflate(getLayoutId(), container, false);
        this.emptyView = (SmartEmptyViewAnimated) view.findViewById(2131624434);
        this.emptyView.setButtonClickListener(new C03071());
        this.gridView = (HideTabbarGridView) view.findViewById(C0263R.id.grid);
        this.gridView.getViewTreeObserver().addOnGlobalLayoutListener(new C03082());
        this.gridView.setOnItemClickListener(new C03093());
        if (this.albumsInfoList != null) {
            updateGridAdapter();
            if (this.albumsInfoList.isEmpty()) {
                setState(3);
            } else {
                setState(1);
                if (savedInstanceState != null) {
                    this.gridView.setSelection(savedInstanceState.getInt("pos"));
                }
            }
        }
        updateTitle();
        setState(this.state);
        return view;
    }

    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter || getParentFragment() == null) {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
        return NESTED_FRAGMENT_EXIT_DUMMY_ANIMATION;
    }

    protected void updateGridAdapter() {
        if (this.albumsInfoAdapter == null && getActivity() != null) {
            this.albumsInfoAdapter = new AlbumsInfoAdapter(getActivity(), this.albumsInfoList);
            this.albumsInfoAdapter.setOnNearListEndListener(new C03104());
            this.albumsInfoAdapter.setMinTileSize(this.minColumnWidth);
        }
        this.gridView.setAdapter(this.albumsInfoAdapter);
    }

    public void setState(int state) {
        this.state = state;
        switch (state) {
            case RECEIVED_VALUE:
                this.emptyView.setState(State.LOADING);
                this.emptyView.setVisibility(0);
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.emptyView.setVisibility(8);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                this.emptyView.setType(Type.ALBUM_LOAD_FAIL);
                this.emptyView.setState(State.LOADED);
                this.emptyView.setVisibility(0);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                this.emptyView.setType(Type.ALBUMS);
                this.emptyView.setState(State.LOADED);
                this.emptyView.setVisibility(0);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                this.emptyView.setType(Type.RESTRICTED);
                this.emptyView.setState(State.LOADED);
                this.emptyView.setVisibility(0);
            default:
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("pwnr", this.photoOwner);
        outState.putParcelableArrayList("ailst", this.albumsInfoList);
        outState.putString("anchor", this.currentAnchor);
        outState.putInt("pos", this.gridView.getFirstVisiblePosition());
        outState.putInt("pastate", this.state);
    }

    protected final void loadNextAlbumsChunk() {
        if (getActivity() != null && this.pendingAnchor == null && this.currentAnchor != null) {
            this.pendingAnchor = this.currentAnchor;
            Bundle inBundle = new Bundle();
            populateAlbumsInfosListExtras(inBundle);
            inBundle.putString("anchr", this.currentAnchor);
            GlobalBus.send(2131624001, new BusEvent(inBundle));
            getActivity().setProgressBarIndeterminateVisibility(true);
        }
    }

    protected CharSequence getTitle() {
        if (this.photoOwner.getOwnerInfo() == null || !this.photoOwner.isCurrentUser()) {
            return getStringLocalized(2131166597);
        }
        return getStringLocalized(2131165282);
    }

    protected CharSequence getSubtitle() {
        return subtitleFromPhotoOwner(this.photoOwner);
    }

    public static String subtitleFromPhotoOwner(PhotoOwner photoOwner) {
        if (photoOwner.getOwnerInfo() == null || photoOwner.isCurrentUser()) {
            return null;
        }
        if (photoOwner.getType() == 1) {
            return ((GroupInfo) photoOwner.getOwnerInfo()).getName();
        }
        return ((UserInfo) photoOwner.getOwnerInfo()).getAnyName();
    }

    protected final void updateTitle() {
        if (getActivity() != null && getParentFragment() == null) {
            if (this.photoOwner.isCurrentUser()) {
                OdklSlidingMenuFragmentActivity.setMenuIndicatorEnable(getActivity(), true);
            } else {
                OdklSlidingMenuFragmentActivity.setMenuIndicatorEnable(getActivity(), false);
            }
            updateActionBarState();
        }
    }

    @Nullable
    public PhotoAlbumInfo findAlbumById(@NonNull String aid) {
        return this.albumsListHelper.findAlbumById(aid);
    }

    @Subscribe(on = 2131623946, to = 2131624157)
    public void onPhotoDeleted(BusEvent event) {
        if (event.resultCode == -1) {
            String aid = event.bundleOutput.getString("aid");
            PhotoAlbumInfo albumInfo = this.albumsListHelper.getPhotoAlbum(aid);
            if (albumInfo != null) {
                Bundle bundle = new Bundle();
                bundle.putString("aid", aid);
                if (this.photoOwner.getType() == 0) {
                    bundle.putString("fid", this.photoOwner.getId());
                } else {
                    bundle.putString("gid", this.photoOwner.getId());
                }
                GlobalBus.send(2131624000, new BusEvent(bundle));
                albumInfo.setPhotoCount(albumInfo.getPhotoCount() - 1);
                this.albumsInfoAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624177)
    public void onAlbumInfoRecieved(BusEvent event) {
        if (event.resultCode == -1) {
            PhotoAlbumInfo info = (PhotoAlbumInfo) event.bundleOutput.getParcelable("lbmnfo");
            PhotoAlbumInfo albumInfo = this.albumsListHelper.getPhotoAlbum(info.getId());
            if (albumInfo != null) {
                albumInfo.setMainPhotoInfo(info.getMainPhotoInfo());
                this.albumsInfoAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624159)
    public void onPhotoAlbumEdit(BusEvent event) {
        if (event.resultCode == -1) {
            PhotoAlbumInfo albumInfo = this.albumsListHelper.findAlbumById(event.bundleInput.getString("aid"));
            if (albumInfo != null) {
                String title = event.bundleInput.getString("ttl");
                List<AccessType> accessTypes = AccessType.asList(event.bundleInput.getIntArray("accss"));
                albumInfo.setTitle(title);
                albumInfo.setTypes(accessTypes);
                this.albumsListHelper.updateAlbum(albumInfo);
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624156)
    public void onPhotoAlbumDelete(BusEvent event) {
        this.albumsListHelper.removeAlbum(this.albumsListHelper.findAlbumById(event.bundleInput.getString("aid")));
    }

    @Subscribe(on = 2131623946, to = 2131624140)
    public void onPhoAlbumCreated(BusEvent event) {
        boolean z = true;
        Bundle data = event.bundleOutput;
        hideProgressDialog();
        String title = data.getString("ttl");
        List<AccessType> accessTypes = AccessType.asList(data.getIntArray("accss"));
        if (event.resultCode == -1) {
            this.albumsListHelper.addNewAlbum(this.photoOwner, data.getString("aid"), title, data.getString("gid"), accessTypes);
        } else if (getActivity() != null) {
            int errorTextRes = 2131165836;
            if (event.resultCode == 1) {
                errorTextRes = 2131166248;
            } else if (TextUtils.isEmpty(title) || event.resultCode == 2) {
                errorTextRes = 2131165839;
            }
            Toast.makeText(getActivity(), getStringLocalized(errorTextRes), 1).show();
            if (!this.isPaused) {
                Builder submitBtnText = new Builder(getActivity()).setDialogTitle(2131165652).setSubmitBtnText(2131165651);
                if (this.photoOwner.getType() != 0) {
                    z = false;
                }
                submitBtnText.setShowAccessControls(z).setAlbumAccessTypes(accessTypes).setAlbumTitle(title).show(getChildFragmentManager(), null);
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624258)
    public void onSetMainPhotoEvent(BusEvent event) {
        if (event.resultCode == -1) {
            Bundle data = event.bundleOutput;
            this.albumsListHelper.setAlbumMainPhoto(data.getString("aid"), (PhotoInfo) data.getParcelable("pnfo"));
        }
    }

    @Subscribe(on = 2131623946, to = 2131624178)
    public void onPhotoAlbumsEvent(BusEvent event) {
        if (getActivity() != null) {
            if (event.resultCode == -1) {
                PhotoAlbumsInfo paip = (PhotoAlbumsInfo) event.bundleOutput.getParcelable("albmnfo");
                this.pendingAnchor = null;
                if (paip != null) {
                    onAlbumsReceived(paip, (PhotosInfo) event.bundleOutput.getParcelable("mpnfo"), (PhotosInfo) event.bundleOutput.getParcelable("tpnfo"));
                }
                PhotoOwner photoOwnerResp = (PhotoOwner) event.bundleOutput.getParcelable("wnrnfo");
                if (photoOwnerResp != null) {
                    this.photoOwner = photoOwnerResp;
                    if (getActivity() != null) {
                        getActivity().supportInvalidateOptionsMenu();
                        updateTitle();
                    }
                }
            } else if (event.resultCode == 2) {
                setState(4);
            } else if (this.albumsInfoList == null) {
                setState(2);
            }
            getActivity().setProgressBarIndeterminateVisibility(false);
        }
    }

    protected final void onAlbumsReceived(PhotoAlbumsInfo paip, PhotosInfo mainPhotoInfo, PhotosInfo tagsPhotoInfo) {
        Collection<PhotoAlbumInfo> albums;
        if (this.albumsInfoList == null) {
            this.albumsInfoList = new ArrayList();
            if (mainPhotoInfo != null && (mainPhotoInfo.getTotalCount() > 0 || this.photoOwner.isCurrentUser())) {
                PhotoAlbumInfo album = PhotoAlbumsHelper.createVirtualAlbum(null, getStringLocalized(2131166341), mainPhotoInfo);
                if (this.photoOwner.isCurrentUser()) {
                    album.setCanAddPhoto(true);
                }
                this.albumsInfoList.add(album);
            }
            if (!(tagsPhotoInfo == null || tagsPhotoInfo == null || tagsPhotoInfo.getTotalCount() <= 0)) {
                this.albumsInfoList.add(PhotoAlbumsHelper.createVirtualAlbum("tags", getStringLocalized(2131165285), tagsPhotoInfo));
            }
            if (paip.getAlbums() != null) {
                albums = new ArrayList(paip.getAlbums().size());
                for (PhotoAlbumInfo info : paip.getAlbums()) {
                    albums.add(info.clone());
                }
                if (this.photoOwner.getType() == 0) {
                    albums = this.albumsListHelper.filterEmptyAlbums(albums);
                }
                this.albumsInfoList.addAll(albums);
            }
            updateGridAdapter();
        } else if (paip.getAlbums() != null) {
            albums = new ArrayList(paip.getAlbums().size());
            for (PhotoAlbumInfo info2 : paip.getAlbums()) {
                albums.add(info2.clone());
            }
            if (this.photoOwner.getType() == 0) {
                albums = this.albumsListHelper.filterEmptyAlbums(albums);
            }
            albums.removeAll(this.albumsInfoList);
            this.albumsInfoList.addAll(albums);
            this.albumsInfoAdapter.notifyDataSetChanged();
        }
        if (this.albumsInfoList.isEmpty()) {
            setState(3);
        } else {
            setState(1);
        }
        if (paip.isHasMore()) {
            this.currentAnchor = paip.getPagingAnchor();
        } else {
            this.currentAnchor = null;
        }
    }

    @Subscribe(on = 2131623946, to = 2131624225)
    public void onImageUploaded(BusEvent event) {
        if (event.resultCode == 1) {
            ImageForUpload image = (ImageForUpload) event.bundleOutput.getParcelable("img");
            PhotoAlbumInfo albumInfo = image.getAlbumInfo();
            if (image.getCurrentStatus() == 5 && TextUtils.equals(albumInfo.getId(), image.getAlbumInfo().getId()) && this.albumsListHelper.updateAlbumWithNewUpload(this.photoOwner, albumInfo)) {
                Bundle bundle = new Bundle();
                bundle.putString("aid", albumInfo.getId());
                if (this.photoOwner.getType() == 0) {
                    bundle.putString("fid", this.photoOwner.getId());
                } else {
                    bundle.putString("gid", this.photoOwner.getId());
                }
                GlobalBus.send(2131624000, new BusEvent(bundle));
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624184)
    public void onPhotoAlbumLike(BusEvent event) {
        if (event.resultCode == -1) {
            String aid = event.bundleInput.getString("album_id");
            if (!TextUtils.isEmpty(aid)) {
                PhotoAlbumInfo album = this.albumsListHelper.getPhotoAlbum(aid);
                if (album != null) {
                    album.setViewerLiked(true);
                    album.setLikesCount(album.getLikesCount() + 1);
                }
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624261)
    public void onPhotoAlbumUnlike(BusEvent event) {
        if (event.resultCode == -1) {
            String aid = event.bundleInput.getString("album_id");
            if (!TextUtils.isEmpty(aid)) {
                PhotoAlbumInfo album = this.albumsListHelper.getPhotoAlbum(aid);
                if (album != null) {
                    album.setViewerLiked(false);
                    album.setLikesCount(album.getLikesCount() - 1);
                }
            }
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!(!inflateMenuLocalized(2131689513, menu) || this.photoOwner == null || this.photoOwner.getOwnerInfo() == null)) {
            MenuItem addPhotoItem = menu.findItem(2131625215);
            MenuItem addAlbumItem = menu.findItem(2131625494);
            if (this.photoOwner.isCurrentUser()) {
                addPhotoItem.setVisible(true);
                addAlbumItem.setVisible(true);
            } else if (this.photoOwner.getType() == 1) {
                addPhotoItem.setVisible(false);
                addAlbumItem.setVisible(((GroupInfo) this.photoOwner.getOwnerInfo()).isCanAddAlbum());
            } else {
                addPhotoItem.setVisible(false);
                addAlbumItem.setVisible(false);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean z = false;
        Activity activity = getActivity();
        if (activity == null) {
            return false;
        }
        switch (item.getItemId()) {
            case 2131625215:
                startPhotoChooserActivity(activity);
                return true;
            case 2131625494:
                Builder submitBtnText = new Builder(activity).setDialogTitle(2131165652).setSubmitBtnText(2131165651);
                if (this.photoOwner.getType() == 0) {
                    z = true;
                }
                submitBtnText.setShowAccessControls(z).show(getChildFragmentManager(), null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onAlbumEditSubmit(PhotoAlbumEditDialog dialog, CharSequence title, List<AccessType> accessTypes) {
        ProgressDialogFragment.createInstance(getStringLocalized(2131166864), false).show(getChildFragmentManager(), "prgrdlg");
        Bundle inData = new Bundle();
        inData.putString("ttl", title.toString());
        inData.putIntArray("accss", AccessType.asIntArray(accessTypes));
        if (this.photoOwner.getType() == 1) {
            inData.putString("gid", this.photoOwner.getId());
        }
        GlobalBus.send(2131623961, new BusEvent(inData));
    }

    protected final void hideProgressDialog() {
        DialogFragment fragment = (DialogFragment) getChildFragmentManager().findFragmentByTag("prgrdlg");
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    private void startPhotoChooserActivity(Activity activity) {
        PhotoAlbumInfo album = null;
        if (this.photoOwner.getType() == 1) {
            album = new PhotoAlbumInfo();
            album.setGroupId(this.photoOwner.getId());
        }
        NavigationHelper.startPhotoUploadSequence(activity, album, 0, 0);
    }

    public List<PhotoAlbumInfo> getAlbumsInfoList() {
        return this.albumsInfoList;
    }

    public void notifyDataSetChanged() {
        this.albumsInfoAdapter.notifyDataSetChanged();
    }
}
