package com.android.server.usb.hal.gadget;

import android.hardware.usb.UsbManager;
import android.hardware.usb.gadget.V1_0.IUsbGadget;
import android.hardware.usb.gadget.V1_2.IUsbGadgetCallback;
import android.hidl.manager.V1_0.IServiceManager;
import android.hidl.manager.V1_0.IServiceNotification;
import android.os.IHwBinder;
import android.os.RemoteException;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.usb.UsbDeviceManager;
import java.util.NoSuchElementException;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class UsbGadgetHidl implements UsbGadgetHal {
    public UsbDeviceManager mDeviceManager;
    @GuardedBy({"mGadgetProxyLock"})
    public IUsbGadget mGadgetProxy;
    public final Object mGadgetProxyLock = new Object();
    public final IndentingPrintWriter mPw;
    public UsbGadgetCallback mUsbGadgetCallback;

    @Override // com.android.server.usb.hal.gadget.UsbGadgetHal
    @UsbManager.UsbGadgetHalVersion
    public int getGadgetHalVersion() throws RemoteException {
        int i;
        synchronized (this.mGadgetProxyLock) {
            IUsbGadget iUsbGadget = this.mGadgetProxy;
            if (iUsbGadget == null) {
                throw new RemoteException("IUsbGadget not initialized yet");
            }
            if (android.hardware.usb.gadget.V1_2.IUsbGadget.castFrom(iUsbGadget) != null) {
                i = 12;
            } else {
                i = android.hardware.usb.gadget.V1_1.IUsbGadget.castFrom(this.mGadgetProxy) != null ? 11 : 10;
            }
            IndentingPrintWriter indentingPrintWriter = this.mPw;
            UsbDeviceManager.logAndPrint(4, indentingPrintWriter, "USB Gadget HAL HIDL version: " + i);
        }
        return i;
    }

    /* loaded from: classes2.dex */
    public final class DeathRecipient implements IHwBinder.DeathRecipient {
        public final IndentingPrintWriter mPw;

        public DeathRecipient(IndentingPrintWriter indentingPrintWriter) {
            this.mPw = indentingPrintWriter;
        }

        public void serviceDied(long j) {
            if (j == 2000) {
                IndentingPrintWriter indentingPrintWriter = this.mPw;
                UsbDeviceManager.logAndPrint(6, indentingPrintWriter, "Usb Gadget hal service died cookie: " + j);
                synchronized (UsbGadgetHidl.this.mGadgetProxyLock) {
                    UsbGadgetHidl.this.mGadgetProxy = null;
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class ServiceNotification extends IServiceNotification.Stub {
        public ServiceNotification() {
        }

        @Override // android.hidl.manager.V1_0.IServiceNotification
        public void onRegistration(String str, String str2, boolean z) {
            IndentingPrintWriter indentingPrintWriter = UsbGadgetHidl.this.mPw;
            UsbDeviceManager.logAndPrint(4, indentingPrintWriter, "Usb gadget hal service started " + str + " " + str2);
            UsbGadgetHidl.this.connectToProxy(null);
        }
    }

    public final void connectToProxy(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mGadgetProxyLock) {
            if (this.mGadgetProxy != null) {
                return;
            }
            try {
                try {
                    IUsbGadget service = IUsbGadget.getService();
                    this.mGadgetProxy = service;
                    service.linkToDeath(new DeathRecipient(indentingPrintWriter), 2000L);
                } catch (RemoteException e) {
                    UsbDeviceManager.logAndPrintException(indentingPrintWriter, "connectToProxy: usb gadget hal service not responding", e);
                }
            } catch (NoSuchElementException e2) {
                UsbDeviceManager.logAndPrintException(indentingPrintWriter, "connectToProxy: usb gadget hal service not found. Did the service fail to start?", e2);
            }
        }
    }

    public static boolean isServicePresent(IndentingPrintWriter indentingPrintWriter) {
        try {
            IUsbGadget.getService(true);
        } catch (RemoteException e) {
            UsbDeviceManager.logAndPrintException(indentingPrintWriter, "IUSBGadget hal service present but failed to get service", e);
        } catch (NoSuchElementException e2) {
            UsbDeviceManager.logAndPrintException(indentingPrintWriter, "connectToProxy: usb gadget hidl hal service not found.", e2);
            return false;
        }
        return true;
    }

    public UsbGadgetHidl(UsbDeviceManager usbDeviceManager, IndentingPrintWriter indentingPrintWriter) {
        Objects.requireNonNull(usbDeviceManager);
        this.mDeviceManager = usbDeviceManager;
        this.mPw = indentingPrintWriter;
        try {
            if (!IServiceManager.getService().registerForNotifications("android.hardware.usb.gadget@1.0::IUsbGadget", "", new ServiceNotification())) {
                UsbDeviceManager.logAndPrint(6, indentingPrintWriter, "Failed to register service start notification");
            }
            connectToProxy(indentingPrintWriter);
        } catch (RemoteException e) {
            UsbDeviceManager.logAndPrintException(indentingPrintWriter, "Failed to register service start notification", e);
        }
    }

    @Override // com.android.server.usb.hal.gadget.UsbGadgetHal
    public void getCurrentUsbFunctions(long j) {
        try {
            synchronized (this.mGadgetProxyLock) {
                this.mGadgetProxy.getCurrentUsbFunctions(new UsbGadgetCallback());
            }
        } catch (RemoteException e) {
            UsbDeviceManager.logAndPrintException(this.mPw, "RemoteException while calling getCurrentUsbFunctions", e);
        }
    }

    @Override // com.android.server.usb.hal.gadget.UsbGadgetHal
    public void getUsbSpeed(long j) {
        try {
            synchronized (this.mGadgetProxyLock) {
                if (android.hardware.usb.gadget.V1_2.IUsbGadget.castFrom(this.mGadgetProxy) != null) {
                    android.hardware.usb.gadget.V1_2.IUsbGadget.castFrom(this.mGadgetProxy).getUsbSpeed(new UsbGadgetCallback());
                }
            }
        } catch (RemoteException e) {
            UsbDeviceManager.logAndPrintException(this.mPw, "get UsbSpeed failed", e);
        }
    }

    @Override // com.android.server.usb.hal.gadget.UsbGadgetHal
    public void reset(long j) {
        try {
            synchronized (this.mGadgetProxyLock) {
                if (android.hardware.usb.gadget.V1_1.IUsbGadget.castFrom(this.mGadgetProxy) != null) {
                    android.hardware.usb.gadget.V1_1.IUsbGadget.castFrom(this.mGadgetProxy).reset();
                }
            }
        } catch (RemoteException e) {
            UsbDeviceManager.logAndPrintException(this.mPw, "RemoteException while calling reset", e);
        }
    }

    @Override // com.android.server.usb.hal.gadget.UsbGadgetHal
    public void setCurrentUsbFunctions(int i, long j, boolean z, int i2, long j2) {
        try {
            this.mUsbGadgetCallback = new UsbGadgetCallback(null, i, j, z);
            synchronized (this.mGadgetProxyLock) {
                this.mGadgetProxy.setCurrentUsbFunctions(j, this.mUsbGadgetCallback, i2);
            }
        } catch (RemoteException e) {
            IndentingPrintWriter indentingPrintWriter = this.mPw;
            UsbDeviceManager.logAndPrintException(indentingPrintWriter, "RemoteException while calling setCurrentUsbFunctions mRequest = " + i + ", mFunctions = " + j + ", timeout = " + i2 + ", mChargingFunctions = " + z + ", operationId =" + j2, e);
        }
    }

    /* loaded from: classes2.dex */
    public class UsbGadgetCallback extends IUsbGadgetCallback.Stub {
        public boolean mChargingFunctions;
        public long mFunctions;
        public int mRequest;

        public UsbGadgetCallback() {
        }

        public UsbGadgetCallback(IndentingPrintWriter indentingPrintWriter, int i, long j, boolean z) {
            this.mRequest = i;
            this.mFunctions = j;
            this.mChargingFunctions = z;
        }

        public void setCurrentUsbFunctionsCb(long j, int i) {
            UsbGadgetHidl.this.mDeviceManager.setCurrentUsbFunctionsCb(j, i, this.mRequest, this.mFunctions, this.mChargingFunctions);
        }

        public void getCurrentUsbFunctionsCb(long j, int i) {
            UsbGadgetHidl.this.mDeviceManager.getCurrentUsbFunctionsCb(j, i);
        }

        public void getUsbSpeedCb(int i) {
            UsbGadgetHidl.this.mDeviceManager.getUsbSpeedCb(i);
        }
    }
}
