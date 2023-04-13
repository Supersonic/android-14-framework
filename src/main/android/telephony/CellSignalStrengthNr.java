package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes3.dex */
public final class CellSignalStrengthNr extends CellSignalStrength implements Parcelable {
    private static final String TAG = "CellSignalStrengthNr";
    public static final int UNKNOWN_ASU_LEVEL = 99;
    public static final int USE_SSRSRP = 1;
    public static final int USE_SSRSRQ = 2;
    public static final int USE_SSSINR = 4;
    private static final boolean VDBG = false;
    private List<Integer> mCsiCqiReport;
    private int mCsiCqiTableIndex;
    private int mCsiRsrp;
    private int mCsiRsrq;
    private int mCsiSinr;
    private int mLevel;
    private int mParametersUseForLevel;
    private int mSsRsrp;
    private int[] mSsRsrpThresholds;
    private int mSsRsrq;
    private int[] mSsRsrqThresholds;
    private int mSsSinr;
    private int[] mSsSinrThresholds;
    private int mTimingAdvance;
    private static final CellSignalStrengthNr sInvalid = new CellSignalStrengthNr();
    public static final Parcelable.Creator<CellSignalStrengthNr> CREATOR = new Parcelable.Creator<CellSignalStrengthNr>() { // from class: android.telephony.CellSignalStrengthNr.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellSignalStrengthNr createFromParcel(Parcel in) {
            return new CellSignalStrengthNr(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellSignalStrengthNr[] newArray(int size) {
            return new CellSignalStrengthNr[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SignalLevelAndReportCriteriaSource {
    }

    public CellSignalStrengthNr() {
        this.mSsRsrpThresholds = new int[]{-110, -90, -80, -65};
        this.mSsRsrqThresholds = new int[]{-31, -19, -7, 6};
        this.mSsSinrThresholds = new int[]{-5, 5, 15, 30};
        setDefaultValues();
    }

    public CellSignalStrengthNr(int csiRsrp, int csiRsrq, int csiSinr, int csiCqiTableIndex, List<Byte> csiCqiReport, int ssRsrp, int ssRsrq, int ssSinr, int timingAdvance) {
        this.mSsRsrpThresholds = new int[]{-110, -90, -80, -65};
        this.mSsRsrqThresholds = new int[]{-31, -19, -7, 6};
        this.mSsSinrThresholds = new int[]{-5, 5, 15, 30};
        this.mCsiRsrp = inRangeOrUnavailable(csiRsrp, -156, -31);
        this.mCsiRsrq = inRangeOrUnavailable(csiRsrq, -20, -3);
        this.mCsiSinr = inRangeOrUnavailable(csiSinr, -23, 23);
        this.mCsiCqiTableIndex = inRangeOrUnavailable(csiCqiTableIndex, 1, 3);
        this.mCsiCqiReport = (List) csiCqiReport.stream().map(new Function() { // from class: android.telephony.CellSignalStrengthNr$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer valueOf;
                valueOf = Integer.valueOf(CellSignalStrengthNr.inRangeOrUnavailable(Byte.toUnsignedInt(((Byte) obj).byteValue()), 0, 15));
                return valueOf;
            }
        }).collect(Collectors.toList());
        this.mSsRsrp = inRangeOrUnavailable(ssRsrp, -156, -31);
        this.mSsRsrq = inRangeOrUnavailable(ssRsrq, -43, 20);
        this.mSsSinr = inRangeOrUnavailable(ssSinr, -23, 40);
        this.mTimingAdvance = inRangeOrUnavailable(timingAdvance, 0, 1282);
        updateLevel(null, null);
    }

    public CellSignalStrengthNr(int csiRsrp, int csiRsrq, int csiSinr, int ssRsrp, int ssRsrq, int ssSinr) {
        this(csiRsrp, csiRsrq, csiSinr, Integer.MAX_VALUE, Collections.emptyList(), ssRsrp, ssRsrq, ssSinr, Integer.MAX_VALUE);
    }

    public static int flip(int val) {
        return val != Integer.MAX_VALUE ? -val : val;
    }

    public int getSsRsrp() {
        return this.mSsRsrp;
    }

    public int getSsRsrq() {
        return this.mSsRsrq;
    }

    public int getSsSinr() {
        return this.mSsSinr;
    }

    public int getCsiRsrp() {
        return this.mCsiRsrp;
    }

    public int getCsiRsrq() {
        return this.mCsiRsrq;
    }

    public int getCsiSinr() {
        return this.mCsiSinr;
    }

    public int getCsiCqiTableIndex() {
        return this.mCsiCqiTableIndex;
    }

    public List<Integer> getCsiCqiReport() {
        return this.mCsiCqiReport;
    }

    public int getTimingAdvanceMicros() {
        return this.mTimingAdvance;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mCsiRsrp);
        dest.writeInt(this.mCsiRsrq);
        dest.writeInt(this.mCsiSinr);
        dest.writeInt(this.mCsiCqiTableIndex);
        dest.writeList(this.mCsiCqiReport);
        dest.writeInt(this.mSsRsrp);
        dest.writeInt(this.mSsRsrq);
        dest.writeInt(this.mSsSinr);
        dest.writeInt(this.mLevel);
        dest.writeInt(this.mTimingAdvance);
    }

    private CellSignalStrengthNr(Parcel in) {
        this.mSsRsrpThresholds = new int[]{-110, -90, -80, -65};
        this.mSsRsrqThresholds = new int[]{-31, -19, -7, 6};
        this.mSsSinrThresholds = new int[]{-5, 5, 15, 30};
        this.mCsiRsrp = in.readInt();
        this.mCsiRsrq = in.readInt();
        this.mCsiSinr = in.readInt();
        this.mCsiCqiTableIndex = in.readInt();
        this.mCsiCqiReport = in.readArrayList(Integer.class.getClassLoader(), Integer.class);
        this.mSsRsrp = in.readInt();
        this.mSsRsrq = in.readInt();
        this.mSsSinr = in.readInt();
        this.mLevel = in.readInt();
        this.mTimingAdvance = in.readInt();
    }

    @Override // android.telephony.CellSignalStrength
    public void setDefaultValues() {
        this.mCsiRsrp = Integer.MAX_VALUE;
        this.mCsiRsrq = Integer.MAX_VALUE;
        this.mCsiSinr = Integer.MAX_VALUE;
        this.mCsiCqiTableIndex = Integer.MAX_VALUE;
        this.mCsiCqiReport = Collections.emptyList();
        this.mSsRsrp = Integer.MAX_VALUE;
        this.mSsRsrq = Integer.MAX_VALUE;
        this.mSsSinr = Integer.MAX_VALUE;
        this.mLevel = 0;
        this.mParametersUseForLevel = 1;
        this.mTimingAdvance = Integer.MAX_VALUE;
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
        if (cc != null) {
            this.mParametersUseForLevel = cc.getInt(CarrierConfigManager.KEY_PARAMETERS_USE_FOR_5G_NR_SIGNAL_BAR_INT, 1);
            this.mSsRsrpThresholds = cc.getIntArray(CarrierConfigManager.KEY_5G_NR_SSRSRP_THRESHOLDS_INT_ARRAY);
            this.mSsRsrqThresholds = cc.getIntArray(CarrierConfigManager.KEY_5G_NR_SSRSRQ_THRESHOLDS_INT_ARRAY);
            this.mSsSinrThresholds = cc.getIntArray(CarrierConfigManager.KEY_5G_NR_SSSINR_THRESHOLDS_INT_ARRAY);
        } else {
            this.mParametersUseForLevel = 1;
        }
        int ssRsrpLevel = Integer.MAX_VALUE;
        int ssRsrqLevel = Integer.MAX_VALUE;
        int ssSinrLevel = Integer.MAX_VALUE;
        if (isLevelForParameter(1)) {
            int rsrpBoost = 0;
            if (ss != null) {
                rsrpBoost = ss.getArfcnRsrpBoost();
            }
            ssRsrpLevel = updateLevelWithMeasure(this.mSsRsrp + rsrpBoost, this.mSsRsrpThresholds);
        }
        if (isLevelForParameter(2)) {
            ssRsrqLevel = updateLevelWithMeasure(this.mSsRsrq, this.mSsRsrqThresholds);
        }
        if (isLevelForParameter(4)) {
            ssSinrLevel = updateLevelWithMeasure(this.mSsSinr, this.mSsSinrThresholds);
        }
        this.mLevel = Math.min(Math.min(ssRsrpLevel, ssRsrqLevel), ssSinrLevel);
    }

    private int updateLevelWithMeasure(int measure, int[] thresholds) {
        if (measure == Integer.MAX_VALUE) {
            return 0;
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

    @Override // android.telephony.CellSignalStrength
    public int getAsuLevel() {
        int nrDbm = getDbm();
        if (nrDbm == Integer.MAX_VALUE) {
            return 99;
        }
        if (nrDbm <= -140) {
            return 0;
        }
        if (nrDbm >= -43) {
            return 97;
        }
        int asuLevel = nrDbm + 140;
        return asuLevel;
    }

    @Override // android.telephony.CellSignalStrength
    public int getDbm() {
        return this.mSsRsrp;
    }

    public CellSignalStrengthNr(CellSignalStrengthNr s) {
        this.mSsRsrpThresholds = new int[]{-110, -90, -80, -65};
        this.mSsRsrqThresholds = new int[]{-31, -19, -7, 6};
        this.mSsSinrThresholds = new int[]{-5, 5, 15, 30};
        this.mCsiRsrp = s.mCsiRsrp;
        this.mCsiRsrq = s.mCsiRsrq;
        this.mCsiSinr = s.mCsiSinr;
        this.mCsiCqiTableIndex = s.mCsiCqiTableIndex;
        this.mCsiCqiReport = s.mCsiCqiReport;
        this.mSsRsrp = s.mSsRsrp;
        this.mSsRsrq = s.mSsRsrq;
        this.mSsSinr = s.mSsSinr;
        this.mLevel = s.mLevel;
        this.mParametersUseForLevel = s.mParametersUseForLevel;
        this.mTimingAdvance = s.mTimingAdvance;
    }

    @Override // android.telephony.CellSignalStrength
    public CellSignalStrengthNr copy() {
        return new CellSignalStrengthNr(this);
    }

    @Override // android.telephony.CellSignalStrength
    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mCsiRsrp), Integer.valueOf(this.mCsiRsrq), Integer.valueOf(this.mCsiSinr), Integer.valueOf(this.mCsiCqiTableIndex), this.mCsiCqiReport, Integer.valueOf(this.mSsRsrp), Integer.valueOf(this.mSsRsrq), Integer.valueOf(this.mSsSinr), Integer.valueOf(this.mLevel), Integer.valueOf(this.mTimingAdvance));
    }

    @Override // android.telephony.CellSignalStrength
    public boolean isValid() {
        return !equals(sInvalid);
    }

    @Override // android.telephony.CellSignalStrength
    public boolean equals(Object obj) {
        if (obj instanceof CellSignalStrengthNr) {
            CellSignalStrengthNr o = (CellSignalStrengthNr) obj;
            return this.mCsiRsrp == o.mCsiRsrp && this.mCsiRsrq == o.mCsiRsrq && this.mCsiSinr == o.mCsiSinr && this.mCsiCqiTableIndex == o.mCsiCqiTableIndex && this.mCsiCqiReport.equals(o.mCsiCqiReport) && this.mSsRsrp == o.mSsRsrp && this.mSsRsrq == o.mSsRsrq && this.mSsSinr == o.mSsSinr && this.mLevel == o.mLevel && this.mTimingAdvance == o.mTimingAdvance;
        }
        return false;
    }

    public String toString() {
        return "CellSignalStrengthNr:{" + (" csiRsrp = " + this.mCsiRsrp) + (" csiRsrq = " + this.mCsiRsrq) + (" csiCqiTableIndex = " + this.mCsiCqiTableIndex) + (" csiCqiReport = " + this.mCsiCqiReport) + (" ssRsrp = " + this.mSsRsrp) + (" ssRsrq = " + this.mSsRsrq) + (" ssSinr = " + this.mSsSinr) + (" level = " + this.mLevel) + (" parametersUseForLevel = " + this.mParametersUseForLevel) + (" timingAdvance = " + this.mTimingAdvance) + " }";
    }
}
