package android.hardware.fingerprint;

import android.hardware.biometrics.ComponentInfoInternal;
import android.hardware.biometrics.SensorLocationInternal;
import android.hardware.biometrics.SensorPropertiesInternal;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.logging.nano.MetricsProto;
import java.util.List;
/* loaded from: classes.dex */
public class FingerprintSensorPropertiesInternal extends SensorPropertiesInternal {
    public static final Parcelable.Creator<FingerprintSensorPropertiesInternal> CREATOR = new Parcelable.Creator<FingerprintSensorPropertiesInternal>() { // from class: android.hardware.fingerprint.FingerprintSensorPropertiesInternal.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FingerprintSensorPropertiesInternal createFromParcel(Parcel in) {
            return new FingerprintSensorPropertiesInternal(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FingerprintSensorPropertiesInternal[] newArray(int size) {
            return new FingerprintSensorPropertiesInternal[size];
        }
    };
    public final boolean halControlsIllumination;
    private final List<SensorLocationInternal> mSensorLocations;
    public final int sensorType;

    public FingerprintSensorPropertiesInternal(int sensorId, int strength, int maxEnrollmentsPerUser, List<ComponentInfoInternal> componentInfo, int sensorType, boolean halControlsIllumination, boolean resetLockoutRequiresHardwareAuthToken, List<SensorLocationInternal> sensorLocations) {
        super(sensorId, strength, maxEnrollmentsPerUser, componentInfo, resetLockoutRequiresHardwareAuthToken, false);
        this.sensorType = sensorType;
        this.halControlsIllumination = halControlsIllumination;
        this.mSensorLocations = List.copyOf(sensorLocations);
    }

    public FingerprintSensorPropertiesInternal(int sensorId, int strength, int maxEnrollmentsPerUser, List<ComponentInfoInternal> componentInfo, int sensorType, boolean resetLockoutRequiresHardwareAuthToken) {
        this(sensorId, strength, maxEnrollmentsPerUser, componentInfo, sensorType, false, resetLockoutRequiresHardwareAuthToken, List.of(new SensorLocationInternal("", 540, MetricsProto.MetricsEvent.FIELD_TEXT_CLASSIFIER_SECOND_ENTITY_TYPE, 130)));
    }

    protected FingerprintSensorPropertiesInternal(Parcel in) {
        super(in);
        this.sensorType = in.readInt();
        this.halControlsIllumination = in.readBoolean();
        this.mSensorLocations = in.createTypedArrayList(SensorLocationInternal.CREATOR);
    }

    @Override // android.hardware.biometrics.SensorPropertiesInternal, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.hardware.biometrics.SensorPropertiesInternal, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.sensorType);
        dest.writeBoolean(this.halControlsIllumination);
        dest.writeTypedList(this.mSensorLocations);
    }

    public boolean isAnyUdfpsType() {
        switch (this.sensorType) {
            case 2:
            case 3:
                return true;
            default:
                return false;
        }
    }

    public boolean isAnySidefpsType() {
        switch (this.sensorType) {
            case 4:
                return true;
            default:
                return false;
        }
    }

    public SensorLocationInternal getLocation() {
        SensorLocationInternal location = getLocation("");
        return location != null ? location : SensorLocationInternal.DEFAULT;
    }

    public SensorLocationInternal getLocation(String displayId) {
        for (SensorLocationInternal location : this.mSensorLocations) {
            if (location.displayId.equals(displayId)) {
                return location;
            }
        }
        return null;
    }

    public List<SensorLocationInternal> getAllLocations() {
        return this.mSensorLocations;
    }

    @Override // android.hardware.biometrics.SensorPropertiesInternal
    public String toString() {
        return "ID: " + this.sensorId + ", Strength: " + this.sensorStrength + ", Type: " + this.sensorType;
    }
}
