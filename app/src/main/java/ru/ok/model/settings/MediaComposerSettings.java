package ru.ok.model.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import com.google.android.gms.location.LocationStatusCodes;
import org.json.JSONObject;

public class MediaComposerSettings extends PMSSettings {
    public static final Creator<MediaComposerSettings> CREATOR;
    public int maxBlockCount;
    public int maxGroupBlockCount;
    public int maxPollAnswerLength;
    public int maxPollAnswersCount;
    public int maxPollQuestionLength;
    public int maxTextLength;

    /* renamed from: ru.ok.model.settings.MediaComposerSettings.1 */
    static class C15921 implements Creator<MediaComposerSettings> {
        C15921() {
        }

        public MediaComposerSettings createFromParcel(Parcel source) {
            return new MediaComposerSettings(source);
        }

        public MediaComposerSettings[] newArray(int size) {
            return new MediaComposerSettings[size];
        }
    }

    public MediaComposerSettings() {
        this.maxTextLength = LocationStatusCodes.GEOFENCE_NOT_AVAILABLE;
        this.maxBlockCount = 16;
        this.maxGroupBlockCount = 16;
        this.maxPollAnswersCount = 10;
        this.maxPollAnswerLength = 50;
        this.maxPollQuestionLength = 250;
    }

    @NonNull
    public static MediaComposerSettings fromJson(JSONObject json) {
        MediaComposerSettings settings = new MediaComposerSettings();
        settings.maxTextLength = json.optInt("media.topic.max.text.length", LocationStatusCodes.GEOFENCE_NOT_AVAILABLE);
        settings.maxBlockCount = json.optInt("media.topic.max.blocks", 16);
        settings.maxGroupBlockCount = json.optInt("media.topic.group.max.blocks", 16);
        settings.maxPollAnswersCount = json.optInt("media.topic.poll.max.answers", 10);
        settings.maxPollAnswerLength = json.optInt("media.topic.poll.max.answer.length", 50);
        settings.maxPollQuestionLength = json.optInt("media.topic.poll.max.question.length", 250);
        return settings;
    }

    @NonNull
    public static MediaComposerSettings fromSharedPreferences(SharedPreferences prefs) {
        MediaComposerSettings settings = new MediaComposerSettings();
        Editor[] cleanupEditor = new Editor[1];
        settings.maxTextLength = PMSSettings.readIntPref(prefs, "media.topic.max.text.length", LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, cleanupEditor);
        settings.maxBlockCount = PMSSettings.readIntPref(prefs, "media.topic.max.blocks", 16, cleanupEditor);
        settings.maxGroupBlockCount = PMSSettings.readIntPref(prefs, "media.topic.group.max.blocks", 16, cleanupEditor);
        settings.maxPollAnswersCount = PMSSettings.readIntPref(prefs, "media.topic.poll.max.answers", 10, cleanupEditor);
        settings.maxPollAnswerLength = PMSSettings.readIntPref(prefs, "media.topic.poll.max.answer.length", 50, cleanupEditor);
        settings.maxPollQuestionLength = PMSSettings.readIntPref(prefs, "media.topic.poll.max.question.length", 250, cleanupEditor);
        if (cleanupEditor[0] != null) {
            cleanupEditor[0].apply();
        }
        return settings;
    }

    public void toSharedPreferences(SharedPreferences prefs) {
        Editor editor = prefs.edit();
        editor.putInt("media.topic.max.text.length", this.maxTextLength);
        editor.putInt("media.topic.max.blocks", this.maxBlockCount);
        editor.putInt("media.topic.group.max.blocks", this.maxGroupBlockCount);
        editor.putInt("media.topic.poll.max.answers", this.maxPollAnswersCount);
        editor.putInt("media.topic.poll.max.answer.length", this.maxPollAnswerLength);
        editor.putInt("media.topic.poll.max.question.length", this.maxPollQuestionLength);
        editor.apply();
    }

    public String toString() {
        return "MediaComposerSettings[maxTextLength=" + this.maxTextLength + " maxBlockCount=" + this.maxBlockCount + " maxGroupBlockCount=" + this.maxGroupBlockCount + " maxPollAnswersCount=" + this.maxPollAnswersCount + " maxPollAnswerLength=" + this.maxPollAnswerLength + " maxPollQuestionLength=" + this.maxPollQuestionLength + "]";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maxTextLength);
        dest.writeInt(this.maxBlockCount);
        dest.writeInt(this.maxGroupBlockCount);
        dest.writeInt(this.maxPollAnswersCount);
        dest.writeInt(this.maxPollAnswerLength);
        dest.writeInt(this.maxPollQuestionLength);
    }

    MediaComposerSettings(Parcel src) {
        this.maxTextLength = LocationStatusCodes.GEOFENCE_NOT_AVAILABLE;
        this.maxBlockCount = 16;
        this.maxGroupBlockCount = 16;
        this.maxPollAnswersCount = 10;
        this.maxPollAnswerLength = 50;
        this.maxPollQuestionLength = 250;
        this.maxTextLength = src.readInt();
        this.maxBlockCount = src.readInt();
        this.maxGroupBlockCount = src.readInt();
        this.maxPollAnswersCount = src.readInt();
        this.maxPollAnswerLength = src.readInt();
        this.maxPollQuestionLength = src.readInt();
    }

    static {
        CREATOR = new C15921();
    }
}
