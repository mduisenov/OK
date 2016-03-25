package ru.ok.android.ui.stream.list;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import ru.ok.android.app.SpritesHelper;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.CompositePresentView;
import ru.ok.android.ui.custom.UsersStripView;
import ru.ok.android.ui.presents.helpers.PresentSettingsHelper;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.android.utils.Utils;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.FeedUtils;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedAchievementEntity;
import ru.ok.model.stream.entities.FeedAchievementTypeEntity;
import ru.ok.model.stream.entities.FeedPresentEntity;
import ru.ok.model.stream.entities.FeedPresentTypeEntity;
import ru.ok.model.stream.entities.IPresentEntity;
import ru.ok.model.stream.message.FeedMessage;
import ru.ok.model.stream.message.FeedMessageSpan;

public class StreamManyPresentsItem extends StreamItem {
    private final int achievementSize;
    final Set<String> avatarUrls;
    private int badgesCount;
    private final boolean needShowPresentLabels;
    private int noUserPresents;
    final List<PresentInfo> presentInfos;
    private final int presentSizeBig;
    private final int presentSizeNormal;

    static class ManyPresentsViewHolder extends ViewHolder {
        private final ViewGroup presents;
        private final Button sendPresentBtn;

        public ManyPresentsViewHolder(View view) {
            super(view);
            this.presents = (ViewGroup) view.findViewById(2131625359);
            this.sendPresentBtn = (Button) view.findViewById(2131625360);
        }
    }

    protected StreamManyPresentsItem(FeedWithState feed, int presentSizeNormal, int presentSizeBig, int achievementSize) {
        super(14, 3, 1, feed);
        this.presentInfos = new ArrayList();
        this.presentSizeNormal = presentSizeNormal;
        this.presentSizeBig = presentSizeBig;
        this.achievementSize = achievementSize;
        this.avatarUrls = new HashSet();
        for (BaseEntity entity : this.feedWithState.feed.getPresents()) {
            IPresentEntity present = null;
            BaseEntity senderEntity = null;
            BaseEntity receiverEntity = null;
            int type = entity.getType();
            FeedPresentEntity presentEntity = null;
            if (type == 6) {
                presentEntity = (FeedPresentEntity) entity;
                FeedPresentTypeEntity presentType = presentEntity.getPresentType();
                senderEntity = presentEntity.getSender();
                receiverEntity = presentEntity.getReceiver();
                if (presentType != null) {
                    present = presentType;
                }
            } else if (type == 22) {
                FeedAchievementTypeEntity achType = ((FeedAchievementEntity) entity).getAchievementType();
                if (achType != null) {
                    Object present2 = achType;
                }
            } else if (entity instanceof IPresentEntity) {
                present = (IPresentEntity) entity;
            }
            GeneralUserInfo sender = FeedUtils.getUserInfoFromEntity(senderEntity);
            GeneralUserInfo receiver = FeedUtils.getUserInfoFromEntity(receiverEntity);
            if (present != null) {
                PresentInfo presentInfo = new PresentInfo(present, feed.feed, presentEntity);
                this.presentInfos.add(presentInfo);
                if (presentInfo.isBadge) {
                    this.badgesCount++;
                }
                if (sender != null) {
                    if (!presentInfo.senders.contains(sender)) {
                        presentInfo.senders.add(sender);
                    }
                } else {
                    this.noUserPresents++;
                }
                if (receiver != null) {
                    if (!presentInfo.receivers.contains(receiver)) {
                        presentInfo.receivers.add(receiver);
                    }
                }
            }
        }
        fillPrefetchUrlsFromPresentInfos(this.avatarUrls, this.presentInfos);
        this.needShowPresentLabels = needShowPresentLabels(feed.feed);
    }

