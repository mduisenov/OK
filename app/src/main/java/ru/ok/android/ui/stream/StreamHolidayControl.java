package ru.ok.android.ui.stream;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;
import ru.ok.android.fragments.web.shortlinks.SendPresentShortLinkBuilder;
import ru.ok.android.ui.stream.view.HolidayView.HolidayViewListener;
import ru.ok.android.ui.stream.view.PromoLinkAndHolidayView;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.model.UserInfo;
import ru.ok.model.stream.Holiday;
import ru.ok.model.stream.Holidays;

class StreamHolidayControl implements HolidayViewListener {
    private static int position;
    private final Activity activity;
    private List<Holiday> holidays;
    private final PromoLinkAndHolidayView view;

    static {
        position = 0;
    }

    public StreamHolidayControl(@NonNull PromoLinkAndHolidayView view, @NonNull Activity activity) {
        this.view = view;
        this.activity = activity;
        view.holidayView.setListener(this);
    }

    public void nextHoliday() {
        if (this.holidays != null) {
            position++;
            setHoliday();
        }
    }

    public void updateHolidays(@Nullable Holidays holidaysLocal) {
        if (holidaysLocal != null) {
            this.holidays = holidaysLocal.getHolidays();
        } else {
            this.holidays = null;
        }
        setHoliday();
    }

    public void onHolidayClicked(@NonNull Holiday holiday) {
        if (!holiday.getUsers().isEmpty()) {
            NavigationHelper.showExternalUrlPage(this.activity, SendPresentShortLinkBuilder.choosePresentWithSelectedUser(((UserInfo) holiday.getUsers().get(0)).uid).setHoliday(holiday.getId()).setOrigin(getOrigin(holiday)).build(), false);
        }
    }

    public void onUserAvatarClicked(@NonNull UserInfo userInfo) {
        NavigationHelper.showUserInfo(this.activity, userInfo.getId());
    }

    private void setHoliday() {
        if (this.holidays == null || this.holidays.isEmpty()) {
            this.view.setHoliday(null);
            return;
        }
        if (position >= this.holidays.size()) {
            position = 0;
        }
        this.view.setHoliday((Holiday) this.holidays.get(position));
    }

    @NonNull
    private String getOrigin(@NonNull Holiday holiday) {
        if (holiday.isNameday()) {
            return "8";
        }
        if (holiday.isBirthday()) {
            return "J";
        }
        return "K";
    }
}
