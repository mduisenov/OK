package ru.ok.java.api.utils;

import android.os.Parcel;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ObjectUtils {

    public interface ObjectsEqual<T> {
        boolean equal(T t, T t2);
    }

    public interface UncompatibleObjectEquals<T1, T2> {
        boolean equal(T1 t1, T2 t2);
    }

    public static boolean equals(Object o1, Object o2) {
        return (o1 == null && o2 == null) || (o1 != null && o1.equals(o2));
    }

    public static boolean listsEqual(List<?> list1, List<?> list2) {
        return listsEqual(list1, list2, null);
    }

    public static <T> boolean listsEqual(List<? extends T> list1, List<? extends T> list2, ObjectsEqual<T> objEqual) {
        boolean z = true;
        if (list1 == null) {
            if (list2 != null) {
                z = false;
            }
            return z;
        } else if (list2 == null || list1.size() != list2.size()) {
            return false;
        } else {
            Iterator<? extends T> itr1 = list1.iterator();
            Iterator<? extends T> itr2 = list2.iterator();
            while (itr1.hasNext() && itr2.hasNext()) {
                T item1 = itr1.next();
                T item2 = itr2.next();
                if (item1 == null && item2 != null) {
                    return false;
                }
                if (item1 != null && objEqual != null && !objEqual.equal(item1, item2)) {
                    return false;
                }
                if (objEqual == null && !item1.equals(item2)) {
                    return false;
                }
            }
            if (itr1.hasNext() || itr2.hasNext()) {
                return false;
            }
            return true;
        }
    }

    public static <T1, T2> boolean containsAll(List<? extends T1> list1, List<? extends T2> list2, UncompatibleObjectEquals<T1, T2> cmp) {
        if (list1 == null) {
            return false;
        }
        if (list2 == null) {
            return true;
        }
        for (T2 item2 : list2) {
            if (!contains(list1, item2, cmp)) {
                return false;
            }
        }
        return true;
    }

    public static <T1, T2> boolean contains(List<? extends T1> list, T2 obj, UncompatibleObjectEquals<T1, T2> cmp) {
        if (list == null) {
            return false;
        }
        for (T1 item : list) {
            if (item == null) {
                if (obj == null) {
                    return true;
                }
            } else if (cmp.equal(item, obj)) {
                return true;
            }
        }
        return false;
    }

    public static int collectionHashCode(Collection<?> col) {
        int hashCode = 0;
        if (col != null) {
            for (Object o : col) {
                if (o != null) {
                    hashCode += 1239545759 * o.hashCode();
                }
                hashCode += 927411613;
            }
        }
        return hashCode;
    }

    public static <T> boolean setsEqual(Set<? extends T> set1, Set<? extends T> set2) {
        boolean z = true;
        if (set1 == null) {
            if (set2 != null) {
                z = false;
            }
            return z;
        } else if (set2 == null || set1.size() != set2.size()) {
            return false;
        } else {
            for (T obj : set1) {
                if (!set1.contains(obj)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static <T> void writeSet(Set<T> set, Parcel dest) {
        if (set == null) {
            dest.writeInt(-1);
            return;
        }
        dest.writeInt(set.size());
        for (T s : set) {
            dest.writeValue(s);
        }
    }

    public static <T> void readSet(Parcel src, Set<T> set, ClassLoader cl) {
        int size = src.readInt();
        if (size != -1) {
            for (int i = 0; i < size; i++) {
                set.add(src.readValue(cl));
            }
        }
    }

    public static <T> HashSet<T> readHashSet(Parcel src, ClassLoader cl) {
        HashSet<T> set = new HashSet();
        readSet(src, set, cl);
        return set;
    }

    public static <T> Set<T> readUnmodifiableSet(Parcel src, ClassLoader cl) {
        return Collections.unmodifiableSet(readHashSet(src, cl));
    }

    public static Set<String> readUnmodifiableStringSet(Parcel src, ClassLoader cl) {
        return readUnmodifiableSet(src, cl);
    }

    public static <T> Set<T> unmodifiableCopy(Collection<T> col) {
        if (col == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet(col));
    }
}
