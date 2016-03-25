package ru.ok.android.ui.adapters.photo;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.custom.photo.PhotoTileView.OnPhotoTileClickListener;
import ru.ok.android.ui.custom.photo.PhotoTilesRowView;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.stream.LikeInfo;

public class PhotoInfosListAdapter extends BaseAdapter {
    private static int instanceCount;
    private static int photoInfosBulkInstanceCount;
    private final ArrayList<PhotoInfosBulk> bulks;
    private final ArrayList<PhotoInfosBulk> bulksBuffer;
    private final ArrayList<PhotoInfosBulk> bulksRecycleBuffer;
    private int columnsCount;
    private Context context;
    private final boolean darkenBlocked;
    private final List<PhotoInfo> deletedItems;
    private final int instanceId;
    private final ArrayList<PhotoInfo> items;
    private OnNearListEndListener onNearListEndListener;
    protected OnPhotoTileClickListener onPhotoTileClickListener;
    private int tileSize;

    public interface OnNearListEndListener {
        void onNearListEnd();
    }

    private final class PhotoInfosBulk {
        public PhotoInfo bigPhotoInfo;
        public int bigPhotoPos;
        public int endPos;
        private final int instanceId;
        public PhotoInfo[] photoInfos;
        public int startPos;

        private PhotoInfosBulk() {
            this.startPos = LinearLayoutManager.INVALID_OFFSET;
            this.endPos = LinearLayoutManager.INVALID_OFFSET;
            this.bigPhotoPos = LinearLayoutManager.INVALID_OFFSET;
            this.instanceId = PhotoInfosListAdapter.access$104();
        }

        public final void reset() {
            this.startPos = LinearLayoutManager.INVALID_OFFSET;
            this.endPos = LinearLayoutManager.INVALID_OFFSET;
            this.bigPhotoPos = LinearLayoutManager.INVALID_OFFSET;
            this.photoInfos = null;
            this.bigPhotoInfo = null;
        }

        public final boolean hasBigPhoto() {
            return this.bigPhotoPos != LinearLayoutManager.INVALID_OFFSET;
        }

        public final int getCount() {
            return (this.endPos + 1) - this.startPos;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("PhotoInfosBulk@").append(Integer.toString(this.instanceId));
            sb.append("[startPos=").append(this.startPos == LinearLayoutManager.INVALID_OFFSET ? "NO_SELECTION" : Integer.toString(this.startPos));
            sb.append(" endPos=").append(this.endPos == LinearLayoutManager.INVALID_OFFSET ? "NO_SELECTION" : Integer.toString(this.endPos));
            sb.append(" bigPhotoPos=").append(this.bigPhotoPos == LinearLayoutManager.INVALID_OFFSET ? "NO_SELECTION" : Integer.toString(this.bigPhotoPos));
            sb.append(" ids={");
            int count = PhotoInfosListAdapter.this.items == null ? 0 : PhotoInfosListAdapter.this.items.size();
            for (int i = 0; i < count; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(((PhotoInfo) PhotoInfosListAdapter.this.items.get(i)).getId());
            }
            sb.append("} bigPhotoId=");
            if (this.bigPhotoInfo == null) {
                sb.append("null]");
            } else {
                sb.append(this.bigPhotoInfo.getId()).append(']');
            }
            return sb.toString();
        }
    }

    static /* synthetic */ int access$104() {
        int i = photoInfosBulkInstanceCount + 1;
        photoInfosBulkInstanceCount = i;
        return i;
    }

    public PhotoInfosListAdapter(Context context, List<PhotoInfo> deletedItems, boolean darkenBlocked) {
        this.items = new ArrayList();
        this.bulks = new ArrayList();
        this.bulksBuffer = new ArrayList();
        this.bulksRecycleBuffer = new ArrayList();
        int i = instanceCount + 1;
        instanceCount = i;
        this.instanceId = i;
        this.context = context;
        this.deletedItems = deletedItems;
        addBulked(this.items);
        this.darkenBlocked = darkenBlocked;
    }

    public final void clear() {
        this.items.clear();
        this.bulks.clear();
        this.bulksBuffer.clear();
        this.bulksRecycleBuffer.clear();
    }

