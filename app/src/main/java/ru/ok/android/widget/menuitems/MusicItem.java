package ru.ok.android.widget.menuitems;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.MenuView;
import ru.ok.android.widget.MenuView.MenuItem;
import ru.ok.android.widget.MenuView.ViewHolder;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;

public class MusicItem extends MenuItem {
    private static int icon_size;
    private Activity activity;
    private boolean isMusicInit;
    private boolean isPause;
    private OnClickListener onClickListener;
    private CharSequence trackInfo;

    class Holder extends ViewHolder {
        public ImageView buttonNext;
        public ImageView buttonPlay;
        public ImageView buttonPrew;
        public View icon;
        public View textMusic;
        public TextView textName;

        public Holder(int type, int position) {
            super(type, position);
        }
    }

    static {
        icon_size = 0;
    }

    public MusicItem(Activity activity, int height) {
        super(height, Type.music);
        this.isMusicInit = false;
        this.isPause = false;
        this.trackInfo = null;
        this.onClickListener = null;
        this.activity = activity;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setIsMusicInit(boolean isMusicInit) {
        this.isMusicInit = isMusicInit;
    }

    public void setIsPause(boolean isPause) {
        this.isPause = isPause;
    }

    public void onClick(MenuView menuView, MenuItem item) {
        super.onClick(menuView, item);
        NavigationHelper.showMusicPage(this.activity);
    }

    public void setTrackInfo(CharSequence trackInfo) {
        this.trackInfo = trackInfo;
    }

    public int getType() {
        return 2;
    }

    public View getView(LocalizationManager inflater, View view, int position, Type selectedItem) {
        Holder holder;
        boolean isSelected;
        if (icon_size == 0) {
            icon_size = inflater.getContext().getResources().getDrawable(2130838405).getMinimumWidth();
        }
        if (view == null) {
            holder = createViewHolder(getType(), position);
            view = LocalizationManager.inflate(inflater.getContext(), 2130903316, null, false);
            holder.buttonNext = (ImageView) view.findViewById(2131625074);
            holder.buttonPlay = (ImageView) view.findViewById(2131625075);
            holder.buttonPrew = (ImageView) view.findViewById(2131625072);
            holder.textMusic = view.findViewById(2131625064);
            holder.textName = (TextView) view.findViewById(2131625073);
            holder.buttonNext.setOnClickListener(this.onClickListener);
            holder.buttonPrew.setOnClickListener(this.onClickListener);
            holder.buttonPlay.setOnClickListener(this.onClickListener);
            holder.icon = view.findViewById(2131625062);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
            holder.position = position;
        }
        int visibilitySubViews = this.isMusicInit ? 0 : 8;
        holder.buttonPlay.setVisibility(visibilitySubViews);
        holder.buttonPrew.setVisibility(visibilitySubViews);
        holder.buttonNext.setVisibility(visibilitySubViews);
        holder.textName.setVisibility(visibilitySubViews);
        holder.buttonPlay.setImageResource(this.isPause ? 2130838352 : 2130838349);
        holder.textName.setText(this.trackInfo);
        if (selectedItem == Type.music) {
            isSelected = true;
        } else {
            isSelected = false;
        }
        holder.textName.setSelected(isSelected);
        holder.icon.setSelected(isSelected);
        holder.textMusic.setSelected(isSelected);
        return view;
    }

    public Holder createViewHolder(int type, int position) {
        return new Holder(type, position);
    }
}
