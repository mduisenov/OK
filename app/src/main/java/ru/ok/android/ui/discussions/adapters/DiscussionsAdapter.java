package ru.ok.android.ui.discussions.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.adapters.ScrollLoadBlocker;
import ru.ok.android.ui.custom.NotificationsView;
import ru.ok.android.ui.custom.animationlist.DataChangeAdapter;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;
import ru.ok.android.ui.custom.imageview.ImageRoundPressedView;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.ui.stream.list.StreamLinkItem.SimpleTemplateChooser.ImageType;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.PhotoUtil;
import ru.ok.android.utils.StringUtils;
import ru.ok.java.api.response.discussion.DiscussionsListResponse;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;
import ru.ok.model.ImageUrl;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.mediatopics.MediaItem;
import ru.ok.model.mediatopics.MediaItemApp;
import ru.ok.model.mediatopics.MediaItemLink;
import ru.ok.model.mediatopics.MediaItemMusic;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.mediatopics.MediaItemPoll;
import ru.ok.model.mediatopics.MediaItemTopic;
import ru.ok.model.mediatopics.MediaItemVideo;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;
import ru.ok.model.stream.entities.FeedMediaTopicEntity;
import ru.ok.model.stream.entities.FeedVideoEntity;

public class DiscussionsAdapter extends DataChangeAdapter<DiscussionsListResponse> {
    private final Context context;
    private DiscussionsListResponse discussions;
    private final ScrollLoadBlocker imageLoadBlocker;
    private final int imageSize;

    private class ViewHolder {
        final TextView author;
        final ImageRoundPressedView avatar;
        final TextView date;
        final AsyncDraweeView image;
        final ImageView mediaIconImage;
        final NotificationsView notification;
        final TextView title;

        ViewHolder(View view) {
            this.avatar = (ImageRoundPressedView) view.findViewById(2131624657);
            this.image = (AsyncDraweeView) view.findViewById(C0263R.id.image);
            this.mediaIconImage = (ImageView) view.findViewById(2131624972);
            this.author = (TextView) view.findViewById(2131624973);
            this.title = (TextView) view.findViewById(2131624665);
            this.date = (TextView) view.findViewById(2131624974);
            this.notification = (NotificationsView) view.findViewById(2131624798);
        }
    }

    public DiscussionsAdapter(Context context) {
        this.imageLoadBlocker = ScrollLoadBlocker.forIdleAndTouchIdle();
        this.context = context;
        this.imageSize = context.getResources().getDimensionPixelSize(2131230951);
    }

    public static View getDiscussionView(Context context, DiscussionInfoResponse discussion) {
        DiscussionsAdapter adapter = new DiscussionsAdapter(context);
        DiscussionsListResponse response = new DiscussionsListResponse();
        response.getDiscussions().add(discussion);
        adapter.setData(response);
        return adapter.getView(0, null, null);
    }

    public void setData(DiscussionsListResponse data) {
        this.discussions = data;
    }

    public DiscussionsListResponse getData() {
        return this.discussions;
    }

    public int getCount() {
        return this.discussions != null ? this.discussions.getDiscussions().size() : 0;
    }

    public Object getItem(int i) {
        return this.discussions.getDiscussions().get(i);
    }

    public long getItemId(int i) {
        return (long) ((DiscussionInfoResponse) this.discussions.getDiscussions().get(i)).generalInfo.id.hashCode();
    }

