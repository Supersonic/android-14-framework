package com.android.server.power.batterysaver;

import android.app.UiModeManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.BatterySaverPolicyConfig;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerSaveState;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.IndentingPrintWriter;
import android.util.KeyValueListParser;
import android.util.Slog;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.ConcurrentUtils;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes2.dex */
public class BatterySaverPolicy extends ContentObserver implements DeviceConfig.OnPropertiesChangedListener {
    public static final Policy DEFAULT_ADAPTIVE_POLICY;
    public static final Policy DEFAULT_FULL_POLICY;
    @VisibleForTesting
    static final String KEY_ADJUST_BRIGHTNESS_FACTOR = "adjust_brightness_factor";
    @VisibleForTesting
    static final String KEY_ADVERTISE_IS_ENABLED = "advertise_is_enabled";
    @VisibleForTesting
    static final String KEY_DEFER_FULL_BACKUP = "defer_full_backup";
    @VisibleForTesting
    static final String KEY_DEFER_KEYVALUE_BACKUP = "defer_keyvalue_backup";
    @VisibleForTesting
    static final String KEY_DISABLE_ANIMATION = "disable_animation";
    @VisibleForTesting
    static final String KEY_DISABLE_AOD = "disable_aod";
    @VisibleForTesting
    static final String KEY_DISABLE_LAUNCH_BOOST = "disable_launch_boost";
    @VisibleForTesting
    static final String KEY_DISABLE_OPTIONAL_SENSORS = "disable_optional_sensors";
    @VisibleForTesting
    static final String KEY_DISABLE_VIBRATION = "disable_vibration";
    @VisibleForTesting
    static final String KEY_ENABLE_BRIGHTNESS_ADJUSTMENT = "enable_brightness_adjustment";
    @VisibleForTesting
    static final String KEY_ENABLE_DATASAVER = "enable_datasaver";
    @VisibleForTesting
    static final String KEY_ENABLE_FIREWALL = "enable_firewall";
    @VisibleForTesting
    static final String KEY_ENABLE_NIGHT_MODE = "enable_night_mode";
    @VisibleForTesting
    static final String KEY_ENABLE_QUICK_DOZE = "enable_quick_doze";
    @VisibleForTesting
    static final String KEY_FORCE_ALL_APPS_STANDBY = "force_all_apps_standby";
    @VisibleForTesting
    static final String KEY_FORCE_BACKGROUND_CHECK = "force_background_check";
    @VisibleForTesting
    static final String KEY_LOCATION_MODE = "location_mode";
    @VisibleForTesting
    static final String KEY_SOUNDTRIGGER_MODE = "soundtrigger_mode";
    @VisibleForTesting
    static final Policy OFF_POLICY;
    @VisibleForTesting
    final PolicyBoolean mAccessibilityEnabled;
    @GuardedBy({"mLock"})
    public Policy mAdaptivePolicy;
    @VisibleForTesting
    final PolicyBoolean mAutomotiveProjectionActive;
    public final BatterySavingStats mBatterySavingStats;
    public final ContentResolver mContentResolver;
    public final Context mContext;
    @GuardedBy({"mLock"})
    public Policy mDefaultAdaptivePolicy;
    @GuardedBy({"mLock"})
    public Policy mDefaultFullPolicy;
    @GuardedBy({"mLock"})
    public String mDeviceSpecificSettings;
    @GuardedBy({"mLock"})
    public String mDeviceSpecificSettingsSource;
    @GuardedBy({"mLock"})
    public Policy mEffectivePolicyRaw;
    @GuardedBy({"mLock"})
    public String mEventLogKeys;
    @GuardedBy({"mLock"})
    public Policy mFullPolicy;
    public final Handler mHandler;
    @GuardedBy({"mLock"})
    public DeviceConfig.Properties mLastDeviceConfigProperties;
    @GuardedBy({"mLock"})
    public final List<BatterySaverPolicyListener> mListeners;
    public final Object mLock;
    public final UiModeManager.OnProjectionStateChangedListener mOnProjectionStateChangedListener;
    @GuardedBy({"mLock"})
    public int mPolicyLevel;
    @GuardedBy({"mLock"})
    public String mSettings;

    /* loaded from: classes2.dex */
    public interface BatterySaverPolicyListener {
        void onBatterySaverPolicyChanged(BatterySaverPolicy batterySaverPolicy);
    }

    @VisibleForTesting
    public int getDeviceSpecificConfigResId() {
        return 17039835;
    }

