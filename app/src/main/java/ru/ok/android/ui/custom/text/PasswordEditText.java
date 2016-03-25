package ru.ok.android.ui.custom.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.regex.Pattern;
import ru.ok.android.C0206R;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;

public final class PasswordEditText extends LinearLayout {
    private static final Pattern mustContainPattern;
    private static final Pattern unAllowedSymbolsPattern;
    private CheckBox checkBox;
    private EditText editText;
    private boolean isPasswordAlwaysVisible;
    private TextWatcher textChangedListener;
    private TextView textView;
    private boolean validatePassword;

    /* renamed from: ru.ok.android.ui.custom.text.PasswordEditText.1 */
    class C07541 implements TextWatcher {
        C07541() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (PasswordEditText.this.textChangedListener != null) {
                PasswordEditText.this.textChangedListener.beforeTextChanged(s, start, count, after);
            }
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (PasswordEditText.this.textChangedListener != null) {
                PasswordEditText.this.textChangedListener.onTextChanged(s, start, before, count);
            }
            PasswordEditText.this.validatePassword(true);
        }

        public void afterTextChanged(Editable s) {
            if (PasswordEditText.this.textChangedListener != null) {
                PasswordEditText.this.textChangedListener.afterTextChanged(s);
            }
            if (!PasswordEditText.this.isPasswordAlwaysVisible) {
                if (StringUtils.isEmpty(s.toString())) {
                    PasswordEditText.this.checkBox.setVisibility(8);
                } else {
                    PasswordEditText.this.checkBox.setVisibility(0);
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.text.PasswordEditText.2 */
    class C07552 implements OnCheckedChangeListener {
        C07552() {
        }

        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            int selectionPosition = PasswordEditText.this.editText.getSelectionEnd();
            if (b) {
                PasswordEditText.this.editText.setTransformationMethod(null);
                PasswordEditText.this.editText.setSelection(selectionPosition);
                return;
            }
            PasswordEditText.this.editText.setTransformationMethod(new PasswordTransformationMethod());
            PasswordEditText.this.editText.setSelection(selectionPosition);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.text.PasswordEditText.3 */
    class C07563 implements OnClickListener {
        C07563() {
        }

        public void onClick(View v) {
            PasswordEditText.this.validatePassword(true);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.text.PasswordEditText.4 */
    class C07574 implements OnFocusChangeListener {
        C07574() {
        }

        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                PasswordEditText.this.validatePassword(true);
            } else {
                PasswordEditText.this.hideValidation();
            }
        }
    }

    private enum PASSWORD_STRENGTH {
        EMPTY(2130838301, 2131493136),
        CONTAINS_UNALLOWED_SYMBOLS(2130838301, 2131493136),
        VERY_POOR(2130838302, 2131493136),
        POOR(2130838303, 2131493210),
        OK(2130838299, 2131493007);
        
        private int editTextBackgroundId;
        private int textViewTextColorId;

        private PASSWORD_STRENGTH(int editTextBackgroundId, int textViewTextColorId) {
            this.editTextBackgroundId = editTextBackgroundId;
            this.textViewTextColorId = textViewTextColorId;
        }
    }

    static {
        unAllowedSymbolsPattern = Pattern.compile("[^a-zA-Z\\d!#$%^&*()_\\-+]");
        mustContainPattern = Pattern.compile("(?=.*[a-zA-Z])(?=.*\\d)");
    }

    public PasswordEditText(Context context) {
        this(context, null);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        int gravity;
        int paddingRight;
        int paddingLeft;
        super(context, attrs, defStyleAttr);
        init();
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, C0206R.styleable.PasswordEditText, defStyleAttr, 0);
        this.editText.setHint(LocalizationManager.getString(getContext(), typedArray.getResourceId(0, 2131166328)));
        this.validatePassword = typedArray.getBoolean(2, false);
        if (typedArray.getInt(5, 0) == 0) {
            gravity = 3;
        } else {
            gravity = 1;
        }
        this.editText.setGravity(gravity);
        this.isPasswordAlwaysVisible = typedArray.getBoolean(1, false);
        if (typedArray.hasValue(3)) {
            paddingRight = (int) typedArray.getDimension(3, 0.0f);
        } else {
            paddingRight = this.isPasswordAlwaysVisible ? 0 : (int) getResources().getDimension(2131231031);
        }
        if (typedArray.hasValue(4)) {
            paddingLeft = (int) typedArray.getDimension(4, 0.0f);
        } else {
            paddingLeft = (gravity != 1 || this.isPasswordAlwaysVisible) ? 0 : paddingRight;
        }
        this.editText.setPadding(paddingLeft, 0, paddingRight, 0);
        if (this.isPasswordAlwaysVisible) {
            this.checkBox.setVisibility(8);
            this.editText.setTransformationMethod(null);
        }
        validatePassword();
        typedArray.recycle();
    }

    public void setValidatePassword(boolean validatePassword, boolean isEmptyOk) {
        this.validatePassword = validatePassword;
        validatePassword(isEmptyOk);
    }

    public void setValidatePassword(boolean validatePassword) {
        setValidatePassword(validatePassword, false);
    }

    public void setTextChangedListener(TextWatcher textChangedListener) {
        this.textChangedListener = textChangedListener;
    }

    public void hideValidation() {
        this.textView.setVisibility(8);
        setEditTextBackground(2130838300);
    }

    public EditText getEditText() {
        return this.editText;
    }

    public void setEditTextBackground(int id) {
        Utils.setViewBackgroundWithoutResettingPadding(this.editText, id);
    }

    private void showValidationIfNeeded() {
        this.textView.setVisibility(this.validatePassword ? 0 : 8);
    }

    public void setText(String text) {
        this.editText.setText(text);
    }

    public String getText() {
        return this.editText.getText().toString();
    }

    public void clearText() {
        this.editText.getText().clear();
    }

    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return this.editText.requestFocus(direction, previouslyFocusedRect);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(2130903370, this, true);
        this.editText = (EditText) findViewById(2131625162);
        this.checkBox = (CheckBox) findViewById(2131625163);
        this.textView = (TextView) findViewById(2131625164);
        this.editText.addTextChangedListener(new C07541());
        this.checkBox.setOnCheckedChangeListener(new C07552());
        this.editText.setOnClickListener(new C07563());
        this.editText.setOnFocusChangeListener(new C07574());
    }

    public boolean validatePassword() {
        return validatePassword(false);
    }

    public boolean validatePassword(boolean okIfEmpty) {
        showValidationIfNeeded();
        return !this.validatePassword || validatePassword(this.editText.getText().toString(), okIfEmpty);
    }

    private boolean isPasswordLong(String password) {
        return password.length() > 5;
    }

    private boolean validatePassword(String password, boolean okIfEmpty) {
        int messageId = 2131166330;
        PASSWORD_STRENGTH passwordStrength = PASSWORD_STRENGTH.EMPTY;
        if (!StringUtils.isEmpty(password)) {
            if (unAllowedSymbolsPattern.matcher(password).find()) {
                passwordStrength = PASSWORD_STRENGTH.CONTAINS_UNALLOWED_SYMBOLS;
                messageId = 2131166334;
            } else if (!mustContainPattern.matcher(password).find()) {
                messageId = 2131166329;
                if (isPasswordLong(password)) {
                    passwordStrength = PASSWORD_STRENGTH.POOR;
                } else {
                    passwordStrength = PASSWORD_STRENGTH.VERY_POOR;
                }
            } else if (isPasswordLong(password)) {
                messageId = 2131166331;
                passwordStrength = PASSWORD_STRENGTH.OK;
            } else {
                passwordStrength = PASSWORD_STRENGTH.POOR;
                messageId = 2131166332;
            }
        }
        if (passwordStrength == PASSWORD_STRENGTH.EMPTY && okIfEmpty) {
            hideValidation();
            return true;
        }
        this.textView.setText(LocalizationManager.getString(getContext(), messageId));
        setEditTextBackground(passwordStrength.editTextBackgroundId);
        this.textView.setTextColor(getResources().getColor(passwordStrength.textViewTextColorId));
        if (passwordStrength != PASSWORD_STRENGTH.OK) {
            return false;
        }
        return true;
    }
}
