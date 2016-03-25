package ru.ok.android.ui.image.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.model.pagination.PageAnchor;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.photo.PhotoTag;
import ru.ok.model.photo.PhotosInfo;

public class PhotoInfoProvider extends Fragment {
    private boolean isAlive;
    protected final List<WeakReference<OnPhotoTagsReceivedListener>> onPhotoTagsReceiveListeners;
    protected final Set<String> pengingAnchorRequests;
    protected final Set<String> pengingTagsRequests;
    private long providerId;

    public interface OnPhotoTagsReceivedListener {
        void onTagsFailed(String str);

        boolean onTagsReceived(String str, ArrayList<UserInfo> arrayList, ArrayList<PhotoTag> arrayList2);
    }

    public interface OnBatchReceiveListener {
        void onBatchReceived(@Nullable PageAnchor pageAnchor, PhotoInfo photoInfo, PhotosInfo photosInfo, PhotosInfo photosInfo2, PhotoAlbumInfo photoAlbumInfo, PhotoOwner photoOwner, List<PhotoInfo> list);

        void onBatchRequestFailed(String str, int i);
    }

    public interface OnFullPhotoInfoReceiveListener {
        void onFullPhotoInfoReceived(PhotoInfo photoInfo, PhotoAlbumInfo photoAlbumInfo, UserInfo userInfo, GroupInfo groupInfo);

        void onFullPhotoRequestFailed(String str);
    }

    public interface OnPhotoInfoReceiveListener {
        void onPhotoInfoReceived(PhotoInfo photoInfo);

        void onPhotoInfoRequestFailed(String str);
    }

    public interface OnPhotoInfosReceiveListener {
        void onPhotoInfosRequestFailed(String str, String str2, boolean z);

        void onPhotosInfoReceived(PhotosInfo photosInfo, boolean z, String str);
    }

    public PhotoInfoProvider() {
        this.pengingAnchorRequests = new HashSet();
        this.pengingTagsRequests = new HashSet();
        this.onPhotoTagsReceiveListeners = Collections.synchronizedList(new ArrayList());
        this.isAlive = true;
        this.providerId = System.currentTimeMillis();
    }

    public static PhotoInfoProvider findOrCreateRetainFragment(FragmentManager fragmentManager) {
        PhotoInfoProvider fragment = (PhotoInfoProvider) fragmentManager.findFragmentByTag("PhotoInfoProvider");
        if (fragment != null && fragment.isAlive) {
            return fragment;
        }
        Fragment fragment2 = new PhotoInfoProvider();
        fragmentManager.beginTransaction().add(fragment2, "PhotoInfoProvider").commit();
        return fragment2;
    }

    public final void destroyProvider() {
        this.isAlive = false;
        GlobalBus.unregister(this);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        GlobalBus.register(this);
    }

    public void onDestroy() {
        super.onDestroy();
        GlobalBus.unregister(this);
    }

    public final void requestFullPhotoInfo(String photoId, String albumId, String groupId, String userId, boolean requestUserInfo, boolean requestAlbumInfo, boolean requestGroupInfo) {
        Bundle bundleInput = new Bundle();
        bundleInput.putString("pid", photoId);
        bundleInput.putString("aid", albumId);
        bundleInput.putString("uid", userId);
        bundleInput.putString("gid", groupId);
        bundleInput.putBoolean("rai", requestAlbumInfo);
        bundleInput.putBoolean("rui", requestUserInfo);
        bundleInput.putBoolean("rgi", requestGroupInfo);
        signRequest(bundleInput);
        GlobalBus.send(2131623999, new BusEvent(bundleInput));
    }

    public final void requestInfoBatch(boolean requestAlbumInfo, String albumId, boolean requestPhotoInfo, String photoId, String[] spids, PhotoOwner photoOwner, @Nullable PageAnchor pageAnchor) {
        Bundle bundleInput = new Bundle();
        bundleInput.putString("aid", albumId);
        bundleInput.putBoolean("ganfo", requestAlbumInfo);
        bundleInput.putBoolean("gphtnfo", requestPhotoInfo);
        bundleInput.putString("pid", photoId);
        bundleInput.putParcelable("phwnr", photoOwner);
        bundleInput.putParcelable("anchor", pageAnchor);
        bundleInput.putInt("cnt", 40);
        bundleInput.putStringArray("phtseq", spids);
        if (photoOwner.getType() == 1 && photoOwner.getOwnerInfo() == null) {
            bundleInput.putBoolean("ggnfo", true);
        }
        signRequest(bundleInput);
        GlobalBus.send(2131624006, new BusEvent(bundleInput));
    }

