package com.android.server.biometrics;

import android.app.admin.DevicePolicyManager;
import android.app.trust.ITrustManager;
import android.content.Context;
import android.hardware.SensorPrivacyManager;
import android.hardware.biometrics.PromptInfo;
import android.os.RemoteException;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.biometrics.BiometricService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class PreAuthInfo {
    public final boolean confirmationRequested;
    public final Context context;
    public final boolean credentialAvailable;
    public final boolean credentialRequested;
    public final List<BiometricSensor> eligibleSensors;
    public final boolean ignoreEnrollmentState;
    public final List<Pair<BiometricSensor, Integer>> ineligibleSensors;
    public final boolean mBiometricRequested;
    public final int mBiometricStrengthRequested;
    public final int userId;

    public PreAuthInfo(boolean z, int i, boolean z2, List<BiometricSensor> list, List<Pair<BiometricSensor, Integer>> list2, boolean z3, boolean z4, boolean z5, int i2, Context context) {
        this.mBiometricRequested = z;
        this.mBiometricStrengthRequested = i;
        this.credentialRequested = z2;
        this.eligibleSensors = list;
        this.ineligibleSensors = list2;
        this.credentialAvailable = z3;
        this.confirmationRequested = z4;
        this.ignoreEnrollmentState = z5;
        this.userId = i2;
        this.context = context;
    }

    public static PreAuthInfo create(ITrustManager iTrustManager, DevicePolicyManager devicePolicyManager, BiometricService.SettingObserver settingObserver, List<BiometricSensor> list, int i, PromptInfo promptInfo, String str, boolean z, Context context) throws RemoteException {
        boolean isConfirmationRequested = promptInfo.isConfirmationRequested();
        boolean isBiometricRequested = Utils.isBiometricRequested(promptInfo);
        int publicBiometricStrength = Utils.getPublicBiometricStrength(promptInfo);
        boolean isCredentialRequested = Utils.isCredentialRequested(promptInfo);
        boolean isDeviceSecure = iTrustManager.isDeviceSecure(i, context.getAssociatedDisplayId());
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        if (isBiometricRequested) {
            Iterator<BiometricSensor> it = list.iterator();
            while (it.hasNext()) {
                BiometricSensor next = it.next();
                Iterator<BiometricSensor> it2 = it;
                int statusForBiometricAuthenticator = getStatusForBiometricAuthenticator(devicePolicyManager, settingObserver, next, i, str, z, publicBiometricStrength, promptInfo.getAllowedSensorIds(), promptInfo.isIgnoreEnrollmentState(), context);
                Slog.d("BiometricService/PreAuthInfo", "Package: " + str + " Sensor ID: " + next.f1133id + " Modality: " + next.modality + " Status: " + statusForBiometricAuthenticator);
                if (statusForBiometricAuthenticator == 1 || statusForBiometricAuthenticator == 12) {
                    arrayList.add(next);
                } else {
                    arrayList2.add(new Pair(next, Integer.valueOf(statusForBiometricAuthenticator)));
                }
                it = it2;
            }
        }
        return new PreAuthInfo(isBiometricRequested, publicBiometricStrength, isCredentialRequested, arrayList, arrayList2, isDeviceSecure, isConfirmationRequested, promptInfo.isIgnoreEnrollmentState(), i, context);
    }

    public static int getStatusForBiometricAuthenticator(DevicePolicyManager devicePolicyManager, BiometricService.SettingObserver settingObserver, BiometricSensor biometricSensor, int i, String str, boolean z, int i2, List<Integer> list, boolean z2, Context context) {
        if (list.isEmpty() || list.contains(Integer.valueOf(biometricSensor.f1133id))) {
            boolean isAtLeastStrength = Utils.isAtLeastStrength(biometricSensor.oemStrength, i2);
            boolean isAtLeastStrength2 = Utils.isAtLeastStrength(biometricSensor.getCurrentStrength(), i2);
            if (!isAtLeastStrength || isAtLeastStrength2) {
                if (isAtLeastStrength) {
                    int i3 = 6;
                    if (biometricSensor.impl.isHardwareDetected(str)) {
                        if (biometricSensor.impl.hasEnrolledTemplates(i, str) || z2) {
                            SensorPrivacyManager sensorPrivacyManager = (SensorPrivacyManager) context.getSystemService(SensorPrivacyManager.class);
                            if (sensorPrivacyManager != null && biometricSensor.modality == 8 && sensorPrivacyManager.isSensorPrivacyEnabled(2, i)) {
                                return 12;
                            }
                            int lockoutModeForUser = biometricSensor.impl.getLockoutModeForUser(i);
                            i3 = 1;
                            if (lockoutModeForUser == 1) {
                                return 10;
                            }
                            if (lockoutModeForUser == 2) {
                                return 11;
                            }
                            if (isEnabledForApp(settingObserver, biometricSensor.modality, i)) {
                                if (z && isBiometricDisabledByDevicePolicy(devicePolicyManager, biometricSensor.modality, i)) {
                                    return 3;
                                }
                                return i3;
                            }
                            return 8;
                        }
                        return 7;
                    }
                    return 6;
                }
                return 4;
            }
            return 5;
        }
        return 2;
    }

    public static boolean isEnabledForApp(BiometricService.SettingObserver settingObserver, int i, int i2) {
        return settingObserver.getEnabledForApps(i2);
    }

    public static boolean isBiometricDisabledByDevicePolicy(DevicePolicyManager devicePolicyManager, int i, int i2) {
        int mapModalityToDevicePolicyType = mapModalityToDevicePolicyType(i);
        if (mapModalityToDevicePolicyType == 0) {
            throw new IllegalStateException("Modality unknown to devicePolicyManager: " + i);
        }
        boolean z = (devicePolicyManager.getKeyguardDisabledFeatures(null, i2) & mapModalityToDevicePolicyType) != 0;
        Slog.w("BiometricService/PreAuthInfo", "isBiometricDisabledByDevicePolicy(" + i + "," + i2 + ")=" + z);
        return z;
    }

    public static int mapModalityToDevicePolicyType(int i) {
        if (i != 2) {
            if (i != 4) {
                if (i != 8) {
                    Slog.e("BiometricService/PreAuthInfo", "Error modality=" + i);
                    return 0;
                }
                return 128;
            }
            return 256;
        }
        return 32;
    }

    public final Pair<BiometricSensor, Integer> calculateErrorByPriority() {
        for (Pair<BiometricSensor, Integer> pair : this.ineligibleSensors) {
            if (((Integer) pair.second).intValue() == 7) {
                return pair;
            }
        }
        return this.ineligibleSensors.get(0);
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x0073, code lost:
        if (r0 != false) goto L31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0098, code lost:
        if (r0 != false) goto L31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x009a, code lost:
        r1 = 12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Pair<Integer, Integer> getInternalStatus() {
        SensorPrivacyManager sensorPrivacyManager = (SensorPrivacyManager) this.context.getSystemService(SensorPrivacyManager.class);
        int i = 2;
        int i2 = 0;
        boolean isSensorPrivacyEnabled = sensorPrivacyManager != null ? sensorPrivacyManager.isSensorPrivacyEnabled(2, this.userId) : false;
        boolean z = this.mBiometricRequested;
        if (z && this.credentialRequested) {
            if (this.credentialAvailable || !this.eligibleSensors.isEmpty()) {
                for (BiometricSensor biometricSensor : this.eligibleSensors) {
                    i2 |= biometricSensor.modality;
                }
                if (this.credentialAvailable) {
                    i2 |= 1;
                } else if (i2 == 8) {
                }
                i = 1;
            } else {
                if (!this.ineligibleSensors.isEmpty()) {
                    Pair<BiometricSensor, Integer> calculateErrorByPriority = calculateErrorByPriority();
                    i2 = 0 | ((BiometricSensor) calculateErrorByPriority.first).modality;
                    i = ((Integer) calculateErrorByPriority.second).intValue();
                }
                i = 9;
                i2 = 1;
            }
        } else if (z) {
            if (!this.eligibleSensors.isEmpty()) {
                for (BiometricSensor biometricSensor2 : this.eligibleSensors) {
                    i2 |= biometricSensor2.modality;
                }
                if (i2 == 8) {
                }
                i = 1;
            } else if (!this.ineligibleSensors.isEmpty()) {
                Pair<BiometricSensor, Integer> calculateErrorByPriority2 = calculateErrorByPriority();
                i2 = 0 | ((BiometricSensor) calculateErrorByPriority2.first).modality;
                i = ((Integer) calculateErrorByPriority2.second).intValue();
            }
        } else if (this.credentialRequested) {
            if (this.credentialAvailable) {
                i = 1;
                i2 = 1;
            }
            i = 9;
            i2 = 1;
        } else {
            Slog.e("BiometricService/PreAuthInfo", "No authenticators requested");
        }
        Slog.d("BiometricService/PreAuthInfo", "getCanAuthenticateInternal Modality: " + i2 + " AuthenticatorStatus: " + i);
        return new Pair<>(Integer.valueOf(i2), Integer.valueOf(i));
    }

    public int getCanAuthenticateResult() {
        return Utils.biometricConstantsToBiometricManager(Utils.authenticatorStatusToBiometricConstant(((Integer) getInternalStatus().second).intValue()));
    }

    public Pair<Integer, Integer> getPreAuthenticateStatus() {
        Pair<Integer, Integer> internalStatus = getInternalStatus();
        int authenticatorStatusToBiometricConstant = Utils.authenticatorStatusToBiometricConstant(((Integer) internalStatus.second).intValue());
        int intValue = ((Integer) internalStatus.first).intValue();
        switch (((Integer) internalStatus.second).intValue()) {
            case 1:
            case 2:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
            case 12:
                break;
            case 3:
            case 4:
            case 8:
            default:
                intValue = 0;
                break;
        }
        return new Pair<>(Integer.valueOf(intValue), Integer.valueOf(authenticatorStatusToBiometricConstant));
    }

    public boolean shouldShowCredential() {
        return this.credentialRequested && this.credentialAvailable;
    }

    public int getEligibleModalities() {
        int i = 0;
        for (BiometricSensor biometricSensor : this.eligibleSensors) {
            i |= biometricSensor.modality;
        }
        return (this.credentialRequested && this.credentialAvailable) ? i | 1 : i;
    }

    public int numSensorsWaitingForCookie() {
        int i = 0;
        for (BiometricSensor biometricSensor : this.eligibleSensors) {
            if (biometricSensor.getSensorState() == 1) {
                Slog.d("BiometricService/PreAuthInfo", "Sensor ID: " + biometricSensor.f1133id + " Waiting for cookie: " + biometricSensor.getCookie());
                i++;
            }
        }
        return i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("BiometricRequested: " + this.mBiometricRequested + ", StrengthRequested: " + this.mBiometricStrengthRequested + ", CredentialRequested: " + this.credentialRequested);
        sb.append(", Eligible:{");
        for (BiometricSensor biometricSensor : this.eligibleSensors) {
            sb.append(biometricSensor.f1133id);
            sb.append(" ");
        }
        sb.append("}");
        sb.append(", Ineligible:{");
        for (Pair<BiometricSensor, Integer> pair : this.ineligibleSensors) {
            sb.append(pair.first);
            sb.append(XmlUtils.STRING_ARRAY_SEPARATOR);
            sb.append(pair.second);
            sb.append(" ");
        }
        sb.append("}");
        sb.append(", CredentialAvailable: ");
        sb.append(this.credentialAvailable);
        sb.append(", ");
        return sb.toString();
    }
}
