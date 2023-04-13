package android.p008os;

import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;
import android.p008os.IDeviceIdentifiersPolicyService;
import android.sysprop.DeviceProperties;
import android.sysprop.SocProperties;
import android.sysprop.TelephonyProperties;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.accessibility.common.ShortcutConstants;
import dalvik.system.VMRuntime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
/* renamed from: android.os.Build */
/* loaded from: classes3.dex */
public class Build {
    @Deprecated
    public static final String CPU_ABI;
    @Deprecated
    public static final String CPU_ABI2;
    public static final String FINGERPRINT;
    public static final String HOST;
    public static final int HW_TIMEOUT_MULTIPLIER;
    public static final boolean IS_ARC;
    public static final boolean IS_DEBUGGABLE;
    public static final boolean IS_ENG;
    public static final boolean IS_TREBLE_ENABLED;
    public static final boolean IS_USER;
    public static final boolean IS_USERDEBUG;
    @SystemApi
    public static final boolean PERMISSIONS_REVIEW_REQUIRED = true;
    private static final String TAG = "Build";
    public static final String TAGS;
    public static final long TIME;
    public static final String TYPE;
    public static final String UNKNOWN = "unknown";
    public static final String USER;

    /* renamed from: ID */
    public static final String f303ID = getString("ro.build.id");
    public static final String DISPLAY = getString("ro.build.display.id");
    public static final String PRODUCT = getString("ro.product.name");
    public static final String PRODUCT_FOR_ATTESTATION = getString("ro.product.name_for_attestation");
    public static final String DEVICE = getString("ro.product.device");
    public static final String BOARD = getString("ro.product.board");
    public static final String MANUFACTURER = getString("ro.product.manufacturer");
    public static final String BRAND = getString("ro.product.brand");
    public static final String BRAND_FOR_ATTESTATION = getString("ro.product.brand_for_attestation");
    public static final String MODEL = getString("ro.product.model");
    public static final String MODEL_FOR_ATTESTATION = getString("ro.product.model_for_attestation");
    public static final String SOC_MANUFACTURER = SocProperties.soc_manufacturer().orElse("unknown");
    public static final String SOC_MODEL = SocProperties.soc_model().orElse("unknown");
    public static final String BOOTLOADER = getString("ro.bootloader");
    @Deprecated
    public static final String RADIO = joinListOrElse(TelephonyProperties.baseband_version(), "unknown");
    public static final String HARDWARE = getString("ro.hardware");
    public static final String SKU = getString("ro.boot.hardware.sku");
    public static final String ODM_SKU = getString("ro.boot.product.hardware.sku");
    public static final boolean IS_EMULATOR = getString("ro.boot.qemu").equals("1");
    @Deprecated
    public static final String SERIAL = getString("no.such.thing");
    public static final String[] SUPPORTED_ABIS = getStringList("ro.product.cpu.abilist", ",");
    public static final String[] SUPPORTED_32_BIT_ABIS = getStringList("ro.product.cpu.abilist32", ",");
    public static final String[] SUPPORTED_64_BIT_ABIS = getStringList("ro.product.cpu.abilist64", ",");

    /* renamed from: android.os.Build$VERSION_CODES */
    /* loaded from: classes3.dex */
    public static class VERSION_CODES {
        public static final int BASE = 1;
        public static final int BASE_1_1 = 2;
        public static final int CUPCAKE = 3;
        public static final int CUR_DEVELOPMENT = 10000;
        public static final int DONUT = 4;
        public static final int ECLAIR = 5;
        public static final int ECLAIR_0_1 = 6;
        public static final int ECLAIR_MR1 = 7;
        public static final int FROYO = 8;
        public static final int GINGERBREAD = 9;
        public static final int GINGERBREAD_MR1 = 10;
        public static final int HONEYCOMB = 11;
        public static final int HONEYCOMB_MR1 = 12;
        public static final int HONEYCOMB_MR2 = 13;
        public static final int ICE_CREAM_SANDWICH = 14;
        public static final int ICE_CREAM_SANDWICH_MR1 = 15;
        public static final int JELLY_BEAN = 16;
        public static final int JELLY_BEAN_MR1 = 17;
        public static final int JELLY_BEAN_MR2 = 18;
        public static final int KITKAT = 19;
        public static final int KITKAT_WATCH = 20;

        /* renamed from: L */
        public static final int f304L = 21;
        public static final int LOLLIPOP = 21;
        public static final int LOLLIPOP_MR1 = 22;

        /* renamed from: M */
        public static final int f305M = 23;

        /* renamed from: N */
        public static final int f306N = 24;
        public static final int N_MR1 = 25;

        /* renamed from: O */
        public static final int f307O = 26;
        public static final int O_MR1 = 27;

        /* renamed from: P */
        public static final int f308P = 28;

        /* renamed from: Q */
        public static final int f309Q = 29;

        /* renamed from: R */
        public static final int f310R = 30;

