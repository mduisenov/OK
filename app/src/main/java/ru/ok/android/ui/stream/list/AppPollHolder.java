package ru.ok.android.ui.stream.list;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.AbsListView.LayoutParams;
import android.widget.TextView;
import org.json.JSONArray;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskService;
import ru.ok.android.services.processors.poll.AppPollPreferences;
import ru.ok.android.services.processors.poll.UploadAppPollAnswerTask;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.ViewUtil;

public class AppPollHolder extends ViewHolder implements OnClickListener {
    private TextView closeView;
    private final Activity context;
    private TextView descrView;
    private boolean isOpened;
    private TextView lastShowTextView;
    private TextView lastUpdateTextView;
    private StreamLayoutConfig layoutConfig;
    private TextView openView;
    private View pollView;
    private final ViewStub testInfoStub;
    private View testInfoView;
    private TextView titleView;
    private final ViewStub viewStub;

    public AppPollHolder(View view, Activity activity) {
        super(view);
        this.isOpened = false;
        this.viewStub = (ViewStub) view.findViewById(2131625320);
        this.testInfoStub = (ViewStub) view.findViewById(2131625319);
        this.context = activity;
        initIfNessecary(this.testInfoStub);
    }

    private void initIfNessecary(ViewStub testInfoStub) {
        if (AppPollPreferences.isAppPollRepeatMode(this.context) && this.testInfoView == null) {
            this.testInfoView = testInfoStub.inflate();
            this.lastUpdateTextView = (TextView) this.testInfoView.findViewById(2131625322);
            this.lastShowTextView = (TextView) this.testInfoView.findViewById(2131625323);
            ViewUtil.resetLayoutParams(this.testInfoView, -1, -2);
            this.itemView.setBackgroundResource(2130837902);
            ViewUtil.visible(this.itemView, this.testInfoView);
            if (this.itemView.getLayoutParams() == null) {
                this.itemView.setLayoutParams(new LayoutParams(-1, -2));
            } else {
                ViewUtil.resetLayoutParams(this.itemView, -1, -2);
            }
        }
    }

    public void inflate() {
        this.pollView = this.viewStub.inflate();
        this.openView = (TextView) this.pollView.findViewById(2131625318);
        this.closeView = (TextView) this.pollView.findViewById(2131625317);
        this.titleView = (TextView) this.pollView.findViewById(2131625314);
        this.descrView = (TextView) this.pollView.findViewById(2131625315);
        this.closeView.setOnClickListener(this);
        this.openView.setOnClickListener(this);
        close();
    }

    public void update(boolean isStreamEmpty) {
        boolean isTimeToShow = AppPollPreferences.isTimeToShowPoll(this.context);
        boolean reallyShow = timeToShow(isStreamEmpty, isTimeToShow);
        Logger.m172d("Update: stream empty" + isStreamEmpty + "; Is time to show:" + isTimeToShow + "; Really:" + reallyShow);
        if (reallyShow) {
            if (this.pollView == null) {
                inflate();
            }
            open();
            if (isTimeToShow) {
                AppPollPreferences.setLastDisplayTime(this.context, System.currentTimeMillis());
                Context context = this.context;
                PersistentTaskService.submit(r18, new UploadAppPollAnswerTask(OdnoklassnikiApplication.getCurrentUser().getId(), AppPollPreferences.getVersion(this.context), false, true, 0, new JSONArray().toString()));
            }
            if (this.layoutConfig != null) {
                Logger.m172d("Show layout update");
                int extraMargin = this.layoutConfig.getExtraMarginForLandscapeAsInPortrait(true);
                StreamItem.applyExtraMarginsToBg(this.itemView, extraMargin, extraMargin);
                this.itemView.setPadding(this.originalLeftPadding + extraMargin, this.originalTopPadding, this.originalRightPadding + extraMargin, this.originalBottomPadding);
            }
        } else if (this.pollView != null) {
            close();
        }
        if (AppPollPreferences.isAppPollRepeatMode(this.context)) {
            initIfNessecary(this.testInfoStub);
            long updateTime = AppPollPreferences.getLastUpdateTime(this.context);
            long showTime = AppPollPreferences.getLastDisplayTime(this.context);
            this.lastUpdateTextView.setText(DateFormatter.formatHHmm(updateTime));
            this.lastShowTextView.setText(DateFormatter.formatHHmm(showTime));
        }
    }

