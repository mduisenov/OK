package ru.ok.android.ui.image.view;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.ok.android.model.pagination.Page;
import ru.ok.android.model.pagination.Page.Id;
import ru.ok.android.model.pagination.PageAnchor;
import ru.ok.android.model.pagination.PageList;
import ru.ok.android.model.pagination.impl.AbstractPage;
import ru.ok.android.model.pagination.impl.CircularPageList;
import ru.ok.android.model.pagination.impl.ItemIdPageAnchor;
import ru.ok.android.ui.adapters.photo.PhotoLayerAdapter.PhotoAdapterListItem;
import ru.ok.android.ui.adapters.photo.PhotoLayerAdapter.TearListItem;
import ru.ok.android.ui.adapters.photo.StreamPhotoLayerAdapter.PhotoInfoListItem;
import ru.ok.android.ui.image.view.PositionInPageListTracker.Callbacks;
import ru.ok.java.api.request.paging.PagingAnchor;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.model.photo.PhotoInfo;

public final class PhotoInfoListController implements Callbacks<PhotoAdapterListItem> {
    private ListControllerCallback listControllerCallback;
    private PageListWithTears pageList;
    private final PositionInPageListTracker positionTracker;

    public interface ListControllerCallback {
        void onPhotosRequired(@Nullable String str, boolean z, boolean z2);
    }

    private static class PageListWithTears extends CircularPageList<PhotoAdapterListItem> {
        public static final Creator<PageListWithTears> CREATOR;
        boolean hasTearPages;

        /* renamed from: ru.ok.android.ui.image.view.PhotoInfoListController.PageListWithTears.1 */
        static class C10061 implements Creator<PageListWithTears> {
            C10061() {
            }

            public PageListWithTears createFromParcel(Parcel source) {
                return new PageListWithTears(null);
            }

            public PageListWithTears[] newArray(int size) {
                return new PageListWithTears[size];
            }
        }

        private PageListWithTears(Parcel source) {
            boolean z = true;
            super(source);
            if (source.readInt() != 1) {
                z = false;
            }
            this.hasTearPages = z;
        }

        protected void doAddPage(int location, @NonNull Page<PhotoAdapterListItem> page, @NonNull PagingDirection direction) {
            super.addPage(location, page);
            updateTearPagesIfNecessary(location, direction);
            tryToClosePageList(location, direction);
        }

        private void tryToClosePageList(int pageLocation, @NonNull PagingDirection direction) {
            int i;
            boolean z = true;
            int pageCount = getPageCount();
            if (this.hasTearPages) {
                i = 2;
            } else {
                i = 0;
            }
            if (pageCount - i >= 2) {
                int factor;
                if (PagingDirection.BACKWARD == direction) {
                    factor = -1;
                } else {
                    factor = 1;
                }
                int nextPhotoPageLocationInDirection = pageLocation + factor;
                if (nextPhotoPageLocationInDirection >= 0 && nextPhotoPageLocationInDirection < getPageCount()) {
                    Page nextPhotoPageInDirection = getPage(nextPhotoPageLocationInDirection);
                    if (nextPhotoPageInDirection instanceof TearPage) {
                        if (this.hasTearPages) {
                            i = factor * 2;
                        } else {
                            i = 0;
                        }
                        nextPhotoPageLocationInDirection = pageLocation + (factor + i);
                        if (nextPhotoPageLocationInDirection >= 0 && nextPhotoPageLocationInDirection < getPageCount()) {
                            nextPhotoPageInDirection = getPage(nextPhotoPageLocationInDirection);
                        } else {
                            return;
                        }
                    }
                    if (nextPhotoPageInDirection.getCount() != 0) {
                        if (factor <= 0) {
                            z = false;
                        }
                        removeOverlappingIfPossible(z, pageLocation, nextPhotoPageLocationInDirection);
                    }
                }
            }
        }

