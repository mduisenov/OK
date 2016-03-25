package ru.ok.android.ui.places;

import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.ui.places.fragments.CategoryFragment;
import ru.ok.android.ui.utils.HomeButtonUtils;
import ru.ok.model.Location;
import ru.ok.model.places.PlaceCategory;

public class CategoryActivity extends OdklSubActivity {
    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        ActivityExecutor activityExecutor = new ActivityExecutor(this, CategoryFragment.class);
        activityExecutor.setAddToBackStack(false);
        activityExecutor.setNeedToolbar(false);
        activityExecutor.setArguments(CategoryFragment.getArguments(getLocation()));
        activityExecutor.setHideHomeButton(false);
        activityExecutor.setSlidingMenuEnable(false);
        HomeButtonUtils.hideHomeButton(this);
        showFragment(activityExecutor);
    }

    public boolean isNeedShowLeftMenu() {
        return false;
    }

    public Location getLocation() {
        return (Location) getIntent().getParcelableExtra("location_input");
    }

    public void onCategorySelect(PlaceCategory category) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("category_result", category);
        Intent placeIntent = new Intent();
        placeIntent.putExtras(bundle);
        setResult(-1, placeIntent);
        finish();
    }
}
