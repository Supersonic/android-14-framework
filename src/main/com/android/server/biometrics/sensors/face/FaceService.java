package com.android.server.biometrics.sensors.face;

import android.annotation.EnforcePermission;
import android.app.ActivityManager;
import android.content.Context;
import android.hardware.biometrics.IBiometricSensorReceiver;
import android.hardware.biometrics.IBiometricService;
import android.hardware.biometrics.IBiometricServiceLockoutResetCallback;
import android.hardware.biometrics.IBiometricStateListener;
import android.hardware.biometrics.IInvalidationCallback;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.biometrics.face.IFace;
import android.hardware.face.Face;
import android.hardware.face.FaceAuthenticateOptions;
import android.hardware.face.FaceSensorPropertiesInternal;
import android.hardware.face.FaceServiceReceiver;
import android.hardware.face.IFaceAuthenticatorsRegisteredCallback;
import android.hardware.face.IFaceService;
import android.hardware.face.IFaceServiceReceiver;
import android.os.Binder;
import android.os.IBinder;
import android.os.NativeHandle;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Pair;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.Surface;
import com.android.internal.util.DumpUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.SystemService;
import com.android.server.biometrics.Utils;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.sensors.BiometricStateCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.LockoutResetDispatcher;
import com.android.server.biometrics.sensors.face.FaceService;
import com.android.server.biometrics.sensors.face.aidl.FaceProvider;
import com.android.server.biometrics.sensors.face.hidl.Face10;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceService extends SystemService {
    public final BiometricStateCallback<ServiceProvider, FaceSensorPropertiesInternal> mBiometricStateCallback;
    public final LockPatternUtils mLockPatternUtils;
    public final LockoutResetDispatcher mLockoutResetDispatcher;
    public final FaceServiceRegistry mRegistry;
    public final FaceServiceWrapper mServiceWrapper;

    public static native NativeHandle acquireSurfaceHandle(Surface surface);

    public static native void releaseSurfaceHandle(NativeHandle nativeHandle);

    /* loaded from: classes.dex */
    public final class FaceServiceWrapper extends IFaceService.Stub {
        public FaceServiceWrapper() {
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public ITestSession createTestSession(int i, ITestSessionCallback iTestSessionCallback, String str) {
            super.createTestSession_enforcePermission();
            ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FaceService", "Null provider for createTestSession, sensorId: " + i);
                return null;
            }
            return providerForSensor.createTestSession(i, iTestSessionCallback, str);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public byte[] dumpSensorServiceStateProto(int i, boolean z) {
            super.dumpSensorServiceStateProto_enforcePermission();
            ProtoOutputStream protoOutputStream = new ProtoOutputStream();
            ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor != null) {
                providerForSensor.dumpProtoState(i, protoOutputStream, z);
            }
            protoOutputStream.flush();
            return protoOutputStream.getBytes();
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public List<FaceSensorPropertiesInternal> getSensorPropertiesInternal(String str) {
            super.getSensorPropertiesInternal_enforcePermission();
            return FaceService.this.mRegistry.getAllProperties();
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public FaceSensorPropertiesInternal getSensorProperties(int i, String str) {
            super.getSensorProperties_enforcePermission();
            ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FaceService", "No matching sensor for getSensorProperties, sensorId: " + i + ", caller: " + str);
                return null;
            }
            return providerForSensor.getSensorProperties(i);
        }

        @EnforcePermission("android.permission.MANAGE_BIOMETRIC")
        public void generateChallenge(IBinder iBinder, int i, int i2, IFaceServiceReceiver iFaceServiceReceiver, String str) {
            super.generateChallenge_enforcePermission();
            ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FaceService", "No matching sensor for generateChallenge, sensorId: " + i);
                return;
            }
            providerForSensor.scheduleGenerateChallenge(i, i2, iBinder, iFaceServiceReceiver, str);
        }

        @EnforcePermission("android.permission.MANAGE_BIOMETRIC")
        public void revokeChallenge(IBinder iBinder, int i, int i2, String str, long j) {
            super.revokeChallenge_enforcePermission();
            ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FaceService", "No matching sensor for revokeChallenge, sensorId: " + i);
                return;
            }
            providerForSensor.scheduleRevokeChallenge(i, i2, iBinder, str, j);
        }

        @EnforcePermission("android.permission.MANAGE_BIOMETRIC")
        public long enroll(int i, IBinder iBinder, byte[] bArr, IFaceServiceReceiver iFaceServiceReceiver, String str, int[] iArr, Surface surface, boolean z) {
            super.enroll_enforcePermission();
            Pair<Integer, ServiceProvider> singleProvider = FaceService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FaceService", "Null provider for enroll");
                return -1L;
            }
            return ((ServiceProvider) singleProvider.second).scheduleEnroll(((Integer) singleProvider.first).intValue(), iBinder, bArr, i, iFaceServiceReceiver, str, iArr, surface, z);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void scheduleWatchdog() {
            super.scheduleWatchdog_enforcePermission();
            Pair<Integer, ServiceProvider> singleProvider = FaceService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FaceService", "Null provider for scheduling watchdog");
            } else {
                ((ServiceProvider) singleProvider.second).scheduleWatchdog(((Integer) singleProvider.first).intValue());
            }
        }

        @EnforcePermission("android.permission.MANAGE_BIOMETRIC")
        public long enrollRemotely(int i, IBinder iBinder, byte[] bArr, IFaceServiceReceiver iFaceServiceReceiver, String str, int[] iArr) {
            super.enrollRemotely_enforcePermission();
            return -1L;
        }

        @EnforcePermission("android.permission.MANAGE_BIOMETRIC")
        public void cancelEnrollment(IBinder iBinder, long j) {
            super.cancelEnrollment_enforcePermission();
            Pair<Integer, ServiceProvider> singleProvider = FaceService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FaceService", "Null provider for cancelEnrollment");
            } else {
                ((ServiceProvider) singleProvider.second).cancelEnrollment(((Integer) singleProvider.first).intValue(), iBinder, j);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public long authenticate(IBinder iBinder, long j, IFaceServiceReceiver iFaceServiceReceiver, FaceAuthenticateOptions faceAuthenticateOptions) {
            super.authenticate_enforcePermission();
            String opPackageName = faceAuthenticateOptions.getOpPackageName();
            boolean isKeyguard = Utils.isKeyguard(FaceService.this.getContext(), opPackageName);
            boolean isKeyguard2 = Utils.isKeyguard(FaceService.this.getContext(), opPackageName);
            Pair<Integer, ServiceProvider> singleProvider = FaceService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FaceService", "Null provider for authenticate");
                return -1L;
            }
            faceAuthenticateOptions.setSensorId(((Integer) singleProvider.first).intValue());
            return ((ServiceProvider) singleProvider.second).scheduleAuthenticate(iBinder, j, 0, new ClientMonitorCallbackConverter(iFaceServiceReceiver), faceAuthenticateOptions, false, isKeyguard ? 1 : 0, isKeyguard2);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public long detectFace(IBinder iBinder, IFaceServiceReceiver iFaceServiceReceiver, FaceAuthenticateOptions faceAuthenticateOptions) {
            super.detectFace_enforcePermission();
            String opPackageName = faceAuthenticateOptions.getOpPackageName();
            if (!Utils.isKeyguard(FaceService.this.getContext(), opPackageName)) {
                Slog.w("FaceService", "detectFace called from non-sysui package: " + opPackageName);
                return -1L;
            }
            Pair<Integer, ServiceProvider> singleProvider = FaceService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FaceService", "Null provider for detectFace");
                return -1L;
            }
            faceAuthenticateOptions.setSensorId(((Integer) singleProvider.first).intValue());
            return ((ServiceProvider) singleProvider.second).scheduleFaceDetect(iBinder, new ClientMonitorCallbackConverter(iFaceServiceReceiver), faceAuthenticateOptions, 1);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void prepareForAuthentication(boolean z, IBinder iBinder, long j, IBiometricSensorReceiver iBiometricSensorReceiver, FaceAuthenticateOptions faceAuthenticateOptions, long j2, int i, boolean z2) {
            super.prepareForAuthentication_enforcePermission();
            ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(faceAuthenticateOptions.getSensorId());
            if (providerForSensor == null) {
                Slog.w("FaceService", "Null provider for prepareForAuthentication");
            } else {
                providerForSensor.scheduleAuthenticate(iBinder, j, i, new ClientMonitorCallbackConverter(iBiometricSensorReceiver), faceAuthenticateOptions, j2, true, 2, z2);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void startPreparedClient(int i, int i2) {
            super.startPreparedClient_enforcePermission();
            ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FaceService", "Null provider for startPreparedClient");
            } else {
                providerForSensor.startPreparedClient(i, i2);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void cancelAuthentication(IBinder iBinder, String str, long j) {
            super.cancelAuthentication_enforcePermission();
            Pair<Integer, ServiceProvider> singleProvider = FaceService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FaceService", "Null provider for cancelAuthentication");
            } else {
                ((ServiceProvider) singleProvider.second).cancelAuthentication(((Integer) singleProvider.first).intValue(), iBinder, j);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void cancelFaceDetect(IBinder iBinder, String str, long j) {
            super.cancelFaceDetect_enforcePermission();
            if (!Utils.isKeyguard(FaceService.this.getContext(), str)) {
                Slog.w("FaceService", "cancelFaceDetect called from non-sysui package: " + str);
                return;
            }
            Pair<Integer, ServiceProvider> singleProvider = FaceService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FaceService", "Null provider for cancelFaceDetect");
            } else {
                ((ServiceProvider) singleProvider.second).cancelFaceDetect(((Integer) singleProvider.first).intValue(), iBinder, j);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void cancelAuthenticationFromService(int i, IBinder iBinder, String str, long j) {
            super.cancelAuthenticationFromService_enforcePermission();
            ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FaceService", "Null provider for cancelAuthenticationFromService");
            } else {
                providerForSensor.cancelAuthentication(i, iBinder, j);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void remove(IBinder iBinder, int i, int i2, IFaceServiceReceiver iFaceServiceReceiver, String str) {
            super.remove_enforcePermission();
            Pair<Integer, ServiceProvider> singleProvider = FaceService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FaceService", "Null provider for remove");
            } else {
                ((ServiceProvider) singleProvider.second).scheduleRemove(((Integer) singleProvider.first).intValue(), iBinder, i, i2, iFaceServiceReceiver, str);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void removeAll(IBinder iBinder, int i, final IFaceServiceReceiver iFaceServiceReceiver, String str) {
            super.removeAll_enforcePermission();
            IFaceServiceReceiver iFaceServiceReceiver2 = new FaceServiceReceiver() { // from class: com.android.server.biometrics.sensors.face.FaceService.FaceServiceWrapper.1
                public final int numSensors;
                public int sensorsFinishedRemoving = 0;

                {
                    this.numSensors = FaceServiceWrapper.this.getSensorPropertiesInternal(FaceService.this.getContext().getOpPackageName()).size();
                }

                public void onRemoved(Face face, int i2) throws RemoteException {
                    if (i2 == 0) {
                        this.sensorsFinishedRemoving++;
                        Slog.d("FaceService", "sensorsFinishedRemoving: " + this.sensorsFinishedRemoving + ", numSensors: " + this.numSensors);
                        if (this.sensorsFinishedRemoving == this.numSensors) {
                            iFaceServiceReceiver.onRemoved((Face) null, 0);
                        }
                    }
                }
            };
            for (ServiceProvider serviceProvider : FaceService.this.mRegistry.getProviders()) {
                for (FaceSensorPropertiesInternal faceSensorPropertiesInternal : serviceProvider.getSensorProperties()) {
                    serviceProvider.scheduleRemoveAll(faceSensorPropertiesInternal.sensorId, iBinder, i, iFaceServiceReceiver2, str);
                }
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void addLockoutResetCallback(IBiometricServiceLockoutResetCallback iBiometricServiceLockoutResetCallback, String str) {
            super.addLockoutResetCallback_enforcePermission();
            FaceService.this.mLockoutResetDispatcher.addCallback(iBiometricServiceLockoutResetCallback, str);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) throws RemoteException {
            new FaceShellCommand(FaceService.this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(FaceService.this.getContext(), "FaceService", printWriter)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    if (strArr.length > 1 && "--proto".equals(strArr[0]) && "--state".equals(strArr[1])) {
                        ProtoOutputStream protoOutputStream = new ProtoOutputStream(fileDescriptor);
                        for (ServiceProvider serviceProvider : FaceService.this.mRegistry.getProviders()) {
                            for (FaceSensorPropertiesInternal faceSensorPropertiesInternal : serviceProvider.getSensorProperties()) {
                                serviceProvider.dumpProtoState(faceSensorPropertiesInternal.sensorId, protoOutputStream, false);
                            }
                        }
                        protoOutputStream.flush();
                    } else if (strArr.length > 0 && "--proto".equals(strArr[0])) {
                        for (ServiceProvider serviceProvider2 : FaceService.this.mRegistry.getProviders()) {
                            for (FaceSensorPropertiesInternal faceSensorPropertiesInternal2 : serviceProvider2.getSensorProperties()) {
                                serviceProvider2.dumpProtoMetrics(faceSensorPropertiesInternal2.sensorId, fileDescriptor);
                            }
                        }
                    } else if (strArr.length > 1 && "--hal".equals(strArr[0])) {
                        for (ServiceProvider serviceProvider3 : FaceService.this.mRegistry.getProviders()) {
                            for (FaceSensorPropertiesInternal faceSensorPropertiesInternal3 : serviceProvider3.getSensorProperties()) {
                                serviceProvider3.dumpHal(faceSensorPropertiesInternal3.sensorId, fileDescriptor, (String[]) Arrays.copyOfRange(strArr, 1, strArr.length, strArr.getClass()));
                            }
                        }
                    } else {
                        for (ServiceProvider serviceProvider4 : FaceService.this.mRegistry.getProviders()) {
                            for (FaceSensorPropertiesInternal faceSensorPropertiesInternal4 : serviceProvider4.getSensorProperties()) {
                                printWriter.println("Dumping for sensorId: " + faceSensorPropertiesInternal4.sensorId + ", provider: " + serviceProvider4.getClass().getSimpleName());
                                serviceProvider4.dumpInternal(faceSensorPropertiesInternal4.sensorId, printWriter);
                                printWriter.println();
                            }
                        }
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public boolean isHardwareDetected(int i, String str) {
            super.isHardwareDetected_enforcePermission();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(i);
                if (providerForSensor == null) {
                    Slog.w("FaceService", "Null provider for isHardwareDetected, caller: " + str);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return false;
                }
                return providerForSensor.isHardwareDetected(i);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public List<Face> getEnrolledFaces(int i, int i2, String str) {
            super.getEnrolledFaces_enforcePermission();
            if (i2 != UserHandle.getCallingUserId()) {
                Utils.checkPermission(FaceService.this.getContext(), "android.permission.INTERACT_ACROSS_USERS");
            }
            ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FaceService", "Null provider for getEnrolledFaces, caller: " + str);
                return Collections.emptyList();
            }
            return providerForSensor.getEnrolledFaces(i, i2);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public boolean hasEnrolledFaces(int i, int i2, String str) {
            super.hasEnrolledFaces_enforcePermission();
            if (i2 != UserHandle.getCallingUserId()) {
                Utils.checkPermission(FaceService.this.getContext(), "android.permission.INTERACT_ACROSS_USERS");
            }
            ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor != null) {
                return providerForSensor.getEnrolledFaces(i, i2).size() > 0;
            }
            Slog.w("FaceService", "Null provider for hasEnrolledFaces, caller: " + str);
            return false;
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public int getLockoutModeForUser(int i, int i2) {
            super.getLockoutModeForUser_enforcePermission();
            ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FaceService", "Null provider for getLockoutModeForUser");
                return 0;
            }
            return providerForSensor.getLockoutModeForUser(i, i2);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void invalidateAuthenticatorId(int i, int i2, IInvalidationCallback iInvalidationCallback) {
            super.invalidateAuthenticatorId_enforcePermission();
            ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FaceService", "Null provider for invalidateAuthenticatorId");
            } else {
                providerForSensor.scheduleInvalidateAuthenticatorId(i, i2, iInvalidationCallback);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public long getAuthenticatorId(int i, int i2) {
            super.getAuthenticatorId_enforcePermission();
            ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FaceService", "Null provider for getAuthenticatorId");
                return 0L;
            }
            return providerForSensor.getAuthenticatorId(i, i2);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void resetLockout(IBinder iBinder, int i, int i2, byte[] bArr, String str) {
            super.resetLockout_enforcePermission();
            ServiceProvider providerForSensor = FaceService.this.mRegistry.getProviderForSensor(i);
            if (providerForSensor == null) {
                Slog.w("FaceService", "Null provider for resetLockout, caller: " + str);
                return;
            }
            providerForSensor.scheduleResetLockout(i, i2, bArr);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void setFeature(IBinder iBinder, int i, int i2, boolean z, byte[] bArr, IFaceServiceReceiver iFaceServiceReceiver, String str) {
            super.setFeature_enforcePermission();
            Pair<Integer, ServiceProvider> singleProvider = FaceService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FaceService", "Null provider for setFeature");
            } else {
                ((ServiceProvider) singleProvider.second).scheduleSetFeature(((Integer) singleProvider.first).intValue(), iBinder, i, i2, z, bArr, iFaceServiceReceiver, str);
            }
        }

        @EnforcePermission("android.permission.MANAGE_BIOMETRIC")
        public void getFeature(IBinder iBinder, int i, int i2, IFaceServiceReceiver iFaceServiceReceiver, String str) {
            super.getFeature_enforcePermission();
            Pair<Integer, ServiceProvider> singleProvider = FaceService.this.mRegistry.getSingleProvider();
            if (singleProvider == null) {
                Slog.w("FaceService", "Null provider for getFeature");
            } else {
                ((ServiceProvider) singleProvider.second).scheduleGetFeature(((Integer) singleProvider.first).intValue(), iBinder, i, i2, new ClientMonitorCallbackConverter(iFaceServiceReceiver), str);
            }
        }

        public final List<ServiceProvider> getAidlProviders() {
            ArrayList arrayList = new ArrayList();
            String[] declaredInstances = ServiceManager.getDeclaredInstances(IFace.DESCRIPTOR);
            if (declaredInstances != null && declaredInstances.length != 0) {
                for (String str : declaredInstances) {
                    String str2 = IFace.DESCRIPTOR + "/" + str;
                    IFace asInterface = IFace.Stub.asInterface(Binder.allowBlocking(ServiceManager.waitForDeclaredService(str2)));
                    if (asInterface == null) {
                        Slog.e("FaceService", "Unable to get declared service: " + str2);
                    } else {
                        try {
                            arrayList.add(new FaceProvider(FaceService.this.getContext(), FaceService.this.mBiometricStateCallback, asInterface.getSensorProps(), str, FaceService.this.mLockoutResetDispatcher, BiometricContext.getInstance(FaceService.this.getContext())));
                        } catch (RemoteException unused) {
                            Slog.e("FaceService", "Remote exception in getSensorProps: " + str2);
                        }
                    }
                }
            }
            return arrayList;
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void registerAuthenticators(final List<FaceSensorPropertiesInternal> list) {
            super.registerAuthenticators_enforcePermission();
            FaceService.this.mRegistry.registerAll(new Supplier() { // from class: com.android.server.biometrics.sensors.face.FaceService$FaceServiceWrapper$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    List lambda$registerAuthenticators$0;
                    lambda$registerAuthenticators$0 = FaceService.FaceServiceWrapper.this.lambda$registerAuthenticators$0(list);
                    return lambda$registerAuthenticators$0;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ List lambda$registerAuthenticators$0(List list) {
            ArrayList arrayList = new ArrayList();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                arrayList.add(Face10.newInstance(FaceService.this.getContext(), FaceService.this.mBiometricStateCallback, (FaceSensorPropertiesInternal) it.next(), FaceService.this.mLockoutResetDispatcher));
            }
            arrayList.addAll(getAidlProviders());
            return arrayList;
        }

        public void addAuthenticatorsRegisteredCallback(IFaceAuthenticatorsRegisteredCallback iFaceAuthenticatorsRegisteredCallback) {
            Utils.checkPermission(FaceService.this.getContext(), "android.permission.USE_BIOMETRIC_INTERNAL");
            FaceService.this.mRegistry.addAllRegisteredCallback(iFaceAuthenticatorsRegisteredCallback);
        }

        public void registerBiometricStateListener(IBiometricStateListener iBiometricStateListener) {
            FaceService.this.mBiometricStateCallback.registerBiometricStateListener(iBiometricStateListener);
        }
    }

    public FaceService(Context context) {
        super(context);
        FaceServiceWrapper faceServiceWrapper = new FaceServiceWrapper();
        this.mServiceWrapper = faceServiceWrapper;
        this.mLockoutResetDispatcher = new LockoutResetDispatcher(context);
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mBiometricStateCallback = new BiometricStateCallback<>(UserManager.get(context));
        FaceServiceRegistry faceServiceRegistry = new FaceServiceRegistry(faceServiceWrapper, new Supplier() { // from class: com.android.server.biometrics.sensors.face.FaceService$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                IBiometricService lambda$new$0;
                lambda$new$0 = FaceService.lambda$new$0();
                return lambda$new$0;
            }
        });
        this.mRegistry = faceServiceRegistry;
        faceServiceRegistry.addAllRegisteredCallback(new IFaceAuthenticatorsRegisteredCallback.Stub() { // from class: com.android.server.biometrics.sensors.face.FaceService.1
            public void onAllAuthenticatorsRegistered(List<FaceSensorPropertiesInternal> list) {
                FaceService.this.mBiometricStateCallback.start(FaceService.this.mRegistry.getProviders());
            }
        });
    }

    public static /* synthetic */ IBiometricService lambda$new$0() {
        return IBiometricService.Stub.asInterface(ServiceManager.getService("biometric"));
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("face", this.mServiceWrapper);
    }

    public void syncEnrollmentsNow() {
        Utils.checkPermissionOrShell(getContext(), "android.permission.MANAGE_FACE");
        if (Utils.isVirtualEnabled(getContext())) {
            Slog.i("FaceService", "Sync virtual enrollments");
            int currentUser = ActivityManager.getCurrentUser();
            for (ServiceProvider serviceProvider : this.mRegistry.getProviders()) {
                for (FaceSensorPropertiesInternal faceSensorPropertiesInternal : serviceProvider.getSensorProperties()) {
                    serviceProvider.scheduleInternalCleanup(faceSensorPropertiesInternal.sensorId, currentUser, null, true);
                }
            }
        }
    }
}
