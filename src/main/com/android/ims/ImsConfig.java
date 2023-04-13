package com.android.ims;

import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.telephony.ims.ProvisioningManager;
import android.telephony.ims.aidl.IImsConfig;
import android.telephony.ims.aidl.IImsConfigCallback;
import com.android.internal.telephony.util.HandlerExecutor;
import com.android.telephony.Rlog;
import java.util.concurrent.Executor;
@Deprecated
/* loaded from: classes4.dex */
public class ImsConfig {
    public static final String ACTION_IMS_CONFIG_CHANGED = "com.android.intent.action.IMS_CONFIG_CHANGED";
    public static final String ACTION_IMS_FEATURE_CHANGED = "com.android.intent.action.IMS_FEATURE_CHANGED";
    public static final String EXTRA_CHANGED_ITEM = "item";
    public static final String EXTRA_NEW_VALUE = "value";
    private static final String TAG = "ImsConfig";
    private boolean DBG = true;
    private final IImsConfig miConfig;

    /* loaded from: classes4.dex */
    public static class ConfigConstants {
        @Deprecated
        public static final int AMR_BANDWIDTH_EFFICIENT_PT = 50;
        @Deprecated
        public static final int AMR_DEFAULT_MODE = 53;
        @Deprecated
        public static final int AMR_OCTET_ALIGNED_PT = 49;
        @Deprecated
        public static final int AMR_WB_BANDWIDTH_EFFICIENT_PT = 48;
        @Deprecated
        public static final int AMR_WB_OCTET_ALIGNED_PT = 47;
        @Deprecated
        public static final int AVAILABILITY_CACHE_EXPIRATION = 19;
        @Deprecated
        public static final int CANCELLATION_TIMER = 4;
        @Deprecated
        public static final int CAPABILITIES_CACHE_EXPIRATION = 18;
        @Deprecated
        public static final int CAPABILITIES_POLL_INTERVAL = 20;
        @Deprecated
        public static final int CAPABILITY_DISCOVERY_ENABLED = 17;
        @Deprecated
        public static final int CAPAB_POLL_LIST_SUB_EXP = 23;
        public static final int CONFIG_START = 0;
        @Deprecated
        public static final int DOMAIN_NAME = 12;
        @Deprecated
        public static final int DTMF_NB_PT = 52;
        @Deprecated
        public static final int DTMF_WB_PT = 51;
        @Deprecated
        public static final int EAB_SETTING_ENABLED = 25;
        @Deprecated
        public static final int GZIP_FLAG = 24;
        @Deprecated
        public static final int KEEP_ALIVE_ENABLED = 32;
        @Deprecated
        public static final int LBO_PCSCF_ADDRESS = 31;
        @Deprecated
        public static final int LVC_SETTING_ENABLED = 11;
        @Deprecated
        public static final int MAX_NUMENTRIES_IN_RCL = 22;
        @Deprecated
        public static final int MIN_SE = 3;
        @Deprecated
        public static final int MOBILE_DATA_ENABLED = 29;
        public static final int PROVISIONED_CONFIG_END = 67;
        public static final int PROVISIONED_CONFIG_START = 0;
        @Deprecated
        public static final int PUBLISH_TIMER = 15;
        @Deprecated
        public static final int PUBLISH_TIMER_EXTENDED = 16;
        @Deprecated
        public static final int REGISTRATION_RETRY_BASE_TIME_SEC = 33;
        @Deprecated
        public static final int REGISTRATION_RETRY_MAX_TIME_SEC = 34;
        @Deprecated
        public static final int RTT_SETTING_ENABLED = 66;
        @Deprecated
        public static final int SILENT_REDIAL_ENABLE = 6;
        @Deprecated
        public static final int SIP_ACK_RECEIPT_WAIT_TIME_MSEC = 43;
        @Deprecated
        public static final int SIP_ACK_RETX_WAIT_TIME_MSEC = 44;
        @Deprecated
        public static final int SIP_INVITE_REQ_RETX_INTERVAL_MSEC = 37;
        @Deprecated
        public static final int SIP_INVITE_RSP_RETX_INTERVAL_MSEC = 42;
        @Deprecated
        public static final int SIP_INVITE_RSP_RETX_WAIT_TIME_MSEC = 39;
        @Deprecated
        public static final int SIP_INVITE_RSP_WAIT_TIME_MSEC = 38;
        @Deprecated
        public static final int SIP_NON_INVITE_REQ_RETX_INTERVAL_MSEC = 40;
        @Deprecated
        public static final int SIP_NON_INVITE_REQ_RETX_WAIT_TIME_MSEC = 45;
        @Deprecated
        public static final int SIP_NON_INVITE_RSP_RETX_WAIT_TIME_MSEC = 46;
        @Deprecated
        public static final int SIP_NON_INVITE_TXN_TIMEOUT_TIMER_MSEC = 41;
        @Deprecated
        public static final int SIP_SESSION_TIMER = 2;
        @Deprecated
        public static final int SIP_T1_TIMER = 7;
        @Deprecated
        public static final int SIP_T2_TIMER = 8;
        @Deprecated
        public static final int SIP_TF_TIMER = 9;
        @Deprecated
        public static final int SMS_FORMAT = 13;
        @Deprecated
        public static final int SMS_OVER_IP = 14;
        @Deprecated
        public static final int SMS_PSI = 54;
        @Deprecated
        public static final int SOURCE_THROTTLE_PUBLISH = 21;
        @Deprecated
        public static final int SPEECH_END_PORT = 36;
        @Deprecated
        public static final int SPEECH_START_PORT = 35;
        @Deprecated
        public static final int TDELAY = 5;
        @Deprecated
        public static final int TH_1x = 59;
        @Deprecated
        public static final int TH_LTE1 = 56;
        @Deprecated
        public static final int TH_LTE2 = 57;
        @Deprecated
        public static final int TH_LTE3 = 58;
        @Deprecated
        public static final int T_EPDG_1X = 64;
        @Deprecated
        public static final int T_EPDG_LTE = 62;
        @Deprecated
        public static final int T_EPDG_WIFI = 63;
        @Deprecated
        public static final int VICE_SETTING_ENABLED = 65;
        @Deprecated
        public static final int VIDEO_QUALITY = 55;
        @Deprecated
        public static final int VLT_SETTING_ENABLED = 10;
        @Deprecated
        public static final int VOCODER_AMRMODESET = 0;
        @Deprecated
        public static final int VOCODER_AMRWBMODESET = 1;
        @Deprecated
        public static final int VOICE_OVER_WIFI_MODE = 27;
        @Deprecated
        public static final int VOICE_OVER_WIFI_ROAMING = 26;
        @Deprecated
        public static final int VOICE_OVER_WIFI_SETTING_ENABLED = 28;
        @Deprecated
        public static final int VOLTE_USER_OPT_IN_STATUS = 30;
        @Deprecated
        public static final int VOWT_A = 60;
        @Deprecated
        public static final int VOWT_B = 61;
    }

