package android.p008os;

import android.p008os.IWakeLockCallback;
import android.p008os.Parcelable;
import java.util.List;
/* renamed from: android.os.IPowerManager */
/* loaded from: classes3.dex */
public interface IPowerManager extends IInterface {
    public static final int GO_TO_SLEEP_FLAG_NO_DOZE = 1;
    public static final int GO_TO_SLEEP_REASON_ACCESSIBILITY = 7;
    public static final int GO_TO_SLEEP_REASON_APPLICATION = 0;
    public static final int GO_TO_SLEEP_REASON_FORCE_SUSPEND = 8;
    public static final int GO_TO_SLEEP_REASON_HDMI = 5;
    public static final int GO_TO_SLEEP_REASON_INATTENTIVE = 9;
    public static final int GO_TO_SLEEP_REASON_LID_SWITCH = 3;
    public static final int GO_TO_SLEEP_REASON_MAX = 10;
    public static final int GO_TO_SLEEP_REASON_MIN = 0;
    public static final int GO_TO_SLEEP_REASON_POWER_BUTTON = 4;
    public static final int GO_TO_SLEEP_REASON_QUIESCENT = 10;
    public static final int GO_TO_SLEEP_REASON_SLEEP_BUTTON = 6;
    public static final int GO_TO_SLEEP_REASON_TIMEOUT = 2;
    public static final int LOCATION_MODE_ALL_DISABLED_WHEN_SCREEN_OFF = 2;
    public static final int LOCATION_MODE_FOREGROUND_ONLY = 3;
    public static final int LOCATION_MODE_GPS_DISABLED_WHEN_SCREEN_OFF = 1;
    public static final int LOCATION_MODE_NO_CHANGE = 0;
    public static final int LOCATION_MODE_THROTTLE_REQUESTS_WHEN_SCREEN_OFF = 4;
    public static final int MAX_LOCATION_MODE = 4;
    public static final int MIN_LOCATION_MODE = 0;

    void acquireLowPowerStandbyPorts(IBinder iBinder, List<LowPowerStandbyPortDescription> list) throws RemoteException;

    void acquireWakeLock(IBinder iBinder, int i, String str, String str2, WorkSource workSource, String str3, int i2, IWakeLockCallback iWakeLockCallback) throws RemoteException;

    void acquireWakeLockAsync(IBinder iBinder, int i, String str, String str2, WorkSource workSource, String str3) throws RemoteException;

    void acquireWakeLockWithUid(IBinder iBinder, int i, String str, String str2, int i2, int i3, IWakeLockCallback iWakeLockCallback) throws RemoteException;

    boolean areAutoPowerSaveModesEnabled() throws RemoteException;

    void boostScreenBrightness(long j) throws RemoteException;

    void crash(String str) throws RemoteException;

    void forceLowPowerStandbyActive(boolean z) throws RemoteException;

    boolean forceSuspend() throws RemoteException;

    List<LowPowerStandbyPortDescription> getActiveLowPowerStandbyPorts() throws RemoteException;

    ParcelDuration getBatteryDischargePrediction() throws RemoteException;

    float getBrightnessConstraint(int i) throws RemoteException;

    BatterySaverPolicyConfig getFullPowerSavePolicy() throws RemoteException;

    int getLastShutdownReason() throws RemoteException;

    int getLastSleepReason() throws RemoteException;

    LowPowerStandbyPolicy getLowPowerStandbyPolicy() throws RemoteException;

    int getPowerSaveModeTrigger() throws RemoteException;

    PowerSaveState getPowerSaveState(int i) throws RemoteException;

    void goToSleep(long j, int i, int i2) throws RemoteException;

    void goToSleepWithDisplayId(int i, long j, int i2, int i3) throws RemoteException;

    boolean isAmbientDisplayAvailable() throws RemoteException;

    boolean isAmbientDisplaySuppressed() throws RemoteException;

    boolean isAmbientDisplaySuppressedForToken(String str) throws RemoteException;

    boolean isAmbientDisplaySuppressedForTokenByApp(String str, int i) throws RemoteException;

    boolean isBatteryDischargePredictionPersonalized() throws RemoteException;

    boolean isDeviceIdleMode() throws RemoteException;

    boolean isExemptFromLowPowerStandby() throws RemoteException;

    boolean isFeatureAllowedInLowPowerStandby(String str) throws RemoteException;

    boolean isInteractive() throws RemoteException;

    boolean isLightDeviceIdleMode() throws RemoteException;

    boolean isLowPowerStandbyEnabled() throws RemoteException;

    boolean isLowPowerStandbySupported() throws RemoteException;

    boolean isPowerSaveMode() throws RemoteException;

    boolean isReasonAllowedInLowPowerStandby(int i) throws RemoteException;

    boolean isScreenBrightnessBoosted() throws RemoteException;

    boolean isWakeLockLevelSupported(int i) throws RemoteException;

    void nap(long j) throws RemoteException;

    void reboot(boolean z, String str, boolean z2) throws RemoteException;

    void rebootSafeMode(boolean z, boolean z2) throws RemoteException;

    void releaseLowPowerStandbyPorts(IBinder iBinder) throws RemoteException;

    void releaseWakeLock(IBinder iBinder, int i) throws RemoteException;

    void releaseWakeLockAsync(IBinder iBinder, int i) throws RemoteException;

    boolean setAdaptivePowerSaveEnabled(boolean z) throws RemoteException;

    boolean setAdaptivePowerSavePolicy(BatterySaverPolicyConfig batterySaverPolicyConfig) throws RemoteException;

