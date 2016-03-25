package ru.ok.android.ui.image.crop.gallery;

import android.net.Uri;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class ImageListUber implements IImageList {
    private int mLastListIndex;
    private final PriorityQueue<MergeSlot> mQueue;
    private int[] mSkipCounts;
    private long[] mSkipList;
    private int mSkipListSize;
    private final IImageList[] mSubList;

    private static class AscendingComparator implements Comparator<MergeSlot> {
        private AscendingComparator() {
        }

        public int compare(MergeSlot m1, MergeSlot m2) {
            if (m1.mDateTaken != m2.mDateTaken) {
                return m1.mDateTaken < m2.mDateTaken ? -1 : 1;
            } else {
                return m1.mListIndex - m2.mListIndex;
            }
        }
    }

    private static class DescendingComparator implements Comparator<MergeSlot> {
        private DescendingComparator() {
        }

        public int compare(MergeSlot m1, MergeSlot m2) {
            if (m1.mDateTaken != m2.mDateTaken) {
                return m1.mDateTaken < m2.mDateTaken ? 1 : -1;
            } else {
                return m1.mListIndex - m2.mListIndex;
            }
        }
    }

    private static class MergeSlot {
        long mDateTaken;
        IImage mImage;
        private final IImageList mList;
        int mListIndex;
        private int mOffset;

        public MergeSlot(IImageList list, int index) {
            this.mOffset = -1;
            this.mList = list;
            this.mListIndex = index;
        }

        public boolean next() {
            if (this.mOffset >= this.mList.getCount() - 1) {
                return false;
            }
            IImageList iImageList = this.mList;
            int i = this.mOffset + 1;
            this.mOffset = i;
            this.mImage = iImageList.getImageAt(i);
            this.mDateTaken = this.mImage.getDateTaken();
            return true;
        }
    }

    public ImageListUber(IImageList[] sublist, int sort) {
        Comparator ascendingComparator;
        this.mSubList = (IImageList[]) sublist.clone();
        if (sort == 1) {
            ascendingComparator = new AscendingComparator();
        } else {
            ascendingComparator = new DescendingComparator();
        }
        this.mQueue = new PriorityQueue(4, ascendingComparator);
        this.mSkipList = new long[16];
        this.mSkipListSize = 0;
        this.mSkipCounts = new int[this.mSubList.length];
        this.mLastListIndex = -1;
        this.mQueue.clear();
        int n = this.mSubList.length;
        for (int i = 0; i < n; i++) {
            MergeSlot slot = new MergeSlot(this.mSubList[i], i);
            if (slot.next()) {
                this.mQueue.add(slot);
            }
        }
    }

    public int getCount() {
        int count = 0;
        for (IImageList subList : this.mSubList) {
            count += subList.getCount();
        }
        return count;
    }

    public IImage getImageAt(int index) {
        if (index < 0 || index > getCount()) {
            throw new IndexOutOfBoundsException("index " + index + " out of range max is " + getCount());
        }
        MergeSlot slot;
        Arrays.fill(this.mSkipCounts, 0);
        int skipCount = 0;
        int n = this.mSkipListSize;
        for (int i = 0; i < n; i++) {
            long v = this.mSkipList[i];
            int offset = (int) (-1 & v);
            int which = (int) (v >> 32);
            if (skipCount + offset > index) {
                return this.mSubList[which].getImageAt(this.mSkipCounts[which] + (index - skipCount));
            }
            skipCount += offset;
            int[] iArr = this.mSkipCounts;
            iArr[which] = iArr[which] + offset;
        }
        while (true) {
            slot = nextMergeSlot();
            if (slot == null) {
                return null;
            }
            if (skipCount == index) {
                break;
            }
            if (slot.next()) {
                this.mQueue.add(slot);
            }
            skipCount++;
        }
        IImage result = slot.mImage;
        if (!slot.next()) {
            return result;
        }
        this.mQueue.add(slot);
        return result;
    }

    private MergeSlot nextMergeSlot() {
        MergeSlot slot = (MergeSlot) this.mQueue.poll();
        if (slot == null) {
            return null;
        }
        if (slot.mListIndex == this.mLastListIndex) {
            int lastIndex = this.mSkipListSize - 1;
            long[] jArr = this.mSkipList;
            jArr[lastIndex] = jArr[lastIndex] + 1;
            return slot;
        }
        this.mLastListIndex = slot.mListIndex;
        if (this.mSkipList.length == this.mSkipListSize) {
            long[] temp = new long[(this.mSkipListSize * 2)];
            System.arraycopy(this.mSkipList, 0, temp, 0, this.mSkipListSize);
            this.mSkipList = temp;
        }
        jArr = this.mSkipList;
        int i = this.mSkipListSize;
        this.mSkipListSize = i + 1;
        jArr[i] = (((long) this.mLastListIndex) << 32) | 1;
        return slot;
    }

    public IImage getImageForUri(Uri uri) {
        for (IImageList sublist : this.mSubList) {
            IImage image = sublist.getImageForUri(uri);
            if (image != null) {
                return image;
            }
        }
        return null;
    }

    public void close() {
        for (IImageList close : this.mSubList) {
            close.close();
        }
    }
}
