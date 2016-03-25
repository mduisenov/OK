package ru.ok.android.services.processors.settings;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.model.settings.MediaComposerSettings;

public final class MediaComposerSettingsProcessor {
    private static final String[] SETTING_NAMES;
    private final Context context;

    public MediaComposerSettingsProcessor(Context context) {
        this.context = context.getApplicationContext();
    }

    static {
        SETTING_NAMES = new String[]{"media.topic.*"};
    }

    @Subscribe(on = 2131623944, to = 2131624022)
    public void getMediaComposerSettings(BusEvent event) {
        Logger.m172d(">>>");
        Bundle outBundle = new Bundle();
        int resultCode = -2;
        try {
            MediaComposerSettings settings = MediaComposerSettings.fromJson(SettingsGetProcessor.performSettingGetJsonRequest(this.context, SETTING_NAMES));
            settings.toSharedPreferences(ServicesSettingsHelper.getPreferences(OdnoklassnikiApplication.getContext()));
            outBundle.putParcelable("result", settings);
            resultCode = -1;
            Logger.m173d("<<< %s", settings);
        } catch (Throwable e) {
            Logger.m186w(e, "<<< Failed to fetch media composer settings: " + e);
            CommandProcessor.fillErrorBundle(outBundle, e);
        }
        GlobalBus.send(2131624196, new BusEvent(event.bundleInput, outBundle, resultCode));
    }

    public static void updateWithTestPreferences(@NonNull Context context, @NonNull MediaComposerSettings settings) {
        if (isIgnoreLimitsSetInTestPreferences(context)) {
            settings.maxTextLength = Integer.MAX_VALUE;
            settings.maxBlockCount = Integer.MAX_VALUE;
            settings.maxGroupBlockCount = Integer.MAX_VALUE;
            settings.maxPollAnswerLength = Integer.MAX_VALUE;
            settings.maxPollAnswersCount = Integer.MAX_VALUE;
            settings.maxPollQuestionLength = Integer.MAX_VALUE;
        }
    }

    private static boolean isIgnoreLimitsSetInTestPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(2131166701), false);
    }
}
