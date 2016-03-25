package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.LinkItem;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerStyleParams;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class LinkItemAdapterHandler extends MediaItemAdapterHandler<LinkItem> {
    protected LinkItemAdapterHandler(Context context, LocalizationManager localizationManager, FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType, MediaComposerStyleParams styleParams, ImageHandler imageHandler) {
        super(context, localizationManager, styleParams, mediaComposerController, fragmentBridge, mediaTopicType, imageHandler);
    }

    MediaItemActionProvider createActionProvider() {
        return new LinkItemActionProvider(this.fragmentBridge, this.mediaComposerController, this.mediaTopicType);
    }

    public View createView(LinkItem linkItem, ViewGroup parent, boolean isEditable, int viewId) {
        LocalizationManager localizationManager = this.localizationManager;
        TextView textView = (TextView) LocalizationManager.inflate(this.context, 2130903301, parent, false);
        String linkUrl = linkItem.getLinkUrl();
        if (linkUrl == null) {
            linkUrl = "";
        }
        SpannableString content = new SpannableString(linkUrl);
        content.setSpan(new UnderlineSpan(), 0, linkUrl.length(), 0);
        textView.setText(content);
        installActions(linkItem, textView, textView, null);
        updateViewIsEditable(textView, linkItem, parent, isEditable);
        textView.setId(viewId);
        return textView;
    }

    protected boolean canHaveInsertTextAction() {
        return true;
    }

    protected void onCreateActions(MediaItem item, View itemView, List<ActionItem> outActions) {
        outActions.add(new ActionItem(2131624282, 2131166318, 2130838205));
        outActions.add(new ActionItem(2131624283, 2131166457, 2130838574));
        if (canHaveInsertTextAction()) {
            outActions.add(new ActionItem(2131624281, 2131166006, 2130838211));
        }
    }
}
