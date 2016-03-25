package ru.ok.android.ui.image.crop;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.provider.MediaStore.Images.Media;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import ru.ok.android.ui.image.crop.gallery.BaseImageList;
import ru.ok.android.ui.image.crop.gallery.IImage;
import ru.ok.android.ui.image.crop.gallery.IImageList;
import ru.ok.android.ui.image.crop.gallery.ImageList;
import ru.ok.android.ui.image.crop.gallery.ImageListUber;
import ru.ok.android.ui.image.crop.gallery.SingleImageList;

public class ImageManager {
    public static final String CAMERA_IMAGE_BUCKET_ID;
    public static final String CAMERA_IMAGE_BUCKET_NAME;
    private static final Uri STORAGE_URI;

    public enum DataLocation {
        NONE,
        INTERNAL,
        EXTERNAL,
        ALL
    }

    private static class EmptyImageList implements IImageList {
        private EmptyImageList() {
        }

        public void close() {
        }

        public int getCount() {
            return 0;
        }

        public IImage getImageAt(int i) {
            return null;
        }

        public IImage getImageForUri(Uri uri) {
            return null;
        }
    }

    public static class ImageListParam implements Parcelable {
        public static final Creator<ImageListParam> CREATOR;
        public String mBucketId;
        public int mInclusion;
        public boolean mIsEmptyImageList;
        public DataLocation mLocation;
        public Uri mSingleImageUri;
        public int mSort;

        /* renamed from: ru.ok.android.ui.image.crop.ImageManager.ImageListParam.1 */
        static class C09831 implements Creator<ImageListParam> {
            C09831() {
            }

            public ImageListParam createFromParcel(Parcel in) {
                return new ImageListParam(null);
            }

            public ImageListParam[] newArray(int size) {
                return new ImageListParam[size];
            }
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.mLocation.ordinal());
            out.writeInt(this.mInclusion);
            out.writeInt(this.mSort);
            out.writeString(this.mBucketId);
            out.writeParcelable(this.mSingleImageUri, flags);
            out.writeInt(this.mIsEmptyImageList ? 1 : 0);
        }

        private ImageListParam(Parcel in) {
            this.mLocation = DataLocation.values()[in.readInt()];
            this.mInclusion = in.readInt();
            this.mSort = in.readInt();
            this.mBucketId = in.readString();
            this.mSingleImageUri = (Uri) in.readParcelable(null);
            this.mIsEmptyImageList = in.readInt() != 0;
        }

        public String toString() {
            return String.format("ImageListParam{loc=%s,inc=%d,sort=%d,bucket=%s,empty=%b,single=%s}", new Object[]{this.mLocation, Integer.valueOf(this.mInclusion), Integer.valueOf(this.mSort), this.mBucketId, Boolean.valueOf(this.mIsEmptyImageList), this.mSingleImageUri});
        }

        static {
            CREATOR = new C09831();
        }

        public int describeContents() {
            return 0;
        }
    }

    static {
        STORAGE_URI = Media.EXTERNAL_CONTENT_URI;
        CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
        CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME);
    }

    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    public static IImageList makeImageList(ContentResolver cr, ImageListParam param) {
        DataLocation location = param.mLocation;
        int inclusion = param.mInclusion;
        int sort = param.mSort;
        String bucketId = param.mBucketId;
        Uri singleImageUri = param.mSingleImageUri;
        if (param.mIsEmptyImageList || cr == null) {
            return new EmptyImageList();
        }
        if (singleImageUri != null) {
            return new SingleImageList(cr, singleImageUri);
        }
        boolean haveSdCard = hasStorage(false);
        ArrayList<BaseImageList> l = new ArrayList();
        if (!(!haveSdCard || location == DataLocation.INTERNAL || (inclusion & 1) == 0)) {
            l.add(new ImageList(cr, STORAGE_URI, sort, bucketId));
        }
        if ((location == DataLocation.INTERNAL || location == DataLocation.ALL) && (inclusion & 1) != 0) {
            l.add(new ImageList(cr, Media.INTERNAL_CONTENT_URI, sort, bucketId));
        }
        Iterator<BaseImageList> iter = l.iterator();
        while (iter.hasNext()) {
            BaseImageList sublist = (BaseImageList) iter.next();
            if (sublist.isEmpty()) {
                sublist.close();
                iter.remove();
            }
        }
        if (l.size() == 1) {
            return (BaseImageList) l.get(0);
        }
        return new ImageListUber((IImageList[]) l.toArray(new IImageList[l.size()]), sort);
    }

    public static IImageList makeImageList(ContentResolver cr, Uri uri, int sort) {
        String uriString = uri != null ? uri.toString() : "";
        if (uriString.startsWith("content://drm")) {
            return makeImageList(cr, DataLocation.ALL, 2, sort, null);
        }
        if (uriString.startsWith("content://media/external/video")) {
            return makeImageList(cr, DataLocation.EXTERNAL, 4, sort, null);
        }
        if (isSingleImageMode(uriString)) {
            return makeSingleImageList(cr, uri);
        }
        return makeImageList(cr, DataLocation.ALL, 1, sort, uri.getQueryParameter("bucketId"));
    }

    static boolean isSingleImageMode(String uriString) {
        return (uriString.startsWith(Media.EXTERNAL_CONTENT_URI.toString()) || uriString.startsWith(Media.INTERNAL_CONTENT_URI.toString())) ? false : true;
    }

    public static ImageListParam getImageListParam(DataLocation location, int inclusion, int sort, String bucketId) {
        ImageListParam param = new ImageListParam();
        param.mLocation = location;
        param.mInclusion = inclusion;
        param.mSort = sort;
        param.mBucketId = bucketId;
        return param;
    }

    public static ImageListParam getSingleImageListParam(Uri uri) {
        ImageListParam param = new ImageListParam();
        param.mSingleImageUri = uri;
        return param;
    }

    public static IImageList makeImageList(ContentResolver cr, DataLocation location, int inclusion, int sort, String bucketId) {
        return makeImageList(cr, getImageListParam(location, inclusion, sort, bucketId));
    }

    public static IImageList makeSingleImageList(ContentResolver cr, Uri uri) {
        return makeImageList(cr, getSingleImageListParam(uri));
    }

    private static boolean checkFsWritable() {
        String directoryName = Environment.getExternalStorageDirectory().toString() + "/DCIM";
        File directory = new File(directoryName);
        if (!directory.isDirectory() && !directory.mkdirs()) {
            return false;
        }
        File f = new File(directoryName, ".probe");
        try {
            if (f.exists()) {
                f.delete();
            }
            if (!f.createNewFile()) {
                return false;
            }
            f.delete();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean hasStorage(boolean requireWriteAccess) {
        String state = Environment.getExternalStorageState();
        if ("mounted".equals(state)) {
            if (requireWriteAccess) {
                return checkFsWritable();
            }
            return true;
        } else if (requireWriteAccess || !"mounted_ro".equals(state)) {
            return false;
        } else {
            return true;
        }
    }
}
