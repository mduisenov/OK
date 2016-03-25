package ru.ok.android.model.pagination.impl;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import java.util.List;
import ru.ok.android.model.pagination.PageAnchor;
import ru.ok.model.photo.PhotoInfo;

public class PhotoInfoPage extends AbstractPage<PhotoInfo> {
    public static final Creator<PhotoInfoPage> CREATOR;

    /* renamed from: ru.ok.android.model.pagination.impl.PhotoInfoPage.1 */
    static class C03761 implements Creator<PhotoInfoPage> {
        C03761() {
        }

        public PhotoInfoPage createFromParcel(Parcel source) {
            return new PhotoInfoPage(null);
        }

        public PhotoInfoPage[] newArray(int size) {
            return new PhotoInfoPage[size];
        }
    }

    public PhotoInfoPage(@NonNull List<PhotoInfo> photoInfoList, @NonNull PageAnchor anchor) {
        super(photoInfoList, anchor);
    }

    private PhotoInfoPage(Parcel source) {
        super(source);
    }

    static {
        CREATOR = new C03761();
    }
}
