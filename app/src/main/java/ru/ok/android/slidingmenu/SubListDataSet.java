package ru.ok.android.slidingmenu;

import java.util.ArrayList;
import java.util.List;

class SubListDataSet<TItem> {
    private List<TItem> lastSubList;
    private final List<List<TItem>> subLists;

    SubListDataSet() {
        this.subLists = new ArrayList();
    }

    public void addItem(TItem item) {
        if (this.lastSubList == null) {
            this.lastSubList = new ArrayList();
            this.subLists.add(this.lastSubList);
        }
        this.lastSubList.add(item);
    }

    public void addSubList(List<TItem> subList) {
        this.subLists.add(subList);
        this.lastSubList = null;
    }

    public int getCount() {
        int count = 0;
        for (List subList : this.subLists) {
            count += subList.size();
        }
        return count;
    }

    public TItem getItem(int position) {
        int overallPosition = 0;
        for (List<TItem> subList : this.subLists) {
            if (position < subList.size() + overallPosition) {
                return subList.get(position - overallPosition);
            }
            overallPosition += subList.size();
        }
        return null;
    }
}
