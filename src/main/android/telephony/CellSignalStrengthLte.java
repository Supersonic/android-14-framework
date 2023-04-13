package android.telephony;

import android.content.p001pm.PackageManager;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class CellSignalStrengthLte extends CellSignalStrength implements Parcelable {
    private static final boolean DBG = false;
    private static final String LOG_TAG = "CellSignalStrengthLte";
    private static final int MAX_LTE_RSRP = -44;
    private static final int MIN_LTE_RSRP = -140;
    private static final int SIGNAL_STRENGTH_LTE_RSSI_ASU_UNKNOWN = 99;
    private static final int SIGNAL_STRENGTH_LTE_RSSI_VALID_ASU_MAX_VALUE = 31;
    private static final int SIGNAL_STRENGTH_LTE_RSSI_VALID_ASU_MIN_VALUE = 0;
    public static final int USE_RSRP = 1;
    public static final int USE_RSRQ = 2;
    public static final int USE_RSSNR = 4;
    private static final int sRsrpBoost = 0;
    private int mCqi;
    private int mCqiTableIndex;
    private int mLevel;
    private int mParametersUseForLevel;
    private int mRsrp;
    private int mRsrq;
    private int mRssi;
    private int mRssnr;
    private int mSignalStrength;
    private int mTimingAdvance;
    private static final int[] sRsrpThresholds = {PackageManager.INSTALL_FAILED_ABORTED, PackageManager.INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING, -95, -85};
    private static final int[] sRsrqThresholds = {-19, -17, -14, -12};
    private static final int[] sRssnrThresholds = {-3, 1, 5, 13};
    private static final CellSignalStrengthLte sInvalid = new CellSignalStrengthLte();
    public static final Parcelable.Creator<CellSignalStrengthLte> CREATOR = new Parcelable.Creator<CellSignalStrengthLte>() { // from class: android.telephony.CellSignalStrengthLte.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellSignalStrengthLte createFromParcel(Parcel in) {
            return new CellSignalStrengthLte(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellSignalStrengthLte[] newArray(int size) {
            return new CellSignalStrengthLte[size];
        }
    };

    public CellSignalStrengthLte() {
        setDefaultValues();
    }

    public CellSignalStrengthLte(int rssi, int rsrp, int rsrq, int rssnr, int cqiTableIndex, int cqi, int timingAdvance) {
        int inRangeOrUnavailable = inRangeOrUnavailable(rssi, -113, -51);
        this.mRssi = inRangeOrUnavailable;
        this.mSignalStrength = inRangeOrUnavailable;
        this.mRsrp = inRangeOrUnavailable(rsrp, -140, -43);
        this.mRsrq = inRangeOrUnavailable(rsrq, -34, 3);
        this.mRssnr = inRangeOrUnavailable(rssnr, -20, 30);
        this.mCqiTableIndex = inRangeOrUnavailable(cqiTableIndex, 1, 6);
        this.mCqi = inRangeOrUnavailable(cqi, 0, 15);
        this.mTimingAdvance = inRangeOrUnavailable(timingAdvance, 0, 1282);
        updateLevel(null, null);
    }

    public CellSignalStrengthLte(int rssi, int rsrp, int rsrq, int rssnr, int cqi, int timingAdvance) {
        this(rssi, rsrp, rsrq, rssnr, Integer.MAX_VALUE, cqi, timingAdvance);
    }

    public CellSignalStrengthLte(CellSignalStrengthLte s) {
        copyFrom(s);
    }

    protected void copyFrom(CellSignalStrengthLte s) {
        this.mSignalStrength = s.mSignalStrength;
        this.mRssi = s.mRssi;
        this.mRsrp = s.mRsrp;
        this.mRsrq = s.mRsrq;
        this.mRssnr = s.mRssnr;
        this.mCqiTableIndex = s.mCqiTableIndex;
        this.mCqi = s.mCqi;
        this.mTimingAdvance = s.mTimingAdvance;
        this.mLevel = s.mLevel;
        this.mParametersUseForLevel = s.mParametersUseForLevel;
    }

    @Override // android.telephony.CellSignalStrength
    public CellSignalStrengthLte copy() {
        return new CellSignalStrengthLte(this);
    }

    @Override // android.telephony.CellSignalStrength
    public void setDefaultValues() {
        this.mSignalStrength = Integer.MAX_VALUE;
        this.mRssi = Integer.MAX_VALUE;
        this.mRsrp = Integer.MAX_VALUE;
        this.mRsrq = Integer.MAX_VALUE;
        this.mRssnr = Integer.MAX_VALUE;
        this.mCqiTableIndex = Integer.MAX_VALUE;
        this.mCqi = Integer.MAX_VALUE;
        this.mTimingAdvance = Integer.MAX_VALUE;
        this.mLevel = 0;
        this.mParametersUseForLevel = 1;
    }

    @Override // android.telephony.CellSignalStrength
    public int getLevel() {
        return this.mLevel;
    }

    private boolean isLevelForParameter(int parameterType) {
        return (this.mParametersUseForLevel & parameterType) == parameterType;
    }

    @Override // android.telephony.CellSignalStrength
    public void updateLevel(PersistableBundle cc, ServiceState ss) {
        int[] rsrpThresholds;
        int[] rsrqThresholds;
        int[] rssnrThresholds;
        boolean rsrpOnly;
        int rssiLevel;
        int level;
        if (cc == null) {
            this.mParametersUseForLevel = 1;
            rsrpThresholds = sRsrpThresholds;
            rsrqThresholds = sRsrqThresholds;
            rssnrThresholds = sRssnrThresholds;
            rsrpOnly = false;
        } else {
            this.mParametersUseForLevel = cc.getInt(CarrierConfigManager.KEY_PARAMETERS_USED_FOR_LTE_SIGNAL_BAR_INT);
            rsrpThresholds = cc.getIntArray(CarrierConfigManager.KEY_LTE_RSRP_THRESHOLDS_INT_ARRAY);
            if (rsrpThresholds == null) {
                rsrpThresholds = sRsrpThresholds;
            }
            rsrqThresholds = cc.getIntArray(CarrierConfigManager.KEY_LTE_RSRQ_THRESHOLDS_INT_ARRAY);
            if (rsrqThresholds == null) {
                rsrqThresholds = sRsrqThresholds;
            }
            rssnrThresholds = cc.getIntArray(CarrierConfigManager.KEY_LTE_RSSNR_THRESHOLDS_INT_ARRAY);
            if (rssnrThresholds == null) {
                rssnrThresholds = sRssnrThresholds;
            }
            rsrpOnly = cc.getBoolean(CarrierConfigManager.KEY_USE_ONLY_RSRP_FOR_LTE_SIGNAL_BAR_BOOL, false);
        }
        int rsrpBoost = 0;
        if (ss != null) {
            rsrpBoost = ss.getArfcnRsrpBoost();
        }
        int rsrp = inRangeOrUnavailable(this.mRsrp + rsrpBoost, -140, -44);
        if (rsrpOnly && (level = updateLevelWithMeasure(rsrp, rsrpThresholds)) != Integer.MAX_VALUE) {
            this.mLevel = level;
            return;
        }
        int rsrpLevel = Integer.MAX_VALUE;
        int rsrqLevel = Integer.MAX_VALUE;
        int rssnrLevel = Integer.MAX_VALUE;
        if (isLevelForParameter(1)) {
            rsrpLevel = updateLevelWithMeasure(rsrp, rsrpThresholds);
        }
        if (isLevelForParameter(2)) {
            rsrqLevel = updateLevelWithMeasure(this.mRsrq, rsrqThresholds);
        }
        if (isLevelForParameter(4)) {
            rssnrLevel = updateLevelWithMeasure(this.mRssnr, rssnrThresholds);
        }
        int min = Math.min(Math.min(rsrpLevel, rsrqLevel), rssnrLevel);
        this.mLevel = min;
        if (min == Integer.MAX_VALUE) {
            int i = this.mRssi;
            if (i > -51) {
                rssiLevel = 0;
            } else if (i >= -89) {
                rssiLevel = 4;
            } else if (i >= -97) {
                rssiLevel = 3;
            } else if (i >= -103) {
                rssiLevel = 2;
            } else if (i >= -113) {
                rssiLevel = 1;
            } else {
                rssiLevel = 0;
            }
            this.mLevel = rssiLevel;
        }
    }

    private int updateLevelWithMeasure(int measure, int[] thresholds) {
        if (measure == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (measure >= thresholds[3]) {
            return 4;
        }
        if (measure >= thresholds[2]) {
            return 3;
        }
        if (measure >= thresholds[1]) {
            return 2;
        }
        if (measure >= thresholds[0]) {
            return 1;
        }
        return 0;
    }

    public int getRsrq() {
        return this.mRsrq;
    }

    public int getRssi() {
        return this.mRssi;
    }

    public int getRssnr() {
        return this.mRssnr;
    }

    public int getRsrp() {
        return this.mRsrp;
    }

    public int getCqiTableIndex() {
        return this.mCqiTableIndex;
    }

    public int getCqi() {
        return this.mCqi;
    }

    @Override // android.telephony.CellSignalStrength
    public int getDbm() {
        return this.mRsrp;
    }

    @Override // android.telephony.CellSignalStrength
    public int getAsuLevel() {
        int lteDbm = this.mRsrp;
        if (lteDbm == Integer.MAX_VALUE) {
            return 99;
        }
        if (lteDbm <= -140) {
            return 0;
        }
        if (lteDbm >= -43) {
            return 97;
        }
        int lteAsuLevel = lteDbm + 140;
        return lteAsuLevel;
    }

    public int getTimingAdvance() {
        return this.mTimingAdvance;
    }

    @Override // android.telephony.CellSignalStrength
    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mRssi), Integer.valueOf(this.mRsrp), Integer.valueOf(this.mRsrq), Integer.valueOf(this.mRssnr), Integer.valueOf(this.mCqiTableIndex), Integer.valueOf(this.mCqi), Integer.valueOf(this.mTimingAdvance), Integer.valueOf(this.mLevel));
    }

    @Override // android.telephony.CellSignalStrength
    public boolean isValid() {
        return !equals(sInvalid);
    }

    @Override // android.telephony.CellSignalStrength
    public boolean equals(Object o) {
        if (o instanceof CellSignalStrengthLte) {
            CellSignalStrengthLte s = (CellSignalStrengthLte) o;
            return this.mRssi == s.mRssi && this.mRsrp == s.mRsrp && this.mRsrq == s.mRsrq && this.mRssnr == s.mRssnr && this.mCqiTableIndex == s.mCqiTableIndex && this.mCqi == s.mCqi && this.mTimingAdvance == s.mTimingAdvance && this.mLevel == s.mLevel;
        }
        return false;
    }

    public String toString() {
        return "CellSignalStrengthLte: rssi=" + this.mRssi + " rsrp=" + this.mRsrp + " rsrq=" + this.mRsrq + " rssnr=" + this.mRssnr + " cqiTableIndex=" + this.mCqiTableIndex + " cqi=" + this.mCqi + " ta=" + this.mTimingAdvance + " level=" + this.mLevel + " parametersUseForLevel=" + this.mParametersUseForLevel;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRssi);
        dest.writeInt(this.mRsrp);
        dest.writeInt(this.mRsrq);
        dest.writeInt(this.mRssnr);
        dest.writeInt(this.mCqiTableIndex);
        dest.writeInt(this.mCqi);
        dest.writeInt(this.mTimingAdvance);
        dest.writeInt(this.mLevel);
    }

    private CellSignalStrengthLte(Parcel in) {
        int readInt = in.readInt();
        this.mRssi = readInt;
        this.mSignalStrength = readInt;
        this.mRsrp = in.readInt();
        this.mRsrq = in.readInt();
        this.mRssnr = in.readInt();
        this.mCqiTableIndex = in.readInt();
        this.mCqi = in.readInt();
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

    public static int convertRssnrUnitFromTenDbToDB(int rssnr) {
        return (int) Math.floor(rssnr / 10.0f);
    }

    public static int convertRssiAsuToDBm(int rssiAsu) {
        if (rssiAsu == 99) {
            return Integer.MAX_VALUE;
        }
        if (rssiAsu < 0 || rssiAsu > 31) {
            com.android.telephony.Rlog.m8e(LOG_TAG, "convertRssiAsuToDBm: invalid RSSI in ASU=" + rssiAsu);
            return Integer.MAX_VALUE;
        }
        return (rssiAsu * 2) - 113;
    }
}
