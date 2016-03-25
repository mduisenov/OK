package ru.ok.android.utils;

import java.util.Iterator;
import java.util.TreeSet;
import ru.ok.model.photo.PhotoSize;

public class PhotoUtil {
    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static ru.ok.model.photo.PhotoSize getClosestSize(int r4, int r5, java.util.TreeSet<ru.ok.model.photo.PhotoSize> r6) {
        /*
        r0 = 0;
        r1 = r6.iterator();
    L_0x0005:
        r3 = r1.hasNext();
        if (r3 == 0) goto L_0x001f;
    L_0x000b:
        r2 = r1.next();
        r2 = (ru.ok.model.photo.PhotoSize) r2;
        r3 = r2.getWidth();
        if (r3 < r4) goto L_0x001f;
    L_0x0017:
        r3 = r2.getHeight();
        if (r3 < r5) goto L_0x001f;
    L_0x001d:
        r0 = r2;
        goto L_0x0005;
    L_0x001f:
        if (r0 != 0) goto L_0x0031;
    L_0x0021:
        r3 = r6.isEmpty();
        if (r3 != 0) goto L_0x0031;
    L_0x0027:
        r3 = r6.iterator();
        r0 = r3.next();
        r0 = (ru.ok.model.photo.PhotoSize) r0;
    L_0x0031:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.utils.PhotoUtil.getClosestSize(int, int, java.util.TreeSet):ru.ok.model.photo.PhotoSize");
    }

    public static PhotoSize getClosestSquaredSize(int width, TreeSet<PhotoSize> sizes) {
        PhotoSize closest = null;
        Iterator i$ = sizes.iterator();
        while (i$.hasNext()) {
            PhotoSize size = (PhotoSize) i$.next();
            if (!size.getJsonKey().contains("min") && size.getWidth() >= width && size.getHeight() == size.getWidth()) {
                if (closest == null || size.getWidth() < closest.getWidth()) {
                    closest = size;
                }
            }
        }
        if (closest == null) {
            return getClosestSize(width, width, sizes);
        }
        return closest;
    }
}
