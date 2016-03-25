package ru.ok.model.stream;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.mediatopics.MediaReshareItem;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.stream.banner.Banner;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedAlbumEntity;
import ru.ok.model.stream.entities.FeedBannerEntity;
import ru.ok.model.stream.entities.FeedGroupEntity;
import ru.ok.model.stream.entities.FeedHolidayEntity;
import ru.ok.model.stream.entities.FeedUserEntity;
import ru.ok.model.stream.message.FeedEntitySpan;
import ru.ok.model.stream.message.FeedMessage;
import ru.ok.model.stream.message.FeedMessageSpan;

public final class FeedUtils {
    public static List<? extends BaseEntity> getPhotos(Feed feed) {
        if (feed == null) {
            return Collections.emptyList();
        }
        if ((feed.getPattern() == 5 || feed.getPattern() == 7) && (feed.getTargetTypesMask() & 1) == 1) {
            return feed.getTargets();
        }
        if (feed.getPattern() == 6 && (feed.getPlaceTypesMask() & 1) == 1) {
            return feed.getPlaces();
        }
        return Collections.emptyList();
    }

    public static List<? extends BaseEntity> getFriends(Feed feed) {
        if (feed != null && feed.getPattern() == 2 && (feed.getFriendTypesMask() & 8) == 8) {
            return feed.getFriends();
        }
        return Collections.emptyList();
    }

    public static BaseEntity findFirstOwner(Feed feed) {
        if (feed != null) {
            List<? extends BaseEntity> owners = feed.getOwners();
            if (owners != null && owners.size() > 0) {
                return (BaseEntity) owners.get(0);
            }
        }
        return null;
    }

    public static BaseEntity findFirstAuthor(Feed feed) {
        if (feed != null) {
            List<? extends BaseEntity> authors = feed.getAuthors();
            if (authors != null && authors.size() > 0) {
                return (BaseEntity) authors.get(0);
            }
        }
        return null;
    }

    public static FeedHolidayEntity findFirstHoliday(Feed feed) {
        if (feed != null) {
            List<? extends BaseEntity> holidays = feed.getHolidays();
            if (!(holidays == null || holidays.isEmpty())) {
                for (BaseEntity holiday : holidays) {
                    if (holiday.getType() == 23) {
                        return (FeedHolidayEntity) holiday;
                    }
                }
            }
        }
        return null;
    }

    public static FeedAlbumEntity findFirstPhotoAlbum(Feed feed) {
        if (feed != null) {
            List<? extends BaseEntity> places = feed.getPlaces();
            if (places != null) {
                for (BaseEntity place : places) {
                    if (place.getType() == 8) {
                        return (FeedAlbumEntity) place;
                    }
                }
            }
        }
        return null;
    }

