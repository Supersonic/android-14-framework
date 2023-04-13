package android.p008os;

import android.annotation.SystemApi;
import android.content.Context;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.VpnManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
@SystemApi
/* renamed from: android.os.PowerExemptionManager */
/* loaded from: classes3.dex */
public class PowerExemptionManager {
    public static final int EVENT_MMS = 2;
    public static final int EVENT_SMS = 1;
    public static final int EVENT_UNSPECIFIED = 0;
    public static final int REASON_ACCOUNT_TRANSFER = 104;
    public static final int REASON_ACTIVE_DEVICE_ADMIN = 324;
    public static final int REASON_ACTIVITY_RECOGNITION = 103;
    public static final int REASON_ACTIVITY_STARTER = 52;
    public static final int REASON_ACTIVITY_VISIBILITY_GRACE_PERIOD = 67;
    public static final int REASON_ALARM_MANAGER_ALARM_CLOCK = 301;
    public static final int REASON_ALARM_MANAGER_WHILE_IDLE = 302;
    public static final int REASON_ALLOWLISTED_PACKAGE = 65;
    public static final int REASON_APPOP = 66;
    public static final int REASON_BACKGROUND_ACTIVITY_PERMISSION = 58;
    public static final int REASON_BACKGROUND_FGS_PERMISSION = 59;
    public static final int REASON_BLUETOOTH_BROADCAST = 203;
    public static final int REASON_BOOT_COMPLETED = 200;
    public static final int REASON_CARRIER_PRIVILEGED_APP = 321;
    public static final int REASON_COMPANION_DEVICE_MANAGER = 57;
    public static final int REASON_CURRENT_INPUT_METHOD = 71;
    public static final int REASON_DENIED = -1;
    public static final int REASON_DEVICE_DEMO_MODE = 63;
    public static final int REASON_DEVICE_OWNER = 55;
    public static final int REASON_DISALLOW_APPS_CONTROL = 323;
    public static final int REASON_DOMAIN_VERIFICATION_V1 = 307;
    public static final int REASON_DOMAIN_VERIFICATION_V2 = 308;
    public static final int REASON_DPO_PROTECTED_APP = 322;
    public static final int REASON_EVENT_MMS = 315;
    public static final int REASON_EVENT_SMS = 314;
    public static final int REASON_FGS_BINDING = 54;
    public static final int REASON_GEOFENCING = 100;
    public static final int REASON_INSTR_BACKGROUND_ACTIVITY_PERMISSION = 60;
    public static final int REASON_INSTR_BACKGROUND_FGS_PERMISSION = 61;
    public static final int REASON_KEY_CHAIN = 304;
    public static final int REASON_LOCALE_CHANGED = 206;
    @SystemApi
    public static final int REASON_LOCATION_PROVIDER = 312;
    public static final int REASON_LOCKED_BOOT_COMPLETED = 202;
    public static final int REASON_MEDIA_BUTTON = 313;
    public static final int REASON_MEDIA_NOTIFICATION_TRANSFER = 325;
    public static final int REASON_MEDIA_SESSION_CALLBACK = 317;
    public static final int REASON_NOTIFICATION_SERVICE = 310;
    public static final int REASON_OPT_OUT_REQUESTED = 1000;
    public static final int REASON_OP_ACTIVATE_PLATFORM_VPN = 69;
    public static final int REASON_OP_ACTIVATE_VPN = 68;
    public static final int REASON_OTHER = 1;
    public static final int REASON_PACKAGE_INSTALLER = 326;
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
    public static final int REASON_REFRESH_SAFETY_SOURCES = 208;
    public static final int REASON_ROLE_DIALER = 318;
    public static final int REASON_ROLE_EMERGENCY = 319;
    public static final int REASON_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED = 207;
    public static final int REASON_SERVICE_LAUNCH = 303;
    public static final int REASON_SHELL = 316;
    public static final int REASON_START_ACTIVITY_FLAG = 53;
    public static final int REASON_SYNC_MANAGER = 306;
    public static final int REASON_SYSTEM_ALERT_WINDOW_PERMISSION = 62;
    public static final int REASON_SYSTEM_ALLOW_LISTED = 300;
    public static final int REASON_SYSTEM_EXEMPT_APP_OP = 327;
    public static final int REASON_SYSTEM_MODULE = 320;
    public static final int REASON_SYSTEM_UID = 51;
    public static final int REASON_TEMP_ALLOWED_WHILE_IN_USE = 70;
    public static final int REASON_TIMEZONE_CHANGED = 204;
    public static final int REASON_TIME_CHANGED = 205;
    public static final int REASON_UID_VISIBLE = 50;
    public static final int REASON_UNKNOWN = 0;
    public static final int REASON_VPN = 309;
    public static final int TEMPORARY_ALLOW_LIST_TYPE_FOREGROUND_SERVICE_ALLOWED = 0;
    public static final int TEMPORARY_ALLOW_LIST_TYPE_FOREGROUND_SERVICE_NOT_ALLOWED = 1;
    public static final int TEMPORARY_ALLOW_LIST_TYPE_NONE = -1;
    private final Context mContext;
    private final IDeviceIdleController mService;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerExemptionManager$AllowListEvent */
    /* loaded from: classes3.dex */
    public @interface AllowListEvent {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerExemptionManager$ReasonCode */
    /* loaded from: classes3.dex */
    public @interface ReasonCode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerExemptionManager$TempAllowListType */
    /* loaded from: classes3.dex */
    public @interface TempAllowListType {
    }

