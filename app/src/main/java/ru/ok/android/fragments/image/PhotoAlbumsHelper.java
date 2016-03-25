package ru.ok.android.fragments.image;

import android.text.TextUtils;
import android.text.format.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.java.api.utils.DateUtils;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.AccessType;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.photo.PhotosInfo;

public final class PhotoAlbumsHelper {
    private AlbumsHelperCallback callback;

    public interface AlbumsHelperCallback {
        List<PhotoAlbumInfo> getAlbumsInfoList();

        void notifyDataSetChanged();

        void setState(int i);
    }

    public PhotoAlbumsHelper(AlbumsHelperCallback callback) {
        this.callback = callback;
    }

    public final Collection<PhotoAlbumInfo> filterEmptyAlbums(Collection<PhotoAlbumInfo> albums) {
        if (albums == null || albums.isEmpty()) {
            return albums;
        }
        Collection<PhotoAlbumInfo> arrayList = new ArrayList(albums.size());
        for (PhotoAlbumInfo album : albums) {
            if (album.getPhotoCount() > 0 || album.isCanAddPhoto() || album.isCanModify() || album.isCanDelete()) {
                arrayList.add(album);
            }
        }
        return arrayList;
    }

    public void moveAlbumToTop(PhotoAlbumInfo albumInfo) {
        this.callback.getAlbumsInfoList().remove(albumInfo);
        this.callback.getAlbumsInfoList().add(0, albumInfo);
    }

    public void insertNewAlbum(PhotoAlbumInfo album) {
        int firstPos = 0;
        for (int i = 0; i < this.callback.getAlbumsInfoList().size(); i++) {
            if (!((PhotoAlbumInfo) this.callback.getAlbumsInfoList().get(i)).isVirtual()) {
                firstPos = i;
                break;
            }
        }
        this.callback.getAlbumsInfoList().add(firstPos, album);
    }

    protected final PhotoAlbumInfo getPhotoAlbum(String aid) {
        if (this.callback.getAlbumsInfoList() == null) {
            return null;
        }
        for (PhotoAlbumInfo anfo : this.callback.getAlbumsInfoList()) {
            if (TextUtils.equals(anfo.getId(), aid)) {
                return anfo;
            }
        }
        return null;
    }

    public PhotoAlbumInfo findAlbumById(String aid) {
        for (PhotoAlbumInfo album : this.callback.getAlbumsInfoList()) {
            if (aid.equals(album.getId())) {
                return album;
            }
        }
        return null;
    }

    public final void updateAlbum(PhotoAlbumInfo album) {
        PhotoAlbumInfo foundAlbum = findAlbumById(album.getId());
        if (foundAlbum != null) {
            foundAlbum.setTitle(album.getTitle());
            foundAlbum.setType(album.getType());
            foundAlbum.setTypes(album.getTypes());
            this.callback.notifyDataSetChanged();
        }
    }

    public static PhotoAlbumInfo createVirtualAlbum(String aid, String title, PhotosInfo photo) {
        PhotoAlbumInfo album = createEmptyAlbum(aid, title);
        album.setType(AccessType.PUBLIC);
        List<AccessType> accessTypes = new ArrayList(1);
        accessTypes.add(AccessType.PUBLIC);
        album.setTypes(accessTypes);
        if (photo != null) {
            if (!(photo.getPhotos() == null || photo.getPhotos().isEmpty())) {
                album.setMainPhotoInfo((PhotoInfo) photo.getPhotos().get(0));
            }
            album.setPhotoCount(photo.getTotalCount());
        }
        return album;
    }

    public final void removeAlbum(PhotoAlbumInfo album) {
        this.callback.getAlbumsInfoList().remove(album);
        this.callback.notifyDataSetChanged();
        if (this.callback.getAlbumsInfoList().isEmpty()) {
            this.callback.setState(3);
        } else {
            this.callback.setState(1);
        }
    }

    public final void setAlbumMainPhoto(String aid, PhotoInfo pinfo) {
        PhotoAlbumInfo loadedAlbum = getPhotoAlbum(aid);
        if (loadedAlbum != null && pinfo != null) {
            loadedAlbum.setMainPhotoInfo(pinfo);
            this.callback.notifyDataSetChanged();
        }
    }

    public final boolean updateAlbumWithNewUpload(PhotoOwner photoOwner, PhotoAlbumInfo albumInfo) {
        if (photoOwner.getType() == 1 && !TextUtils.equals(albumInfo.getGroupId(), photoOwner.getId())) {
            return false;
        }
        if (photoOwner.getType() == 0 && !TextUtils.equals(albumInfo.getUserId(), photoOwner.getId())) {
            return false;
        }
        PhotoAlbumInfo loadedAlbum = getPhotoAlbum(albumInfo.getId());
        if (loadedAlbum == null) {
            loadedAlbum = albumInfo;
            if (this.callback == null || this.callback.getAlbumsInfoList() == null) {
                return false;
            }
            this.callback.getAlbumsInfoList().add(0, albumInfo);
        } else {
            moveAlbumToTop(loadedAlbum);
        }
        loadedAlbum.setPhotoCount(loadedAlbum.getPhotoCount() + 1);
        this.callback.notifyDataSetChanged();
        return true;
    }

    public final void addNewAlbum(PhotoOwner photoOwner, String aid, String title, String gid, List<AccessType> accessTypes) {
        PhotoAlbumInfo album = createEmptyAlbum(aid, title);
        album.setOwnerType(photoOwner.getType() == 1 ? OwnerType.GROUP : OwnerType.USER);
        album.setGroupId(gid);
        album.setCanDelete(true);
        album.setCanModify(true);
        album.setCanAddPhoto(true);
        album.setUserId(OdnoklassnikiApplication.getCurrentUser().uid);
        album.setTypes(accessTypes);
        Time now = new Time();
        now.setToNow();
        album.setCreated(DateUtils.getShortStringFromDate(now));
        insertNewAlbum(album);
        this.callback.notifyDataSetChanged();
        this.callback.setState(1);
    }

    public static PhotoAlbumInfo createEmptyAlbum(String aid, String title) {
        PhotoAlbumInfo album = new PhotoAlbumInfo();
        album.setTitle(title);
        album.setId(aid);
        return album;
    }
}
