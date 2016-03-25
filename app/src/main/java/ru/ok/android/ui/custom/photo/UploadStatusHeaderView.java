package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GroupInfo;
import ru.ok.model.photo.PhotoAlbumInfo;

public class UploadStatusHeaderView extends LinearLayout {
    private PhotoAlbumInfo albumInfo;
    private TextView albumNameView;
    private TextView albumPrefixView;
    private GroupInfo groupInfo;
    private TextView groupNameView;
    private TextView groupPrefixView;
    private int type;

    public UploadStatusHeaderView(Context context) {
        super(context);
        onCreate();
    }

    public UploadStatusHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public UploadStatusHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreate();
    }

    private void onCreate() {
        LayoutInflater.from(getContext()).inflate(2130903242, this, true);
        this.albumPrefixView = (TextView) findViewById(2131624947);
        this.albumNameView = (TextView) findViewById(2131624948);
        this.groupPrefixView = (TextView) findViewById(2131624949);
        this.groupNameView = (TextView) findViewById(2131624950);
        setBackgroundDrawable(getResources().getDrawable(2130838652));
    }

    public void setAlbumInfo(PhotoAlbumInfo albumInfo) {
        this.albumInfo = albumInfo;
    }

    public GroupInfo getGroupInfo() {
        return this.groupInfo;
    }

    public PhotoAlbumInfo getAlbumInfo() {
        return this.albumInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public void setType(int type) {
        this.type = type;
        updateViewsState();
    }

    private void updateViewsState() {
        this.albumNameView.setText(null);
        this.albumNameView.setVisibility(8);
        this.albumPrefixView.setText(null);
        this.albumPrefixView.setVisibility(8);
        this.groupNameView.setText(null);
        this.groupNameView.setVisibility(8);
        this.groupPrefixView.setText(null);
        this.groupPrefixView.setVisibility(8);
        switch (this.type) {
            case RECEIVED_VALUE:
                this.albumPrefixView.setVisibility(0);
                this.albumNameView.setVisibility(0);
                this.albumPrefixView.setText(LocalizationManager.getString(getContext(), 2131165369) + " ");
                if (TextUtils.isEmpty(this.albumInfo.getId())) {
                    this.albumNameView.setText(LocalizationManager.getString(getContext(), 2131166341));
                } else {
                    this.albumNameView.setText(this.albumInfo.getTitle());
                }
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.albumPrefixView.setVisibility(0);
                this.albumNameView.setVisibility(0);
                this.albumPrefixView.setText(LocalizationManager.getString(getContext(), 2131165369) + " ");
                this.albumPrefixView.append(this.albumInfo.getTitle());
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                this.albumNameView.setText(LocalizationManager.getString(getContext(), 2131165933));
            default:
        }
    }
}
