package com.android.internal.widget;

import android.app.admin.PasswordMetrics;
import com.android.internal.widget.LockPatternUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes5.dex */
public abstract class LockSettingsInternal {
    public static final int ARM_REBOOT_ERROR_ESCROW_NOT_READY = 2;
    public static final int ARM_REBOOT_ERROR_KEYSTORE_FAILURE = 6;
    public static final int ARM_REBOOT_ERROR_NONE = 0;
    public static final int ARM_REBOOT_ERROR_NO_ESCROW_KEY = 5;
    public static final int ARM_REBOOT_ERROR_NO_PROVIDER = 3;
    public static final int ARM_REBOOT_ERROR_PROVIDER_MISMATCH = 4;
    public static final int ARM_REBOOT_ERROR_STORE_ESCROW_KEY = 7;
    public static final int ARM_REBOOT_ERROR_UNSPECIFIED = 1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes5.dex */
    public @interface ArmRebootEscrowErrorCode {
    }

    public abstract long addEscrowToken(byte[] bArr, int i, LockPatternUtils.EscrowTokenStateChangeCallback escrowTokenStateChangeCallback);

    public abstract int armRebootEscrow();

    public abstract boolean clearRebootEscrow();

    public abstract void createNewUser(int i, int i2);

    public abstract PasswordMetrics getUserPasswordMetrics(int i);

    public abstract boolean isEscrowTokenActive(long j, int i);

    public abstract void onThirdPartyAppsStarted();

    public abstract boolean prepareRebootEscrow();

    public abstract void refreshStrongAuthTimeout(int i);

    public abstract boolean removeEscrowToken(long j, int i);

    public abstract void removeUser(int i);

    public abstract boolean setLockCredentialWithToken(LockscreenCredential lockscreenCredential, long j, byte[] bArr, int i);

    public abstract void setRebootEscrowListener(RebootEscrowListener rebootEscrowListener);

    public abstract void unlockUserKeyIfUnsecured(int i);

    public abstract boolean unlockUserWithToken(long j, byte[] bArr, int i);
}
