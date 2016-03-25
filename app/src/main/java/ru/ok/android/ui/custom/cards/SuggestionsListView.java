package ru.ok.android.ui.custom.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SuggestionsListView extends ListView implements OnItemClickListener, OnItemLongClickListener {
    private ArrayAdapter<String> adapter;
    private OnSuggestionClickListener onSuggestionClickListener;
    private OnSuggestionLongClickListener onSuggestionLongClickListener;
    private List<String> suggestions;

    public interface OnSuggestionClickListener {
        void onClick(SuggestionsListView suggestionsListView, View view, int i, String str);
    }

    public interface OnSuggestionLongClickListener {
        void onLongClick(SuggestionsListView suggestionsListView, View view, int i, String str);
    }

    public SuggestionsListView(Context context) {
        super(context);
        this.suggestions = new ArrayList();
        onCreate();
    }

    public SuggestionsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.suggestions = new ArrayList();
        onCreate();
    }

    public SuggestionsListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.suggestions = new ArrayList();
        onCreate();
    }

    private void onCreate() {
        this.adapter = new ArrayAdapter(getContext(), 2130903428, this.suggestions);
        setAdapter(this.adapter);
        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
    }

    public void setSuggestions(String... suggestions) {
        this.suggestions.clear();
        Collections.addAll(this.suggestions, suggestions);
        this.adapter.notifyDataSetInvalidated();
    }

    public void setSuggestions(Collection<String> suggestions) {
        this.suggestions.clear();
        this.suggestions.addAll(suggestions);
        this.adapter.notifyDataSetInvalidated();
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (this.onSuggestionClickListener != null) {
            this.onSuggestionClickListener.onClick(this, view, position, (String) this.suggestions.get(position));
        }
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (this.onSuggestionLongClickListener != null) {
            this.onSuggestionLongClickListener.onLongClick(this, view, position, (String) this.suggestions.get(position));
        }
        return true;
    }

    public void setOnSuggestionClickListener(OnSuggestionClickListener onSuggestionClickListener) {
        this.onSuggestionClickListener = onSuggestionClickListener;
    }

    public void setOnSuggestionLongClickListener(OnSuggestionLongClickListener onSuggestionLongClickListener) {
        this.onSuggestionLongClickListener = onSuggestionLongClickListener;
    }
}
