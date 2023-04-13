package com.android.internal.telephony.cdma;

import android.os.Parcel;
/* loaded from: classes.dex */
public final class CdmaInformationRecords {
    public static final int RIL_CDMA_CALLED_PARTY_NUMBER_INFO_REC = 1;
    public static final int RIL_CDMA_CALLING_PARTY_NUMBER_INFO_REC = 2;
    public static final int RIL_CDMA_CONNECTED_NUMBER_INFO_REC = 3;
    public static final int RIL_CDMA_DISPLAY_INFO_REC = 0;
    public static final int RIL_CDMA_EXTENDED_DISPLAY_INFO_REC = 7;
    public static final int RIL_CDMA_LINE_CONTROL_INFO_REC = 6;
    public static final int RIL_CDMA_REDIRECTING_NUMBER_INFO_REC = 5;
    public static final int RIL_CDMA_SIGNAL_INFO_REC = 4;
    public static final int RIL_CDMA_T53_AUDIO_CONTROL_INFO_REC = 10;
    public static final int RIL_CDMA_T53_CLIR_INFO_REC = 8;
    public static final int RIL_CDMA_T53_RELEASE_INFO_REC = 9;
    public Object record;

    public static String idToString(int i) {
        switch (i) {
            case 0:
                return "RIL_CDMA_DISPLAY_INFO_REC";
            case 1:
                return "RIL_CDMA_CALLED_PARTY_NUMBER_INFO_REC";
            case 2:
                return "RIL_CDMA_CALLING_PARTY_NUMBER_INFO_REC";
            case 3:
                return "RIL_CDMA_CONNECTED_NUMBER_INFO_REC";
            case 4:
                return "RIL_CDMA_SIGNAL_INFO_REC";
            case 5:
                return "RIL_CDMA_REDIRECTING_NUMBER_INFO_REC";
            case 6:
                return "RIL_CDMA_LINE_CONTROL_INFO_REC";
            case 7:
                return "RIL_CDMA_EXTENDED_DISPLAY_INFO_REC";
            case 8:
                return "RIL_CDMA_T53_CLIR_INFO_REC";
            case 9:
                return "RIL_CDMA_T53_RELEASE_INFO_REC";
            case 10:
                return "RIL_CDMA_T53_AUDIO_CONTROL_INFO_REC";
            default:
                return "<unknown record>";
        }
    }

    public CdmaInformationRecords(CdmaDisplayInfoRec cdmaDisplayInfoRec) {
        this.record = cdmaDisplayInfoRec;
    }

    public CdmaInformationRecords(CdmaNumberInfoRec cdmaNumberInfoRec) {
        this.record = cdmaNumberInfoRec;
    }

    public CdmaInformationRecords(CdmaSignalInfoRec cdmaSignalInfoRec) {
        this.record = cdmaSignalInfoRec;
    }

    public CdmaInformationRecords(CdmaRedirectingNumberInfoRec cdmaRedirectingNumberInfoRec) {
        this.record = cdmaRedirectingNumberInfoRec;
    }

    public CdmaInformationRecords(CdmaLineControlInfoRec cdmaLineControlInfoRec) {
        this.record = cdmaLineControlInfoRec;
    }

    public CdmaInformationRecords(CdmaT53ClirInfoRec cdmaT53ClirInfoRec) {
        this.record = cdmaT53ClirInfoRec;
    }

    public CdmaInformationRecords(CdmaT53AudioControlInfoRec cdmaT53AudioControlInfoRec) {
        this.record = cdmaT53AudioControlInfoRec;
    }

