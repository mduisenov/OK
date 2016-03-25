package ru.ok.android.ui.fragments.messages.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.ImageRoundView;
import ru.ok.android.ui.custom.imageview.RoundedColorDrawable;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.utils.URLUtil;
import ru.ok.android.utils.Utils;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public class ParticipantsPreviewView extends ViewGroup {
    private final float circlePadding;
    protected TextView count;
    private HandleBlocker handleBlocker;
    boolean isBorderEnabled;
    protected int maxAvatars;
    private List<UserInfo> participants;

    /* renamed from: ru.ok.android.ui.fragments.messages.view.ParticipantsPreviewView.1 */
    class C09021 implements OnPreDrawListener {
        final /* synthetic */ int val$finalParticipantsWithoutAvatarsToUse;
        final /* synthetic */ List val$participants;

        C09021(List list, int i) {
            this.val$participants = list;
            this.val$finalParticipantsWithoutAvatarsToUse = i;
        }

        public boolean onPreDraw() {
            ParticipantsPreviewView.this.getViewTreeObserver().removeOnPreDrawListener(this);
            ParticipantsPreviewView.this.getImages(this.val$participants, this.val$finalParticipantsWithoutAvatarsToUse);
            return true;
        }
    }

    public ParticipantsPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.maxAvatars = 3;
        this.isBorderEnabled = true;
        this.circlePadding = Utils.dipToPixels(context, 1.5f);
        this.count = new TextView(context);
        this.count.setBackgroundDrawable(new RoundedColorDrawable(context.getResources().getColor(2131493156), this.circlePadding));
        this.count.setGravity(17);
        this.count.setTextColor(-2236963);
        addView(this.count);
    }

    public void setIsBorderEnabled(boolean isEnabled) {
        this.isBorderEnabled = isEnabled;
    }

    public void setMaxAvatars(int maxAvatars) {
        this.maxAvatars = maxAvatars;
    }

    public void setHandleBlocker(HandleBlocker handleBlocker) {
        this.handleBlocker = handleBlocker;
    }

    public void setParticipants(List<UserInfo> participants) {
        setParticipants(participants, true);
    }

    public List<UserInfo> getParticipants() {
        if (this.participants == null) {
            this.participants = new ArrayList();
        }
        return this.participants;
    }

    public void setParticipants(List<UserInfo> participants, boolean showCount) {
        int i = 0;
        if (participants == null) {
            throw new NullPointerException("participants can't be null");
        }
        this.participants = participants;
        int participantsWithAvatars = 0;
        for (UserInfo userInfo : participants) {
            if (!URLUtil.isStubUrl(userInfo.picUrl)) {
                participantsWithAvatars++;
            }
        }
        int actualAvatars = Math.min(this.maxAvatars, participants.size());
        int participantsWithoutAvatarsToUse = Math.max(0, actualAvatars - participantsWithAvatars);
        while (getChildCount() > actualAvatars + 1) {
            removeViewAt(0);
        }
        while (getChildCount() <= actualAvatars) {
            ImageRoundView child = new ImageRoundView(getContext(), null);
            if (this.isBorderEnabled) {
                child.setStroke(this.circlePadding);
            }
            child.setScaleType(ScaleType.CENTER_CROP);
            addView(child, getChildCount() - 1);
        }
        if (getWidth() <= 0) {
            getViewTreeObserver().addOnPreDrawListener(new C09021(participants, participantsWithoutAvatarsToUse));
        } else {
            getImages(participants, participantsWithoutAvatarsToUse);
        }
        int restCount = Math.max(0, participants.size());
        TextView textView = this.count;
        if (!showCount || restCount <= this.maxAvatars) {
            i = 8;
        }
        textView.setVisibility(i);
        if (!showCount) {
            this.count.setText(null);
        } else if (restCount > this.maxAvatars) {
            this.count.setText(String.valueOf(restCount));
        }
    }

    private void getImages(List<UserInfo> participants, int participantsWithoutAvatarsToUse) {
        Iterator<UserInfo> it = participants.iterator();
        int i = 0;
        while (i < getChildCount() - 1 && it.hasNext()) {
            UserInfo user = (UserInfo) it.next();
            if (!(!URLUtil.isStubUrl(user.picUrl))) {
                if (participantsWithoutAvatarsToUse > 0) {
                    participantsWithoutAvatarsToUse--;
                }
            }
            ImageViewManager.getInstance().displayImage(user.picUrl, (UrlImageView) getChildAt(i), user.genderType == UserGenderType.MALE ? 2130838321 : 2130837927, null);
            i++;
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (this.participants == null || this.participants.isEmpty()) {
            width = 0;
        } else {
            width = height;
        }
        if (this.participants != null) {
            width = (int) ((((float) (Math.max(0, getChildCount() - 2) * height)) * 0.75f) + ((float) width));
        }
        setMeasuredDimension(width, height);
        int spec = MeasureSpec.makeMeasureSpec(height, 1073741824);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(spec, spec);
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int currentX = Math.max(0, ((r - l) + 1) - getMeasuredWidth());
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(currentX, 0, child.getMeasuredWidth() + currentX, child.getMeasuredHeight());
            if (i < childCount - 2) {
                currentX = (int) (((float) currentX) + (((float) child.getMeasuredWidth()) * 0.75f));
            }
        }
    }
}
