package ru.ok.android.ui.utils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

public final class StickyHeaderItemDecorator extends ItemDecoration {
    private final Adapter adapter;
    private final SparseBooleanArray blockStartItems;
    private final SparseArray<CharSequence> cachedHeaders;
    private int firstDataPositionWithHeader;
    private final StickyHeaderCache headersCache;
    private final HeaderViewProvider headersProvider;
    private int sectionsGap;
    private int totalOffset;

    public interface HeaderViewProvider {
        void bindHeaderView(ViewHolderHeader viewHolderHeader, int i);

        int getAnchorViewId(int i);

        CharSequence getHeader(int i);

        int getHeaderViewType(int i);

        ViewHolderHeader newHeaderView(int i, ViewGroup viewGroup);
    }

    public static class ViewHolderHeader {
        int paddingLeft;
        int paddingTop;
        public final View view;
        int viewType;

        public ViewHolderHeader(View view) {
            this.view = view;
        }
    }

    /* renamed from: ru.ok.android.ui.utils.StickyHeaderItemDecorator.1 */
    class C13501 extends AdapterDataObserver {
        C13501() {
        }

        public void onChanged() {
            super.onChanged();
            StickyHeaderItemDecorator.this.blockStartItems.clear();
            StickyHeaderItemDecorator.this.cachedHeaders.clear();
            StickyHeaderItemDecorator.this.firstDataPositionWithHeader = Integer.MAX_VALUE;
        }
    }

    public StickyHeaderItemDecorator(RecyclerView recyclerView, Adapter adapter, HeaderViewProvider headersProvider) {
        this.blockStartItems = new SparseBooleanArray();
        this.cachedHeaders = new SparseArray();
        this.firstDataPositionWithHeader = Integer.MAX_VALUE;
        this.adapter = adapter;
        this.headersProvider = headersProvider;
        this.headersCache = new StickyHeaderCache(recyclerView, headersProvider);
        adapter.registerAdapterDataObserver(new C13501());
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        int dataPosition = parent.getChildPosition(view);
        if (isStartItem(dataPosition) && getHeader(dataPosition) != null) {
            ViewHolderHeader headerView = this.headersCache.getHeaderView(dataPosition);
            if (dataPosition > this.firstDataPositionWithHeader) {
                outRect.top = this.sectionsGap;
            }
            if (headerView.paddingTop < 0) {
                outRect.top -= headerView.paddingTop;
            }
        }
    }

    public void onDrawOver(Canvas c, RecyclerView parent, State state) {
        super.onDrawOver(c, parent, state);
        int childCount = parent.getChildCount();
        int itemCount = this.adapter.getItemCount();
        if (childCount > 0 && itemCount > 0) {
            Rect outRect = new Rect();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                int dataPosition = parent.getChildPosition(child);
                if (dataPosition != -1 && dataPosition >= 0 && dataPosition < itemCount) {
                    boolean isStartInBlock = isStartItem(dataPosition);
                    getItemOffsets(outRect, child, parent, state);
                    boolean isFakeFirst = child.getTop() - outRect.top <= this.totalOffset && child.getBottom() + outRect.bottom > this.totalOffset;
                    boolean shouldDrawHeader = isFakeFirst || isStartInBlock;
                    if (shouldDrawHeader && getHeader(dataPosition) != null) {
                        int anchorViewId = this.headersProvider.getAnchorViewId(dataPosition);
                        int topAbsolute = 0;
                        int bottomAbsolute = child.getMeasuredHeight();
                        if (anchorViewId != 0) {
                            View anchor = child.findViewById(anchorViewId);
                            if (anchor != null) {
                                topAbsolute = anchor.getTop();
                                bottomAbsolute = anchor.getBottom();
                            }
                        }
                        bottomAbsolute += child.getTop();
                        int headerY = topAbsolute + child.getTop();
                        ViewHolderHeader headerHolder = this.headersCache.getHeaderView(dataPosition);
                        if (isStartInBlock) {
                            headerY += headerHolder.paddingTop;
                        }
                        if (isFakeFirst) {
                            boolean mayPulledUpByNextRow;
                            int i2;
                            if (dataPosition < itemCount - 1) {
                                if (isStartItem(dataPosition + 1)) {
                                    mayPulledUpByNextRow = true;
                                    if (mayPulledUpByNextRow) {
                                        if (!isStartInBlock) {
                                            headerY = this.totalOffset;
                                        } else if (isStartInBlock) {
                                            i2 = this.totalOffset;
                                            if (headerY < r0) {
                                                headerY = this.totalOffset;
                                            }
                                        }
                                    } else if (!isStartInBlock) {
                                        headerY = this.totalOffset;
                                        if (headerHolder.view.getMeasuredHeight() + headerY > bottomAbsolute) {
                                            headerY = bottomAbsolute - headerHolder.view.getMeasuredHeight();
                                        }
                                    }
                                }
                            }
                            mayPulledUpByNextRow = false;
                            if (mayPulledUpByNextRow) {
                                if (!isStartInBlock) {
                                    headerY = this.totalOffset;
                                } else if (isStartInBlock) {
                                    i2 = this.totalOffset;
                                    if (headerY < r0) {
                                        headerY = this.totalOffset;
                                    }
                                }
                            } else if (isStartInBlock) {
                                headerY = this.totalOffset;
                                if (headerHolder.view.getMeasuredHeight() + headerY > bottomAbsolute) {
                                    headerY = bottomAbsolute - headerHolder.view.getMeasuredHeight();
                                }
                            }
                        }
                        drawHeader(c, headerHolder, ((float) headerY) + child.getTranslationY());
                    }
                }
            }
        }
    }

    public void setSectionsGap(int sectionsGap) {
        this.sectionsGap = sectionsGap;
    }

    public boolean isStartItem(int dataPosition) {
        boolean result = true;
        int keyIndex = this.blockStartItems.indexOfKey(dataPosition);
        if (keyIndex >= 0) {
            return this.blockStartItems.valueAt(keyIndex);
        }
        boolean headerIsNull;
        CharSequence header = getHeader(dataPosition);
        if (header == null) {
            headerIsNull = true;
        } else {
            headerIsNull = false;
        }
        if (dataPosition != 0) {
            CharSequence headerPrev = getHeader(dataPosition - 1);
            if (((headerPrev == null ? 1 : 0) ^ headerIsNull) == 0 && (headerIsNull || header.equals(headerPrev))) {
                result = false;
            }
        } else if (headerIsNull) {
            result = false;
        }
        this.blockStartItems.put(dataPosition, result);
        if (!result || dataPosition >= this.firstDataPositionWithHeader) {
            return result;
        }
        this.firstDataPositionWithHeader = dataPosition;
        return result;
    }

    private CharSequence getHeader(int dataPosition) {
        int keyIndex = this.cachedHeaders.indexOfKey(dataPosition);
        if (keyIndex >= 0) {
            return (CharSequence) this.cachedHeaders.valueAt(keyIndex);
        }
        CharSequence header = this.headersProvider.getHeader(dataPosition);
        this.cachedHeaders.put(dataPosition, header);
        return header;
    }

    private void drawHeader(Canvas c, ViewHolderHeader holder, float delta) {
        c.save();
        View view = holder.view;
        c.translate((float) holder.paddingLeft, delta);
        view.draw(c);
        c.restore();
    }

    public void setStickyPermanentOffset(int stickyPermanentOffset) {
        this.totalOffset = stickyPermanentOffset;
    }
}