    public CdmaInformationRecords(Parcel parcel) {
        int readInt = parcel.readInt();
        switch (readInt) {
            case 0:
            case 7:
                this.record = new CdmaDisplayInfoRec(readInt, parcel.readString());
                return;
            case 1:
            case 2:
            case 3:
                this.record = new CdmaNumberInfoRec(readInt, parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                return;
            case 4:
                this.record = new CdmaSignalInfoRec(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                return;
            case 5:
                this.record = new CdmaRedirectingNumberInfoRec(parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                return;
            case 6:
                this.record = new CdmaLineControlInfoRec(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                return;
            case 8:
                this.record = new CdmaT53ClirInfoRec(parcel.readInt());
                return;
            case 9:
            default:
                throw new RuntimeException("RIL_UNSOL_CDMA_INFO_REC: unsupported record. Got " + idToString(readInt) + " ");
            case 10:
                this.record = new CdmaT53AudioControlInfoRec(parcel.readInt(), parcel.readInt());
                return;
        }
    }

    /* loaded from: classes.dex */
    public static class CdmaSignalInfoRec {
        public int alertPitch;
        public boolean isPresent;
        public int signal;
        public int signalType;

        public CdmaSignalInfoRec() {
        }

        public CdmaSignalInfoRec(int i, int i2, int i3, int i4) {
            this.isPresent = i != 0;
            this.signalType = i2;
            this.alertPitch = i3;
            this.signal = i4;
        }

        public String toString() {
            return "CdmaSignalInfo: { isPresent: " + this.isPresent + ", signalType: " + this.signalType + ", alertPitch: " + this.alertPitch + ", signal: " + this.signal + " }";
        }
    }

    /* loaded from: classes.dex */
    public static class CdmaDisplayInfoRec {
        public String alpha;

        /* renamed from: id */
        public int f6id;

        public CdmaDisplayInfoRec(int i, String str) {
            this.f6id = i;
            this.alpha = str;
        }

        public String toString() {
            return "CdmaDisplayInfoRec: { id: " + CdmaInformationRecords.idToString(this.f6id) + ", alpha: " + this.alpha + " }";
        }
    }

    /* loaded from: classes.dex */
    public static class CdmaNumberInfoRec {

        /* renamed from: id */
        public int f7id;
        public String number;
        public byte numberPlan;
        public byte numberType;

        /* renamed from: pi */
        public byte f8pi;

        /* renamed from: si */
        public byte f9si;

        public CdmaNumberInfoRec(int i, String str, int i2, int i3, int i4, int i5) {
            this.f7id = i;
            this.number = str;
            this.numberType = (byte) i2;
            this.numberPlan = (byte) i3;
            this.f8pi = (byte) i4;
            this.f9si = (byte) i5;
        }

        public String toString() {
            return "CdmaNumberInfoRec: { id: " + CdmaInformationRecords.idToString(this.f7id) + ", number: <MASKED>, numberType: " + ((int) this.numberType) + ", numberPlan: " + ((int) this.numberPlan) + ", pi: " + ((int) this.f8pi) + ", si: " + ((int) this.f9si) + " }";
        }
    }

    /* loaded from: classes.dex */
    public static class CdmaRedirectingNumberInfoRec {
        public static final int REASON_CALLED_DTE_OUT_OF_ORDER = 9;
        public static final int REASON_CALL_FORWARDING_BUSY = 1;
        public static final int REASON_CALL_FORWARDING_BY_THE_CALLED_DTE = 10;
        public static final int REASON_CALL_FORWARDING_NO_REPLY = 2;
        public static final int REASON_CALL_FORWARDING_UNCONDITIONAL = 15;
        public static final int REASON_UNKNOWN = 0;
        public CdmaNumberInfoRec numberInfoRec;
        public int redirectingReason;

        public CdmaRedirectingNumberInfoRec(String str, int i, int i2, int i3, int i4, int i5) {
            this.numberInfoRec = new CdmaNumberInfoRec(5, str, i, i2, i3, i4);
            this.redirectingReason = i5;
        }

        public String toString() {
            return "CdmaNumberInfoRec: { numberInfoRec: " + this.numberInfoRec + ", redirectingReason: " + this.redirectingReason + " }";
        }
    }

    /* loaded from: classes.dex */
    public static class CdmaLineControlInfoRec {
        public byte lineCtrlPolarityIncluded;
        public byte lineCtrlPowerDenial;
        public byte lineCtrlReverse;
        public byte lineCtrlToggle;

        public CdmaLineControlInfoRec(int i, int i2, int i3, int i4) {
            this.lineCtrlPolarityIncluded = (byte) i;
            this.lineCtrlToggle = (byte) i2;
            this.lineCtrlReverse = (byte) i3;
            this.lineCtrlPowerDenial = (byte) i4;
        }

        public String toString() {
            return "CdmaLineControlInfoRec: { lineCtrlPolarityIncluded: " + ((int) this.lineCtrlPolarityIncluded) + " lineCtrlToggle: " + ((int) this.lineCtrlToggle) + " lineCtrlReverse: " + ((int) this.lineCtrlReverse) + " lineCtrlPowerDenial: " + ((int) this.lineCtrlPowerDenial) + " }";
        }
    }

    /* loaded from: classes.dex */
    public static class CdmaT53ClirInfoRec {
        public byte cause;

        public CdmaT53ClirInfoRec(int i) {
            this.cause = (byte) i;
        }

        public String toString() {
            return "CdmaT53ClirInfoRec: { cause: " + ((int) this.cause) + " }";
        }
    }

    /* loaded from: classes.dex */
    public static class CdmaT53AudioControlInfoRec {
        public byte downlink;
        public byte uplink;

        public CdmaT53AudioControlInfoRec(int i, int i2) {
            this.uplink = (byte) i;
            this.downlink = (byte) i2;
        }

        public String toString() {
            return "CdmaT53AudioControlInfoRec: { uplink: " + ((int) this.uplink) + " downlink: " + ((int) this.downlink) + " }";
        }
    }
}
