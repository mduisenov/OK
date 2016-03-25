package ru.ok.android.ui.users.fragments.profiles;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.GeneralDataLoader;
import android.support.v4.content.res.ResourcesCompat;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.fresco.FrescoBackgroundRelativeLayout;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.fresco.FrescoOdkl.SideCrop;
import ru.ok.android.fresco.postprocessors.ImageBlurPostprocessor;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.fragments.messages.view.ParticipantsPreviewView;
import ru.ok.android.ui.measuredobserver.MeasureObservable.MeasureObservableHelper;
import ru.ok.android.ui.users.fragments.UsersByIdFragment;
import ru.ok.android.ui.users.fragments.profiles.ProfileLoadCallBack.ProfileType;
import ru.ok.android.utils.FriendlySpannableStringBuilder;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.URLUtil;
import ru.ok.model.UserInfo;

public abstract class ProfileBaseFragment extends BaseFragment implements OnClickListener {
    private SimpleDraweeView avatar;
    private FrescoBackgroundRelativeLayout bgLayout;
    private int boundarySize;
    private ParticipantsPreviewView friendsAvatars;
    private View friendsHolder;
    private ViewStub friendsHolderStub;
    private TextView friendsText;
    protected ProfileLoadCallBack loadCallBack;
    protected final MeasureObservableHelper measureObservableHelper;
    protected final ProfileInfoViewChangeObserver notifyProfileDataObserver;
    protected String pendingAction;
    protected ErrorType pendingError;
    private OnPreDrawListener preDrawListener;

    /* renamed from: ru.ok.android.ui.users.fragments.profiles.ProfileBaseFragment.1 */
    class C13381 implements OnPreDrawListener {
        C13381() {
        }

        public boolean onPreDraw() {
            ProfileBaseFragment.this.friendsHolder.getViewTreeObserver().removeOnPreDrawListener(this);
            ProfileBaseFragment.this.setUserNamesText((List) ProfileBaseFragment.this.friendsHolder.getTag(2131624346));
            return false;
        }
    }

    class ProfileInfoViewChangeObserver extends Observable {
        ProfileInfoViewChangeObserver() {
        }

        public void profileDataChange() {
            setChanged();
        }

        public void notifyViewChange() {
            if (ProfileBaseFragment.this.getView() != null) {
                notifyObservers(ProfileBaseFragment.this.getView());
            }
        }
    }

    protected abstract int getDefaultImageResId();

    protected abstract int getFriendsPrefixResId();

    protected abstract GeneralDataLoader getLoader();

    protected abstract void logMainPhotoClick();

    protected abstract void showMainPhoto();

    public abstract void updateProfile();

    public ProfileBaseFragment() {
        this.notifyProfileDataObserver = new ProfileInfoViewChangeObserver();
        this.measureObservableHelper = new MeasureObservableHelper();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPendingAction(savedInstanceState);
    }

    private void initPendingAction(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.pendingAction = savedInstanceState.getString("pending_action");
            return;
        }
        Bundle args = getArguments();
        this.pendingAction = args == null ? null : args.getString("pending_action");
        if (this.pendingAction == null) {
            this.pendingAction = getActivity().getIntent().getStringExtra("pending_action");
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("pending_action", this.pendingAction);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.boundarySize = getResources().getDimensionPixelOffset(2131231139);
        this.friendsHolderStub = (ViewStub) view.findViewById(2131625264);
        this.avatar = (SimpleDraweeView) view.findViewById(2131624911);
        this.avatar.setOnClickListener(this);
        this.bgLayout = (FrescoBackgroundRelativeLayout) view.findViewById(2131624915);
        ((GenericDraweeHierarchy) this.avatar.getHierarchy()).setFadeDuration(500);
    }

    protected void updateFriendsBlockWithUsers(List<UserInfo> friends) {
        if (friends != null && !friends.isEmpty()) {
            if (this.friendsHolderStub != null) {
                this.friendsHolder = this.friendsHolderStub.inflate();
                this.friendsHolderStub = null;
                if (this.friendsHolder != null) {
                    this.friendsHolder.setOnClickListener(this);
                    this.friendsAvatars = (ParticipantsPreviewView) this.friendsHolder.findViewById(2131625262);
                    this.friendsText = (TextView) this.friendsHolder.findViewById(2131625263);
                }
            }
            if (this.friendsHolder != null) {
                this.friendsHolder.setVisibility(0);
                this.friendsHolder.setTag(2131624346, friends);
                if (this.friendsAvatars != null) {
                    this.friendsAvatars.setParticipants(friends, true);
                }
                if (this.friendsText == null) {
                    return;
                }
                if (this.friendsHolder.getWidth() == 0) {
                    this.friendsHolder.setTag(2131624346, friends);
                    this.friendsHolder.getViewTreeObserver().addOnPreDrawListener(getPreDrawListener());
                    return;
                }
                setUserNamesText(friends);
            }
        } else if (this.friendsHolder != null) {
            this.friendsHolder.setVisibility(8);
        }
    }

