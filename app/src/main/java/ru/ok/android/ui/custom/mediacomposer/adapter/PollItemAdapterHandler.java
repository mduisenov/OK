package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.TextView;
import java.util.List;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerStyleParams;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.android.ui.custom.mediacomposer.PollItem;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class PollItemAdapterHandler extends MediaItemAdapterHandler<PollItem> {
    protected PollItemAdapterHandler(Context context, LocalizationManager localizationManager, FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType, MediaComposerStyleParams styleParams, ImageHandler imageHandler) {
        super(context, localizationManager, styleParams, mediaComposerController, fragmentBridge, mediaTopicType, imageHandler);
    }

    MediaItemActionProvider createActionProvider() {
        return new PollItemActionProvider(this.fragmentBridge, this.mediaComposerController, this.mediaTopicType);
    }

    public View createView(PollItem pollItem, ViewGroup parent, boolean isEditable, int viewId) {
        LocalizationManager localizationManager = this.localizationManager;
        View view = LocalizationManager.inflate(this.context, 2130903401, parent, false);
        ((TextView) view.findViewById(2131624665)).setText(pollItem.getTitle());
        ViewGroup answersList = (ViewGroup) view.findViewById(2131625252);
        List<String> answers = pollItem.getAnswers();
        int answersCount = answers.size();
        int choiceItemDrawable = pollItem.isMultiAnswersAllowed() ? 2130837782 : 2130838620;
        for (int i = 0; i < answersCount; i++) {
            String answerText = (String) answers.get(i);
            localizationManager = this.localizationManager;
            View answerItem = LocalizationManager.inflate(this.context, 2130903098, answersList, false);
            TextView answerItemText = (TextView) answerItem.findViewById(2131624613);
            answerItemText.setText(answerText);
            answerItemText.setCompoundDrawablesWithIntrinsicBounds(choiceItemDrawable, 0, 0, 0);
            if (i == answersCount - 1) {
                ((MarginLayoutParams) answerItem.getLayoutParams()).bottomMargin = this.context.getResources().getDimensionPixelSize(2131231073);
            }
            answersList.addView(answerItem);
        }
        View itemView = createDecoratedViewWithActions(pollItem, view, parent, null);
        updateViewIsEditable(itemView, pollItem, parent, isEditable);
        itemView.setId(viewId);
        return itemView;
    }

    public void updateViewIsEditable(View view, PollItem mediaItem, ViewGroup parent, boolean isEditable) {
        super.updateViewIsEditable(view, mediaItem, parent, isEditable);
        view.setClickable(isEditable);
    }
}
