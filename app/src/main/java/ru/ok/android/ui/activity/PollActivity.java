package ru.ok.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.mediacomposer.PollItem;
import ru.ok.android.ui.fragments.PollEditFragment;
import ru.ok.android.ui.fragments.PollEditFragment.PollEditFragmentListener;
import ru.ok.android.utils.localization.base.LocalizedActivity;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class PollActivity extends LocalizedActivity implements PollEditFragmentListener {
    private PollEditFragment mPollEditFragment;

    protected void onCreateLocalized(Bundle savedInstanceState) {
        setContentView(2130903397);
        setTitle(getStringLocalized(2131166379));
        PollEditFragment pollEditFragment = (PollEditFragment) getSupportFragmentManager().findFragmentByTag("poll_fragment");
        if (pollEditFragment == null) {
            Intent intent = getIntent();
            pollEditFragment = PollEditFragment.newInstance((PollItem) intent.getParcelableExtra("key_poll"), (MediaTopicType) intent.getSerializableExtra("mt_type"));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(C0263R.id.container, pollEditFragment, "poll_fragment");
            fragmentTransaction.commit();
        }
        pollEditFragment.setListener(this);
        this.mPollEditFragment = pollEditFragment;
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        setProgressBarIndeterminateVisibility(false);
        setProgressBarVisibility(false);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        intent.putExtra("key_poll", this.mPollEditFragment.getItem());
        setResult(-1, intent);
        finish();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(2131689520, menu);
        MenuItem menuItem = menu.findItem(2131625438);
        if (menuItem != null) {
            menuItem.setTitle(getIntent().getParcelableExtra("key_poll") == null ? 2131166377 : 2131166381);
        }
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem addItem = menu.findItem(2131625438);
        if (addItem != null) {
            addItem.setEnabled(this.mPollEditFragment.isValid());
        }
        return true;
    }

    public void onPollValidityChanged() {
        supportInvalidateOptionsMenu();
    }
}
