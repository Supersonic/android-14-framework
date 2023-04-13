package com.android.server.biometrics.sensors.fingerprint;

import android.annotation.EnforcePermission;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.biometrics.IBiometricSensorReceiver;
import android.hardware.biometrics.IBiometricService;
import android.hardware.biometrics.IBiometricServiceLockoutResetCallback;
import android.hardware.biometrics.IBiometricStateListener;
import android.hardware.biometrics.IInvalidationCallback;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.biometrics.fingerprint.IFingerprint;
import android.hardware.biometrics.fingerprint.PointerContext;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintAuthenticateOptions;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.hardware.fingerprint.FingerprintServiceReceiver;
import android.hardware.fingerprint.IFingerprintAuthenticatorsRegisteredCallback;
import android.hardware.fingerprint.IFingerprintClientActiveCallback;
import android.hardware.fingerprint.IFingerprintService;
import android.hardware.fingerprint.IFingerprintServiceReceiver;
import android.hardware.fingerprint.ISidefpsController;
import android.hardware.fingerprint.IUdfpsOverlay;
import android.hardware.fingerprint.IUdfpsOverlayController;
import android.os.Binder;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.EventLog;
import android.util.Pair;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.SystemService;
import com.android.server.biometrics.Utils;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.sensors.BiometricStateCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.LockoutResetDispatcher;
import com.android.server.biometrics.sensors.fingerprint.FingerprintService;
import com.android.server.biometrics.sensors.fingerprint.aidl.FingerprintProvider;
import com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21;
import com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21UdfpsMock;
import com.google.android.collect.Lists;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintService extends SystemService {
    public final Supplier<String[]> mAidlInstanceNameSupplier;
    public final AppOpsManager mAppOps;
    public final BiometricContext mBiometricContext;
    public final BiometricStateCallback<ServiceProvider, FingerprintSensorPropertiesInternal> mBiometricStateCallback;
    public final Function<String, FingerprintProvider> mFingerprintProvider;
    public final GestureAvailabilityDispatcher mGestureAvailabilityDispatcher;
    public final Handler mHandler;
    public final LockPatternUtils mLockPatternUtils;
    public final LockoutResetDispatcher mLockoutResetDispatcher;
    public final FingerprintServiceRegistry mRegistry;
    @VisibleForTesting
    final IFingerprintService.Stub mServiceWrapper;

    /* renamed from: com.android.server.biometrics.sensors.fingerprint.FingerprintService$1 */
    /* loaded from: classes.dex */
    public class C06091 extends IFingerprintService.Stub {
        public C06091() {
        }

        @EnforcePermission("android.permission.TEST_BIOMETRIC")
        public ITestSession createTestSession(int i, ITestSessionCallback iTestSessionCallback, String str) {
            super.createTestSession_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "Null provider for createTestSession, sensorId: " + i);
                return null;
            }
            return providerForSensor.createTestSession(i, iTestSessionCallback, str);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public byte[] dumpSensorServiceStateProto(int i, boolean z) {
            super.dumpSensorServiceStateProto_enforcePermission();
            ProtoOutputStream protoOutputStream = new ProtoOutputStream();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor != null) {
                providerForSensor.dumpProtoState(i, protoOutputStream, z);
            }
            protoOutputStream.flush();
            return protoOutputStream.getBytes();
        }

        public List<FingerprintSensorPropertiesInternal> getSensorPropertiesInternal(String str) {
            if (FingerprintService.this.getContext().checkCallingOrSelfPermission("android.permission.USE_BIOMETRIC_INTERNAL") != 0) {
                Utils.checkPermission(FingerprintService.this.getContext(), "android.permission.TEST_BIOMETRIC");
            }
            return FingerprintService.this.mRegistry.getAllProperties();
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public FingerprintSensorPropertiesInternal getSensorProperties(int i, String str) {
            super.getSensorProperties_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "No matching sensor for getSensorProperties, sensorId: " + i + ", caller: " + str);
                return null;
            }
            return providerForSensor.getSensorProperties(i);
        }

        @EnforcePermission("android.permission.MANAGE_FINGERPRINT")
        public void generateChallenge(IBinder iBinder, int i, int i2, IFingerprintServiceReceiver iFingerprintServiceReceiver, String str) {
            super.generateChallenge_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "No matching sensor for generateChallenge, sensorId: " + i);
                return;
            }
            providerForSensor.scheduleGenerateChallenge(i, i2, iBinder, iFingerprintServiceReceiver, str);
        }

        @EnforcePermission("android.permission.MANAGE_FINGERPRINT")
        public void revokeChallenge(IBinder iBinder, int i, int i2, String str, long j) {
            super.revokeChallenge_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "No matching sensor for revokeChallenge, sensorId: " + i);
                return;
            }
            providerForSensor.scheduleRevokeChallenge(i, i2, iBinder, str, j);
        }

        @EnforcePermission("android.permission.MANAGE_FINGERPRINT")
        public long enroll(IBinder iBinder, byte[] bArr, int i, IFingerprintServiceReceiver iFingerprintServiceReceiver, String str, int i2) {
            super.enroll_enforcePermission();
            Pair<Integer, ServiceProvider> singleProvider = FingerprintService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FingerprintService", "Null provider for enroll");
                return -1L;
            }
            return ((ServiceProvider) singleProvider.second).scheduleEnroll(((Integer) singleProvider.first).intValue(), iBinder, bArr, i, iFingerprintServiceReceiver, str, i2);
        }

        @EnforcePermission("android.permission.MANAGE_FINGERPRINT")
        public void cancelEnrollment(IBinder iBinder, long j) {
            super.cancelEnrollment_enforcePermission();
            Pair<Integer, ServiceProvider> singleProvider = FingerprintService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FingerprintService", "Null provider for cancelEnrollment");
            } else {
                ((ServiceProvider) singleProvider.second).cancelEnrollment(((Integer) singleProvider.first).intValue(), iBinder, j);
            }
        }

        public long authenticate(IBinder iBinder, long j, IFingerprintServiceReceiver iFingerprintServiceReceiver, FingerprintAuthenticateOptions fingerprintAuthenticateOptions) {
            Pair<Integer, ServiceProvider> pair;
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            int callingUserId = UserHandle.getCallingUserId();
            String opPackageName = fingerprintAuthenticateOptions.getOpPackageName();
            String attributionTag = fingerprintAuthenticateOptions.getAttributionTag();
            int userId = fingerprintAuthenticateOptions.getUserId();
            if (!FingerprintService.this.canUseFingerprint(opPackageName, attributionTag, true, callingUid, callingPid, callingUserId)) {
                Slog.w("FingerprintService", "Authenticate rejecting package: " + opPackageName);
                return -1L;
            }
            boolean isKeyguard = Utils.isKeyguard(FingerprintService.this.getContext(), opPackageName);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            if (isKeyguard) {
                try {
                    if (Utils.isUserEncryptedOrLockdown(FingerprintService.this.mLockPatternUtils, userId)) {
                        EventLog.writeEvent(1397638484, "79776455");
                        Slog.e("FingerprintService", "Authenticate invoked when user is encrypted or lockdown");
                        return -1L;
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            boolean z = FingerprintService.this.getContext().checkCallingPermission("android.permission.MANAGE_FINGERPRINT") != 0;
            int i = isKeyguard ? 1 : 3;
            if (fingerprintAuthenticateOptions.getSensorId() == -1) {
                pair = FingerprintService.this.mRegistry.getSingleProvider();
            } else {
                Utils.checkPermission(FingerprintService.this.getContext(), "android.permission.USE_BIOMETRIC_INTERNAL");
                pair = new Pair<>(Integer.valueOf(fingerprintAuthenticateOptions.getSensorId()), FingerprintService.this.mRegistry.getProviderForSensor(fingerprintAuthenticateOptions.getSensorId()));
            }
            if (pair == null) {
                Slog.w("FingerprintService", "Null provider for authenticate");
                return -1L;
            }
            fingerprintAuthenticateOptions.setSensorId(((Integer) pair.first).intValue());
            FingerprintSensorPropertiesInternal sensorProperties = ((ServiceProvider) pair.second).getSensorProperties(fingerprintAuthenticateOptions.getSensorId());
            if (!isKeyguard && !Utils.isSettings(FingerprintService.this.getContext(), opPackageName) && sensorProperties != null && sensorProperties.isAnyUdfpsType()) {
                try {
                    return authenticateWithPrompt(j, sensorProperties, callingUid, callingUserId, iFingerprintServiceReceiver, opPackageName, fingerprintAuthenticateOptions.isIgnoreEnrollmentState());
                } catch (PackageManager.NameNotFoundException e) {
                    Slog.e("FingerprintService", "Invalid package", e);
                    return -1L;
                }
            }
            return ((ServiceProvider) pair.second).scheduleAuthenticate(iBinder, j, 0, new ClientMonitorCallbackConverter(iFingerprintServiceReceiver), fingerprintAuthenticateOptions, z, i, isKeyguard);
        }

        public final long authenticateWithPrompt(long j, final FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal, int i, final int i2, final IFingerprintServiceReceiver iFingerprintServiceReceiver, String str, boolean z) throws PackageManager.NameNotFoundException {
            Context uiContext = FingerprintService.this.getUiContext();
            Context createPackageContextAsUser = uiContext.createPackageContextAsUser(str, 0, UserHandle.getUserHandleForUid(i));
            Executor mainExecutor = uiContext.getMainExecutor();
            return new BiometricPrompt.Builder(createPackageContextAsUser).setTitle(uiContext.getString(17039747)).setSubtitle(uiContext.getString(17040321)).setNegativeButton(uiContext.getString(17039360), mainExecutor, new DialogInterface.OnClickListener() { // from class: com.android.server.biometrics.sensors.fingerprint.FingerprintService$1$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i3) {
                    FingerprintService.C06091.lambda$authenticateWithPrompt$0(iFingerprintServiceReceiver, dialogInterface, i3);
                }
            }).setIsForLegacyFingerprintManager(fingerprintSensorPropertiesInternal.sensorId).setIgnoreEnrollmentState(z).build().authenticateForOperation(new CancellationSignal(), mainExecutor, new BiometricPrompt.AuthenticationCallback() { // from class: com.android.server.biometrics.sensors.fingerprint.FingerprintService.1.1
                @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
                public void onAuthenticationError(int i3, CharSequence charSequence) {
                    try {
                        if (FingerprintUtils.isKnownErrorCode(i3)) {
                            iFingerprintServiceReceiver.onError(i3, 0);
                        } else {
                            iFingerprintServiceReceiver.onError(8, i3);
                        }
                    } catch (RemoteException e) {
                        Slog.e("FingerprintService", "Remote exception in onAuthenticationError()", e);
                    }
                }

                @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult authenticationResult) {
                    try {
                        iFingerprintServiceReceiver.onAuthenticationSucceeded(new Fingerprint("", 0, 0L), i2, fingerprintSensorPropertiesInternal.sensorStrength == 2);
                    } catch (RemoteException e) {
                        Slog.e("FingerprintService", "Remote exception in onAuthenticationSucceeded()", e);
                    }
                }

                @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
                public void onAuthenticationFailed() {
                    try {
                        iFingerprintServiceReceiver.onAuthenticationFailed();
                    } catch (RemoteException e) {
                        Slog.e("FingerprintService", "Remote exception in onAuthenticationFailed()", e);
                    }
                }

                public void onAuthenticationAcquired(int i3) {
                    try {
                        if (FingerprintUtils.isKnownAcquiredCode(i3)) {
                            iFingerprintServiceReceiver.onAcquired(i3, 0);
                        } else {
                            iFingerprintServiceReceiver.onAcquired(6, i3);
                        }
                    } catch (RemoteException e) {
                        Slog.e("FingerprintService", "Remote exception in onAuthenticationAcquired()", e);
                    }
                }
            }, j);
        }

        public static /* synthetic */ void lambda$authenticateWithPrompt$0(IFingerprintServiceReceiver iFingerprintServiceReceiver, DialogInterface dialogInterface, int i) {
            try {
                iFingerprintServiceReceiver.onError(10, 0);
            } catch (RemoteException e) {
                Slog.e("FingerprintService", "Remote exception in negative button onClick()", e);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public long detectFingerprint(IBinder iBinder, IFingerprintServiceReceiver iFingerprintServiceReceiver, FingerprintAuthenticateOptions fingerprintAuthenticateOptions) {
            super.detectFingerprint_enforcePermission();
            String opPackageName = fingerprintAuthenticateOptions.getOpPackageName();
            if (!Utils.isKeyguard(FingerprintService.this.getContext(), opPackageName)) {
                Slog.w("FingerprintService", "detectFingerprint called from non-sysui package: " + opPackageName);
                return -1L;
            }
            Pair<Integer, ServiceProvider> singleProvider = FingerprintService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FingerprintService", "Null provider for detectFingerprint");
                return -1L;
            }
            fingerprintAuthenticateOptions.setSensorId(((Integer) singleProvider.first).intValue());
            return ((ServiceProvider) singleProvider.second).scheduleFingerDetect(iBinder, new ClientMonitorCallbackConverter(iFingerprintServiceReceiver), fingerprintAuthenticateOptions, 1);
        }

        @EnforcePermission("android.permission.MANAGE_BIOMETRIC")
        public void prepareForAuthentication(IBinder iBinder, long j, IBiometricSensorReceiver iBiometricSensorReceiver, FingerprintAuthenticateOptions fingerprintAuthenticateOptions, long j2, int i, boolean z) {
            super.prepareForAuthentication_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(fingerprintAuthenticateOptions.getSensorId());
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "Null provider for prepareForAuthentication");
            } else {
                providerForSensor.scheduleAuthenticate(iBinder, j, i, new ClientMonitorCallbackConverter(iBiometricSensorReceiver), fingerprintAuthenticateOptions, j2, true, 2, z);
            }
        }

        @EnforcePermission("android.permission.MANAGE_BIOMETRIC")
        public void startPreparedClient(int i, int i2) {
            super.startPreparedClient_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "Null provider for startPreparedClient");
            } else {
                providerForSensor.startPreparedClient(i, i2);
            }
        }

        public void cancelAuthentication(IBinder iBinder, String str, String str2, long j) {
            if (!FingerprintService.this.canUseFingerprint(str, str2, true, Binder.getCallingUid(), Binder.getCallingPid(), UserHandle.getCallingUserId())) {
                Slog.w("FingerprintService", "cancelAuthentication rejecting package: " + str);
                return;
            }
            Pair<Integer, ServiceProvider> singleProvider = FingerprintService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FingerprintService", "Null provider for cancelAuthentication");
            } else {
                ((ServiceProvider) singleProvider.second).cancelAuthentication(((Integer) singleProvider.first).intValue(), iBinder, j);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void cancelFingerprintDetect(IBinder iBinder, String str, long j) {
            super.cancelFingerprintDetect_enforcePermission();
            if (!Utils.isKeyguard(FingerprintService.this.getContext(), str)) {
                Slog.w("FingerprintService", "cancelFingerprintDetect called from non-sysui package: " + str);
                return;
            }
            Pair<Integer, ServiceProvider> singleProvider = FingerprintService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FingerprintService", "Null provider for cancelFingerprintDetect");
            } else {
                ((ServiceProvider) singleProvider.second).cancelAuthentication(((Integer) singleProvider.first).intValue(), iBinder, j);
            }
        }

        @EnforcePermission("android.permission.MANAGE_BIOMETRIC")
        public void cancelAuthenticationFromService(int i, IBinder iBinder, String str, long j) {
            super.cancelAuthenticationFromService_enforcePermission();
            Slog.d("FingerprintService", "cancelAuthenticationFromService, sensorId: " + i);
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "Null provider for cancelAuthenticationFromService");
            } else {
                providerForSensor.cancelAuthentication(i, iBinder, j);
            }
        }

        @EnforcePermission("android.permission.MANAGE_FINGERPRINT")
        public void remove(IBinder iBinder, int i, int i2, IFingerprintServiceReceiver iFingerprintServiceReceiver, String str) {
            super.remove_enforcePermission();
            Pair<Integer, ServiceProvider> singleProvider = FingerprintService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FingerprintService", "Null provider for remove");
            } else {
                ((ServiceProvider) singleProvider.second).scheduleRemove(((Integer) singleProvider.first).intValue(), iBinder, iFingerprintServiceReceiver, i, i2, str);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void removeAll(IBinder iBinder, int i, final IFingerprintServiceReceiver iFingerprintServiceReceiver, String str) {
            super.removeAll_enforcePermission();
            IFingerprintServiceReceiver iFingerprintServiceReceiver2 = new FingerprintServiceReceiver() { // from class: com.android.server.biometrics.sensors.fingerprint.FingerprintService.1.2
                public final int numSensors;
                public int sensorsFinishedRemoving = 0;

                {
                    this.numSensors = C06091.this.getSensorPropertiesInternal(FingerprintService.this.getContext().getOpPackageName()).size();
                }

                public void onRemoved(Fingerprint fingerprint, int i2) throws RemoteException {
                    if (i2 == 0) {
                        this.sensorsFinishedRemoving++;
                        Slog.d("FingerprintService", "sensorsFinishedRemoving: " + this.sensorsFinishedRemoving + ", numSensors: " + this.numSensors);
                        if (this.sensorsFinishedRemoving == this.numSensors) {
                            iFingerprintServiceReceiver.onRemoved((Fingerprint) null, 0);
                        }
                    }
                }
            };
            for (ServiceProvider serviceProvider : FingerprintService.this.mRegistry.getProviders()) {
                for (FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal : serviceProvider.getSensorProperties()) {
                    serviceProvider.scheduleRemoveAll(fingerprintSensorPropertiesInternal.sensorId, iBinder, iFingerprintServiceReceiver2, i, str);
                }
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void addLockoutResetCallback(IBiometricServiceLockoutResetCallback iBiometricServiceLockoutResetCallback, String str) {
            super.addLockoutResetCallback_enforcePermission();
            FingerprintService.this.mLockoutResetDispatcher.addCallback(iBiometricServiceLockoutResetCallback, str);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) throws RemoteException {
            new FingerprintShellCommand(FingerprintService.this.getContext(), FingerprintService.this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(FingerprintService.this.getContext(), "FingerprintService", printWriter)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    if (strArr.length > 1 && "--proto".equals(strArr[0]) && "--state".equals(strArr[1])) {
                        ProtoOutputStream protoOutputStream = new ProtoOutputStream(fileDescriptor);
                        for (ServiceProvider serviceProvider : FingerprintService.this.mRegistry.getProviders()) {
                            for (FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal : serviceProvider.getSensorProperties()) {
                                serviceProvider.dumpProtoState(fingerprintSensorPropertiesInternal.sensorId, protoOutputStream, false);
                            }
                        }
                        protoOutputStream.flush();
                    } else if (strArr.length > 0 && "--proto".equals(strArr[0])) {
                        for (ServiceProvider serviceProvider2 : FingerprintService.this.mRegistry.getProviders()) {
                            for (FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal2 : serviceProvider2.getSensorProperties()) {
                                serviceProvider2.dumpProtoMetrics(fingerprintSensorPropertiesInternal2.sensorId, fileDescriptor);
                            }
                        }
                    } else {
                        for (ServiceProvider serviceProvider3 : FingerprintService.this.mRegistry.getProviders()) {
                            for (FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal3 : serviceProvider3.getSensorProperties()) {
                                printWriter.println("Dumping for sensorId: " + fingerprintSensorPropertiesInternal3.sensorId + ", provider: " + serviceProvider3.getClass().getSimpleName());
                                StringBuilder sb = new StringBuilder();
                                sb.append("Fps state: ");
                                sb.append(FingerprintService.this.mBiometricStateCallback.getBiometricState());
                                printWriter.println(sb.toString());
                                serviceProvider3.dumpInternal(fingerprintSensorPropertiesInternal3.sensorId, printWriter);
                                printWriter.println();
                            }
                        }
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public boolean isHardwareDetectedDeprecated(String str, String str2) {
            if (FingerprintService.this.canUseFingerprint(str, str2, false, Binder.getCallingUid(), Binder.getCallingPid(), UserHandle.getCallingUserId())) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    Pair<Integer, ServiceProvider> singleProvider = FingerprintService.this.mRegistry.getSingleProvider();
                    if (singleProvider == null) {
                        Slog.w("FingerprintService", "Null provider for isHardwareDetectedDeprecated, caller: " + str);
                        return false;
                    }
                    return ((ServiceProvider) singleProvider.second).isHardwareDetected(((Integer) singleProvider.first).intValue());
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return false;
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public boolean isHardwareDetected(int i, String str) {
            super.isHardwareDetected_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "Null provider for isHardwareDetected, caller: " + str);
                return false;
            }
            return providerForSensor.isHardwareDetected(i);
        }

        @EnforcePermission("android.permission.MANAGE_FINGERPRINT")
        public void rename(int i, int i2, String str) {
            super.rename_enforcePermission();
            if (Utils.isCurrentUserOrProfile(FingerprintService.this.getContext(), i2)) {
                Pair<Integer, ServiceProvider> singleProvider = FingerprintService.this.mRegistry.getSingleProvider();
                if (singleProvider == null) {
                    Slog.w("FingerprintService", "Null provider for rename");
                } else {
                    ((ServiceProvider) singleProvider.second).rename(((Integer) singleProvider.first).intValue(), i, i2, str);
                }
            }
        }

        public List<Fingerprint> getEnrolledFingerprints(int i, String str, String str2) {
            if (!FingerprintService.this.canUseFingerprint(str, str2, false, Binder.getCallingUid(), Binder.getCallingPid(), UserHandle.getCallingUserId())) {
                return Collections.emptyList();
            }
            if (i != UserHandle.getCallingUserId()) {
                Utils.checkPermission(FingerprintService.this.getContext(), "android.permission.INTERACT_ACROSS_USERS");
            }
            return FingerprintService.this.getEnrolledFingerprintsDeprecated(i, str);
        }

        public boolean hasEnrolledFingerprintsDeprecated(int i, String str, String str2) {
            if (FingerprintService.this.canUseFingerprint(str, str2, false, Binder.getCallingUid(), Binder.getCallingPid(), UserHandle.getCallingUserId())) {
                if (i != UserHandle.getCallingUserId()) {
                    Utils.checkPermission(FingerprintService.this.getContext(), "android.permission.INTERACT_ACROSS_USERS");
                }
                return !FingerprintService.this.getEnrolledFingerprintsDeprecated(i, str).isEmpty();
            }
            return false;
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public boolean hasEnrolledFingerprints(int i, int i2, String str) {
            super.hasEnrolledFingerprints_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor != null) {
                return providerForSensor.getEnrolledFingerprints(i, i2).size() > 0;
            }
            Slog.w("FingerprintService", "Null provider for hasEnrolledFingerprints, caller: " + str);
            return false;
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public int getLockoutModeForUser(int i, int i2) {
            super.getLockoutModeForUser_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "Null provider for getLockoutModeForUser");
                return 0;
            }
            return providerForSensor.getLockoutModeForUser(i, i2);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void invalidateAuthenticatorId(int i, int i2, IInvalidationCallback iInvalidationCallback) {
            super.invalidateAuthenticatorId_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "Null provider for invalidateAuthenticatorId");
            } else {
                providerForSensor.scheduleInvalidateAuthenticatorId(i, i2, iInvalidationCallback);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public long getAuthenticatorId(int i, int i2) {
            super.getAuthenticatorId_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "Null provider for getAuthenticatorId");
                return 0L;
            }
            return providerForSensor.getAuthenticatorId(i, i2);
        }

        @EnforcePermission("android.permission.RESET_FINGERPRINT_LOCKOUT")
        public void resetLockout(IBinder iBinder, int i, int i2, byte[] bArr, String str) {
            super.resetLockout_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "Null provider for resetLockout, caller: " + str);
                return;
            }
            providerForSensor.scheduleResetLockout(i, i2, bArr);
        }

        @EnforcePermission("android.permission.MANAGE_FINGERPRINT")
        public boolean isClientActive() {
            super.isClientActive_enforcePermission();
            return FingerprintService.this.mGestureAvailabilityDispatcher.isAnySensorActive();
        }

        @EnforcePermission("android.permission.MANAGE_FINGERPRINT")
        public void addClientActiveCallback(IFingerprintClientActiveCallback iFingerprintClientActiveCallback) {
            super.addClientActiveCallback_enforcePermission();
            FingerprintService.this.mGestureAvailabilityDispatcher.registerCallback(iFingerprintClientActiveCallback);
        }

        @EnforcePermission("android.permission.MANAGE_FINGERPRINT")
        public void removeClientActiveCallback(IFingerprintClientActiveCallback iFingerprintClientActiveCallback) {
            super.removeClientActiveCallback_enforcePermission();
            FingerprintService.this.mGestureAvailabilityDispatcher.removeCallback(iFingerprintClientActiveCallback);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void registerAuthenticators(final List<FingerprintSensorPropertiesInternal> list) {
            super.registerAuthenticators_enforcePermission();
            FingerprintService.this.mRegistry.registerAll(new Supplier() { // from class: com.android.server.biometrics.sensors.fingerprint.FingerprintService$1$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    List lambda$registerAuthenticators$1;
                    lambda$registerAuthenticators$1 = FingerprintService.C06091.this.lambda$registerAuthenticators$1(list);
                    return lambda$registerAuthenticators$1;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ List lambda$registerAuthenticators$1(List list) {
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(FingerprintService.this.getHidlProviders(list));
            ArrayList arrayList2 = new ArrayList();
            String[] strArr = (String[]) FingerprintService.this.mAidlInstanceNameSupplier.get();
            if (strArr != null) {
                arrayList2.addAll(Lists.newArrayList(strArr));
            }
            FingerprintService fingerprintService = FingerprintService.this;
            arrayList.addAll(fingerprintService.getAidlProviders(Utils.filterAvailableHalInstances(fingerprintService.getContext(), arrayList2)));
            return arrayList;
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void addAuthenticatorsRegisteredCallback(IFingerprintAuthenticatorsRegisteredCallback iFingerprintAuthenticatorsRegisteredCallback) {
            super.addAuthenticatorsRegisteredCallback_enforcePermission();
            FingerprintService.this.mRegistry.addAllRegisteredCallback(iFingerprintAuthenticatorsRegisteredCallback);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void registerBiometricStateListener(IBiometricStateListener iBiometricStateListener) {
            super.registerBiometricStateListener_enforcePermission();
            FingerprintService.this.mBiometricStateCallback.registerBiometricStateListener(iBiometricStateListener);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void onPointerDown(long j, int i, PointerContext pointerContext) {
            super.onPointerDown_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "No matching provider for onFingerDown, sensorId: " + i);
                return;
            }
            providerForSensor.onPointerDown(j, i, pointerContext);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void onPointerUp(long j, int i, PointerContext pointerContext) {
            super.onPointerUp_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "No matching provider for onFingerUp, sensorId: " + i);
                return;
            }
            providerForSensor.onPointerUp(j, i, pointerContext);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void onUiReady(long j, int i) {
            super.onUiReady_enforcePermission();
            ServiceProvider providerForSensor = FingerprintService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FingerprintService", "No matching provider for onUiReady, sensorId: " + i);
                return;
            }
            providerForSensor.onUiReady(j, i);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void setUdfpsOverlayController(IUdfpsOverlayController iUdfpsOverlayController) {
            super.setUdfpsOverlayController_enforcePermission();
            for (ServiceProvider serviceProvider : FingerprintService.this.mRegistry.getProviders()) {
                serviceProvider.setUdfpsOverlayController(iUdfpsOverlayController);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void setSidefpsController(ISidefpsController iSidefpsController) {
            super.setSidefpsController_enforcePermission();
            for (ServiceProvider serviceProvider : FingerprintService.this.mRegistry.getProviders()) {
                serviceProvider.setSidefpsController(iSidefpsController);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void setUdfpsOverlay(IUdfpsOverlay iUdfpsOverlay) {
            super.setUdfpsOverlay_enforcePermission();
            for (ServiceProvider serviceProvider : FingerprintService.this.mRegistry.getProviders()) {
                serviceProvider.setUdfpsOverlay(iUdfpsOverlay);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void onPowerPressed() {
            super.onPowerPressed_enforcePermission();
            for (ServiceProvider serviceProvider : FingerprintService.this.mRegistry.getProviders()) {
                serviceProvider.onPowerPressed();
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void scheduleWatchdog() {
            super.scheduleWatchdog_enforcePermission();
            Pair<Integer, ServiceProvider> singleProvider = FingerprintService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FingerprintService", "Null provider for scheduling watchdog");
            } else {
                ((ServiceProvider) singleProvider.second).scheduleWatchdog(((Integer) singleProvider.first).intValue());
            }
        }
    }

    public FingerprintService(Context context) {
        this(context, BiometricContext.getInstance(context), new Supplier() { // from class: com.android.server.biometrics.sensors.fingerprint.FingerprintService$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                IBiometricService lambda$new$0;
                lambda$new$0 = FingerprintService.lambda$new$0();
                return lambda$new$0;
            }
        }, new Supplier() { // from class: com.android.server.biometrics.sensors.fingerprint.FingerprintService$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                String[] lambda$new$1;
                lambda$new$1 = FingerprintService.lambda$new$1();
                return lambda$new$1;
            }
        }, null);
    }

    public static /* synthetic */ IBiometricService lambda$new$0() {
        return IBiometricService.Stub.asInterface(ServiceManager.getService("biometric"));
    }

    public static /* synthetic */ String[] lambda$new$1() {
        return ServiceManager.getDeclaredInstances(IFingerprint.DESCRIPTOR);
    }

    @VisibleForTesting
    public FingerprintService(Context context, BiometricContext biometricContext, Supplier<IBiometricService> supplier, Supplier<String[]> supplier2, Function<String, FingerprintProvider> function) {
        super(context);
        C06091 c06091 = new C06091();
        this.mServiceWrapper = c06091;
        this.mBiometricContext = biometricContext;
        this.mAidlInstanceNameSupplier = supplier2;
        this.mAppOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mGestureAvailabilityDispatcher = new GestureAvailabilityDispatcher();
        this.mLockoutResetDispatcher = new LockoutResetDispatcher(context);
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mBiometricStateCallback = new BiometricStateCallback<>(UserManager.get(context));
        this.mFingerprintProvider = function == null ? new Function() { // from class: com.android.server.biometrics.sensors.fingerprint.FingerprintService$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                FingerprintProvider lambda$new$2;
                lambda$new$2 = FingerprintService.this.lambda$new$2((String) obj);
                return lambda$new$2;
            }
        } : function;
        this.mHandler = new Handler(Looper.getMainLooper());
        FingerprintServiceRegistry fingerprintServiceRegistry = new FingerprintServiceRegistry(c06091, supplier);
        this.mRegistry = fingerprintServiceRegistry;
        fingerprintServiceRegistry.addAllRegisteredCallback(new IFingerprintAuthenticatorsRegisteredCallback.Stub() { // from class: com.android.server.biometrics.sensors.fingerprint.FingerprintService.2
            public void onAllAuthenticatorsRegistered(List<FingerprintSensorPropertiesInternal> list) {
                FingerprintService.this.mBiometricStateCallback.start(FingerprintService.this.mRegistry.getProviders());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ FingerprintProvider lambda$new$2(String str) {
        String str2 = IFingerprint.DESCRIPTOR + "/" + str;
        IFingerprint asInterface = IFingerprint.Stub.asInterface(Binder.allowBlocking(ServiceManager.waitForDeclaredService(str2)));
        if (asInterface != null) {
            try {
                return new FingerprintProvider(getContext(), this.mBiometricStateCallback, asInterface.getSensorProps(), str, this.mLockoutResetDispatcher, this.mGestureAvailabilityDispatcher, this.mBiometricContext);
            } catch (RemoteException unused) {
                Slog.e("FingerprintService", "Remote exception in getSensorProps: " + str2);
                return null;
            }
        }
        Slog.e("FingerprintService", "Unable to get declared service: " + str2);
        return null;
    }

    public final List<ServiceProvider> getHidlProviders(List<FingerprintSensorPropertiesInternal> list) {
        Fingerprint21 newInstance;
        ArrayList arrayList = new ArrayList();
        for (FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal : list) {
            if ((Build.IS_USERDEBUG || Build.IS_ENG) && getContext().getResources().getBoolean(17891340) && Settings.Secure.getIntForUser(getContext().getContentResolver(), "com.android.server.biometrics.sensors.fingerprint.test_udfps.enable", 0, -2) != 0) {
                newInstance = Fingerprint21UdfpsMock.newInstance(getContext(), this.mBiometricStateCallback, fingerprintSensorPropertiesInternal, this.mLockoutResetDispatcher, this.mGestureAvailabilityDispatcher, BiometricContext.getInstance(getContext()));
            } else {
                newInstance = Fingerprint21.newInstance(getContext(), this.mBiometricStateCallback, fingerprintSensorPropertiesInternal, this.mHandler, this.mLockoutResetDispatcher, this.mGestureAvailabilityDispatcher);
            }
            arrayList.add(newInstance);
        }
        return arrayList;
    }

    public final List<ServiceProvider> getAidlProviders(List<String> list) {
        ArrayList arrayList = new ArrayList();
        for (String str : list) {
            Slog.i("FingerprintService", "Adding AIDL provider: " + str);
            arrayList.add(this.mFingerprintProvider.apply(str));
        }
        return arrayList;
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("fingerprint", this.mServiceWrapper);
    }

    public final List<Fingerprint> getEnrolledFingerprintsDeprecated(int i, String str) {
        Pair<Integer, ServiceProvider> singleProvider = this.mRegistry.getSingleProvider();
        if (singleProvider == null) {
            Slog.w("FingerprintService", "Null provider for getEnrolledFingerprintsDeprecated, caller: " + str);
            return Collections.emptyList();
        }
        return ((ServiceProvider) singleProvider.second).getEnrolledFingerprints(((Integer) singleProvider.first).intValue(), i);
    }

    public final boolean canUseFingerprint(String str, String str2, boolean z, int i, int i2, int i3) {
        if (getContext().checkCallingPermission("android.permission.USE_FINGERPRINT") != 0) {
            Utils.checkPermission(getContext(), "android.permission.USE_BIOMETRIC");
        }
        if (Binder.getCallingUid() == 1000 || Utils.isKeyguard(getContext(), str)) {
            return true;
        }
        if (!Utils.isCurrentUserOrProfile(getContext(), i3)) {
            Slog.w("FingerprintService", "Rejecting " + str + "; not a current user or profile");
            return false;
        } else if (!checkAppOps(i, str, str2)) {
            Slog.w("FingerprintService", "Rejecting " + str + "; permission denied");
            return false;
        } else if (!z || Utils.isForeground(i, i2)) {
            return true;
        } else {
            Slog.w("FingerprintService", "Rejecting " + str + "; not in foreground");
            return false;
        }
    }

    public final boolean checkAppOps(int i, String str, String str2) {
        return this.mAppOps.noteOp(78, i, str, str2, (String) null) == 0 || this.mAppOps.noteOp(55, i, str, str2, (String) null) == 0;
    }

    public void syncEnrollmentsNow() {
        Utils.checkPermissionOrShell(getContext(), "android.permission.MANAGE_FINGERPRINT");
        if (Utils.isVirtualEnabled(getContext())) {
            Slog.i("FingerprintService", "Sync virtual enrollments");
            int currentUser = ActivityManager.getCurrentUser();
            for (ServiceProvider serviceProvider : this.mRegistry.getProviders()) {
                for (FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal : serviceProvider.getSensorProperties()) {
                    serviceProvider.scheduleInternalCleanup(fingerprintSensorPropertiesInternal.sensorId, currentUser, null, true);
                }
            }
        }
    }
}
