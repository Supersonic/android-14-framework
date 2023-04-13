package com.android.server.p006am;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
/* renamed from: com.android.server.am.SettingsToPropertiesMapper */
/* loaded from: classes.dex */
public class SettingsToPropertiesMapper {
    public final ContentResolver mContentResolver;
    public final String[] mDeviceConfigScopes;
    public final String[] mGlobalSettings;
    @VisibleForTesting
    static final String[] sGlobalSettings = {"native_flags_health_check_enabled"};
    @VisibleForTesting
    static final String[] sDeviceConfigScopes = {"activity_manager_native_boot", "camera_native", "configuration", "connectivity", "input_native_boot", "intelligence_content_suggestions", "lmkd_native", "media_native", "mglru_native", "netd_native", "nnapi_native", "profcollect_native_boot", "remote_key_provisioning_native", "runtime_native", "runtime_native_boot", "statsd_native", "statsd_native_boot", "storage_native_boot", "surface_flinger_native_boot", "swcodec_native", "tethering", "vendor_system_native", "vendor_system_native_boot", "virtualization_framework_native", "window_manager_native_boot", "memory_safety_native_boot", "memory_safety_native", "hdmi_control"};

    @VisibleForTesting
    public SettingsToPropertiesMapper(ContentResolver contentResolver, String[] strArr, String[] strArr2) {
        this.mContentResolver = contentResolver;
        this.mGlobalSettings = strArr;
        this.mDeviceConfigScopes = strArr2;
    }

    @VisibleForTesting
    public void updatePropertiesFromSettings() {
        String[] strArr;
        for (final String str : this.mGlobalSettings) {
            Uri uriFor = Settings.Global.getUriFor(str);
            final String makePropertyName = makePropertyName("global_settings", str);
            if (uriFor == null) {
                log("setting uri is null for globalSetting " + str);
            } else if (makePropertyName == null) {
                log("invalid prop name for globalSetting " + str);
            } else {
                ContentObserver contentObserver = new ContentObserver(null) { // from class: com.android.server.am.SettingsToPropertiesMapper.1
                    @Override // android.database.ContentObserver
                    public void onChange(boolean z) {
                        SettingsToPropertiesMapper.this.updatePropertyFromSetting(str, makePropertyName);
                    }
                };
                if (!isNativeFlagsResetPerformed()) {
                    updatePropertyFromSetting(str, makePropertyName);
                }
                this.mContentResolver.registerContentObserver(uriFor, false, contentObserver);
            }
        }
        for (String str2 : this.mDeviceConfigScopes) {
            DeviceConfig.addOnPropertiesChangedListener(str2, AsyncTask.THREAD_POOL_EXECUTOR, new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.am.SettingsToPropertiesMapper$$ExternalSyntheticLambda0
                public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                    SettingsToPropertiesMapper.this.lambda$updatePropertiesFromSettings$0(properties);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePropertiesFromSettings$0(DeviceConfig.Properties properties) {
        String namespace = properties.getNamespace();
        for (String str : properties.getKeyset()) {
            String makePropertyName = makePropertyName(namespace, str);
            if (makePropertyName == null) {
                log("unable to construct system property for " + namespace + "/" + str);
                return;
            }
            setProperty(makePropertyName, properties.getString(str, (String) null));
        }
    }

    public static SettingsToPropertiesMapper start(ContentResolver contentResolver) {
        SettingsToPropertiesMapper settingsToPropertiesMapper = new SettingsToPropertiesMapper(contentResolver, sGlobalSettings, sDeviceConfigScopes);
        settingsToPropertiesMapper.updatePropertiesFromSettings();
        return settingsToPropertiesMapper;
    }

    public static boolean isNativeFlagsResetPerformed() {
        return "true".equals(SystemProperties.get("device_config.reset_performed"));
    }

    public static String[] getResetNativeCategories() {
        if (isNativeFlagsResetPerformed()) {
            String resetFlagsFileContent = getResetFlagsFileContent();
            if (TextUtils.isEmpty(resetFlagsFileContent)) {
                return new String[0];
            }
            String[] split = resetFlagsFileContent.split(";");
            HashSet hashSet = new HashSet();
            for (String str : split) {
                String[] split2 = str.split("\\.");
                if (split2.length < 3) {
                    log("failed to extract category name from property " + str);
                } else {
                    hashSet.add(split2[2]);
                }
            }
            return (String[]) hashSet.toArray(new String[0]);
        }
        return new String[0];
    }

    @VisibleForTesting
    public static String makePropertyName(String str, String str2) {
        String str3 = "persist.device_config." + str + "." + str2;
        if (!str3.matches("^[\\w\\.\\-@:]*$") || str3.contains("..")) {
            return null;
        }
        return str3;
    }

    public final void setProperty(String str, String str2) {
        if (str2 == null) {
            if (TextUtils.isEmpty(SystemProperties.get(str))) {
                return;
            }
            str2 = "";
        } else if (str2.length() > 92) {
            log("key=" + str + " value=" + str2 + " exceeds system property max length.");
            return;
        }
        try {
            SystemProperties.set(str, str2);
        } catch (Exception e) {
            log("Unable to set property " + str + " value '" + str2 + "'", e);
        }
    }

    public static void log(String str, Exception exc) {
        if (Build.IS_DEBUGGABLE) {
            Slog.wtf("SettingsToPropertiesMapper", str, exc);
        } else {
            Slog.e("SettingsToPropertiesMapper", str, exc);
        }
    }

    public static void log(String str) {
        if (Build.IS_DEBUGGABLE) {
            Slog.wtf("SettingsToPropertiesMapper", str);
        } else {
            Slog.e("SettingsToPropertiesMapper", str);
        }
    }

    @VisibleForTesting
    public static String getResetFlagsFileContent() {
        String str = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("/data/server_configurable_flags/reset_flags")));
            str = bufferedReader.readLine();
            bufferedReader.close();
            return str;
        } catch (IOException e) {
            log("failed to read file /data/server_configurable_flags/reset_flags", e);
            return str;
        }
    }

    @VisibleForTesting
    public void updatePropertyFromSetting(String str, String str2) {
        setProperty(str2, Settings.Global.getString(this.mContentResolver, str));
    }
}
