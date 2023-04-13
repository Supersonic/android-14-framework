package com.android.server.recoverysystem.hal;

import android.hardware.boot.IBootControl;
import android.os.IBinder;
import android.os.IHwInterface;
import android.os.RemoteException;
import android.util.Slog;
/* loaded from: classes2.dex */
public class BootControlHIDL implements IBootControl {
    public final android.hardware.boot.V1_1.IBootControl v1_1_hal;
    public final android.hardware.boot.V1_2.IBootControl v1_2_hal;
    public final android.hardware.boot.V1_0.IBootControl v1_hal;

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return null;
    }

    public static boolean isServicePresent() {
        try {
            android.hardware.boot.V1_0.IBootControl.getService(true);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public static boolean isV1_2ServicePresent() {
        try {
            android.hardware.boot.V1_2.IBootControl.getService(true);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public static BootControlHIDL getService() throws RemoteException {
        android.hardware.boot.V1_0.IBootControl service = android.hardware.boot.V1_0.IBootControl.getService(true);
        return new BootControlHIDL(service, android.hardware.boot.V1_1.IBootControl.castFrom((IHwInterface) service), android.hardware.boot.V1_2.IBootControl.castFrom((IHwInterface) service));
    }

    public BootControlHIDL(android.hardware.boot.V1_0.IBootControl iBootControl, android.hardware.boot.V1_1.IBootControl iBootControl2, android.hardware.boot.V1_2.IBootControl iBootControl3) throws RemoteException {
        this.v1_hal = iBootControl;
        this.v1_1_hal = iBootControl2;
        this.v1_2_hal = iBootControl3;
        if (iBootControl == null) {
            throw new RemoteException("Failed to find V1.0 BootControl HIDL");
        }
        if (iBootControl3 != null) {
            Slog.i("BootControlHIDL", "V1.2 version of BootControl HIDL HAL available, using V1.2");
        } else if (iBootControl2 != null) {
            Slog.i("BootControlHIDL", "V1.1 version of BootControl HIDL HAL available, using V1.1");
        } else {
            Slog.i("BootControlHIDL", "V1.0 version of BootControl HIDL HAL available, using V1.0");
        }
    }

    @Override // android.hardware.boot.IBootControl
    public int getActiveBootSlot() throws RemoteException {
        android.hardware.boot.V1_2.IBootControl iBootControl = this.v1_2_hal;
        if (iBootControl == null) {
            throw new RemoteException("getActiveBootSlot() requires V1.2 BootControl HAL");
        }
        return iBootControl.getActiveBootSlot();
    }

    @Override // android.hardware.boot.IBootControl
    public int getCurrentSlot() throws RemoteException {
        return this.v1_hal.getCurrentSlot();
    }
}
