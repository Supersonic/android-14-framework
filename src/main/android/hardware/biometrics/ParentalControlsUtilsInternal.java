package android.hardware.biometrics;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.p008os.Build;
import android.p008os.UserHandle;
import android.provider.Settings;
/* loaded from: classes.dex */
public class ParentalControlsUtilsInternal {
    private static final String TEST_ALWAYS_REQUIRE_CONSENT_CLASS = "android.hardware.biometrics.ParentalControlsUtilsInternal.require_consent_class";
    private static final String TEST_ALWAYS_REQUIRE_CONSENT_PACKAGE = "android.hardware.biometrics.ParentalControlsUtilsInternal.require_consent_package";

    public static ComponentName getTestComponentName(Context context, int userId) {
        if (Build.IS_USERDEBUG || Build.IS_ENG) {
            String pkg = Settings.Secure.getStringForUser(context.getContentResolver(), TEST_ALWAYS_REQUIRE_CONSENT_PACKAGE, userId);
            String cls = Settings.Secure.getStringForUser(context.getContentResolver(), TEST_ALWAYS_REQUIRE_CONSENT_CLASS, userId);
            if (pkg == null || cls == null) {
                return null;
            }
            return new ComponentName(pkg, cls);
        }
        return null;
    }

    public static boolean parentConsentRequired(Context context, DevicePolicyManager dpm, int modality, UserHandle userHandle) {
        if (getTestComponentName(context, userHandle.getIdentifier()) != null) {
            return true;
        }
        return parentConsentRequired(dpm, modality, userHandle);
    }

    public static boolean parentConsentRequired(DevicePolicyManager dpm, int modality, UserHandle userHandle) {
        ComponentName cn = getSupervisionComponentName(dpm, userHandle);
        if (cn == null) {
            return false;
        }
        int keyguardDisabledFeatures = dpm.getKeyguardDisabledFeatures(cn);
        boolean dpmFpDisabled = containsFlag(keyguardDisabledFeatures, 32);
        boolean dpmFaceDisabled = containsFlag(keyguardDisabledFeatures, 128);
        boolean dpmIrisDisabled = containsFlag(keyguardDisabledFeatures, 256);
        if (containsFlag(modality, 2) && dpmFpDisabled) {
            return true;
        }
        if (containsFlag(modality, 8) && dpmFaceDisabled) {
            return true;
        }
        if (containsFlag(modality, 4) && dpmIrisDisabled) {
            return true;
        }
        return false;
    }

    public static ComponentName getSupervisionComponentName(DevicePolicyManager dpm, UserHandle userHandle) {
        return dpm.getProfileOwnerOrDeviceOwnerSupervisionComponent(userHandle);
    }

    private static boolean containsFlag(int haystack, int needle) {
        return (haystack & needle) != 0;
    }
}