    void setAttentionLight(boolean z, int i) throws RemoteException;

    void setBatteryDischargePrediction(ParcelDuration parcelDuration, boolean z) throws RemoteException;

    void setDozeAfterScreenOff(boolean z) throws RemoteException;

    boolean setDynamicPowerSaveHint(boolean z, int i) throws RemoteException;

    boolean setFullPowerSavePolicy(BatterySaverPolicyConfig batterySaverPolicyConfig) throws RemoteException;

    void setLowPowerStandbyActiveDuringMaintenance(boolean z) throws RemoteException;

    void setLowPowerStandbyEnabled(boolean z) throws RemoteException;

    void setLowPowerStandbyPolicy(LowPowerStandbyPolicy lowPowerStandbyPolicy) throws RemoteException;

    void setPowerBoost(int i, int i2) throws RemoteException;

    void setPowerMode(int i, boolean z) throws RemoteException;

    boolean setPowerModeChecked(int i, boolean z) throws RemoteException;

    boolean setPowerSaveModeEnabled(boolean z) throws RemoteException;

    void setStayOnSetting(int i) throws RemoteException;

    void shutdown(boolean z, String str, boolean z2) throws RemoteException;

    void suppressAmbientDisplay(String str, boolean z) throws RemoteException;

    void updateWakeLockCallback(IBinder iBinder, IWakeLockCallback iWakeLockCallback) throws RemoteException;

    void updateWakeLockUids(IBinder iBinder, int[] iArr) throws RemoteException;

    void updateWakeLockUidsAsync(IBinder iBinder, int[] iArr) throws RemoteException;

    void updateWakeLockWorkSource(IBinder iBinder, WorkSource workSource, String str) throws RemoteException;

    void userActivity(int i, long j, int i2, int i3) throws RemoteException;

    void wakeUp(long j, int i, String str, String str2) throws RemoteException;

