package com.android.server.biometrics;

import android.annotation.EnforcePermission;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.IAuthService;
import android.hardware.biometrics.IBiometricEnabledOnKeyguardCallback;
import android.hardware.biometrics.IBiometricService;
import android.hardware.biometrics.IBiometricServiceReceiver;
import android.hardware.biometrics.IInvalidationCallback;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.biometrics.PromptInfo;
import android.hardware.biometrics.SensorLocationInternal;
import android.hardware.biometrics.SensorPropertiesInternal;
import android.hardware.face.FaceSensorPropertiesInternal;
import android.hardware.face.IFaceService;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.hardware.fingerprint.IFingerprintService;
import android.hardware.iris.IIrisService;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.SystemService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class AuthService extends SystemService {
    public IBiometricService mBiometricService;
    @VisibleForTesting
    final IAuthService.Stub mImpl;
    public final Injector mInjector;

    public static int getCredentialBackupModality(int i) {
        return i == 1 ? i : i & (-2);
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        @VisibleForTesting
        public IBiometricService getBiometricService() {
            return IBiometricService.Stub.asInterface(ServiceManager.getService("biometric"));
        }

        @VisibleForTesting
        public void publishBinderService(AuthService authService, IAuthService.Stub stub) {
            authService.publishBinderService("auth", stub);
        }

        @VisibleForTesting
        public String[] getConfiguration(Context context) {
            return context.getResources().getStringArray(17236004);
        }

        @VisibleForTesting
        public IFingerprintService getFingerprintService() {
            return IFingerprintService.Stub.asInterface(ServiceManager.getService("fingerprint"));
        }

        @VisibleForTesting
        public IFaceService getFaceService() {
            return IFaceService.Stub.asInterface(ServiceManager.getService("face"));
        }

        @VisibleForTesting
        public IIrisService getIrisService() {
            return IIrisService.Stub.asInterface(ServiceManager.getService("iris"));
        }

        @VisibleForTesting
        public AppOpsManager getAppOps(Context context) {
            return (AppOpsManager) context.getSystemService(AppOpsManager.class);
        }

        @VisibleForTesting
        public boolean isHidlDisabled(Context context) {
            return (Build.IS_ENG || Build.IS_USERDEBUG) && Settings.Secure.getIntForUser(context.getContentResolver(), "com.android.server.biometrics.AuthService.hidlDisabled", 0, -2) == 1;
        }
    }

    /* loaded from: classes.dex */
    public final class AuthServiceImpl extends IAuthService.Stub {
        public AuthServiceImpl() {
        }

        @EnforcePermission("android.permission.TEST_BIOMETRIC")
        public ITestSession createTestSession(int i, ITestSessionCallback iTestSessionCallback, String str) throws RemoteException {
            super.createTestSession_enforcePermission();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return AuthService.this.mInjector.getBiometricService().createTestSession(i, iTestSessionCallback, str);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @EnforcePermission("android.permission.TEST_BIOMETRIC")
        public List<SensorPropertiesInternal> getSensorProperties(String str) throws RemoteException {
            super.getSensorProperties_enforcePermission();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return AuthService.this.mInjector.getBiometricService().getSensorProperties(str);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @EnforcePermission("android.permission.TEST_BIOMETRIC")
        public String getUiPackage() {
            super.getUiPackage_enforcePermission();
            return AuthService.this.getContext().getResources().getString(17039842);
        }

        public long authenticate(IBinder iBinder, long j, int i, IBiometricServiceReceiver iBiometricServiceReceiver, String str, PromptInfo promptInfo) throws RemoteException {
            int callingUserId = UserHandle.getCallingUserId();
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            if (i == callingUserId) {
                AuthService.this.checkPermission();
            } else {
                Slog.w("AuthService", "User " + callingUserId + " is requesting authentication of userid: " + i);
                AuthService.this.checkInternalPermission();
            }
            if (!AuthService.this.checkAppOps(callingUid, str, "authenticate()")) {
                authenticateFastFail("Denied by app ops: " + str, iBiometricServiceReceiver);
                return -1L;
            } else if (iBinder == null || iBiometricServiceReceiver == null || str == null || promptInfo == null) {
                authenticateFastFail("Unable to authenticate, one or more null arguments", iBiometricServiceReceiver);
                return -1L;
            } else if (!Utils.isForeground(callingUid, callingPid)) {
                authenticateFastFail("Caller is not foreground: " + str, iBiometricServiceReceiver);
                return -1L;
            } else {
                if (promptInfo.containsTestConfigurations() && AuthService.this.getContext().checkCallingOrSelfPermission("android.permission.TEST_BIOMETRIC") != 0) {
                    AuthService.this.checkInternalPermission();
                }
                if (promptInfo.containsPrivateApiConfigurations()) {
                    AuthService.this.checkInternalPermission();
                }
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return AuthService.this.mBiometricService.authenticate(iBinder, j, i, iBiometricServiceReceiver, str, promptInfo);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public final void authenticateFastFail(String str, IBiometricServiceReceiver iBiometricServiceReceiver) {
            Slog.e("AuthService", "authenticateFastFail: " + str);
            try {
                iBiometricServiceReceiver.onError(0, 5, 0);
            } catch (RemoteException e) {
                Slog.e("AuthService", "authenticateFastFail failed to notify caller", e);
            }
        }

        public void cancelAuthentication(IBinder iBinder, String str, long j) throws RemoteException {
            AuthService.this.checkPermission();
            if (iBinder == null || str == null) {
                Slog.e("AuthService", "Unable to cancel authentication, one or more null arguments");
                return;
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                AuthService.this.mBiometricService.cancelAuthentication(iBinder, str, j);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public int canAuthenticate(String str, int i, @BiometricManager.Authenticators.Types int i2) throws RemoteException {
            int callingUserId = UserHandle.getCallingUserId();
            if (i != callingUserId) {
                AuthService.this.checkInternalPermission();
            } else {
                AuthService.this.checkPermission();
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                int canAuthenticate = AuthService.this.mBiometricService.canAuthenticate(str, i, callingUserId, i2);
                Slog.d("AuthService", "canAuthenticate, userId: " + i + ", callingUserId: " + callingUserId + ", authenticators: " + i2 + ", result: " + canAuthenticate);
                return canAuthenticate;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean hasEnrolledBiometrics(int i, String str) throws RemoteException {
            AuthService.this.checkInternalPermission();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return AuthService.this.mBiometricService.hasEnrolledBiometrics(i, str);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void registerEnabledOnKeyguardCallback(IBiometricEnabledOnKeyguardCallback iBiometricEnabledOnKeyguardCallback) throws RemoteException {
            AuthService.this.checkInternalPermission();
            int callingUserId = UserHandle.getCallingUserId();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                AuthService.this.mBiometricService.registerEnabledOnKeyguardCallback(iBiometricEnabledOnKeyguardCallback, callingUserId);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void invalidateAuthenticatorIds(int i, int i2, IInvalidationCallback iInvalidationCallback) throws RemoteException {
            AuthService.this.checkInternalPermission();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                AuthService.this.mBiometricService.invalidateAuthenticatorIds(i, i2, iInvalidationCallback);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public long[] getAuthenticatorIds(int i) throws RemoteException {
            if (i != UserHandle.getCallingUserId()) {
                AuthService.this.getContext().enforceCallingOrSelfPermission("android.permission.USE_BIOMETRIC_INTERNAL", "Must have android.permission.USE_BIOMETRIC_INTERNAL permission.");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return AuthService.this.mBiometricService.getAuthenticatorIds(i);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void resetLockoutTimeBound(IBinder iBinder, String str, int i, int i2, byte[] bArr) throws RemoteException {
            AuthService.this.checkInternalPermission();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                AuthService.this.mBiometricService.resetLockoutTimeBound(iBinder, str, i, i2, bArr);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void resetLockout(int i, byte[] bArr) throws RemoteException {
            AuthService.this.checkInternalPermission();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                AuthService.this.mBiometricService.resetLockout(i, bArr);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public CharSequence getButtonLabel(int i, String str, @BiometricManager.Authenticators.Types int i2) throws RemoteException {
            String str2;
            int callingUserId = UserHandle.getCallingUserId();
            if (i != callingUserId) {
                AuthService.this.checkInternalPermission();
            } else {
                AuthService.this.checkPermission();
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                int credentialBackupModality = AuthService.getCredentialBackupModality(AuthService.this.mBiometricService.getCurrentModality(str, i, callingUserId, i2));
                if (credentialBackupModality == 0) {
                    str2 = null;
                } else if (credentialBackupModality == 1) {
                    str2 = AuthService.this.getContext().getString(17041469);
                } else if (credentialBackupModality == 2) {
                    str2 = AuthService.this.getContext().getString(17040319);
                } else if (credentialBackupModality == 8) {
                    str2 = AuthService.this.getContext().getString(17040263);
                } else {
                    str2 = AuthService.this.getContext().getString(17039745);
                }
                return str2;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public CharSequence getPromptMessage(int i, String str, @BiometricManager.Authenticators.Types int i2) throws RemoteException {
            String str2;
            int callingUserId = UserHandle.getCallingUserId();
            if (i != callingUserId) {
                AuthService.this.checkInternalPermission();
            } else {
                AuthService.this.checkPermission();
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                int currentModality = AuthService.this.mBiometricService.getCurrentModality(str, i, callingUserId, i2);
                boolean isCredentialRequested = Utils.isCredentialRequested(i2);
                int credentialBackupModality = AuthService.getCredentialBackupModality(currentModality);
                if (credentialBackupModality == 0) {
                    str2 = null;
                } else if (credentialBackupModality == 1) {
                    str2 = AuthService.this.getContext().getString(17041470);
                } else if (credentialBackupModality != 2) {
                    if (credentialBackupModality != 8) {
                        if (isCredentialRequested) {
                            str2 = AuthService.this.getContext().getString(17039755);
                        } else {
                            str2 = AuthService.this.getContext().getString(17039746);
                        }
                    } else if (isCredentialRequested) {
                        str2 = AuthService.this.getContext().getString(17040283);
                    } else {
                        str2 = AuthService.this.getContext().getString(17040266);
                    }
                } else if (isCredentialRequested) {
                    str2 = AuthService.this.getContext().getString(17040339);
                } else {
                    str2 = AuthService.this.getContext().getString(17040321);
                }
                return str2;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public CharSequence getSettingName(int i, String str, @BiometricManager.Authenticators.Types int i2) throws RemoteException {
            String str2;
            if (i != UserHandle.getCallingUserId()) {
                AuthService.this.checkInternalPermission();
            } else {
                AuthService.this.checkPermission();
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                int supportedModalities = AuthService.this.mBiometricService.getSupportedModalities(i2);
                if (supportedModalities == 0) {
                    str2 = null;
                } else if (supportedModalities == 1) {
                    str2 = AuthService.this.getContext().getString(17041469);
                } else if (supportedModalities == 2) {
                    str2 = AuthService.this.getContext().getString(17040319);
                } else if (supportedModalities == 4) {
                    str2 = AuthService.this.getContext().getString(17039745);
                } else if (supportedModalities == 8) {
                    str2 = AuthService.this.getContext().getString(17040263);
                } else if ((supportedModalities & 1) == 0) {
                    str2 = AuthService.this.getContext().getString(17039745);
                } else {
                    int i3 = supportedModalities & (-2);
                    if (i3 == 2) {
                        str2 = AuthService.this.getContext().getString(17040338);
                    } else if (i3 == 8) {
                        str2 = AuthService.this.getContext().getString(17040282);
                    } else {
                        str2 = AuthService.this.getContext().getString(17039754);
                    }
                }
                return str2;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public AuthService(Context context) {
        this(context, new Injector());
    }

    public AuthService(Context context, Injector injector) {
        super(context);
        this.mInjector = injector;
        this.mImpl = new AuthServiceImpl();
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        SensorConfig[] sensorConfigArr;
        this.mBiometricService = this.mInjector.getBiometricService();
        if (this.mInjector.isHidlDisabled(getContext())) {
            sensorConfigArr = null;
        } else {
            int i = SystemProperties.getInt("ro.board.api_level", SystemProperties.getInt("ro.board.first_api_level", 0));
            String[] configuration = this.mInjector.getConfiguration(getContext());
            if (configuration.length == 0 && i == 30) {
                Slog.w("AuthService", "Found R vendor partition without config_biometric_sensors");
                configuration = generateRSdkCompatibleConfiguration();
            }
            sensorConfigArr = new SensorConfig[configuration.length];
            for (int i2 = 0; i2 < configuration.length; i2++) {
                sensorConfigArr[i2] = new SensorConfig(configuration[i2]);
            }
        }
        registerAuthenticators(sensorConfigArr);
        this.mInjector.publishBinderService(this, this.mImpl);
    }

    public final String[] generateRSdkCompatibleConfiguration() {
        PackageManager packageManager = getContext().getPackageManager();
        ArrayList arrayList = new ArrayList();
        if (packageManager.hasSystemFeature("android.hardware.fingerprint")) {
            arrayList.add(String.valueOf(2));
        }
        if (packageManager.hasSystemFeature("android.hardware.biometrics.face")) {
            arrayList.add(String.valueOf(8));
        }
        String valueOf = String.valueOf(4095);
        String[] strArr = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            strArr[i] = String.join(XmlUtils.STRING_ARRAY_SEPARATOR, String.valueOf(i), (String) arrayList.get(i), valueOf);
        }
        Slog.d("AuthService", "Generated config_biometric_sensors: " + Arrays.toString(strArr));
        return strArr;
    }

    public final void registerAuthenticators(SensorConfig[] sensorConfigArr) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        if (sensorConfigArr != null) {
            for (SensorConfig sensorConfig : sensorConfigArr) {
                Slog.d("AuthService", "Registering HIDL ID: " + sensorConfig.f1134id + " Modality: " + sensorConfig.modality + " Strength: " + sensorConfig.strength);
                int i = sensorConfig.modality;
                if (i == 2) {
                    arrayList.add(getHidlFingerprintSensorProps(sensorConfig.f1134id, sensorConfig.strength));
                } else if (i == 4) {
                    arrayList3.add(getHidlIrisSensorProps(sensorConfig.f1134id, sensorConfig.strength));
                } else if (i == 8) {
                    arrayList2.add(getHidlFaceSensorProps(sensorConfig.f1134id, sensorConfig.strength));
                } else {
                    Slog.e("AuthService", "Unknown modality: " + sensorConfig.modality);
                }
            }
        }
        IFingerprintService fingerprintService = this.mInjector.getFingerprintService();
        if (fingerprintService != null) {
            try {
                fingerprintService.registerAuthenticators(arrayList);
            } catch (RemoteException e) {
                Slog.e("AuthService", "RemoteException when registering fingerprint authenticators", e);
            }
        } else if (arrayList.size() > 0) {
            Slog.e("AuthService", "HIDL fingerprint configuration exists, but FingerprintService is null.");
        }
        IFaceService faceService = this.mInjector.getFaceService();
        if (faceService != null) {
            try {
                faceService.registerAuthenticators(arrayList2);
            } catch (RemoteException e2) {
                Slog.e("AuthService", "RemoteException when registering face authenticators", e2);
            }
        } else if (arrayList2.size() > 0) {
            Slog.e("AuthService", "HIDL face configuration exists, but FaceService is null.");
        }
        IIrisService irisService = this.mInjector.getIrisService();
        if (irisService != null) {
            try {
                irisService.registerAuthenticators(arrayList3);
            } catch (RemoteException e3) {
                Slog.e("AuthService", "RemoteException when registering iris authenticators", e3);
            }
        } else if (arrayList3.size() > 0) {
            Slog.e("AuthService", "HIDL iris configuration exists, but IrisService is null.");
        }
    }

    public final void checkInternalPermission() {
        getContext().enforceCallingOrSelfPermission("android.permission.USE_BIOMETRIC_INTERNAL", "Must have USE_BIOMETRIC_INTERNAL permission");
    }

    public final void checkPermission() {
        if (getContext().checkCallingOrSelfPermission("android.permission.USE_FINGERPRINT") != 0) {
            getContext().enforceCallingOrSelfPermission("android.permission.USE_BIOMETRIC", "Must have USE_BIOMETRIC permission");
        }
    }

    public final boolean checkAppOps(int i, String str, String str2) {
        return this.mInjector.getAppOps(getContext()).noteOp(78, i, str, (String) null, str2) == 0;
    }

    public final FingerprintSensorPropertiesInternal getHidlFingerprintSensorProps(int i, @BiometricManager.Authenticators.Types int i2) {
        int[] intArray = getContext().getResources().getIntArray(17236158);
        boolean z = !ArrayUtils.isEmpty(intArray);
        int i3 = z ? 3 : getContext().getResources().getBoolean(17891712) ? 4 : 1;
        int integer = getContext().getResources().getInteger(17694845);
        ArrayList arrayList = new ArrayList();
        if (z && intArray.length == 3) {
            return new FingerprintSensorPropertiesInternal(i, Utils.authenticatorStrengthToPropertyStrength(i2), integer, arrayList, i3, true, false, List.of(new SensorLocationInternal("", intArray[0], intArray[1], intArray[2])));
        }
        return new FingerprintSensorPropertiesInternal(i, Utils.authenticatorStrengthToPropertyStrength(i2), integer, arrayList, i3, false);
    }

    public final FaceSensorPropertiesInternal getHidlFaceSensorProps(int i, @BiometricManager.Authenticators.Types int i2) {
        boolean z = getContext().getResources().getBoolean(17891682);
        return new FaceSensorPropertiesInternal(i, Utils.authenticatorStrengthToPropertyStrength(i2), getContext().getResources().getInteger(17694844), new ArrayList(), 0, false, z, true);
    }

    public final SensorPropertiesInternal getHidlIrisSensorProps(int i, @BiometricManager.Authenticators.Types int i2) {
        return new SensorPropertiesInternal(i, Utils.authenticatorStrengthToPropertyStrength(i2), 1, new ArrayList(), false, false);
    }
}