    public void updateForLayoutSize(StreamLayoutConfig layoutConfig) {
        this.layoutConfig = layoutConfig;
        int extraMargin = layoutConfig.getExtraMarginForLandscapeAsInPortrait(true);
        StreamItem.applyExtraMarginsToBg(this.itemView, extraMargin, extraMargin);
        this.itemView.setPadding(this.originalLeftPadding + extraMargin, this.originalTopPadding, this.originalRightPadding + extraMargin, this.originalBottomPadding);
    }

    private void close() {
        ViewUtil.gone(this.pollView);
        ViewUtil.resetLayoutParams(this.pollView, 0, 0);
        if (AppPollPreferences.isAppPollRepeatMode(this.context)) {
            initIfNessecary(this.testInfoStub);
            this.itemView.setBackgroundResource(2130837902);
            ViewUtil.visible(this.itemView, this.testInfoView);
            ViewUtil.resetLayoutParams(this.itemView, -1, -2);
        } else {
            this.itemView.setBackgroundResource(2131492919);
            ViewUtil.gone(this.itemView, this.testInfoView);
            ViewUtil.resetLayoutParams(this.itemView, 0, 0);
        }
        this.isOpened = false;
    }

    private void open() {
        if (!this.isOpened) {
            String openText;
            String titleText;
            String descrText;
            String closeText;
            synchronized (AppPollPreferences.class) {
                openText = getText(AppPollPreferences.isStarted(this.context) ? "app_poll_stream_resume" : "app_poll_stream_start");
                titleText = getText("app_poll_stream_title");
                descrText = getText("app_poll_stream_description");
                closeText = getText("app_poll_stream_cancel");
            }
            if (StringUtils.allNotEmpty(openText, titleText, descrText, closeText)) {
                this.openView.setText(openText);
                this.titleView.setText(titleText);
                this.descrView.setText(descrText);
                this.closeView.setText(closeText);
                this.itemView.setBackgroundResource(2130837907);
                ViewUtil.visible(this.itemView, this.pollView, this.testInfoView);
                ViewUtil.resetLayoutParams(this.pollView, -1, -2);
                ViewUtil.resetLayoutParams(this.itemView, -1, -2);
                if (AppPollPreferences.isAppPollRepeatMode(this.context)) {
                    initIfNessecary(this.testInfoStub);
                    ViewUtil.resetLayoutParams(this.testInfoView, -1, -2);
                }
                this.isOpened = true;
                return;
            }
            close();
        }
    }

    private String getText(String key) {
        return AppPollPreferences.getTextByKey(this.context, key);
    }

    private boolean timeToShow(boolean isStreamEmpty, boolean timeToShowPoll) {
        return ((!AppPollPreferences.isStarted(this.context) && !timeToShowPoll && !AppPollPreferences.isInShowingInterval(this.context)) || isStreamEmpty || AppPollPreferences.getVersion(this.context) == 0) ? false : true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case 2131625317:
                boolean cancel;
                if (AppPollPreferences.isAppPollRepeatMode(this.context)) {
                    cancel = false;
                } else {
                    cancel = true;
                }
                PersistentTask task = new UploadAppPollAnswerTask(OdnoklassnikiApplication.getCurrentUser().getId(), AppPollPreferences.getVersion(this.context), cancel, true, 0, new JSONArray().toString());
                AppPollPreferences.clearAppPoll(this.context);
                PersistentTaskService.submit(this.context, task);
                update(false);
            case 2131625318:
                update(false);
                synchronized (AppPollPreferences.class) {
                    if (AppPollPreferences.getVersion(this.context) != 0) {
                        NavigationHelper.showAppPoll(this.context);
                        AppPollPreferences.setStarted(this.context, true);
                        PersistentTaskService.submit(this.context, new UploadAppPollAnswerTask(OdnoklassnikiApplication.getCurrentUser().getId(), AppPollPreferences.getVersion(this.context), false, true, AppPollPreferences.getStep(this.context), new JSONArray().toString()));
                    }
                    break;
                }
            default:
        }
    }
}
