package android.companion.virtual.sensor;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.SystemClock;
@SystemApi
/* loaded from: classes.dex */
public final class VirtualSensorEvent implements Parcelable {
    public static final Parcelable.Creator<VirtualSensorEvent> CREATOR = new Parcelable.Creator<VirtualSensorEvent>() { // from class: android.companion.virtual.sensor.VirtualSensorEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualSensorEvent createFromParcel(Parcel source) {
            return new VirtualSensorEvent(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualSensorEvent[] newArray(int size) {
            return new VirtualSensorEvent[size];
        }
    };
    private long mTimestampNanos;
    private float[] mValues;

    private VirtualSensorEvent(float[] values, long timestampNanos) {
        this.mValues = values;
        this.mTimestampNanos = timestampNanos;
    }

    private VirtualSensorEvent(Parcel parcel) {
        int valuesLength = parcel.readInt();
        float[] fArr = new float[valuesLength];
        this.mValues = fArr;
        parcel.readFloatArray(fArr);
        this.mTimestampNanos = parcel.readLong();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int parcelableFlags) {
        parcel.writeInt(this.mValues.length);
        parcel.writeFloatArray(this.mValues);
        parcel.writeLong(this.mTimestampNanos);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public float[] getValues() {
        return this.mValues;
    }

    public long getTimestampNanos() {
        return this.mTimestampNanos;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private long mTimestampNanos = 0;
        private float[] mValues;

        public Builder(float[] values) {
            this.mValues = values;
        }

        public VirtualSensorEvent build() {
            float[] fArr = this.mValues;
            if (fArr == null || fArr.length == 0) {
                throw new IllegalArgumentException("Cannot build virtual sensor event with no values.");
            }
            if (this.mTimestampNanos <= 0) {
                this.mTimestampNanos = SystemClock.elapsedRealtimeNanos();
            }
            return new VirtualSensorEvent(this.mValues, this.mTimestampNanos);
        }

        public Builder setTimestampNanos(long timestampNanos) {
            this.mTimestampNanos = timestampNanos;
            return this;
        }
    }
}
