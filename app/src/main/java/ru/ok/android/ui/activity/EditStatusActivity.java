package ru.ok.android.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.dialogs.ErrorReplaceDialog;
import ru.ok.android.utils.localization.LocalizationManager;

public class EditStatusActivity extends BaseActivity {
    private View mCancelView;
    private View mSaveView;
    private EditText mStatusView;

    /* renamed from: ru.ok.android.ui.activity.EditStatusActivity.1 */
    class C05451 implements OnFocusChangeListener {
        C05451() {
        }

        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                EditStatusActivity.this.getWindow().setSoftInputMode(5);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.activity.EditStatusActivity.2 */
    class C05462 implements OnClickListener {
        C05462() {
        }

        public void onClick(View v) {
            EditStatusActivity.this.setStatus(EditStatusActivity.this.mStatusView.getText().toString().trim());
        }
    }

    /* renamed from: ru.ok.android.ui.activity.EditStatusActivity.3 */
    class C05473 implements OnClickListener {
        C05473() {
        }

        public void onClick(View v) {
            EditStatusActivity.this.finish();
        }
    }

    /* renamed from: ru.ok.android.ui.activity.EditStatusActivity.4 */
    class C05484 implements TextWatcher {
        C05484() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EditStatusActivity.this.mSaveView.setEnabled(!TextUtils.isEmpty(s));
        }

        public void afterTextChanged(Editable s) {
        }
    }

    /* renamed from: ru.ok.android.ui.activity.EditStatusActivity.5 */
    class C05495 implements DialogInterface.OnClickListener {
        C05495() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            EditStatusActivity.this.setStatus(EditStatusActivity.this.mStatusView.getText().toString().trim());
        }
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String text = "";
        if (extras != null) {
            text = extras.getString("android.intent.extra.TEXT");
            if (TextUtils.isEmpty(text)) {
                finish();
                return;
            }
        }
        setContentView(2130903172);
        this.mStatusView = (EditText) findViewById(2131624717);
        this.mStatusView.setOnFocusChangeListener(new C05451());
        this.mSaveView = findViewById(2131624799);
        this.mSaveView.setOnClickListener(new C05462());
        this.mCancelView = findViewById(C0263R.id.cancel);
        this.mCancelView.setOnClickListener(new C05473());
        this.mStatusView.addTextChangedListener(new C05484());
        if (!TextUtils.isEmpty(text)) {
            this.mStatusView.setText(text);
        }
    }

    private void setStatus(String status) {
        Message msg = Message.obtain(null, 2131624116, 0, 0);
        Bundle data = new Bundle();
        data.putString(NotificationCompat.CATEGORY_STATUS, status);
        msg.setData(data);
        GlobalBus.sendMessage(msg);
    }

    @Subscribe(on = 2131623946, to = 2131624260)
    public final void onStatusUpdate(BusEvent event) {
        switch (event.resultCode) {
            case PagerAdapter.POSITION_NONE /*-2*/:
                onStatusUpdateFail();
            case RecyclerView.NO_POSITION /*-1*/:
                onStatusUpdated();
            default:
        }
    }

    public final void onStatusUpdateFail() {
        ErrorReplaceDialog dialog = new ErrorReplaceDialog(this, LocalizationManager.getString((Context) this, 2131166737), LocalizationManager.getString((Context) this, 2131165476), LocalizationManager.getString((Context) this, 2131166460));
        dialog.setOnReplaceButtonClickListener(new C05495());
        dialog.show();
    }

    public final void onStatusUpdated() {
        finish();
    }
}
