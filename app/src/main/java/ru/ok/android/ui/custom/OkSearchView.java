package ru.ok.android.ui.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.SearchView;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.lang.reflect.Field;
import ru.mail.libverify.C0176R;

public class OkSearchView extends SearchView {
    private EditText autoComplete;

    public OkSearchView(Context context) {
        this(context, null);
    }

    public OkSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, C0176R.attr.searchViewStyle);
    }

    public OkSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(new ContextThemeWrapper(context, 2131296790), attrs, defStyleAttr);
        init();
    }

    private void init() {
        View searchPlate = findViewById(C0176R.id.search_plate);
        if (searchPlate != null) {
            searchPlate.setBackgroundResource(17170445);
        }
        View viewEditText = findViewById(C0176R.id.search_src_text);
        if (viewEditText != null && (viewEditText instanceof EditText)) {
            EditText editText = (EditText) viewEditText;
            if (editText != null) {
                editText.setHintTextColor(getResources().getColor(getThemedAttrVal(2130772005, 2131493209)));
                editText.setTextColor(getResources().getColor(getThemedAttrVal(2130772006, 2131493208)));
                editText.setHighlightColor(getResources().getColor(2131493147));
                try {
                    Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
                    f.setAccessible(true);
                    f.set(editText, Integer.valueOf(2130837829));
                } catch (Exception e) {
                }
                editText.setHint("");
                this.autoComplete = editText;
            }
        }
    }

    public void setQueryHint(CharSequence hint) {
        if (this.autoComplete == null) {
            super.setQueryHint(hint);
            return;
        }
        try {
            Class<?> clazz = Class.forName("android.widget.SearchView$SearchAutoComplete");
            SpannableStringBuilder stopHint = new SpannableStringBuilder("   ");
            stopHint.append(hint);
            Drawable searchIcon = getResources().getDrawable(2130837593);
            int textSize = (int) (((Float) clazz.getMethod("getTextSize", new Class[0]).invoke(this.autoComplete, new Object[0])).floatValue() * 1.25f);
            searchIcon.setBounds(0, 0, textSize, textSize);
            stopHint.setSpan(new ImageSpan(searchIcon), 1, 2, 33);
            clazz.getMethod("setHint", new Class[]{CharSequence.class}).invoke(this.autoComplete, new Object[]{stopHint});
        } catch (Exception e) {
            super.setQueryHint(hint);
        }
    }

    private int getThemedAttrVal(int attrId, int defValue) {
        TypedValue val = new TypedValue();
        int ret = defValue;
        if (getContext().getTheme().resolveAttribute(attrId, val, true) && val.type == 1) {
            return val.data;
        }
        return ret;
    }
}
