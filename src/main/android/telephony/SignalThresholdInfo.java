package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class SignalThresholdInfo implements Parcelable {
    public static final Parcelable.Creator<SignalThresholdInfo> CREATOR = new Parcelable.Creator<SignalThresholdInfo>() { // from class: android.telephony.SignalThresholdInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SignalThresholdInfo createFromParcel(Parcel in) {
            return new SignalThresholdInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SignalThresholdInfo[] newArray(int size) {
            return new SignalThresholdInfo[size];
        }
    };
    private static final int HYSTERESIS_DB_DEFAULT = 2;
    public static final int HYSTERESIS_DB_MINIMUM = 0;
    public static final int HYSTERESIS_MS_DISABLED = 0;
    public static final int MAXIMUM_NUMBER_OF_THRESHOLDS_ALLOWED = 4;
    public static final int MINIMUM_NUMBER_OF_THRESHOLDS_ALLOWED = 1;
    public static final int SIGNAL_ECNO_MAX_VALUE = 1;
    public static final int SIGNAL_ECNO_MIN_VALUE = -24;
    public static final int SIGNAL_MEASUREMENT_TYPE_ECNO = 9;
    public static final int SIGNAL_MEASUREMENT_TYPE_RSCP = 2;
    public static final int SIGNAL_MEASUREMENT_TYPE_RSRP = 3;
    public static final int SIGNAL_MEASUREMENT_TYPE_RSRQ = 4;
    public static final int SIGNAL_MEASUREMENT_TYPE_RSSI = 1;
    public static final int SIGNAL_MEASUREMENT_TYPE_RSSNR = 5;
    public static final int SIGNAL_MEASUREMENT_TYPE_SSRSRP = 6;
    public static final int SIGNAL_MEASUREMENT_TYPE_SSRSRQ = 7;
    public static final int SIGNAL_MEASUREMENT_TYPE_SSSINR = 8;
    public static final int SIGNAL_MEASUREMENT_TYPE_UNKNOWN = 0;
    public static final int SIGNAL_RSCP_MAX_VALUE = -25;
    public static final int SIGNAL_RSCP_MIN_VALUE = -120;
    public static final int SIGNAL_RSRP_MAX_VALUE = -44;
    public static final int SIGNAL_RSRP_MIN_VALUE = -140;
    public static final int SIGNAL_RSRQ_MAX_VALUE = 3;
    public static final int SIGNAL_RSRQ_MIN_VALUE = -34;
    public static final int SIGNAL_RSSI_MAX_VALUE = -51;
    public static final int SIGNAL_RSSI_MIN_VALUE = -113;
    public static final int SIGNAL_RSSNR_MAX_VALUE = 30;
    public static final int SIGNAL_RSSNR_MIN_VALUE = -20;
    public static final int SIGNAL_SSRSRP_MAX_VALUE = -44;
    public static final int SIGNAL_SSRSRP_MIN_VALUE = -140;
    public static final int SIGNAL_SSRSRQ_MAX_VALUE = 20;
    public static final int SIGNAL_SSRSRQ_MIN_VALUE = -43;
    public static final int SIGNAL_SSSINR_MAX_VALUE = 40;
    public static final int SIGNAL_SSSINR_MIN_VALUE = -23;
    private final int mHysteresisDb;
    private final int mHysteresisMs;
    private final boolean mIsEnabled;
    private final int mRan;
    private final int mSignalMeasurementType;
    private final int[] mThresholds;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SignalMeasurementType {
    }

    private SignalThresholdInfo(int ran, int signalMeasurementType, int hysteresisMs, int hysteresisDb, int[] thresholds, boolean isEnabled) {
        Objects.requireNonNull(thresholds, "thresholds must not be null");
        validateRanWithMeasurementType(ran, signalMeasurementType);
        validateThresholdRange(signalMeasurementType, thresholds);
        this.mRan = ran;
        this.mSignalMeasurementType = signalMeasurementType;
        this.mHysteresisMs = hysteresisMs < 0 ? 0 : hysteresisMs;
        this.mHysteresisDb = hysteresisDb;
        this.mThresholds = thresholds;
        this.mIsEnabled = isEnabled;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private int mRan = 0;
        private int mSignalMeasurementType = 0;
        private int mHysteresisMs = 0;
        private int mHysteresisDb = 2;
        private int[] mThresholds = null;
        private boolean mIsEnabled = false;

        public Builder setRadioAccessNetworkType(int ran) {
            this.mRan = ran;
            return this;
        }

        public Builder setSignalMeasurementType(int signalMeasurementType) {
            this.mSignalMeasurementType = signalMeasurementType;
            return this;
        }

        public Builder setHysteresisMs(int hysteresisMs) {
            this.mHysteresisMs = hysteresisMs;
            return this;
        }

        public Builder setHysteresisDb(int hysteresisDb) {
            if (hysteresisDb < 0) {
                throw new IllegalArgumentException("hysteresis db value should not be less than 0");
            }
            this.mHysteresisDb = hysteresisDb;
            return this;
        }

        public Builder setThresholds(int[] thresholds) {
            return setThresholds(thresholds, false);
        }

        public Builder setThresholds(int[] thresholds, boolean isSystem) {
            Objects.requireNonNull(thresholds, "thresholds must not be null");
            if (!isSystem && (thresholds.length < 1 || thresholds.length > 4)) {
                throw new IllegalArgumentException("thresholds length must between 1 and 4");
            }
            int[] iArr = (int[]) thresholds.clone();
            this.mThresholds = iArr;
            Arrays.sort(iArr);
            return this;
        }

        public Builder setIsEnabled(boolean isEnabled) {
            this.mIsEnabled = isEnabled;
            return this;
        }

        public SignalThresholdInfo build() {
            return new SignalThresholdInfo(this.mRan, this.mSignalMeasurementType, this.mHysteresisMs, this.mHysteresisDb, this.mThresholds, this.mIsEnabled);
        }
    }

    public int getRadioAccessNetworkType() {
        return this.mRan;
    }

    public int getSignalMeasurementType() {
        return this.mSignalMeasurementType;
    }

    public int getHysteresisMs() {
        return this.mHysteresisMs;
    }

    public int getHysteresisDb() {
        return this.mHysteresisDb;
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    public int[] getThresholds() {
        return (int[]) this.mThresholds.clone();
    }

    public static int getMinimumNumberOfThresholdsAllowed() {
        return 1;
    }

    public static int getMaximumNumberOfThresholdsAllowed() {
        return 4;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mRan);
        out.writeInt(this.mSignalMeasurementType);
        out.writeInt(this.mHysteresisMs);
        out.writeInt(this.mHysteresisDb);
        out.writeIntArray(this.mThresholds);
        out.writeBoolean(this.mIsEnabled);
    }

    private SignalThresholdInfo(Parcel in) {
        this.mRan = in.readInt();
        this.mSignalMeasurementType = in.readInt();
        this.mHysteresisMs = in.readInt();
        this.mHysteresisDb = in.readInt();
        this.mThresholds = in.createIntArray();
        this.mIsEnabled = in.readBoolean();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof SignalThresholdInfo) {
            SignalThresholdInfo other = (SignalThresholdInfo) o;
            return this.mRan == other.mRan && this.mSignalMeasurementType == other.mSignalMeasurementType && this.mHysteresisMs == other.mHysteresisMs && this.mHysteresisDb == other.mHysteresisDb && Arrays.equals(this.mThresholds, other.mThresholds) && this.mIsEnabled == other.mIsEnabled;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mRan), Integer.valueOf(this.mSignalMeasurementType), Integer.valueOf(this.mHysteresisMs), Integer.valueOf(this.mHysteresisDb), Integer.valueOf(Arrays.hashCode(this.mThresholds)), Boolean.valueOf(this.mIsEnabled));
    }

    public String toString() {
        return "SignalThresholdInfo{mRan=" + this.mRan + " mSignalMeasurementType=" + this.mSignalMeasurementType + " mHysteresisMs=" + this.mHysteresisMs + " mHysteresisDb=" + this.mHysteresisDb + " mThresholds=" + Arrays.toString(this.mThresholds) + " mIsEnabled=" + this.mIsEnabled + "}";
    }

    private static boolean isValidThreshold(int type, int threshold) {
        switch (type) {
            case 1:
                return threshold >= -113 && threshold <= -51;
            case 2:
                return threshold >= -120 && threshold <= -25;
            case 3:
                return threshold >= -140 && threshold <= -44;
            case 4:
                return threshold >= -34 && threshold <= 3;
            case 5:
                return threshold >= -20 && threshold <= 30;
            case 6:
                return threshold >= -140 && threshold <= -44;
            case 7:
                return threshold >= -43 && threshold <= 20;
            case 8:
                return threshold >= -23 && threshold <= 40;
            case 9:
                return threshold >= -24 && threshold <= 1;
            default:
                return false;
        }
    }

    private static boolean isValidRanWithMeasurementType(int ran, int type) {
        switch (type) {
            case 1:
                return ran == 1 || ran == 4;
            case 2:
            case 9:
                return ran == 2;
            case 3:
            case 4:
            case 5:
                return ran == 3;
            case 6:
            case 7:
            case 8:
                return ran == 6;
            default:
                return false;
        }
    }

    private void validateRanWithMeasurementType(int ran, int signalMeasurement) {
        if (!isValidRanWithMeasurementType(ran, signalMeasurement)) {
            throw new IllegalArgumentException("invalid RAN: " + ran + " with signal measurement type: " + signalMeasurement);
        }
    }

    private void validateThresholdRange(int signalMeasurement, int[] thresholds) {
        for (int threshold : thresholds) {
            if (!isValidThreshold(signalMeasurement, threshold)) {
                throw new IllegalArgumentException("invalid signal measurement type: " + signalMeasurement + " with threshold: " + threshold);
            }
        }
    }
}
