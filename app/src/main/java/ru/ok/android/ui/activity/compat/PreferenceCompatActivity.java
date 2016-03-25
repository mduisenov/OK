package ru.ok.android.ui.activity.compat;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class PreferenceCompatActivity extends PreferenceActivity {
    private Toolbar mActionBar;

    /* renamed from: ru.ok.android.ui.activity.compat.PreferenceCompatActivity.1 */
    class C05721 implements OnClickListener {
        C05721() {
        }

        public void onClick(View v) {
            PreferenceCompatActivity.this.finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActionBar.setTitle(getTitle());
    }

    public void setContentView(int layoutResID) {
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(2130903537, new LinearLayout(this), false);
        this.mActionBar = (Toolbar) contentView.findViewById(2131624641);
        this.mActionBar.setNavigationOnClickListener(new C05721());
        LayoutInflater.from(this).inflate(layoutResID, (ViewGroup) contentView.findViewById(2131624639), true);
        getWindow().setContentView(contentView);
    }
}
