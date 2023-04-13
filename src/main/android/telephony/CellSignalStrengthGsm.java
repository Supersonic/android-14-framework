package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class CellSignalStrengthGsm extends CellSignalStrength implements Parcelable {
    private static final boolean DBG = false;
    private static final int GSM_RSSI_GOOD = -97;
    private static final int GSM_RSSI_GREAT = -89;
    private static final int GSM_RSSI_MAX = -51;
    private static final int GSM_RSSI_MIN = -113;
    private static final int GSM_RSSI_MODERATE = -103;
    private static final int GSM_RSSI_POOR = -107;
    private static final String LOG_TAG = "CellSignalStrengthGsm";
    private int mBitErrorRate;
    private int mLevel;
    private int mRssi;
    private int mTimingAdvance;
    private static final int[] sRssiThresholds = {-107, -103, -97, -89};
    private static final CellSignalStrengthGsm sInvalid = new CellSignalStrengthGsm();
    public static final Parcelable.Creator<CellSignalStrengthGsm> CREATOR = new Parcelable.Creator<CellSignalStrengthGsm>() { // from class: android.telephony.CellSignalStrengthGsm.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellSignalStrengthGsm createFromParcel(Parcel in) {
            return new CellSignalStrengthGsm(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellSignalStrengthGsm[] newArray(int size) {
            return new CellSignalStrengthGsm[size];
        }
    };

    public CellSignalStrengthGsm() {
        setDefaultValues();
    }

    public CellSignalStrengthGsm(int rssi, int ber, int ta) {
        this.mRssi = inRangeOrUnavailable(rssi, -113, -51);
        this.mBitErrorRate = inRangeOrUnavailable(ber, 0, 7, 99);
        this.mTimingAdvance = inRangeOrUnavailable(ta, 0, 219);
        updateLevel(null, null);
    }

    public CellSignalStrengthGsm(CellSignalStrengthGsm s) {
        copyFrom(s);
    }

    protected void copyFrom(CellSignalStrengthGsm s) {
        this.mRssi = s.mRssi;
        this.mBitErrorRate = s.mBitErrorRate;
        this.mTimingAdvance = s.mTimingAdvance;
        this.mLevel = s.mLevel;
    }

    @Override // android.telephony.CellSignalStrength
    public CellSignalStrengthGsm copy() {
        return new CellSignalStrengthGsm(this);
    }

    @Override // android.telephony.CellSignalStrength
    public void setDefaultValues() {
        this.mRssi = Integer.MAX_VALUE;
        this.mBitErrorRate = Integer.MAX_VALUE;
        this.mTimingAdvance = Integer.MAX_VALUE;
        this.mLevel = 0;
    }

    @Override // android.telephony.CellSignalStrength
    public int getLevel() {
        return this.mLevel;
    }

    @Override // android.telephony.CellSignalStrength
    public void updateLevel(PersistableBundle cc, ServiceState ss) {
        int[] rssiThresholds;
        if (cc == null) {
            rssiThresholds = sRssiThresholds;
        } else {
            rssiThresholds = cc.getIntArray(CarrierConfigManager.KEY_GSM_RSSI_THRESHOLDS_INT_ARRAY);
            if (rssiThresholds == null || rssiThresholds.length != 4) {
                rssiThresholds = sRssiThresholds;
            }
        }
        int level = 4;
        int i = this.mRssi;
        if (i < -113 || i > -51) {
            this.mLevel = 0;
            return;
        }
        while (level > 0 && this.mRssi < rssiThresholds[level - 1]) {
            level--;
        }
        this.mLevel = level;
    }

    public int getTimingAdvance() {
        return this.mTimingAdvance;
    }

    @Override // android.telephony.CellSignalStrength
    public int getDbm() {
        return this.mRssi;
    }

    @Override // android.telephony.CellSignalStrength
    public int getAsuLevel() {
        return getAsuFromRssiDbm(this.mRssi);
    }

    public int getRssi() {
        return this.mRssi;
    }

    public int getBitErrorRate() {
        return this.mBitErrorRate;
    }

    @Override // android.telephony.CellSignalStrength
    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mRssi), Integer.valueOf(this.mBitErrorRate), Integer.valueOf(this.mTimingAdvance));
    }

    @Override // android.telephony.CellSignalStrength
    public boolean isValid() {
        return !equals(sInvalid);
    }

    @Override // android.telephony.CellSignalStrength
    public boolean equals(Object o) {
        if (o instanceof CellSignalStrengthGsm) {
            CellSignalStrengthGsm s = (CellSignalStrengthGsm) o;
            return this.mRssi == s.mRssi && this.mBitErrorRate == s.mBitErrorRate && this.mTimingAdvance == s.mTimingAdvance && this.mLevel == s.mLevel;
        }
        return false;
    }

    public String toString() {
        return "CellSignalStrengthGsm: rssi=" + this.mRssi + " ber=" + this.mBitErrorRate + " mTa=" + this.mTimingAdvance + " mLevel=" + this.mLevel;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRssi);
        dest.writeInt(this.mBitErrorRate);
        dest.writeInt(this.mTimingAdvance);
        dest.writeInt(this.mLevel);
    }

    private CellSignalStrengthGsm(Parcel in) {
        this.mRssi = in.readInt();
        this.mBitErrorRate = in.readInt();
        this.mTimingAdvance = in.readInt();
        this.mLevel = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private static void log(String s) {
        com.android.telephony.Rlog.m2w(LOG_TAG, s);
    }
}
