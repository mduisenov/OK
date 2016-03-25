package ru.ok.android.ui.stream.list;

import android.support.annotation.NonNull;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.FeedFooterView;

interface FeedFooterViewHolder {
    FeedFooterView getFeedFooterView(@NonNull StreamItemViewController streamItemViewController);

    void hideFeedFooterView();

    void setTagFor(FeedFooterView feedFooterView);
}