        private void removeOverlappingIfPossible(boolean forward, int pageLocation, int nextPhotoPageLocationInDirection) {
            int overlappingOffset = getOverlappingOffsetInNewPage(pageLocation, nextPhotoPageLocationInDirection, forward);
            if (overlappingOffset >= 0) {
                removePages(forward, pageLocation, nextPhotoPageLocationInDirection, getPageWithoutOverlappedItems(pageLocation, overlappingOffset, forward));
            }
        }

        private int getOverlappingOffsetInNewPage(int pageLocation, int nextOldPageLocation, boolean forward) {
            Page<PhotoAdapterListItem> newPage = getPage(pageLocation);
            Page<PhotoAdapterListItem> oldPage = getPage(nextOldPageLocation);
            if ((newPage instanceof TearPage) || (oldPage instanceof TearPage)) {
                return -1;
            }
            List<PhotoAdapterListItem> oldItems = oldPage.getElements();
            if (oldItems.size() <= 0) {
                return -1;
            }
            PhotoAdapterListItem oldItem = (PhotoAdapterListItem) oldItems.get(forward ? 0 : oldItems.size() - 1);
            if (oldItem instanceof PhotoInfoListItem) {
                PhotoInfo oldPhotoInfo = ((PhotoInfoListItem) oldItem).getPhotoInfo();
                if (oldPhotoInfo != null) {
                    return getItemOffsetInPage(newPage, oldPhotoInfo);
                }
            }
            return -1;
        }

        @Nullable
        private Page<PhotoAdapterListItem> getPageWithoutOverlappedItems(int pageLocation, int position, boolean forward) {
            int endPos;
            Page<PhotoAdapterListItem> page = getPage(pageLocation);
            List<PhotoAdapterListItem> items = page.getElements();
            int startPos = forward ? position : 0;
            if (forward) {
                endPos = items.size() - 1;
            } else {
                endPos = position;
            }
            for (int i = endPos; i >= startPos; i--) {
                items.remove(i);
            }
            return !items.isEmpty() ? page : null;
        }

        private int getItemOffsetInPage(@NonNull Page<PhotoAdapterListItem> page, @NonNull PhotoInfo photoInfo) {
            List<PhotoAdapterListItem> items = page.getElements();
            int size = items.size();
            for (int i = 0; i < size; i++) {
                PhotoAdapterListItem item = (PhotoAdapterListItem) items.get(i);
                if ((item instanceof PhotoInfoListItem) && ((PhotoInfoListItem) item).getPhotoInfo().getId().equals(photoInfo.getId())) {
                    return i;
                }
            }
            return -1;
        }

        private void removePages(boolean forward, int newPos, int oldPos, @Nullable Page<PhotoAdapterListItem> newPage) {
            int startPos;
            int endPos;
            int i = 0;
            if (forward) {
                startPos = newPos;
            } else {
                startPos = oldPos;
            }
            if (forward) {
                endPos = oldPos;
            } else {
                endPos = newPos;
            }
            Page<PhotoAdapterListItem> oldPage = (Page) this.pages.get(oldPos);
            for (int i2 = endPos; i2 >= startPos; i2--) {
                if (((Page) this.pages.remove(i2)) instanceof TearPage) {
                    this.hasTearPages = false;
                }
            }
            this.pages.add(startPos, oldPage);
            if (newPage != null) {
                List list = this.pages;
                if (!forward) {
                    i = 1;
                }
                list.add(i + startPos, newPage);
            }
            invalidateCache();
        }

        private void updateTearPagesIfNecessary(int location, @NonNull PagingDirection direction) {
            if (this.hasTearPages && getPageCount() != 0) {
                int prevPageCount = getPageCount() - 1;
                if (prevPageCount == 0) {
                    this.pages.add(0, new TearPage());
                    this.pages.add(getPageCount(), new TearPage());
                    invalidateCache();
                    return;
                }
                moveTearPagesIfNecessary(location, prevPageCount, direction);
            }
        }