        /* renamed from: S */
        public static final int f311S = 31;
        public static final int S_V2 = 32;
        public static final int TIRAMISU = 33;
        public static final int UPSIDE_DOWN_CAKE = 10000;
    }

    static {
        String[] abiList;
        if (VMRuntime.getRuntime().is64Bit()) {
            abiList = SUPPORTED_64_BIT_ABIS;
        } else {
            abiList = SUPPORTED_32_BIT_ABIS;
        }
        CPU_ABI = abiList[0];
        if (abiList.length > 1) {
            CPU_ABI2 = abiList[1];
        } else {
            CPU_ABI2 = "";
        }
        String string = getString("ro.build.type");
        TYPE = string;
        TAGS = getString("ro.build.tags");
        FINGERPRINT = deriveFingerprint();
        HW_TIMEOUT_MULTIPLIER = SystemProperties.getInt("ro.hw_timeout_multiplier", 1);
        IS_TREBLE_ENABLED = SystemProperties.getBoolean("ro.treble.enabled", false);
        TIME = getLong("ro.build.date.utc") * 1000;
        USER = getString("ro.build.user");
        HOST = getString("ro.build.host");
        IS_DEBUGGABLE = SystemProperties.getInt("ro.debuggable", 0) == 1;
        IS_ENG = "eng".equals(string);
        IS_USERDEBUG = "userdebug".equals(string);
        IS_USER = "user".equals(string);
        IS_ARC = SystemProperties.getBoolean("ro.boot.container", false);
    }

    public static String getSerial() {
        IDeviceIdentifiersPolicyService service = IDeviceIdentifiersPolicyService.Stub.asInterface(ServiceManager.getService(Context.DEVICE_IDENTIFIERS_SERVICE));
        try {
            Application application = ActivityThread.currentApplication();
            String callingPackage = application != null ? application.getPackageName() : null;
            return service.getSerialForPackage(callingPackage, null);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return "unknown";
        }
    }

    public static boolean is64BitAbi(String abi) {
        return VMRuntime.is64BitAbi(abi);
    }

    /* renamed from: android.os.Build$VERSION */
    /* loaded from: classes3.dex */
    public static class VERSION {
        public static final String[] ACTIVE_CODENAMES;
        private static final String[] ALL_CODENAMES;
        public static final String CODENAME;
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public static final int DEVICE_INITIAL_SDK_INT;
        @SystemApi
        public static final Set<String> KNOWN_CODENAMES;
        public static final int MIN_SUPPORTED_TARGET_SDK_INT;
        @SystemApi
        public static final String PREVIEW_SDK_FINGERPRINT;
        public static final int PREVIEW_SDK_INT;
        public static final int RESOURCES_SDK_INT;
        public static final int SDK_INT;
        public static final String INCREMENTAL = Build.getString("ro.build.version.incremental");
        public static final String RELEASE = Build.getString("ro.build.version.release");
        public static final String RELEASE_OR_CODENAME = Build.getString("ro.build.version.release_or_codename");
        public static final String RELEASE_OR_PREVIEW_DISPLAY = Build.getString("ro.build.version.release_or_preview_display");
        public static final String BASE_OS = SystemProperties.get("ro.build.version.base_os", "");
        public static final String SECURITY_PATCH = SystemProperties.get("ro.build.version.security_patch", "");
        public static final int MEDIA_PERFORMANCE_CLASS = DeviceProperties.media_performance_class().orElse(0).intValue();
        @Deprecated
        public static final String SDK = Build.getString("ro.build.version.sdk");

        static {
            int i = SystemProperties.getInt("ro.build.version.sdk", 0);
            SDK_INT = i;
            DEVICE_INITIAL_SDK_INT = SystemProperties.getInt("ro.product.first_api_level", 0);
            PREVIEW_SDK_INT = SystemProperties.getInt("ro.build.version.preview_sdk", 0);
            PREVIEW_SDK_FINGERPRINT = SystemProperties.get("ro.build.version.preview_sdk_fingerprint", "REL");
            CODENAME = Build.getString("ro.build.version.codename");
            KNOWN_CODENAMES = new ArraySet(Build.getStringList("ro.build.version.known_codenames", ","));
            String[] stringList = Build.getStringList("ro.build.version.all_codenames", ",");
            ALL_CODENAMES = stringList;
            if ("REL".equals(stringList[0])) {
                stringList = new String[0];
            }
            ACTIVE_CODENAMES = stringList;
            RESOURCES_SDK_INT = i + stringList.length;
            MIN_SUPPORTED_TARGET_SDK_INT = SystemProperties.getInt("ro.build.version.min_supported_target_sdk", 0);
        }
    }

