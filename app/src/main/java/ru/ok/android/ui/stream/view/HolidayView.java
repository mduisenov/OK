package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.imageview.RoundAvatarImageView;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.stream.Holiday;

public class HolidayView extends LinearLayout implements OnClickListener {
    private RoundAvatarImageView avatarImg;
    private Holiday holiday;
    private HolidayViewListener listener;
    private TextView messageTxt;
    private UserInfo userInfo;

    public interface HolidayViewListener {
        void onHolidayClicked(@NonNull Holiday holiday);

        void onUserAvatarClicked(@NonNull UserInfo userInfo);
    }

    public HolidayView(@NonNull Context context) {
        super(context);
        init();
    }

    public HolidayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setHoliday(@NonNull Holiday holiday, @Nullable HandleBlocker handleBlocker) {
        this.holiday = holiday;
        if (!holiday.getUsers().isEmpty()) {
            this.userInfo = (UserInfo) holiday.getUsers().get(0);
            ImageViewManager.getInstance().displayImage(this.userInfo.getPicUrl(), this.avatarImg, this.userInfo.genderType == UserGenderType.MALE, handleBlocker);
        }
        this.messageTxt.setText(createMessage());
    }

    public void setListener(@Nullable HolidayViewListener listener) {
        this.listener = listener;
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(2130903256, this);
        this.avatarImg = (RoundAvatarImageView) findViewById(2131624980);
        this.messageTxt = (TextView) findViewById(2131624981);
        this.avatarImg.setOnClickListener(this);
        setOnClickListener(this);
        setOrientation(0);
    }

    @NonNull
    private Spanned createMessage() {
        return Html.fromHtml(String.format("<b>%s</b> %s %s", new Object[]{this.userInfo.name, getText(this.holiday.getType(), this.userInfo.genderType), this.holiday.getMessage()}));
    }

    @NonNull
    private String getText(int holidayType, @NonNull UserGenderType gender) {
        int textId;
        switch (holidayType) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                textId = 2131165976;
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                textId = 2131165977;
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                if (gender != UserGenderType.FEMALE) {
                    textId = 2131165979;
                    break;
                }
                textId = 2131165978;
                break;
            default:
                textId = 2131165976;
                Logger.m184w("invalid holiday type");
                break;
        }
        return LocalizationManager.getString(getContext(), textId);
    }

    public void onClick(View view) {
        if (this.listener != null) {
            if (view == this.avatarImg) {
                this.listener.onUserAvatarClicked(this.userInfo);
            } else {
                this.listener.onHolidayClicked(this.holiday);
            }
        }
    }
}
