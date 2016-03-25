package ru.ok.android.ui.adapters.music.collections;

import android.view.View;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.imageview.UrlImageView;

public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
    public View dots;
    public UrlImageView image;
    public TextView textCollectionName;
    public TextView textCount;

    public ViewHolder(View view) {
        super(view);
        this.textCollectionName = (TextView) view.findViewById(2131624982);
        this.textCount = (TextView) view.findViewById(2131624983);
        this.image = (UrlImageView) view.findViewById(C0263R.id.image);
        this.dots = view.findViewById(2131624874);
    }
}
