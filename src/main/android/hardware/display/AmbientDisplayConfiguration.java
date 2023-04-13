package android.hardware.display;

import android.content.Context;
import android.media.AudioSystem;
import android.p008os.Build;
import android.p008os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.SparseArray;
import com.android.internal.C4057R;
import com.android.internal.util.ArrayUtils;
import java.util.Map;
/* loaded from: classes.dex */
public class AmbientDisplayConfiguration {
    private static final String[] DOZE_SETTINGS = {Settings.Secure.DOZE_ENABLED, Settings.Secure.DOZE_ALWAYS_ON, Settings.Secure.DOZE_PICK_UP_GESTURE, Settings.Secure.DOZE_PULSE_ON_LONG_PRESS, Settings.Secure.DOZE_DOUBLE_TAP_GESTURE, Settings.Secure.DOZE_WAKE_LOCK_SCREEN_GESTURE, Settings.Secure.DOZE_WAKE_DISPLAY_GESTURE, Settings.Secure.DOZE_TAP_SCREEN_GESTURE};
    private static final String[] NON_USER_CONFIGURABLE_DOZE_SETTINGS = {Settings.Secure.DOZE_QUICK_PICKUP_GESTURE};
    private static final String TAG = "AmbientDisplayConfig";
    private final boolean mAlwaysOnByDefault;
    private final Context mContext;
    private final boolean mPickupGestureEnabledByDefault;
    final SparseArray<Map<String, String>> mUsersInitialValues = new SparseArray<>();

    public AmbientDisplayConfiguration(Context context) {
        this.mContext = context;
        this.mAlwaysOnByDefault = context.getResources().getBoolean(C4057R.bool.config_dozeAlwaysOnEnabled);
        this.mPickupGestureEnabledByDefault = context.getResources().getBoolean(C4057R.bool.config_dozePickupGestureEnabled);
    }

    public boolean enabled(int user) {
        return pulseOnNotificationEnabled(user) || pulseOnLongPressEnabled(user) || alwaysOnEnabled(user) || wakeLockScreenGestureEnabled(user) || wakeDisplayGestureEnabled(user) || pickupGestureEnabled(user) || tapGestureEnabled(user) || doubleTapGestureEnabled(user) || quickPickupSensorEnabled(user) || screenOffUdfpsEnabled(user);
    }

    public boolean pulseOnNotificationEnabled(int user) {
        return boolSettingDefaultOn(Settings.Secure.DOZE_ENABLED, user) && pulseOnNotificationAvailable();
    }

    public boolean pulseOnNotificationAvailable() {
        return this.mContext.getResources().getBoolean(C4057R.bool.config_pulseOnNotificationsAvailable) && ambientDisplayAvailable();
    }

    public boolean pickupGestureEnabled(int user) {
        return boolSetting(Settings.Secure.DOZE_PICK_UP_GESTURE, user, this.mPickupGestureEnabledByDefault ? 1 : 0) && dozePickupSensorAvailable();
    }

    public boolean dozePickupSensorAvailable() {
        return this.mContext.getResources().getBoolean(C4057R.bool.config_dozePulsePickup);
    }

    public boolean tapGestureEnabled(int user) {
        return boolSettingDefaultOn(Settings.Secure.DOZE_TAP_SCREEN_GESTURE, user) && tapSensorAvailable();
    }

    public boolean tapSensorAvailable() {
        String[] tapSensorTypeMapping;
        for (String tapType : tapSensorTypeMapping()) {
            if (!TextUtils.isEmpty(tapType)) {
                return true;
            }
        }
        return false;
    }

    public boolean doubleTapGestureEnabled(int user) {
        return boolSettingDefaultOn(Settings.Secure.DOZE_DOUBLE_TAP_GESTURE, user) && doubleTapSensorAvailable();
    }

    public boolean doubleTapSensorAvailable() {
        return !TextUtils.isEmpty(doubleTapSensorType());
    }

    public boolean quickPickupSensorEnabled(int user) {
        return boolSettingDefaultOn(Settings.Secure.DOZE_QUICK_PICKUP_GESTURE, user) && !TextUtils.isEmpty(quickPickupSensorType()) && pickupGestureEnabled(user) && !alwaysOnEnabled(user);
    }

    public boolean screenOffUdfpsEnabled(int user) {
        return !TextUtils.isEmpty(udfpsLongPressSensorType()) && boolSettingDefaultOff("screen_off_udfps_enabled", user);
    }

    public boolean wakeScreenGestureAvailable() {
        return this.mContext.getResources().getBoolean(C4057R.bool.config_dozeWakeLockScreenSensorAvailable);
    }

    public boolean wakeLockScreenGestureEnabled(int user) {
        return boolSettingDefaultOn(Settings.Secure.DOZE_WAKE_LOCK_SCREEN_GESTURE, user) && wakeScreenGestureAvailable();
    }

    public boolean wakeDisplayGestureEnabled(int user) {
        return boolSettingDefaultOn(Settings.Secure.DOZE_WAKE_DISPLAY_GESTURE, user) && wakeScreenGestureAvailable();
    }

