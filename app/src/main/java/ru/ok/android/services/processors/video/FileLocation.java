package ru.ok.android.services.processors.video;

import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import ru.ok.android.utils.Logger;

public class FileLocation implements Parcelable, Serializable {
    public static final Creator<FileLocation> CREATOR;
    public static final long serialVersionUID = 1;
    private transient int hashCode;
    private final String path;
    private final int root;

    /* renamed from: ru.ok.android.services.processors.video.FileLocation.1 */
    static class C05001 implements Creator<FileLocation> {
        C05001() {
        }

        public FileLocation createFromParcel(Parcel source) {
            return new FileLocation(source.readInt(), source.readString());
        }

        public FileLocation[] newArray(int size) {
            return new FileLocation[size];
        }
    }

    public static FileLocation external(String path) {
        return new FileLocation(2, path);
    }

    protected FileLocation(int root, String path) {
        while (path.length() > 0 && path.charAt(0) == '/') {
            path = path.substring(1);
        }
        this.root = root;
        this.path = path;
    }

    public File getFile() throws IOException {
        if (this.root == 1) {
            return new File("/" + this.path);
        }
        if (this.root == 2) {
            File externalRoot = Environment.getExternalStorageDirectory();
            if (externalRoot != null) {
                return new File(externalRoot, this.path);
            }
            throw new IOException("External storage root not found");
        }
        throw new IOException("Unknown storage root type: " + this.root);
    }

    public Uri getUriSafe() {
        try {
            return Uri.fromFile(getFile());
        } catch (IOException e) {
            return Uri.fromFile(new File("/" + this.path));
        }
    }

    public boolean delete() {
        boolean z = false;
        try {
            z = getFile().delete();
        } catch (IOException e) {
            Logger.m185w("Failed to delete file: %s", toString());
        }
        return z;
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        FileLocation other = (FileLocation) o;
        if (!(this.root == other.root && TextUtils.equals(this.path, other.path))) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int hashCode = this.hashCode;
        if (hashCode == 0) {
            hashCode = this.root * 1540923637;
            if (this.path != null) {
                hashCode += 520374233 * this.path.hashCode();
            }
            if (hashCode == 0) {
                hashCode = 1;
            }
            this.hashCode = hashCode;
        }
        return hashCode;
    }

    public String toString() {
        StringBuilder append = new StringBuilder().append("FileLocation[root=");
        String str = this.root == 2 ? "External" : this.root == 1 ? "Internal" : "Unknown(" + this.root + ")";
        return append.append(str).append(" path=").append(this.path).append("]").toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.root);
        dest.writeString(this.path);
    }

    static {
        CREATOR = new C05001();
    }

    public static FileLocation createFromExternalFile(File file) {
        if (file == null) {
            return null;
        }
        return external(getRelativePath(Environment.getExternalStorageDirectory(), file));
    }

    private static String getRelativePath(File root, File file) {
        String rootPath = stripLeadingSlashes(root.getAbsolutePath());
        String path = stripLeadingSlashes(file.getAbsolutePath());
        if (path.startsWith(rootPath)) {
            return stripLeadingSlashes(path.substring(rootPath.length()));
        }
        return path;
    }

    private static String stripLeadingSlashes(String path) {
        while (path.length() > 0 && path.charAt(0) == File.separatorChar) {
            path = path.substring(1);
        }
        return path;
    }
}
