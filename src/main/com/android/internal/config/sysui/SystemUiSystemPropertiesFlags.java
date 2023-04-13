package com.android.internal.config.sysui;

import android.p008os.Build;
import android.p008os.SystemProperties;
import android.util.Log;
/* loaded from: classes4.dex */
public class SystemUiSystemPropertiesFlags {
    private static final FlagResolver MAIN_RESOLVER;
    public static final Flag TEAMFOOD = devFlag("persist.sysui.teamfood");
    public static FlagResolver TEST_RESOLVER;

    /* loaded from: classes4.dex */
    public interface FlagResolver {
        boolean isEnabled(Flag flag);
    }

    /* loaded from: classes4.dex */
    public static final class NotificationFlags {
        public static final Flag FSI_FORCE_DEMOTE = SystemUiSystemPropertiesFlags.devFlag("persist.sysui.notification.fsi_force_demote");
        public static final Flag SHOW_STICKY_HUN_FOR_DENIED_FSI = SystemUiSystemPropertiesFlags.releasedFlag("persist.sysui.notification.show_sticky_hun_for_denied_fsi");
        public static final Flag ALLOW_DISMISS_ONGOING = SystemUiSystemPropertiesFlags.releasedFlag("persist.sysui.notification.ongoing_dismissal");
        public static final Flag OTP_REDACTION = SystemUiSystemPropertiesFlags.devFlag("persist.sysui.notification.otp_redaction");
    }

    static {
        MAIN_RESOLVER = Build.IS_DEBUGGABLE ? new DebugResolver() : new ProdResolver();
        TEST_RESOLVER = null;
    }

    public static FlagResolver getResolver() {
        if (Build.IS_DEBUGGABLE && TEST_RESOLVER != null) {
            Log.m108i("SystemUiSystemPropertiesFlags", "Returning debug resolver " + TEST_RESOLVER);
            return TEST_RESOLVER;
        }
        return MAIN_RESOLVER;
    }

    public static Flag devFlag(String name) {
        return new Flag(name, false, null);
    }

    public static Flag teamfoodFlag(String name) {
        return new Flag(name, false, TEAMFOOD);
    }

    public static Flag releasedFlag(String name) {
        return new Flag(name, true, null);
    }

    /* loaded from: classes4.dex */
    public static final class Flag {
        public final Flag mDebugDefault;
        public final boolean mDefaultValue;
        public final String mSysPropKey;

        public Flag(String sysPropKey, boolean defaultValue, Flag debugDefault) {
            this.mSysPropKey = sysPropKey;
            this.mDefaultValue = defaultValue;
            this.mDebugDefault = debugDefault;
        }
    }

    /* loaded from: classes4.dex */
    public static final class ProdResolver implements FlagResolver {
        @Override // com.android.internal.config.sysui.SystemUiSystemPropertiesFlags.FlagResolver
        public boolean isEnabled(Flag flag) {
            return flag.mDefaultValue;
        }
    }

    /* loaded from: classes4.dex */
    public static class DebugResolver implements FlagResolver {
        @Override // com.android.internal.config.sysui.SystemUiSystemPropertiesFlags.FlagResolver
        public final boolean isEnabled(Flag flag) {
            if (flag.mDebugDefault == null) {
                return getBoolean(flag.mSysPropKey, flag.mDefaultValue);
            }
            return getBoolean(flag.mSysPropKey, isEnabled(flag.mDebugDefault));
        }

        public boolean getBoolean(String key, boolean defaultValue) {
            return SystemProperties.getBoolean(key, defaultValue);
        }
    }
}
