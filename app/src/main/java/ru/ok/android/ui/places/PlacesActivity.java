package ru.ok.android.ui.places;

import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.ui.places.fragments.PlacesFragment;
import ru.ok.android.ui.utils.HomeButtonUtils;
import ru.ok.model.places.Place;

public final class PlacesActivity extends OdklSubActivity {
    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        ActivityExecutor activityExecutor = new ActivityExecutor(this, PlacesFragment.class);
        activityExecutor.setAddToBackStack(false);
        activityExecutor.setNeedToolbar(false);
        activityExecutor.setSlidingMenuEnable(false);
        activityExecutor.setArguments(PlacesFragment.getArguments(getPlace()));
        activityExecutor.setHideHomeButton(false);
        HomeButtonUtils.hideHomeButton(this);
        showFragment(activityExecutor);
    }

    public boolean isNeedShowLeftMenu() {
        return false;
    }

    public void onPlaceSelect(Place place) {
        Intent placeIntent = new Intent();
        placeIntent.putExtra("place_result", place);
        setResult(-1, placeIntent);
        finish();
    }

    public Place getPlace() {
        return (Place) getIntent().getParcelableExtra("place_input");
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
