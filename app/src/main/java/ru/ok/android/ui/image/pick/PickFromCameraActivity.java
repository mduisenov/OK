package ru.ok.android.ui.image.pick;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.Loader.OnLoadCompleteListener;
import android.util.Pair;
import android.widget.TextView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.image.MediaUnmountAwareActivity;
import ru.ok.android.utils.BitmapRender;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.Storage.External.Application;
import ru.ok.android.utils.UserMedia;
import ru.ok.android.utils.UserMedia.OnImageAddedListener;

public class PickFromCameraActivity extends MediaUnmountAwareActivity {
    private static final String[] ID_DATA_PROJECTION;
    private static final String[] ID_PROJECTION;
    private Set<Integer> mCurrentCameraImageSet;
    private boolean mGenerated;
    private TextView mMessageView;
    private File mTempImageFile;
    private Uri mTempImageFileUri;

    /* renamed from: ru.ok.android.ui.image.pick.PickFromCameraActivity.1 */
    class C09911 implements OnLoadCompleteListener<Cursor> {
        C09911() {
        }

        public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
            if (cursor != null) {
                try {
                    PickFromCameraActivity.this.populateExistingCameraImages(cursor);
                } finally {
                    IOUtils.closeSilently(cursor);
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.image.pick.PickFromCameraActivity.2 */
    class C09922 implements OnLoadCompleteListener<Cursor> {
        C09922() {
        }

        public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
            loader.unregisterListener(this);
            GalleryImageInfo cameraImage = PickFromCameraActivity.this.getCameraImageFromMediaStore(cursor);
            if (cameraImage == null) {
                cameraImage = PickFromCameraActivity.this.getCameraImageFromTempFile();
            }
            if (cameraImage != null) {
                PickFromCameraActivity.this.returnCameraImage(cameraImage);
            } else {
                PickFromCameraActivity.this.showErrorDialog(PickFromCameraActivity.this.getStringLocalized(2131166092));
            }
        }
    }

    /* renamed from: ru.ok.android.ui.image.pick.PickFromCameraActivity.3 */
    class C09933 implements OnImageAddedListener {
        final /* synthetic */ GalleryImageInfo val$cameraImage;

        C09933(GalleryImageInfo galleryImageInfo) {
            this.val$cameraImage = galleryImageInfo;
        }

        public void onImageAdded(String path, Uri uri) {
            PickFromCameraActivity.this.doReturnCameraImage(this.val$cameraImage);
        }
    }

    static {
        ID_PROJECTION = new String[]{"_id"};
        ID_DATA_PROJECTION = new String[]{"_id", "_data", "width", "height", "orientation", "mime_type", "date_added"};
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setResult(0);
        setContentView(2130903240);
        goFurtherIfExternalMediaMounted(savedInstanceState);
    }

    private void goFurtherIfExternalMediaMounted(Bundle savedInstanceState) {
        if (Environment.getExternalStorageState().equals("mounted")) {
            goFurther(savedInstanceState);
        }
    }

    @TargetApi(23)
    private void goFurther(Bundle savedInstanceState) {
        this.mMessageView = (TextView) findViewById(2131624940);
        if (savedInstanceState != null) {
            restoreSavedState(savedInstanceState);
        }
        if (this.mTempImageFileUri == null) {
            this.mMessageView.setText(getStringLocalized(2131165474));
            this.mTempImageFileUri = (Uri) getIntent().getParcelableExtra("output");
            if (this.mTempImageFileUri != null) {
                this.mTempImageFile = new File(this.mTempImageFileUri.getPath());
            } else if (createTempImageFile()) {
                this.mTempImageFileUri = Uri.fromFile(this.mTempImageFile);
                this.mGenerated = true;
            } else {
                return;
            }
            loadExistingCameraImages();
            if (PermissionUtils.checkSelfPermission(this, "android.permission.CAMERA") == 0) {
                startCameraForResult();
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, 2);
        }
    }

    private void startCameraForResult() {
        startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE").putExtra("output", this.mTempImageFileUri), 1);
    }

    private boolean createTempImageFile() {
        try {
            String outDirPath = getIntent().getStringExtra("out_dir");
            File cacheDir = Application.getCacheDir(this);
            if (cacheDir == null) {
                throw new FileNotFoundException("Cache dir is null");
            }
            this.mTempImageFile = FileUtils.generateEmptyFile(outDirPath == null ? cacheDir : new File(cacheDir, outDirPath), ".jpg");
            return true;
        } catch (Throwable exc) {
            Logger.m179e(exc, "Not possible to create a temp image file");
            showErrorDialog(getStringLocalized(2131166092));
            doCleanUp();
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (PermissionUtils.getGrantResult(grantResults) == 0) {
                    startCameraForResult();
                } else {
                    finish();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        if (this.mTempImageFileUri != null) {
            outState.putParcelable("file_uri", this.mTempImageFileUri);
        }
        outState.putBoolean("gen", this.mGenerated);
        if (this.mCurrentCameraImageSet != null) {
            outState.putIntegerArrayList("cis", new ArrayList(this.mCurrentCameraImageSet));
        }
        super.onSaveInstanceState(outState);
    }

    protected void restoreSavedState(Bundle savedInstanceState) {
        this.mGenerated = savedInstanceState.getBoolean("gen");
        this.mTempImageFileUri = (Uri) savedInstanceState.getParcelable("file_uri");
        if (this.mTempImageFileUri != null) {
            this.mTempImageFile = new File(this.mTempImageFileUri.getPath());
        }
        ArrayList<Integer> currentCameraImageList = savedInstanceState.getIntegerArrayList("cis");
        if (currentCameraImageList != null) {
            this.mCurrentCameraImageSet = new HashSet(currentCameraImageList);
        }
    }

    private void loadExistingCameraImages() {
        this.mCurrentCameraImageSet = new HashSet();
        CursorLoader cursorLoader = new CursorLoader(this, Media.EXTERNAL_CONTENT_URI, ID_PROJECTION, null, null, null);
        cursorLoader.registerListener(1, new C09911());
        cursorLoader.startLoading();
    }

    private void populateExistingCameraImages(@NonNull Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                this.mCurrentCameraImageSet.add(Integer.valueOf(cursor.getInt(0)));
            } while (cursor.moveToNext());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (1 == requestCode) {
            onCaptureImageResult(resultCode);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onCaptureImageResult(int resultCode) {
        if (-1 == resultCode) {
            this.mMessageView.setText(getStringLocalized(2131165475));
            findCameraImageFileAndFinish();
            return;
        }
        finish();
    }

    private void findCameraImageFileAndFinish() {
        CursorLoader cursorLoader = new CursorLoader(this, Media.EXTERNAL_CONTENT_URI, ID_DATA_PROJECTION, null, null, null);
        cursorLoader.registerListener(2, new C09922());
        cursorLoader.startLoading();
    }

    @Nullable
    private GalleryImageInfo getCameraImageFromMediaStore(Cursor cursor) {
        GalleryImageInfo cameraImage = null;
        try {
            if (cursor.moveToFirst()) {
                do {
                    int imageId = cursor.getInt(0);
                    if (!this.mCurrentCameraImageSet.contains(Integer.valueOf(imageId))) {
                        String path = cursor.getString(1);
                        int width = cursor.getInt(2);
                        int height = cursor.getInt(3);
                        int rotation = cursor.getInt(4);
                        String mimeType = cursor.getString(5);
                        long dateAdded = cursor.getLong(6);
                        File cameraImageFile = new File(path);
                        if (width == 0 || height == 0) {
                            Options options = BitmapRender.getBitmapInfo(getContentResolver(), Uri.fromFile(cameraImageFile)).options;
                            width = options.outWidth;
                            height = options.outHeight;
                        }
                        cleanUpIfCameraImageFileIsDifferentThanTempFile(imageId, cameraImageFile);
                        cameraImage = new GalleryImageInfo(this.mTempImageFileUri, mimeType, rotation, dateAdded, width, height, false);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            return cameraImage;
        } catch (Throwable th) {
            cursor.close();
        }
    }

    private void cleanUpIfCameraImageFileIsDifferentThanTempFile(int cameraImageId, @NonNull File cameraImageFile) {
        if (!cameraImageFile.equals(this.mTempImageFile)) {
            try {
                FileUtils.moveFile(cameraImageFile, this.mTempImageFile);
                deleteCameraImageFromMediaStore(cameraImageId);
            } catch (IOException exc) {
                Logger.m180e(exc, "Exception occurred when moving file from (%s) to (%s)", cameraImageFile, this.mTempImageFile);
                showErrorDialog(getStringLocalized(2131166092));
                doCleanUp();
            }
        }
    }

    private void deleteCameraImageFromMediaStore(int imageId) {
        int result = getContentResolver().delete(Media.EXTERNAL_CONTENT_URI, "_id=" + imageId, null);
    }

    @Nullable
    private GalleryImageInfo getCameraImageFromTempFile() {
        if (this.mTempImageFile.length() <= 0) {
            return null;
        }
        Pair<Integer, Integer> dimensions = decodeImageDimensionsFromTempImageFile();
        return new GalleryImageInfo(this.mTempImageFileUri, "image/jpeg", UserMedia.getImageRotation(this, Uri.fromFile(this.mTempImageFile)), this.mTempImageFile.lastModified(), ((Integer) dimensions.first).intValue(), ((Integer) dimensions.second).intValue(), false);
    }

    @NonNull
    private Pair<Integer, Integer> decodeImageDimensionsFromTempImageFile() {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(this.mTempImageFile.getAbsolutePath(), options);
        return new Pair(Integer.valueOf(options.outWidth), Integer.valueOf(options.outHeight));
    }

    private void returnCameraImage(GalleryImageInfo cameraImage) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(2131166630), true)) {
            setUnmountAware(false);
            UserMedia.copyImageToUserGallery(this.mTempImageFile, this, new C09933(cameraImage));
            return;
        }
        doReturnCameraImage(cameraImage);
    }

    private void doReturnCameraImage(GalleryImageInfo cameraImage) {
        setResult(-1, new Intent().putExtra("camera_image", cameraImage));
        finish();
    }

    private void doCleanUp() {
        if (this.mGenerated && this.mTempImageFile.exists() && !this.mTempImageFile.delete()) {
            Logger.m177e("Temp image file (%s) cannot be deleted", this.mTempImageFile);
        }
    }
}