        private void moveTearPagesIfNecessary(int location, int prevPageCount, @NonNull PagingDirection direction) {
            if (PagingDirection.BACKWARD == direction) {
                if (location == prevPageCount) {
                    moveTearPageIfNecessary(0, prevPageCount - 1);
                }
            } else if (location == 0) {
                moveTearPageIfNecessary(prevPageCount, 1);
            }
        }

        private void moveTearPageIfNecessary(int oldPosition, int newPosition) {
            Page<PhotoAdapterListItem> page = getPage(oldPosition);
            if (page instanceof TearPage) {
                this.pages.remove(oldPosition);
                this.pages.add(newPosition, page);
                invalidateCache();
            }
        }

        protected boolean compareToPageBackwardAnchor(@NonNull String anchor, @NonNull Page<PhotoAdapterListItem> page) {
            if (page instanceof TearPage) {
                return false;
            }
            if (!anchor.startsWith("id:")) {
                return super.compareToPageBackwardAnchor(anchor, page);
            }
            String photoId = PagingAnchor.extractUidFromAnchor(anchor);
            List<PhotoAdapterListItem> pageItems = page.getElements();
            if (pageItems.isEmpty()) {
                return false;
            }
            PhotoInfoListItem photoItem = (PhotoInfoListItem) pageItems.get(0);
            if (photoItem == null || !photoItem.getPhotoInfo().getId().equals(photoId)) {
                return false;
            }
            return true;
        }

        protected boolean compareToPageForwardAnchor(@NonNull String anchor, @NonNull Page<PhotoAdapterListItem> page) {
            if (page instanceof TearPage) {
                return false;
            }
            if (!anchor.startsWith("id:")) {
                return super.compareToPageForwardAnchor(anchor, page);
            }
            String photoId = PagingAnchor.extractUidFromAnchor(anchor);
            List<PhotoAdapterListItem> pageItems = page.getElements();
            if (pageItems.isEmpty()) {
                return false;
            }
            PhotoInfoListItem photoItem = (PhotoInfoListItem) pageItems.get(pageItems.size() - 1);
            if (photoItem == null || !photoItem.getPhotoInfo().getId().equals(photoId)) {
                return false;
            }
            return true;
        }

        public boolean removeItemByPosition(int position) {
            int pageLocation = getPageLocationForPosition(position);
            Page<PhotoAdapterListItem> page = getPage(pageLocation);
            if (page instanceof TearPage) {
                return false;
            }
            List<PhotoAdapterListItem> photos = page.getElements();
            int offsetInPage = getOffsetInPage(position);
            if (offsetInPage == -1) {
                return false;
            }
            photos.remove(offsetInPage);
            if (photos.isEmpty()) {
                removePage(pageLocation);
            }
            invalidateCache();
            return true;
        }

        @NonNull
        public Page<PhotoAdapterListItem> removePage(int location) {
            Page<PhotoAdapterListItem> page = (Page) this.pages.remove(location);
            if (this.hasTearPages && getPageCount() <= 2) {
                this.pages.clear();
                this.hasTearPages = false;
            }
            invalidateCache();
            return page;
        }

        public boolean removeTearPagesIfNecessary() {
            if (!this.hasTearPages || getPageCount() == 0) {
                return false;
            }
            this.pages.remove(getPageCount() - 1);
            this.pages.remove(0);
            this.hasTearPages = false;
            invalidateCache();
            return true;
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.hasTearPages ? 1 : 0);
        }