    private OnPreDrawListener getPreDrawListener() {
        if (this.preDrawListener == null) {
            this.preDrawListener = new C13381();
        }
        return this.preDrawListener;
    }

    private void setUserNamesText(List<UserInfo> friends) {
        boolean useShortName;
        if (this.friendsHolder.getWidth() < this.boundarySize) {
            useShortName = true;
        } else {
            useShortName = false;
        }
        FriendlySpannableStringBuilder sb = new FriendlySpannableStringBuilder();
        for (UserInfo userInfo : friends) {
            if (sb.length() > 0) {
                sb.append(", ");
            } else {
                sb.append(getStringLocalized(getFriendsPrefixResId())).append(": ");
            }
            sb.append(useShortName ? userInfo.firstName : userInfo.getAnyName(), new StyleSpan(1));
        }
        this.friendsText.setText(sb.build());
    }

    public void onClick(View v) {
        if (v == this.friendsHolder) {
            List<UserInfo> users = (List) this.friendsHolder.getTag(2131624346);
            if (users != null) {
                ArrayList<String> userIds = new ArrayList();
                for (UserInfo userInfo : users) {
                    userIds.add(userInfo.uid);
                }
                UsersByIdFragment.newInstanceCommonFriends(userIds, getFriendsPrefixResId()).show(getFragmentManager(), "users-list");
            }
        } else if (v == this.avatar) {
            showMainPhoto();
            logMainPhotoClick();
        }
    }

    protected void processAvatarUrl(String bigUrl, String lowUrl) {
        if (URLUtil.isStubUrl(bigUrl) && URLUtil.isStubUrl(lowUrl)) {
            createDefaultAvatar();
            return;
        }
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        if (!URLUtil.isStubUrl(bigUrl)) {
            builder.setUri(Uri.parse(bigUrl));
        }
        if (!URLUtil.isStubUrl(lowUrl)) {
            builder.setLowResImageRequest(ImageRequest.fromUri(lowUrl));
            builder.setRetainImageOnFailure(true);
        }
        builder.setOldController(this.avatar.getController());
        this.avatar.setController(builder.build());
        FrescoOdkl.cropToSide(this.avatar, SideCrop.TOP_CENTER, FrescoOdkl.ACTUAL_IMAGE);
        if (this.bgLayout != null && lowUrl != null) {
            this.bgLayout.setBackgroundController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setOldController(this.bgLayout.getBackgroundController())).setImageRequest(ImageRequestBuilder.newBuilderWithSource(Uri.parse(lowUrl)).setPostprocessor(new ImageBlurPostprocessor(lowUrl)).build())).build());
        }
    }

    private void createDefaultAvatar() {
        if (this.bgLayout != null) {
            this.bgLayout.setBackgroundController(((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setImageRequest(ImageRequestBuilder.newBuilderWithResourceId(2130838590).setPostprocessor(new ImageBlurPostprocessor("R.drawable.profile_bg")).build())).build());
        }
        int resId = getDefaultImageResId();
        Drawable background = ResourcesCompat.getDrawable(getResources(), 2130838590, getContext().getTheme());
        ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(resId).build();
        this.avatar.setHierarchy(GenericDraweeHierarchyBuilder.newInstance(getResources()).setBackground(background).build());
        this.avatar.setController(((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setImageRequest(request)).build());
    }

    protected final void onErrorResult(BusEvent event) {
        onErrorResult(getErrorType(event));
    }

    protected final void onErrorResult(ErrorType errorType) {
        if (getActivity() != null && errorType != ErrorType.GENERAL) {
            showTimedToastIfVisible(errorType.getDefaultErrorMessage(), 0);
        }
    }

    protected ErrorType getErrorType(BusEvent event) {
        return ErrorType.from(event.bundleOutput);
    }

    protected void onProfileInfoLoadError(ProfileType profileType, ErrorType errorType) {
        boolean loadInProgress = true;
        Logger.m185w("Profile info load error: %s %s", profileType, errorType);
        GeneralDataLoader profileLoader = getLoader();
        if (profileLoader == null || !profileLoader.isLoading()) {
            loadInProgress = false;
        }
        if (loadInProgress || this.loadCallBack == null) {
            this.pendingError = errorType;
            return;
        }
        this.loadCallBack.onProfileInfoLoadError(profileType, errorType);
        this.pendingError = null;
    }

    public void addViewChangeObserver(Observer observer) {
        this.notifyProfileDataObserver.addObserver(observer);
    }

    public void removeViewChangeObserver(Observer observer) {
        this.notifyProfileDataObserver.deleteObserver(observer);
    }

    public void addMeasureViewChangeObserver(Observer observer) {
        this.measureObservableHelper.addObserver(observer);
    }

    public void removeMeasureViewChangeObserver(Observer observer) {
        this.measureObservableHelper.deleteObserver(observer);
    }
}
