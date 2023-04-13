package android.app;

import android.Manifest;
import android.app.compat.CompatChanges;
import android.compat.Compatibility;
import android.content.Context;
import android.content.PermissionChecker;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.ArraySet;
import android.util.SparseArray;
import com.android.internal.compat.CompatibilityChangeConfig;
import com.android.internal.compat.IPlatformCompat;
import com.android.internal.util.ArrayUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
/* loaded from: classes.dex */
public abstract class ForegroundServiceTypePolicy {
    static final boolean DEBUG_FOREGROUND_SERVICE_TYPE_POLICY = false;
    public static final long FGS_TYPE_PERMISSION_CHANGE_ID = 254662522;
    public static final int FGS_TYPE_POLICY_CHECK_DEPRECATED = 2;
    public static final int FGS_TYPE_POLICY_CHECK_DISABLED = 3;
    public static final int FGS_TYPE_POLICY_CHECK_OK = 1;
    public static final int FGS_TYPE_POLICY_CHECK_PERMISSION_DENIED_ENFORCED = 5;
    public static final int FGS_TYPE_POLICY_CHECK_PERMISSION_DENIED_PERMISSIVE = 4;
    public static final int FGS_TYPE_POLICY_CHECK_UNKNOWN = 0;
    static final String TAG = "ForegroundServiceTypePolicy";
    public static final long FGS_TYPE_NONE_DEPRECATION_CHANGE_ID = 255042465;
    public static final long FGS_TYPE_NONE_DISABLED_CHANGE_ID = 255038118;
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_MANIFEST = new ForegroundServiceTypePolicyInfo(-1, FGS_TYPE_NONE_DEPRECATION_CHANGE_ID, FGS_TYPE_NONE_DISABLED_CHANGE_ID, null, null);
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_NONE = new ForegroundServiceTypePolicyInfo(0, FGS_TYPE_NONE_DEPRECATION_CHANGE_ID, FGS_TYPE_NONE_DISABLED_CHANGE_ID, null, null);
    public static final long FGS_TYPE_DATA_SYNC_DEPRECATION_CHANGE_ID = 255039210;
    public static final long FGS_TYPE_DATA_SYNC_DISABLED_CHANGE_ID = 255659651;
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_DATA_SYNC = new ForegroundServiceTypePolicyInfo(1, FGS_TYPE_DATA_SYNC_DEPRECATION_CHANGE_ID, FGS_TYPE_DATA_SYNC_DISABLED_CHANGE_ID, new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.FOREGROUND_SERVICE_DATA_SYNC)}, true), null);
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_MEDIA_PLAYBACK = new ForegroundServiceTypePolicyInfo(2, 0, 0, new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK)}, true), null);
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_PHONE_CALL = new ForegroundServiceTypePolicyInfo(4, 0, 0, new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.FOREGROUND_SERVICE_PHONE_CALL)}, true), new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.MANAGE_OWN_CALLS)}, false));
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_LOCATION = new ForegroundServiceTypePolicyInfo(8, 0, 0, new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.FOREGROUND_SERVICE_LOCATION)}, true), new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.ACCESS_COARSE_LOCATION), new RegularPermission(Manifest.C0000permission.ACCESS_FINE_LOCATION)}, false));
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_CONNECTED_DEVICE = new ForegroundServiceTypePolicyInfo(16, 0, 0, new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.FOREGROUND_SERVICE_CONNECTED_DEVICE)}, true), new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.BLUETOOTH_ADVERTISE), new RegularPermission(Manifest.C0000permission.BLUETOOTH_CONNECT), new RegularPermission(Manifest.C0000permission.BLUETOOTH_SCAN), new RegularPermission(Manifest.C0000permission.CHANGE_NETWORK_STATE), new RegularPermission(Manifest.C0000permission.CHANGE_WIFI_STATE), new RegularPermission(Manifest.C0000permission.CHANGE_WIFI_MULTICAST_STATE), new RegularPermission(Manifest.C0000permission.NFC), new RegularPermission(Manifest.C0000permission.TRANSMIT_IR), new RegularPermission(Manifest.C0000permission.UWB_RANGING), new UsbDevicePermission(), new UsbAccessoryPermission()}, false));
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_MEDIA_PROJECTION = new ForegroundServiceTypePolicyInfo(32, 0, 0, new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.FOREGROUND_SERVICE_MEDIA_PROJECTION)}, true), null);
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_CAMERA = new ForegroundServiceTypePolicyInfo(64, 0, 0, new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.FOREGROUND_SERVICE_CAMERA)}, true), new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.CAMERA), new RegularPermission(Manifest.C0000permission.SYSTEM_CAMERA)}, false));
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_MICROPHONE = new ForegroundServiceTypePolicyInfo(128, 0, 0, new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.FOREGROUND_SERVICE_MICROPHONE)}, true), new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.CAPTURE_AUDIO_HOTWORD), new RegularPermission(Manifest.C0000permission.CAPTURE_AUDIO_OUTPUT), new RegularPermission(Manifest.C0000permission.CAPTURE_MEDIA_OUTPUT), new RegularPermission(Manifest.C0000permission.CAPTURE_TUNER_AUDIO_INPUT), new RegularPermission(Manifest.C0000permission.CAPTURE_VOICE_COMMUNICATION_OUTPUT), new RegularPermission(Manifest.C0000permission.RECORD_AUDIO)}, false));
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_HEALTH = new ForegroundServiceTypePolicyInfo(256, 0, 0, new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.FOREGROUND_SERVICE_HEALTH)}, true), new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.ACTIVITY_RECOGNITION), new RegularPermission(Manifest.C0000permission.BODY_SENSORS), new RegularPermission(Manifest.C0000permission.BODY_SENSORS_WRIST_TEMPERATURE), new RegularPermission(Manifest.C0000permission.HIGH_SAMPLING_RATE_SENSORS)}, false));
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_REMOTE_MESSAGING = new ForegroundServiceTypePolicyInfo(512, 0, 0, new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.FOREGROUND_SERVICE_REMOTE_MESSAGING)}, true), null);
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_SYSTEM_EXEMPTED = new ForegroundServiceTypePolicyInfo(1024, 0, 0, new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.FOREGROUND_SERVICE_SYSTEM_EXEMPTED)}, true), new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.SCHEDULE_EXACT_ALARM), new RegularPermission(Manifest.C0000permission.USE_EXACT_ALARM), new AppOpPermission(47)}, false));
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_SHORT_SERVICE = new ForegroundServiceTypePolicyInfo(2048, 0, 0, null, null);
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_FILE_MANAGEMENT = new ForegroundServiceTypePolicyInfo(4096, 0, 0, new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.FOREGROUND_SERVICE_FILE_MANAGEMENT)}, true), null);
    public static final ForegroundServiceTypePolicyInfo FGS_TYPE_POLICY_SPECIAL_USE = new ForegroundServiceTypePolicyInfo(1073741824, 0, 0, new ForegroundServiceTypePermissions(new ForegroundServiceTypePermission[]{new RegularPermission(Manifest.C0000permission.FOREGROUND_SERVICE_SPECIAL_USE)}, true), null);
    private static ForegroundServiceTypePolicy sDefaultForegroundServiceTypePolicy = null;
    private static final Object sLock = new Object();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ForegroundServicePolicyCheckCode {
    }

    public abstract int checkForegroundServiceTypePolicy(Context context, String str, int i, int i2, boolean z, ForegroundServiceTypePolicyInfo foregroundServiceTypePolicyInfo);

    public abstract ForegroundServiceTypePolicyInfo getForegroundServiceTypePolicyInfo(int i, int i2);

    public static ForegroundServiceTypePolicy getDefaultPolicy() {
        ForegroundServiceTypePolicy foregroundServiceTypePolicy;
        synchronized (sLock) {
            if (sDefaultForegroundServiceTypePolicy == null) {
                sDefaultForegroundServiceTypePolicy = new DefaultForegroundServiceTypePolicy();
            }
            foregroundServiceTypePolicy = sDefaultForegroundServiceTypePolicy;
        }
        return foregroundServiceTypePolicy;
    }

    /* loaded from: classes.dex */
    public static final class ForegroundServiceTypePolicyInfo {
        private static final long INVALID_CHANGE_ID = 0;
        final ForegroundServiceTypePermissions mAllOfPermissions;
        final ForegroundServiceTypePermissions mAnyOfPermissions;
        ForegroundServiceTypePermission mCustomPermission;
        final long mDeprecationChangeId;
        final long mDisabledChangeId;
        final int mType;

        private static boolean isValidChangeId(long changeId) {
            return changeId != 0;
        }

        public ForegroundServiceTypePolicyInfo(int type, long deprecationChangeId, long disabledChangeId, ForegroundServiceTypePermissions allOfPermissions, ForegroundServiceTypePermissions anyOfPermissions) {
            this.mType = type;
            this.mDeprecationChangeId = deprecationChangeId;
            this.mDisabledChangeId = disabledChangeId;
            this.mAllOfPermissions = allOfPermissions;
            this.mAnyOfPermissions = anyOfPermissions;
        }

        public int getForegroundServiceType() {
            return this.mType;
        }

        public String toString() {
            StringBuilder sb = toPermissionString(new StringBuilder());
            sb.append("type=0x");
            sb.append(Integer.toHexString(this.mType));
            sb.append(" deprecationChangeId=");
            sb.append(this.mDeprecationChangeId);
            sb.append(" disabledChangeId=");
            sb.append(this.mDisabledChangeId);
            sb.append(" customPermission=");
            sb.append(this.mCustomPermission);
            return sb.toString();
        }

        public String toPermissionString() {
            return toPermissionString(new StringBuilder()).toString();
        }

        private StringBuilder toPermissionString(StringBuilder sb) {
            if (this.mAllOfPermissions != null) {
                sb.append("all of the permissions ");
                sb.append(this.mAllOfPermissions.toString());
                sb.append(' ');
            }
            if (this.mAnyOfPermissions != null) {
                sb.append("any of the permissions ");
                sb.append(this.mAnyOfPermissions.toString());
                sb.append(' ');
            }
            return sb;
        }

        public void setCustomPermission(ForegroundServiceTypePermission customPermission) {
            this.mCustomPermission = customPermission;
        }

        public Optional<String[]> getRequiredAllOfPermissionsForTest(Context context) {
            ForegroundServiceTypePermissions foregroundServiceTypePermissions = this.mAllOfPermissions;
            if (foregroundServiceTypePermissions == null) {
                return Optional.empty();
            }
            return Optional.of(foregroundServiceTypePermissions.toStringArray(context));
        }

        public Optional<String[]> getRequiredAnyOfPermissionsForTest(Context context) {
            ForegroundServiceTypePermissions foregroundServiceTypePermissions = this.mAnyOfPermissions;
            if (foregroundServiceTypePermissions == null) {
                return Optional.empty();
            }
            return Optional.of(foregroundServiceTypePermissions.toStringArray(context));
        }

        public boolean isTypeDisabled(int callerUid) {
            return isValidChangeId(this.mDisabledChangeId) && CompatChanges.isChangeEnabled(this.mDisabledChangeId, callerUid);
        }

        public void setTypeDisabledForTest(boolean disabled, String packageName) throws RemoteException {
            overrideChangeIdForTest(this.mDisabledChangeId, disabled, packageName);
        }

        public void clearTypeDisabledForTest(String packageName) throws RemoteException {
            clearOverrideForTest(this.mDisabledChangeId, packageName);
        }

        boolean isTypeDeprecated(int callerUid) {
            return isValidChangeId(this.mDeprecationChangeId) && CompatChanges.isChangeEnabled(this.mDeprecationChangeId, callerUid);
        }

        private void overrideChangeIdForTest(long changeId, boolean enable, String packageName) throws RemoteException {
            if (!isValidChangeId(changeId)) {
                return;
            }
            ArraySet<Long> enabled = new ArraySet<>();
            ArraySet<Long> disabled = new ArraySet<>();
            if (enable) {
                enabled.add(Long.valueOf(changeId));
            } else {
                disabled.add(Long.valueOf(changeId));
            }
            CompatibilityChangeConfig overrides = new CompatibilityChangeConfig(new Compatibility.ChangeConfig(enabled, disabled));
            IPlatformCompat platformCompat = IPlatformCompat.Stub.asInterface(ServiceManager.getService(Context.PLATFORM_COMPAT_SERVICE));
            platformCompat.setOverridesForTest(overrides, packageName);
        }

        private void clearOverrideForTest(long changeId, String packageName) throws RemoteException {
            IPlatformCompat platformCompat = IPlatformCompat.Stub.asInterface(ServiceManager.getService(Context.PLATFORM_COMPAT_SERVICE));
            platformCompat.clearOverrideForTest(changeId, packageName);
        }
    }

    /* loaded from: classes.dex */
    public static class ForegroundServiceTypePermissions {
        final boolean mAllOf;
        final ForegroundServiceTypePermission[] mPermissions;

        public ForegroundServiceTypePermissions(ForegroundServiceTypePermission[] permissions, boolean allOf) {
            this.mPermissions = permissions;
            this.mAllOf = allOf;
        }

        public int checkPermissions(Context context, int callerUid, int callerPid, String packageName, boolean allowWhileInUse) {
            ForegroundServiceTypePermission[] foregroundServiceTypePermissionArr;
            if (this.mAllOf) {
                for (ForegroundServiceTypePermission perm : this.mPermissions) {
                    int result = perm.checkPermission(context, callerUid, callerPid, packageName, allowWhileInUse);
                    if (result != 0) {
                        return -1;
                    }
                }
                return 0;
            }
            boolean anyOfGranted = false;
            ForegroundServiceTypePermission[] foregroundServiceTypePermissionArr2 = this.mPermissions;
            int length = foregroundServiceTypePermissionArr2.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                ForegroundServiceTypePermission perm2 = foregroundServiceTypePermissionArr2[i];
                int result2 = perm2.checkPermission(context, callerUid, callerPid, packageName, allowWhileInUse);
                if (result2 != 0) {
                    i++;
                } else {
                    anyOfGranted = true;
                    break;
                }
            }
            return anyOfGranted ? 0 : -1;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("allOf=");
            sb.append(this.mAllOf);
            sb.append(' ');
            sb.append('[');
            for (int i = 0; i < this.mPermissions.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(this.mPermissions[i].toString());
            }
            sb.append(']');
            return sb.toString();
        }

        String[] toStringArray(Context context) {
            ArrayList<String> list = new ArrayList<>();
            int i = 0;
            while (true) {
                ForegroundServiceTypePermission[] foregroundServiceTypePermissionArr = this.mPermissions;
                if (i < foregroundServiceTypePermissionArr.length) {
                    foregroundServiceTypePermissionArr[i].addToList(context, list);
                    i++;
                } else {
                    int i2 = list.size();
                    return (String[]) list.toArray(new String[i2]);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ForegroundServiceTypePermission {
        protected final String mName;

        public abstract int checkPermission(Context context, int i, int i2, String str, boolean z);

        public ForegroundServiceTypePermission(String name) {
            this.mName = name;
        }

        public String toString() {
            return this.mName;
        }

        void addToList(Context context, ArrayList<String> list) {
            list.add(this.mName);
        }
    }

    /* loaded from: classes.dex */
    static class RegularPermission extends ForegroundServiceTypePermission {
        RegularPermission(String name) {
            super(name);
        }

        @Override // android.app.ForegroundServiceTypePolicy.ForegroundServiceTypePermission
        public int checkPermission(Context context, int callerUid, int callerPid, String packageName, boolean allowWhileInUse) {
            return checkPermission(context, this.mName, callerUid, callerPid, packageName, allowWhileInUse);
        }

        int checkPermission(Context context, String name, int callerUid, int callerPid, String packageName, boolean allowWhileInUse) {
            int result = PermissionChecker.checkPermissionForPreflight(context, name, callerPid, callerUid, packageName);
            if (result == 0) {
                return 0;
            }
            if (allowWhileInUse && result == 1) {
                int opCode = AppOpsManager.permissionToOpCode(name);
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
                if (opCode != -1) {
                    int currentMode = appOpsManager.unsafeCheckOpRawNoThrow(opCode, callerUid, packageName);
                    if (currentMode == 4) {
                        return 0;
                    }
                }
            }
            return -1;
        }
    }

    /* loaded from: classes.dex */
    static class AppOpPermission extends ForegroundServiceTypePermission {
        final int mOpCode;

        AppOpPermission(int opCode) {
            super(AppOpsManager.opToPublicName(opCode));
            this.mOpCode = opCode;
        }

        @Override // android.app.ForegroundServiceTypePolicy.ForegroundServiceTypePermission
        public int checkPermission(Context context, int callerUid, int callerPid, String packageName, boolean allowWhileInUse) {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
            int mode = appOpsManager.unsafeCheckOpRawNoThrow(this.mOpCode, callerUid, packageName);
            return (mode == 0 || (allowWhileInUse && mode == 4)) ? 0 : -1;
        }
    }

    /* loaded from: classes.dex */
    static class UsbDevicePermission extends ForegroundServiceTypePermission {
        UsbDevicePermission() {
            super("USB Device");
        }

        @Override // android.app.ForegroundServiceTypePolicy.ForegroundServiceTypePermission
        public int checkPermission(Context context, int callerUid, int callerPid, String packageName, boolean allowWhileInUse) {
            UsbManager usbManager = (UsbManager) context.getSystemService(UsbManager.class);
            HashMap<String, UsbDevice> devices = usbManager.getDeviceList();
            if (!ArrayUtils.isEmpty(devices)) {
                for (UsbDevice device : devices.values()) {
                    if (usbManager.hasPermission(device, packageName, callerPid, callerUid)) {
                        return 0;
                    }
                }
                return -1;
            }
            return -1;
        }
    }

    /* loaded from: classes.dex */
    static class UsbAccessoryPermission extends ForegroundServiceTypePermission {
        UsbAccessoryPermission() {
            super("USB Accessory");
        }

        @Override // android.app.ForegroundServiceTypePolicy.ForegroundServiceTypePermission
        public int checkPermission(Context context, int callerUid, int callerPid, String packageName, boolean allowWhileInUse) {
            UsbManager usbManager = (UsbManager) context.getSystemService(UsbManager.class);
            UsbAccessory[] accessories = usbManager.getAccessoryList();
            if (!ArrayUtils.isEmpty(accessories)) {
                for (UsbAccessory accessory : accessories) {
                    if (usbManager.hasPermission(accessory, callerPid, callerUid)) {
                        return 0;
                    }
                }
                return -1;
            }
            return -1;
        }
    }

    /* loaded from: classes.dex */
    public static class DefaultForegroundServiceTypePolicy extends ForegroundServiceTypePolicy {
        private final SparseArray<ForegroundServiceTypePolicyInfo> mForegroundServiceTypePolicies;

        public DefaultForegroundServiceTypePolicy() {
            SparseArray<ForegroundServiceTypePolicyInfo> sparseArray = new SparseArray<>();
            this.mForegroundServiceTypePolicies = sparseArray;
            sparseArray.put(-1, FGS_TYPE_POLICY_MANIFEST);
            sparseArray.put(0, FGS_TYPE_POLICY_NONE);
            sparseArray.put(1, FGS_TYPE_POLICY_DATA_SYNC);
            sparseArray.put(2, FGS_TYPE_POLICY_MEDIA_PLAYBACK);
            sparseArray.put(4, FGS_TYPE_POLICY_PHONE_CALL);
            sparseArray.put(8, FGS_TYPE_POLICY_LOCATION);
            sparseArray.put(16, FGS_TYPE_POLICY_CONNECTED_DEVICE);
            sparseArray.put(32, FGS_TYPE_POLICY_MEDIA_PROJECTION);
            sparseArray.put(64, FGS_TYPE_POLICY_CAMERA);
            sparseArray.put(128, FGS_TYPE_POLICY_MICROPHONE);
            sparseArray.put(256, FGS_TYPE_POLICY_HEALTH);
            sparseArray.put(512, FGS_TYPE_POLICY_REMOTE_MESSAGING);
            sparseArray.put(1024, FGS_TYPE_POLICY_SYSTEM_EXEMPTED);
            sparseArray.put(2048, FGS_TYPE_POLICY_SHORT_SERVICE);
            sparseArray.put(4096, FGS_TYPE_POLICY_FILE_MANAGEMENT);
            sparseArray.put(1073741824, FGS_TYPE_POLICY_SPECIAL_USE);
        }

        @Override // android.app.ForegroundServiceTypePolicy
        public ForegroundServiceTypePolicyInfo getForegroundServiceTypePolicyInfo(int type, int defaultToType) {
            ForegroundServiceTypePolicyInfo info = this.mForegroundServiceTypePolicies.get(type);
            if (info == null && (info = this.mForegroundServiceTypePolicies.get(defaultToType)) == null) {
                throw new IllegalArgumentException("Invalid default fgs type " + defaultToType);
            }
            return info;
        }

        @Override // android.app.ForegroundServiceTypePolicy
        public int checkForegroundServiceTypePolicy(Context context, String packageName, int callerUid, int callerPid, boolean allowWhileInUse, ForegroundServiceTypePolicyInfo policy) {
            if (policy.isTypeDisabled(callerUid)) {
                return 3;
            }
            int permissionResult = 0;
            if (policy.mAllOfPermissions != null) {
                permissionResult = policy.mAllOfPermissions.checkPermissions(context, callerUid, callerPid, packageName, allowWhileInUse);
            }
            if (permissionResult == 0) {
                boolean checkCustomPermission = true;
                if (policy.mAnyOfPermissions != null && (permissionResult = policy.mAnyOfPermissions.checkPermissions(context, callerUid, callerPid, packageName, allowWhileInUse)) == 0) {
                    checkCustomPermission = false;
                }
                if (checkCustomPermission && policy.mCustomPermission != null) {
                    permissionResult = policy.mCustomPermission.checkPermission(context, callerUid, callerPid, packageName, allowWhileInUse);
                }
            }
            if (permissionResult != 0) {
                if (CompatChanges.isChangeEnabled(ForegroundServiceTypePolicy.FGS_TYPE_PERMISSION_CHANGE_ID, callerUid)) {
                    return 5;
                }
                return 4;
            } else if (policy.isTypeDeprecated(callerUid)) {
                return 2;
            } else {
                return 1;
            }
        }
    }
}
