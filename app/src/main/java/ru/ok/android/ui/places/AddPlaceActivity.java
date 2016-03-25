package ru.ok.android.ui.places;

import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.ui.places.fragments.AddPlaceFragment;
import ru.ok.android.ui.utils.HomeButtonUtils;
import ru.ok.model.places.Place;

public class AddPlaceActivity extends OdklSubActivity {
    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        getWindow().setSoftInputMode(32);
        ActivityExecutor activityExecutor = new ActivityExecutor(this, AddPlaceFragment.class);
        activityExecutor.setAddToBackStack(false);
        activityExecutor.setNeedToolbar(false);
        activityExecutor.setHideHomeButton(false);
        activityExecutor.setSlidingMenuEnable(false);
        activityExecutor.setArguments(createArguments());
        HomeButtonUtils.hideHomeButton(this);
        showFragment(activityExecutor);
    }

    public boolean isNeedShowLeftMenu() {
        return false;
    }

    private Bundle createArguments() {
        Bundle bundle = new Bundle();
        bundle.putString("extra_text", getText());
        return bundle;
    }

    public String getText() {
        return getIntent().getStringExtra("def_text");
    }

    public void onNewPlace(Place place) {
        Intent placeIntent = new Intent();
        placeIntent.putExtra("place_result", place);
        setResult(-1, placeIntent);
        finish();
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
