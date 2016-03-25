package ru.ok.model.stream.banner;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import ru.ok.model.photo.PhotoSize;

public class Banner {
    public final int actionType;
    public final String ageRestriction;
    public final String clickUrl;
    public final int color;
    public final long db_id;
    public final String deepLink;
    public final String disclaimer;
    public final String header;
    public final int iconType;
    public final String iconUrl;
    public final String iconUrlHd;
    public final String id;
    private final TreeSet<PhotoSize> imageUrls;
    public final String info;
    public final int template;
    public final String text;
    private final String topicId;

    Banner(long db_id, String id, int template, String header, String text, int actionType, int iconType, String iconUrl, String iconUrlHd, List<PhotoSize> imageUrls, String clickUrl, int color, String disclaimer, String info, String ageRestriction, String topicId, String deepLink) {
        this.imageUrls = new TreeSet();
        this.db_id = db_id;
        this.id = id;
        this.template = template;
        this.header = header;
        this.text = text;
        this.actionType = actionType;
        this.iconType = iconType;
        this.iconUrl = iconUrl;
        this.iconUrlHd = iconUrlHd;
        this.imageUrls.addAll(imageUrls);
        this.clickUrl = clickUrl;
        this.color = color;
        this.disclaimer = disclaimer;
        this.info = info;
        this.ageRestriction = ageRestriction;
        this.topicId = topicId;
        this.deepLink = deepLink;
    }

    public PhotoSize getLargestSize() {
        if (this.imageUrls.isEmpty()) {
            return null;
        }
        return (PhotoSize) this.imageUrls.first();
    }

    public final PhotoSize getClosestSize(int width) {
        PhotoSize closest = null;
        Iterator<PhotoSize> it = this.imageUrls.iterator();
        while (it.hasNext()) {
            PhotoSize size = (PhotoSize) it.next();
            if (size.getWidth() < width) {
                break;
            }
            closest = size;
        }
        if (closest == null) {
            return getLargestSize();
        }
        return closest;
    }

    public TreeSet<PhotoSize> getPics() {
        return this.imageUrls;
    }

    public String toString() {
        return this.header;
    }
}
