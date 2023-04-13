package com.android.server.p014wm;

import android.provider.DeviceConfig;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import java.util.Map;
import java.util.concurrent.Executor;
/* renamed from: com.android.server.wm.LetterboxConfigurationDeviceConfig */
/* loaded from: classes2.dex */
public final class LetterboxConfigurationDeviceConfig implements DeviceConfig.OnPropertiesChangedListener {
    @VisibleForTesting
    static final Map<String, Boolean> sKeyToDefaultValueMap;
    public boolean mIsCameraCompatTreatmentEnabled = true;
    public boolean mIsDisplayRotationImmersiveAppCompatPolicyEnabled = true;
    public boolean mIsAllowIgnoreOrientationRequest = true;
    public boolean mIsCompatFakeFocusAllowed = true;
    public boolean mIsTranslucentLetterboxingAllowed = true;
    public final ArraySet<String> mActiveDeviceConfigsSet = new ArraySet<>();

    static {
        Boolean bool = Boolean.TRUE;
        sKeyToDefaultValueMap = Map.of("enable_compat_camera_treatment", bool, "enable_display_rotation_immersive_app_compat_policy", bool, "allow_ignore_orientation_request", bool, "enable_compat_fake_focus", bool, "enable_letterbox_translucent_activity", bool);
    }

    public LetterboxConfigurationDeviceConfig(Executor executor) {
        DeviceConfig.addOnPropertiesChangedListener("window_manager", executor, this);
    }

    public void onPropertiesChanged(DeviceConfig.Properties properties) {
        for (int size = this.mActiveDeviceConfigsSet.size() - 1; size >= 0; size--) {
            String valueAt = this.mActiveDeviceConfigsSet.valueAt(size);
            if (properties.getKeyset().contains(valueAt)) {
                readAndSaveValueFromDeviceConfig(valueAt);
            }
        }
    }

    public void updateFlagActiveStatus(boolean z, String str) {
        if (z) {
            this.mActiveDeviceConfigsSet.add(str);
            readAndSaveValueFromDeviceConfig(str);
        }
    }

    public boolean getFlag(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1925051249:
                if (str.equals("enable_compat_fake_focus")) {
                    c = 0;
                    break;
                }
                break;
            case -1785181243:
                if (str.equals("enable_display_rotation_immersive_app_compat_policy")) {
                    c = 1;
                    break;
                }
                break;
            case 111337355:
                if (str.equals("enable_letterbox_translucent_activity")) {
                    c = 2;
                    break;
                }
                break;
            case 735247337:
                if (str.equals("allow_ignore_orientation_request")) {
                    c = 3;
                    break;
                }
                break;
            case 1471627327:
                if (str.equals("enable_compat_camera_treatment")) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return this.mIsCompatFakeFocusAllowed;
            case 1:
                return this.mIsDisplayRotationImmersiveAppCompatPolicyEnabled;
            case 2:
                return this.mIsTranslucentLetterboxingAllowed;
            case 3:
                return this.mIsAllowIgnoreOrientationRequest;
            case 4:
                return this.mIsCameraCompatTreatmentEnabled;
            default:
                throw new AssertionError("Unexpected flag name: " + str);
        }
    }

    public final void readAndSaveValueFromDeviceConfig(String str) {
        Boolean bool = sKeyToDefaultValueMap.get(str);
        if (bool == null) {
            throw new AssertionError("Haven't found default value for flag: " + str);
        }
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1925051249:
                if (str.equals("enable_compat_fake_focus")) {
                    c = 0;
                    break;
                }
                break;
            case -1785181243:
                if (str.equals("enable_display_rotation_immersive_app_compat_policy")) {
                    c = 1;
                    break;
                }
                break;
            case 111337355:
                if (str.equals("enable_letterbox_translucent_activity")) {
                    c = 2;
                    break;
                }
                break;
            case 735247337:
                if (str.equals("allow_ignore_orientation_request")) {
                    c = 3;
                    break;
                }
                break;
            case 1471627327:
                if (str.equals("enable_compat_camera_treatment")) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                this.mIsCompatFakeFocusAllowed = getDeviceConfig(str, bool.booleanValue());
                return;
            case 1:
                this.mIsDisplayRotationImmersiveAppCompatPolicyEnabled = getDeviceConfig(str, bool.booleanValue());
                return;
            case 2:
                this.mIsTranslucentLetterboxingAllowed = getDeviceConfig(str, bool.booleanValue());
                return;
            case 3:
                this.mIsAllowIgnoreOrientationRequest = getDeviceConfig(str, bool.booleanValue());
                return;
            case 4:
                this.mIsCameraCompatTreatmentEnabled = getDeviceConfig(str, bool.booleanValue());
                return;
            default:
                throw new AssertionError("Unexpected flag name: " + str);
        }
    }

    public final boolean getDeviceConfig(String str, boolean z) {
        return DeviceConfig.getBoolean("window_manager", str, z);
    }
}
