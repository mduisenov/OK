package ru.ok.android.ui.nativeRegistration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import java.util.List;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.utils.CountryUtil;
import ru.ok.android.utils.CountryUtil.Country;
import ru.ok.android.utils.localization.LocalizationManager;

public class CountryCodeListFragment extends BaseFragment {
    private CountryCodeListAdapter countryCodeListAdapter;
    private ListView countryList;
    private EditText filterText;
    private OnCountrySelectionListener onCountrySelectionListener;

    public interface OnCountrySelectionListener {
        void onCountrySelected(Country country);
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.CountryCodeListFragment.1 */
    class C10741 implements TextWatcher {
        C10741() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            CountryCodeListFragment.this.countryCodeListAdapter.filterData(String.valueOf(s));
        }

        public void afterTextChanged(Editable s) {
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.CountryCodeListFragment.2 */
    class C10752 implements OnItemClickListener {
        C10752() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Country selectedCountry = (Country) CountryCodeListFragment.this.countryList.getAdapter().getItem(position);
            Fragment fragment = CountryCodeListFragment.this.getTargetFragment();
            if (fragment != null && (fragment instanceof OnCountrySelectionListener)) {
                ((OnCountrySelectionListener) fragment).onCountrySelected(selectedCountry);
            }
            if (CountryCodeListFragment.this.onCountrySelectionListener != null) {
                CountryCodeListFragment.this.onCountrySelectionListener.onCountrySelected(selectedCountry);
            }
            if (CountryCodeListFragment.this.getShowsDialog()) {
                CountryCodeListFragment.this.getDialog().dismiss();
            }
            CountryCodeListFragment.this.hideKeyboard();
        }
    }

    public void setOnCountrySelectionListener(OnCountrySelectionListener onCountrySelectionListener) {
        this.onCountrySelectionListener = onCountrySelectionListener;
    }

    protected int getLayoutId() {
        return 0;
    }

    protected CharSequence getTitle() {
        return LocalizationManager.getString(getContext(), 2131165647);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LocalizationManager.inflate(getActivity(), 2130903201, container, false);
        Country initialCountry = (Country) getArguments().getParcelable("code");
        this.countryList = (ListView) view.findViewById(2131624850);
        this.filterText = (EditText) view.findViewById(2131624849);
        this.filterText.addTextChangedListener(new C10741());
        this.countryList.setEmptyView(view.findViewById(2131624851));
        List<Country> countries = CountryUtil.getInstance().getCounties();
        this.countryCodeListAdapter = new CountryCodeListAdapter(getActivity(), 2130903197, countries);
        this.countryList.setAdapter(this.countryCodeListAdapter);
        if (initialCountry != null) {
            int initialPosition = 0;
            for (Country country : countries) {
                if (country.getDisplayName().equals(initialCountry.getDisplayName())) {
                    this.countryList.setSelection(initialPosition);
                    break;
                }
                initialPosition++;
            }
            this.countryCodeListAdapter.setSelection(initialCountry);
        }
        this.countryList.setOnItemClickListener(new C10752());
        return view;
    }
}
