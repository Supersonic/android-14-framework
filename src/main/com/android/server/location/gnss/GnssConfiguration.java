package com.android.server.location.gnss;

import android.content.Context;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.FrameworkStatsLog;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import libcore.io.IoUtils;
/* loaded from: classes.dex */
public class GnssConfiguration {
    public static final boolean DEBUG = Log.isLoggable("GnssConfiguration", 3);
    public final Context mContext;
    public int mEsExtensionSec = 0;
    public final Properties mProperties = new Properties();

    /* loaded from: classes.dex */
    public interface SetCarrierProperty {
        boolean set(int i);
    }

    private static native HalInterfaceVersion native_get_gnss_configuration_version();

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean native_set_emergency_supl_pdn(int i);

    private static native boolean native_set_es_extension_sec(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean native_set_gnss_pos_protocol_select(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean native_set_gps_lock(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean native_set_lpp_profile(int i);

    private static native boolean native_set_satellite_blocklist(int[] iArr, int[] iArr2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean native_set_supl_es(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean native_set_supl_mode(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean native_set_supl_version(int i);

    /* loaded from: classes.dex */
    public static class HalInterfaceVersion {
        public final int mMajor;
        public final int mMinor;

        public HalInterfaceVersion(int i, int i2) {
            this.mMajor = i;
            this.mMinor = i2;
        }
    }

    public GnssConfiguration(Context context) {
        this.mContext = context;
    }

    public Properties getProperties() {
        return this.mProperties;
    }

    public int getEsExtensionSec() {
        return this.mEsExtensionSec;
    }

    public String getSuplHost() {
        return this.mProperties.getProperty("SUPL_HOST");
    }

    public int getSuplPort(int i) {
        return getIntConfig("SUPL_PORT", i);
    }

    public String getC2KHost() {
        return this.mProperties.getProperty("C2K_HOST");
    }

    public int getC2KPort(int i) {
        return getIntConfig("C2K_PORT", i);
    }

    public int getSuplMode(int i) {
        return getIntConfig("SUPL_MODE", i);
    }

    public int getSuplEs(int i) {
        return getIntConfig("SUPL_ES", i);
    }

    public String getLppProfile() {
        return this.mProperties.getProperty("LPP_PROFILE");
    }

    public List<String> getProxyApps() {
        String property = this.mProperties.getProperty("NFW_PROXY_APPS");
        if (TextUtils.isEmpty(property)) {
            return Collections.emptyList();
        }
        String[] split = property.trim().split("\\s+");
        if (split.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(split);
    }

    public boolean isPsdsPeriodicDownloadEnabled() {
        return getBooleanConfig("ENABLE_PSDS_PERIODIC_DOWNLOAD", false);
    }

    public boolean isActiveSimEmergencySuplEnabled() {
        return getBooleanConfig("ENABLE_ACTIVE_SIM_EMERGENCY_SUPL", false);
    }

    public boolean isNiSuplMessageInjectionEnabled() {
        return getBooleanConfig("ENABLE_NI_SUPL_MESSAGE_INJECTION", false);
    }

    public boolean isLongTermPsdsServerConfigured() {
        return (this.mProperties.getProperty("LONGTERM_PSDS_SERVER_1") == null && this.mProperties.getProperty("LONGTERM_PSDS_SERVER_2") == null && this.mProperties.getProperty("LONGTERM_PSDS_SERVER_3") == null) ? false : true;
    }

    public void setSatelliteBlocklist(int[] iArr, int[] iArr2) {
        native_set_satellite_blocklist(iArr, iArr2);
    }

    public HalInterfaceVersion getHalInterfaceVersion() {
        return native_get_gnss_configuration_version();
    }

    public void reloadGpsProperties() {
        reloadGpsProperties(false, -1);
    }

    public void reloadGpsProperties(boolean z, int i) {
        boolean z2 = DEBUG;
        if (z2) {
            Log.d("GnssConfiguration", "Reset GPS properties, previous size = " + this.mProperties.size() + ", inEmergency:" + z + ", activeSubId=" + i);
        }
        loadPropertiesFromCarrierConfig(z, i);
        if (isSimAbsent(this.mContext)) {
            String str = SystemProperties.get("persist.sys.gps.lpp");
            if (!TextUtils.isEmpty(str)) {
                this.mProperties.setProperty("LPP_PROFILE", str);
            }
        }
        loadPropertiesFromGpsDebugConfig(this.mProperties);
        this.mEsExtensionSec = getRangeCheckedConfigEsExtensionSec();
        logConfigurations();
        HalInterfaceVersion halInterfaceVersion = getHalInterfaceVersion();
        if (halInterfaceVersion == null) {
            if (z2) {
                Log.d("GnssConfiguration", "Skipped configuration update because GNSS configuration in GPS HAL is not supported");
                return;
            }
            return;
        }
        if (isConfigEsExtensionSecSupported(halInterfaceVersion) && !native_set_es_extension_sec(this.mEsExtensionSec)) {
            Log.e("GnssConfiguration", "Unable to set ES_EXTENSION_SEC: " + this.mEsExtensionSec);
        }
        HashMap hashMap = new HashMap();
        hashMap.put("SUPL_VER", new SetCarrierProperty() { // from class: com.android.server.location.gnss.GnssConfiguration$$ExternalSyntheticLambda0
            @Override // com.android.server.location.gnss.GnssConfiguration.SetCarrierProperty
            public final boolean set(int i2) {
                boolean native_set_supl_version;
                native_set_supl_version = GnssConfiguration.native_set_supl_version(i2);
                return native_set_supl_version;
            }
        });
        hashMap.put("SUPL_MODE", new SetCarrierProperty() { // from class: com.android.server.location.gnss.GnssConfiguration$$ExternalSyntheticLambda1
            @Override // com.android.server.location.gnss.GnssConfiguration.SetCarrierProperty
            public final boolean set(int i2) {
                boolean native_set_supl_mode;
                native_set_supl_mode = GnssConfiguration.native_set_supl_mode(i2);
                return native_set_supl_mode;
            }
        });
        if (isConfigSuplEsSupported(halInterfaceVersion)) {
            hashMap.put("SUPL_ES", new SetCarrierProperty() { // from class: com.android.server.location.gnss.GnssConfiguration$$ExternalSyntheticLambda2
                @Override // com.android.server.location.gnss.GnssConfiguration.SetCarrierProperty
                public final boolean set(int i2) {
                    boolean native_set_supl_es;
                    native_set_supl_es = GnssConfiguration.native_set_supl_es(i2);
                    return native_set_supl_es;
                }
            });
        }
        hashMap.put("LPP_PROFILE", new SetCarrierProperty() { // from class: com.android.server.location.gnss.GnssConfiguration$$ExternalSyntheticLambda3
            @Override // com.android.server.location.gnss.GnssConfiguration.SetCarrierProperty
            public final boolean set(int i2) {
                boolean native_set_lpp_profile;
                native_set_lpp_profile = GnssConfiguration.native_set_lpp_profile(i2);
                return native_set_lpp_profile;
            }
        });
        hashMap.put("A_GLONASS_POS_PROTOCOL_SELECT", new SetCarrierProperty() { // from class: com.android.server.location.gnss.GnssConfiguration$$ExternalSyntheticLambda4
            @Override // com.android.server.location.gnss.GnssConfiguration.SetCarrierProperty
            public final boolean set(int i2) {
                boolean native_set_gnss_pos_protocol_select;
                native_set_gnss_pos_protocol_select = GnssConfiguration.native_set_gnss_pos_protocol_select(i2);
                return native_set_gnss_pos_protocol_select;
            }
        });
        hashMap.put("USE_EMERGENCY_PDN_FOR_EMERGENCY_SUPL", new SetCarrierProperty() { // from class: com.android.server.location.gnss.GnssConfiguration$$ExternalSyntheticLambda5
            @Override // com.android.server.location.gnss.GnssConfiguration.SetCarrierProperty
            public final boolean set(int i2) {
                boolean native_set_emergency_supl_pdn;
                native_set_emergency_supl_pdn = GnssConfiguration.native_set_emergency_supl_pdn(i2);
                return native_set_emergency_supl_pdn;
            }
        });
        if (isConfigGpsLockSupported(halInterfaceVersion)) {
            hashMap.put("GPS_LOCK", new SetCarrierProperty() { // from class: com.android.server.location.gnss.GnssConfiguration$$ExternalSyntheticLambda6
                @Override // com.android.server.location.gnss.GnssConfiguration.SetCarrierProperty
                public final boolean set(int i2) {
                    boolean native_set_gps_lock;
                    native_set_gps_lock = GnssConfiguration.native_set_gps_lock(i2);
                    return native_set_gps_lock;
                }
            });
        }
        for (Map.Entry entry : hashMap.entrySet()) {
            String str2 = (String) entry.getKey();
            String property = this.mProperties.getProperty(str2);
            if (property != null) {
                try {
                    if (!((SetCarrierProperty) entry.getValue()).set(Integer.decode(property).intValue())) {
                        Log.e("GnssConfiguration", "Unable to set " + str2);
                    }
                } catch (NumberFormatException unused) {
                    Log.e("GnssConfiguration", "Unable to parse propertyName: " + property);
                }
            }
        }
    }

    public final void logConfigurations() {
        FrameworkStatsLog.write(132, getSuplHost(), getSuplPort(0), getC2KHost(), getC2KPort(0), getIntConfig("SUPL_VER", 0), getSuplMode(0), getSuplEs(0) == 1, getIntConfig("LPP_PROFILE", 0), getIntConfig("A_GLONASS_POS_PROTOCOL_SELECT", 0), getIntConfig("USE_EMERGENCY_PDN_FOR_EMERGENCY_SUPL", 0) == 1, getIntConfig("GPS_LOCK", 0), getEsExtensionSec(), this.mProperties.getProperty("NFW_PROXY_APPS"));
    }

    public void loadPropertiesFromCarrierConfig(boolean z, int i) {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService("carrier_config");
        if (carrierConfigManager == null) {
            return;
        }
        int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
        if (!z || i < 0) {
            i = defaultDataSubscriptionId;
        }
        PersistableBundle configForSubId = SubscriptionManager.isValidSubscriptionId(i) ? carrierConfigManager.getConfigForSubId(i) : carrierConfigManager.getConfig();
        if (configForSubId == null) {
            if (DEBUG) {
                Log.d("GnssConfiguration", "SIM not ready, use default carrier config.");
            }
            configForSubId = CarrierConfigManager.getDefaultConfig();
        }
        for (String str : configForSubId.keySet()) {
            if (str.startsWith("gps.")) {
                String upperCase = str.substring(4).toUpperCase();
                Object obj = configForSubId.get(str);
                if (DEBUG) {
                    Log.d("GnssConfiguration", "Gps config: " + upperCase + " = " + obj);
                }
                if (obj instanceof String) {
                    this.mProperties.setProperty(upperCase, (String) obj);
                } else if (obj != null) {
                    this.mProperties.setProperty(upperCase, obj.toString());
                }
            }
        }
    }

    public final void loadPropertiesFromGpsDebugConfig(Properties properties) {
        try {
            FileInputStream fileInputStream = null;
            try {
                FileInputStream fileInputStream2 = new FileInputStream(new File("/etc/gps_debug.conf"));
                try {
                    properties.load(fileInputStream2);
                    IoUtils.closeQuietly(fileInputStream2);
                } catch (Throwable th) {
                    th = th;
                    fileInputStream = fileInputStream2;
                    IoUtils.closeQuietly(fileInputStream);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException unused) {
            if (DEBUG) {
                Log.d("GnssConfiguration", "Could not open GPS configuration file /etc/gps_debug.conf");
            }
        }
    }

    public final int getRangeCheckedConfigEsExtensionSec() {
        int intConfig = getIntConfig("ES_EXTENSION_SEC", 0);
        if (intConfig > 300) {
            Log.w("GnssConfiguration", "ES_EXTENSION_SEC: " + intConfig + " too high, reset to 300");
            return 300;
        } else if (intConfig < 0) {
            Log.w("GnssConfiguration", "ES_EXTENSION_SEC: " + intConfig + " is negative, reset to zero.");
            return 0;
        } else {
            return intConfig;
        }
    }

    public final int getIntConfig(String str, int i) {
        String property = this.mProperties.getProperty(str);
        if (TextUtils.isEmpty(property)) {
            return i;
        }
        try {
            return Integer.decode(property).intValue();
        } catch (NumberFormatException unused) {
            Log.e("GnssConfiguration", "Unable to parse config parameter " + str + " value: " + property + ". Using default value: " + i);
            return i;
        }
    }

    public final boolean getBooleanConfig(String str, boolean z) {
        String property = this.mProperties.getProperty(str);
        return TextUtils.isEmpty(property) ? z : Boolean.parseBoolean(property);
    }

    public static boolean isConfigEsExtensionSecSupported(HalInterfaceVersion halInterfaceVersion) {
        return halInterfaceVersion.mMajor >= 2;
    }

    public static boolean isConfigSuplEsSupported(HalInterfaceVersion halInterfaceVersion) {
        return halInterfaceVersion.mMajor < 2;
    }

    public static boolean isConfigGpsLockSupported(HalInterfaceVersion halInterfaceVersion) {
        return halInterfaceVersion.mMajor < 2;
    }

    public static boolean isSimAbsent(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getSimState() == 1;
    }
}
