package android.hardware.usb;

import android.annotation.SystemApi;
import android.app.slice.Slice;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Binder;
import android.util.Log;
import com.android.internal.accessibility.common.ShortcutConstants;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
@SystemApi
/* loaded from: classes2.dex */
public final class UsbPort {
    public static final int ENABLE_LIMIT_POWER_TRANSFER_ERROR_INTERNAL = 1;
    public static final int ENABLE_LIMIT_POWER_TRANSFER_ERROR_NOT_SUPPORTED = 2;
    public static final int ENABLE_LIMIT_POWER_TRANSFER_ERROR_OTHER = 4;
    public static final int ENABLE_LIMIT_POWER_TRANSFER_ERROR_PORT_MISMATCH = 3;
    public static final int ENABLE_LIMIT_POWER_TRANSFER_SUCCESS = 0;
    public static final int ENABLE_USB_DATA_ERROR_INTERNAL = 1;
    public static final int ENABLE_USB_DATA_ERROR_NOT_SUPPORTED = 2;
    public static final int ENABLE_USB_DATA_ERROR_OTHER = 4;
    public static final int ENABLE_USB_DATA_ERROR_PORT_MISMATCH = 3;
    public static final int ENABLE_USB_DATA_SUCCESS = 0;
    public static final int ENABLE_USB_DATA_WHILE_DOCKED_ERROR_DATA_ENABLED = 4;
    public static final int ENABLE_USB_DATA_WHILE_DOCKED_ERROR_INTERNAL = 1;
    public static final int ENABLE_USB_DATA_WHILE_DOCKED_ERROR_NOT_SUPPORTED = 2;
    public static final int ENABLE_USB_DATA_WHILE_DOCKED_ERROR_OTHER = 5;
    public static final int ENABLE_USB_DATA_WHILE_DOCKED_ERROR_PORT_MISMATCH = 3;
    public static final int ENABLE_USB_DATA_WHILE_DOCKED_SUCCESS = 0;
    public static final int FLAG_ALT_MODE_TYPE_DISPLAYPORT = 1;
    private static final int NUM_DATA_ROLES = 3;
    private static final int POWER_ROLE_OFFSET = 0;
    public static final int RESET_USB_PORT_ERROR_INTERNAL = 1;
    public static final int RESET_USB_PORT_ERROR_NOT_SUPPORTED = 2;
    public static final int RESET_USB_PORT_ERROR_OTHER = 4;
    public static final int RESET_USB_PORT_ERROR_PORT_MISMATCH = 3;
    public static final int RESET_USB_PORT_SUCCESS = 0;
    private static final String TAG = "UsbPort";
    private static final AtomicInteger sUsbOperationCount = new AtomicInteger();
    private final String mId;
    private final int mSupportedAltModes;
    private final int mSupportedContaminantProtectionModes;
    private final int mSupportedModes;
    private final boolean mSupportsComplianceWarnings;
    private final boolean mSupportsEnableContaminantPresenceDetection;
    private final boolean mSupportsEnableContaminantPresenceProtection;
    private final UsbManager mUsbManager;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface AltModeType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    @interface EnableLimitPowerTransferStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    @interface EnableUsbDataStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    @interface EnableUsbDataWhileDockedStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    @interface ResetUsbPortStatus {
    }

    public UsbPort(UsbManager usbManager, String id, int supportedModes, int supportedContaminantProtectionModes, boolean supportsEnableContaminantPresenceProtection, boolean supportsEnableContaminantPresenceDetection) {
        this(usbManager, id, supportedModes, supportedContaminantProtectionModes, supportsEnableContaminantPresenceProtection, supportsEnableContaminantPresenceDetection, false, 0);
    }

    public UsbPort(UsbManager usbManager, String id, int supportedModes, int supportedContaminantProtectionModes, boolean supportsEnableContaminantPresenceProtection, boolean supportsEnableContaminantPresenceDetection, boolean supportsComplianceWarnings, int supportedAltModes) {
        Objects.requireNonNull(id);
        Preconditions.checkFlagsArgument(supportedModes, 15);
        this.mUsbManager = usbManager;
        this.mId = id;
        this.mSupportedModes = supportedModes;
        this.mSupportedContaminantProtectionModes = supportedContaminantProtectionModes;
        this.mSupportsEnableContaminantPresenceProtection = supportsEnableContaminantPresenceProtection;
        this.mSupportsEnableContaminantPresenceDetection = supportsEnableContaminantPresenceDetection;
        this.mSupportsComplianceWarnings = supportsComplianceWarnings;
        this.mSupportedAltModes = supportedAltModes;
    }

    public String getId() {
        return this.mId;
    }

    public int getSupportedModes() {
        return this.mSupportedModes;
    }

    public int getSupportedContaminantProtectionModes() {
        return this.mSupportedContaminantProtectionModes;
    }

    public boolean supportsEnableContaminantPresenceProtection() {
        return this.mSupportsEnableContaminantPresenceProtection;
    }