    /* loaded from: classes4.dex */
    public static class FeatureConstants {
        public static final int FEATURE_TYPE_UNKNOWN = -1;
        public static final int FEATURE_TYPE_UT_OVER_LTE = 4;
        public static final int FEATURE_TYPE_UT_OVER_WIFI = 5;
        public static final int FEATURE_TYPE_VIDEO_OVER_LTE = 1;
        public static final int FEATURE_TYPE_VIDEO_OVER_WIFI = 3;
        public static final int FEATURE_TYPE_VOICE_OVER_LTE = 0;
        public static final int FEATURE_TYPE_VOICE_OVER_WIFI = 2;
    }

    /* loaded from: classes4.dex */
    public static class FeatureValueConstants {
        public static final int ERROR = -1;
        public static final int OFF = 0;

        /* renamed from: ON */
        public static final int f548ON = 1;
    }

    /* loaded from: classes4.dex */
    public static class OperationStatusConstants {
        public static final int FAILED = 1;
        public static final int SUCCESS = 0;
        public static final int UNKNOWN = -1;
        public static final int UNSUPPORTED_CAUSE_DISABLED = 4;
        public static final int UNSUPPORTED_CAUSE_NONE = 2;
        public static final int UNSUPPORTED_CAUSE_RAT = 3;
    }

