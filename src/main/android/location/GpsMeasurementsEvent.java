package android.location;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
@SystemApi
@Deprecated
/* loaded from: classes2.dex */
public class GpsMeasurementsEvent implements Parcelable {
    public static final Parcelable.Creator<GpsMeasurementsEvent> CREATOR = new Parcelable.Creator<GpsMeasurementsEvent>() { // from class: android.location.GpsMeasurementsEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GpsMeasurementsEvent createFromParcel(Parcel in) {
            ClassLoader classLoader = getClass().getClassLoader();
            GpsClock clock = (GpsClock) in.readParcelable(classLoader, GpsClock.class);
            int measurementsLength = in.readInt();
            GpsMeasurement[] measurementsArray = new GpsMeasurement[measurementsLength];
            in.readTypedArray(measurementsArray, GpsMeasurement.CREATOR);
            return new GpsMeasurementsEvent(clock, measurementsArray);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GpsMeasurementsEvent[] newArray(int size) {
            return new GpsMeasurementsEvent[size];
        }
    };
    public static final int STATUS_GPS_LOCATION_DISABLED = 2;
    public static final int STATUS_NOT_SUPPORTED = 0;
    public static final int STATUS_READY = 1;
    private final GpsClock mClock;
    private final Collection<GpsMeasurement> mReadOnlyMeasurements;

    @SystemApi
    /* loaded from: classes2.dex */
    public interface Listener {
        void onGpsMeasurementsReceived(GpsMeasurementsEvent gpsMeasurementsEvent);

        void onStatusChanged(int i);
    }

    public GpsMeasurementsEvent(GpsClock clock, GpsMeasurement[] measurements) {
        if (clock == null) {
            throw new InvalidParameterException("Parameter 'clock' must not be null.");
        }
        if (measurements == null || measurements.length == 0) {
            throw new InvalidParameterException("Parameter 'measurements' must not be null or empty.");
        }
        this.mClock = clock;
        Collection<GpsMeasurement> measurementCollection = Arrays.asList(measurements);
        this.mReadOnlyMeasurements = Collections.unmodifiableCollection(measurementCollection);
    }

    public GpsClock getClock() {
        return this.mClock;
    }

    public Collection<GpsMeasurement> getMeasurements() {
        return this.mReadOnlyMeasurements;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.mClock, flags);
        int measurementsCount = this.mReadOnlyMeasurements.size();
        GpsMeasurement[] measurementsArray = (GpsMeasurement[]) this.mReadOnlyMeasurements.toArray(new GpsMeasurement[measurementsCount]);
        parcel.writeInt(measurementsArray.length);
        parcel.writeTypedArray(measurementsArray, flags);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("[ GpsMeasurementsEvent:\n\n");
        builder.append(this.mClock.toString());
        builder.append("\n");
        for (GpsMeasurement measurement : this.mReadOnlyMeasurements) {
            builder.append(measurement.toString());
            builder.append("\n");
        }
        builder.append(NavigationBarInflaterView.SIZE_MOD_END);
        return builder.toString();
    }
}
