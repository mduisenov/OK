package ru.ok.android.ui.adapters.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import ru.ok.android.utils.localization.LocalizationManager;

public class HeaderMusicAdapter {
    private Context context;
    private ViewHandler handler;
    int imageId;
    private LayoutInflater inflater;
    final boolean showDivider;
    int textId;

    class ViewHandler {
        View convertView;
        SimpleDraweeView image;
        TextView textName;

        ViewHandler() {
        }
    }

    public HeaderMusicAdapter(Context context, int textRes, int imageRes, boolean showDivider) {
        this.context = context;
        this.textId = textRes;
        this.imageId = imageRes;
        this.showDivider = showDivider;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(ViewGroup parent) {
        if (this.handler == null) {
            this.handler = createNewMusicView(parent);
            initViewHandler(this.handler);
        }
        return this.handler.convertView;
    }

    public void setSelection(boolean selection) {
        if (this.handler != null && this.handler.convertView != null) {
            this.handler.convertView.setSelected(selection);
        }
    }

    private ViewHandler createNewMusicView(ViewGroup parent) {
        View newView = this.inflater.inflate(2130903255, parent, false);
        ViewHandler handler = new ViewHandler();
        handler.convertView = newView;
        handler.textName = (TextView) newView.findViewById(2131624977);
        handler.image = (SimpleDraweeView) newView.findViewById(2131624657);
        ((GenericDraweeHierarchy) handler.image.getHierarchy()).setFadeDuration(0);
        return handler;
    }

    protected void initViewHandler(ViewHandler handler) {
        handler.textName.setText(LocalizationManager.getString(this.context, this.textId));
        handler.image.setImageResource(this.imageId);
    }
}
