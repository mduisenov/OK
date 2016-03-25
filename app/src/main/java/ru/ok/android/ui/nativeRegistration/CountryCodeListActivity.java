package ru.ok.android.ui.nativeRegistration;

import android.content.Intent;
import android.support.v4.app.Fragment;
import ru.ok.android.ui.nativeRegistration.CountryCodeListFragment.OnCountrySelectionListener;
import ru.ok.android.utils.CountryUtil.Country;

public class CountryCodeListActivity extends TallTitleFullHeightShowDialogFragmentActivity implements OnCountrySelectionListener {
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof CountryCodeListFragment) {
            ((CountryCodeListFragment) fragment).setOnCountrySelectionListener(this);
        }
    }

    public void onCountrySelected(Country country) {
        Intent data = new Intent();
        data.putExtra("code", country);
        setResult(-1, data);
        finish();
    }
}
