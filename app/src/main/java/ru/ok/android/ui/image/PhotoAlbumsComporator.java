package ru.ok.android.ui.image;

import java.util.Comparator;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;

public class PhotoAlbumsComporator implements Comparator<PhotoAlbumInfo> {
    private String mobileAlbumTitle;
    private String personalAlbumTitle;

    public PhotoAlbumsComporator(String personalAlbumTitle, String mobileAlbumTitle) {
        this.personalAlbumTitle = personalAlbumTitle;
        this.mobileAlbumTitle = mobileAlbumTitle;
    }

    public int compare(PhotoAlbumInfo lhs, PhotoAlbumInfo rhs) {
        if (lhs.getOwnerType() == OwnerType.GROUP) {
            return -1;
        }
        if (rhs.getOwnerType() == OwnerType.GROUP) {
            return 1;
        }
        String lhsName = lhs.getTitle();
        String rhsName = rhs.getTitle();
        if (lhsName == null || rhsName == null) {
            if (lhsName == null) {
                return rhsName == null ? 0 : 1;
            } else {
                return -1;
            }
        } else if (lhsName.equalsIgnoreCase(this.personalAlbumTitle)) {
            return -1;
        } else {
            if (rhsName.equalsIgnoreCase(this.personalAlbumTitle)) {
                return 1;
            }
            if (lhsName.equalsIgnoreCase(this.mobileAlbumTitle)) {
                return -1;
            }
            if (rhsName.equalsIgnoreCase(this.mobileAlbumTitle)) {
                return 1;
            }
            char lhsFirst = lhsName.charAt(0);
            char rhsFirst = rhsName.charAt(0);
            boolean firstCyr = '\u0400' <= lhsFirst && lhsFirst <= '\u04ff';
            boolean secondCyr = '\u0400' <= rhsFirst && rhsFirst <= '\u04ff';
            if (firstCyr && secondCyr) {
                return lhsName.compareTo(rhsName);
            }
            if (firstCyr) {
                return -1;
            }
            if (secondCyr) {
                return 1;
            }
            boolean firstNumber = false;
            try {
                Integer.parseInt(String.valueOf(lhsFirst));
                firstNumber = true;
            } catch (Exception e) {
            }
            boolean secondNumber = false;
            try {
                Integer.parseInt(String.valueOf(rhsFirst));
                secondNumber = true;
            } catch (Exception e2) {
            }
            if (firstNumber && secondNumber) {
                return lhsName.compareTo(rhsName);
            }
            if (firstNumber) {
                return 1;
            }
            if (secondNumber) {
                return -1;
            }
            return lhsName.compareTo(rhsName);
        }
    }
}
