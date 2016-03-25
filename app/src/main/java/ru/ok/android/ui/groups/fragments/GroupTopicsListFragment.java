package ru.ok.android.ui.groups.fragments;

import android.app.Activity;
import android.os.Bundle;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.mediatopics.MediaTopicsListFragment;
import ru.ok.android.ui.stream.list.StreamItemAdapter.StreamAdapterListener;
import ru.ok.android.ui.stream.list.controller.MediaTopicsStreamViewController;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public class GroupTopicsListFragment extends MediaTopicsListFragment {
    private boolean isCanPin;

    public static Bundle newArguments(String userId, String groupId, String filter, Long tagId, boolean isCanPin) {
        Bundle bundle = MediaTopicsListFragment.newArguments(userId, groupId, filter, tagId);
        bundle.putBoolean("can_pin", isCanPin);
        return bundle;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.isCanPin = getArguments().getBoolean("can_pin");
    }

    protected Type getSmartEmptyViewAnimatedType() {
        return Type.GROUP_TOPICS_LIST;
    }

    protected int getMediaTopicDeleteFailedStringResId() {
        return 2131165951;
    }

    protected int getMediaTopicDeleteSuccessStringResId() {
        return 2131165952;
    }

    protected StreamItemViewController obtainStreamItemViewController(Activity activity, StreamAdapterListener streamAdapterListener, String logContext) {
        MediaTopicsStreamViewController streamItemViewController = (MediaTopicsStreamViewController) super.obtainStreamItemViewController(activity, streamAdapterListener, logContext);
        streamItemViewController.setTopicCanPinEnabled(this.isCanPin);
        return streamItemViewController;
    }

    public void setCanPinTopic(boolean isCanPin) {
        if (this.isCanPin != isCanPin) {
            this.isCanPin = isCanPin;
            if (this.streamItemRecyclerAdapter != null) {
                MediaTopicsStreamViewController mediaTopicsStreamViewController = (MediaTopicsStreamViewController) this.streamItemRecyclerAdapter.getStreamItemViewController();
                mediaTopicsStreamViewController.setTopicCanPinEnabled(isCanPin);
                mediaTopicsStreamViewController.notifyContentWithOptionsChanged();
            }
        }
    }
}