    /* renamed from: android.os.IPowerManager$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IPowerManager {
        @Override // android.p008os.IPowerManager
        public void acquireWakeLock(IBinder lock, int flags, String tag, String packageName, WorkSource ws, String historyTag, int displayId, IWakeLockCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void acquireWakeLockWithUid(IBinder lock, int flags, String tag, String packageName, int uidtoblame, int displayId, IWakeLockCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void releaseWakeLock(IBinder lock, int flags) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void updateWakeLockUids(IBinder lock, int[] uids) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void setPowerBoost(int boost, int durationMs) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void setPowerMode(int mode, boolean enabled) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public boolean setPowerModeChecked(int mode, boolean enabled) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public void updateWakeLockWorkSource(IBinder lock, WorkSource ws, String historyTag) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void updateWakeLockCallback(IBinder lock, IWakeLockCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public boolean isWakeLockLevelSupported(int level) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public void userActivity(int displayId, long time, int event, int flags) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void wakeUp(long time, int reason, String details, String opPackageName) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void goToSleep(long time, int reason, int flags) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void goToSleepWithDisplayId(int displayId, long time, int reason, int flags) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void nap(long time) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public float getBrightnessConstraint(int constraint) throws RemoteException {
            return 0.0f;
        }

        @Override // android.p008os.IPowerManager
        public boolean isInteractive() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public boolean areAutoPowerSaveModesEnabled() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public boolean isPowerSaveMode() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public PowerSaveState getPowerSaveState(int serviceType) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IPowerManager
        public boolean setPowerSaveModeEnabled(boolean mode) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public BatterySaverPolicyConfig getFullPowerSavePolicy() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IPowerManager
        public boolean setFullPowerSavePolicy(BatterySaverPolicyConfig config) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public boolean setDynamicPowerSaveHint(boolean powerSaveHint, int disableThreshold) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public boolean setAdaptivePowerSavePolicy(BatterySaverPolicyConfig config) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public boolean setAdaptivePowerSaveEnabled(boolean enabled) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public int getPowerSaveModeTrigger() throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IPowerManager
        public void setBatteryDischargePrediction(ParcelDuration timeRemaining, boolean isCustomized) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public ParcelDuration getBatteryDischargePrediction() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IPowerManager
        public boolean isBatteryDischargePredictionPersonalized() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public boolean isDeviceIdleMode() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public boolean isLightDeviceIdleMode() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public boolean isLowPowerStandbySupported() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public boolean isLowPowerStandbyEnabled() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public void setLowPowerStandbyEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void setLowPowerStandbyActiveDuringMaintenance(boolean activeDuringMaintenance) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void forceLowPowerStandbyActive(boolean active) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void setLowPowerStandbyPolicy(LowPowerStandbyPolicy policy) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public LowPowerStandbyPolicy getLowPowerStandbyPolicy() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IPowerManager
        public boolean isExemptFromLowPowerStandby() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public boolean isReasonAllowedInLowPowerStandby(int reason) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public boolean isFeatureAllowedInLowPowerStandby(String feature) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public void acquireLowPowerStandbyPorts(IBinder token, List<LowPowerStandbyPortDescription> ports) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void releaseLowPowerStandbyPorts(IBinder token) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public List<LowPowerStandbyPortDescription> getActiveLowPowerStandbyPorts() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IPowerManager
        public void reboot(boolean confirm, String reason, boolean wait) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void rebootSafeMode(boolean confirm, boolean wait) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void shutdown(boolean confirm, String reason, boolean wait) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void crash(String message) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public int getLastShutdownReason() throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IPowerManager
        public int getLastSleepReason() throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IPowerManager
        public void setStayOnSetting(int val) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void boostScreenBrightness(long time) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void acquireWakeLockAsync(IBinder lock, int flags, String tag, String packageName, WorkSource ws, String historyTag) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void releaseWakeLockAsync(IBinder lock, int flags) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void updateWakeLockUidsAsync(IBinder lock, int[] uids) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public boolean isScreenBrightnessBoosted() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public void setAttentionLight(boolean on, int color) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public void setDozeAfterScreenOff(boolean on) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public boolean isAmbientDisplayAvailable() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public void suppressAmbientDisplay(String token, boolean suppress) throws RemoteException {
        }

        @Override // android.p008os.IPowerManager
        public boolean isAmbientDisplaySuppressedForToken(String token) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public boolean isAmbientDisplaySuppressed() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public boolean isAmbientDisplaySuppressedForTokenByApp(String token, int appUid) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPowerManager
        public boolean forceSuspend() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IPowerManager$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IPowerManager {
        public static final String DESCRIPTOR = "android.os.IPowerManager";
        static final int TRANSACTION_acquireLowPowerStandbyPorts = 43;
        static final int TRANSACTION_acquireWakeLock = 1;
        static final int TRANSACTION_acquireWakeLockAsync = 54;
        static final int TRANSACTION_acquireWakeLockWithUid = 2;
        static final int TRANSACTION_areAutoPowerSaveModesEnabled = 18;
        static final int TRANSACTION_boostScreenBrightness = 53;
        static final int TRANSACTION_crash = 49;
        static final int TRANSACTION_forceLowPowerStandbyActive = 37;
        static final int TRANSACTION_forceSuspend = 65;
        static final int TRANSACTION_getActiveLowPowerStandbyPorts = 45;
        static final int TRANSACTION_getBatteryDischargePrediction = 29;
        static final int TRANSACTION_getBrightnessConstraint = 16;
        static final int TRANSACTION_getFullPowerSavePolicy = 22;
        static final int TRANSACTION_getLastShutdownReason = 50;
        static final int TRANSACTION_getLastSleepReason = 51;
        static final int TRANSACTION_getLowPowerStandbyPolicy = 39;
        static final int TRANSACTION_getPowerSaveModeTrigger = 27;
        static final int TRANSACTION_getPowerSaveState = 20;
        static final int TRANSACTION_goToSleep = 13;
        static final int TRANSACTION_goToSleepWithDisplayId = 14;
        static final int TRANSACTION_isAmbientDisplayAvailable = 60;
        static final int TRANSACTION_isAmbientDisplaySuppressed = 63;
        static final int TRANSACTION_isAmbientDisplaySuppressedForToken = 62;
        static final int TRANSACTION_isAmbientDisplaySuppressedForTokenByApp = 64;
        static final int TRANSACTION_isBatteryDischargePredictionPersonalized = 30;
        static final int TRANSACTION_isDeviceIdleMode = 31;
        static final int TRANSACTION_isExemptFromLowPowerStandby = 40;
        static final int TRANSACTION_isFeatureAllowedInLowPowerStandby = 42;
        static final int TRANSACTION_isInteractive = 17;
        static final int TRANSACTION_isLightDeviceIdleMode = 32;
        static final int TRANSACTION_isLowPowerStandbyEnabled = 34;
        static final int TRANSACTION_isLowPowerStandbySupported = 33;
        static final int TRANSACTION_isPowerSaveMode = 19;
        static final int TRANSACTION_isReasonAllowedInLowPowerStandby = 41;
        static final int TRANSACTION_isScreenBrightnessBoosted = 57;
        static final int TRANSACTION_isWakeLockLevelSupported = 10;
        static final int TRANSACTION_nap = 15;
        static final int TRANSACTION_reboot = 46;
        static final int TRANSACTION_rebootSafeMode = 47;
        static final int TRANSACTION_releaseLowPowerStandbyPorts = 44;
        static final int TRANSACTION_releaseWakeLock = 3;
        static final int TRANSACTION_releaseWakeLockAsync = 55;
        static final int TRANSACTION_setAdaptivePowerSaveEnabled = 26;
        static final int TRANSACTION_setAdaptivePowerSavePolicy = 25;
        static final int TRANSACTION_setAttentionLight = 58;
        static final int TRANSACTION_setBatteryDischargePrediction = 28;
        static final int TRANSACTION_setDozeAfterScreenOff = 59;
        static final int TRANSACTION_setDynamicPowerSaveHint = 24;
        static final int TRANSACTION_setFullPowerSavePolicy = 23;
        static final int TRANSACTION_setLowPowerStandbyActiveDuringMaintenance = 36;
        static final int TRANSACTION_setLowPowerStandbyEnabled = 35;
        static final int TRANSACTION_setLowPowerStandbyPolicy = 38;
        static final int TRANSACTION_setPowerBoost = 5;
        static final int TRANSACTION_setPowerMode = 6;
        static final int TRANSACTION_setPowerModeChecked = 7;
        static final int TRANSACTION_setPowerSaveModeEnabled = 21;
        static final int TRANSACTION_setStayOnSetting = 52;
        static final int TRANSACTION_shutdown = 48;
        static final int TRANSACTION_suppressAmbientDisplay = 61;
        static final int TRANSACTION_updateWakeLockCallback = 9;
        static final int TRANSACTION_updateWakeLockUids = 4;
        static final int TRANSACTION_updateWakeLockUidsAsync = 56;
        static final int TRANSACTION_updateWakeLockWorkSource = 8;
        static final int TRANSACTION_userActivity = 11;
        static final int TRANSACTION_wakeUp = 12;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPowerManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPowerManager)) {
                return (IPowerManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "acquireWakeLock";
                case 2:
                    return "acquireWakeLockWithUid";
                case 3:
                    return "releaseWakeLock";
                case 4:
                    return "updateWakeLockUids";
                case 5:
                    return "setPowerBoost";
                case 6:
                    return "setPowerMode";
                case 7:
                    return "setPowerModeChecked";
                case 8:
                    return "updateWakeLockWorkSource";
                case 9:
                    return "updateWakeLockCallback";
                case 10:
                    return "isWakeLockLevelSupported";
                case 11:
                    return "userActivity";
                case 12:
                    return "wakeUp";
                case 13:
                    return "goToSleep";
                case 14:
                    return "goToSleepWithDisplayId";
                case 15:
                    return "nap";
                case 16:
                    return "getBrightnessConstraint";
                case 17:
                    return "isInteractive";
                case 18:
                    return "areAutoPowerSaveModesEnabled";
                case 19:
                    return "isPowerSaveMode";
                case 20:
                    return "getPowerSaveState";
                case 21:
                    return "setPowerSaveModeEnabled";
                case 22:
                    return "getFullPowerSavePolicy";
                case 23:
                    return "setFullPowerSavePolicy";
                case 24:
                    return "setDynamicPowerSaveHint";
                case 25:
                    return "setAdaptivePowerSavePolicy";
                case 26:
                    return "setAdaptivePowerSaveEnabled";
                case 27:
                    return "getPowerSaveModeTrigger";
                case 28:
                    return "setBatteryDischargePrediction";
                case 29:
                    return "getBatteryDischargePrediction";
                case 30:
                    return "isBatteryDischargePredictionPersonalized";
                case 31:
                    return "isDeviceIdleMode";
                case 32:
                    return "isLightDeviceIdleMode";
                case 33:
                    return "isLowPowerStandbySupported";
                case 34:
                    return "isLowPowerStandbyEnabled";
                case 35:
                    return "setLowPowerStandbyEnabled";
                case 36:
                    return "setLowPowerStandbyActiveDuringMaintenance";
                case 37:
                    return "forceLowPowerStandbyActive";
                case 38:
                    return "setLowPowerStandbyPolicy";
                case 39:
                    return "getLowPowerStandbyPolicy";
                case 40:
                    return "isExemptFromLowPowerStandby";
                case 41:
                    return "isReasonAllowedInLowPowerStandby";
                case 42:
                    return "isFeatureAllowedInLowPowerStandby";
                case 43:
                    return "acquireLowPowerStandbyPorts";
                case 44:
                    return "releaseLowPowerStandbyPorts";
                case 45:
                    return "getActiveLowPowerStandbyPorts";
                case 46:
                    return "reboot";
                case 47:
                    return "rebootSafeMode";
                case 48:
                    return "shutdown";
                case 49:
                    return "crash";
                case 50:
                    return "getLastShutdownReason";
                case 51:
                    return "getLastSleepReason";
                case 52:
                    return "setStayOnSetting";
                case 53:
                    return "boostScreenBrightness";
                case 54:
                    return "acquireWakeLockAsync";
                case 55:
                    return "releaseWakeLockAsync";
                case 56:
                    return "updateWakeLockUidsAsync";
                case 57:
                    return "isScreenBrightnessBoosted";
                case 58:
                    return "setAttentionLight";
                case 59:
                    return "setDozeAfterScreenOff";
                case 60:
                    return "isAmbientDisplayAvailable";
                case 61:
                    return "suppressAmbientDisplay";
                case 62:
                    return "isAmbientDisplaySuppressedForToken";
                case 63:
                    return "isAmbientDisplaySuppressed";
                case 64:
                    return "isAmbientDisplaySuppressedForTokenByApp";
                case 65:
                    return "forceSuspend";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IBinder _arg0 = data.readStrongBinder();
                            int _arg1 = data.readInt();
                            String _arg2 = data.readString();
                            String _arg3 = data.readString();
                            WorkSource _arg4 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            String _arg5 = data.readString();
                            int _arg6 = data.readInt();
                            IWakeLockCallback _arg7 = IWakeLockCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            acquireWakeLock(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
                            reply.writeNoException();
                            break;
                        case 2:
                            IBinder _arg02 = data.readStrongBinder();
                            int _arg12 = data.readInt();
                            String _arg22 = data.readString();
                            String _arg32 = data.readString();
                            int _arg42 = data.readInt();
                            int _arg52 = data.readInt();
                            IWakeLockCallback _arg62 = IWakeLockCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            acquireWakeLockWithUid(_arg02, _arg12, _arg22, _arg32, _arg42, _arg52, _arg62);
                            reply.writeNoException();
                            break;
                        case 3:
                            IBinder _arg03 = data.readStrongBinder();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            releaseWakeLock(_arg03, _arg13);
                            reply.writeNoException();
                            break;
                        case 4:
                            IBinder _arg04 = data.readStrongBinder();
                            int[] _arg14 = data.createIntArray();
                            data.enforceNoDataAvail();
                            updateWakeLockUids(_arg04, _arg14);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            setPowerBoost(_arg05, _arg15);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            boolean _arg16 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setPowerMode(_arg06, _arg16);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            boolean _arg17 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result = setPowerModeChecked(_arg07, _arg17);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 8:
                            IBinder _arg08 = data.readStrongBinder();
                            WorkSource _arg18 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            String _arg23 = data.readString();
                            data.enforceNoDataAvail();
                            updateWakeLockWorkSource(_arg08, _arg18, _arg23);
                            reply.writeNoException();
                            break;
                        case 9:
                            IBinder _arg09 = data.readStrongBinder();
                            IWakeLockCallback _arg19 = IWakeLockCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            updateWakeLockCallback(_arg09, _arg19);
                            reply.writeNoException();
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result2 = isWakeLockLevelSupported(_arg010);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            long _arg110 = data.readLong();
                            int _arg24 = data.readInt();
                            int _arg33 = data.readInt();
                            data.enforceNoDataAvail();
                            userActivity(_arg011, _arg110, _arg24, _arg33);
                            reply.writeNoException();
                            break;
                        case 12:
                            long _arg012 = data.readLong();
                            int _arg111 = data.readInt();
                            String _arg25 = data.readString();
                            String _arg34 = data.readString();
                            data.enforceNoDataAvail();
                            wakeUp(_arg012, _arg111, _arg25, _arg34);
                            reply.writeNoException();
                            break;
                        case 13:
                            long _arg013 = data.readLong();
                            int _arg112 = data.readInt();
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            goToSleep(_arg013, _arg112, _arg26);
                            reply.writeNoException();
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            long _arg113 = data.readLong();
                            int _arg27 = data.readInt();
                            int _arg35 = data.readInt();
                            data.enforceNoDataAvail();
                            goToSleepWithDisplayId(_arg014, _arg113, _arg27, _arg35);
                            reply.writeNoException();
                            break;
                        case 15:
                            long _arg015 = data.readLong();
                            data.enforceNoDataAvail();
                            nap(_arg015);
                            reply.writeNoException();
                            break;
                        case 16:
                            int _arg016 = data.readInt();
                            data.enforceNoDataAvail();
                            float _result3 = getBrightnessConstraint(_arg016);
                            reply.writeNoException();
                            reply.writeFloat(_result3);
                            break;
                        case 17:
                            boolean _result4 = isInteractive();
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 18:
                            boolean _result5 = areAutoPowerSaveModesEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 19:
                            boolean _result6 = isPowerSaveMode();
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 20:
                            int _arg017 = data.readInt();
                            data.enforceNoDataAvail();
                            PowerSaveState _result7 = getPowerSaveState(_arg017);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 21:
                            boolean _arg018 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result8 = setPowerSaveModeEnabled(_arg018);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 22:
                            BatterySaverPolicyConfig _result9 = getFullPowerSavePolicy();
                            reply.writeNoException();
                            reply.writeTypedObject(_result9, 1);
                            break;
                        case 23:
                            BatterySaverPolicyConfig _arg019 = (BatterySaverPolicyConfig) data.readTypedObject(BatterySaverPolicyConfig.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result10 = setFullPowerSavePolicy(_arg019);
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            break;
                        case 24:
                            boolean _arg020 = data.readBoolean();
                            int _arg114 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result11 = setDynamicPowerSaveHint(_arg020, _arg114);
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            break;
                        case 25:
                            BatterySaverPolicyConfig _arg021 = (BatterySaverPolicyConfig) data.readTypedObject(BatterySaverPolicyConfig.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result12 = setAdaptivePowerSavePolicy(_arg021);
                            reply.writeNoException();
                            reply.writeBoolean(_result12);
                            break;
                        case 26:
                            boolean _arg022 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result13 = setAdaptivePowerSaveEnabled(_arg022);
                            reply.writeNoException();
                            reply.writeBoolean(_result13);
                            break;
                        case 27:
                            int _result14 = getPowerSaveModeTrigger();
                            reply.writeNoException();
                            reply.writeInt(_result14);
                            break;
                        case 28:
                            ParcelDuration _arg023 = (ParcelDuration) data.readTypedObject(ParcelDuration.CREATOR);
                            boolean _arg115 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setBatteryDischargePrediction(_arg023, _arg115);
                            reply.writeNoException();
                            break;
                        case 29:
                            ParcelDuration _result15 = getBatteryDischargePrediction();
                            reply.writeNoException();
                            reply.writeTypedObject(_result15, 1);
                            break;
                        case 30:
                            boolean _result16 = isBatteryDischargePredictionPersonalized();
                            reply.writeNoException();
                            reply.writeBoolean(_result16);
                            break;
                        case 31:
                            boolean _result17 = isDeviceIdleMode();
                            reply.writeNoException();
                            reply.writeBoolean(_result17);
                            break;
                        case 32:
                            boolean _result18 = isLightDeviceIdleMode();
                            reply.writeNoException();
                            reply.writeBoolean(_result18);
                            break;
                        case 33:
                            boolean _result19 = isLowPowerStandbySupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result19);
                            break;
                        case 34:
                            boolean _result20 = isLowPowerStandbyEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result20);
                            break;
                        case 35:
                            boolean _arg024 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setLowPowerStandbyEnabled(_arg024);
                            reply.writeNoException();
                            break;
                        case 36:
                            boolean _arg025 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setLowPowerStandbyActiveDuringMaintenance(_arg025);
                            reply.writeNoException();
                            break;
                        case 37:
                            boolean _arg026 = data.readBoolean();
                            data.enforceNoDataAvail();
                            forceLowPowerStandbyActive(_arg026);
                            reply.writeNoException();
                            break;
                        case 38:
                            LowPowerStandbyPolicy _arg027 = (LowPowerStandbyPolicy) data.readTypedObject(LowPowerStandbyPolicy.CREATOR);
                            data.enforceNoDataAvail();
                            setLowPowerStandbyPolicy(_arg027);
                            reply.writeNoException();
                            break;
                        case 39:
                            LowPowerStandbyPolicy _result21 = getLowPowerStandbyPolicy();
                            reply.writeNoException();
                            reply.writeTypedObject(_result21, 1);
                            break;
                        case 40:
                            boolean _result22 = isExemptFromLowPowerStandby();
                            reply.writeNoException();
                            reply.writeBoolean(_result22);
                            break;
                        case 41:
                            int _arg028 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result23 = isReasonAllowedInLowPowerStandby(_arg028);
                            reply.writeNoException();
                            reply.writeBoolean(_result23);
                            break;
                        case 42:
                            String _arg029 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result24 = isFeatureAllowedInLowPowerStandby(_arg029);
                            reply.writeNoException();
                            reply.writeBoolean(_result24);
                            break;
                        case 43:
                            IBinder _arg030 = data.readStrongBinder();
                            List<LowPowerStandbyPortDescription> _arg116 = data.createTypedArrayList(LowPowerStandbyPortDescription.CREATOR);
                            data.enforceNoDataAvail();
                            acquireLowPowerStandbyPorts(_arg030, _arg116);
                            reply.writeNoException();
                            break;
                        case 44:
                            IBinder _arg031 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            releaseLowPowerStandbyPorts(_arg031);
                            reply.writeNoException();
                            break;
                        case 45:
                            List<LowPowerStandbyPortDescription> _result25 = getActiveLowPowerStandbyPorts();
                            reply.writeNoException();
                            reply.writeTypedList(_result25, 1);
                            break;
                        case 46:
                            boolean _arg032 = data.readBoolean();
                            String _arg117 = data.readString();
                            boolean _arg28 = data.readBoolean();
                            data.enforceNoDataAvail();
                            reboot(_arg032, _arg117, _arg28);
                            reply.writeNoException();
                            break;
                        case 47:
                            boolean _arg033 = data.readBoolean();
                            boolean _arg118 = data.readBoolean();
                            data.enforceNoDataAvail();
                            rebootSafeMode(_arg033, _arg118);
                            reply.writeNoException();
                            break;
                        case 48:
                            boolean _arg034 = data.readBoolean();
                            String _arg119 = data.readString();
                            boolean _arg29 = data.readBoolean();
                            data.enforceNoDataAvail();
                            shutdown(_arg034, _arg119, _arg29);
                            reply.writeNoException();
                            break;
                        case 49:
                            String _arg035 = data.readString();
                            data.enforceNoDataAvail();
                            crash(_arg035);
                            reply.writeNoException();
                            break;
                        case 50:
                            int _result26 = getLastShutdownReason();
                            reply.writeNoException();
                            reply.writeInt(_result26);
                            break;
                        case 51:
                            int _result27 = getLastSleepReason();
                            reply.writeNoException();
                            reply.writeInt(_result27);
                            break;
                        case 52:
                            int _arg036 = data.readInt();
                            data.enforceNoDataAvail();
                            setStayOnSetting(_arg036);
                            reply.writeNoException();
                            break;
                        case 53:
                            long _arg037 = data.readLong();
                            data.enforceNoDataAvail();
                            boostScreenBrightness(_arg037);
                            reply.writeNoException();
                            break;
                        case 54:
                            IBinder _arg038 = data.readStrongBinder();
                            int _arg120 = data.readInt();
                            String _arg210 = data.readString();
                            String _arg36 = data.readString();
                            WorkSource _arg43 = (WorkSource) data.readTypedObject(WorkSource.CREATOR);
                            String _arg53 = data.readString();
                            data.enforceNoDataAvail();
                            acquireWakeLockAsync(_arg038, _arg120, _arg210, _arg36, _arg43, _arg53);
                            break;
                        case 55:
                            IBinder _arg039 = data.readStrongBinder();
                            int _arg121 = data.readInt();
                            data.enforceNoDataAvail();
                            releaseWakeLockAsync(_arg039, _arg121);
                            break;
                        case 56:
                            IBinder _arg040 = data.readStrongBinder();
                            int[] _arg122 = data.createIntArray();
                            data.enforceNoDataAvail();
                            updateWakeLockUidsAsync(_arg040, _arg122);
                            break;
                        case 57:
                            boolean _result28 = isScreenBrightnessBoosted();
                            reply.writeNoException();
                            reply.writeBoolean(_result28);
                            break;
                        case 58:
                            boolean _arg041 = data.readBoolean();
                            int _arg123 = data.readInt();
                            data.enforceNoDataAvail();
                            setAttentionLight(_arg041, _arg123);
                            reply.writeNoException();
                            break;
                        case 59:
                            boolean _arg042 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setDozeAfterScreenOff(_arg042);
                            reply.writeNoException();
                            break;
                        case 60:
                            boolean _result29 = isAmbientDisplayAvailable();
                            reply.writeNoException();
                            reply.writeBoolean(_result29);
                            break;
                        case 61:
                            String _arg043 = data.readString();
                            boolean _arg124 = data.readBoolean();
                            data.enforceNoDataAvail();
                            suppressAmbientDisplay(_arg043, _arg124);
                            reply.writeNoException();
                            break;
                        case 62:
                            String _arg044 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result30 = isAmbientDisplaySuppressedForToken(_arg044);
                            reply.writeNoException();
                            reply.writeBoolean(_result30);
                            break;
                        case 63:
                            boolean _result31 = isAmbientDisplaySuppressed();
                            reply.writeNoException();
                            reply.writeBoolean(_result31);
                            break;
                        case 64:
                            String _arg045 = data.readString();
                            int _arg125 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result32 = isAmbientDisplaySuppressedForTokenByApp(_arg045, _arg125);
                            reply.writeNoException();
                            reply.writeBoolean(_result32);
                            break;
                        case 65:
                            boolean _result33 = forceSuspend();
                            reply.writeNoException();
                            reply.writeBoolean(_result33);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IPowerManager$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IPowerManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.p008os.IPowerManager
            public void acquireWakeLock(IBinder lock, int flags, String tag, String packageName, WorkSource ws, String historyTag, int displayId, IWakeLockCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    _data.writeInt(flags);
                    _data.writeString(tag);
                    _data.writeString(packageName);
                    _data.writeTypedObject(ws, 0);
                    _data.writeString(historyTag);
                    _data.writeInt(displayId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void acquireWakeLockWithUid(IBinder lock, int flags, String tag, String packageName, int uidtoblame, int displayId, IWakeLockCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    _data.writeInt(flags);
                    _data.writeString(tag);
                    _data.writeString(packageName);
                    _data.writeInt(uidtoblame);
                    _data.writeInt(displayId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void releaseWakeLock(IBinder lock, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    _data.writeInt(flags);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void updateWakeLockUids(IBinder lock, int[] uids) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    _data.writeIntArray(uids);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void setPowerBoost(int boost, int durationMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(boost);
                    _data.writeInt(durationMs);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void setPowerMode(int mode, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean setPowerModeChecked(int mode, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void updateWakeLockWorkSource(IBinder lock, WorkSource ws, String historyTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    _data.writeTypedObject(ws, 0);
                    _data.writeString(historyTag);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void updateWakeLockCallback(IBinder lock, IWakeLockCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isWakeLockLevelSupported(int level) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(level);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void userActivity(int displayId, long time, int event, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeLong(time);
                    _data.writeInt(event);
                    _data.writeInt(flags);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void wakeUp(long time, int reason, String details, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(time);
                    _data.writeInt(reason);
                    _data.writeString(details);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void goToSleep(long time, int reason, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(time);
                    _data.writeInt(reason);
                    _data.writeInt(flags);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void goToSleepWithDisplayId(int displayId, long time, int reason, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeLong(time);
                    _data.writeInt(reason);
                    _data.writeInt(flags);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void nap(long time) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(time);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public float getBrightnessConstraint(int constraint) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(constraint);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isInteractive() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean areAutoPowerSaveModesEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isPowerSaveMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public PowerSaveState getPowerSaveState(int serviceType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serviceType);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    PowerSaveState _result = (PowerSaveState) _reply.readTypedObject(PowerSaveState.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean setPowerSaveModeEnabled(boolean mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(mode);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public BatterySaverPolicyConfig getFullPowerSavePolicy() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    BatterySaverPolicyConfig _result = (BatterySaverPolicyConfig) _reply.readTypedObject(BatterySaverPolicyConfig.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean setFullPowerSavePolicy(BatterySaverPolicyConfig config) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(config, 0);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean setDynamicPowerSaveHint(boolean powerSaveHint, int disableThreshold) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(powerSaveHint);
                    _data.writeInt(disableThreshold);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean setAdaptivePowerSavePolicy(BatterySaverPolicyConfig config) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(config, 0);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean setAdaptivePowerSaveEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public int getPowerSaveModeTrigger() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void setBatteryDischargePrediction(ParcelDuration timeRemaining, boolean isCustomized) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(timeRemaining, 0);
                    _data.writeBoolean(isCustomized);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public ParcelDuration getBatteryDischargePrediction() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    ParcelDuration _result = (ParcelDuration) _reply.readTypedObject(ParcelDuration.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isBatteryDischargePredictionPersonalized() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isDeviceIdleMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isLightDeviceIdleMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isLowPowerStandbySupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isLowPowerStandbyEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void setLowPowerStandbyEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void setLowPowerStandbyActiveDuringMaintenance(boolean activeDuringMaintenance) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(activeDuringMaintenance);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void forceLowPowerStandbyActive(boolean active) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(active);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void setLowPowerStandbyPolicy(LowPowerStandbyPolicy policy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policy, 0);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public LowPowerStandbyPolicy getLowPowerStandbyPolicy() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    LowPowerStandbyPolicy _result = (LowPowerStandbyPolicy) _reply.readTypedObject(LowPowerStandbyPolicy.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isExemptFromLowPowerStandby() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isReasonAllowedInLowPowerStandby(int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(reason);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isFeatureAllowedInLowPowerStandby(String feature) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(feature);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void acquireLowPowerStandbyPorts(IBinder token, List<LowPowerStandbyPortDescription> ports) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeTypedList(ports, 0);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void releaseLowPowerStandbyPorts(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public List<LowPowerStandbyPortDescription> getActiveLowPowerStandbyPorts() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    List<LowPowerStandbyPortDescription> _result = _reply.createTypedArrayList(LowPowerStandbyPortDescription.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void reboot(boolean confirm, String reason, boolean wait) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(confirm);
                    _data.writeString(reason);
                    _data.writeBoolean(wait);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void rebootSafeMode(boolean confirm, boolean wait) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(confirm);
                    _data.writeBoolean(wait);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void shutdown(boolean confirm, String reason, boolean wait) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(confirm);
                    _data.writeString(reason);
                    _data.writeBoolean(wait);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void crash(String message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(message);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public int getLastShutdownReason() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public int getLastSleepReason() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void setStayOnSetting(int val) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(val);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void boostScreenBrightness(long time) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(time);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void acquireWakeLockAsync(IBinder lock, int flags, String tag, String packageName, WorkSource ws, String historyTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    _data.writeInt(flags);
                    _data.writeString(tag);
                    _data.writeString(packageName);
                    _data.writeTypedObject(ws, 0);
                    _data.writeString(historyTag);
                    this.mRemote.transact(54, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void releaseWakeLockAsync(IBinder lock, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    _data.writeInt(flags);
                    this.mRemote.transact(55, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void updateWakeLockUidsAsync(IBinder lock, int[] uids) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    _data.writeIntArray(uids);
                    this.mRemote.transact(56, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isScreenBrightnessBoosted() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void setAttentionLight(boolean on, int color) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(on);
                    _data.writeInt(color);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void setDozeAfterScreenOff(boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(on);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isAmbientDisplayAvailable() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public void suppressAmbientDisplay(String token, boolean suppress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(token);
                    _data.writeBoolean(suppress);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isAmbientDisplaySuppressedForToken(String token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(token);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isAmbientDisplaySuppressed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(63, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean isAmbientDisplaySuppressedForTokenByApp(String token, int appUid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(token);
                    _data.writeInt(appUid);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPowerManager
            public boolean forceSuspend() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 64;
        }
    }

    /* renamed from: android.os.IPowerManager$LowPowerStandbyPolicy */
    /* loaded from: classes3.dex */
    public static class LowPowerStandbyPolicy implements Parcelable {
        public static final Parcelable.Creator<LowPowerStandbyPolicy> CREATOR = new Parcelable.Creator<LowPowerStandbyPolicy>() { // from class: android.os.IPowerManager.LowPowerStandbyPolicy.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public LowPowerStandbyPolicy createFromParcel(Parcel _aidl_source) {
                LowPowerStandbyPolicy _aidl_out = new LowPowerStandbyPolicy();
                _aidl_out.readFromParcel(_aidl_source);
                return _aidl_out;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public LowPowerStandbyPolicy[] newArray(int _aidl_size) {
                return new LowPowerStandbyPolicy[_aidl_size];
            }
        };
        public List<String> allowedFeatures;
        public int allowedReasons = 0;
        public List<String> exemptPackages;
        public String identifier;