    public long getWakeLockScreenDebounce() {
        return this.mContext.getResources().getInteger(C4057R.integer.config_dozeWakeLockScreenDebounce);
    }

    public String doubleTapSensorType() {
        return this.mContext.getResources().getString(C4057R.string.config_dozeDoubleTapSensorType);
    }

    public String[] tapSensorTypeMapping() {
        String[] postureMapping = this.mContext.getResources().getStringArray(C4057R.array.config_dozeTapSensorPostureMapping);
        if (ArrayUtils.isEmpty(postureMapping)) {
            return new String[]{this.mContext.getResources().getString(C4057R.string.config_dozeTapSensorType)};
        }
        return postureMapping;
    }

    public String longPressSensorType() {
        return this.mContext.getResources().getString(C4057R.string.config_dozeLongPressSensorType);
    }

    public String udfpsLongPressSensorType() {
        return this.mContext.getResources().getString(C4057R.string.config_dozeUdfpsLongPressSensorType);
    }

    public String quickPickupSensorType() {
        return this.mContext.getResources().getString(C4057R.string.config_quickPickupSensorType);
    }

    public boolean pulseOnLongPressEnabled(int user) {
        return pulseOnLongPressAvailable() && boolSettingDefaultOff(Settings.Secure.DOZE_PULSE_ON_LONG_PRESS, user);
    }

    private boolean pulseOnLongPressAvailable() {
        return !TextUtils.isEmpty(longPressSensorType());
    }

    public boolean alwaysOnEnabled(int user) {
        return boolSetting(Settings.Secure.DOZE_ALWAYS_ON, user, this.mAlwaysOnByDefault ? 1 : 0) && alwaysOnAvailable() && !accessibilityInversionEnabled(user);
    }

    public boolean alwaysOnAvailable() {
        return (alwaysOnDisplayDebuggingEnabled() || alwaysOnDisplayAvailable()) && ambientDisplayAvailable();
    }

    public boolean alwaysOnAvailableForUser(int user) {
        return alwaysOnAvailable() && !accessibilityInversionEnabled(user);
    }

    public String ambientDisplayComponent() {
        return this.mContext.getResources().getString(C4057R.string.config_dozeComponent);
    }

    public boolean accessibilityInversionEnabled(int user) {
        return boolSettingDefaultOff(Settings.Secure.ACCESSIBILITY_DISPLAY_INVERSION_ENABLED, user);
    }

    public boolean ambientDisplayAvailable() {
        return !TextUtils.isEmpty(ambientDisplayComponent());
    }

    public boolean dozeSuppressed(int user) {
        return boolSettingDefaultOff(Settings.Secure.SUPPRESS_DOZE, user);
    }

    private boolean alwaysOnDisplayAvailable() {
        return this.mContext.getResources().getBoolean(C4057R.bool.config_dozeAlwaysOnDisplayAvailable);
    }

    private boolean alwaysOnDisplayDebuggingEnabled() {
        return SystemProperties.getBoolean("debug.doze.aod", false) && Build.IS_DEBUGGABLE;
    }

    private boolean boolSettingDefaultOn(String name, int user) {
        return boolSetting(name, user, 1);
    }

    private boolean boolSettingDefaultOff(String name, int user) {
        return boolSetting(name, user, 0);
    }

    private boolean boolSetting(String name, int user, int def) {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), name, def, user) != 0;
    }

    public void disableDozeSettings(int userId) {
        disableDozeSettings(false, userId);
    }

    public void disableDozeSettings(boolean shouldDisableNonUserConfigurable, int userId) {
        String[] strArr;
        String[] strArr2;
        Map<String, String> initialValues = this.mUsersInitialValues.get(userId);
        if (initialValues != null && !initialValues.isEmpty()) {
            throw new IllegalStateException("Don't call #disableDozeSettings more than once,without first calling #restoreDozeSettings");
        }
        Map<String, String> initialValues2 = new ArrayMap<>();
        for (String name : DOZE_SETTINGS) {
            initialValues2.put(name, getDozeSetting(name, userId));
            putDozeSetting(name, AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, userId);
        }
        if (shouldDisableNonUserConfigurable) {
            for (String name2 : NON_USER_CONFIGURABLE_DOZE_SETTINGS) {
                initialValues2.put(name2, getDozeSetting(name2, userId));
                putDozeSetting(name2, AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, userId);
            }
        }
        this.mUsersInitialValues.put(userId, initialValues2);
    }

    public void restoreDozeSettings(int userId) {
        String[] strArr;
        Map<String, String> initialValues = this.mUsersInitialValues.get(userId);
        if (initialValues != null && !initialValues.isEmpty()) {
            for (String name : DOZE_SETTINGS) {
                putDozeSetting(name, initialValues.get(name), userId);
            }
            this.mUsersInitialValues.remove(userId);
        }
    }

    private String getDozeSetting(String name, int userId) {
        return Settings.Secure.getStringForUser(this.mContext.getContentResolver(), name, userId);
    }

    private void putDozeSetting(String name, String value, int userId) {
        Settings.Secure.putStringForUser(this.mContext.getContentResolver(), name, value, userId);
    }
}
