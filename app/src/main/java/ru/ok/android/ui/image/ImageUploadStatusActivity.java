package ru.ok.android.ui.image;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView.OnHeaderClickListener;
import java.util.ArrayList;
import java.util.Iterator;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.image.ImageForUpload;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.app.IntentUtils;
import ru.ok.android.ui.activity.ShowFragmentActivity;
import ru.ok.android.ui.custom.ButtonBar;
import ru.ok.android.ui.custom.ImageUploadStatusView;
import ru.ok.android.ui.image.UploadsAdapter.HeaderData;
import ru.ok.android.ui.image.view.PhotoLayerAnimationHelper;
import ru.ok.android.utils.Constants.Image;
import ru.ok.android.utils.MimeTypes;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.animation.SyncBus.MessageCallback;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;

public class ImageUploadStatusActivity extends ShowFragmentActivity implements MessageCallback {
    protected View controlsView;
    protected Button mCancelBtn;
    protected Context mContext;
    protected int mGridSpacing;
    protected int mMeasuredTileSide;
    protected int mMinTileSide;
    protected Button mPauseBtn;
    protected ButtonBar mPauseButtonbar;
    protected Button mResumeBtn;
    protected ImageUploadStatusView mSingleImageView;
    protected Button mToAlbumBtn;
    protected ArrayList<ImageForUpload> mUploads;
    protected UploadsAdapter mUploadsAdapter;
    protected StickyGridHeadersGridView mUploadsGridView;
    private int prevWidth;
    protected boolean shouldFinilizeOnPause;

    /* renamed from: ru.ok.android.ui.image.ImageUploadStatusActivity.1 */
    class C09491 implements OnClickListener {
        C09491() {
        }

        public void onClick(View view) {
            ImageUploadStatusActivity.this.onItemClicked((ImageForUpload) ImageUploadStatusActivity.this.mUploads.get(0));
        }
    }

    /* renamed from: ru.ok.android.ui.image.ImageUploadStatusActivity.2 */
    class C09502 implements OnGlobalLayoutListener {
        C09502() {
        }

        public void onGlobalLayout() {
            ImageUploadStatusActivity.this.recalculateGridSpacing();
        }
    }