    public final void requestUserAlbumPhotosInfos(String albumId, String anchor, boolean forward, PhotoOwner photoOwner) {
        boolean request = false;
        String anchorKey = anchor + forward;
        if (!this.pengingAnchorRequests.contains(anchorKey)) {
            this.pengingAnchorRequests.add(anchorKey);
            request = true;
        }
        if (request) {
            Bundle bundleInput = new Bundle();
            bundleInput.putString("aid", albumId);
            bundleInput.putString("anchr", anchor);
            bundleInput.putInt("cnt", 40);
            bundleInput.putParcelable("pwnr", photoOwner);
            bundleInput.putBoolean("dtctcnt", !"stream".equals(albumId));
            bundleInput.putBoolean("fwd", forward);
            signRequest(bundleInput);
            GlobalBus.send(2131624005, new BusEvent(bundleInput));
        }
    }

    public final void requestPhotoTagsInfo(String photoId) {
        if (!this.pengingTagsRequests.contains(photoId)) {
            this.pengingTagsRequests.add(photoId);
            Bundle bundleInput = new Bundle();
            bundleInput.putString("pid", photoId);
            signRequest(bundleInput);
            GlobalBus.send(2131624004, new BusEvent(bundleInput));
        }
    }

    private void signRequest(Bundle request) {
        request.putLong("reqsig", this.providerId);
    }