    public View getView(int i, View convertView, ViewGroup viewGroup) {
        DiscussionInfoResponse discussionInfo = (DiscussionInfoResponse) this.discussions.getDiscussions().get(i);
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(2130903253, viewGroup, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        bindView((ViewHolder) convertView.getTag(), discussionInfo);
        return convertView;
    }

    private void bindView(ViewHolder holder, DiscussionInfoResponse discussion) {
        bindTitle(holder.title, discussion);
        bindAuthor(holder.author, discussion);
        bindAvatar(holder.avatar, discussion);
        bindDate(holder.date, discussion);
        bindImage(holder.image, holder.mediaIconImage, discussion);
        bindNotification(holder.notification, discussion);
    }

    private void bindDate(TextView date, DiscussionInfoResponse discussion) {
        date.setText(DateFormatter.formatTodayTimeOrOlderDate(this.context, discussion.generalInfo.creationDate));
    }

    private void bindAuthor(TextView textView, DiscussionInfoResponse discussion) {
        if (discussion.generalInfo.user != null) {
            textView.setText(discussion.generalInfo.user.name);
        } else if (discussion.generalInfo.group != null) {
            textView.setText(discussion.generalInfo.group.name);
        } else {
            textView.setVisibility(8);
        }
    }

    private void bindTitle(TextView title, DiscussionInfoResponse discussion) {
        if (discussion.photoInfo != null) {
            title.setText(this.context.getString(2131166343));
        } else if (discussion.videoInfo != null) {
            title.setText(this.context.getString(2131166606) + ": " + discussion.generalInfo.title);
        } else if (discussion.albumInfo != null) {
            title.setText(this.context.getString(2131165369) + ": " + discussion.albumInfo.getTitle());
        } else if (discussion.generalInfo.type == Type.USER_FORUM || discussion.generalInfo.type == Type.SCHOOL_FORUM) {
            title.setText(2131166583);
        } else if (discussion.generalInfo.isMusicStatus()) {
            title.setText("");
        } else {
            title.setText(StringUtils.removeEmptyLines(discussion.generalInfo.title));
        }
        title.setVisibility(title.length() > 0 ? 0 : 8);
    }

    private void bindAvatar(ImageRoundPressedView imageView, DiscussionInfoResponse discussion) {
        if (discussion.generalInfo.user != null) {
            ImageViewManager.getInstance().displayImage(discussion.generalInfo.user.avatar, (UrlImageView) imageView, discussion.generalInfo.user.gender == UserGenderType.MALE ? 2130838321 : 2130837927, this.imageLoadBlocker);
        } else if (discussion.generalInfo.group != null) {
            ImageViewManager.getInstance().displayImage(discussion.generalInfo.group.avatar, (UrlImageView) imageView, 2130837663, this.imageLoadBlocker);
        } else {
            imageView.setImageResource(2130838290);
        }
    }

    private void bindImage(AsyncDraweeView imageView, ImageView mediaIconImage, DiscussionInfoResponse discussion) {
        String imageUrl = null;
        int imageResourceId = 0;
        if (discussion.photoInfo != null) {
            imageUrl = PhotoUtil.getClosestSquaredSize(this.imageSize, discussion.photoInfo.getSizes()).getUrl();
        } else if (discussion.videoInfo != null && discussion.videoInfo.thumbnails.size() > 0) {
            imageUrl = PhotoUtil.getClosestSize(this.imageSize, this.imageSize, discussion.videoInfo.thumbnails).getUrl();
            imageResourceId = 2130837849;
        } else if (discussion.albumInfo != null) {
            imageUrl = discussion.albumInfo.getPicUrl();
        } else if (discussion.mediaTopic != null) {
            bindMediaTopicImage(imageView, mediaIconImage, discussion.mediaTopic, false);
            return;
        }
        if (imageUrl != null) {
            imageView.setVisibility(0);
            imageView.setUri(Uri.parse(imageUrl));
        } else {
            imageView.setVisibility(8);
        }
        if (imageResourceId != 0) {
            mediaIconImage.setImageResource(imageResourceId);
            mediaIconImage.setVisibility(0);
            return;
        }
        mediaIconImage.setVisibility(4);
    }

    private boolean bindMediaTopicImage(AsyncDraweeView imageView, ImageView mediaIconImage, FeedMediaTopicEntity mediaTopic, boolean isReshare) {
        String imageUrl = null;
        int topicImageResource = 0;
        int i = 0;
        while (i < mediaTopic.getMediaItemsCount()) {
            MediaItem mediaItem = mediaTopic.getMediaItem(i);
            if (!(mediaItem instanceof MediaItemPhoto) || ((MediaItemPhoto) mediaItem).getPhotos().size() <= 0) {
                if ((mediaItem instanceof MediaItemVideo) && ((MediaItemVideo) mediaItem).getVideos().size() > 0) {
                    imageUrl = PhotoUtil.getClosestSize(this.imageSize, this.imageSize, ((FeedVideoEntity) ((MediaItemVideo) mediaItem).getVideos().get(0)).thumbnailUrls).getUrl();
                    topicImageResource = 2130837849;
                    break;
                } else if ((mediaItem instanceof MediaItemLink) && ((MediaItemLink) mediaItem).getImageUrls().size() > 0) {
                    ImageUrl url = (ImageUrl) ((MediaItemLink) mediaItem).getImageUrls().get(0);
                    if (url.getWidth() == 0) {
                        imageUrl = url.getUrlPrefix();
                    } else {
                        imageUrl = url.getUrlPrefix() + ImageType.LOW_XHDPI.getUrlType();
                    }
                    topicImageResource = 2130837841;
                } else if (mediaItem instanceof MediaItemApp) {
                    imageUrl = ((MediaItemApp) mediaItem).getImage();
                    break;
                } else if (mediaItem instanceof MediaItemPoll) {
                    topicImageResource = 2130837843;
                    break;
                } else if (mediaItem instanceof MediaItemMusic) {
                    topicImageResource = 2130837842;
                    break;
                } else {
                    if (mediaItem instanceof MediaItemTopic) {
                        for (FeedMediaTopicEntity innerTopic : ((MediaItemTopic) mediaItem).getMediaTopics()) {
                            if (bindMediaTopicImage(imageView, mediaIconImage, innerTopic, ((MediaItemTopic) mediaItem).isReshare())) {
                                return true;
                            }
                        }
                        continue;
                    }
                    i++;
                }
            } else {
                imageUrl = PhotoUtil.getClosestSquaredSize(this.imageSize, ((AbsFeedPhotoEntity) ((MediaItemPhoto) mediaItem).getPhotos().get(0)).getPhotoInfo().getSizes()).getUrl();
                break;
            }
        }
        if (isReshare) {
            mediaIconImage.setVisibility(0);
            mediaIconImage.setImageResource(2130837844);
        } else if (topicImageResource != 0) {
            mediaIconImage.setVisibility(0);
            mediaIconImage.setImageResource(topicImageResource);
        } else {
            mediaIconImage.setVisibility(4);
        }
        if (imageUrl != null) {
            imageView.setVisibility(0);
            imageView.setUri(Uri.parse(imageUrl));
        } else {
            imageView.setVisibility(8);
        }
        if (imageUrl == null && topicImageResource == 0) {
            return false;
        }
        return true;
    }

    private void bindNotification(NotificationsView notification, DiscussionInfoResponse discussion) {
        if (discussion.generalInfo.getFlags().repliesUnread) {
            notification.setVisibility(0);
            notification.setImage(2130838719);
            notification.setNotificationText("");
        } else if (discussion.generalInfo.getFlags().likesUnread) {
            notification.setVisibility(0);
            notification.setImage(2130838717);
            notification.setNotificationText("");
        } else {
            String countString = getCountString(discussion.generalInfo.getNewCommentsCount());
            if (countString != null) {
                notification.setNotificationText(countString);
                notification.hideImage();
                notification.setVisibility(0);
                return;
            }
            notification.setVisibility(4);
        }
    }

    private String getCountString(int count) {
        if (count <= 0) {
            return null;
        }
        if (count > 99) {
            return "99+";
        }
        return String.valueOf(count);
    }
}