    public PowerExemptionManager(Context context) {
        this.mContext = context;
        this.mService = ((DeviceIdleManager) context.getSystemService(DeviceIdleManager.class)).getService();
    }

    public void addToPermanentAllowList(String packageName) {
        addToPermanentAllowList(Collections.singletonList(packageName));
    }

    public void addToPermanentAllowList(List<String> packageNames) {
        try {
            this.mService.addPowerSaveWhitelistApps(packageNames);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int[] getAllowListedAppIds(boolean includingIdle) {
        try {
            if (includingIdle) {
                return this.mService.getAppIdWhitelist();
            }
            return this.mService.getAppIdWhitelistExceptIdle();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isAllowListed(String packageName, boolean includingIdle) {
        try {
            if (includingIdle) {
                return this.mService.isPowerSaveWhitelistApp(packageName);
            }
            return this.mService.isPowerSaveWhitelistExceptIdleApp(packageName);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void removeFromPermanentAllowList(String packageName) {
        try {
            this.mService.removePowerSaveWhitelistApp(packageName);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void addToTemporaryAllowList(String packageName, int reasonCode, String reason, long durationMs) {
        try {
            this.mService.addPowerSaveTempWhitelistApp(packageName, durationMs, this.mContext.getUserId(), reasonCode, reason);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public long addToTemporaryAllowListForEvent(String packageName, int reasonCode, String reason, int event) {
        try {
            switch (event) {
                case 1:
                    return this.mService.addPowerSaveTempWhitelistAppForSms(packageName, this.mContext.getUserId(), reasonCode, reason);
                case 2:
                    return this.mService.addPowerSaveTempWhitelistAppForMms(packageName, this.mContext.getUserId(), reasonCode, reason);
                default:
                    return this.mService.whitelistAppTemporarily(packageName, this.mContext.getUserId(), reasonCode, reason);
            }
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static int getReasonCodeFromProcState(int procState) {
        if (procState <= 0) {
            return 10;
        }
        if (procState <= 1) {
            return 11;
        }
        if (procState <= 2) {
            return 12;
        }
        if (procState <= 3) {
            return 13;
        }
        if (procState <= 4) {
            return 14;
        }
        if (procState <= 5) {
            return 15;
        }
        return -1;
    }

    public static int getExemptionReasonForStatsd(int reasonCode) {
        switch (reasonCode) {
            case 10:
                return 10;
            case 11:
                return 11;
            case 51:
                return 51;
            case 55:
                return 55;
            case 56:
                return 56;
            case 57:
                return 57;
            case 63:
                return 63;
            case 65:
                return 65;
            case 68:
                return 68;
            case 69:
                return 69;
            case 300:
                return 300;
            case 318:
                return 318;
            case 319:
                return 319;
            case 320:
                return 320;
            case 321:
                return 321;
            case 322:
                return 322;
            case 323:
                return 323;
            case 324:
                return 324;
            default:
                return 1;
        }
    }

    public static String reasonCodeToString(int reasonCode) {
        switch (reasonCode) {
            case -1:
                return "DENIED";
            case 0:
                return "UNKNOWN";
            case 1:
                return "OTHER";
            case 10:
                return "PROC_STATE_PERSISTENT";
            case 11:
                return "PROC_STATE_PERSISTENT_UI";
            case 12:
                return "PROC_STATE_TOP";
            case 13:
                return "PROC_STATE_BTOP";
            case 14:
                return "PROC_STATE_FGS";
            case 15:
                return "PROC_STATE_BFGS";
            case 50:
                return "UID_VISIBLE";
            case 51:
                return "SYSTEM_UID";
            case 52:
                return "ACTIVITY_STARTER";
            case 53:
                return "START_ACTIVITY_FLAG";
            case 54:
                return "FGS_BINDING";
            case 55:
                return "DEVICE_OWNER";
            case 56:
                return "PROFILE_OWNER";
            case 57:
                return "COMPANION_DEVICE_MANAGER";
            case 58:
                return "BACKGROUND_ACTIVITY_PERMISSION";
            case 59:
                return "BACKGROUND_FGS_PERMISSION";
            case 60:
                return "INSTR_BACKGROUND_ACTIVITY_PERMISSION";
            case 61:
                return "INSTR_BACKGROUND_FGS_PERMISSION";
            case 62:
                return "SYSTEM_ALERT_WINDOW_PERMISSION";
            case 63:
                return "DEVICE_DEMO_MODE";
            case 65:
                return "ALLOWLISTED_PACKAGE";
            case 66:
                return "APPOP";
            case 67:
                return "ACTIVITY_VISIBILITY_GRACE_PERIOD";
            case 68:
                return "OP_ACTIVATE_VPN";
            case 69:
                return "OP_ACTIVATE_PLATFORM_VPN";
            case 70:
                return "TEMP_ALLOWED_WHILE_IN_USE";
            case 71:
                return "CURRENT_INPUT_METHOD";
            case 100:
                return "GEOFENCING";
            case 101:
                return "PUSH_MESSAGING";
            case 102:
                return "PUSH_MESSAGING_OVER_QUOTA";
            case 103:
                return "ACTIVITY_RECOGNITION";
            case 104:
                return "REASON_ACCOUNT_TRANSFER";
            case 200:
                return "BOOT_COMPLETED";
            case 201:
                return "PRE_BOOT_COMPLETED";
            case 202:
                return "LOCKED_BOOT_COMPLETED";
            case 203:
                return "BLUETOOTH_BROADCAST";
            case 204:
                return "TIMEZONE_CHANGED";
            case 205:
                return "TIME_CHANGED";
            case 206:
                return "LOCALE_CHANGED";
            case 207:
                return "REASON_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED";
            case 208:
                return "REASON_REFRESH_SAFETY_SOURCES";
            case 300:
                return "SYSTEM_ALLOW_LISTED";
            case 301:
                return "ALARM_MANAGER_ALARM_CLOCK";
            case 302:
                return "ALARM_MANAGER_WHILE_IDLE";
            case 303:
                return "SERVICE_LAUNCH";
            case 304:
                return "KEY_CHAIN";
            case 305:
                return "PACKAGE_VERIFIER";
            case 306:
                return "SYNC_MANAGER";
            case 307:
                return "DOMAIN_VERIFICATION_V1";
            case 308:
                return "DOMAIN_VERIFICATION_V2";
            case 309:
                return VpnManager.NOTIFICATION_CHANNEL_VPN;
            case 310:
                return "NOTIFICATION_SERVICE";
            case 311:
                return "PACKAGE_REPLACED";
            case 312:
                return "LOCATION_PROVIDER";
            case 313:
                return "MEDIA_BUTTON";
            case 314:
                return "EVENT_SMS";
            case 315:
                return "EVENT_MMS";
            case 316:
                return "SHELL";
            case 317:
                return "MEDIA_SESSION_CALLBACK";
            case 318:
                return "ROLE_DIALER";
            case 319:
                return "ROLE_EMERGENCY";
            case 320:
                return "SYSTEM_MODULE";
            case 321:
                return "CARRIER_PRIVILEGED_APP";
            case 322:
                return "DPO_PROTECTED_APP";
            case 323:
                return "DISALLOW_APPS_CONTROL";
            case 324:
                return "ACTIVE_DEVICE_ADMIN";
            case 325:
                return "REASON_MEDIA_NOTIFICATION_TRANSFER";
            case 326:
                return "REASON_PACKAGE_INSTALLER";
            case 1000:
                return "REASON_OPT_OUT_REQUESTED";
            default:
                return "(unknown:" + reasonCode + NavigationBarInflaterView.KEY_CODE_END;
        }
    }
}
