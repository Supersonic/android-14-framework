package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class Duration implements Parcelable {
    public static final Parcelable.Creator<Duration> CREATOR = new Parcelable.Creator<Duration>() { // from class: com.android.internal.telephony.cat.Duration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Duration createFromParcel(Parcel parcel) {
            return new Duration(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Duration[] newArray(int i) {
            return new Duration[i];
        }
    };
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int timeInterval;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public TimeUnit timeUnit;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* loaded from: classes.dex */
    public enum TimeUnit {
        MINUTE(0),
        SECOND(1),
        TENTH_SECOND(2);
        
        private int mValue;

        TimeUnit(int i) {
            this.mValue = i;
        }

        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        public int value() {
            return this.mValue;
        }
    }

    public Duration(int i, TimeUnit timeUnit) {
        this.timeInterval = i;
        this.timeUnit = timeUnit;
    }

    private Duration(Parcel parcel) {
        this.timeInterval = parcel.readInt();
        this.timeUnit = TimeUnit.values()[parcel.readInt()];
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.timeInterval);
        parcel.writeInt(this.timeUnit.ordinal());
    }
}
