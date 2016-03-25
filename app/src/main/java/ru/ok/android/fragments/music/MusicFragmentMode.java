package ru.ok.android.fragments.music;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

public enum MusicFragmentMode implements Parcelable {
    STANDARD {
        public boolean onCreateOptionsMenu(Menu menu, MenuInflater inflater, Fragment fragment, boolean wantSearch) {
            return true;
        }

        public boolean onPrepareOptionsMenu(Menu menu, Fragment fragment) {
            return true;
        }
    },
    MULTI_SELECTION {
        public boolean onCreateOptionsMenu(Menu menu, MenuInflater inflater, Fragment fragment, boolean wantSearch) {
            return true;
        }

        public boolean onPrepareOptionsMenu(Menu menu, Fragment fragment) {
            return false;
        }
    };
    
    public static final Creator<MusicFragmentMode> CREATOR;

    /* renamed from: ru.ok.android.fragments.music.MusicFragmentMode.3 */
    static class C03153 implements Creator<MusicFragmentMode> {
        C03153() {
        }

        public MusicFragmentMode createFromParcel(Parcel source) {
            return MusicFragmentMode.values()[source.readInt()];
        }

        public MusicFragmentMode[] newArray(int size) {
            return new MusicFragmentMode[size];
        }
    }

    public abstract boolean onCreateOptionsMenu(Menu menu, MenuInflater menuInflater, Fragment fragment, boolean z);

    public abstract boolean onPrepareOptionsMenu(Menu menu, Fragment fragment);

    static {
        CREATOR = new C03153();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }
}
