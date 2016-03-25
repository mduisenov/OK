package ru.ok.model.poll;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class PollQuestion implements Parcelable {
    protected final int step;
    protected final String title;

    protected PollQuestion(String title, int step) {
        this.title = title;
        this.step = step;
    }

    public PollQuestion(Parcel in) {
        this.title = in.readString();
        this.step = in.readInt();
    }

    public String getTitle() {
        return this.title;
    }

    public int getStep() {
        return this.step;
    }

    public boolean isSkip() {
        return false;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeInt(this.step);
    }
}