    public void prefetch() {
        int i = 0;
        while (i < this.presentInfos.size() && i < 5) {
            FeedPresentTypeEntity presentType = ((PresentInfo) this.presentInfos.get(i)).presentType;
            if (presentType.isAnimated() && PresentSettingsHelper.isAnimatedPresentsEnabled()) {
                SpritesHelper.prefetch(presentType, new Point(this.presentSizeBig, this.presentSizeBig));
            } else {
                PrefetchUtils.prefetchUrl(presentType.getLargestPicUrl());
            }
            i++;
        }
        for (String avatarUrl : this.avatarUrls) {
            PrefetchUtils.prefetchUrl(avatarUrl);
        }
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903483, parent, false);
    }

    public void bindView(ViewHolder viewHolder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        super.bindView(viewHolder, streamItemViewController, layoutConfig);
        if (viewHolder instanceof ManyPresentsViewHolder) {
            ManyPresentsViewHolder holder = (ManyPresentsViewHolder) viewHolder;
            streamItemViewController.getViewCache().collectAndClearChildViews(holder.presents);
            bindMakePresentButton(holder, streamItemViewController);
            int position = 0;
            int hSpacing = streamItemViewController.getActivity().getResources().getDimensionPixelOffset(2131231200);
            for (PresentInfo presentInfo : this.presentInfos) {
                boolean singlePresent;
                View container;
                CompositePresentView image;
                List<GeneralUserInfo> users;
                Object users2;
                View usersStrip;
                View view4Tags;
                View name;
                Spannable presentLabel;
                MarginLayoutParams imageLp;
                int i;
                Point point;
                MarginLayoutParams lp;
                boolean isPresent = presentInfo.presentType != null;
                if (presentInfo.senders.size() <= 1) {
                    if (presentInfo.receivers.size() <= 1) {
                        singlePresent = true;
                        container = streamItemViewController.getViewCache().getViewWithLayoutId(singlePresent ? 2130903460 : 2130903461, holder.presents);
                        image = (CompositePresentView) container.findViewById(C0263R.id.image);
                        image.setClickable(false);
                        if (presentInfo.feed.getActionType() != 0) {
                            users = presentInfo.senders;
                        } else {
                            users2 = presentInfo.receivers;
                        }
                        if (singlePresent) {
                            usersStrip = (UsersStripView) container.findViewById(2131625336);
                            usersStrip.setTag(2131624346, users);
                            usersStrip.setTag(2131624343, "present-senders");
                            usersStrip.setOnClickListener(streamItemViewController.getGeneralUsersClickListener());
                            usersStrip.setHandleBlocker(streamItemViewController.getImageLoadBlocker());
                            usersStrip.setUsers(users, users.size());
                            view4Tags = usersStrip;
                        } else {
                            name = (TextView) container.findViewById(C0263R.id.name);
                            name.setOnClickListener(streamItemViewController.getGeneralUsersClickListener());
                            presentLabel = getPresentLabel(presentInfo);
                            if (this.needShowPresentLabels || presentLabel == null) {
                                name.setVisibility(8);
                            } else {
                                Utils.setTextViewTextWithVisibility(name, presentLabel);
                            }
                            view4Tags = name;
                        }
                        view4Tags.setTag(2131624322, this.feedWithState);
                        view4Tags.setTag(2131624346, users);
                        view4Tags.setTag(2131624343, "present-senders");
                        imageLp = (MarginLayoutParams) image.getLayoutParams();
                        if (isPresent) {
                            i = this.achievementSize;
                            imageLp.height = i;
                            imageLp.width = i;
                        } else {
                            i = presentInfo.isBig ? this.presentSizeBig : this.presentSizeNormal;
                            imageLp.height = i;
                            imageLp.width = i;
                        }
                        image.setLayoutParams(imageLp);
                        point = new Point(imageLp.width, imageLp.width);
                        if (isPresent) {
                            container.setClickable(false);
                            image.setAchive(presentInfo.presentType, point);
                        } else {
                            container.setTag(2131624336, presentInfo);
                            container.setTag(2131624322, this.feedWithState);
                            container.setClickable(true);
                            container.setOnClickListener(streamItemViewController.getPresentClickListener());
                            image.setPresentType(presentInfo.presentType, point);
                        }
                        lp = (MarginLayoutParams) container.getLayoutParams();
                        if (position <= 0) {
                            lp.leftMargin = hSpacing;
                        } else {
                            lp.leftMargin = 0;
                        }
                        holder.presents.addView(container, lp);
                        position++;
                    }
                }
                singlePresent = false;
                if (singlePresent) {
                }
                container = streamItemViewController.getViewCache().getViewWithLayoutId(singlePresent ? 2130903460 : 2130903461, holder.presents);
                image = (CompositePresentView) container.findViewById(C0263R.id.image);
                image.setClickable(false);
                if (presentInfo.feed.getActionType() != 0) {
                    users2 = presentInfo.receivers;
                } else {
                    users = presentInfo.senders;
                }
                if (singlePresent) {
                    usersStrip = (UsersStripView) container.findViewById(2131625336);
                    usersStrip.setTag(2131624346, users);
                    usersStrip.setTag(2131624343, "present-senders");
                    usersStrip.setOnClickListener(streamItemViewController.getGeneralUsersClickListener());
                    usersStrip.setHandleBlocker(streamItemViewController.getImageLoadBlocker());
                    usersStrip.setUsers(users, users.size());
                    view4Tags = usersStrip;
                } else {
                    name = (TextView) container.findViewById(C0263R.id.name);
                    name.setOnClickListener(streamItemViewController.getGeneralUsersClickListener());
                    presentLabel = getPresentLabel(presentInfo);
                    if (this.needShowPresentLabels) {
                    }
                    name.setVisibility(8);
                    view4Tags = name;
                }
                view4Tags.setTag(2131624322, this.feedWithState);
                view4Tags.setTag(2131624346, users);
                view4Tags.setTag(2131624343, "present-senders");
                imageLp = (MarginLayoutParams) image.getLayoutParams();
                if (isPresent) {
                    i = this.achievementSize;
                    imageLp.height = i;
                    imageLp.width = i;
                } else {
                    if (presentInfo.isBig) {
                    }
                    imageLp.height = i;
                    imageLp.width = i;
                }
                image.setLayoutParams(imageLp);
                point = new Point(imageLp.width, imageLp.width);
                if (isPresent) {
                    container.setClickable(false);
                    image.setAchive(presentInfo.presentType, point);
                } else {
                    container.setTag(2131624336, presentInfo);
                    container.setTag(2131624322, this.feedWithState);
                    container.setClickable(true);
                    container.setOnClickListener(streamItemViewController.getPresentClickListener());
                    image.setPresentType(presentInfo.presentType, point);
                }
                lp = (MarginLayoutParams) container.getLayoutParams();
                if (position <= 0) {
                    lp.leftMargin = 0;
                } else {
                    lp.leftMargin = hSpacing;
                }
                holder.presents.addView(container, lp);
                position++;
            }
        }
    }

    private static void fillPrefetchUrlsFromPresentInfos(Set<String> urls, List<PresentInfo> presentInfos) {
        for (PresentInfo presentInfo : presentInfos) {
            if (presentInfo.senders.size() > 1 && presentInfo.feed.getActionType() == 0) {
                fillPrefetchUrlsFromUsers(urls, presentInfo.senders);
            } else if (presentInfo.receivers.size() > 1 && presentInfo.feed.getActionType() == 1) {
                fillPrefetchUrlsFromUsers(urls, presentInfo.receivers);
            }
        }
    }

    private static void fillPrefetchUrlsFromUsers(Set<String> urls, List<GeneralUserInfo> users) {
        int i = 0;
        while (i < users.size() && i < 5) {
            GeneralUserInfo receiverInfo = (GeneralUserInfo) users.get(i);
            if (receiverInfo.getPicUrl() != null) {
                urls.add(receiverInfo.getPicUrl());
            }
            i++;
        }
    }

    private boolean needShowPresentLabels(@NonNull Feed feed) {
        boolean z = true;
        if (feed.getPresents().size() <= 1) {
            return false;
        }
        if (feed.getActionType() == 1) {
            if (feed.getReceivers().size() <= 1) {
                z = false;
            }
            return z;
        } else if (feed.getActionType() != 0) {
            return false;
        } else {
            if (feed.getSenders().size() + this.noUserPresents <= 1) {
                z = false;
            }
            return z;
        }
    }

    private boolean needShowSendPresentBtn() {
        return this.feedWithState.feed.getReceivers().size() == 1 && this.badgesCount <= 0;
    }

    private void bindMakePresentButton(ManyPresentsViewHolder holder, StreamItemViewController streamItemViewController) {
        boolean btnVisible = false;
        GeneralUserInfo userInfo = null;
        if (needShowSendPresentBtn()) {
            userInfo = FeedUtils.getUserInfoFromEntity((BaseEntity) this.feedWithState.feed.getReceivers().get(0));
        }
        if (userInfo != null) {
            holder.sendPresentBtn.setTag(2131624336, this.presentInfos.get(0));
            holder.sendPresentBtn.setTag(2131624322, this.feedWithState);
            holder.sendPresentBtn.setTag(2131624354, userInfo);
            holder.sendPresentBtn.setOnClickListener(streamItemViewController.getMakePresentClickListener());
            btnVisible = true;
        }
        if (btnVisible) {
            holder.sendPresentBtn.setVisibility(0);
        } else {
            holder.sendPresentBtn.setVisibility(8);
        }
    }

    @Nullable
    private Spannable getPresentLabel(@NonNull PresentInfo presentInfo) {
        if (this.feedWithState.feed.getActionType() == 0) {
            if (presentInfo.senderLabel == null) {
                return null;
            }
            return createPresentLabel(presentInfo.senderLabel);
        } else if (presentInfo.receiverLabel != null) {
            return createPresentLabel(presentInfo.receiverLabel);
        } else {
            return null;
        }
    }

    @NonNull
    private static Spannable createPresentLabel(@NonNull FeedMessage message) {
        Spannable spannable = new SpannableString(message.getText());
        StyleSpan spanned = new StyleSpan(1);
        Iterator i$ = message.getSpans().iterator();
        while (i$.hasNext()) {
            FeedMessageSpan span = (FeedMessageSpan) i$.next();
            spannable.setSpan(spanned, span.getStartIndex(), span.getEndIndex(), 17);
        }
        return spannable;
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new ManyPresentsViewHolder(view);
    }
}