    /* renamed from: ru.ok.android.ui.image.ImageUploadStatusActivity.3 */
    class C09513 implements OnItemClickListener {
        C09513() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            ImageUploadStatusActivity.this.onItemClicked((ImageForUpload) ImageUploadStatusActivity.this.mUploads.get(position));
        }
    }

    /* renamed from: ru.ok.android.ui.image.ImageUploadStatusActivity.4 */
    class C09524 implements OnHeaderClickListener {
        C09524() {
        }

        public void onHeaderClick(AdapterView<?> adapterView, View view, long id) {
            HeaderData headerData = ImageUploadStatusActivity.this.mUploadsAdapter.getHeaderData((int) id);
            if (headerData.type == 0) {
                NavigationHelper.showUserPhotoAlbum(ImageUploadStatusActivity.this.mContext, OdnoklassnikiApplication.getCurrentUser().uid, headerData.albumInfo.getId());
            } else if (headerData.type == 1) {
                NavigationHelper.showGroupPhotoAlbum(ImageUploadStatusActivity.this.mContext, headerData.albumInfo.getGroupId(), headerData.albumInfo.getId());
            } else if (headerData.type == 2) {
                NavigationHelper.showGroupInfo(ImageUploadStatusActivity.this, headerData.albumInfo.getGroupId());
            }
        }
    }

    /* renamed from: ru.ok.android.ui.image.ImageUploadStatusActivity.5 */
    class C09535 implements OnClickListener {
        C09535() {
        }

        public void onClick(View view) {
            ImageUploadStatusActivity.this.sendUploaderEvent(2, null);
        }
    }

    /* renamed from: ru.ok.android.ui.image.ImageUploadStatusActivity.6 */
    class C09546 implements OnClickListener {
        C09546() {
        }

        public void onClick(View view) {
            ImageUploadStatusActivity.this.sendUploaderEvent(3, null);
        }
    }

    /* renamed from: ru.ok.android.ui.image.ImageUploadStatusActivity.7 */
    class C09557 implements OnClickListener {
        C09557() {
        }

        public void onClick(View view) {
            ImageUploadStatusActivity.this.showDialog(1);
        }
    }

    /* renamed from: ru.ok.android.ui.image.ImageUploadStatusActivity.8 */
    class C09568 implements OnClickListener {
        C09568() {
        }

        public void onClick(View view) {
            PhotoAlbumInfo album = null;
            if (!ImageUploadStatusActivity.this.mUploads.isEmpty()) {
                for (int i = ImageUploadStatusActivity.this.mUploads.size() - 1; i >= 0; i--) {
                    ImageForUpload image = (ImageForUpload) ImageUploadStatusActivity.this.mUploads.get(i);
                    album = image.getAlbumInfo();
                    if (image.getCurrentStatus() == 5) {
                        break;
                    }
                }
            }
            ImageUploadStatusActivity.this.shouldFinilizeOnPause = true;
            if (album != null) {
                if (album.getOwnerType() == OwnerType.USER) {
                    NavigationHelper.showUserPhotoAlbum(ImageUploadStatusActivity.this, album.getUserId(), album.getId());
                } else {
                    NavigationHelper.showGroupPhotoAlbum(ImageUploadStatusActivity.this, album.getGroupId(), album.getId());
                }
                ImageUploadStatusActivity.this.finish();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.image.ImageUploadStatusActivity.9 */
    class C09579 implements DialogInterface.OnClickListener {
        C09579() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    public ImageUploadStatusActivity() {
        this.mContext = this;
        this.mUploads = new ArrayList();
        this.shouldFinilizeOnPause = false;
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setContentView(2130903241);
        PhotoLayerAnimationHelper.registerCallback(1, this);
        PhotoLayerAnimationHelper.registerCallback(2, this);
        PhotoLayerAnimationHelper.registerCallback(3, this);
        if (savedInstanceState != null) {
            this.mUploads = savedInstanceState.getParcelableArrayList("imgs");
        } else {
            this.mUploads = new ArrayList();
        }
        this.mSingleImageView = (ImageUploadStatusView) findViewById(2131624945);
        this.mSingleImageView.setOnClickListener(new C09491());
        this.mUploadsGridView = (StickyGridHeadersGridView) findViewById(2131624946);
        this.mUploadsGridView.getViewTreeObserver().addOnGlobalLayoutListener(new C09502());
        this.mUploadsGridView.setOnItemClickListener(new C09513());
        this.mUploadsGridView.setOnHeaderClickListener(new C09524());
        this.mUploadsAdapter = new UploadsAdapter(this, this, this.mUploads);
        this.mUploadsGridView.setAdapter(this.mUploadsAdapter);
        this.mMinTileSide = getResources().getDimensionPixelSize(2131230787);
        this.mGridSpacing = getResources().getDimensionPixelSize(2131230786);
        this.controlsView = findViewById(2131624935);
        this.mPauseButtonbar = (ButtonBar) findViewById(2131624942);
        this.mPauseBtn = (Button) findViewById(2131624941);
        this.mPauseBtn.setOnClickListener(new C09535());
        this.mResumeBtn = (Button) findViewById(2131624943);
        this.mResumeBtn.setOnClickListener(new C09546());
        this.mCancelBtn = (Button) findViewById(C0263R.id.cancel);
        this.mCancelBtn.setOnClickListener(new C09557());
        this.mToAlbumBtn = (Button) findViewById(2131624944);
        this.mToAlbumBtn.setOnClickListener(new C09568());
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(LocalizationManager.getString(getContext(), 2131165394));
        setProgressBarIndeterminateVisibility(false);
    }

    protected final void onItemClicked(ImageForUpload ifu) {
        if (ifu.getError() != null) {
            Builder builder = new Builder(this);
            builder.setTitle(getStringLocalized(2131165791));
            builder.setMessage(getStringLocalized(Image.getStringResForUpldError(ifu.getError())));
            builder.setPositiveButton(getStringLocalized(2131166310), null);
            builder.show();
        } else if (ifu.getCurrentStatus() == 5) {
            PhotoOwner photoOwner = new PhotoOwner();
            if (ifu.getAlbumInfo().getOwnerType() == OwnerType.USER) {
                photoOwner.setType(0);
                photoOwner.setId(OdnoklassnikiApplication.getCurrentUser().uid);
                photoOwner.setOwnerInfo(OdnoklassnikiApplication.getCurrentUser());
            } else if (ifu.getAlbumInfo().getOwnerType() == OwnerType.GROUP) {
                photoOwner.setType(1);
                photoOwner.setId(ifu.getAlbumInfo().getGroupId());
                photoOwner.setOwnerInfo(ifu.getGroupInfo());
            }
            NavigationHelper.showPhoto(this.mContext, photoOwner, ifu.getAlbumInfo().getId(), ifu.getRemoteId(), 6);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        LocalizationManager.inflate((Context) this, getMenuInflater(), 2131689493, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625215:
                startPhotoChooserActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected Dialog onCreateDialog(int id) {
        if (id != 1) {
            return super.onCreateDialog(id);
        }
        Builder builder = new Builder(this);
        builder.setTitle(getStringLocalized(2131165414));
        builder.setMessage(getStringLocalized(2131165478));
        builder.setNegativeButton(getStringLocalized(2131166257), new C09579());
        builder.setPositiveButton(getStringLocalized(2131166881), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ImageUploadStatusActivity.this.sendUploaderEvent(4, null);
            }
        });
        return builder.create();
    }

    protected final void sendUploaderEvent(int action, Bundle inputBundle) {
        if (inputBundle == null) {
            inputBundle = new Bundle();
        }
        inputBundle.putInt("impldract", action);
        GlobalBus.send(2131624084, new BusEvent(inputBundle));
    }

    protected final void recalculateGridSpacing() {
        int totalWidth = this.mUploadsGridView.getMeasuredWidth();
        if (totalWidth != this.prevWidth) {
            int spacing;
            this.mMeasuredTileSide = this.mMinTileSide;
            this.prevWidth = totalWidth;
            int inRowCount = totalWidth / this.mMinTileSide;
            int remainder = totalWidth - (this.mMinTileSide * inRowCount);
            int prefferedSpacingTotal = (this.mGridSpacing * inRowCount) - this.mGridSpacing;
            if (prefferedSpacingTotal < remainder) {
                this.mMeasuredTileSide += (remainder - prefferedSpacingTotal) / inRowCount;
                spacing = this.mGridSpacing;
            } else {
                spacing = remainder / (inRowCount - 1);
            }
            if (spacing == 0) {
                float minSpacing = 2.0f * getResources().getDisplayMetrics().density;
                this.mMeasuredTileSide = (int) (((float) this.mMeasuredTileSide) - minSpacing);
                spacing = (int) minSpacing;
            }
            this.mUploadsGridView.setColumnWidth(this.mMeasuredTileSide);
            this.mUploadsGridView.setHorizontalSpacing(spacing);
            this.mUploadsGridView.setVerticalSpacing(spacing);
            this.mUploadsAdapter.notifyDataSetChanged();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("imgs", this.mUploads);
        super.onSaveInstanceState(outState);
    }

    protected void startPhotoChooserActivity() {
        this.shouldFinilizeOnPause = false;
        startActivityForResult(IntentUtils.createIntentToAddImages(this.mContext, null, 0, 0, true, true, "imgupldr"), 0);
    }

    public void onBackPressed() {
        this.shouldFinilizeOnPause = true;
        super.onBackPressed();
    }

    protected void toastOnInternetProblems() {
        sendUploaderEvent(5, null);
        sendUploaderEvent(7, null);
    }

    @Subscribe(on = 2131623946, to = 2131624225)
    public void oinImageUploaderEvent(BusEvent event) {
        int status = event.bundleOutput.getInt("upldrsts");
        updateUploaderControls(status);
        switch (event.resultCode) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                onUploadStatusChange(event.bundleOutput);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                onUploadsListRecieved(event.bundleOutput);
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                toastIfWaitingInternet(status);
            default:
        }
    }

    private void onUploadStatusChange(Bundle bundle) {
        ImageForUpload upload = (ImageForUpload) bundle.getParcelable("img");
        ImageForUpload stored = null;
        Iterator i$ = this.mUploads.iterator();
        while (i$.hasNext()) {
            ImageForUpload imageForUpload = (ImageForUpload) i$.next();
            if (imageForUpload.getId().equals(upload.getId())) {
                stored = imageForUpload;
                stored.setCurrentStatus(upload.getCurrentStatus());
                stored.setPreviousStatus(upload.getPreviousStatus());
                stored.setError(upload.getError());
                stored.setPhotoId(upload.getRemoteId());
                break;
            }
        }
        if (stored != null) {
            boolean shouldUpdateViews = false;
            switch (stored.getCurrentStatus()) {
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                case Message.TYPE_FIELD_NUMBER /*3*/:
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    switch (stored.getPreviousStatus()) {
                        case Message.TEXT_FIELD_NUMBER /*1*/:
                        case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        case Message.TYPE_FIELD_NUMBER /*3*/:
                        case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                            break;
                        default:
                            shouldUpdateViews = true;
                            break;
                    }
                default:
                    shouldUpdateViews = true;
                    break;
            }
            if (!shouldUpdateViews) {
                return;
            }
            if (this.mUploads.size() == 1) {
                this.mSingleImageView.setStatus(upload.getCurrentStatus(), upload.getError());
            } else {
                this.mUploadsAdapter.notifyDataSetChanged();
            }
        }
    }

    private void onUploadsListRecieved(Bundle bundle) {
        ArrayList<Parcelable> parcelables = bundle.getParcelableArrayList("imgs");
        this.mUploads.clear();
        Iterator i$ = parcelables.iterator();
        while (i$.hasNext()) {
            ImageForUpload image = (ImageForUpload) ((Parcelable) i$.next());
            if (image.getUploadTarget() != 3) {
                this.mUploads.add(image);
            }
        }
        if (this.mUploads.size() == 0) {
            this.mSingleImageView.setVisibility(8);
            this.mUploadsGridView.setVisibility(8);
        } else if (this.mUploads.size() == 1) {
            ImageForUpload imageForUpload = (ImageForUpload) this.mUploads.get(0);
            this.mSingleImageView.setVisibility(0);
            this.mUploadsGridView.setVisibility(8);
            this.mSingleImageView.setStatus(imageForUpload.getCurrentStatus(), imageForUpload.getError());
            this.mSingleImageView.setImage(imageForUpload.getUri(), imageForUpload.getRotation());
            this.mSingleImageView.setShouldDrawGifMarker(MimeTypes.isGif(imageForUpload.getMimeType()));
        } else {
            this.mSingleImageView.setVisibility(8);
            this.mUploadsGridView.setVisibility(0);
            this.mUploadsAdapter.notifyDataSetChanged();
        }
    }

    protected final void toastIfWaitingInternet(int uploaderStatus) {
        if (4 == uploaderStatus) {
            int toastMessage = 2131166755;
            if (PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean("wifiupld", false)) {
                toastMessage = 2131166756;
            }
            Toast.makeText(this.mContext, getStringLocalized(toastMessage), 1).show();
        }
    }

    protected void onPause() {
        if (this.shouldFinilizeOnPause) {
            super.onPause();
        } else {
            super.onPause();
        }
    }

    protected void onResume() {
        super.onResume();
        sendUploaderEvent(5, null);
        toastOnInternetProblems();
    }

    protected final void updateUploaderControls(int uploaderStatus) {
        this.controlsView.setVisibility(8);
        this.mPauseBtn.setVisibility(8);
        this.mPauseButtonbar.setVisibility(8);
        this.mToAlbumBtn.setVisibility(8);
        switch (uploaderStatus) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.controlsView.setVisibility(0);
                this.mPauseBtn.setVisibility(0);
                this.mPauseBtn.setEnabled(true);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                this.controlsView.setVisibility(0);
                this.mPauseBtn.setVisibility(0);
                this.mPauseBtn.setEnabled(false);
            case Message.TYPE_FIELD_NUMBER /*3*/:
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
            case Message.UUID_FIELD_NUMBER /*5*/:
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                this.controlsView.setVisibility(0);
                this.mPauseButtonbar.setVisibility(0);
                this.mResumeBtn.setEnabled(true);
                this.mCancelBtn.setEnabled(true);
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                this.controlsView.setVisibility(0);
                this.mPauseButtonbar.setVisibility(0);
                this.mResumeBtn.setEnabled(false);
                this.mCancelBtn.setEnabled(false);
            default:
        }
    }

    public Bundle onMessage(android.os.Message message) {
        return null;
    }

    protected void onDestroy() {
        super.onDestroy();
        PhotoLayerAnimationHelper.unregisterCallback(1, this);
        PhotoLayerAnimationHelper.unregisterCallback(2, this);
        PhotoLayerAnimationHelper.unregisterCallback(3, this);
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
