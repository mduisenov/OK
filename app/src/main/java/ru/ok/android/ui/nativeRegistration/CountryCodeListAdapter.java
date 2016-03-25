package ru.ok.android.ui.nativeRegistration;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.utils.CountryUtil.Country;
import ru.ok.android.utils.StringUtils;

public class CountryCodeListAdapter extends BaseAdapter {
    private Context context;
    private List<Country> countries;
    private int countryCodeColor;
    private List<Country> filteredCountries;
    private int resourceId;
    private Country selectedCountry;
    private int selectedItemColor;
    private Drawable selectedItemIcon;

    private static class ViewHolder {
        TextView firstLetter;
        TextView textField;

        private ViewHolder() {
        }
    }

    public CountryCodeListAdapter(Context context, int resourceId, List<Country> countries) {
        this.context = context;
        this.countries = countries;
        this.resourceId = resourceId;
        this.filteredCountries = countries;
        this.selectedItemColor = context.getResources().getColor(2131493081);
        this.countryCodeColor = context.getResources().getColor(2131493005);
        this.selectedItemIcon = context.getResources().getDrawable(2130838019);
    }

    public void setSelection(Country country) {
        this.selectedCountry = country;
    }

    public int getCount() {
        return this.filteredCountries.size();
    }

    public Object getItem(int position) {
        return this.filteredCountries.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public void filterData(String s) {
        if (StringUtils.isEmpty(s)) {
            this.filteredCountries = this.countries;
        } else {
            if (s.startsWith("+")) {
                s = s.substring(1);
            }
            ArrayList<Country> tmp = new ArrayList();
            for (Country country : this.countries) {
                if (country.getDisplayName().toUpperCase().contains(s.toUpperCase()) || String.valueOf(country.getZip()).startsWith(s)) {
                    tmp.add(country);
                }
            }
            this.filteredCountries = tmp;
        }
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(this.resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textField = (TextView) convertView.findViewById(2131624838);
            viewHolder.firstLetter = (TextView) convertView.findViewById(2131624837);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Country currentCountry = (Country) this.filteredCountries.get(position);
        if (position == 0 || currentCountry.getDisplayName().charAt(0) != ((Country) this.filteredCountries.get(position - 1)).getDisplayName().charAt(0)) {
            viewHolder.firstLetter.setText(String.valueOf(currentCountry.getDisplayName().charAt(0)));
        } else {
            viewHolder.firstLetter.setText("");
        }
        String countryFieldText = currentCountry.getDisplayName() + " +" + currentCountry.getZip();
        if (this.selectedCountry == null || !currentCountry.getDisplayName().equals(this.selectedCountry.getDisplayName())) {
            SpannableString spannableString = new SpannableString(countryFieldText);
            spannableString.setSpan(new ForegroundColorSpan(this.countryCodeColor), countryFieldText.indexOf("+"), countryFieldText.length(), 17);
            viewHolder.textField.setTextColor(ViewCompat.MEASURED_STATE_MASK);
            viewHolder.textField.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            viewHolder.textField.setText(spannableString, BufferType.SPANNABLE);
        } else {
            viewHolder.textField.setTextColor(this.selectedItemColor);
            viewHolder.textField.setCompoundDrawablesWithIntrinsicBounds(null, null, this.selectedItemIcon, null);
            viewHolder.textField.setText(countryFieldText, BufferType.NORMAL);
        }
        return convertView;
    }
}
