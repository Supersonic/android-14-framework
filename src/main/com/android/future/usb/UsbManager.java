package com.android.future.usb;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.IUsbManager;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
/* loaded from: classes.dex */
public class UsbManager {
    public static final String ACTION_USB_ACCESSORY_ATTACHED = "android.hardware.usb.action.USB_ACCESSORY_ATTACHED";
    public static final String ACTION_USB_ACCESSORY_DETACHED = "android.hardware.usb.action.USB_ACCESSORY_DETACHED";
    public static final String EXTRA_PERMISSION_GRANTED = "permission";
    private static final String TAG = "UsbManager";
    private final Context mContext;
    private final IUsbManager mService;

    private UsbManager(Context context, IUsbManager service) {
        this.mContext = context;
        this.mService = service;
    }

    public static UsbManager getInstance(Context context) {
        IBinder b = ServiceManager.getService("usb");
        return new UsbManager(context, IUsbManager.Stub.asInterface(b));
    }

    public static UsbAccessory getAccessory(Intent intent) {
        android.hardware.usb.UsbAccessory accessory = (android.hardware.usb.UsbAccessory) intent.getParcelableExtra("accessory");
        if (accessory == null) {
            return null;
        }
        return new UsbAccessory(accessory);
    }

    public UsbAccessory[] getAccessoryList() {
        try {
            android.hardware.usb.UsbAccessory accessory = this.mService.getCurrentAccessory();
            if (accessory == null) {
                return null;
            }
            return new UsbAccessory[]{new UsbAccessory(accessory)};
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in getAccessoryList", e);
            return null;
        }
    }

    public ParcelFileDescriptor openAccessory(UsbAccessory accessory) {
        try {
            return this.mService.openAccessory(new android.hardware.usb.UsbAccessory(accessory.getManufacturer(), accessory.getModel(), accessory.getDescription(), accessory.getVersion(), accessory.getUri(), accessory.getSerial()));
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in openAccessory", e);
            return null;
        }
    }

    public boolean hasPermission(UsbAccessory accessory) {
        try {
            return this.mService.hasAccessoryPermission(new android.hardware.usb.UsbAccessory(accessory.getManufacturer(), accessory.getModel(), accessory.getDescription(), accessory.getVersion(), accessory.getUri(), accessory.getSerial()));
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in hasPermission", e);
            return false;
        }
    }

    public void requestPermission(UsbAccessory accessory, PendingIntent pi) {
        try {
            this.mService.requestAccessoryPermission(new android.hardware.usb.UsbAccessory(accessory.getManufacturer(), accessory.getModel(), accessory.getDescription(), accessory.getVersion(), accessory.getUri(), accessory.getSerial()), this.mContext.getPackageName(), pi);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in requestPermission", e);
        }
    }
}
