package android.p008os;

import android.annotation.SystemApi;
import android.content.Context;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
@SystemApi
@Deprecated
/* renamed from: android.os.PowerWhitelistManager */
/* loaded from: classes3.dex */
public class PowerWhitelistManager {
    public static final int EVENT_MMS = 2;
    public static final int EVENT_SMS = 1;
    public static final int EVENT_UNSPECIFIED = 0;
    public static final int REASON_ACTIVITY_RECOGNITION = 103;
    public static final int REASON_ACTIVITY_STARTER = 52;
    public static final int REASON_ALARM_MANAGER_ALARM_CLOCK = 301;
    public static final int REASON_ALARM_MANAGER_WHILE_IDLE = 302;
    public static final int REASON_ALLOWLISTED_PACKAGE = 65;
    public static final int REASON_APPOP = 66;
    public static final int REASON_BACKGROUND_ACTIVITY_PERMISSION = 58;
    public static final int REASON_BACKGROUND_FGS_PERMISSION = 59;
    public static final int REASON_BOOT_COMPLETED = 200;
    public static final int REASON_COMPANION_DEVICE_MANAGER = 57;
    public static final int REASON_DENIED = -1;
    public static final int REASON_DEVICE_DEMO_MODE = 63;
    public static final int REASON_DEVICE_OWNER = 55;
    public static final int REASON_DOMAIN_VERIFICATION_V1 = 307;
    public static final int REASON_DOMAIN_VERIFICATION_V2 = 308;
    public static final int REASON_EVENT_MMS = 315;
    public static final int REASON_EVENT_SMS = 314;
    public static final int REASON_FGS_BINDING = 54;
    public static final int REASON_GEOFENCING = 100;
    public static final int REASON_INSTR_BACKGROUND_ACTIVITY_PERMISSION = 60;
    public static final int REASON_INSTR_BACKGROUND_FGS_PERMISSION = 61;
    public static final int REASON_KEY_CHAIN = 304;
    @SystemApi
    public static final int REASON_LOCATION_PROVIDER = 312;
    public static final int REASON_LOCKED_BOOT_COMPLETED = 202;
    public static final int REASON_MEDIA_BUTTON = 313;
    public static final int REASON_NOTIFICATION_SERVICE = 310;
    public static final int REASON_OTHER = 1;
    public static final int REASON_PACKAGE_REPLACED = 311;
    public static final int REASON_PACKAGE_VERIFIER = 305;
    public static final int REASON_PRE_BOOT_COMPLETED = 201;
    public static final int REASON_PROC_STATE_BFGS = 15;
    public static final int REASON_PROC_STATE_BTOP = 13;
    public static final int REASON_PROC_STATE_FGS = 14;
    public static final int REASON_PROC_STATE_PERSISTENT = 10;
    public static final int REASON_PROC_STATE_PERSISTENT_UI = 11;
    public static final int REASON_PROC_STATE_TOP = 12;
    public static final int REASON_PROFILE_OWNER = 56;
    public static final int REASON_PUSH_MESSAGING = 101;
    public static final int REASON_PUSH_MESSAGING_OVER_QUOTA = 102;
    public static final int REASON_SERVICE_LAUNCH = 303;
    public static final int REASON_SHELL = 316;
    public static final int REASON_START_ACTIVITY_FLAG = 53;
    public static final int REASON_SYNC_MANAGER = 306;
    public static final int REASON_SYSTEM_ALERT_WINDOW_PERMISSION = 62;
    public static final int REASON_SYSTEM_ALLOW_LISTED = 300;
    public static final int REASON_SYSTEM_UID = 51;
    public static final int REASON_UID_VISIBLE = 50;
    public static final int REASON_UNKNOWN = 0;
    public static final int REASON_VPN = 309;
    public static final int TEMPORARY_ALLOWLIST_TYPE_FOREGROUND_SERVICE_ALLOWED = 0;
    public static final int TEMPORARY_ALLOWLIST_TYPE_FOREGROUND_SERVICE_NOT_ALLOWED = 1;
    private final Context mContext;
    private final PowerExemptionManager mPowerExemptionManager;
    private final IDeviceIdleController mService;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerWhitelistManager$ReasonCode */
    /* loaded from: classes3.dex */
    public @interface ReasonCode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerWhitelistManager$TempAllowListType */
    /* loaded from: classes3.dex */
    public @interface TempAllowListType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerWhitelistManager$WhitelistEvent */
    /* loaded from: classes3.dex */
    public @interface WhitelistEvent {
    }

    public PowerWhitelistManager(Context context) {
        this.mContext = context;
        this.mService = ((DeviceIdleManager) context.getSystemService(DeviceIdleManager.class)).getService();
        this.mPowerExemptionManager = (PowerExemptionManager) context.getSystemService(PowerExemptionManager.class);
    }

    @Deprecated
    public void addToWhitelist(String packageName) {
        this.mPowerExemptionManager.addToPermanentAllowList(packageName);
    }

    @Deprecated
    public void addToWhitelist(List<String> packageNames) {
        this.mPowerExemptionManager.addToPermanentAllowList(packageNames);
    }

    @Deprecated
    public int[] getWhitelistedAppIds(boolean includingIdle) {
        return this.mPowerExemptionManager.getAllowListedAppIds(includingIdle);
    }

    @Deprecated
    public boolean isWhitelisted(String packageName, boolean includingIdle) {
        return this.mPowerExemptionManager.isAllowListed(packageName, includingIdle);
    }

    @Deprecated
    public void removeFromWhitelist(String packageName) {
        this.mPowerExemptionManager.removeFromPermanentAllowList(packageName);
    }

    @Deprecated
    public void whitelistAppTemporarily(String packageName, long durationMs, int reasonCode, String reason) {
        this.mPowerExemptionManager.addToTemporaryAllowList(packageName, reasonCode, reason, durationMs);
    }

    @Deprecated
    public void whitelistAppTemporarily(String packageName, long durationMs) {
        this.mPowerExemptionManager.addToTemporaryAllowList(packageName, 0, packageName, durationMs);
    }

    @Deprecated
    public long whitelistAppTemporarilyForEvent(String packageName, int event, String reason) {
        return this.mPowerExemptionManager.addToTemporaryAllowListForEvent(packageName, 0, reason, event);
    }

    @Deprecated
    public long whitelistAppTemporarilyForEvent(String packageName, int event, int reasonCode, String reason) {
        return this.mPowerExemptionManager.addToTemporaryAllowListForEvent(packageName, reasonCode, reason, event);
    }

    @Deprecated
    public static int getReasonCodeFromProcState(int procState) {
        return PowerExemptionManager.getReasonCodeFromProcState(procState);
    }

    @Deprecated
    public static String reasonCodeToString(int reasonCode) {
        return PowerExemptionManager.reasonCodeToString(reasonCode);
    }
}
