package ru.ok.android.services.processors.video;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import ru.ok.android.utils.BitmapRender;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.InputStreamHolder;
import ru.ok.android.utils.Logger;

public class MediaInfoTempFile extends MediaInfo {
    public static final Creator<MediaInfoTempFile> CREATOR;
    private static final long serialVersionUID = 1;
    private final FileLocation tempMediaFile;
    private final FileLocation tempThumbFile;

    /* renamed from: ru.ok.android.services.processors.video.MediaInfoTempFile.1 */
    static class C05061 implements Creator<MediaInfoTempFile> {
        C05061() {
        }

        public MediaInfoTempFile createFromParcel(Parcel source) {
            return new MediaInfoTempFile(source);
        }

        public MediaInfoTempFile[] newArray(int size) {
            return new MediaInfoTempFile[size];
        }
    }

    public MediaInfoTempFile(FileLocation tempMediaFile, FileLocation tempThumbFile, String displayName, long sizeBytes) {
        super(tempMediaFile.getUriSafe(), displayName, sizeBytes, getMimeTypeFromFileLocation(tempMediaFile));
        this.tempMediaFile = tempMediaFile;
        this.tempThumbFile = tempThumbFile;
    }

    protected MediaInfoTempFile(Parcel src) {
        super(src);
        ClassLoader cl = getClass().getClassLoader();
        this.tempMediaFile = (FileLocation) src.readParcelable(cl);
        this.tempThumbFile = (FileLocation) src.readParcelable(cl);
    }

    private static String getMimeTypeFromFileLocation(@NonNull FileLocation fileLocation) {
        try {
            return FileUtils.getMimeType(fileLocation.getFile().getName());
        } catch (IOException e) {
            Logger.m177e("Mime type can't be resolved from file at %s", fileLocation);
            return null;
        }
    }

    public InputStream open(ContentResolver cr) throws FileNotFoundException {
        return cr.openInputStream(getUri());
    }

    public Bitmap getThumbnail(ContentResolver cr, int thumbWidth, int thumbHeight) {
        if (this.tempThumbFile == null) {
            return getThumbnailForMedia(this.tempMediaFile);
        }
        try {
            return BitmapRender.getBySampleSize(cr, this.tempThumbFile.getUriSafe(), thumbWidth, thumbHeight);
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to decode bitmap");
            return null;
        } catch (Throwable e2) {
            Logger.m179e(e2, "Not enough memory for bitmap");
            return null;
        }
    }

    private Bitmap getThumbnailForMedia(FileLocation fileLocation) {
        Throwable e;
        Throwable th;
        Bitmap bitmap = null;
        if (fileLocation != null) {
            MediaMetadataRetriever metadataRetriever = null;
            try {
                String filePath = this.tempMediaFile.getFile().getAbsolutePath();
                if (VERSION.SDK_INT >= 10) {
                    MediaMetadataRetriever metadataRetriever2 = new MediaMetadataRetriever();
                    try {
                        metadataRetriever2.setDataSource(filePath);
                        bitmap = metadataRetriever2.getFrameAtTime();
                        if (metadataRetriever2 != null) {
                            try {
                                metadataRetriever2.release();
                            } catch (Exception e2) {
                            }
                        }
                    } catch (IOException e3) {
                        e = e3;
                        metadataRetriever = metadataRetriever2;
                        try {
                            Logger.m177e("Failed to decode thumbnail for %s", fileLocation);
                            Logger.m178e(e);
                            if (metadataRetriever != null) {
                                try {
                                    metadataRetriever.release();
                                } catch (Exception e4) {
                                }
                            }
                            return bitmap;
                        } catch (Throwable th2) {
                            th = th2;
                            if (metadataRetriever != null) {
                                try {
                                    metadataRetriever.release();
                                } catch (Exception e5) {
                                }
                            }
                            throw th;
                        }
                    } catch (OutOfMemoryError e6) {
                        e = e6;
                        metadataRetriever = metadataRetriever2;
                        Logger.m179e(e, "Not enough memory for bitmap");
                        if (metadataRetriever != null) {
                            try {
                                metadataRetriever.release();
                            } catch (Exception e7) {
                            }
                        }
                        return bitmap;
                    } catch (Throwable th3) {
                        th = th3;
                        metadataRetriever = metadataRetriever2;
                        if (metadataRetriever != null) {
                            metadataRetriever.release();
                        }
                        throw th;
                    }
                } else if (VERSION.SDK_INT >= 8) {
                    bitmap = ThumbnailUtils.createVideoThumbnail(filePath, 1);
                    if (metadataRetriever != null) {
                        try {
                            metadataRetriever.release();
                        } catch (Exception e8) {
                        }
                    }
                } else if (metadataRetriever != null) {
                    try {
                        metadataRetriever.release();
                    } catch (Exception e9) {
                    }
                }
            } catch (IOException e10) {
                e = e10;
                Logger.m177e("Failed to decode thumbnail for %s", fileLocation);
                Logger.m178e(e);
                if (metadataRetriever != null) {
                    metadataRetriever.release();
                }
                return bitmap;
            } catch (OutOfMemoryError e11) {
                e = e11;
                Logger.m179e(e, "Not enough memory for bitmap");
                if (metadataRetriever != null) {
                    metadataRetriever.release();
                }
                return bitmap;
            }
        }
        return bitmap;
    }

    public InputStreamHolder getThumbnailStreamHolder(ContentResolver cr, int thumbWidth, int thumbHeight) {
        return null;
    }

    public void cleanUp() {
        if (this.tempMediaFile != null) {
            this.tempMediaFile.delete();
        }
        if (this.tempThumbFile != null) {
            this.tempThumbFile.delete();
        }
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.tempMediaFile, flags);
        dest.writeParcelable(this.tempThumbFile, flags);
    }

    static {
        CREATOR = new C05061();
    }
}
