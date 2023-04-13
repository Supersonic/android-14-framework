package com.android.server.health;

import android.hardware.health.HealthInfo;
import android.os.BatteryProperty;
import android.os.HandlerThread;
import android.os.RemoteException;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.health.HealthServiceWrapperAidl;
import com.android.server.health.HealthServiceWrapperHidl;
import java.util.NoSuchElementException;
/* loaded from: classes.dex */
public abstract class HealthServiceWrapper {
    @VisibleForTesting
    public abstract HandlerThread getHandlerThread();

    public abstract HealthInfo getHealthInfo() throws RemoteException;

    public abstract int getProperty(int i, BatteryProperty batteryProperty) throws RemoteException;

    public abstract void scheduleUpdate() throws RemoteException;

    public static HealthServiceWrapper create(HealthInfoCallback healthInfoCallback) throws RemoteException, NoSuchElementException {
        return create(healthInfoCallback == null ? null : new HealthRegCallbackAidl(healthInfoCallback), new HealthServiceWrapperAidl.ServiceManagerStub() { // from class: com.android.server.health.HealthServiceWrapper.1
        }, healthInfoCallback != null ? new HealthHalCallbackHidl(healthInfoCallback) : null, new HealthServiceWrapperHidl.IServiceManagerSupplier() { // from class: com.android.server.health.HealthServiceWrapper.2
        }, new HealthServiceWrapperHidl.IHealthSupplier() { // from class: com.android.server.health.HealthServiceWrapper.3
        });
    }

    @VisibleForTesting
    public static HealthServiceWrapper create(HealthRegCallbackAidl healthRegCallbackAidl, HealthServiceWrapperAidl.ServiceManagerStub serviceManagerStub, HealthServiceWrapperHidl.Callback callback, HealthServiceWrapperHidl.IServiceManagerSupplier iServiceManagerSupplier, HealthServiceWrapperHidl.IHealthSupplier iHealthSupplier) throws RemoteException, NoSuchElementException {
        try {
            return new HealthServiceWrapperAidl(healthRegCallbackAidl, serviceManagerStub);
        } catch (NoSuchElementException unused) {
            return new HealthServiceWrapperHidl(callback, iServiceManagerSupplier, iHealthSupplier);
        }
    }
}
