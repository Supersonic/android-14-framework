package android.telephony.satellite;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes3.dex */
public final class PointingInfo implements Parcelable {
    public static final Parcelable.Creator<PointingInfo> CREATOR = new Parcelable.Creator<PointingInfo>() { // from class: android.telephony.satellite.PointingInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PointingInfo createFromParcel(Parcel in) {
            return new PointingInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PointingInfo[] newArray(int size) {
            return new PointingInfo[size];
        }
    };
    private float mAntennaAzimuthDegrees;
    private float mAntennaPitchDegrees;
    private float mAntennaRollDegrees;
    private float mSatelliteAzimuthDegrees;
    private float mSatelliteElevationDegrees;

    public PointingInfo(float satelliteAzimuthDegrees, float satelliteElevationDegrees, float antennaAzimuthDegrees, float antennaPitchDegrees, float antennaRollDegrees) {
        this.mSatelliteAzimuthDegrees = satelliteAzimuthDegrees;
        this.mSatelliteElevationDegrees = satelliteElevationDegrees;
        this.mAntennaAzimuthDegrees = antennaAzimuthDegrees;
        this.mAntennaPitchDegrees = antennaPitchDegrees;
        this.mAntennaRollDegrees = antennaRollDegrees;
    }

    private PointingInfo(Parcel in) {
        readFromParcel(in);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(this.mSatelliteAzimuthDegrees);
        out.writeFloat(this.mSatelliteElevationDegrees);
        out.writeFloat(this.mAntennaAzimuthDegrees);
        out.writeFloat(this.mAntennaPitchDegrees);
        out.writeFloat(this.mAntennaRollDegrees);
    }

    public String toString() {
        return "SatelliteAzimuthDegrees:" + this.mSatelliteAzimuthDegrees + ",SatelliteElevationDegrees:" + this.mSatelliteElevationDegrees + ",AntennaAzimuthDegrees:" + this.mAntennaAzimuthDegrees + ",AntennaPitchDegrees:" + this.mAntennaPitchDegrees + ",AntennaRollDegrees:" + this.mAntennaRollDegrees;
    }

    public float getSatelliteAzimuthDegrees() {
        return this.mSatelliteAzimuthDegrees;
    }

    public float getSatelliteElevationDegrees() {
        return this.mSatelliteElevationDegrees;
    }

    public float getAntennaAzimuthDegrees() {
        return this.mAntennaAzimuthDegrees;
    }

    public float getAntennaPitchDegrees() {
        return this.mAntennaPitchDegrees;
    }

    public float getAntennaRollDegrees() {
        return this.mAntennaRollDegrees;
    }

    private void readFromParcel(Parcel in) {
        this.mSatelliteAzimuthDegrees = in.readFloat();
        this.mSatelliteElevationDegrees = in.readFloat();
        this.mAntennaAzimuthDegrees = in.readFloat();
        this.mAntennaPitchDegrees = in.readFloat();
        this.mAntennaRollDegrees = in.readFloat();
    }
}