    public boolean supportsEnableContaminantPresenceDetection() {
        return this.mSupportsEnableContaminantPresenceDetection;
    }

    public UsbPortStatus getStatus() {
        return this.mUsbManager.getPortStatus(this);
    }

    public boolean supportsComplianceWarnings() {
        return this.mSupportsComplianceWarnings;
    }

    public int getSupportedAltModesMask() {
        return this.mSupportedAltModes;
    }

    public boolean isAltModeSupported(int typeMask) {
        return (this.mSupportedAltModes & typeMask) == typeMask;
    }

    public void setRoles(int powerRole, int dataRole) {
        checkRoles(powerRole, dataRole);
        this.mUsbManager.setPortRoles(this, powerRole, dataRole);
    }

    public void resetUsbPort(Executor executor, Consumer<Integer> consumer) {
        int operationId = sUsbOperationCount.incrementAndGet() + Binder.getCallingUid();
        Log.m108i(TAG, "resetUsbPort opId:" + operationId);
        UsbOperationInternal opCallback = new UsbOperationInternal(operationId, this.mId, executor, consumer);
        this.mUsbManager.resetUsbPort(this, operationId, opCallback);
    }

    public int enableUsbData(boolean enable) {
        int operationId = sUsbOperationCount.incrementAndGet() + Binder.getCallingUid();
        Log.m108i(TAG, "enableUsbData opId:" + operationId + " callingUid:" + Binder.getCallingUid());
        UsbOperationInternal opCallback = new UsbOperationInternal(operationId, this.mId);
        if (this.mUsbManager.enableUsbData(this, enable, operationId, opCallback)) {
            opCallback.waitForOperationComplete();
        }
        int result = opCallback.getStatus();
        switch (result) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                return 4;
        }
    }

    public int enableUsbDataWhileDocked() {
        int operationId = sUsbOperationCount.incrementAndGet() + Binder.getCallingUid();
        Log.m108i(TAG, "enableUsbData opId:" + operationId + " callingUid:" + Binder.getCallingUid());
        UsbPortStatus portStatus = getStatus();
        if (portStatus != null && (portStatus.getUsbDataStatus() & 8) != 8) {
            return 4;
        }
        UsbOperationInternal opCallback = new UsbOperationInternal(operationId, this.mId);
        this.mUsbManager.enableUsbDataWhileDocked(this, operationId, opCallback);
        opCallback.waitForOperationComplete();
        int result = opCallback.getStatus();
        switch (result) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                return 5;
        }
    }

    public int enableLimitPowerTransfer(boolean enable) {
        int operationId = sUsbOperationCount.incrementAndGet() + Binder.getCallingUid();
        Log.m108i(TAG, "enableLimitPowerTransfer opId:" + operationId + " callingUid:" + Binder.getCallingUid());
        UsbOperationInternal opCallback = new UsbOperationInternal(operationId, this.mId);
        this.mUsbManager.enableLimitPowerTransfer(this, enable, operationId, opCallback);
        opCallback.waitForOperationComplete();
        int result = opCallback.getStatus();
        switch (result) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                return 4;
        }
    }

    public void enableContaminantDetection(boolean enable) {
        this.mUsbManager.enableContaminantDetection(this, enable);
    }

    public static int combineRolesAsBit(int powerRole, int dataRole) {
        checkRoles(powerRole, dataRole);
        int index = ((powerRole + 0) * 3) + dataRole;
        return 1 << index;
    }

    public static String modeToString(int mode) {
        StringBuilder modeString = new StringBuilder();
        if (mode == 0) {
            return "none";
        }
        if ((mode & 3) == 3) {
            modeString.append("dual, ");
        } else if ((mode & 2) == 2) {
            modeString.append("dfp, ");
        } else if ((mode & 1) == 1) {
            modeString.append("ufp, ");
        }
        if ((mode & 4) == 4) {
            modeString.append("audio_acc, ");
        }
        if ((mode & 8) == 8) {
            modeString.append("debug_acc, ");
        }
        if (modeString.length() == 0) {
            return Integer.toString(mode);
        }
        return modeString.substring(0, modeString.length() - 2);
    }

    public static String powerRoleToString(int role) {
        switch (role) {
            case 0:
                return "no-power";
            case 1:
                return Slice.SUBTYPE_SOURCE;
            case 2:
                return "sink";
            default:
                return Integer.toString(role);
        }
    }

    public static String dataRoleToString(int role) {
        switch (role) {
            case 0:
                return "no-data";
            case 1:
                return "host";
            case 2:
                return UsbManager.EXTRA_DEVICE;
            default:
                return Integer.toString(role);
        }
    }

    public static String contaminantPresenceStatusToString(int contaminantPresenceStatus) {
        switch (contaminantPresenceStatus) {
            case 0:
                return "not-supported";
            case 1:
                return "disabled";
            case 2:
                return "not detected";
            case 3:
                return "detected";
            default:
                return Integer.toString(contaminantPresenceStatus);
        }
    }

    public static String usbDataStatusToString(int usbDataStatus) {
        StringBuilder statusString = new StringBuilder();
        if (usbDataStatus == 0) {
            return "unknown";
        }
        if ((usbDataStatus & 1) == 1) {
            return "enabled";
        }
        if ((usbDataStatus & 2) == 2) {
            statusString.append("disabled-overheat, ");
        }
        if ((usbDataStatus & 4) == 4) {
            statusString.append("disabled-contaminant, ");
        }
        if ((usbDataStatus & 8) == 8) {
            statusString.append("disabled-dock, ");
        }
        if ((usbDataStatus & 16) == 16) {
            statusString.append("disabled-force, ");
        }
        if ((usbDataStatus & 32) == 32) {
            statusString.append("disabled-debug, ");
        }
        if ((usbDataStatus & 64) == 64) {
            statusString.append("disabled-host-dock, ");
        }
        if ((usbDataStatus & 128) == 128) {
            statusString.append("disabled-device-dock, ");
        }
        return statusString.toString().replaceAll(", $", "");
    }

    public static String powerBrickConnectionStatusToString(int powerBrickConnectionStatus) {
        switch (powerBrickConnectionStatus) {
            case 0:
                return "unknown";
            case 1:
                return "connected";
            case 2:
                return "disconnected";
            default:
                return Integer.toString(powerBrickConnectionStatus);
        }
    }

    public static String roleCombinationsToString(int combo) {
        StringBuilder result = new StringBuilder();
        result.append(NavigationBarInflaterView.SIZE_MOD_START);
        boolean first = true;
        while (combo != 0) {
            int index = Integer.numberOfTrailingZeros(combo);
            combo &= ~(1 << index);
            int powerRole = (index / 3) + 0;
            int dataRole = index % 3;
            if (first) {
                first = false;
            } else {
                result.append(", ");
            }
            result.append(powerRoleToString(powerRole));
            result.append(ShortcutConstants.SERVICES_SEPARATOR);
            result.append(dataRoleToString(dataRole));
        }
        result.append(NavigationBarInflaterView.SIZE_MOD_END);
        return result.toString();
    }

    public static String complianceWarningsToString(int[] complianceWarnings) {
        StringBuilder complianceWarningString = new StringBuilder();
        complianceWarningString.append(NavigationBarInflaterView.SIZE_MOD_START);
        if (complianceWarnings != null) {
            for (int warning : complianceWarnings) {
                switch (warning) {
                    case 1:
                        complianceWarningString.append("other, ");
                        break;
                    case 2:
                        complianceWarningString.append("debug accessory, ");
                        break;
                    case 3:
                        complianceWarningString.append("bc12, ");
                        break;
                    case 4:
                        complianceWarningString.append("missing rp, ");
                        break;
                    default:
                        complianceWarningString.append(String.format("Unknown(%d), ", Integer.valueOf(warning)));
                        break;
                }
            }
        }
        complianceWarningString.append(NavigationBarInflaterView.SIZE_MOD_END);
        return complianceWarningString.toString().replaceAll(", ]$", NavigationBarInflaterView.SIZE_MOD_END);
    }

    public static String dpAltModeStatusToString(int dpAltModeStatus) {
        switch (dpAltModeStatus) {
            case 0:
                return "Unknown";
            case 1:
                return "Not Capable";
            case 2:
                return "Capable-Disabled";
            case 3:
                return "Enabled";
            default:
                return Integer.toString(dpAltModeStatus);
        }
    }

    public static void checkMode(int powerRole) {
        Preconditions.checkArgumentInRange(powerRole, 0, 3, "portMode");
    }

    public static void checkPowerRole(int dataRole) {
        Preconditions.checkArgumentInRange(dataRole, 0, 2, "powerRole");
    }

    public static void checkDataRole(int mode) {
        Preconditions.checkArgumentInRange(mode, 0, 2, "powerRole");
    }

    public static void checkRoles(int powerRole, int dataRole) {
        Preconditions.checkArgumentInRange(powerRole, 0, 2, "powerRole");
        Preconditions.checkArgumentInRange(dataRole, 0, 2, "dataRole");
    }

    public boolean isModeSupported(int mode) {
        return (this.mSupportedModes & mode) == mode;
    }

    public String toString() {
        return "UsbPort{id=" + this.mId + ", supportedModes=" + modeToString(this.mSupportedModes) + ", supportedContaminantProtectionModes=" + this.mSupportedContaminantProtectionModes + ", supportsEnableContaminantPresenceProtection=" + this.mSupportsEnableContaminantPresenceProtection + ", supportsEnableContaminantPresenceDetection=" + this.mSupportsEnableContaminantPresenceDetection + ", supportsComplianceWarnings=" + this.mSupportsComplianceWarnings;
    }
}