    private static String deriveFingerprint() {
        String finger = SystemProperties.get("ro.build.fingerprint");
        if (TextUtils.isEmpty(finger)) {
            return getString("ro.product.brand") + '/' + getString("ro.product.name") + '/' + getString("ro.product.device") + ShortcutConstants.SERVICES_SEPARATOR + getString("ro.build.version.release") + '/' + getString("ro.build.id") + '/' + getString("ro.build.version.incremental") + ShortcutConstants.SERVICES_SEPARATOR + getString("ro.build.type") + '/' + getString("ro.build.tags");
        }
        return finger;
    }

    public static void ensureFingerprintProperty() {
        if (TextUtils.isEmpty(SystemProperties.get("ro.build.fingerprint"))) {
            try {
                SystemProperties.set("ro.build.fingerprint", FINGERPRINT);
            } catch (IllegalArgumentException e) {
                Slog.m95e(TAG, "Failed to set fingerprint property", e);
            }
        }
    }

    public static boolean isBuildConsistent() {
        if (IS_ENG) {
            return true;
        }
        if (IS_TREBLE_ENABLED) {
            int result = VintfObject.verifyWithoutAvb();
            if (result != 0) {
                Slog.m96e(TAG, "Vendor interface is incompatible, error=" + String.valueOf(result));
            }
            return result == 0;
        }
        String system = SystemProperties.get("ro.system.build.fingerprint");
        String vendor = SystemProperties.get("ro.vendor.build.fingerprint");
        SystemProperties.get("ro.bootimage.build.fingerprint");
        SystemProperties.get("ro.build.expect.bootloader");
        SystemProperties.get("ro.bootloader");
        SystemProperties.get("ro.build.expect.baseband");
        joinListOrElse(TelephonyProperties.baseband_version(), "");
        if (TextUtils.isEmpty(system)) {
            Slog.m96e(TAG, "Required ro.system.build.fingerprint is empty!");
            return false;
        } else if (TextUtils.isEmpty(vendor) || Objects.equals(system, vendor)) {
            return true;
        } else {
            Slog.m96e(TAG, "Mismatched fingerprints; system reported " + system + " but vendor reported " + vendor);
            return false;
        }
    }

    /* renamed from: android.os.Build$Partition */
    /* loaded from: classes3.dex */
    public static class Partition {
        public static final String PARTITION_NAME_BOOTIMAGE = "bootimage";
        public static final String PARTITION_NAME_ODM = "odm";
        public static final String PARTITION_NAME_OEM = "oem";
        public static final String PARTITION_NAME_PRODUCT = "product";
        public static final String PARTITION_NAME_SYSTEM = "system";
        public static final String PARTITION_NAME_SYSTEM_EXT = "system_ext";
        public static final String PARTITION_NAME_VENDOR = "vendor";
        private final String mFingerprint;
        private final String mName;
        private final long mTimeMs;

        private Partition(String name, String fingerprint, long timeMs) {
            this.mName = name;
            this.mFingerprint = fingerprint;
            this.mTimeMs = timeMs;
        }

        public String getName() {
            return this.mName;
        }

        public String getFingerprint() {
            return this.mFingerprint;
        }

        public long getBuildTimeMillis() {
            return this.mTimeMs;
        }

        public boolean equals(Object o) {
            if (o instanceof Partition) {
                Partition op = (Partition) o;
                return this.mName.equals(op.mName) && this.mFingerprint.equals(op.mFingerprint) && this.mTimeMs == op.mTimeMs;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mName, this.mFingerprint, Long.valueOf(this.mTimeMs));
        }
    }

    public static List<Partition> getFingerprintedPartitions() {
        ArrayList<Partition> partitions = new ArrayList<>();
        String[] names = {Partition.PARTITION_NAME_BOOTIMAGE, Partition.PARTITION_NAME_ODM, "product", Partition.PARTITION_NAME_SYSTEM_EXT, "system", "vendor"};
        for (String name : names) {
            String fingerprint = SystemProperties.get("ro." + name + ".build.fingerprint");
            if (!TextUtils.isEmpty(fingerprint)) {
                long time = getLong("ro." + name + ".build.date.utc") * 1000;
                partitions.add(new Partition(name, fingerprint, time));
            }
        }
        return partitions;
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static boolean isDebuggable() {
        return IS_DEBUGGABLE;
    }

    public static String getRadioVersion() {
        return joinListOrElse(TelephonyProperties.baseband_version(), null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getString(String property) {
        return SystemProperties.get(property, "unknown");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String[] getStringList(String property, String separator) {
        String value = SystemProperties.get(property);
        if (value.isEmpty()) {
            return new String[0];
        }
        return value.split(separator);
    }

    private static long getLong(String property) {
        try {
            return Long.parseLong(SystemProperties.get(property));
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    private static <T> String joinListOrElse(List<T> list, String defaultValue) {
        String ret = (String) list.stream().map(new Function() { // from class: android.os.Build$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Build.lambda$joinListOrElse$0(obj);
            }
        }).collect(Collectors.joining(","));
        return ret.isEmpty() ? defaultValue : ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ String lambda$joinListOrElse$0(Object elem) {
        return elem == null ? "" : elem.toString();
    }
}
