package ru.ok.android.ui.adapters.places;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.places.PlaceCategory;

public class CategoriesAdapter extends BaseAdapter implements Filterable {
    private List<PlaceCategory> allData;
    private final Context context;
    private final List<PlaceCategory> data;
    private final CategoriesFilter filter;
    private List<PlaceCategory> filteredData;

    public class CategoriesFilter extends Filter {
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint == null || constraint.toString().length() <= 0) {
                synchronized (this) {
                    result.values = CategoriesAdapter.this.allData;
                    result.count = CategoriesAdapter.this.allData.size();
                }
            } else {
                ArrayList<PlaceCategory> filteredItems = new ArrayList();
                int l = CategoriesAdapter.this.allData.size();
                for (int i = 0; i < l; i++) {
                    PlaceCategory m = (PlaceCategory) CategoriesAdapter.this.allData.get(i);
                    if (m.text.toLowerCase().contains(constraint)) {
                        filteredItems.add(m);
                    }
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            return result;
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            CategoriesAdapter.this.filteredData = (ArrayList) results.values;
            CategoriesAdapter.this.data.clear();
            CategoriesAdapter.this.data.addAll(CategoriesAdapter.this.filteredData);
            CategoriesAdapter.this.notifyDataSetChanged();
        }
    }

    private static class ViewHolder {
        public UrlImageView image;
        public TextView name;

        private ViewHolder() {
        }

        public static ViewHolder createViewHolder(View convertView) {
            ViewHolder holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(2131624977);
            holder.image = (UrlImageView) convertView.findViewById(C0263R.id.image);
            return holder;
        }
    }

    public CategoriesAdapter(Context context) {
        this.allData = new ArrayList();
        this.filteredData = new ArrayList();
        this.data = new ArrayList();
        this.filter = new CategoriesFilter();
        this.context = context;
    }

    public void setCategories(List<PlaceCategory> categories) {
        clearData();
        for (PlaceCategory category : categories) {
            for (PlaceCategory subCategory : category.subCategories) {
                this.allData.add(subCategory);
            }
        }
        this.data.addAll(this.allData);
    }

    public void clearData() {
        this.data.clear();
        this.allData.clear();
        this.filteredData.clear();
    }

    public void byParentCategory(PlaceCategory category) {
        this.filteredData = category.subCategories;
        this.data.clear();
        this.data.addAll(this.filteredData);
    }

    public void clearFilter() {
        this.filteredData = this.allData;
        this.data.clear();
        this.data.addAll(this.filteredData);
    }

    public int getCount() {
        return this.data.size();
    }

    public Object getItem(int position) {
        return this.data.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public int getItemViewType(int position) {
        if (((PlaceCategory) getItem(position)).hasSubcategories()) {
            return 0;
        }
        return 1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (getItemViewType(position) == 0) {
                convertView = newView(parent);
            } else {
                convertView = newViewSubCategory(parent);
            }
        }
        bindView((ViewHolder) convertView.getTag(), (PlaceCategory) getItem(position));
        return convertView;
    }

    protected View newView(ViewGroup parent) {
        View convertView = LocalizationManager.inflate(this.context, 2130903264, parent, false);
        convertView.setTag(ViewHolder.createViewHolder(convertView));
        return convertView;
    }

    protected View newViewSubCategory(ViewGroup parent) {
        View convertView = LocalizationManager.inflate(this.context, 2130903265, parent, false);
        convertView.setTag(ViewHolder.createViewHolder(convertView));
        return convertView;
    }

    protected void bindView(ViewHolder holder, PlaceCategory category) {
        holder.name.setText(category.text);
        ImageViewManager.getInstance().displayImage(getImageUrl(category), holder.image, 2130837933, null);
    }

    private String getImageUrl(PlaceCategory category) {
        return "http://stg.odnoklassniki.ru/static/mobapp-android/1-0-0/img/places/ic_" + category.id + ".png";
    }

    public Filter getFilter() {
        return this.filter;
    }
}