    public static List<GeneralUserInfo> getFeedHeaderAvatars(Feed feed, boolean authorInHeader) {
        FeedMessage title;
        ArrayList<FeedMessageSpan> spans;
        int i;
        int size;
        FeedMessageSpan span;
        FeedEntitySpan feedEntitySpan;
        List<GeneralUserInfo> usersInfo = new ArrayList();
        boolean isContent = feed.getPattern() == 5 || feed.getPattern() == 7;
        if (isContent && feed.hasDataFlag(8)) {
            List<? extends BaseEntity> pins = feed.getPins();
            title = feed.getTitle();
            if (title != null) {
                spans = title.getSpans();
                if (spans != null) {
                    i = 0;
                    size = spans.size();
                    while (i < size) {
                        span = (FeedMessageSpan) spans.get(i);
                        if (span instanceof FeedEntitySpan) {
                            feedEntitySpan = (FeedEntitySpan) span;
                            if (contains(pins, feedEntitySpan.getEntityType(), feedEntitySpan.getEntityId())) {
                                entitiesToUsers(pins, usersInfo);
                            }
                        } else {
                            i++;
                        }
                    }
                }
            }
        }
        if (feed.getPattern() == 4) {
            title = feed.getTitle();
            if (title != null) {
                spans = title.getSpans();
                if (spans != null) {
                    size = spans.size();
                    for (i = 0; i < size; i++) {
                        span = (FeedMessageSpan) spans.get(i);
                        if (span instanceof FeedEntitySpan) {
                            feedEntitySpan = (FeedEntitySpan) span;
                            int type = feedEntitySpan.getEntityType();
                            String id = feedEntitySpan.getEntityId();
                            if (contains(feed.getReceivers(), type, id)) {
                                entitiesToUsers(feed.getReceivers(), usersInfo);
                                break;
                            } else if (contains(feed.getSenders(), type, id)) {
                                entitiesToUsers(feed.getSenders(), usersInfo);
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (isContent && feed.hasDataFlag(1)) {
            entitiesToUsers(authorInHeader ? feed.getAuthors() : feed.getOwners(), usersInfo);
        }
        if (usersInfo.isEmpty()) {
            entitiesToUsers(feed.getActors(), usersInfo);
        }
        if (usersInfo.isEmpty()) {
            entitiesToUsers(feed.getOwners(), usersInfo);
        }
        if (usersInfo.isEmpty()) {
            entitiesToUsers(feed.getFeedOwners(), usersInfo);
        }
        return usersInfo;
    }

    private static void entitiesToUsers(List<? extends BaseEntity> entities, List<GeneralUserInfo> outUsers) {
        for (BaseEntity entity : entities) {
            if (entity instanceof FeedUserEntity) {
                outUsers.add(((FeedUserEntity) entity).getUserInfo());
            } else if (entity instanceof FeedGroupEntity) {
                outUsers.add(((FeedGroupEntity) entity).getGroupInfo());
            }
        }
    }

    public static ArrayList<FeedUserEntity> asFeedUserEntities(List<? extends BaseEntity> entities) {
        ArrayList<FeedUserEntity> out = new ArrayList();
        for (BaseEntity entity : entities) {
            if (entity instanceof FeedUserEntity) {
                out.add((FeedUserEntity) entity);
            }
        }
        return out;
    }

    public static BaseEntity findReshareOwner(MediaReshareItem item) {
        List<BaseEntity> owners = item.getReshareOwners();
        if (owners == null || owners.isEmpty()) {
            return null;
        }
        return (BaseEntity) owners.get(0);
    }

    public static boolean contains(Collection<? extends BaseEntity> entities, int type, String id) {
        if (entities == null) {
            return false;
        }
        for (BaseEntity entity : entities) {
            if (entity.getType() == type && TextUtils.equals(entity.getId(), id)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchesFeedOwner(@NonNull Feed feed, @Nullable ArrayList<String> userIds, @Nullable ArrayList<String> groupIds) {
        ArrayList<? extends BaseEntity> feedOwners = feed.getFeedOwners();
        for (int i = feedOwners.size() - 1; i >= 0; i--) {
            BaseEntity owner = (BaseEntity) feedOwners.get(i);
            int ownerEntityType = owner.getType();
            if (ownerEntityType == 7) {
                if (userIds != null && userIds.contains(owner.getId())) {
                    return true;
                }
            } else if (ownerEntityType == 2 && groupIds != null && groupIds.contains(owner.getId())) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static BaseEntity getPromoOwner(@NonNull Feed feed) {
        if (feed.getPattern() != 7) {
            return null;
        }
        ArrayList<? extends BaseEntity> banners = feed.getBanners();
        if (banners == null || banners.size() == 0) {
            return null;
        }
        BaseEntity bannerEntity = (BaseEntity) banners.get(0);
        if (!(bannerEntity instanceof FeedBannerEntity)) {
            return null;
        }
        Banner banner = ((FeedBannerEntity) bannerEntity).getBanner();
        if (banner == null || banner.template != 6) {
            return null;
        }
        ArrayList<? extends BaseEntity> owners = feed.getOwners();
        if (owners.size() != 0) {
            return (BaseEntity) owners.get(0);
        }
        return null;
    }

    @NonNull
    public static List<PhotoInfo> getPhotoInfos(@NonNull Collection<? extends BaseEntity> entities) {
        ArrayList<PhotoInfo> photoInfos = new ArrayList();
        for (BaseEntity photoEntity : entities) {
            if (photoEntity instanceof AbsFeedPhotoEntity) {
                photoInfos.add(((AbsFeedPhotoEntity) photoEntity).getPhotoInfo());
            }
        }
        return photoInfos;
    }

    @Nullable
    public static GeneralUserInfo getUserInfoFromEntity(BaseEntity baseEntity) {
        if (baseEntity == null) {
            return null;
        }
        if (baseEntity.getType() == 7) {
            return ((FeedUserEntity) baseEntity).getUserInfo();
        }
        if (baseEntity.getType() == 2) {
            return ((FeedGroupEntity) baseEntity).getGroupInfo();
        }
        return null;
    }
}
