package ru.ok.android.ui.search.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import ru.ok.android.services.processors.SearchCitiesProcessor;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;
import ru.ok.model.search.SearchCityResult;

public class CitiesAutoCompleteAdapter extends SearchBaseAdapter<SearchCityResult> {
    Context context;

    public CitiesAutoCompleteAdapter(Context context) {
        this.context = context;
    }

    public long getItemId(int position) {
        return ((SearchCityResult) getItem(position)).id;
    }

    protected ArrayList<SearchCityResult> performFiltering(CharSequence constraint) throws Exception {
        ArrayList<SearchCityResult> cities = new ArrayList();
        if (constraint == null) {
            return cities;
        }
        return SearchCitiesProcessor.searchCities(constraint.toString(), Settings.getCurrentLocale(this.context));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        SearchCityResult city = (SearchCityResult) getItem(position);
        if (convertView == null) {
            textView = (TextView) LocalizationManager.inflate(this.context, 2130903425, parent, false);
        } else {
            textView = (TextView) convertView;
        }
        textView.setText(city.getCitySummary());
        return textView;
    }
}
