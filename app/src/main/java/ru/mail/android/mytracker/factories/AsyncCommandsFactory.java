package ru.mail.android.mytracker.factories;

import android.content.Context;
import ru.mail.android.mytracker.TrackerParams;
import ru.mail.android.mytracker.async.commands.AsyncCommand;
import ru.mail.android.mytracker.async.commands.SendEventsCommand;
import ru.mail.android.mytracker.async.commands.TrackEventCommand;
import ru.mail.android.mytracker.async.commands.TrackLaunchSessionCommand;
import ru.mail.android.mytracker.async.commands.TrackMajorEventsCommand;
import ru.mail.android.mytracker.async.commands.TrackReferrerCommand;
import ru.mail.android.mytracker.database.MyTrackerDBHelper;
import ru.mail.android.mytracker.enums.CriterionSend;
import ru.mail.android.mytracker.models.events.Event;
import ru.mail.android.mytracker.net.Hosts;

public class AsyncCommandsFactory {
    public static AsyncCommand getTrackEventCommand(Event event, CriterionSend criterion, MyTrackerDBHelper dbHelper, TrackerParams trackerParams, Context context) {
        return new TrackEventCommand(Hosts.getTrackerHost(), event, criterion, dbHelper, trackerParams, context);
    }

    public static AsyncCommand getTrackLaunchSessionCommand(MyTrackerDBHelper dbHelper, TrackerParams trackerParams, long lastStopTimestamp, Context context) {
        return new TrackLaunchSessionCommand(Hosts.getTrackerHost(), dbHelper, trackerParams, lastStopTimestamp, context);
    }

    public static AsyncCommand getTrackMajorEventsCommand(MyTrackerDBHelper dbHelper, TrackerParams trackerParams, Context context) {
        return new TrackMajorEventsCommand(Hosts.getTrackerHost(), dbHelper, trackerParams, context);
    }

    public static AsyncCommand getTrackReferrerCommand(String referrer, String trackerId, Context context) {
        return new TrackReferrerCommand(Hosts.getTrackerHost(), referrer, trackerId, context);
    }

    public static AsyncCommand getSendEventsCommand(CriterionSend criterion, MyTrackerDBHelper dbHelper, TrackerParams params, Context context) {
        return new SendEventsCommand(Hosts.getTrackerHost(), criterion, dbHelper, params, context);
    }

    private AsyncCommandsFactory() {
    }
}
