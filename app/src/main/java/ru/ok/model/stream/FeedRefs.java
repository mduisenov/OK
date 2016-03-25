package ru.ok.model.stream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import ru.ok.android.utils.Logger;

class FeedRefs<T> {
    final ArrayList<T>[] refs;

    FeedRefs() {
        this.refs = new ArrayList[10];
    }

    void add(int position, T obj) {
        ArrayList<T>[] allrefs = this.refs;
        ArrayList<T> refs = allrefs[position];
        if (refs == null) {
            refs = new ArrayList();
            allrefs[position] = refs;
        }
        refs.add(obj);
    }

    void set(int position, ArrayList<T> refs) {
        this.refs[position] = refs;
    }

    ArrayList<T> getRefs(int position) {
        return this.refs[position];
    }

    <S> void resolve(Map<T, S> map, FeedRefs<S> out, Map<T, S> outMap) {
        ArrayList<T>[] srcRefs = this.refs;
        ArrayList<S>[] targetRefs = out.refs;
        if (outMap != null) {
            outMap.clear();
        }
        for (int i = 0; i < 10; i++) {
            ArrayList<T> refs = srcRefs[i];
            if (refs == null) {
                targetRefs[i] = null;
            } else {
                ArrayList<S> outRefs = targetRefs[i];
                if (outRefs == null) {
                    outRefs = new ArrayList();
                    targetRefs[i] = outRefs;
                } else {
                    outRefs.clear();
                }
                Iterator i$ = refs.iterator();
                while (i$.hasNext()) {
                    T key = i$.next();
                    S value = map.get(key);
                    if (value == null) {
                        Logger.m185w("Not resolved: %s", key);
                    } else {
                        Logger.m173d("Resolved: %s --> %s", key, value);
                        outRefs.add(value);
                        if (outMap != null) {
                            outMap.put(key, value);
                        }
                    }
                }
            }
        }
    }
}
