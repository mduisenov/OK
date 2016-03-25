package ru.ok.android.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import java.util.ArrayList;
import java.util.List;

public class MessageCheckBox extends CheckBox implements OnCheckedChangeListener {
    private List<OnCheckedChangeListener> listeners;

    public MessageCheckBox(Context context) {
        super(context);
        this.listeners = new ArrayList();
        init();
    }

    public MessageCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.listeners = new ArrayList();
        init();
    }

    public MessageCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.listeners = new ArrayList();
    }

    public void addOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.listeners.add(listener);
    }

    private void init() {
        setOnCheckedChangeListener(this);
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        notifyCheckedChanged(compoundButton, b);
    }

    private void notifyCheckedChanged(CompoundButton compoundButton, boolean b) {
        for (OnCheckedChangeListener listener : this.listeners) {
            listener.onCheckedChanged(compoundButton, b);
        }
    }
}
