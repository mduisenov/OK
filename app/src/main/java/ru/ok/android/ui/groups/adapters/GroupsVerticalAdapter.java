package ru.ok.android.ui.groups.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import ru.ok.android.fresco.postprocessors.ImageCenterCropRoundPostprocessor;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.services.processors.groups.GroupsProcessor.GroupAdditionalInfo;
import ru.ok.android.ui.custom.NotificationsView;
import ru.ok.android.ui.groups.GroupUtils;
import ru.ok.android.ui.groups.holders.GroupVerticalViewHolder;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;
import ru.ok.onelog.groups.GroupsPageGroupClickFactory;
import ru.ok.onelog.groups.GroupsPageGroupClickSource;

public class GroupsVerticalAdapter extends GroupsRecyclerAdapter<ViewHolder> {
    private final boolean hasJoinFunctionality;
    private Set<String> invitedGroupIds;
    private final boolean isBigCardLayout;
    private OnClickListener joinClickListener;
    private final DecimalFormat membersCountDecimalFormat;
    private Drawable placeHolderDrawable;

    /* renamed from: ru.ok.android.ui.groups.adapters.GroupsVerticalAdapter.1 */
    class C09111 implements OnClickListener {
        C09111() {
        }

        public void onClick(View v) {
            GroupInfo groupInfo = (GroupInfo) v.getTag(2131624323);
            if (GroupsVerticalAdapter.this.listener != null) {
                GroupsVerticalAdapter.this.listener.onGroupInfoJoinClick(groupInfo);
            }
        }
    }

    public GroupsVerticalAdapter(Context context, boolean hasJoinFunctionality, boolean isBigCardLayout) {
        this.joinClickListener = new C09111();
        this.invitedGroupIds = new HashSet();
        this.placeHolderDrawable = GroupUtils.getRoundedDrawable(context, 2130837663, context.getResources().getDimensionPixelSize(2131231017));
        this.hasJoinFunctionality = hasJoinFunctionality;
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        this.membersCountDecimalFormat = formatter;
        this.isBigCardLayout = isBigCardLayout;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GroupVerticalViewHolder viewHolder = new GroupVerticalViewHolder(LayoutInflater.from(parent.getContext()).inflate(this.isBigCardLayout ? 2130903230 : 2130903229, parent, false));
        if (this.hasJoinFunctionality) {
            viewHolder.join.setOnClickListener(this.joinClickListener);
        } else {
            viewHolder.join.setVisibility(8);
            viewHolder.joined.setVisibility(8);
            viewHolder.participants.setVisibility(8);
        }
        ViewUtil.setTouchDelegate(viewHolder.join, parent.getContext().getResources().getDimensionPixelOffset(2131231201));
        ((GenericDraweeHierarchy) viewHolder.image.getHierarchy()).setPlaceholderImage(this.placeHolderDrawable);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        GroupInfo groupInfo = (GroupInfo) this.items.get(position);
        GroupVerticalViewHolder groupVerticalViewHolder = (GroupVerticalViewHolder) holder;
        groupVerticalViewHolder.title.setText(groupInfo.getName());
        GroupAdditionalInfo additionalInfo = (GroupAdditionalInfo) this.groupAdditionalInfoMap.get(groupInfo.getId());
        if (this.hasJoinFunctionality) {
            if (this.invitedGroupIds.contains(groupInfo.getId())) {
                groupVerticalViewHolder.joined.setText(LocalizationManager.getString(holder.itemView.getContext(), groupInfo.isPrivateGroup() ? 2131166028 : 2131166029));
                groupVerticalViewHolder.joined.setVisibility(0);
                groupVerticalViewHolder.join.setVisibility(8);
            } else {
                groupVerticalViewHolder.joined.setVisibility(8);
                groupVerticalViewHolder.join.setVisibility(0);
                groupVerticalViewHolder.join.setTag(2131624323, groupInfo);
            }
            List<UserInfo> friendsMembers = additionalInfo != null ? additionalInfo.friendMembers : null;
            if (friendsMembers == null || friendsMembers.size() <= 0) {
                groupVerticalViewHolder.participants.setVisibility(8);
            } else {
                groupVerticalViewHolder.participants.setParticipants(friendsMembers, false);
                groupVerticalViewHolder.participants.setVisibility(0);
            }
        }
        int count = groupInfo.getMembersCount();
        String shortenedMembersCountString = GroupUtils.shortenedCountString((long) count);
        int membersCountRes = shortenedMembersCountString == null ? StringUtils.plural((long) count, 2131166186, 2131166187, 2131166188) : 2131166188;
        TextView textView = groupVerticalViewHolder.membersCount;
        Context context = holder.itemView.getContext();
        Object[] objArr = new Object[1];
        if (shortenedMembersCountString == null) {
            shortenedMembersCountString = Integer.toString(count);
        }
        objArr[0] = shortenedMembersCountString;
        textView.setText(LocalizationManager.getString(context, membersCountRes, objArr));
        long friendsMembersCount = additionalInfo == null ? 0 : additionalInfo.friendMembersCount;
        if (friendsMembersCount > 0) {
            int friendMembersCountRes = StringUtils.plural(friendsMembersCount, 2131165884, 2131165885, 2131165886);
            textView = groupVerticalViewHolder.friendsMembersCount;
            context = holder.itemView.getContext();
            objArr = new Object[1];
            objArr[0] = this.membersCountDecimalFormat.format(friendsMembersCount);
            textView.setText(LocalizationManager.getString(context, friendMembersCountRes, objArr));
            groupVerticalViewHolder.friendsMembersCount.setVisibility(0);
        } else {
            groupVerticalViewHolder.friendsMembersCount.setVisibility(8);
        }
        bindImage(groupVerticalViewHolder.image, groupInfo);
        bindUnreadEvents(groupVerticalViewHolder.unreadEvents, groupInfo);
        this.itemClickListenerController.onBindViewHolder(holder, position);
    }

    public static void bindUnreadEvents(NotificationsView notificationsView, GroupInfo groupInfo) {
        long unreadEventsCount = groupInfo.getUnreadEventsCount();
        if (unreadEventsCount > 0) {
            notificationsView.setValue((int) unreadEventsCount);
            notificationsView.setVisibility(0);
            return;
        }
        notificationsView.setVisibility(8);
    }

    public static boolean bindImage(SimpleDraweeView simpleDraweeView, GroupInfo groupInfo) {
        String url = groupInfo.getAnyPicUrl();
        Uri uri = url == null ? null : Uri.parse(url);
        if (uri == null) {
            simpleDraweeView.setImageURI(null);
            return false;
        }
        simpleDraweeView.setController((PipelineDraweeController) ((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setImageRequest(ImageRequestBuilder.newBuilderWithSource(uri).setPostprocessor(new ImageCenterCropRoundPostprocessor(uri)).build())).setOldController(simpleDraweeView.getController())).build());
        return true;
    }

    public void addInvitedGroupIdAndNotify(String groupId) {
        this.invitedGroupIds.add(groupId);
        if (this.items != null) {
            int size = this.items.size();
            for (int i = 0; i < size; i++) {
                if (groupId.equals(((GroupInfo) this.items.get(i)).getId())) {
                    notifyItemChanged(i);
                    return;
                }
            }
        }
    }

    protected void logClick() {
        OneLog.log(GroupsPageGroupClickFactory.get(this.isBigCardLayout ? GroupsPageGroupClickSource.groups_page_combo_portal : GroupsPageGroupClickSource.groups_page_portal));
    }
}