        @Override // android.p008os.Parcelable
        public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.writeInt(0);
            _aidl_parcel.writeString(this.identifier);
            _aidl_parcel.writeStringList(this.exemptPackages);
            _aidl_parcel.writeInt(this.allowedReasons);
            _aidl_parcel.writeStringList(this.allowedFeatures);
            int _aidl_end_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.setDataPosition(_aidl_start_pos);
            _aidl_parcel.writeInt(_aidl_end_pos - _aidl_start_pos);
            _aidl_parcel.setDataPosition(_aidl_end_pos);
        }

        public final void readFromParcel(Parcel _aidl_parcel) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            int _aidl_parcelable_size = _aidl_parcel.readInt();
            try {
                if (_aidl_parcelable_size < 4) {
                    throw new BadParcelableException("Parcelable too small");
                }
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.identifier = _aidl_parcel.readString();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.exemptPackages = _aidl_parcel.createStringArrayList();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.allowedReasons = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.allowedFeatures = _aidl_parcel.createStringArrayList();
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
            } catch (Throwable th) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                throw th;
            }
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }
    }

    /* renamed from: android.os.IPowerManager$LowPowerStandbyPortDescription */
    /* loaded from: classes3.dex */
    public static class LowPowerStandbyPortDescription implements Parcelable {
        public static final Parcelable.Creator<LowPowerStandbyPortDescription> CREATOR = new Parcelable.Creator<LowPowerStandbyPortDescription>() { // from class: android.os.IPowerManager.LowPowerStandbyPortDescription.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public LowPowerStandbyPortDescription createFromParcel(Parcel _aidl_source) {
                LowPowerStandbyPortDescription _aidl_out = new LowPowerStandbyPortDescription();
                _aidl_out.readFromParcel(_aidl_source);
                return _aidl_out;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public LowPowerStandbyPortDescription[] newArray(int _aidl_size) {
                return new LowPowerStandbyPortDescription[_aidl_size];
            }
        };
        public byte[] localAddress;
        public int protocol = 0;
        public int portMatcher = 0;
        public int portNumber = 0;

        @Override // android.p008os.Parcelable
        public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.writeInt(0);
            _aidl_parcel.writeInt(this.protocol);
            _aidl_parcel.writeInt(this.portMatcher);
            _aidl_parcel.writeInt(this.portNumber);
            _aidl_parcel.writeByteArray(this.localAddress);
            int _aidl_end_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.setDataPosition(_aidl_start_pos);
            _aidl_parcel.writeInt(_aidl_end_pos - _aidl_start_pos);
            _aidl_parcel.setDataPosition(_aidl_end_pos);
        }

        public final void readFromParcel(Parcel _aidl_parcel) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            int _aidl_parcelable_size = _aidl_parcel.readInt();
            try {
                if (_aidl_parcelable_size < 4) {
                    throw new BadParcelableException("Parcelable too small");
                }
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.protocol = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.portMatcher = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.portNumber = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.localAddress = _aidl_parcel.createByteArray();
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
            } catch (Throwable th) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                throw th;
            }
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }
    }
}
