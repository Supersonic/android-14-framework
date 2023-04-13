package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class CellSignalStrengthWcdma extends CellSignalStrength implements Parcelable {
    private static final boolean DBG = false;
    private static final String DEFAULT_LEVEL_CALCULATION_METHOD = "rssi";
    public static final String LEVEL_CALCULATION_METHOD_RSCP = "rscp";
    public static final String LEVEL_CALCULATION_METHOD_RSSI = "rssi";
    private static final String LOG_TAG = "CellSignalStrengthWcdma";
    private static final int WCDMA_RSCP_GOOD = -95;
    private static final int WCDMA_RSCP_GREAT = -85;
    private static final int WCDMA_RSCP_MAX = -24;
    private static final int WCDMA_RSCP_MIN = -120;
    private static final int WCDMA_RSCP_MODERATE = -105;
    private static final int WCDMA_RSCP_POOR = -115;
    private static final int WCDMA_RSSI_GREAT = -77;
    private static final int WCDMA_RSSI_MAX = -51;
    private static final int WCDMA_RSSI_MIN = -113;
    private static final int WCDMA_RSSI_MODERATE = -97;
    private static final int WCDMA_RSSI_POOR = -107;
    private int mBitErrorRate;
    private int mEcNo;
    private int mLevel;
    private int mRscp;
    private int mRssi;
    private static final int WCDMA_RSSI_GOOD = -87;
    private static final int[] sRssiThresholds = {-107, -97, WCDMA_RSSI_GOOD, -77};
    private static final int[] sRscpThresholds = {-115, -105, -95, -85};
    private static final CellSignalStrengthWcdma sInvalid = new CellSignalStrengthWcdma();
    public static final Parcelable.Creator<CellSignalStrengthWcdma> CREATOR = new Parcelable.Creator<CellSignalStrengthWcdma>() { // from class: android.telephony.CellSignalStrengthWcdma.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellSignalStrengthWcdma createFromParcel(Parcel in) {
            return new CellSignalStrengthWcdma(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellSignalStrengthWcdma[] newArray(int size) {
            return new CellSignalStrengthWcdma[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface LevelCalculationMethod {
    }

    public CellSignalStrengthWcdma() {
        setDefaultValues();
    }

    public CellSignalStrengthWcdma(int rssi, int ber, int rscp, int ecno) {
        this.mRssi = inRangeOrUnavailable(rssi, -113, -51);
        this.mBitErrorRate = inRangeOrUnavailable(ber, 0, 7, 99);
        this.mRscp = inRangeOrUnavailable(rscp, -120, -24);
        this.mEcNo = inRangeOrUnavailable(ecno, -24, 1);
        updateLevel(null, null);
    }

    public CellSignalStrengthWcdma(CellSignalStrengthWcdma s) {
        copyFrom(s);
    }

    protected void copyFrom(CellSignalStrengthWcdma s) {
        this.mRssi = s.mRssi;
        this.mBitErrorRate = s.mBitErrorRate;
        this.mRscp = s.mRscp;
        this.mEcNo = s.mEcNo;
        this.mLevel = s.mLevel;
    }

    @Override // android.telephony.CellSignalStrength
    public CellSignalStrengthWcdma copy() {
        return new CellSignalStrengthWcdma(this);
    }

    @Override // android.telephony.CellSignalStrength
    public void setDefaultValues() {
        this.mRssi = Integer.MAX_VALUE;
        this.mBitErrorRate = Integer.MAX_VALUE;
        this.mRscp = Integer.MAX_VALUE;
        this.mEcNo = Integer.MAX_VALUE;
        this.mLevel = 0;
    }

    @Override // android.telephony.CellSignalStrength
    public int getLevel() {
        return this.mLevel;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.telephony.CellSignalStrength
    public void updateLevel(PersistableBundle cc, ServiceState ss) {
        String calcMethod;
        int[] rscpThresholds;
        char c;
        if (cc != null) {
            calcMethod = cc.getString(CarrierConfigManager.KEY_WCDMA_DEFAULT_SIGNAL_STRENGTH_MEASUREMENT_STRING, "rssi");
            if (TextUtils.isEmpty(calcMethod)) {
                calcMethod = "rssi";
            }
            rscpThresholds = cc.getIntArray(CarrierConfigManager.KEY_WCDMA_RSCP_THRESHOLDS_INT_ARRAY);
            if (rscpThresholds == null || rscpThresholds.length != 4) {
                rscpThresholds = sRscpThresholds;
            }
        } else {
            calcMethod = "rssi";
            rscpThresholds = sRscpThresholds;
        }
        int level = 4;
        switch (calcMethod.hashCode()) {
            case 3509870:
                if (calcMethod.equals(LEVEL_CALCULATION_METHOD_RSCP)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 3510359:
                if (calcMethod.equals("rssi")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                int i = this.mRscp;
                if (i < -120 || i > -24) {
                    this.mLevel = 0;
                    return;
                }
                while (level > 0 && this.mRscp < rscpThresholds[level - 1]) {
                    level--;
                }
                this.mLevel = level;
                return;
            case 1:
            default:
                loge("Invalid Level Calculation Method for CellSignalStrengthWcdma = " + calcMethod);
                break;
            case 2:
                break;
        }
        int i2 = this.mRssi;
        if (i2 < -113 || i2 > -51) {
            this.mLevel = 0;
            return;
        }
        while (level > 0 && this.mRssi < sRssiThresholds[level - 1]) {
            level--;
        }
        this.mLevel = level;
    }

    @Override // android.telephony.CellSignalStrength
    public int getDbm() {
        int i = this.mRscp;
        return i != Integer.MAX_VALUE ? i : this.mRssi;
    }

    @Override // android.telephony.CellSignalStrength
    public int getAsuLevel() {
        int i = this.mRscp;
        if (i != Integer.MAX_VALUE) {
            return getAsuFromRscpDbm(i);
        }
        int i2 = this.mRssi;
        return i2 != Integer.MAX_VALUE ? getAsuFromRssiDbm(i2) : getAsuFromRscpDbm(Integer.MAX_VALUE);
    }

    public int getRssi() {
        return this.mRssi;
    }

    public int getRscp() {
        return this.mRscp;
    }

    public int getEcNo() {
        return this.mEcNo;
    }

    public int getBitErrorRate() {
        return this.mBitErrorRate;
    }

    @Override // android.telephony.CellSignalStrength
    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mRssi), Integer.valueOf(this.mBitErrorRate), Integer.valueOf(this.mRscp), Integer.valueOf(this.mEcNo), Integer.valueOf(this.mLevel));
    }

    @Override // android.telephony.CellSignalStrength
    public boolean isValid() {
        return !equals(sInvalid);
    }

    @Override // android.telephony.CellSignalStrength
    public boolean equals(Object o) {
        if (o instanceof CellSignalStrengthWcdma) {
            CellSignalStrengthWcdma s = (CellSignalStrengthWcdma) o;
            return this.mRssi == s.mRssi && this.mBitErrorRate == s.mBitErrorRate && this.mRscp == s.mRscp && this.mEcNo == s.mEcNo && this.mLevel == s.mLevel;
        }
        return false;
    }

    public String toString() {
        return "CellSignalStrengthWcdma: ss=" + this.mRssi + " ber=" + this.mBitErrorRate + " rscp=" + this.mRscp + " ecno=" + this.mEcNo + " level=" + this.mLevel;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRssi);
        dest.writeInt(this.mBitErrorRate);
        dest.writeInt(this.mRscp);
        dest.writeInt(this.mEcNo);
        dest.writeInt(this.mLevel);
    }

    private CellSignalStrengthWcdma(Parcel in) {
        this.mRssi = in.readInt();
        this.mBitErrorRate = in.readInt();
        this.mRscp = in.readInt();
        this.mEcNo = in.readInt();
        this.mLevel = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private static void log(String s) {
        com.android.telephony.Rlog.m2w(LOG_TAG, s);
    }

    private static void loge(String s) {
        com.android.telephony.Rlog.m8e(LOG_TAG, s);
    }
}