    public final void add(List<PhotoInfo> items) {
        Logger.m173d("(%d) >>> add items=%s", Integer.valueOf(this.instanceId), items);
        Logger.m173d("(%d) add: columnsCount=%d", Integer.valueOf(this.instanceId), Integer.valueOf(this.columnsCount));
        if (!(items == null || items.isEmpty())) {
            if (!this.bulks.isEmpty()) {
                int countInBulk;
                Logger.m173d("(%d) reusing bulks...", Integer.valueOf(this.instanceId));
                dumpItems("before add:");
                PhotoInfosBulk lastBulk = (PhotoInfosBulk) this.bulks.get(this.bulks.size() - 1);
                int inLastBulk = lastBulk.photoInfos.length;
                if (lastBulk.bigPhotoInfo == null) {
                    countInBulk = this.columnsCount;
                } else {
                    countInBulk = (this.columnsCount * 2) - 3;
                }
                int missing = countInBulk - inLastBulk;
                if (missing > 0) {
                    boolean removingItems = false;
                    PhotoInfo[] infos = new PhotoInfo[countInBulk];
                    int length = lastBulk.photoInfos.length;
                    int offset = 0;
                    while (offset < length) {
                        infos[offset] = lastBulk.photoInfos[offset];
                        offset++;
                    }
                    int i = 0;
                    List<PhotoInfo> items2 = items;
                    while (i < missing) {
                        if (removingItems) {
                            items = items2;
                        } else {
                            items = new LinkedList(items2);
                            removingItems = true;
                        }
                        infos[i + offset] = (PhotoInfo) ((LinkedList) items).removeFirst();
                        if (items.isEmpty()) {
                            break;
                        }
                        i++;
                        items2 = items;
                    }
                    items = items2;
                    lastBulk.photoInfos = infos;
                    lastBulk.endPos += missing;
                }
            }
            if (!items.isEmpty()) {
                addBulked(items);
            }
            dumpItems("after add:");
        }
        Logger.m173d("(%d) <<< add", Integer.valueOf(this.instanceId));
    }

    private void addBulked(List<PhotoInfo> items) {
        if (items != null && !items.isEmpty()) {
            PhotoInfosBulk bulk;
            int singleRowCount = this.columnsCount;
            int doubleRowCount = (this.columnsCount * 2) - 3;
            int chunkCount = singleRowCount + doubleRowCount;
            int size = items.size();
            int counter = 0;
            int chunkBigPhotoPos = -1;
            int chunkBigPhotoActionsCount = -1;
            int position = -1;
            for (PhotoInfo info : items) {
                position++;
                counter++;
                LikeInfo likeInfo = info.getLikeInfo();
                int currentActionsCount = (info.getCommentsCount() + (likeInfo == null ? 0 : likeInfo.count)) + info.getMarksCount();
                if (currentActionsCount > chunkBigPhotoActionsCount) {
                    chunkBigPhotoActionsCount = currentActionsCount;
                    chunkBigPhotoPos = position;
                }
                if (counter == chunkCount || position == size - 1) {
                    int startPos = (position - counter) + 1;
                    int endPos = (startPos + chunkCount) - 1;
                    if (endPos >= size) {
                        endPos = size - 1;
                    }
                    if (chunkBigPhotoPos >= startPos + singleRowCount) {
                        bulk = getCleanBulk();
                        this.bulksBuffer.add(bulk);
                        bulk.startPos = startPos;
                        bulk.endPos = (startPos + singleRowCount) - 1;
                        fillBulk(items, bulk);
                        startPos = bulk.endPos + 1;
                        bulk = getCleanBulk();
                        this.bulksBuffer.add(bulk);
                        bulk.startPos = startPos;
                        bulk.endPos = endPos;
                        bulk.bigPhotoPos = chunkBigPhotoPos;
                        ensureBigPhotoInRightPosition(items, bulk);
                        fillBulk(items, bulk);
                    } else {
                        bulk = getCleanBulk();
                        this.bulksBuffer.add(bulk);
                        bulk.startPos = startPos;
                        bulk.bigPhotoPos = chunkBigPhotoPos;
                        int bulkEndPos = (startPos + doubleRowCount) - 1;
                        if (bulkEndPos >= endPos) {
                            bulk.endPos = endPos;
                            ensureBigPhotoInRightPosition(items, bulk);
                            fillBulk(items, bulk);
                        } else {
                            bulk.endPos = bulkEndPos;
                            ensureBigPhotoInRightPosition(items, bulk);
                            fillBulk(items, bulk);
                            bulk = getCleanBulk();
                            this.bulksBuffer.add(bulk);
                            bulk.startPos = bulkEndPos + 1;
                            bulk.endPos = endPos;
                            fillBulk(items, bulk);
                        }
                    }
                    counter = 0;
                    chunkBigPhotoActionsCount = -1;
                    chunkBigPhotoPos = -1;
                }
            }
            int offset = this.items.size();
            int i = 0;
            while (true) {
                if (i < this.bulksBuffer.size()) {
                    bulk = (PhotoInfosBulk) this.bulksBuffer.get(i);
                    int i2 = bulk.bigPhotoPos;
                    if (r0 != Integer.MIN_VALUE) {
                        bulk.bigPhotoPos += offset;
                    }
                    bulk.startPos += offset;
                    bulk.endPos += offset;
                    i++;
                } else {
                    this.bulks.addAll(this.bulksBuffer);
                    this.items.addAll(items);
                    this.bulksBuffer.clear();
                    return;
                }
            }
        }
    }

    private void ensureBigPhotoInRightPosition(List<PhotoInfo> items, PhotoInfosBulk bulk) {
        int maxPosition = bulk.startPos + (((int) Math.ceil(((double) bulk.getCount()) * 0.5d)) - 1);
        if (maxPosition > 0 && bulk.bigPhotoPos > maxPosition) {
            Collections.swap(items, bulk.bigPhotoPos, maxPosition);
            bulk.bigPhotoPos = maxPosition;
        }
    }

