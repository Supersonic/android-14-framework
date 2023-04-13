package com.android.internal.telephony.cat;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class ToneSettings implements Parcelable {
    public static final Parcelable.Creator<ToneSettings> CREATOR = new Parcelable.Creator<ToneSettings>() { // from class: com.android.internal.telephony.cat.ToneSettings.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ToneSettings createFromParcel(Parcel parcel) {
            return new ToneSettings(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ToneSettings[] newArray(int i) {
            return new ToneSettings[i];
        }
    };
    public Duration duration;
    public Tone tone;
    public boolean vibrate;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ToneSettings(Duration duration, Tone tone, boolean z) {
        this.duration = duration;
        this.tone = tone;
        this.vibrate = z;
    }

    private ToneSettings(Parcel parcel) {
        this.duration = (Duration) parcel.readParcelable(Duration.class.getClassLoader());
        this.tone = (Tone) parcel.readParcelable(Tone.class.getClassLoader());
        this.vibrate = parcel.readInt() == 1;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.duration, 0);
        parcel.writeParcelable(this.tone, 0);
        parcel.writeInt(this.vibrate ? 1 : 0);
    }
}
