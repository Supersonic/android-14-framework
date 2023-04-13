package android.location;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class GnssStatus implements Parcelable {
    public static final int CONSTELLATION_BEIDOU = 5;
    public static final int CONSTELLATION_COUNT = 8;
    public static final int CONSTELLATION_GALILEO = 6;
    public static final int CONSTELLATION_GLONASS = 3;
    public static final int CONSTELLATION_GPS = 1;
    public static final int CONSTELLATION_IRNSS = 7;
    public static final int CONSTELLATION_QZSS = 4;
    public static final int CONSTELLATION_SBAS = 2;
    private static final int CONSTELLATION_TYPE_MASK = 15;
    private static final int CONSTELLATION_TYPE_SHIFT_WIDTH = 8;
    public static final int CONSTELLATION_UNKNOWN = 0;
    public static final Parcelable.Creator<GnssStatus> CREATOR = new Parcelable.Creator<GnssStatus>() { // from class: android.location.GnssStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssStatus createFromParcel(Parcel in) {
            int svCount = in.readInt();
            int[] svidWithFlags = new int[svCount];
            float[] cn0DbHzs = new float[svCount];
            float[] elevations = new float[svCount];
            float[] azimuths = new float[svCount];
            float[] carrierFrequencies = new float[svCount];
            float[] basebandCn0DbHzs = new float[svCount];
            for (int i = 0; i < svCount; i++) {
                svidWithFlags[i] = in.readInt();
                cn0DbHzs[i] = in.readFloat();
                elevations[i] = in.readFloat();
                azimuths[i] = in.readFloat();
                carrierFrequencies[i] = in.readFloat();
                basebandCn0DbHzs[i] = in.readFloat();
            }
            return new GnssStatus(svCount, svidWithFlags, cn0DbHzs, elevations, azimuths, carrierFrequencies, basebandCn0DbHzs);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssStatus[] newArray(int size) {
            return new GnssStatus[size];
        }
    };
    private static final int SVID_FLAGS_HAS_ALMANAC_DATA = 2;
    private static final int SVID_FLAGS_HAS_BASEBAND_CN0 = 16;
    private static final int SVID_FLAGS_HAS_CARRIER_FREQUENCY = 8;
    private static final int SVID_FLAGS_HAS_EPHEMERIS_DATA = 1;
    private static final int SVID_FLAGS_NONE = 0;
    private static final int SVID_FLAGS_USED_IN_FIX = 4;
    private static final int SVID_SHIFT_WIDTH = 12;
    private final float[] mAzimuths;
    private final float[] mBasebandCn0DbHzs;
    private final float[] mCarrierFrequencies;
    private final float[] mCn0DbHzs;
    private final float[] mElevations;
    private final int mSvCount;
    private final int[] mSvidWithFlags;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface ConstellationType {
    }

    /* loaded from: classes2.dex */
    public static abstract class Callback {
        public void onStarted() {
        }

        public void onStopped() {
        }

        public void onFirstFix(int ttffMillis) {
        }

        public void onSatelliteStatusChanged(GnssStatus status) {
        }
    }

    public static GnssStatus wrap(int svCount, int[] svidWithFlags, float[] cn0DbHzs, float[] elevations, float[] azimuths, float[] carrierFrequencies, float[] basebandCn0DbHzs) {
        Preconditions.checkState(svCount >= 0);
        Preconditions.checkState(svidWithFlags.length >= svCount);
        Preconditions.checkState(elevations.length >= svCount);
        Preconditions.checkState(azimuths.length >= svCount);
        Preconditions.checkState(carrierFrequencies.length >= svCount);
        Preconditions.checkState(basebandCn0DbHzs.length >= svCount);
        return new GnssStatus(svCount, svidWithFlags, cn0DbHzs, elevations, azimuths, carrierFrequencies, basebandCn0DbHzs);
    }

    private GnssStatus(int svCount, int[] svidWithFlags, float[] cn0DbHzs, float[] elevations, float[] azimuths, float[] carrierFrequencies, float[] basebandCn0DbHzs) {
        this.mSvCount = svCount;
        this.mSvidWithFlags = svidWithFlags;
        this.mCn0DbHzs = cn0DbHzs;
        this.mElevations = elevations;
        this.mAzimuths = azimuths;
        this.mCarrierFrequencies = carrierFrequencies;
        this.mBasebandCn0DbHzs = basebandCn0DbHzs;
    }

    public int getSatelliteCount() {
        return this.mSvCount;
    }

    public int getConstellationType(int satelliteIndex) {
        return (this.mSvidWithFlags[satelliteIndex] >> 8) & 15;
    }

    public int getSvid(int satelliteIndex) {
        return this.mSvidWithFlags[satelliteIndex] >> 12;
    }

    public float getCn0DbHz(int satelliteIndex) {
        return this.mCn0DbHzs[satelliteIndex];
    }

    public float getElevationDegrees(int satelliteIndex) {
        return this.mElevations[satelliteIndex];
    }

    public float getAzimuthDegrees(int satelliteIndex) {
        return this.mAzimuths[satelliteIndex];
    }

    public boolean hasEphemerisData(int satelliteIndex) {
        return (this.mSvidWithFlags[satelliteIndex] & 1) != 0;
    }

    public boolean hasAlmanacData(int satelliteIndex) {
        return (this.mSvidWithFlags[satelliteIndex] & 2) != 0;
    }

    public boolean usedInFix(int satelliteIndex) {
        return (this.mSvidWithFlags[satelliteIndex] & 4) != 0;
    }

    public boolean hasCarrierFrequencyHz(int satelliteIndex) {
        return (this.mSvidWithFlags[satelliteIndex] & 8) != 0;
    }

    public float getCarrierFrequencyHz(int satelliteIndex) {
        return this.mCarrierFrequencies[satelliteIndex];
    }

    public boolean hasBasebandCn0DbHz(int satelliteIndex) {
        return (this.mSvidWithFlags[satelliteIndex] & 16) != 0;
    }

    public float getBasebandCn0DbHz(int satelliteIndex) {
        return this.mBasebandCn0DbHzs[satelliteIndex];
    }

    public static String constellationTypeToString(int constellationType) {
        switch (constellationType) {
            case 0:
                return "UNKNOWN";
            case 1:
                return "GPS";
            case 2:
                return "SBAS";
            case 3:
                return "GLONASS";
            case 4:
                return "QZSS";
            case 5:
                return "BEIDOU";
            case 6:
                return "GALILEO";
            case 7:
                return "IRNSS";
            default:
                return Integer.toString(constellationType);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof GnssStatus) {
            GnssStatus that = (GnssStatus) o;
            return this.mSvCount == that.mSvCount && Arrays.equals(this.mSvidWithFlags, that.mSvidWithFlags) && Arrays.equals(this.mCn0DbHzs, that.mCn0DbHzs) && Arrays.equals(this.mElevations, that.mElevations) && Arrays.equals(this.mAzimuths, that.mAzimuths) && Arrays.equals(this.mCarrierFrequencies, that.mCarrierFrequencies) && Arrays.equals(this.mBasebandCn0DbHzs, that.mBasebandCn0DbHzs);
        }
        return false;
    }

    public int hashCode() {
        int result = Objects.hash(Integer.valueOf(this.mSvCount));
        return (((result * 31) + Arrays.hashCode(this.mSvidWithFlags)) * 31) + Arrays.hashCode(this.mCn0DbHzs);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mSvCount);
        for (int i = 0; i < this.mSvCount; i++) {
            parcel.writeInt(this.mSvidWithFlags[i]);
            parcel.writeFloat(this.mCn0DbHzs[i]);
            parcel.writeFloat(this.mElevations[i]);
            parcel.writeFloat(this.mAzimuths[i]);
            parcel.writeFloat(this.mCarrierFrequencies[i]);
            parcel.writeFloat(this.mBasebandCn0DbHzs[i]);
        }
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private final ArrayList<GnssSvInfo> mSatellites = new ArrayList<>();

        public Builder addSatellite(int constellationType, int svid, float cn0DbHz, float elevation, float azimuth, boolean hasEphemeris, boolean hasAlmanac, boolean usedInFix, boolean hasCarrierFrequency, float carrierFrequency, boolean hasBasebandCn0DbHz, float basebandCn0DbHz) {
            this.mSatellites.add(new GnssSvInfo(constellationType, svid, cn0DbHz, elevation, azimuth, hasEphemeris, hasAlmanac, usedInFix, hasCarrierFrequency, carrierFrequency, hasBasebandCn0DbHz, basebandCn0DbHz));
            return this;
        }

        public Builder clearSatellites() {
            this.mSatellites.clear();
            return this;
        }

        public GnssStatus build() {
            int svCount = this.mSatellites.size();
            int[] svidWithFlags = new int[svCount];
            float[] cn0DbHzs = new float[svCount];
            float[] elevations = new float[svCount];
            float[] azimuths = new float[svCount];
            float[] carrierFrequencies = new float[svCount];
            float[] basebandCn0DbHzs = new float[svCount];
            for (int i = 0; i < svidWithFlags.length; i++) {
                svidWithFlags[i] = this.mSatellites.get(i).mSvidWithFlags;
            }
            for (int i2 = 0; i2 < cn0DbHzs.length; i2++) {
                cn0DbHzs[i2] = this.mSatellites.get(i2).mCn0DbHz;
            }
            for (int i3 = 0; i3 < elevations.length; i3++) {
                elevations[i3] = this.mSatellites.get(i3).mElevation;
            }
            for (int i4 = 0; i4 < azimuths.length; i4++) {
                azimuths[i4] = this.mSatellites.get(i4).mAzimuth;
            }
            for (int i5 = 0; i5 < carrierFrequencies.length; i5++) {
                carrierFrequencies[i5] = this.mSatellites.get(i5).mCarrierFrequency;
            }
            for (int i6 = 0; i6 < basebandCn0DbHzs.length; i6++) {
                basebandCn0DbHzs[i6] = this.mSatellites.get(i6).mBasebandCn0DbHz;
            }
            return new GnssStatus(svCount, svidWithFlags, cn0DbHzs, elevations, azimuths, carrierFrequencies, basebandCn0DbHzs);
        }
    }

    /* loaded from: classes2.dex */
    private static class GnssSvInfo {
        private final float mAzimuth;
        private final float mBasebandCn0DbHz;
        private final float mCarrierFrequency;
        private final float mCn0DbHz;
        private final float mElevation;
        private final int mSvidWithFlags;

        private GnssSvInfo(int constellationType, int svid, float cn0DbHz, float elevation, float azimuth, boolean hasEphemeris, boolean hasAlmanac, boolean usedInFix, boolean hasCarrierFrequency, float carrierFrequency, boolean hasBasebandCn0DbHz, float basebandCn0DbHz) {
            this.mSvidWithFlags = (svid << 12) | ((constellationType & 15) << 8) | (hasEphemeris ? 1 : 0) | (hasAlmanac ? 2 : 0) | (usedInFix ? 4 : 0) | (hasCarrierFrequency ? 8 : 0) | (hasBasebandCn0DbHz ? 16 : 0);
            this.mCn0DbHz = cn0DbHz;
            this.mElevation = elevation;
            this.mAzimuth = azimuth;
            this.mCarrierFrequency = hasCarrierFrequency ? carrierFrequency : 0.0f;
            this.mBasebandCn0DbHz = hasBasebandCn0DbHz ? basebandCn0DbHz : 0.0f;
        }
    }
}
