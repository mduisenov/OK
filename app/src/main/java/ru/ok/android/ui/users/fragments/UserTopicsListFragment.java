package ru.ok.android.ui.users.fragments;

import android.app.Activity;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.mediatopics.MediaTopicsListFragment;
import ru.ok.android.ui.stream.list.StreamItemAdapter.StreamAdapterListener;
import ru.ok.android.ui.stream.list.controller.MediaTopicsStreamViewController;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.settings.Settings;

public class UserTopicsListFragment extends MediaTopicsListFragment {
    protected Type getSmartEmptyViewAnimatedType() {
        return Settings.getCurrentUser(getContext()).uid.equals(this.userId) ? Type.MY_TOPICS_LIST : Type.USER_TOPICS_LIST;
    }

    protected int getMediaTopicDeleteFailedStringResId() {
        return 2131166794;
    }

    protected int getMediaTopicDeleteSuccessStringResId() {
        return 2131166795;
    }

    protected StreamItemViewController obtainStreamItemViewController(Activity activity, StreamAdapterListener streamAdapterListener, String logContext) {
        MediaTopicsStreamViewController streamItemViewController = (MediaTopicsStreamViewController) super.obtainStreamItemViewController(activity, streamAdapterListener, logContext);
        streamItemViewController.setTopicToStatusEnabled(OdnoklassnikiApplication.getCurrentUser().getId().equals(this.userId));
        return streamItemViewController;
    }
}
