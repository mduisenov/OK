package ru.ok.android.ui.mediatopics;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import org.jivesoftware.smack.packet.Stanza;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.bus.BusMediatopicHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.ServicesSettingsHelper;

public final class MediaTopicTextEditActivity extends BaseActivity implements TextWatcher {
    private int blockIndex;
    private int completedResourceId;
    private EditText editText;
    private int errorResourceId;
    private String initialText;
    private MenuItem sendItem;
    private String topicId;

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setContentView(2130903080);
        this.editText = (EditText) findViewById(2131624538);
        this.editText.addTextChangedListener(this);
        int maxLength = ServicesSettingsHelper.getServicesSettings().getMultichatMaxTextLength();
        this.editText.setFilters(new InputFilter[]{new LengthFilter(maxLength)});
        this.initialText = getIntent().getStringExtra(Stanza.TEXT);
        this.topicId = getIntent().getStringExtra("topic_id");
        this.blockIndex = getIntent().getIntExtra("block_index", 0);
        this.completedResourceId = getIntent().getIntExtra("completed_resource_id", 2131166121);
        this.errorResourceId = getIntent().getIntExtra("error_resource_id", 2131166120);
        if (savedInstanceState == null) {
            this.editText.setText(this.initialText);
            this.editText.setSelection(this.editText.length());
        }
        setTitle(getStringLocalized(getIntent().getIntExtra("title_resource_id", 2131165729), Integer.valueOf(0)));
        this.editText.requestFocus();
        KeyBoardUtils.showKeyBoard(this.editText);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        LocalizationManager.inflate((Context) this, getMenuInflater(), 2131689504, menu);
        this.sendItem = menu.findItem(2131624519);
        updateSendItemVisibility();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131624519:
                this.sendItem.setEnabled(false);
                this.editText.setEnabled(false);
                BusMediatopicHelper.editText(this.topicId, this.editText.getText().toString(), this.blockIndex);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624230)
    public void onMediaTopicTextEdit(BusEvent event) {
        if (event.resultCode == -1) {
            Toast.makeText(getContext(), this.completedResourceId, 1).show();
            setResult(-1);
            finish();
            return;
        }
        int errorResource;
        this.sendItem.setEnabled(true);
        this.editText.setEnabled(true);
        ErrorType error = ErrorType.from(event.bundleOutput);
        if (error != ErrorType.GENERAL) {
            errorResource = error.getDefaultErrorMessage();
        } else {
            errorResource = this.errorResourceId;
        }
        Toast.makeText(getContext(), errorResource, 1).show();
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateSendItemVisibility();
    }

    public void afterTextChanged(Editable s) {
    }

    private void updateSendItemVisibility() {
        if (this.sendItem != null) {
            String trimmedText = this.editText.getText().toString().trim();
            MenuItem menuItem = this.sendItem;
            boolean z = trimmedText.length() > 0 && !TextUtils.equals(trimmedText, this.initialText);
            menuItem.setEnabled(z);
        }
    }
}
