package com.android.server.health;

import android.hardware.health.HealthInfo;
import android.hardware.health.IHealth;
import android.os.BatteryProperty;
import android.os.Binder;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IServiceCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.os.Trace;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.health.HealthServiceWrapperAidl;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class HealthServiceWrapperAidl extends HealthServiceWrapper {
    @VisibleForTesting
    static final String SERVICE_NAME = IHealth.DESCRIPTOR + "/default";
    public final HandlerThread mHandlerThread;
    public final AtomicReference<IHealth> mLastService;
    public final HealthRegCallbackAidl mRegCallback;
    public final IServiceCallback mServiceCallback;

    /* loaded from: classes.dex */
    public interface ServiceManagerStub {
        default IHealth waitForDeclaredService(String str) {
            return IHealth.Stub.asInterface(ServiceManager.waitForDeclaredService(str));
        }

        default void registerForNotifications(String str, IServiceCallback iServiceCallback) throws RemoteException {
            ServiceManager.registerForNotifications(str, iServiceCallback);
        }
    }

    public HealthServiceWrapperAidl(HealthRegCallbackAidl healthRegCallbackAidl, ServiceManagerStub serviceManagerStub) throws RemoteException, NoSuchElementException {
        HandlerThread handlerThread = new HandlerThread("HealthServiceBinder");
        this.mHandlerThread = handlerThread;
        AtomicReference<IHealth> atomicReference = new AtomicReference<>();
        this.mLastService = atomicReference;
        ServiceCallback serviceCallback = new ServiceCallback();
        this.mServiceCallback = serviceCallback;
        traceBegin("HealthInitGetServiceAidl");
        try {
            String str = SERVICE_NAME;
            IHealth waitForDeclaredService = serviceManagerStub.waitForDeclaredService(str);
            if (waitForDeclaredService == null) {
                throw new NoSuchElementException("IHealth service instance isn't available. Perhaps no permission?");
            }
            atomicReference.set(waitForDeclaredService);
            this.mRegCallback = healthRegCallbackAidl;
            if (healthRegCallbackAidl != null) {
                healthRegCallbackAidl.onRegistration(null, waitForDeclaredService);
            }
            traceBegin("HealthInitRegisterNotificationAidl");
            handlerThread.start();
            try {
                serviceManagerStub.registerForNotifications(str, serviceCallback);
                traceEnd();
                Slog.i("HealthServiceWrapperAidl", "health: HealthServiceWrapper listening to AIDL HAL");
            } finally {
            }
        } finally {
        }
    }

    @Override // com.android.server.health.HealthServiceWrapper
    @VisibleForTesting
    public HandlerThread getHandlerThread() {
        return this.mHandlerThread;
    }

    @Override // com.android.server.health.HealthServiceWrapper
    public int getProperty(int i, BatteryProperty batteryProperty) throws RemoteException {
        traceBegin("HealthGetPropertyAidl");
        try {
            return getPropertyInternal(i, batteryProperty);
        } finally {
            traceEnd();
        }
    }

    public final int getPropertyInternal(int i, BatteryProperty batteryProperty) throws RemoteException {
        IHealth iHealth = this.mLastService.get();
        if (iHealth == null) {
            throw new RemoteException("no health service");
        }
        try {
            switch (i) {
                case 1:
                    batteryProperty.setLong(iHealth.getChargeCounterUah());
                    break;
                case 2:
                    batteryProperty.setLong(iHealth.getCurrentNowMicroamps());
                    break;
                case 3:
                    batteryProperty.setLong(iHealth.getCurrentAverageMicroamps());
                    break;
                case 4:
                    batteryProperty.setLong(iHealth.getCapacity());
                    break;
                case 5:
                    batteryProperty.setLong(iHealth.getEnergyCounterNwh());
                    break;
                case 6:
                    batteryProperty.setLong(iHealth.getChargeStatus());
                    break;
                case 7:
                    batteryProperty.setLong(iHealth.getBatteryHealthData().batteryManufacturingDateSeconds);
                    break;
                case 8:
                    batteryProperty.setLong(iHealth.getBatteryHealthData().batteryFirstUsageSeconds);
                    break;
                case 9:
                    batteryProperty.setLong(iHealth.getChargingPolicy());
                    break;
                case 10:
                    batteryProperty.setLong(iHealth.getBatteryHealthData().batteryStateOfHealth);
                    break;
                default:
                    return 0;
            }
            return 0;
        } catch (UnsupportedOperationException unused) {
            return -1;
        } catch (ServiceSpecificException unused2) {
            return -2;
        }
    }

    @Override // com.android.server.health.HealthServiceWrapper
    public void scheduleUpdate() throws RemoteException {
        getHandlerThread().getThreadHandler().post(new Runnable() { // from class: com.android.server.health.HealthServiceWrapperAidl$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                HealthServiceWrapperAidl.this.lambda$scheduleUpdate$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleUpdate$0() {
        IHealth iHealth;
        traceBegin("HealthScheduleUpdate");
        try {
            try {
                iHealth = this.mLastService.get();
            } catch (RemoteException | ServiceSpecificException e) {
                Slog.e("HealthServiceWrapperAidl", "Cannot call update on health AIDL HAL", e);
            }
            if (iHealth == null) {
                Slog.e("HealthServiceWrapperAidl", "no health service");
            } else {
                iHealth.update();
            }
        } finally {
            traceEnd();
        }
    }

    @Override // com.android.server.health.HealthServiceWrapper
    public HealthInfo getHealthInfo() throws RemoteException {
        IHealth iHealth = this.mLastService.get();
        if (iHealth == null) {
            return null;
        }
        try {
            return iHealth.getHealthInfo();
        } catch (UnsupportedOperationException | ServiceSpecificException unused) {
            return null;
        }
    }

    public static void traceBegin(String str) {
        Trace.traceBegin(524288L, str);
    }

    public static void traceEnd() {
        Trace.traceEnd(524288L);
    }

    /* loaded from: classes.dex */
    public class ServiceCallback extends IServiceCallback.Stub {
        public ServiceCallback() {
        }

        public void onRegistration(String str, final IBinder iBinder) throws RemoteException {
            if (HealthServiceWrapperAidl.SERVICE_NAME.equals(str)) {
                HealthServiceWrapperAidl.this.getHandlerThread().getThreadHandler().post(new Runnable() { // from class: com.android.server.health.HealthServiceWrapperAidl$ServiceCallback$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        HealthServiceWrapperAidl.ServiceCallback.this.lambda$onRegistration$0(iBinder);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onRegistration$0(IBinder iBinder) {
            IHealth asInterface = IHealth.Stub.asInterface(Binder.allowBlocking(iBinder));
            IHealth iHealth = (IHealth) HealthServiceWrapperAidl.this.mLastService.getAndSet(asInterface);
            if (Objects.equals(iBinder, iHealth != null ? iHealth.asBinder() : null)) {
                return;
            }
            Slog.i("HealthServiceWrapperAidl", "New health AIDL HAL service registered");
            if (HealthServiceWrapperAidl.this.mRegCallback != null) {
                HealthServiceWrapperAidl.this.mRegCallback.onRegistration(iHealth, asInterface);
            }
        }
    }
}
