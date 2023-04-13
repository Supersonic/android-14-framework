package com.android.server.biometrics.sensors.iris;

import android.annotation.EnforcePermission;
import android.content.Context;
import android.hardware.biometrics.IBiometricService;
import android.hardware.biometrics.SensorPropertiesInternal;
import android.hardware.iris.IIrisService;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;
import com.android.server.ServiceThread;
import com.android.server.SystemService;
import com.android.server.biometrics.Utils;
import com.android.server.biometrics.sensors.iris.IrisService;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class IrisService extends SystemService {
    public final IrisServiceWrapper mServiceWrapper;

    /* loaded from: classes.dex */
    public final class IrisServiceWrapper extends IIrisService.Stub {
        public IrisServiceWrapper() {
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void registerAuthenticators(final List<SensorPropertiesInternal> list) {
            super.registerAuthenticators_enforcePermission();
            ServiceThread serviceThread = new ServiceThread("IrisService", 10, true);
            serviceThread.start();
            new Handler(serviceThread.getLooper()).post(new Runnable() { // from class: com.android.server.biometrics.sensors.iris.IrisService$IrisServiceWrapper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    IrisService.IrisServiceWrapper.this.lambda$registerAuthenticators$0(list);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$registerAuthenticators$0(List list) {
            IBiometricService asInterface = IBiometricService.Stub.asInterface(ServiceManager.getService("biometric"));
            Iterator it = list.iterator();
            while (it.hasNext()) {
                SensorPropertiesInternal sensorPropertiesInternal = (SensorPropertiesInternal) it.next();
                int i = sensorPropertiesInternal.sensorId;
                try {
                    asInterface.registerAuthenticator(i, 4, Utils.propertyStrengthToAuthenticatorStrength(sensorPropertiesInternal.sensorStrength), new IrisAuthenticator(IrisService.this.mServiceWrapper, i));
                } catch (RemoteException unused) {
                    Slog.e("IrisService", "Remote exception when registering sensorId: " + i);
                }
            }
        }
    }

    public IrisService(Context context) {
        super(context);
        this.mServiceWrapper = new IrisServiceWrapper();
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("iris", this.mServiceWrapper);
    }
}