        static {
            CREATOR = new C10061();
        }
    }

    private static class PhotosPage extends AbstractPage<PhotoAdapterListItem> {
        public static final Creator<PhotosPage> CREATOR;

        /* renamed from: ru.ok.android.ui.image.view.PhotoInfoListController.PhotosPage.1 */
        static class C10071 implements Creator<PhotosPage> {
            C10071() {
            }

            public PhotosPage createFromParcel(Parcel source) {
                return new PhotosPage(null);
            }

            public PhotosPage[] newArray(int size) {
                return new PhotosPage[size];
            }
        }

        public PhotosPage(@NonNull List<PhotoAdapterListItem> elementList, @NonNull PageAnchor anchor) {
            super(elementList, anchor);
        }

        private PhotosPage(Parcel source) {
            super(source);
        }

        static {
            CREATOR = new C10071();
        }
    }

    private static class TearPage extends AbstractPage<PhotoAdapterListItem> {
        public static final Creator<TearPage> CREATOR;

        /* renamed from: ru.ok.android.ui.image.view.PhotoInfoListController.TearPage.1 */
        static class C10081 implements Creator<TearPage> {
            C10081() {
            }

            public TearPage createFromParcel(Parcel source) {
                return new TearPage(source);
            }

            public TearPage[] newArray(int size) {
                return new TearPage[size];
            }
        }

        public TearPage() {
            super(Collections.singletonList(new TearListItem()), new TearPageAnchor());
        }

        protected TearPage(Parcel source) {
            super(source);
        }

        static {
            CREATOR = new C10081();
        }
    }

    private static class TearPageAnchor implements PageAnchor {
        public static final Creator<TearPageAnchor> CREATOR;

        /* renamed from: ru.ok.android.ui.image.view.PhotoInfoListController.TearPageAnchor.1 */
        static class C10091 implements Creator<TearPageAnchor> {
            C10091() {
            }

            public TearPageAnchor createFromParcel(Parcel source) {
                return new TearPageAnchor();
            }

            public TearPageAnchor[] newArray(int size) {
                return new TearPageAnchor[size];
            }
        }

        private TearPageAnchor() {
        }

        @NonNull
        public String getBackwardAnchor() {
            return "backward";
        }

        @NonNull
        public String getForwardAnchor() {
            return "forward";
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
        }

        static {
            CREATOR = new C10091();
        }
    }

    public PhotoInfoListController() {
        this.pageList = new PageListWithTears();
        this.positionTracker = new PositionInPageListTracker(this);
    }

    public boolean hasItems() {
        return !this.pageList.isEmpty();
    }

    public PhotoAdapterListItem getItemByPosition(int realPosition) {
        return (PhotoAdapterListItem) this.pageList.getElement(realPosition);
    }

    public int getCount() {
        return this.pageList.getElementCount();
    }

    @NonNull
    public List<PhotoAdapterListItem> getItems() {
        return this.pageList.getAllElements();
    }

    public int addInitialPage(@NonNull PhotoInfo photoInfo, boolean hasMoreItems) {
        return addInitialPage(Collections.singletonList(photoInfo), new ItemIdPageAnchor(photoInfo.getId(), photoInfo.getId()), 0, hasMoreItems);
    }

    public int addInitialPage(@NonNull List<PhotoInfo> photoInfoList, @NonNull PageAnchor anchor, int trackPosition, boolean hasMoreItems) {
        this.pageList.hasTearPages = hasMoreItems;
        this.pageList.doAddPage(0, createPage(photoInfoList, anchor), PagingDirection.FORWARD);
        if (hasMoreItems) {
            trackPosition++;
            return trackPosition;
        }
        return trackPosition;
    }

    public int addPageInDirection(@Nullable List<PhotoInfo> photoInfoList, @Nullable String requestAnchor, @NonNull PageAnchor anchor, @NonNull PagingDirection direction, boolean hasMoreInDirection, int trackPosition) {
        return addPageInDirection(new PhotosPage(buildItems(photoInfoList), anchor), requestAnchor, direction, hasMoreInDirection, trackPosition);
    }

    private int addPageInDirection(@NonNull Page<PhotoAdapterListItem> page, @Nullable String requestAnchor, @NonNull PagingDirection direction, boolean hasMoreInDirection, int trackPosition) {
        if (page.getCount() == 0) {
            if (requestAnchor == null) {
                trackPosition = removeTearItemsIfNecessary(trackPosition);
            } else if (hasMoreInDirection) {
                this.pageList.addPage(page, requestAnchor, direction);
            }
            return trackPosition;
        }
        this.positionTracker.initialize(trackPosition);
        this.pageList.addPage(page, requestAnchor, direction);
        return this.positionTracker.getNewPositionAfterAddingPage();
    }

    private static List<PhotoAdapterListItem> buildItems(@Nullable List<PhotoInfo> photoInfoList) {
        List<PhotoAdapterListItem> items = new ArrayList();
        if (!(photoInfoList == null || photoInfoList.isEmpty())) {
            for (PhotoInfo photoInfo : photoInfoList) {
                items.add(new PhotoInfoListItem(photoInfo));
            }
        }
        return items;
    }

    private static Page<PhotoAdapterListItem> createPage(@NonNull List<PhotoInfo> photoInfos, @NonNull PageAnchor pageAnchor) {
        return new PhotosPage(buildItems(photoInfos), pageAnchor);
    }

    public PhotoInfo getPhotoInfoById(String photoId) {
        if (!(this.pageList == null || this.pageList.isEmpty())) {
            for (PhotoAdapterListItem item : this.pageList.getAllElements()) {
                if (item instanceof PhotoInfoListItem) {
                    PhotoInfoListItem photoItem = (PhotoInfoListItem) item;
                    if (TextUtils.equals(photoItem.getPhotoInfo().getId(), photoId)) {
                        return photoItem.getPhotoInfo();
                    }
                }
            }
        }
        return null;
    }

    public void checkNearTearPosition(int position) {
        int pageLocation = this.pageList.getPageLocationForPosition(position);
        if (pageLocation != -1) {
            notifyNearBackwardTearPositionIfNecessary(pageLocation, position);
            notifyNearForwardTearPositionIfNecessary(pageLocation, position);
        }
    }

    private void notifyNearBackwardTearPositionIfNecessary(int pageLocation, int trackPosition) {
        Page<PhotoAdapterListItem> currentPage = this.pageList.getPage(pageLocation);
        int prevPageLocation = pageLocation - 1;
        int emptyPageLocation = findLastEmptyPhotosPageLocationInBackwardDirection(prevPageLocation);
        if (emptyPageLocation != -1) {
            prevPageLocation = emptyPageLocation - 1;
            currentPage = this.pageList.getPage(emptyPageLocation);
        }
        if (prevPageLocation >= 0 && (this.pageList.getPage(prevPageLocation) instanceof TearPage) && trackPosition - this.pageList.getStartPositionForPage(prevPageLocation) <= 4) {
            notifyCallback(currentPage.getAnchor().getBackwardAnchor(), false);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int findLastEmptyPhotosPageLocationInBackwardDirection(int r5) {
        /*
        r4 = this;
        r1 = -1;
        r0 = r5;
    L_0x0002:
        if (r0 < 0) goto L_0x0018;
    L_0x0004:
        r3 = r4.pageList;
        r2 = r3.getPage(r0);
        r3 = r2 instanceof ru.ok.android.ui.image.view.PhotoInfoListController.PhotosPage;
        if (r3 == 0) goto L_0x0018;
    L_0x000e:
        r3 = r2.getCount();
        if (r3 != 0) goto L_0x0018;
    L_0x0014:
        r1 = r0;
        r0 = r0 + -1;
        goto L_0x0002;
    L_0x0018:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.image.view.PhotoInfoListController.findLastEmptyPhotosPageLocationInBackwardDirection(int):int");
    }

    private void notifyNearForwardTearPositionIfNecessary(int pageLocation, int trackPosition) {
        Page<PhotoAdapterListItem> currentPage = this.pageList.getPage(pageLocation);
        int nextPageLocation = pageLocation + 1;
        int emptyPageLocation = findLastEmptyPhotosPageLocationInForwardDirection(nextPageLocation);
        if (emptyPageLocation != -1) {
            nextPageLocation = emptyPageLocation + 1;
            currentPage = this.pageList.getPage(emptyPageLocation);
        }
        if (nextPageLocation < this.pageList.getPageCount() && (this.pageList.getPage(nextPageLocation) instanceof TearPage) && this.pageList.getStartPositionForPage(nextPageLocation) - trackPosition <= 4) {
            notifyCallback(currentPage.getAnchor().getForwardAnchor(), true);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int findLastEmptyPhotosPageLocationInForwardDirection(int r6) {
        /*
        r5 = this;
        r1 = -1;
        r0 = r6;
        r4 = r5.pageList;
        r3 = r4.getPageCount();
    L_0x0008:
        if (r0 >= r3) goto L_0x001e;
    L_0x000a:
        r4 = r5.pageList;
        r2 = r4.getPage(r0);
        r4 = r2 instanceof ru.ok.android.ui.image.view.PhotoInfoListController.PhotosPage;
        if (r4 == 0) goto L_0x001e;
    L_0x0014:
        r4 = r2.getCount();
        if (r4 != 0) goto L_0x001e;
    L_0x001a:
        r1 = r0;
        r0 = r0 + 1;
        goto L_0x0008;
    L_0x001e:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.image.view.PhotoInfoListController.findLastEmptyPhotosPageLocationInForwardDirection(int):int");
    }

    public int removeItemByPosition(int position) {
        this.positionTracker.initialize(position);
        return this.positionTracker.getNewPositionAfterRemovingPageItem(removeFromPageList(position));
    }

    public int getTrackPositionOffsetAfterRemovingPageItem(int trackPosition) {
        Page<PhotoAdapterListItem> trackPage = this.pageList.getPageForPosition(trackPosition);
        int itemCount = this.pageList.getElementCount();
        if (!(trackPage instanceof TearPage)) {
            return 0;
        }
        if (trackPosition + 1 < itemCount && !(this.pageList.getPageForPosition(trackPosition + 1) instanceof TearPage)) {
            return 0 + 1;
        }
        if (this.pageList.getPageForPosition(trackPosition - 1) instanceof TearPage) {
            return 0;
        }
        return 0 - 1;
    }

    private boolean removeFromPageList(int position) {
        return this.pageList.removeItemByPosition(position);
    }

    public boolean updatePhotoInfo(@NonNull PhotoInfo photoInfo) {
        if (!hasItems()) {
            return false;
        }
        PhotoInfoListItem photoItem = null;
        for (PhotoAdapterListItem item : getItems()) {
            if (item.getType() == 2) {
                PhotoInfoListItem infoListItem = (PhotoInfoListItem) item;
                if (infoListItem.getPhotoInfo().getId().equals(photoInfo.getId())) {
                    photoItem = infoListItem;
                    break;
                }
            }
        }
        if (photoItem != null) {
            photoItem.setPhotoInfo(photoInfo);
        }
        if (photoItem != null) {
            return true;
        }
        return false;
    }

    public int removeTearItemsIfNecessary(int trackPosition) {
        return trackPosition - (this.pageList.removeTearPagesIfNecessary() ? 1 : 0);
    }

    private void notifyCallback(@Nullable String anchor, boolean forward) {
        if (this.listControllerCallback != null) {
            this.listControllerCallback.onPhotosRequired(anchor, forward, false);
        }
    }

    public void setListControllerCallback(@Nullable ListControllerCallback listControllerCallback) {
        this.listControllerCallback = listControllerCallback;
    }

    public void onSaveInstanceState(@NonNull Bundle bundle) {
        bundle.putParcelable("list_items", this.pageList);
    }

    public void onRestoreInstanceState(@NonNull Bundle bundle) {
        this.pageList = (PageListWithTears) bundle.getParcelable("list_items");
    }

    @NonNull
    public Id getPageIdForPosition(int position) {
        return this.pageList.getPage(this.pageList.getPageLocationForPosition(position)).getId();
    }

    @NonNull
    public PageList<PhotoAdapterListItem> getPageList() {
        return this.pageList;
    }
}