    static {
        Policy policy = new Policy(1.0f, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, 0, 0);
        OFF_POLICY = policy;
        DEFAULT_ADAPTIVE_POLICY = policy;
        DEFAULT_FULL_POLICY = new Policy(0.5f, true, true, true, false, true, true, true, true, false, false, true, true, true, true, true, 3, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, Set set) {
        this.mAutomotiveProjectionActive.update(!set.isEmpty());
    }

    public BatterySaverPolicy(Object obj, Context context, BatterySavingStats batterySavingStats) {
        super(BackgroundThread.getHandler());
        this.mAccessibilityEnabled = new PolicyBoolean("accessibility");
        this.mAutomotiveProjectionActive = new PolicyBoolean("automotiveProjection");
        Policy policy = DEFAULT_ADAPTIVE_POLICY;
        this.mDefaultAdaptivePolicy = policy;
        this.mAdaptivePolicy = policy;
        Policy policy2 = DEFAULT_FULL_POLICY;
        this.mDefaultFullPolicy = policy2;
        this.mFullPolicy = policy2;
        this.mEffectivePolicyRaw = OFF_POLICY;
        this.mPolicyLevel = 0;
        this.mOnProjectionStateChangedListener = new UiModeManager.OnProjectionStateChangedListener() { // from class: com.android.server.power.batterysaver.BatterySaverPolicy$$ExternalSyntheticLambda2
            public final void onProjectionStateChanged(int i, Set set) {
                BatterySaverPolicy.this.lambda$new$0(i, set);
            }
        };
        this.mListeners = new ArrayList();
        this.mLock = obj;
        this.mHandler = BackgroundThread.getHandler();
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mBatterySavingStats = batterySavingStats;
    }

    public void systemReady() {
        ConcurrentUtils.wtfIfLockHeld("BatterySaverPolicy", this.mLock);
        this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("battery_saver_constants"), false, this);
        this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("battery_saver_device_specific_constants"), false, this);
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.mContext.getSystemService(AccessibilityManager.class);
        accessibilityManager.addAccessibilityStateChangeListener(new AccessibilityManager.AccessibilityStateChangeListener() { // from class: com.android.server.power.batterysaver.BatterySaverPolicy$$ExternalSyntheticLambda1
            @Override // android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener
            public final void onAccessibilityStateChanged(boolean z) {
                BatterySaverPolicy.this.lambda$systemReady$1(z);
            }
        });
        this.mAccessibilityEnabled.initialize(accessibilityManager.isEnabled());
        UiModeManager uiModeManager = (UiModeManager) this.mContext.getSystemService(UiModeManager.class);
        uiModeManager.addOnProjectionStateChangedListener(1, this.mContext.getMainExecutor(), this.mOnProjectionStateChangedListener);
        this.mAutomotiveProjectionActive.initialize(uiModeManager.getActiveProjectionTypes() != 0);
        DeviceConfig.addOnPropertiesChangedListener("battery_saver", this.mContext.getMainExecutor(), this);
        this.mLastDeviceConfigProperties = DeviceConfig.getProperties("battery_saver", new String[0]);
        onChange(true, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$systemReady$1(boolean z) {
        this.mAccessibilityEnabled.update(z);
    }

    @VisibleForTesting
    public void addListener(BatterySaverPolicyListener batterySaverPolicyListener) {
        synchronized (this.mLock) {
            this.mListeners.add(batterySaverPolicyListener);
        }
    }

    @VisibleForTesting
    public String getGlobalSetting(String str) {
        return Settings.Global.getString(this.mContentResolver, str);
    }

    @VisibleForTesting
    public void invalidatePowerSaveModeCaches() {
        PowerManager.invalidatePowerSaveModeCaches();
    }

    public final void maybeNotifyListenersOfPolicyChange() {
        synchronized (this.mLock) {
            if (this.mPolicyLevel == 0) {
                return;
            }
            List<BatterySaverPolicyListener> list = this.mListeners;
            final BatterySaverPolicyListener[] batterySaverPolicyListenerArr = (BatterySaverPolicyListener[]) list.toArray(new BatterySaverPolicyListener[list.size()]);
            this.mHandler.post(new Runnable() { // from class: com.android.server.power.batterysaver.BatterySaverPolicy$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BatterySaverPolicy.this.lambda$maybeNotifyListenersOfPolicyChange$2(batterySaverPolicyListenerArr);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$maybeNotifyListenersOfPolicyChange$2(BatterySaverPolicyListener[] batterySaverPolicyListenerArr) {
        for (BatterySaverPolicyListener batterySaverPolicyListener : batterySaverPolicyListenerArr) {
            batterySaverPolicyListener.onBatterySaverPolicyChanged(this);
        }
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z, Uri uri) {
        refreshSettings();
    }

    public void onPropertiesChanged(DeviceConfig.Properties properties) {
        boolean maybeUpdateDefaultFullPolicy;
        this.mLastDeviceConfigProperties = DeviceConfig.getProperties("battery_saver", new String[0]);
        synchronized (this.mLock) {
            Policy policy = null;
            Policy policy2 = null;
            for (String str : properties.getKeyset()) {
                if (str != null) {
                    if (str.endsWith("_adaptive")) {
                        if (policy2 == null) {
                            policy2 = Policy.fromSettings("", "", this.mLastDeviceConfigProperties, "_adaptive", DEFAULT_ADAPTIVE_POLICY);
                        }
                    } else if (policy == null) {
                        policy = Policy.fromSettings(this.mSettings, this.mDeviceSpecificSettings, this.mLastDeviceConfigProperties, null, DEFAULT_FULL_POLICY);
                    }
                }
            }
            maybeUpdateDefaultFullPolicy = policy != null ? maybeUpdateDefaultFullPolicy(policy) | false : false;
            if (policy2 != null && !this.mAdaptivePolicy.equals(policy2)) {
                this.mDefaultAdaptivePolicy = policy2;
                this.mAdaptivePolicy = policy2;
                maybeUpdateDefaultFullPolicy |= this.mPolicyLevel == 1;
            }
            updatePolicyDependenciesLocked();
        }
        if (maybeUpdateDefaultFullPolicy) {
            maybeNotifyListenersOfPolicyChange();
        }
    }

    public final void refreshSettings() {
        synchronized (this.mLock) {
            String globalSetting = getGlobalSetting("battery_saver_constants");
            String globalSetting2 = getGlobalSetting("battery_saver_device_specific_constants");
            this.mDeviceSpecificSettingsSource = "battery_saver_device_specific_constants";
            if (TextUtils.isEmpty(globalSetting2) || "null".equals(globalSetting2)) {
                globalSetting2 = this.mContext.getString(getDeviceSpecificConfigResId());
                this.mDeviceSpecificSettingsSource = "(overlay)";
            }
            if (updateConstantsLocked(globalSetting, globalSetting2)) {
                maybeNotifyListenersOfPolicyChange();
            }
        }
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public boolean updateConstantsLocked(String str, String str2) {
        String emptyIfNull = TextUtils.emptyIfNull(str);
        String emptyIfNull2 = TextUtils.emptyIfNull(str2);
        if (emptyIfNull.equals(this.mSettings) && emptyIfNull2.equals(this.mDeviceSpecificSettings)) {
            return false;
        }
        this.mSettings = emptyIfNull;
        this.mDeviceSpecificSettings = emptyIfNull2;
        boolean maybeUpdateDefaultFullPolicy = maybeUpdateDefaultFullPolicy(Policy.fromSettings(emptyIfNull, emptyIfNull2, this.mLastDeviceConfigProperties, null, DEFAULT_FULL_POLICY));
        Policy fromSettings = Policy.fromSettings("", "", this.mLastDeviceConfigProperties, "_adaptive", DEFAULT_ADAPTIVE_POLICY);
        this.mDefaultAdaptivePolicy = fromSettings;
        if (this.mPolicyLevel == 1 && !this.mAdaptivePolicy.equals(fromSettings)) {
            maybeUpdateDefaultFullPolicy = true;
        }
        this.mAdaptivePolicy = this.mDefaultAdaptivePolicy;
        updatePolicyDependenciesLocked();
        return maybeUpdateDefaultFullPolicy;
    }

    /* JADX WARN: Code restructure failed: missing block: B:7:0x0016, code lost:
        if (r2 != 3) goto L7;
     */
    @GuardedBy({"mLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void updatePolicyDependenciesLocked() {
        int i;
        Policy currentRawPolicyLocked = getCurrentRawPolicyLocked();
        invalidatePowerSaveModeCaches();
        if (this.mAutomotiveProjectionActive.get() && (r2 = currentRawPolicyLocked.locationMode) != 0) {
            i = 3;
        }
        i = currentRawPolicyLocked.locationMode;
        this.mEffectivePolicyRaw = new Policy(currentRawPolicyLocked.adjustBrightnessFactor, currentRawPolicyLocked.advertiseIsEnabled, currentRawPolicyLocked.deferFullBackup, currentRawPolicyLocked.deferKeyValueBackup, currentRawPolicyLocked.disableAnimation, currentRawPolicyLocked.disableAod, currentRawPolicyLocked.disableLaunchBoost, currentRawPolicyLocked.disableOptionalSensors, currentRawPolicyLocked.disableVibration && !this.mAccessibilityEnabled.get(), currentRawPolicyLocked.enableAdjustBrightness, currentRawPolicyLocked.enableDataSaver, currentRawPolicyLocked.enableFirewall, currentRawPolicyLocked.enableNightMode && !this.mAutomotiveProjectionActive.get(), currentRawPolicyLocked.enableQuickDoze, currentRawPolicyLocked.forceAllAppsStandby, currentRawPolicyLocked.forceBackgroundCheck, i, currentRawPolicyLocked.soundTriggerMode);
        StringBuilder sb = new StringBuilder();
        if (this.mEffectivePolicyRaw.forceAllAppsStandby) {
            sb.append("A");
        }
        if (this.mEffectivePolicyRaw.forceBackgroundCheck) {
            sb.append("B");
        }
        if (this.mEffectivePolicyRaw.disableVibration) {
            sb.append("v");
        }
        if (this.mEffectivePolicyRaw.disableAnimation) {
            sb.append("a");
        }
        sb.append(this.mEffectivePolicyRaw.soundTriggerMode);
        if (this.mEffectivePolicyRaw.deferFullBackup) {
            sb.append("F");
        }
        if (this.mEffectivePolicyRaw.deferKeyValueBackup) {
            sb.append("K");
        }
        if (this.mEffectivePolicyRaw.enableFirewall) {
            sb.append("f");
        }
        if (this.mEffectivePolicyRaw.enableDataSaver) {
            sb.append("d");
        }
        if (this.mEffectivePolicyRaw.enableAdjustBrightness) {
            sb.append("b");
        }
        if (this.mEffectivePolicyRaw.disableLaunchBoost) {
            sb.append("l");
        }
        if (this.mEffectivePolicyRaw.disableOptionalSensors) {
            sb.append("S");
        }
        if (this.mEffectivePolicyRaw.disableAod) {
            sb.append("o");
        }
        if (this.mEffectivePolicyRaw.enableQuickDoze) {
            sb.append("q");
        }
        sb.append(this.mEffectivePolicyRaw.locationMode);
        this.mEventLogKeys = sb.toString();
    }

    /* loaded from: classes2.dex */
    public static class Policy {
        public final float adjustBrightnessFactor;
        public final boolean advertiseIsEnabled;
        public final boolean deferFullBackup;
        public final boolean deferKeyValueBackup;
        public final boolean disableAnimation;
        public final boolean disableAod;
        public final boolean disableLaunchBoost;
        public final boolean disableOptionalSensors;
        public final boolean disableVibration;
        public final boolean enableAdjustBrightness;
        public final boolean enableDataSaver;
        public final boolean enableFirewall;
        public final boolean enableNightMode;
        public final boolean enableQuickDoze;
        public final boolean forceAllAppsStandby;
        public final boolean forceBackgroundCheck;
        public final int locationMode;
        public final int mHashCode;
        public final int soundTriggerMode;

        public Policy(float f, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, boolean z10, boolean z11, boolean z12, boolean z13, boolean z14, boolean z15, int i, int i2) {
            this.adjustBrightnessFactor = Math.min(1.0f, Math.max(0.0f, f));
            this.advertiseIsEnabled = z;
            this.deferFullBackup = z2;
            this.deferKeyValueBackup = z3;
            this.disableAnimation = z4;
            this.disableAod = z5;
            this.disableLaunchBoost = z6;
            this.disableOptionalSensors = z7;
            this.disableVibration = z8;
            this.enableAdjustBrightness = z9;
            this.enableDataSaver = z10;
            this.enableFirewall = z11;
            this.enableNightMode = z12;
            this.enableQuickDoze = z13;
            this.forceAllAppsStandby = z14;
            this.forceBackgroundCheck = z15;
            if (i < 0 || 4 < i) {
                Slog.e("BatterySaverPolicy", "Invalid location mode: " + i);
                this.locationMode = 0;
            } else {
                this.locationMode = i;
            }
            if (i2 < 0 || i2 > 2) {
                Slog.e("BatterySaverPolicy", "Invalid SoundTrigger mode: " + i2);
                this.soundTriggerMode = 0;
            } else {
                this.soundTriggerMode = i2;
            }
            this.mHashCode = Objects.hash(Float.valueOf(f), Boolean.valueOf(z), Boolean.valueOf(z2), Boolean.valueOf(z3), Boolean.valueOf(z4), Boolean.valueOf(z5), Boolean.valueOf(z6), Boolean.valueOf(z7), Boolean.valueOf(z8), Boolean.valueOf(z9), Boolean.valueOf(z10), Boolean.valueOf(z11), Boolean.valueOf(z12), Boolean.valueOf(z13), Boolean.valueOf(z14), Boolean.valueOf(z15), Integer.valueOf(i), Integer.valueOf(i2));
        }

        public static Policy fromConfig(BatterySaverPolicyConfig batterySaverPolicyConfig) {
            if (batterySaverPolicyConfig == null) {
                Slog.e("BatterySaverPolicy", "Null config passed down to BatterySaverPolicy");
                return BatterySaverPolicy.OFF_POLICY;
            }
            batterySaverPolicyConfig.getDeviceSpecificSettings();
            return new Policy(batterySaverPolicyConfig.getAdjustBrightnessFactor(), batterySaverPolicyConfig.getAdvertiseIsEnabled(), batterySaverPolicyConfig.getDeferFullBackup(), batterySaverPolicyConfig.getDeferKeyValueBackup(), batterySaverPolicyConfig.getDisableAnimation(), batterySaverPolicyConfig.getDisableAod(), batterySaverPolicyConfig.getDisableLaunchBoost(), batterySaverPolicyConfig.getDisableOptionalSensors(), batterySaverPolicyConfig.getDisableVibration(), batterySaverPolicyConfig.getEnableAdjustBrightness(), batterySaverPolicyConfig.getEnableDataSaver(), batterySaverPolicyConfig.getEnableFirewall(), batterySaverPolicyConfig.getEnableNightMode(), batterySaverPolicyConfig.getEnableQuickDoze(), batterySaverPolicyConfig.getForceAllAppsStandby(), batterySaverPolicyConfig.getForceBackgroundCheck(), batterySaverPolicyConfig.getLocationMode(), batterySaverPolicyConfig.getSoundTriggerMode());
        }

        public BatterySaverPolicyConfig toConfig() {
            return new BatterySaverPolicyConfig.Builder().setAdjustBrightnessFactor(this.adjustBrightnessFactor).setAdvertiseIsEnabled(this.advertiseIsEnabled).setDeferFullBackup(this.deferFullBackup).setDeferKeyValueBackup(this.deferKeyValueBackup).setDisableAnimation(this.disableAnimation).setDisableAod(this.disableAod).setDisableLaunchBoost(this.disableLaunchBoost).setDisableOptionalSensors(this.disableOptionalSensors).setDisableVibration(this.disableVibration).setEnableAdjustBrightness(this.enableAdjustBrightness).setEnableDataSaver(this.enableDataSaver).setEnableFirewall(this.enableFirewall).setEnableNightMode(this.enableNightMode).setEnableQuickDoze(this.enableQuickDoze).setForceAllAppsStandby(this.forceAllAppsStandby).setForceBackgroundCheck(this.forceBackgroundCheck).setLocationMode(this.locationMode).setSoundTriggerMode(this.soundTriggerMode).build();
        }

        @VisibleForTesting
        public static Policy fromSettings(String str, String str2, DeviceConfig.Properties properties, String str3) {
            return fromSettings(str, str2, properties, str3, BatterySaverPolicy.OFF_POLICY);
        }

        public static Policy fromSettings(String str, String str2, DeviceConfig.Properties properties, String str3, Policy policy) {
            KeyValueListParser keyValueListParser = new KeyValueListParser(',');
            String emptyIfNull = TextUtils.emptyIfNull(str3);
            try {
                keyValueListParser.setString(str2 == null ? "" : str2);
            } catch (IllegalArgumentException unused) {
                Slog.wtf("BatterySaverPolicy", "Bad device specific battery saver constants: " + str2);
            }
            try {
                keyValueListParser.setString(str != null ? str : "");
            } catch (IllegalArgumentException unused2) {
                Slog.wtf("BatterySaverPolicy", "Bad battery saver constants: " + str);
            }
            float f = keyValueListParser.getFloat(BatterySaverPolicy.KEY_ADJUST_BRIGHTNESS_FACTOR, properties.getFloat(BatterySaverPolicy.KEY_ADJUST_BRIGHTNESS_FACTOR + emptyIfNull, policy.adjustBrightnessFactor));
            boolean z = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_ADVERTISE_IS_ENABLED, properties.getBoolean(BatterySaverPolicy.KEY_ADVERTISE_IS_ENABLED + emptyIfNull, policy.advertiseIsEnabled));
            boolean z2 = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_DEFER_FULL_BACKUP, properties.getBoolean(BatterySaverPolicy.KEY_DEFER_FULL_BACKUP + emptyIfNull, policy.deferFullBackup));
            boolean z3 = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_DEFER_KEYVALUE_BACKUP, properties.getBoolean(BatterySaverPolicy.KEY_DEFER_KEYVALUE_BACKUP + emptyIfNull, policy.deferKeyValueBackup));
            boolean z4 = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_DISABLE_ANIMATION, properties.getBoolean(BatterySaverPolicy.KEY_DISABLE_ANIMATION + emptyIfNull, policy.disableAnimation));
            boolean z5 = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_DISABLE_AOD, properties.getBoolean(BatterySaverPolicy.KEY_DISABLE_AOD + emptyIfNull, policy.disableAod));
            boolean z6 = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_DISABLE_LAUNCH_BOOST, properties.getBoolean(BatterySaverPolicy.KEY_DISABLE_LAUNCH_BOOST + emptyIfNull, policy.disableLaunchBoost));
            boolean z7 = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_DISABLE_OPTIONAL_SENSORS, properties.getBoolean(BatterySaverPolicy.KEY_DISABLE_OPTIONAL_SENSORS + emptyIfNull, policy.disableOptionalSensors));
            boolean z8 = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_DISABLE_VIBRATION, properties.getBoolean(BatterySaverPolicy.KEY_DISABLE_VIBRATION + emptyIfNull, policy.disableVibration));
            boolean z9 = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_ENABLE_BRIGHTNESS_ADJUSTMENT, properties.getBoolean(BatterySaverPolicy.KEY_ENABLE_BRIGHTNESS_ADJUSTMENT + emptyIfNull, policy.enableAdjustBrightness));
            boolean z10 = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_ENABLE_DATASAVER, properties.getBoolean(BatterySaverPolicy.KEY_ENABLE_DATASAVER + emptyIfNull, policy.enableDataSaver));
            boolean z11 = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_ENABLE_FIREWALL, properties.getBoolean(BatterySaverPolicy.KEY_ENABLE_FIREWALL + emptyIfNull, policy.enableFirewall));
            boolean z12 = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_ENABLE_NIGHT_MODE, properties.getBoolean(BatterySaverPolicy.KEY_ENABLE_NIGHT_MODE + emptyIfNull, policy.enableNightMode));
            boolean z13 = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_ENABLE_QUICK_DOZE, properties.getBoolean(BatterySaverPolicy.KEY_ENABLE_QUICK_DOZE + emptyIfNull, policy.enableQuickDoze));
            boolean z14 = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_FORCE_ALL_APPS_STANDBY, properties.getBoolean(BatterySaverPolicy.KEY_FORCE_ALL_APPS_STANDBY + emptyIfNull, policy.forceAllAppsStandby));
            boolean z15 = keyValueListParser.getBoolean(BatterySaverPolicy.KEY_FORCE_BACKGROUND_CHECK, properties.getBoolean(BatterySaverPolicy.KEY_FORCE_BACKGROUND_CHECK + emptyIfNull, policy.forceBackgroundCheck));
            int i = keyValueListParser.getInt(BatterySaverPolicy.KEY_LOCATION_MODE, properties.getInt(BatterySaverPolicy.KEY_LOCATION_MODE + emptyIfNull, policy.locationMode));
            return new Policy(f, z, z2, z3, z4, z5, z6, z7, z8, z9, z10, z11, z12, z13, z14, z15, i, keyValueListParser.getInt(BatterySaverPolicy.KEY_SOUNDTRIGGER_MODE, properties.getInt(BatterySaverPolicy.KEY_SOUNDTRIGGER_MODE + emptyIfNull, policy.soundTriggerMode)));
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Policy) {
                Policy policy = (Policy) obj;
                return Float.compare(policy.adjustBrightnessFactor, this.adjustBrightnessFactor) == 0 && this.advertiseIsEnabled == policy.advertiseIsEnabled && this.deferFullBackup == policy.deferFullBackup && this.deferKeyValueBackup == policy.deferKeyValueBackup && this.disableAnimation == policy.disableAnimation && this.disableAod == policy.disableAod && this.disableLaunchBoost == policy.disableLaunchBoost && this.disableOptionalSensors == policy.disableOptionalSensors && this.disableVibration == policy.disableVibration && this.enableAdjustBrightness == policy.enableAdjustBrightness && this.enableDataSaver == policy.enableDataSaver && this.enableFirewall == policy.enableFirewall && this.enableNightMode == policy.enableNightMode && this.enableQuickDoze == policy.enableQuickDoze && this.forceAllAppsStandby == policy.forceAllAppsStandby && this.forceBackgroundCheck == policy.forceBackgroundCheck && this.locationMode == policy.locationMode && this.soundTriggerMode == policy.soundTriggerMode;
            }
            return false;
        }

        public int hashCode() {
            return this.mHashCode;
        }
    }

    public PowerSaveState getBatterySaverPolicy(int i) {
        synchronized (this.mLock) {
            Policy currentPolicyLocked = getCurrentPolicyLocked();
            PowerSaveState.Builder globalBatterySaverEnabled = new PowerSaveState.Builder().setGlobalBatterySaverEnabled(currentPolicyLocked.advertiseIsEnabled);
            boolean z = false;
            switch (i) {
                case 1:
                    if (currentPolicyLocked.advertiseIsEnabled || currentPolicyLocked.locationMode != 0) {
                        z = true;
                    }
                    return globalBatterySaverEnabled.setBatterySaverEnabled(z).setLocationMode(currentPolicyLocked.locationMode).build();
                case 2:
                    return globalBatterySaverEnabled.setBatterySaverEnabled(currentPolicyLocked.disableVibration).build();
                case 3:
                    return globalBatterySaverEnabled.setBatterySaverEnabled(currentPolicyLocked.disableAnimation).build();
                case 4:
                    return globalBatterySaverEnabled.setBatterySaverEnabled(currentPolicyLocked.deferFullBackup).build();
                case 5:
                    return globalBatterySaverEnabled.setBatterySaverEnabled(currentPolicyLocked.deferKeyValueBackup).build();
                case 6:
                    return globalBatterySaverEnabled.setBatterySaverEnabled(currentPolicyLocked.enableFirewall).build();
                case 7:
                    return globalBatterySaverEnabled.setBatterySaverEnabled(currentPolicyLocked.enableAdjustBrightness).setBrightnessFactor(currentPolicyLocked.adjustBrightnessFactor).build();
                case 8:
                    if (currentPolicyLocked.advertiseIsEnabled || currentPolicyLocked.soundTriggerMode != 0) {
                        z = true;
                    }
                    return globalBatterySaverEnabled.setBatterySaverEnabled(z).setSoundTriggerMode(currentPolicyLocked.soundTriggerMode).build();
                case 9:
                default:
                    return globalBatterySaverEnabled.setBatterySaverEnabled(currentPolicyLocked.advertiseIsEnabled).build();
                case 10:
                    return globalBatterySaverEnabled.setBatterySaverEnabled(currentPolicyLocked.enableDataSaver).build();
                case 11:
                    return globalBatterySaverEnabled.setBatterySaverEnabled(currentPolicyLocked.forceAllAppsStandby).build();
                case 12:
                    return globalBatterySaverEnabled.setBatterySaverEnabled(currentPolicyLocked.forceBackgroundCheck).build();
                case 13:
                    return globalBatterySaverEnabled.setBatterySaverEnabled(currentPolicyLocked.disableOptionalSensors).build();
                case 14:
                    return globalBatterySaverEnabled.setBatterySaverEnabled(currentPolicyLocked.disableAod).build();
                case 15:
                    return globalBatterySaverEnabled.setBatterySaverEnabled(currentPolicyLocked.enableQuickDoze).build();
                case 16:
                    return globalBatterySaverEnabled.setBatterySaverEnabled(currentPolicyLocked.enableNightMode).build();
            }
        }
    }

    public boolean setPolicyLevel(int i) {
        synchronized (this.mLock) {
            int i2 = this.mPolicyLevel;
            if (i2 == i) {
                return false;
            }
            if (i2 == 2) {
                this.mFullPolicy = this.mDefaultFullPolicy;
            }
            if (i == 0 || i == 1 || i == 2) {
                this.mPolicyLevel = i;
                updatePolicyDependenciesLocked();
                return true;
            }
            Slog.wtf("BatterySaverPolicy", "setPolicyLevel invalid level given: " + i);
            return false;
        }
    }

    public Policy getPolicyLocked(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return this.mFullPolicy;
                }
                throw new IllegalArgumentException("getPolicyLocked: incorrect policy level provided - " + i);
            }
            return this.mAdaptivePolicy;
        }
        return OFF_POLICY;
    }

    public final boolean maybeUpdateDefaultFullPolicy(Policy policy) {
        boolean z = false;
        if (!this.mDefaultFullPolicy.equals(policy)) {
            if (!(!this.mDefaultFullPolicy.equals(this.mFullPolicy))) {
                this.mFullPolicy = policy;
                if (this.mPolicyLevel == 2) {
                    z = true;
                }
            }
            this.mDefaultFullPolicy = policy;
        }
        return z;
    }

    public boolean setFullPolicyLocked(Policy policy) {
        if (policy == null) {
            Slog.wtf("BatterySaverPolicy", "setFullPolicy given null policy");
            return false;
        } else if (this.mFullPolicy.equals(policy)) {
            return false;
        } else {
            this.mFullPolicy = policy;
            if (this.mPolicyLevel == 2) {
                updatePolicyDependenciesLocked();
                return true;
            }
            return false;
        }
    }

    public boolean setAdaptivePolicyLocked(Policy policy) {
        if (policy == null) {
            Slog.wtf("BatterySaverPolicy", "setAdaptivePolicy given null policy");
            return false;
        } else if (this.mAdaptivePolicy.equals(policy)) {
            return false;
        } else {
            this.mAdaptivePolicy = policy;
            if (this.mPolicyLevel == 1) {
                updatePolicyDependenciesLocked();
                return true;
            }
            return false;
        }
    }

    public boolean resetAdaptivePolicyLocked() {
        return setAdaptivePolicyLocked(this.mDefaultAdaptivePolicy);
    }

    public final Policy getCurrentPolicyLocked() {
        return this.mEffectivePolicyRaw;
    }

    public final Policy getCurrentRawPolicyLocked() {
        int i = this.mPolicyLevel;
        if (i != 1) {
            if (i == 2) {
                return this.mFullPolicy;
            }
            return OFF_POLICY;
        }
        return this.mAdaptivePolicy;
    }

    public boolean isLaunchBoostDisabled() {
        boolean z;
        synchronized (this.mLock) {
            z = getCurrentPolicyLocked().disableLaunchBoost;
        }
        return z;
    }

    public boolean shouldAdvertiseIsEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = getCurrentPolicyLocked().advertiseIsEnabled;
        }
        return z;
    }

    public String toEventLogString() {
        String str;
        synchronized (this.mLock) {
            str = this.mEventLogKeys;
        }
        return str;
    }

    public void dump(PrintWriter printWriter) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        synchronized (this.mLock) {
            indentingPrintWriter.println();
            this.mBatterySavingStats.dump(indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.println("Battery saver policy (*NOTE* they only apply when battery saver is ON):");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("Settings: battery_saver_constants");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("value: " + this.mSettings);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("Settings: " + this.mDeviceSpecificSettingsSource);
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("value: " + this.mDeviceSpecificSettings);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("DeviceConfig: battery_saver");
            indentingPrintWriter.increaseIndent();
            Set<String> keyset = this.mLastDeviceConfigProperties.getKeyset();
            if (keyset.size() == 0) {
                indentingPrintWriter.println("N/A");
            } else {
                for (String str : keyset) {
                    indentingPrintWriter.print(str);
                    indentingPrintWriter.print(": ");
                    indentingPrintWriter.println(this.mLastDeviceConfigProperties.getString(str, (String) null));
                }
            }
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("mAccessibilityEnabled=" + this.mAccessibilityEnabled.get());
            indentingPrintWriter.println("mAutomotiveProjectionActive=" + this.mAutomotiveProjectionActive.get());
            indentingPrintWriter.println("mPolicyLevel=" + this.mPolicyLevel);
            dumpPolicyLocked(indentingPrintWriter, "default full", this.mDefaultFullPolicy);
            dumpPolicyLocked(indentingPrintWriter, "current full", this.mFullPolicy);
            dumpPolicyLocked(indentingPrintWriter, "default adaptive", this.mDefaultAdaptivePolicy);
            dumpPolicyLocked(indentingPrintWriter, "current adaptive", this.mAdaptivePolicy);
            dumpPolicyLocked(indentingPrintWriter, "effective", this.mEffectivePolicyRaw);
            indentingPrintWriter.decreaseIndent();
        }
    }

    public final void dumpPolicyLocked(IndentingPrintWriter indentingPrintWriter, String str, Policy policy) {
        indentingPrintWriter.println();
        indentingPrintWriter.println("Policy '" + str + "'");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("advertise_is_enabled=" + policy.advertiseIsEnabled);
        indentingPrintWriter.println("disable_vibration=" + policy.disableVibration);
        indentingPrintWriter.println("disable_animation=" + policy.disableAnimation);
        indentingPrintWriter.println("defer_full_backup=" + policy.deferFullBackup);
        indentingPrintWriter.println("defer_keyvalue_backup=" + policy.deferKeyValueBackup);
        indentingPrintWriter.println("enable_firewall=" + policy.enableFirewall);
        indentingPrintWriter.println("enable_datasaver=" + policy.enableDataSaver);
        indentingPrintWriter.println("disable_launch_boost=" + policy.disableLaunchBoost);
        indentingPrintWriter.println("enable_brightness_adjustment=" + policy.enableAdjustBrightness);
        indentingPrintWriter.println("adjust_brightness_factor=" + policy.adjustBrightnessFactor);
        indentingPrintWriter.println("location_mode=" + policy.locationMode);
        indentingPrintWriter.println("force_all_apps_standby=" + policy.forceAllAppsStandby);
        indentingPrintWriter.println("force_background_check=" + policy.forceBackgroundCheck);
        indentingPrintWriter.println("disable_optional_sensors=" + policy.disableOptionalSensors);
        indentingPrintWriter.println("disable_aod=" + policy.disableAod);
        indentingPrintWriter.println("soundtrigger_mode=" + policy.soundTriggerMode);
        indentingPrintWriter.println("enable_quick_doze=" + policy.enableQuickDoze);
        indentingPrintWriter.println("enable_night_mode=" + policy.enableNightMode);
        indentingPrintWriter.decreaseIndent();
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public class PolicyBoolean {
        public final String mDebugName;
        @GuardedBy({"mLock"})
        public boolean mValue;

        public PolicyBoolean(String str) {
            this.mDebugName = str;
        }

        public final void initialize(boolean z) {
            synchronized (BatterySaverPolicy.this.mLock) {
                this.mValue = z;
            }
        }

        public final boolean get() {
            boolean z;
            synchronized (BatterySaverPolicy.this.mLock) {
                z = this.mValue;
            }
            return z;
        }

        @VisibleForTesting
        public void update(boolean z) {
            synchronized (BatterySaverPolicy.this.mLock) {
                if (this.mValue != z) {
                    Slog.d("BatterySaverPolicy", this.mDebugName + " changed to " + z + ", updating policy.");
                    this.mValue = z;
                    BatterySaverPolicy.this.updatePolicyDependenciesLocked();
                    BatterySaverPolicy.this.maybeNotifyListenersOfPolicyChange();
                }
            }
        }
    }
}
