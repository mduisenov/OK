package ru.ok.android.bus;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class BusEvent implements Parcelable {
    public static final Creator<BusEvent> CREATOR;
    public final Bundle bundleInput;
    public final Bundle bundleOutput;
    public final int resultCode;

    /* renamed from: ru.ok.android.bus.BusEvent.1 */
    static class C02381 implements Creator<BusEvent> {
        C02381() {
        }

        public BusEvent createFromParcel(Parcel parcel) {
            return new BusEvent(null);
        }

        public BusEvent[] newArray(int count) {
            return new BusEvent[count];
        }
    }

    public BusEvent() {
        this(null, null, -1);
    }

    public BusEvent(Bundle bundleInput) {
        this(bundleInput, null, -1);
    }

    public BusEvent(Bundle bundleInput, Bundle bundleOutput) {
        this(bundleInput, bundleOutput, -1);
    }

    public BusEvent(Bundle bundleOutput, int resultCode) {
        this(null, bundleOutput, resultCode);
    }

    public BusEvent(Bundle bundleInput, Bundle bundleOutput, int resultCode) {
        this.bundleInput = bundleInput == null ? new Bundle() : new Bundle(bundleInput);
        this.bundleOutput = bundleOutput == null ? new Bundle() : new Bundle(bundleOutput);
        this.resultCode = resultCode;
    }

    private BusEvent(Parcel parcel) {
        this.bundleInput = parcel.readBundle();
        this.bundleOutput = parcel.readBundle();
        this.resultCode = parcel.readInt();
    }

    public void writeToParcel(Parcel parcel, int ii) {
        parcel.writeBundle(this.bundleInput);
        parcel.writeBundle(this.bundleOutput);
        parcel.writeInt(this.resultCode);
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new C02381();
    }

    public String toString() {
        return "BusEvent{resultCode=" + this.resultCode + '}';
    }
}
