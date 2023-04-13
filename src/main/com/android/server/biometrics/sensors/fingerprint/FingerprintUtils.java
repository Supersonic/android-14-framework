package com.android.server.biometrics.sensors.fingerprint;

import android.content.Context;
import android.hardware.fingerprint.Fingerprint;
import android.text.TextUtils;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.server.biometrics.sensors.BiometricUtils;
import java.util.List;
/* loaded from: classes.dex */
public class FingerprintUtils implements BiometricUtils<Fingerprint> {
    public static final Object sInstanceLock = new Object();
    public static SparseArray<FingerprintUtils> sInstances;
    public final String mFileName;
    @GuardedBy({"this"})
    public final SparseArray<FingerprintUserState> mUserStates = new SparseArray<>();

    public static boolean isKnownAcquiredCode(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
                return true;
            case 8:
            default:
                return false;
        }
    }

    public static boolean isKnownErrorCode(int i) {
        switch (i) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return true;
            default:
                return false;
        }
    }

    public static FingerprintUtils getInstance(int i) {
        return getInstance(i, null);
    }

    public static FingerprintUtils getInstance(int i, String str) {
        FingerprintUtils fingerprintUtils;
        synchronized (sInstanceLock) {
            if (sInstances == null) {
                sInstances = new SparseArray<>();
            }
            if (sInstances.get(i) == null) {
                if (str == null) {
                    str = "settings_fingerprint_" + i + ".xml";
                }
                sInstances.put(i, new FingerprintUtils(str));
            }
            fingerprintUtils = sInstances.get(i);
        }
        return fingerprintUtils;
    }

    public static FingerprintUtils getLegacyInstance(int i) {
        return getInstance(i, "settings_fingerprint.xml");
    }

    public FingerprintUtils(String str) {
        this.mFileName = str;
    }

    @Override // com.android.server.biometrics.sensors.BiometricUtils
    public List<Fingerprint> getBiometricsForUser(Context context, int i) {
        return getStateForUser(context, i).getBiometrics();
    }

    @Override // com.android.server.biometrics.sensors.BiometricUtils
    public void addBiometricForUser(Context context, int i, Fingerprint fingerprint) {
        getStateForUser(context, i).addBiometric(fingerprint);
    }

    @Override // com.android.server.biometrics.sensors.BiometricUtils
    public void removeBiometricForUser(Context context, int i, int i2) {
        getStateForUser(context, i).removeBiometric(i2);
    }

    public void renameBiometricForUser(Context context, int i, int i2, CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        getStateForUser(context, i).renameBiometric(i2, charSequence);
    }

    public CharSequence getUniqueName(Context context, int i) {
        return getStateForUser(context, i).getUniqueName();
    }

    @Override // com.android.server.biometrics.sensors.BiometricUtils
    public void setInvalidationInProgress(Context context, int i, boolean z) {
        getStateForUser(context, i).setInvalidationInProgress(z);
    }

    public boolean isInvalidationInProgress(Context context, int i) {
        return getStateForUser(context, i).isInvalidationInProgress();
    }

    public final FingerprintUserState getStateForUser(Context context, int i) {
        FingerprintUserState fingerprintUserState;
        synchronized (this) {
            fingerprintUserState = this.mUserStates.get(i);
            if (fingerprintUserState == null) {
                fingerprintUserState = new FingerprintUserState(context, i, this.mFileName);
                this.mUserStates.put(i, fingerprintUserState);
            }
        }
        return fingerprintUserState;
    }
}
