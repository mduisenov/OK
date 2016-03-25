package ru.ok.android.ui.messaging.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import java.util.ArrayList;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.users.activity.SelectFriendsFilteredActivity;
import ru.ok.android.utils.bus.BusMessagingHelper;

public final class SelectFriendsForChatActivity extends SelectFriendsFilteredActivity {
    private View progress;

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        this.progress = findViewById(2131624548);
    }

    protected int getLayoutId() {
        return 2130903219;
    }

    protected void processSelectionParams(ArrayList<String> selectedIds) {
        if (selectedIds != null && !selectedIds.isEmpty() && this.progress.getVisibility() != 0) {
            if (selectedIds.size() == 1) {
                Intent result = new Intent();
                result.putExtra("user_id", (String) selectedIds.get(0));
                setResult(-1, result);
                finish();
                return;
            }
            selectedIds.add(OdnoklassnikiApplication.getCurrentUser().uid);
            BusMessagingHelper.createChat(selectedIds);
            this.progress.setVisibility(0);
            this.progress.clearAnimation();
            this.progress.startAnimation(AnimationUtils.loadAnimation(this, 2130968598));
        }
    }

    @Subscribe(on = 2131623946, to = 2131624131)
    public void onChatCreated(BusEvent e) {
        if (e.resultCode == -2) {
            this.progress.setVisibility(8);
            this.progress.startAnimation(AnimationUtils.loadAnimation(this, 2130968599));
            Toast.makeText(this, getStringLocalized(ErrorType.from(e.bundleOutput).getDefaultErrorMessage()), 0).show();
            return;
        }
        String chatId = e.bundleOutput.getString("CONVERSATION_ID");
        Intent result = new Intent();
        result.putExtra("conversation_id", chatId);
        setResult(-1, result);
        finish();
    }
}
