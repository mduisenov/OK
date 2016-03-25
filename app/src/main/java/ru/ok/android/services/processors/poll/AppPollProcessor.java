package ru.ok.android.services.processors.poll;

import android.os.Bundle;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.json.JsonAppPollParser;
import ru.ok.java.api.request.polls.GetAppPollsRequest;
import ru.ok.model.poll.AppPollAnswer;
import ru.ok.model.poll.PollStep;

public final class AppPollProcessor {
    @Subscribe(on = 2131623944, to = 2131623983)
    public void loadAppPolls(BusEvent event) {
        try {
            ArrayList<PollStep> stepList = JsonAppPollParser.getSteps(new JSONObject(AppPollPreferences.getStepsJson(OdnoklassnikiApplication.getContext())), AppPollPreferences.defaultItems);
            ArrayList<AppPollAnswer> answers = AppPollPreferences.getAnswers(OdnoklassnikiApplication.getContext());
            Bundle output = new Bundle();
            output.putParcelableArrayList("app_poll_steps", stepList);
            output.putParcelableArrayList("app_poll_answers", answers);
            GlobalBus.send(2131624164, new BusEvent(output, -1));
        } catch (JSONException e) {
            GlobalBus.send(2131624164, new BusEvent(CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131624099)
    public synchronized void saveAnswers(BusEvent event) {
        ArrayList<AppPollAnswer> answers = event.bundleInput.getParcelableArrayList("app_poll_answers");
        ArrayList<AppPollAnswer> savedAnswers = AppPollPreferences.getAnswers(OdnoklassnikiApplication.getContext());
        if (answers != null && savedAnswers.size() < answers.size()) {
            AppPollPreferences.setAnswers(OdnoklassnikiApplication.getContext(), answers);
        }
    }

    public static void downloadAndSaveAppPolls() {
        try {
            AppPollPreferences.parseAndSave(OdnoklassnikiApplication.getContext(), JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new GetAppPollsRequest()).getResultAsObject());
            GlobalBus.send(2131624186, new BusEvent(new Bundle(), -1));
        } catch (Exception e) {
            GlobalBus.send(2131624186, new BusEvent(CommandProcessor.createErrorBundle(e), -2));
        }
    }
}
