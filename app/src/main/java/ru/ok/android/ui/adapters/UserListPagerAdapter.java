package ru.ok.android.ui.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import java.util.ArrayList;
import ru.ok.android.model.AuthorizedUser;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.ui.nativeRegistration.UserAvatar;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public class UserListPagerAdapter extends PagerAdapter {
    private Context context;
    private UserAvatarCancelClickListener userAvatarCancelClickListener;
    private UserAvatarClickListener userAvatarClickListener;
    private ArrayList users;

    /* renamed from: ru.ok.android.ui.adapters.UserListPagerAdapter.1 */
    class C05801 implements OnClickListener {
        final /* synthetic */ Object val$object;
        final /* synthetic */ int val$position;

        C05801(Object obj, int i) {
            this.val$object = obj;
            this.val$position = i;
        }

        public void onClick(View view) {
            UserListPagerAdapter.this.userAvatarClickListener.onAvatarClick(this.val$object, this.val$position);
        }
    }

    /* renamed from: ru.ok.android.ui.adapters.UserListPagerAdapter.2 */
    class C05812 implements OnClickListener {
        final /* synthetic */ int val$position;
        final /* synthetic */ UserInfo val$userInfo;

        C05812(UserInfo userInfo, int i) {
            this.val$userInfo = userInfo;
            this.val$position = i;
        }

        public void onClick(View view) {
            UserListPagerAdapter.this.userAvatarCancelClickListener.onAvatarCancelClick(this.val$userInfo, this.val$position);
        }
    }

    public interface UserAvatarCancelClickListener {
        void onAvatarCancelClick(UserInfo userInfo, int i);
    }

    public interface UserAvatarClickListener {
        void onAvatarClick(Object obj, int i);
    }

    public void showEmptyAuthorizedUser() {
        AuthorizedUser user = new AuthorizedUser();
        this.users = new ArrayList();
        this.users.add(user);
        notifyDataSetChanged();
    }

    public void setData(ArrayList userList) {
        if (userList != null) {
            this.users = userList;
            notifyDataSetChanged();
        }
    }

    public int getItemPosition(Object object) {
        return -2;
    }

    public UserListPagerAdapter(Context context, UserAvatarClickListener userAvatarClickListener) {
        this.users = new ArrayList();
        this.context = context;
        this.userAvatarClickListener = userAvatarClickListener;
    }

    public UserListPagerAdapter(Context context, UserAvatarClickListener userAvatarClickListener, UserAvatarCancelClickListener userAvatarCancelClickListener) {
        this.users = new ArrayList();
        this.context = context;
        this.userAvatarClickListener = userAvatarClickListener;
        this.userAvatarCancelClickListener = userAvatarCancelClickListener;
    }

    public void deleteItem(int position) {
        this.users.remove(position);
        notifyDataSetChanged();
    }

    public Object getUserAtPosition(int position) {
        return this.users.get(position);
    }

    private void setPersonClickListener(View view, int position, Object object) {
        view.setOnClickListener(new C05801(object, position));
    }

    private void setCancelClickListener(View view, int position, UserInfo userInfo) {
        view.setOnClickListener(new C05812(userInfo, position));
    }

    public Object instantiateItem(ViewGroup collection, int position) {
        UserInfo userInfo;
        collection.setClipChildren(false);
        UserAvatar rowView = new UserAvatar(this.context);
        rowView.setLayoutParams(new LayoutParams(-1, -1));
        AuthorizedUser obj = this.users.get(position);
        if (obj instanceof AuthorizedUser) {
            userInfo = obj.user;
        } else {
            userInfo = (UserInfo) obj;
        }
        if (this.userAvatarClickListener != null) {
            setPersonClickListener(rowView.avatar, position, obj);
            setPersonClickListener(rowView.ok, position, obj);
        }
        if (userInfo.getId().equals("")) {
            rowView.ok.setVisibility(0);
            rowView.avatar.setVisibility(8);
        } else {
            boolean z;
            ImageViewManager instance = ImageViewManager.getInstance();
            String str = userInfo.picUrl;
            AvatarImageView avatarImageView = rowView.avatar;
            if (userInfo.genderType == UserGenderType.MALE) {
                z = true;
            } else {
                z = false;
            }
            instance.displayImage(str, avatarImageView, z, ScrollLoadBlocker.forIdleAndTouchIdle());
            if (this.userAvatarCancelClickListener != null) {
                rowView.setCancelVisible(true);
                setCancelClickListener(rowView.cancel, position, userInfo);
            }
        }
        rowView.setAlpha(0.4f);
        rowView.setTag(Integer.valueOf(position));
        rowView.setPadding(0, 0, 0, 0);
        collection.addView(rowView);
        return rowView;
    }

    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
        if (position >= collection.getChildCount() - 1) {
            for (int i = position; i < collection.getChildCount(); i++) {
                collection.getChildAt(i).setTag(Integer.valueOf(i));
            }
        }
    }

    public int getCount() {
        return this.users.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