    /* loaded from: classes4.dex */
    public static class OperationValuesConstants {
        public static final int VIDEO_QUALITY_HIGH = 1;
        public static final int VIDEO_QUALITY_LOW = 0;
        public static final int VIDEO_QUALITY_UNKNOWN = -1;
    }

    /* loaded from: classes4.dex */
    public static class VideoQualityFeatureValuesConstants {
        public static final int HIGH = 1;
        public static final int LOW = 0;
    }

    /* loaded from: classes4.dex */
    public static class WfcModeFeatureValueConstants {
        public static final int CELLULAR_PREFERRED = 1;
        public static final int WIFI_ONLY = 0;
        public static final int WIFI_PREFERRED = 2;
    }

    public ImsConfig(IImsConfig iconfig) {
        this.miConfig = iconfig;
    }

    public int getProvisionedValue(int item) throws ImsException {
        return getConfigInt(item);
    }

    public int getConfigInt(int item) throws ImsException {
        try {
            int ret = this.miConfig.getConfigInt(item);
            if (this.DBG) {
                Rlog.m10d(TAG, "getInt(): item = " + item + ", ret =" + ret);
            }
            return ret;
        } catch (RemoteException e) {
            throw new ImsException("getInt()", e, 131);
        }
    }

    public String getProvisionedStringValue(int item) throws ImsException {
        return getConfigString(item);
    }

    public String getConfigString(int item) throws ImsException {
        try {
            String ret = this.miConfig.getConfigString(item);
            if (this.DBG) {
                Rlog.m10d(TAG, "getConfigString(): item = " + item + ", ret =" + ret);
            }
            return ret;
        } catch (RemoteException e) {
            throw new ImsException("getConfigString()", e, 131);
        }
    }

    public int setProvisionedValue(int item, int value) throws ImsException {
        return setConfig(item, value);
    }

    public int setProvisionedStringValue(int item, String value) throws ImsException {
        return setConfig(item, value);
    }

    public int setConfig(int item, int value) throws ImsException {
        if (this.DBG) {
            Rlog.m10d(TAG, "setConfig(): item = " + item + "value = " + value);
        }
        try {
            int ret = this.miConfig.setConfigInt(item, value);
            if (this.DBG) {
                Rlog.m10d(TAG, "setConfig(): item = " + item + " value = " + value + " ret = " + ret);
            }
            return ret;
        } catch (RemoteException e) {
            throw new ImsException("setConfig()", e, 131);
        }
    }

    public int setConfig(int item, String value) throws ImsException {
        if (this.DBG) {
            Rlog.m10d(TAG, "setConfig(): item = " + item + "value = " + value);
        }
        try {
            int ret = this.miConfig.setConfigString(item, value);
            if (this.DBG) {
                Rlog.m10d(TAG, "setConfig(): item = " + item + " value = " + value + " ret = " + ret);
            }
            return ret;
        } catch (RemoteException e) {
            throw new ImsException("setConfig()", e, 131);
        }
    }

    public void addConfigCallback(ProvisioningManager.Callback callback) throws ImsException {
        callback.setExecutor(getThreadExecutor());
        addConfigCallback(callback.getBinder());
    }

    public void addConfigCallback(IImsConfigCallback callback) throws ImsException {
        if (this.DBG) {
            Rlog.m10d(TAG, "addConfigCallback: " + callback);
        }
        try {
            this.miConfig.addImsConfigCallback(callback);
        } catch (RemoteException e) {
            throw new ImsException("addConfigCallback()", e, 131);
        }
    }

    public void removeConfigCallback(IImsConfigCallback callback) throws ImsException {
        if (this.DBG) {
            Rlog.m10d(TAG, "removeConfigCallback: " + callback);
        }
        try {
            this.miConfig.removeImsConfigCallback(callback);
        } catch (RemoteException e) {
            throw new ImsException("removeConfigCallback()", e, 131);
        }
    }

    public boolean isBinderAlive() {
        return this.miConfig.asBinder().isBinderAlive();
    }

    private Executor getThreadExecutor() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        return new HandlerExecutor(new Handler(Looper.myLooper()));
    }
}
