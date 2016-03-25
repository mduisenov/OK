package ru.ok.android.ui.stream.list;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.app.GifAsMp4PlayerHelper.AutoplayContext;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.pagination.impl.ItemIdPageAnchor;
import ru.ok.android.model.pagination.impl.PhotoInfoPage;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.TwoPhotoCollageItem.OrientationDecoratorLocate;
import ru.ok.android.ui.stream.list.TwoPhotoCollageItem.PhotoLocate;
import ru.ok.android.ui.stream.list.TwoPhotoCollageItem.WidthAspectRatioPhotoLocate;
import ru.ok.android.ui.stream.list.TwoPhotoCollageItem.WidthAspectRatioWithMarginLocate;
import ru.ok.android.ui.stream.list.TwoPhotoCollageItem.WidthHeightWithMarginLocate;
import ru.ok.android.ui.stream.view.FeedFooterInfo;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.LikeInfoContext;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;
import ru.ok.model.stream.entities.BaseEntity;

public class PhotoCollageStreamItemBinder {
    public static int addPhotoItemWithCollage(Context context, Feed2StreamItemBinder binder, FeedWithState feedWithState, List<? extends BaseEntity> photos, boolean needDividerBefore, int orderInFeed, boolean isLastBlock, boolean[] outShowMore, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> outFooterInfo) {
        ArrayList<AbsFeedPhotoEntity> photoList = new ArrayList(photos.size());
        ArrayList<PhotoInfo> arrayList = new ArrayList(photos.size());
        int border = context.getResources().getDimensionPixelOffset(2131230997);
        int onePhotoMargin = context.getResources().getDimensionPixelOffset(2131230984);
        int startFeedOrder = orderInFeed;
        for (BaseEntity photo : photos) {
            if (photo instanceof AbsFeedPhotoEntity) {
                AbsFeedPhotoEntity photoEntity = (AbsFeedPhotoEntity) photo;
                photoList.add(photoEntity);
                arrayList.add(photoEntity.getPhotoInfo());
            }
        }
        if (photoList.isEmpty()) {
            return 0;
        }
        PhotoInfoPage photoInfoPage = new PhotoInfoPage((List) arrayList, new ItemIdPageAnchor(((PhotoInfo) arrayList.get(0)).getId(), ((PhotoInfo) arrayList.get(arrayList.size() - 1)).getId()));
        DeviceLayoutType deviceType = DeviceUtils.getType(context);
        boolean isTablet = deviceType == DeviceLayoutType.LARGE || deviceType == DeviceLayoutType.BIG;
        float aspectRatio = isTablet ? 1.0f : DeviceUtils.getDeviceAspectRatio();
        int minWidth = DeviceUtils.getStreamHighQualityPhotoWidth();
        float sizeCoef = (float) (OdnoklassnikiApplication.getContext().getResources().getDimensionPixelSize(2131230982) / 120);
        int minSize = context.getResources().getDimensionPixelSize(2131230983);
        switch (reviewPhotos(photoList, isTablet, aspectRatio, minWidth, sizeCoef)) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                orderInFeed += addTwoPhotoCollageItem(binder, feedWithState, needDividerBefore, orderInFeed, isLastBlock, outShowMore, outItems, photoList, border, photoInfoPage);
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                orderInFeed += addThreePhotoCollage(binder, feedWithState, needDividerBefore, orderInFeed, isLastBlock, outShowMore, outItems, photoList, border, photoInfoPage);
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                orderInFeed += addDoubleTwoPhotoCollage(binder, feedWithState, needDividerBefore, orderInFeed, outItems, photoList, isLastBlock, outShowMore, border, photoInfoPage);
                break;
            default:
                addOnePhotoWithLikesAndOthers(binder, feedWithState, needDividerBefore, orderInFeed, isLastBlock, outShowMore, outItems, outFooterInfo, photoList, onePhotoMargin, minWidth, sizeCoef, minSize, isTablet, photoInfoPage);
                break;
        }
        return orderInFeed - startFeedOrder;
    }

    private static int addOnePhotoWithLikesAndOthers(Feed2StreamItemBinder binder, FeedWithState feedWithState, boolean needDividerBefore, int orderInFeed, boolean isLastBlock, boolean[] outShowMore, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> outFooterInfo, ArrayList<AbsFeedPhotoEntity> photoList, int onePhotoMargin, int minWidth, float sizeCoef, int minSize, boolean isTablet, PhotoInfoPage photoInfoPage) {
        AbsFeedPhotoEntity photo = (AbsFeedPhotoEntity) photoList.get(0);
        int startOrder = orderInFeed;
        if (photoList.size() == 1) {
            String comment = photo.getPhotoInfo().getComment();
            if (!TextUtils.isEmpty(comment)) {
                orderInFeed += binder.addItemWithOptionalDivider(new StreamTextItem(feedWithState, comment, new PhotoClickAction(feedWithState, photo, null)), false, outItems, orderInFeed);
            }
        }
        orderInFeed += addOnePhotoItem(binder, feedWithState, photo, null, photoInfoPage, needDividerBefore, orderInFeed, outItems, onePhotoMargin, minWidth, sizeCoef, minSize, isTablet);
        if (photoList.size() <= 1) {
            LikeInfoContext likeInfo = photo.getLikeInfo();
            DiscussionSummary discussionSummary = photo.getDiscussionSummary();
            if (!(outFooterInfo == null || (likeInfo == null && discussionSummary == null))) {
                outFooterInfo.set(new FeedFooterInfo(feedWithState, likeInfo, discussionSummary, null));
            }
        } else if (isLastBlock) {
            outShowMore[0] = true;
        } else {
            orderInFeed += binder.addStreamItem(outItems, getThreeDotsItem(feedWithState, (AbsFeedPhotoEntity) photoList.get(1), photoInfoPage), orderInFeed);
        }
        return orderInFeed - startOrder;
    }

    private static int addTwoPhotoCollageItem(Feed2StreamItemBinder binder, FeedWithState feedWithState, boolean needDividerBefore, int orderInFeed, boolean isLastBlock, boolean[] outShowMore, List<StreamItem> outItems, ArrayList<AbsFeedPhotoEntity> photoList, int border, PhotoInfoPage photoInfoPage) {
        int startOrder = orderInFeed;
        orderInFeed += binder.addItemWithOptionalDivider(getTwoCollageItem(feedWithState, (AbsFeedPhotoEntity) photoList.get(0), (AbsFeedPhotoEntity) photoList.get(1), border, photoInfoPage), needDividerBefore, outItems, orderInFeed);
        if (photoList.size() > 2) {
            if (isLastBlock) {
                outShowMore[0] = true;
            } else {
                orderInFeed += binder.addStreamItem(outItems, getThreeDotsItem(feedWithState, (AbsFeedPhotoEntity) photoList.get(2), photoInfoPage), orderInFeed);
            }
        }
        return orderInFeed - startOrder;
    }

    private static int addThreePhotoCollage(Feed2StreamItemBinder binder, FeedWithState feedWithState, boolean needDividerBefore, int orderInFeed, boolean isLastBlock, boolean[] outShowMore, List<StreamItem> outItems, ArrayList<AbsFeedPhotoEntity> photoList, int border, PhotoInfoPage photoInfoPage) {
        int startOrder = orderInFeed;
        StreamItem onePhotoItem = getOnePhotoCollageItem(feedWithState, (AbsFeedPhotoEntity) photoList.get(0), 0, photoInfoPage);
        orderInFeed += binder.addItemWithOptionalDivider(onePhotoItem, needDividerBefore, outItems, orderInFeed);
        orderInFeed += binder.addStreamItem(outItems, new StreamVSpaceItem(feedWithState, null, border), orderInFeed);
        orderInFeed += binder.addStreamItem(outItems, getTwoCollageItem(feedWithState, (AbsFeedPhotoEntity) photoList.get(1), (AbsFeedPhotoEntity) photoList.get(2), border, photoInfoPage), orderInFeed);
        if (photoList.size() > 3) {
            if (isLastBlock) {
                outShowMore[0] = true;
            } else {
                orderInFeed += binder.addStreamItem(outItems, getThreeDotsItem(feedWithState, (AbsFeedPhotoEntity) photoList.get(3), photoInfoPage), orderInFeed);
            }
        }
        return orderInFeed - startOrder;
    }

    private static int reviewPhotos(ArrayList<AbsFeedPhotoEntity> photoList, boolean isTablet, float aspectRatio, int minWidth, float sizeCoef) {
        int photosCount = photoList.size();
        if (photosCount < 2) {
            return photosCount;
        }
        int numberOfPhotosInCollage = 0;
        int numberOfReviewedPhotos = Math.min(photoList.size(), 4);
        int i = 0;
        while (true) {
            int j = i + 1;
            if (j < numberOfReviewedPhotos) {
                switch (canUsePhotosInCollage(i, j, photoList, isTablet, aspectRatio, minWidth, sizeCoef)) {
                    case RecyclerView.NO_POSITION /*-1*/:
                        break;
                    case RECEIVED_VALUE:
                        numberOfPhotosInCollage += 2;
                        continue;
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        numberOfPhotosInCollage += 2;
                        break;
                    default:
                        break;
                }
            }
            if (numberOfPhotosInCollage > 3 || photosCount < 3) {
                return numberOfPhotosInCollage;
            }
            PhotoInfo firstPhotoInfo = ((AbsFeedPhotoEntity) photoList.get(0)).getPhotoInfo();
            int width = firstPhotoInfo.getStandartWidth();
            float imageAspectRatio = ((float) width) / ((float) firstPhotoInfo.getStandartHeight());
            if (((float) minWidth) * 1.0f > ((float) ((int) (((float) width) * sizeCoef)))) {
                return numberOfPhotosInCollage;
            }
            if ((isTablet || imageAspectRatio < aspectRatio) && (!isTablet || imageAspectRatio < 2.0f * aspectRatio)) {
                return numberOfPhotosInCollage;
            }
            switch (canUsePhotosInCollage(1, 2, photoList, isTablet, aspectRatio, minWidth, sizeCoef)) {
                case RECEIVED_VALUE:
                    return 3;
                default:
                    return numberOfPhotosInCollage;
            }
            i += 2;
        }
    }

    private static int canUsePhotosInCollage(int i, int j, ArrayList<AbsFeedPhotoEntity> photoList, boolean isTablet, float aspectRatio, int minWidth, float sizeCoef) {
        PhotoInfo info = ((AbsFeedPhotoEntity) photoList.get(i)).getPhotoInfo();
        int w = info.getStandartWidth();
        int h = info.getStandartHeight();
        int hSize = (int) (((float) h) * sizeCoef);
        PhotoInfo info1 = ((AbsFeedPhotoEntity) photoList.get(j)).getPhotoInfo();
        int w1 = info1.getStandartWidth();
        int h1 = info1.getStandartHeight();
        int h1Size = (int) (((float) h1) * sizeCoef);
        float f = (float) w;
        f = (float) h;
        float collageAspectRatio = ((((float) w1) * (((float) h) / ((float) h1))) + r0) / r0;
        float scaledHeight = ((float) ((int) (((float) minWidth) / collageAspectRatio))) * 1.0f;
        if (collageAspectRatio >= aspectRatio && collageAspectRatio <= 5.0f) {
            if (scaledHeight <= ((float) hSize)) {
                if (scaledHeight <= ((float) h1Size)) {
                    if (!isTablet || collageAspectRatio >= 2.0f * aspectRatio) {
                        return 0;
                    }
                    return i == 0 ? 1 : -1;
                }
            }
        }
        return -1;
    }

    private static int addDoubleTwoPhotoCollage(Feed2StreamItemBinder binder, FeedWithState feedWithState, boolean needDividerBefore, int orderInFeed, List<StreamItem> outItems, ArrayList<AbsFeedPhotoEntity> photoList, boolean isLastBlock, boolean[] outShowMore, int border, PhotoInfoPage photoInfoPage) {
        int startOrder = orderInFeed;
        TwoPhotoCollageItem firstTwoCollage = getTwoCollageItem(feedWithState, (AbsFeedPhotoEntity) photoList.get(0), (AbsFeedPhotoEntity) photoList.get(1), border, photoInfoPage);
        orderInFeed += binder.addItemWithOptionalDivider(firstTwoCollage, needDividerBefore, outItems, orderInFeed);
        orderInFeed += binder.addStreamItem(outItems, new StreamVSpaceItem(feedWithState, null, border), orderInFeed);
        orderInFeed += binder.addStreamItem(outItems, getTwoCollageItem(feedWithState, (AbsFeedPhotoEntity) photoList.get(2), (AbsFeedPhotoEntity) photoList.get(3), border, photoInfoPage), orderInFeed);
        if (photoList.size() > 4) {
            if (isLastBlock) {
                outShowMore[0] = true;
            } else {
                orderInFeed += binder.addStreamItem(outItems, getThreeDotsItem(feedWithState, (AbsFeedPhotoEntity) photoList.get(4), photoInfoPage), orderInFeed);
            }
        }
        return orderInFeed - startOrder;
    }

    private static StreamItem getThreeDotsItem(FeedWithState feedWithState, AbsFeedPhotoEntity photoEntity, PhotoInfoPage photoInfoPage) {
        return new StreamThreeDotsItem(feedWithState, new PhotoClickAction(feedWithState, photoEntity, null), photoInfoPage);
    }

    private static OnePhotoCollageItem getOnePhotoCollageItem(FeedWithState feedWithState, AbsFeedPhotoEntity photo, int border, PhotoInfoPage photoInfoPage) {
        return new OnePhotoCollageItem(feedWithState, 3, new PhotoCollagePart(feedWithState, photo, new WidthAspectRatioWithMarginLocate(((float) photo.getPhotoInfo().getStandartWidth()) / ((float) photo.getPhotoInfo().getStandartHeight()), 1.0f, border, border), null, 1), photoInfoPage);
    }

    private static TwoPhotoCollageItem getTwoCollageItem(FeedWithState feedWithState, AbsFeedPhotoEntity photo, AbsFeedPhotoEntity photo1, int border, PhotoInfoPage photoInfoPage) {
        int w = photo.getPhotoInfo().getStandartWidth();
        int h = photo.getPhotoInfo().getStandartHeight();
        int w1 = photo1.getPhotoInfo().getStandartWidth();
        int h1 = photo1.getPhotoInfo().getStandartHeight();
        float h_h1 = ((float) h) / ((float) h1);
        return new TwoPhotoCollageItem(feedWithState, new PhotoCollagePart(feedWithState, photo, new WidthAspectRatioPhotoLocate(((float) w) / ((float) h), ((float) w) / (((float) w) + (((float) w1) * h_h1)), border), null, 2), new PhotoCollagePart(feedWithState, photo1, new WidthAspectRatioPhotoLocate(((float) w1) / ((float) h1), (((float) w1) * h_h1) / (((float) w) + (((float) w1) * h_h1)), border), null, 2), photoInfoPage);
    }

    private static int addOnePhotoItem(Feed2StreamItemBinder binder, FeedWithState feedWithState, AbsFeedPhotoEntity photo, @Nullable MediaItemPhoto itemPhoto, @Nullable PhotoInfoPage photoInfoPage, boolean needDividersBefore, int orderInFeed, List<StreamItem> outItems, int margin, int minWidth, float sizeCoef, int minSize, boolean isTablet) {
        int startOrder = orderInFeed;
        int w = photo.getPhotoInfo().getStandartWidth();
        int h = photo.getPhotoInfo().getStandartHeight();
        PhotoLocate result = isTablet ? getTabletPhotoLocate(margin, minWidth, sizeCoef, w, h, minSize) : getPhonePhotoLocate(margin, minWidth, sizeCoef, w, h, minSize);
        return (orderInFeed + binder.addItemWithOptionalDivider(GifAsMp4PlayerHelper.shouldPlayGifAsMp4InPlace(photo.getPhotoInfo(), AutoplayContext.FEED) ? new OneGifCollageItem(feedWithState, photo, itemPhoto, result) : new OnePhotoCollageItem(feedWithState, 2, new PhotoCollagePart(feedWithState, photo, result, itemPhoto, 1), photoInfoPage), needDividersBefore, outItems, orderInFeed)) - startOrder;
    }

    private static PhotoLocate getPhonePhotoLocate(int margin, int minWidth, float sizeCoef, int w, int h, int minSize) {
        float aspectRatio = ((float) w) / ((float) h);
        if (aspectRatio < 0.75f) {
            aspectRatio = 0.75f;
        }
        float deviceAspectRatio = DeviceUtils.getDeviceAspectRatio();
        int widthSize = (int) (((float) w) * sizeCoef);
        int heightSize = (int) (((float) h) * sizeCoef);
        if (widthSize < minWidth) {
            if (widthSize < minSize) {
                if (widthSize > heightSize) {
                    widthSize = minSize;
                } else {
                    widthSize = (int) (((float) minSize) * aspectRatio);
                }
            }
            PhotoLocate portrait = new WidthAspectRatioWithMarginLocate(aspectRatio, ((float) widthSize) / ((float) minWidth), margin, margin);
            if (heightSize < minWidth) {
                return new OrientationDecoratorLocate(portrait, new WidthHeightWithMarginLocate(widthSize, (int) (((float) widthSize) / aspectRatio), margin, margin));
            }
            return new OrientationDecoratorLocate(portrait, new WidthAspectRatioWithMarginLocate(aspectRatio, aspectRatio / deviceAspectRatio, margin, margin));
        } else if (aspectRatio < deviceAspectRatio) {
            return new OrientationDecoratorLocate(new WidthAspectRatioWithMarginLocate(aspectRatio, 1.0f, 0, 0), new WidthAspectRatioWithMarginLocate(aspectRatio, aspectRatio / deviceAspectRatio, margin, margin));
        } else {
            if (aspectRatio > 3.0f) {
                aspectRatio = 3.0f;
            }
            return new WidthAspectRatioWithMarginLocate(aspectRatio, 1.0f, 0, 0);
        }
    }

    private static PhotoLocate getTabletPhotoLocate(int margin, int minWidth, float sizeCoef, int w, int h, int minSize) {
        float aspectRatio = ((float) w) / ((float) h);
        int widthSize = (int) (((float) w) * sizeCoef);
        int heightSize = (int) (((float) h) * sizeCoef);
        if (widthSize < minWidth) {
            if (widthSize < minSize) {
                if (widthSize > heightSize) {
                    widthSize = minSize;
                } else {
                    widthSize = (int) (((float) minSize) * aspectRatio);
                }
            }
            PhotoLocate portrait = new WidthAspectRatioWithMarginLocate(aspectRatio, ((float) widthSize) / ((float) minWidth), margin, margin);
            if (heightSize < minWidth) {
                return new OrientationDecoratorLocate(portrait, new WidthHeightWithMarginLocate(widthSize, (int) (((float) widthSize) / aspectRatio), margin, margin));
            }
            return new OrientationDecoratorLocate(portrait, new WidthAspectRatioWithMarginLocate(aspectRatio, aspectRatio, margin, margin));
        } else if (aspectRatio < 1.0f) {
            return new WidthAspectRatioWithMarginLocate(aspectRatio, aspectRatio, margin, margin);
        } else {
            if (aspectRatio > 3.0f) {
                aspectRatio = 3.0f;
            }
            return new WidthAspectRatioWithMarginLocate(aspectRatio, 1.0f, 0, 0);
        }
    }

    public static int addOnePhotoItem(Context context, Feed2StreamItemBinder binder, FeedWithState feedWithState, BaseEntity photo, MediaItemPhoto itemPhoto, boolean needDividersBefore, int orderInFeed, List<StreamItem> outItems) {
        int startOrder = orderInFeed;
        DeviceLayoutType deviceType = DeviceUtils.getType(context);
        boolean isTablet = deviceType == DeviceLayoutType.LARGE || deviceType == DeviceLayoutType.BIG;
        int minWidth = DeviceUtils.getStreamHighQualityPhotoWidth();
        int margin = context.getResources().getDimensionPixelOffset(2131230984);
        float sizeCoef = (float) (OdnoklassnikiApplication.getContext().getResources().getDimensionPixelSize(2131230982) / 120);
        if (photo instanceof AbsFeedPhotoEntity) {
            AbsFeedPhotoEntity photoEntity = (AbsFeedPhotoEntity) photo;
            int w = photoEntity.getPhotoInfo().getStandartWidth();
            int h = photoEntity.getPhotoInfo().getStandartHeight();
            int minSize = context.getResources().getDimensionPixelSize(2131230983);
            PhotoLocate result = isTablet ? getTabletPhotoLocate(margin, minWidth, sizeCoef, w, h, minSize) : getPhonePhotoLocate(margin, minWidth, sizeCoef, w, h, minSize);
            StreamItem oneGifCollageItem;
            if (GifAsMp4PlayerHelper.shouldPlayGifAsMp4InPlace(photoEntity.getPhotoInfo(), AutoplayContext.FEED)) {
                oneGifCollageItem = new OneGifCollageItem(feedWithState, photoEntity, itemPhoto, result);
            } else {
                oneGifCollageItem = new OnePhotoCollageItem(feedWithState, 2, new PhotoCollagePart(feedWithState, photoEntity, result, itemPhoto, 1));
            }
            orderInFeed += binder.addItemWithOptionalDivider(item, needDividersBefore, outItems, orderInFeed);
        }
        return orderInFeed - startOrder;
    }
}