    @Subscribe(on = 2131623946, to = 2131624176)
    public void onFullPhotoInfoReceived(BusEvent event) {
        if (isRequestValid(event)) {
            Bundle bundleOutput = event.bundleOutput;
            if (event.resultCode == -1) {
                PhotoInfo photoInfo = (PhotoInfo) bundleOutput.getParcelable("pnfo");
                PhotoAlbumInfo albumInfo = (PhotoAlbumInfo) bundleOutput.getParcelable("anfo");
                UserInfo userInfo = (UserInfo) bundleOutput.getParcelable("unfo");
                GroupInfo groupInfo = (GroupInfo) bundleOutput.getParcelable("gnfo");
                if (getActivity() != null) {
                    ((OnFullPhotoInfoReceiveListener) getActivity()).onFullPhotoInfoReceived(photoInfo, albumInfo, userInfo, groupInfo);
                }
            } else if (getActivity() != null) {
                ((OnFullPhotoInfoReceiveListener) getActivity()).onFullPhotoRequestFailed(bundleOutput.getString("pid"));
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624180)
    public void onGetPhotoInfoReceived(BusEvent event) {
        if (isRequestValid(event)) {
            Bundle bundleOutput = event.bundleOutput;
            if (event.resultCode == -1) {
                PhotoInfo photoInfo = (PhotoInfo) bundleOutput.getParcelable("xtrpi");
                if (getActivity() != null) {
                    ((OnPhotoInfoReceiveListener) getActivity()).onPhotoInfoReceived(photoInfo);
                }
            } else if (getActivity() != null) {
                ((OnPhotoInfoReceiveListener) getActivity()).onPhotoInfoRequestFailed(bundleOutput.getString("id"));
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624183)
    public void onPhotosBatchReceived(BusEvent event) {
        if (isRequestValid(event)) {
            Bundle bundleOutput = event.bundleOutput;
            if (event.resultCode == -1) {
                PageAnchor anchor = (PageAnchor) bundleOutput.getParcelable("anchor");
                PhotoInfo photoInfo = (PhotoInfo) bundleOutput.getParcelable("phtinfo");
                PhotoAlbumInfo albumInfo = (PhotoAlbumInfo) bundleOutput.getParcelable("albnfo");
                PhotosInfo backwardPhotosInfo = (PhotosInfo) bundleOutput.getParcelable("phtsbckw");
                PhotosInfo forwardPhotosInfo = (PhotosInfo) bundleOutput.getParcelable("phtsfwd");
                PhotoOwner photoOwner = (PhotoOwner) bundleOutput.getParcelable("phwnr");
                List<PhotoInfo> spInfos = bundleOutput.getParcelableArrayList("phtseq");
                if (getActivity() != null) {
                    ((OnBatchReceiveListener) getActivity()).onBatchReceived(anchor, photoInfo, backwardPhotosInfo, forwardPhotosInfo, albumInfo, photoOwner, spInfos);
                    return;
                }
                return;
            }
            String id = bundleOutput.getString("pid");
            if (getActivity() != null) {
                int error = 1;
                if (event.resultCode == 4) {
                    error = 2;
                } else if (event.resultCode == 3) {
                    error = 3;
                } else if (event.resultCode == 5) {
                    error = 4;
                }
                ((OnBatchReceiveListener) getActivity()).onBatchRequestFailed(id, error);
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624182)
    public void onPhotosReceived(BusEvent event) {
        if (isRequestValid(event)) {
            Bundle bundleOutput = event.bundleOutput;
            boolean forward = bundleOutput.getBoolean("fwd");
            String anchor = bundleOutput.getString("anchr");
            String aid = bundleOutput.getString("aid");
            if (event.resultCode == -1) {
                PhotosInfo userPhotos = (PhotosInfo) bundleOutput.getParcelable("phtsnfo");
                if (getActivity() != null) {
                    ((OnPhotoInfosReceiveListener) getActivity()).onPhotosInfoReceived(userPhotos, forward, anchor);
                }
            } else if (getActivity() != null) {
                ((OnPhotoInfosReceiveListener) getActivity()).onPhotoInfosRequestFailed(anchor, aid, forward);
            }
            clearPendingAnchors(anchor, forward);
        }
    }

    protected final void clearPendingAnchors(String anchor, boolean forward) {
        this.pengingAnchorRequests.remove(anchor + forward);
    }

    @Subscribe(on = 2131623946, to = 2131624181)
    public void onTagsReceived(BusEvent event) {
        if (isRequestValid(event)) {
            Bundle bundleOutput = event.bundleOutput;
            String photoId = bundleOutput.getString("pid");
            this.pengingTagsRequests.remove(photoId);
            Iterator<WeakReference<OnPhotoTagsReceivedListener>> it;
            OnPhotoTagsReceivedListener listener;
            if (event.resultCode == -1) {
                ArrayList<UserInfo> users = bundleOutput.getParcelableArrayList("usrs");
                ArrayList<PhotoTag> tags = bundleOutput.getParcelableArrayList("tags");
                synchronized (this.onPhotoTagsReceiveListeners) {
                    it = this.onPhotoTagsReceiveListeners.iterator();
                    while (it.hasNext()) {
                        listener = (OnPhotoTagsReceivedListener) ((WeakReference) it.next()).get();
                        if (listener == null) {
                            it.remove();
                        } else {
                            listener.onTagsReceived(photoId, users, tags);
                        }
                    }
                }
                return;
            }
            synchronized (this.onPhotoTagsReceiveListeners) {
                it = this.onPhotoTagsReceiveListeners.iterator();
                while (it.hasNext()) {
                    listener = (OnPhotoTagsReceivedListener) ((WeakReference) it.next()).get();
                    if (listener == null) {
                        it.remove();
                    } else {
                        listener.onTagsFailed(photoId);
                    }
                }
            }
        }
    }

    private boolean isRequestValid(BusEvent event) {
        return event.bundleInput.getLong("reqsig") == this.providerId;
    }

    public void addOnPhototagsReceivedListener(OnPhotoTagsReceivedListener toAdd) {
        Iterator<WeakReference<OnPhotoTagsReceivedListener>> it = this.onPhotoTagsReceiveListeners.iterator();
        while (it.hasNext()) {
            OnPhotoTagsReceivedListener listener = (OnPhotoTagsReceivedListener) ((WeakReference) it.next()).get();
            if (listener == null) {
                it.remove();
            } else if (listener == toAdd) {
                return;
            }
        }
        this.onPhotoTagsReceiveListeners.add(new WeakReference(toAdd));
    }

    public void removeOnPhototagsReceivedListener(OnPhotoTagsReceivedListener victim) {
        Iterator<WeakReference<OnPhotoTagsReceivedListener>> it = this.onPhotoTagsReceiveListeners.iterator();
        while (it.hasNext()) {
            OnPhotoTagsReceivedListener listener = (OnPhotoTagsReceivedListener) ((WeakReference) it.next()).get();
            if (listener == null || listener == victim) {
                it.remove();
            }
        }
    }

    public final boolean hasPendingTagsRequests() {
        return !this.pengingTagsRequests.isEmpty();
    }
}
