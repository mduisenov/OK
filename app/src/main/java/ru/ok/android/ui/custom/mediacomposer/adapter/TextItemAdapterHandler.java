package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MaxLengthFilter;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerStyleParams;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.android.ui.custom.mediacomposer.TextItem;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class TextItemAdapterHandler extends MediaItemAdapterHandler<TextItem> {
    private InputFilter[] maxTextLengthFilter;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.adapter.TextItemAdapterHandler.1 */
    class C06931 extends SimpleTextWatcher {
        final /* synthetic */ TextItem val$textItem;

        C06931(TextItem textItem) {
            this.val$textItem = textItem;
        }

        public void afterTextChanged(Editable s) {
            this.val$textItem.setText(s.toString());
            this.val$textItem.notifyContentChanged();
        }
    }

    protected TextItemAdapterHandler(Context context, LocalizationManager localizationManager, FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType, MediaComposerStyleParams styleParams, ImageHandler imageHandler) {
        super(context, localizationManager, styleParams, mediaComposerController, fragmentBridge, mediaTopicType, imageHandler);
    }

    MediaItemActionProvider createActionProvider() {
        return new TextItemActionProvider(this.fragmentBridge, this.mediaComposerController, this.mediaTopicType);
    }

    public View createView(TextItem textItem, ViewGroup parent, boolean isEditable, int viewId) {
        LocalizationManager localizationManager = this.localizationManager;
        View editText = (EditText) LocalizationManager.inflate(this.context, 2130903306, parent, false);
        editText.setText(textItem.getText());
        editText.setFilters(getMaxTextLengthFilter());
        editText.addTextChangedListener(new C06931(textItem));
        updateViewIsEditable(editText, textItem, parent, isEditable);
        editText.setId(viewId);
        return editText;
    }

    public void updateViewIsEditable(View view, TextItem textItem, ViewGroup parent, boolean isEditable) {
        super.updateViewIsEditable(view, textItem, parent, isEditable);
        view.setFocusableInTouchMode(isEditable);
    }

    protected boolean canHaveInsertTextAction() {
        return false;
    }

    private InputFilter[] getMaxTextLengthFilter() {
        if (this.maxTextLengthFilter == null) {
            this.maxTextLengthFilter = new InputFilter[]{new MaxLengthFilter(this.mediaComposerController.getMaxTextLength(), "text_length", this.mediaTopicType)};
        }
        return this.maxTextLengthFilter;
    }
}