    private void fillBulk(List<PhotoInfo> items, PhotoInfosBulk bulk) {
        PhotoInfo[] photoInfos;
        if (bulk.bigPhotoPos != LinearLayoutManager.INVALID_OFFSET) {
            bulk.bigPhotoInfo = (PhotoInfo) items.get(bulk.bigPhotoPos);
        }
        if (bulk.startPos <= bulk.endPos) {
            photoInfos = (PhotoInfo[]) Utils.copyOfRange(items.toArray(new PhotoInfo[0]), bulk.startPos, bulk.endPos + 1);
        } else {
            photoInfos = new PhotoInfo[0];
            String message = String.format("Wrong photos bulk (%s), columns count (%d), photos (%s)", new Object[]{bulk, Integer.valueOf(this.columnsCount), items});
            Logger.m184w(message);
            StatisticManager.getInstance().reportError("PhotoInfosListAdapter#fillBulk", message, new IllegalArgumentException(message));
        }
        bulk.photoInfos = photoInfos;
    }

    private PhotoInfosBulk getCleanBulk() {
        if (this.bulksRecycleBuffer.isEmpty()) {
            return new PhotoInfosBulk();
        }
        PhotoInfosBulk bulk = (PhotoInfosBulk) this.bulksRecycleBuffer.remove(0);
        bulk.reset();
        return bulk;
    }

    public int getCount() {
        return this.bulks.size();
    }

    public PhotoInfosBulk getItem(int position) {
        return (PhotoInfosBulk) this.bulks.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        notifyPosition(position);
        PhotoTilesRowView rowView = (PhotoTilesRowView) convertView;
        if (rowView == null) {
            rowView = new PhotoTilesRowView(this.context, this.tileSize);
            rowView.setOnPhotoTileClickListener(this.onPhotoTileClickListener);
        }
        PhotoInfosBulk bulk = getItem(position);
        rowView.update(this.columnsCount, bulk.bigPhotoInfo, this.deletedItems, this.darkenBlocked, bulk.photoInfos);
        return rowView;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public int getItemViewType(int position) {
        return ((PhotoInfosBulk) this.bulks.get(position)).hasBigPhoto() ? 1 : 0;
    }

    public int getColumnsCount() {
        return this.columnsCount;
    }

    public void setColumnsCount(int columnsCount) {
        this.columnsCount = columnsCount;
        notifyDataSetChanged();
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }

    public int getBulkPositionForPhotoId(String photoId) {
        int i;
        int bulkPosition = LinearLayoutManager.INVALID_OFFSET;
        int itemPosition = LinearLayoutManager.INVALID_OFFSET;
        int size = this.items.size();
        for (i = 0; i < size; i++) {
            if (TextUtils.equals(photoId, ((PhotoInfo) this.items.get(i)).getId())) {
                itemPosition = i;
                break;
            }
        }
        if (itemPosition != LinearLayoutManager.INVALID_OFFSET) {
            size = this.bulks.size();
            for (i = 0; i < size; i++) {
                PhotoInfosBulk bulk = (PhotoInfosBulk) this.bulks.get(i);
                if (itemPosition >= bulk.startPos && itemPosition <= bulk.endPos) {
                    bulkPosition = i;
                }
            }
        }
        return bulkPosition;
    }

    public final PhotoInfo[] getPhotoInfosForPosition(int position) {
        if (position >= this.bulks.size()) {
            return null;
        }
        return ((PhotoInfosBulk) this.bulks.get(position)).photoInfos;
    }

    private void notifyPosition(int position) {
        if (this.onNearListEndListener != null && getCount() - position == 4) {
            this.onNearListEndListener.onNearListEnd();
        }
    }

    public final List<PhotoInfo> getPhotoInfos() {
        return this.items;
    }

    public final void setOnPhotoTileClickListener(OnPhotoTileClickListener onPhotoTileClickListener) {
        this.onPhotoTileClickListener = onPhotoTileClickListener;
    }

    public void setOnNearListEndListener(OnNearListEndListener onNearListEndListener) {
        this.onNearListEndListener = onNearListEndListener;
    }

    static {
        photoInfosBulkInstanceCount = 0;
        instanceCount = 0;
    }

    private void dumpItems(String prefix) {
        if (Logger.isLoggingEnable()) {
            int i;
            int bulkCount = this.bulks == null ? 0 : this.bulks.size();
            for (i = 0; i < bulkCount; i++) {
                Logger.m173d("(%d) %s bulk[%d]=%s", Integer.valueOf(this.instanceId), prefix, Integer.valueOf(i), this.bulks.get(i));
            }
            int itemCount = this.items == null ? 0 : this.items.size();
            for (i = 0; i < itemCount; i++) {
                Logger.m173d("(%d) %s item[%d]=%s", Integer.valueOf(this.instanceId), prefix, Integer.valueOf(i), this.items.get(i));
            }
        }
    }
}
