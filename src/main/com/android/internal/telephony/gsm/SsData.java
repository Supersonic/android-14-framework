package com.android.internal.telephony.gsm;

import com.android.internal.telephony.CallForwardInfo;
import com.android.internal.telephony.GsmCdmaPhone;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class SsData {
    public CallForwardInfo[] cfInfo;
    public RequestType requestType;
    public int result;
    public int serviceClass;
    public ServiceType serviceType;
    public int[] ssInfo;
    public TeleserviceType teleserviceType;

    /* loaded from: classes.dex */
    public enum TeleserviceType {
        SS_ALL_TELE_AND_BEARER_SERVICES,
        SS_ALL_TELESEVICES,
        SS_TELEPHONY,
        SS_ALL_DATA_TELESERVICES,
        SS_SMS_SERVICES,
        SS_ALL_TELESERVICES_EXCEPT_SMS
    }

    /* loaded from: classes.dex */
    public enum ServiceType {
        SS_CFU,
        SS_CF_BUSY,
        SS_CF_NO_REPLY,
        SS_CF_NOT_REACHABLE,
        SS_CF_ALL,
        SS_CF_ALL_CONDITIONAL,
        SS_CLIP,
        SS_CLIR,
        SS_COLP,
        SS_COLR,
        SS_WAIT,
        SS_BAOC,
        SS_BAOIC,
        SS_BAOIC_EXC_HOME,
        SS_BAIC,
        SS_BAIC_ROAMING,
        SS_ALL_BARRING,
        SS_OUTGOING_BARRING,
        SS_INCOMING_BARRING;

        public boolean isTypeCF() {
            return this == SS_CFU || this == SS_CF_BUSY || this == SS_CF_NO_REPLY || this == SS_CF_NOT_REACHABLE || this == SS_CF_ALL || this == SS_CF_ALL_CONDITIONAL;
        }

        public boolean isTypeUnConditional() {
            return this == SS_CFU || this == SS_CF_ALL;
        }

        public boolean isTypeCW() {
            return this == SS_WAIT;
        }

        public boolean isTypeClip() {
            return this == SS_CLIP;
        }

        public boolean isTypeClir() {
            return this == SS_CLIR;
        }

        public boolean isTypeBarring() {
            return this == SS_BAOC || this == SS_BAOIC || this == SS_BAOIC_EXC_HOME || this == SS_BAIC || this == SS_BAIC_ROAMING || this == SS_ALL_BARRING || this == SS_OUTGOING_BARRING || this == SS_INCOMING_BARRING;
        }
    }

    /* loaded from: classes.dex */
    public enum RequestType {
        SS_ACTIVATION,
        SS_DEACTIVATION,
        SS_INTERROGATION,
        SS_REGISTRATION,
        SS_ERASURE;

        public boolean isTypeInterrogation() {
            return this == SS_INTERROGATION;
        }
    }

    public ServiceType ServiceTypeFromRILInt(int i) {
        try {
            return ServiceType.values()[i];
        } catch (IndexOutOfBoundsException unused) {
            Rlog.e(GsmCdmaPhone.LOG_TAG, "Invalid Service type");
            return null;
        }
    }

    public RequestType RequestTypeFromRILInt(int i) {
        try {
            return RequestType.values()[i];
        } catch (IndexOutOfBoundsException unused) {
            Rlog.e(GsmCdmaPhone.LOG_TAG, "Invalid Request type");
            return null;
        }
    }

    public TeleserviceType TeleserviceTypeFromRILInt(int i) {
        try {
            return TeleserviceType.values()[i];
        } catch (IndexOutOfBoundsException unused) {
            Rlog.e(GsmCdmaPhone.LOG_TAG, "Invalid Teleservice type");
            return null;
        }
    }

    public String toString() {
        return "[SsData] ServiceType: " + this.serviceType + " RequestType: " + this.requestType + " TeleserviceType: " + this.teleserviceType + " ServiceClass: " + this.serviceClass + " Result: " + this.result + " Is Service Type CF: " + this.serviceType.isTypeCF();
    }
}
